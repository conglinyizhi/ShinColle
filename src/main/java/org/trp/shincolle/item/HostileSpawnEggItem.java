package org.trp.shincolle.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import org.trp.shincolle.crafting.ShipyardRecipes;

import java.util.function.Supplier;

public class HostileSpawnEggItem extends DeferredSpawnEggItem {
    private final boolean largeShipyardEgg;

    public HostileSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type,
                                boolean largeShipyardEgg,
                                int primaryColor,
                                int secondaryColor,
                                Properties properties) {
        super(type, primaryColor, secondaryColor, properties);
        this.largeShipyardEgg = largeShipyardEgg;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        injectHostileEntityData(context.getLevel(), context.getItemInHand());
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        injectHostileEntityData(level, stack);
        return super.use(level, player, hand);
    }

    private void injectHostileEntityData(Level level, ItemStack stack) {
        if (level.isClientSide || stack.isEmpty()) return;
        EntityType<? extends Mob> entityType = ShipyardRecipes.rollShipEntityType(this.largeShipyardEgg, stack);
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        if (key == null) return;
        stack.update(DataComponents.ENTITY_DATA, CustomData.EMPTY, existingData -> existingData.update(tag -> {
            tag.putString("id", key.toString());
            tag.remove("Owner");
            tag.remove("Tame");
        }));
    }
}
