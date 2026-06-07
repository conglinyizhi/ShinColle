package org.trp.shincolle.api.event

import net.minecraft.world.entity.LivingEntity
import net.neoforged.bus.api.Event
import net.neoforged.bus.api.ICancellableEvent
import org.trp.shincolle.entity.base.EntityShipBase

/**
 * 舰娘攻击事件。
 *
 * 在舰娘执行攻击（轻攻击、重攻击、舰载机出击）时触发。
 * 第三方可通过 [net.neoforged.bus.api.SubscribeEvent] 监听此事件以修改伤害、取消攻击或附加效果。
 */
abstract class ShipAttackEvent(
    val ship: EntityShipBase,
    val target: LivingEntity?,
    val attackType: AttackType,
    var damage: Float
) : Event() {

    enum class AttackType {
        LIGHT,
        HEAVY,
        AIRCRAFT
    }

    /**
     * 攻击前事件。可取消，可修改伤害。
     */
    class Pre(ship: EntityShipBase, target: LivingEntity?, attackType: AttackType, damage: Float) :
        ShipAttackEvent(ship, target, attackType, damage), ICancellableEvent

    /**
     * 攻击后事件。仅用于通知，不可取消。
     */
    class Post(ship: EntityShipBase, target: LivingEntity?, attackType: AttackType, damage: Float) :
        ShipAttackEvent(ship, target, attackType, damage)
}
