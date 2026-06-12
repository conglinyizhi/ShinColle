package org.trp.shincolle.menu

import net.minecraft.world.inventory.DataSlot

/**
 * 创建基于 getter/setter 的布尔 DataSlot。
 *
 * @param getter 返回值应为 0 或 1
 * @param setter 接收 0 或 1
 */
fun booleanSlot(getter: () -> Int, setter: (Int) -> Unit): DataSlot =
    object : DataSlot() {
        override fun get(): Int = getter()
        override fun set(value: Int) = setter(value)
    }

/**
 * 创建基于 getter/setter 的整型 DataSlot。
 */
fun intSlot(getter: () -> Int, setter: (Int) -> Unit): DataSlot =
    object : DataSlot() {
        override fun get(): Int = getter()
        override fun set(value: Int) = setter(value)
    }

/**
 * 创建将 32 位整数拆分为高 16 位和低 16 位的 DataSlot 对。
 *
 * 返回的 Pair 为 `(lowSlot, highSlot)`，可按原有注册顺序解构为两个 DataSlot。
 */
fun splitIntSlot(
    highGetter: () -> Int,
    highSetter: (Int) -> Unit,
    lowGetter: () -> Int,
    lowSetter: (Int) -> Unit,
): Pair<DataSlot, DataSlot> {
    val lowSlot = object : DataSlot() {
        override fun get(): Int = lowGetter()
        override fun set(value: Int) = lowSetter(value)
    }
    val highSlot = object : DataSlot() {
        override fun get(): Int = highGetter()
        override fun set(value: Int) = highSetter(value)
    }
    return lowSlot to highSlot
}
