/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe.book;

public final class RecipeBookCategory
extends Enum<RecipeBookCategory> {
    public static final /* enum */ RecipeBookCategory CRAFTING = new RecipeBookCategory();
    public static final /* enum */ RecipeBookCategory FURNACE = new RecipeBookCategory();
    public static final /* enum */ RecipeBookCategory BLAST_FURNACE = new RecipeBookCategory();
    public static final /* enum */ RecipeBookCategory SMOKER = new RecipeBookCategory();
    private static final /* synthetic */ RecipeBookCategory[] field_25767;

    public static RecipeBookCategory[] values() {
        return (RecipeBookCategory[])field_25767.clone();
    }

    public static RecipeBookCategory valueOf(String string) {
        return Enum.valueOf(RecipeBookCategory.class, string);
    }

    private static /* synthetic */ RecipeBookCategory[] method_36674() {
        return new RecipeBookCategory[]{CRAFTING, FURNACE, BLAST_FURNACE, SMOKER};
    }

    static {
        field_25767 = RecipeBookCategory.method_36674();
    }
}

