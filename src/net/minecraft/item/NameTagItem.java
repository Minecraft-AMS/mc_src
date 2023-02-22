/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class NameTagItem
extends Item {
    public NameTagItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (stack.hasCustomName() && !(entity instanceof PlayerEntity)) {
            if (entity.isAlive()) {
                entity.setCustomName(stack.getName());
                if (entity instanceof MobEntity) {
                    ((MobEntity)entity).setPersistent();
                }
                stack.decrement(1);
            }
            return true;
        }
        return false;
    }
}

