package org.trp.shincolle.entity.base.tick

import org.trp.shincolle.api.ApiCallSafety
import org.trp.shincolle.api.equip.IShipEquip
import org.trp.shincolle.api.equip.ShipEquipRegistry
import org.trp.shincolle.entity.base.EntityShipBase

/**
 * Refreshes special equipment effects for items in the ship's inventory.
 */
object ShipEquipEffectsTickHandler : ShipTickHandler {

    override fun tick(ship: EntityShipBase): Boolean {
        val inv = ship.inventory ?: return true
        for (slot in 0..<inv.slots) {
            val stack = inv.getStackInSlot(slot)
            if (stack.isEmpty) continue
            val item = stack.item
            if (item is IShipEquip) {
                val effect = ShipEquipRegistry.getEffect(
                    ApiCallSafety.runWithDefault(
                        "IShipEquip.getEquipTypeId", -1
                    ) { item.getEquipTypeId(stack) }
                )
                if (effect != null) {
                    ApiCallSafety.run("ShipEquipSpecialEffect.tick") {
                        effect.tick(ship, stack)
                    }
                }
            }
        }
        return true
    }
}
