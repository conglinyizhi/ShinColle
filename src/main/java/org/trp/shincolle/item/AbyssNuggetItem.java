package org.trp.shincolle.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class AbyssNuggetItem extends Item {
    private static final String TAG_VARIANT = "LegacyVariant";

    public AbyssNuggetItem(Properties properties) {
        super(properties);
    }

    public int getVariant(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return 0;
        }
        return Mth.clamp(customData.copyTag().getInt(TAG_VARIANT), 0, 1);
    }

    public int getModelVariant(ItemStack stack) {
        return getVariant(stack);
    }

    public ItemStack createVariantStack(int variant) {
        int clamped = Mth.clamp(variant, 0, 1);
        ItemStack stack = new ItemStack(this);
        if (clamped > 0) {
            stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY,
                    data -> data.update(tag -> tag.putInt(TAG_VARIANT, clamped)));
        }
        return stack;
    }

    public void addAllVariantsToCreativeTab(CreativeModeTab.Output output) {
        output.accept(createVariantStack(0));
        output.accept(createVariantStack(1));
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(getVariant(stack) == 0
                ? "item.shincolle.AbyssNugget.name"
                : "item.shincolle.AbyssNugget1.name");
    }
}
