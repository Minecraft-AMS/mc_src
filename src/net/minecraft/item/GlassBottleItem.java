/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class GlassBottleItem
extends Item {
    public GlassBottleItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        List<AreaEffectCloudEntity> list = world.getEntities(AreaEffectCloudEntity.class, user.getBoundingBox().expand(2.0), entity -> entity != null && entity.isAlive() && entity.getOwner() instanceof EnderDragonEntity);
        ItemStack itemStack = user.getStackInHand(hand);
        if (!list.isEmpty()) {
            AreaEffectCloudEntity areaEffectCloudEntity = list.get(0);
            areaEffectCloudEntity.setRadius(areaEffectCloudEntity.getRadius() - 0.5f);
            world.playSound(null, user.x, user.y, user.z, SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, this.fill(itemStack, user, new ItemStack(Items.DRAGON_BREATH)));
        }
        HitResult hitResult = GlassBottleItem.rayTrace(world, user, RayTraceContext.FluidHandling.SOURCE_ONLY);
        if (hitResult.getType() == HitResult.Type.MISS) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
        }
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
            if (!world.canPlayerModifyAt(user, blockPos)) {
                return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
            }
            if (world.getFluidState(blockPos).matches(FluidTags.WATER)) {
                world.playSound(user, user.x, user.y, user.z, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, this.fill(itemStack, user, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
            }
        }
        return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
    }

    protected ItemStack fill(ItemStack emptyBottle, PlayerEntity player, ItemStack filledBottle) {
        emptyBottle.decrement(1);
        player.incrementStat(Stats.USED.getOrCreateStat(this));
        if (emptyBottle.isEmpty()) {
            return filledBottle;
        }
        if (!player.inventory.insertStack(filledBottle)) {
            player.dropItem(filledBottle, false);
        }
        return emptyBottle;
    }
}
