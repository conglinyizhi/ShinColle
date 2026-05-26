package org.trp.shincolle.entity.base.path;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

final class ShipLegacyPath {

    private final ShipLegacyPathPoint[] points;
    private int currentPathIndex;

    ShipLegacyPath(ShipLegacyPathPoint[] points) {
        this.points = points;
    }

    int getCurrentPathLength() {
        return this.points.length;
    }

    int getCurrentPathIndex() {
        return this.currentPathIndex;
    }

    void setCurrentPathIndex(int currentPathIndex) {
        this.currentPathIndex = Math.max(0, Math.min(currentPathIndex, this.points.length));
    }

    void incrementPathIndex() {
        setCurrentPathIndex(this.currentPathIndex + 1);
    }

    boolean isFinished() {
        return this.currentPathIndex >= this.points.length;
    }

    ShipLegacyPathPoint getFinalPathPoint() {
        return this.points.length > 0 ? this.points[this.points.length - 1] : null;
    }

    @Nullable
    ShipLegacyPathPoint getPathPointFromIndex(int index) {
        if (index < 0 || index >= this.points.length) {
            return null;
        }

        return this.points[index];
    }

    @Nullable
    Vec3 getCurrentPos() {
        if (isFinished()) {
            return null;
        }

        ShipLegacyPathPoint point = this.points[this.currentPathIndex];
        return new Vec3(point.getX(), point.getY(), point.getZ());
    }

    @Nullable
    Vec3 getVectorFromIndex(Entity entity, int index) {
        if (index < 0 || index >= this.points.length) {
            return null;
        }

        ShipLegacyPathPoint point = this.points[index];
        double x = point.getX() + (double) ((int) (entity.getBbWidth() + 1.0F)) * 0.5D;
        double y = point.getY();
        double z = point.getZ() + (double) ((int) (entity.getBbWidth() + 1.0F)) * 0.5D;
        return new Vec3(x, y, z);
    }

    @Nullable
    Vec3 getPosition(Entity entity) {
        return getVectorFromIndex(entity, this.currentPathIndex);
    }
}
