package org.trp.shincolle.menu

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.DataSlot
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidUtil
import net.neoforged.neoforge.items.SlotItemHandler
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.base.EntityShipBase.EquipOption
import org.trp.shincolle.inventory.ShipInventoryHandler
import org.trp.shincolle.item.LegacyEquipItem
import org.trp.shincolle.item.ShipTankItem
import org.trp.shincolle.server.PlayerStateService.registerCollectedShip
import kotlin.math.max
import kotlin.math.min

class ShipContainerMenu(containerId: Int, playerInv: Inventory, ship: EntityShipBase) :
    AbstractContainerMenu(ModMenus.SHIP_MENU.get(), containerId) {
    @JvmField
    val ship: EntityShipBase
    private val pageData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            inventoryPage = clampPage(inventoryPage)
            return inventoryPage
        }

        override fun set(value: Int) {
            setInventoryPage(clampPage(value))
        }
    }
    private val unlockedStoragePagesData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return this.unlockedStoragePagesServer
        }

        override fun set(value: Int) {
            unlockedStoragePagesSynced = Mth.clamp(value, 0, SHIP_PAGE_COUNT - 1)
            setInventoryPage(clampPage(inventoryPage))
        }
    }
    private val canMeleeData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return if (ship.isStateCanMelee()) 1 else 0
        }

        override fun set(value: Int) {
            this.isCanMeleeEnabled = value != 0
        }
    }
    private val lightAttackData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return if (ship.isStateLightAttack()) 1 else 0
        }

        override fun set(value: Int) {
            this.isLightAttackEnabled = value != 0
        }
    }
    private val heavyAttackData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return if (ship.isStateHeavyAttack()) 1 else 0
        }

        override fun set(value: Int) {
            this.isHeavyAttackEnabled = value != 0
        }
    }
    private val lightAircraftAttackData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return if (ship.isStateLightAircraftAttack()) 1 else 0
        }

        override fun set(value: Int) {
            this.isLightAircraftAttackEnabled = value != 0
        }
    }
    private val heavyAircraftAttackData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return if (ship.isStateHeavyAircraftAttack()) 1 else 0
        }

        override fun set(value: Int) {
            this.isHeavyAircraftAttackEnabled = value != 0
        }
    }
    private val ringEffectData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return if (ship.isStateRingEffect()) 1 else 0
        }

        override fun set(value: Int) {
            this.isRingEffectEnabled = value != 0
        }
    }
    private val ammoLightData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return ship.getAmmoLight()
        }

        override fun set(value: Int) {
            ammoLightSynced = value
        }
    }
    private val ammoHeavyData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return ship.getAmmoHeavy()
        }

        override fun set(value: Int) {
            ammoHeavySynced = value
        }
    }
    private val marriedData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return if (ship.isStateMarried()) 1 else 0
        }

        override fun set(value: Int) {
            this.isMarried = value != 0
        }
    }
    private val followMinData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return ship.getStateMinor(STATE_MINOR_FOLLOW_MIN)
        }

        override fun set(value: Int) {
            this.followMinDistance = clampFollowMin(value)
        }
    }
    private val followMaxData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return ship.getStateMinor(STATE_MINOR_FOLLOW_MAX)
        }

        override fun set(value: Int) {
            this.followMaxDistance = clampFollowMax(value, this.followMinDistance)
        }
    }
    private val fleeHpData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return ship.getStateMinor(STATE_MINOR_FLEE_HP)
        }

        override fun set(value: Int) {
            this.fleeHpPercent = clampFleeHp(value)
        }
    }
    private val passiveAttackData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return if (ship.getStateFlag(STATE_FLAG_PASSIVE_ATTACK)) 1 else 0
        }

        override fun set(value: Int) {
            this.isPassiveAttackEnabled = value != 0
        }
    }
    private val onSightData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return if (ship.getStateFlag(STATE_FLAG_ON_SIGHT)) 1 else 0
        }

        override fun set(value: Int) {
            this.isOnSightEnabled = value != 0
        }
    }
    private val pvpData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return if (ship.getStateFlag(STATE_FLAG_PVP)) 1 else 0
        }

        override fun set(value: Int) {
            this.isPvpEnabled = value != 0
        }
    }
    private val antiAirData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return if (ship.getStateFlag(STATE_FLAG_ANTI_AIR)) 1 else 0
        }

        override fun set(value: Int) {
            this.isAntiAirEnabled = value != 0
        }
    }
    private val antiSubData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return if (ship.getStateFlag(STATE_FLAG_ANTI_SUB)) 1 else 0
        }

        override fun set(value: Int) {
            this.isAntiSubEnabled = value != 0
        }
    }
    private val timeKeepingData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return if (ship.getStateFlag(STATE_FLAG_TIMEKEEP)) 1 else 0
        }

        override fun set(value: Int) {
            this.isTimeKeepingEnabled = value != 0
        }
    }
    private val pickItemData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return if (ship.getStateFlag(STATE_FLAG_PICK_ITEM)) 1 else 0
        }

        override fun set(value: Int) {
            this.isPickItemEnabled = value != 0
        }
    }
    private val autoPumpData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return if (ship.getStateFlag(STATE_FLAG_AUTO_PUMP)) 1 else 0
        }

        override fun set(value: Int) {
            this.isAutoPumpEnabled = value != 0
        }
    }
    private val appearanceData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return if (ship.isStateAppearance()) 1 else 0
        }

        override fun set(value: Int) {
            this.isAppearanceEnabled = value != 0
        }
    }
    private val mountData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return (ship.getStateEmotion(0) and 1)
        }

        override fun set(value: Int) {
            this.isMountEnabled = value != 0
        }
    }
    private val rationMoraleData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return ship.getStateMinor(STATE_MINOR_RATION_MORALE)
        }

        override fun set(value: Int) {
            this.rationMoraleThreshold = clampRationMorale(value)
        }
    }
    private val wpStayData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return ship.getStateMinor(STATE_MINOR_WP_STAY)
        }

        override fun set(value: Int) {
            this.wpStaySetting = clampWpStay(value)
        }
    }
    private val taskIdData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return ship.getStateMinor(STATE_MINOR_TASK_ID)
        }

        override fun set(value: Int) {
            this.taskId = value
        }
    }
    private val taskSideData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            return ship.getStateMinor(STATE_MINOR_TASK_SIDE)
        }

        override fun set(value: Int) {
            this.taskSideFlags = value
        }
    }
    private val shipTankFluidAmountLowData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            refreshShipTankFluidSyncValues()
            return shipTankFluidAmountSynced and 0xFFFF
        }

        override fun set(value: Int) {
            shipTankFluidAmountSynced = (shipTankFluidAmountSynced and -0x10000) or (value and 0xFFFF)
        }
    }
    private val shipTankFluidAmountHighData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            refreshShipTankFluidSyncValues()
            return (shipTankFluidAmountSynced ushr 16) and 0xFFFF
        }

        override fun set(value: Int) {
            shipTankFluidAmountSynced = (shipTankFluidAmountSynced and 0xFFFF) or ((value and 0xFFFF) shl 16)
        }
    }
    private val shipTankFluidCapacityLowData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            refreshShipTankFluidSyncValues()
            return shipTankFluidCapacitySynced and 0xFFFF
        }

        override fun set(value: Int) {
            shipTankFluidCapacitySynced = (shipTankFluidCapacitySynced and -0x10000) or (value and 0xFFFF)
        }
    }
    private val shipTankFluidCapacityHighData: DataSlot = object : DataSlot() {
        override fun get(): Int {
            refreshShipTankFluidSyncValues()
            return (shipTankFluidCapacitySynced ushr 16) and 0xFFFF
        }

        override fun set(value: Int) {
            shipTankFluidCapacitySynced = (shipTankFluidCapacitySynced and 0xFFFF) or ((value and 0xFFFF) shl 16)
        }
    }
    private var inventoryPage = 0
    private val pagedShipSlots: MutableList<PagedShipSlot> = ArrayList<PagedShipSlot>()
    private var unlockedStoragePagesSynced: Int
    var isCanMeleeEnabled: Boolean
        private set
    var isLightAttackEnabled: Boolean
        private set
    var isHeavyAttackEnabled: Boolean
        private set
    var isLightAircraftAttackEnabled: Boolean
        private set
    var isHeavyAircraftAttackEnabled: Boolean
        private set
    var isRingEffectEnabled: Boolean
        private set
    var ammoLightSynced: Int
        private set
    var ammoHeavySynced: Int
        private set
    var isMarried: Boolean
        private set
    var followMinDistance: Int
        private set
    var followMaxDistance: Int
        private set
    var fleeHpPercent: Int
        private set
    var isPassiveAttackEnabled: Boolean
        private set
    var isOnSightEnabled: Boolean
        private set
    var isPvpEnabled: Boolean
        private set
    var isAntiAirEnabled: Boolean
        private set
    var isAntiSubEnabled: Boolean
        private set
    var isTimeKeepingEnabled: Boolean
        private set
    var isPickItemEnabled: Boolean
        private set
    var isAutoPumpEnabled: Boolean
        private set
    var isAppearanceEnabled: Boolean
        private set
    var isMountEnabled: Boolean
        private set
    var rationMoraleThreshold: Int
        private set
    var wpStaySetting: Int
        private set
    var taskId: Int
        private set
    var taskSideFlags: Int
        private set
    private var shipTankFluidAmountSynced = 0
    private var shipTankFluidCapacitySynced = 0

    constructor(containerId: Int, playerInv: Inventory, buf: RegistryFriendlyByteBuf) : this(
        containerId,
        playerInv,
        getEntity(playerInv, buf)
    )

    init {
        check(!(ship == null || !ship.isAlive() || ship.isRemoved())) { "Ship entity is not available for menu access." }
        this.ship = ship

        if (!ship.level().isClientSide) {
            ship.incrementGuiOpen()
        }
        if (!ship.level().isClientSide) {
            ship.onInventoryChanged()
        }

        if (!ship.level().isClientSide && playerInv.player != null) {
            val classID = ship.getStateMinor(EntityShipBase.STATE_MINOR_SHIP_CLASS)
            if (playerInv.player is ServerPlayer) {
                registerCollectedShip(player, classID)
            }
        }

        this.unlockedStoragePagesSynced = Mth.clamp(ship.getStateMinor(STATE_MINOR_EQUIP_DRUM), 0, SHIP_PAGE_COUNT - 1)
        this.isCanMeleeEnabled = ship.isStateCanMelee()
        this.isLightAttackEnabled = ship.isStateLightAttack()
        this.isHeavyAttackEnabled = ship.isStateHeavyAttack()
        this.isLightAircraftAttackEnabled = ship.isStateLightAircraftAttack()
        this.isHeavyAircraftAttackEnabled = ship.isStateHeavyAircraftAttack()
        this.isRingEffectEnabled = ship.isStateRingEffect()
        this.ammoLightSynced = ship.getAmmoLight()
        this.ammoHeavySynced = ship.getAmmoHeavy()
        this.isMarried = ship.isStateMarried()
        this.followMinDistance = clampFollowMin(ship.getStateMinor(STATE_MINOR_FOLLOW_MIN))
        this.followMaxDistance = clampFollowMax(ship.getStateMinor(STATE_MINOR_FOLLOW_MAX), this.followMinDistance)
        this.fleeHpPercent = clampFleeHp(ship.getStateMinor(STATE_MINOR_FLEE_HP))
        this.isPassiveAttackEnabled = ship.getStateFlag(STATE_FLAG_PASSIVE_ATTACK)
        this.isOnSightEnabled = ship.getStateFlag(STATE_FLAG_ON_SIGHT)
        this.isPvpEnabled = ship.getStateFlag(STATE_FLAG_PVP)
        this.isAntiAirEnabled = ship.getStateFlag(STATE_FLAG_ANTI_AIR)
        this.isAntiSubEnabled = ship.getStateFlag(STATE_FLAG_ANTI_SUB)
        this.isTimeKeepingEnabled = ship.getStateFlag(STATE_FLAG_TIMEKEEP)
        this.isPickItemEnabled = ship.getStateFlag(STATE_FLAG_PICK_ITEM)
        this.isAutoPumpEnabled = ship.getStateFlag(STATE_FLAG_AUTO_PUMP)
        this.isAppearanceEnabled = ship.isStateAppearance()
        this.isMountEnabled = (ship.getStateEmotion(0) and 1) != 0
        this.rationMoraleThreshold = clampRationMorale(ship.getStateMinor(STATE_MINOR_RATION_MORALE))
        this.wpStaySetting = clampWpStay(ship.getStateMinor(STATE_MINOR_WP_STAY))
        this.taskId = ship.getStateMinor(STATE_MINOR_TASK_ID)
        this.taskSideFlags = ship.getStateMinor(STATE_MINOR_TASK_SIDE)
        refreshShipTankFluidSyncValues()

        ship.setStateMinor(STATE_MINOR_FOLLOW_MIN, this.followMinDistance)
        ship.setStateMinor(STATE_MINOR_FOLLOW_MAX, this.followMaxDistance)
        ship.setStateMinor(STATE_MINOR_FLEE_HP, this.fleeHpPercent)
        ship.setStateMinor(STATE_MINOR_RATION_MORALE, this.rationMoraleThreshold)
        ship.setStateMinor(STATE_MINOR_WP_STAY, this.wpStaySetting)

        this.addDataSlot(pageData)
        this.addDataSlot(unlockedStoragePagesData)
        this.addDataSlot(canMeleeData)
        this.addDataSlot(lightAttackData)
        this.addDataSlot(heavyAttackData)
        this.addDataSlot(lightAircraftAttackData)
        this.addDataSlot(heavyAircraftAttackData)
        this.addDataSlot(ringEffectData)
        this.addDataSlot(ammoLightData)
        this.addDataSlot(ammoHeavyData)
        this.addDataSlot(marriedData)
        this.addDataSlot(followMinData)
        this.addDataSlot(followMaxData)
        this.addDataSlot(fleeHpData)
        this.addDataSlot(passiveAttackData)
        this.addDataSlot(onSightData)
        this.addDataSlot(pvpData)
        this.addDataSlot(antiAirData)
        this.addDataSlot(antiSubData)
        this.addDataSlot(timeKeepingData)
        this.addDataSlot(pickItemData)
        this.addDataSlot(autoPumpData)
        this.addDataSlot(appearanceData)
        this.addDataSlot(mountData)
        this.addDataSlot(rationMoraleData)
        this.addDataSlot(wpStayData)
        this.addDataSlot(taskIdData)
        this.addDataSlot(taskSideData)
        this.addDataSlot(shipTankFluidAmountLowData)
        this.addDataSlot(shipTankFluidAmountHighData)
        this.addDataSlot(shipTankFluidCapacityLowData)
        this.addDataSlot(shipTankFluidCapacityHighData)

        for (i in 0..<EQUIP_SLOTS) {
            this.addSlot(EquipSlot(i, EQUIP_INV_X, EQUIP_INV_Y + i * 18))
        }

        for (row in 0..5) {
            for (col in 0..2) {
                val localIndex = row * 3 + col
                val pagedSlot = PagedShipSlot(localIndex, SHIP_INV_X + col * 18, SHIP_INV_Y + row * 18)
                this.pagedShipSlots.add(pagedSlot)
                this.addSlot(pagedSlot)
            }
        }

        for (row in 0..2) {
            for (col in 0..8) {
                val x: Int = PLAYER_INV_X + col * 18
                val y: Int = PLAYER_INV_Y + row * 18
                this.addSlot(Slot(playerInv, col + row * 9 + 9, x, y))
            }
        }

        for (col in 0..8) {
            val x: Int = PLAYER_INV_X + col * 18
            this.addSlot(Slot(playerInv, col, x, HOTBAR_Y))
        }
    }

    val shipLevel: Int
        get() = ship.getLevel()

    val shipExp: Int
        get() = ship.getExp()

    val shipKills: Int
        get() = ship.getShipKills()

    val shipFuel: Int
        get() = ship.getFuel()

    val isCreativeDebuggerActive: Boolean
        get() = ship.hasCreativeDebugger()

    val shipFirepower: Float
        get() = ship.legacyShipStats.getFirepower()

    val shipArmor: Float
        get() = ship.legacyShipStats.getArmor()

    val shipReloadSpeed: Float
        get() = ship.legacyShipStats.getReloadSpeed()

    val shipMoveSpeed: Float
        get() = ship.legacyShipStats.getMoveSpeed()

    val shipRange: Float
        get() = ship.legacyShipStats.getAttackRange()

    val shipHealth: Float
        get() = ship.getHealth()

    val shipMaxHealth: Float
        get() = ship.getMaxHealth()

    val aircraftLight: Int
        get() = ship.getNumAircraftLight()

    val aircraftHeavy: Int
        get() = ship.getNumAircraftHeavy()

    override fun removed(player: Player) {
        super.removed(player)
        if (!ship.level().isClientSide) {
            ship.decrementGuiOpen()
        }
    }

    fun getInventoryPage(): Int {
        this.inventoryPage = clampPage(this.inventoryPage)
        return inventoryPage
    }

    val unlockedStoragePages: Int
        get() = Mth.clamp(this.unlockedStoragePagesSynced, 0, SHIP_PAGE_COUNT - 1)

    val unlockedInventoryPageCount: Int
        get() = 1 + this.unlockedStoragePages

    val shipTankFluidAmount: Int
        get() = max(0, shipTankFluidAmountSynced)

    val shipTankFluidCapacity: Int
        get() = max(0, shipTankFluidCapacitySynced)

    val equipOptions: MutableList<EquipOption>?
        get() = ship.getEquipOptions()

    val equipOptionCount: Int
        get() = ship.getEquipOptions().size

    fun getEquipOptionLabel(index: Int): Component {
        if (index < 0 || index >= ship.getEquipOptions().size) {
            return Component.empty()
        }
        return Component.translatable(ship.getEquipOptions().get(index).labelKey)
    }

    fun isEquipOptionEnabled(index: Int): Boolean {
        if (index < 0 || index >= ship.getEquipOptions().size) {
            return false
        }
        return ship.getEquipFlag(ship.getEquipOptions().get(index).key)
    }

    fun getEquipOptionButtonId(index: Int): Int {
        return EQUIP_BUTTON_BASE + index
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        var copied = ItemStack.EMPTY
        val slot = this.slots.get(index)
        if (slot != null && slot.hasItem()) {
            val stack = slot.getItem()
            copied = stack.copy()
            val shipEquipCandidate = stack.getItem() is LegacyEquipItem
                    || stack.`is`(ShipInventoryHandler.getEquipItemsTag())

            if (index < VISIBLE_SHIP_SLOTS) {
                if (index >= EQUIP_SLOTS && shipEquipCandidate) {
                    if (!this.moveItemStackTo(stack, 0, EQUIP_SLOTS, false)
                        && !this.moveItemStackTo(stack, FIRST_PLAYER_SLOT, END_PLAYER_SLOT, true)
                    ) {
                        return ItemStack.EMPTY
                    }
                } else if (!this.moveItemStackTo(stack, FIRST_PLAYER_SLOT, END_PLAYER_SLOT, true)) {
                    return ItemStack.EMPTY
                }
            } else {
                if (!this.moveItemStackTo(stack, 0, EQUIP_SLOTS, false)
                    && !this.moveItemStackTo(stack, EQUIP_SLOTS, VISIBLE_SHIP_SLOTS, false)
                ) {
                    return ItemStack.EMPTY
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }
        }
        return copied
    }

    override fun clicked(slotId: Int, button: Int, clickType: ClickType, player: Player) {
        super.clicked(slotId, button, clickType, player)
        if (!this.ship.level().isClientSide) {
            this.ship.onInventoryChanged()
        }
    }

    override fun stillValid(player: Player): Boolean {
        return ship.isAlive() && !ship.isRemoved() && player.distanceToSqr(ship) < 64.0
    }

    override fun broadcastChanges() {
        if (!this.ship.level().isClientSide) {
            this.ship.onInventoryChanged()
        }
        super.broadcastChanges()
    }

    override fun clickMenuButton(player: Player, id: Int): Boolean {
        if (player.level().isClientSide) {
            when (id) {
                PAGE_BUTTON_0 -> this.setInventoryPage(0)
                PAGE_BUTTON_1 -> {
                    if (isPageUnlocked(1)) {
                        this.setInventoryPage(1)
                    }
                }

                PAGE_BUTTON_2 -> {
                    if (isPageUnlocked(2)) {
                        this.setInventoryPage(2)
                    }
                }

                else -> {}
            }
            return true
        }

        when (id) {
            PAGE_BUTTON_0 -> {
                this.setInventoryPage(0)
                this.broadcastFullState()
                return true
            }

            PAGE_BUTTON_1 -> {
                if (!isPageUnlocked(1)) {
                    return true
                }
                this.setInventoryPage(1)
                this.broadcastFullState()
                return true
            }

            PAGE_BUTTON_2 -> {
                if (!isPageUnlocked(2)) {
                    return true
                }
                this.setInventoryPage(2)
                this.broadcastFullState()
                return true
            }

            else -> {}
        }

        when (id) {
            TOGGLE_BUTTON_CAN_MELEE -> {
                ship.setStateCanMelee(!ship.isStateCanMelee())
                this.isCanMeleeEnabled = ship.isStateCanMelee()
                this.broadcastFullState()
                return true
            }

            TOGGLE_BUTTON_LIGHT_ATTACK -> {
                if (ship.isStateGuiBtn1()) {
                    ship.setStateLightAttack(!ship.isStateLightAttack())
                    this.isLightAttackEnabled = ship.isStateLightAttack()
                    this.broadcastFullState()
                }
                return true
            }

            TOGGLE_BUTTON_HEAVY_ATTACK -> {
                if (ship.isStateGuiBtn2()) {
                    ship.setStateHeavyAttack(!ship.isStateHeavyAttack())
                    this.isHeavyAttackEnabled = ship.isStateHeavyAttack()
                    this.broadcastFullState()
                }
                return true
            }

            TOGGLE_BUTTON_LIGHT_AIRCRAFT -> {
                if (ship.isStateGuiBtn3()) {
                    ship.setStateLightAircraftAttack(!ship.isStateLightAircraftAttack())
                    this.isLightAircraftAttackEnabled = ship.isStateLightAircraftAttack()
                    this.broadcastFullState()
                }
                return true
            }

            TOGGLE_BUTTON_HEAVY_AIRCRAFT -> {
                if (ship.isStateGuiBtn4()) {
                    ship.setStateHeavyAircraftAttack(!ship.isStateHeavyAircraftAttack())
                    this.isHeavyAircraftAttackEnabled = ship.isStateHeavyAircraftAttack()
                    this.broadcastFullState()
                }
                return true
            }

            TOGGLE_BUTTON_RING_EFFECT -> {
                ship.setStateRingEffect(!ship.isStateRingEffect())
                this.isRingEffectEnabled = ship.isStateRingEffect()
                this.broadcastFullState()
                return true
            }

            TOGGLE_BUTTON_PASSIVE_ATTACK -> {
                ship.setStateFlag(STATE_FLAG_PASSIVE_ATTACK, !ship.getStateFlag(STATE_FLAG_PASSIVE_ATTACK))
                this.isPassiveAttackEnabled = ship.getStateFlag(STATE_FLAG_PASSIVE_ATTACK)
                this.broadcastFullState()
                return true
            }

            TOGGLE_BUTTON_ON_SIGHT -> {
                ship.setStateFlag(STATE_FLAG_ON_SIGHT, !ship.getStateFlag(STATE_FLAG_ON_SIGHT))
                this.isOnSightEnabled = ship.getStateFlag(STATE_FLAG_ON_SIGHT)
                this.broadcastFullState()
                return true
            }

            TOGGLE_BUTTON_PVP -> {
                ship.setStateFlag(STATE_FLAG_PVP, !ship.getStateFlag(STATE_FLAG_PVP))
                this.isPvpEnabled = ship.getStateFlag(STATE_FLAG_PVP)
                this.broadcastFullState()
                return true
            }

            TOGGLE_BUTTON_ANTI_AIR -> {
                ship.setStateFlag(STATE_FLAG_ANTI_AIR, !ship.getStateFlag(STATE_FLAG_ANTI_AIR))
                this.isAntiAirEnabled = ship.getStateFlag(STATE_FLAG_ANTI_AIR)
                this.broadcastFullState()
                return true
            }

            TOGGLE_BUTTON_ANTI_SUB -> {
                ship.setStateFlag(STATE_FLAG_ANTI_SUB, !ship.getStateFlag(STATE_FLAG_ANTI_SUB))
                this.isAntiSubEnabled = ship.getStateFlag(STATE_FLAG_ANTI_SUB)
                this.broadcastFullState()
                return true
            }

            TOGGLE_BUTTON_TIMEKEEP -> {
                ship.setStateFlag(STATE_FLAG_TIMEKEEP, !ship.getStateFlag(STATE_FLAG_TIMEKEEP))
                this.isTimeKeepingEnabled = ship.getStateFlag(STATE_FLAG_TIMEKEEP)
                this.broadcastFullState()
                return true
            }

            TOGGLE_BUTTON_PICK_ITEM -> {
                ship.setStateFlag(STATE_FLAG_PICK_ITEM, !ship.getStateFlag(STATE_FLAG_PICK_ITEM))
                this.isPickItemEnabled = ship.getStateFlag(STATE_FLAG_PICK_ITEM)
                this.broadcastFullState()
                return true
            }

            TOGGLE_BUTTON_AUTO_PUMP -> {
                ship.setStateFlag(STATE_FLAG_AUTO_PUMP, !ship.getStateFlag(STATE_FLAG_AUTO_PUMP))
                this.isAutoPumpEnabled = ship.getStateFlag(STATE_FLAG_AUTO_PUMP)
                this.broadcastFullState()
                return true
            }

            TOGGLE_BUTTON_SHOW_HELD -> {
                ship.setStateAppearance(!ship.isStateAppearance())
                this.isAppearanceEnabled = ship.isStateAppearance()
                this.broadcastFullState()
                return true
            }

            TOGGLE_BUTTON_MOUNT -> {
                val current = ship.getStateEmotion(0)
                ship.setStateEmotion(0, current xor 1, true)
                this.isMountEnabled = (ship.getStateEmotion(0) and 1) != 0
                this.broadcastFullState()
                return true
            }

            else -> {}
        }

        if (id >= SLIDER_FOLLOW_MIN_BASE && id <= SLIDER_FOLLOW_MIN_BASE + FOLLOW_MIN_MAX) {
            val requested: Int = id - SLIDER_FOLLOW_MIN_BASE
            this.followMinDistance = clampFollowMin(requested)
            this.followMaxDistance = clampFollowMax(this.followMaxDistance, this.followMinDistance)
            ship.setStateMinor(STATE_MINOR_FOLLOW_MIN, this.followMinDistance)
            ship.setStateMinor(STATE_MINOR_FOLLOW_MAX, this.followMaxDistance)
            return true
        }

        if (id >= SLIDER_FOLLOW_MAX_BASE && id <= SLIDER_FOLLOW_MAX_BASE + FOLLOW_MAX_MAX) {
            val requested: Int = id - SLIDER_FOLLOW_MAX_BASE
            this.followMaxDistance = clampFollowMax(requested, this.followMinDistance)
            ship.setStateMinor(STATE_MINOR_FOLLOW_MAX, this.followMaxDistance)
            return true
        }

        if (id >= SLIDER_FLEE_HP_BASE && id <= SLIDER_FLEE_HP_BASE + FLEE_HP_MAX) {
            val requested: Int = id - SLIDER_FLEE_HP_BASE
            this.fleeHpPercent = clampFleeHp(requested)
            ship.setStateMinor(STATE_MINOR_FLEE_HP, this.fleeHpPercent)
            return true
        }

        if (id >= SLIDER_WP_STAY_BASE && id <= SLIDER_WP_STAY_BASE + WP_STAY_MAX) {
            val requested: Int = id - SLIDER_WP_STAY_BASE
            this.wpStaySetting = clampWpStay(requested)
            ship.setStateMinor(STATE_MINOR_WP_STAY, this.wpStaySetting)
            return true
        }

        if (id >= SLIDER_RATION_MORALE_BASE && id <= SLIDER_RATION_MORALE_BASE + RATION_MORALE_MAX) {
            val requested: Int = id - SLIDER_RATION_MORALE_BASE
            this.rationMoraleThreshold = clampRationMorale(requested)
            ship.setStateMinor(STATE_MINOR_RATION_MORALE, this.rationMoraleThreshold)
            return true
        }

        val index: Int = id - EQUIP_BUTTON_BASE
        if (index >= 0 && index < ship.getEquipOptions().size) {
            val option = ship.getEquipOptions().get(index)
            val next = !ship.getEquipFlag(option.key)
            ship.setEquipFlag(option.key, next)
            return true
        }

        if (id >= ACTION_TASK_SELECT_BASE + 1 && id <= ACTION_TASK_SELECT_BASE + 4) {
            val newTask: Int = id - ACTION_TASK_SELECT_BASE
            val curTask = ship.getStateMinor(STATE_MINOR_TASK_ID)
            ship.setStateMinor(STATE_MINOR_TASK_ID, if (curTask != newTask) newTask else 0)
            this.taskId = ship.getStateMinor(STATE_MINOR_TASK_ID)
            return true
        }

        if (id == ACTION_TASK_META_TOGGLE) {
            ship.setStateMinor(STATE_MINOR_TASK_SIDE, ship.getStateMinor(STATE_MINOR_TASK_SIDE) xor (1 shl 18))
            this.taskSideFlags = ship.getStateMinor(STATE_MINOR_TASK_SIDE)
            return true
        }
        if (id == ACTION_TASK_ORE_TOGGLE) {
            ship.setStateMinor(STATE_MINOR_TASK_SIDE, ship.getStateMinor(STATE_MINOR_TASK_SIDE) xor (1 shl 19))
            this.taskSideFlags = ship.getStateMinor(STATE_MINOR_TASK_SIDE)
            return true
        }
        if (id == ACTION_TASK_NBT_TOGGLE) {
            ship.setStateMinor(STATE_MINOR_TASK_SIDE, ship.getStateMinor(STATE_MINOR_TASK_SIDE) xor (1 shl 20))
            this.taskSideFlags = ship.getStateMinor(STATE_MINOR_TASK_SIDE)
            return true
        }

        if (id >= ACTION_SIDE_TOGGLE_BASE && id < ACTION_SIDE_TOGGLE_BASE + 18) {
            val bit: Int = id - ACTION_SIDE_TOGGLE_BASE
            ship.setStateMinor(STATE_MINOR_TASK_SIDE, ship.getStateMinor(STATE_MINOR_TASK_SIDE) xor (1 shl bit))
            this.taskSideFlags = ship.getStateMinor(STATE_MINOR_TASK_SIDE)
            return true
        }

        return super.clickMenuButton(player, id)
    }

    private fun clampPage(page: Int): Int {
        val unlockedCount = this.unlockedPageCount
        if (unlockedCount <= 1) {
            return 0
        }
        if (page < 0) {
            return 0
        }
        if (page >= unlockedCount) {
            return unlockedCount - 1
        }
        return page
    }

    private val unlockedStoragePagesServer: Int
        get() {
            if (this.ship.level().isClientSide) {
                return this.unlockedStoragePages
            }
            return Mth.clamp(
                this.ship.getStateMinor(STATE_MINOR_EQUIP_DRUM),
                0,
                SHIP_PAGE_COUNT - 1
            )
        }

    private val unlockedPageCount: Int
        get() = 1 + Mth.clamp(this.unlockedStoragePagesServer, 0, SHIP_PAGE_COUNT - 1)

    private fun isPageUnlocked(page: Int): Boolean {
        return page >= 0 && page < this.unlockedPageCount
    }

    private fun clampFollowMin(value: Int): Int {
        val clamped = max(FOLLOW_MIN_MIN, min(FOLLOW_MIN_MAX, value))
        return min(clamped, FOLLOW_MAX_MAX - 1)
    }

    private fun clampFollowMax(value: Int, followMin: Int): Int {
        val min = max(FOLLOW_MAX_MIN, followMin + 1)
        return max(min, min(FOLLOW_MAX_MAX, value))
    }

    private fun setInventoryPage(page: Int) {
        val next = clampPage(page)
        if (this.inventoryPage == next) {
            return
        }
        this.inventoryPage = next
        clearPagedSlotClientCache()
        if (!this.ship.level().isClientSide) {
            this.broadcastFullState()
        }
    }

    private fun clearPagedSlotClientCache() {
        for (slot in this.pagedShipSlots) {
            slot.clearClientCache()
        }
    }

    private fun clampFleeHp(value: Int): Int {
        return max(FLEE_HP_MIN, min(FLEE_HP_MAX, value))
    }

    private fun clampWpStay(value: Int): Int {
        return max(WP_STAY_MIN, min(WP_STAY_MAX, value))
    }

    private fun clampRationMorale(value: Int): Int {
        return max(RATION_MORALE_MIN, min(RATION_MORALE_MAX, value))
    }

    private fun refreshShipTankFluidSyncValues() {
        if (this.ship.level().isClientSide) {
            return
        }

        var totalAmount = 0
        var totalCapacity = 0
        val slotCount = this.ship.getAccessibleInventorySlotCount()
        for (i in ShipInventoryHandler.getEquipSlotCount()..<slotCount) {
            val stack = this.ship.inventory.getStackInSlot(i)
            if (stack.isEmpty() || stack.getItem() !is ShipTankItem) {
                continue
            }

            val handlerOptional = FluidUtil.getFluidHandler(stack)
            if (handlerOptional.isPresent()) {
                val handler = handlerOptional.get()
                totalAmount += handler.getFluidInTank(0).getAmount()
                totalCapacity += handler.getTankCapacity(0)
            } else {
                totalCapacity += ShipTankItem.getCapacity(stack)
            }
        }

        this.shipTankFluidAmountSynced = max(0, totalAmount)
        this.shipTankFluidCapacitySynced = max(0, totalCapacity)
    }

    private fun toActualShipSlot(localVisibleSlot: Int): Int {
        val mapped: Int = EQUIP_SLOTS + (clampPage(inventoryPage) * PAGE_SLOTS) + localVisibleSlot
        if (mapped < 0) {
            return 0
        }
        if (mapped >= SHIP_STORAGE_SIZE) {
            return SHIP_STORAGE_SIZE - 1
        }
        return mapped
    }

    private inner class EquipSlot(index: Int, x: Int, y: Int) : SlotItemHandler(ship.inventory, index, x, y) {
        override fun mayPlace(stack: ItemStack): Boolean {
            return ship.inventory.isItemValid(this.getSlotIndex(), stack)
        }
    }

    private inner class PagedShipSlot(private val localVisibleSlot: Int, x: Int, y: Int) :
        Slot(SimpleContainer(1), DUMMY_SLOT_INDEX, x, y) {
        override fun hasItem(): Boolean {
            return !getItem().isEmpty()
        }

        override fun getItem(): ItemStack {
            val idx = toActualShipSlot(localVisibleSlot)
            if (!ship.inventory.isSlotAvailable(idx)) {
                return ItemStack.EMPTY
            }
            return ship.inventory.getStackInSlot(idx)
        }

        override fun set(stack: ItemStack) {
            val idx = toActualShipSlot(localVisibleSlot)
            if (!ship.inventory.isSlotAvailable(idx)) {
                return
            }
            ship.inventory.setStackInSlot(idx, stack)
            setChanged()
        }

        fun initialize(stack: ItemStack) {
            this.set(stack)
        }

        override fun safeInsert(stack: ItemStack, count: Int): ItemStack {
            val idx = toActualShipSlot(localVisibleSlot)
            if (stack.isEmpty() || !ship.inventory.isSlotAvailable(idx) || !mayPlace(stack)) {
                return stack
            }

            val existing = ship.inventory.getStackInSlot(idx)
            var limit = min(getMaxStackSize(stack), ship.inventory.getSlotLimit(idx))
            if (!existing.isEmpty()) {
                if (!ItemStack.isSameItemSameComponents(existing, stack)) {
                    return stack
                }
                limit -= existing.getCount()
            }

            if (limit <= 0) {
                return stack
            }

            val move = min(limit, min(count, stack.getCount()))
            if (move <= 0) {
                return stack
            }

            val remainder = stack.copy()
            val inserted = stack.copyWithCount(move)
            remainder.shrink(move)

            if (existing.isEmpty()) {
                ship.inventory.setStackInSlot(idx, inserted)
            } else {
                val merged = existing.copy()
                merged.grow(move)
                ship.inventory.setStackInSlot(idx, merged)
            }

            setChanged()
            return remainder
        }

        override fun setByPlayer(newStack: ItemStack) {
            set(newStack)
        }

        override fun setByPlayer(newStack: ItemStack, oldStack: ItemStack) {
            set(newStack)
        }

        override fun remove(amount: Int): ItemStack {
            val idx = toActualShipSlot(localVisibleSlot)
            if (!ship.inventory.isSlotAvailable(idx)) {
                return ItemStack.EMPTY
            }
            return ship.inventory.extractItem(idx, amount, false)
        }

        override fun safeTake(count: Int, decrement: Int, player: Player): ItemStack {
            val taken = super.safeTake(count, decrement, player)
            if (!taken.isEmpty() && !ship.level().isClientSide) {
                ship.onInventoryChanged()
            }
            return taken
        }

        override fun mayPlace(stack: ItemStack): Boolean {
            val idx = toActualShipSlot(localVisibleSlot)
            return ship.inventory.isSlotAvailable(idx)
                    && ship.inventory.isItemValid(idx, stack)
        }

        override fun mayPickup(player: Player): Boolean {
            val idx = toActualShipSlot(localVisibleSlot)
            if (!ship.inventory.isSlotAvailable(idx)) {
                return false
            }
            return !ship.inventory.extractItem(idx, 1, true).isEmpty()
        }

        override fun getContainerSlot(): Int {
            return toActualShipSlot(localVisibleSlot)
        }

        override fun getSlotIndex(): Int {
            return toActualShipSlot(localVisibleSlot)
        }

        override fun getMaxStackSize(): Int {
            val idx = toActualShipSlot(localVisibleSlot)
            if (!ship.inventory.isSlotAvailable(idx)) {
                return 0
            }
            return ship.inventory.getSlotLimit(idx)
        }

        override fun setChanged() {
            if (!ship.level().isClientSide) {
                ship.onInventoryChanged()
            }
            super.setChanged()
        }

        fun clearClientCache() {
            // Slot contents are mirrored into the ship inventory directly on both sides.
        }

        companion object {
            private const val DUMMY_SLOT_INDEX = 0
        }
    }

    companion object {
        const val EQUIP_BUTTON_BASE: Int = 100
        const val PAGE_BUTTON_0: Int = 15
        const val PAGE_BUTTON_1: Int = 16
        const val PAGE_BUTTON_2: Int = 17
        const val TOGGLE_BUTTON_CAN_MELEE: Int = 30
        const val TOGGLE_BUTTON_LIGHT_ATTACK: Int = 31
        const val TOGGLE_BUTTON_HEAVY_ATTACK: Int = 32
        const val TOGGLE_BUTTON_LIGHT_AIRCRAFT: Int = 33
        const val TOGGLE_BUTTON_HEAVY_AIRCRAFT: Int = 34
        const val TOGGLE_BUTTON_RING_EFFECT: Int = 35
        const val TOGGLE_BUTTON_PASSIVE_ATTACK: Int = 50
        const val TOGGLE_BUTTON_ON_SIGHT: Int = 51
        const val TOGGLE_BUTTON_PVP: Int = 52
        const val TOGGLE_BUTTON_ANTI_AIR: Int = 53
        const val TOGGLE_BUTTON_ANTI_SUB: Int = 54
        const val TOGGLE_BUTTON_TIMEKEEP: Int = 55
        const val TOGGLE_BUTTON_PICK_ITEM: Int = 60
        const val TOGGLE_BUTTON_AUTO_PUMP: Int = 61
        const val TOGGLE_BUTTON_MOUNT: Int = 70
        const val TOGGLE_BUTTON_SHOW_HELD: Int = 71

        const val SLIDER_FOLLOW_MIN_BASE: Int = 400
        const val SLIDER_FOLLOW_MAX_BASE: Int = 500
        const val SLIDER_WP_STAY_BASE: Int = 600
        const val SLIDER_FLEE_HP_BASE: Int = 700
        const val SLIDER_RATION_MORALE_BASE: Int = 900
        const val ACTION_TASK_SELECT_BASE: Int = 1000
        const val ACTION_TASK_META_TOGGLE: Int = 1010
        const val ACTION_TASK_ORE_TOGGLE: Int = 1011
        const val ACTION_TASK_NBT_TOGGLE: Int = 1012
        const val ACTION_SIDE_TOGGLE_BASE: Int = 1100

        const val STATE_FLAG_CAN_MELEE: Int = 3
        const val STATE_FLAG_LIGHT_ATTACK: Int = 4
        const val STATE_FLAG_HEAVY_ATTACK: Int = 5
        const val STATE_FLAG_LIGHT_AIRCRAFT_ATTACK: Int = 6
        const val STATE_FLAG_HEAVY_AIRCRAFT_ATTACK: Int = 7
        const val STATE_FLAG_RING_EFFECT: Int = 9
        const val STATE_FLAG_ON_SIGHT: Int = 12
        const val STATE_FLAG_PVP: Int = 18
        const val STATE_FLAG_ANTI_AIR: Int = 19
        const val STATE_FLAG_ANTI_SUB: Int = 20
        const val STATE_FLAG_PASSIVE_ATTACK: Int = 21
        const val STATE_FLAG_TIMEKEEP: Int = 22
        const val STATE_FLAG_PICK_ITEM: Int = 23
        private const val STATE_FLAG_APPEARANCE = 25
        const val STATE_FLAG_AUTO_PUMP: Int = 26

        const val STATE_MINOR_RATION_MORALE: Int = 9
        const val STATE_MINOR_FOLLOW_MIN: Int = 10
        const val STATE_MINOR_FOLLOW_MAX: Int = 11
        const val STATE_MINOR_FLEE_HP: Int = 12
        const val STATE_MINOR_TASK_ID: Int = 40
        const val STATE_MINOR_TASK_SIDE: Int = 41
        const val STATE_MINOR_WP_STAY: Int = 44
        const val STATE_MINOR_GUARD_X: Int = 14
        const val STATE_MINOR_GUARD_Y: Int = 15
        const val STATE_MINOR_GUARD_Z: Int = 16
        const val STATE_MINOR_GUARD_DIM: Int = 17
        const val STATE_MINOR_GUARD_TYPE: Int = 18
        private const val STATE_MINOR_EQUIP_DRUM = 36

        private const val FOLLOW_MIN_MIN = 1
        private const val FOLLOW_MIN_MAX = 31
        private const val FOLLOW_MAX_MIN = 2
        private const val FOLLOW_MAX_MAX = 32
        private const val FLEE_HP_MIN = 0
        private const val FLEE_HP_MAX = 100
        private const val WP_STAY_MIN = 0
        private const val WP_STAY_MAX = 16
        private const val RATION_MORALE_MIN = 1
        private const val RATION_MORALE_MAX = 4

        private const val EQUIP_SLOTS = 6
        private const val PAGE_SLOTS = 18
        private const val SHIP_STORAGE_SIZE = 60
        private const val SHIP_PAGE_COUNT = 3

        const val SHIP_INV_X: Int = 8
        const val SHIP_INV_Y: Int = 18
        const val EQUIP_INV_X: Int = 144
        const val EQUIP_INV_Y: Int = 18
        const val PLAYER_INV_X: Int = 8
        const val PLAYER_INV_Y: Int = 132
        const val HOTBAR_Y: Int = 190

        private val VISIBLE_SHIP_SLOTS: Int = EQUIP_SLOTS + PAGE_SLOTS
        private const val PLAYER_INV_SLOTS = 27
        private const val PLAYER_HOTBAR_SLOTS = 9
        private val FIRST_PLAYER_SLOT: Int = VISIBLE_SHIP_SLOTS
        private val END_PLAYER_SLOT: Int = FIRST_PLAYER_SLOT + PLAYER_INV_SLOTS + PLAYER_HOTBAR_SLOTS

        private fun getEntity(playerInv: Inventory, buf: RegistryFriendlyByteBuf): EntityShipBase {
            checkNotNull(buf) { "Missing ship entity data." }

            val entityId = buf.readInt()
            if (playerInv.player.level().getEntity(entityId) is EntityShipBase
                && ship.isAlive()
                && !ship.isRemoved()
            ) {
                return ship
            }

            throw IllegalStateException("Ship entity not found.")
        }
    }
}
