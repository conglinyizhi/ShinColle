package org.trp.shincolle.client

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.trp.shincolle.block.entity.IWaypoint
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.init.ModParticles
import org.trp.shincolle.item.PointerItem
import org.trp.shincolle.item.PointerItem.getMode
import org.trp.shincolle.item.TargetWrenchItem

object WaypointClientHelper {
    @JvmStatic
    fun tickClient(level: Level, pos: BlockPos, be: IWaypoint, tickCount: Int) {
        val localPlayer: Player? = Minecraft.getInstance().player
        if (localPlayer == null) return

        val playerWatching =
            isWatchingItem(localPlayer.getMainHandItem()) || isWatchingItem(localPlayer.getOffhandItem())
        if (!playerWatching) return

        if ((tickCount and 7) == 0) {
            if (be.showBaseParticle()) {
                level.addParticle(
                    ModParticles.PARTICLE_WAYPOINT.get(),
                    pos.getX() + 0.5, pos.getY() - 0.25, pos.getZ() + 0.5,
                    0.2, 0.0, 0.0
                )
            }

            if ((tickCount and 15) == 0) {
                val next = be.nextPos
                if (next != null && next != BlockPos.ZERO) {
                    val dx = (next.getX() - pos.getX()) * 0.01
                    val dy = (next.getY() - pos.getY()) * 0.01
                    val dz = (next.getZ() - pos.getZ()) * 0.01
                    level.addParticle(
                        ModParticles.PARTICLE_WAYPOINT_LINE.get(),
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        dx, dy, dz
                    )
                }
                val chest = be.chestPos
                if (chest != null && chest != BlockPos.ZERO) {
                    val dx = (chest.getX() - pos.getX()) * 0.01
                    val dy = (chest.getY() - pos.getY()) * 0.01
                    val dz = (chest.getZ() - pos.getZ()) * 0.01
                    level.addParticle(
                        ModParticles.PARTICLE_WAYPOINT_LINE_PURPLE.get(),
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        dx, dy, dz
                    )
                }

                if ((tickCount and 31) == 0) {
                    if (be.showBaseParticle()) {
                        level.addParticle(
                            ModParticles.PARTICLE_WAYPOINT.get(),
                            pos.getX() + 0.5, pos.getY() - 0.25, pos.getZ() + 0.5,
                            0.2, 0.0, 0.0
                        )
                    }

                    val sb = StringBuilder()
                    val stayTime = be.getStayTimeDisplay()
                    if (stayTime != null && !stayTime.isEmpty()) {
                        sb.append(ChatFormatting.GOLD).append(stayTime)
                    }
                    val ownerName = be.getOwnerName()
                    if (ownerName != null && !ownerName.isEmpty()) {
                        if (sb.length > 0) sb.append(" | ")
                        sb.append(ChatFormatting.GREEN).append(ownerName)
                    }
                    if (be.lastPos != BlockPos.ZERO) {
                        if (sb.length > 0) sb.append(" | ")
                        sb.append(ChatFormatting.LIGHT_PURPLE).append("F: ").append(be.lastPos.toShortString())
                    }
                    if (be.nextPos != BlockPos.ZERO) {
                        if (sb.length > 0) sb.append(" | ")
                        sb.append(ChatFormatting.AQUA).append("T: ").append(be.nextPos.toShortString())
                    }
                    if (be.chestPos != BlockPos.ZERO) {
                        if (sb.length > 0) sb.append(" | ")
                        sb.append(ChatFormatting.YELLOW).append("C: ").append(be.chestPos.toShortString())
                    }
                    if (sb.length > 0) {
                        localPlayer.displayClientMessage(Component.literal(sb.toString()), true)
                    }
                }
            }
        }
    }

    private fun isWatchingItem(stack: ItemStack): Boolean {
        if (stack.isEmpty()) return false
        if (stack.getItem() is TargetWrenchItem) return true
        if (stack.getItem() === ModItems.WAYPOINT.get()) return true
        if (stack.getItem() === ModItems.CRANE.get()) return true
        if (stack.getItem() is PointerItem) {
            val mode: Int = pointer.getMode(stack)
            return mode < 3
        }
        return false
    }
}
