package org.trp.shincolle.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.trp.shincolle.entity.base.EmotionParticleType;
import org.trp.shincolle.entity.base.EntityShipBase;

import java.util.List;

public class MarriageRingItem extends Item {

    public MarriageRingItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && isSelected && entity instanceof Player player) {
            if (player.tickCount % 64 == 0) {
                applyAuraToNearbyShips(player, level);
            }
        }
    }

    private void applyAuraToNearbyShips(Player player, Level level) {
        AABB area = player.getBoundingBox().inflate(6.0, 5.0, 6.0);
        List<EntityShipBase> nearbyShips = level.getEntitiesOfClass(EntityShipBase.class, area);

        for (EntityShipBase ship : nearbyShips) {
            if (ship == null || !ship.isAlive() || !ship.isTame() || !ship.isOwnedBy(player)) {
                continue;
            }

            ship.setEmotionPrimary(EntityShipBase.EMOTION_HAPPY);
            if (ship.getRandom().nextInt(5) == 0) {
                ship.applyParticleEmotion(EmotionParticleType.HEART);
            }
        }
    }
}
