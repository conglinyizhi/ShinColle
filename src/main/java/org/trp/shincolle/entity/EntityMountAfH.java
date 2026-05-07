package org.trp.shincolle.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.trp.shincolle.entity.base.EntityMountBase;
import org.trp.shincolle.init.ModParticles;

public class EntityMountAfH extends EntityMountBase {

    public EntityMountAfH(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setSeatPos(0.59f, -0.25f, 0.0f);
        this.setSeatPos2(-0.85f, 1.0f, -1.12f);
    }

    @Override
    protected void updateClientLogic() {
        super.updateClientLogic();
        if (this.tickCount % 8 == 0) {
            float radians = this.yBodyRot * (float) (Math.PI / 180.0);
            float cosR = (float) Math.cos(radians);
            float sinR = (float) Math.sin(radians);

            double px1 = this.getX() - cosR;
            double pz1 = this.getZ() - sinR;
            double px2 = this.getX() - 1.8 * cosR;
            double pz2 = this.getZ() - 1.8 * sinR;

            this.level().addParticle(ModParticles.PARTICLE_SPRAY_RED.get(), px1, this.getY() + 0.9, pz1, 0.0, 0.1, 0.0);
            this.level().addParticle(ModParticles.PARTICLE_SPRAY_RED.get(), px2, this.getY() + 0.9, pz2, 0.0, 0.1, 0.0);
        }
    }
}
