/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.server.tag;

import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.AbstractItemTagProvider;
import net.minecraft.data.server.tag.AbstractTagProvider;
import net.minecraft.data.server.tag.ValueLookupTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;

public class VanillaItemTagProvider
extends AbstractItemTagProvider {
    public VanillaItemTagProvider(DataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, AbstractTagProvider<Block> abstractTagProvider) {
        super(dataOutput, completableFuture, abstractTagProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookup) {
        this.copy(BlockTags.WOOL, ItemTags.WOOL);
        this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
        this.copy(BlockTags.STONE_BRICKS, ItemTags.STONE_BRICKS);
        this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        this.copy(BlockTags.BUTTONS, ItemTags.BUTTONS);
        this.copy(BlockTags.WOOL_CARPETS, ItemTags.WOOL_CARPETS);
        this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        this.copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
        this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        this.copy(BlockTags.DOORS, ItemTags.DOORS);
        this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        this.copy(BlockTags.OAK_LOGS, ItemTags.OAK_LOGS);
        this.copy(BlockTags.DARK_OAK_LOGS, ItemTags.DARK_OAK_LOGS);
        this.copy(BlockTags.BIRCH_LOGS, ItemTags.BIRCH_LOGS);
        this.copy(BlockTags.ACACIA_LOGS, ItemTags.ACACIA_LOGS);
        this.copy(BlockTags.SPRUCE_LOGS, ItemTags.SPRUCE_LOGS);
        this.copy(BlockTags.MANGROVE_LOGS, ItemTags.MANGROVE_LOGS);
        this.copy(BlockTags.JUNGLE_LOGS, ItemTags.JUNGLE_LOGS);
        this.copy(BlockTags.CRIMSON_STEMS, ItemTags.CRIMSON_STEMS);
        this.copy(BlockTags.WARPED_STEMS, ItemTags.WARPED_STEMS);
        this.copy(BlockTags.WART_BLOCKS, ItemTags.WART_BLOCKS);
        this.copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
        this.copy(BlockTags.LOGS, ItemTags.LOGS);
        this.copy(BlockTags.SAND, ItemTags.SAND);
        this.copy(BlockTags.SLABS, ItemTags.SLABS);
        this.copy(BlockTags.WALLS, ItemTags.WALLS);
        this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
        this.copy(BlockTags.ANVIL, ItemTags.ANVIL);
        this.copy(BlockTags.RAILS, ItemTags.RAILS);
        this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
        this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        this.copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
        this.copy(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS);
        this.copy(BlockTags.BEDS, ItemTags.BEDS);
        this.copy(BlockTags.FENCES, ItemTags.FENCES);
        this.copy(BlockTags.TALL_FLOWERS, ItemTags.TALL_FLOWERS);
        this.copy(BlockTags.FLOWERS, ItemTags.FLOWERS);
        this.copy(BlockTags.SOUL_FIRE_BASE_BLOCKS, ItemTags.SOUL_FIRE_BASE_BLOCKS);
        this.copy(BlockTags.CANDLES, ItemTags.CANDLES);
        this.copy(BlockTags.DAMPENS_VIBRATIONS, ItemTags.DAMPENS_VIBRATIONS);
        this.copy(BlockTags.GOLD_ORES, ItemTags.GOLD_ORES);
        this.copy(BlockTags.IRON_ORES, ItemTags.IRON_ORES);
        this.copy(BlockTags.DIAMOND_ORES, ItemTags.DIAMOND_ORES);
        this.copy(BlockTags.REDSTONE_ORES, ItemTags.REDSTONE_ORES);
        this.copy(BlockTags.LAPIS_ORES, ItemTags.LAPIS_ORES);
        this.copy(BlockTags.COAL_ORES, ItemTags.COAL_ORES);
        this.copy(BlockTags.EMERALD_ORES, ItemTags.EMERALD_ORES);
        this.copy(BlockTags.COPPER_ORES, ItemTags.COPPER_ORES);
        this.copy(BlockTags.DIRT, ItemTags.DIRT);
        this.copy(BlockTags.TERRACOTTA, ItemTags.TERRACOTTA);
        this.copy(BlockTags.COMPLETES_FIND_TREE_TUTORIAL, ItemTags.COMPLETES_FIND_TREE_TUTORIAL);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.BANNERS)).add(Items.WHITE_BANNER, Items.ORANGE_BANNER, Items.MAGENTA_BANNER, Items.LIGHT_BLUE_BANNER, Items.YELLOW_BANNER, Items.LIME_BANNER, Items.PINK_BANNER, Items.GRAY_BANNER, Items.LIGHT_GRAY_BANNER, Items.CYAN_BANNER, Items.PURPLE_BANNER, Items.BLUE_BANNER, Items.BROWN_BANNER, Items.GREEN_BANNER, Items.RED_BANNER, Items.BLACK_BANNER);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.BOATS)).add(Items.OAK_BOAT, Items.SPRUCE_BOAT, Items.BIRCH_BOAT, Items.JUNGLE_BOAT, Items.ACACIA_BOAT, Items.DARK_OAK_BOAT, Items.MANGROVE_BOAT).addTag((TagKey)ItemTags.CHEST_BOATS);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.CHEST_BOATS)).add(Items.OAK_CHEST_BOAT, Items.SPRUCE_CHEST_BOAT, Items.BIRCH_CHEST_BOAT, Items.JUNGLE_CHEST_BOAT, Items.ACACIA_CHEST_BOAT, Items.DARK_OAK_CHEST_BOAT, Items.MANGROVE_CHEST_BOAT);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.FISHES)).add(Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH);
        this.copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.CREEPER_DROP_MUSIC_DISCS)).add(Items.MUSIC_DISC_13, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_11, Items.MUSIC_DISC_WAIT);
        ((ValueLookupTagProvider.ObjectBuilder)((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.MUSIC_DISCS)).addTag((TagKey)ItemTags.CREEPER_DROP_MUSIC_DISCS)).add(Items.MUSIC_DISC_PIGSTEP).add(Items.MUSIC_DISC_OTHERSIDE).add(Items.MUSIC_DISC_5);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.COALS)).add(Items.COAL, Items.CHARCOAL);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.ARROWS)).add(Items.ARROW, Items.TIPPED_ARROW, Items.SPECTRAL_ARROW);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.LECTERN_BOOKS)).add(Items.WRITTEN_BOOK, Items.WRITABLE_BOOK);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.BEACON_PAYMENT_ITEMS)).add(Items.NETHERITE_INGOT, Items.EMERALD, Items.DIAMOND, Items.GOLD_INGOT, Items.IRON_INGOT);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.PIGLIN_REPELLENTS)).add(Items.SOUL_TORCH).add(Items.SOUL_LANTERN).add(Items.SOUL_CAMPFIRE);
        ((ValueLookupTagProvider.ObjectBuilder)((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.PIGLIN_LOVED)).addTag((TagKey)ItemTags.GOLD_ORES)).add(Items.GOLD_BLOCK, Items.GILDED_BLACKSTONE, Items.LIGHT_WEIGHTED_PRESSURE_PLATE, Items.GOLD_INGOT, Items.BELL, Items.CLOCK, Items.GOLDEN_CARROT, Items.GLISTERING_MELON_SLICE, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR, Items.GOLDEN_SWORD, Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.RAW_GOLD, Items.RAW_GOLD_BLOCK);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.IGNORED_BY_PIGLIN_BABIES)).add(Items.LEATHER);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.PIGLIN_FOOD)).add(Items.PORKCHOP, Items.COOKED_PORKCHOP);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.FOX_FOOD)).add(Items.SWEET_BERRIES, Items.GLOW_BERRIES);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.NON_FLAMMABLE_WOOD)).add(Items.WARPED_STEM, Items.STRIPPED_WARPED_STEM, Items.WARPED_HYPHAE, Items.STRIPPED_WARPED_HYPHAE, Items.CRIMSON_STEM, Items.STRIPPED_CRIMSON_STEM, Items.CRIMSON_HYPHAE, Items.STRIPPED_CRIMSON_HYPHAE, Items.CRIMSON_PLANKS, Items.WARPED_PLANKS, Items.CRIMSON_SLAB, Items.WARPED_SLAB, Items.CRIMSON_PRESSURE_PLATE, Items.WARPED_PRESSURE_PLATE, Items.CRIMSON_FENCE, Items.WARPED_FENCE, Items.CRIMSON_TRAPDOOR, Items.WARPED_TRAPDOOR, Items.CRIMSON_FENCE_GATE, Items.WARPED_FENCE_GATE, Items.CRIMSON_STAIRS, Items.WARPED_STAIRS, Items.CRIMSON_BUTTON, Items.WARPED_BUTTON, Items.CRIMSON_DOOR, Items.WARPED_DOOR, Items.CRIMSON_SIGN, Items.WARPED_SIGN);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.STONE_TOOL_MATERIALS)).add(Items.COBBLESTONE, Items.BLACKSTONE, Items.COBBLED_DEEPSLATE);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.STONE_CRAFTING_MATERIALS)).add(Items.COBBLESTONE, Items.BLACKSTONE, Items.COBBLED_DEEPSLATE);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.FREEZE_IMMUNE_WEARABLES)).add(Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_HORSE_ARMOR);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.AXOLOTL_TEMPT_ITEMS)).add(Items.TROPICAL_FISH_BUCKET);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.CLUSTER_MAX_HARVESTABLES)).add(Items.DIAMOND_PICKAXE, Items.GOLDEN_PICKAXE, Items.IRON_PICKAXE, Items.NETHERITE_PICKAXE, Items.STONE_PICKAXE, Items.WOODEN_PICKAXE);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.COMPASSES)).add(Items.COMPASS).add(Items.RECOVERY_COMPASS);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)ItemTags.CREEPER_IGNITERS)).add(Items.FLINT_AND_STEEL).add(Items.FIRE_CHARGE);
    }
}

