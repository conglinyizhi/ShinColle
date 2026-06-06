package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModEntities

class EntityShipFishingHook(type: EntityType<*>, level: Level) : Entity(type, level) {
    private var host: EntityShipBase? = null

    fun getHost(): EntityShipBase? {
        if (this.host != null && (!this.host!!.isAlive || this.host!!.isRemoved)) {
            this.host = null
        }
        return this.host
    }

    constructor(level: Level, host: EntityShipBase) : this(ModEntities.SHIP_FISHING_HOOK.get(), level) {
        this.host = host
        this.entityData.set<Int?>(HOST_ID, host.getId())
        this.setPos(host.getX(), host.getY() + host.getEyeHeight(), host.getZ())
        host.fishHook = this
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define<Int?>(HOST_ID, -1)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
    }

    override fun tick() {
        super.tick()

        if (this.level().isClientSide && this.host == null) {
            val id = this.entityData.get<Int?>(HOST_ID)
            if (id != -1) {
                val e = this.level().getEntity(id)
                if (e is EntityShipBase && e.isAlive && !e.isRemoved) {
                    this.host = e
                    e.fishHook = this
                }
            }
        }

        if (this.tickCount > Config.tickFishingMin + Config.tickFishingMax) {
            this.discard()
        }
        if (!this.level().isClientSide) {
            if (this.host == null || !this.host!!.isAlive || this.host!!.isRemoved) {
                this.discard()
                return
            }
            var rod = this.host!!.getItemBySlot(EquipmentSlot.MAINHAND)
            if (rod.isEmpty() || rod.getItem() !== Items.FISHING_ROD) {
                rod = this.host!!.getItemBySlot(EquipmentSlot.OFFHAND)
            }
            if (rod.isEmpty() || rod.getItem() !== Items.FISHING_ROD || this.distanceToSqr(this.host) > 1024.0) {
                this.discard()
                return
            }
            if (this.tickCount == 4) {
                this.playSound(
                    SoundEvents.FISHING_BOBBER_SPLASH,
                    0.25f,
                    1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f
                )
            }
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 0.9, 0.9))
            if (this.tickCount == 4 || ((this.tickCount and 0x3F) == 0 && this.random.nextFloat() < 0.35f)) {
                val x = this.getX()
                val y = this.getY() - 0.1
                val z = this.getZ()
                for (i in 0..13) {
                    val ranY = ((this.random.nextFloat() - 0.5f) * 0.25f).toDouble()
                    val ranX = ((this.random.nextFloat() - 0.5f) * 1.5f).toDouble()
                    val ranZ = ((this.random.nextFloat() - 0.5f) * 1.5f).toDouble()
                    this.level().addParticle(ParticleTypes.BUBBLE, x + ranX, y + ranY, z + ranZ, 0.0, 0.0, 0.0)
                    this.level().addParticle(ParticleTypes.FISHING, x + ranX, y + ranY, z + ranZ, 0.0, 0.0, 0.0)
                }
            }
        }
    }

    override fun remove(reason: RemovalReason) {
        super.remove(reason)
        if (this.host != null) {
            this.host!!.fishHook = null
        }
    }

    companion object {
        private val HOST_ID: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipFishingHook::class.java, EntityDataSerializers.INT)
    }
}
