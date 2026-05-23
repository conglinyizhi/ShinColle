package org.trp.shincolle.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.trp.shincolle.init.ModItems;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.entity.base.EntitySummonBase;

public class KaitaiHammerItem extends Item {

    public KaitaiHammerItem(Properties properties) {
        super(properties.stacksTo(1).durability(20));
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        ItemStack remainder = stack.copy();
        remainder.setCount(1);
        remainder.setDamageValue(remainder.getDamageValue() + 1);
        if (remainder.getDamageValue() >= remainder.getMaxDamage()) {
            return ItemStack.EMPTY;
        }
        return remainder;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level().isClientSide) {
            if (entity instanceof EntityShipBase ship) {
                if (ship.isOwnedBy(player) || player.hasPermissions(2)) {
                    ship.applyParticleEmotion(8);
                    ship.applyEmotesAOE(10.0, 6, false);
                    ship.hurt(player.damageSources().fellOutOfWorld(), Float.MAX_VALUE);
                    
                    if (!player.getAbilities().instabuild) {
                        stack.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
                    }
                    return true;
                }
            } else if (entity instanceof EntitySummonBase summon) {
                if (summon instanceof TamableAnimal tamable) {
                    if (tamable.isOwnedBy(player) || player.hasPermissions(2)) {
                        summon.discard();
                        if (!player.getAbilities().instabuild) {
                            stack.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
