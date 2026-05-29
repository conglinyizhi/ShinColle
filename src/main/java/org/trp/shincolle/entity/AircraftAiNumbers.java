package org.trp.shincolle.entity;

final class AircraftAiNumbers {
    static final int LIFETIME_TICKS = 1200;
    static final int HOST_CHECK_TIMEOUT = 20;
    static final int INITIAL_BOOST_DURATION = 34;
    static final double INITIAL_BOOST_SPEED = 0.375D;
    static final double INITIAL_BOOST_Y = 0.1D;
    static final int TARGETING_INTERVAL = 16;
    static final int RETURN_HOME_CHECK_INTERVAL = 16;
    static final int RETURN_HOME_STUCK_TICK_LIMIT = 120;
    static final int RETURN_HOME_FAILSAFE_TICKS = 20 * 30;
    static final int RETURN_HOME_TELEPORT_COOLDOWN_TICKS = 100;
    static final double RETURN_HOME_TELEPORT_DISTANCE_SQ = 256.0D;
    static final double TARGETING_RANGE_NORMAL = 24.0D;
    static final double TARGETING_RANGE_AIR_ONLY = 32.0D;

    static final float ATTACK_RANGE_LIGHT = 6.0F;
    static final float ATTACK_RANGE_HEAVY = 16.0F;
    static final double RAND_POS_MIN_LIGHT = 4.5D;
    static final double RAND_POS_RAND_LIGHT = 1.5D;
    static final double RAND_POS_MIN_HEAVY = 12.0D;
    static final double RAND_POS_RAND_HEAVY = 4.0D;

    static final double DEATH_GRAVITY = 0.08D;
    static final int DEATH_TIME_BURNING = 30;
    static final int DEATH_TIME_EXPLOSION = 90;
    static final int AMMO_RETURN_PENALTY_LIGHT = 3;
    static final int AMMO_RETURN_PENALTY_HEAVY = 1;
    static final int INITIAL_AMMO_LIGHT = 9;
    static final int INITIAL_AMMO_HEAVY = 3;
    static final int BASE_ATTACK_SPEED_AIRCRAFT = 100;
    static final int FIXED_ATTACK_DELAY_AIRCRAFT = 35;

    static final int ATTACK_ACTIVATION_TICKS = 20;
    static final int ATTACK_BEHAVIOR_PRIORITY = 1;
    static final int ATTACK_RECALC_INTERVAL_MASK = 0xF;
    static final double ATTACK_TARGET_Y_OFFSET = 2.0D;
    static final double ATTACK_SPEED_SLOW = 0.3D;
    static final double ATTACK_SPEED_FAST = 0.6D;
    static final double RETURN_HOME_SPEED = 0.5D;
    static final double RETURN_HOME_EXTRA_HEIGHT = 1.0D;
    static final double RETURN_HOME_ARRIVAL_BASE = 2.0D;
    static final double RETURN_HOME_TELEPORT_EXTRA = 0.75D;
    static final int RANDOM_CRUISE_ATTEMPTS = 25;
    static final float RANDOM_CRUISE_ANGLE_STEP = 15.0F;
    static final double RANDOM_CRUISE_Y_OFFSET = 2.0D;
    static final double RANDOM_CRUISE_Y_RANDOM = 2.0D;
    static final double RANDOM_CRUISE_FALLBACK_Y = 5.0D;

    private AircraftAiNumbers() {
    }
}
