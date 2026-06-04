package org.trp.shincolle.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.trp.shincolle.attachment.AdmiralData;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.item.PointerItem;
import org.trp.shincolle.server.PlayerStateService;

import java.util.List;
import java.util.UUID;

public final class PointerItemClientHelper {
    private PointerItemClientHelper() {
    }

    public static void appendHoverText(PointerItem item, ItemStack stack, List<Component> tooltipComponents) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        AdmiralData data = PlayerStateService.admiralData(mc.player);
        int teamId = data.getCurrentTeamID();
        int formationId = data.getFormationID(teamId);
        int mode = item.getMode(stack);

        Component modeComp = Component.translatable(PointerItem.getModeTranslationKey(mode))
                .withStyle(modeStyle(mode));
        if (mode == PointerItem.MODE_FORMATION) {
            Component formationComp = Component.translatable("gui.shincolle.formation.format" + formationId)
                    .withStyle(ChatFormatting.GOLD);
            tooltipComponents.add(modeComp.copy().append(" : ").append(formationComp));
        } else {
            tooltipComponents.add(modeComp);
        }

        tooltipComponents.add(Component.translatable("gui.shincolle.pointer3").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("gui.shincolle.pointer4")
                .append(" " + (teamId + 1))
                .withStyle(ChatFormatting.YELLOW, ChatFormatting.UNDERLINE));

        int displayedCount = 1;
        for (int i = 0; i < AdmiralData.SLOT_COUNT; i++) {
            UUID uuid = data.getShipUUID(teamId, i);
            if (uuid == null) {
                continue;
            }

            Component shipLine = resolveShipLine(mc, data, teamId, i, displayedCount, uuid);
            tooltipComponents.add(shipLine);
            displayedCount++;
        }
    }

    private static Component resolveShipLine(Minecraft mc, AdmiralData data, int teamId, int slotId, int displayedCount, UUID uuid) {
        if (mc.level != null) {
            for (var entity : mc.level.entitiesForRendering()) {
                if (entity.getUUID().equals(uuid) && entity instanceof EntityShipBase ship) {
                    String name = ship.hasCustomName() ? ship.getCustomName().getString() : ship.getDisplayName().getString();
                    ChatFormatting color = data.isSelected(teamId, slotId) ? ChatFormatting.WHITE : ChatFormatting.GRAY;
                    return Component.literal(displayedCount + ": " + name + " - Lv " + ship.getLevel()).withStyle(color);
                }
            }
        }

        return Component.translatable("gui.shincolle.formation.nosignal")
                .withStyle(ChatFormatting.DARK_RED)
                .append(Component.literal(" |||").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.OBFUSCATED));
    }

    private static ChatFormatting modeStyle(int mode) {
        return switch (mode) {
            case PointerItem.MODE_GROUP -> ChatFormatting.RED;
            case PointerItem.MODE_FORMATION -> ChatFormatting.GOLD;
            default -> ChatFormatting.AQUA;
        };
    }
}
