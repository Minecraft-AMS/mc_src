/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.ThrownEnderpearlEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EnderPearlItem
extends Item {
    public EnderPearlItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!user.abilities.creativeMode) {
            itemStack.decrement(1);
        }
        world.playSound(null, user.x, user.y, user.z, SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (RANDOM.nextFloat() * 0.4f + 0.8f));
        user.getItemCooldownManager().set(this, 20);
        if (!world.isClient) {
            ThrownEnderpearlEntity thrownEnderpearlEntity = new ThrownEnderpearlEntity(world, user);
            thrownEnderpearlEntity.setItem(itemStack);
            thrownEnderpearlEntity.setProperties(user, user.pitch, user.yaw, 0.0f, 1.5f, 1.0f);
            world.spawnEntity(thrownEnderpearlEntity);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, itemStack);
    }
}
