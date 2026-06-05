package org.trp.shincolle.entity.base

internal object MountAiNumbers {
    const val SHIP_FLOATING_DEPTH: Double = 0.3
    const val BUOY_MIN_DEPTH: Double = 0.15
    const val BUOY_COEFF: Double = 0.035
    const val BUOY_EXPONENT: Double = 0.6
    const val BUOY_OFFSET: Double = 0.005
    const val BUOY_DAMP: Double = 0.80
    const val BUOY_MAX_MOTION: Double = 0.1

    const val FOLLOW_TELEPORT_COOLDOWN_TICKS: Int = 100
    const val FOLLOW_STUCK_TICK_LIMIT: Int = 120
    const val FOLLOW_TELEPORT_DISTANCE_SQ: Double = 256.0
    const val FOLLOW_MOVE_SPEED: Double = 1.0
    const val FOLLOW_WIDTH_PADDING: Double = 0.75
    const val TELEPORT_VERTICAL_OFFSET: Double = 0.75

    const val LOOK_YAW: Float = 30.0f
    const val LOOK_PITCH: Float = 30.0f
    const val FOLLOW_BEHAVIOR_PRIORITY: Int = 2
    const val RANGE_ATTACK_BEHAVIOR_PRIORITY: Int = 11
    const val RANDOM_STROLL_BEHAVIOR_PRIORITY: Int = 25
    const val RANGE_ATTACK_MOVE_SPEED: Double = 0.8
    const val RANDOM_STROLL_SPEED: Double = 0.8
    const val RANDOM_STROLL_CHANCE: Int = 120
    const val RANDOM_STROLL_HORIZONTAL_RANGE: Int = 10
    const val RANDOM_STROLL_VERTICAL_RANGE: Int = 7

    const val STOP_SHIP_AI_AIR_SUPPLY: Int = 300
    const val SERVER_SYNC_INTERVAL_MASK: Int = 0x1F
    const val AIR_SUPPLY_INTERVAL_MASK: Int = 0x7F
    const val RIDER_INTERACT_DISTANCE_SQ: Double = 16.0
    const val ROTATION_EPSILON: Double = 0.001
    const val SUBMARINE_DISABLE_DEPTH: Double = 0.4
    const val PARTICLE_MOTION_LIMIT: Double = 0.25
    const val PARTICLE_BASE_AMOUNT: Int = 2
    const val PARTICLE_RANDOM_AMOUNT: Int = 3
    const val PARTICLE_TRAIL_SCALE: Double = 3.0
    const val PARTICLE_WIDTH_CENTER_OFFSET: Double = 0.5
    const val PARTICLE_WIDTH_Y_SCALE: Double = 0.15
    const val PARTICLE_Y_OFFSET: Double = 0.6
    const val PARTICLE_SPEED_SCALE: Double = 1.5
    const val WATER_PARTICLE_INTERVAL: Int = 2
    const val FORWARD_BRAKE_FACTOR: Float = 0.25f
    const val STRAFE_FACTOR: Float = 0.5f
    const val SUBMARINE_PITCH_THRESHOLD: Float = 60.0f

    const val HOST_MAX_HEALTH_SCALE: Double = 0.5
    const val FOLLOW_RANGE_ATTR: Double = 64.0
    const val BASE_MAX_HEALTH: Double = 4.0
    const val BASE_MOVEMENT_SPEED: Double = 0.3
    const val BASE_KNOCKBACK_RESISTANCE: Double = 1.0
    const val BASE_STEP_HEIGHT: Double = 3.0
    const val BASE_ATTACK_DAMAGE: Double = 2.0

    const val AIM_BASE_TICKS: Int = 10
    const val AIM_SCALE_TICKS: Float = 20.0f
    const val LEVEL_CAP: Int = 150
    const val MIN_ATTACK_RANGE: Double = 1.0
}
