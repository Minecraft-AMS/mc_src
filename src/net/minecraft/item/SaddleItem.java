/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.event.GameEvent;

public class SaddleItem
extends Item {
    public SaddleItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        Saddleable saddleable;
        if (entity instanceof Saddleable && entity.isAlive() && !(saddleable = (Saddleable)((Object)entity)).isSaddled() && saddleable.canBeSaddled()) {
            if (!user.getWorld().isClient) {
                saddleable.saddle(SoundCategory.NEUTRAL);
                entity.getWorld().emitGameEvent((Entity)entity, GameEvent.EQUIP, entity.getPos());
                stack.decrement(1);
            }
            return ActionResult.success(user.getWorld().isClient);
        }
        return ActionResult.PASS;
    }
}

