package org.trp.shincolle.entity

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModEntities
import kotlin.math.sqrt

/**
 * 浮游要塞（自杀式爆炸无人机）。
 * 从宿主舰娘位置发射，向目标移动并在接近时爆炸。
 */
class EntityFloatingFort(type: EntityType<out TamableAnimal>, level: Level) : EntityAircraftBase(type, level) {

    private var fuseTicks = 0
    private var maxLifeTicks = 500

    constructor(level: Level) : this(ModEntities.FLOATING_FORT.get(), level)

    override fun aiStep() {
        super.aiStep()

        if (this.level().isClientSide) {
            return
        }

        val level = this.level()
        if (level !is ServerLevel) return

        this.fuseTicks++

        // Check max lifetime
        if (this.fuseTicks >= this.maxLifeTicks) {
            explode()
            return
        }

        // Check if target is dead or gone
        val target = this.target
        if (target != null && (!target.isAlive || target.isRemoved)) {
            explode()
            return
        }

        // Check proximity to target
        if (target is LivingEntity && target.isAlive) {
            val distSqr = this.distanceToSqr(target)
            if (distSqr < 4.0) { // within 2 blocks
                explode()
                return
            }
        }
    }

    private fun explode() {
        val level = this.level()
        if (level !is ServerLevel) {
            this.discard()
            return
        }

        val explosionRange = 4.5
        val box = this.boundingBox.inflate(explosionRange)
        val entities = level.getEntitiesOfClass(LivingEntity::class.java, box)

        val carrier = this.carrier
        val carrierOwner = carrier?.ownerUUID

        for (entity in entities) {
            if (entity === this) continue
            if (entity === carrier) continue
            // Skip friendly entities
            if (carrier != null && carrier.isOwnedBy(entity)) continue
            if (entity is EntityShipBase && entity.ownerUUID == carrierOwner) continue
            if (entity is TamableAnimal && entity.ownerUUID == carrierOwner) continue

            val dist = this.distanceTo(entity)
            val factor = 1.0f - (dist / explosionRange.toFloat()).coerceIn(0.0f, 1.0f)
            if (factor > 0) {
                val damage = 15.0f * factor
                val source = if (carrier != null) {
                    this.damageSources().mobAttack(carrier)
                } else {
                    this.damageSources().explosion(this, this)
                }
                entity.hurt(source, damage)
            }
        }

        // Spawn explosion particles
        level.sendParticles(
            net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER,
            this.x, this.y, this.z,
            1, 0.0, 0.0, 0.0, 0.0
        )

        this.discard()
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        // Floating fort is immune to most damage
        if (source.`is`(net.minecraft.world.damagesource.DamageTypes.GENERIC_KILL)) {
            return super.hurt(source, amount)
        }
        return false
    }

    override fun isPickable(): Boolean {
        return false
    }

    override fun push(entity: Entity) {
        // No collision
    }

    override fun isPushable(): Boolean {
        return false
    }
}
