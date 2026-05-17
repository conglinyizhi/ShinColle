

package net.neoforged.neoforge.common.extensions;

import java.util.OptionalInt;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.IContainerFactory;

public interface IPlayerExtension {
    private Player self() {
        return (Player) this;
    }

    
    default boolean isCloseEnough(Entity entity, double dist) {
        Vec3 eye = self().getEyePosition();
        AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
        return aabb.distanceToSqr(eye) < dist * dist;
    }

    
    default OptionalInt openMenu(MenuProvider menuProvider, BlockPos pos) {
        return openMenu(menuProvider, buf -> buf.writeBlockPos(pos));
    }

    
    default OptionalInt openMenu(MenuProvider menuProvider, Consumer<RegistryFriendlyByteBuf> extraDataWriter) {
        return OptionalInt.empty();
    }

    
    @SuppressWarnings("deprecation")
    default boolean mayFly() {
        
        return self().getAbilities().mayfly || self().getAttributeValue(NeoForgeMod.CREATIVE_FLIGHT) > 0;
    }

    
    default boolean isFakePlayer() {
        return false;
    }
}
