package org.trp.shincolle.api.entity

import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.LivingEntity
import org.trp.shincolle.entity.base.EntityShipBase

/**
 * 舰娘攻击效果钩子接口。
 *
 * 实现此接口的 [EntityShipBase] 子类可以自定义攻击时的声音与粒子效果。
 * 第三方 Addon 若创建自定义舰娘实体，建议实现此接口以提供个性化的攻击表现。
 */
interface IShipAttackEffect {

    /**
     * 轻攻击时播放的自定义声音。
     *
     * @return 要播放的 [SoundEvent]，返回 null 则使用默认声音
     */
    fun onLightAttackSound(ship: EntityShipBase, target: LivingEntity?): SoundEvent?

    /**
     * 重攻击时播放的自定义声音。
     *
     * @return 要播放的 [SoundEvent]，返回 null 则使用默认声音
     */
    fun onHeavyAttackSound(ship: EntityShipBase, target: LivingEntity?): SoundEvent?

    /**
     * 轻攻击时的自定义粒子效果。
     *
     * @return true 表示已自行处理粒子效果，不再播放默认粒子；false 则继续播放默认粒子
     */
    fun onLightAttackParticles(ship: EntityShipBase, target: LivingEntity?): Boolean

    /**
     * 重攻击时的自定义粒子效果。
     *
     * @return true 表示已自行处理粒子效果，不再播放默认粒子；false 则继续播放默认粒子
     */
    fun onHeavyAttackParticles(ship: EntityShipBase, target: LivingEntity?): Boolean
}
