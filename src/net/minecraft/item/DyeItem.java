/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;

public class DyeItem
extends Item {
    private static final Map<DyeColor, DyeItem> DYES = Maps.newEnumMap(DyeColor.class);
    private final DyeColor color;

    public DyeItem(DyeColor color, Item.Settings settings) {
        super(settings);
        this.color = color;
        DYES.put(color, this);
    }

    @Override
    public boolean useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof SheepEntity) {
            SheepEntity sheepEntity = (SheepEntity)entity;
            if (sheepEntity.isAlive() && !sheepEntity.isSheared() && sheepEntity.getColor() != this.color) {
                sheepEntity.setColor(this.color);
                stack.decrement(1);
            }
            return true;
        }
        return false;
    }

    public DyeColor getColor() {
        return this.color;
    }

    public static DyeItem byColor(DyeColor color) {
        return DYES.get(color);
    }
}

