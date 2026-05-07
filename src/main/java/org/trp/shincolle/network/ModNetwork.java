package org.trp.shincolle.network;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.init.ModDataComponents;
import org.trp.shincolle.item.DeskItemBook;

@EventBusSubscriber(modid = Shincolle.MODID)
public class ModNetwork {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Shincolle.MODID);
        registrar.playToServer(
                C2SBookStatePayload.TYPE,
                C2SBookStatePayload.STREAM_CODEC,
                ModNetwork::handleBookState
        );
    }

    private static void handleBookState(final C2SBookStatePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            ItemStack stack = player.getMainHandItem();
            if (!(stack.getItem() instanceof DeskItemBook)) {
                stack = player.getOffhandItem();
            }

            if (stack.getItem() instanceof DeskItemBook) {
                stack.set(ModDataComponents.BOOK_CHAPTER, payload.chapter());
                stack.set(ModDataComponents.BOOK_PAGE, payload.page());
            }
        });
    }
}
