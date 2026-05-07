package org.trp.shincolle.client.renderer;

import net.minecraft.client.model.EntityModel;
import org.trp.shincolle.client.model.ShipModelBaseAdv;
import org.trp.shincolle.entity.base.EntityShipBase;

public final class LegacyScale {
    private static final float DEFAULT = 0.34f;

    private LegacyScale() {
    }

    public static float getScale(net.minecraft.world.entity.LivingEntity entity, EntityModel<?> model) {
        if (entity instanceof EntityShipBase ship && model instanceof ShipModelBaseAdv<?> shipModel) {
            return shipModel.getLegacyScale(ship);
        }
        if (entity instanceof org.trp.shincolle.entity.base.EntityShincolleSimpleMob simpleMob) {
            float base = 0.34f;
            if (simpleMob instanceof org.trp.shincolle.entity.EntityRensouhou || simpleMob instanceof org.trp.shincolle.entity.EntityRensouhouS) {
                base = 0.4f;
            }
            return base * (simpleMob.getScaleLevel() + 1);
        }
        return DEFAULT;
    }
}
