package org.trp.shincolle.entity.base;

import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.TamableAnimal;
import org.trp.shincolle.Config;
import java.util.UUID;
import org.trp.shincolle.entity.EntityAircraftBase;
import org.trp.shincolle.entity.base.EntityMountBase;
import org.trp.shincolle.entity.projectile.EntityAbyssMissile;
import org.trp.shincolle.init.ModItems;
import org.trp.shincolle.init.ModSounds;

class EntityShipBaseCombat {
    private static final float HEAVY_MISSILE_DAMAGE_MULTIPLIER = 1.4F;
    private static final float HEAVY_MISSILE_SPEED = 0.7F;
    private static final int HEAVY_MISSILE_LIFE = 200;
    private static final float HEAVY_MISSILE_EXPLOSION_RADIUS = 3.5F;
    private static final float MIN_FIRE_CLEAR_DISTANCE = 4.5F;
    private static final float TORPEDO_SPEED_STEP = 0.025F;
    private static final float TORPEDO_ACCEL_STEP = 0.004F;
    private static final String TAG_POTION_LIST = "PList";
    private static final String TAG_POTION_ID = "PID";
    private static final String TAG_POTION_LEVEL = "PLV";
    private static final String TAG_POTION_TIME = "PTick";
    private static final String TAG_POTION_CHANCE = "PChance";

    private static final int AMMO_LIGHT_VALUE = 30;
    private static final int AMMO_LIGHT_CONTAINER_VALUE = 270;
    private static final int AMMO_HEAVY_VALUE = 15;
    private static final int AMMO_HEAVY_CONTAINER_VALUE = 135;
    private static final int AIRCRAFT_LIGHT_AMMO_COST = 6;
    private static final int AIRCRAFT_HEAVY_AMMO_COST = 2;
    private static final int AIRCRAFT_RECOVERY_BASE_DELAY = 120;
    private static final int AIRCRAFT_COOLDOWN_FALLBACK = 40;

    private int aircraftRecoveryTick = 0;
    private int aircraftLaunchDelay = 20;
    private boolean aircraftLaunchTypeLight = false;

    private final EntityShipBase ship;

    EntityShipBaseCombat(EntityShipBase ship) {
        this.ship = ship;
    }

    boolean canUseLightAmmo() {
        return this.ship.isStateGuiBtn1()
                && this.ship.isStateLightAttack()
                && this.ship.getAmmoLight() > 0;
    }

    boolean canUseHeavyAmmo() {
        return this.ship.isStateGuiBtn2()
                && this.ship.isStateHeavyAttack()
                && this.ship.getAmmoHeavy() > 0;
    }

    boolean canUseMeleeAttack() {
        return this.ship.isStateCanMelee();
    }

    boolean canUseLightAircraft() {
        return this.ship.isStateGuiBtn3()
                && this.ship.isStateLightAircraftAttack()
                && this.ship.hasAirLight()
                && this.ship.getAmmoLight() >= AIRCRAFT_LIGHT_AMMO_COST;
    }

    boolean canUseHeavyAircraft() {
        return this.ship.isStateGuiBtn4()
                && this.ship.isStateHeavyAircraftAttack()
                && this.ship.hasAirHeavy()
                && this.ship.getAmmoHeavy() >= AIRCRAFT_HEAVY_AMMO_COST;
    }

    boolean hasAircraftAttackEnabled() {
        return canUseLightAircraft() || canUseHeavyAircraft();
    }

    void tickAircraftRecovery() {
        if (!this.ship.supportsAircraftCombat()) {
            return;
        }

        int maxLight = getMaxAircraftLight();
        int maxHeavy = getMaxAircraftHeavy();
        if (maxLight <= 0 && maxHeavy <= 0) {
            return;
        }

        if (this.ship.getNumAircraftLight() <= 0 && this.ship.getNumAircraftHeavy() <= 0 && this.ship.tickCount < 20) {
            this.ship.setNumAircraftLight(maxLight);
            this.ship.setNumAircraftHeavy(maxHeavy);
        }

        if (this.ship.getNumAircraftLight() > maxLight) {
            this.ship.setNumAircraftLight(maxLight);
        }
        if (this.ship.getNumAircraftHeavy() > maxHeavy) {
            this.ship.setNumAircraftHeavy(maxHeavy);
        }

        this.aircraftRecoveryTick--;
        if (this.aircraftRecoveryTick > 0) {
            return;
        }

        this.aircraftRecoveryTick = Math.max(20, AIRCRAFT_RECOVERY_BASE_DELAY);
        if (this.ship.getNumAircraftLight() < maxLight) {
            this.ship.setNumAircraftLight(this.ship.getNumAircraftLight() + 1);
        }
        if (this.ship.getNumAircraftHeavy() < maxHeavy) {
            this.ship.setNumAircraftHeavy(this.ship.getNumAircraftHeavy() + 1);
        }
    }

    boolean tryPerformAircraftCycle(Entity target) {
        if (!this.ship.supportsAircraftCombat()) {
            return false;
        }
        if (!(this.ship.level() instanceof ServerLevel)) {
            return false;
        }
        if (target == null || !target.isAlive()) {
            return false;
        }

        this.aircraftLaunchDelay--;
        if (!this.ship.isStateLightAircraftAttack()) {
            this.aircraftLaunchTypeLight = false;
        }
        if (!this.ship.isStateHeavyAircraftAttack()) {
            this.aircraftLaunchTypeLight = true;
        }

        if (this.aircraftLaunchDelay > 0) {
            return false;
        }

        boolean launched = false;
        if (this.aircraftLaunchTypeLight) {
            launched = performLightAircraftAttack(target);
            if (!launched) {
                launched = performHeavyAircraftAttack(target);
            }
        } else {
            launched = performHeavyAircraftAttack(target);
            if (!launched) {
                launched = performLightAircraftAttack(target);
            }
        }

        this.aircraftLaunchTypeLight = !this.aircraftLaunchTypeLight;
        if (launched) {
            int lightDelay = this.ship.getLegacyShipStats().getLightDelay();
            int heavyDelay = this.ship.getLegacyShipStats().getHeavyDelay();
            int delay = Math.max(20, Math.max(lightDelay, heavyDelay));
            this.aircraftLaunchDelay = delay;
            return true;
        }

        this.aircraftLaunchDelay = AIRCRAFT_COOLDOWN_FALLBACK;
        return false;
    }

    void recalculateAmmoCounts() {
        if (this.ship.hasCreativeDebugger()) {
            this.ship.setAmmoLight(30000);
            this.ship.setAmmoHeavy(30000);
            return;
        }
        int light = 0;
        int heavy = 0;
        for (int i = 0; i < this.ship.getInventory().getSlots(); i++) {
            ItemStack stack = this.ship.getInventory().getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (isLightAmmo(stack)) {
                light += stack.getCount() * AMMO_LIGHT_VALUE;
            } else if (isLightAmmoContainer(stack)) {
                light += stack.getCount() * AMMO_LIGHT_CONTAINER_VALUE;
            } else if (isHeavyAmmo(stack)) {
                heavy += stack.getCount() * AMMO_HEAVY_VALUE;
            } else if (isHeavyAmmoContainer(stack)) {
                heavy += stack.getCount() * AMMO_HEAVY_CONTAINER_VALUE;
            }
        }
        this.ship.setAmmoLight(light);
        this.ship.setAmmoHeavy(heavy);
    }

    void performLightAttack(Entity target) {
        if (!canUseLightAmmo()) {
            return;
        }
        if (!(this.ship.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (target == null || !target.isAlive()) {
            return;
        }
        if (isSameOwner(target)) {
            return;
        }
        if (!consumeLightAmmo(1)) {
            return;
        }

        float damage = this.ship.getLegacyShipStats().getFirepower();
        if (damage <= 0.0F) {
            damage = 2.0F;
        }
        target.hurt(this.ship.damageSources().mobAttack(this.ship), damage);
        this.ship.spawnLightAttackTargetParticles(serverLevel, target);
        this.ship.spawnLightAttackMuzzleParticles(serverLevel, target);
        this.ship.playSound(ModSounds.SHIP_FIRELIGHT.get(), Math.max(0.0F, org.trp.shincolle.Config.volumeAttack),
                this.ship.getRandom().nextFloat() * 0.12F + 0.98F);
        this.ship.setAttackTick(50);
        this.ship.setFuel(this.ship.getFuel() - org.trp.shincolle.Config.fuelConsumeActionLight);
        this.ship.applyEmotesReaction(3);
    }

    boolean performHeavyAttack(Entity target) {
        if (!canUseHeavyAmmo()) {
            return false;
        }
        if (!(this.ship.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (isSameOwner(target)) {
            return false;
        }
        if (Config.enableFiringLineCheck && !hasClearFiringLine(target)) {
            return false;
        }
        if (!consumeHeavyAmmo(1)) {
            return false;
        }

        float damage = this.ship.getLegacyShipStats().getFirepower();
        if (damage <= 0.0F) {
            damage = 4.0F;
        }
        float missileDamage = damage * HEAVY_MISSILE_DAMAGE_MULTIPLIER;

        EntityAbyssMissile missile = createHeavyMissile(serverLevel, target, missileDamage);
        serverLevel.addFreshEntity(missile);
        this.ship.playSound(ModSounds.SHIP_FIREHEAVY.get(), Math.max(0.0F, org.trp.shincolle.Config.volumeAttack),
                this.ship.getRandom().nextFloat() * 0.12F + 0.83F);
        this.ship.setAttackTick(50);
        this.ship.setFuel(this.ship.getFuel() - org.trp.shincolle.Config.fuelConsumeActionHeavy);
        this.ship.applyEmotesReaction(3);
        return true;
    }

    private EntityAbyssMissile createHeavyMissile(ServerLevel serverLevel, Entity target, float damage) {
        int specialAmmoVariant = this.ship.getSpecialAmmoVariant();
        int torpedoSpeedLevel = this.ship.getTorpedoSpeedLevel();

        EntityAbyssMissile.MoveType moveType = EntityAbyssMissile.MoveType.DIRECT;
        float speed = HEAVY_MISSILE_SPEED;
        float accY1 = 1.04F;
        float accY2 = 1.04F;
        float explosionRadius = HEAVY_MISSILE_EXPLOSION_RADIUS;
        Vec3 presetVelocity = null;

        if (torpedoSpeedLevel > 0) {
            moveType = EntityAbyssMissile.MoveType.TORPEDO;
            speed += torpedoSpeedLevel * TORPEDO_SPEED_STEP;
            accY2 = 1.05F + torpedoSpeedLevel * TORPEDO_ACCEL_STEP;
        }

        if (specialAmmoVariant == 5) {
            moveType = EntityAbyssMissile.MoveType.ARC_HOMING;
            accY1 = 0.9F;
            accY2 = 0.9F;
            explosionRadius += 0.5F;
        } else if (specialAmmoVariant == 8) {
            moveType = EntityAbyssMissile.MoveType.ARC;
            explosionRadius += 1.0F;
        } else if (specialAmmoVariant == 6) {
            moveType = EntityAbyssMissile.MoveType.DIRECT;
            accY1 = -0.045F;
            accY2 = -0.045F;
        }

        EntityAbyssMissile missile = new EntityAbyssMissile(serverLevel, this.ship, target, damage,
                moveType, speed, accY1, accY2, presetVelocity, HEAVY_MISSILE_LIFE, explosionRadius);
        if (specialAmmoVariant == 8) {
            missile.markClusterMain();
        } else if (specialAmmoVariant == 5) {
            missile.markBlackHole();
        }
        configureAmmoEffects(missile);
        return missile;
    }

    private void configureAmmoEffects(EntityAbyssMissile missile) {
        for (int i = 0; i < this.ship.getInventory().getSlots(); i++) {
            ItemStack stack = this.ship.getInventory().getStackInSlot(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof org.trp.shincolle.item.LegacyEquipItem equipItem)) {
                continue;
            }
            if (equipItem.getEquipTypeId(stack) != 29) {
                continue;
            }

            int variant = equipItem.getVariant(stack);
            switch (variant) {
                case 0 -> missile.addImpactEffect(MobEffects.POISON, 0, 120, 50);
                case 1 -> missile.addImpactEffect(MobEffects.POISON, 1, 120, 70);
                case 3 -> missile.addImpactEffect(MobEffects.CONFUSION, 0, 120, 50);
                case 4 -> missile.addImpactEffect(MobEffects.WITHER, 0, 100, 25);
                case 6 -> missile.addImpactEffect(MobEffects.LEVITATION, 0, 100, 50);
                case 7 -> addEnchantShellEffects(missile, stack);
                default -> {
                }
            }
        }
    }

    private void addEnchantShellEffects(EntityAbyssMissile missile, ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return;
        }

        CompoundTag tag = customData.copyTag();
        if (!tag.contains(TAG_POTION_LIST, Tag.TAG_LIST)) {
            return;
        }

        ListTag effectList = tag.getList(TAG_POTION_LIST, Tag.TAG_COMPOUND);
        for (int i = 0; i < effectList.size(); i++) {
            CompoundTag effectTag = effectList.getCompound(i);
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.byId(effectTag.getInt(TAG_POTION_ID));
            if (effect == null) {
                continue;
            }
            missile.addImpactEffect(
                    Holder.direct(effect),
                    effectTag.getInt(TAG_POTION_LEVEL),
                    effectTag.getInt(TAG_POTION_TIME),
                    effectTag.getInt(TAG_POTION_CHANCE)
            );
        }
    }

    boolean consumeHeavyAmmo(int amount) {
        if (amount <= 0) {
            return true;
        }
        if (this.ship.hasCreativeDebugger()) {
            return true;
        }
        int remaining = amount;
        for (int i = 0; i < this.ship.getInventory().getSlots() && remaining > 0; i++) {
            ItemStack stack = this.ship.getInventory().getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (isHeavyAmmo(stack)) {
                int take = Math.min(stack.getCount(), remaining);
                ItemStack updated = stack.copy();
                updated.shrink(take);
                this.ship.getInventory().setStackInSlot(i, updated);
                remaining -= take;
            } else if (isHeavyAmmoContainer(stack)) {
                if (stack.getCount() <= 0) {
                    continue;
                }
                ItemStack updated = stack.copy();
                updated.shrink(1);
                this.ship.getInventory().setStackInSlot(i, updated);

                int used = Math.min(remaining, AMMO_HEAVY_CONTAINER_VALUE);
                int leftover = AMMO_HEAVY_CONTAINER_VALUE - used;
                remaining -= used;

                if (leftover > 0) {
                    insertAmmoRemainder(ModItems.AMMO_HEAVY.get(), leftover, i);
                }
            }
        }
        return remaining <= 0;
    }

    boolean consumeLightAmmo(int amount) {
        if (amount <= 0) {
            return true;
        }
        if (this.ship.hasCreativeDebugger()) {
            return true;
        }
        int remaining = amount;
        for (int i = 0; i < this.ship.getInventory().getSlots() && remaining > 0; i++) {
            ItemStack stack = this.ship.getInventory().getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (isLightAmmo(stack)) {
                int take = Math.min(stack.getCount(), remaining);
                ItemStack updated = stack.copy();
                updated.shrink(take);
                this.ship.getInventory().setStackInSlot(i, updated);
                remaining -= take;
            } else if (isLightAmmoContainer(stack)) {
                if (stack.getCount() <= 0) {
                    continue;
                }
                ItemStack updated = stack.copy();
                updated.shrink(1);
                this.ship.getInventory().setStackInSlot(i, updated);

                int used = Math.min(remaining, AMMO_LIGHT_CONTAINER_VALUE);
                int leftover = AMMO_LIGHT_CONTAINER_VALUE - used;
                remaining -= used;

                if (leftover > 0) {
                    insertAmmoRemainder(ModItems.AMMO_LIGHT.get(), leftover, i);
                }
            }
        }
        return remaining <= 0;
    }

    private boolean isLightAmmo(ItemStack stack) {
        return stack.is(ModItems.AMMO_LIGHT.get());
    }

    void returnAircraftToDeck(boolean lightAircraft) {
        if (!this.ship.supportsAircraftCombat()) {
            return;
        }
        if (lightAircraft) {
            int max = getMaxAircraftLight();
            this.ship.setNumAircraftLight(Math.min(max, this.ship.getNumAircraftLight() + 1));
        } else {
            int max = getMaxAircraftHeavy();
            this.ship.setNumAircraftHeavy(Math.min(max, this.ship.getNumAircraftHeavy() + 1));
        }
    }

    private boolean isLightAmmoContainer(ItemStack stack) {
        return stack.is(ModItems.AMMO_LIGHT_CONTAINER.get());
    }

    private boolean isHeavyAmmo(ItemStack stack) {
        return stack.is(ModItems.AMMO_HEAVY.get());
    }

    private boolean isHeavyAmmoContainer(ItemStack stack) {
        return stack.is(ModItems.AMMO_HEAVY_CONTAINER.get());
    }

    private void insertAmmoRemainder(net.minecraft.world.item.Item item, int count, int avoidSlot) {
        if (count <= 0) {
            return;
        }
        int remaining = count;
        int maxStackSize = item.getDefaultInstance().getMaxStackSize();
        while (remaining > 0) {
            ItemStack stack = new ItemStack(item, Math.min(remaining, maxStackSize));
            ItemStack leftover = stack;
            for (int i = 0; i < this.ship.getInventory().getSlots() && !leftover.isEmpty(); i++) {
                if (i == avoidSlot) {
                    continue;
                }
                leftover = this.ship.getInventory().insertItem(i, leftover, false);
            }
            int inserted = stack.getCount() - leftover.getCount();
            remaining -= inserted;
            if (!leftover.isEmpty()) {
                if (this.ship.level() instanceof ServerLevel serverLevel) {
                    serverLevel.addFreshEntity(new ItemEntity(serverLevel, this.ship.getX(), this.ship.getY(), this.ship.getZ(), leftover));
                }
                remaining -= leftover.getCount();
            }
        }
    }

    private int getMaxAircraftLight() {
        return 8 + this.ship.getLevel() / 5 + (int) (this.ship.getLevel() * this.ship.getAircraftLightLevelBonus());
    }

    private int getMaxAircraftHeavy() {
        return 4 + this.ship.getLevel() / 10 + (int) (this.ship.getLevel() * this.ship.getAircraftHeavyLevelBonus());
    }

    private boolean performLightAircraftAttack(Entity target) {
        if (!canUseLightAircraft()) {
            return false;
        }
        return spawnAircraft(target, true);
    }

    private boolean performHeavyAircraftAttack(Entity target) {
        if (!canUseHeavyAircraft()) {
            return false;
        }
        return spawnAircraft(target, false);
    }

    private boolean spawnAircraft(Entity target, boolean lightAircraft) {
        EntityType<? extends net.minecraft.world.entity.TamableAnimal> type = this.ship.getAttackAircraftType(lightAircraft);
        if (type == null || !(this.ship.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        Entity spawned = type.create(serverLevel);
        if (!(spawned instanceof EntityAircraftBase aircraft)) {
            return false;
        }

        if (lightAircraft) {
            if (!consumeLightAmmo(AIRCRAFT_LIGHT_AMMO_COST)) {
                return false;
            }
            this.ship.setNumAircraftLight(Math.max(0, this.ship.getNumAircraftLight() - 1));
            this.ship.setFuel(this.ship.getFuel() - org.trp.shincolle.Config.fuelConsumeActionLightAircraft);
        } else {
            if (!consumeHeavyAmmo(AIRCRAFT_HEAVY_AMMO_COST)) {
                return false;
            }
            this.ship.setNumAircraftHeavy(Math.max(0, this.ship.getNumAircraftHeavy() - 1));
            this.ship.setFuel(this.ship.getFuel() - org.trp.shincolle.Config.fuelConsumeActionHeavyAircraft);
        }

        double launchY = this.ship.getY() + this.ship.getAircraftLaunchHeight();
        aircraft.moveTo(this.ship.getX(), launchY, this.ship.getZ(), this.ship.getYRot(), this.ship.getXRot());
        aircraft.initCarrierMission(this.ship, target, lightAircraft);
        serverLevel.addFreshEntity(aircraft);

        this.ship.setAttackTick(50);
        this.ship.applyEmotesReaction(3);
        return true;
    }

    void resetAircraftLaunchDelay() {
        int lightDelay = this.ship.getLegacyShipStats().getLightDelay();
        int heavyDelay = this.ship.getLegacyShipStats().getHeavyDelay();
        this.aircraftLaunchDelay = Math.max(20, Math.max(lightDelay, heavyDelay));
    }

    /**
     * Quick check: is there at least MIN_FIRE_CLEAR_DISTANCE of open space
     * between the ship and the target? Prevents missiles from exploding
     * on blocks right in front of the ship (self-damage / 炸膛).
     */
    private boolean hasClearFiringLine(Entity target) {
        var level = this.ship.level();
        Vec3 start = this.ship.getEyePosition();
        Vec3 end = target.getEyePosition();
        Vec3 dir = end.subtract(start);
        double dist = dir.length();
        if (dist <= MIN_FIRE_CLEAR_DISTANCE) {
            return false; // Target too close, don't risk self-damage
        }
        // Raycast from ship toward target, check for blocks
        var ctx = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.ship);
        var hit = (BlockHitResult) level.clip(ctx);
        if (hit.getType() == HitResult.Type.BLOCK) {
            double blockDist = start.distanceTo(hit.getLocation());
            if (blockDist < MIN_FIRE_CLEAR_DISTANCE) {
                return false; // Block too close, skip firing
            }
        }
        return true;
    }

    /**
     * Check if the target is a ship (TamableAnimal) owned by the same player.
     */
    private boolean isSameOwner(Entity target) {
        UUID shipOwnerId = this.ship.getOwnerUUID();
        if (shipOwnerId == null) return false;

        if (target instanceof net.minecraft.world.entity.player.Player player) {
            return shipOwnerId.equals(player.getUUID());
        }
        if (target instanceof EntityShipBase shipTarget) {
            return shipOwnerId.equals(shipTarget.getOwnerUUID());
        }
        if (target instanceof TamableAnimal t) {
            return shipOwnerId.equals(t.getOwnerUUID());
        }
        if (target instanceof EntityMountBase mount) {
            EntityShipBase host = mount.getHost();
            if (host != null) {
                return shipOwnerId.equals(host.getOwnerUUID());
            }
            return shipOwnerId.equals(mount.getHostUUID());
        }
        if (target instanceof EntityAircraftBase aircraft) {
            return shipOwnerId.equals(aircraft.getOwnerUUID());
        }
        return false;
    }
}
