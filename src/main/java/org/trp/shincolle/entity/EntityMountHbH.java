package org.trp.shincolle.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.trp.shincolle.entity.base.EntityMountBase;
import org.trp.shincolle.init.ModParticles;

public class EntityMountHbH extends EntityMountBase {

    public EntityMountHbH(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setSeatPos(0.0f, -0.29f, 0.0f);
        this.setSeatPos2(-1.5f, 1.06f, 0.44f);
    }

    @Override
    protected void updateClientLogic() {
        super.updateClientLogic();
        if (this.tickCount % 16 == 0) {
            this.level().addParticle(ModParticles.PARTICLE_LIGHTNING.get(), this.getX(), this.getY(), this.getZ(), 0.0, this.getId(), 0.0);
            if (this.random.nextInt(3) == 0) {
                this.level().addParticle(ModParticles.PARTICLE_LIGHTNING.get(), this.getX(), this.getY(), this.getZ(), 0.0, this.getId(), 0.0);
            }
            if (this.random.nextInt(3) == 0) {
                this.level().addParticle(ModParticles.PARTICLE_LIGHTNING.get(), this.getX(), this.getY(), this.getZ(), 0.0, this.getId(), 0.0);
            }
            if (this.random.nextInt(3) == 0) {
                this.level().addParticle(ModParticles.PARTICLE_LIGHTNING.get(), this.getX(), this.getY(), this.getZ(), 0.0, this.getId(), 0.0);
            }
        }
    }
}
