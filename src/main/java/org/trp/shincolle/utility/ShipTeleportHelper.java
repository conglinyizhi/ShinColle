package org.trp.shincolle.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class ShipTeleportHelper {

    private static final double MIN_PLAYER_DISTANCE_SQ = 9.0D;
    private static final double TARGET_REACH_DISTANCE_SQ = 144.0D;
    private static final int[][] PREFERRED_OFFSETS = {
            {-4, 0}, {-4, -2}, {-4, 2},
            {-5, 0}, {-5, -3}, {-5, 3},
            {-3, -3}, {-3, 3}, {-6, -1}, {-6, 1},
            {-2, -4}, {-2, 4}
    };
    private static final int[][] FALLBACK_OFFSETS = {
            {-7, 0}, {-7, -2}, {-7, 2},
            {-5, -5}, {-5, 5}, {-1, -5}, {-1, 5},
            {0, -6}, {0, 6}, {-8, 0}
    };
    private static final int[][] POINT_OFFSETS = {
            {0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {2, 0}, {-2, 0}, {0, 2}, {0, -2},
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {-1, 2}, {1, -2}, {-1, -2},
            {3, 0}, {-3, 0}, {0, 3}, {0, -3}
    };

    private ShipTeleportHelper() {
    }

    public static boolean teleportNearLiving(Entity entity, LivingEntity anchor, double verticalOffset) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        Vec3 basePos = new Vec3(anchor.getX(), anchor.getY() + verticalOffset, anchor.getZ());
        Vec3 facing = anchor.getLookAngle();
        Vec3 horizontalFacing = new Vec3(facing.x, 0.0D, facing.z);
        if (horizontalFacing.lengthSqr() < 1.0E-4D) {
            horizontalFacing = new Vec3(0.0D, 0.0D, 1.0D);
        } else {
            horizontalFacing = horizontalFacing.normalize();
        }
        Vec3 right = new Vec3(-horizontalFacing.z, 0.0D, horizontalFacing.x);

        Vec3 candidate = findCandidate(serverLevel, entity, anchor, basePos, horizontalFacing, right, PREFERRED_OFFSETS, true);
        if (candidate == null) {
            candidate = findCandidate(serverLevel, entity, anchor, basePos, horizontalFacing, right, FALLBACK_OFFSETS, true);
        }
        if (candidate == null) {
            candidate = findVerticalFallback(serverLevel, entity, anchor, basePos, horizontalFacing);
        }
        if (candidate == null) {
            return false;
        }

        entity.teleportTo(candidate.x, candidate.y, candidate.z);
        return true;
    }

    public static boolean teleportNearPoint(Entity entity, Vec3 anchor, double verticalOffset) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        Vec3 candidate = findPointCandidate(serverLevel, entity, anchor.add(0.0D, verticalOffset, 0.0D));
        if (candidate == null) {
            return false;
        }

        entity.teleportTo(candidate.x, candidate.y, candidate.z);
        return true;
    }

    private static Vec3 findCandidate(ServerLevel level, Entity entity, LivingEntity anchor, Vec3 basePos, Vec3 back, Vec3 right, int[][] offsets, boolean rejectFront) {
        for (int[] offset : offsets) {
            Vec3 candidate = basePos
                    .add(back.scale(offset[0]))
                    .add(right.scale(offset[1]));
            Vec3 safePos = validateCandidate(level, entity, anchor, candidate, back, rejectFront);
            if (safePos != null) {
                return safePos;
            }
        }
        return null;
    }

    private static Vec3 validateCandidate(ServerLevel level, Entity entity, LivingEntity anchor, Vec3 candidate, Vec3 facing, boolean rejectFront) {
        double dx = candidate.x - anchor.getX();
        double dz = candidate.z - anchor.getZ();
        double horizontalDistSq = dx * dx + dz * dz;
        if (horizontalDistSq < MIN_PLAYER_DISTANCE_SQ) {
            return null;
        }

        if (rejectFront) {
            double dot = dx * facing.x + dz * facing.z;
            if (dot > 0.0D) {
                return null;
            }
        }

        if (anchor.distanceToSqr(candidate) > TARGET_REACH_DISTANCE_SQ) {
            return null;
        }

        BlockPos baseBlock = BlockPos.containing(candidate.x, candidate.y, candidate.z);
        for (int dy = 2; dy >= -2; dy--) {
            BlockPos testPos = baseBlock.offset(0, dy, 0);
            if (!canStandAt(level, entity, testPos)) {
                continue;
            }
            return new Vec3(testPos.getX() + 0.5D, testPos.getY(), testPos.getZ() + 0.5D);
        }
        return null;
    }

    private static Vec3 findVerticalFallback(ServerLevel level, Entity entity, LivingEntity anchor, Vec3 basePos, Vec3 facing) {
        BlockPos baseBlock = BlockPos.containing(basePos.x, basePos.y, basePos.z);
        for (int dy = 4; dy >= -4; dy--) {
            BlockPos testPos = baseBlock.offset(0, dy, 0);
            Vec3 candidate = new Vec3(testPos.getX() + 0.5D, testPos.getY(), testPos.getZ() + 0.5D);
            if (validateCandidate(level, entity, anchor, candidate, facing, true) != null) {
                return candidate;
            }
        }
        return null;
    }

    private static Vec3 findPointCandidate(ServerLevel level, Entity entity, Vec3 basePos) {
        BlockPos baseBlock = BlockPos.containing(basePos.x, basePos.y, basePos.z);
        for (int[] offset : POINT_OFFSETS) {
            for (int dy = 2; dy >= -3; dy--) {
                BlockPos testPos = baseBlock.offset(offset[0], dy, offset[1]);
                if (!canStandAt(level, entity, testPos)) {
                    continue;
                }
                return new Vec3(testPos.getX() + 0.5D, testPos.getY(), testPos.getZ() + 0.5D);
            }
        }
        return null;
    }

    private static boolean canStandAt(Level level, Entity entity, BlockPos pos) {
        if (level.isOutsideBuildHeight(pos) || level.isOutsideBuildHeight(pos.above())) {
            return false;
        }
        if (!level.getBlockState(pos.below()).entityCanStandOn(level, pos.below(), entity)) {
            return false;
        }
        if (!level.getBlockState(pos).canBeReplaced() || !level.getBlockState(pos.above()).canBeReplaced()) {
            return false;
        }

        double x = pos.getX() + 0.5D;
        double y = pos.getY();
        double z = pos.getZ() + 0.5D;
        return level.noCollision(entity, entity.getBoundingBox().move(
                x - entity.getX(),
                y - entity.getY(),
                z - entity.getZ()
        ));
    }
}
