/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeBookCategory;

@Environment(value=EnvType.CLIENT)
public final class RecipeBookGroup
extends Enum<RecipeBookGroup> {
    public static final /* enum */ RecipeBookGroup CRAFTING_SEARCH = new RecipeBookGroup(new ItemStack(Items.COMPASS));
    public static final /* enum */ RecipeBookGroup CRAFTING_BUILDING_BLOCKS = new RecipeBookGroup(new ItemStack(Blocks.BRICKS));
    public static final /* enum */ RecipeBookGroup CRAFTING_REDSTONE = new RecipeBookGroup(new ItemStack(Items.REDSTONE));
    public static final /* enum */ RecipeBookGroup CRAFTING_EQUIPMENT = new RecipeBookGroup(new ItemStack(Items.IRON_AXE), new ItemStack(Items.GOLDEN_SWORD));
    public static final /* enum */ RecipeBookGroup CRAFTING_MISC = new RecipeBookGroup(new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.APPLE));
    public static final /* enum */ RecipeBookGroup FURNACE_SEARCH = new RecipeBookGroup(new ItemStack(Items.COMPASS));
    public static final /* enum */ RecipeBookGroup FURNACE_FOOD = new RecipeBookGroup(new ItemStack(Items.PORKCHOP));
    public static final /* enum */ RecipeBookGroup FURNACE_BLOCKS = new RecipeBookGroup(new ItemStack(Blocks.STONE));
    public static final /* enum */ RecipeBookGroup FURNACE_MISC = new RecipeBookGroup(new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.EMERALD));
    public static final /* enum */ RecipeBookGroup BLAST_FURNACE_SEARCH = new RecipeBookGroup(new ItemStack(Items.COMPASS));
    public static final /* enum */ RecipeBookGroup BLAST_FURNACE_BLOCKS = new RecipeBookGroup(new ItemStack(Blocks.REDSTONE_ORE));
    public static final /* enum */ RecipeBookGroup BLAST_FURNACE_MISC = new RecipeBookGroup(new ItemStack(Items.IRON_SHOVEL), new ItemStack(Items.GOLDEN_LEGGINGS));
    public static final /* enum */ RecipeBookGroup SMOKER_SEARCH = new RecipeBookGroup(new ItemStack(Items.COMPASS));
    public static final /* enum */ RecipeBookGroup SMOKER_FOOD = new RecipeBookGroup(new ItemStack(Items.PORKCHOP));
    public static final /* enum */ RecipeBookGroup STONECUTTER = new RecipeBookGroup(new ItemStack(Items.CHISELED_STONE_BRICKS));
    public static final /* enum */ RecipeBookGroup SMITHING = new RecipeBookGroup(new ItemStack(Items.NETHERITE_CHESTPLATE));
    public static final /* enum */ RecipeBookGroup CAMPFIRE = new RecipeBookGroup(new ItemStack(Items.PORKCHOP));
    public static final /* enum */ RecipeBookGroup UNKNOWN = new RecipeBookGroup(new ItemStack(Items.BARRIER));
    public static final List<RecipeBookGroup> SMOKER;
    public static final List<RecipeBookGroup> BLAST_FURNACE;
    public static final List<RecipeBookGroup> FURNACE;
    public static final List<RecipeBookGroup> CRAFTING;
    public static final Map<RecipeBookGroup, List<RecipeBookGroup>> SEARCH_MAP;
    private final List<ItemStack> icons;
    private static final /* synthetic */ RecipeBookGroup[] field_1805;

    public static RecipeBookGroup[] values() {
        return (RecipeBookGroup[])field_1805.clone();
    }

    public static RecipeBookGroup valueOf(String string) {
        return Enum.valueOf(RecipeBookGroup.class, string);
    }

    private RecipeBookGroup(ItemStack ... entries) {
        this.icons = ImmutableList.copyOf((Object[])entries);
    }

    public static List<RecipeBookGroup> getGroups(RecipeBookCategory category) {
        switch (category) {
            case CRAFTING: {
                return CRAFTING;
            }
            case FURNACE: {
                return FURNACE;
            }
            case BLAST_FURNACE: {
                return BLAST_FURNACE;
            }
            case SMOKER: {
                return SMOKER;
            }
        }
        return ImmutableList.of();
    }

    public List<ItemStack> getIcons() {
        return this.icons;
    }

    private static /* synthetic */ RecipeBookGroup[] method_36866() {
        return new RecipeBookGroup[]{CRAFTING_SEARCH, CRAFTING_BUILDING_BLOCKS, CRAFTING_REDSTONE, CRAFTING_EQUIPMENT, CRAFTING_MISC, FURNACE_SEARCH, FURNACE_FOOD, FURNACE_BLOCKS, FURNACE_MISC, BLAST_FURNACE_SEARCH, BLAST_FURNACE_BLOCKS, BLAST_FURNACE_MISC, SMOKER_SEARCH, SMOKER_FOOD, STONECUTTER, SMITHING, CAMPFIRE, UNKNOWN};
    }

    static {
        field_1805 = RecipeBookGroup.method_36866();
        SMOKER = ImmutableList.of((Object)((Object)SMOKER_SEARCH), (Object)((Object)SMOKER_FOOD));
        BLAST_FURNACE = ImmutableList.of((Object)((Object)BLAST_FURNACE_SEARCH), (Object)((Object)BLAST_FURNACE_BLOCKS), (Object)((Object)BLAST_FURNACE_MISC));
        FURNACE = ImmutableList.of((Object)((Object)FURNACE_SEARCH), (Object)((Object)FURNACE_FOOD), (Object)((Object)FURNACE_BLOCKS), (Object)((Object)FURNACE_MISC));
        CRAFTING = ImmutableList.of((Object)((Object)CRAFTING_SEARCH), (Object)((Object)CRAFTING_EQUIPMENT), (Object)((Object)CRAFTING_BUILDING_BLOCKS), (Object)((Object)CRAFTING_MISC), (Object)((Object)CRAFTING_REDSTONE));
        SEARCH_MAP = ImmutableMap.of((Object)((Object)CRAFTING_SEARCH), (Object)ImmutableList.of((Object)((Object)CRAFTING_EQUIPMENT), (Object)((Object)CRAFTING_BUILDING_BLOCKS), (Object)((Object)CRAFTING_MISC), (Object)((Object)CRAFTING_REDSTONE)), (Object)((Object)FURNACE_SEARCH), (Object)ImmutableList.of((Object)((Object)FURNACE_FOOD), (Object)((Object)FURNACE_BLOCKS), (Object)((Object)FURNACE_MISC)), (Object)((Object)BLAST_FURNACE_SEARCH), (Object)ImmutableList.of((Object)((Object)BLAST_FURNACE_BLOCKS), (Object)((Object)BLAST_FURNACE_MISC)), (Object)((Object)SMOKER_SEARCH), (Object)ImmutableList.of((Object)((Object)SMOKER_FOOD)));
    }
}

