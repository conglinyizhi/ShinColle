package org.trp.shincolle.item

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import java.util.function.Supplier

open class ShipSpawnEggItem(
    type: Supplier<out EntityType<out Mob?>?>?,
    @JvmField val shipClass: ShipClass?,
    primaryColor: Int,
    secondaryColor: Int,
    properties: Properties?
) : OwnedSpawnEggItem(type, primaryColor, secondaryColor, properties)
