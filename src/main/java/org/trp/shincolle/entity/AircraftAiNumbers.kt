package org.trp.shincolle.entity

internal object AircraftAiNumbers {
    const val LIFETIME_TICKS: Int = 1200
    const val HOST_CHECK_TIMEOUT: Int = 20
    const val INITIAL_BOOST_DURATION: Int = 34
    const val INITIAL_BOOST_SPEED: Double = 0.375
    const val INITIAL_BOOST_Y: Double = 0.1
    const val TARGETING_INTERVAL: Int = 16
    const val RETURN_HOME_CHECK_INTERVAL: Int = 16
    const val RETURN_HOME_STUCK_TICK_LIMIT: Int = 120
    val RETURN_HOME_FAILSAFE_TICKS: Int = 20 * 30
    const val RETURN_HOME_TELEPORT_COOLDOWN_TICKS: Int = 100
    const val RETURN_HOME_TELEPORT_DISTANCE_SQ: Double = 256.0
    const val TARGETING_RANGE_NORMAL: Double = 24.0
    const val TARGETING_RANGE_AIR_ONLY: Double = 32.0

    const val ATTACK_RANGE_LIGHT: Float = 6.0f
    const val ATTACK_RANGE_HEAVY: Float = 16.0f
    const val RAND_POS_MIN_LIGHT: Double = 4.5
    const val RAND_POS_RAND_LIGHT: Double = 1.5
    const val RAND_POS_MIN_HEAVY: Double = 12.0
    const val RAND_POS_RAND_HEAVY: Double = 4.0

    const val DEATH_GRAVITY: Double = 0.08
    const val DEATH_TIME_BURNING: Int = 30
    const val DEATH_TIME_EXPLOSION: Int = 90
    const val AMMO_RETURN_PENALTY_LIGHT: Int = 3
    const val AMMO_RETURN_PENALTY_HEAVY: Int = 1
    const val INITIAL_AMMO_LIGHT: Int = 9
    const val INITIAL_AMMO_HEAVY: Int = 3
    const val BASE_ATTACK_SPEED_AIRCRAFT: Int = 100
    const val FIXED_ATTACK_DELAY_AIRCRAFT: Int = 35

    const val ATTACK_ACTIVATION_TICKS: Int = 20
    const val ATTACK_BEHAVIOR_PRIORITY: Int = 1
    const val ATTACK_RECALC_INTERVAL_MASK: Int = 0xF
    const val ATTACK_TARGET_Y_OFFSET: Double = 2.0
    const val ATTACK_SPEED_SLOW: Double = 0.3
    const val ATTACK_SPEED_FAST: Double = 0.6
    const val RETURN_HOME_SPEED: Double = 0.5
    const val RETURN_HOME_EXTRA_HEIGHT: Double = 1.0
    const val RETURN_HOME_ARRIVAL_BASE: Double = 2.0
    const val RETURN_HOME_TELEPORT_EXTRA: Double = 0.75
    const val RANDOM_CRUISE_ATTEMPTS: Int = 25
    const val RANDOM_CRUISE_ANGLE_STEP: Float = 15.0f
    const val RANDOM_CRUISE_Y_OFFSET: Double = 2.0
    const val RANDOM_CRUISE_Y_RANDOM: Double = 2.0
    const val RANDOM_CRUISE_FALLBACK_Y: Double = 5.0
}
