package org.trp.shincolle.init

import com.mojang.serialization.Codec
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.trp.shincolle.Shincolle
import org.trp.shincolle.entity.base.ShipBrainMemory.*
import java.util.*
import java.util.function.Supplier

object ModMemoryModules {
    val MEMORY_MODULE_TYPES: DeferredRegister<MemoryModuleType<*>?> =
        DeferredRegister.create<MemoryModuleType<*>?>(Registries.MEMORY_MODULE_TYPE, Shincolle.MODID)

    @JvmField
    val SHIP_POINTER_TARGET: DeferredHolder<MemoryModuleType<*>?, MemoryModuleType<PointerTargetMemory?>?> =
        MEMORY_MODULE_TYPES.register<MemoryModuleType<PointerTargetMemory?>?>("ship_pointer_target", Supplier {
            MemoryModuleType<PointerTargetMemory?>(
                Optional.empty<Codec<PointerTargetMemory?>?>()
            )
        })
    @JvmField
    val SHIP_GUARD_TARGET: DeferredHolder<MemoryModuleType<*>?, MemoryModuleType<GuardTargetMemory?>?> =
        MEMORY_MODULE_TYPES.register<MemoryModuleType<GuardTargetMemory?>?>("ship_guard_target", Supplier {
            MemoryModuleType<GuardTargetMemory?>(
                Optional.empty<Codec<GuardTargetMemory?>?>()
            )
        })
    @JvmField
    val SHIP_FOLLOW_STATE: DeferredHolder<MemoryModuleType<*>?, MemoryModuleType<FollowStateMemory?>?> =
        MEMORY_MODULE_TYPES.register<MemoryModuleType<FollowStateMemory?>?>("ship_follow_state", Supplier {
            MemoryModuleType<FollowStateMemory?>(
                Optional.empty<Codec<FollowStateMemory?>?>()
            )
        })
    @JvmField
    val SHIP_PASSIVE_COMBAT_STATE: DeferredHolder<MemoryModuleType<*>?, MemoryModuleType<PassiveCombatStateMemory?>?> =
        MEMORY_MODULE_TYPES.register<MemoryModuleType<PassiveCombatStateMemory?>?>(
            "ship_passive_combat_state",
            Supplier {
                MemoryModuleType<PassiveCombatStateMemory?>(
                    Optional.empty<Codec<PassiveCombatStateMemory?>?>()
                )
            })
    @JvmField
    val SHIP_POINTER_RECOVERY: DeferredHolder<MemoryModuleType<*>?, MemoryModuleType<RecoveryStateMemory?>?> =
        MEMORY_MODULE_TYPES.register<MemoryModuleType<RecoveryStateMemory?>?>("ship_pointer_recovery", Supplier {
            MemoryModuleType<RecoveryStateMemory?>(
                Optional.empty<Codec<RecoveryStateMemory?>?>()
            )
        })
    @JvmField
    val SHIP_GUARD_RECOVERY: DeferredHolder<MemoryModuleType<*>?, MemoryModuleType<RecoveryStateMemory?>?> =
        MEMORY_MODULE_TYPES.register<MemoryModuleType<RecoveryStateMemory?>?>("ship_guard_recovery", Supplier {
            MemoryModuleType<RecoveryStateMemory?>(
                Optional.empty<Codec<RecoveryStateMemory?>?>()
            )
        })
    @JvmField
    val SHIP_FOLLOW_RECOVERY: DeferredHolder<MemoryModuleType<*>?, MemoryModuleType<RecoveryStateMemory?>?> =
        MEMORY_MODULE_TYPES.register<MemoryModuleType<RecoveryStateMemory?>?>("ship_follow_recovery", Supplier {
            MemoryModuleType<RecoveryStateMemory?>(
                Optional.empty<Codec<RecoveryStateMemory?>?>()
            )
        })
    @JvmField
    val SHIP_COMBAT_RECOVERY: DeferredHolder<MemoryModuleType<*>?, MemoryModuleType<RecoveryStateMemory?>?> =
        MEMORY_MODULE_TYPES.register<MemoryModuleType<RecoveryStateMemory?>?>("ship_combat_recovery", Supplier {
            MemoryModuleType<RecoveryStateMemory?>(
                Optional.empty<Codec<RecoveryStateMemory?>?>()
            )
        })
}
