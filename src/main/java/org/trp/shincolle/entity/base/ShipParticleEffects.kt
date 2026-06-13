package org.trp.shincolle.entity.base

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import org.trp.shincolle.Config
import org.trp.shincolle.init.ModParticles
import org.trp.shincolle.init.ModSounds
import org.trp.shincolle.init.ModSounds.getShipSound
import org.trp.shincolle.server.TemporaryLightService

/**
 * Encapsulates ship-specific particle effects and sound playback helpers.
 *
 * Centralizing these helpers removes repetitive `getShipSound(...)` + `playSound(...)`
 * boilerplate and keeps particle spawning logic in one place.
 */
@Suppress("MagicNumber", "TooManyFunctions")
internal class ShipParticleEffects(private val ship: EntityShipBase) {

    /** Plays a custom ship sound with the ship's default volume and pitch. */
    fun playShipSound(
        type: Config.ShipCustomSoundType,
        volume: Float = ship.getShipSoundVolume(),
        pitch: Float = ship.getShipSoundPitch()
    ) {
        val sound = getShipSound(type, ship.getStateMinor(EntityShipBase.STATE_MINOR_SHIP_CLASS), ship.random)
        ship.playSound(sound, volume, pitch)
    }

    /** Plays the standard feeding sound, respecting [EntityShipBase.feedSoundCooldown]. */
    fun playFeedSoundIfReady(): Boolean {
        if (ship.feedSoundCooldown > 0) return false
        playShipSound(Config.ShipCustomSoundType.FEED)
        ship.feedSoundCooldown = FEED_SOUND_COOLDOWN_TICKS
        return true
    }

    /** Plays the sound used for marriage / modern-kit successes. */
    fun playMarrySound(volume: Float = ship.getShipSoundVolume(), pitch: Float = 1.0f) {
        playShipSound(Config.ShipCustomSoundType.MARRY, volume, pitch)
    }

    /** Plays the sound used on a successful melee attack. */
    fun playAttackSound() {
        playShipSound(Config.ShipCustomSoundType.ATTACK, ship.getShipSoundVolume(), ship.getShipSoundPitch())
    }

    /** Plays one of the randomized level-up sounds. */
    fun playLevelUpEffects() {
        if (ship.level().isClientSide) return
        val sound = if (ship.random.nextInt(4) == 0) {
            SoundEvents.PLAYER_LEVELUP
        } else {
            ModSounds.SHIP_LEVELUP.get()
        }
        ship.playSound(sound, LEVEL_UP_VOLUME, 1.0f)
    }

    /** Spawns muzzle smoke particles for a light attack. */
    fun spawnLightAttackMuzzleParticles(serverLevel: ServerLevel, target: Entity) {
        val from = ship.position().add(0.0, 0.8, 0.0)
        val to = target.position().add(0.0, target.bbHeight * 0.5, 0.0)
        var look = to.subtract(from)
        if (look.lengthSqr() < 1.0E-6) {
            look = ship.lookAngle
        } else {
            look = look.normalize()
        }

        val posX = ship.x
        val posY = ship.y
        val posZ = ship.z

        for (i in 0..23) {
            val ran1 = (ship.random.nextFloat() - 0.5f).toDouble()
            val ran2 = ship.random.nextFloat().toDouble()
            val ran3 = ship.random.nextFloat().toDouble()
            val baseX = posX + look.x - 0.5 + 0.05 * i
            val baseZ = posZ + look.z - 0.5 + 0.05 * i

            serverLevel.sendParticles<SimpleParticleType?>(
                ParticleTypes.LARGE_SMOKE,
                baseX, posY + 0.6 + ran1, baseZ,
                1, look.x * 0.3 * ran2, 0.05 * ran2, look.z * 0.3 * ran2, 0.0
            )
            serverLevel.sendParticles<SimpleParticleType?>(
                ParticleTypes.LARGE_SMOKE,
                baseX, posY + 1.0 + ran1, baseZ,
                1, look.x * 0.3 * ran3, 0.05 * ran3, look.z * 0.3 * ran3, 0.0
            )
        }
    }

    /** Spawns impact particles on a light attack target. */
    fun spawnLightAttackTargetParticles(serverLevel: ServerLevel, target: Entity) {
        val posX = target.x
        val posY = target.y
        val posZ = target.z

        serverLevel.sendParticles<SimpleParticleType?>(
            ParticleTypes.EXPLOSION_EMITTER, posX, posY + 1.5, posZ,
            1, 0.0, 0.0, 0.0, 0.0
        )

        for (i in 0..14) {
            val ran1 = ((ship.random.nextFloat() * 3.0f) - 1.5f).toDouble()
            val ran2 = ((ship.random.nextFloat() * 3.0f) - 1.5f).toDouble()
            serverLevel.sendParticles<SimpleParticleType?>(
                ParticleTypes.LAVA,
                posX + ran1, posY + 1.0, posZ + ran2,
                1, 0.0, 0.0, 0.0, 0.0
            )
        }
    }

    /** Spawns the legacy heal sparkle beam. */
    fun spawnLegacyHealParticles() {
        val serverLevel = ship.level() as? ServerLevel ?: return
        val beamHeight = ship.bbHeight * 0.4
        val beamRiseSpeed = 0.1
        val beamFad = ship.bbWidth * 1.5

        serverLevel.sendParticles<SimpleParticleType?>(
            ModParticles.PARTICLE_HEAL_SPARKLE.get(),
            ship.x, ship.y, ship.z,
            0,
            beamFad, beamRiseSpeed, beamHeight, 1.0
        )
    }

    /** Spawns the goddess-protection beam. */
    fun spawnGoddessParticles() {
        val serverLevel = ship.level() as? ServerLevel ?: return
        val beamHeight = ship.bbHeight * 0.4
        val beamRiseSpeed = 0.03
        val beamFad = ship.bbWidth * 2.0

        serverLevel.sendParticles<SimpleParticleType?>(
            ModParticles.PARTICLE_GODDESS.get(),
            ship.x, ship.y, ship.z,
            0,
            beamFad, beamRiseSpeed, beamHeight, 1.0
        )
    }

    /** Spawns a combat text particle (miss / hit / dodge etc.). */
    fun spawnCombatTextParticle(type: Int) {
        val serverLevel = ship.level() as? ServerLevel ?: return
        val clampedType = net.minecraft.util.Mth.clamp(
            type,
            EntityShipBase.COMBAT_TEXT_MISS,
            EntityShipBase.COMBAT_TEXT_DODGE
        )
        serverLevel.sendParticles<SimpleParticleType?>(
            ModParticles.PARTICLE_TEXTS.get(),
            ship.x,
            ship.y + ship.bbHeight * 1.3,
            ship.z,
            0,
            clampedType.toDouble(),
            0.08,
            kotlin.math.max(0.2, ship.bbWidth * 0.45),
            1.0
        )
    }

    /** Spawns a flare particle around [target] and applies glowing. */
    fun spawnFlareTarget(target: Entity?) {
        if (target == null || ship.getStateMinor(EntityShipBase.STATE_MINOR_EQUIP_FLARE) <= 0) return
        val serverLevel = ship.level() as? ServerLevel ?: return

        val posX = target.x
        val posY = target.y + target.bbHeight * 0.5
        val posZ = target.z
        serverLevel.sendParticles<SimpleParticleType?>(
            ParticleTypes.FIREWORK,
            posX, posY, posZ,
            12, 0.5, 0.6, 0.5, 0.05
        )

        if (target is LivingEntity) {
            target.addEffect(
                MobEffectInstance(
                    MobEffects.GLOWING,
                    EntityShipBase.SPECIAL_EQUIP_FLARE_GLOW_TICKS, 0, false, true, true
                ), ship
            )
        }

        TemporaryLightService.refreshLight(serverLevel, target.blockPosition(), ship.uuid)
    }

    companion object {
        private const val LEVEL_UP_VOLUME = 0.75f
        private const val FEED_SOUND_COOLDOWN_TICKS = 30
    }
}
