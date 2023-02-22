/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SplashPotionItem
extends PotionItem {
    public SplashPotionItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        ItemStack itemStack2 = user.abilities.creativeMode ? itemStack.copy() : itemStack.split(1);
        world.playSound(null, user.x, user.y, user.z, SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5f, 0.4f / (RANDOM.nextFloat() * 0.4f + 0.8f));
        if (!world.isClient) {
            ThrownPotionEntity thrownPotionEntity = new ThrownPotionEntity(world, user);
            thrownPotionEntity.setItemStack(itemStack2);
            thrownPotionEntity.setProperties(user, user.pitch, user.yaw, -20.0f, 0.5f, 1.0f);
            world.spawnEntity(thrownPotionEntity);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, itemStack);
    }
}

