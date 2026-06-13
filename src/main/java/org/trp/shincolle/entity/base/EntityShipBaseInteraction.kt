package org.trp.shincolle.entity.base

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuConstructor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.trp.shincolle.Config
import org.trp.shincolle.api.ApiCallSafety
import org.trp.shincolle.api.consumable.IShipConsumable
import org.trp.shincolle.entity.base.EmotionParticleType
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.item.CombatRationItem
import org.trp.shincolle.menu.ShipContainerMenu
import org.trp.shincolle.server.PlayerStateService.adjustOwnedMarriedShipCount
import java.util.Random
import java.util.function.Consumer

/**
 * Encapsulates player interaction logic for a ship (right-click / item use).
 *
 * This moves the large `mobInteract` method and its helpers out of
 * [EntityShipBase], turning each branch into a focused, testable handler.
 */
@Suppress(
    "TooManyFunctions",
    "CyclomaticComplexMethod",
    "ReturnCount",
    "ComplexCondition",
    "MagicNumber",
    "MaxLineLength"
)
internal class EntityShipBaseInteraction(private val ship: EntityShipBase) {

    fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        val stack = player.getItemInHand(hand)

        if (ship.level().isClientSide || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS
        }
        if (!ship.isTame || !ship.isOwnedBy(player)) {
            return InteractionResult.PASS
        }
        if (stack.`is`(ModItems.TRAINING_BOOK.get()) || stack.`is`(ModItems.MODERN_KIT.get())) {
            return InteractionResult.PASS
        }

        return tryMarriage(stack, player)
            ?: tryCombatRation(stack, player)
            ?: tryKaitaiHammer(stack, player)
            ?: tryBucketRepair(stack, player)
            ?: tryToyAirplane(stack, player)
            ?: tryGrudge(stack, player)
            ?: tryFood(stack, player)
            ?: tryThirdPartyConsumable(stack, player)
            ?: tryOpenMenu(player)
            ?: toggleSit(player)
    }

    fun interactModernKit(player: Player, stack: ItemStack): Boolean {
        if (!ship.legacyShipStats.addBonusRandom(Random())) {
            return false
        }

        ship.syncLegacyBonusData()
        ship.recalculateLegacyShipStats()
        ship.emotionPrimary = EntityShipBase.EMOTION_HAPPY
        ship.applyParticleEmotion(EmotionParticleType.HEART)
        ship.particleEffects.playMarrySound(volume = maxOf(0.0f, Config.volumeShip), pitch = 1.0f)
        ship.focusOnPlayer(player)

        if (!player.abilities.instabuild) {
            stack.shrink(1)
        }

        return true
    }

    fun openShipMenu(player: Player?) {
        if (player !is ServerPlayer || !ship.isAlive) {
            return
        }
        if (ship.level() !== player.level()) {
            return
        }
        if (!ship.isOwnedBy(player)) {
            return
        }
        val provider: MenuProvider = SimpleMenuProvider(
            MenuConstructor { id: Int, inv: net.minecraft.world.entity.player.Inventory?, ply: Player? ->
                ShipContainerMenu(id, inv!!, ship)
            },
            Component.translatable("gui.shincolle.ship")
        )
        player.openMenu(provider, Consumer { buffer: RegistryFriendlyByteBuf? ->
            buffer!!.writeInt(ship.id)
        })
    }

    private fun tryMarriage(stack: ItemStack, player: Player): InteractionResult? {
        if (!stack.`is`(ModItems.MARRIAGE_RING.get()) || ship.isStateMarried) {
            return null
        }

        if (!player.abilities.instabuild) {
            stack.shrink(1)
        }
        ship.isStateMarried = true
        adjustOwnedMarriedShipCount(player, 1)
        ship.morale = 16000
        ship.emotionPrimary = EntityShipBase.EMOTION_HAPPY
        ship.applyParticleEmotion(EmotionParticleType.HEART)
        spawnMarriageParticles()
        ship.particleEffects.playMarrySound()

        val javaRand = Random()
        repeat(3) {
            ship.legacyShipStats.addBonusRandom(javaRand)
        }
        ship.recalculateLegacyShipStats()

        ship.resetInteractionEmotionState()
        ship.focusOnPlayer(player)
        return InteractionResult.sidedSuccess(ship.level().isClientSide)
    }

    private fun spawnMarriageParticles() {
        val serverLevel = ship.level() as? ServerLevel ?: return
        repeat(7) {
            val px = ship.x + (ship.random.nextFloat() * 2.0f - 1.0f)
            val py = ship.y + 0.5 + (ship.random.nextFloat() * 2.0f)
            val pz = ship.z + (ship.random.nextFloat() * 2.0f - 1.0f)
            val d0 = ship.random.nextGaussian() * 0.02
            val d1 = ship.random.nextGaussian() * 0.02
            val d2 = ship.random.nextGaussian() * 0.02
            serverLevel.sendParticles<SimpleParticleType?>(
                ParticleTypes.HEART, px, py, pz, 0, d0, d1, d2, 1.0
            )
        }
    }

    private fun tryCombatRation(stack: ItemStack, player: Player): InteractionResult? {
        if (stack.item !is CombatRationItem) return null
        if (!consumeCombatRationInHand(stack, player)) return null
        ship.focusOnPlayer(player)
        return InteractionResult.sidedSuccess(ship.level().isClientSide)
    }

    private fun consumeCombatRationInHand(stack: ItemStack, player: Player): Boolean {
        if (stack.item !is CombatRationItem) {
            return false
        }
        ship.applyCombatRationEffect((stack.item as CombatRationItem).getVariant(stack))
        if (!player.abilities.instabuild) {
            stack.shrink(1)
        }
        return true
    }

    private fun tryKaitaiHammer(stack: ItemStack, player: Player): InteractionResult? {
        if (!stack.`is`(ModItems.KAITAI_HAMMER.get()) || !player.isShiftKeyDown) {
            return null
        }
        spawnKaitaiDrops()
        ship.applyParticleEmotion(8)
        ship.applyEmotesAOE(10.0, 6, false)
        ship.hurt(player.damageSources().fellOutOfWorld(), Float.MAX_VALUE)
        if (!player.abilities.instabuild) {
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND)
        }
        ship.focusOnPlayer(player)
        return InteractionResult.sidedSuccess(ship.level().isClientSide)
    }

    private fun tryBucketRepair(stack: ItemStack, player: Player): InteractionResult? {
        if (!stack.`is`(ModItems.BUCKET_REPAIR.get())) return null
        if (!consumeBucketRepairInHand(stack, player)) return null
        return InteractionResult.sidedSuccess(ship.level().isClientSide)
    }

    private fun consumeBucketRepairInHand(stack: ItemStack, player: Player): Boolean {
        if (!stack.`is`(ModItems.BUCKET_REPAIR.get())) {
            return false
        }
        if (ship.health >= ship.maxHealth) {
            return false
        }

        if (ship.supportsAircraftCombat()) {
            ship.heal(ship.maxHealth * 0.05f + 10.0f)
        } else {
            ship.heal(ship.maxHealth * 0.1f + 5.0f)
        }
        ship.recordCreativeDebuggerBucketRepair()

        if (ship.supportsAircraftCombat()) {
            ship.numAircraftLight += 1
            ship.numAircraftHeavy += 1
        }

        if (!player.abilities.instabuild) {
            stack.shrink(1)
        }

        ship.emotionPrimary = EntityShipBase.EMOTION_HAPPY
        ship.applyParticleEmotion(EmotionParticleType.HEART)
        ship.particleEffects.playShipSound(Config.ShipCustomSoundType.FEED)
        ship.focusOnPlayer(player)
        return true
    }

    private fun tryToyAirplane(stack: ItemStack, player: Player): InteractionResult? {
        if (!stack.`is`(ModItems.TOY_AIRPLANE.get())) return null
        if (!consumeToyAirplaneInHand(stack, player)) return null
        return InteractionResult.sidedSuccess(ship.level().isClientSide)
    }

    private fun consumeToyAirplaneInHand(stack: ItemStack, player: Player): Boolean {
        if (!stack.`is`(ModItems.TOY_AIRPLANE.get())) {
            return false
        }
        if (ship.supportsAircraftCombat()) {
            ship.numAircraftLight += 2
            ship.numAircraftHeavy += 2
        }
        ship.addMorale(200)
        ship.emotionPrimary = EntityShipBase.EMOTION_HAPPY
        ship.applyParticleEmotion(EmotionParticleType.HAPPY_BOB)
        ship.particleEffects.playShipSound(Config.ShipCustomSoundType.FEED)

        if (!player.abilities.instabuild) {
            stack.shrink(1)
        }
        ship.focusOnPlayer(player)
        return true
    }

    private fun tryGrudge(stack: ItemStack, player: Player): InteractionResult? {
        if (!stack.`is`(ModItems.GRUDGE.get())) return null
        val gain = 300 + ship.random.nextInt(500)
        ship.fuel += gain
        if (!player.abilities.instabuild) {
            stack.shrink(1)
        }
        ship.particleEffects.playFeedSoundIfReady()
        ship.emotionPrimary = EntityShipBase.EMOTION_HAPPY
        ship.resetInteractionEmotionState()
        ship.focusOnPlayer(player)
        return InteractionResult.sidedSuccess(ship.level().isClientSide)
    }

    private fun tryFood(stack: ItemStack, player: Player): InteractionResult? {
        if (!stack.has(net.minecraft.core.component.DataComponents.FOOD)) return null
        val food = stack.getFoodProperties(player) ?: return null
        if (food.nutrition() <= 0) return null

        ship.fuel += food.nutrition()
        if (!player.abilities.instabuild) {
            stack.shrink(1)
        }
        ship.particleEffects.playFeedSoundIfReady()
        ship.emotionPrimary = EntityShipBase.EMOTION_HAPPY
        ship.resetInteractionEmotionState()
        ship.focusOnPlayer(player)
        return InteractionResult.sidedSuccess(ship.level().isClientSide)
    }

    private fun tryThirdPartyConsumable(stack: ItemStack, player: Player): InteractionResult? {
        if (stack.item !is IShipConsumable) return null
        val consumable = stack.item as IShipConsumable
        val canInteract = ApiCallSafety.runWithDefault(
            "IShipConsumable.canInteractWithShip", false
        ) { consumable.canInteractWithShip(stack, ship, player) }
        if (!canInteract) return null

        val success = ApiCallSafety.runWithDefault(
            "IShipConsumable.onInteractWithShip", false
        ) { consumable.onInteractWithShip(stack, ship, player) }
        if (!success) return null

        val shouldConsume = ApiCallSafety.runWithDefault(
            "IShipConsumable.consumeItemOnInteract", true
        ) { consumable.consumeItemOnInteract(stack, ship, player) }
        if (shouldConsume && !player.abilities.instabuild) {
            stack.shrink(1)
        }
        ship.focusOnPlayer(player)
        return InteractionResult.sidedSuccess(ship.level().isClientSide)
    }

    private fun tryOpenMenu(player: Player): InteractionResult? {
        if (!player.isShiftKeyDown) return null
        openShipMenu(player)
        ship.resetInteractionEmotionState()
        ship.focusOnPlayer(player)
        return InteractionResult.sidedSuccess(ship.level().isClientSide)
    }

    private fun toggleSit(player: Player): InteractionResult {
        val isSitting = !ship.isOrderedToSit()
        ship.setOrderedToSit(isSitting)
        ship.setInSittingPose(isSitting)
        if (!isSitting && ship.hasBlockGuardTarget()) {
            ship.clearGuardTarget()
        }
        ship.resetInteractionEmotionState()
        ship.focusOnPlayer(player)
        return InteractionResult.sidedSuccess(ship.level().isClientSide)
    }

    private fun spawnKaitaiDrops() {
        val serverLevel = ship.level() as? ServerLevel ?: return

        for (drop in buildKaitaiMaterialDrops()) {
            if (!drop.isEmpty) {
                serverLevel.addFreshEntity(
                    net.minecraft.world.entity.item.ItemEntity(
                        serverLevel, ship.x, ship.y + 0.8, ship.z, drop
                    )
                )
            }
        }

        val inventory = ship.inventory ?: return
        for (slot in 0..<inventory.slots) {
            val stack = inventory.getStackInSlot(slot)
            if (stack.isEmpty) continue
            serverLevel.addFreshEntity(
                net.minecraft.world.entity.item.ItemEntity(
                    serverLevel, ship.x, ship.y + 0.8, ship.z, stack.copy()
                )
            )
            inventory.setStackInSlot(slot, ItemStack.EMPTY)
        }
    }

    private fun buildKaitaiMaterialDrops(): MutableList<ItemStack> {
        val drops: MutableList<ItemStack> = ArrayList(4)
        val shipClass = ship.getStateMinor(EntityShipBase.STATE_MINOR_SHIP_CLASS)
        val rarity = maxOf(0, ship.getStateMinor(EntityShipBase.STATE_MINOR_RARITY))
        val firepower = maxOf(1.0f, ship.legacyShipStats.firepower)
        val maxHealth = maxOf(1.0f, ship.legacyShipStats.maxHealth)

        var primary: Item? = ModItems.GRUDGE.get()
        var grudge = 4 + rarity
        var abyssMetal = 0
        var ammo = 0
        var polymetal = 0

        if (shipClass >= 20 || shipClass == 12 || shipClass == 13 || shipClass == 14 || shipClass == 15 || shipClass == 16) {
            grudge += 4
            abyssMetal += 6 + rarity
            ammo += 6 + net.minecraft.util.Mth.floor(firepower * 0.2f)
        }

        if (shipClass >= 26 || shipClass == 20 || shipClass == 21 || shipClass == 30 || shipClass == 31 || shipClass == 33 || shipClass == 49) {
            primary = ModItems.ABYSS_POLYMETAL.get()
            grudge = 0
            abyssMetal += 10 + rarity * 2
            ammo += 10 + net.minecraft.util.Mth.floor(firepower * 0.25f)
            polymetal += 3 + rarity
        } else if (shipClass == 17 || shipClass == 18 || shipClass == 19 || shipClass == 38 || shipClass == 39 || shipClass == 44 || shipClass == 72) {
            ammo += 4 + rarity
            abyssMetal += 2 + net.minecraft.util.Mth.floor(maxHealth * 0.03f)
        } else if (shipClass == 12 || shipClass == 20 || shipClass == 33 || shipClass == 47 || shipClass == 48) {
            ammo += 8 + rarity
            polymetal += 1 + rarity / 2
        } else if (shipClass == 13 || shipClass == 14 || shipClass == 15 || shipClass == 26 || shipClass == 37 || shipClass == 46 || shipClass == 60 || shipClass == 61 || shipClass == 62 || shipClass == 63) {
            abyssMetal += 8 + rarity
            ammo += 5 + net.minecraft.util.Mth.floor(firepower * 0.15f)
        } else {
            abyssMetal += 2 + rarity / 2
            ammo += 2 + rarity / 2
        }

        addNonEmptyDrop(drops, primary, grudge)
        addNonEmptyDrop(drops, ModItems.ABYSS_METAL.get(), abyssMetal)
        addNonEmptyDrop(drops, ModItems.AMMO_LIGHT.get(), ammo)
        addNonEmptyDrop(drops, ModItems.ABYSS_POLYMETAL.get(), polymetal)
        return drops
    }

    private fun addNonEmptyDrop(drops: MutableList<ItemStack>, item: Item?, amount: Int) {
        if (item == null || amount <= 0) {
            return
        }
        var remaining = amount
        val maxStackSize = item.defaultInstance.maxStackSize
        while (remaining > 0) {
            val stackCount = minOf(remaining, maxStackSize)
            drops.add(ItemStack(item, stackCount))
            remaining -= stackCount
        }
    }
}
