package org.trp.shincolle.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.trp.shincolle.entity.base.EntityShipBase;

import java.util.List;

public class DebugInspectorItem extends Item {

    private static final int MAX_CHAT_LENGTH = 8000;
    private static final String TAG_BUCKET_REPAIR_COUNT = "BucketRepairCount";
    private static final String TAG_BUCKET_REPAIR_GAME_TIME = "BucketRepairGameTime";
    private static final String TAG_BUCKET_REPAIR_SHIP = "BucketRepairShip";

    public DebugInspectorItem(Properties properties) {
        super(properties);
    }

    public static Component creativeInfiniteLabel() {
        return Component.translatable("gui.shincolle.creative_infinite").withStyle(ChatFormatting.GOLD);
    }

    public static void markBucketRepairTriggered(ItemStack stack, EntityShipBase ship) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(tag -> {
            tag.putInt(TAG_BUCKET_REPAIR_COUNT, tag.getInt(TAG_BUCKET_REPAIR_COUNT) + 1);
            tag.putLong(TAG_BUCKET_REPAIR_GAME_TIME, ship.level().getGameTime());
            tag.putString(TAG_BUCKET_REPAIR_SHIP, ship.getName().getString());
        }));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.shincolle.debug_inspector.desc").withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Component.translatable("item.shincolle.debug_inspector.desc2").withStyle(ChatFormatting.GRAY));

        var customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return;
        }

        CompoundTag tag = customData.copyTag();
        int repairCount = tag.getInt(TAG_BUCKET_REPAIR_COUNT);
        if (repairCount > 0) {
            tooltipComponents.add(Component.translatable("item.shincolle.debug_inspector.bucket_count", repairCount)
                    .withStyle(ChatFormatting.GOLD));
            String shipName = tag.getString(TAG_BUCKET_REPAIR_SHIP);
            if (!shipName.isEmpty()) {
                tooltipComponents.add(Component.translatable("item.shincolle.debug_inspector.bucket_ship", shipName)
                        .withStyle(ChatFormatting.DARK_AQUA));
            }
            long gameTime = tag.getLong(TAG_BUCKET_REPAIR_GAME_TIME);
            if (gameTime > 0L) {
                tooltipComponents.add(Component.translatable("item.shincolle.debug_inspector.bucket_time", gameTime)
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (target instanceof EntityShipBase ship) {
            inspectShip((ServerPlayer) player, ship);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static void handleItemFrameInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide) return;
        Player player = event.getEntity();
        ItemStack held = event.getItemStack();
        if (held.isEmpty() || !(held.getItem() instanceof DebugInspectorItem)) return;
        if (!(event.getTarget() instanceof ItemFrame frame)) return;

        event.setCanceled(true);

        ItemStack frameStack = frame.getItem();
        if (frameStack.isEmpty()) {
            player.displayClientMessage(Component.literal("Item frame is empty"), false);
            return;
        }

        inspectItemStack((ServerPlayer) player, frameStack);
    }

    private static void inspectShip(ServerPlayer player, EntityShipBase ship) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Ship Debug Info ===\n");
        sb.append("Type: ").append(ship.getType().builtInRegistryHolder().key().location()).append("\n");
        sb.append("UUID: ").append(ship.getUUID()).append("\n");
        sb.append("Position: ").append(formatPos(ship)).append("\n");
        sb.append("Health: ").append(String.format("%.1f / %.1f", ship.getHealth(), ship.getMaxHealth())).append("\n");
        sb.append("Owner: ").append(ship.getOwnerUUID() != null ? ship.getOwnerUUID() : "none").append("\n");
        sb.append("Tame: ").append(ship.isTame()).append("\n");
        sb.append("Level: ").append(ship.getLevel()).append("\n");
        sb.append("ShipKills: ").append(ship.getShipKills()).append("\n");
        sb.append("Fuel: ").append(ship.getFuel()).append("\n");
        sb.append("Ammo L/H: ").append(ship.getAmmoLight()).append(" / ").append(ship.getAmmoHeavy()).append("\n");
        sb.append("Morale: ").append(ship.getMorale()).append("\n");
        sb.append("Married: ").append(ship.isStateMarried()).append("\n");

        Entity target = ship.getTarget();
        if (target != null) {
            sb.append("Target: ").append(target.getType().builtInRegistryHolder().key().location()).append(" ").append(target.getUUID()).append("\n");
        } else {
            sb.append("Target: none\n");
        }

        for (int i = 0; i < 8; i++) {
            sb.append("Emotion[").append(i).append("]: ").append(ship.getStateEmotion(i)).append("\n");
        }
        for (int i = 0; i < 16; i++) {
            int v = ship.getStateMinor(i);
            if (v != 0) {
                sb.append("Minor[").append(i).append("]: ").append(v).append("\n");
            }
        }

        var nav = ship.getNavigation();
        if (nav != null) {
            sb.append("Navigation: ").append(nav.isDone() ? "idle" : "moving").append("\n");
            if (nav.getPath() != null) {
                sb.append("Path nodes: ").append(nav.getPath().getNodeCount()).append("\n");
            }
        }

        sb.append("OnGround: ").append(ship.onGround()).append("\n");
        sb.append("InWater: ").append(ship.isInWater()).append("\n");
        sb.append("Passengers: ").append(ship.getPassengers().size()).append("\n");

        sendChatWithCopy(player, sb.toString());
    }

    private static void inspectItemStack(ServerPlayer player, ItemStack stack) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Item Debug Info ===\n");
        sb.append("Item: ").append(stack.getItem()).append("\n");
        sb.append("Count: ").append(stack.getCount()).append("\n");

        var components = stack.getComponents();
        sb.append("Components (").append(components.size()).append("):\n");
        for (var entry : components) {
            sb.append("  ").append(entry.type()).append(" = ").append(entry.value()).append("\n");
        }
        var customData = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if (customData != null) {
            sb.append("CustomData: ").append(customData.copyTag()).append("\n");
        } else {
            sb.append("CustomData: null\n");
        }

        sendChatWithCopy(player, sb.toString());
    }

    private static void sendChatWithCopy(ServerPlayer player, String text) {
        String remaining = text;
        int part = 0;
        while (!remaining.isEmpty()) {
            int end = Math.min(remaining.length(), MAX_CHAT_LENGTH);
            String chunk = remaining.substring(0, end);
            remaining = remaining.substring(end);

            MutableComponent msg = Component.literal(chunk);

            if (part == 0) {
                MutableComponent copyHint = Component.literal(" [COPY]")
                        .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                        .withStyle(style -> style
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy all debug info"))));
                msg.append(copyHint);
            }

            player.sendSystemMessage(msg);
            part++;
        }
    }

    private static String formatPos(Entity entity) {
        return String.format("(%.2f, %.2f, %.2f) dim=%s",
                entity.getX(), entity.getY(), entity.getZ(),
                entity.level().dimension().location());
    }
}
