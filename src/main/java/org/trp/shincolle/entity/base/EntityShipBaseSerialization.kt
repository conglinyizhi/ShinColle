package org.trp.shincolle.entity.base

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.SynchedEntityData
import org.trp.shincolle.menu.ShipContainerMenu

internal class EntityShipBaseSerialization(private val ship: EntityShipBase) {
    fun addAdditionalSaveData(compound: CompoundTag) {
        compound.put("ShipInventory", this.ship.inventory.serializeNBT(this.ship.registryAccess()))

        compound.putInt("ShipLevel", this.ship.level)
        compound.putInt("ShipExp", this.ship.getExp())
        compound.putInt("AmmoLight", this.ship.ammoLight)
        compound.putInt("AmmoHeavy", this.ship.ammoHeavy)
        compound.putInt("AircraftLight", this.ship.getNumAircraftLight())
        compound.putInt("AircraftHeavy", this.ship.getNumAircraftHeavy())
        compound.putInt("EmotionPrimary", this.ship.getEmotionPrimary())
        compound.putInt("EmotionSecondary", this.ship.getEmotionSecondary())
        compound.putInt("Morale", this.ship.getMorale())
        compound.putBoolean("NoFuel", this.ship.isNoFuel())
        compound.putInt("Fuel", this.ship.fuel)
        compound.put("EquipFlags", this.ship.copyEquipFlagsTag())
        compound.putBoolean("PointerSelected", this.ship.isPointerSelected())
        compound.putInt("FormationTeam", this.ship.getFormationTeam())
        compound.putInt("FormationSlot", this.ship.getFormationSlot())
        compound.putIntArray("StateEmotion", this.ship.getLegacyEmotionSnapshotInternal())
        compound.putInt("AttackTick", this.ship.attackTick)
        compound.putInt("AttackTick2", this.ship.attackTick2)
        compound.putInt("RidingState", this.ship.getRidingState())
        compound.putInt("ScaleLevel", this.ship.getScaleLevel())
        compound.putInt("ShipKills", this.ship.getShipKills())

        compound.put("LegacyPoint", this.ship.createLegacyBonusTag())

        compound.putBoolean("LegacyStateInit", this.ship.isLegacyStateInitializedInternal())
        val legacyState = this.ship.getLegacyStateInternal()
        compound.putIntArray("LegacyStateMinor", legacyState.stateMinor)
        compound.putIntArray("LegacyStateTimer", legacyState.stateTimer)
        compound.putByteArray("LegacyStateFlags", legacyState.toByteArray(legacyState.stateFlag))
        compound.putByteArray("LegacyUpdateFlags", legacyState.toByteArray(legacyState.updateFlag))
        compound.putByteArray("LegacyBodyHeightStand", legacyState.bodyHeightStand)
        compound.putByteArray("LegacyBodyHeightSit", legacyState.bodyHeightSit)
        compound.putIntArray("LegacyModelPos", legacyState.getModelPosBits())
        compound.putIntArray("LegacyWaypoints", legacyState.getWaypointBits())
        if (this.ship.getGuardedEntityIdInternal() != null) {
            compound.putUUID("GuardedEntityId", this.ship.getGuardedEntityIdInternal())
        }
        val legacyShipExtPropsBackup = this.ship.getLegacyShipExtPropsBackupInternal()
        if (!legacyShipExtPropsBackup.isEmpty()) {
            compound.put(LEGACY_EXT_PROPS_BACKUP, legacyShipExtPropsBackup)
        }
        this.ship.savePointerToNbt(compound)
    }

    fun readAdditionalSaveData(compound: CompoundTag) {
        if (compound.contains("ShipInventory")) {
            this.ship.inventory.deserializeNBT(this.ship.registryAccess(), compound.getCompound("ShipInventory"))
        } else if (compound.contains(LEGACY_INV_TAG)) {
            this.ship.inventory.deserializeNBT(this.ship.registryAccess(), compound.getCompound(LEGACY_INV_TAG))
        }

        this.ship.setLevel(compound.getInt("ShipLevel"))
        this.ship.setExp(compound.getInt("ShipExp"))
        this.ship.ammoLight = compound.getInt("AmmoLight")
        this.ship.ammoHeavy = compound.getInt("AmmoHeavy")
        this.ship.emotionPrimary = compound.getInt("EmotionPrimary")
        this.ship.setEmotionSecondary(compound.getInt("EmotionSecondary"))

        if (compound.contains("Morale")) {
            this.ship.setMorale(compound.getInt("Morale"))
        }

        if (compound.contains("PointerSelected")) {
            this.ship.setPointerSelected(compound.getBoolean("PointerSelected"))
        } else {
            this.ship.setPointerSelected(false)
        }
        this.ship.setFormationTeam(compound.getInt("FormationTeam"))
        this.ship.setFormationSlot(compound.getInt("FormationSlot"))
        this.ship.loadPointerFromNbt(compound)

        if (compound.contains("EquipFlags")) {
            this.ship.setEquipFlagsTag(compound.getCompound("EquipFlags"))
        } else if (compound.contains("StateFlags")) {
            this.ship.migrateLegacyStateFlags(compound.getInt("StateFlags"))
        } else if (compound.contains(LEGACY_EXT_PROPS)) {
            loadLegacyShipExtProps(compound.getCompound(LEGACY_EXT_PROPS))
        }
        if (compound.contains(LEGACY_EXT_PROPS_BACKUP)) {
            this.ship.setLegacyShipExtPropsBackupInternal(compound.getCompound(LEGACY_EXT_PROPS_BACKUP))
        }

        if (compound.contains("StateEmotion")) {
            this.ship.applyLegacyEmotionSnapshotInternal(compound.getIntArray("StateEmotion"))
            this.ship.setLegacyStateInitializedInternal(true)
        }
        if (compound.contains("AttackTick")) {
            this.ship.attackTick = compound.getInt("AttackTick")
        }
        if (compound.contains("AttackTick2")) {
            this.ship.setAttackTick2(compound.getInt("AttackTick2"))
        }
        if (compound.contains("RidingState")) {
            this.ship.ridingState = compound.getInt("RidingState")
        }
        if (compound.contains("ScaleLevel")) {
            this.ship.setScaleLevel(compound.getInt("ScaleLevel"))
        }
        // Tamed ships should always use default scale (hostile boss scale persists in egg NBT)
        if (this.ship.isTame && this.ship.getScaleLevel() > 0) {
            this.ship.setScaleLevel(0)
        }

        if (compound.contains("ShipKills")) {
            this.ship.setShipKills(compound.getInt("ShipKills"))
        }

        if (compound.contains("LegacyPoint")) {
            this.ship.applyLegacyBonusTag(compound.getCompound("LegacyPoint"))
        }

        if (compound.contains("LegacyStateInit")) {
            this.ship.setLegacyStateInitializedInternal(compound.getBoolean("LegacyStateInit"))
        }

        val legacyState = this.ship.getLegacyStateInternal()
        if (compound.contains("LegacyStateMinor")) {
            legacyState.applyIntArray(legacyState.stateMinor, compound.getIntArray("LegacyStateMinor"))
        }

        // Sync synched data from legacy array or separate tags
        val fuel =
            if (compound.contains("Fuel")) compound.getInt("Fuel") else legacyState.getInt(legacyState.stateMinor, 6)
        this.ship.fuel = fuel
        val airLight = if (compound.contains("AircraftLight")) compound.getInt("AircraftLight") else legacyState.getInt(
            legacyState.stateMinor,
            7
        )
        this.ship.setNumAircraftLight(airLight)
        val airHeavy = if (compound.contains("AircraftHeavy")) compound.getInt("AircraftHeavy") else legacyState.getInt(
            legacyState.stateMinor,
            8
        )
        this.ship.setNumAircraftHeavy(airHeavy)
        if (compound.contains("LegacyStateTimer")) {
            legacyState.applyIntArray(legacyState.stateTimer, compound.getIntArray("LegacyStateTimer"))
        }
        if (compound.contains("LegacyStateFlags")) {
            legacyState.applyByteArray(legacyState.stateFlag, compound.getByteArray("LegacyStateFlags"))
        }
        if (compound.contains("LegacyUpdateFlags")) {
            legacyState.applyByteArray(legacyState.updateFlag, compound.getByteArray("LegacyUpdateFlags"))
        }
        if (compound.contains("LegacyBodyHeightStand")) {
            legacyState.applyByteArray(legacyState.bodyHeightStand, compound.getByteArray("LegacyBodyHeightStand"))
        }
        if (compound.contains("LegacyBodyHeightSit")) {
            legacyState.applyByteArray(legacyState.bodyHeightSit, compound.getByteArray("LegacyBodyHeightSit"))
        }
        if (compound.contains("LegacyModelPos")) {
            legacyState.applyModelPosBits(compound.getIntArray("LegacyModelPos"))
        }
        if (compound.contains("LegacyWaypoints")) {
            legacyState.applyWaypointBits(compound.getIntArray("LegacyWaypoints"))
        }
        if (compound.hasUUID("GuardedEntityId")) {
            this.ship.loadGuardedEntityIdInternal(compound.getUUID("GuardedEntityId"))
        } else {
            this.ship.loadGuardedEntityIdInternal(null)
        }

        this.ship.refreshDimensions()

        if (compound.getBoolean(EntityShipBase.Companion.getSpawnEggTagName())) {
            this.ship.resetDeathStateForSpawnEgg()
        }

        if (!this.ship.isLegacyStateInitializedInternal()) {
            this.ship.initializeLegacyStateInternal()
        }

        this.ship.syncLegacyBonusData()

        // Rebuild derived combat/resource counts from the restored inventory so
        // stale legacy NBT values do not leave ships with visible ammo items but
        // zero usable light/heavy ammo after load.
        this.ship.onInventoryChanged()
    }

    private fun loadLegacyShipExtProps(legacyExt: CompoundTag) {
        this.ship.setLegacyShipExtPropsBackupInternal(legacyExt.copy())

        val minor = legacyExt.getCompound("Minor")
        if (!minor.isEmpty()) {
            if (minor.contains("Level")) {
                this.ship.setLevel(minor.getInt("Level"))
            }
            if (minor.contains("Exp")) {
                this.ship.setExp(minor.getInt("Exp"))
            }
            if (minor.contains("NumAmmoL")) {
                this.ship.ammoLight = minor.getInt("NumAmmoL")
            }
            if (minor.contains("NumAmmoH")) {
                this.ship.ammoHeavy = minor.getInt("NumAmmoH")
            }
            if (minor.contains("NumGrudge")) {
                this.ship.fuel = minor.getInt("NumGrudge")
            }
            if (minor.contains("NumAirL")) {
                this.ship.setNumAircraftLight(minor.getInt("NumAirL"))
            }
            if (minor.contains("NumAirH")) {
                this.ship.setNumAircraftHeavy(minor.getInt("NumAirH"))
            }
            if (minor.contains("GuardX")) {
                this.ship.setStateMinor(EntityShipBase.Companion.STATE_MINOR_GUARD_X, minor.getInt("GuardX"))
            }
            if (minor.contains("GuardY")) {
                this.ship.setStateMinor(EntityShipBase.Companion.STATE_MINOR_GUARD_Y, minor.getInt("GuardY"))
            }
            if (minor.contains("GuardZ")) {
                this.ship.setStateMinor(EntityShipBase.Companion.STATE_MINOR_GUARD_Z, minor.getInt("GuardZ"))
            }
            if (minor.contains("GuardDim")) {
                this.ship.setStateMinor(EntityShipBase.Companion.STATE_MINOR_GUARD_DIM, minor.getInt("GuardDim"))
            }
            if (minor.contains("GuardType")) {
                this.ship.setStateMinor(EntityShipBase.Companion.STATE_MINOR_GUARD_TYPE, minor.getInt("GuardType"))
            }
            if (minor.contains("FType")) {
                this.ship.setFormationTeam(minor.getInt("FType"))
            }
            if (minor.contains("FPos")) {
                this.ship.setFormationSlot(minor.getInt("FPos"))
            }
            if (minor.contains("Morale")) {
                this.ship.setMorale(minor.getInt("Morale"))
            }
            if (minor.contains("Crane")) {
                this.ship.setStateMinor(EntityShipBase.Companion.STATE_MINOR_CRANING, minor.getInt("Crane"))
            }
            if (minor.contains("FMin")) {
                this.ship.setStateMinor(ShipContainerMenu.STATE_MINOR_FOLLOW_MIN, minor.getInt("FMin"))
            }
            if (minor.contains("FMax")) {
                this.ship.setStateMinor(ShipContainerMenu.STATE_MINOR_FOLLOW_MAX, minor.getInt("FMax"))
            }
            if (minor.contains("FHP")) {
                this.ship.setStateMinor(ShipContainerMenu.STATE_MINOR_FLEE_HP, minor.getInt("FHP"))
            }
            if (minor.contains("AutoCR")) {
                this.ship.setStateMinor(ShipContainerMenu.STATE_MINOR_RATION_MORALE, minor.getInt("AutoCR"))
            }
            if (minor.contains("WpStay")) {
                this.ship.setStateMinor(ShipContainerMenu.STATE_MINOR_WP_STAY, minor.getInt("WpStay"))
            }
            if (minor.contains("Task")) {
                this.ship.setStateMinor(ShipContainerMenu.STATE_MINOR_TASK_ID, minor.getInt("Task"))
            }
            if (minor.contains("Side")) {
                this.ship.setStateMinor(ShipContainerMenu.STATE_MINOR_TASK_SIDE, minor.getInt("Side"))
            }
            if (minor.contains("tagName")) {
                val customName = minor.getString("tagName")
                if (!customName.isEmpty()) {
                    this.ship.setCustomName(Component.literal(customName))
                }
            }
        }

        val display = legacyExt.getCompound("Display")
        if (!display.isEmpty()) {
            if (display.contains("State")) {
                this.ship.setStateEmotion(0, display.getInt("State"), false)
            }
            if (display.contains("Emotion")) {
                this.ship.setStateEmotion(1, display.getInt("Emotion"), false)
            }
            if (display.contains("Emotion2")) {
                this.ship.setStateEmotion(2, display.getInt("Emotion2"), false)
            }
            if (display.contains("Phase")) {
                this.ship.setStateEmotion(3, display.getInt("Phase"), false)
            }
            this.ship.setLegacyStateInitializedInternal(true)
        }

        val legacyBonus = legacyExt.getCompound("Point")
        if (!legacyBonus.isEmpty()) {
            this.ship.applyLegacyBonusTag(legacyBonus)
        }

        val flags = legacyExt.getCompound("ShipFlags")
        if (!flags.isEmpty()) {
            if (flags.contains("IsMarried")) {
                this.ship.setStateMarried(flags.getBoolean("IsMarried"))
            }
            if (flags.contains("NoFuel")) {
                this.ship.setNoFuel(flags.getBoolean("NoFuel"))
            }
            if (flags.contains("Melee")) {
                this.ship.setStateCanMelee(flags.getBoolean("Melee"))
            }
            if (flags.contains("AmmoL")) {
                this.ship.setStateLightAttack(flags.getBoolean("AmmoL"))
            }
            if (flags.contains("AmmoH")) {
                this.ship.setStateHeavyAttack(flags.getBoolean("AmmoH"))
            }
            if (flags.contains("AirL")) {
                this.ship.setStateLightAircraftAttack(flags.getBoolean("AirL"))
            }
            if (flags.contains("AirH")) {
                this.ship.setStateHeavyAircraftAttack(flags.getBoolean("AirH"))
            }
            if (flags.contains("WedEffect")) {
                this.ship.setStateRingEffect(flags.getBoolean("WedEffect"))
            }
            if (flags.contains("CanFollow")) {
                this.ship.setOrderedToSit(!flags.getBoolean("CanFollow"))
            }
            if (flags.contains("CanDrop")) {
                this.ship.setLegacyDeathDropInternal(flags.getBoolean("CanDrop"))
            }
            if (flags.contains("OnSight")) {
                this.ship.setStateFlag(ShipContainerMenu.STATE_FLAG_ON_SIGHT, flags.getBoolean("OnSight"))
            }
            if (flags.contains("PVPFirst")) {
                this.ship.setStateFlag(ShipContainerMenu.STATE_FLAG_PVP, flags.getBoolean("PVPFirst"))
            }
            if (flags.contains("AA")) {
                this.ship.setStateAntiAir(flags.getBoolean("AA"))
            }
            if (flags.contains("ASM")) {
                this.ship.setStateFlag(ShipContainerMenu.STATE_FLAG_ANTI_SUB, flags.getBoolean("ASM"))
            }
            if (flags.contains("PassiveAI")) {
                this.ship.setStateFlag(ShipContainerMenu.STATE_FLAG_PASSIVE_ATTACK, flags.getBoolean("PassiveAI"))
            }
            if (flags.contains("TimeKeeper")) {
                this.ship.setStateFlag(ShipContainerMenu.STATE_FLAG_TIMEKEEP, flags.getBoolean("TimeKeeper"))
            }
            if (flags.contains("PickItem")) {
                this.ship.setStateFlag(ShipContainerMenu.STATE_FLAG_PICK_ITEM, flags.getBoolean("PickItem"))
            }
            if (flags.contains("AutoPump")) {
                this.ship.setStateFlag(ShipContainerMenu.STATE_FLAG_AUTO_PUMP, flags.getBoolean("AutoPump"))
            }
            if (flags.contains("HeldItem")) {
                this.ship.setStateAppearance(flags.getBoolean("HeldItem"))
            }
        }

        val timer = legacyExt.getCompound("Timer")
        if (!timer.isEmpty() && timer.contains("Crane")) {
            this.ship.setStateTimer(LEGACY_TIMER_CRANE, timer.getInt("Crane"))
        }
    }

    companion object {
        private const val LEGACY_EXT_PROPS = "ShipExtProps"
        private const val LEGACY_EXT_PROPS_BACKUP = "LegacyShipExtPropsBackup"
        private const val LEGACY_INV_TAG = "ShipInv"
        private const val LEGACY_TIMER_CRANE = 4

        fun defineSynchedData(builder: SynchedEntityData.Builder) {
            builder.define<Int?>(EntityShipBase.Companion.SHIP_LEVEL, 1)
            builder.define<Int?>(EntityShipBase.Companion.SHIP_EXP, 0)
            builder.define<Int?>(EntityShipBase.Companion.SHIP_KILLS, 0)
            builder.define<Int?>(EntityShipBase.Companion.FACE_ID, 0)
            builder.define<Int?>(EntityShipBase.Companion.MOUTH_ID, 0)
            builder.define<Int?>(EntityShipBase.Companion.EMOTION_PRIMARY, EntityShipBase.Companion.EMOTION_NORMAL)
            builder.define<Int?>(EntityShipBase.Companion.EMOTION_SECONDARY, EntityShipBase.Companion.EMOTION_NORMAL)
            builder.define<Int?>(EntityShipBase.Companion.EMOTION_PARTICLE, 0)
            builder.define<Boolean?>(EntityShipBase.Companion.CREATIVE_DEBUGGER_ACTIVE, false)
            builder.define<Boolean?>(EntityShipBase.Companion.NO_FUEL, false)
            builder.define<Int?>(EntityShipBase.Companion.MORALE, EntityShipBase.Companion.getMoraleDefaultValue())
            builder.define<Int?>(EntityShipBase.Companion.FUEL, 0)
            builder.define<Int?>(EntityShipBase.Companion.AMMO_LIGHT, 0)
            builder.define<Int?>(EntityShipBase.Companion.AMMO_HEAVY, 0)
            builder.define<Int?>(EntityShipBase.Companion.AIRCRAFT_LIGHT, 0)
            builder.define<Int?>(EntityShipBase.Companion.AIRCRAFT_HEAVY, 0)
            builder.define<CompoundTag?>(EntityShipBase.Companion.EQUIP_FLAGS, CompoundTag())
            builder.define<Boolean?>(EntityShipBase.Companion.POINTER_SELECTED, false)
            builder.define<Int?>(EntityShipBase.Companion.FORMATION_TEAM, -1)
            builder.define<Int?>(EntityShipBase.Companion.FORMATION_SLOT, -1)

            builder.define<Int?>(EntityShipBase.Companion.LEGACY_EMOTION_0, 0)
            builder.define<Int?>(EntityShipBase.Companion.LEGACY_EMOTION_1, 0)
            builder.define<Int?>(EntityShipBase.Companion.LEGACY_EMOTION_2, 0)
            builder.define<Int?>(EntityShipBase.Companion.LEGACY_EMOTION_3, 0)
            builder.define<Int?>(EntityShipBase.Companion.LEGACY_EMOTION_4, 0)
            builder.define<Int?>(EntityShipBase.Companion.LEGACY_EMOTION_5, 0)
            builder.define<Int?>(EntityShipBase.Companion.LEGACY_EMOTION_6, 0)
            builder.define<Int?>(EntityShipBase.Companion.LEGACY_EMOTION_7, 0)

            builder.define<Int?>(EntityShipBase.Companion.LEGACY_ATTACK_TICK, 0)
            builder.define<Int?>(EntityShipBase.Companion.LEGACY_ATTACK_TICK_2, 0)
            builder.define<Int?>(EntityShipBase.Companion.LEGACY_RIDING_STATE, 0)
            builder.define<Int?>(EntityShipBase.Companion.LEGACY_SCALE_LEVEL, 0)
        }
    }
}
