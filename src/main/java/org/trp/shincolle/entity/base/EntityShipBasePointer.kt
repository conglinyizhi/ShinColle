package org.trp.shincolle.entity.base

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.server.PlayerStateService.admiralData
import org.trp.shincolle.utility.FormationHelper
import org.trp.shincolle.utility.FormationHelper.getFormationDirection
import java.util.*
import kotlin.math.max

internal class EntityShipBasePointer(private val ship: EntityShipBase) {
    private var pointerTarget: Vec3? = null
    private var pointerTargetUntil: Long = 0
    private var pointerAlongX = false
    private var pointerFaceP = false

    private var pointerTargetEntityId: UUID? = null
    private var pointerTargetEntityUntil: Long = 0

    fun saveToNbt(compound: CompoundTag) {
        if (this.pointerTarget != null) {
            val remaining = max(0L, this.pointerTargetUntil - this.ship.level().getGameTime())
            if (remaining > 0L) {
                val targetTag = CompoundTag()
                targetTag.putDouble("X", this.pointerTarget!!.x)
                targetTag.putDouble("Y", this.pointerTarget!!.y)
                targetTag.putDouble("Z", this.pointerTarget!!.z)
                targetTag.putLong("Remaining", remaining)
                targetTag.putBoolean("AlongX", this.pointerAlongX)
                targetTag.putBoolean("FaceP", this.pointerFaceP)
                compound.put("PointerTarget", targetTag)
            }
        }
        if (this.pointerTargetEntityId != null) {
            val remaining = max(0L, this.pointerTargetEntityUntil - this.ship.level().getGameTime())
            if (remaining > 0L) {
                val targetTag = CompoundTag()
                targetTag.putUUID("Id", this.pointerTargetEntityId)
                targetTag.putLong("Remaining", remaining)
                compound.put("PointerTargetEntity", targetTag)
            }
        }
    }

    fun loadFromNbt(compound: CompoundTag) {
        if (compound.contains("PointerTarget")) {
            val targetTag = compound.getCompound("PointerTarget")
            val x = targetTag.getDouble("X")
            val y = targetTag.getDouble("Y")
            val z = targetTag.getDouble("Z")
            val remaining = targetTag.getLong("Remaining")
            if (remaining > 0L) {
                this.pointerTarget = Vec3(x, y, z)
                this.pointerTargetUntil = this.ship.level().getGameTime() + remaining
                this.pointerAlongX = targetTag.getBoolean("AlongX")
                this.pointerFaceP = targetTag.getBoolean("FaceP")
            } else {
                this.pointerTarget = null
                this.pointerTargetUntil = 0L
            }
        } else {
            this.pointerTarget = null
            this.pointerTargetUntil = 0L
        }
        if (compound.contains("PointerTargetEntity")) {
            val targetTag = compound.getCompound("PointerTargetEntity")
            val id = if (targetTag.hasUUID("Id")) targetTag.getUUID("Id") else null
            val remaining = targetTag.getLong("Remaining")
            if (id != null && remaining > 0L) {
                this.pointerTargetEntityId = id
                this.pointerTargetEntityUntil = this.ship.level().getGameTime() + remaining
            } else {
                this.pointerTargetEntityId = null
                this.pointerTargetEntityUntil = 0L
            }
        } else {
            this.pointerTargetEntityId = null
            this.pointerTargetEntityUntil = 0L
        }
    }

    fun setPointerTarget(target: Vec3, durationTicks: Long) {
        this.pointerTarget = target
        this.pointerTargetUntil = this.ship.level().getGameTime() + max(0L, durationTicks)

        val ownerRaw = this.ship.getOwner()
        if (ownerRaw is Player) {
            var refPos = this.ship.position()
            val teamId = this.ship.formationTeam
            if (teamId >= 0 && this.ship.formationSlot > 0) {
                val leader = findFormationLeader(ownerRaw, teamId)
                if (leader != null) {
                    refPos = leader.position()
                }
            }
            val dir = getFormationDirection(
                target.x, target.z, refPos.x, refPos.z
            )
            this.pointerAlongX = dir[0]
            this.pointerFaceP = dir[1]
        }

        updateSynchedData()
    }

    fun hasPointerTarget(): Boolean {
        if (this.ship.level().isClientSide) {
            readSynchedData()
        }
        return this.pointerTarget != null && this.ship.level().getGameTime() <= this.pointerTargetUntil
    }

    fun getPointerTarget(): Vec3? {
        if (this.ship.level().isClientSide) {
            readSynchedData()
        }
        if (this.pointerTarget == null) return null

        val teamId = this.ship.formationTeam
        val slotId = this.ship.formationSlot

        if (teamId >= 0 && slotId > 0) {
            val ownerRaw = this.ship.getOwner()
            if (ownerRaw is Player) {
                val data = admiralData(ownerRaw)
                val formationId = data.getFormationID(teamId)

                return FormationHelper.getFormationPos(
                    formationId,
                    slotId,
                    this.pointerTarget!!,
                    this.pointerAlongX,
                    this.pointerFaceP
                )
            }
        }

        return this.pointerTarget
    }

    val rawPointerTarget: Vec3?
        get() {
            if (this.ship.level().isClientSide) {
                readSynchedData()
            }
            return this.pointerTarget
        }

    val pointerTargetRemainingTicks: Long
        get() {
            if (this.ship.level().isClientSide) {
                readSynchedData()
            }
            return if (this.pointerTarget == null) 0L else max(
                0L,
                this.pointerTargetUntil - this.ship.level().getGameTime()
            )
        }

    fun clearPointerTarget() {
        this.pointerTarget = null
        this.pointerTargetUntil = 0L
        updateSynchedData()
    }

    fun setPointerTargetEntity(target: Entity?, durationTicks: Long) {
        if (target == null) {
            clearPointerTargetEntity()
            return
        }
        this.pointerTarget = null
        this.pointerTargetUntil = 0L
        this.pointerTargetEntityId = target.getUUID()
        this.pointerTargetEntityUntil = this.ship.level().getGameTime() + max(0L, durationTicks)

        this.ship.combat.resetAircraftLaunchDelay()
        updateSynchedData()
    }

    fun hasPointerTargetEntity(): Boolean {
        if (this.ship.level().isClientSide) {
            readSynchedData()
        }
        return this.pointerTargetEntityId != null && this.ship.level().getGameTime() <= this.pointerTargetEntityUntil
    }

    val pointerTargetEntity: Entity?
        get() {
            if (this.ship.level().isClientSide) {
                readSynchedData()
            }
            if (!hasPointerTargetEntity() || this.pointerTargetEntityId == null) {
                return null
            }
            if (this.ship.level() is ServerLevel) {
                val serverLevel = this.ship.level() as ServerLevel
                val entity: Entity? = serverLevel.getEntity(this.pointerTargetEntityId)
                if (entity == null || !entity.isAlive || entity.isRemoved) {
                    return null
                }
                return entity
            }
            if (this.ship.level().isClientSide && this.ship.level() is ClientLevel) {
                val clientLevel = this.ship.level() as ClientLevel
                for (e in clientLevel.entitiesForRendering()) {
                    if (e.uuid == this.pointerTargetEntityId && e.isAlive && !e.isRemoved) {
                        return e
                    }
                }
            }
            return null
        }

    val pointerTargetEntityRemainingTicks: Long
        get() {
            if (this.ship.level().isClientSide) {
                readSynchedData()
            }
            return if (this.pointerTargetEntityId == null)
                0L
            else max(0L, this.pointerTargetEntityUntil - this.ship.level().getGameTime())
        }

    fun clearPointerTargetEntity() {
        this.pointerTargetEntityId = null
        this.pointerTargetEntityUntil = 0L
        updateSynchedData()
    }

    private fun findFormationLeader(owner: Player, teamId: Int): EntityShipBase? {
        if (teamId < 0) return null
        val data = admiralData(owner)
        val leaderUuid = data.getShipUUID(teamId, 0) ?: return null
        val level = this.ship.level()
        val entity = when (level) {
            is ServerLevel -> level.getEntity(leaderUuid)
            is ClientLevel -> level.entitiesForRendering().find { it.uuid == leaderUuid }
            else -> null
        }
        return if (entity is EntityShipBase && entity.isAlive && !entity.isRemoved) entity else null
    }

    private fun updateSynchedData() {
        if (this.ship.level().isClientSide) return

        val tag = CompoundTag()
        if (this.pointerTarget != null) {
            tag.putDouble("PX", this.pointerTarget!!.x)
            tag.putDouble("PY", this.pointerTarget!!.y)
            tag.putDouble("PZ", this.pointerTarget!!.z)
            tag.putLong("PUntil", this.pointerTargetUntil)
            tag.putBoolean("PAX", this.pointerAlongX)
            tag.putBoolean("PFP", this.pointerFaceP)
        }
        if (this.pointerTargetEntityId != null) {
            tag.putUUID("PEId", this.pointerTargetEntityId)
            tag.putLong("PEUntil", this.pointerTargetEntityUntil)
        }
        this.ship.entityData.set(EntityShipBase.Companion.POINTER_TARGET_DATA, tag)
    }

    private fun readSynchedData() {
        if (!this.ship.level().isClientSide) return

        val tag = this.ship.entityData.get(EntityShipBase.Companion.POINTER_TARGET_DATA) ?: return
        if (tag.isEmpty()) {
            this.pointerTarget = null
            this.pointerTargetUntil = 0L
            this.pointerTargetEntityId = null
            this.pointerTargetEntityUntil = 0L
            return
        }

        if (tag.contains("PX")) {
            this.pointerTarget = Vec3(tag.getDouble("PX"), tag.getDouble("PY"), tag.getDouble("PZ"))
            this.pointerTargetUntil = tag.getLong("PUntil")
            this.pointerAlongX = tag.getBoolean("PAX")
            this.pointerFaceP = tag.getBoolean("PFP")
        } else {
            this.pointerTarget = null
            this.pointerTargetUntil = 0L
        }

        if (tag.contains("PEId")) {
            this.pointerTargetEntityId = tag.getUUID("PEId")
            this.pointerTargetEntityUntil = tag.getLong("PEUntil")
        } else {
            this.pointerTargetEntityId = null
            this.pointerTargetEntityUntil = 0L
        }
    }
}
