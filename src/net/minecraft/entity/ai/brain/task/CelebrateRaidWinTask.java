/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.SeekSkyTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.item.FireworkItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class CelebrateRaidWinTask
extends Task<VillagerEntity> {
    @Nullable
    private Raid raid;

    public CelebrateRaidWinTask(int minRunTime, int maxRunTime) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(), minRunTime, maxRunTime);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        this.raid = serverWorld.getRaidAt(new BlockPos(villagerEntity));
        return this.raid != null && this.raid.hasWon() && SeekSkyTask.isSkyVisible(serverWorld, villagerEntity);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        return this.raid != null && !this.raid.hasStopped();
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        this.raid = null;
        villagerEntity.getBrain().refreshActivities(serverWorld.getTimeOfDay(), serverWorld.getTime());
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        Random random = villagerEntity.getRandom();
        if (random.nextInt(100) == 0) {
            villagerEntity.playCelebrateSound();
        }
        if (random.nextInt(200) == 0 && SeekSkyTask.isSkyVisible(serverWorld, villagerEntity)) {
            DyeColor dyeColor = DyeColor.values()[random.nextInt(DyeColor.values().length)];
            int i = random.nextInt(3);
            ItemStack itemStack = this.createFirework(dyeColor, i);
            FireworkEntity fireworkEntity = new FireworkEntity(villagerEntity.world, villagerEntity.x, villagerEntity.y + (double)villagerEntity.getStandingEyeHeight(), villagerEntity.z, itemStack);
            villagerEntity.world.spawnEntity(fireworkEntity);
        }
    }

    private ItemStack createFirework(DyeColor color, int flight) {
        ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET, 1);
        ItemStack itemStack2 = new ItemStack(Items.FIREWORK_STAR);
        CompoundTag compoundTag = itemStack2.getOrCreateSubTag("Explosion");
        ArrayList list = Lists.newArrayList();
        list.add(color.getFireworkColor());
        compoundTag.putIntArray("Colors", list);
        compoundTag.putByte("Type", (byte)FireworkItem.Type.BURST.getId());
        CompoundTag compoundTag2 = itemStack.getOrCreateSubTag("Fireworks");
        ListTag listTag = new ListTag();
        CompoundTag compoundTag3 = itemStack2.getSubTag("Explosion");
        if (compoundTag3 != null) {
            listTag.add(compoundTag3);
        }
        compoundTag2.putByte("Flight", (byte)flight);
        if (!listTag.isEmpty()) {
            compoundTag2.put("Explosions", listTag);
        }
        return itemStack;
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (VillagerEntity)entity, time);
    }
}

