package org.trp.shincolle.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.trp.shincolle.entity.base.EntityMountBase;

public class EntityMountIsH extends EntityMountBase {

    public EntityMountIsH(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setSeatPos(-0.32f, 1.0f, 0.0f);
        this.setSeatPos2(0.61f, 0.28f, 0.0f);
    }

    @Override
    protected void updateClientLogic() {
        super.updateClientLogic();
        if (this.tickCount % 8 == 0) {
            float radians = this.getYRot() * (float) (Math.PI / 180.0);
            float cosR = (float) Math.cos(radians);
            float sinR = (float) Math.sin(radians);
            double px = this.getX() + (-0.15 * cosR - 0.65 * sinR);
            double pz = this.getZ() + (0.65 * cosR - 0.15 * sinR);
            this.level().addParticle(net.minecraft.core.particles.ParticleTypes.DRIPPING_WATER, px, this.getY() + 0.7, pz, 0.0, 0.0, 0.0);
        }
    }
}
