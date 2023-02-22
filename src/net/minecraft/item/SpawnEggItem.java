/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SpawnEggItem
extends Item {
    private static final Map<EntityType<?>, SpawnEggItem> SPAWN_EGGS = Maps.newIdentityHashMap();
    private final int primaryColor;
    private final int secondaryColor;
    private final EntityType<?> type;

    public SpawnEggItem(EntityType<?> type, int primaryColor, int secondaryColor, Item.Settings settings) {
        super(settings);
        this.type = type;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        SPAWN_EGGS.put(type, this);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockEntity blockEntity;
        World world = context.getWorld();
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        ItemStack itemStack = context.getStack();
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block == Blocks.SPAWNER && (blockEntity = world.getBlockEntity(blockPos)) instanceof MobSpawnerBlockEntity) {
            MobSpawnerLogic mobSpawnerLogic = ((MobSpawnerBlockEntity)blockEntity).getLogic();
            EntityType<?> entityType = this.getEntityType(itemStack.getTag());
            mobSpawnerLogic.setEntityId(entityType);
            blockEntity.markDirty();
            world.updateListeners(blockPos, blockState, blockState, 3);
            itemStack.decrement(1);
            return ActionResult.SUCCESS;
        }
        BlockPos blockPos2 = blockState.getCollisionShape(world, blockPos).isEmpty() ? blockPos : blockPos.offset(direction);
        EntityType<?> entityType2 = this.getEntityType(itemStack.getTag());
        if (entityType2.spawnFromItemStack(world, itemStack, context.getPlayer(), blockPos2, SpawnType.SPAWN_EGG, true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP) != null) {
            itemStack.decrement(1);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (world.isClient) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
        }
        HitResult hitResult = SpawnEggItem.rayTrace(world, user, RayTraceContext.FluidHandling.SOURCE_ONLY);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
        }
        BlockHitResult blockHitResult = (BlockHitResult)hitResult;
        BlockPos blockPos = blockHitResult.getBlockPos();
        if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
        }
        if (!world.canPlayerModifyAt(user, blockPos) || !user.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
            return new TypedActionResult<ItemStack>(ActionResult.FAIL, itemStack);
        }
        EntityType<?> entityType = this.getEntityType(itemStack.getTag());
        if (entityType.spawnFromItemStack(world, itemStack, user, blockPos, SpawnType.SPAWN_EGG, false, false) == null) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
        }
        if (!user.abilities.creativeMode) {
            itemStack.decrement(1);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, itemStack);
    }

    public boolean isOfSameEntityType(@Nullable CompoundTag tag, EntityType<?> type) {
        return Objects.equals(this.getEntityType(tag), type);
    }

    @Environment(value=EnvType.CLIENT)
    public int getColor(int num) {
        return num == 0 ? this.primaryColor : this.secondaryColor;
    }

    @Environment(value=EnvType.CLIENT)
    public static SpawnEggItem forEntity(@Nullable EntityType<?> type) {
        return SPAWN_EGGS.get(type);
    }

    public static Iterable<SpawnEggItem> getAll() {
        return Iterables.unmodifiableIterable(SPAWN_EGGS.values());
    }

    public EntityType<?> getEntityType(@Nullable CompoundTag tag) {
        CompoundTag compoundTag;
        if (tag != null && tag.contains("EntityTag", 10) && (compoundTag = tag.getCompound("EntityTag")).contains("id", 8)) {
            return EntityType.get(compoundTag.getString("id")).orElse(this.type);
        }
        return this.type;
    }
}

