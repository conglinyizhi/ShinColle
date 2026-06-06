package org.trp.shincolle.api.equip

/**
 * 舰娘装备特殊效果注册表。
 *
 * 第三方 Addon 在模组初始化时（如 `FMLCommonSetupEvent`）调用 [register] 注册自定义效果。
 *
 * **注意：** 注册应在服务端和客户端都执行，或放在 common setup 中。
 */
object ShipEquipRegistry {

    private val effects: MutableMap<Int, ShipEquipSpecialEffect> = mutableMapOf()

    /**
     * 注册一个装备特殊效果处理器。
     *
     * 若同一类型 ID 已注册，后注册的效果会覆盖先前的。
     */
    @JvmStatic
    fun register(effect: ShipEquipSpecialEffect) {
        effects[effect.getEquipTypeId()] = effect
    }

    /**
     * 获取指定装备类型 ID 的效果处理器。
     *
     * @return 对应的效果处理器，若未注册则返回 null。
     */
    @JvmStatic
    fun getEffect(typeId: Int): ShipEquipSpecialEffect? = effects[typeId]

    /**
     * 检查指定装备类型 ID 是否已注册效果处理器。
     */
    @JvmStatic
    fun hasEffect(typeId: Int): Boolean = typeId in effects
}
