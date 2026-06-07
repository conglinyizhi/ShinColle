package org.trp.shincolle.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;
import org.trp.shincolle.attachment.AdmiralData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PointerItemClientHelperTest {

    @Test
    void pointerClientHelperShouldRenderNoSignalFallbackWhenShipCannotBeResolved() throws Exception {
        Component line = invokeResolveShipLine(allocateMinecraftShell(), new AdmiralData(), UUID.randomUUID());

        assertTrue(line.getString().contains("|||"));
        assertTrue(line.getString().contains("gui.shincolle.formation.nosignal")
                || line.getString().toLowerCase().contains("no signal"));
    }

    private static Component invokeResolveShipLine(Minecraft mc, AdmiralData data, UUID uuid) throws Exception {
        Method method = PointerItemClientHelper.class.getDeclaredMethod(
                "resolveShipLine",
                Minecraft.class,
                AdmiralData.class,
                int.class,
                int.class,
                int.class,
                UUID.class
        );
        method.setAccessible(true);
        Object instance = PointerItemClientHelper.class.getDeclaredField("INSTANCE").get(null);
        return (Component) method.invoke(instance, mc, data, 0, 0, 1, uuid);
    }

    private static Minecraft allocateMinecraftShell() throws Exception {
        Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        sun.misc.Unsafe unsafe = (sun.misc.Unsafe) unsafeField.get(null);
        return (Minecraft) unsafe.allocateInstance(Minecraft.class);
    }
}
