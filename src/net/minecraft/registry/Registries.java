/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Lifecycle
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 */
package net.minecraft.registry;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DecoratedPotPatterns;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.decoration.painting.PaintingVariants;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Instrument;
import net.minecraft.item.Instruments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.provider.nbt.LootNbtProviderType;
import net.minecraft.loot.provider.nbt.LootNbtProviderTypes;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.loot.provider.score.LootScoreProviderType;
import net.minecraft.loot.provider.score.LootScoreProviderTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleDefaultedRegistry;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.structure.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.floatprovider.FloatProviderType;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSources;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSourceType;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGenerators;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.size.FeatureSizeType;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.heightprovider.HeightProviderType;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;
import net.minecraft.world.gen.root.RootPlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.structure.StructureType;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class Registries {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Identifier, Supplier<?>> DEFAULT_ENTRIES = Maps.newLinkedHashMap();
    public static final Identifier ROOT_KEY = new Identifier("root");
    private static final MutableRegistry<MutableRegistry<?>> ROOT = new SimpleRegistry(RegistryKey.ofRegistry(ROOT_KEY), Lifecycle.stable());
    public static final DefaultedRegistry<GameEvent> GAME_EVENT = Registries.createIntrusive(RegistryKeys.GAME_EVENT, "step", registry -> GameEvent.STEP);
    public static final Registry<SoundEvent> SOUND_EVENT = Registries.create(RegistryKeys.SOUND_EVENT, registry -> SoundEvents.ENTITY_ITEM_PICKUP);
    public static final DefaultedRegistry<Fluid> FLUID = Registries.createIntrusive(RegistryKeys.FLUID, "empty", registry -> Fluids.EMPTY);
    public static final Registry<StatusEffect> STATUS_EFFECT = Registries.create(RegistryKeys.STATUS_EFFECT, registry -> StatusEffects.LUCK);
    public static final DefaultedRegistry<Block> BLOCK = Registries.createIntrusive(RegistryKeys.BLOCK, "air", registry -> Blocks.AIR);
    public static final Registry<Enchantment> ENCHANTMENT = Registries.create(RegistryKeys.ENCHANTMENT, registry -> Enchantments.FORTUNE);
    public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = Registries.createIntrusive(RegistryKeys.ENTITY_TYPE, "pig", registry -> EntityType.PIG);
    public static final DefaultedRegistry<Item> ITEM = Registries.createIntrusive(RegistryKeys.ITEM, "air", registry -> Items.AIR);
    public static final DefaultedRegistry<Potion> POTION = Registries.create(RegistryKeys.POTION, "empty", (Registry<T> registry) -> Potions.EMPTY);
    public static final Registry<ParticleType<?>> PARTICLE_TYPE = Registries.create(RegistryKeys.PARTICLE_TYPE, registry -> ParticleTypes.BLOCK);
    public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = Registries.create(RegistryKeys.BLOCK_ENTITY_TYPE, registry -> BlockEntityType.FURNACE);
    public static final DefaultedRegistry<PaintingVariant> PAINTING_VARIANT = Registries.create(RegistryKeys.PAINTING_VARIANT, "kebab", PaintingVariants::registerAndGetDefault);
    public static final Registry<Identifier> CUSTOM_STAT = Registries.create(RegistryKeys.CUSTOM_STAT, registry -> Stats.JUMP);
    public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS = Registries.create(RegistryKeys.CHUNK_STATUS, "empty", (Registry<T> registry) -> ChunkStatus.EMPTY);
    public static final Registry<RuleTestType<?>> RULE_TEST = Registries.create(RegistryKeys.RULE_TEST, registry -> RuleTestType.ALWAYS_TRUE);
    public static final Registry<RuleBlockEntityModifierType<?>> RULE_BLOCK_ENTITY_MODIFIER = Registries.create(RegistryKeys.RULE_BLOCK_ENTITY_MODIFIER, registry -> RuleBlockEntityModifierType.PASSTHROUGH);
    public static final Registry<PosRuleTestType<?>> POS_RULE_TEST = Registries.create(RegistryKeys.POS_RULE_TEST, registry -> PosRuleTestType.ALWAYS_TRUE);
    public static final Registry<ScreenHandlerType<?>> SCREEN_HANDLER = Registries.create(RegistryKeys.SCREEN_HANDLER, registry -> ScreenHandlerType.ANVIL);
    public static final Registry<RecipeType<?>> RECIPE_TYPE = Registries.create(RegistryKeys.RECIPE_TYPE, registry -> RecipeType.CRAFTING);
    public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER = Registries.create(RegistryKeys.RECIPE_SERIALIZER, registry -> RecipeSerializer.SHAPELESS);
    public static final Registry<EntityAttribute> ATTRIBUTE = Registries.create(RegistryKeys.ATTRIBUTE, registry -> EntityAttributes.GENERIC_LUCK);
    public static final Registry<PositionSourceType<?>> POSITION_SOURCE_TYPE = Registries.create(RegistryKeys.POSITION_SOURCE_TYPE, registry -> PositionSourceType.BLOCK);
    public static final Registry<ArgumentSerializer<?, ?>> COMMAND_ARGUMENT_TYPE = Registries.create(RegistryKeys.COMMAND_ARGUMENT_TYPE, ArgumentTypes::register);
    public static final Registry<StatType<?>> STAT_TYPE = Registries.create(RegistryKeys.STAT_TYPE, registry -> Stats.USED);
    public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE = Registries.create(RegistryKeys.VILLAGER_TYPE, "plains", (Registry<T> registry) -> VillagerType.PLAINS);
    public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION = Registries.create(RegistryKeys.VILLAGER_PROFESSION, "none", (Registry<T> registry) -> VillagerProfession.NONE);
    public static final Registry<PointOfInterestType> POINT_OF_INTEREST_TYPE = Registries.create(RegistryKeys.POINT_OF_INTEREST_TYPE, PointOfInterestTypes::registerAndGetDefault);
    public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE = Registries.create(RegistryKeys.MEMORY_MODULE_TYPE, "dummy", (Registry<T> registry) -> MemoryModuleType.DUMMY);
    public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE = Registries.create(RegistryKeys.SENSOR_TYPE, "dummy", (Registry<T> registry) -> SensorType.DUMMY);
    public static final Registry<Schedule> SCHEDULE = Registries.create(RegistryKeys.SCHEDULE, registry -> Schedule.EMPTY);
    public static final Registry<Activity> ACTIVITY = Registries.create(RegistryKeys.ACTIVITY, registry -> Activity.IDLE);
    public static final Registry<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = Registries.create(RegistryKeys.LOOT_POOL_ENTRY_TYPE, registry -> LootPoolEntryTypes.EMPTY);
    public static final Registry<LootFunctionType> LOOT_FUNCTION_TYPE = Registries.create(RegistryKeys.LOOT_FUNCTION_TYPE, registry -> LootFunctionTypes.SET_COUNT);
    public static final Registry<LootConditionType> LOOT_CONDITION_TYPE = Registries.create(RegistryKeys.LOOT_CONDITION_TYPE, registry -> LootConditionTypes.INVERTED);
    public static final Registry<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE = Registries.create(RegistryKeys.LOOT_NUMBER_PROVIDER_TYPE, registry -> LootNumberProviderTypes.CONSTANT);
    public static final Registry<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE = Registries.create(RegistryKeys.LOOT_NBT_PROVIDER_TYPE, registry -> LootNbtProviderTypes.CONTEXT);
    public static final Registry<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE = Registries.create(RegistryKeys.LOOT_SCORE_PROVIDER_TYPE, registry -> LootScoreProviderTypes.CONTEXT);
    public static final Registry<FloatProviderType<?>> FLOAT_PROVIDER_TYPE = Registries.create(RegistryKeys.FLOAT_PROVIDER_TYPE, registry -> FloatProviderType.CONSTANT);
    public static final Registry<IntProviderType<?>> INT_PROVIDER_TYPE = Registries.create(RegistryKeys.INT_PROVIDER_TYPE, registry -> IntProviderType.CONSTANT);
    public static final Registry<HeightProviderType<?>> HEIGHT_PROVIDER_TYPE = Registries.create(RegistryKeys.HEIGHT_PROVIDER_TYPE, registry -> HeightProviderType.CONSTANT);
    public static final Registry<BlockPredicateType<?>> BLOCK_PREDICATE_TYPE = Registries.create(RegistryKeys.BLOCK_PREDICATE_TYPE, registry -> BlockPredicateType.NOT);
    public static final Registry<Carver<?>> CARVER = Registries.create(RegistryKeys.CARVER, registry -> Carver.CAVE);
    public static final Registry<Feature<?>> FEATURE = Registries.create(RegistryKeys.FEATURE, registry -> Feature.ORE);
    public static final Registry<StructurePlacementType<?>> STRUCTURE_PLACEMENT = Registries.create(RegistryKeys.STRUCTURE_PLACEMENT, registry -> StructurePlacementType.RANDOM_SPREAD);
    public static final Registry<StructurePieceType> STRUCTURE_PIECE = Registries.create(RegistryKeys.STRUCTURE_PIECE, registry -> StructurePieceType.MINESHAFT_ROOM);
    public static final Registry<StructureType<?>> STRUCTURE_TYPE = Registries.create(RegistryKeys.STRUCTURE_TYPE, registry -> StructureType.JIGSAW);
    public static final Registry<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPE = Registries.create(RegistryKeys.PLACEMENT_MODIFIER_TYPE, registry -> PlacementModifierType.COUNT);
    public static final Registry<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPE = Registries.create(RegistryKeys.BLOCK_STATE_PROVIDER_TYPE, registry -> BlockStateProviderType.SIMPLE_STATE_PROVIDER);
    public static final Registry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPE = Registries.create(RegistryKeys.FOLIAGE_PLACER_TYPE, registry -> FoliagePlacerType.BLOB_FOLIAGE_PLACER);
    public static final Registry<TrunkPlacerType<?>> TRUNK_PLACER_TYPE = Registries.create(RegistryKeys.TRUNK_PLACER_TYPE, registry -> TrunkPlacerType.STRAIGHT_TRUNK_PLACER);
    public static final Registry<RootPlacerType<?>> ROOT_PLACER_TYPE = Registries.create(RegistryKeys.ROOT_PLACER_TYPE, registry -> RootPlacerType.MANGROVE_ROOT_PLACER);
    public static final Registry<TreeDecoratorType<?>> TREE_DECORATOR_TYPE = Registries.create(RegistryKeys.TREE_DECORATOR_TYPE, registry -> TreeDecoratorType.LEAVE_VINE);
    public static final Registry<FeatureSizeType<?>> FEATURE_SIZE_TYPE = Registries.create(RegistryKeys.FEATURE_SIZE_TYPE, registry -> FeatureSizeType.TWO_LAYERS_FEATURE_SIZE);
    public static final Registry<Codec<? extends BiomeSource>> BIOME_SOURCE = Registries.create(RegistryKeys.BIOME_SOURCE, Lifecycle.stable(), BiomeSources::registerAndGetDefault);
    public static final Registry<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR = Registries.create(RegistryKeys.CHUNK_GENERATOR, Lifecycle.stable(), ChunkGenerators::registerAndGetDefault);
    public static final Registry<Codec<? extends MaterialRules.MaterialCondition>> MATERIAL_CONDITION = Registries.create(RegistryKeys.MATERIAL_CONDITION, MaterialRules.MaterialCondition::registerAndGetDefault);
    public static final Registry<Codec<? extends MaterialRules.MaterialRule>> MATERIAL_RULE = Registries.create(RegistryKeys.MATERIAL_RULE, MaterialRules.MaterialRule::registerAndGetDefault);
    public static final Registry<Codec<? extends DensityFunction>> DENSITY_FUNCTION_TYPE = Registries.create(RegistryKeys.DENSITY_FUNCTION_TYPE, DensityFunctionTypes::registerAndGetDefault);
    public static final Registry<StructureProcessorType<?>> STRUCTURE_PROCESSOR = Registries.create(RegistryKeys.STRUCTURE_PROCESSOR, registry -> StructureProcessorType.BLOCK_IGNORE);
    public static final Registry<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT = Registries.create(RegistryKeys.STRUCTURE_POOL_ELEMENT, registry -> StructurePoolElementType.EMPTY_POOL_ELEMENT);
    public static final Registry<CatVariant> CAT_VARIANT = Registries.create(RegistryKeys.CAT_VARIANT, CatVariant::registerAndGetDefault);
    public static final Registry<FrogVariant> FROG_VARIANT = Registries.create(RegistryKeys.FROG_VARIANT, registry -> FrogVariant.TEMPERATE);
    public static final Registry<BannerPattern> BANNER_PATTERN = Registries.create(RegistryKeys.BANNER_PATTERN, BannerPatterns::registerAndGetDefault);
    public static final Registry<Instrument> INSTRUMENT = Registries.create(RegistryKeys.INSTRUMENT, Instruments::registerAndGetDefault);
    public static final Registry<String> DECORATED_POT_PATTERN = Registries.create(RegistryKeys.DECORATED_POT_PATTERN, DecoratedPotPatterns::registerAndGetDefault);
    public static final Registry<ItemGroup> ITEM_GROUP = Registries.create(RegistryKeys.ITEM_GROUP, ItemGroups::registerAndGetDefault);
    public static final Registry<? extends Registry<?>> REGISTRIES = ROOT;

    private static <T> Registry<T> create(RegistryKey<? extends Registry<T>> key, Initializer<T> initializer) {
        return Registries.create(key, Lifecycle.stable(), initializer);
    }

    private static <T> DefaultedRegistry<T> create(RegistryKey<? extends Registry<T>> key, String defaultId, Initializer<T> initializer) {
        return Registries.create(key, defaultId, Lifecycle.stable(), initializer);
    }

    private static <T> DefaultedRegistry<T> createIntrusive(RegistryKey<? extends Registry<T>> key, String defaultId, Initializer<T> initializer) {
        return Registries.createIntrusive(key, defaultId, Lifecycle.stable(), initializer);
    }

    private static <T> Registry<T> create(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, Initializer<T> initializer) {
        return Registries.create(key, new SimpleRegistry(key, lifecycle, false), initializer, lifecycle);
    }

    private static <T> DefaultedRegistry<T> create(RegistryKey<? extends Registry<T>> key, String defaultId, Lifecycle lifecycle, Initializer<T> initializer) {
        return Registries.create(key, new SimpleDefaultedRegistry(defaultId, key, lifecycle, false), initializer, lifecycle);
    }

    private static <T> DefaultedRegistry<T> createIntrusive(RegistryKey<? extends Registry<T>> key, String defaultId, Lifecycle lifecycle, Initializer<T> initializer) {
        return Registries.create(key, new SimpleDefaultedRegistry(defaultId, key, lifecycle, true), initializer, lifecycle);
    }

    private static <T, R extends MutableRegistry<T>> R create(RegistryKey<? extends Registry<T>> key, R registry, Initializer<T> initializer, Lifecycle lifecycle) {
        Identifier identifier = key.getValue();
        DEFAULT_ENTRIES.put(identifier, () -> initializer.run(registry));
        ROOT.add(key, registry, lifecycle);
        return registry;
    }

    public static void bootstrap() {
        Registries.init();
        Registries.freezeRegistries();
        Registries.validate(REGISTRIES);
    }

    private static void init() {
        DEFAULT_ENTRIES.forEach((id, initializer) -> {
            if (initializer.get() == null) {
                LOGGER.error("Unable to bootstrap registry '{}'", id);
            }
        });
    }

    private static void freezeRegistries() {
        REGISTRIES.freeze();
        for (Registry registry : REGISTRIES) {
            registry.freeze();
        }
    }

    private static <T extends Registry<?>> void validate(Registry<T> registries) {
        registries.forEach(registry -> {
            if (registry.getIds().isEmpty()) {
                Util.error("Registry '" + registries.getId(registry) + "' was empty after loading");
            }
            if (registry instanceof DefaultedRegistry) {
                Identifier identifier = ((DefaultedRegistry)registry).getDefaultId();
                Validate.notNull(registry.get(identifier), (String)("Missing default of DefaultedMappedRegistry: " + identifier), (Object[])new Object[0]);
            }
        });
    }

    @FunctionalInterface
    static interface Initializer<T> {
        public T run(Registry<T> var1);
    }
}

