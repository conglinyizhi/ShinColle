package org.trp.shincolle.event

import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.AnvilUpdateEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent.*
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.*
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import org.trp.shincolle.Shincolle
import org.trp.shincolle.command.ModCommands
import org.trp.shincolle.entity.*
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.base.EntityShincolleSimpleMob
import org.trp.shincolle.entity.base.EntityShipBaseSimple
import org.trp.shincolle.entity.base.EntitySummonBase
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.item.DebugInspectorItem.Companion.handleItemFrameInteract
import org.trp.shincolle.item.LegacyEquipItem
import org.trp.shincolle.server.HostileDropService.handleLivingDrops
import org.trp.shincolle.server.MarriageRingService.applyTickAbilities
import org.trp.shincolle.server.MarriageRingService.getUnderwaterBreakSpeedMultiplier
import org.trp.shincolle.server.MarriageRingService.handleFireDamageEvent
import org.trp.shincolle.server.PlayerStateService.copyPersistentPlayerState
import org.trp.shincolle.server.PlayerStateService.giveInitialManualIfNeeded
import org.trp.shincolle.server.PlayerStateService.syncAdmiralState
import org.trp.shincolle.server.PointerInteractionService.handleLeftClickBlock
import org.trp.shincolle.server.PointerInteractionService.handlePointerAttack
import org.trp.shincolle.server.PointerInteractionService.handleRightClickBlock
import org.trp.shincolle.server.PointerInteractionService.handleRightClickItem
import org.trp.shincolle.server.PlayerSkillService.tickCooldowns
import org.trp.shincolle.server.ShipRegistrySavedData
import org.trp.shincolle.server.TemporaryLightService
import org.trp.shincolle.utility.PerformanceTrace.beginServerTick
import org.trp.shincolle.utility.PerformanceTrace.endServerTick
import kotlin.math.max
import kotlin.math.min

@EventBusSubscriber(modid = Shincolle.MODID)
object ModEventBusEvents {
    @JvmStatic
    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        ModCommands.register(event.getDispatcher())
    }

    @JvmStatic
    @SubscribeEvent
    fun onServerTickPre(event: ServerTickEvent.Pre) {
        beginServerTick(event.getServer().getTickCount())
    }

    @JvmStatic
    @SubscribeEvent
    fun onServerTickPost(event: ServerTickEvent.Post?) {
        endServerTick()
        val server = event?.getServer() ?: return
        for (serverLevel in server.allLevels) {
            TemporaryLightService.tick(serverLevel)
        }
        tickCooldowns()
    }

    @JvmStatic
    @SubscribeEvent
    fun registerAttributes(event: EntityAttributeCreationEvent) {
        event.put(ModEntities.NORTHERN_HIME.get(), EntityNorthernHime.createAttributes().build())
        event.put(ModEntities.DESTROYER_IKAZUCHI.get(), EntityDestroyerIkazuchi.createAttributes().build())
        event.put(ModEntities.AIRFIELD_HIME.get(), EntityAirfieldHime.createAttributes().build())
        event.put(ModEntities.BATTLESHIP_RU.get(), EntityBattleshipRu.createAttributes().build())

        event.put(ModEntities.BATTLESHIP_HIME.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.BATTLESHIP_NAGATO.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.BATTLESHIP_RE.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.BATTLESHIP_TA.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.BATTLESHIP_YAMATO.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.BB_HARUNA.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.BB_HIEI.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.BB_KIRISHIMA.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.BB_KONGOU.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CA_HIME.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CARRIER_AKAGI.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CARRIER_HIME.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CARRIER_KAGA.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CARRIER_W_DEMON.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CARRIER_WO.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CRUISER_ATAGO.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CRUISER_TAKAO.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CRUISER_TATSUTA.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.CRUISER_TENRYUU.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_AKATSUKI.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_HA.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_HIBIKI.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_HIME.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_I.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_INAZUMA.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_NI.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_RO.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.DESTROYER_SHIMAKAZE.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.HARBOUR_HIME.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.HEAVY_CRUISER_NE.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.HEAVY_CRUISER_RI.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.ISOLATED_HIME.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.MIDWAY_HIME.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.SSNH.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.SUBM_HIME.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.SUBM_KA.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.SUBM_RO500.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.SUBM_SO.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.SUBM_U511.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.SUBM_YO.get(), EntityShipBaseSimple.createAttributes().build())
        event.put(ModEntities.TRANSPORT_WA.get(), EntityShipBaseSimple.createAttributes().build())

        event.put(ModEntities.AIRPLANE.get(), EntityAircraftBase.createAttributes().build())
        event.put(ModEntities.AIRPLANE_T.get(), EntityAircraftBase.createAttributes().build())
        event.put(ModEntities.AIRPLANE_ZERO.get(), EntityAircraftBase.createAttributes().build())
        event.put(ModEntities.MOUNT_AF_H.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.MOUNT_BA_H.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.MOUNT_CA_H.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.MOUNT_CA_WD.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.MOUNT_HB_H.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.MOUNT_IS_H.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.MOUNT_MI_H.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.MOUNT_SU_H.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.RENSOUHOU.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.RENSOUHOU_S.get(), EntityShincolleSimpleMob.createAttributes().build())
        event.put(ModEntities.TAKOYAKI.get(), EntityAircraftBase.createAttributes().build())

        event.put(ModEntities.FLOATING_FORT.get(), EntityAircraftBase.createAttributes().build())
        event.put(ModEntities.AIRPLANE_T_MOB.get(), EntityAircraftBase.createAttributes().build())
        event.put(ModEntities.AIRPLANE_ZERO_MOB.get(), EntityAircraftBase.createAttributes().build())
        event.put(ModEntities.RENSOUHOU_MOB.get(), EntityShincolleSimpleMob.createAttributes().build())
    }

    @JvmStatic
    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent.Post) {
        val player = event.getEntity()
        HostileSpawnManager.tickPlayer(player)

        if (player.level().isClientSide) {
            return
        }

        applyTickAbilities(player)
    }

    @JvmStatic
    @SubscribeEvent
    fun onPlayerBreakSpeed(event: BreakSpeed) {
        val multiplier = getUnderwaterBreakSpeedMultiplier(event.getEntity())
        if (multiplier > 1.0f) {
            event.setNewSpeed(event.getOriginalSpeed() * multiplier)
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onLivingIncomingDamage(event: LivingIncomingDamageEvent) {
        val target = event.entity
        val source = event.source
        val level = target.level()
        if (level.isClientSide) return

        // 1. Mount riding immunity: prevent fall/in-wall damage while riding mounts
        val vehicle = target.vehicle
        if (vehicle is EntityMountBase &&
            (source.`is`(DamageTypes.FALL) || source.`is`(DamageTypes.IN_WALL))
        ) {
            event.isCanceled = true
            return
        }

        // 2. Fire damage immunity for married players (existing logic)
        if (target is Player && handleFireDamageEvent(target, source)) {
            event.isCanceled = true
            return
        }

        val attacker = source.entity
        if (attacker == null) return

        // 3. Player attacking -> set revenge target for friendly ships around player
        if (attacker is Player) {
            setRevengeTargetAroundPlayer(level, attacker, target)
        }

        // 4. Player being attacked -> set revenge target for friendly ships around player
        if (target is Player) {
            setRevengeTargetAroundPlayer(level, target, attacker)
        }

        // 5. Hostile ship being attacked -> call for help from nearby hostile ships
        if (target is EntityShipBase && target.isHostileShipMob) {
            if (attacker !is EntityShipBase || !attacker.isHostileShipMob) {
                setRevengeTargetAroundHostileShip(level, target, attacker)
            }
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onLivingDeath(event: LivingDeathEvent) {
        val entity = event.entity
        val level = entity.level()
        if (level.isClientSide) return

        val source = event.source

        // 1. Update ship registry when a ship dies
        if (entity is EntityShipBase) {
            ShipRegistrySavedData.get(level as ServerLevel).markRemoved(entity)
        }

        // 2. Add kills to the attacker ship
        val trueSource = source.entity
        if (trueSource is EntityShipBase) {
            trueSource.addShipKill()
            trueSource.addMorale(2)
        }

        // 3. Add kills to carrier if attacker is aircraft or summon
        val carrier = when (trueSource) {
            is EntityAircraftBase -> trueSource.carrier
            is EntitySummonBase -> trueSource.carrier
            else -> null
        }
        if (carrier != null) {
            carrier.addShipKill()
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onAnvilUpdate(event: AnvilUpdateEvent) {
        val left = event.left
        val right = event.right

        if (left.item is LegacyEquipItem && right.`is`(Items.ENCHANTED_BOOK)) {
            val result = left.copy()
            val equipEnchants = EnchantmentHelper.getEnchantmentsForCrafting(result)
            val bookEnchants = EnchantmentHelper.getEnchantmentsForCrafting(right)
            val mutableEnchants = net.minecraft.world.item.enchantment.ItemEnchantments.Mutable(equipEnchants)

            for (holder in bookEnchants.keySet()) {
                val bookLevel = bookEnchants.getLevel(holder)
                if (bookLevel <= 0) continue

                val equipLevel = equipEnchants.getLevel(holder)
                val maxLevel = holder.value().maxLevel
                val newLevel = if (equipLevel == bookLevel) {
                    min(bookLevel + 1, maxLevel)
                } else {
                    max(equipLevel, bookLevel)
                }

                if (newLevel > 0) {
                    mutableEnchants.set(holder, newLevel)
                }
            }

            EnchantmentHelper.setEnchantments(result, mutableEnchants.toImmutable())
            event.output = result
            event.cost = 30
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onPlayerLogin(event: PlayerLoggedInEvent) {
        val serverPlayer = event.getEntity()
        if (serverPlayer is ServerPlayer) {
            giveInitialManualIfNeeded(serverPlayer)
            syncAdmiralState(serverPlayer)
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val serverPlayer = event.getEntity()
        if (serverPlayer is ServerPlayer) {
            syncAdmiralState(serverPlayer)
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onPlayerChangedDimension(event: PlayerChangedDimensionEvent) {
        val serverPlayer = event.getEntity()
        if (serverPlayer is ServerPlayer) {
            syncAdmiralState(serverPlayer)
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onPlayerClone(event: PlayerEvent.Clone) {
        copyPersistentPlayerState(event.getOriginal(), event.getEntity())
    }

    @JvmStatic
    @SubscribeEvent
    fun onPointerItemAttack(event: AttackEntityEvent) {
        if (handlePointerAttack(event.getEntity(), event.getTarget())) {
            event.setCanceled(true)
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onPointerItemLeftClickBlock(event: LeftClickBlock) {
        handleLeftClickBlock(event.getEntity(), event)
    }

    @JvmStatic
    @SubscribeEvent
    fun onPointerItemRightClickItem(event: RightClickItem) {
        if (handleRightClickItem(event.getEntity(), event)) {
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getEntity().level().isClientSide))
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onPointerItemRightClickBlock(event: RightClickBlock) {
        if (handleRightClickBlock(event.getEntity(), event)) {
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getEntity().level().isClientSide))
        }
    }


    @JvmStatic
    @SubscribeEvent
    fun onHostileEntityDropsGrudge(event: LivingDropsEvent?) {
        handleLivingDrops(event)
    }

    @JvmStatic
    @SubscribeEvent
    fun onEntityInteract(event: EntityInteract) {
        handleItemFrameInteract(event)
    }

    /**
     * Set revenge target for friendly ships around the player.
     */
    private fun setRevengeTargetAroundPlayer(
        level: net.minecraft.world.level.Level,
        player: Player,
        target: net.minecraft.world.entity.Entity
    ) {
        if (level !is ServerLevel) return
        val livingTarget = if (target is LivingEntity) target else return
        val box = player.boundingBox.inflate(32.0)
        val ships = level.getEntitiesOfClass(EntityShipBase::class.java, box)
        for (ship in ships) {
            if (ship == target) continue
            if (ship.isOwnedBy(player)) {
                ship.setLastHurtByMob(livingTarget)
            }
        }
    }

    /**
     * Call for help: set revenge target for hostile ships around the attacked hostile ship.
     */
    private fun setRevengeTargetAroundHostileShip(
        level: net.minecraft.world.level.Level,
        host: EntityShipBase,
        target: net.minecraft.world.entity.Entity
    ) {
        if (level !is ServerLevel) return
        val livingTarget = if (target is LivingEntity) target else return
        val box = host.boundingBox.inflate(64.0)
        val ships = level.getEntitiesOfClass(EntityShipBase::class.java, box)
        for (ship in ships) {
            if (!ship.isHostileShipMob || ship == host) continue
            ship.setLastHurtByMob(livingTarget)
        }
    }
}
