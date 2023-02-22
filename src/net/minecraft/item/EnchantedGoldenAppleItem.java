/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EnchantedGoldenAppleItem
extends Item {
    public EnchantedGoldenAppleItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}

