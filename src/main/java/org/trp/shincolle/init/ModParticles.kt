package org.trp.shincolle.init

import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.Registries
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.trp.shincolle.Shincolle
import java.util.function.Supplier

object ModParticles {
    val PARTICLES: DeferredRegister<ParticleType<*>?> =
        DeferredRegister.create<ParticleType<*>?>(Registries.PARTICLE_TYPE, Shincolle.MODID)

    @JvmField
    val PARTICLE_TEAM: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particleteam", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_TEAM_SELECTED: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particleteam_selected", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_TEAM_SELECTED_RED: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particleteam_selected_red", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_TEAM_SELECTED_YELLOW: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particleteam_selected_yellow", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_TEAM_TARGET: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particleteam_target", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_TEAM_TARGET_ENTITY: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particleteam_target_entity", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_EMOTION: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particleemotion", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_HEAL_SPARKLE: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particleheal_sparkle", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_TEXTS: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particletexts", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_LIGHTNING: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particle_lightning", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_SPRAY_RED: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particle_spray_red", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_SPRAY: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particle_spray", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_GODDESS: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particle_goddess", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_WAYPOINT: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particle_waypoint", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_WAYPOINT_LINE: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particle_waypoint_line", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_WAYPOINT_LINE_PURPLE: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particle_waypoint_line_purple", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_WAYPOINT_LINE_RED: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particle_waypoint_line_red", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_CRANING: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particle_craning", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_SPARKLE: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particle_sparkle", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_CHI: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particle_chi", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_91TYPE: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particle_91type", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_CUBE: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particle_cube", Supplier { SimpleParticleType(false) })

    @JvmField
    val PARTICLE_BEAM: DeferredHolder<ParticleType<*>?, SimpleParticleType?> =
        PARTICLES.register<SimpleParticleType?>("particle_beam", Supplier { SimpleParticleType(false) })
}
