package org.trp.shincolle.reference

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.trp.shincolle.init.ModItems
import org.trp.shincolle.item.GrudgeItem
import org.trp.shincolle.item.LegacyEquipItem
import org.trp.shincolle.item.ShipTankItem
import java.util.*

object Values {
    @JvmField
    val ShipBookList: MutableList<Int?>
    @JvmField
    val EnemyBookList: MutableList<Int?>
    @JvmField
    val ShipNameIconMap: MutableMap<Int?, IntArray?>
    @JvmField
    val ShipTypeIconMap: MutableMap<Byte?, IntArray?>
    @JvmField
    val ItemIconMap: MutableMap<Short?, ItemStack?>
    @JvmField
    val BookList: MutableMap<Int?, MutableList<IntArray?>?>
    val FormationAttrs: MutableMap<Int?, FloatArray?>
    @JvmField
    val MoraleAttrs: MutableMap<Int?, FloatArray?>
    @JvmField
    val PageLimit: IntArray = intArrayOf(2, 29, 6, 20, 26, 19, 4)

    init {
        // Ship Book List
        ShipBookList = Collections.unmodifiableList<Int?>(
            mutableListOf<Int?>(
                0,
                1,
                2,
                3,
                9,
                10,
                12,
                14,
                15,
                21,
                26,
                28,
                31,
                33,
                16,
                17,
                18,
                19,
                20,
                13,
                27,
                49,
                29,
                30,
                44,
                72
            )
        )

        // Enemy Book List
        EnemyBookList = Collections.unmodifiableList<Int?>(
            mutableListOf<Int?>(
                36,
                37,
                38,
                39,
                46,
                47,
                48,
                51,
                52,
                53,
                54,
                56,
                57,
                58,
                59,
                60,
                61,
                62,
                63
            )
        )

        // Ship Name Icon Map (sheetID, U, V)
        val tempShipNameIconMap: MutableMap<Int?, IntArray?> = HashMap<Int?, IntArray?>()
        tempShipNameIconMap.put(0, intArrayOf(1, 0, 0))
        tempShipNameIconMap.put(1, intArrayOf(1, 11, 0))
        tempShipNameIconMap.put(2, intArrayOf(1, 22, 0))
        tempShipNameIconMap.put(3, intArrayOf(1, 33, 0))
        tempShipNameIconMap.put(4, intArrayOf(1, 44, 0))
        tempShipNameIconMap.put(5, intArrayOf(1, 55, 0))
        tempShipNameIconMap.put(6, intArrayOf(1, 66, 0))
        tempShipNameIconMap.put(7, intArrayOf(1, 77, 0))
        tempShipNameIconMap.put(8, intArrayOf(1, 88, 0))
        tempShipNameIconMap.put(9, intArrayOf(1, 99, 0))
        tempShipNameIconMap.put(10, intArrayOf(1, 110, 0))
        tempShipNameIconMap.put(11, intArrayOf(1, 121, 0))
        tempShipNameIconMap.put(12, intArrayOf(1, 132, 0))
        tempShipNameIconMap.put(13, intArrayOf(1, 143, 0))
        tempShipNameIconMap.put(14, intArrayOf(1, 154, 0))
        tempShipNameIconMap.put(15, intArrayOf(1, 165, 0))
        tempShipNameIconMap.put(16, intArrayOf(1, 176, 0))
        tempShipNameIconMap.put(17, intArrayOf(1, 187, 0))
        tempShipNameIconMap.put(18, intArrayOf(1, 198, 0))
        tempShipNameIconMap.put(19, intArrayOf(1, 209, 0))
        tempShipNameIconMap.put(64, intArrayOf(1, 220, 0))
        tempShipNameIconMap.put(20, intArrayOf(2, 0, 59))
        tempShipNameIconMap.put(21, intArrayOf(2, 11, 59))
        tempShipNameIconMap.put(22, intArrayOf(2, 22, 59))
        tempShipNameIconMap.put(23, intArrayOf(2, 33, 59))
        tempShipNameIconMap.put(34, intArrayOf(2, 44, 59))
        tempShipNameIconMap.put(41, intArrayOf(2, 55, 59))
        tempShipNameIconMap.put(26, intArrayOf(2, 66, 59))
        tempShipNameIconMap.put(27, intArrayOf(2, 77, 59))
        tempShipNameIconMap.put(28, intArrayOf(2, 88, 59))
        tempShipNameIconMap.put(29, intArrayOf(2, 99, 59))
        tempShipNameIconMap.put(30, intArrayOf(2, 110, 59))
        tempShipNameIconMap.put(31, intArrayOf(2, 121, 59))
        tempShipNameIconMap.put(32, intArrayOf(2, 132, 59))
        tempShipNameIconMap.put(40, intArrayOf(2, 143, 59))
        tempShipNameIconMap.put(33, intArrayOf(2, 154, 59))
        tempShipNameIconMap.put(35, intArrayOf(2, 165, 59))
        tempShipNameIconMap.put(25, intArrayOf(2, 176, 59))
        tempShipNameIconMap.put(24, intArrayOf(2, 187, 59))
        tempShipNameIconMap.put(45, intArrayOf(2, 198, 59))
        tempShipNameIconMap.put(43, intArrayOf(2, 209, 59))
        tempShipNameIconMap.put(49, intArrayOf(2, 220, 59))
        tempShipNameIconMap.put(44, intArrayOf(2, 231, 59))
        tempShipNameIconMap.put(50, intArrayOf(2, 242, 59))
        tempShipNameIconMap.put(65, intArrayOf(3, 0, 118))
        tempShipNameIconMap.put(66, intArrayOf(3, 11, 118))
        tempShipNameIconMap.put(67, intArrayOf(3, 22, 118))
        tempShipNameIconMap.put(68, intArrayOf(3, 33, 118))
        tempShipNameIconMap.put(69, intArrayOf(3, 44, 118))
        tempShipNameIconMap.put(70, intArrayOf(3, 55, 118))
        tempShipNameIconMap.put(71, intArrayOf(3, 66, 118))
        tempShipNameIconMap.put(72, intArrayOf(3, 77, 118))
        tempShipNameIconMap.put(73, intArrayOf(3, 88, 118))
        tempShipNameIconMap.put(74, intArrayOf(3, 99, 118))
        tempShipNameIconMap.put(75, intArrayOf(3, 110, 118))
        tempShipNameIconMap.put(76, intArrayOf(3, 121, 118))
        tempShipNameIconMap.put(77, intArrayOf(3, 132, 118))
        tempShipNameIconMap.put(78, intArrayOf(3, 143, 118))
        tempShipNameIconMap.put(79, intArrayOf(3, 154, 118))
        tempShipNameIconMap.put(80, intArrayOf(3, 165, 118))
        tempShipNameIconMap.put(81, intArrayOf(3, 176, 118))
        tempShipNameIconMap.put(82, intArrayOf(3, 187, 118))
        tempShipNameIconMap.put(83, intArrayOf(4, 0, 177))
        tempShipNameIconMap.put(84, intArrayOf(4, 11, 177))
        tempShipNameIconMap.put(36, intArrayOf(101, 0, 0))
        tempShipNameIconMap.put(37, intArrayOf(101, 11, 0))
        tempShipNameIconMap.put(46, intArrayOf(101, 22, 0))
        tempShipNameIconMap.put(47, intArrayOf(101, 33, 0))
        tempShipNameIconMap.put(48, intArrayOf(101, 44, 0))
        tempShipNameIconMap.put(51, intArrayOf(101, 55, 0))
        tempShipNameIconMap.put(52, intArrayOf(101, 66, 0))
        tempShipNameIconMap.put(53, intArrayOf(101, 77, 0))
        tempShipNameIconMap.put(54, intArrayOf(101, 88, 0))
        tempShipNameIconMap.put(55, intArrayOf(101, 99, 0))
        tempShipNameIconMap.put(56, intArrayOf(101, 110, 0))
        tempShipNameIconMap.put(57, intArrayOf(101, 121, 0))
        tempShipNameIconMap.put(58, intArrayOf(101, 132, 0))
        tempShipNameIconMap.put(59, intArrayOf(101, 143, 0))
        tempShipNameIconMap.put(60, intArrayOf(101, 154, 0))
        tempShipNameIconMap.put(61, intArrayOf(101, 165, 0))
        tempShipNameIconMap.put(62, intArrayOf(101, 176, 0))
        tempShipNameIconMap.put(63, intArrayOf(101, 187, 0))
        tempShipNameIconMap.put(38, intArrayOf(101, 198, 0))
        tempShipNameIconMap.put(39, intArrayOf(101, 209, 0))
        ShipNameIconMap = Collections.unmodifiableMap<Int?, IntArray?>(tempShipNameIconMap)

        // Ship Type Icon Map (U, V)
        val tempShipTypeIconMap: MutableMap<Byte?, IntArray?> = HashMap<Byte?, IntArray?>()
        tempShipTypeIconMap.put(7.toByte(), intArrayOf(12, 74))
        tempShipTypeIconMap.put((-1).toByte(), intArrayOf(41, 0))
        tempShipTypeIconMap.put(1.toByte(), intArrayOf(41, 29))
        tempShipTypeIconMap.put(2.toByte(), intArrayOf(41, 58))
        tempShipTypeIconMap.put(3.toByte(), intArrayOf(41, 87))
        tempShipTypeIconMap.put(6.toByte(), intArrayOf(70, 0))
        tempShipTypeIconMap.put(5.toByte(), intArrayOf(70, 29))
        tempShipTypeIconMap.put(4.toByte(), intArrayOf(70, 58))
        tempShipTypeIconMap.put(10.toByte(), intArrayOf(70, 87))
        tempShipTypeIconMap.put(8.toByte(), intArrayOf(99, 0))
        tempShipTypeIconMap.put(9.toByte(), intArrayOf(99, 58))
        ShipTypeIconMap = Collections.unmodifiableMap<Byte?, IntArray?>(tempShipTypeIconMap)

        val tempItemIconMap: MutableMap<Short?, ItemStack?> = HashMap<Short?, ItemStack?>()
        tempItemIconMap.put(0.toShort(), ItemStack(Items.IRON_INGOT))
        tempItemIconMap.put(1.toShort(), ItemStack(ModItems.GRUDGE.get()))
        val grudgeItem = ModItems.GRUDGE.get()
        if (grudgeItem is GrudgeItem) {
            tempItemIconMap.put(77.toShort(), grudgeItem.createVariantStack(1))
        }
        tempItemIconMap.put(2.toShort(), ItemStack(ModItems.GRUDGE_BLOCK.get()))
        tempItemIconMap.put(3.toShort(), ItemStack(ModItems.GRUDGE_HEAVY_BLOCK.get()))
        tempItemIconMap.put(4.toShort(), ItemStack(ModItems.ABYSS_METAL.get()))
        tempItemIconMap.put(5.toShort(), ItemStack(ModItems.ABYSSIUM.get()))
        tempItemIconMap.put(6.toShort(), ItemStack(ModItems.ABYSS_POLYMETAL.get()))
        tempItemIconMap.put(9.toShort(), ItemStack(ModItems.POLYMETAL_ORE.get()))
        tempItemIconMap.put(7.toShort(), ItemStack(ModItems.POLYMETAL.get()))
        tempItemIconMap.put(8.toShort(), ItemStack(ModItems.POLYMETAL_GRAVEL.get()))
        tempItemIconMap.put(10.toShort(), ItemStack(Items.GUNPOWDER))
        tempItemIconMap.put(11.toShort(), ItemStack(Items.BLAZE_POWDER))
        tempItemIconMap.put(12.toShort(), ItemStack(ModItems.AMMO_LIGHT.get()))
        tempItemIconMap.put(13.toShort(), ItemStack(ModItems.AMMO_LIGHT_CONTAINER.get()))
        tempItemIconMap.put(14.toShort(), ItemStack(ModItems.AMMO_HEAVY.get()))
        tempItemIconMap.put(15.toShort(), ItemStack(ModItems.AMMO_HEAVY_CONTAINER.get()))
        tempItemIconMap.put(16.toShort(), ItemStack(ModItems.BUCKET_REPAIR.get()))
        tempItemIconMap.put(17.toShort(), ItemStack(Items.LAVA_BUCKET))
        tempItemIconMap.put(18.toShort(), ItemStack(Items.NETHER_STAR))
        tempItemIconMap.put(19.toShort(), ItemStack(ModItems.MARRIAGE_RING.get()))
        tempItemIconMap.put(20.toShort(), ItemStack(Items.PAPER))
        tempItemIconMap.put(21.toShort(), ItemStack(ModItems.OWNER_PAPER.get()))
        tempItemIconMap.put(22.toShort(), ItemStack(Items.STICK))
        tempItemIconMap.put(23.toShort(), ItemStack(ModItems.KAITAI_HAMMER.get()))
        tempItemIconMap.put(24.toShort(), ItemStack(ModItems.MODERN_KIT.get()))
        tempItemIconMap.put(25.toShort(), ItemStack(ModItems.SHIPSPAWNEGGS.get()))
        tempItemIconMap.put(26.toShort(), ItemStack(ModItems.SHIPSPAWNEGGL.get()))
        // tempItemIconMap.put((short)27, new ItemStack(ModItems.ShipSpawnEgg, 1, 2)); // Meta-based
        tempItemIconMap.put(28.toShort(), ItemStack(ModItems.INSTANT_CON_MAT.get()))
        tempItemIconMap.put(29.toShort(), ItemStack(Items.DIAMOND_BLOCK))
        tempItemIconMap.put(30.toShort(), ItemStack(ModItems.REPAIR_GODDESS.get()))
        tempItemIconMap.put(31.toShort(), ItemStack(ModItems.POINTER_ITEM.get()))
        tempItemIconMap.put(32.toShort(), ItemStack(ModItems.TOY_AIRPLANE.get()))
        // tempItemIconMap.put((short)37, new ItemStack(ModBlocks.BlockChair.get())); // Missing
        tempItemIconMap.put(33.toShort(), ItemStack(ModItems.DESK.get()))
        tempItemIconMap.put(50.toShort(), ItemStack(ModItems.DESK_ITEM_BOOK.get()))
        tempItemIconMap.put(51.toShort(), ItemStack(ModItems.DESK_ITEM_RADAR.get()))
        tempItemIconMap.put(52.toShort(), ItemStack(Items.WRITABLE_BOOK))
        tempItemIconMap.put(53.toShort(), ItemStack(Items.COMPASS))
        tempItemIconMap.put(34.toShort(), ItemStack(Items.OBSIDIAN))
        tempItemIconMap.put(35.toShort(), ItemStack(Items.WHITE_WOOL))
        tempItemIconMap.put(38.toShort(), ItemStack(Items.OAK_PLANKS))
        tempItemIconMap.put(39.toShort(), ItemStack(Items.LEATHER))
        tempItemIconMap.put(36.toShort(), ItemStack(ModItems.SMALL_SHIPYARD.get()))
        tempItemIconMap.put(54.toShort(), ItemStack(ModItems.TARGET_WRENCH.get()))
        tempItemIconMap.put(55.toShort(), ItemStack(ModItems.VOL_CORE.get()))
        tempItemIconMap.put(56.toShort(), ItemStack(ModItems.VOL_BLOCK.get()))
        tempItemIconMap.put(61.toShort(), ItemStack(ModItems.FRAME_BLOCK.get()))
        tempItemIconMap.put(62.toShort(), ItemStack(ModItems.WAYPOINT.get()))
        tempItemIconMap.put(63.toShort(), ItemStack(ModItems.CRANE.get()))
        tempItemIconMap.put(64.toShort(), ItemStack(Items.PISTON))
        tempItemIconMap.put(65.toShort(), ItemStack(ModItems.TRAINING_BOOK.get()))
        tempItemIconMap.put(66.toShort(), ItemStack(Items.MAGMA_BLOCK))
        tempItemIconMap.put(67.toShort(), ItemStack(ModItems.SHIP_TANK.get()))
        tempItemIconMap.put(68.toShort(), (ModItems.SHIP_TANK.get() as ShipTankItem).createVariantStack(1))
        tempItemIconMap.put(69.toShort(), (ModItems.SHIP_TANK.get() as ShipTankItem).createVariantStack(2))
        tempItemIconMap.put(70.toShort(), (ModItems.SHIP_TANK.get() as ShipTankItem).createVariantStack(3))
        tempItemIconMap.put(71.toShort(), ItemStack(Items.CAULDRON))
        tempItemIconMap.put(72.toShort(), ItemStack(Items.LAPIS_LAZULI))
        tempItemIconMap.put(73.toShort(), ItemStack(ModItems.RECIPE_PAPER.get()))
        tempItemIconMap.put(74.toShort(), ItemStack(ModItems.OP_TOOL.get()))
        tempItemIconMap.put(75.toShort(), (ModItems.EQUIP_AMMO.get() as LegacyEquipItem).createVariantStack(7))
        tempItemIconMap.put(76.toShort(), ItemStack(Items.POTION))
        // tempItemIconMap.put((short)77, new ItemStack(ModItems.Grudge, 1, 1));
        tempItemIconMap.put(78.toShort(), ItemStack(ModItems.GRUDGE_XP_BLOCK.get()))
        tempItemIconMap.put(79.toShort(), ItemStack(Items.EXPERIENCE_BOTTLE))
        ItemIconMap = Collections.unmodifiableMap<Short?, ItemStack?>(tempItemIconMap)

        // Book List
        val tempBookList: MutableMap<Int?, MutableList<IntArray?>?> = HashMap<Int?, MutableList<IntArray?>?>()
        tempBookList.put(0, Arrays.asList<IntArray?>(intArrayOf(0, 0, 0, 0), intArrayOf(0, 1, 0, 0)))
        tempBookList.put(1, Arrays.asList<IntArray?>(intArrayOf(0, 0, 0, 0), intArrayOf(0, 1, 0, 0)))
        tempBookList.put(
            1000,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, 76, 0, 0, 0, 100, 56),
                intArrayOf(2, 0, 13, -3, 1),
                intArrayOf(2, 0, 43, -3, 2),
                intArrayOf(2, 0, 73, -3, 3)
            )
        )
        tempBookList.put(
            1001,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, 17, 1),
                intArrayOf(2, 0, 23, 17, 0),
                intArrayOf(2, 0, 81, 17, 4)
            )
        )
        tempBookList.put(
            1002,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 0, 56, 100, 56),
                intArrayOf(1, 1, 0, 73, 0, 0, 112, 100, 59),
                intArrayOf(2, 0, 5, 52, 9),
                intArrayOf(2, 0, 30, 52, 6),
                intArrayOf(2, 0, 55, 52, 7),
                intArrayOf(2, 0, 80, 52, 8)
            )
        )
        tempBookList.put(
            1003,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, -3, 0),
                intArrayOf(2, 0, 23, -3, 0),
                intArrayOf(2, 0, 43, -3, 0),
                intArrayOf(2, 0, 3, 17, 0),
                intArrayOf(2, 0, 23, 17, 1),
                intArrayOf(2, 0, 43, 17, 0),
                intArrayOf(2, 0, 3, 37, 0),
                intArrayOf(2, 0, 23, 37, 10),
                intArrayOf(2, 0, 43, 37, 0),
                intArrayOf(2, 0, 81, 17, 12),
                intArrayOf(2, 1, 3, 110, 12),
                intArrayOf(2, 1, 28, 110, 13),
                intArrayOf(2, 1, 53, 110, 14),
                intArrayOf(2, 1, 78, 110, 15)
            )
        )
        tempBookList.put(
            1004,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, 17, 17),
                intArrayOf(2, 0, 23, 17, 1),
                intArrayOf(2, 0, 81, 17, 16)
            )
        )
        tempBookList.put(
            1005,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 23, -3, 18),
                intArrayOf(2, 0, 3, 17, 4),
                intArrayOf(2, 0, 43, 17, 4),
                intArrayOf(2, 0, 23, 37, 4),
                intArrayOf(2, 0, 81, 17, 19)
            )
        )
        tempBookList.put(
            1006,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, 17, 20),
                intArrayOf(2, 0, 23, 17, 1),
                intArrayOf(2, 0, 81, 17, 21)
            )
        )
        tempBookList.put(
            1007,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, -3, 4),
                intArrayOf(2, 0, 23, -3, 4),
                intArrayOf(2, 0, 43, -3, 4),
                intArrayOf(2, 0, 3, 17, 4),
                intArrayOf(2, 0, 23, 17, 4),
                intArrayOf(2, 0, 43, 17, 4),
                intArrayOf(2, 0, 23, 37, 22),
                intArrayOf(2, 0, 81, 17, 23)
            )
        )
        tempBookList.put(
            1008,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, -3, 23),
                intArrayOf(2, 0, 23, -3, 54),
                intArrayOf(2, 0, 43, -3, 78),
                intArrayOf(2, 0, 3, 17, 78),
                intArrayOf(2, 0, 23, 17, 78),
                intArrayOf(2, 0, 43, 17, 78),
                intArrayOf(2, 0, 81, 17, 24)
            )
        )
        tempBookList.put(
            1009,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, 17, 23),
                intArrayOf(2, 0, 23, 17, 25),
                intArrayOf(2, 0, 81, 17, 28)
            )
        )
        tempBookList.put(
            1010,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, -3, 2),
                intArrayOf(2, 0, 23, -3, 3),
                intArrayOf(2, 0, 43, -3, 2),
                intArrayOf(2, 0, 3, 17, 3),
                intArrayOf(2, 0, 23, 17, 29),
                intArrayOf(2, 0, 43, 17, 3),
                intArrayOf(2, 0, 3, 37, 2),
                intArrayOf(2, 0, 23, 37, 3),
                intArrayOf(2, 0, 43, 37, 2),
                intArrayOf(2, 0, 81, 17, 30)
            )
        )
        tempBookList.put(
            1011,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, 37, 6),
                intArrayOf(2, 0, 23, 17, 6),
                intArrayOf(2, 0, 43, -3, 2),
                intArrayOf(2, 0, 81, 17, 31)
            )
        )
        tempBookList.put(1012, Arrays.asList<IntArray?>(intArrayOf(0, 0, 0, 0), intArrayOf(0, 1, 0, 0)))
        tempBookList.put(1013, Arrays.asList<IntArray?>(intArrayOf(0, 0, 0, 0), intArrayOf(0, 1, 0, 0)))
        tempBookList.put(
            1014,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 23, -3, 6),
                intArrayOf(2, 0, 3, 17, 6),
                intArrayOf(2, 0, 23, 17, 6),
                intArrayOf(2, 0, 43, 17, 6),
                intArrayOf(2, 0, 23, 37, 6),
                intArrayOf(2, 0, 81, 17, 32)
            )
        )
        tempBookList.put(
            1015,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, -3, 1),
                intArrayOf(2, 0, 23, -3, 1),
                intArrayOf(2, 0, 43, -3, 1),
                intArrayOf(2, 0, 3, 17, 1),
                intArrayOf(2, 0, 23, 17, 52),
                intArrayOf(2, 0, 43, 17, 1),
                intArrayOf(2, 0, 3, 37, 1),
                intArrayOf(2, 0, 23, 37, 1),
                intArrayOf(2, 0, 43, 37, 1),
                intArrayOf(2, 0, 81, 17, 50)
            )
        )
        tempBookList.put(
            1016,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, -3, 1),
                intArrayOf(2, 0, 23, -3, 1),
                intArrayOf(2, 0, 43, -3, 1),
                intArrayOf(2, 0, 3, 17, 1),
                intArrayOf(2, 0, 23, 17, 53),
                intArrayOf(2, 0, 43, 17, 1),
                intArrayOf(2, 0, 3, 37, 1),
                intArrayOf(2, 0, 23, 37, 1),
                intArrayOf(2, 0, 43, 37, 1),
                intArrayOf(2, 0, 81, 17, 51)
            )
        )
        tempBookList.put(
            1017,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, -3, 51),
                intArrayOf(2, 0, 23, -3, 50),
                intArrayOf(2, 0, 43, -3, 35),
                intArrayOf(2, 0, 3, 17, 34),
                intArrayOf(2, 0, 23, 17, 34),
                intArrayOf(2, 0, 43, 17, 34),
                intArrayOf(2, 0, 3, 37, 34),
                intArrayOf(2, 0, 43, 37, 34),
                intArrayOf(2, 0, 81, 17, 33)
            )
        )
        tempBookList.put(
            1018,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, -3, 1),
                intArrayOf(2, 0, 23, -3, 1),
                intArrayOf(2, 0, 43, -3, 38),
                intArrayOf(2, 0, 3, 17, 38),
                intArrayOf(2, 0, 23, 17, 38),
                intArrayOf(2, 0, 43, 17, 38),
                intArrayOf(2, 0, 3, 37, 38),
                intArrayOf(2, 0, 23, 37, 39),
                intArrayOf(2, 0, 43, 37, 38),
                intArrayOf(2, 0, 81, 17, 37)
            )
        )
        tempBookList.put(
            1019,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, -3, 4),
                intArrayOf(2, 0, 43, -3, 4),
                intArrayOf(2, 0, 3, 17, 4),
                intArrayOf(2, 0, 23, 17, 4),
                intArrayOf(2, 0, 43, 17, 4),
                intArrayOf(2, 0, 23, 37, 4),
                intArrayOf(2, 0, 81, 17, 54)
            )
        )
        tempBookList.put(
            1020,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, -3, 34),
                intArrayOf(2, 0, 23, -3, 66),
                intArrayOf(2, 0, 43, -3, 34),
                intArrayOf(2, 0, 3, 17, 66),
                intArrayOf(2, 0, 23, 17, 2),
                intArrayOf(2, 0, 43, 17, 66),
                intArrayOf(2, 0, 3, 37, 34),
                intArrayOf(2, 0, 23, 37, 66),
                intArrayOf(2, 0, 43, 37, 34),
                intArrayOf(2, 0, 81, 17, 56)
            )
        )
        tempBookList.put(
            1021,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, -3, 34),
                intArrayOf(2, 0, 23, -3, 56),
                intArrayOf(2, 0, 43, -3, 34),
                intArrayOf(2, 0, 3, 17, 56),
                intArrayOf(2, 0, 23, 17, 3),
                intArrayOf(2, 0, 43, 17, 56),
                intArrayOf(2, 0, 3, 37, 34),
                intArrayOf(2, 0, 23, 37, 56),
                intArrayOf(2, 0, 43, 37, 34),
                intArrayOf(2, 0, 81, 17, 55)
            )
        )
        tempBookList.put(
            1022,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, -3, 4),
                intArrayOf(2, 0, 43, -3, 4),
                intArrayOf(2, 0, 23, 17, 34),
                intArrayOf(2, 0, 3, 37, 4),
                intArrayOf(2, 0, 43, 37, 4),
                intArrayOf(2, 0, 81, 17, 61)
            )
        )
        tempBookList.put(
            1023,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 23, 17, 1),
                intArrayOf(2, 0, 23, 37, 22),
                intArrayOf(2, 0, 81, 17, 62)
            )
        )
        tempBookList.put(
            1024,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, -3, 4),
                intArrayOf(2, 0, 23, -3, 4),
                intArrayOf(2, 0, 43, -3, 4),
                intArrayOf(2, 0, 3, 17, 4),
                intArrayOf(2, 0, 23, 17, 2),
                intArrayOf(2, 0, 43, 17, 4),
                intArrayOf(2, 0, 3, 37, 4),
                intArrayOf(2, 0, 23, 37, 64),
                intArrayOf(2, 0, 43, 37, 4),
                intArrayOf(2, 0, 81, 17, 63)
            )
        )
        tempBookList.put(
            1025, Arrays.asList<IntArray?>(
                intArrayOf(3, 4, 1),
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, -3, 23),
                intArrayOf(2, 0, 23, -3, 24),
                intArrayOf(2, 0, 43, -3, 52),
                intArrayOf(2, 0, 3, 17, 78),
                intArrayOf(2, 0, 23, 17, 78),
                intArrayOf(2, 0, 43, 17, 78),
                intArrayOf(2, 0, 3, 37, 78),
                intArrayOf(2, 0, 81, 17, 65),
                intArrayOf(1, 2, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 2, 3, -3, 77),
                intArrayOf(2, 2, 23, -3, 77),
                intArrayOf(2, 2, 43, -3, 77),
                intArrayOf(2, 2, 3, 17, 77),
                intArrayOf(2, 2, 23, 17, 77),
                intArrayOf(2, 2, 43, 17, 77),
                intArrayOf(2, 2, 3, 37, 77),
                intArrayOf(2, 2, 23, 37, 77),
                intArrayOf(2, 2, 43, 37, 77),
                intArrayOf(2, 2, 81, 17, 78),
                intArrayOf(1, 4, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 4, 3, -3, 79),
                intArrayOf(2, 4, 23, -3, 79),
                intArrayOf(2, 4, 43, -3, 79),
                intArrayOf(2, 4, 3, 17, 79),
                intArrayOf(2, 4, 23, 17, 1),
                intArrayOf(2, 4, 43, 17, 79),
                intArrayOf(2, 4, 3, 37, 79),
                intArrayOf(2, 4, 23, 37, 79),
                intArrayOf(2, 4, 43, 37, 79),
                intArrayOf(2, 4, 81, 17, 77)
            )
        )
        tempBookList.put(
            1026, Arrays.asList<IntArray?>(
                intArrayOf(3, 6, 1),
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, -3, 6),
                intArrayOf(2, 0, 23, -3, 71),
                intArrayOf(2, 0, 43, -3, 6),
                intArrayOf(2, 0, 3, 17, 6),
                intArrayOf(2, 0, 23, 17, 71),
                intArrayOf(2, 0, 43, 17, 6),
                intArrayOf(2, 0, 3, 37, 6),
                intArrayOf(2, 0, 23, 37, 71),
                intArrayOf(2, 0, 43, 37, 6),
                intArrayOf(2, 0, 81, 17, 67),
                intArrayOf(1, 2, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 2, 3, -3, 34),
                intArrayOf(2, 2, 23, -3, 67),
                intArrayOf(2, 2, 43, -3, 34),
                intArrayOf(2, 2, 3, 17, 34),
                intArrayOf(2, 2, 23, 17, 67),
                intArrayOf(2, 2, 43, 17, 34),
                intArrayOf(2, 2, 3, 37, 34),
                intArrayOf(2, 2, 23, 37, 67),
                intArrayOf(2, 2, 43, 37, 34),
                intArrayOf(2, 2, 81, 17, 68),
                intArrayOf(1, 4, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 4, 3, -3, 5),
                intArrayOf(2, 4, 23, -3, 68),
                intArrayOf(2, 4, 43, -3, 5),
                intArrayOf(2, 4, 3, 17, 5),
                intArrayOf(2, 4, 23, 17, 68),
                intArrayOf(2, 4, 43, 17, 5),
                intArrayOf(2, 4, 3, 37, 5),
                intArrayOf(2, 4, 23, 37, 68),
                intArrayOf(2, 4, 43, 37, 5),
                intArrayOf(2, 4, 81, 17, 69),
                intArrayOf(1, 6, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 6, 3, -3, 3),
                intArrayOf(2, 6, 23, -3, 69),
                intArrayOf(2, 6, 43, -3, 3),
                intArrayOf(2, 6, 3, 17, 3),
                intArrayOf(2, 6, 23, 17, 69),
                intArrayOf(2, 6, 43, 17, 3),
                intArrayOf(2, 6, 3, 37, 3),
                intArrayOf(2, 6, 23, 37, 69),
                intArrayOf(2, 6, 43, 37, 3),
                intArrayOf(2, 6, 81, 17, 70)
            )
        )
        tempBookList.put(
            1027,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, 17, 1),
                intArrayOf(2, 0, 23, 17, 20),
                intArrayOf(2, 0, 43, 17, 72),
                intArrayOf(2, 0, 81, 17, 73)
            )
        )
        tempBookList.put(
            1028,
            Arrays.asList<IntArray?>(intArrayOf(0, 0, 0, 0), intArrayOf(0, 1, 0, 0), intArrayOf(2, 0, 43, 17, 74))
        )
        tempBookList.put(
            1029,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(2, 0, 3, -3, 76),
                intArrayOf(2, 0, 23, -3, 76),
                intArrayOf(2, 0, 43, -3, 76),
                intArrayOf(2, 0, 3, 17, 76),
                intArrayOf(2, 0, 23, 17, 75),
                intArrayOf(2, 0, 43, 17, 76),
                intArrayOf(2, 0, 3, 37, 76),
                intArrayOf(2, 0, 23, 37, 76),
                intArrayOf(2, 0, 43, 37, 76),
                intArrayOf(2, 0, 81, 17, 75)
            )
        )
        tempBookList.put(
            2000,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, -6, 0, 100, 72, 100, 62),
                intArrayOf(1, 1, 0, -6, 0, 100, 134, 100, 46),
                intArrayOf(2, 0, 3, -3, 1),
                intArrayOf(2, 0, 23, -3, 17),
                intArrayOf(2, 0, 43, -3, 1),
                intArrayOf(2, 0, 3, 17, 17),
                intArrayOf(2, 0, 23, 17, 34),
                intArrayOf(2, 0, 43, 17, 17),
                intArrayOf(2, 0, 3, 37, 34),
                intArrayOf(2, 0, 23, 37, 34),
                intArrayOf(2, 0, 43, 37, 34),
                intArrayOf(2, 0, 81, 17, 36)
            )
        )
        tempBookList.put(
            2001,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 25, -12, 0, 0, 230, 50, 26)
            )
        )
        tempBookList.put(
            2002,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 25, -12, 0, 50, 230, 50, 26)
            )
        )
        tempBookList.put(
            2003,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, 60, 0, 100, 180, 100, 65),
                intArrayOf(1, 1, -7, -18, 0, 200, 0, 38, 38),
                intArrayOf(1, 1, 31, -18, 0, 200, 38, 38, 38),
                intArrayOf(1, 1, 69, -18, 0, 200, 76, 38, 38)
            )
        )
        tempBookList.put(2004, Arrays.asList<IntArray?>(intArrayOf(0, 0, 0, 0), intArrayOf(0, 1, 0, 0)))
        tempBookList.put(
            3013,
            Arrays.asList<IntArray?>(
                intArrayOf(0, 0, 0, 0),
                intArrayOf(0, 1, 0, 0),
                intArrayOf(1, 0, 0, 120, 0, 100, 245, 100, 11)
            )
        )
        BookList = Collections.unmodifiableMap<Int?, MutableList<IntArray?>?>(tempBookList)

        // Formation Attributes
        val tempFormationAttrs: MutableMap<Int?, FloatArray?> = HashMap<Int?, FloatArray?>()
        tempFormationAttrs.put(
            10,
            floatArrayOf(
                0.0f,
                2.0f,
                2.0f,
                1.2f,
                1.2f,
                0.3f,
                1.3f,
                0.08f,
                4.0f,
                1.75f,
                1.75f,
                1.75f,
                1.25f,
                0.5f,
                0.4f,
                0.1f,
                0.0f,
                0.2f,
                0.0f,
                0.0f,
                0.0f
            )
        )
        tempFormationAttrs.put(
            11,
            floatArrayOf(
                0.0f,
                1.75f,
                1.75f,
                1.2f,
                1.2f,
                0.4f,
                1.3f,
                0.08f,
                4.0f,
                1.55f,
                1.55f,
                1.55f,
                1.2f,
                0.5f,
                0.4f,
                0.1f,
                0.0f,
                0.2f,
                0.0f,
                0.0f,
                0.0f
            )
        )
        tempFormationAttrs.put(
            12,
            floatArrayOf(
                0.0f,
                1.55f,
                1.55f,
                1.15f,
                1.15f,
                0.5f,
                1.2f,
                0.08f,
                3.0f,
                1.4f,
                1.4f,
                1.4f,
                1.2f,
                0.5f,
                0.4f,
                0.1f,
                0.0f,
                0.2f,
                0.0f,
                0.0f,
                0.0f
            )
        )
        tempFormationAttrs.put(
            13,
            floatArrayOf(
                0.0f,
                1.4f,
                1.4f,
                1.15f,
                1.15f,
                0.6f,
                1.2f,
                0.08f,
                3.0f,
                1.3f,
                1.3f,
                1.3f,
                1.15f,
                0.5f,
                0.4f,
                0.1f,
                0.0f,
                0.2f,
                0.0f,
                0.0f,
                0.0f
            )
        )
        tempFormationAttrs.put(
            14,
            floatArrayOf(
                0.0f,
                1.3f,
                1.3f,
                1.1f,
                1.1f,
                0.7f,
                1.1f,
                0.08f,
                2.0f,
                1.2f,
                1.2f,
                1.2f,
                1.15f,
                0.5f,
                0.4f,
                0.1f,
                0.0f,
                0.2f,
                0.0f,
                0.0f,
                0.0f
            )
        )
        tempFormationAttrs.put(
            15,
            floatArrayOf(
                0.0f,
                1.25f,
                1.25f,
                1.1f,
                1.1f,
                0.8f,
                1.1f,
                0.08f,
                2.0f,
                1.1f,
                1.1f,
                1.1f,
                1.1f,
                0.5f,
                0.4f,
                0.1f,
                0.0f,
                0.2f,
                0.0f,
                0.0f,
                0.0f
            )
        )
        tempFormationAttrs.put(
            20,
            floatArrayOf(
                0.0f,
                1.4f,
                1.4f,
                1.1f,
                1.1f,
                0.9f,
                1.08f,
                0.0f,
                2.0f,
                1.15f,
                1.15f,
                1.15f,
                1.55f,
                1.2f,
                1.0f,
                -0.15f,
                0.0f,
                0.1f,
                0.0f,
                0.3f,
                0.05f
            )
        )
        tempFormationAttrs.put(
            21,
            floatArrayOf(
                0.0f,
                1.4f,
                1.4f,
                1.1f,
                1.1f,
                0.9f,
                1.08f,
                0.0f,
                2.0f,
                1.15f,
                1.15f,
                1.15f,
                1.55f,
                1.2f,
                1.0f,
                -0.15f,
                0.0f,
                0.1f,
                0.0f,
                0.3f,
                0.05f
            )
        )
        tempFormationAttrs.put(
            22,
            floatArrayOf(
                0.0f,
                1.5f,
                1.5f,
                1.15f,
                1.15f,
                0.75f,
                1.15f,
                0.0f,
                3.0f,
                1.3f,
                1.3f,
                1.3f,
                1.75f,
                1.1f,
                1.0f,
                -0.05f,
                0.0f,
                0.1f,
                0.0f,
                0.1f,
                0.05f
            )
        )
        tempFormationAttrs.put(
            23,
            floatArrayOf(
                0.0f,
                1.5f,
                1.5f,
                1.15f,
                1.15f,
                0.75f,
                1.15f,
                0.0f,
                3.0f,
                1.3f,
                1.3f,
                1.3f,
                1.75f,
                1.1f,
                1.0f,
                -0.05f,
                0.0f,
                0.1f,
                0.0f,
                0.1f,
                0.05f
            )
        )
        tempFormationAttrs.put(
            24,
            floatArrayOf(
                0.0f,
                1.3f,
                1.3f,
                1.05f,
                1.05f,
                1.0f,
                1.0f,
                0.0f,
                1.0f,
                1.1f,
                1.1f,
                1.1f,
                1.35f,
                1.1f,
                1.0f,
                -0.05f,
                0.0f,
                0.1f,
                0.0f,
                0.1f,
                0.05f
            )
        )
        tempFormationAttrs.put(
            25,
            floatArrayOf(
                0.0f,
                1.3f,
                1.3f,
                1.05f,
                1.05f,
                1.0f,
                1.0f,
                0.0f,
                1.0f,
                1.1f,
                1.1f,
                1.1f,
                1.35f,
                1.1f,
                1.0f,
                -0.05f,
                0.0f,
                0.1f,
                0.0f,
                0.1f,
                0.05f
            )
        )
        tempFormationAttrs.put(
            30,
            floatArrayOf(
                0.0f,
                0.6f,
                0.3f,
                2.0f,
                2.0f,
                1.5f,
                1.0f,
                -0.1f,
                4.0f,
                1.1f,
                1.0f,
                1.0f,
                1.0f,
                2.0f,
                1.0f,
                -0.5f,
                0.0f,
                0.0f,
                0.0f,
                0.5f,
                0.1f
            )
        )
        tempFormationAttrs.put(
            31,
            floatArrayOf(
                0.0f,
                1.0f,
                0.65f,
                1.2f,
                1.2f,
                1.25f,
                1.0f,
                -0.1f,
                1.0f,
                1.1f,
                1.1f,
                1.1f,
                1.0f,
                1.75f,
                1.3f,
                -0.3f,
                0.0f,
                0.0f,
                0.0f,
                0.3f,
                0.1f
            )
        )
        tempFormationAttrs.put(
            32,
            floatArrayOf(
                0.0f,
                1.0f,
                0.65f,
                1.2f,
                1.2f,
                1.25f,
                1.0f,
                -0.1f,
                1.0f,
                1.1f,
                1.1f,
                1.1f,
                1.0f,
                1.75f,
                1.3f,
                -0.3f,
                0.0f,
                0.0f,
                0.0f,
                0.3f,
                0.1f
            )
        )
        tempFormationAttrs.put(
            33,
            floatArrayOf(
                0.0f,
                1.0f,
                0.65f,
                1.2f,
                1.2f,
                1.25f,
                1.0f,
                -0.1f,
                1.0f,
                1.1f,
                1.1f,
                1.1f,
                1.0f,
                1.75f,
                1.3f,
                -0.3f,
                0.0f,
                0.0f,
                0.0f,
                0.3f,
                0.1f
            )
        )
        tempFormationAttrs.put(
            34,
            floatArrayOf(
                0.0f,
                1.0f,
                0.65f,
                1.2f,
                1.2f,
                1.25f,
                1.0f,
                -0.1f,
                1.0f,
                1.1f,
                1.1f,
                1.1f,
                1.0f,
                1.75f,
                1.3f,
                -0.3f,
                0.0f,
                0.0f,
                0.0f,
                0.3f,
                0.1f
            )
        )
        tempFormationAttrs.put(
            35,
            floatArrayOf(
                0.0f,
                0.6f,
                0.3f,
                2.0f,
                2.0f,
                1.5f,
                1.0f,
                -0.1f,
                4.0f,
                1.1f,
                1.0f,
                1.0f,
                1.0f,
                2.0f,
                1.0f,
                -0.5f,
                0.0f,
                0.0f,
                0.0f,
                0.5f,
                0.1f
            )
        )
        tempFormationAttrs.put(
            40,
            floatArrayOf(
                0.0f,
                1.2f,
                1.2f,
                1.0f,
                1.0f,
                0.75f,
                1.0f,
                0.18f,
                2.0f,
                1.25f,
                1.25f,
                1.25f,
                0.65f,
                0.3f,
                0.8f,
                0.25f,
                0.0f,
                0.25f,
                0.0f,
                0.0f,
                0.0f
            )
        )
        tempFormationAttrs.put(
            41,
            floatArrayOf(
                0.0f,
                1.1f,
                1.1f,
                1.0f,
                1.0f,
                0.85f,
                1.0f,
                0.18f,
                2.0f,
                1.2f,
                1.2f,
                1.2f,
                0.7f,
                0.3f,
                0.8f,
                0.25f,
                0.0f,
                0.25f,
                0.0f,
                0.0f,
                0.0f
            )
        )
        tempFormationAttrs.put(
            42,
            floatArrayOf(
                0.0f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                0.95f,
                1.0f,
                0.18f,
                2.0f,
                1.15f,
                1.15f,
                1.15f,
                0.75f,
                0.3f,
                0.8f,
                0.25f,
                0.0f,
                0.25f,
                0.0f,
                0.0f,
                0.0f
            )
        )
        tempFormationAttrs.put(
            43,
            floatArrayOf(
                0.0f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.05f,
                1.0f,
                0.18f,
                1.0f,
                1.1f,
                1.1f,
                1.1f,
                0.8f,
                0.3f,
                0.8f,
                0.25f,
                0.0f,
                0.25f,
                0.0f,
                0.0f,
                0.0f
            )
        )
        tempFormationAttrs.put(
            44,
            floatArrayOf(
                0.0f,
                0.9f,
                0.9f,
                1.0f,
                1.0f,
                1.15f,
                1.0f,
                0.18f,
                1.0f,
                1.05f,
                1.05f,
                1.05f,
                0.85f,
                0.3f,
                0.8f,
                0.25f,
                0.0f,
                0.25f,
                0.0f,
                0.0f,
                0.0f
            )
        )
        tempFormationAttrs.put(
            45,
            floatArrayOf(
                0.0f,
                0.8f,
                0.8f,
                1.0f,
                1.0f,
                1.25f,
                1.0f,
                0.18f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                0.9f,
                0.3f,
                0.8f,
                0.25f,
                0.0f,
                0.25f,
                0.0f,
                0.0f,
                0.0f
            )
        )
        tempFormationAttrs.put(
            50,
            floatArrayOf(
                0.0f,
                0.9f,
                0.9f,
                0.9f,
                0.9f,
                1.35f,
                0.8f,
                0.05f,
                -2.0f,
                1.15f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.75f,
                -0.15f,
                0.0f,
                0.0f,
                0.0f,
                0.1f,
                0.0f
            )
        )
        tempFormationAttrs.put(
            51,
            floatArrayOf(
                0.0f,
                0.9f,
                0.9f,
                0.9f,
                0.9f,
                1.35f,
                0.8f,
                0.05f,
                -2.0f,
                1.15f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.75f,
                -0.15f,
                0.0f,
                0.0f,
                0.0f,
                0.1f,
                0.0f
            )
        )
        tempFormationAttrs.put(
            52,
            floatArrayOf(
                0.0f,
                0.9f,
                0.9f,
                0.9f,
                0.9f,
                1.35f,
                0.8f,
                0.05f,
                -2.0f,
                1.15f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.75f,
                -0.15f,
                0.0f,
                0.0f,
                0.0f,
                0.1f,
                0.0f
            )
        )
        tempFormationAttrs.put(
            53,
            floatArrayOf(
                0.0f,
                0.9f,
                0.9f,
                0.9f,
                0.9f,
                1.35f,
                0.8f,
                0.05f,
                -2.0f,
                1.15f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.75f,
                -0.15f,
                0.0f,
                0.0f,
                0.0f,
                0.1f,
                0.0f
            )
        )
        tempFormationAttrs.put(
            54,
            floatArrayOf(
                0.0f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.2f,
                0.9f,
                0.05f,
                -1.0f,
                1.15f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.75f,
                -0.1f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f
            )
        )
        tempFormationAttrs.put(
            55,
            floatArrayOf(
                0.0f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.2f,
                0.9f,
                0.05f,
                -1.0f,
                1.15f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.75f,
                -0.1f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f
            )
        )
        FormationAttrs = Collections.unmodifiableMap<Int?, FloatArray?>(tempFormationAttrs)

        // Morale Attributes
        val tempMoraleAttrs: MutableMap<Int?, FloatArray?> = HashMap<Int?, FloatArray?>()
        tempMoraleAttrs.put(
            0,
            floatArrayOf(
                0.0f,
                1.25f,
                1.25f,
                1.25f,
                1.25f,
                1.2f,
                1.4f,
                0.15f,
                4.0f,
                1.2f,
                1.2f,
                1.2f,
                1.5f,
                1.5f,
                1.5f,
                0.25f,
                0.5f,
                0.5f,
                0.5f,
                0.5f,
                0.25f
            )
        )
        tempMoraleAttrs.put(
            1,
            floatArrayOf(
                0.0f,
                1.1f,
                1.1f,
                1.1f,
                1.1f,
                1.1f,
                1.2f,
                0.08f,
                2.0f,
                1.1f,
                1.1f,
                1.1f,
                1.25f,
                1.25f,
                1.25f,
                0.12f,
                0.25f,
                0.25f,
                0.25f,
                0.25f,
                0.15f
            )
        )
        tempMoraleAttrs.put(
            2,
            floatArrayOf(
                0.0f,
                0.9f,
                0.9f,
                0.9f,
                0.9f,
                0.9f,
                0.8f,
                -0.08f,
                -2.0f,
                0.9f,
                0.9f,
                0.9f,
                0.75f,
                0.75f,
                0.75f,
                -0.12f,
                -0.25f,
                -0.25f,
                -0.25f,
                -0.25f,
                -0.1f
            )
        )
        tempMoraleAttrs.put(
            3,
            floatArrayOf(
                0.0f,
                0.75f,
                0.75f,
                0.75f,
                0.75f,
                0.8f,
                0.6f,
                -0.15f,
                -4.0f,
                0.8f,
                0.8f,
                0.8f,
                0.5f,
                0.5f,
                0.5f,
                -0.25f,
                -0.5f,
                -0.5f,
                -0.5f,
                -0.5f,
                -0.2f
            )
        )
        MoraleAttrs = Collections.unmodifiableMap<Int?, FloatArray?>(tempMoraleAttrs)
    }

    @JvmStatic
    val resetFormationValue: FloatArray
        get() = floatArrayOf(
            0.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            0.0f,
            0.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f
        )

    @JvmStatic
    val resetMoraleValue: FloatArray
        get() = floatArrayOf(
            0.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            0.0f,
            0.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            1.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f
        )
}
