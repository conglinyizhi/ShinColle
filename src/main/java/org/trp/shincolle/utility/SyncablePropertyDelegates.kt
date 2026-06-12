package org.trp.shincolle.utility

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 返回一个布尔委托属性，初始值为 [initialValue]，仅当值发生变化时调用 [mark] 进行同步标记。
 */
fun syncableBoolean(initialValue: Boolean, mark: () -> Unit): ReadWriteProperty<Any?, Boolean> =
    object : ReadWriteProperty<Any?, Boolean> {
        private var value: Boolean = initialValue

        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean = value

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            if (this.value == value) return
            this.value = value
            mark()
        }
    }

/**
 * 返回一个初始值为 `false` 的布尔委托属性，仅当值发生变化时调用 [mark] 进行同步标记。
 */
fun syncableBoolean(mark: () -> Unit): ReadWriteProperty<Any?, Boolean> =
    syncableBoolean(false, mark)

/**
 * 返回一个整型委托属性，初始值为 [initialValue]，仅当值发生变化时调用 [mark] 进行同步标记。
 */
fun syncableInt(initialValue: Int, mark: () -> Unit): ReadWriteProperty<Any?, Int> =
    object : ReadWriteProperty<Any?, Int> {
        private var value: Int = initialValue

        override fun getValue(thisRef: Any?, property: KProperty<*>): Int = value

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            if (this.value == value) return
            this.value = value
            mark()
        }
    }

/**
 * 返回一个初始值为 `0` 的整型委托属性，仅当值发生变化时调用 [mark] 进行同步标记。
 */
fun syncableInt(mark: () -> Unit): ReadWriteProperty<Any?, Int> =
    syncableInt(0, mark)
