/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ThrowablePotionItem
extends PotionItem {
    public ThrowablePotionItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!world.isClient) {
            ThrownPotionEntity thrownPotionEntity = new ThrownPotionEntity(world, user);
            thrownPotionEntity.setItemStack(itemStack);
            thrownPotionEntity.setProperties(user, user.pitch, user.yaw, -20.0f, 0.5f, 1.0f);
            world.spawnEntity(thrownPotionEntity);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.abilities.creativeMode) {
            itemStack.decrement(1);
        }
        return TypedActionResult.success(itemStack);
    }
}

