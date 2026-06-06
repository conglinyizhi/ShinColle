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
import org.trp.shincolle.item.TargetWrenchItem

object WaypointClientHelper {
    @JvmStatic
    fun tickClient(level: Level, pos: BlockPos, be: IWaypoint, tickCount: Int) {
        val localPlayer: Player? = Minecraft.getInstance().player
        if (localPlayer == null) return

        val playerWatching =
            isWatchingItem(localPlayer.mainHandItem) || isWatchingItem(localPlayer.offhandItem)
        if (!playerWatching) return

        if ((tickCount and 7) == 0) {
            if (be.showBaseParticle()) {
                level.addParticle(
                    ModParticles.PARTICLE_WAYPOINT.get(),
                    pos.x + 0.5, pos.y - 0.25, pos.z + 0.5,
                    0.2, 0.0, 0.0
                )
            }

            if ((tickCount and 15) == 0) {
                val next = be.nextPos
                if (next != null && next != BlockPos.ZERO) {
                    val dx = (next.x - pos.x) * 0.01
                    val dy = (next.y - pos.y) * 0.01
                    val dz = (next.z - pos.z) * 0.01
                    level.addParticle(
                        ModParticles.PARTICLE_WAYPOINT_LINE.get(),
                        pos.x + 0.5, pos.y + 0.5, pos.z + 0.5,
                        dx, dy, dz
                    )
                }
                val chest = be.chestPos
                if (chest != null && chest != BlockPos.ZERO) {
                    val dx = (chest.x - pos.x) * 0.01
                    val dy = (chest.y - pos.y) * 0.01
                    val dz = (chest.z - pos.z) * 0.01
                    level.addParticle(
                        ModParticles.PARTICLE_WAYPOINT_LINE_PURPLE.get(),
                        pos.x + 0.5, pos.y + 0.5, pos.z + 0.5,
                        dx, dy, dz
                    )
                }

                if ((tickCount and 31) == 0) {
                    if (be.showBaseParticle()) {
                        level.addParticle(
                            ModParticles.PARTICLE_WAYPOINT.get(),
                            pos.x + 0.5, pos.y - 0.25, pos.z + 0.5,
                            0.2, 0.0, 0.0
                        )
                    }

                    val sb = StringBuilder()
                    val stayTime = be.stayTimeDisplay
                    if (stayTime != null && stayTime.isNotEmpty()) {
                        sb.append(ChatFormatting.GOLD).append(stayTime)
                    }
                    val ownerName = be.ownerName
                    if (ownerName != null && ownerName.isNotEmpty()) {
                        if (sb.length > 0) sb.append(" | ")
                        sb.append(ChatFormatting.GREEN).append(ownerName)
                    }
                    val lastPos = be.lastPos
                    if (lastPos != null && lastPos != BlockPos.ZERO) {
                        if (sb.length > 0) sb.append(" | ")
                        sb.append(ChatFormatting.LIGHT_PURPLE).append("F: ").append(lastPos.toShortString())
                    }
                    val nextPos = be.nextPos
                    if (nextPos != null && nextPos != BlockPos.ZERO) {
                        if (sb.length > 0) sb.append(" | ")
                        sb.append(ChatFormatting.AQUA).append("T: ").append(nextPos.toShortString())
                    }
                    val chestPos = be.chestPos
                    if (chestPos != null && chestPos != BlockPos.ZERO) {
                        if (sb.length > 0) sb.append(" | ")
                        sb.append(ChatFormatting.YELLOW).append("C: ").append(chestPos.toShortString())
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
        if (stack.item is TargetWrenchItem) return true
        if (stack.item === ModItems.WAYPOINT.get()) return true
        if (stack.item === ModItems.CRANE.get()) return true
        if (stack.item is PointerItem) {
            val mode: Int = (stack.item as PointerItem).getMode(stack)
            return mode < 3
        }
        return false
    }
}
