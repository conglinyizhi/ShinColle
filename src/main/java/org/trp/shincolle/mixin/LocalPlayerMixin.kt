package org.trp.shincolle.mixin

import net.minecraft.client.player.LocalPlayer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.trp.shincolle.entity.base.EntityMountBase

@Mixin(LocalPlayer::class)
abstract class LocalPlayerMixin {
    @Inject(method = ["isControlledCamera"], at = [At("HEAD")], cancellable = true)
    private fun `shincolle$forceControlOnMount`(cir: CallbackInfoReturnable<Boolean?>) {
        val player = this as Any as LocalPlayer
        if (player.getVehicle() is EntityMountBase) {
            cir.setReturnValue(true)
        }
    }
}