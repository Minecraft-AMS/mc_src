/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe.book;

import net.minecraft.util.StringIdentifiable;

public final class CookingRecipeCategory
extends Enum<CookingRecipeCategory>
implements StringIdentifiable {
    public static final /* enum */ CookingRecipeCategory FOOD = new CookingRecipeCategory("food");
    public static final /* enum */ CookingRecipeCategory BLOCKS = new CookingRecipeCategory("blocks");
    public static final /* enum */ CookingRecipeCategory MISC = new CookingRecipeCategory("misc");
    public static final StringIdentifiable.Codec<CookingRecipeCategory> CODEC;
    private final String id;
    private static final /* synthetic */ CookingRecipeCategory[] field_40247;

    public static CookingRecipeCategory[] values() {
        return (CookingRecipeCategory[])field_40247.clone();
    }

    public static CookingRecipeCategory valueOf(String string) {
        return Enum.valueOf(CookingRecipeCategory.class, string);
    }

    private CookingRecipeCategory(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ CookingRecipeCategory[] method_45439() {
        return new CookingRecipeCategory[]{FOOD, BLOCKS, MISC};
    }

    static {
        field_40247 = CookingRecipeCategory.method_45439();
        CODEC = StringIdentifiable.createCodec(CookingRecipeCategory::values);
    }
}

