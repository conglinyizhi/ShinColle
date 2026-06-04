package org.trp.shincolle.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;
import org.trp.shincolle.init.ModItems;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TargetWrenchItemTest {

    @Test
    void targetWrenchShouldStoreAndClearMarkedWaypointCoordinates() throws Exception {
        TargetWrenchItem item = (TargetWrenchItem) ModItems.TARGET_WRENCH.get();
        ItemStack stack = new ItemStack(item);
        BlockPos marked = new BlockPos(12, 34, 56);

        assertFalse(hasMarked(item, stack));

        setMarked(item, stack, marked);

        assertTrue(hasMarked(item, stack));
        assertEquals(marked, getMarked(item, stack));

        clearMarked(item, stack);

        assertFalse(hasMarked(item, stack));
        assertEquals(BlockPos.ZERO, getMarked(item, stack));
        assertFalse(stack.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA));
    }

    @Test
    void targetWrenchTooltipShouldExposeLocalizedHintsAndMarkedPosition() throws Exception {
        TargetWrenchItem item = (TargetWrenchItem) ModItems.TARGET_WRENCH.get();
        ItemStack stack = new ItemStack(item);
        List<Component> tooltip = new ArrayList<>();

        setMarked(item, stack, new BlockPos(1, 2, 3));
        item.appendHoverText(stack, null, tooltip, null);

        assertEquals(4, tooltip.size());
        assertEquals("gui.shincolle.wrench1", translationKey(tooltip.get(0)));
        assertEquals("gui.shincolle.wrench2", translationKey(tooltip.get(1)));
        assertEquals("gui.shincolle.wrench3", translationKey(tooltip.get(2)));
        assertTrue(tooltip.get(3).getString().contains("1 2 3"));
    }

    private static String translationKey(Component component) {
        return ((TranslatableContents) component.getContents()).getKey();
    }

    private static boolean hasMarked(TargetWrenchItem item, ItemStack stack) throws Exception {
        Method method = TargetWrenchItem.class.getDeclaredMethod("hasMarked", ItemStack.class);
        method.setAccessible(true);
        return (boolean) method.invoke(item, stack);
    }

    private static BlockPos getMarked(TargetWrenchItem item, ItemStack stack) throws Exception {
        Method method = TargetWrenchItem.class.getDeclaredMethod("getMarked", ItemStack.class);
        method.setAccessible(true);
        return (BlockPos) method.invoke(item, stack);
    }

    private static void setMarked(TargetWrenchItem item, ItemStack stack, BlockPos pos) throws Exception {
        Method method = TargetWrenchItem.class.getDeclaredMethod("setMarked", ItemStack.class, BlockPos.class);
        method.setAccessible(true);
        method.invoke(item, stack, pos);
    }

    private static void clearMarked(TargetWrenchItem item, ItemStack stack) throws Exception {
        Method method = TargetWrenchItem.class.getDeclaredMethod("clearMarked", ItemStack.class);
        method.setAccessible(true);
        method.invoke(item, stack);
    }
}
