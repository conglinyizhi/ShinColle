package org.trp.shincolle.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.trp.shincolle.Shincolle;
import org.trp.shincolle.entity.base.ShipBrainMemory;

import java.util.Optional;

public final class ModMemoryModules {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES =
            DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, Shincolle.MODID);

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<ShipBrainMemory.PointerTargetMemory>> SHIP_POINTER_TARGET =
            MEMORY_MODULE_TYPES.register("ship_pointer_target", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<ShipBrainMemory.GuardTargetMemory>> SHIP_GUARD_TARGET =
            MEMORY_MODULE_TYPES.register("ship_guard_target", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<ShipBrainMemory.FollowStateMemory>> SHIP_FOLLOW_STATE =
            MEMORY_MODULE_TYPES.register("ship_follow_state", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<ShipBrainMemory.PassiveCombatStateMemory>> SHIP_PASSIVE_COMBAT_STATE =
            MEMORY_MODULE_TYPES.register("ship_passive_combat_state", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<ShipBrainMemory.RecoveryStateMemory>> SHIP_POINTER_RECOVERY =
            MEMORY_MODULE_TYPES.register("ship_pointer_recovery", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<ShipBrainMemory.RecoveryStateMemory>> SHIP_GUARD_RECOVERY =
            MEMORY_MODULE_TYPES.register("ship_guard_recovery", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<ShipBrainMemory.RecoveryStateMemory>> SHIP_FOLLOW_RECOVERY =
            MEMORY_MODULE_TYPES.register("ship_follow_recovery", () -> new MemoryModuleType<>(Optional.empty()));
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<ShipBrainMemory.RecoveryStateMemory>> SHIP_COMBAT_RECOVERY =
            MEMORY_MODULE_TYPES.register("ship_combat_recovery", () -> new MemoryModuleType<>(Optional.empty()));

    private ModMemoryModules() {
    }
}
