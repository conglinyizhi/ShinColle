package org.trp.shincolle.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.trp.shincolle.entity.base.EntityMountBase
import org.trp.shincolle.entity.base.EntityShipBase
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.init.ModItems
import kotlin.math.max

class EntityAirfieldHime(type: EntityType<out TamableAnimal>, level: Level) : EntityShipBase(type, level) {
    init {
        this.modelPos = floatArrayOf(-6f, 30f, 0f, 40f)
        setStateMinor(STATE_MINOR_FACTION_ID, 10)
        setStateMinor(STATE_MINOR_SHIP_CLASS, 21)
        setStateMinor(STATE_MINOR_SPECIAL_EQUIP, 2)
        setStateMinor(STATE_MINOR_RARITY, 4)
        this.isStateCanRide = true
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
        list.add(EquipOption(EQUIP_HAND, "gui.shincolle.equip.hand"))
        list.add(EquipOption(EQUIP_ARMOR, "gui.shincolle.equip.armor"))
        list.add(EquipOption(EQUIP_POSE_1, "gui.shincolle.equip.pose1"))
        list.add(EquipOption(EQUIP_POSE_2, "gui.shincolle.equip.pose2"))
        return list
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
                if (this.getStateEmotion(1) == 4) {
                    return (this.getBbHeight() * 0.65f).toDouble()
                }
                return (this.getBbHeight() * 0.56f).toDouble()
            }
            return (this.getBbHeight() * 0.75f).toDouble()
        }

    private fun updateServerLogic() {
        if (this.getStateMinor(6) > 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(this.getMaxHealth() * 0.06f + 1.0f)
        }

        if (!(this.isStateMarried && this.isStateRingEffect && this.getStateMinor(6) > 50)) {
            return
        }

        var healCount = this.level / 15 + 2
        val range = this.getBoundingBox().inflate(12.0, 12.0, 12.0)
        val targets = this.level().getEntitiesOfClass<LivingEntity?>(LivingEntity::class.java, range)
        for (target in targets) {
            if (healCount <= 0) {
                break
            }
            if (target === this || target.getHealth() / target.getMaxHealth() >= 0.96f) {
                continue
            }

            var healed = false
            if (target is Player) {
                if (target.getUUID() != this.getOwnerUUID()) {
                    continue
                }
                target.heal(1.0f + target.getMaxHealth() * 0.04f + this.level * 0.04f)
                healed = true
            } else if (target is EntityShipBase) {
                if (target.getOwnerUUID() != this.getOwnerUUID()) {
                    continue
                }
                target.heal(1.0f + target.getMaxHealth() * 0.04f + this.level * 0.1f)
                healed = true
            }

            if (!healed) {
                continue
            }
            spawnHealParticles(target)
            consumeGrudge(50)
            healCount--
        }
    }

    private fun consumeGrudge(amount: Int) {
        val next = max(0, this.getStateMinor(6) - amount)
        this.setStateMinor(6, next)
    }

    private fun spawnHealParticles(target: LivingEntity) {
        if (this.level() !is ServerLevel) {
            return
        }
        val serverLevel = this.level() as ServerLevel
        val y = target.getY() + target.getBbHeight() * 0.6
        serverLevel.sendParticles<SimpleParticleType?>(
            ParticleTypes.HAPPY_VILLAGER, target.getX(), y, target.getZ(),
            4, 0.3, 0.2, 0.3, 0.01
        )
    }

    override val shipSpawnEggItem: Item?
        get() = ModItems.AIRFIELD_HIME_SPAWN_EGG.get()

    override fun hasShipMounts(): Boolean {
        return true
    }

    override fun summonMountEntity(): EntityMountBase {
        return EntityMountAfH(ModEntities.MOUNT_AF_H.get(), this.level())
    }

    companion object {
        const val EQUIP_HAND: String = "equip_hand"
        const val EQUIP_ARMOR: String = "equip_armor"
        const val EQUIP_POSE_1: String = "equip_pose_1"
        const val EQUIP_POSE_2: String = "equip_pose_2"

        @JvmStatic
        fun createAttributes(): AttributeSupplier.Builder {
            return createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 180.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, 9.0)
                .add(Attributes.FOLLOW_RANGE, 36.0)
                .add(Attributes.STEP_HEIGHT, 1.0)
        }
    }
}
