package org.trp.shincolle.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;

public class OwnerPaperItem extends Item {
    private static final String SIGN_NAME_A = "SignNameA";
    private static final String SIGN_NAME_B = "SignNameB";
    private static final String SIGN_ID_A = "SignIDA";
    private static final String SIGN_ID_B = "SignIDB";
    private static final String SIGN_POS = "signPos";

    public OwnerPaperItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && !player.isShiftKeyDown()) {
            stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(tag -> {
                boolean firstWrite = !tag.contains(SIGN_NAME_A);
                if (firstWrite) {
                    tag.putString(SIGN_NAME_A, player.getName().getString());
                    tag.putString(SIGN_NAME_B, "");
                    tag.putString(SIGN_ID_A, player.getUUID().toString());
                    tag.putString(SIGN_ID_B, "");
                    tag.putBoolean(SIGN_POS, false);
                    return;
                }

                if (tag.getBoolean(SIGN_POS)) {
                    tag.putString(SIGN_NAME_A, player.getName().getString());
                    tag.putString(SIGN_ID_A, player.getUUID().toString());
                    tag.putBoolean(SIGN_POS, false);
                } else {
                    tag.putString(SIGN_NAME_B, player.getName().getString());
                    tag.putString(SIGN_ID_B, player.getUUID().toString());
                    tag.putBoolean(SIGN_POS, true);
                }
            }));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return;
        }
        var tag = customData.copyTag();
        if (tag.contains(SIGN_NAME_A)) {
            String id = tag.getString(SIGN_ID_A);
            tooltipComponents.add(Component.literal(id + " ").withStyle(ChatFormatting.RED)
                    .append(Component.literal(tag.getString(SIGN_NAME_A)).withStyle(ChatFormatting.AQUA)));
        }
        if (tag.contains(SIGN_NAME_B)) {
            String id = tag.getString(SIGN_ID_B);
            tooltipComponents.add(Component.literal(id + " ").withStyle(ChatFormatting.RED)
                    .append(Component.literal(tag.getString(SIGN_NAME_B)).withStyle(ChatFormatting.AQUA)));
        }
    }
}
