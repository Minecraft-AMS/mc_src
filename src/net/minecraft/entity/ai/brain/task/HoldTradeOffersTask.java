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
import java.util.List;
import java.util.Map;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;
import org.jetbrains.annotations.Nullable;

public class HoldTradeOffersTask
extends Task<VillagerEntity> {
    @Nullable
    private ItemStack field_18392;
    private final List<ItemStack> offers = Lists.newArrayList();
    private int field_18394;
    private int field_18395;
    private int field_18396;

    public HoldTradeOffersTask(int rminRunTime, int maxRunTime) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT)), rminRunTime, maxRunTime);
    }

    @Override
    public boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        Brain<VillagerEntity> brain = villagerEntity.getBrain();
        if (!brain.getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).isPresent()) {
            return false;
        }
        LivingEntity livingEntity = brain.getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
        return livingEntity.getType() == EntityType.PLAYER && villagerEntity.isAlive() && livingEntity.isAlive() && !villagerEntity.isBaby() && villagerEntity.squaredDistanceTo(livingEntity) <= 17.0;
    }

    @Override
    public boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        return this.shouldRun(serverWorld, villagerEntity) && this.field_18396 > 0 && villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    @Override
    public void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        super.run(serverWorld, villagerEntity, l);
        this.method_19603(villagerEntity);
        this.field_18394 = 0;
        this.field_18395 = 0;
        this.field_18396 = 40;
    }

    @Override
    public void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        LivingEntity livingEntity = this.method_19603(villagerEntity);
        this.method_19027(livingEntity, villagerEntity);
        if (!this.offers.isEmpty()) {
            this.method_19026(villagerEntity);
        } else {
            villagerEntity.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.field_18396 = Math.min(this.field_18396, 40);
        }
        --this.field_18396;
    }

    @Override
    public void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        super.finishRunning(serverWorld, villagerEntity, l);
        villagerEntity.getBrain().forget(MemoryModuleType.INTERACTION_TARGET);
        villagerEntity.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        this.field_18392 = null;
    }

    private void method_19027(LivingEntity livingEntity, VillagerEntity villagerEntity) {
        boolean bl = false;
        ItemStack itemStack = livingEntity.getMainHandStack();
        if (this.field_18392 == null || !ItemStack.areItemsEqualIgnoreDamage(this.field_18392, itemStack)) {
            this.field_18392 = itemStack;
            bl = true;
            this.offers.clear();
        }
        if (bl && !this.field_18392.isEmpty()) {
            this.method_19601(villagerEntity);
            if (!this.offers.isEmpty()) {
                this.field_18396 = 900;
                this.method_19598(villagerEntity);
            }
        }
    }

    private void method_19598(VillagerEntity villagerEntity) {
        villagerEntity.equipStack(EquipmentSlot.MAINHAND, this.offers.get(0));
    }

    private void method_19601(VillagerEntity villagerEntity) {
        for (TradeOffer tradeOffer : villagerEntity.getOffers()) {
            if (tradeOffer.isDisabled() || !this.method_19028(tradeOffer)) continue;
            this.offers.add(tradeOffer.getMutableSellItem());
        }
    }

    private boolean method_19028(TradeOffer tradeOffer) {
        return ItemStack.areItemsEqualIgnoreDamage(this.field_18392, tradeOffer.getAdjustedFirstBuyItem()) || ItemStack.areItemsEqualIgnoreDamage(this.field_18392, tradeOffer.getSecondBuyItem());
    }

    private LivingEntity method_19603(VillagerEntity villagerEntity) {
        Brain<VillagerEntity> brain = villagerEntity.getBrain();
        LivingEntity livingEntity = brain.getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
        brain.putMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(livingEntity));
        return livingEntity;
    }

    private void method_19026(VillagerEntity villagerEntity) {
        if (this.offers.size() >= 2 && ++this.field_18394 >= 40) {
            ++this.field_18395;
            this.field_18394 = 0;
            if (this.field_18395 > this.offers.size() - 1) {
                this.field_18395 = 0;
            }
            villagerEntity.equipStack(EquipmentSlot.MAINHAND, this.offers.get(this.field_18395));
        }
    }

    @Override
    public /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    public /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    public /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    public /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (VillagerEntity)entity, time);
    }
}
