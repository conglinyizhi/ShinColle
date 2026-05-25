package org.trp.shincolle.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record ShipGuardTarget(int x, int y, int z, int dimensionId, Type type) {
    public static final ShipGuardTarget NONE = new ShipGuardTarget(-1, -1, -1, 0, Type.NONE);

    public static ShipGuardTarget fromShip(EntityShipBase ship) {
        return new ShipGuardTarget(
                ship.getGuardedPos(0),
                ship.getGuardedPos(1),
                ship.getGuardedPos(2),
                ship.getGuardedPos(3),
                Type.fromLegacy(ship.getGuardedPos(4))
        );
    }

    public boolean isActive() {
        return this.type != Type.NONE;
    }

    public boolean isBlock() {
        return this.type == Type.BLOCK;
    }

    public boolean isEntity() {
        return this.type == Type.ENTITY;
    }

    public boolean isIn(Level level) {
        return this.dimensionId == EntityShipBase.getLegacyDimensionId(level);
    }

    public BlockPos blockPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    public Vec3 blockCenter() {
        return new Vec3(this.x + 0.5D, this.y, this.z + 0.5D);
    }

    public int legacyType() {
        return this.type.legacyId;
    }

    public enum Type {
        NONE(0),
        BLOCK(1),
        ENTITY(2);

        private final int legacyId;

        Type(int legacyId) {
            this.legacyId = legacyId;
        }

        public int legacyId() {
            return this.legacyId;
        }

        static Type fromLegacy(int legacyId) {
            return switch (legacyId) {
                case 1 -> BLOCK;
                case 2 -> ENTITY;
                default -> NONE;
            };
        }
    }
}
