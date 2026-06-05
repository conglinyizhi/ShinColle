package org.trp.shincolle.entity

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.init.ModEntities
import java.util.*

class EntityShipGrudge(type: EntityType<out EntityShipGrudge?>, level: Level) : Entity(type, level) {
    private var storedItem: ItemStack = ItemStack.EMPTY
    private var ownerId: UUID? = null
    private var pickupDelay: Int = DEFAULT_PICKUP_DELAY
    private var age = 0

    init {
        this.noPhysics = true
        this.setNoGravity(true)
    }

    constructor(
        level: Level,
        x: Double,
        y: Double,
        z: Double,
        stack: ItemStack,
        ownerId: UUID?
    ) : this(ModEntities.SHIP_GRUDGE.get(), level) {
        this.setPos(x, y, z)
        this.storedItem = stack.copy()
        this.ownerId = ownerId
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
    }

    override fun tick() {
        super.tick()
        if (this.pickupDelay > 0) {
            this.pickupDelay--
        }
        if (!this.level().isClientSide) {
            if (this.age++ >= DESPAWN_TICKS) {
                this.discard()
                return
            }
            this.setDeltaMovement(Vec3.ZERO)
        }
    }

    override fun isPickable(): Boolean {
        return true
    }

    override fun playerTouch(player: Player) {
        if (this.level().isClientSide) {
            return
        }
        if (this.pickupDelay > 0 || this.storedItem.isEmpty()) {
            return
        }
        if (this.ownerId != null && this.ownerId != player.getUUID()) {
            return
        }

        val count = this.storedItem.getCount()
        if (player.addItem(this.storedItem)) {
            if (this.storedItem.isEmpty()) {
                player.take(this, count)
                this.discard()
            }
        }
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        if (!this.storedItem.isEmpty()) {
            tag.put(TAG_ITEM, this.storedItem.save(this.registryAccess()))
        }
        if (this.ownerId != null) {
            tag.putUUID(TAG_OWNER, this.ownerId)
        }
        tag.putInt(TAG_PICKUP_DELAY, this.pickupDelay)
        tag.putInt(TAG_AGE, this.age)
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        if (tag.contains(TAG_ITEM)) {
            this.storedItem = ItemStack.parse(this.registryAccess(), tag.getCompound(TAG_ITEM)).orElse(ItemStack.EMPTY)
        } else {
            this.storedItem = ItemStack.EMPTY
        }
        this.ownerId = if (tag.hasUUID(TAG_OWNER)) tag.getUUID(TAG_OWNER) else null
        this.pickupDelay = tag.getInt(TAG_PICKUP_DELAY)
        this.age = tag.getInt(TAG_AGE)
    }

    companion object {
        private const val DEFAULT_PICKUP_DELAY = 20
        private val DESPAWN_TICKS = 20 * 60 * 5
        private const val TAG_ITEM = "StoredItem"
        private const val TAG_OWNER = "Owner"
        private const val TAG_PICKUP_DELAY = "PickupDelay"
        private const val TAG_AGE = "Age"
    }
}
