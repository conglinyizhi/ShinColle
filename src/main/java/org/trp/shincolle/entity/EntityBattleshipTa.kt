package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import org.trp.shincolle.Config
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.init.ModItems
import kotlin.math.max
import kotlin.math.min

class EntityBattleshipTa(type: EntityType<out TamableAnimal?>?, level: Level?) : EntityShipBase(type, level),
    IShipSummonAttack {
    private var numRensouhou = 0

    init {
        this.modelPos = floatArrayOf(0f, 25f, 0f, 40f)
        setStateMinor(STATE_MINOR_FACTION_ID, 6)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 14)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 3)
        setStateMinor(STATE_MINOR_RARITY, 3)
        this.isStateGuiBtn4 = false
    }

    override fun tickAliveLogic() {
        super.tickAliveLogic()

        if ((this.tickCount % 128) == 0) {
            updateServerLogic()
        }
    }

    val passengersRidingOffset: Double
        get() {
            if (this.isInSittingPose) {
                return if (this.getStateEmotion(1) == 4) 0.0 else (this.getBbHeight() * 0.47f).toDouble()
            }
            return (this.getBbHeight() * 0.76f).toDouble()
        }

    override val equipOptions: MutableList<EquipOption>
        get() {
        val list: MutableList<EquipOption> = ArrayList(super.equipOptions)
        list.add(EquipOption(EQUIP_CLOAK, "gui.shincolle.equip.cloak"))
        list.add(EquipOption(EQUIP_RIGGING, "gui.shincolle.equip.rigging"))
        list.add(EquipOption(EQUIP_ARMOR, "gui.shincolle.equip.armor"))
        return list
    }

    override fun performLightAttack(target: Entity?) {
        if (this.level() !is ServerLevel) {
            return
        }
        if (target == null || !target.isAlive) {
            return
        }
        if (isSameOwnerAttackTarget(target)) {
            return
        }

        if (this.numRensouhou > 0 && this.getRandom().nextInt(3) == 0) {
            if (consumeLightAmmo(4)) {
                this.numRensouhou--
                this.fuel = this.fuel - Config.fuelConsumeActionLight
                this.attackTick = 100
                this.applyEmotesReaction(3)
                serverLevel.sendParticles<SimpleParticleType?>(
                    ParticleTypes.CLOUD, this.getX(), this.getY() + 1.0, this.getZ(),
                    10, 0.25, 0.1, 0.25, 0.02
                )
                summonRensouhou(serverLevel, target)
                return
            }
        }

        super.performLightAttack(target)
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tag.putInt("NumRensouhou", this.numRensouhou)
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        if (tag.contains("NumRensouhou")) {
            this.numRensouhou = max(0, min(MAX_RENSOUHOU, tag.getInt("NumRensouhou")))
        }
    }

    override fun getNumServant(): Int {
        return this.numRensouhou
    }

    override fun setNumServant(num: Int) {
        this.numRensouhou = max(0, min(MAX_RENSOUHOU, num))
    }

    private fun updateServerLogic() {
        if (this.numRensouhou < MAX_RENSOUHOU) {
            this.numRensouhou++
        }
        if (this.isStateMarried && this.isStateRingEffect && this.getStateMinor(6) > 0) {
            applyBuffToNearbyAllies()
        }
    }

    private fun applyBuffToNearbyAllies() {
        val ships = this.level().getEntitiesOfClass<EntityShipBase?>(
            EntityShipBase::class.java,
            this.getBoundingBox().inflate(16.0, 16.0, 16.0)
        )
        if (ships.isEmpty()) {
            return
        }
        val duration = 50 + this.getStateMinor(0)
        val amp = max(0, this.getStateMinor(0) / 80)
        for (ship in ships) {
            if (ship === this) {
                continue
            }
            if (ship.getOwnerUUID() != this.getOwnerUUID()) {
                continue
            }
            ship.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, amp, false, false))
        }
    }

    private fun summonRensouhou(serverLevel: ServerLevel, target: Entity?) {
        if (checkModelState(0, this.getStateEmotion(0))) {
            val rensouhou = ModEntities.RENSOUHOU.get().create(serverLevel)
            if (rensouhou != null) {
                rensouhou.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot())
                rensouhou.initSummon(this, target, 0)
                serverLevel.addFreshEntity(rensouhou)
            }
        } else {
            val rensouhouS = ModEntities.RENSOUHOU_S.get().create(serverLevel)
            if (rensouhouS != null) {
                rensouhouS.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot())
                rensouhouS.initSummon(this, target, 0)
                serverLevel.addFreshEntity(rensouhouS)
            }
        }
    }

    override fun getShipSpawnEggItem(): Item {
        return ModItems.BATTLESHIP_TA_SPAWN_EGG.get()
    }

    companion object {
        const val EQUIP_CLOAK: String = "equip_cloak"
        const val EQUIP_RIGGING: String = "equip_rigging"
        const val EQUIP_ARMOR: String = "equip_armor"

        private const val MAX_RENSOUHOU = 6
    }
}
