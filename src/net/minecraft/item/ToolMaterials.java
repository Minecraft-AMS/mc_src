/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import java.util.function.Supplier;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Lazy;

public final class ToolMaterials
extends Enum<ToolMaterials>
implements ToolMaterial {
    public static final /* enum */ ToolMaterials WOOD = new ToolMaterials(0, 59, 2.0f, 0.0f, 15, () -> Ingredient.fromTag(ItemTags.PLANKS));
    public static final /* enum */ ToolMaterials STONE = new ToolMaterials(1, 131, 4.0f, 1.0f, 5, () -> Ingredient.fromTag(ItemTags.STONE_TOOL_MATERIALS));
    public static final /* enum */ ToolMaterials IRON = new ToolMaterials(2, 250, 6.0f, 2.0f, 14, () -> Ingredient.ofItems(Items.IRON_INGOT));
    public static final /* enum */ ToolMaterials DIAMOND = new ToolMaterials(3, 1561, 8.0f, 3.0f, 10, () -> Ingredient.ofItems(Items.DIAMOND));
    public static final /* enum */ ToolMaterials GOLD = new ToolMaterials(0, 32, 12.0f, 0.0f, 22, () -> Ingredient.ofItems(Items.GOLD_INGOT));
    public static final /* enum */ ToolMaterials NETHERITE = new ToolMaterials(4, 2031, 9.0f, 4.0f, 15, () -> Ingredient.ofItems(Items.NETHERITE_INGOT));
    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Lazy<Ingredient> repairIngredient;
    private static final /* synthetic */ ToolMaterials[] field_8926;

    public static ToolMaterials[] values() {
        return (ToolMaterials[])field_8926.clone();
    }

    public static ToolMaterials valueOf(String string) {
        return Enum.valueOf(ToolMaterials.class, string);
    }

    private ToolMaterials(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient) {
        this.miningLevel = miningLevel;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = new Lazy<Ingredient>(repairIngredient);
    }

    @Override
    public int getDurability() {
        return this.itemDurability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    private static /* synthetic */ ToolMaterials[] method_36684() {
        return new ToolMaterials[]{WOOD, STONE, IRON, DIAMOND, GOLD, NETHERITE};
    }

    static {
        field_8926 = ToolMaterials.method_36684();
    }
}

