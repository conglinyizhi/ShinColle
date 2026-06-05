package org.trp.shincolle.entity.base

import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import java.util.*
import kotlin.math.max
import kotlin.math.min

class LegacyShipStats {
    private val bonus = ByteArray(6)
    private val raw = FloatArray(21)
    private val buffed = FloatArray(21)

    fun copyBonusFrom(data: ByteArray?) {
        if (data == null) {
            return
        }
        val len = min(this.bonus.size, data.size)
        System.arraycopy(data, 0, this.bonus, 0, len)
        for (i in 0..<len) {
            this.bonus[i] = Mth.clamp(this.bonus[i].toInt(), 0, MODERN_LIMIT).toByte()
        }
    }

    fun copyBonus(): ByteArray? {
        return this.bonus.clone()
    }

    fun getBonus(index: Int): Int {
        if (index < 0 || index >= this.bonus.size) {
            return 0
        }
        return this.bonus[index].toInt()
    }

    fun setBonus(index: Int, value: Int) {
        if (index < 0 || index >= this.bonus.size) {
            return
        }
        this.bonus[index] = Mth.clamp(value, 0, MODERN_LIMIT).toByte()
    }

    fun addBonusRandom(random: Random): Boolean {
        val pick = random.nextInt(this.bonus.size)
        if (this.bonus[pick] < MODERN_LIMIT) {
            this.bonus[pick]++
            return true
        }
        for (i in this.bonus.indices) {
            if (this.bonus[i] < MODERN_LIMIT) {
                this.bonus[i]++
                return true
            }
        }
        return false
    }

    fun hasBonusCapacity(): Boolean {
        for (value in this.bonus) {
            if (value < MODERN_LIMIT) {
                return true
            }
        }
        return false
    }

    fun recalculate(
        shipClass: Int,
        level: Int,
        equipBonuses: FloatArray?,
        formationBuffs: FloatArray?,
        moraleBuffs: FloatArray?
    ) {
        val base: FloatArray = SHIP_ATTR_MAP.getOrDefault(shipClass, DEFAULT_BASE)
        val type = floatArrayOf(base[6], base[7], base[8], base[9], base[10], base[11])

        val safeLevel = max(1, level).toFloat()

        zero(raw)
        zero(buffed)

        raw[0] = base[0] + (this.bonus[0] + 1.0f) * safeLevel * type[0]
        raw[5] = base[2] + (this.bonus[2] + 1.0f) * safeLevel * LV_SCALE_DEF * type[2]
        raw[6] = base[3] + (this.bonus[3] + 1.0f) * safeLevel * LV_SCALE_SPD * type[3]
        raw[7] = base[4] + (this.bonus[4] + 1.0f) * safeLevel * LV_SCALE_MOV * type[4]
        raw[8] = base[5] + (this.bonus[5] + 1.0f) * safeLevel * LV_SCALE_HIT * type[5]

        val baseAtk: Float = base[1] + (this.bonus[1] + 1.0f) * safeLevel * LV_SCALE_ATK * type[1]
        raw[1] = baseAtk
        raw[2] = baseAtk * 3.0f
        raw[3] = baseAtk
        raw[4] = baseAtk * 3.0f

        raw[16] = 1.0f
        raw[17] = 1.0f
        raw[18] = 1.0f
        raw[19] = 1.0f
        raw[20] = safeLevel * 0.005f

        if (equipBonuses != null) {
            val len = min(raw.size, equipBonuses.size)
            for (i in 0..<len) {
                raw[i] += equipBonuses[i]
            }
        }

        System.arraycopy(raw, 0, buffed, 0, raw.size)

        if (formationBuffs != null) {
            val len = min(buffed.size, formationBuffs.size)
            for (i in 0..<len) {
                if ((i >= 1 && i <= 6) || (i >= 9 && i <= 14)) {
                    buffed[i] *= formationBuffs[i]
                } else {
                    buffed[i] += formationBuffs[i]
                }
            }
        }

        if (moraleBuffs != null) {
            val len = min(buffed.size, moraleBuffs.size)
            for (i in 0..<len) {
                if ((i >= 1 && i <= 6) || (i >= 9 && i <= 14)) {
                    buffed[i] *= moraleBuffs[i]
                } else {
                    buffed[i] += moraleBuffs[i]
                }
            }
        }

        applyLimits(buffed)
    }

    val maxHealth: Float
        get() = buffed[0]

    val firepower: Float
        get() = buffed[1]

    val armor: Float
        get() = buffed[5]

    val reloadSpeed: Float
        get() = max(0.001f, buffed[6])

    val moveSpeed: Float
        get() = buffed[7]

    val attackRange: Float
        get() = buffed[8]

    fun getDefenseReducedDamage(incoming: Float, random: RandomSource): Float {
        var reduced = incoming * (1.0f - this.armor + (random.nextFloat() * 0.5f - 0.25f))
        if (reduced > 0.0f && reduced < 1.0f) {
            reduced = 1.0f
        } else if (reduced < 0.0f) {
            reduced = 0.0f
        }
        return reduced
    }

    val meleeDelay: Int
        get() = getAttackDelay(this.reloadSpeed, 0)

    val lightDelay: Int
        get() = getAttackDelay(this.reloadSpeed, 1)

    val heavyDelay: Int
        get() = getAttackDelay(this.reloadSpeed, 2)

    fun getBuffedAttr(index: Int): Float {
        if (index < 0 || index >= this.buffed.size) {
            return 0.0f
        }
        return this.buffed[index]
    }

    fun getRawAttr(index: Int): Float {
        if (index < 0 || index >= this.raw.size) {
            return 0.0f
        }
        return this.raw[index]
    }

    companion object {
        private const val LV_SCALE_DEF = 0.00133f
        private const val LV_SCALE_SPD = 0.004f
        private const val LV_SCALE_MOV = 0.002f
        private const val LV_SCALE_HIT = 0.02f
        private const val LV_SCALE_ATK = 0.133f

        private val DEFAULT_BASE =
            floatArrayOf(20.0f, 3.0f, 0.05f, 1.0f, 0.5f, 6.0f, 0.3f, 0.25f, 0.11f, 0.5f, 1.0f, 0.4f)

        private val BASE_ATTACK_SPEED = intArrayOf(40, 80, 120, 100, 100)
        private val FIXED_ATTACK_DELAY = intArrayOf(0, 20, 50, 35, 35)

        private val ATTR_LIMITS = floatArrayOf(
            -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.8f, 4.0f, 0.6f, 64.0f,
            0.9f, 0.9f, 0.9f, 0.9f, -1.0f, -1.0f, 0.75f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f
        )

        private const val MODERN_LIMIT = 3

        private val SHIP_ATTR_MAP: MutableMap<Int?, FloatArray> = HashMap<Int?, FloatArray>()

        init {
            SHIP_ATTR_MAP.put(
                -10,
                floatArrayOf(20.0f, 3.0f, 0.08f, 1.0f, 0.38f, 6.0f, 0.3f, 0.25f, 0.1f, 0.5f, 0.8f, 0.5f)
            )
            SHIP_ATTR_MAP.put(
                -11,
                floatArrayOf(40.0f, 6.0f, 0.2f, 1.0f, 0.3f, 8.0f, 0.6f, 0.45f, 0.2f, 0.65f, 0.6f, 0.6f)
            )
            SHIP_ATTR_MAP.put(
                -12,
                floatArrayOf(35.0f, 4.0f, 0.16f, 1.0f, 0.32f, 8.0f, 0.5f, 0.35f, 0.16f, 0.6f, 0.7f, 0.6f)
            )
            SHIP_ATTR_MAP.put(
                -13,
                floatArrayOf(25.0f, 4.0f, 0.1f, 1.0f, 0.4f, 6.0f, 0.35f, 0.35f, 0.1f, 0.6f, 0.8f, 0.5f)
            )
            SHIP_ATTR_MAP.put(
                -14,
                floatArrayOf(30.0f, 5.0f, 0.12f, 1.0f, 0.32f, 15.0f, 0.4f, 0.4f, 0.12f, 0.55f, 0.7f, 0.8f)
            )
            SHIP_ATTR_MAP.put(
                -15,
                floatArrayOf(20.0f, 3.0f, 0.08f, 1.0f, 0.5f, 5.0f, 0.3f, 0.3f, 0.08f, 0.55f, 1.0f, 0.45f)
            )
            SHIP_ATTR_MAP.put(
                -16,
                floatArrayOf(15.0f, 8.0f, 0.05f, 1.0f, 0.3f, 4.0f, 0.25f, 0.6f, 0.05f, 0.6f, 0.6f, 0.4f)
            )
            SHIP_ATTR_MAP.put(
                0,
                floatArrayOf(20.0f, 3.0f, 0.05f, 1.0f, 0.5f, 6.0f, 0.3f, 0.25f, 0.11f, 0.5f, 1.0f, 0.4f)
            )
            SHIP_ATTR_MAP.put(
                1,
                floatArrayOf(22.0f, 4.0f, 0.06f, 1.0f, 0.5f, 6.0f, 0.32f, 0.28f, 0.12f, 0.5f, 1.0f, 0.4f)
            )
            SHIP_ATTR_MAP.put(
                2,
                floatArrayOf(24.0f, 3.0f, 0.07f, 1.0f, 0.5f, 6.0f, 0.34f, 0.25f, 0.13f, 0.5f, 1.0f, 0.4f)
            )
            SHIP_ATTR_MAP.put(
                3,
                floatArrayOf(28.0f, 4.0f, 0.09f, 1.0f, 0.5f, 6.0f, 0.36f, 0.28f, 0.15f, 0.5f, 1.0f, 0.4f)
            )
            SHIP_ATTR_MAP.put(
                9,
                floatArrayOf(58.0f, 14.0f, 0.18f, 1.0f, 0.42f, 9.0f, 0.48f, 0.4f, 0.21f, 0.56f, 0.84f, 0.5f)
            )
            SHIP_ATTR_MAP.put(
                10,
                floatArrayOf(62.0f, 15.0f, 0.19f, 1.0f, 0.42f, 9.0f, 0.5f, 0.42f, 0.22f, 0.56f, 0.84f, 0.5f)
            )
            SHIP_ATTR_MAP.put(
                12,
                floatArrayOf(85.0f, 25.0f, 0.21f, 1.0f, 0.36f, 16.0f, 0.65f, 0.6f, 0.23f, 0.6f, 0.72f, 0.6f)
            )
            SHIP_ATTR_MAP.put(
                13,
                floatArrayOf(95.0f, 30.0f, 0.3f, 1.0f, 0.32f, 12.0f, 0.85f, 0.65f, 0.27f, 0.63f, 0.66f, 0.5f)
            )
            SHIP_ATTR_MAP.put(
                14,
                floatArrayOf(84.0f, 19.0f, 0.23f, 1.2f, 0.42f, 10.0f, 0.65f, 0.55f, 0.24f, 0.7f, 0.84f, 0.5f)
            )
            SHIP_ATTR_MAP.put(
                15,
                floatArrayOf(120.0f, 27.0f, 0.25f, 1.1f, 0.36f, 12.0f, 0.8f, 0.65f, 0.25f, 0.63f, 0.72f, 0.5f)
            )
            SHIP_ATTR_MAP.put(
                16,
                floatArrayOf(90.0f, 3.0f, 0.1f, 1.0f, 0.3f, 8.0f, 0.7f, 0.25f, 0.16f, 0.35f, 0.6f, 0.3f)
            )
            SHIP_ATTR_MAP.put(
                17,
                floatArrayOf(40.0f, 28.0f, 0.09f, 0.8f, 0.3f, 5.0f, 0.35f, 0.67f, 0.14f, 0.7f, 0.6f, 0.3f)
            )
            SHIP_ATTR_MAP.put(
                18,
                floatArrayOf(36.0f, 30.0f, 0.1f, 0.8f, 0.3f, 5.0f, 0.33f, 0.7f, 0.16f, 0.7f, 0.6f, 0.3f)
            )
            SHIP_ATTR_MAP.put(
                19,
                floatArrayOf(34.0f, 38.0f, 0.12f, 0.8f, 0.28f, 5.5f, 0.3f, 0.8f, 0.18f, 0.7f, 0.6f, 0.3f)
            )
            SHIP_ATTR_MAP.put(
                20,
                floatArrayOf(180.0f, 40.0f, 0.28f, 1.0f, 0.45f, 22.0f, 0.85f, 0.75f, 0.26f, 0.62f, 0.85f, 0.7f)
            )
            SHIP_ATTR_MAP.put(
                21,
                floatArrayOf(240.0f, 16.0f, 0.32f, 1.0f, 0.3f, 26.0f, 1.2f, 0.45f, 0.28f, 0.6f, 0.6f, 0.8f)
            )
            SHIP_ATTR_MAP.put(
                26,
                floatArrayOf(220.0f, 42.0f, 0.4f, 1.0f, 0.4f, 16.0f, 1.0f, 0.8f, 0.32f, 0.73f, 0.8f, 0.6f)
            )
            SHIP_ATTR_MAP.put(
                27,
                floatArrayOf(90.0f, 22.0f, 0.2f, 1.0f, 0.52f, 12.0f, 0.55f, 0.5f, 0.22f, 0.6f, 1.0f, 0.5f)
            )
            SHIP_ATTR_MAP.put(
                28,
                floatArrayOf(260.0f, 14.0f, 0.36f, 0.8f, 0.2f, 24.0f, 1.35f, 0.4f, 0.3f, 0.6f, 0.4f, 0.8f)
            )
            SHIP_ATTR_MAP.put(
                29,
                floatArrayOf(225.0f, 13.0f, 0.34f, 0.9f, 0.22f, 24.0f, 1.3f, 0.4f, 0.29f, 0.6f, 0.44f, 0.8f)
            )
            SHIP_ATTR_MAP.put(
                30,
                floatArrayOf(350.0f, 22.0f, 0.45f, 0.8f, 0.25f, 30.0f, 1.5f, 0.5f, 0.34f, 0.6f, 0.4f, 0.8f)
            )
            SHIP_ATTR_MAP.put(
                31,
                floatArrayOf(210.0f, 13.0f, 0.3f, 0.8f, 0.32f, 22.0f, 1.15f, 0.35f, 0.27f, 0.6f, 0.64f, 0.8f)
            )
            SHIP_ATTR_MAP.put(
                33,
                floatArrayOf(190.0f, 45.0f, 0.4f, 1.0f, 0.42f, 25.0f, 1.0f, 0.95f, 0.32f, 0.75f, 0.84f, 0.8f)
            )
            SHIP_ATTR_MAP.put(
                36,
                floatArrayOf(38.0f, 11.0f, 0.12f, 1.0f, 0.6f, 9.0f, 0.35f, 0.4f, 0.16f, 0.55f, 1.2f, 0.46f)
            )
            SHIP_ATTR_MAP.put(
                37,
                floatArrayOf(135.0f, 40.0f, 0.26f, 1.0f, 0.32f, 14.0f, 0.85f, 0.8f, 0.25f, 0.63f, 0.64f, 0.6f)
            )
            SHIP_ATTR_MAP.put(
                38,
                floatArrayOf(28.0f, 30.0f, 0.07f, 0.8f, 0.3f, 10.0f, 0.3f, 0.7f, 0.13f, 0.7f, 0.6f, 0.4f)
            )
            SHIP_ATTR_MAP.put(
                39,
                floatArrayOf(32.0f, 32.0f, 0.1f, 0.8f, 0.3f, 11.0f, 0.33f, 0.75f, 0.16f, 0.7f, 0.6f, 0.4f)
            )
            SHIP_ATTR_MAP.put(
                44,
                floatArrayOf(75.0f, 45.0f, 0.15f, 1.0f, 0.3f, 7.5f, 0.5f, 0.9f, 0.2f, 0.7f, 0.6f, 0.4f)
            )
            SHIP_ATTR_MAP.put(
                46,
                floatArrayOf(150.0f, 55.0f, 0.36f, 1.0f, 0.3f, 20.0f, 1.0f, 1.0f, 0.3f, 0.7f, 0.6f, 0.7f)
            )
            SHIP_ATTR_MAP.put(
                47,
                floatArrayOf(70.0f, 22.0f, 0.21f, 1.0f, 0.34f, 16.0f, 0.65f, 0.6f, 0.23f, 0.6f, 0.72f, 0.6f)
            )
            SHIP_ATTR_MAP.put(
                48,
                floatArrayOf(75.0f, 22.0f, 0.2f, 1.0f, 0.32f, 16.0f, 0.65f, 0.6f, 0.23f, 0.6f, 0.72f, 0.6f)
            )
            SHIP_ATTR_MAP.put(
                49,
                floatArrayOf(180.0f, 35.0f, 0.32f, 1.0f, 0.45f, 14.0f, 0.85f, 0.77f, 0.29f, 0.65f, 0.9f, 0.6f)
            )
            SHIP_ATTR_MAP.put(
                51,
                floatArrayOf(32.0f, 9.0f, 0.09f, 1.0f, 0.5f, 11.0f, 0.32f, 0.38f, 0.12f, 0.5f, 1.0f, 0.5f)
            )
            SHIP_ATTR_MAP.put(
                52,
                floatArrayOf(40.0f, 7.0f, 0.11f, 1.0f, 0.5f, 10.0f, 0.38f, 0.36f, 0.14f, 0.5f, 1.0f, 0.48f)
            )
            SHIP_ATTR_MAP.put(
                53,
                floatArrayOf(30.0f, 5.0f, 0.09f, 1.0f, 0.5f, 9.0f, 0.3f, 0.32f, 0.12f, 0.5f, 1.0f, 0.46f)
            )
            SHIP_ATTR_MAP.put(
                54,
                floatArrayOf(30.0f, 5.0f, 0.09f, 1.0f, 0.5f, 9.0f, 0.3f, 0.32f, 0.12f, 0.5f, 1.0f, 0.46f)
            )
            SHIP_ATTR_MAP.put(
                56,
                floatArrayOf(42.0f, 13.0f, 0.16f, 1.0f, 0.42f, 8.0f, 0.4f, 0.4f, 0.2f, 0.6f, 0.9f, 0.4f)
            )
            SHIP_ATTR_MAP.put(
                57,
                floatArrayOf(42.0f, 13.0f, 0.16f, 1.0f, 0.42f, 8.0f, 0.4f, 0.4f, 0.2f, 0.6f, 0.9f, 0.4f)
            )
            SHIP_ATTR_MAP.put(
                58,
                floatArrayOf(62.0f, 15.0f, 0.18f, 1.0f, 0.42f, 9.0f, 0.5f, 0.42f, 0.22f, 0.56f, 0.84f, 0.5f)
            )
            SHIP_ATTR_MAP.put(
                59,
                floatArrayOf(62.0f, 15.0f, 0.18f, 1.0f, 0.42f, 9.0f, 0.5f, 0.42f, 0.22f, 0.56f, 0.84f, 0.5f)
            )
            SHIP_ATTR_MAP.put(
                60,
                floatArrayOf(90.0f, 28.0f, 0.36f, 1.0f, 0.42f, 12.0f, 0.7f, 0.6f, 0.24f, 0.6f, 0.84f, 0.55f)
            )
            SHIP_ATTR_MAP.put(
                61,
                floatArrayOf(90.0f, 28.0f, 0.36f, 1.0f, 0.42f, 12.0f, 0.7f, 0.6f, 0.24f, 0.6f, 0.84f, 0.55f)
            )
            SHIP_ATTR_MAP.put(
                62,
                floatArrayOf(90.0f, 28.0f, 0.36f, 1.0f, 0.42f, 12.0f, 0.7f, 0.6f, 0.24f, 0.6f, 0.84f, 0.55f)
            )
            SHIP_ATTR_MAP.put(
                63,
                floatArrayOf(90.0f, 28.0f, 0.36f, 1.0f, 0.42f, 12.0f, 0.7f, 0.6f, 0.24f, 0.6f, 0.84f, 0.55f)
            )
            SHIP_ATTR_MAP.put(
                72,
                floatArrayOf(55.0f, 34.0f, 0.1f, 1.0f, 0.4f, 5.5f, 0.4f, 0.75f, 0.16f, 0.7f, 0.7f, 0.4f)
            )
        }

        private fun applyLimits(data: FloatArray) {
            var i = 0
            while (i < data.size && i < ATTR_LIMITS.size) {
                val limit: Float = ATTR_LIMITS[i]
                if (limit >= 0.0f && data[i] > limit) {
                    data[i] = limit
                }
                if (data[i] < 0.0f) {
                    data[i] = 0.0f
                }
                i++
            }

            if (data[0] < 1.0f) data[0] = 1.0f
            if (data[1] < 1.0f) data[1] = 1.0f
            if (data[2] < 1.0f) data[2] = 1.0f
            if (data[3] < 1.0f) data[3] = 1.0f
            if (data[4] < 1.0f) data[4] = 1.0f
            if (data[8] < 1.0f) data[8] = 1.0f
            if (data[6] < 0.2f) data[6] = 0.2f
        }

        private fun zero(data: FloatArray) {
            for (i in data.indices) {
                data[i] = 0.0f
            }
        }

        fun getAttackDelay(attackSpeed: Float, type: Int): Int {
            val safe = max(0.01f, attackSpeed)
            if (type >= 0 && type < BASE_ATTACK_SPEED.size) {
                return (BASE_ATTACK_SPEED[type] / safe).toInt() + FIXED_ATTACK_DELAY[type]
            }
            return 40
        }
    }
}
