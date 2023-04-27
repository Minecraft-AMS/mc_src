/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.enchantment;

import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.item.Vanishable;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public abstract class EnchantmentTarget
extends Enum<EnchantmentTarget> {
    public static final /* enum */ EnchantmentTarget ARMOR = new EnchantmentTarget(){

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof ArmorItem;
        }
    };
    public static final /* enum */ EnchantmentTarget ARMOR_FEET = new EnchantmentTarget(){

        @Override
        public boolean isAcceptableItem(Item item) {
            ArmorItem armorItem;
            return item instanceof ArmorItem && (armorItem = (ArmorItem)item).getSlotType() == EquipmentSlot.FEET;
        }
    };
    public static final /* enum */ EnchantmentTarget ARMOR_LEGS = new EnchantmentTarget(){

        @Override
        public boolean isAcceptableItem(Item item) {
            ArmorItem armorItem;
            return item instanceof ArmorItem && (armorItem = (ArmorItem)item).getSlotType() == EquipmentSlot.LEGS;
        }
    };
    public static final /* enum */ EnchantmentTarget ARMOR_CHEST = new EnchantmentTarget(){

        @Override
        public boolean isAcceptableItem(Item item) {
            ArmorItem armorItem;
            return item instanceof ArmorItem && (armorItem = (ArmorItem)item).getSlotType() == EquipmentSlot.CHEST;
        }
    };
    public static final /* enum */ EnchantmentTarget ARMOR_HEAD = new EnchantmentTarget(){

        @Override
        public boolean isAcceptableItem(Item item) {
            ArmorItem armorItem;
            return item instanceof ArmorItem && (armorItem = (ArmorItem)item).getSlotType() == EquipmentSlot.HEAD;
        }
    };
    public static final /* enum */ EnchantmentTarget WEAPON = new EnchantmentTarget(){

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof SwordItem;
        }
    };
    public static final /* enum */ EnchantmentTarget DIGGER = new EnchantmentTarget(){

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof MiningToolItem;
        }
    };
    public static final /* enum */ EnchantmentTarget FISHING_ROD = new EnchantmentTarget(){

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof FishingRodItem;
        }
    };
    public static final /* enum */ EnchantmentTarget TRIDENT = new EnchantmentTarget(){

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof TridentItem;
        }
    };
    public static final /* enum */ EnchantmentTarget BREAKABLE = new EnchantmentTarget(){

        @Override
        public boolean isAcceptableItem(Item item) {
            return item.isDamageable();
        }
    };
    public static final /* enum */ EnchantmentTarget BOW = new EnchantmentTarget(){

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof BowItem;
        }
    };
    public static final /* enum */ EnchantmentTarget WEARABLE = new EnchantmentTarget(){

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof Equipment || Block.getBlockFromItem(item) instanceof Equipment;
        }
    };
    public static final /* enum */ EnchantmentTarget CROSSBOW = new EnchantmentTarget(){

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof CrossbowItem;
        }
    };
    public static final /* enum */ EnchantmentTarget VANISHABLE = new EnchantmentTarget(){

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof Vanishable || Block.getBlockFromItem(item) instanceof Vanishable || BREAKABLE.isAcceptableItem(item);
        }
    };
    private static final /* synthetic */ EnchantmentTarget[] field_9077;

    public static EnchantmentTarget[] values() {
        return (EnchantmentTarget[])field_9077.clone();
    }

    public static EnchantmentTarget valueOf(String string) {
        return Enum.valueOf(EnchantmentTarget.class, string);
    }

    public abstract boolean isAcceptableItem(Item var1);

    private static /* synthetic */ EnchantmentTarget[] method_36688() {
        return new EnchantmentTarget[]{ARMOR, ARMOR_FEET, ARMOR_LEGS, ARMOR_CHEST, ARMOR_HEAD, WEAPON, DIGGER, FISHING_ROD, TRIDENT, BREAKABLE, BOW, WEARABLE, CROSSBOW, VANISHABLE};
    }

    static {
        field_9077 = EnchantmentTarget.method_36688();
    }
}

