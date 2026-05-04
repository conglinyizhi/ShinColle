package org.trp.shincolle.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.trp.shincolle.entity.base.EntityMountBase;

public class EntityMountBaH extends EntityMountBase {

    public EntityMountBaH(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setSeatPos(1.05f, 3.0f, 0.0f);
        this.setSeatPos2(1.2f, 0.7f, -1.3f);
    }

    @Override
    protected void updateClientLogic() {
        super.updateClientLogic();
        if (this.tickCount % 4 == 0) {
            this.level().addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, this.getX(), this.getY() + 3.0, this.getZ(), 0.0, 0.0, 0.0);
        }
    }
}
