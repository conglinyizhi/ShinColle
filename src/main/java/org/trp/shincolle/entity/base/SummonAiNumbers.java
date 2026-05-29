package org.trp.shincolle.entity.base;

final class SummonAiNumbers {
    static final int LIFETIME_TICKS = 1200;
    static final int RETURN_STUCK_TICK_LIMIT = 120;
    static final int RETURN_FAILSAFE_TICKS = 20 * 30;
    static final int RETURN_TELEPORT_COOLDOWN_TICKS = 100;
    static final double RETURN_TELEPORT_DISTANCE_SQ = 256.0D;
    static final double RETURN_MOVE_SPEED = 1.2D;
    static final double RETURN_REACH_DISTANCE_SQ = 4.0D;
    static final int RETURN_MIN_MISSION_TICKS = 40;
    static final int RETURN_RECOVERY_POLL_INTERVAL = 20;
    static final double TELEPORT_VERTICAL_OFFSET = 0.75D;

    static final double ATTACK_MOVE_SPEED = 1.2D;
    static final int ATTACK_BEHAVIOR_PRIORITY = 1;
    static final int FOLLOW_BEHAVIOR_PRIORITY = 2;
    static final int ATTACK_DELAY_TICKS = 20;
    static final float ATTACK_LOOK_YAW = 30.0F;
    static final float ATTACK_LOOK_PITCH = 30.0F;
    static final double FOLLOW_CARRIER_DISTANCE_SQ = 64.0D;
    static final double FOLLOW_CARRIER_SPEED = 1.2D;
    static final int FOLLOW_RECALC_TICKS = 10;
    static final double RANDOM_STROLL_SPEED = 1.0D;
    static final float LOOK_AT_PLAYER_DISTANCE = 8.0F;
    static final int LOOK_AT_PLAYER_PRIORITY = 4;
    static final int RANDOM_LOOK_PRIORITY = 5;
    static final int RANDOM_STROLL_PRIORITY = 6;
    static final int RANDOM_LOOK_CHANCE = 50;
    static final int RANDOM_STROLL_CHANCE = 120;
    static final int RANDOM_STROLL_HORIZONTAL_RANGE = 10;
    static final int RANDOM_STROLL_VERTICAL_RANGE = 7;
    static final double INIT_SUMMON_OFFSET_RANGE = 3.0D;
    static final double INIT_SUMMON_OFFSET_CENTER = 1.5D;
    static final float DEFAULT_ATTACK_RANGE_SQ = 16.0F;
    static final double FOLLOW_RANGE_ATTR = 32.0D;
    static final float HEALTH_SCALE_FACTOR = 0.2F;
    static final float HEALTH_BASE = 10.0F;
    static final float SPEED_SCALE_FACTOR = 0.1F;
    static final float SPEED_BASE = 0.25F;
    static final float DAMAGE_SCALE_FACTOR = 0.5F;
    static final float DAMAGE_MIN = 2.0F;
    static final float ATTACK_DAMAGE_DEFAULT = 4.0F;
    static final float ATTACK_DAMAGE_CARRIER_FACTOR = 0.35F;
    static final int INITIAL_LIGHT_AMMO = 6;
    static final float ATTACK_SOUND_VOLUME = 1.0F;
    static final float ATTACK_SOUND_PITCH = 1.0F;
    static final int CRIT_PARTICLE_COUNT = 8;
    static final double CRIT_PARTICLE_OFFSET = 0.2D;
    static final double CRIT_PARTICLE_SPEED = 0.1D;

    private SummonAiNumbers() {
    }
}
