package org.trp.shincolle.item

import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.common.DeferredSpawnEggItem
import org.trp.shincolle.entity.base.EntityShipBase
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.function.UnaryOperator
import kotlin.math.max

class BossSpawnEggItem(
    private val typeSupplier: Supplier<out EntityType<out Mob?>?>,
    primaryColor: Int, secondaryColor: Int, properties: Properties
) : DeferredSpawnEggItem(
    typeSupplier, primaryColor, secondaryColor, properties
) {
    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.getLevel()
        if (level.isClientSide) return super.useOn(context)

        val stack = context.getItemInHand()
        val player = context.getPlayer()
        val clickPos = context.getClickedPos().relative(context.getClickedFace())

        // Inject entity type without Owner/Tame -> spawns untamed (hostile)
        val key = BuiltInRegistries.ENTITY_TYPE.getKey(this.typeSupplier.get())
        if (key == null) return super.useOn(context)

        stack.update<CustomData?>(
            DataComponents.ENTITY_DATA,
            CustomData.EMPTY,
            UnaryOperator { existingData: CustomData? ->
                existingData!!.update(
                    Consumer { tag: CompoundTag? ->
                        tag!!.putString("id", key.toString())
                        tag.remove("Owner")
                        tag.remove("Tame")
                    })
            })

        val result = super.useOn(context)

        // Find the spawned ship: set boss scale, ammo, fuel
        if (result.consumesAction() && level is ServerLevel) {
            val spawnedType: EntityType<*>? = this.typeSupplier.get()
            val bossScale = 2 + level.random.nextInt(2) // 2 or 3 -> 2.0x or 2.5x
            for (entity in level.getEntities(
                null as Entity?,
                AABB(clickPos).inflate(4.0),
                Predicate { e: Entity? -> e!!.getType() === spawnedType && e is EntityShipBase })) {
                val ship = entity as EntityShipBase
                ship.initializeHostileSpawnState(bossScale)
                ship.setAmmoLight(ship.getAmmoLight() + 128)
                ship.setAmmoHeavy(ship.getAmmoHeavy() + 64)
                ship.setFuel(max(ship.getFuel(), 5000))
                break
            }
        }

        return result
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        if (level.isClientSide) return super.use(level, player, hand)

        val stack = player.getItemInHand(hand)

        val key = BuiltInRegistries.ENTITY_TYPE.getKey(this.typeSupplier.get())
        if (key == null) return super.use(level, player, hand)

        stack.update<CustomData?>(
            DataComponents.ENTITY_DATA,
            CustomData.EMPTY,
            UnaryOperator { existingData: CustomData? ->
                existingData!!.update(
                    Consumer { tag: CompoundTag? ->
                        tag!!.putString("id", key.toString())
                        tag.remove("Owner")
                        tag.remove("Tame")
                    })
            })

        val result = super.use(level, player, hand)
        if (result.getResult().consumesAction() && level is ServerLevel) {
            val spawnedType: EntityType<*>? = this.typeSupplier.get()
            val spawnPos = player.blockPosition()
            val bossScale = 2 + level.random.nextInt(2)
            for (entity in level.getEntities(
                null as Entity?,
                AABB(spawnPos).inflate(4.0),
                Predicate { e: Entity? -> e!!.getType() === spawnedType && e is EntityShipBase })) {
                val ship = entity as EntityShipBase
                ship.initializeHostileSpawnState(bossScale)
                ship.setAmmoLight(ship.getAmmoLight() + 128)
                ship.setAmmoHeavy(ship.getAmmoHeavy() + 64)
                ship.setFuel(max(ship.getFuel(), 5000))
                break
            }
        }

        return result
    }
}
