package org.trp.shincolle.server;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.trp.shincolle.Config;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.init.ModItems;

public final class HostileDropService {
    private HostileDropService() {
    }

    public static void handleLivingDrops(LivingDropsEvent event) {
        Entity target = event.getEntity();
        if (target.level().isClientSide || !isHostileDropTarget(target)) {
            return;
        }

        if (!target.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            return;
        }

        Entity sourceEntity = event.getSource().getEntity();
        if (sourceEntity instanceof EntityShipBase ship) {
            ship.addShipExp(Config.shipExpGainKill);
        }

        float dropRate = Math.max(0.0F, Config.hostileDropGrudgeRate);
        if (dropRate <= 0.0F) {
            return;
        }

        int fixedDrop = (int) dropRate;
        if (fixedDrop > 0) {
            event.getDrops().add(new ItemEntity(target.level(),
                    target.getX(), target.getY(), target.getZ(), new ItemStack(ModItems.GRUDGE.get(), fixedDrop)));
        }

        if (target.getRandom().nextFloat() < (dropRate - fixedDrop)) {
            event.getDrops().add(new ItemEntity(target.level(),
                    target.getX(), target.getY(), target.getZ(), new ItemStack(ModItems.GRUDGE.get())));
        }
    }

    private static boolean isHostileDropTarget(Entity entity) {
        if (entity instanceof EntityShipBase ship) {
            return ship.isHostileShipMob();
        }
        return entity instanceof Enemy || entity instanceof Slime || entity instanceof AbstractGolem;
    }
}
