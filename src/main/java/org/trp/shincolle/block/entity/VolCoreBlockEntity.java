package org.trp.shincolle.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.trp.shincolle.Config;
import org.trp.shincolle.entity.EntityAircraftBase;
import org.trp.shincolle.entity.base.EntityMountBase;
import org.trp.shincolle.entity.base.EntityShipBase;
import org.trp.shincolle.init.ModBlockEntities;
import org.trp.shincolle.init.ModItems;
import org.trp.shincolle.init.ModParticles;
import org.trp.shincolle.menu.VolCoreMenu;
import org.trp.shincolle.utility.PerformanceTrace;

import java.util.List;

public class VolCoreBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_COUNT = 9;
    private final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            markForSync();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(ModItems.GRUDGE.get()) || stack.is(ModItems.GRUDGE_BLOCK.get());
        }
    };

    private int remainedPower = 0;
    private boolean btnActive = false;
    private int syncTime = 0;

    public VolCoreBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.VOL_CORE.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, VolCoreBlockEntity blockEntity) {
        if (level.isClientSide) return;
        boolean tracing = PerformanceTrace.enabled();
        long start = tracing ? PerformanceTrace.now() : 0L;
        try {
            blockEntity.serverTickInternal(level, pos);
        } finally {
            if (tracing) {
                long elapsed = PerformanceTrace.elapsed(start);
                PerformanceTrace.addBlockEntityTime(elapsed);
                PerformanceTrace.logSlowBlockEntityTick(blockEntity, "vol_core", elapsed,
                        "active=" + blockEntity.btnActive
                                + " working=" + blockEntity.isWorking()
                                + " power=" + blockEntity.remainedPower
                                + " syncTime=" + blockEntity.syncTime);
            }
        }
    }

    private void serverTickInternal(Level level, BlockPos pos) {
        this.syncTime++;

        if (this.syncTime % 16 == 0) {
            boolean canWork = this.remainedPower >= Config.volCoreConsumeSpeed;
            if (canWork && this.btnActive) {
                this.remainedPower -= Config.volCoreConsumeSpeed;
                markForSync();
            }
            if (isWorking() && level instanceof ServerLevel serverLevel) {
                double bx = pos.getX() + 0.5;
                double by = pos.getY() + 1.5;
                double bz = pos.getZ() + 0.5;
                for (int i = 0; i < 25; i++) {
                    double px = bx + (level.getRandom().nextFloat() * 13.0f) - 6.5;
                    double py = by + (level.getRandom().nextFloat() * 13.0f) - 4.5;
                    double pz = bz + (level.getRandom().nextFloat() * 13.0f) - 6.5;
                    serverLevel.sendParticles(
                        ModParticles.PARTICLE_SPRAY.get(),
                        px, py, pz,
                        0,
                        0.0, 0.05, 0.0,
                        1.0
                    );
                }
            }
        }

        if (this.syncTime % 32 == 0) {
            decrItemFuel();
            if (isWorking()) {
                volcoreFunction();
            }
        }

        if (this.syncTime % 256 == 0 && isWorking()) {
            double dx = pos.getX() + 0.5;
            double dy = pos.getY() + 2.5;
            double dz = pos.getZ() + 0.5;
            AABB box = new AABB(dx - 6.0, dy - 6.0, dz - 6.0, dx + 6.0, dy + 6.0, dz + 6.0);
            List<EntityShipBase> slist = level.getEntitiesOfClass(EntityShipBase.class, box);

            if (!slist.isEmpty()) {
                int emotes = level.getRandom().nextInt(11);
                for (EntityShipBase ship : slist) {
                    if (ship.isAlive()) {
                        ship.applyParticleEmotion(emotes);
                    }
                }
            }
        }
    }

    private void decrItemFuel() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            int fuelx = 0;
            if (stack.is(ModItems.GRUDGE.get())) {
                fuelx = Config.volCoreFuelMagnitude;
            } else if (stack.is(ModItems.GRUDGE_BLOCK.get())) {
                fuelx = Config.volCoreFuelMagnitude * 9;
            }

            if (fuelx > 0 && remainedPower + fuelx <= Config.volCorePowerMax) {
                stack.shrink(1);
                remainedPower += fuelx;
                markForSync();
                break;
            }
        }
    }

    private boolean isWorking() {
        return btnActive && remainedPower >= Config.volCoreConsumeSpeed;
    }

    private void volcoreFunction() {
        if (level == null) return;

        double dx = worldPosition.getX() + 0.5;
        double dy = worldPosition.getY() + 0.5;
        double dz = worldPosition.getZ() + 0.5;
        AABB box = new AABB(dx - 6.0, dy - 6.0, dz - 6.0, dx + 6.0, dy + 6.0, dz + 6.0);

        if (isNearbyLiquid()) {
            List<EntityShipBase> slist = level.getEntitiesOfClass(EntityShipBase.class, box);
            for (EntityShipBase s : slist) {
                if (s.isTame() && s.isInWaterOrBubble()) {
                    if (s.getHealth() < s.getMaxHealth()) {
                        s.heal(s.getMaxHealth() * 0.01f + 4.0f);
                    }
                    if (s.getMorale() < 9180) {
                        s.addMorale(80);
                    }
                }
            }
        } else {
            List<LivingEntity> elist = level.getEntitiesOfClass(LivingEntity.class, box);
            DamageSource fireSource = level.damageSources().onFire();
            for (LivingEntity ent : elist) {
                if (ent instanceof EntityShipBase
                        || ent instanceof EntityMountBase
                        || ent instanceof EntityAircraftBase
                        || ent instanceof EntityShipBase ship && ship.isHostileShipMob()
                        || ent instanceof Player) {
                    continue;
                }

                ent.igniteForTicks(40);
                ent.hurt(fireSource, 4.0f);
            }
        }
    }

    private boolean isNearbyLiquid() {
        if (level == null) return false;
        for (Direction dir : Direction.values()) {
            if (!level.getFluidState(worldPosition.relative(dir)).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public int getRemainedPower() {
        return remainedPower;
    }

    public void setRemainedPower(int remainedPower) {
        this.remainedPower = remainedPower;
        markForSync();
    }

    public boolean isBtnActive() {
        return btnActive;
    }

    public void setBtnActive(boolean btnActive) {
        this.btnActive = btnActive;
        markForSync();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Power", remainedPower);
        tag.putBoolean("Active", btnActive);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
        remainedPower = tag.getInt("Power");
        btnActive = tag.getBoolean("Active");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("tile.shincolle.BlockVolCore.name");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new VolCoreMenu(containerId, playerInventory, this);
    }

    public void markForSync() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
