package org.trp.shincolle.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.items.ItemStackHandler
import org.trp.shincolle.Config
import org.trp.shincolle.entity.EntityAircraftBase
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModBlockEntities
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.init.ModParticles
import org.trp.shincolle.menu.VolCoreMenu
import org.trp.shincolle.utility.PerformanceTrace.addBlockEntityTime
import org.trp.shincolle.utility.PerformanceTrace.elapsed
import org.trp.shincolle.utility.PerformanceTrace.enabled
import org.trp.shincolle.utility.PerformanceTrace.logSlowBlockEntityTick
import org.trp.shincolle.utility.PerformanceTrace.now

class VolCoreBlockEntity(pos: BlockPos, blockState: BlockState) :
    BlockEntity(ModBlockEntities.VOL_CORE.get(), pos, blockState), MenuProvider {
    val inventory: ItemStackHandler = object : ItemStackHandler(SLOT_COUNT) {
        override fun onContentsChanged(slot: Int) {
            markForSync()
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return stack.`is`(ModItems.GRUDGE.get()) || stack.`is`(ModItems.GRUDGE_BLOCK.get())
        }
    }

    private var remainedPower = 0
    private var btnActive = false
    private var syncTime = 0

    private fun serverTickInternal(level: Level, pos: BlockPos) {
        this.syncTime++

        if (this.syncTime % 16 == 0) {
            val canWork = this.remainedPower >= Config.volCoreConsumeSpeed
            if (canWork && this.btnActive) {
                this.remainedPower -= Config.volCoreConsumeSpeed
                markForSync()
            }
            if (this.isWorking && level is ServerLevel) {
                val bx = pos.getX() + 0.5
                val by = pos.getY() + 1.5
                val bz = pos.getZ() + 0.5
                for (i in 0..24) {
                    val px = bx + (level.getRandom().nextFloat() * 13.0f) - 6.5
                    val py = by + (level.getRandom().nextFloat() * 13.0f) - 4.5
                    val pz = bz + (level.getRandom().nextFloat() * 13.0f) - 6.5
                    level.sendParticles<SimpleParticleType?>(
                        ModParticles.PARTICLE_SPRAY.get(),
                        px, py, pz,
                        0,
                        0.0, 0.05, 0.0,
                        1.0
                    )
                }
            }
        }

        if (this.syncTime % 32 == 0) {
            decrItemFuel()
            if (this.isWorking) {
                volcoreFunction()
            }
        }

        if (this.syncTime % 256 == 0 && this.isWorking) {
            val dx = pos.getX() + 0.5
            val dy = pos.getY() + 2.5
            val dz = pos.getZ() + 0.5
            val box = AABB(dx - 6.0, dy - 6.0, dz - 6.0, dx + 6.0, dy + 6.0, dz + 6.0)
            val slist = level.getEntitiesOfClass<EntityShipBase?>(EntityShipBase::class.java, box)

            if (!slist.isEmpty()) {
                val emotes = level.getRandom().nextInt(11)
                for (ship in slist) {
                    if (ship.isAlive) {
                        ship.applyParticleEmotion(emotes)
                    }
                }
            }
        }
    }

    private fun decrItemFuel() {
        for (i in 0..<inventory.getSlots()) {
            val stack = inventory.getStackInSlot(i)
            if (stack.isEmpty()) continue

            var fuelx = 0
            if (stack.`is`(ModItems.GRUDGE.get())) {
                fuelx = Config.volCoreFuelMagnitude
            } else if (stack.`is`(ModItems.GRUDGE_BLOCK.get())) {
                fuelx = Config.volCoreFuelMagnitude * 9
            }

            if (fuelx > 0 && remainedPower + fuelx <= Config.volCorePowerMax) {
                stack.shrink(1)
                remainedPower += fuelx
                markForSync()
                break
            }
        }
    }

    private val isWorking: Boolean
        get() = btnActive && remainedPower >= Config.volCoreConsumeSpeed

    private fun volcoreFunction() {
        if (level == null) return

        val dx = worldPosition.getX() + 0.5
        val dy = worldPosition.getY() + 0.5
        val dz = worldPosition.getZ() + 0.5
        val box = AABB(dx - 6.0, dy - 6.0, dz - 6.0, dx + 6.0, dy + 6.0, dz + 6.0)

        if (this.isNearbyLiquid) {
            val slist = level!!.getEntitiesOfClass<EntityShipBase?>(EntityShipBase::class.java, box)
            for (s in slist) {
                if (s.isTame && s.isInWaterOrBubble()) {
                    if (s.getHealth() < s.getMaxHealth()) {
                        s.heal(s.getMaxHealth() * 0.01f + 4.0f)
                    }
                    if (s.morale < 9180) {
                        s.addMorale(80)
                    }
                }
            }
        } else {
            val elist = level!!.getEntitiesOfClass<LivingEntity?>(LivingEntity::class.java, box)
            val fireSource = level!!.damageSources().onFire()
            for (ent in elist) {
                if (ent is EntityShipBase
                    || ent is EntityMountBase
                    || ent is EntityAircraftBase
                    || ent is EntityShipBase && ent.isHostileShipMob || ent is Player
                ) {
                    continue
                }

                ent.igniteForTicks(40)
                ent.hurt(fireSource, 4.0f)
            }
        }
    }

    private val isNearbyLiquid: Boolean
        get() {
            if (level == null) return false
            for (dir in Direction.entries) {
                if (!level!!.getFluidState(worldPosition.relative(dir)).isEmpty()) {
                    return true
                }
            }
            return false
        }

    fun getRemainedPower(): Int {
        return remainedPower
    }

    fun setRemainedPower(remainedPower: Int) {
        if (this.remainedPower == remainedPower) {
            return
        }
        this.remainedPower = remainedPower
        markForSync()
    }

    fun isBtnActive(): Boolean {
        return btnActive
    }

    fun setBtnActive(btnActive: Boolean) {
        if (this.btnActive == btnActive) {
            return
        }
        this.btnActive = btnActive
        markForSync()
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.put("Inventory", inventory.serializeNBT(registries))
        tag.putInt("Power", remainedPower)
        tag.putBoolean("Active", btnActive)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        if (tag.contains("Inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("Inventory"))
        }
        remainedPower = tag.getInt("Power")
        btnActive = tag.getBoolean("Active")
    }

    override fun getDisplayName(): Component {
        return Component.translatable("tile.shincolle.BlockVolCore.name")
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu? {
        return VolCoreMenu(containerId, playerInventory, this)
    }

    fun markForSync() {
        setChanged()
        if (level != null) {
            level!!.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3)
        }
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener?>? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return saveWithoutMetadata(registries)
    }

    companion object {
        const val SLOT_COUNT: Int = 9
        fun serverTick(level: Level, pos: BlockPos, state: BlockState?, blockEntity: VolCoreBlockEntity) {
            if (level.isClientSide) return
            val tracing = enabled()
            val start = if (tracing) now() else 0L
            try {
                blockEntity.serverTickInternal(level, pos)
            } finally {
                if (tracing) {
                    val elapsed = elapsed(start)
                    addBlockEntityTime(elapsed)
                    logSlowBlockEntityTick(
                        blockEntity, "vol_core", elapsed,
                        ("active=" + blockEntity.btnActive
                                + " working=" + blockEntity.isWorking
                                + " power=" + blockEntity.remainedPower
                                + " syncTime=" + blockEntity.syncTime)
                    )
                }
            }
        }
    }
}
