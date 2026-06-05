package org.trp.shincolle.entity.base

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

@JvmRecord
data class ShipGuardTarget(val x: Int, val y: Int, val z: Int, val dimensionId: Int, val type: Type?) {
    val isActive: Boolean
        get() = this.type != Type.NONE

    val isBlock: Boolean
        get() = this.type == Type.BLOCK

    val isEntity: Boolean
        get() = this.type == Type.ENTITY

    fun isIn(level: Level): Boolean {
        return this.dimensionId == EntityShipBase.Companion.getLegacyDimensionId(level)
    }

    fun blockPos(): BlockPos {
        return BlockPos(this.x, this.y, this.z)
    }

    fun blockCenter(): Vec3 {
        return Vec3(this.x + 0.5, this.y.toDouble(), this.z + 0.5)
    }

    fun legacyType(): Int {
        return this.type.legacyId
    }

    enum class Type(private val legacyId: Int) {
        NONE(0),
        BLOCK(1),
        ENTITY(2);

        fun legacyId(): Int {
            return this.legacyId
        }

        companion object {
            fun fromLegacy(legacyId: Int): Type {
                return when (legacyId) {
                    1 -> Type.BLOCK
                    2 -> Type.ENTITY
                    else -> Type.NONE
                }
            }
        }
    }

    companion object {
        val NONE: ShipGuardTarget = ShipGuardTarget(-1, -1, -1, 0, Type.NONE)

        fun fromShip(ship: EntityShipBase): ShipGuardTarget {
            return ShipGuardTarget(
                ship.getGuardedPos(0),
                ship.getGuardedPos(1),
                ship.getGuardedPos(2),
                ship.getGuardedPos(3),
                Type.Companion.fromLegacy(ship.getGuardedPos(4))
            )
        }
    }
}
