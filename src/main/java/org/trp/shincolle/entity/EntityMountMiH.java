package org.trp.shincolle.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.trp.shincolle.entity.base.EntityMountBase;

public class EntityMountMiH extends EntityMountBase {

    public EntityMountMiH(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setSeatPos(0.0f, 2.7f, 0.0f);
        this.setSeatPos2(-0.3f, 0.5f, 0.0f);
    }

    @Override
    protected void updateClientLogic() {
        super.updateClientLogic();
        if (this.tickCount % 8 == 0) {
            float radians = this.getYRot() * (float) (Math.PI / 180.0);
            float cosR = (float) Math.cos(radians);
            float sinR = (float) Math.sin(radians);
            double px = this.getX() + (-0.25 * cosR - 1.2 * sinR);
            double pz = this.getZ() + (1.2 * cosR - 0.25 * sinR);
            this.level().addParticle(net.minecraft.core.particles.ParticleTypes.DRIPPING_LAVA, px, this.getY() + 0.85, pz, 0.0, 0.0, 0.0);
        }
    }
}
