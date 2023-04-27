/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.data.server.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SingleItemRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SmithingTrimRecipeJsonBuilder;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public abstract class RecipeProvider
implements DataProvider {
    private final DataOutput.PathResolver recipesPathResolver;
    private final DataOutput.PathResolver advancementsPathResolver;
    private static final Map<BlockFamily.Variant, BiFunction<ItemConvertible, ItemConvertible, CraftingRecipeJsonBuilder>> VARIANT_FACTORIES = ImmutableMap.builder().put((Object)BlockFamily.Variant.BUTTON, (output, input) -> RecipeProvider.createTransmutationRecipe(output, Ingredient.ofItems(input))).put((Object)BlockFamily.Variant.CHISELED, (output, input) -> RecipeProvider.createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItems(input))).put((Object)BlockFamily.Variant.CUT, (itemConvertible, itemConvertible2) -> RecipeProvider.createCutCopperRecipe(RecipeCategory.BUILDING_BLOCKS, itemConvertible, Ingredient.ofItems(itemConvertible2))).put((Object)BlockFamily.Variant.DOOR, (output, input) -> RecipeProvider.createDoorRecipe(output, Ingredient.ofItems(input))).put((Object)BlockFamily.Variant.CUSTOM_FENCE, (output, input) -> RecipeProvider.createFenceRecipe(output, Ingredient.ofItems(input))).put((Object)BlockFamily.Variant.FENCE, (output, input) -> RecipeProvider.createFenceRecipe(output, Ingredient.ofItems(input))).put((Object)BlockFamily.Variant.CUSTOM_FENCE_GATE, (itemConvertible, itemConvertible2) -> RecipeProvider.createFenceGateRecipe(itemConvertible, Ingredient.ofItems(itemConvertible2))).put((Object)BlockFamily.Variant.FENCE_GATE, (output, input) -> RecipeProvider.createFenceGateRecipe(output, Ingredient.ofItems(input))).put((Object)BlockFamily.Variant.SIGN, (output, input) -> RecipeProvider.createSignRecipe(output, Ingredient.ofItems(input))).put((Object)BlockFamily.Variant.SLAB, (output, input) -> RecipeProvider.createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItems(input))).put((Object)BlockFamily.Variant.STAIRS, (output, input) -> RecipeProvider.createStairsRecipe(output, Ingredient.ofItems(input))).put((Object)BlockFamily.Variant.PRESSURE_PLATE, (output, input) -> RecipeProvider.createPressurePlateRecipe(RecipeCategory.REDSTONE, output, Ingredient.ofItems(input))).put((Object)BlockFamily.Variant.POLISHED, (output, input) -> RecipeProvider.createCondensingRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItems(input))).put((Object)BlockFamily.Variant.TRAPDOOR, (output, input) -> RecipeProvider.createTrapdoorRecipe(output, Ingredient.ofItems(input))).put((Object)BlockFamily.Variant.WALL, (output, input) -> RecipeProvider.getWallRecipe(RecipeCategory.DECORATIONS, output, Ingredient.ofItems(input))).build();

    public RecipeProvider(DataOutput output) {
        this.recipesPathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "recipes");
        this.advancementsPathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "advancements");
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        HashSet set = Sets.newHashSet();
        ArrayList list = new ArrayList();
        this.generate(jsonProvider -> {
            if (!set.add(jsonProvider.getRecipeId())) {
                throw new IllegalStateException("Duplicate recipe " + jsonProvider.getRecipeId());
            }
            list.add(DataProvider.writeToPath(writer, (JsonElement)jsonProvider.toJson(), this.recipesPathResolver.resolveJson(jsonProvider.getRecipeId())));
            JsonObject jsonObject = jsonProvider.toAdvancementJson();
            if (jsonObject != null) {
                list.add(DataProvider.writeToPath(writer, (JsonElement)jsonObject, this.advancementsPathResolver.resolveJson(jsonProvider.getAdvancementId())));
            }
        });
        return CompletableFuture.allOf((CompletableFuture[])list.toArray(CompletableFuture[]::new));
    }

    public CompletableFuture<?> saveRecipeAdvancement(DataWriter cache, Identifier advancementId, Advancement.Builder advancementBuilder) {
        return DataProvider.writeToPath(cache, (JsonElement)advancementBuilder.toJson(), this.advancementsPathResolver.resolveJson(advancementId));
    }

    public abstract void generate(Consumer<RecipeJsonProvider> var1);

    public static void generateFamilies(Consumer<RecipeJsonProvider> exporter, FeatureSet enabledFeatures) {
        BlockFamilies.getFamilies().filter(family -> family.shouldGenerateRecipes(enabledFeatures)).forEach(family -> RecipeProvider.generateFamily(exporter, family));
    }

    public static void offerSingleOutputShapelessRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, @Nullable String group) {
        RecipeProvider.offerShapelessRecipe(exporter, output, input, group, 1);
    }

    public static void offerShapelessRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, @Nullable String group, int outputCount) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, output, outputCount).input(input).group(group).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter, RecipeProvider.convertBetween(output, input));
    }

    public static void offerSmelting(Consumer<RecipeJsonProvider> exporter, List<ItemConvertible> inputs, RecipeCategory category, ItemConvertible output, float experience, int cookingTime, String group) {
        RecipeProvider.offerMultipleOptions(exporter, RecipeSerializer.SMELTING, inputs, category, output, experience, cookingTime, group, "_from_smelting");
    }

    public static void offerBlasting(Consumer<RecipeJsonProvider> exporter, List<ItemConvertible> inputs, RecipeCategory category, ItemConvertible output, float experience, int cookingTime, String group) {
        RecipeProvider.offerMultipleOptions(exporter, RecipeSerializer.BLASTING, inputs, category, output, experience, cookingTime, group, "_from_blasting");
    }

    public static void offerMultipleOptions(Consumer<RecipeJsonProvider> exporter, RecipeSerializer<? extends AbstractCookingRecipe> serializer, List<ItemConvertible> inputs, RecipeCategory category, ItemConvertible output, float experience, int cookingTime, String group, String method) {
        for (ItemConvertible itemConvertible : inputs) {
            CookingRecipeJsonBuilder.create(Ingredient.ofItems(itemConvertible), category, output, experience, cookingTime, serializer).group(group).criterion(RecipeProvider.hasItem(itemConvertible), RecipeProvider.conditionsFromItem(itemConvertible)).offerTo(exporter, RecipeProvider.getItemPath(output) + method + "_" + RecipeProvider.getItemPath(itemConvertible));
        }
    }

    public static void offerNetheriteUpgradeRecipe(Consumer<RecipeJsonProvider> exporter, Item input, RecipeCategory category, Item result) {
        SmithingTransformRecipeJsonBuilder.create(Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.ofItems(input), Ingredient.ofItems(Items.NETHERITE_INGOT), category, result).criterion("has_netherite_ingot", RecipeProvider.conditionsFromItem(Items.NETHERITE_INGOT)).offerTo(exporter, RecipeProvider.getItemPath(result) + "_smithing");
    }

    public static void offerSmithingTrimRecipe(Consumer<RecipeJsonProvider> exporter, Item template, Identifier recipeId) {
        SmithingTrimRecipeJsonBuilder.create(Ingredient.ofItems(template), Ingredient.fromTag(ItemTags.TRIMMABLE_ARMOR), Ingredient.fromTag(ItemTags.TRIM_MATERIALS), RecipeCategory.MISC).criterion("has_smithing_trim_template", RecipeProvider.conditionsFromItem(template)).offerTo(exporter, recipeId);
    }

    public static void offer2x2CompactingRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(category, output, 1).input(Character.valueOf('#'), input).pattern("##").pattern("##").criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static void offerCompactingRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input, String criterionName) {
        ShapelessRecipeJsonBuilder.create(category, output).input(input, 9).criterion(criterionName, RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static void offerCompactingRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        RecipeProvider.offerCompactingRecipe(exporter, category, output, input, RecipeProvider.hasItem(input));
    }

    public static void offerPlanksRecipe2(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, TagKey<Item> input, int count) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, count).input(input).group("planks").criterion("has_log", RecipeProvider.conditionsFromTag(input)).offerTo(exporter);
    }

    public static void offerPlanksRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, TagKey<Item> input, int count) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, count).input(input).group("planks").criterion("has_logs", RecipeProvider.conditionsFromTag(input)).offerTo(exporter);
    }

    public static void offerBarkBlockRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 3).input(Character.valueOf('#'), input).pattern("##").pattern("##").group("bark").criterion("has_log", RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static void offerBoatRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, output).input(Character.valueOf('#'), input).pattern("# #").pattern("###").group("boat").criterion("in_water", RecipeProvider.requireEnteringFluid(Blocks.WATER)).offerTo(exporter);
    }

    public static void offerChestBoatRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, output).input(Blocks.CHEST).input(input).group("chest_boat").criterion("has_boat", RecipeProvider.conditionsFromTag(ItemTags.BOATS)).offerTo(exporter);
    }

    public static CraftingRecipeJsonBuilder createTransmutationRecipe(ItemConvertible output, Ingredient input) {
        return ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, output).input(input);
    }

    public static CraftingRecipeJsonBuilder createDoorRecipe(ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, output, 3).input(Character.valueOf('#'), input).pattern("##").pattern("##").pattern("##");
    }

    public static CraftingRecipeJsonBuilder createFenceRecipe(ItemConvertible output, Ingredient input) {
        int i = output == Blocks.NETHER_BRICK_FENCE ? 6 : 3;
        Item item = output == Blocks.NETHER_BRICK_FENCE ? Items.NETHER_BRICK : Items.STICK;
        return ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, i).input(Character.valueOf('W'), input).input(Character.valueOf('#'), item).pattern("W#W").pattern("W#W");
    }

    public static CraftingRecipeJsonBuilder createFenceGateRecipe(ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, output).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('W'), input).pattern("#W#").pattern("#W#");
    }

    public static void offerPressurePlateRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        RecipeProvider.createPressurePlateRecipe(RecipeCategory.REDSTONE, output, Ingredient.ofItems(input)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static CraftingRecipeJsonBuilder createPressurePlateRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(category, output).input(Character.valueOf('#'), input).pattern("##");
    }

    public static void offerSlabRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        RecipeProvider.createSlabRecipe(category, output, Ingredient.ofItems(input)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static CraftingRecipeJsonBuilder createSlabRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(category, output, 6).input(Character.valueOf('#'), input).pattern("###");
    }

    public static CraftingRecipeJsonBuilder createStairsRecipe(ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 4).input(Character.valueOf('#'), input).pattern("#  ").pattern("## ").pattern("###");
    }

    public static CraftingRecipeJsonBuilder createTrapdoorRecipe(ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, output, 2).input(Character.valueOf('#'), input).pattern("###").pattern("###");
    }

    public static CraftingRecipeJsonBuilder createSignRecipe(ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, 3).group("sign").input(Character.valueOf('#'), input).input(Character.valueOf('X'), Items.STICK).pattern("###").pattern("###").pattern(" X ");
    }

    public static void offerHangingSignRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, 6).group("hanging_sign").input(Character.valueOf('#'), input).input(Character.valueOf('X'), Items.CHAIN).pattern("X X").pattern("###").pattern("###").criterion("has_stripped_logs", RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static void offerWoolDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output).input(input).input(Blocks.WHITE_WOOL).group("wool").criterion("has_white_wool", RecipeProvider.conditionsFromItem(Blocks.WHITE_WOOL)).offerTo(exporter);
    }

    public static void offerCarpetRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, 3).input(Character.valueOf('#'), input).pattern("##").group("carpet").criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static void offerCarpetDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, 8).input(Character.valueOf('#'), Blocks.WHITE_CARPET).input(Character.valueOf('$'), input).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", RecipeProvider.conditionsFromItem(Blocks.WHITE_CARPET)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter, RecipeProvider.convertBetween(output, Blocks.WHITE_CARPET));
    }

    public static void offerBedRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output).input(Character.valueOf('#'), input).input(Character.valueOf('X'), ItemTags.PLANKS).pattern("###").pattern("XXX").group("bed").criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static void offerBedDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output).input(Items.WHITE_BED).input(input).group("dyed_bed").criterion("has_bed", RecipeProvider.conditionsFromItem(Items.WHITE_BED)).offerTo(exporter, RecipeProvider.convertBetween(output, Items.WHITE_BED));
    }

    public static void offerBannerRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output).input(Character.valueOf('#'), input).input(Character.valueOf('|'), Items.STICK).pattern("###").pattern("###").pattern(" | ").group("banner").criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static void offerStainedGlassDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 8).input(Character.valueOf('#'), Blocks.GLASS).input(Character.valueOf('X'), input).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", RecipeProvider.conditionsFromItem(Blocks.GLASS)).offerTo(exporter);
    }

    public static void offerStainedGlassPaneRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, 16).input(Character.valueOf('#'), input).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static void offerStainedGlassPaneDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, 8).input(Character.valueOf('#'), Blocks.GLASS_PANE).input(Character.valueOf('$'), input).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", RecipeProvider.conditionsFromItem(Blocks.GLASS_PANE)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter, RecipeProvider.convertBetween(output, Blocks.GLASS_PANE));
    }

    public static void offerTerracottaDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 8).input(Character.valueOf('#'), Blocks.TERRACOTTA).input(Character.valueOf('X'), input).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", RecipeProvider.conditionsFromItem(Blocks.TERRACOTTA)).offerTo(exporter);
    }

    public static void offerConcretePowderDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 8).input(input).input(Blocks.SAND, 4).input(Blocks.GRAVEL, 4).group("concrete_powder").criterion("has_sand", RecipeProvider.conditionsFromItem(Blocks.SAND)).criterion("has_gravel", RecipeProvider.conditionsFromItem(Blocks.GRAVEL)).offerTo(exporter);
    }

    public static void offerCandleDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output).input(Blocks.CANDLE).input(input).group("dyed_candle").criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static void offerWallRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        RecipeProvider.getWallRecipe(category, output, Ingredient.ofItems(input)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static CraftingRecipeJsonBuilder getWallRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(category, output, 6).input(Character.valueOf('#'), input).pattern("###").pattern("###");
    }

    public static void offerPolishedStoneRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        RecipeProvider.createCondensingRecipe(category, output, Ingredient.ofItems(input)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static CraftingRecipeJsonBuilder createCondensingRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(category, output, 4).input(Character.valueOf('S'), input).pattern("SS").pattern("SS");
    }

    public static void offerCutCopperRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        RecipeProvider.createCutCopperRecipe(category, output, Ingredient.ofItems(input)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static ShapedRecipeJsonBuilder createCutCopperRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(category, output, 4).input(Character.valueOf('#'), input).pattern("##").pattern("##");
    }

    public static void offerChiseledBlockRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        RecipeProvider.createChiseledBlockRecipe(category, output, Ingredient.ofItems(input)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static void offerMosaicRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(category, output).input(Character.valueOf('#'), input).pattern("#").pattern("#").criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static ShapedRecipeJsonBuilder createChiseledBlockRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(category, output).input(Character.valueOf('#'), input).pattern("#").pattern("#");
    }

    public static void offerStonecuttingRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
        RecipeProvider.offerStonecuttingRecipe(exporter, category, output, input, 1);
    }

    public static void offerStonecuttingRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input, int count) {
        SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(input), category, output, count).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter, RecipeProvider.convertBetween(output, input) + "_stonecutting");
    }

    public static void offerCrackingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(input), RecipeCategory.BUILDING_BLOCKS, output, 0.1f, 200).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static void offerReversibleCompactingRecipes(Consumer<RecipeJsonProvider> exporter, RecipeCategory reverseCategory, ItemConvertible baseItem, RecipeCategory compactingCategory, ItemConvertible compactItem) {
        RecipeProvider.offerReversibleCompactingRecipes(exporter, reverseCategory, baseItem, compactingCategory, compactItem, RecipeProvider.getRecipeName(compactItem), null, RecipeProvider.getRecipeName(baseItem), null);
    }

    public static void offerReversibleCompactingRecipesWithCompactingRecipeGroup(Consumer<RecipeJsonProvider> exporter, RecipeCategory reverseCategory, ItemConvertible baseItem, RecipeCategory compactingCategory, ItemConvertible compactItem, String compactingId, String compactingGroup) {
        RecipeProvider.offerReversibleCompactingRecipes(exporter, reverseCategory, baseItem, compactingCategory, compactItem, compactingId, compactingGroup, RecipeProvider.getRecipeName(baseItem), null);
    }

    public static void offerReversibleCompactingRecipesWithReverseRecipeGroup(Consumer<RecipeJsonProvider> exporter, RecipeCategory reverseCategory, ItemConvertible baseItem, RecipeCategory compactingCategory, ItemConvertible compactItem, String reverseId, String reverseGroup) {
        RecipeProvider.offerReversibleCompactingRecipes(exporter, reverseCategory, baseItem, compactingCategory, compactItem, RecipeProvider.getRecipeName(compactItem), null, reverseId, reverseGroup);
    }

    public static void offerReversibleCompactingRecipes(Consumer<RecipeJsonProvider> exporter, RecipeCategory reverseCategory, ItemConvertible baseItem, RecipeCategory compactingCategory, ItemConvertible compactItem, String compactingId, @Nullable String compactingGroup, String reverseId, @Nullable String reverseGroup) {
        ShapelessRecipeJsonBuilder.create(reverseCategory, baseItem, 9).input(compactItem).group(reverseGroup).criterion(RecipeProvider.hasItem(compactItem), RecipeProvider.conditionsFromItem(compactItem)).offerTo(exporter, new Identifier(reverseId));
        ShapedRecipeJsonBuilder.create(compactingCategory, compactItem).input(Character.valueOf('#'), baseItem).pattern("###").pattern("###").pattern("###").group(compactingGroup).criterion(RecipeProvider.hasItem(baseItem), RecipeProvider.conditionsFromItem(baseItem)).offerTo(exporter, new Identifier(compactingId));
    }

    public static void offerSmithingTemplateCopyingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible template, TagKey<Item> resource) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, template, 2).input(Character.valueOf('#'), Items.DIAMOND).input(Character.valueOf('C'), resource).input(Character.valueOf('S'), template).pattern("#S#").pattern("#C#").pattern("###").criterion(RecipeProvider.hasItem(template), RecipeProvider.conditionsFromItem(template)).offerTo(exporter);
    }

    public static void offerSmithingTemplateCopyingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible template, ItemConvertible resource) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, template, 2).input(Character.valueOf('#'), Items.DIAMOND).input(Character.valueOf('C'), resource).input(Character.valueOf('S'), template).pattern("#S#").pattern("#C#").pattern("###").criterion(RecipeProvider.hasItem(template), RecipeProvider.conditionsFromItem(template)).offerTo(exporter);
    }

    public static void generateCookingRecipes(Consumer<RecipeJsonProvider> exporter, String cooker, RecipeSerializer<? extends AbstractCookingRecipe> serializer, int cookingTime) {
        RecipeProvider.offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.BEEF, Items.COOKED_BEEF, 0.35f);
        RecipeProvider.offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35f);
        RecipeProvider.offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.COD, Items.COOKED_COD, 0.35f);
        RecipeProvider.offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.KELP, Items.DRIED_KELP, 0.1f);
        RecipeProvider.offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.SALMON, Items.COOKED_SALMON, 0.35f);
        RecipeProvider.offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.MUTTON, Items.COOKED_MUTTON, 0.35f);
        RecipeProvider.offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35f);
        RecipeProvider.offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.POTATO, Items.BAKED_POTATO, 0.35f);
        RecipeProvider.offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.RABBIT, Items.COOKED_RABBIT, 0.35f);
    }

    public static void offerFoodCookingRecipe(Consumer<RecipeJsonProvider> exporter, String cooker, RecipeSerializer<? extends AbstractCookingRecipe> serializer, int cookingTime, ItemConvertible input, ItemConvertible output, float experience) {
        CookingRecipeJsonBuilder.create(Ingredient.ofItems(input), RecipeCategory.FOOD, output, experience, cookingTime, serializer).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter, RecipeProvider.getItemPath(output) + "_from_" + cooker);
    }

    public static void offerWaxingRecipes(Consumer<RecipeJsonProvider> exporter) {
        HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().forEach((input, output) -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output).input((ItemConvertible)input).input(Items.HONEYCOMB).group(RecipeProvider.getItemPath(output)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter, RecipeProvider.convertBetween(output, Items.HONEYCOMB)));
    }

    public static void generateFamily(Consumer<RecipeJsonProvider> exporter, BlockFamily family) {
        family.getVariants().forEach((variant, block) -> {
            BiFunction<ItemConvertible, ItemConvertible, CraftingRecipeJsonBuilder> biFunction = VARIANT_FACTORIES.get(variant);
            Block itemConvertible = RecipeProvider.getVariantRecipeInput(family, variant);
            if (biFunction != null) {
                CraftingRecipeJsonBuilder craftingRecipeJsonBuilder = biFunction.apply((ItemConvertible)block, itemConvertible);
                family.getGroup().ifPresent(group -> craftingRecipeJsonBuilder.group(group + (String)(variant == BlockFamily.Variant.CUT ? "" : "_" + variant.getName())));
                craftingRecipeJsonBuilder.criterion(family.getUnlockCriterionName().orElseGet(() -> RecipeProvider.hasItem(itemConvertible)), RecipeProvider.conditionsFromItem(itemConvertible));
                craftingRecipeJsonBuilder.offerTo(exporter);
            }
            if (variant == BlockFamily.Variant.CRACKED) {
                RecipeProvider.offerCrackingRecipe(exporter, block, itemConvertible);
            }
        });
    }

    public static Block getVariantRecipeInput(BlockFamily family, BlockFamily.Variant variant) {
        if (variant == BlockFamily.Variant.CHISELED) {
            if (!family.getVariants().containsKey((Object)BlockFamily.Variant.SLAB)) {
                throw new IllegalStateException("Slab is not defined for the family.");
            }
            return family.getVariant(BlockFamily.Variant.SLAB);
        }
        return family.getBaseBlock();
    }

    public static EnterBlockCriterion.Conditions requireEnteringFluid(Block block) {
        return new EnterBlockCriterion.Conditions(EntityPredicate.Extended.EMPTY, block, StatePredicate.ANY);
    }

    public static InventoryChangedCriterion.Conditions conditionsFromItem(NumberRange.IntRange count, ItemConvertible item) {
        return RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(item).count(count).build());
    }

    public static InventoryChangedCriterion.Conditions conditionsFromItem(ItemConvertible item) {
        return RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(item).build());
    }

    public static InventoryChangedCriterion.Conditions conditionsFromTag(TagKey<Item> tag) {
        return RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().tag(tag).build());
    }

    public static InventoryChangedCriterion.Conditions conditionsFromItemPredicates(ItemPredicate ... predicates) {
        return new InventoryChangedCriterion.Conditions(EntityPredicate.Extended.EMPTY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, predicates);
    }

    public static String hasItem(ItemConvertible item) {
        return "has_" + RecipeProvider.getItemPath(item);
    }

    public static String getItemPath(ItemConvertible item) {
        return Registries.ITEM.getId(item.asItem()).getPath();
    }

    public static String getRecipeName(ItemConvertible item) {
        return RecipeProvider.getItemPath(item);
    }

    public static String convertBetween(ItemConvertible to, ItemConvertible from) {
        return RecipeProvider.getItemPath(to) + "_from_" + RecipeProvider.getItemPath(from);
    }

    public static String getSmeltingItemPath(ItemConvertible item) {
        return RecipeProvider.getItemPath(item) + "_from_smelting";
    }

    public static String getBlastingItemPath(ItemConvertible item) {
        return RecipeProvider.getItemPath(item) + "_from_blasting";
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}

