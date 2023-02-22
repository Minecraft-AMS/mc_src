/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe.book;

import net.minecraft.util.StringIdentifiable;

public final class CraftingRecipeCategory
extends Enum<CraftingRecipeCategory>
implements StringIdentifiable {
    public static final /* enum */ CraftingRecipeCategory BUILDING = new CraftingRecipeCategory("building");
    public static final /* enum */ CraftingRecipeCategory REDSTONE = new CraftingRecipeCategory("redstone");
    public static final /* enum */ CraftingRecipeCategory EQUIPMENT = new CraftingRecipeCategory("equipment");
    public static final /* enum */ CraftingRecipeCategory MISC = new CraftingRecipeCategory("misc");
    public static final StringIdentifiable.Codec<CraftingRecipeCategory> CODEC;
    private final String id;
    private static final /* synthetic */ CraftingRecipeCategory[] field_40254;

    public static CraftingRecipeCategory[] values() {
        return (CraftingRecipeCategory[])field_40254.clone();
    }

    public static CraftingRecipeCategory valueOf(String string) {
        return Enum.valueOf(CraftingRecipeCategory.class, string);
    }

    private CraftingRecipeCategory(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ CraftingRecipeCategory[] method_45440() {
        return new CraftingRecipeCategory[]{BUILDING, REDSTONE, EQUIPMENT, MISC};
    }

    static {
        field_40254 = CraftingRecipeCategory.method_45440();
        CODEC = StringIdentifiable.createCodec(CraftingRecipeCategory::values);
    }
}

