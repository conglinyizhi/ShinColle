package org.trp.shincolle.network;

import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

class PayloadPlayerGuardTest {

    @Test
    void c2sPayloadHandlersShouldIgnoreMissingServerPlayer() throws Exception {
        IPayloadContext context = new NullPlayerPayloadContext();

        invokeHandler("handleBookState", new C2SBookStatePayload(1, 2), context);
        invokeHandler("handleDeskGui", new C2SDeskGuiPayload(3, 1), context);
        invokeHandler("handleWaypointAction", new C2SWaypointActionPayload(0, 1, 2, 3, 4, 5, 6), context);
        invokeHandler("handlePointerAction", new C2SPointerActionPayload(0, Optional.of(UUID.randomUUID()), Optional.of(Vec3.ZERO)), context);
        invokeHandler("handleFormationAction", new C2SFormationActionPayload(0, 1, 2, "Fleet", Optional.of(UUID.randomUUID())), context);
        invokeHandler("handleDeskOpenShip", new C2SDeskOpenShipPayload(UUID.randomUUID()), context);
        invokeHandler("handleDeskSummon", new C2SDeskSummonPayload(List.of(UUID.randomUUID())), context);
        invokeHandler("handleTeamDiplomacy", new C2STeamDiplomacyPayload(C2STeamDiplomacyPayload.ACTION_ADD_ALLY, UUID.randomUUID()), context);
    }

    private static void invokeHandler(String name, Object payload, IPayloadContext context) throws Exception {
        Method method = ModNetwork.class.getDeclaredMethod(name, payload.getClass(), IPayloadContext.class);
        method.setAccessible(true);
        Object instance = ModNetwork.class.getDeclaredField("INSTANCE").get(null);
        method.invoke(instance, payload, context);
    }

    private static final class NullPlayerPayloadContext implements IPayloadContext {
        @Override
        public ICommonPacketListener listener() {
            throw new UnsupportedOperationException("listener not needed for null-player guard tests");
        }

        @Override
        public Player player() {
            return null;
        }

        @Override
        public CompletableFuture<Void> enqueueWork(Runnable runnable) {
            runnable.run();
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public <T> CompletableFuture<T> enqueueWork(Supplier<T> supplier) {
            return CompletableFuture.completedFuture(supplier.get());
        }

        @Override
        public PacketFlow flow() {
            return PacketFlow.SERVERBOUND;
        }

        @Override
        public void handle(CustomPacketPayload payload) {
        }

        @Override
        public void finishCurrentTask(ConfigurationTask.Type type) {
        }
    }
}
