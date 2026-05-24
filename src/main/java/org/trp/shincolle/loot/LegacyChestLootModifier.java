package org.trp.shincolle.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.trp.shincolle.Config;
import org.trp.shincolle.init.ModLootModifiers;
import org.trp.shincolle.item.LegacyEquipItem;

import java.util.ArrayList;
import java.util.List;

public class LegacyChestLootModifier extends LootModifier {
    public static final MapCodec<LegacyChestLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> codecStart(instance)
            .and(Codec.STRING.fieldOf("category").forGetter(LegacyChestLootModifier::category))
            .apply(instance, LegacyChestLootModifier::new));

    private final String category;

    public LegacyChestLootModifier(LootItemCondition[] conditions, String category) {
        super(conditions);
        this.category = category;
    }

    public String category() {
        return this.category;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        List<Config.LootEntry> candidates = new ArrayList<>();
        int totalWeight = 0;
        for (Config.LootEntry entry : Config.lootEntries) {
            if (!matchesCategory(entry.chestId(), this.category)) continue;
            if (context.getRandom().nextFloat() > entry.chance()) continue;
            candidates.add(entry);
            totalWeight += entry.weight();
        }

        if (candidates.isEmpty() || totalWeight <= 0) {
            return generatedLoot;
        }

        int rolls = Math.max(1, candidates.size() / 2 + 1);
        RandomSource random = context.getRandom();
        for (int i = 0; i < rolls; i++) {
            Config.LootEntry chosen = chooseWeighted(candidates, totalWeight, random.nextInt(totalWeight));
            ItemStack stack = createStack(chosen, random);
            if (!stack.isEmpty()) {
                generatedLoot.add(stack);
            }
        }
        return generatedLoot;
    }

    private static Config.LootEntry chooseWeighted(List<Config.LootEntry> entries, int totalWeight, int roll) {
        Config.LootEntry chosen = entries.get(0);
        for (Config.LootEntry entry : entries) {
            roll -= entry.weight();
            if (roll < 0) {
                chosen = entry;
                break;
            }
        }
        return chosen;
    }

    private static ItemStack createStack(Config.LootEntry entry, RandomSource random) {
        int count = entry.min();
        if (entry.max() > entry.min()) {
            count += random.nextInt(entry.max() - entry.min() + 1);
        }

        ItemStack stack = new ItemStack(entry.item(), count);
        if (entry.itemMeta() == -1 && stack.getItem() instanceof LegacyEquipItem equipItem) {
            int variant = random.nextInt(equipItem.getVariantCount());
            stack = equipItem.createVariantStack(variant);
            stack.setCount(count);
        }
        return stack;
    }

    private static boolean matchesCategory(int chestId, String category) {
        if (category.startsWith("id:")) {
            try {
                return chestId == Integer.parseInt(category.substring(3));
            } catch (NumberFormatException ignored) {
                return false;
            }
        }
        return switch (category) {
            case "basic" -> chestId >= 0 && chestId <= 3;
            case "mid" -> chestId >= 4 && chestId <= 8;
            case "high" -> chestId == 9;
            case "trial" -> chestId >= 7 && chestId <= 9;
            default -> false;
        };
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return ModLootModifiers.LEGACY_CHEST_LOOT.get();
    }
}
