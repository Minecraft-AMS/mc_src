/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NetworkSyncedItem;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EmptyMapItem
extends NetworkSyncedItem {
    public EmptyMapItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = FilledMapItem.createMap(world, MathHelper.floor(user.x), MathHelper.floor(user.z), (byte)0, true, false);
        ItemStack itemStack2 = user.getStackInHand(hand);
        if (!user.abilities.creativeMode) {
            itemStack2.decrement(1);
        }
        if (itemStack2.isEmpty()) {
            return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, itemStack);
        }
        if (!user.inventory.insertStack(itemStack.copy())) {
            user.dropItem(itemStack, false);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, itemStack2);
    }
}

