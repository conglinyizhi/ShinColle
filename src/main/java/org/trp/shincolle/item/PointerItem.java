package org.trp.shincolle.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.trp.shincolle.menu.FormationMenu;
import org.trp.shincolle.network.C2SPointerActionPayload;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.Optional;

public class PointerItem extends Item {
    private static final String TAG_VARIANT = "LegacyVariant";

    public static final int MODE_SINGLE = 0;
    public static final int MODE_GROUP = 1;
    public static final int MODE_FORMATION = 2;

    private static final int MODE_COUNT = 3;

    public PointerItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public int getMode(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return MODE_SINGLE;
        }

        int raw = customData.copyTag().getInt(TAG_VARIANT);
        return Mth.clamp(raw, MODE_SINGLE, MODE_COUNT - 1);
    }

    public int getModelVariant(ItemStack stack) {
        return getMode(stack);
    }

    public int cycleMode(ItemStack stack) {
        int next = (getMode(stack) + 1) % MODE_COUNT;
        setMode(stack, next);
        return next;
    }

    public void setMode(ItemStack stack, int mode) {
        int clamped = Mth.clamp(mode, MODE_SINGLE, MODE_COUNT - 1);
        if (clamped == MODE_SINGLE) {
            stack.remove(DataComponents.CUSTOM_DATA);
            return;
        }

        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY,
                data -> data.update(tag -> tag.putInt(TAG_VARIANT, clamped)));
    }

    public ItemStack createVariantStack(int mode) {
        ItemStack stack = new ItemStack(this);
        setMode(stack, mode);
        return stack;
    }

    public void addAllVariantsToCreativeTab(CreativeModeTab.Output output) {
        for (int mode = MODE_SINGLE; mode < MODE_COUNT; mode++) {
            output.accept(createVariantStack(mode));
        }
    }

    public static String getModeTranslationKey(int mode) {
        return switch (mode) {
            case MODE_GROUP -> "gui.shincolle.pointer1";
            case MODE_FORMATION -> "gui.shincolle.pointer2";
            default -> "gui.shincolle.pointer0";
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (context.level() == null || !context.level().isClientSide()) {
            return;
        }
        appendClientHoverText(stack, tooltipComponents);
    }

    @SuppressWarnings("unchecked")
    private void appendClientHoverText(ItemStack stack, List<Component> tooltipComponents) {
        try {
            Class<?> helperClass = Class.forName("org.trp.shincolle.client.PointerItemClientHelper");
            helperClass.getMethod("appendHoverText", PointerItem.class, ItemStack.class, List.class)
                    .invoke(null, this, stack, tooltipComponents);
        } catch (ReflectiveOperationException ignored) {
            // Client helper is optional on server-only test/runtime paths.
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            if (player.isShiftKeyDown() && getMode(stack) == MODE_FORMATION) {
                player.openMenu(new net.minecraft.world.SimpleMenuProvider(
                        (id, inv, p) -> new FormationMenu(id, inv),
                        Component.translatable("gui.shincolle.formation.title")
                ));
                return InteractionResultHolder.success(stack);
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public net.minecraft.world.InteractionResult useOn(net.minecraft.world.item.context.UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && player.isShiftKeyDown()) {
            return net.minecraft.world.InteractionResult.PASS;
        }

        Level level = context.getLevel();
        if (level.isClientSide && player != null) {
            int mode = getMode(context.getItemInHand());
            if (mode == MODE_FORMATION) {
                BlockPos blockPos = context.getClickedPos();
                Vec3 pos = Vec3.atBottomCenterOf(blockPos).add(0, 1.0, 0);
                org.trp.shincolle.network.ModNetwork.sendToServer(new C2SPointerActionPayload(2, Optional.empty(), Optional.of(pos)));
                return net.minecraft.world.InteractionResult.SUCCESS;
            }
        }
        return net.minecraft.world.InteractionResult.PASS;
    }

    @Override
    public net.minecraft.world.InteractionResult interactLivingEntity(ItemStack stack, Player player, net.minecraft.world.entity.LivingEntity target, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            return net.minecraft.world.InteractionResult.PASS;
        }

        if (player.level().isClientSide) {
            int mode = getMode(stack);
            if (mode == MODE_FORMATION) {
                if (target instanceof org.trp.shincolle.entity.base.EntityShipBase ship && ship.isOwnedBy(player)) {
                    return player.level().isClientSide ? net.minecraft.world.InteractionResult.SUCCESS : net.minecraft.world.InteractionResult.PASS;
                } else {
                    org.trp.shincolle.network.ModNetwork.sendToServer(new C2SPointerActionPayload(1, Optional.of(target.getUUID()), Optional.empty()));
                    return net.minecraft.world.InteractionResult.SUCCESS;
                }
            }
        }
        return net.minecraft.world.InteractionResult.PASS;
    }

    public void onSwingMiss(Player player, ItemStack stack) {
        if (player.level().isClientSide) {
            org.trp.shincolle.network.ModNetwork.sendToServer(new C2SPointerActionPayload(0, Optional.empty(), Optional.empty()));
        }
    }
}
