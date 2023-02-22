/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.ThrownExperienceBottleEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ExperienceBottleItem
extends Item {
    public ExperienceBottleItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasEnchantmentGlint(ItemStack stack) {
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (RANDOM.nextFloat() * 0.4f + 0.8f));
        if (!world.isClient) {
            ThrownExperienceBottleEntity thrownExperienceBottleEntity = new ThrownExperienceBottleEntity(world, user);
            thrownExperienceBottleEntity.setItem(itemStack);
            thrownExperienceBottleEntity.setProperties(user, user.pitch, user.yaw, -20.0f, 0.7f, 1.0f);
            world.spawnEntity(thrownExperienceBottleEntity);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.abilities.creativeMode) {
            itemStack.decrement(1);
        }
        return TypedActionResult.success(itemStack);
    }
}

