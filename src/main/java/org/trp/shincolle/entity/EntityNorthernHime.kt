package org.trp.shincolle.entity

import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModItems

class EntityNorthernHime(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    init {
        this.modelPos = floatArrayOf(-6f, 25f, 0f, 40f)
        setStateMinor(STATE_MINOR_FACTION_ID, 7)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 31)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 5)
        setStateMinor(STATE_MINOR_RARITY, 5)
        setStateMinor(STATE_MINOR_GRUDGE_CONSUMPTION, Config.fuelConsumeBBV)
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
    }

    override fun getBreedOffspring(level: ServerLevel, otherParent: AgeableMob): AgeableMob? {
        return null
    }

    override fun isFood(stack: ItemStack): Boolean {
        return false
    }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_CANNON, "gui.shincolle.equip.cannon"))
        list.add(EquipOption(EQUIP_SANTA_CLOTH, "gui.shincolle.equip.santa_cloth"))
        list.add(EquipOption(EQUIP_SANTA_HAT, "gui.shincolle.equip.santa_hat"))
        list.add(EquipOption(EQUIP_UMBRELLA, "gui.shincolle.equip.umbrella"))
        list.add(EquipOption(EQUIP_SHOES, "gui.shincolle.equip.shoes"))
        return list
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.NORTHERN_HIME_SPAWN_EGG.get()

    override fun migrateLegacyStateFlags(stateFlags: Int) {
        setEquipFlag(EQUIP_CANNON, (stateFlags and (1 shl 0)) != 0)
        val santa = (stateFlags and (1 shl 1)) != 0
        setEquipFlag(EQUIP_SANTA_CLOTH, santa)
        setEquipFlag(EQUIP_SANTA_HAT, santa)
        setEquipFlag(EQUIP_UMBRELLA, (stateFlags and (1 shl 2)) != 0)
        setEquipFlag(EQUIP_SHOES, (stateFlags and (1 shl 3)) != 0)
    }

    override fun setFaceNormal() {
        this.faceId = FACE_EYES_OPEN
        val tick = this.tickCount and EMOTION_TICK_MASK_8BIT
        if (this.emotionSecondary == EMOTION_BORED && tick > 200) {
            this.mouthId = mapLegacyMouth(0)
        } else {
            this.mouthId = mapLegacyMouth(3)
        }
    }

    override fun setFaceCry() {
        val tick = faceElapsed and EMOTION_TICK_MASK_8BIT
        if (tick < 128) {
            this.faceId = FACE_DOT_EYES_TEAR
            this.mouthId = mapLegacyMouth(if (tick < 64) 2 else 5)
        } else {
            this.faceId = FACE_CRY
            this.mouthId = mapLegacyMouth(5)
        }
    }

    override fun setFaceDamaged() {
        val tick = faceElapsed and EMOTION_TICK_MASK_9BIT
        if (tick < 200) {
            this.faceId = FACE_DOT_EYES_TEAR
            this.mouthId = mapLegacyMouth(if (tick < 60) 4 else 5)
        } else if (tick < 400) {
            this.faceId = FACE_TENSION
            this.mouthId = mapLegacyMouth(if (tick < 250) 3 else 5)
        } else {
            this.faceId = FACE_SOFT
            this.mouthId = mapLegacyMouth(if (tick < 450) 2 else 3)
        }
    }

    override fun setFaceScorn() {
        this.faceId = FACE_EYES_HALF
        this.mouthId = mapLegacyMouth(3)
    }

    override fun setFaceHungry() {
        this.faceId = FACE_DESPAIR
        this.mouthId = mapLegacyMouth(3)
    }

    override fun setFaceAngry() {
        val tick = faceElapsed and EMOTION_TICK_MASK_8BIT
        if (tick < 128) {
            this.faceId = FACE_EYES_CLOSED
            this.mouthId = mapLegacyMouth(if (tick < 64) 3 else 1)
        } else {
            this.faceId = FACE_EYES_HALF
            this.mouthId = mapLegacyMouth(if (tick < 170) 0 else 3)
        }
    }

    override fun setFaceBored() {
        val tick = faceElapsed and EMOTION_TICK_MASK_9BIT
        if (tick < 170) {
            this.faceId = FACE_EYES_CLOSED
            this.mouthId = mapLegacyMouth(if (tick < 80) 0 else 3)
        } else if (tick < 340) {
            this.faceId = FACE_DOT_EYES
            this.mouthId = mapLegacyMouth(if (tick < 250) 4 else 3)
        } else {
            this.faceId = FACE_EYES_OPEN
            this.mouthId = mapLegacyMouth(if (tick < 420) 4 else 3)
        }
    }

    override fun setFaceShy() {
        val tick = faceElapsed and EMOTION_TICK_MASK_8BIT
        this.faceId = FACE_EYES_OPEN
        this.mouthId = mapLegacyMouth(if (tick < 150) 3 else 2)
    }

    override fun setFaceHappy() {
        val tick = faceElapsed and EMOTION_TICK_MASK_8BIT
        if (tick < 140) {
            this.faceId = FACE_TENSION
            this.mouthId = mapLegacyMouth(if (tick < 80) 4 else 3)
        } else {
            this.faceId = FACE_WINK
            this.mouthId = mapLegacyMouth(3)
        }
    }


    companion object {
        const val EQUIP_CANNON: String = "equip_cannon"
        const val EQUIP_SANTA_CLOTH: String = "equip_santa_cloth"
        const val EQUIP_SANTA_HAT: String = "equip_santa_hat"
        const val EQUIP_UMBRELLA: String = "equip_umbrella"
        const val EQUIP_SHOES: String = "equip_shoes"

        @JvmStatic
        fun createAttributes(): AttributeSupplier.Builder {
            return createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 200.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.STEP_HEIGHT, 1.0)
        }
    }
}
