package org.trp.shincolle.event

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.material.FogType
import net.minecraft.world.phys.Vec3
import java.util.Optional
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RenderHandEvent
import net.neoforged.neoforge.client.event.ViewportEvent.RenderFog
import org.trp.shincolle.Shincolle
import org.trp.shincolle.item.PointerItem
import org.trp.shincolle.network.C2SOpToolActionPayload
import org.trp.shincolle.network.C2SFormationActionPayload
import org.trp.shincolle.network.C2SPlayerSkillPayload
import org.trp.shincolle.network.ModNetwork
import org.trp.shincolle.server.MarriageRingService.getUnderwaterFogDistanceMultiplier

@EventBusSubscriber(modid = Shincolle.MODID, value = [Dist.CLIENT])
object ClientForgeEventBusEvents {

    private var debugCooldown = 0
    private var pointerCooldown = 0
    private var optoolCooldown = 0

    @JvmStatic
    @SubscribeEvent
    fun onClientTick(event: ClientTickEvent.Post) {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return

        if (debugCooldown > 0) debugCooldown--
        if (pointerCooldown > 0) pointerCooldown--
        if (optoolCooldown > 0) optoolCooldown--

        handlePointerKeyInput(player)
        handleOPToolKeyInput(player)
        handleShipSkillKeys(player)
        handleDebugKeys(player)
    }

    /**
     * Handle ship skill keys when player is riding a mount or being ridden by a ship.
     */
    private fun handleShipSkillKeys(player: LocalPlayer) {
        val mc = Minecraft.getInstance()
        val options = mc.options

        // Check if player is in skill host mode (riding mount or being ridden by ship)
        val isRidingMount = player.vehicle is org.trp.shincolle.entity.base.EntityMountBase
        val isRiddenByShip = player.passengers.any { it is org.trp.shincolle.entity.base.EntityShipBase }
        if (!isRidingMount && !isRiddenByShip) return

        for (i in 0..3) {
            if (options.keyHotbarSlots[i].consumeClick()) {
                val hitResult = mc.hitResult
                val targetEntityUUID: Optional<java.util.UUID> = when (hitResult) {
                    is net.minecraft.world.phys.EntityHitResult -> Optional.of(hitResult.entity.uuid)
                    else -> Optional.empty()
                }
                val targetPos: Optional<Vec3> = when (hitResult) {
                    is net.minecraft.world.phys.BlockHitResult -> Optional.of(Vec3.atCenterOf(hitResult.blockPos))
                    else -> Optional.empty()
                }

                ModNetwork.sendToServer(
                    C2SPlayerSkillPayload(i, targetEntityUUID, targetPos)
                )
                break
            }
        }
    }

    /**
     * Handle pointer item key inputs:
     * - Ctrl + hotbar 1~9: change team ID
     * - Tab: toggle pointer mode (normal / caress head)
     */
    private fun handlePointerKeyInput(player: LocalPlayer) {
        val mc = Minecraft.getInstance()
        val options = mc.options

        // Check if holding pointer item
        val mainHand = player.mainHandItem
        if (mainHand.item !is PointerItem) return

        val sprintKey = options.keySprint
        if (sprintKey.isDown && pointerCooldown <= 0) {
            val originalSlot = player.inventory.selected
            // Ctrl + number key: switch team
            for (i in 0..8) {
                if (options.keyHotbarSlots[i].consumeClick()) {
                    pointerCooldown = 5
                    ModNetwork.sendToServer(
                        C2SFormationActionPayload(0, i, 0, null, Optional.empty())
                    )
                    // Restore hotbar slot to prevent item switching
                    player.inventory.selected = originalSlot
                    break
                }
            }
        }

        val playerListKey = options.keyPlayerList
        if (playerListKey.consumeClick() && pointerCooldown <= 0) {
            pointerCooldown = 5
            // Toggle caress head mode
            val customData = mainHand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag()))
            val tag = customData.copyTag()
            val currentVariant = tag.getInt(PointerItem.TAG_VARIANT)
            val newVariant = when (currentVariant) {
                1, 2 -> currentVariant + 3
                3, 4, 5 -> currentVariant - 3
                else -> 3
            }
            tag.putInt(PointerItem.TAG_VARIANT, newVariant)
            mainHand.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
        }
    }

    /**
     * Handle OP Tool key inputs:
     * - Numpad 1: add/remove target to unattackable list
     * - Numpad 2: show unattackable list
     */
    private fun handleOPToolKeyInput(player: LocalPlayer) {
        val mainHand = player.mainHandItem
        if (mainHand.item !is org.trp.shincolle.item.OPToolItem) return

        val window = Minecraft.getInstance().window.window

        // Numpad 1: toggle unattackable target
        if (InputConstants.isKeyDown(window, InputConstants.KEY_NUMPAD1) && optoolCooldown <= 0) {
            optoolCooldown = 5
            val targetEntity = when (val hitResult = Minecraft.getInstance().hitResult) {
                is net.minecraft.world.phys.EntityHitResult -> Optional.of(hitResult.entity.uuid)
                else -> Optional.empty()
            }
            if (targetEntity.isPresent) {
                ModNetwork.sendToServer(
                    C2SOpToolActionPayload(C2SOpToolActionPayload.ACTION_TOGGLE_UNATTACKABLE_TARGET, targetEntity)
                )
            }
        }

        // Numpad 2: show unattackable list
        if (InputConstants.isKeyDown(window, InputConstants.KEY_NUMPAD2) && optoolCooldown <= 0) {
            optoolCooldown = 20
            ModNetwork.sendToServer(
                C2SOpToolActionPayload(
                    C2SOpToolActionPayload.ACTION_SHOW_UNATTACKABLE_TARGETS,
                    Optional.empty()
                )
            )
        }
    }

    /**
     * Handle debug keys.
     */
    private fun handleDebugKeys(player: LocalPlayer) {
        if (debugCooldown > 0) return
        val window = Minecraft.getInstance().window.window
        val ctrl = InputConstants.isKeyDown(window, InputConstants.KEY_LCONTROL)
        val lalt = InputConstants.isKeyDown(window, InputConstants.KEY_LALT)

        if (ctrl && lalt) {
            // Debug key combinations (reserved for future use)
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onRenderHand(event: RenderHandEvent) {
        if (event.hand != InteractionHand.MAIN_HAND) return

        val mc = Minecraft.getInstance()
        val player = mc.player ?: return
        val stack = event.itemStack

        if (stack.item is PointerItem) {
            val customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag()))
            val variant = customData.copyTag().getInt(PointerItem.TAG_VARIANT)
            if (variant > 2) {
                // Cancel default rendering for caress-head mode
                event.isCanceled = true
                // TODO: custom first-person arm rendering
            }
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onRenderFog(event: RenderFog) {
        if (event.type != FogType.WATER) {
            return
        }

        val player = Minecraft.getInstance().player
        if (player == null) {
            return
        }

        val multiplier = getUnderwaterFogDistanceMultiplier(player)
        if (multiplier <= 1.0f) {
            return
        }

        event.scaleNearPlaneDistance(multiplier)
        event.scaleFarPlaneDistance(multiplier)
        event.setCanceled(true)
    }
}
