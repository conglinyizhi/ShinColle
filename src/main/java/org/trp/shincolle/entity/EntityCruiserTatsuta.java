package org.trp.shincolle.entity;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.init.ModItems;

import java.util.List;

public class EntityCruiserTatsuta extends EntityShipBase {

    public static final String EQUIP_RIGGING = "equip_rigging";
    public static final String EQUIP_RING = "equip_ring";
    public static final String EQUIP_SIDE = "equip_side";

    public EntityCruiserTatsuta(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        setModelPos(new float[]{0, 22, 0, 42});
        setStateMinor(STATE_MINOR_FACTION_ID, 1);
        setStateMinor(STATE_MINOR_SHIP_CLASS, 57);
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 4);
        setStateMinor(STATE_MINOR_RARITY, 3);
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, org.trp.shincolle.Config.fuelConsumeCL);
        setStateGuiBtn3(false);
        setStateGuiBtn4(false);
    }

    @Override
    protected void tickAliveLogic() {
        super.tickAliveLogic();

        if ((this.tickCount % 128) == 0) {
            updateServerLogic();
        }
    }

    private void updateServerLogic() {
        if (!this.level().isDay() && this.isStateMarried() && this.isStateRingEffect() && this.getStateMinor(6) > 0) {
            if (this.getOwnerPlayer() != null && this.distanceToSqr(this.getOwnerPlayer()) < 256.0D) {
                this.getOwnerPlayer().addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, org.trp.shincolle.Config.SHIP_BUFF_DURATION.get(), 0, false, false));
            }
        }
    }

    @Override
    public List<EquipOption> getEquipOptions() {
        List<EquipOption> list = new java.util.ArrayList<>(super.getEquipOptions());
        list.add(new EquipOption(EQUIP_RIGGING, "gui.shincolle.equip.rigging"));
        list.add(new EquipOption(EQUIP_RING, "gui.shincolle.equip.ring"));
        list.add(new EquipOption(EQUIP_SIDE, "gui.shincolle.equip.side"));
        return list;
    }

    @Override
    protected Item getShipSpawnEggItem() {
        return ModItems.CRUISER_TATSUTA_SPAWN_EGG.get();
    }
}

