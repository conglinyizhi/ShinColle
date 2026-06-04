package org.trp.shincolle.network

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.PacketFlow
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.network.ConfigurationTask
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.common.extensions.ICommonPacketListener
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.lang.reflect.Proxy
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

class PayloadClientSyncTest {

    @Test
    fun s2cAdmiralSyncShouldIgnoreMissingClientPlayer() {
        invokeHandler(
            "handleAdmiralDataSync",
            S2CAdmiralDataSyncPayload(CompoundTag(), intArrayOf(1, 2, 3)),
            nullPlayerPayloadContext(PacketFlow.CLIENTBOUND)
        )
    }

    @Test
    fun s2cDeskDiplomacySyncShouldUpdateClientCacheWithoutPlayer() {
        DeskDiplomacySync.clear()
        val owner = UUID.randomUUID()
        val ally = UUID.randomUUID()
        val banned = UUID.randomUUID()
        val display = UUID.randomUUID()

        invokeHandler(
            "handleDeskDiplomacySync",
            S2CDeskDiplomacySyncPayload(
                owner,
                arrayOf(ally),
                arrayOf(banned),
                arrayOf(display),
                arrayOf("Alpha"),
                arrayOf("Leader")
            ),
            nullPlayerPayloadContext(PacketFlow.CLIENTBOUND)
        )

        assertEquals(owner, DeskDiplomacySync.getOwnerUuid())
        assertTrue(DeskDiplomacySync.isAlly(ally))
        assertTrue(DeskDiplomacySync.isBanned(banned))
        assertEquals("Alpha", DeskDiplomacySync.getTeamName(display))
        assertEquals("Leader", DeskDiplomacySync.getLeaderName(display))
        assertFalse(DeskDiplomacySync.isAlly(UUID.randomUUID()))
    }

    private fun invokeHandler(name: String, payload: Any, context: IPayloadContext) {
        val method = ModNetwork::class.java.getDeclaredMethod(name, payload.javaClass, IPayloadContext::class.java)
        method.isAccessible = true
        method.invoke(null, payload, context)
    }

    private fun nullPlayerPayloadContext(flow: PacketFlow): IPayloadContext {
        return Proxy.newProxyInstance(
            IPayloadContext::class.java.classLoader,
            arrayOf(IPayloadContext::class.java)
        ) { _, method, args ->
            when (method.name) {
                "listener" -> throw UnsupportedOperationException("listener not needed for payload guard tests")
                "player" -> null
                "enqueueWork" -> {
                    val arg = args?.firstOrNull()
                    when (arg) {
                        is Runnable -> {
                            arg.run()
                            CompletableFuture.completedFuture(null)
                        }
                        is Supplier<*> -> CompletableFuture.completedFuture(arg.get())
                        else -> throw UnsupportedOperationException("unsupported enqueueWork payload: $arg")
                    }
                }
                "flow" -> flow
                "handle" -> null
                "finishCurrentTask" -> null
                else -> throw UnsupportedOperationException("unsupported method: ${method.name}")
            }
        } as IPayloadContext
    }
}
