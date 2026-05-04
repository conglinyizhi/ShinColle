package org.trp.shincolle.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.trp.shincolle.entity.base.EntityMountBase;

public class EntityMountSuH extends EntityMountBase {

    public EntityMountSuH(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setSeatPos(-0.8f, 0.6f, 0.0f);
        this.setSeatPos2(0.55f, 1.2f, 0.0f);
    }
}
