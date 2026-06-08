package org.trp.shincolle.server

import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import java.util.*
import kotlin.math.max

object PlayerSkillService {

    private val skillCooldowns: MutableMap<UUID, IntArray> = mutableMapOf()

    fun tickCooldowns() {
        val iterator = skillCooldowns.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val cds = entry.value
            for (i in cds.indices) {
                if (cds[i] > 0) cds[i]--
            }
            if (cds.all { it <= 0 }) {
                iterator.remove()
            }
        }
    }

    fun handlePlayerSkill(
        player: ServerPlayer,
        skillType: Int,
        targetEntityUUID: Optional<UUID>,
        targetPos: Optional<Vec3>
    ) {
        val ship = getSkillHostShip(player) ?: return

        if (!ship.isOwnedBy(player)) return

        val cdArray = skillCooldowns.getOrPut(ship.uuid) { IntArray(5) { 0 } }

        when (skillType) {
            0 -> castLightAttack(player, ship, targetEntityUUID, cdArray)
            1 -> castHeavyAttack(player, ship, targetEntityUUID, targetPos, cdArray)
            2 -> castLightAircraftAttack(player, ship, targetEntityUUID, cdArray)
            3 -> castHeavyAircraftAttack(player, ship, targetEntityUUID, cdArray)
            else -> {}
        }
    }

    private fun castLightAttack(
        player: ServerPlayer,
        ship: EntityShipBase,
        targetEntityUUID: Optional<UUID>,
        cdArray: IntArray
    ) {
        if (!ship.isStateGuiBtn1 || !ship.isStateLightAttack) return
        if (cdArray[0] > 0) return
        if (ship.ammoLight <= 0) return

        val target = resolveEntityTarget(player.serverLevel(), targetEntityUUID)
        if (target != null && target.isAlive && !ship.isOwnedBy(target)) {
            ship.performLightAttack(target as net.minecraft.world.entity.LivingEntity?)
            cdArray[0] = getAttackDelay(ship)
        }
    }

    private fun castHeavyAttack(
        player: ServerPlayer,
        ship: EntityShipBase,
        targetEntityUUID: Optional<UUID>,
        targetPos: Optional<Vec3>,
        cdArray: IntArray
    ) {
        if (!ship.isStateGuiBtn2 || !ship.isStateHeavyAttack) return
        if (cdArray[1] > 0) return
        if (ship.ammoHeavy <= 0) return

        val entityTarget = resolveEntityTarget(player.serverLevel(), targetEntityUUID)
        if (entityTarget != null && entityTarget.isAlive && !ship.isOwnedBy(entityTarget)) {
            ship.performHeavyAttack(entityTarget as net.minecraft.world.entity.LivingEntity?)
            cdArray[1] = getAttackDelay(ship) * 2
            return
        }

        if (targetPos.isPresent) {
            val pos = targetPos.get()
            // Heavy attack on block position: perform attack at position
            // Find nearest entity at the position
            val level = player.serverLevel()
            val box = net.minecraft.world.phys.AABB.ofSize(pos, 4.0, 4.0, 4.0)
            val entities = level.getEntitiesOfClass(LivingEntity::class.java, box) { it != player && it != ship }
            val nearest = entities.minByOrNull { it.distanceToSqr(pos) }
            if (nearest != null && !ship.isOwnedBy(nearest)) {
                ship.performHeavyAttack(nearest)
            } else {
                ship.performHeavyAttack(null)
            }
            cdArray[1] = getAttackDelay(ship) * 2
        }
    }

    private fun castLightAircraftAttack(
        player: ServerPlayer,
        ship: EntityShipBase,
        targetEntityUUID: Optional<UUID>,
        cdArray: IntArray
    ) {
        if (!ship.isStateGuiBtn3 || !ship.isStateLightAircraftAttack) return
        if (cdArray[2] > 0 || cdArray[3] > 0) return
        if (!ship.hasAirLight() || ship.ammoLight < 5) return

        val target = resolveEntityTarget(player.serverLevel(), targetEntityUUID)
        if (target != null && target.isAlive && !ship.isOwnedBy(target)) {
            ship.performLightAircraftAttack(target as net.minecraft.world.entity.LivingEntity?)
            cdArray[2] = getAttackDelay(ship) * 3
            cdArray[3] = cdArray[2]
        }
    }

    private fun castHeavyAircraftAttack(
        player: ServerPlayer,
        ship: EntityShipBase,
        targetEntityUUID: Optional<UUID>,
        cdArray: IntArray
    ) {
        if (!ship.isStateGuiBtn4 || !ship.isStateHeavyAircraftAttack) return
        if (cdArray[2] > 0 || cdArray[3] > 0) return
        if (!ship.hasAirHeavy() || ship.ammoHeavy < 5) return

        val target = resolveEntityTarget(player.serverLevel(), targetEntityUUID)
        if (target != null && target.isAlive && !ship.isOwnedBy(target)) {
            ship.performHeavyAircraftAttack(target as net.minecraft.world.entity.LivingEntity?)
            cdArray[2] = getAttackDelay(ship) * 4
            cdArray[3] = cdArray[2]
        }
    }

    private fun resolveEntityTarget(level: ServerLevel, uuid: Optional<UUID>): net.minecraft.world.entity.LivingEntity? {
        if (!uuid.isPresent) return null
        val entity = level.getEntity(uuid.get())
        return if (entity is net.minecraft.world.entity.LivingEntity) entity else null
    }

    private fun getAttackDelay(ship: EntityShipBase): Int {
        return max(10, 60 - ship.level * 2)
    }

    fun getSkillHostShip(player: ServerPlayer): EntityShipBase? {
        // Mount mode
        val vehicle = player.vehicle
        if (vehicle is EntityMountBase) {
            return vehicle.host
        }

        // Rider mode: ship riding on player
        for (passenger in player.passengers) {
            if (passenger is EntityShipBase && passenger.isAlive) {
                return passenger
            }
        }

        return null
    }
}
