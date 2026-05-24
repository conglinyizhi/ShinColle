package org.trp.shincolle.entity;

import org.trp.shincolle.entity.base.EntityShipBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.trp.shincolle.entity.base.EntitySummonBase;

public class EntityRensouhou extends EntitySummonBase {
    private static final int AMMO_RETURN_PENALTY_LIGHT = 2;
    private static final int MAX_RENSOUHOU = 6;

    public EntityRensouhou(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    @Override
    protected void returnSummonResources(EntityShipBase carrier) {
        if (carrier instanceof IShipSummonAttack summonAttack) {
            summonAttack.setNumServant(Math.min(MAX_RENSOUHOU, summonAttack.getNumServant() + 1));
        }

        int returnLight = Math.max(0, this.numAmmoLight - AMMO_RETURN_PENALTY_LIGHT);
        if (returnLight > 0) {
            carrier.setAmmoLight(carrier.getAmmoLight() + returnLight);
        }
    }
}
