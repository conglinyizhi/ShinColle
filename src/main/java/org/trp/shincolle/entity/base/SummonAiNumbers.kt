package org.trp.shincolle.entity.base

internal object SummonAiNumbers {
    const val LIFETIME_TICKS: Int = 1200
    const val RETURN_STUCK_TICK_LIMIT: Int = 120
    val RETURN_FAILSAFE_TICKS: Int = 20 * 30
    const val RETURN_TELEPORT_COOLDOWN_TICKS: Int = 100
    const val RETURN_TELEPORT_DISTANCE_SQ: Double = 256.0
    const val RETURN_MOVE_SPEED: Double = 1.2
    const val RETURN_REACH_DISTANCE_SQ: Double = 4.0
    const val RETURN_MIN_MISSION_TICKS: Int = 40
    const val RETURN_RECOVERY_POLL_INTERVAL: Int = 20
    const val TELEPORT_VERTICAL_OFFSET: Double = 0.75

    const val ATTACK_MOVE_SPEED: Double = 1.2
    const val ATTACK_BEHAVIOR_PRIORITY: Int = 1
    const val FOLLOW_BEHAVIOR_PRIORITY: Int = 2
    const val ATTACK_DELAY_TICKS: Int = 20
    const val ATTACK_LOOK_YAW: Float = 30.0f
    const val ATTACK_LOOK_PITCH: Float = 30.0f
    const val FOLLOW_CARRIER_DISTANCE_SQ: Double = 64.0
    const val FOLLOW_CARRIER_SPEED: Double = 1.2
    const val FOLLOW_RECALC_TICKS: Int = 10
    const val RANDOM_STROLL_SPEED: Double = 1.0
    const val LOOK_AT_PLAYER_DISTANCE: Float = 8.0f
    const val LOOK_AT_PLAYER_PRIORITY: Int = 4
    const val RANDOM_LOOK_PRIORITY: Int = 5
    const val RANDOM_STROLL_PRIORITY: Int = 6
    const val RANDOM_LOOK_CHANCE: Int = 50
    const val RANDOM_STROLL_CHANCE: Int = 120
    const val RANDOM_STROLL_HORIZONTAL_RANGE: Int = 10
    const val RANDOM_STROLL_VERTICAL_RANGE: Int = 7
    const val INIT_SUMMON_OFFSET_RANGE: Double = 3.0
    const val INIT_SUMMON_OFFSET_CENTER: Double = 1.5
    const val DEFAULT_ATTACK_RANGE_SQ: Float = 16.0f
    const val FOLLOW_RANGE_ATTR: Double = 32.0
    const val HEALTH_SCALE_FACTOR: Float = 0.2f
    const val HEALTH_BASE: Float = 10.0f
    const val SPEED_SCALE_FACTOR: Float = 0.1f
    const val SPEED_BASE: Float = 0.25f
    const val DAMAGE_SCALE_FACTOR: Float = 0.5f
    const val DAMAGE_MIN: Float = 2.0f
    const val ATTACK_DAMAGE_DEFAULT: Float = 4.0f
    const val ATTACK_DAMAGE_CARRIER_FACTOR: Float = 0.35f
    const val INITIAL_LIGHT_AMMO: Int = 6
    const val ATTACK_SOUND_VOLUME: Float = 1.0f
    const val ATTACK_SOUND_PITCH: Float = 1.0f
    const val CRIT_PARTICLE_COUNT: Int = 8
    const val CRIT_PARTICLE_OFFSET: Double = 0.2
    const val CRIT_PARTICLE_SPEED: Double = 0.1
}
