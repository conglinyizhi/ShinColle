package org.trp.shincolle.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.trp.shincolle.menu.DeskMenu;
import vazkii.patchouli.api.PatchouliAPI;

public class DeskItemBook extends Item {
    public static final ResourceLocation PATCHOULI_BOOK_ID = ResourceLocation.fromNamespaceAndPath("shincolle", "shincolle_manual");

    public DeskItemBook(Properties properties) {
        super(properties);
    }

    @Override
    public String getDescriptionId() {
        return "item.shincolle.deskitembook.name";
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        int chap = stack.getOrDefault(org.trp.shincolle.init.ModDataComponents.BOOK_CHAPTER, 0);
        int page = stack.getOrDefault(org.trp.shincolle.init.ModDataComponents.BOOK_PAGE, 0);

        if (!PatchouliAPI.get().isStub() && !PatchouliAPI.get().getBookStack(PATCHOULI_BOOK_ID).isEmpty()) {
            if (level.isClientSide) {
                PatchouliAPI.get().openBookGUI(PATCHOULI_BOOK_ID);
            } else if (player instanceof ServerPlayer serverPlayer) {
                PatchouliAPI.get().openBookGUI(serverPlayer, PATCHOULI_BOOK_ID);
            }

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new DeskMenu(id, inv, 2, chap, page),
                Component.translatable("item.shincolle.deskitembook.name")
            ), buffer -> {
                buffer.writeInt(2);
                buffer.writeInt(chap);
                buffer.writeInt(page);
            });
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
