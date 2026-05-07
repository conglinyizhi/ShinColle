package org.trp.shincolle.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.entity.base.EntitySummonBase;

public class KaitaiHammerItem extends Item {

    public KaitaiHammerItem(Properties properties) {
        super(properties.stacksTo(1).durability(20));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level().isClientSide) {
            boolean isShip = entity instanceof EntityShipBase;
            boolean isSummon = entity instanceof EntitySummonBase;

            if (isShip || isSummon) {
                TamableAnimal tamable = (TamableAnimal) entity;
                if (tamable.isOwnedBy(player) || player.hasPermissions(2)) {
                    if (isShip) {
                        entity.hurt(player.damageSources().fellOutOfWorld(), Float.MAX_VALUE);
                    } else {
                        entity.discard();
                    }

                    if (!player.getAbilities().instabuild) {
                        stack.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
