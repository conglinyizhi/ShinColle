package org.trp.shincolle.block.entity

import net.minecraft.core.BlockPos
import java.util.*

interface IWaypoint {
    var lastPos: BlockPos?
    var nextPos: BlockPos?
    var chestPos: BlockPos?
    val ownerUUID: UUID?
    val stayTimeDisplay: String?
        get() = ""
    val ownerName: String?
        get() = ""

    fun showBaseParticle(): Boolean {
        return true
    }
}
