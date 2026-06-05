package org.trp.shincolle.crafting

import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidUtil
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import org.trp.shincolle.init.ModEntities
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.item.LegacyEquipItem
import org.trp.shincolle.item.LegacyEquipStats.allMiscAttrs
import org.trp.shincolle.item.RandomShipSpawnEggItem
import org.trp.shincolle.item.ShipClass
import org.trp.shincolle.item.ShipSpawnEggItem
import java.util.List
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer
import java.util.function.UnaryOperator
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

object ShipyardRecipes {
    private const val SMALL_BASE_POWER = 57600
    private const val SMALL_POWER_PER_MAT = 2100
    private const val SMALL_MIN_EACH_MAT = 16
    private const val SMALL_BASE_TOTAL = 64

    private const val LARGE_BASE_POWER = 460800
    private const val LARGE_POWER_PER_MAT = 256
    private const val LARGE_MIN_EACH_MAT = 100
    private const val LARGE_BASE_TOTAL = 400

    private const val NORMAL_FLOOR = 0.2f
    private const val SMALL_MATS_SCALE = 15.625f
    private const val SMALL_EQUIP_RATE_DENOMINATOR = 128.0f
    private const val MIN_RANDOM_THRESHOLD = 0.0125f

    private const val LAVA_FUEL_MB = 1000
    private const val LAVA_FUEL_VALUE = 20000

    private val SMALL_SHIP_CANDIDATES: MutableList<Candidate> = List.of<Candidate>(
        Candidate(0, 80, 0),
        Candidate(1, 90, 0),
        Candidate(2, 100, 0),
        Candidate(3, 110, 0),
        Candidate(16, 120, 1),
        Candidate(17, 140, 2),
        Candidate(18, 160, 2),
        Candidate(19, 180, 2),
        Candidate(9, 200, 2),
        Candidate(10, 256, 2)
    )

    private val LARGE_SHIP_CANDIDATES: MutableList<Candidate> = List.of<Candidate>(
        Candidate(27, 500, 0),
        Candidate(12, 650, 3),
        Candidate(14, 800, 2),
        Candidate(13, 800, 2),
        Candidate(49, 2000, 2),
        Candidate(31, 2600, 1),
        Candidate(72, 2600, 2),
        Candidate(29, 2700, 1),
        Candidate(28, 2800, 1),
        Candidate(21, 3000, 1),
        Candidate(20, 3000, 3),
        Candidate(44, 3500, 2),
        Candidate(15, 3800, 2),
        Candidate(26, 4600, 2),
        Candidate(30, 4800, 1),
        Candidate(33, 5000, 3)
    )

    private val SMALL_EQUIP_TYPE_CANDIDATES: MutableList<Candidate> = List.of<Candidate>(
        Candidate(18, 80, 1),
        Candidate(26, 80, 2),
        Candidate(27, 80, 0),
        Candidate(25, 90, 0),
        Candidate(20, 100, 2),
        Candidate(24, 120, 1),
        Candidate(28, 120, 2),
        Candidate(0, 128, 2),
        Candidate(4, 160, 2),
        Candidate(14, 200, 0),
        Candidate(12, 256, 3),
        Candidate(1, 320, 2)
    )

    private val LARGE_EQUIP_TYPE_CANDIDATES: MutableList<Candidate> = List.of<Candidate>(
        Candidate(19, 500, 1),
        Candidate(21, 800, 2),
        Candidate(29, 1000, 2),
        Candidate(13, 1000, 3),
        Candidate(5, 1200, 2),
        Candidate(16, 1400, 0),
        Candidate(2, 1600, 2),
        Candidate(15, 2000, 0),
        Candidate(6, 2400, 3),
        Candidate(8, 2400, 3),
        Candidate(10, 2400, 3),
        Candidate(22, 2800, 3),
        Candidate(17, 3200, 0),
        Candidate(7, 3800, 3),
        Candidate(9, 3800, 3),
        Candidate(11, 3800, 3),
        Candidate(3, 4400, 2),
        Candidate(23, 5000, 3)
    )

    fun getSmallMaterialType(stack: ItemStack): Int {
        if (stack.isEmpty()) {
            return -1
        }
        if (stack.`is`(ModItems.GRUDGE.get())) {
            return 0
        }
        if (stack.`is`(ModItems.ABYSS_METAL.get())) {
            return 1
        }
        if (stack.`is`(ModItems.AMMO_LIGHT.get()) || stack.`is`(ModItems.AMMO_LIGHT_CONTAINER.get())
            || stack.`is`(ModItems.AMMO_HEAVY.get()) || stack.`is`(ModItems.AMMO_HEAVY_CONTAINER.get())
        ) {
            return 2
        }
        if (stack.`is`(ModItems.ABYSS_POLYMETAL.get())) {
            return 3
        }
        if (isFuel(stack)) {
            return 4
        }
        return -1
    }

    fun isFuel(stack: ItemStack): Boolean {
        return getFuelValue(stack) > 0
    }

    fun getFuelValue(stack: ItemStack): Int {
        if (stack.isEmpty()) {
            return 0
        }
        if (stack.`is`(Items.LAVA_BUCKET)) {
            return getScaledFuelValue(LAVA_FUEL_VALUE)
        }
        val burn = stack.getBurnTime(RecipeType.SMELTING)
        if (burn > 0) {
            return getScaledFuelValue(burn)
        }
        return if (canDrainLavaFuel(stack)) getScaledFuelValue(LAVA_FUEL_VALUE) else 0
    }

    @JvmStatic
    fun getFuelValue(stack: ItemStack, magnification: Float): Int {
        if (stack.isEmpty()) {
            return 0
        }
        if (stack.`is`(Items.LAVA_BUCKET)) {
            return getScaledFuelValue(LAVA_FUEL_VALUE, magnification)
        }
        val burn = stack.getBurnTime(RecipeType.SMELTING)
        if (burn > 0) {
            return getScaledFuelValue(burn, magnification)
        }
        return if (canDrainLavaFuel(stack)) getScaledFuelValue(LAVA_FUEL_VALUE, magnification) else 0
    }

    @JvmStatic
    fun consumeOneFuel(stack: ItemStack): ItemStack {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY
        }

        if (stack.`is`(Items.LAVA_BUCKET)) {
            return ItemStack(Items.BUCKET)
        }

        if (stack.getCount() == 1) {
            val handlerOptional = FluidUtil.getFluidHandler(stack.copyWithCount(1))
            if (handlerOptional.isPresent()) {
                val handler = handlerOptional.get()
                val drained = handler.drain(FluidStack(Fluids.LAVA, LAVA_FUEL_MB), IFluidHandler.FluidAction.EXECUTE)
                if (isLavaFuelStack(drained)) {
                    return handler.getContainer()
                }
            }
        }

        val remaining = stack.copy()
        remaining.shrink(1)
        return remaining
    }

    private fun canDrainLavaFuel(stack: ItemStack): Boolean {
        if (stack.isEmpty() || stack.getCount() != 1) {
            return false
        }

        val handlerOptional = FluidUtil.getFluidHandler(stack.copyWithCount(1))
        if (handlerOptional.isEmpty()) {
            return false
        }

        val handler = handlerOptional.get()
        val drained = handler.drain(FluidStack(Fluids.LAVA, LAVA_FUEL_MB), IFluidHandler.FluidAction.SIMULATE)
        return isLavaFuelStack(drained)
    }

    private fun isLavaFuelStack(stack: FluidStack): Boolean {
        return !stack.isEmpty() && stack.getAmount() == LAVA_FUEL_MB && (stack.getFluid() === Fluids.LAVA || stack.getFluid() === Fluids.FLOWING_LAVA)
    }

    private fun getScaledFuelValue(baseValue: Int): Int {
        return getScaledFuelValue(baseValue, 1.0f)
    }

    private fun getScaledFuelValue(baseValue: Int, magnification: Float): Int {
        val safeMagnification = max(0.0f, magnification)
        return max(0, Math.round(baseValue * safeMagnification))
    }

    @JvmStatic
    fun canSmallBuild(mats: IntArray): Boolean {
        return mats[0] >= SMALL_MIN_EACH_MAT && mats[1] >= SMALL_MIN_EACH_MAT && mats[2] >= SMALL_MIN_EACH_MAT && mats[3] >= SMALL_MIN_EACH_MAT
    }

    @JvmStatic
    fun calcSmallGoalPower(mats: IntArray): Int {
        if (!canSmallBuild(mats)) {
            return 0
        }
        val total = mats[0] + mats[1] + mats[2] + mats[3]
        return SMALL_BASE_POWER + max(0, total - SMALL_BASE_TOTAL) * SMALL_POWER_PER_MAT
    }

    fun canLargeBuild(mats: IntArray): Boolean {
        return mats[0] >= LARGE_MIN_EACH_MAT && mats[1] >= LARGE_MIN_EACH_MAT && mats[2] >= LARGE_MIN_EACH_MAT && mats[3] >= LARGE_MIN_EACH_MAT
    }

    @JvmStatic
    fun calcLargeGoalPower(mats: IntArray): Int {
        if (!canLargeBuild(mats)) {
            return 0
        }
        val total = mats[0] + mats[1] + mats[2] + mats[3]
        return LARGE_BASE_POWER + max(0, total - LARGE_BASE_TOTAL) * LARGE_POWER_PER_MAT
    }

    @JvmStatic
    fun createSmallShipResult(mats: IntArray): ItemStack {
        val result = ItemStack(ModItems.SHIPSPAWNEGGS.get())
        putShipyardMatsTag(result, mats, false)
        return result
    }

    @JvmStatic
    fun createLargeShipResult(mats: IntArray): ItemStack {
        val result = ItemStack(ModItems.SHIPSPAWNEGGL.get())
        putShipyardMatsTag(result, mats, true)
        return result
    }

    @JvmStatic
    fun createSmallEquipResult(mats: IntArray): ItemStack {
        val totalMats = sumMats(mats)
        val equipRate = min(totalMats / SMALL_EQUIP_RATE_DENOMINATOR, 1.0f)

        if (ThreadLocalRandom.current().nextFloat() < equipRate) {
            val rollType = rollEquipType(false, mats)
            val equip = rollEquipOfType(rollType, totalMats, false)
            if (!equip.isEmpty()) {
                return equip
            }
        }

        if (ThreadLocalRandom.current().nextBoolean()) {
            return ItemStack(ModItems.AMMO_LIGHT_CONTAINER.get(), 11 + ThreadLocalRandom.current().nextInt(11))
        }
        return ItemStack(ModItems.AMMO_HEAVY_CONTAINER.get(), 2 + ThreadLocalRandom.current().nextInt(2))
    }

    @JvmStatic
    fun createLargeEquipResult(mats: IntArray): ItemStack {
        val totalMats = sumMats(mats)
        val rollType = rollEquipType(true, mats)
        val equip = rollEquipOfType(rollType, totalMats, true)
        if (!equip.isEmpty()) {
            return equip
        }

        val fallback: Item = ModItems.EQUIP_CANNON.get()
        if (fallback is LegacyEquipItem) {
            return fallback.createVariantStack(0)
        }
        return ItemStack(fallback)
    }

    fun rollShipEntityType(largeShipyard: Boolean, stack: ItemStack): EntityType<out Mob?> {
        var mats = getShipyardMatsTag(stack)
        if (mats == null) {
            mats = intArrayOf(0, 0, 0, 0)
        }

        val type = rollShipType(largeShipyard, mats)
        return getShipEntityTypeForType(type, largeShipyard)
    }

    @JvmStatic
    fun addLargeMaterialStock(matStock: IntArray, stack: ItemStack): Boolean {
        if (stack.isEmpty()) {
            return false
        }

        val heavyGrudgeMats = getHeavyGrudgeMatsTag(stack)
        if (heavyGrudgeMats != null) {
            matStock[0] += 81 + heavyGrudgeMats[0]
            matStock[1] += heavyGrudgeMats[1]
            matStock[2] += heavyGrudgeMats[2]
            matStock[3] += heavyGrudgeMats[3]
            return true
        }

        val taggedMats = getShipyardMatsTag(stack)
        if (taggedMats != null) {
            for (i in 0..3) {
                matStock[i] += taggedMats[i]
            }
            return true
        }

        val randomEgg = stack.getItem()
        if (randomEgg is RandomShipSpawnEggItem) {
            addShipRecycleMats(
                matStock, randomEgg.shipClass,
                randomEgg == ModItems.SHIPSPAWNEGGL.get()
            )
            return true
        }

        val shipEgg = stack.getItem()
        if (shipEgg is ShipSpawnEggItem) {
            addShipRecycleMats(matStock, shipEgg.shipClass, false)
            return true
        }

        if (stack.`is`(ModItems.GRUDGE.get())) {
            matStock[0] += 1
            return true
        }
        if (stack.`is`(ModItems.ABYSS_METAL.get())) {
            matStock[1] += 1
            return true
        }
        if (stack.`is`(ModItems.AMMO_LIGHT.get()) || stack.`is`(ModItems.AMMO_HEAVY.get())) {
            matStock[2] += 1
            return true
        }
        if (stack.`is`(ModItems.AMMO_LIGHT_CONTAINER.get()) || stack.`is`(ModItems.AMMO_HEAVY_CONTAINER.get())) {
            matStock[2] += 9
            return true
        }
        if (stack.`is`(ModItems.ABYSS_POLYMETAL.get())) {
            matStock[3] += 1
            return true
        }
        if (stack.`is`(ModItems.GRUDGE_BLOCK.get())) {
            matStock[0] += 9
            return true
        }
        if (stack.`is`(ModItems.ABYSSIUM.get())) {
            matStock[1] += 9
            return true
        }
        if (stack.`is`(ModItems.POLYMETAL.get())) {
            matStock[3] += 9
            return true
        }
        if (stack.`is`(ModItems.GRUDGE_HEAVY_BLOCK.get())) {
            matStock[0] += 81
            return true
        }
        return false
    }

    @JvmStatic
    fun createLargeOutputMaterial(selectMat: Int, compressed: Boolean): ItemStack {
        return when (selectMat) {
            0 -> ItemStack(if (compressed) ModItems.GRUDGE_BLOCK.get() else ModItems.GRUDGE.get(), 1)
            1 -> ItemStack(if (compressed) ModItems.ABYSSIUM.get() else ModItems.ABYSS_METAL.get(), 1)
            2 -> ItemStack(if (compressed) ModItems.AMMO_LIGHT_CONTAINER.get() else ModItems.AMMO_LIGHT.get(), 1)
            3 -> ItemStack(if (compressed) ModItems.POLYMETAL.get() else ModItems.ABYSS_POLYMETAL.get(), 1)
            else -> ItemStack.EMPTY
        }
    }

    @JvmStatic
    fun moveBuildMaterialAmount(matBuild: IntArray, matStock: IntArray, matType: Int, value: Int) {
        if (matType < 0 || matType >= 4) {
            return
        }

        val amounts = intArrayOf(1000, 100, 10, 1)
        var step = amounts[value % 4]
        val stockToBuild = value <= 3
        if (stockToBuild) {
            step = min(step, matStock[matType])
            step = min(step, 1000 - matBuild[matType])
            matStock[matType] -= step
            matBuild[matType] += step
        } else {
            step = min(step, matBuild[matType])
            matBuild[matType] -= step
            matStock[matType] += step
        }
    }

    fun getCurrentSmallMaterialAmount(stacks: Array<ItemStack?>): IntArray {
        val mats = IntArray(4)
        for (i in 0..3) {
            mats[i] = if (stacks[i]!!.isEmpty()) 0 else stacks[i]!!.getCount()
        }
        return mats
    }

    fun putShipyardMatsTag(stack: ItemStack, mats: IntArray, large: Boolean) {
        if (stack.isEmpty()) {
            return
        }

        val data = intArrayOf(mats[0], mats[1], mats[2], mats[3])
        stack.update<CustomData?>(
            DataComponents.CUSTOM_DATA,
            CustomData.EMPTY,
            UnaryOperator { customData: CustomData? ->
                customData!!.update(
                    Consumer { tag: CompoundTag? ->
                        tag!!.putIntArray("ShipyardMats", data)
                        tag.putBoolean("LargeShipyard", large)
                    })
            })
    }

    fun getShipyardMatsTag(stack: ItemStack): IntArray? {
        val customData = stack.get<CustomData?>(DataComponents.CUSTOM_DATA)
        if (customData == null) {
            return null
        }
        val tag = customData.copyTag()
        if (!tag.contains("ShipyardMats", Tag.TAG_INT_ARRAY.toInt())) {
            return null
        }
        val mats = tag.getIntArray("ShipyardMats")
        if (mats.size < 4) {
            return null
        }
        return intArrayOf(mats[0], mats[1], mats[2], mats[3])
    }

    @JvmStatic
    fun putHeavyGrudgeStorageTag(stack: ItemStack, mats: IntArray, fuel: Int) {
        if (stack.isEmpty()) {
            return
        }

        val data = intArrayOf(mats[0], mats[1], mats[2], mats[3])
        stack.update<CustomData?>(
            DataComponents.CUSTOM_DATA,
            CustomData.EMPTY,
            UnaryOperator { customData: CustomData? ->
                customData!!.update(
                    Consumer { tag: CompoundTag? ->
                        tag!!.putIntArray("HeavyGrudgeMats", data)
                        tag.putInt("HeavyGrudgeFuel", max(0, fuel))
                    })
            })
    }

    fun getHeavyGrudgeMatsTag(stack: ItemStack): IntArray? {
        val customData = stack.get<CustomData?>(DataComponents.CUSTOM_DATA)
        if (customData == null) {
            return null
        }

        val tag = customData.copyTag()
        if (tag.contains("HeavyGrudgeMats", Tag.TAG_INT_ARRAY.toInt())) {
            val mats = tag.getIntArray("HeavyGrudgeMats")
            if (mats.size >= 4) {
                return intArrayOf(mats[0], mats[1], mats[2], mats[3])
            }
        }

        return null
    }

    fun getHeavyGrudgeFuelTag(stack: ItemStack): Int {
        val customData = stack.get<CustomData?>(DataComponents.CUSTOM_DATA)
        if (customData == null) {
            return 0
        }

        val tag = customData.copyTag()
        return max(0, tag.getInt("HeavyGrudgeFuel"))
    }

    private fun getShipEntityTypeForType(type: Int, largeShipyard: Boolean): EntityType<out Mob?> {
        return when (type) {
            0 -> ModEntities.DESTROYER_I.get()
            1 -> ModEntities.DESTROYER_RO.get()
            2 -> ModEntities.DESTROYER_HA.get()
            3 -> ModEntities.DESTROYER_NI.get()
            9 -> ModEntities.HEAVY_CRUISER_RI.get()
            10 -> ModEntities.HEAVY_CRUISER_NE.get()
            12 -> ModEntities.CARRIER_WO.get()
            13 -> ModEntities.BATTLESHIP_RU.get()
            14 -> ModEntities.BATTLESHIP_TA.get()
            15 -> ModEntities.BATTLESHIP_RE.get()
            16 -> ModEntities.TRANSPORT_WA.get()
            17 -> ModEntities.SUBM_KA.get()
            18 -> ModEntities.SUBM_YO.get()
            19 -> ModEntities.SUBM_SO.get()
            20 -> ModEntities.CARRIER_HIME.get()
            21 -> ModEntities.AIRFIELD_HIME.get()
            26 -> ModEntities.BATTLESHIP_HIME.get()
            27 -> ModEntities.DESTROYER_HIME.get()
            28 -> ModEntities.HARBOUR_HIME.get()
            29 -> ModEntities.ISOLATED_HIME.get()
            30 -> ModEntities.MIDWAY_HIME.get()
            31 -> ModEntities.NORTHERN_HIME.get()
            33 -> ModEntities.CARRIER_W_DEMON.get()
            44 -> ModEntities.SUBM_HIME.get()
            49 -> ModEntities.CA_HIME.get()
            72 -> ModEntities.SSNH.get()
            else -> if (largeShipyard) ModEntities.DESTROYER_HIME.get() else ModEntities.DESTROYER_I.get()
        }!!
    }

    private fun getShipEggForType(type: Int, largeShipyard: Boolean): Item? {
        return when (type) {
            0 -> ModItems.DESTROYER_I_SPAWN_EGG.get()
            1 -> ModItems.DESTROYER_RO_SPAWN_EGG.get()
            2 -> ModItems.DESTROYER_HA_SPAWN_EGG.get()
            3 -> ModItems.DESTROYER_NI_SPAWN_EGG.get()
            9 -> ModItems.HEAVY_CRUISER_RI_SPAWN_EGG.get()
            10 -> ModItems.HEAVY_CRUISER_NE_SPAWN_EGG.get()
            12 -> ModItems.CARRIER_WO_SPAWN_EGG.get()
            13 -> ModItems.BATTLESHIP_RU_SPAWN_EGG.get()
            14 -> ModItems.BATTLESHIP_TA_SPAWN_EGG.get()
            15 -> ModItems.BATTLESHIP_RE_SPAWN_EGG.get()
            16 -> ModItems.TRANSPORT_WA_SPAWN_EGG.get()
            17 -> ModItems.SUBM_KA_SPAWN_EGG.get()
            18 -> ModItems.SUBM_YO_SPAWN_EGG.get()
            19 -> ModItems.SUBM_SO_SPAWN_EGG.get()
            20 -> ModItems.CARRIER_HIME_SPAWN_EGG.get()
            21 -> ModItems.AIRFIELD_HIME_SPAWN_EGG.get()
            26 -> ModItems.BATTLESHIP_HIME_SPAWN_EGG.get()
            27 -> ModItems.DESTROYER_HIME_SPAWN_EGG.get()
            28 -> ModItems.HARBOUR_HIME_SPAWN_EGG.get()
            29 -> ModItems.ISOLATED_HIME_SPAWN_EGG.get()
            30 -> ModItems.MIDWAY_HIME_SPAWN_EGG.get()
            31 -> ModItems.NORTHERN_HIME_SPAWN_EGG.get()
            33 -> ModItems.CARRIER_W_DEMON_SPAWN_EGG.get()
            44 -> ModItems.SUBM_HIME_SPAWN_EGG.get()
            49 -> ModItems.CA_HIME_SPAWN_EGG.get()
            72 -> ModItems.SSNH_SPAWN_EGG.get()
            else -> if (largeShipyard) ModItems.DESTROYER_HIME_SPAWN_EGG.get() else ModItems.DESTROYER_I_SPAWN_EGG.get()
        }
    }

    private fun rollShipType(largeShipyard: Boolean, mats: IntArray): Int {
        val candidates = if (largeShipyard) LARGE_SHIP_CANDIDATES else SMALL_SHIP_CANDIDATES
        val totalMats = sumMats(mats)

        val probs = FloatArray(candidates.size)
        var totalProb = 0.0f
        for (i in candidates.indices) {
            val candidate = candidates.get(i)
            val meanNew = if (candidate.preferredMaterial >= 0 && candidate.preferredMaterial <= 3)
                candidate.mean - mats[candidate.preferredMaterial]
            else
                candidate.mean
            var meanDist = abs(totalMats - meanNew)
            if (!largeShipyard) {
                meanDist = (meanDist * SMALL_MATS_SCALE).toInt()
            }
            val prob = getNormDist(meanDist)
            probs[i] = prob
            totalProb += prob
        }

        if (totalProb <= 0.0f) {
            return candidates.first().id
        }

        val random = ThreadLocalRandom.current().nextFloat() * totalProb
        var sum = MIN_RANDOM_THRESHOLD
        for (i in probs.indices) {
            sum += probs[i]
            if (sum > random) {
                return candidates.get(i).id
            }
        }
        return candidates.last().id
    }

    private fun rollEquipType(largeShipyard: Boolean, mats: IntArray): Int {
        val candidates = if (largeShipyard) LARGE_EQUIP_TYPE_CANDIDATES else SMALL_EQUIP_TYPE_CANDIDATES
        val totalMats = sumMats(mats)

        val probs = FloatArray(candidates.size)
        var totalProb = 0.0f
        for (i in candidates.indices) {
            val candidate = candidates.get(i)
            val meanNew = if (candidate.preferredMaterial >= 0 && candidate.preferredMaterial <= 3)
                candidate.mean - mats[candidate.preferredMaterial]
            else
                candidate.mean
            var meanDist = abs(totalMats - meanNew)
            if (!largeShipyard) {
                meanDist = (meanDist * SMALL_MATS_SCALE).toInt()
            }

            val prob = getNormDist(meanDist)
            probs[i] = prob
            totalProb += prob
        }

        if (totalProb <= 0.0f) {
            return -1
        }

        val random = ThreadLocalRandom.current().nextFloat() * totalProb
        var sum = MIN_RANDOM_THRESHOLD
        for (i in probs.indices) {
            sum += probs[i]
            if (sum > random) {
                return candidates.get(i).id
            }
        }

        return candidates.last().id
    }

    private fun rollEquipOfType(type: Int, totalMats: Int, largeShipyard: Boolean): ItemStack {
        if (type < 0) {
            return ItemStack.EMPTY
        }

        val scaledMats = if (largeShipyard) totalMats else (totalMats * SMALL_MATS_SCALE).toInt()
        val miscAttrs = allMiscAttrs
        val equipIds = IntArray(miscAttrs.size)
        val probs = FloatArray(miscAttrs.size)

        var count = 0
        var totalProb = 0.0f
        for (entry in miscAttrs.entries) {
            val equipId: Int = entry.key!!
            val misc: IntArray = entry.value!!
            if (misc.size < 3 || misc[1] != type) {
                continue
            }

            val meanDist = abs(scaledMats - misc[2])
            val prob = getNormDist(meanDist)
            equipIds[count] = equipId
            probs[count] = prob
            totalProb += prob
            count++
        }

        if (count == 0 || totalProb <= 0.0f) {
            return ItemStack.EMPTY
        }

        val random = ThreadLocalRandom.current().nextFloat() * totalProb
        var sum = 0.0f
        for (i in 0..<count) {
            sum += probs[i]
            if (sum > random) {
                return createEquipStackFromEquipId(equipIds[i])
            }
        }

        return createEquipStackFromEquipId(equipIds[count - 1])
    }

    private fun createEquipStackFromEquipId(equipId: Int): ItemStack {
        if (equipId < 0) {
            return ItemStack.EMPTY
        }

        val itemType = equipId % 100
        val variant = equipId / 100
        val equipItem = resolveEquipItemByType(itemType)
        if (equipItem == null) {
            return ItemStack.EMPTY
        }

        if (equipItem is LegacyEquipItem) {
            return equipItem.createVariantStack(variant)
        }

        return ItemStack(equipItem)
    }

    private fun resolveEquipItemByType(itemType: Int): Item? {
        return when (itemType) {
            0, 1, 2, 3 -> ModItems.EQUIP_CANNON.get()
            4, 5 -> ModItems.EQUIP_TORPEDO.get()
            6, 7, 8, 9, 10, 11, 12, 13 -> ModItems.EQUIP_AIRPLANE.get()
            14, 15 -> ModItems.EQUIP_RADAR.get()
            16, 17 -> ModItems.EQUIP_TURBINE.get()
            18, 19 -> ModItems.EQUIP_ARMOR.get()
            20, 21 -> ModItems.EQUIP_MACHINEGUN.get()
            22, 23 -> ModItems.EQUIP_CATAPULT.get()
            24 -> ModItems.EQUIP_DRUM.get()
            25 -> ModItems.EQUIP_COMPASS.get()
            26 -> ModItems.EQUIP_FLARE.get()
            27 -> ModItems.EQUIP_SEARCHLIGHT.get()
            28, 29 -> ModItems.EQUIP_AMMO.get()
            else -> null
        }
    }

    private fun sumMats(mats: IntArray): Int {
        return mats[0] + mats[1] + mats[2] + mats[3]
    }

    private fun addShipRecycleMats(matStock: IntArray, shipClass: ShipClass?, largeRandomEgg: Boolean) {
        val mats: IntArray?

        if (largeRandomEgg) {
            mats = intArrayOf(
                90 + ThreadLocalRandom.current().nextInt(8),
                90 + ThreadLocalRandom.current().nextInt(8),
                90 + ThreadLocalRandom.current().nextInt(8),
                90 + ThreadLocalRandom.current().nextInt(8)
            )
        } else {
            mats = when (shipClass) {
                ShipClass.BATTLESHIP, ShipClass.AIRCRAFT_CARRIER, ShipClass.PRINCESS, ShipClass.DEMON -> intArrayOf(
                    (10 + ThreadLocalRandom.current().nextInt(3)) * 9,
                    (10 + ThreadLocalRandom.current().nextInt(3)) * 9,
                    (10 + ThreadLocalRandom.current().nextInt(3)) * 9,
                    (10 + ThreadLocalRandom.current().nextInt(3)) * 9
                )

                else -> intArrayOf(
                    12 + ThreadLocalRandom.current().nextInt(8),
                    12 + ThreadLocalRandom.current().nextInt(8),
                    12 + ThreadLocalRandom.current().nextInt(8),
                    12 + ThreadLocalRandom.current().nextInt(8)
                )
            }
        }

        for (i in 0..3) {
            matStock[i] += mats[i]
        }
    }

    private fun getNormDist(x: Int): Float {
        val value = calcNormalDist(0.5f - x * 2.5E-4f) * 0.50132567f
        return max(value, NORMAL_FLOOR)
    }

    private fun calcNormalDist(x: Float): Float {
        val s1 = 2.5066283f
        val s2 = 1.0f / (0.2.toFloat() * s1)
        val s3 = x - 0.5.toFloat()
        val s4 = -(s3 * s3)
        val s5 = 2.0f * 0.2.toFloat() * 0.2.toFloat()
        return (s2 * exp((s4 / s5).toDouble())).toFloat()
    }

    @JvmRecord
    private data class Candidate(val id: Int, val mean: Int, val preferredMaterial: Int)
}
