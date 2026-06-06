package org.trp.shincolle.server

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.animal.AbstractGolem
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.Slime
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameRules
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModItems
import kotlin.math.max

object HostileDropService {
    @JvmStatic
    fun handleLivingDrops(event: LivingDropsEvent?) {
        if (event == null) {
            return
        }
        val target: Entity = event.getEntity()
        if (target == null) {
            return
        }
        if (target.level().isClientSide || !isHostileDropTarget(target)) {
            return
        }

        if (!target.level().gameRules.getBoolean(GameRules.RULE_DOMOBLOOT)) {
            return
        }

        if (event.getSource() == null) {
            return
        }

        val sourceEntity = event.getSource().getEntity()
        if (sourceEntity is EntityShipBase) {
            sourceEntity.addShipExp(Config.shipExpGainKill)
            sourceEntity.addShipKill()
        }

        val dropRate = max(0.0f, Config.hostileDropGrudgeRate)
        if (dropRate <= 0.0f) {
            return
        }

        val fixedDrop = dropRate.toInt()
        if (fixedDrop > 0) {
            event.drops.add(
                ItemEntity(
                    target.level(),
                    target.x, target.y, target.z, ItemStack(ModItems.GRUDGE.get(), fixedDrop)
                )
            )
        }

        if (target.random.nextFloat() < (dropRate - fixedDrop)) {
            event.drops.add(
                ItemEntity(
                    target.level(),
                    target.x, target.y, target.z, ItemStack(ModItems.GRUDGE.get())
                )
            )
        }
    }

    private fun isHostileDropTarget(entity: Entity?): Boolean {
        if (entity is EntityShipBase) {
            return entity.isHostileShipMob
        }
        return entity is Enemy || entity is Slime || entity is AbstractGolem
    }
}
