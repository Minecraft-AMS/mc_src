/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 */
package net.minecraft.data.server.loottable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.TypeSpecificPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

public abstract class EntityLootTableGenerator
implements LootTableGenerator {
    protected static final EntityPredicate.Builder NEEDS_ENTITY_ON_FIRE = EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onFire(true).build());
    private static final Set<EntityType<?>> ENTITY_TYPES_IN_MISC_GROUP_TO_CHECK = ImmutableSet.of(EntityType.PLAYER, EntityType.ARMOR_STAND, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER);
    private final FeatureSet requiredFeatures;
    private final FeatureSet featureSet;
    private final Map<EntityType<?>, Map<Identifier, LootTable.Builder>> lootTables = Maps.newHashMap();

    protected EntityLootTableGenerator(FeatureSet requiredFeatures) {
        this(requiredFeatures, requiredFeatures);
    }

    protected EntityLootTableGenerator(FeatureSet requiredFeatures, FeatureSet featureSet) {
        this.requiredFeatures = requiredFeatures;
        this.featureSet = featureSet;
    }

    protected static LootTable.Builder createForSheep(ItemConvertible item) {
        return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with(ItemEntry.builder(item))).pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with(LootTableEntry.builder(EntityType.SHEEP.getLootTableId())));
    }

    public abstract void generate();

    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> exporter) {
        this.generate();
        HashSet set = Sets.newHashSet();
        Registries.ENTITY_TYPE.streamEntries().forEach(entityType -> {
            EntityType entityType2 = (EntityType)entityType.value();
            if (!entityType2.isEnabled(this.requiredFeatures)) {
                return;
            }
            if (EntityLootTableGenerator.shouldCheck(entityType2)) {
                Map<Identifier, LootTable.Builder> map = this.lootTables.remove(entityType2);
                Identifier identifier = entityType2.getLootTableId();
                if (!(identifier.equals(LootTables.EMPTY) || !entityType2.isEnabled(this.featureSet) || map != null && map.containsKey(identifier))) {
                    throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", identifier, entityType.registryKey().getValue()));
                }
                if (map != null) {
                    map.forEach((lootTableId, lootTableBuilder) -> {
                        if (!set.add(lootTableId)) {
                            throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", lootTableId, entityType.registryKey().getValue()));
                        }
                        exporter.accept((Identifier)lootTableId, (LootTable.Builder)lootTableBuilder);
                    });
                }
            } else {
                Map<Identifier, LootTable.Builder> map = this.lootTables.remove(entityType2);
                if (map != null) {
                    throw new IllegalStateException(String.format(Locale.ROOT, "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot", map.keySet().stream().map(Identifier::toString).collect(Collectors.joining(",")), entityType.registryKey().getValue()));
                }
            }
        });
        if (!this.lootTables.isEmpty()) {
            throw new IllegalStateException("Created loot tables for entities not supported by datapack: " + this.lootTables.keySet());
        }
    }

    private static boolean shouldCheck(EntityType<?> entityType) {
        return ENTITY_TYPES_IN_MISC_GROUP_TO_CHECK.contains(entityType) || entityType.getSpawnGroup() != SpawnGroup.MISC;
    }

    protected LootCondition.Builder killedByFrog() {
        return DamageSourcePropertiesLootCondition.builder(DamageSourcePredicate.Builder.create().sourceEntity(EntityPredicate.Builder.create().type(EntityType.FROG)));
    }

    protected LootCondition.Builder killedByFrog(FrogVariant variant) {
        return DamageSourcePropertiesLootCondition.builder(DamageSourcePredicate.Builder.create().sourceEntity(EntityPredicate.Builder.create().type(EntityType.FROG).typeSpecific(TypeSpecificPredicate.frog(variant))));
    }

    protected void register(EntityType<?> entityType, LootTable.Builder lootTable) {
        this.register(entityType, entityType.getLootTableId(), lootTable);
    }

    protected void register(EntityType<?> entityType, Identifier entityId, LootTable.Builder lootTable) {
        this.lootTables.computeIfAbsent(entityType, type -> new HashMap()).put(entityId, lootTable);
    }
}

