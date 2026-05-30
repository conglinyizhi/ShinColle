package org.trp.shincolle.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import org.trp.shincolle.entity.base.EntityShipBase;

import java.util.function.Supplier;

public class BossSpawnEggItem extends DeferredSpawnEggItem {
    private final Supplier<? extends EntityType<? extends Mob>> typeSupplier;

    public BossSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type,
                             int primaryColor, int secondaryColor, Properties properties) {
        super(type, primaryColor, secondaryColor, properties);
        this.typeSupplier = type;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) return super.useOn(context);

        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        BlockPos clickPos = context.getClickedPos().relative(context.getClickedFace());

        // Inject entity type without Owner/Tame -> spawns untamed (hostile)
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(this.typeSupplier.get());
        if (key == null) return super.useOn(context);

        stack.update(DataComponents.ENTITY_DATA, CustomData.EMPTY, existingData -> existingData.update(tag -> {
            tag.putString("id", key.toString());
            tag.remove("Owner");
            tag.remove("Tame");
        }));

        InteractionResult result = super.useOn(context);

        // Find the spawned ship: set boss scale, ammo, fuel
        if (result.consumesAction() && level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            EntityType<?> spawnedType = this.typeSupplier.get();
            int bossScale = 2 + level.random.nextInt(2); // 2 or 3 -> 2.0x or 2.5x
            for (Entity entity : serverLevel.getEntities((Entity) null, 
                    new net.minecraft.world.phys.AABB(clickPos).inflate(4.0D),
                    e -> e.getType() == spawnedType && e instanceof EntityShipBase)) {
                EntityShipBase ship = (EntityShipBase) entity;
                ship.initializeHostileSpawnState(bossScale);
                ship.setAmmoLight(ship.getAmmoLight() + 128);
                ship.setAmmoHeavy(ship.getAmmoHeavy() + 64);
                ship.setFuel(Math.max(ship.getFuel(), 5000));
                break;
            }
        }

        return result;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return super.use(level, player, hand);

        ItemStack stack = player.getItemInHand(hand);

        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(this.typeSupplier.get());
        if (key == null) return super.use(level, player, hand);

        stack.update(DataComponents.ENTITY_DATA, CustomData.EMPTY, existingData -> existingData.update(tag -> {
            tag.putString("id", key.toString());
            tag.remove("Owner");
            tag.remove("Tame");
        }));

        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        if (result.getResult().consumesAction() && level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            EntityType<?> spawnedType = this.typeSupplier.get();
            BlockPos spawnPos = player.blockPosition();
            int bossScale = 2 + level.random.nextInt(2);
            for (Entity entity : serverLevel.getEntities((Entity) null,
                    new net.minecraft.world.phys.AABB(spawnPos).inflate(4.0D),
                    e -> e.getType() == spawnedType && e instanceof EntityShipBase)) {
                EntityShipBase ship = (EntityShipBase) entity;
                ship.initializeHostileSpawnState(bossScale);
                ship.setAmmoLight(ship.getAmmoLight() + 128);
                ship.setAmmoHeavy(ship.getAmmoHeavy() + 64);
                ship.setFuel(Math.max(ship.getFuel(), 5000));
                break;
            }
        }

        return result;
    }
}
