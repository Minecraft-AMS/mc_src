/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.server.recipe;

import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class OneTwentyRecipeProvider
extends RecipeProvider {
    public OneTwentyRecipeProvider(DataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    protected void generate(Consumer<RecipeJsonProvider> exporter) {
        OneTwentyRecipeProvider.generateFamilies(exporter, FeatureSet.of(FeatureFlags.UPDATE_1_20));
        OneTwentyRecipeProvider.offerCompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_BLOCK, Items.BAMBOO);
        OneTwentyRecipeProvider.offerPlanksRecipe(exporter, Blocks.BAMBOO_PLANKS, ItemTags.BAMBOO_BLOCKS, 2);
        OneTwentyRecipeProvider.offerMosaicRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_SLAB);
        OneTwentyRecipeProvider.offerBoatRecipe(exporter, Items.BAMBOO_RAFT, Blocks.BAMBOO_PLANKS);
        OneTwentyRecipeProvider.offerChestBoatRecipe(exporter, Items.BAMBOO_CHEST_RAFT, Items.BAMBOO_RAFT);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.OAK_HANGING_SIGN, Blocks.STRIPPED_OAK_LOG);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.SPRUCE_HANGING_SIGN, Blocks.STRIPPED_SPRUCE_LOG);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.BIRCH_HANGING_SIGN, Blocks.STRIPPED_BIRCH_LOG);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.JUNGLE_HANGING_SIGN, Blocks.STRIPPED_JUNGLE_LOG);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.ACACIA_HANGING_SIGN, Blocks.STRIPPED_ACACIA_LOG);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.CHERRY_HANGING_SIGN, Blocks.STRIPPED_CHERRY_LOG);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.DARK_OAK_HANGING_SIGN, Blocks.STRIPPED_DARK_OAK_LOG);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.MANGROVE_HANGING_SIGN, Blocks.STRIPPED_MANGROVE_LOG);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.BAMBOO_HANGING_SIGN, Items.STRIPPED_BAMBOO_BLOCK);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.CRIMSON_HANGING_SIGN, Blocks.STRIPPED_CRIMSON_STEM);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.WARPED_HANGING_SIGN, Blocks.STRIPPED_WARPED_STEM);
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_BOOKSHELF).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('X'), ItemTags.WOODEN_SLABS).pattern("###").pattern("XXX").pattern("###").criterion("has_book", OneTwentyRecipeProvider.conditionsFromItem(Items.BOOK)).offerTo(exporter);
        OneTwentyRecipeProvider.offerSmithingTrimRecipe(exporter, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE);
        OneTwentyRecipeProvider.offerSmithingTrimRecipe(exporter, Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
        OneTwentyRecipeProvider.offerSmithingTrimRecipe(exporter, Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
        OneTwentyRecipeProvider.offerSmithingTrimRecipe(exporter, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE);
        OneTwentyRecipeProvider.offerSmithingTrimRecipe(exporter, Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE);
        OneTwentyRecipeProvider.offerSmithingTrimRecipe(exporter, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE);
        OneTwentyRecipeProvider.offerSmithingTrimRecipe(exporter, Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE);
        OneTwentyRecipeProvider.offerSmithingTrimRecipe(exporter, Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE);
        OneTwentyRecipeProvider.offerSmithingTrimRecipe(exporter, Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE);
        OneTwentyRecipeProvider.offerSmithingTrimRecipe(exporter, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
        OneTwentyRecipeProvider.offerSmithingTrimRecipe(exporter, Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE);
        OneTwentyRecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_CHESTPLATE, RecipeCategory.COMBAT, Items.NETHERITE_CHESTPLATE);
        OneTwentyRecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_LEGGINGS, RecipeCategory.COMBAT, Items.NETHERITE_LEGGINGS);
        OneTwentyRecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_HELMET, RecipeCategory.COMBAT, Items.NETHERITE_HELMET);
        OneTwentyRecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_BOOTS, RecipeCategory.COMBAT, Items.NETHERITE_BOOTS);
        OneTwentyRecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_SWORD, RecipeCategory.COMBAT, Items.NETHERITE_SWORD);
        OneTwentyRecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_AXE, RecipeCategory.TOOLS, Items.NETHERITE_AXE);
        OneTwentyRecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_PICKAXE, RecipeCategory.TOOLS, Items.NETHERITE_PICKAXE);
        OneTwentyRecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_HOE, RecipeCategory.TOOLS, Items.NETHERITE_HOE);
        OneTwentyRecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_SHOVEL, RecipeCategory.TOOLS, Items.NETHERITE_SHOVEL);
        OneTwentyRecipeProvider.offerSmithingTemplateCopyingRecipe(exporter, (ItemConvertible)Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, Items.NETHERRACK);
        OneTwentyRecipeProvider.offerSmithingTemplateCopyingRecipe(exporter, (ItemConvertible)Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
        OneTwentyRecipeProvider.offerSmithingTemplateCopyingRecipe(exporter, (ItemConvertible)Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SANDSTONE);
        OneTwentyRecipeProvider.offerSmithingTemplateCopyingRecipe(exporter, (ItemConvertible)Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
        OneTwentyRecipeProvider.offerSmithingTemplateCopyingRecipe(exporter, (ItemConvertible)Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.MOSSY_COBBLESTONE);
        OneTwentyRecipeProvider.offerSmithingTemplateCopyingRecipe(exporter, (ItemConvertible)Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLED_DEEPSLATE);
        OneTwentyRecipeProvider.offerSmithingTemplateCopyingRecipe(exporter, (ItemConvertible)Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.END_STONE);
        OneTwentyRecipeProvider.offerSmithingTemplateCopyingRecipe(exporter, (ItemConvertible)Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
        OneTwentyRecipeProvider.offerSmithingTemplateCopyingRecipe(exporter, (ItemConvertible)Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.PRISMARINE);
        OneTwentyRecipeProvider.offerSmithingTemplateCopyingRecipe(exporter, (ItemConvertible)Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.BLACKSTONE);
        OneTwentyRecipeProvider.offerSmithingTemplateCopyingRecipe(exporter, (ItemConvertible)Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Items.NETHERRACK);
        OneTwentyRecipeProvider.offerSmithingTemplateCopyingRecipe(exporter, (ItemConvertible)Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.PURPUR_BLOCK);
        OneTwentyRecipeProvider.offerSingleOutputShapelessRecipe(exporter, Items.ORANGE_DYE, Blocks.TORCHFLOWER, "orange_dye");
        OneTwentyRecipeProvider.offerPlanksRecipe2(exporter, Blocks.CHERRY_PLANKS, ItemTags.CHERRY_LOGS, 4);
        OneTwentyRecipeProvider.offerBarkBlockRecipe(exporter, Blocks.CHERRY_WOOD, Blocks.CHERRY_LOG);
        OneTwentyRecipeProvider.offerBarkBlockRecipe(exporter, Blocks.STRIPPED_CHERRY_WOOD, Blocks.STRIPPED_CHERRY_LOG);
        OneTwentyRecipeProvider.offerBoatRecipe(exporter, Items.CHERRY_BOAT, Blocks.CHERRY_PLANKS);
        OneTwentyRecipeProvider.offerChestBoatRecipe(exporter, Items.CHERRY_CHEST_BOAT, Items.CHERRY_BOAT);
        OneTwentyRecipeProvider.offerShapelessRecipe(exporter, Items.PINK_DYE, Items.PINK_PETALS, "pink_dye", 1);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.BRUSH).input(Character.valueOf('X'), Items.FEATHER).input(Character.valueOf('#'), Items.COPPER_INGOT).input(Character.valueOf('I'), Items.STICK).pattern("X").pattern("#").pattern("I").criterion("has_copper_ingot", OneTwentyRecipeProvider.conditionsFromItem(Items.COPPER_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Items.DECORATED_POT).input(Character.valueOf('#'), Items.BRICK).pattern(" # ").pattern("# #").pattern(" # ").criterion("has_brick", OneTwentyRecipeProvider.conditionsFromTag(ItemTags.DECORATED_POT_SHARDS)).offerTo(exporter, "decorated_pot_simple");
        ComplexRecipeJsonBuilder.create(RecipeSerializer.CRAFTING_DECORATED_POT).offerTo(exporter, "decorated_pot");
    }
}

