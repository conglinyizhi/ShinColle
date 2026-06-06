package org.trp.shincolle.entity.base

import com.mojang.serialization.Dynamic
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.DamageTypeTags
import net.minecraft.tags.FluidTags
import net.minecraft.util.Mth
import net.minecraft.world.*
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.Brain
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuConstructor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.pathfinder.PathType
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.FluidUtil
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import org.trp.shincolle.Config
import org.trp.shincolle.Config.ShipCustomSoundType.Companion.timeKeeping
import org.trp.shincolle.Shincolle.Companion.diagnosticLog
import org.trp.shincolle.block.entity.CraneBlockEntity
import org.trp.shincolle.block.entity.IWaypoint
import org.trp.shincolle.block.entity.WayPointBlockEntity
import org.trp.shincolle.client.ClientProxy
import org.trp.shincolle.command.ModCommands
import org.trp.shincolle.entity.*
import org.trp.shincolle.entity.base.ShipBrainMemory.PassiveCombatStateMemory
import org.trp.shincolle.entity.base.path.ShipLegacyNavigation
import org.trp.shincolle.entity.base.path.ShipMoveControl
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.init.ModParticles
import org.trp.shincolle.init.ModSounds
import org.trp.shincolle.init.ModSounds.getShipSound
import org.trp.shincolle.inventory.ShipInventoryHandler
import org.trp.shincolle.item.CombatRationItem
import org.trp.shincolle.item.CombatRationItem.Companion.getMoraleValue

import org.trp.shincolle.item.CombatRationItem.Companion.rollFuelGain
import org.trp.shincolle.item.DebugInspectorItem.Companion.markBucketRepairTriggered
import org.trp.shincolle.item.LegacyEquipItem
import org.trp.shincolle.item.LegacyEquipStats
import org.trp.shincolle.item.LegacyEquipStats.getMainAttrs
import org.trp.shincolle.menu.ShipContainerMenu
import org.trp.shincolle.reference.Values
import org.trp.shincolle.reference.Values.resetFormationValue
import org.trp.shincolle.reference.Values.resetMoraleValue
import org.trp.shincolle.server.PlayerStateService.adjustOwnedMarriedShipCount
import org.trp.shincolle.server.PlayerStateService.admiralData
import org.trp.shincolle.server.ShipRegistrySavedData.Companion.get
import org.trp.shincolle.utility.FormationHelper.getFormationBuffs
import org.trp.shincolle.utility.PerformanceTrace.addShipTime
import org.trp.shincolle.utility.PerformanceTrace.elapsed
import org.trp.shincolle.utility.PerformanceTrace.enabled
import org.trp.shincolle.utility.PerformanceTrace.formatMs
import org.trp.shincolle.utility.PerformanceTrace.logSlowShipTick
import org.trp.shincolle.utility.PerformanceTrace.now
import org.trp.shincolle.utility.TaskHelper.onUpdateTask
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.function.Consumer
import java.util.function.Predicate
import kotlin.math.*

abstract class EntityShipBase protected constructor(type: EntityType<out TamableAnimal?>, level: Level) :
    TamableAnimal(type, level) {
    private var marriageCountReleased = false

    @JvmField
    val inventory: ShipInventoryHandler?
    internal val combat: EntityShipBaseCombat
    private val pointer: EntityShipBasePointer
    private val emotions: EntityShipBaseEmotions
    private val faceExpressions: EntityShipBaseFaceExpressions
    private val reactions: EntityShipBaseReactions
    private val passiveCombat: EntityShipBasePassiveCombat
    private val serialization: EntityShipBaseSerialization
    @JvmField
    val legacyShipStats: LegacyShipStats
    internal val legacyStateInternal: EntityShipLegacyState
    val taskRuntime: ShipTaskRuntime
    private val lifecycleMovement: ShipMovementCoordinator
    private val retreatMovement: ShipMovementCoordinator
    private val pickupMovement: ShipMovementCoordinator
    private val guardMovement: ShipMovementCoordinator
    private val pointerMovement: ShipMovementCoordinator
    private val followOwnerMovement: ShipMovementCoordinator
    private val combatMovement: ShipMovementCoordinator
    private val idleMovement: ShipMovementCoordinator
    var guardedEntityIdInternal: UUID? = null
        private set
    var fishHook: EntityShipFishingHook? = null
    var isLegacyStateInitializedInternal: Boolean = false
    private var legacyShipExtPropsBackup = CompoundTag()
    private var shipDeathTicks = 0
    private var hostileCanDrop = true
    private val stateUpdateTimer = 0
    private var customHurtTime = 0
    private var hurtSoundCooldown = 0
    private var feedSoundCooldown = 0
    private var guiOpenCount = 0
    private val forcedCompassChunks: MutableSet<Long?> = HashSet<Long?>()
    private var forcedCompassChunkCenterX = Int.MIN_VALUE
    private var forcedCompassChunkCenterZ = Int.MIN_VALUE
    var customSwingTicks: Int = 0
    var isCustomSwinging: Boolean = false
    private var perfShipCoreNanos: Long = 0
    private var perfShipTaskNanos: Long = 0
    private var perfShipSupportNanos: Long = 0
    private var perfShipPeriodicNanos: Long = 0
    private var loggedBrainAiEntry = false

    fun pointerMovementCoordinator(): ShipMovementCoordinator {
        return this.pointerMovement
    }

    fun guardMovementCoordinator(): ShipMovementCoordinator {
        return this.guardMovement
    }

    fun followOwnerMovementCoordinator(): ShipMovementCoordinator {
        return this.followOwnerMovement
    }

    fun combatMovementCoordinator(): ShipMovementCoordinator {
        return this.combatMovement
    }

    fun idleMovementCoordinator(): ShipMovementCoordinator {
        return this.idleMovement
    }

    override fun brainProvider(): Brain.Provider<EntityShipBase> {
        return Brain.provider<EntityShipBase>(EntityShipBrainAi.MEMORY_TYPES, EntityShipBrainAi.SENSOR_TYPES)
    }

    override fun makeBrain(dynamic: Dynamic<*>): Brain<*> {
        return EntityShipBrainAi.makeBrain(this, this.brainProvider().makeBrain(dynamic))
    }

    override fun customServerAiStep() {
        if (this.level() is ServerLevel) {
            val serverLevel = this.level() as ServerLevel
            if (!this.loggedBrainAiEntry && this.tickCount > 0) {
                this.loggedBrainAiEntry = true
                diagnosticLog(
                    "[SCBrainDiag] customServerAiStep ship={} type={} ownerUuid={} tame={} noFuel={} deadPose={} navigation={} brain={}",
                    this.getUUID(),
                    BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()),
                    this.ownerUUID,
                    this.isTame,
                    this.isNoFuel,
                    this.isInDeadPose,
                    this.getNavigation().javaClass.getSimpleName(),
                    this.getBrain().javaClass.getSimpleName()
                )
            }
            EntityShipBrainAi.tick(serverLevel, this)
        }
        super.customServerAiStep()
    }

    fun moveGuardTargetTo(target: Vec3?, speed: Double): Boolean {
        return if (target != null) this.guardMovement.moveTo(target, speed) else false
    }

    fun moveGuardTargetTo(target: Entity, speed: Double): Boolean {
        return this.guardMovement.moveTo(target, speed)
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        EntityShipBaseSerialization.Companion.defineSynchedData(builder)
        builder.define<CompoundTag?>(LEGACY_BONUS_DATA, CompoundTag())
        builder.define<CompoundTag?>(POINTER_TARGET_DATA, CompoundTag())
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        this.serialization.addAdditionalSaveData(compound)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        this.serialization.readAdditionalSaveData(compound)
        if (!this.level().isClientSide && (compound.hasUUID("Owner") || compound.getBoolean(spawnEggTagName))) {
            diagnosticLog(
                "[SCLoadDiag] readShip ship={} ownerUuid={} tame={} spawnEgg={} noFuel={} fuel={} orderedToSit={} sittingPose={}",
                this.getUUID(),
                this.ownerUUID,
                this.isTame,
                compound.getBoolean(spawnEggTagName),
                this.isNoFuel,
                this.fuel,
                this.isOrderedToSit(),
                this.isInSittingPose()
            )
        }
    }

    var level: Int
        get() = this.entityData.get<Int?>(SHIP_LEVEL)
        set(level) {
            this.entityData.set<Int?>(
                SHIP_LEVEL,
                Mth.clamp(level, 1, SHIP_LEVEL_HARD_CAP)
            )
            this.recalculateLegacyShipStats()
        }

    var exp: Int
        get() = this.entityData.get<Int?>(SHIP_EXP)
        set(exp) {
            this.entityData.set<Int?>(SHIP_EXP, max(0, exp))
        }

    var shipKills: Int
        get() = this.entityData.get<Int?>(SHIP_KILLS)
        set(kills) {
            this.entityData.set<Int?>(SHIP_KILLS, max(0, kills))
        }

    fun addShipKill() {
        this.shipKills = this.shipKills + 1
    }

    val isGuiOpen: Boolean
        get() = this.guiOpenCount > 0

    fun incrementGuiOpen() {
        this.guiOpenCount++
    }

    fun decrementGuiOpen() {
        this.guiOpenCount = max(0, this.guiOpenCount - 1)
    }

    val maxShipLevel: Int
        get() {
            val configured =
                if (this.isStateMarried) Config.shipMaxLevelMarried else Config.shipMaxLevelNormal
            return Mth.clamp(configured, 1, SHIP_LEVEL_HARD_CAP)
        }

    val expToNextLevel: Int
        get() {
            val level = max(1, this.level)
            return max(
                1,
                level * Config.shipExpModifier + Config.shipExpModifier
            )
        }

    fun addShipExp(exp: Int) {
        if (exp <= 0 || this.level().isClientSide || !this.isTame) {
            return
        }

        val maxLevel = this.maxShipLevel
        var level = this.level
        if (level >= maxLevel) {
            return
        }

        var totalExp = this.exp + exp
        var leveledUp = false
        while (level < maxLevel) {
            val expNext = max(1, level * Config.shipExpModifier + Config.shipExpModifier)
            if (totalExp < expNext) {
                break
            }
            totalExp -= expNext
            level++
            leveledUp = true
        }

        if (level >= maxLevel) {
            totalExp = 0
        }

        this.exp = totalExp
        if (leveledUp) {
            this.level = level
            this.setHealth(this.getMaxHealth())
            this.playLevelUpEffects()
        }
    }

    fun addTrainingBookLevel(levelGain: Int): Boolean {
        if (levelGain <= 0 || this.level().isClientSide || !this.isTame) {
            return false
        }

        val maxLevel = this.maxShipLevel
        val currentLevel = this.level
        if (currentLevel >= maxLevel) {
            return false
        }

        val targetLevel = min(maxLevel, currentLevel + levelGain)
        this.level = targetLevel
        this.setHealth(this.getMaxHealth())
        this.playLevelUpEffects()
        return true
    }

    private fun playLevelUpEffects() {
        if (this.level().isClientSide) {
            return
        }

        if (this.getRandom().nextInt(4) == 0) {
            this.playSound(SoundEvents.PLAYER_LEVELUP, 0.75f, 1.0f)
        } else {
            this.playSound(ModSounds.SHIP_LEVELUP.get(), 0.75f, 1.0f)
        }
    }

    var ammoLight: Int
        get() {
            if (this.isCreativeDebuggerActive) {
                return 30000
            }
            return this.entityData.get<Int?>(AMMO_LIGHT)
        }
        set(value) {
            this.entityData.set<Int?>(AMMO_LIGHT, value)
        }

    var ammoHeavy: Int
        get() {
            if (this.isCreativeDebuggerActive) {
                return 30000
            }
            return this.entityData.get<Int?>(AMMO_HEAVY)
        }
        set(value) {
            this.entityData.set<Int?>(AMMO_HEAVY, value)
        }

    var numAircraftLight: Int
        get() = this.entityData.get<Int?>(AIRCRAFT_LIGHT)
        set(count) {
            this.setStateMinor(7, max(0, count))
        }

    var numAircraftHeavy: Int
        get() = this.entityData.get<Int?>(AIRCRAFT_HEAVY)
        set(count) {
            this.setStateMinor(8, max(0, count))
        }

    fun hasAirLight(): Boolean {
        return this.numAircraftLight > 0
    }

    fun hasAirHeavy(): Boolean {
        return this.numAircraftHeavy > 0
    }

    var isPointerSelected: Boolean
        get() = this.entityData.get<Boolean?>(POINTER_SELECTED)
        set(selected) {
            this.entityData.set<Boolean?>(POINTER_SELECTED, selected)
        }

    fun togglePointerSelected() {
        this.isPointerSelected = !this.isPointerSelected
    }

    fun setPointerTarget(target: Vec3?, durationTicks: Long) {
        if (target == null) {
            this.clearPointerTarget()
            return
        }
        if (this.hasBlockGuardTarget()) {
            this.suspendBlockGuardTarget()
        }
        this.pointer.clearPointerTargetEntity()
        this.pointerMovement.stop()
        this.pointer.setPointerTarget(target, durationTicks)
    }

    fun hasPointerTarget(): Boolean {
        return this.pointer.hasPointerTarget()
    }

    val pointerTarget: Vec3?
        get() = this.pointer.getPointerTarget()

    val rawPointerTarget: Vec3?
        get() = this.pointer.rawPointerTarget

    val pointerTargetRemainingTicks: Long
        get() = this.pointer.pointerTargetRemainingTicks

    fun clearPointerTarget() {
        this.pointer.clearPointerTarget()
        this.pointerMovement.stop()
    }

    fun setPointerTargetEntity(target: Entity?, durationTicks: Long) {
        if (target == null) {
            this.clearPointerTargetEntity()
            return
        }
        if (this.hasBlockGuardTarget()) {
            this.suspendBlockGuardTarget()
        }
        this.pointer.setPointerTargetEntity(target, durationTicks)
        this.pointerMovement.stop()
    }

    fun hasPointerTargetEntity(): Boolean {
        return this.pointer.hasPointerTargetEntity()
    }

    val pointerTargetEntity: Entity?
        get() = this.pointer.pointerTargetEntity

    val pointerTargetEntityRemainingTicks: Long
        get() = this.pointer.pointerTargetEntityRemainingTicks

    fun clearPointerTargetEntity() {
        this.pointer.clearPointerTargetEntity()
        this.pointerMovement.stop()
    }

    fun tickPassiveCombatTargetingBrain() {
        this.passiveCombat.tickTargeting()
    }

    fun updatePassiveCombatStateBrain(): PassiveCombatStateMemory {
        return this.passiveCombat.updateActionState()
    }

    fun tickPassiveCombatActionsBrain(state: PassiveCombatStateMemory?) {
        if (state != null) {
            this.passiveCombat.tickAttacks(state)
        }
    }

    fun clearPassiveCombatTargetBrain(stopNavigation: Boolean) {
        this.passiveCombat.clearTarget()
        if (stopNavigation) {
            this.combatMovement.stop()
        }
    }

    var faceId: Int
        get() = this.entityData.get<Int?>(FACE_ID)
        set(id) {
            this.entityData.set<Int?>(
                FACE_ID,
                Mth.clamp(id, FACE_ID_MIN, FACE_ID_MAX)
            )
        }

    var mouthId: Int
        get() = this.entityData.get<Int?>(MOUTH_ID)
        set(id) {
            this.entityData.set<Int?>(
                MOUTH_ID,
                Mth.clamp(id, MOUTH_ID_MIN, MOUTH_ID_MAX)
            )
        }

    var emotionPrimary: Int
        get() = this.entityData.get<Int?>(EMOTION_PRIMARY)
        set(value) {
            this.entityData.set<Int?>(EMOTION_PRIMARY, value)
            this.setStateEmotion(1, value, false)
        }

    var emotionSecondary: Int
        get() = this.entityData.get<Int?>(EMOTION_SECONDARY)
        set(value) {
            this.entityData.set<Int?>(EMOTION_SECONDARY, value)
            this.setStateEmotion(7, value, false)
        }

    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(key)
        if (EMOTION_PARTICLE == key && this.level().isClientSide) {
            val packed = this.entityData.get<Int?>(EMOTION_PARTICLE)
            val typeId = packed and 0xFF
            EmotionParticleType.Companion.fromId(typeId)?.let { this.reactions.spawnEmotionParticleClient(it) }
        } else if (LEGACY_BONUS_DATA == key && this.level().isClientSide) {
            this.applyLegacyBonusTag(this.entityData.get<CompoundTag?>(LEGACY_BONUS_DATA))
            this.recalculateLegacyShipStats()
        }
    }

    var morale: Int
        get() = this.entityData.get<Int?>(MORALE)
        set(value) {
            this.entityData.set<Int?>(
                MORALE,
                Mth.clamp(value, 0, MORALE_MAX)
            )
            if (!this.level().isClientSide) {
                this.recalculateLegacyShipStats()
            }
        }

    fun addMorale(delta: Int) {
        this.morale = this.morale + delta
    }

    var formationTeam: Int
        get() = this.entityData.get<Int?>(FORMATION_TEAM)
        set(team) {
            this.entityData.set<Int?>(FORMATION_TEAM, team)
        }

    var formationSlot: Int
        get() = this.entityData.get<Int?>(FORMATION_SLOT)
        set(slot) {
            this.entityData.set<Int?>(FORMATION_SLOT, slot)
        }

    open val isNonCombatShip: Boolean
        get() = false

    var isNoFuel: Boolean
        get() = this.fuel <= 0
        set(value) {
            val wasNoFuel = this.isNoFuel
            this.entityData.set<Boolean?>(NO_FUEL, value)
            if (value) {
                this.entityData.set<Int?>(FUEL, 0)
            }
            val isNoFuelNow = this.isNoFuel
            if (wasNoFuel != isNoFuelNow) {
                this.updateFuelState(isNoFuelNow)
            }
        }

    protected open fun updateFuelState(nofuel: Boolean) {
    }

    open fun hasShipMounts(): Boolean {
        return false
    }

    fun canSummonMounts(): Boolean {
        return (this.getStateEmotion(0) and 1) == 1 && !this.isInDeadPose
    }

    open fun summonMountEntity(): EntityMountBase? {
        return null
    }

    protected fun updateMountSummon() {
        if (!this.level().isClientSide) {
            if (this.hasShipMounts() && this.canSummonMounts() && !this.isPassenger()) {
                val mount = this.summonMountEntity()
                if (mount != null) {
                    mount.hostUUID = this.uuid
                    mount.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot())
                    this.level().addFreshEntity(mount)
                    this.getPassengers().forEach(Consumer { obj: Entity? -> obj!!.stopRiding() })
                    this.startRiding(mount, true)
                }
            } else if (this.isPassenger() && this.vehicle is EntityMountBase) {
                if (!this.canSummonMounts()) {
                    this.stopRiding()
                }
            }
        }
    }

    val isInDeadPose: Boolean
        get() = this.isDeadOrDying() || this.getHealth() <= 0.0f || this.isNoFuel

    fun getStateEmotion(index: Int): Int {
        return when (index) {
            0 -> this.entityData.get<Int?>(LEGACY_EMOTION_0)
            1 -> this.entityData.get<Int?>(LEGACY_EMOTION_1)
            2 -> this.entityData.get<Int?>(LEGACY_EMOTION_2)
            3 -> this.entityData.get<Int?>(LEGACY_EMOTION_3)
            4 -> this.entityData.get<Int?>(LEGACY_EMOTION_4)
            5 -> this.entityData.get<Int?>(LEGACY_EMOTION_5)
            6 -> this.entityData.get<Int?>(LEGACY_EMOTION_6)
            7 -> this.entityData.get<Int?>(LEGACY_EMOTION_7)
            else -> 0
        }
    }

    fun setStateEmotion(index: Int, value: Int, sync: Boolean) {
        when (index) {
            0 -> this.entityData.set<Int?>(LEGACY_EMOTION_0, value)
            1 -> this.entityData.set<Int?>(LEGACY_EMOTION_1, value)
            2 -> this.entityData.set<Int?>(LEGACY_EMOTION_2, value)
            3 -> this.entityData.set<Int?>(LEGACY_EMOTION_3, value)
            4 -> this.entityData.set<Int?>(LEGACY_EMOTION_4, value)
            5 -> this.entityData.set<Int?>(LEGACY_EMOTION_5, value)
            6 -> this.entityData.set<Int?>(LEGACY_EMOTION_6, value)
            7 -> this.entityData.set<Int?>(LEGACY_EMOTION_7, value)
            else -> {}
        }
    }

    var attackTick: Int
        get() = this.entityData.get<Int?>(LEGACY_ATTACK_TICK)
        set(value) {
            this.entityData.set<Int?>(
                LEGACY_ATTACK_TICK,
                Mth.clamp(value, 0, LEGACY_ATTACK_TICK_MAX)
            )
        }

    var attackTick2: Int
        get() = this.entityData.get<Int?>(LEGACY_ATTACK_TICK_2)
        set(value) {
            this.entityData.set<Int?>(LEGACY_ATTACK_TICK_2, max(0, value))
        }

    fun getSwingTime(partialTick: Float): Float {
        return this.getAttackAnim(partialTick)
    }

    val isSitting: Boolean
        get() = this.isOrderedToSit() || this.isInSittingPose()

    override fun isSprinting(): Boolean {
        return super.isSprinting() || this.walkAnimation.speed() > 0.9f
    }

    protected val ownerPlayer: Player?
        get() {
            val owner = this.getOwner()
            return if (owner is Player) owner else null
        }

    fun playerHasCombatRation(player: Player?): Boolean {
        if (player == null) return false
        val mainHand = player.getMainHandItem()
        if (!mainHand.isEmpty() && mainHand.getItem() is CombatRationItem) {
            return true
        }
        val offHand = player.getOffhandItem()
        if (!offHand.isEmpty() && offHand.getItem() is CombatRationItem) {
            return true
        }
        return false
    }

    fun shouldFollowOwner(): Boolean {
        if (this.isOrderedToSit() || this.isInSittingPose() || this.isInDeadPose || this.isPassenger()) {
            return false
        }
        if (this.isNoFuel) {
            return false
        }
        val owner = this.getOwner()
        if (owner == null) {
            return false
        }
        if (this.hasBlockGuardTarget() || this.hasPointerTarget()) {
            return false
        }
        if (this.hasPointerTargetEntity()) {
            return false
        }
        if (this.getTarget() != null) {
            return false
        }
        val configuredMin = this.getStateMinor(ShipContainerMenu.STATE_MINOR_FOLLOW_MIN)
        val minDist = if (configuredMin <= 0) 5.0f else Mth.clamp(configuredMin, 1, 31).toFloat()

        var checkMinDist = minDist.toDouble()
        if (owner is Player && playerHasCombatRation(owner)) {
            checkMinDist = 1.5
        }

        val distanceSqr = this.distanceToSqr(owner)
        return distanceSqr > (checkMinDist * checkMinDist)
    }

    fun explainFollowBlockReason(): String {
        if (this.isOrderedToSit()) return "orderedToSit"
        if (this.isInSittingPose()) return "sittingPose"
        if (this.isPassenger()) {
            val vehicle = this.getVehicle()
            if (vehicle != null && vehicle.isAlive) return "passenger"
        }
        if (this.isNoFuel) return "noFuel"
        val owner = this.getOwner()
        if (owner == null) {
            val ownerUuid = this.ownerUUID
            return if (ownerUuid == null) "noOwnerUuid" else "ownerEntityMissing"
        }
        if (this.hasBlockGuardTarget()) return "blockGuardTarget"
        if (this.hasPointerTarget()) return "pointerTarget"
        if (this.hasPointerTargetEntity()) return "pointerTargetEntity"
        if (this.getTarget() != null) return "attackTarget"

        val configuredMin = this.getStateMinor(ShipContainerMenu.STATE_MINOR_FOLLOW_MIN)
        val minDist = if (configuredMin <= 0) 5.0f else Mth.clamp(configuredMin, 1, 31).toFloat()

        var checkMinDist = minDist.toDouble()
        if (owner is Player && playerHasCombatRation(owner)) {
            checkMinDist = 1.5
        }

        val distanceSqr = this.distanceToSqr(owner)
        if (distanceSqr <= (checkMinDist * checkMinDist)) return "withinMinDistance"
        return "eligible"
    }


    protected fun consumeLightAmmo(amount: Int): Boolean {
        return this.combat.consumeLightAmmo(amount)
    }

    protected fun consumeHeavyAmmo(amount: Int): Boolean {
        return this.combat.consumeHeavyAmmo(amount)
    }

    protected fun isSameOwnerAttackTarget(target: Entity?): Boolean {
        return this.combat.isSameOwnerTarget(target)
    }

    open fun supportsAircraftCombat(): Boolean {
        return false
    }

    open fun getAttackAircraftType(isLightAircraft: Boolean): EntityType<out TamableAnimal?>? {
        return null
    }

    open val aircraftLaunchHeight: Double
        get() = this.getBbHeight() * 0.65

    open val aircraftLightLevelBonus: Float
        get() = 0.0f

    open val aircraftHeavyLevelBonus: Float
        get() = 0.0f

    open fun performLightAttack(target: Entity?) {
        this.combat.performLightAttack(target)
    }

    fun spawnLightAttackMuzzleParticles(serverLevel: ServerLevel, target: Entity) {
        val from = this.position().add(0.0, 0.8, 0.0)
        val to = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0)
        var look = to.subtract(from)
        if (look.lengthSqr() < 1.0E-6) {
            look = this.getLookAngle()
        } else {
            look = look.normalize()
        }

        val posX = this.getX()
        val posY = this.getY()
        val posZ = this.getZ()

        for (i in 0..23) {
            val ran1 = (this.getRandom().nextFloat() - 0.5f).toDouble()
            val ran2 = this.getRandom().nextFloat().toDouble()
            val ran3 = this.getRandom().nextFloat().toDouble()
            val baseX = posX + look.x - 0.5 + 0.05 * i
            val baseZ = posZ + look.z - 0.5 + 0.05 * i

            serverLevel.sendParticles<SimpleParticleType?>(
                ParticleTypes.LARGE_SMOKE,
                baseX, posY + 0.6 + ran1, baseZ,
                1, look.x * 0.3 * ran2, 0.05 * ran2, look.z * 0.3 * ran2, 0.0
            )
            serverLevel.sendParticles<SimpleParticleType?>(
                ParticleTypes.LARGE_SMOKE,
                baseX, posY + 1.0 + ran1, baseZ,
                1, look.x * 0.3 * ran3, 0.05 * ran3, look.z * 0.3 * ran3, 0.0
            )
        }
    }

    fun spawnLightAttackTargetParticles(serverLevel: ServerLevel, target: Entity) {
        val posX = target.getX()
        val posY = target.getY()
        val posZ = target.getZ()

        serverLevel.sendParticles<SimpleParticleType?>(
            ParticleTypes.EXPLOSION_EMITTER, posX, posY + 1.5, posZ,
            1, 0.0, 0.0, 0.0, 0.0
        )

        for (i in 0..14) {
            val ran1 = ((this.getRandom().nextFloat() * 3.0f) - 1.5f).toDouble()
            val ran2 = ((this.getRandom().nextFloat() * 3.0f) - 1.5f).toDouble()
            serverLevel.sendParticles<SimpleParticleType?>(
                ParticleTypes.LAVA,
                posX + ran1, posY + 1.0, posZ + ran2,
                1, 0.0, 0.0, 0.0, 0.0
            )
        }
    }

    open fun performHeavyAttack(target: Entity?): Boolean {
        return this.combat.performHeavyAttack(target)
    }

    fun getStateMinor(index: Int): Int {
        if (index == 6) {
            return this.fuel
        }
        return legacyStateInternal.getInt(legacyStateInternal.stateMinor, index)
    }

    fun setStateMinor(index: Int, value: Int) {
        if (index == 6) {
            this.fuel = value
            return
        }
        if (index == 7) {
            this.entityData.set<Int?>(AIRCRAFT_LIGHT, value)
        } else if (index == 8) {
            this.entityData.set<Int?>(AIRCRAFT_HEAVY, value)
        }

        legacyStateInternal.setInt(legacyStateInternal.stateMinor, index, value)
        if (index == STATE_MINOR_SHIP_CLASS) {
            this.recalculateLegacyShipStats()
        }
    }

    val guardedPos: IntArray?
        get() = intArrayOf(
            getStateMinor(STATE_MINOR_GUARD_X),
            getStateMinor(STATE_MINOR_GUARD_Y),
            getStateMinor(STATE_MINOR_GUARD_Z),
            getStateMinor(STATE_MINOR_GUARD_DIM),
            getStateMinor(STATE_MINOR_GUARD_TYPE)
        )

    fun setGuardedPos(x: Int, y: Int, z: Int, dim: Int, type: Int) {
        this.setStateMinor(STATE_MINOR_GUARD_X, x)
        this.setStateMinor(STATE_MINOR_GUARD_Y, y)
        this.setStateMinor(STATE_MINOR_GUARD_Z, z)
        this.setStateMinor(STATE_MINOR_GUARD_DIM, dim)
        this.setStateMinor(STATE_MINOR_GUARD_TYPE, type)
        if (type != 2) {
            this.guardedEntityIdInternal = null
        }
    }

    val guardTarget: ShipGuardTarget
        get() = ShipGuardTarget.Companion.fromShip(this)

    fun hasBlockGuardTarget(): Boolean {
        return this.guardTarget.isBlock
    }

    fun hasEntityGuardTarget(): Boolean {
        return this.guardTarget.isEntity
    }

    fun setGuardBlockTarget(pos: BlockPos) {
        this.setGuardBlockTarget(pos, getLegacyDimensionId(this.level()))
    }

    fun setGuardBlockTarget(pos: BlockPos, dimensionId: Int) {
        this.guardedEntityIdInternal = null
        this.setGuardedPos(pos.getX(), pos.getY(), pos.getZ(), dimensionId, ShipGuardTarget.Type.BLOCK.legacyId())
    }

    fun suspendBlockGuardTarget() {
        val target = this.guardTarget
        if (target.isBlock) {
            this.setGuardedPos(target.x, target.y, target.z, target.dimensionId, ShipGuardTarget.Type.NONE.legacyId())
            this.guardMovement.stop()
        }
    }

    fun clearGuardTarget() {
        this.guardedEntityIdInternal = null
        this.setGuardedPos(
            ShipGuardTarget.Companion.NONE.x,
            ShipGuardTarget.Companion.NONE.y,
            ShipGuardTarget.Companion.NONE.z,
            ShipGuardTarget.Companion.NONE.dimensionId,
            ShipGuardTarget.Companion.NONE.legacyType()
        )
        this.guardMovement.stop()
    }

    var guardedEntity: Entity?
        get() {
            if (this.guardedEntityIdInternal == null) {
                return null
            }

            if (this.level() is ServerLevel) {
            val serverLevel = this.level() as ServerLevel
                if (this.getGuardedPos(3) == getLegacyDimensionId(serverLevel)) {
                    val entity: Entity? = serverLevel.getEntity(this.guardedEntityIdInternal)
                    if (entity == null || !entity.isAlive || entity.isRemoved) {
                        return null
                    }
                    return entity
                }

                for (level in serverLevel.getServer().getAllLevels()) {
                    if (this.getGuardedPos(3) == getLegacyDimensionId(level)) {
                        val entity: Entity? = level.getEntity(this.guardedEntityIdInternal)
                        if (entity == null || !entity.isAlive || entity.isRemoved) {
                            return null
                        }
                        return entity
                    }
                }
                return null
            }

            for (entity in this.level().getEntities(
                this,
                this.getBoundingBox().inflate(128.0),
                Predicate { candidate: Entity? -> candidate!!.getUUID() == this.guardedEntityIdInternal })) {
                if (entity.getUUID() == this.guardedEntityIdInternal && entity.isAlive && !entity.isRemoved) {
                    return entity
                }
            }
            return null
        }
        set(entity) {
            if (entity == null) {
                this.guardedEntityIdInternal = null
                if (this.hasEntityGuardTarget()) {
                    this.clearGuardTarget()
                }
                return
            }

            this.guardedEntityIdInternal = entity.getUUID()
            this.setGuardedPos(
                -1,
                -1,
                -1,
                getLegacyDimensionId(entity.level()),
                ShipGuardTarget.Type.ENTITY.legacyId()
            )
        }

    fun loadGuardedEntityIdInternal(guardedEntityId: UUID?) {
        this.guardedEntityIdInternal = guardedEntityId
    }

    fun canLegacyDeathDropInternal(): Boolean {
        return this.hostileCanDrop
    }

    fun setLegacyDeathDropInternal(value: Boolean) {
        this.hostileCanDrop = value
    }

    var legacyShipExtPropsBackupInternal: CompoundTag?
        get() = this.legacyShipExtPropsBackup.copy()
        set(backup) {
            this.legacyShipExtPropsBackup = if (backup == null) CompoundTag() else backup.copy()
        }

    fun getStateTimer(index: Int): Int {
        return legacyStateInternal.getInt(legacyStateInternal.stateTimer, index)
    }

    fun setStateTimer(index: Int, value: Int) {
        legacyStateInternal.setInt(legacyStateInternal.stateTimer, index, value)
    }

    fun getStateFlag(index: Int): Boolean {
        return legacyStateInternal.getBoolean(legacyStateInternal.stateFlag, index)
    }

    fun getStateFlagI(index: Int): Byte {
        return if (legacyStateInternal.getBoolean(legacyStateInternal.stateFlag, index)) 1.toByte() else 0.toByte()
    }

    fun setStateFlag(index: Int, value: Boolean) {
        legacyStateInternal.setBoolean(legacyStateInternal.stateFlag, index, value)
    }

    fun setStateFlagI(index: Int, value: Int) {
        legacyStateInternal.setBoolean(legacyStateInternal.stateFlag, index, value > 0)
    }

    var isStateMarried: Boolean
        get() = getStateFlag(STATE_FLAG_MARRIED)
        set(value) {
            setStateFlag(STATE_FLAG_MARRIED, value)
            if (value) {
                this.marriageCountReleased = false
            }
        }

    var isStateNoEquip: Boolean
        get() = getStateFlag(STATE_FLAG_NO_EQUIP)
        set(value) {
            setStateFlag(STATE_FLAG_NO_EQUIP, value)
        }

    var isStateCanMelee: Boolean
        get() = getStateFlag(STATE_FLAG_CAN_MELEE)
        set(value) {
            setStateFlag(STATE_FLAG_CAN_MELEE, value)
        }

    var isStateLightAttack: Boolean
        get() = getStateFlag(STATE_FLAG_LIGHT_ATTACK)
        set(value) {
            setStateFlag(STATE_FLAG_LIGHT_ATTACK, value)
        }

    var isStateHeavyAttack: Boolean
        get() = getStateFlag(STATE_FLAG_HEAVY_ATTACK)
        set(value) {
            setStateFlag(STATE_FLAG_HEAVY_ATTACK, value)
        }

    var isStateLightAircraftAttack: Boolean
        get() = getStateFlag(STATE_FLAG_LIGHT_AIRCRAFT_ATTACK)
        set(value) {
            setStateFlag(STATE_FLAG_LIGHT_AIRCRAFT_ATTACK, value)
        }

    var isStateHeavyAircraftAttack: Boolean
        get() = getStateFlag(STATE_FLAG_HEAVY_AIRCRAFT_ATTACK)
        set(value) {
            setStateFlag(STATE_FLAG_HEAVY_AIRCRAFT_ATTACK, value)
        }

    var isStateRingEffect: Boolean
        get() = getStateFlag(STATE_FLAG_RING_EFFECT)
        set(value) {
            setStateFlag(STATE_FLAG_RING_EFFECT, value)
        }

    var isStateGuiBtn1: Boolean
        get() {
            if (this is EntityTransportWa) return false
            if (this is EntityCarrierAkagi) return false
            if (this is EntityCarrierKaga) return false
            if (this is EntityCarrierWo) return false
            if (this is EntityCarrierHime) return false
            return true
        }
        set(value) {
            setStateFlag(STATE_FLAG_GUI_BTN_1, value)
        }

    var isStateGuiBtn2: Boolean
        get() {
            if (this is EntityTransportWa) return false
            if (this.supportsAircraftCombat()) return false
            return true
        }
        set(value) {
            setStateFlag(STATE_FLAG_GUI_BTN_2, value)
        }

    var isStateGuiBtn3: Boolean
        get() = this.supportsAircraftCombat()
        set(value) {
            setStateFlag(STATE_FLAG_GUI_BTN_3, value)
        }

    var isStateGuiBtn4: Boolean
        get() = this.supportsAircraftCombat()
        set(value) {
            setStateFlag(STATE_FLAG_GUI_BTN_4, value)
        }

    var isStateAntiAir: Boolean
        get() = getStateFlag(STATE_FLAG_ANTI_AIR)
        set(value) {
            setStateFlag(STATE_FLAG_ANTI_AIR, value)
        }

    var isStateCanRide: Boolean
        get() = getStateFlag(STATE_FLAG_CAN_RIDE)
        set(value) {
            setStateFlag(STATE_FLAG_CAN_RIDE, value)
        }

    var isStateAppearance: Boolean
        get() = getStateFlag(STATE_FLAG_APPEARANCE)
        set(value) {
            setStateFlag(STATE_FLAG_APPEARANCE, value)
        }

    fun canShowHeldItem(): Boolean {
        return this.isStateAppearance && this.attackTick <= 0 && this.attackTick2 <= 0
    }

    val heldItemMainhandSlot: ItemStack
        get() {
            if (this.inventory == null || HELD_MAINHAND_SLOT >= this.inventory.getSlots()) {
                return ItemStack.EMPTY
            }
            return this.inventory.getStackInSlot(HELD_MAINHAND_SLOT)
        }

    val heldItemOffhandSlot: ItemStack
        get() {
            if (this.inventory == null || HELD_OFFHAND_SLOT >= this.inventory.getSlots()) {
                return ItemStack.EMPTY
            }
            return this.inventory.getStackInSlot(HELD_OFFHAND_SLOT)
        }

    override fun getItemBySlot(slot: EquipmentSlot): ItemStack {
        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
            if (!canShowHeldItem()) {
                return ItemStack.EMPTY
            }
            if (this.level().isClientSide) {
                return super.getItemBySlot(slot)
            }
            return if (slot == EquipmentSlot.MAINHAND) this.heldItemMainhandSlot else this.heldItemOffhandSlot
        }
        return super.getItemBySlot(slot)
    }

    open val isSubmarine: Boolean
        get() = false

    override fun isCurrentlyGlowing(): Boolean {
        if (this.level().isClientSide && this.isSubmarine && this.isStateRingEffect) {
            return this.isLocalPlayerOwner
        }
        return super.isCurrentlyGlowing()
    }

    private val isLocalPlayerOwner: Boolean
        get() = ClientProxy.isLocalPlayerOwner(this)

    fun getUpdateFlag(index: Int): Boolean {
        return legacyStateInternal.getBoolean(legacyStateInternal.updateFlag, index)
    }

    fun setUpdateFlag(index: Int, value: Boolean) {
        legacyStateInternal.setBoolean(legacyStateInternal.updateFlag, index, value)
    }

    val bodyHeightStand: ByteArray?
        get() = legacyStateInternal.bodyHeightStand

    val bodyHeightSit: ByteArray?
        get() = legacyStateInternal.bodyHeightSit

    var modelPos: FloatArray?
        get() = legacyStateInternal.modelPos
        set(pos) {
            legacyStateInternal.applyModelPos(pos)
            this.refreshDimensions()
        }

    var waypoints: Array<BlockPos?>?
        get() = legacyStateInternal.waypoints?.copyOf()
        set(points) {
            legacyStateInternal.applyWaypoints(points)
        }

    var ridingState: Int
        get() = this.entityData.get<Int?>(LEGACY_RIDING_STATE)
        set(state) {
            this.entityData.set<Int?>(LEGACY_RIDING_STATE, max(0, state))
        }

    fun getGuardedPos(index: Int): Int {
        return when (index) {
            0 -> this.getStateMinor(ShipContainerMenu.STATE_MINOR_GUARD_X)
            1 -> this.getStateMinor(ShipContainerMenu.STATE_MINOR_GUARD_Y)
            2 -> this.getStateMinor(ShipContainerMenu.STATE_MINOR_GUARD_Z)
            3 -> this.getStateMinor(ShipContainerMenu.STATE_MINOR_GUARD_DIM)
            4 -> this.getStateMinor(ShipContainerMenu.STATE_MINOR_GUARD_TYPE)
            else -> 0
        }
    }

    var scaleLevel: Int
        get() = this.entityData.get<Int?>(LEGACY_SCALE_LEVEL)
        set(level) {
            this.entityData.set<Int?>(LEGACY_SCALE_LEVEL, max(0, level))
            val scaleAttr =
                this.getAttribute(Attributes.SCALE)
            if (scaleAttr != null) {
                val scaleFactor = Mth.clamp(1.0f + level * 0.5f, 1.0f, 2.5f)
                scaleAttr.setBaseValue(scaleFactor.toDouble())
            }
            this.refreshDimensions()
        }

    override fun getPickRadius(): Float {
        val modelPos = this.modelPos
        val visualSize = if (modelPos != null && modelPos.size > 3) modelPos[3] else 50.0f
        val radius: Float = Mth.clamp(visualSize * PICK_RADIUS_MODEL_SCALE, PICK_RADIUS_MIN, PICK_RADIUS_MAX)
        val scaleFactor = Mth.clamp(1.0f + this.scaleLevel * 0.5f, 1.0f, 2.5f)
        return Mth.clamp(radius * scaleFactor, PICK_RADIUS_MIN, PICK_RADIUS_MAX)
    }

    open fun supportsItemPickup(): Boolean {
        return false
    }

    override fun getBreedOffspring(level: ServerLevel, otherParent: AgeableMob): AgeableMob? {
        return null
    }

    override fun isFood(stack: ItemStack): Boolean {
        return false
    }


    init {
        this.inventory = ShipInventoryHandler(this, 60)
        this.combat = EntityShipBaseCombat(this)
        this.pointer = EntityShipBasePointer(this)
        this.emotions = EntityShipBaseEmotions(this)
        this.faceExpressions = EntityShipBaseFaceExpressions(this, this.emotions)
        this.reactions = EntityShipBaseReactions(this)
        this.passiveCombat = EntityShipBasePassiveCombat(this)
        this.serialization = EntityShipBaseSerialization(this)
        this.legacyShipStats = LegacyShipStats()
        this.legacyStateInternal = EntityShipLegacyState()
        this.taskRuntime = ShipTaskRuntime(this)
        this.lifecycleMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.Companion.PRIORITY_EMERGENCY)
        this.retreatMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.Companion.PRIORITY_EMERGENCY)
        this.pickupMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.Companion.PRIORITY_BACKGROUND)
        this.guardMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.Companion.PRIORITY_COMMAND)
        this.pointerMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.Companion.PRIORITY_COMMAND)
        this.followOwnerMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.Companion.PRIORITY_FOLLOW)
        this.combatMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.Companion.PRIORITY_COMBAT)
        this.idleMovement = ShipMovementCoordinator(this, ShipMovementCoordinator.Companion.PRIORITY_BACKGROUND)
        this.moveControl = ShipMoveControl(this, 30.0f)
        this.setPathfindingMalus(PathType.WATER, 0.0f)
        this.setPathfindingMalus(PathType.LAVA, 0.0f)
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0f)
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0f)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeDD)
    }

    override fun getSoundVolume(): Float {
        return max(0.0f, Config.volumeShip)
    }

    protected val shipSoundPitch: Float
        get() = this.getRandom().nextFloat() * 0.12f + 0.98f

    private fun tryAcquireAmbientSoundSlot(): Boolean {
        val gameTime = this.level().getGameTime()
        val count: Int = AMBIENT_SOUNDS_PER_TICK.merge(gameTime, 1) { a: Int?, b: Int? ->
            java.lang.Integer.sum(
                a ?: 0,
                b ?: 0
            )
        }!!

        if (count == 1) {
            AMBIENT_SOUNDS_PER_TICK.keys.removeIf { tick: Long? -> tick!! < gameTime - 1L }
        }

        if (count > AMBIENT_SOUND_MAX_PER_TICK) {
            AMBIENT_SOUNDS_PER_TICK.computeIfPresent(gameTime) { tick: Long?, value: Int? -> max(0, value!! - 1) }
            return false
        }

        return true
    }

    override fun getAmbientSound(): SoundEvent? {
        return getShipSound(
            Config.ShipCustomSoundType.IDLE,
            this.getStateMinor(STATE_MINOR_SHIP_CLASS),
            this.getRandom()
        )
    }

    override fun getHurtSound(source: DamageSource): SoundEvent? {
        return getShipSound(
            Config.ShipCustomSoundType.HURT,
            this.getStateMinor(STATE_MINOR_SHIP_CLASS),
            this.getRandom()
        )
    }

    override fun getDeathSound(): SoundEvent? {
        return getShipSound(
            Config.ShipCustomSoundType.DEAD,
            this.getStateMinor(STATE_MINOR_SHIP_CLASS),
            this.getRandom()
        )
    }

    override fun playAmbientSound() {
        if ((this.tickCount % AMBIENT_SOUND_MIN_INTERVAL_TICKS) != 0) {
            return
        }
        if (this.isNoFuel || this.getRandom().nextInt(10) > 3) {
            return
        }
        if (!tryAcquireAmbientSoundSlot()) {
            return
        }
        val sound: SoundEvent?
        if (this.getStateFlag(1) && this.getRandom().nextInt(5) == 0) {
            sound = getShipSound(
                Config.ShipCustomSoundType.MARRY,
                this.getStateMinor(STATE_MINOR_SHIP_CLASS),
                this.getRandom()
            )
        } else {
            sound = this.getAmbientSound()
        }
        if (sound != null) {
            this.playSound(sound, this.getSoundVolume(), this.shipSoundPitch)
        }
    }

    override fun playHurtSound(source: DamageSource) {
        if (this.hurtSoundCooldown <= 0) {
            this.hurtSoundCooldown = 20 + this.getRandom().nextInt(30)
            super.playHurtSound(source)
        }
    }

    override fun displayFireAnimation(): Boolean {
        return (this.getHealth() / this.getMaxHealth()) <= 0.25f
    }

    var fuel: Int
        get() {
            if (this.isCreativeDebuggerActive) {
                return MAX_FUEL
            }
            return this.entityData.get<Int?>(FUEL)
        }
        set(value) {
            var value: Int = value
            if (this.isCreativeDebuggerActive) {
                value = MAX_FUEL
            }
            val newFuel = max(0, min(MAX_FUEL, value))
            val wasNoFuel = this.isNoFuel
            this.entityData.set<Int?>(FUEL, newFuel)
            this.legacyStateInternal.stateMinor[6] = newFuel
            this.entityData.set<Boolean?>(NO_FUEL, newFuel == 0)
            val isNoFuelNow = newFuel == 0
            if (wasNoFuel != isNoFuelNow) {
                this.updateFuelState(isNoFuelNow)
            }
        }

    fun getEquipFlag(key: String): Boolean {
        if (EQUIP_MOUNT == key) {
            return (this.entityData.get<Int?>(LEGACY_EMOTION_0) and 1) != 0
        }
        return this.entityData.get<CompoundTag?>(EQUIP_FLAGS).getBoolean(key)
    }

    fun setEquipFlag(key: String, value: Boolean) {
        if (EQUIP_MOUNT == key) {
            val current = this.entityData.get<Int?>(LEGACY_EMOTION_0)
            this.setStateEmotion(0, if (value) (current or 1) else (current and 1.inv()), true)
            return
        }
        val tag = this.entityData.get<CompoundTag?>(EQUIP_FLAGS).copy()
        tag.putBoolean(key, value)
        this.entityData.set<CompoundTag?>(EQUIP_FLAGS, tag)
    }

    fun copyEquipFlagsTag(): CompoundTag {
        return this.entityData.get<CompoundTag?>(EQUIP_FLAGS).copy()
    }

    fun setEquipFlagsTag(flags: CompoundTag) {
        this.entityData.set<CompoundTag?>(EQUIP_FLAGS, flags)
    }

    open val equipOptions: MutableList<EquipOption>
        get() {
            val list: MutableList<EquipOption> = ArrayList<EquipOption>()
            if (this.hasShipMounts()) {
                list.add(EquipOption(EQUIP_MOUNT, "gui.shincolle.equip.mount"))
            }
            return list
        }

    protected abstract val shipSpawnEggItem: Item?

    val accessibleInventorySlotCount: Int
        get() = this.inventory!!.accessibleSlotCount

    val isHostileShipMob: Boolean
        get() = !this.isTame && this.ownerUUID == null

    fun initializeHostileSpawnState(scaleLevel: Int) {
        val clampedScale = Mth.clamp(scaleLevel, 0, 3)

        adjustOwnerMarriageCount(-1)
        this.setTame(false, false)
        this.setOwnerUUID(null)
        this.setOrderedToSit(false)
        this.setInSittingPose(false)
        this.scaleLevel = clampedScale
        this.level = when (clampedScale) {
            0 -> 75
            1 -> 100
            2 -> 125
            else -> 150
        }

        this.fuel = 100
        this.isStateCanMelee = true
        this.isStateLightAttack = true
        this.isStateHeavyAttack = true
        this.isStateLightAircraftAttack = true
        this.isStateHeavyAircraftAttack = true
        this.isStateAntiAir = true
        this.setStateFlag(ShipContainerMenu.STATE_FLAG_ANTI_SUB, true)
        this.setStateFlag(ShipContainerMenu.STATE_FLAG_PVP, true)
        this.setStateFlag(ShipContainerMenu.STATE_FLAG_PASSIVE_ATTACK, true)
        this.setStateFlag(ShipContainerMenu.STATE_FLAG_ON_SIGHT, false)
        this.setStateFlag(ShipContainerMenu.STATE_FLAG_PICK_ITEM, false)
        this.setStateFlag(ShipContainerMenu.STATE_FLAG_AUTO_PUMP, false)
        this.setStateMinor(ShipContainerMenu.STATE_MINOR_FLEE_HP, 0)
        this.randomizeEquipFlags()

        fillHostileAmmoLoadout()
        this.recalculateLegacyShipStats()
        this.setHealth(this.getMaxHealth())
        this.shipDeathTicks = 0
        this.hostileCanDrop = true
    }

    fun randomizeEquipFlags() {
        this.entityData.set<Int?>(LEGACY_EMOTION_0, this.getRandom().nextInt(128) and 1.inv())
        val options = this.equipOptions
        if (!options.isEmpty()) {
            val tag = this.entityData.get<CompoundTag?>(EQUIP_FLAGS).copy()
            if (tag.isEmpty()) {
                for (option in options) {
                    if (option.key == EQUIP_MOUNT) {
                        continue
                    }
                    tag.putBoolean(option.key, this.getRandom().nextBoolean())
                }
                this.entityData.set<CompoundTag?>(EQUIP_FLAGS, tag)
            }
        }
    }

    private fun fillHostileAmmoLoadout() {
        val slots = this.accessibleInventorySlotCount
        for (i in 0..<slots) {
            this.inventory!!.setStackInSlot(i, ItemStack.EMPTY)
        }

        if (slots > 0) {
            this.inventory!!.setStackInSlot(
                0,
                ItemStack(ModItems.AMMO_LIGHT_CONTAINER.get(), HOSTILE_LIGHT_AMMO_CONTAINER_COUNT)
            )
        }
        if (slots > 1) {
            this.inventory!!.setStackInSlot(
                1,
                ItemStack(ModItems.AMMO_HEAVY_CONTAINER.get(), HOSTILE_HEAVY_AMMO_CONTAINER_COUNT)
            )
        }
        this.onInventoryChanged()
    }

    fun returnAircraftToDeck(lightAircraft: Boolean) {
        this.combat.returnAircraftToDeck(lightAircraft)
    }

    fun savePointerToNbt(compound: CompoundTag?) {
        if (compound != null) {
            this.pointer.saveToNbt(compound)
        }
    }

    fun loadPointerFromNbt(compound: CompoundTag) {
        this.pointer.loadFromNbt(compound)
    }

    fun getAttrBonus(index: Int): Int {
        return this.legacyShipStats.getBonus(index)
    }

    fun setAttrBonus(index: Int, value: Int) {
        this.legacyShipStats.setBonus(index, value)
        this.syncLegacyBonusData()
        this.recalculateLegacyShipStats()
    }

    fun createLegacyBonusTag(): CompoundTag {
        val legacyBonus = CompoundTag()
        legacyBonus.putByte("HP", this.legacyShipStats.getBonus(0).toByte())
        legacyBonus.putByte("ATK", this.legacyShipStats.getBonus(1).toByte())
        legacyBonus.putByte("DEF", this.legacyShipStats.getBonus(2).toByte())
        legacyBonus.putByte("SPD", this.legacyShipStats.getBonus(3).toByte())
        legacyBonus.putByte("MOV", this.legacyShipStats.getBonus(4).toByte())
        legacyBonus.putByte("HIT", this.legacyShipStats.getBonus(5).toByte())
        return legacyBonus
    }

    fun applyLegacyBonusTag(legacyBonus: CompoundTag?) {
        if (legacyBonus == null) {
            return
        }
        this.legacyShipStats.setBonus(0, legacyBonus.getByte("HP").toInt())
        this.legacyShipStats.setBonus(1, legacyBonus.getByte("ATK").toInt())
        this.legacyShipStats.setBonus(2, legacyBonus.getByte("DEF").toInt())
        this.legacyShipStats.setBonus(3, legacyBonus.getByte("SPD").toInt())
        this.legacyShipStats.setBonus(4, legacyBonus.getByte("MOV").toInt())
        this.legacyShipStats.setBonus(5, legacyBonus.getByte("HIT").toInt())
    }

    fun syncLegacyBonusData() {
        if (this.level() != null && !this.level().isClientSide) {
            this.entityData.set<CompoundTag?>(LEGACY_BONUS_DATA, this.createLegacyBonusTag())
        }
    }

    fun resetInteractionEmotionState() {
        this.emotions.resetFaceTick()
        if (this.emotionPrimary == EMOTION_BORED) {
            this.emotionPrimary = EMOTION_NORMAL
        }
        if (this.emotionSecondary == EMOTION_BORED) {
            this.emotionSecondary = EMOTION_NORMAL
        }
    }

    fun focusOnPlayer(player: Player?) {
        if (player == null) {
            return
        }
        this.getLookControl().setLookAt(player, 30.0f, 30.0f)
    }

    fun getHeadTiltAngle(ageInTicks: Float): Float {
        return this.emotions.getHeadTiltAngle(ageInTicks)
    }

    override fun decreaseAirSupply(air: Int): Int {
        return air
    }

    override fun tick() {
        if (!this.level().isClientSide && ModCommands.isStopShipAi) {
            this.setDeltaMovement(Vec3.ZERO)
            return
        }
        super.tick()

        if (this.isCustomSwinging) {
            this.customSwingTicks++
            if (this.customSwingTicks >= MAX_SWING_TICKS) {
                this.isCustomSwinging = false
                this.customSwingTicks = 0
            }
        }
    }

    override fun aiStep() {
        super.aiStep()

        if (this.isOnFire()) {
            this.clearFire()
        }

        if (this.isAlive && !this.level().isClientSide) {
            if (enabled()) {
                val start = now()
                try {
                    this.tickAliveLogic()
                } finally {
                    val elapsed = elapsed(start)
                    addShipTime(elapsed)
                    val known = (this.perfShipCoreNanos + this.perfShipTaskNanos
                            + this.perfShipSupportNanos + this.perfShipPeriodicNanos)
                    val detail = ("extraMs=" + formatMs(max(0L, elapsed - known))
                            + " taskId=" + this.getStateMinor(ShipContainerMenu.STATE_MINOR_TASK_ID)
                            + " sitting=" + this.isSitting
                            + " noFuel=" + this.isNoFuel
                            + " health=" + this.getHealth() + "/" + this.getMaxHealth()
                            + " fuel=" + this.fuel
                            + " morale=" + this.morale)
                    logSlowShipTick(
                        this, elapsed, this.perfShipCoreNanos,
                        this.perfShipTaskNanos, this.perfShipSupportNanos, this.perfShipPeriodicNanos, detail
                    )
                }
            } else {
                this.tickAliveLogic()
            }
        }
    }

    override fun travel(travelVector: Vec3) {
        super.travel(travelVector)
    }

    override fun getControllingPassenger(): LivingEntity? {
        return null
    }

    override fun removeWhenFarAway(distanceToClosestPlayer: Double): Boolean {
        if (this.isHostileShipMob) {
            return false
        }
        return super.removeWhenFarAway(distanceToClosestPlayer)
    }

    protected fun tickHostileDespawn(): Boolean {
        if (!this.isHostileShipMob) {
            return false
        }
        if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard()
            return true
        }
        val minionDespawn = Config.hostileDespawnMinionTicks
        if (minionDespawn >= 0) {
            val player = this.level().getNearestPlayer(this, -1.0)
            if (player != null) {
                val distSq = player.distanceToSqr(this)
                if (distSq > 16384.0) {
                    this.discard()
                    return true
                } else if (distSq > 1024.0) {
                    if (this.tickCount > minionDespawn && this.getRandom().nextInt(800) == 0) {
                        this.discard()
                        return true
                    }
                }
            } else {
                this.discard()
                return true
            }
        }
        return false
    }

    protected open fun tickAliveLogic() {
        val tracing = enabled()
        if (tracing) {
            this.perfShipCoreNanos = 0L
            this.perfShipTaskNanos = 0L
            this.perfShipSupportNanos = 0L
            this.perfShipPeriodicNanos = 0L
        }

        var segmentStart: Long = startPerfSegment(tracing)
        this.emotions.tickEmotions()

        if (!this.level().isClientSide && tickHostileDespawn()) {
            this.perfShipCoreNanos += finishPerfSegment(tracing, segmentStart)
            return
        }

        this.updateMountSummon()
        applyWaterBuoyancyIfNeeded()

        if (this.isSitting || this.isInDeadPose) {
            this.lifecycleMovement.stopAny()
        }
        this.perfShipCoreNanos += finishPerfSegment(tracing, segmentStart)

        segmentStart = startPerfSegment(tracing)
        if (!this.isNoFuel) {
            val retreatingForLowHealth = shouldRetreatForLowHealth()
            if (retreatingForLowHealth) {
                clearPassiveCombatTargetBrain(true)
                tickRetreatMovement()
            } else {
                this.retreatMovement.stop()
                if (this.hasPointerTargetEntity()) {
                    clearPassiveCombatTargetBrain(true)
                }
            }

            this.combat.tickAircraftRecovery()
            if (retreatingForLowHealth) {
                this.pickupMovement.stop()
            } else {
                tickAutoPickupItems()
            }
            tickAutoPump()
            tickAutoRation()
            this.reactions.tickEmotes()
            if ((this.tickCount and 0xFF) == 0) {
                applyEmotesReaction(4)
            }
        } else {
            this.retreatMovement.stop()
            this.pickupMovement.stop()
            clearPassiveCombatTargetBrain(true)
        }
        this.perfShipSupportNanos += finishPerfSegment(tracing, segmentStart)

        if (this.isAlive && (this.tickCount and 7) == 0) {
            segmentStart = startPerfSegment(tracing)
            onUpdateTask(this)
            this.perfShipTaskNanos += finishPerfSegment(tracing, segmentStart)
        }

        segmentStart = startPerfSegment(tracing)
        tickSearchlightAssist()
        tickCompassChunkLoading()
        tickTimeKeepingSound()

        tickFuelDecay()
        tickAutoRecovery()
        tickAutoSupplies()
        tickLegacyTimers()
        if ((this.tickCount % 20) == 0) {
            syncFormationMembershipFromOwnerData()
        }
        if ((this.tickCount % 16) == 0) {
            tickWaypointMove()
        }
        if ((this.tickCount % 40) == 0) {
            this.recalculateLegacyShipStats()
        }
        if ((this.tickCount % 40) == 0 && this.level() is ServerLevel) {
            val serverLevel = this.level() as ServerLevel
            get(serverLevel).updateShip(this)
        }
        this.perfShipPeriodicNanos += finishPerfSegment(tracing, segmentStart)
    }

    private fun applyWaterBuoyancyIfNeeded() {
        if (!this.isInWater() || this.isPassenger() || this.isSubmarine) {
            return
        }

        if (!this.lifecycleMovement.isNavigationDone && !this.isVehicle()) {
            return
        }

        val depth = this.getFluidHeight(FluidTags.WATER)
        if (depth <= SHIP_BUOY_MIN_DEPTH) {
            return
        }

        val upward: Double = SHIP_BUOY_COEFF * depth.pow(SHIP_BUOY_EXPONENT) - SHIP_BUOY_OFFSET
        val motion = this.getDeltaMovement()
        var newY: Double = (motion.y + upward) * SHIP_BUOY_DAMP
        newY = Mth.clamp(newY, -SHIP_BUOY_MAX_MOTION, SHIP_BUOY_MAX_MOTION)
        this.setDeltaMovement(motion.x, newY, motion.z)
    }

    override fun tickDeath() {
        this.updateMountSummon()
        this.emotionPrimary = EMOTION_HUNGRY
        this.setFaceHungry()
        this.shipDeathTicks++
        if (this.isInWaterOrBubble() || this.isInLava()) {
            this.applyDeadFloatStabilization()
        }
        if (!this.level().isClientSide && this.shipDeathTicks == SHIP_DEATH_MAX_TICKS && this.hostileCanDrop) {
            this.hostileCanDrop = false
            spawnShipGrudge()
        }
        if (this.shipDeathTicks >= SHIP_DEATH_MAX_TICKS) {
            this.discard()
        }
        this.deathTime = 0
    }

    private fun applyDeadFloatStabilization() {
        val motion = this.getDeltaMovement()
        val motionY = Mth.clamp(motion.y * 0.55 + computeDeadFluidSurfaceCorrection(0.08), -0.05, 0.05)
        var motionX = motion.x * 0.3
        var motionZ = motion.z * 0.3

        if (abs(motionX) < DEAD_FLOAT_STOP_EPSILON) {
            motionX = 0.0
        }
        if (abs(motionZ) < DEAD_FLOAT_STOP_EPSILON) {
            motionZ = 0.0
        }

        this.setDeltaMovement(motionX, motionY, motionZ)
    }

    private fun computeDeadFluidSurfaceCorrection(strength: Double): Double {
        val surfaceY = this.deadFluidSurfaceY
        if (java.lang.Double.isNaN(surfaceY)) {
            return 0.0
        }

        val targetY: Double = surfaceY - DEAD_FLOAT_HOVER_OFFSET
        return Mth.clamp((targetY - this.getY()) * strength, -0.03, 0.03)
    }

    private val deadFluidSurfaceY: Double
        get() {
            val level = this.level()
            var pos = BlockPos.containing(this.getX(), this.getY(), this.getZ())
            var fluid = level.getFluidState(pos)

            if (fluid.isEmpty()) {
                val below = pos.below()
                fluid = level.getFluidState(below)

                if (fluid.isEmpty()) {
                    return Double.NaN
                }

                pos = below
            }

            return (pos.getY() + fluid.getHeight(level, pos)).toDouble()
        }

    private fun syncFormationMembershipFromOwnerData() {
        val ownerRaw = this.getOwner()
        if (ownerRaw !is ServerPlayer) {
            return
        }

        val data = admiralData(ownerRaw)
        val actualTeam = data.findShipTeam(this.getUUID())
        if (actualTeam >= 0) {
            val actualSlot = data.findShipSlot(actualTeam, this.getUUID())
            if (this.formationTeam != actualTeam) {
                this.formationTeam = actualTeam
            }
            if (this.formationSlot != actualSlot) {
                this.formationSlot = actualSlot
            }
            if (actualTeam == data.getCurrentTeamID()) {
                val selected = data.isSelected(actualTeam, actualSlot)
                if (this.isPointerSelected != selected) {
                    this.isPointerSelected = selected
                }
            } else if (this.isPointerSelected) {
                this.isPointerSelected = false
            }
            return
        }

        if (this.formationTeam != -1) {
            this.formationTeam = -1
        }
        if (this.formationSlot != -1) {
            this.formationSlot = -1
        }
        if (this.isPointerSelected) {
            this.isPointerSelected = false
        }
    }

    override fun remove(reason: RemovalReason) {
        if (this.level() is ServerLevel) {
            val serverLevel = this.level() as ServerLevel
            val registry = get(serverLevel)
            if (shouldMarkRemovedInRegistry(reason)) {
                registry.markRemoved(this)
            } else {
                registry.updateShip(this)
            }
        }
        if (!this.level().isClientSide && shouldReleaseMarriageCountOnRemove(reason)) {
            adjustOwnerMarriageCount(-1)
        }
        if (this.level() is ServerLevel) {
            val serverLevel = this.level() as ServerLevel
            clearCompassForcedChunks(serverLevel)
        }
        super.remove(reason)
    }

    private fun shouldMarkRemovedInRegistry(reason: RemovalReason?): Boolean {
        return reason == RemovalReason.KILLED || reason == RemovalReason.DISCARDED
    }

    private fun shouldReleaseMarriageCountOnRemove(reason: RemovalReason?): Boolean {
        return reason != RemovalReason.CHANGED_DIMENSION && reason != RemovalReason.UNLOADED_TO_CHUNK && reason != RemovalReason.UNLOADED_WITH_PLAYER
    }

    private fun adjustOwnerMarriageCount(delta: Int) {
        if (delta == 0 || !this.isStateMarried) {
            return
        }

        val ownerId = this.ownerUUID
        if (ownerId == null || this.level() !is ServerLevel) {
            return
        }
        val serverLevel = this.level() as ServerLevel

        if (delta < 0 && this.marriageCountReleased) {
            return
        }

        val owner: ServerPlayer? = serverLevel.getServer().getPlayerList().getPlayer(ownerId)
        if (owner != null) {
            adjustOwnedMarriedShipCount(owner, delta)
        }

        if (delta < 0) {
            this.marriageCountReleased = true
        }
    }

    private fun spawnShipGrudge() {
        val spawnEgg = createShipSpawnEggStack()
        val grudge = EntityShipGrudge(
            this.level(), this.getX(), this.getY() + 0.5,
            this.getZ(), spawnEgg, this.ownerUUID
        )
        this.level().addFreshEntity(grudge)
    }

    private fun createShipSpawnEggStack(): ItemStack {
        val egg = ItemStack(this.shipSpawnEggItem)
        val shipTag = CompoundTag()
        this.addAdditionalSaveData(shipTag)
        shipTag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()).toString())
        shipTag.putBoolean(spawnEggTagName, true)
        if (this.isHostileShipMob) {
            shipTag.putBoolean(TAG_SPAWN_EGG_NO_EXP, true)
        }
        shipTag.putFloat("Health", this.getMaxHealth())
        shipTag.putShort("DeathTime", 0.toShort())
        shipTag.putShort("HurtTime", 0.toShort())
        egg.set<CustomData?>(DataComponents.ENTITY_DATA, CustomData.of(shipTag))
        egg.set<Boolean?>(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
        return egg
    }

    private fun tickFuelDecay() {
        if (this.isHostileShipMob) {
            return
        }
        if (this.tickCount % Config.fuelDecayInterval != 0) {
            return
        }
        if (this.fuel <= 0) {
            return
        }

        var consume = this.getStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION)

        val dist = sqrt(this.distanceToSqr(this.xo, this.yo, this.zo))
        consume += (dist * Config.fuelMoveDecayFactor).toInt()

        this.fuel = this.fuel - consume
    }

    private fun tickAutoRecovery() {
        if (this.isHostileShipMob) {
            return
        }

        if ((this.tickCount and 0x1F) == 0
            && this.getHealth() < this.getMaxHealth() * AUTO_HEAL_THRESHOLD_RATIO
        ) {
            if (this.consumeItemInInventory(ModItems.BUCKET_REPAIR.get())) {
                this.recordCreativeDebuggerBucketRepair()
                this.heal(this.getMaxHealth() * AUTO_HEAL_FAST_RATIO + AUTO_HEAL_FAST_FLAT)
                if (this.supportsAircraftCombat()) {
                    this.numAircraftLight = this.numAircraftLight + 1
                    this.numAircraftHeavy = this.numAircraftHeavy + 1
                }
                this.applyParticleEmotion(EmotionParticleType.HEART)
            }
        }

        if ((this.tickCount and 0xFF) == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(this.getMaxHealth() * AUTO_HEAL_SLOW_RATIO + AUTO_HEAL_SLOW_FLAT)
        }
    }

    private fun shouldRetreatForLowHealth(): Boolean {
        val fleeHp = Mth.clamp(this.getStateMinor(ShipContainerMenu.STATE_MINOR_FLEE_HP), 0, 100)
        if (fleeHp <= 0) {
            return false
        }
        return this.getHealth() <= this.getMaxHealth() * (fleeHp / 100.0f)
    }

    private fun tickRetreatMovement() {
        val owner = this.getOwner()
        if (owner == null) {
            this.retreatMovement.stop()
            return
        }

        val distanceSqr = this.distanceToSqr(owner)
        if (distanceSqr > 4.0) {
            this.retreatMovement.moveTo(owner, 1.25)
        } else {
            this.retreatMovement.stop()
        }
        this.getLookControl().setLookAt(owner, 30.0f, 30.0f)
    }

    private fun tickTimeKeepingSound() {
        if (!Config.canTimeKeeping || !this.getStateFlag(ShipContainerMenu.STATE_FLAG_TIMEKEEP) || !this.isAlive || this.isInDeadPose) {
            return
        }
        val worldTime = this.level().getDayTime()
        if (worldTime % TIMEKEEP_INTERVAL_TICKS != 0L) {
            return
        }

        val hour = ((worldTime / TIMEKEEP_INTERVAL_TICKS) % 24L).toInt()
        val timeSound = getShipSound(
            timeKeeping(hour),
            this.getStateMinor(STATE_MINOR_SHIP_CLASS),
            this.getRandom()
        )
        if (timeSound != null) {
            this.playSound(timeSound, max(0.0f, Config.volumeTimeKeeping), 1.0f)
        }
    }

    protected fun tryFlareTarget(target: Entity?) {
        if (target == null || this.getStateMinor(STATE_MINOR_EQUIP_FLARE) <= 0) {
            return
        }
        if (this.level() !is ServerLevel) {
            return
        }
        val serverLevel = this.level() as ServerLevel

        val posX = target.getX()
        val posY = target.getY() + target.getBbHeight() * 0.5
        val posZ = target.getZ()
        serverLevel.sendParticles<SimpleParticleType?>(
            ParticleTypes.FIREWORK,
            posX, posY, posZ,
            12, 0.5, 0.6, 0.5, 0.05
        )

        if (target is LivingEntity) {
            target.addEffect(
                MobEffectInstance(
                    MobEffects.GLOWING,
                    SPECIAL_EQUIP_FLARE_GLOW_TICKS, 0, false, true, true
                ), this
            )
        }
    }

    private fun tickSearchlightAssist() {
        if ((this.tickCount % SEARCHLIGHT_INTERVAL_TICKS) != 0) {
            return
        }
        if (this.getStateMinor(STATE_MINOR_EQUIP_SEARCHLIGHT) <= 0 || !this.isAlive) {
            return
        }
        if (this.level() !is ServerLevel) {
            return
        }
        val serverLevel = this.level() as ServerLevel
        if (this.level().getMaxLocalRawBrightness(this.blockPosition()) >= 10) {
            return
        }

        this.addEffect(
            MobEffectInstance(
                MobEffects.NIGHT_VISION,
                SPECIAL_EQUIP_SEARCHLIGHT_NIGHT_VISION_TICKS, 0, true, false, false
            )
        )
        serverLevel.sendParticles<SimpleParticleType?>(
            ParticleTypes.END_ROD,
            this.getX(), this.getY() + 1.2, this.getZ(),
            3, 0.25, 0.35, 0.25, 0.01
        )
    }

    private fun tickCompassChunkLoading() {
        if (this.level() !is ServerLevel) {
            return
        }
        val serverLevel = this.level() as ServerLevel

        if (!this.isAlive || this.getStateMinor(STATE_MINOR_EQUIP_COMPASS) <= 0) {
            clearCompassForcedChunks(serverLevel)
            return
        }

        val chunkPos = this.chunkPosition()
        val movedChunk = chunkPos.x != this.forcedCompassChunkCenterX || chunkPos.z != this.forcedCompassChunkCenterZ
        if (!movedChunk && (this.tickCount % COMPASS_CHUNK_REFRESH_INTERVAL_TICKS) != 0) {
            return
        }

        this.forcedCompassChunkCenterX = chunkPos.x
        this.forcedCompassChunkCenterZ = chunkPos.z
        updateCompassForcedChunks(serverLevel, chunkPos.x, chunkPos.z)
    }

    private fun updateCompassForcedChunks(serverLevel: ServerLevel, centerX: Int, centerZ: Int) {
        val desired: MutableSet<Long?> = HashSet<Long?>()
        for (dx in -COMPASS_CHUNK_RADIUS..COMPASS_CHUNK_RADIUS) {
            for (dz in -COMPASS_CHUNK_RADIUS..COMPASS_CHUNK_RADIUS) {
                val cx = centerX + dx
                val cz = centerZ + dz
                val key = ChunkPos.asLong(cx, cz)
                desired.add(key)
                if (!this.forcedCompassChunks.contains(key)) {
                    serverLevel.setChunkForced(cx, cz, true)
                }
            }
        }

        if (!this.forcedCompassChunks.isEmpty()) {
            for (key in HashSet<Long?>(this.forcedCompassChunks)) {
                if (desired.contains(key)) {
                    continue
                }
                serverLevel.setChunkForced(ChunkPos.getX(key!!), ChunkPos.getZ(key), false)
            }
        }

        this.forcedCompassChunks.clear()
        this.forcedCompassChunks.addAll(desired)
    }

    private fun clearCompassForcedChunks(serverLevel: ServerLevel) {
        if (this.forcedCompassChunks.isEmpty()) {
            this.forcedCompassChunkCenterX = Int.MIN_VALUE
            this.forcedCompassChunkCenterZ = Int.MIN_VALUE
            return
        }

        for (key in HashSet<Long?>(this.forcedCompassChunks)) {
            serverLevel.setChunkForced(ChunkPos.getX(key!!), ChunkPos.getZ(key), false)
        }
        this.forcedCompassChunks.clear()
        this.forcedCompassChunkCenterX = Int.MIN_VALUE
        this.forcedCompassChunkCenterZ = Int.MIN_VALUE
    }

    private fun tickAutoPickupItems() {
        if (!supportsItemPickup()) {
            this.pickupMovement.stop()
            return
        }
        if (!this.getStateFlag(ShipContainerMenu.STATE_FLAG_PICK_ITEM)) {
            this.pickupMovement.stop()
            return
        }
        if ((this.tickCount % PICK_ITEM_SCAN_INTERVAL_TICKS) != 0) {
            return
        }
        if (this.isSitting || this.isPassenger() || this.isVehicle() || this.isInDeadPose) {
            this.pickupMovement.stop()
            return
        }
        if (this.hasPointerTarget() || this.hasPointerTargetEntity() || this.getTarget() != null) {
            this.pickupMovement.stop()
            return
        }
        if (!hasCargoRoom()) {
            this.pickupMovement.stop()
            return
        }

        val target = findNearestPickItem()
        if (target == null) {
            this.pickupMovement.stop()
            return
        }

        if (this.distanceToSqr(target) <= 9.0) {
            tryPickupItemEntity(target)
            this.pickupMovement.stop()
        } else {
            this.pickupMovement.moveTo(target, 1.0)
        }
    }

    private fun hasCargoRoom(): Boolean {
        val slotCount = this.accessibleInventorySlotCount
        for (i in ShipInventoryHandler.equipSlotCount..<slotCount) {
            val stack = this.inventory!!.getStackInSlot(i)
            if (stack.isEmpty()) {
                return true
            }
            val limit = min(stack.getMaxStackSize(), this.inventory.getSlotLimit(i))
            if (stack.getCount() < limit) {
                return true
            }
        }
        return false
    }

    private fun findNearestPickItem(): ItemEntity? {
        val followCap = max(2.0, this.getStateMinor(ShipContainerMenu.STATE_MINOR_FOLLOW_MAX).toDouble())
        val statRange = max(2.0, this.legacyShipStats.attackRange * 0.5 + 2.0)
        val pickRange = min(followCap + 2.0, statRange)

        val scanBox = this.getBoundingBox().inflate(pickRange, pickRange * 0.5 + 1.0, pickRange)
        val items = this.level().getEntitiesOfClass<ItemEntity?>(
            ItemEntity::class.java, scanBox,
            Predicate { item: ItemEntity? ->
                item!!.isAlive && !item.getItem().isEmpty() && !item.hasPickUpDelay()
            })
        if (items.isEmpty()) {
            return null
        }

        items.sortWith(Comparator { a: ItemEntity?, b: ItemEntity? ->
            java.lang.Double.compare(
                this.distanceToSqr(a),
                this.distanceToSqr(b)
            )
        })
        return items.get(0)
    }

    private fun tryPickupItemEntity(itemEntity: ItemEntity) {
        val stack = itemEntity.getItem()
        if (stack.isEmpty()) {
            return
        }

        val originalCount = stack.getCount()
        val remaining = insertIntoCargo(stack.copy())
        val inserted = originalCount - remaining.getCount()
        if (inserted <= 0) {
            return
        }

        if (remaining.isEmpty()) {
            itemEntity.discard()
        } else {
            itemEntity.setItem(remaining)
        }

        this.playSound(
            SoundEvents.ITEM_PICKUP, 0.2f,
            ((this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f
        )
    }

    private fun insertIntoCargo(stack: ItemStack): ItemStack {
        var remaining = stack
        val slotCount = this.accessibleInventorySlotCount
        var i: Int = ShipInventoryHandler.equipSlotCount
        while (i < slotCount && !remaining.isEmpty()) {
            remaining = this.inventory!!.insertItem(i, remaining, false)
            i++
        }
        return remaining
    }

    private fun hasLiquidDrumEquip(): Boolean {
        val equipSlots = Math.min(ShipInventoryHandler.equipSlotCount, this.inventory!!.getSlots())
        for (slot in 0..<equipSlots) {
            val stack = this.inventory.getStackInSlot(slot)
            if (stack.isEmpty() || stack.getItem() !is LegacyEquipItem) {
                continue
            }
            if ((stack.getItem() as LegacyEquipItem).getEquipTypeId(stack) == EQUIP_TYPE_DRUM
                && (stack.getItem() as LegacyEquipItem).getVariant(stack) == EQUIP_DRUM_VARIANT_LIQUID
            ) {
                return true
            }
        }
        return false
    }

    private fun tickAutoPump() {
        if (!this.getStateFlag(ShipContainerMenu.STATE_FLAG_AUTO_PUMP)) {
            return
        }
        if (!hasLiquidDrumEquip()) {
            return
        }
        if (this.isSitting || this.isPassenger() || this.isVehicle() || this.isInDeadPose) {
            return
        }

        tickAutoPumpXp()

        if ((this.tickCount % AUTO_PUMP_INTERVAL_TICKS) != 0) {
            return
        }

        val sourcePos = findNearbyPumpSource()
        if (sourcePos == null) {
            return
        }

        val fluidState = this.level().getFluidState(sourcePos)
        if (!fluidState.`is`(Fluids.WATER) && !fluidState.`is`(Fluids.LAVA)) {
            return
        }

        val pumpedFluid = FluidStack(fluidState.getType(), FluidType.BUCKET_VOLUME)

        if (tryStorePumpedFluid(pumpedFluid)) {
            if (this.level() is ServerLevel) {
            val serverLevel = this.level() as ServerLevel
                this.level().setBlockAndUpdate(sourcePos, Blocks.AIR.defaultBlockState())
            }
            val sound = if (fluidState.`is`(Fluids.LAVA)) SoundEvents.BUCKET_FILL_LAVA else SoundEvents.BUCKET_FILL
            this.playSound(sound, 0.5f, this.getRandom().nextFloat() * 0.4f + 0.8f)
        }
    }

    private fun findNearbyPumpSource(): BlockPos? {
        val center = this.blockPosition()
        var bestPos: BlockPos? = null
        var bestDist = Double.MAX_VALUE

        for (dx in -3..3) {
            for (dy in -1..1) {
                for (dz in -3..3) {
                    val pos = center.offset(dx, dy, dz)
                    val fluidState = this.level().getFluidState(pos)
                    if (fluidState.isEmpty() || !fluidState.isSource()) {
                        continue
                    }
                    if (!fluidState.`is`(Fluids.WATER) && !fluidState.`is`(Fluids.LAVA)) {
                        continue
                    }
                    val dist = pos.distToCenterSqr(this.position())
                    if (dist < bestDist) {
                        bestDist = dist
                        bestPos = pos
                    }
                }
            }
        }

        return bestPos
    }

    private fun tryStorePumpedFluid(pumpedFluid: FluidStack): Boolean {
        val slotCount = this.accessibleInventorySlotCount
        for (i in ShipInventoryHandler.equipSlotCount..<slotCount) {
            val stack = this.inventory!!.getStackInSlot(i)
            if (stack.isEmpty()) {
                continue
            }

            val extracted = this.inventory.extractItem(i, 1, false)
            if (extracted.isEmpty()) {
                continue
            }

            val handlerOptional = FluidUtil.getFluidHandler(extracted)
            if (handlerOptional.isEmpty()) {
                val remainder = this.inventory.insertItem(i, extracted, false)
                if (!remainder.isEmpty() && this.level() is ServerLevel) {
                    val serverLevel = this.level() as ServerLevel
                    serverLevel.addFreshEntity(
                        ItemEntity(
                            serverLevel,
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            remainder
                        )
                    )
                }
                continue
            }

            val handler = handlerOptional.get()
            if (handler.fill(pumpedFluid.copy(), IFluidHandler.FluidAction.SIMULATE) < pumpedFluid.getAmount()) {
                val remainder = this.inventory.insertItem(i, extracted, false)
                if (!remainder.isEmpty() && this.level() is ServerLevel) {
                    val serverLevel = this.level() as ServerLevel
                    serverLevel.addFreshEntity(
                        ItemEntity(
                            serverLevel,
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            remainder
                        )
                    )
                }
                continue
            }

            val filled = handler.fill(pumpedFluid.copy(), IFluidHandler.FluidAction.EXECUTE)
            val container = handler.getContainer()
            val remaining = this.inventory.insertItem(i, container, false)
            if (!remaining.isEmpty() && this.level() is ServerLevel) {
                val serverLevel = this.level() as ServerLevel
                serverLevel.addFreshEntity(ItemEntity(serverLevel, this.getX(), this.getY(), this.getZ(), remaining))
            }
            if (filled >= pumpedFluid.getAmount()) {
                return true
            }
        }
        return false
    }

    private fun tickAutoPumpXp() {
        if ((this.tickCount % AUTO_PUMP_XP_INTERVAL_TICKS) != 0) {
            return
        }

        if (this.level() !is ServerLevel) {
            return
        }

        val orbs = this.level().getEntitiesOfClass<ExperienceOrb?>(
            ExperienceOrb::class.java,
            this.getBoundingBox().inflate(7.0)
        )
        if (!orbs.isEmpty()) {
            for (orb in orbs) {
                if (!orb.isAlive) {
                    continue
                }

                val distSqr = this.distanceToSqr(orb)
                if (distSqr > 9.0) {
                    val pull = this.position().add(0.0, 0.4, 0.0)
                        .subtract(orb.position())
                        .normalize()
                        .scale(0.25)
                    orb.setDeltaMovement(orb.getDeltaMovement().add(pull))
                } else {
                    this.setStateMinor(
                        STATE_MINOR_PUMPED_XP,
                        this.getStateMinor(STATE_MINOR_PUMPED_XP) + orb.getValue()
                    )
                    orb.discard()
                }
            }
        }

        var bottleSlot = findFirstCargoItem(Items.GLASS_BOTTLE)
        while (bottleSlot >= 0 && this.getStateMinor(STATE_MINOR_PUMPED_XP) >= XP_BOTTLE_COST) {
            val extracted = this.inventory!!.extractItem(bottleSlot, 1, false)
            if (extracted.isEmpty()) {
                break
            }

            this.setStateMinor(STATE_MINOR_PUMPED_XP, this.getStateMinor(STATE_MINOR_PUMPED_XP) - XP_BOTTLE_COST)

            val remaining = insertIntoCargo(ItemStack(Items.EXPERIENCE_BOTTLE))
            if (!remaining.isEmpty() && this.level() is ServerLevel) {
                val serverLevel = this.level() as ServerLevel
                serverLevel.addFreshEntity(ItemEntity(serverLevel, this.getX(), this.getY(), this.getZ(), remaining))
            }

            bottleSlot = findFirstCargoItem(Items.GLASS_BOTTLE)
        }
    }

    private fun findFirstCargoItem(item: Item?): Int {
        val slotCount = this.accessibleInventorySlotCount
        for (i in ShipInventoryHandler.equipSlotCount..<slotCount) {
            val stack = this.inventory!!.getStackInSlot(i)
            if (!stack.isEmpty() && stack.`is`(item)) {
                return i
            }
        }
        return -1
    }

    private fun tickAutoRation() {
        if ((this.tickCount % AUTO_RATION_INTERVAL_TICKS) != 0) {
            return
        }

        val threshold = Mth.clamp(this.getStateMinor(ShipContainerMenu.STATE_MINOR_RATION_MORALE), 1, 4)
        if (this.getMoraleLevel() < threshold) {
            return
        }

        if (this.fuel >= AUTO_RATION_MAX_FUEL && this.getHealth() >= this.getMaxHealth()) {
            return
        }

        consumeOneCombatRation()
    }

    fun getMoraleLevel(): Int {
        val m = this.morale
        return when {
            m > 5100 -> 0
            m > 3900 -> 1
            m > 2100 -> 2
            m > 900  -> 3
            else     -> 4
        }
    }

    private fun consumeOneCombatRation(): Boolean {
        val slotCount = this.accessibleInventorySlotCount
        for (i in ShipInventoryHandler.equipSlotCount..<slotCount) {
            val stack = this.inventory!!.getStackInSlot(i)
            if (stack.isEmpty()) {
                continue
            }

            if (stack.getItem() !is CombatRationItem) {
                continue
            }

            val variant: Int = (stack.getItem() as CombatRationItem).getVariant(stack)
            applyCombatRationEffect(variant)
            stack.shrink(1)
            if (stack.isEmpty()) {
                this.inventory.setStackInSlot(i, ItemStack.EMPTY)
            } else {
                this.inventory.setStackInSlot(i, stack)
            }

            return true
        }

        return false
    }

    private fun consumeCombatRationInHand(stack: ItemStack, player: Player): Boolean {
        if (stack.getItem() !is CombatRationItem) {
            return false
        }

        applyCombatRationEffect((stack.getItem() as CombatRationItem).getVariant(stack))
        if (!player.getAbilities().instabuild) {
            stack.shrink(1)
        }
        return true
    }

    private fun applyCombatRationEffect(variant: Int) {
        val fuelGain = rollFuelGain(this.getRandom(), variant)
        this.fuel = min(AUTO_RATION_MAX_FUEL, this.fuel + fuelGain)
        this.addMorale(getMoraleValue(variant))

        if (this.getHealth() < this.getMaxHealth()) {
            this.heal(this.getMaxHealth() * 0.05f + 1.0f)
        }

        if (this.feedSoundCooldown <= 0) {
            this.playSound(
                getShipSound(
                    Config.ShipCustomSoundType.FEED,
                    this.getStateMinor(STATE_MINOR_SHIP_CLASS),
                    this.getRandom()
                ), this.getSoundVolume(),
                this.shipSoundPitch
            )
            this.feedSoundCooldown = 30
        }

        this.applyParticleEmotion(
            when (this.getRandom().nextInt(3)) {
                1 -> EmotionParticleType.DROOL
                2 -> EmotionParticleType.SIGH
                else -> EmotionParticleType.HEART
            }
        )
        this.emotionPrimary = EMOTION_HAPPY
        this.resetInteractionEmotionState()
    }

    private fun consumeBucketRepairInHand(stack: ItemStack, player: Player): Boolean {
        if (!stack.`is`(ModItems.BUCKET_REPAIR.get())) {
            return false
        }

        if (this.getHealth() < this.getMaxHealth()) {
            if (this.supportsAircraftCombat()) {
                this.heal(this.getMaxHealth() * 0.05f + 10.0f)
            } else {
                this.heal(this.getMaxHealth() * 0.1f + 5.0f)
            }
            this.recordCreativeDebuggerBucketRepair()

            if (this.supportsAircraftCombat()) {
                this.numAircraftLight = this.numAircraftLight + 1
                this.numAircraftHeavy = this.numAircraftHeavy + 1
            }

            if (!player.getAbilities().instabuild) {
                stack.shrink(1)
            }

            this.emotionPrimary = EMOTION_HAPPY
            this.applyParticleEmotion(EmotionParticleType.HEART)
            this.playSound(
                getShipSound(
                    Config.ShipCustomSoundType.FEED,
                    this.getStateMinor(STATE_MINOR_SHIP_CLASS),
                    this.getRandom()
                ), this.getSoundVolume(),
                this.shipSoundPitch
            )
            this.focusOnPlayer(player)
            return true
        }
        return false
    }

    fun interactModernKit(player: Player, stack: ItemStack): Boolean {
        if (!this.legacyShipStats.addBonusRandom(Random())) {
            return false
        }

        this.syncLegacyBonusData()
        this.recalculateLegacyShipStats()
        this.emotionPrimary = EMOTION_HAPPY
        this.applyParticleEmotion(EmotionParticleType.HEART)
        this.playSound(
            getShipSound(
                Config.ShipCustomSoundType.MARRY,
                this.getStateMinor(STATE_MINOR_SHIP_CLASS),
                this.getRandom()
            ),
            max(0.0f, Config.volumeShip),
            1.0f
        )
        this.focusOnPlayer(player)

        if (!player.getAbilities().instabuild) {
            stack.shrink(1)
        }

        return true
    }

    private fun consumeToyAirplaneInHand(stack: ItemStack, player: Player): Boolean {
        if (!stack.`is`(ModItems.TOY_AIRPLANE.get())) {
            return false
        }

        if (this.supportsAircraftCombat()) {
            this.numAircraftLight = this.numAircraftLight + 2
            this.numAircraftHeavy = this.numAircraftHeavy + 2
        }

        this.addMorale(200)
        this.emotionPrimary = EMOTION_HAPPY
        this.applyParticleEmotion(EmotionParticleType.HAPPY_BOB)
        this.playSound(
            getShipSound(Config.ShipCustomSoundType.FEED, this.getStateMinor(STATE_MINOR_SHIP_CLASS), this.getRandom()),
            this.getSoundVolume(),
            this.shipSoundPitch
        )

        if (!player.getAbilities().instabuild) {
            stack.shrink(1)
        }
        this.focusOnPlayer(player)
        return true
    }

    fun findItemInInventory(item: Item?): Int {
        val slots = this.inventory!!.accessibleSlotCount
        for (i in 0..<slots) {
            val stack = this.inventory.getStackInSlot(i)
            if (!stack.isEmpty() && stack.`is`(item)) {
                return i
            }
        }
        return -1
    }

    fun consumeItemInInventory(item: Item?): Boolean {
        val slot = findItemInInventory(item)
        if (slot >= 0) {
            val stack = this.inventory!!.getStackInSlot(slot)
            stack.shrink(1)
            if (stack.isEmpty()) {
                this.inventory.setStackInSlot(slot, ItemStack.EMPTY)
            }
            this.onInventoryChanged()
            return true
        }
        return false
    }

    private fun hasCreativeDebuggerInInventory(): Boolean {
        return findItemInInventory(ModItems.DEBUG_INSPECTOR.get()) >= 0
    }

    fun hasCreativeDebugger(): Boolean {
        return this.isCreativeDebuggerActive
    }

    val isCreativeDebuggerActive: Boolean
        get() = this.entityData.get<Boolean?>(CREATIVE_DEBUGGER_ACTIVE)

    private fun refreshCreativeDebuggerState() {
        this.entityData.set<Boolean?>(CREATIVE_DEBUGGER_ACTIVE, hasCreativeDebuggerInInventory())
    }

    val creativeDebuggerStack: ItemStack?
        get() {
            val slot = findItemInInventory(ModItems.DEBUG_INSPECTOR.get())
            return if (slot >= 0) this.inventory!!.getStackInSlot(slot) else null
        }

    fun recordCreativeDebuggerBucketRepair() {
        val stack = this.creativeDebuggerStack
        if (stack != null && !stack.isEmpty()) {
            markBucketRepairTriggered(stack, this)
        }
    }

    private fun tickAutoSupplies() {
        if (this.level().isClientSide || this.isHostileShipMob) {
            return
        }

        if (this.fuel <= 0) {
            val modFuel = this.legacyShipStats.getBuffedAttr(17)
            if (this.consumeItemInInventory(ModItems.GRUDGE.get())) {
                this.fuel = (300 * modFuel).toInt()
                this.applyAutoSupplyEffects()
            } else if (this.consumeItemInInventory(ModItems.GRUDGE_BLOCK.get())) {
                this.fuel = (2700 * modFuel).toInt()
                this.applyAutoSupplyEffects()
            }
        }

        // Ammo is derived directly from carried ammo items and containers in the
        // modern inventory model. Consuming a single stack here would reintroduce
        // the old hidden ammo-pool behavior and desync the live GUI counts.
    }

    private fun applyAutoSupplyEffects() {
        if (this.emotesTick <= 0) {
            this.emotesTick = 40
            val rnd = this.random.nextInt(3)
            if (rnd == 0) this.applyParticleEmotion(EmotionParticleType.DROOL)
            else if (rnd == 1) this.applyParticleEmotion(EmotionParticleType.BLINK)
            else this.applyParticleEmotion(EmotionParticleType.SIGH)
        }
    }

    override fun createNavigation(level: Level): PathNavigation {
        val navigation = ShipLegacyNavigation(this, level)
        navigation.setCanFloat(true)
        return navigation
    }

    val shipDepth: Double
        get() {
            val level = this.level()
            val px = Mth.floor(this.getX())
            val py = Mth.floor(this.getBoundingBox().minY)
            val pz = Mth.floor(this.getZ())
            val pos = MutableBlockPos(px, py, pz)
            val state = level.getFluidState(pos)

            if (state.isEmpty()) {
                return 0.0
            }

            var depth = 1.0
            val maxY = level.getMaxBuildHeight()
            var i = 1
            while (py + i < maxY) {
                pos.setY(py + i)
                if (!level.getFluidState(pos).isEmpty()) {
                    depth += 1.0
                } else {
                    break
                }
                i++
            }

            depth -= (this.getY() - Mth.floor(this.getY()))
            return depth
        }

    override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        val stack = player.getItemInHand(hand)

        if (!this.level().isClientSide && hand == InteractionHand.MAIN_HAND) {
            if (!this.isTame) {
                return InteractionResult.PASS
            }

            if (!this.isOwnedBy(player)) {
                return InteractionResult.PASS
            }
            if (stack.`is`(ModItems.TRAINING_BOOK.get()) || stack.`is`(ModItems.MODERN_KIT.get())) {
                return InteractionResult.PASS
            }

            if (stack.`is`(ModItems.MARRIAGE_RING.get()) && !this.isStateMarried) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1)
                }
                this.isStateMarried = true
                adjustOwnedMarriedShipCount(player, 1)
                this.morale = 16000
                this.emotionPrimary = EMOTION_HAPPY
                this.applyParticleEmotion(EmotionParticleType.HEART)
                if (this.level() is ServerLevel) {
            val serverLevel = this.level() as ServerLevel
                    for (i in 0..6) {
                        val px = this.getX() + (this.getRandom().nextFloat() * 2.0f - 1.0f)
                        val py = this.getY() + 0.5 + (this.getRandom().nextFloat() * 2.0f)
                        val pz = this.getZ() + (this.getRandom().nextFloat() * 2.0f - 1.0f)
                        val d0 = this.getRandom().nextGaussian() * 0.02
                        val d1 = this.getRandom().nextGaussian() * 0.02
                        val d2 = this.getRandom().nextGaussian() * 0.02
                        serverLevel.sendParticles<SimpleParticleType?>(
                            ParticleTypes.HEART,
                            px,
                            py,
                            pz,
                            0,
                            d0,
                            d1,
                            d2,
                            1.0
                        )
                    }
                }
                this.playSound(
                    getShipSound(
                        Config.ShipCustomSoundType.MARRY,
                        this.getStateMinor(STATE_MINOR_SHIP_CLASS),
                        this.getRandom()
                    ), this.getSoundVolume(),
                    this.shipSoundPitch
                )

                val javaRand = Random()
                for (i in 0..2) {
                    this.legacyShipStats.addBonusRandom(javaRand)
                }
                this.recalculateLegacyShipStats()

                this.resetInteractionEmotionState()
                this.focusOnPlayer(player)
                return InteractionResult.sidedSuccess(this.level().isClientSide)
            }

            if (stack.getItem() is CombatRationItem) {
                if (consumeCombatRationInHand(stack, player)) {
                    this.focusOnPlayer(player)
                    return InteractionResult.sidedSuccess(this.level().isClientSide)
                }
            }

            if (stack.`is`(ModItems.KAITAI_HAMMER.get()) && player.isShiftKeyDown()) {
                spawnKaitaiDrops()
                this.applyParticleEmotion(8)
                this.applyEmotesAOE(10.0, 6, false)
                this.hurt(player.damageSources().fellOutOfWorld(), Float.MAX_VALUE)
                if (!player.getAbilities().instabuild) {
                    stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND)
                }
                this.focusOnPlayer(player)
                return InteractionResult.sidedSuccess(this.level().isClientSide)
            }

            if (stack.`is`(ModItems.BUCKET_REPAIR.get())) {
                if (consumeBucketRepairInHand(stack, player)) {
                    return InteractionResult.sidedSuccess(this.level().isClientSide)
                }
            }

            if (stack.`is`(ModItems.TOY_AIRPLANE.get())) {
                if (consumeToyAirplaneInHand(stack, player)) {
                    return InteractionResult.sidedSuccess(this.level().isClientSide)
                }
            }

            if (stack.`is`(ModItems.GRUDGE.get())) {
                val gain = 300 + this.getRandom().nextInt(500)
                this.fuel = this.fuel + gain
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1)
                }
                if (this.feedSoundCooldown <= 0) {
                    this.playSound(
                        getShipSound(
                            Config.ShipCustomSoundType.FEED,
                            this.getStateMinor(STATE_MINOR_SHIP_CLASS),
                            this.getRandom()
                        ), this.getSoundVolume(),
                        this.shipSoundPitch
                    )
                    this.feedSoundCooldown = 30
                }
                this.emotionPrimary = EMOTION_HAPPY
                this.resetInteractionEmotionState()
                this.focusOnPlayer(player)
                return InteractionResult.sidedSuccess(this.level().isClientSide)
            }

            if (stack.has(DataComponents.FOOD)) {
                val food = stack.getFoodProperties(player)
                if (food != null && food.nutrition() > 0) {
                    this.fuel = this.fuel + food.nutrition()
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1)
                    }
                    if (this.feedSoundCooldown <= 0) {
                        this.playSound(
                            getShipSound(
                                Config.ShipCustomSoundType.FEED,
                                this.getStateMinor(STATE_MINOR_SHIP_CLASS),
                                this.getRandom()
                            ), this.getSoundVolume(),
                            this.shipSoundPitch
                        )
                        this.feedSoundCooldown = 30
                    }
                    this.emotionPrimary = EMOTION_HAPPY
                    this.resetInteractionEmotionState()
                    this.focusOnPlayer(player)
                    return InteractionResult.sidedSuccess(this.level().isClientSide)
                }
            }

            if (player.isShiftKeyDown()) {
                this.openShipMenu(player)
                this.resetInteractionEmotionState()
                this.focusOnPlayer(player)
                return InteractionResult.sidedSuccess(this.level().isClientSide)
            }

            val isSitting = !this.isOrderedToSit()
            this.setOrderedToSit(isSitting)
            this.setInSittingPose(isSitting)
            if (!isSitting && this.hasBlockGuardTarget()) {
                this.clearGuardTarget()
            }
            this.resetInteractionEmotionState()
            this.focusOnPlayer(player)

            return InteractionResult.sidedSuccess(this.level().isClientSide)
        }
        return super.mobInteract(player, hand)
    }

    private fun spawnKaitaiDrops() {
        if (this.level() !is ServerLevel) {
            return
        }
        val serverLevel = this.level() as ServerLevel

        for (drop in buildKaitaiMaterialDrops()) {
            if (!drop.isEmpty()) {
                serverLevel.addFreshEntity(ItemEntity(serverLevel, this.getX(), this.getY() + 0.8, this.getZ(), drop))
            }
        }

        for (i in 0..<this.inventory!!.getSlots()) {
            val stack = this.inventory.getStackInSlot(i)
            if (stack.isEmpty()) {
                continue
            }
            serverLevel.addFreshEntity(
                ItemEntity(
                    serverLevel,
                    this.getX(),
                    this.getY() + 0.8,
                    this.getZ(),
                    stack.copy()
                )
            )
            this.inventory.setStackInSlot(i, ItemStack.EMPTY)
        }
    }

    private fun buildKaitaiMaterialDrops(): MutableList<ItemStack> {
        val drops: MutableList<ItemStack> = ArrayList<ItemStack>(4)
        val shipClass = this.getStateMinor(STATE_MINOR_SHIP_CLASS)
        val rarity = max(0, this.getStateMinor(STATE_MINOR_RARITY))
        val firepower = max(1.0f, this.legacyShipStats.firepower)
        val maxHealth = max(1.0f, this.legacyShipStats.maxHealth)

        var primary = ModItems.GRUDGE.get()
        var grudge = 4 + rarity
        var abyssMetal = 0
        var ammo = 0
        var polymetal = 0

        if (shipClass >= 20 || shipClass == 12 || shipClass == 13 || shipClass == 14 || shipClass == 15 || shipClass == 16) {
            grudge += 4
            abyssMetal += 6 + rarity
            ammo += 6 + Mth.floor(firepower * 0.2f)
        }

        if (shipClass >= 26 || shipClass == 20 || shipClass == 21 || shipClass == 30 || shipClass == 31 || shipClass == 33 || shipClass == 49) {
            primary = ModItems.ABYSS_POLYMETAL.get()
            grudge = 0
            abyssMetal += 10 + rarity * 2
            ammo += 10 + Mth.floor(firepower * 0.25f)
            polymetal += 3 + rarity
        } else if (shipClass == 17 || shipClass == 18 || shipClass == 19 || shipClass == 38 || shipClass == 39 || shipClass == 44 || shipClass == 72) {
            ammo += 4 + rarity
            abyssMetal += 2 + Mth.floor(maxHealth * 0.03f)
        } else if (shipClass == 12 || shipClass == 20 || shipClass == 33 || shipClass == 47 || shipClass == 48) {
            ammo += 8 + rarity
            polymetal += 1 + rarity / 2
        } else if (shipClass == 13 || shipClass == 14 || shipClass == 15 || shipClass == 26 || shipClass == 37 || shipClass == 46 || shipClass == 60 || shipClass == 61 || shipClass == 62 || shipClass == 63) {
            abyssMetal += 8 + rarity
            ammo += 5 + Mth.floor(firepower * 0.15f)
        } else {
            abyssMetal += 2 + rarity / 2
            ammo += 2 + rarity / 2
        }

        addNonEmptyDrop(drops, primary, grudge)
        addNonEmptyDrop(drops, ModItems.ABYSS_METAL.get(), abyssMetal)
        addNonEmptyDrop(drops, ModItems.AMMO_LIGHT.get(), ammo)
        addNonEmptyDrop(drops, ModItems.ABYSS_POLYMETAL.get(), polymetal)
        return drops
    }

    override fun doHurtTarget(target: Entity): Boolean {
        if (!this.getStateFlag(ShipContainerMenu.STATE_FLAG_CAN_MELEE)) {
            return false
        }
        if (!this.level().isClientSide) {
            this.addShipExp(Config.shipExpGainMelee)
        }
        val result = super.doHurtTarget(target)
        if (result && !this.level().isClientSide) {
            this.playSound(
                getShipSound(
                    Config.ShipCustomSoundType.ATTACK,
                    this.getStateMinor(STATE_MINOR_SHIP_CLASS),
                    this.getRandom()
                ), this.getSoundVolume(),
                this.shipSoundPitch
            )
            this.attackTick = 50
            applyEmotesReaction(3)
        }
        return result
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (this.customHurtTime > 0 || source.`is`(DamageTypeTags.IS_FIRE) || source.`is`(DamageTypes.IN_WALL)) {
            return false
        }

        if (!this.level().isClientSide && tryLegacyDodge(source)) {
            return false
        }

        var reduced = amount
        if (!this.level().isClientSide && amount < 100000.0f) {
            reduced = this.legacyShipStats.getDefenseReducedDamage(amount, this.getRandom())
        }

        val isHammer = source.getEntity() is Player && (source.getEntity() as Player).getMainHandItem().`is`(ModItems.KAITAI_HAMMER.get())

        if (!this.level().isClientSide && this.isCreativeDebuggerActive && !isHammer && reduced >= this.getHealth()) {
            this.setHealth(max(1.0f, this.getHealth()))
            this.customHurtTime = 20
            return false
        }

        if (!this.level().isClientSide && !isHammer && !this.isDeadOrDying() && reduced >= this.getHealth() && !source.`is`(
                DamageTypeTags.BYPASSES_INVULNERABILITY
            )
        ) {
            val attacker = source.getEntity()
            val isOwnerAttack = attacker is Player && attacker.getUUID() == this.ownerUUID

            if (!isOwnerAttack && this.consumeItemInInventory(ModItems.REPAIR_GODDESS.get())) {
                this.setHealth(this.getMaxHealth())
                this.customHurtTime = 120
                this.spawnGoddessParticles()
                this.playSound(
                    getShipSound(
                        Config.ShipCustomSoundType.FEED,
                        this.getStateMinor(STATE_MINOR_SHIP_CLASS),
                        this.getRandom()
                    ), this.getSoundVolume(),
                    this.shipSoundPitch
                )
                return false
            }
        }

        val result = super.hurt(source, reduced)
        if (!this.level().isClientSide && result) {
            if (this.isOrderedToSit() || this.isInSittingPose()) {
                this.setOrderedToSit(false)
                this.setInSittingPose(false)
            }
            if (this.getRandom().nextInt(5) == 0) {
                applyEmotesReaction(2)
                this.emotionPrimary = EMOTION_SCORN
            }
        }
        return result
    }

    private fun tryLegacyDodge(source: DamageSource): Boolean {
        val attacker = source.getEntity()
        if (attacker == null || attacker === this) {
            return false
        }
        if (source.`is`(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false
        }

        val dodge = Mth.clamp(this.legacyShipStats.getBuffedAttr(15), 0.0f, 0.9f)
        if (dodge <= 0.0f || this.getRandom().nextFloat() > dodge) {
            return false
        }

        this.spawnCombatTextParticle(COMBAT_TEXT_DODGE)
        return true
    }

    override fun heal(amount: Float) {
        if (!this.level().isClientSide) {
            this.spawnLegacyHealParticles()
        }
        super.heal(amount * this.legacyShipStats.getBuffedAttr(19))
    }

    private fun spawnLegacyHealParticles() {
        if (this.level() !is ServerLevel) {
            return
        }
        val serverLevel = this.level() as ServerLevel

        val beamHeight = this.getBbHeight() * 0.4
        val beamRiseSpeed = 0.1
        val beamFad = this.getBbWidth() * 1.5

        serverLevel.sendParticles<SimpleParticleType?>(
            ModParticles.PARTICLE_HEAL_SPARKLE.get(),
            this.getX(),
            this.getY(),
            this.getZ(),
            0,
            beamFad,
            beamRiseSpeed,
            beamHeight,
            1.0
        )
    }

    private fun spawnGoddessParticles() {
        if (this.level() !is ServerLevel) {
            return
        }
        val serverLevel = this.level() as ServerLevel

        val beamHeight = this.getBbHeight() * 0.4
        val beamRiseSpeed = 0.03
        val beamFad = this.getBbWidth() * 2.0

        serverLevel.sendParticles<SimpleParticleType?>(
            ModParticles.PARTICLE_GODDESS.get(),
            this.getX(),
            this.getY(),
            this.getZ(),
            0,
            beamFad,
            beamRiseSpeed,
            beamHeight,
            1.0
        )
    }

    fun onInventoryChanged() {
        this.refreshCreativeDebuggerState()
        this.combat.recalculateAmmoCounts()
        this.recalculateLegacyShipStats()
    }

    fun recalculateLegacyShipStats() {
        val teamId = this.formationTeam
        val slotId = this.formationSlot
        var formationBuffs: FloatArray? = resetFormationValue
        var moraleBuffs: FloatArray? = resetMoraleValue

        val morale = this.morale
        var moraleId = -1
        if (morale > 5100) moraleId = 0
        else if (morale > 3900) moraleId = 1
        else if (morale <= 900) moraleId = 3
        else if (morale <= 2100) moraleId = 2
        val attrsMorale = Values.MoraleAttrs.get(moraleId)
        if (attrsMorale != null) {
            moraleBuffs = attrsMorale
        }

        val owner = this.ownerPlayer
        if (owner != null && teamId >= 0) {
            val data = admiralData(owner)
            val formationId = data.getFormationID(teamId)
            formationBuffs = getFormationBuffs(formationId, slotId)
        }

        this.legacyShipStats.recalculate(
            this.getStateMinor(STATE_MINOR_SHIP_CLASS),
            this.level,
            this.collectEquipBonuses(),
            formationBuffs,
            moraleBuffs
        )

        if (this.level() != null && !this.level().isClientSide) {
            this.getAttribute(Attributes.MAX_HEALTH)?.setBaseValue(this.legacyShipStats.maxHealth.toDouble())
            this.getAttribute(Attributes.ATTACK_DAMAGE)
                ?.setBaseValue((this.legacyShipStats.firepower * LEGACY_MELEE_DAMAGE_FACTOR).toDouble())
            this.getAttribute(Attributes.MOVEMENT_SPEED)
                ?.setBaseValue((this.legacyShipStats.moveSpeed * Config.cruiseSpeedFactor.toFloat()).toDouble())
            this.getAttribute(Attributes.FOLLOW_RANGE)
                ?.setBaseValue(max(24.0, this.legacyShipStats.attackRange.toDouble()))
            if (this.getHealth() > this.getMaxHealth()) {
                this.setHealth(this.getMaxHealth())
            }
        }
    }

    private fun collectEquipBonuses(): FloatArray {
        val equipBonuses = FloatArray(LegacyEquipStats.ATTR_COUNT)
        val equipSlots = Math.min(ShipInventoryHandler.equipSlotCount, this.inventory!!.getSlots())
        var drumCount = 0
        var compassCount = 0
        var flareCount = 0
        var searchlightCount = 0
        var specialAmmoVariant = -1
        var torpedoSpeedLevel = 0

        for (slot in 0..<equipSlots) {
            val stack = this.inventory.getStackInSlot(slot)
            if (stack.isEmpty() || stack.getItem() !is LegacyEquipItem) {
                continue
            }

            val equipTypeId: Int = (stack.getItem() as LegacyEquipItem).getEquipTypeId(stack)
            when (equipTypeId) {
                EQUIP_TYPE_DRUM -> drumCount++
                EQUIP_TYPE_COMPASS -> compassCount++
                EQUIP_TYPE_FLARE -> flareCount++
                EQUIP_TYPE_SEARCHLIGHT -> searchlightCount++
                else -> {}
            }

            if ((stack.getItem() as LegacyEquipItem).getEquipTypeId(stack) == 29) {
                val variant: Int = (stack.getItem() as LegacyEquipItem).getVariant(stack)
                if (variant == 5 || variant == 6 || variant == 8) {
                    specialAmmoVariant = max(specialAmmoVariant, variant)
                }
            } else if ((stack.getItem() as LegacyEquipItem).getEquipTypeId(stack) == 5) {
                val variant: Int = (stack.getItem() as LegacyEquipItem).getVariant(stack)
                if (variant >= 3) {
                    val speed = when (variant) {
                        3, 4 -> 1
                        5 -> 2
                        6 -> 3
                        else -> 0
                    }
                    torpedoSpeedLevel = max(torpedoSpeedLevel, speed)
                }
            }

            val stats = getMainAttrs((stack.getItem() as LegacyEquipItem).getEquipId(stack))
            if (stats == null) {
                continue
            }

            val len = min(equipBonuses.size, stats.size)
            for (i in 0..<len) {
                equipBonuses[i] += stats[i]
            }
        }

        this.setStateMinor(STATE_MINOR_EQUIP_DRUM, drumCount)
        this.setStateMinor(STATE_MINOR_EQUIP_COMPASS, compassCount)
        this.setStateMinor(STATE_MINOR_EQUIP_FLARE, flareCount)
        this.setStateMinor(STATE_MINOR_EQUIP_SEARCHLIGHT, searchlightCount)
        this.setStateMinor(STATE_MINOR_EQUIP_SPECIAL_AMMO, specialAmmoVariant)
        this.setStateMinor(STATE_MINOR_EQUIP_TORPEDO_SPEED, torpedoSpeedLevel)

        return equipBonuses
    }

    val specialAmmoVariant: Int
        get() = this.getStateMinor(STATE_MINOR_EQUIP_SPECIAL_AMMO)

    val torpedoSpeedLevel: Int
        get() = this.getStateMinor(STATE_MINOR_EQUIP_TORPEDO_SPEED)

    open fun setFaceNormal() {
        this.faceExpressions.setFaceNormal()
    }

    open fun setFaceCry() {
        this.faceExpressions.setFaceCry()
    }

    fun setFaceScornOrDamaged() {
        this.faceExpressions.setFaceScornOrDamaged()
    }

    protected open fun setFaceScorn() {
        this.faceExpressions.setFaceScorn()
    }

    protected open fun setFaceDamaged() {
        this.faceExpressions.setFaceDamaged()
    }

    open fun setFaceHungry() {
        this.faceExpressions.setFaceHungry()
    }

    open fun setFaceAngry() {
        this.faceExpressions.setFaceAngry()
    }

    open fun setFaceBored() {
        this.faceExpressions.setFaceBored()
    }

    open fun setFaceShy() {
        this.faceExpressions.setFaceShy()
    }

    open fun setFaceHappy() {
        this.faceExpressions.setFaceHappy()
    }

    protected fun ensureFaceTick() {
        this.emotions.ensureFaceTick()
    }

    val faceElapsed: Int
        get() = this.emotions.faceElapsed

    fun resolveMouthId(id: Int): Int {
        return when (id) {
            MOUTH_FLIP_0 -> MOUTH_FRONT_0
            MOUTH_FLIP_1 -> MOUTH_FRONT_1
            MOUTH_FLIP_2 -> MOUTH_FRONT_2
            else -> id
        }
    }

    protected fun mapLegacyMouth(legacyId: Int): Int {
        return when (legacyId) {
            0 -> MOUTH_FRONT_0
            1 -> MOUTH_FRONT_1
            2 -> MOUTH_FRONT_2
            3 -> MOUTH_FLIP_0
            4 -> MOUTH_FLIP_1
            5 -> MOUTH_FLIP_2
            else -> MOUTH_FRONT_0
        }
    }

    protected fun getLegacyFaceTick(mask: Int): Int {
        return (this.tickCount + (this.getStateMinor(22) shl 7)) and mask
    }

    fun openShipMenu(player: Player?) {
        if (player !is ServerPlayer || !this.isAlive) {
            return
        }
        if (this.level() !== player.level()) {
            return
        }
        if (!this.isOwnedBy(player)) {
            return
        }
        run {
            val provider: MenuProvider = SimpleMenuProvider(
                MenuConstructor { id: Int, inv: Inventory?, ply: Player? -> ShipContainerMenu(id, inv!!, this) },
                Component.translatable("gui.shincolle.ship")
            )
            (player).openMenu(
                provider,
                Consumer { buffer: RegistryFriendlyByteBuf? -> buffer!!.writeInt(this.getId()) })
        }
    }

    open fun migrateLegacyStateFlags(stateFlags: Int) {
    }

    protected val legacyModelStateRange: Int
        get() = 128

    protected fun getInitialLegacyEmotion(index: Int): Int {
        if (index == 0) {
            return 0
        }
        return 0
    }

    private fun initializeLegacyState() {
        for (i in 0..<LEGACY_STATE_EMOTION_COUNT) {
            setStateEmotion(i, getInitialLegacyEmotion(i), false)
        }
        this.isLegacyStateInitializedInternal = true
    }

    fun initializeLegacyStateInternal() {
        initializeLegacyState()
    }

    private val legacyEmotionSnapshot: IntArray
        get() = intArrayOf(
            getStateEmotion(0),
            getStateEmotion(1),
            getStateEmotion(2),
            getStateEmotion(3),
            getStateEmotion(4),
            getStateEmotion(5),
            getStateEmotion(6),
            getStateEmotion(7)
        )

    val legacyEmotionSnapshotInternal: IntArray
        get() = this.legacyEmotionSnapshot

    private fun applyLegacyEmotionSnapshot(legacy: IntArray?) {
        if (legacy == null || legacy.size == 0) {
            return
        }
        val length = min(legacy.size, LEGACY_STATE_EMOTION_COUNT)
        for (i in 0..<length) {
            setStateEmotion(i, legacy[i], false)
        }
    }

    fun applyLegacyEmotionSnapshotInternal(legacy: IntArray?) {
        applyLegacyEmotionSnapshot(legacy)
    }

    fun resetDeathStateForSpawnEgg() {
        this.setHealth(this.getMaxHealth())
        this.deathTime = 0
        this.shipDeathTicks = 0
    }

    val wpStayTimeMax: Int
        get() {
            val wpstay = this.getStateMinor(44)
            if (wpstay >= 1 && wpstay <= 5) return wpstay * 100
            if (wpstay >= 6 && wpstay <= 10) return (wpstay - 5) * 1200
            if (wpstay >= 11 && wpstay <= 16) return (wpstay - 10) * 12000
            return 0
        }

    protected fun tickWaypointMove() {
        if (this.getStateFlag(11) || this.isOrderedToSit() || this.isLeashed() || this.isVehicle()) {
            this.clearWaypointMoveRuntimeState()
            return
        }

        if (this.hasEntityGuardTarget()) {
            val guardedEntity = this.guardedEntity
            if (guardedEntity == null || !guardedEntity.isAlive) {
                this.guardedEntity = null
            } else {
                val guardedDim: Int = getLegacyDimensionId(guardedEntity.level())
                if (guardedDim != this.getGuardedPos(3)) {
                    this.setGuardedPos(-1, -1, -1, guardedDim, ShipGuardTarget.Type.ENTITY.legacyId())
                }
            }
            return
        }

        val guardTarget = this.guardTarget
        if (!guardTarget.isBlock) {
            this.clearWaypointMoveRuntimeState()
            return
        }

        val pos = guardTarget.blockPos()
        val distSq = this.distanceToSqr(pos.getX() + 0.5, pos.getY().toDouble(), pos.getZ() + 0.5)

        val be = this.level().getBlockEntity(pos)
        if (be is CraneBlockEntity) {
            if (distSq < 64.0) {
                if (this.getStateMinor(43) == 0) {
                    this.setStateMinor(43, 1)
                    this.ejectPassengers()
                }
            } else if (this.getStateMinor(6) > 0) {
                this.guardMovement.moveTo(Vec3(pos.getX() + 0.5, pos.getY() - 2.0, pos.getZ() + 0.5), 1.0)
            }
        } else {
            this.setStateMinor(43, 0)
        }

        if (be is IWaypoint) {
            if (this.getStateMinor(26) > 0 && this.getStateMinor(27) > 0) return
            if (distSq < 9.0) {
                try {
                    var timeout = false
                    val wpstay = this.getStateTimer(4)
                    val staytimemax =
                        max(this.wpStayTimeMax, if (be is WayPointBlockEntity) be.stayTimeTicks else 0)
                    if (wpstay < staytimemax) {
                        this.setStateTimer(4, wpstay + 16)
                    } else {
                        timeout = true
                    }
                    if (timeout) {
                        this.setStateTimer(4, 0)
                        val next = be.nextPos
                        val last = be.lastPos
                        val wps = this.waypoints
                        val shiplast = if (wps != null && wps.size > 0) wps[0] else BlockPos.ZERO
                        var targetPos: BlockPos? = null
                        if (next != null && next.y > 0 && next == shiplast) {
                            if (last != null && last.y > 0) targetPos = last
                            else if (next.y > 0) targetPos = next
                        } else if (next != null && next.y > 0) {
                            targetPos = next
                        }
                        if (targetPos != null) {
                            this.setGuardedPos(
                                targetPos.getX(),
                                targetPos.getY(),
                                targetPos.getZ(),
                                getLegacyDimensionId(this.level()),
                                guardTarget.legacyType()
                            )
                            if (this.getStateMinor(6) > 0) {
                                this.setStateMinor(10, 2)
                                this.guardMovement.moveTo(
                                    Vec3(
                                        targetPos.getX() + 0.5,
                                        targetPos.getY().toDouble(),
                                        targetPos.getZ() + 0.5
                                    ), 1.0
                                )
                            }
                        }
                        val newWps: Array<BlockPos?> =
                            if (wps != null && wps.size > 0) wps.copyOf() else arrayOf(BlockPos.ZERO)
                        newWps[0] = pos
                        this.waypoints = newWps
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if ((this.tickCount and 0x7F) == 0 && this.getStateMinor(6) > 0) {
                this.guardMovement.moveTo(Vec3(pos.getX() + 0.5, pos.getY().toDouble(), pos.getZ() + 0.5), 1.0)
            }
        }
    }

    private fun clearWaypointMoveRuntimeState() {
        this.setStateMinor(43, 0)
        this.setStateTimer(4, 0)
        this.guardMovement.stop()
    }

    private fun tickLegacyTimers() {
        val attackTick = this.attackTick
        if (attackTick > 0) {
            this.attackTick = attackTick - 1
        }
        if (this.customHurtTime > 0) {
            this.customHurtTime--
        }
        if (this.hurtSoundCooldown > 0) {
            this.hurtSoundCooldown--
        }
        if (this.feedSoundCooldown > 0) {
            this.feedSoundCooldown--
        }
    }

    fun applyEmotesReaction(type: Int) {
        this.reactions.applyEmotesReaction(type)
    }

    fun applyEmotesAOE(range: Double, type: Int, includeNonOwned: Boolean) {
        if (this.level().isClientSide) return
        val box = this.getBoundingBox().inflate(range)
        val list = this.level().getEntitiesOfClass<EntityShipBase?>(EntityShipBase::class.java, box)
        val owner = this.getOwner()
        for (s in list) {
            if (s.isAlive && s != this) {
                if (includeNonOwned || (owner != null && s.isOwnedBy(owner))) {
                    s.applyEmotesReaction(type)
                }
            }
        }
    }

    fun applyParticleEmotion(type: EmotionParticleType?) {
        if (type != null) {
            this.reactions.applyParticleEmotion(type)
        }
    }

    fun applyParticleEmotion(typeId: Int) {
        this.reactions.applyParticleEmotion(typeId)
    }

    var emotesTick: Int
        get() = this.reactions.getEmotesTick()
        set(ticks) {
            this.reactions.setEmotesTick(ticks)
        }

    fun spawnCombatTextParticle(type: Int) {
        if (this.level() !is ServerLevel) {
            return
        }
        val serverLevel = this.level() as ServerLevel

        val clampedType = Mth.clamp(type, COMBAT_TEXT_MISS, COMBAT_TEXT_DODGE)
        serverLevel.sendParticles<SimpleParticleType?>(
            ModParticles.PARTICLE_TEXTS.get(),
            this.getX(),
            this.getY() + this.getBbHeight() * 1.3,
            this.getZ(),
            0,
            clampedType.toDouble(),
            0.08,
            max(0.2, this.getBbWidth() * 0.45),
            1.0
        )
    }

    fun setEmotionParticlePacked(packed: Int) {
        this.entityData.set<Int?>(EMOTION_PARTICLE, packed)
    }

    @JvmRecord
    data class EquipOption(@JvmField val key: String?, val labelKey: String?)

    override fun die(cause: DamageSource) {
        val isHammer = cause.getEntity() is Player && (cause.getEntity() as Player).getMainHandItem().`is`(ModItems.KAITAI_HAMMER.get())
        if (!this.level().isClientSide && this.isCreativeDebuggerActive && !isHammer) {
            this.setHealth(max(1.0f, this.getHealth()))
            this.customHurtTime = 20
            return
        }

        if (!this.level().isClientSide) {
            if (this.isHostileShipMob) {
                this.applyEmotesAOE(48.0, 6, true)
            } else {
                this.applyEmotesAOE(16.0, 6, false)
            }
        }

        if (!this.level().isClientSide && this.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
            val customMessage: Component =
                Component.translatable("chat.shincolle.entity_fainted", this.getDisplayName())

            val owner = if (this is TamableAnimal) this.getOwner() else null
            if (owner is ServerPlayer) {
                owner.sendSystemMessage(customMessage)
            } else if (this.isTame || this.hasCustomName()) {
                val server = this.level().getServer()
                if (server != null) {
                    server.getPlayerList().broadcastSystemMessage(customMessage, false)
                }
            }
        }

        val backupName = this.getCustomName()
        this.setCustomName(null)

        var backupOwner: UUID? = null
        if (this is TamableAnimal) {
            backupOwner = this.ownerUUID
            this.setOwnerUUID(null)
        }

        super.die(cause)

        this.setCustomName(backupName)
        if (this is TamableAnimal && backupOwner != null) {
            this.setOwnerUUID(backupOwner)
        }
    }

    override fun handleEntityEvent(id: Byte) {
        if (id.toInt() == 104) {
            this.isCustomSwinging = true
            this.customSwingTicks = 0
        } else {
            super.handleEntityEvent(id)
        }
    }

    fun getCustomAttackAnim(partialTick: Float): Float {
        if (!this.isCustomSwinging) return 0.0f
        return (this.customSwingTicks.toFloat() + partialTick) / MAX_SWING_TICKS.toFloat()
    }

    fun startCustomSwing() {
        this.isCustomSwinging = true
        this.customSwingTicks = 0

        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, 104.toByte())
        }
    }

    override fun fireImmune(): Boolean {
        return true
    }

    override fun causeFallDamage(fallDistance: Float, damageMultiplier: Float, source: DamageSource): Boolean {
        return false
    }

    protected fun checkModelState(id: Int, state: Int): Boolean = Companion.checkModelState(id, state)

    protected fun rotateXZByAxis(z: Float, x: Float, radians: Float, scale: Float): FloatArray = Companion.rotateXZByAxis(z, x, radians, scale)

    companion object {
        const val EMOTION_NORMAL: Int = 0
        const val EMOTION_BLINK: Int = 1
        const val EMOTION_CRY: Int = 2
        const val EMOTION_SCORN: Int = 3
        const val EMOTION_BORED: Int = 4
        const val EMOTION_HUNGRY: Int = 5
        const val EMOTION_ANGRY: Int = 6
        const val EMOTION_SHY: Int = 7
        const val EMOTION_HAPPY: Int = 8
        const val EMOTION_DEBUG: Int = 9

        const val COMBAT_TEXT_MISS: Int = 0
        const val COMBAT_TEXT_CRITICAL: Int = 1
        const val COMBAT_TEXT_DOUBLE_HIT: Int = 2
        const val COMBAT_TEXT_TRIPLE_HIT: Int = 3
        const val COMBAT_TEXT_DODGE: Int = 4

        const val FACE_ID_MIN: Int = 0
        const val FACE_ID_MAX: Int = 9
        const val MOUTH_ID_MIN: Int = 0
        const val MOUTH_ID_MAX: Int = 5

        const val FACE_EYES_OPEN: Int = 0
        const val FACE_EYES_CLOSED: Int = 1
        const val FACE_EYES_HALF: Int = 2
        const val FACE_TENSION: Int = 3
        const val FACE_DESPAIR: Int = 4
        const val FACE_DOT_EYES: Int = 5
        const val FACE_DOT_EYES_TEAR: Int = 6
        const val FACE_CRY: Int = 7
        const val FACE_WINK: Int = 8
        const val FACE_SOFT: Int = 9

        private const val FUEL_DECAY_AMOUNT = 1
        private const val MAX_FUEL = 10000
        private const val MORALE_MAX = 16000
        const val moraleDefaultValue: Int = 4000
        private const val AUTO_HEAL_THRESHOLD_RATIO = 0.9f
        private const val AUTO_HEAL_FAST_RATIO = 0.08f
        private const val AUTO_HEAL_FAST_FLAT = 15.0f
        private const val AUTO_HEAL_FAST_FUEL_COST = 7
        private const val AUTO_HEAL_SLOW_RATIO = 0.03f
        private const val AUTO_HEAL_SLOW_FLAT = 1.0f
        private const val LEGACY_MELEE_DAMAGE_FACTOR = 0.125f
        private const val PICK_RADIUS_MODEL_SCALE = 0.012f
        private const val PICK_RADIUS_MIN = 0.05f
        private const val PICK_RADIUS_MAX = 0.20f
        private const val SHIP_DEATH_MAX_TICKS = 300
        private const val SHIP_LEVEL_HARD_CAP = 150
        private const val DEAD_FLOAT_HOVER_OFFSET = 0.08
        private const val DEAD_FLOAT_STOP_EPSILON = 0.003
        private const val TIMEKEEP_INTERVAL_TICKS = 1000L
        private const val PICK_ITEM_SCAN_INTERVAL_TICKS = 16
        private const val AUTO_PUMP_INTERVAL_TICKS = 40
        private const val AUTO_PUMP_XP_INTERVAL_TICKS = 4
        private const val AUTO_RATION_INTERVAL_TICKS = 128
        private const val AUTO_RATION_MAX_FUEL = 100
        private const val SEARCHLIGHT_INTERVAL_TICKS = 4
        private const val COMPASS_CHUNK_REFRESH_INTERVAL_TICKS = 40
        private const val COMPASS_CHUNK_RADIUS = 1
        private const val SPECIAL_EQUIP_FLARE_GLOW_TICKS = 80
        private const val SPECIAL_EQUIP_SEARCHLIGHT_NIGHT_VISION_TICKS = 220
        private const val XP_BOTTLE_COST = 8
        private const val HOSTILE_LIGHT_AMMO_CONTAINER_COUNT = 16
        private const val HOSTILE_HEAVY_AMMO_CONTAINER_COUNT = 12
        const val spawnEggTagName: String = "ShincolleSpawnEgg"
        private const val TAG_SPAWN_EGG_NO_EXP = "ShincolleSpawnEggNoExpCost"

        const val STATE_MINOR_FACTION_ID: Int = 19
        const val STATE_MINOR_SHIP_CLASS: Int = 20
        const val STATE_MINOR_SPECIAL_EQUIP: Int = 25
        const val STATE_MINOR_GRUDGE_CONSUMPTION: Int = 28
        const val STATE_MINOR_RARITY: Int = 13
        private const val STATE_MINOR_EQUIP_DRUM = 36
        private const val STATE_MINOR_EQUIP_COMPASS = 37
        private const val STATE_MINOR_EQUIP_FLARE = 38
        private const val STATE_MINOR_EQUIP_SEARCHLIGHT = 39
        private const val STATE_MINOR_EQUIP_SPECIAL_AMMO = 40
        private const val STATE_MINOR_EQUIP_TORPEDO_SPEED = 41
        const val STATE_MINOR_PUMPED_XP: Int = 42
        const val STATE_MINOR_GUARD_X: Int = 14
        const val STATE_MINOR_GUARD_Y: Int = 15
        const val STATE_MINOR_GUARD_Z: Int = 16
        const val STATE_MINOR_GUARD_DIM: Int = 17
        const val STATE_MINOR_GUARD_TYPE: Int = 18
        const val STATE_MINOR_CRANING: Int = 43

        private const val HELD_MAINHAND_SLOT = 22
        private const val HELD_OFFHAND_SLOT = 23

        private const val EQUIP_TYPE_DRUM = 24
        private const val EQUIP_TYPE_COMPASS = 25
        private const val EQUIP_TYPE_FLARE = 26
        private const val EQUIP_TYPE_SEARCHLIGHT = 27
        private const val EQUIP_DRUM_VARIANT_LIQUID = 1

        const val STATE_FLAG_MARRIED: Int = 1
        const val STATE_FLAG_NO_EQUIP: Int = 2
        const val STATE_FLAG_CAN_MELEE: Int = 3
        const val STATE_FLAG_LIGHT_ATTACK: Int = 4
        const val STATE_FLAG_HEAVY_ATTACK: Int = 5
        const val STATE_FLAG_LIGHT_AIRCRAFT_ATTACK: Int = 6
        const val STATE_FLAG_HEAVY_AIRCRAFT_ATTACK: Int = 7
        const val STATE_FLAG_RING_EFFECT: Int = 9
        const val STATE_FLAG_GUI_BTN_1: Int = 13
        const val STATE_FLAG_GUI_BTN_2: Int = 14
        const val STATE_FLAG_GUI_BTN_3: Int = 15
        const val STATE_FLAG_GUI_BTN_4: Int = 16
        const val STATE_FLAG_ANTI_AIR: Int = 19
        const val STATE_FLAG_CAN_RIDE: Int = 24
        const val STATE_FLAG_APPEARANCE: Int = 25
        const val STATE_FLAG_DISABLE_GUARD_POS: Int = 11

        const val MOUTH_FRONT_0: Int = 0
        const val MOUTH_FRONT_1: Int = 1
        const val MOUTH_FRONT_2: Int = 2
        const val MOUTH_FLIP_0: Int = 3
        const val MOUTH_FLIP_1: Int = 4
        const val MOUTH_FLIP_2: Int = 5

        protected const val EMOTION_TICK_MASK_8BIT: Int = 0xFF
        protected const val EMOTION_TICK_MASK_9BIT: Int = 0x1FF

        protected const val LEGACY_STATE_EMOTION_COUNT: Int = 8
        protected const val LEGACY_ATTACK_TICK_MAX: Int = 100

        val SHIP_LEVEL: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val SHIP_EXP: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val SHIP_KILLS: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)

        val FACE_ID: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val POINTER_SELECTED: EntityDataAccessor<Boolean?> =
            SynchedEntityData.defineId<Boolean?>(EntityShipBase::class.java, EntityDataSerializers.BOOLEAN)
        val MOUTH_ID: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)

        val EMOTION_PRIMARY: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val EMOTION_SECONDARY: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val EMOTION_PARTICLE: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val CREATIVE_DEBUGGER_ACTIVE: EntityDataAccessor<Boolean?> =
            SynchedEntityData.defineId<Boolean?>(EntityShipBase::class.java, EntityDataSerializers.BOOLEAN)
        val NO_FUEL: EntityDataAccessor<Boolean?> =
            SynchedEntityData.defineId<Boolean?>(EntityShipBase::class.java, EntityDataSerializers.BOOLEAN)
        val MORALE: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val FORMATION_TEAM: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val FORMATION_SLOT: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)

        val FUEL: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)

        val AMMO_LIGHT: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val AMMO_HEAVY: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)

        val AIRCRAFT_LIGHT: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val AIRCRAFT_HEAVY: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)

        val EQUIP_FLAGS: EntityDataAccessor<CompoundTag?> =
            SynchedEntityData.defineId<CompoundTag?>(EntityShipBase::class.java, EntityDataSerializers.COMPOUND_TAG)
        const val EQUIP_MOUNT: String = "equip_mount"

        val LEGACY_EMOTION_0: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val LEGACY_EMOTION_1: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val LEGACY_EMOTION_2: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val LEGACY_EMOTION_3: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val LEGACY_EMOTION_4: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val LEGACY_EMOTION_5: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val LEGACY_EMOTION_6: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val LEGACY_EMOTION_7: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)

        val LEGACY_ATTACK_TICK: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val LEGACY_ATTACK_TICK_2: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val LEGACY_RIDING_STATE: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        val LEGACY_SCALE_LEVEL: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(EntityShipBase::class.java, EntityDataSerializers.INT)
        protected val LEGACY_BONUS_DATA: EntityDataAccessor<CompoundTag?> =
            SynchedEntityData.defineId<CompoundTag?>(EntityShipBase::class.java, EntityDataSerializers.COMPOUND_TAG)
        val POINTER_TARGET_DATA: EntityDataAccessor<CompoundTag?> =
            SynchedEntityData.defineId<CompoundTag?>(EntityShipBase::class.java, EntityDataSerializers.COMPOUND_TAG)

        const val MAX_SWING_TICKS: Int = 6
        private const val SHIP_BUOY_MIN_DEPTH = 0.15
        private const val SHIP_BUOY_COEFF = 0.035
        private const val SHIP_BUOY_EXPONENT = 0.6
        private const val SHIP_BUOY_OFFSET = 0.005
        private const val SHIP_BUOY_DAMP = 0.80
        private const val SHIP_BUOY_MAX_MOTION = 0.1

        protected fun checkModelState(id: Int, state: Int): Boolean {
            if (id < 0 || id >= 31) {
                return false
            }
            return (state and (1 shl id)) != 0
        }

        protected fun rotateXZByAxis(z: Float, x: Float, radians: Float, scale: Float): FloatArray {
            val cosD = Mth.cos(radians)
            val sinD = Mth.sin(radians)
            val newPos = floatArrayOf(0.0f, 0.0f)
            newPos[0] = z * cosD + x * sinD
            newPos[1] = x * cosD - z * sinD
            newPos[0] = newPos[0] * scale
            newPos[1] = newPos[1] * scale
            return newPos
        }

        private const val AMBIENT_SOUND_MIN_INTERVAL_TICKS = 80
        private const val AMBIENT_SOUND_MAX_PER_TICK = 3
        private val AMBIENT_SOUNDS_PER_TICK: ConcurrentMap<Long?, Int?> = ConcurrentHashMap<Long?, Int?>()

        private fun startPerfSegment(tracing: Boolean): Long {
            return if (tracing) now() else 0L
        }

        private fun finishPerfSegment(tracing: Boolean, startNanos: Long): Long {
            return if (tracing) elapsed(startNanos) else 0L
        }

        private fun addNonEmptyDrop(drops: MutableList<ItemStack>, item: Item?, amount: Int) {
            if (item == null || amount <= 0) {
                return
            }
            var remaining = amount
            val maxStackSize = item.getDefaultInstance().getMaxStackSize()
            while (remaining > 0) {
                val stackCount = min(remaining, maxStackSize)
                drops.add(ItemStack(item, stackCount))
                remaining -= stackCount
            }
        }

        fun getLegacyDimensionId(level: Level): Int {
            val key = level.dimension().location().toString()
            return when (key) {
                "minecraft:overworld" -> 0
                "minecraft:the_nether" -> -1
                "minecraft:the_end" -> 1
                else -> Int.MIN_VALUE
            }
        }
    }
}
