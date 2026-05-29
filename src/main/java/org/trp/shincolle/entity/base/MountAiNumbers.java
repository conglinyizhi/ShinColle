package org.trp.shincolle.entity.base;

final class MountAiNumbers {
    static final double SHIP_FLOATING_DEPTH = 0.3D;
    static final double BUOY_MIN_DEPTH = 0.15D;
    static final double BUOY_COEFF = 0.035D;
    static final double BUOY_EXPONENT = 0.6D;
    static final double BUOY_OFFSET = 0.005D;
    static final double BUOY_DAMP = 0.80D;
    static final double BUOY_MAX_MOTION = 0.1D;

    static final int FOLLOW_TELEPORT_COOLDOWN_TICKS = 100;
    static final int FOLLOW_STUCK_TICK_LIMIT = 120;
    static final double FOLLOW_TELEPORT_DISTANCE_SQ = 256.0D;
    static final double FOLLOW_MOVE_SPEED = 1.0D;
    static final double FOLLOW_WIDTH_PADDING = 0.75D;
    static final double TELEPORT_VERTICAL_OFFSET = 0.75D;

    static final float LOOK_YAW = 30.0F;
    static final float LOOK_PITCH = 30.0F;
    static final int FOLLOW_BEHAVIOR_PRIORITY = 2;
    static final int RANGE_ATTACK_BEHAVIOR_PRIORITY = 11;
    static final int RANDOM_STROLL_BEHAVIOR_PRIORITY = 25;
    static final double RANGE_ATTACK_MOVE_SPEED = 0.8D;
    static final double RANDOM_STROLL_SPEED = 0.8D;
    static final int RANDOM_STROLL_CHANCE = 120;
    static final int RANDOM_STROLL_HORIZONTAL_RANGE = 10;
    static final int RANDOM_STROLL_VERTICAL_RANGE = 7;

    static final int STOP_SHIP_AI_AIR_SUPPLY = 300;
    static final int SERVER_SYNC_INTERVAL_MASK = 0x1F;
    static final int AIR_SUPPLY_INTERVAL_MASK = 0x7F;
    static final double RIDER_INTERACT_DISTANCE_SQ = 16.0D;
    static final double ROTATION_EPSILON = 0.001D;
    static final double SUBMARINE_DISABLE_DEPTH = 0.4D;
    static final double PARTICLE_MOTION_LIMIT = 0.25D;
    static final int PARTICLE_BASE_AMOUNT = 2;
    static final int PARTICLE_RANDOM_AMOUNT = 3;
    static final double PARTICLE_TRAIL_SCALE = 3.0D;
    static final double PARTICLE_WIDTH_CENTER_OFFSET = 0.5D;
    static final double PARTICLE_WIDTH_Y_SCALE = 0.15D;
    static final double PARTICLE_Y_OFFSET = 0.6D;
    static final double PARTICLE_SPEED_SCALE = 1.5D;
    static final int WATER_PARTICLE_INTERVAL = 2;
    static final float FORWARD_BRAKE_FACTOR = 0.25F;
    static final float STRAFE_FACTOR = 0.5F;
    static final float SUBMARINE_PITCH_THRESHOLD = 60.0F;

    static final double HOST_MAX_HEALTH_SCALE = 0.5D;
    static final double FOLLOW_RANGE_ATTR = 64.0D;
    static final double BASE_MAX_HEALTH = 4.0D;
    static final double BASE_MOVEMENT_SPEED = 0.3D;
    static final double BASE_KNOCKBACK_RESISTANCE = 1.0D;
    static final double BASE_STEP_HEIGHT = 3.0D;
    static final double BASE_ATTACK_DAMAGE = 2.0D;

    static final int AIM_BASE_TICKS = 10;
    static final float AIM_SCALE_TICKS = 20.0F;
    static final int LEVEL_CAP = 150;
    static final double MIN_ATTACK_RANGE = 1.0D;

    private MountAiNumbers() {
    }
}
