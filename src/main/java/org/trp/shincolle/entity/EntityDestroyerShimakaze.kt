package org.trp.shincolle.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.entity.projectile.EntityAbyssMissile
import org.trp.shincolle.entity.projectile.EntityAbyssMissile.MoveType
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.init.ModItems
import java.util.List
import kotlin.math.max

class EntityDestroyerShimakaze(type: EntityType<out TamableAnimal?>?, level: Level?) : EntityShipBase(type, level),
    IShipSummonAttack {
    private var numRensouhou: Int = MAX_RENSOUHOU

    init {
        setModelPos(floatArrayOf(0f, 25f, 0f, 45f))
        setStateMinor(STATE_MINOR_FACTION_ID, -1)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 36)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 5)
        setStateMinor(STATE_MINOR_RARITY, 6)
        setStateGuiBtn3(false)
        setStateGuiBtn4(false)
        setStateCanRide(true)
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()

        if ((this.tickCount % 128) == 0) {
            updateServerLogic()
        }
    }

    val passengersRidingOffset: Double
        get() {
            if (this.getIsSitting()) {
                return (if (this.getStateEmotion(1) == 4) this.getBbHeight() * -0.04f else this.getBbHeight() * 0.16f).toDouble()
            }
            return (this.getBbHeight() * 0.67f).toDouble()
        }

    override fun getEquipOptions(): MutableList<EquipOption?> {
        val list: MutableList<EquipOption?> = ArrayList<EquipOption?>(super.getEquipOptions())
        list.addAll(
            List.of<EquipOption?>(
                EquipOption(EQUIP_RENSOUHOU_TYPE, "gui.shincolle.equip.rensouhou_type"),
                EquipOption(EQUIP_RIGGING, "gui.shincolle.equip.rigging"),
                EquipOption(EQUIP_HAIR_ANCHOR, "gui.shincolle.equip.hair_anchor"),
                EquipOption(EQUIP_HAIR_FRONT_1, "gui.shincolle.equip.hair_front_1"),
                EquipOption(EQUIP_HAIR_FRONT_2, "gui.shincolle.equip.hair_front_2"),
                EquipOption(EQUIP_HAIR_FRONT_3, "gui.shincolle.equip.hair_front_3")
            )
        )
        return list
    }

    private fun updateServerLogic() {
        if (this.numRensouhou < MAX_RENSOUHOU) {
            this.numRensouhou++
        }
        if (this.isStateMarried() && this.isStateRingEffect() && this.getStateMinor(6) > 0) {
            if (this.getOwnerPlayer() != null && this.distanceToSqr(this.getOwnerPlayer()) < 256.0) {
                val amp = this.getStateMinor(0) / 35 + 1
                this.getOwnerPlayer().addEffect(
                    MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED,
                        80 + this.getStateMinor(0), amp, false, false
                    )
                )
            }
        }
    }

    override fun performLightAttack(target: Entity?) {
        if (this.numRensouhou > 0 && this.getRandom().nextInt(3) == 0) {
            if (this.attackEntityWithAmmo(target)) {
                return
            }
        }
        super.performLightAttack(target)
    }

    override fun performHeavyAttack(target: Entity?): Boolean {
        if (this.attackEntityWithHeavyAmmo(target)) {
            return true
        }
        return super.performHeavyAttack(target)
    }

    fun attackEntityWithAmmo(target: Entity?): Boolean {
        if (this.numRensouhou <= 0) {
            return false
        }
        if (target == null || !target.isAlive()) {
            return false
        }
        if (isSameOwnerAttackTarget(target)) {
            return false
        }
        if (!consumeLightAmmo(4)) {
            return false
        }

        this.numRensouhou--
        this.setFuel(this.getFuel() - Config.fuelConsumeActionLight)
        this.setAttackTick(100)
        this.applyEmotesReaction(3)

        spawnAttackEffects()
        summonRensouhou(target)

        return true
    }

    fun attackEntityWithHeavyAmmo(target: BlockPos): Boolean {
        return launchTorpedoSalvo(target, null)
    }

    fun attackEntityWithHeavyAmmo(target: Entity?): Boolean {
        if (target == null) {
            return false
        }
        if (!target.isAlive()) {
            return false
        }
        if (isSameOwnerAttackTarget(target)) {
            return false
        }
        return launchTorpedoSalvo(target.blockPosition(), target)
    }

    private fun spawnAttackEffects() {
        if (this.level() is ServerLevel) {
            serverLevel.sendParticles<SimpleParticleType?>(
                ParticleTypes.CLOUD, this.getX(), this.getY() + 1.0, this.getZ(),
                12, 0.25, 0.1, 0.25, 0.02
            )
        }
    }

    private fun summonRensouhou(target: Entity?) {
        if (this.level() !is ServerLevel) {
            return
        }

        if (checkModelState(0, this.getStateEmotion(0))) {
            val rensouhou = ModEntities.RENSOUHOU_S.get().create(serverLevel)
            if (rensouhou != null) {
                rensouhou.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot())
                rensouhou.initSummon(this, target, 0)
                serverLevel.addFreshEntity(rensouhou)
            }
        } else {
            val rensouhou = ModEntities.RENSOUHOU.get().create(serverLevel)
            if (rensouhou != null) {
                rensouhou.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot())
                rensouhou.initSummon(this, target, 0)
                serverLevel.addFreshEntity(rensouhou)
            }
        }
    }

    private fun launchTorpedoSalvo(targetPos: BlockPos, targetEntity: Entity?): Boolean {
        if (!consumeHeavyAmmo(1)) {
            return false
        }

        this.setFuel(this.getFuel() - Config.fuelConsumeActionHeavy)
        this.setAttackTick(50)
        this.applyEmotesReaction(3)

        var aimPos = Vec3(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5)
        val toTarget = aimPos.subtract(this.position())
        if (toTarget.length() < 6.0 && toTarget.length() > 1.0E-6) {
            val push = toTarget.normalize().scale(6.0 - toTarget.length())
            aimPos = aimPos.add(push)
        }

        spawnTorpedoes(aimPos, targetEntity)
        return true
    }

    private fun spawnTorpedoes(centerTarget: Vec3, targetEntity: Entity?) {
        val baseDamage = this.getAttributeValue(Attributes.ATTACK_DAMAGE).toFloat()
        val damage = max(4.0f, baseDamage * 0.3f)
        val speed = 0.7f
        val life = 160
        val explosionRadius = 3.5f

        val targetY = if (targetEntity != null)
            targetEntity.getY() + targetEntity.getBbHeight() * 0.1
        else
            centerTarget.y + 0.2

        for (offset in TORPEDO_OFFSETS) {
            val target = centerTarget.add(offset[0].toDouble(), targetY - centerTarget.y, offset[1].toDouble())
            val direction = target.subtract(this.position().add(0.0, this.getBbHeight() * 0.7, 0.0))
            val velocity = if (direction.lengthSqr() < 1.0E-6)
                Vec3(0.0, 0.0, 0.0)
            else
                direction.normalize().scale(speed.toDouble())

            val missile = EntityAbyssMissile(
                this.level(), this, targetEntity, damage,
                MoveType.PRESET_VELOCITY, speed, 0.25f, 0.25f, velocity, life, explosionRadius
            )
            this.level().addFreshEntity(missile)
        }
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tag.putInt("NumRensouhou", this.numRensouhou)
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        if (tag.contains("NumRensouhou")) {
            this.numRensouhou = Mth.clamp(tag.getInt("NumRensouhou"), 0, MAX_RENSOUHOU)
        }
    }

    override fun getNumServant(): Int {
        return this.numRensouhou
    }

    override fun setNumServant(num: Int) {
        this.numRensouhou = Mth.clamp(num, 0, MAX_RENSOUHOU)
    }

    protected override fun setFaceNormal() {
        this.setFaceId(FACE_EYES_OPEN)
        val tick = this.tickCount and EMOTION_TICK_MASK_8BIT
        if (this.getStateEmotion(7) == 4 && tick > 160) {
            this.setMouthId(mapLegacyMouth(3))
        } else {
            this.setMouthId(mapLegacyMouth(0))
        }
    }

    protected override fun setFaceCry() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 128) {
            this.setFaceId(FACE_DOT_EYES_TEAR)
            this.setMouthId(mapLegacyMouth(if (tick < 64) 5 else 2))
        } else {
            this.setFaceId(FACE_CRY)
            this.setMouthId(mapLegacyMouth(2))
        }
    }

    override fun setFaceDamaged() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_9BIT)
        if (tick < 200) {
            this.setFaceId(FACE_DOT_EYES_TEAR)
            this.setMouthId(mapLegacyMouth(if (tick < 60) 5 else 2))
        } else if (tick < 400) {
            this.setFaceId(FACE_TENSION)
            this.setMouthId(mapLegacyMouth(if (tick < 250) 0 else 4))
        } else {
            this.setFaceId(FACE_SOFT)
            this.setMouthId(mapLegacyMouth(if (tick < 450) 0 else 1))
        }
    }

    override fun setFaceScorn() {
        this.setFaceId(FACE_EYES_HALF)
        this.setMouthId(mapLegacyMouth(1))
    }

    protected override fun setFaceHungry() {
        this.setFaceId(FACE_DESPAIR)
        this.setMouthId(mapLegacyMouth(2))
    }

    protected override fun setFaceAngry() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 128) {
            this.setFaceId(FACE_EYES_CLOSED)
            this.setMouthId(mapLegacyMouth(if (tick < 64) 0 else 1))
        } else {
            this.setFaceId(FACE_EYES_HALF)
            this.setMouthId(mapLegacyMouth(if (tick < 170) 1 else 2))
        }
    }

    protected override fun setFaceBored() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_9BIT)
        if (tick < 170) {
            this.setFaceId(FACE_DOT_EYES)
            this.setMouthId(mapLegacyMouth(if (tick < 80) 0 else 4))
        } else if (tick < 340) {
            this.setFaceId(FACE_WINK)
            this.setMouthId(mapLegacyMouth(0))
        } else {
            this.setFaceId(FACE_EYES_OPEN)
            this.setMouthId(mapLegacyMouth(0))
        }
    }

    protected override fun setFaceShy() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 140) {
            this.setFaceId(FACE_EYES_OPEN)
            this.setMouthId(mapLegacyMouth(if (tick < 80) 3 else 2))
        } else {
            this.setFaceId(FACE_WINK)
            this.setMouthId(mapLegacyMouth(0))
        }
    }

    protected override fun setFaceHappy() {
        val tick = getLegacyFaceTick(EMOTION_TICK_MASK_8BIT)
        if (tick < 140) {
            this.setFaceId(FACE_TENSION)
            this.setMouthId(mapLegacyMouth(if (tick < 80) 0 else 4))
        } else {
            this.setFaceId(FACE_WINK)
            this.setMouthId(mapLegacyMouth(4))
        }
    }


    override fun supportsItemPickup(): Boolean {
        return true
    }

    override fun getShipSpawnEggItem(): Item {
        return ModItems.DESTROYER_SHIMAKAZE_SPAWN_EGG.get()
    }

    companion object {
        const val EQUIP_RIGGING: String = "equip_rigging"
        const val EQUIP_RENSOUHOU_TYPE: String = "equip_rensouhou_type"
        const val EQUIP_HAIR_ANCHOR: String = "equip_hair_anchor"
        const val EQUIP_HAIR_FRONT_1: String = "equip_hair_front_1"
        const val EQUIP_HAIR_FRONT_2: String = "equip_hair_front_2"
        const val EQUIP_HAIR_FRONT_3: String = "equip_hair_front_3"

        private const val MAX_RENSOUHOU = 6
        private val TORPEDO_OFFSETS = arrayOf<FloatArray>(
            floatArrayOf(0f, 0f),
            floatArrayOf(3.5f, 3.5f),
            floatArrayOf(3.5f, -3.5f),
            floatArrayOf(-3.5f, 3.5f),
            floatArrayOf(-3.5f, -3.5f)
        )
    }
}
