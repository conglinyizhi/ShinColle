package org.trp.shincolle.network

import net.minecraft.network.protocol.PacketFlow
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.network.ConfigurationTask
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.extensions.ICommonPacketListener
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.junit.jupiter.api.Test

import java.lang.reflect.Method
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

class PayloadPlayerGuardTest {

    @Test
    fun c2sPayloadHandlersShouldIgnoreMissingServerPlayer() {
        val context: IPayloadContext = NullPlayerPayloadContext()

        invokeHandler("handleBookState", C2SBookStatePayload(1, 2), context)
        invokeHandler("handleDeskGui", C2SDeskGuiPayload(3, 1), context)
        invokeHandler("handleWaypointAction", C2SWaypointActionPayload(0, 1, 2, 3, 4, 5, 6), context)
        invokeHandler("handlePointerAction", C2SPointerActionPayload(0, Optional.of(UUID.randomUUID()), Optional.of(Vec3.ZERO)), context)
        invokeHandler("handleFormationAction", C2SFormationActionPayload(0, 1, 2, "Fleet", Optional.of(UUID.randomUUID())), context)
        invokeHandler("handleDeskOpenShip", C2SDeskOpenShipPayload(UUID.randomUUID()), context)
        invokeHandler("handleDeskSummon", C2SDeskSummonPayload(mutableListOf(UUID.randomUUID())), context)
        invokeHandler("handleTeamDiplomacy", C2STeamDiplomacyPayload(C2STeamDiplomacyPayload.ACTION_ADD_ALLY, UUID.randomUUID()), context)
    }

    private fun invokeHandler(name: String, payload: Any, context: IPayloadContext) {
        val method: Method = ModNetwork::class.java.getDeclaredMethod(name, payload.javaClass, IPayloadContext::class.java)
        method.isAccessible = true
        val instance: Any = ModNetwork::class.java.getDeclaredField("INSTANCE").get(null)
        method.invoke(instance, payload, context)
    }

    private class NullPlayerPayloadContext : IPayloadContext {
        override fun listener(): ICommonPacketListener {
            throw UnsupportedOperationException("listener not needed for null-player guard tests")
        }

        override fun player(): Player? {
            return null
        }

        override fun enqueueWork(runnable: Runnable): CompletableFuture<Void> {
            runnable.run()
            return CompletableFuture.completedFuture(null)
        }

        override fun <T> enqueueWork(supplier: Supplier<T>): CompletableFuture<T> {
            return CompletableFuture.completedFuture(supplier.get())
        }

        override fun flow(): PacketFlow {
            return PacketFlow.SERVERBOUND
        }

        override fun handle(payload: CustomPacketPayload) {
        }

        override fun finishCurrentTask(type: ConfigurationTask.Type) {
        }
    }
}
