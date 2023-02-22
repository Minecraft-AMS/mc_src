/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.recipe.book;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.EnumMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.util.Util;

public final class RecipeBookOptions {
    private static final Map<RecipeBookCategory, Pair<String, String>> CATEGORY_OPTION_NAMES = ImmutableMap.of((Object)((Object)RecipeBookCategory.CRAFTING), (Object)Pair.of((Object)"isGuiOpen", (Object)"isFilteringCraftable"), (Object)((Object)RecipeBookCategory.FURNACE), (Object)Pair.of((Object)"isFurnaceGuiOpen", (Object)"isFurnaceFilteringCraftable"), (Object)((Object)RecipeBookCategory.BLAST_FURNACE), (Object)Pair.of((Object)"isBlastingFurnaceGuiOpen", (Object)"isBlastingFurnaceFilteringCraftable"), (Object)((Object)RecipeBookCategory.SMOKER), (Object)Pair.of((Object)"isSmokerGuiOpen", (Object)"isSmokerFilteringCraftable"));
    private final Map<RecipeBookCategory, CategoryOption> categoryOptions;

    private RecipeBookOptions(Map<RecipeBookCategory, CategoryOption> categoryOptions) {
        this.categoryOptions = categoryOptions;
    }

    public RecipeBookOptions() {
        this(Util.make(Maps.newEnumMap(RecipeBookCategory.class), enumMap -> {
            for (RecipeBookCategory recipeBookCategory : RecipeBookCategory.values()) {
                enumMap.put(recipeBookCategory, new CategoryOption(false, false));
            }
        }));
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isGuiOpen(RecipeBookCategory category) {
        return this.categoryOptions.get((Object)category).guiOpen;
    }

    public void setGuiOpen(RecipeBookCategory category, boolean open) {
        this.categoryOptions.get((Object)category).guiOpen = open;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFilteringCraftable(RecipeBookCategory category) {
        return this.categoryOptions.get((Object)category).filteringCraftable;
    }

    public void setFilteringCraftable(RecipeBookCategory category, boolean filtering) {
        this.categoryOptions.get((Object)category).filteringCraftable = filtering;
    }

    public static RecipeBookOptions fromPacket(PacketByteBuf buf) {
        EnumMap map = Maps.newEnumMap(RecipeBookCategory.class);
        for (RecipeBookCategory recipeBookCategory : RecipeBookCategory.values()) {
            boolean bl = buf.readBoolean();
            boolean bl2 = buf.readBoolean();
            map.put(recipeBookCategory, new CategoryOption(bl, bl2));
        }
        return new RecipeBookOptions(map);
    }

    public void toPacket(PacketByteBuf buf) {
        for (RecipeBookCategory recipeBookCategory : RecipeBookCategory.values()) {
            CategoryOption categoryOption = this.categoryOptions.get((Object)recipeBookCategory);
            if (categoryOption == null) {
                buf.writeBoolean(false);
                buf.writeBoolean(false);
                continue;
            }
            buf.writeBoolean(categoryOption.guiOpen);
            buf.writeBoolean(categoryOption.filteringCraftable);
        }
    }

    public static RecipeBookOptions fromNbt(NbtCompound nbt) {
        EnumMap map = Maps.newEnumMap(RecipeBookCategory.class);
        CATEGORY_OPTION_NAMES.forEach((recipeBookCategory, pair) -> {
            boolean bl = nbt.getBoolean((String)pair.getFirst());
            boolean bl2 = nbt.getBoolean((String)pair.getSecond());
            map.put(recipeBookCategory, new CategoryOption(bl, bl2));
        });
        return new RecipeBookOptions(map);
    }

    public void writeNbt(NbtCompound nbt) {
        CATEGORY_OPTION_NAMES.forEach((recipeBookCategory, pair) -> {
            CategoryOption categoryOption = this.categoryOptions.get(recipeBookCategory);
            nbt.putBoolean((String)pair.getFirst(), categoryOption.guiOpen);
            nbt.putBoolean((String)pair.getSecond(), categoryOption.filteringCraftable);
        });
    }

    public RecipeBookOptions copy() {
        EnumMap map = Maps.newEnumMap(RecipeBookCategory.class);
        for (RecipeBookCategory recipeBookCategory : RecipeBookCategory.values()) {
            CategoryOption categoryOption = this.categoryOptions.get((Object)recipeBookCategory);
            map.put(recipeBookCategory, categoryOption.copy());
        }
        return new RecipeBookOptions(map);
    }

    public void copyFrom(RecipeBookOptions other) {
        this.categoryOptions.clear();
        for (RecipeBookCategory recipeBookCategory : RecipeBookCategory.values()) {
            CategoryOption categoryOption = other.categoryOptions.get((Object)recipeBookCategory);
            this.categoryOptions.put(recipeBookCategory, categoryOption.copy());
        }
    }

    public boolean equals(Object object) {
        return this == object || object instanceof RecipeBookOptions && this.categoryOptions.equals(((RecipeBookOptions)object).categoryOptions);
    }

    public int hashCode() {
        return this.categoryOptions.hashCode();
    }

    static final class CategoryOption {
        private boolean guiOpen;
        private boolean filteringCraftable;

        public CategoryOption(boolean guiOpen, boolean filteringCraftable) {
            this.guiOpen = guiOpen;
            this.filteringCraftable = filteringCraftable;
        }

        public CategoryOption copy() {
            return new CategoryOption(this.guiOpen, this.filteringCraftable);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof CategoryOption) {
                CategoryOption categoryOption = (CategoryOption)object;
                return this.guiOpen == categoryOption.guiOpen && this.filteringCraftable == categoryOption.filteringCraftable;
            }
            return false;
        }

        public int hashCode() {
            int i = this.guiOpen ? 1 : 0;
            i = 31 * i + (this.filteringCraftable ? 1 : 0);
            return i;
        }

        public String toString() {
            return "[open=" + this.guiOpen + ", filtering=" + this.filteringCraftable + ']';
        }
    }
}

