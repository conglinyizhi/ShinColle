package org.trp.shincolle.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.trp.shincolle.entity.base.EntityMountBase;

public class EntityMountCaWD extends EntityMountBase {

    public EntityMountCaWD(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setSeatPos(0.25f, 1.6f, 0.0f);
        this.setSeatPos2(0.14f, -0.39f, 0.0f);
    }
}
