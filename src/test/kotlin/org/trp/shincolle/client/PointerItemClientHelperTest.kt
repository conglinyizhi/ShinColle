package org.trp.shincolle.client

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import org.junit.jupiter.api.Test
import org.trp.shincolle.attachment.AdmiralData

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.UUID

import org.junit.jupiter.api.Assertions.assertTrue

class PointerItemClientHelperTest {

    @Test
    fun pointerClientHelperShouldRenderNoSignalFallbackWhenShipCannotBeResolved() {
        val line: Component = invokeResolveShipLine(allocateMinecraftShell(), AdmiralData(), UUID.randomUUID())

        assertTrue(line.getString().contains("|||"))
        assertTrue(line.getString().contains("gui.shincolle.formation.nosignal")
                || line.getString().lowercase().contains("no signal"))
    }

    private fun invokeResolveShipLine(mc: Minecraft, data: AdmiralData, uuid: UUID): Component {
        val method: Method = PointerItemClientHelper::class.java.getDeclaredMethod(
                "resolveShipLine",
                Minecraft::class.java,
                AdmiralData::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                UUID::class.java
        )
        method.isAccessible = true
        val instance: Any = PointerItemClientHelper::class.java.getDeclaredField("INSTANCE").get(null)
        return method.invoke(instance, mc, data, 0, 0, 1, uuid) as Component
    }

    private fun allocateMinecraftShell(): Minecraft {
        val unsafeField: Field = sun.misc.Unsafe::class.java.getDeclaredField("theUnsafe")
        unsafeField.isAccessible = true
        val unsafe: sun.misc.Unsafe = unsafeField.get(null) as sun.misc.Unsafe
        return unsafe.allocateInstance(Minecraft::class.java) as Minecraft
    }
}
