/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;

public class DyeableArmorItem
extends ArmorItem
implements DyeableItem {
    public DyeableArmorItem(ArmorMaterial armorMaterial, ArmorItem.Type type, Item.Settings settings) {
        super(armorMaterial, type, settings);
    }
}

