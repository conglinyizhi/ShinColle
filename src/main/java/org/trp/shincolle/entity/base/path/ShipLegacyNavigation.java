package org.trp.shincolle.entity.base.path;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.Config;

public class ShipLegacyNavigation extends GroundPathNavigation {

    // --- Constants ---
    private static final int STUCK_CHECK_INTERVAL = 32;
    private static final int STUCK_MAX_TICKS = 100;
    private static final double STUCK_DISTANCE_SQR = 1.0D;
    private static final double POSITION_TIMEOUT_MOVED_SQR = 0.25D;
    private static final float JUMP_SPRINT_FACTOR = 0.35F;
    private static final float UNSTUCK_DIRECTION_FACTOR = 0.5F;
    private static final double LIQUID_HOVER_OFFSET = 0.08D;
    private static final int INERTIA_TICKS = 10;

    // --- Core navigation state ---
    private final ShipLegacyPathFinder pathFinder;
    private final float maxDistanceToWaypoint;
    private final int hostCeilWidth;
    private final int hostCeilHeight;
    private final int hostCeilDepth;
    private ShipLegacyPath currentPath;
    private double speedModifier;
    private BlockPos targetPos;

    // --- Time keeping ---
    private int totalTicks;

    // --- Stuck detection state ---
    private int ticksAtLastPos;
    private Vec3 lastPosCheck = Vec3.ZERO;
    private Vec3 lastPosStuck = Vec3.ZERO;

    // --- Path timeout state ---
    private long timeoutCachedNode;
    private long timeoutTimer;
    private double timeoutLimit;

    // --- Log throttling state ---
    private BlockPos loggedTargetPos;
    private BlockPos lastExceededLogTarget;
    private BlockPos lastStuckApplyLogTarget;
    private boolean loggedPathFailure;
    private int loggedPathLength = -1;
    private int lastSetPathLogTick = Integer.MIN_VALUE;
    private int lastExceededLogTick = Integer.MIN_VALUE;
    private int lastStuckApplyLogTick = Integer.MIN_VALUE;

    // --- Transient navigation flags ---
    private boolean preserveCurrentPathOnNextFailure;
    private int inertiaTicks;

    // ========================================================================
    // Construction
    // ========================================================================

    public ShipLegacyNavigation(Mob mob, Level level) {
        super(mob, level);
        this.pathFinder = new ShipLegacyPathFinder(level, false);
        this.hostCeilWidth = Mth.floor(mob.getBbWidth() + 1.0F);
        this.hostCeilHeight = Mth.floor(mob.getBbHeight() + 1.0F);
        this.hostCeilDepth = this.hostCeilWidth;
        this.maxDistanceToWaypoint = mob.getBbWidth() > 0.75F ? mob.getBbWidth() * 0.5F : 0.75F - mob.getBbWidth() * 0.5F;
    }

    // ========================================================================
    // Public API — move / stop / query
    // ========================================================================

    @Override
    public boolean moveTo(double x, double y, double z, double speed) {
        if (!canStartMove()) return false;
        BlockPos pos = BlockPos.containing(x, y, z);
        BlockPos previousTarget = this.targetPos;
        ShipLegacyPath path = getPathToPos(pos);
        return setPath(path, speed, isSameNavigationTarget(previousTarget, pos), pos);
    }

    @Override
    public boolean moveTo(Entity entity, double speed) {
        if (!canStartMove()) return false;
        BlockPos previousTarget = this.targetPos;
        BlockPos nextTarget = entity.blockPosition();
        ShipLegacyPath path = getPathToEntity(entity);
        return setPath(path, speed, isSameNavigationTarget(previousTarget, nextTarget), nextTarget);
    }

    @Override
    public void stop() {
        this.preserveCurrentPathOnNextFailure = false;
        this.currentPath = null;
        this.path = null;
    }

    @Override
    public boolean isDone() {
        return noPath();
    }

    public boolean noPath() {
        return this.currentPath == null || this.currentPath.isFinished();
    }

    public void preserveCurrentPathOnNextFailure() {
        this.preserveCurrentPathOnNextFailure = true;
    }

    // ========================================================================
    // Tick — main loop
    // ========================================================================

    public void tick() {
        this.totalTicks++;

        if (noPath()) {
            tickInertia();
            return;
        }
        this.inertiaTicks = 0;

        if (this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED) <= 1.0E-4D) {
            stop();
            return;
        }

        if (canNavigate()) {
            pathFollow();
        }

        if (noPath()) {
            return;
        }

        Vec3 target = this.currentPath.getPosition(this.mob);
        if (target == null) {
            stop();
            return;
        }

        double wantedY = resolveTargetY(target);
        this.mob.getMoveControl().setWantedPosition(target.x, wantedY, target.z, this.speedModifier);
        logNavigationTick(target, wantedY);
    }

    // ========================================================================
    // Path following
    // ========================================================================

    private void pathFollow() {
        Vec3 hostPos = getEntityPosition();
        Vec3 nextPos = this.currentPath.getCurrentPos();
        if (nextPos == null) {
            stop();
            return;
        }
        double centerX = nextPos.x + 0.5D;
        double centerZ = nextPos.z + 0.5D;
        double reach = this.maxDistanceToWaypoint + Math.min(1.25D, this.speedModifier * 0.35D);
        double dx = this.mob.getX() - centerX;
        double dz = this.mob.getZ() - centerZ;

        if (dx * dx + dz * dz <= reach * reach) {
            this.currentPath.incrementPathIndex();
        }
        checkForStuck(hostPos);
    }

    // ========================================================================
    // Navigation guards
    // ========================================================================

    private boolean canStartMove() {
        if (this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED) <= 1.0E-4D) {
            this.preserveCurrentPathOnNextFailure = false;
            return false;
        }
        if (frozenByGui()) return false;
        if (!canNavigate()) {
            this.preserveCurrentPathOnNextFailure = false;
            return false;
        }
        return true;
    }

    private boolean canNavigate() {
        return !this.mob.isPassenger()
                && (this.mob.onGround() || isInLiquid() || this.mob.isNoGravity()
                    || this.mob.fallDistance < 2.0F);
    }

    private boolean frozenByGui() {
        if (!Config.SHIP_FREEZE_WHEN_GUI_OPEN.get()) return false;
        return this.mob instanceof org.trp.shincolle.entity.base.EntityShipBase ship && ship.isGuiOpen();
    }

    // ========================================================================
    // Inertia — brief coast after path ends
    // ========================================================================

    private void tickInertia() {
        if (this.targetPos != null && this.inertiaTicks < INERTIA_TICKS) {
            this.inertiaTicks++;
            Vec3 lastTarget = Vec3.atCenterOf(this.targetPos);
            this.mob.getMoveControl().setWantedPosition(
                lastTarget.x, lastTarget.y, lastTarget.z, this.speedModifier);
        }
    }

    // ========================================================================
    // Stuck detection & recovery
    // ========================================================================

    private void checkForStuck(Vec3 hostPos) {
        double progressDistanceSqr = hostPos.distanceToSqr(this.lastPosStuck);
        if (progressDistanceSqr >= STUCK_DISTANCE_SQR) {
            this.lastPosStuck = hostPos;
            this.ticksAtLastPos = this.totalTicks;
            checkPathTimeout(hostPos);
            return;
        }

        int stationaryTicks = this.totalTicks - this.ticksAtLastPos;

        if (stationaryTicks > STUCK_CHECK_INTERVAL && shouldLogExceededCheck()) {
            logExceededCheck(hostPos);
        }

        if (stationaryTicks > STUCK_MAX_TICKS && this.currentPath != null) {
            logStuckApply(hostPos);
            applyUnstuckMotion(hostPos);
            this.lastPosStuck = hostPos;
            this.ticksAtLastPos = this.totalTicks;
            resetPathTimeoutState();
        }

        checkPathTimeout(hostPos);
    }

    private void applyUnstuckMotion(Vec3 hostPos) {
        Vec3 targetPos = this.currentPath.getPosition(this.mob);
        if (targetPos == null) return;

        double dx = targetPos.x - hostPos.x;
        double dz = targetPos.z - hostPos.z;
        double lengthSq = dx * dx + dz * dz;
        if (lengthSq < 1.0E-6D) return;

        double length = Math.sqrt(lengthSq);
        Vec3 motion = this.mob.getDeltaMovement();
        this.mob.setDeltaMovement(
                dx / length * UNSTUCK_DIRECTION_FACTOR * this.speedModifier,
                motion.y,
                dz / length * UNSTUCK_DIRECTION_FACTOR * this.speedModifier);

        if (this.mob.getRandom().nextBoolean()) {
            this.mob.getJumpControl().jump();
            float bonus = this.mob.getSpeed() * JUMP_SPRINT_FACTOR;
            this.mob.setDeltaMovement(
                    this.mob.getDeltaMovement().x + dx / length * bonus,
                    this.mob.getDeltaMovement().y,
                    this.mob.getDeltaMovement().z + dz / length * bonus);
        }
    }

    // ========================================================================
    // Path timeout — retry when stuck on same node too long
    // ========================================================================

    private void checkPathTimeout(Vec3 hostPos) {
        if (this.currentPath == null || this.currentPath.isFinished()) {
            this.timeoutTimer = 0L;
            return;
        }

        if (hostPos.distanceToSqr(this.lastPosCheck) < POSITION_TIMEOUT_MOVED_SQR) {
            if (this.timeoutCachedNode == this.currentPath.getCurrentPathIndex()) {
                this.timeoutTimer++;
                if (this.timeoutLimit <= 0.0D) {
                    Vec3 pathPos = this.currentPath.getPosition(this.mob);
                    if (pathPos != null) {
                        double dist = hostPos.distanceTo(pathPos);
                        this.timeoutLimit = ShipLegacyNavigationPolicy.calculateTimeoutLimit(dist, this.mob.getSpeed());
                    }
                }
                if (ShipLegacyNavigationPolicy.shouldRetryTimedOutPath(this.timeoutTimer, this.timeoutLimit)) {
                    ShipLegacyPath retryPath = recalculatePathToCurrentTarget();
                    if (!setPath(retryPath, this.speedModifier, true, this.targetPos)) {
                        stop();
                    }
                    resetPathTimeoutState();
                }
            } else {
                resetPathTimeoutStateTo(this.currentPath.getCurrentPathIndex());
            }
        } else {
            resetPathTimeoutStateTo(this.currentPath.getCurrentPathIndex());
        }
        this.lastPosCheck = hostPos;
    }

    // ========================================================================
    // Path resolution
    // ========================================================================

    private ShipLegacyPath getPathToEntity(Entity target) {
        if (this.currentPath != null && !this.currentPath.isFinished()) {
            ShipLegacyPathPoint finalPoint = this.currentPath.getFinalPathPoint();
            if (finalPoint != null
                    && finalPoint.getX() == target.blockPosition().getX()
                    && finalPoint.getY() == target.blockPosition().getY()
                    && finalPoint.getZ() == target.blockPosition().getZ()) {
                return this.currentPath;
            }
        }
        float range = getPathSearchRange();
        return this.pathFinder.findPath(this.mob, target, range);
    }

    private ShipLegacyPath getPathToPos(BlockPos pos) {
        if (this.currentPath != null
                && !this.currentPath.isFinished()
                && pos.equals(this.targetPos)) {
            return this.currentPath;
        }
        float range = getPathSearchRange();
        return this.pathFinder.findPath(this.mob, pos.getX(), pos.getY(), pos.getZ(), range);
    }

    private ShipLegacyPath recalculatePathToCurrentTarget() {
        if (this.targetPos == null) return null;
        float range = getPathSearchRange();
        return this.pathFinder.findPath(this.mob, this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), range);
    }

    private float getPathSearchRange() {
        double follow = this.mob.getAttributeValue(Attributes.FOLLOW_RANGE);
        return (float) Math.max(48.0D, follow);
    }

    // ========================================================================
    // Path assignment
    // ========================================================================

    private boolean setPath(ShipLegacyPath path, double speed, boolean sameNavigationTarget, BlockPos nextTarget) {
        boolean hadActivePath = this.currentPath != null && !this.currentPath.isFinished();

        if (path == null) {
            if (shouldLogSetPath(-1, true, nextTarget)) {
                logSetPath(nextTarget, true, -1);
            }
            if (!this.preserveCurrentPathOnNextFailure) {
                this.currentPath = null;
                this.path = null;
                this.targetPos = nextTarget;
            }
            this.preserveCurrentPathOnNextFailure = false;
            return false;
        }

        this.preserveCurrentPathOnNextFailure = false;
        this.targetPos = nextTarget;
        if (path == this.currentPath) {
            this.speedModifier = speed;
            return true;
        }

        this.currentPath = path;
        this.path = null;
        this.speedModifier = speed;

        Vec3 hostPos = getEntityPosition();
        if (ShipLegacyNavigationPolicy.shouldResetStuckProgress(hadActivePath, sameNavigationTarget)) {
            resetStuckProgressState(hostPos);
        }
        resetPathTimeoutState();
        logSetPath(nextTarget, false, path.getCurrentPathLength());
        return true;
    }

    // ========================================================================
    // Position helpers
    // ========================================================================

    private Vec3 getEntityPosition() {
        return new Vec3(this.mob.getX(), getPathableYPos(), this.mob.getZ());
    }

    private int getPathableYPos() {
        if (this.mob.isInWater() || this.mob.isInLava()) {
            BlockPos.MutableBlockPos pos = this.mob.blockPosition().mutable();
            int y = pos.getY();
            int scan = 0;
            while (scan++ < 16 && y < this.level.getMaxBuildHeight() && !this.level.getFluidState(pos).isEmpty()) {
                y++;
                pos.setY(y);
            }
            return y;
        }
        return Mth.floor(this.mob.getY() + 0.5D);
    }

    private double resolveTargetY(Vec3 target) {
        if (isInLiquid()) {
            return Mth.lerp(0.4D, this.mob.getY(), getLiquidHoverY(target));
        }
        BlockPos pos = BlockPos.containing(target.x, target.y - 0.5D, target.z);
        BlockState state = this.level.getBlockState(pos);
        double maxY = state.getCollisionShape(this.level, pos).max(net.minecraft.core.Direction.Axis.Y);
        if (!Double.isInfinite(maxY) && !Double.isNaN(maxY)) {
            return pos.getY() + maxY + 0.1D;
        }
        return target.y;
    }

    private double getLiquidHoverY(Vec3 target) {
        BlockPos pos = BlockPos.containing(target.x, target.y, target.z);
        FluidState fluid = this.level.getFluidState(pos);
        if (fluid.isEmpty()) {
            BlockPos below = pos.below();
            fluid = this.level.getFluidState(below);
            if (fluid.isEmpty()) return target.y;
            pos = below;
        }
        return pos.getY() + fluid.getHeight(this.level, pos) - LIQUID_HOVER_OFFSET;
    }

    private boolean isInLiquid() {
        return this.mob.isInWaterOrBubble() || this.mob.isInLava();
    }

    // ========================================================================
    // Direct path check
    // ========================================================================

    private boolean isDirectPathBetweenPoints(Vec3 from, Vec3 to, int sizeX, int sizeY, int sizeZ) {
        int x1 = Mth.floor(from.x);
        int y1 = (int) from.y;
        int z1 = Mth.floor(from.z);
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        double offsetSq = dx * dx + dy * dy + dz * dz;
        if (offsetSq < 1.0E-8D) return false;

        double invDist = 1.0D / Math.sqrt(offsetSq);
        dx *= invDist;
        dy *= invDist;
        dz *= invDist;

        if (!this.isSafeToStandAt(x1, y1, z1, sizeX + 2, sizeY + 1, sizeZ + 2, from, dx, dz)) return false;

        double unitX = 1.0D / Math.abs(dx);
        double unitY = 1.0D / Math.abs(dy);
        double unitZ = 1.0D / Math.abs(dz);
        double proX = x1 - from.x;
        double proY = y1 - from.y;
        double proZ = z1 - from.z;
        if (dx >= 0.0D) proX += 1.0D;
        if (dy >= 0.0D) proY += 1.0D;
        if (dz >= 0.0D) proZ += 1.0D;
        proX /= dx;
        proY /= dy;
        proZ /= dz;
        int dirX = dx < 0.0D ? -1 : 1;
        int dirY = dy < 0.0D ? -1 : 1;
        int dirZ = dz < 0.0D ? -1 : 1;
        int x2 = Mth.floor(to.x);
        int y2 = Mth.floor(to.y);
        int z2 = Mth.floor(to.z);
        int xIntOffset = x2 - x1;
        int yIntOffset = y2 - y1;
        int zIntOffset = z2 - z1;

        while (xIntOffset * dirX > 0 || yIntOffset * dirY > 0 || zIntOffset * dirZ > 0) {
            if (proX < proY && proX < proZ) {
                proX += unitX;
                x1 += dirX;
                xIntOffset = x2 - x1;
            } else if (proY < proX && proY < proZ) {
                proY += unitY;
                y1 += dirY;
                yIntOffset = y2 - y1;
            } else {
                proZ += unitZ;
                z1 += dirZ;
                zIntOffset = z2 - z1;
            }
            if (!this.isSafeToStandAt(x1, y1, z1, sizeX, sizeY, sizeZ, from, dx, dz)) return false;
        }
        return true;
    }

    private boolean isSafeToStandAt(int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize, Vec3 orgPos, double vecX, double vecZ) {
        int xSize2 = xOffset - xSize / 2;
        int zSize2 = zOffset - zSize / 2;
        if (!this.isPositionClear(xSize2, yOffset, zSize2, xSize, ySize, zSize, orgPos, vecX, vecZ)) return false;
        if (isInLiquid()) return true;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x1 = xSize2; x1 < xSize2 + xSize; x1++) {
            for (int z1 = zSize2; z1 < zSize2 + zSize; z1++) {
                double x2 = x1 + 0.5D - orgPos.x;
                double z2 = z1 + 0.5D - orgPos.z;
                if (x2 * vecX + z2 * vecZ < 0.0D) continue;
                pos.set(x1, yOffset - 1, z1);
                if (this.level.getBlockState(pos).isAir()) return false;
            }
        }
        return true;
    }

    private boolean isPositionClear(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3 from, double dirX, double dirZ) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int ix = x; ix < x + sizeX; ix++) {
            for (int iy = y; iy < y + sizeY; iy++) {
                for (int iz = z; iz < z + sizeZ; iz++) {
                    double deltaX = ix + 0.5D - from.x;
                    double deltaZ = iz + 0.5D - from.z;
                    if (deltaX * dirX + deltaZ * dirZ < 0.0D) continue;
                    pos.set(ix, iy, iz);
                    BlockState state = this.level.getBlockState(pos);
                    if (state.getBlock() instanceof StairBlock
                            || state.getBlock() instanceof LadderBlock
                            || state.is(BlockTags.CLIMBABLE)) return false;
                    if (!state.getCollisionShape(this.level, pos).isEmpty()) return false;
                }
            }
        }
        return true;
    }

    // ========================================================================
    // Policy delegates
    // ========================================================================

    private boolean isSameNavigationTarget(BlockPos previousTarget, BlockPos nextTarget) {
        return ShipLegacyNavigationPolicy.isSameNavigationTarget(policyTarget(previousTarget), policyTarget(nextTarget));
    }

    private boolean shouldLogSetPath(int pathLength, boolean failure, BlockPos logTarget) {
        return ShipLegacyNavigationPolicy.shouldLogSetPath(
                this.loggedPathFailure, failure,
                policyTarget(this.loggedTargetPos), policyTarget(logTarget),
                this.totalTicks, this.lastSetPathLogTick);
    }

    private boolean shouldLogExceededCheck() {
        return ShipLegacyNavigationPolicy.shouldLogNavigationEvent(
                policyTarget(this.lastExceededLogTarget), policyTarget(this.targetPos),
                this.totalTicks, this.lastExceededLogTick);
    }

    private boolean shouldLogStuckApply() {
        return ShipLegacyNavigationPolicy.shouldLogNavigationEvent(
                policyTarget(this.lastStuckApplyLogTarget), policyTarget(this.targetPos),
                this.totalTicks, this.lastStuckApplyLogTick);
    }

    private static ShipLegacyNavigationPolicy.Target policyTarget(BlockPos target) {
        if (target == null) return null;
        return new ShipLegacyNavigationPolicy.Target(target.getX(), target.getY(), target.getZ());
    }

    // ========================================================================
    // State transitions
    // ========================================================================

    private void resetStuckProgressState(Vec3 hostPos) {
        this.ticksAtLastPos = this.totalTicks;
        this.lastPosCheck = hostPos;
        this.lastPosStuck = hostPos;
    }

    private void resetPathTimeoutState() {
        this.timeoutCachedNode = 0L;
        this.timeoutTimer = 0L;
        this.timeoutLimit = 0.0D;
    }

    private void resetPathTimeoutStateTo(long nodeIndex) {
        this.timeoutCachedNode = nodeIndex;
        this.timeoutTimer = 0L;
        this.timeoutLimit = 0.0D;
    }

    // ========================================================================
    // Logging
    // ========================================================================

    private void logNavigationTick(Vec3 target, double wantedY) {
        if (this.totalTicks % 20 != 0) return;
        Vec3 pos = this.mob.position();
        double distToTarget = pos.distanceTo(new Vec3(target.x, wantedY, target.z));
        double speed = this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
        Shincolle.diagnosticLog("[SCNaviTick] mob={} speedMod={} movSpeedAttr={} distToNode={} pathIdx={}/{}",
                this.mob.getUUID(), this.speedModifier, speed, distToTarget,
                this.currentPath.getCurrentPathIndex(), this.currentPath.getCurrentPathLength());
    }

    private void logSetPath(BlockPos nextTarget, boolean failure, int pathLength) {
        if (failure) {
            Shincolle.diagnosticLog("[SCNavDiag] setPath failed mob={} targetPos={}", this.mob.getUUID(), nextTarget);
        } else {
            Shincolle.diagnosticLog("[SCNavDiag] setPath success mob={} targetPos={} speed={} pathLength={}",
                    this.mob.getUUID(), nextTarget, this.speedModifier, pathLength);
        }
        this.loggedTargetPos = nextTarget;
        this.loggedPathFailure = failure;
        this.loggedPathLength = pathLength;
        this.lastSetPathLogTick = this.totalTicks;
    }

    private void logExceededCheck(Vec3 hostPos) {
        Shincolle.diagnosticLog("[SCNavDiag] exceededCheck mob={} pos={} targetPos={}",
                this.mob.getUUID(), hostPos, this.targetPos);
        this.lastExceededLogTarget = this.targetPos;
        this.lastExceededLogTick = this.totalTicks;
    }

    private void logStuckApply(Vec3 hostPos) {
        if (!shouldLogStuckApply()) return;
        Shincolle.diagnosticLog("[SCNavDiag] stuckApply mob={} pos={} targetPos={}",
                this.mob.getUUID(), hostPos, this.targetPos);
        this.lastStuckApplyLogTarget = this.targetPos;
        this.lastStuckApplyLogTick = this.totalTicks;
    }
}
