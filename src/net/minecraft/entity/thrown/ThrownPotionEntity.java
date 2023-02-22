/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.EnvironmentInterface
 *  net.fabricmc.api.EnvironmentInterfaces
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.thrown;

import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.thrown.ThrownEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@EnvironmentInterfaces(value={@EnvironmentInterface(value=EnvType.CLIENT, itf=FlyingItemEntity.class)})
public class ThrownPotionEntity
extends ThrownEntity
implements FlyingItemEntity {
    private static final TrackedData<ItemStack> ITEM_STACK = DataTracker.registerData(ThrownPotionEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Predicate<LivingEntity> WATER_HURTS = ThrownPotionEntity::doesWaterHurt;

    public ThrownPotionEntity(EntityType<? extends ThrownPotionEntity> entityType, World world) {
        super((EntityType<? extends ThrownEntity>)entityType, world);
    }

    public ThrownPotionEntity(World world, LivingEntity livingEntity) {
        super(EntityType.POTION, livingEntity, world);
    }

    public ThrownPotionEntity(World world, double x, double y, double d) {
        super(EntityType.POTION, x, y, d, world);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(ITEM_STACK, ItemStack.EMPTY);
    }

    @Override
    public ItemStack getStack() {
        ItemStack itemStack = this.getDataTracker().get(ITEM_STACK);
        if (itemStack.getItem() != Items.SPLASH_POTION && itemStack.getItem() != Items.LINGERING_POTION) {
            if (this.world != null) {
                LOGGER.error("ThrownPotion entity {} has no item?!", (Object)this.getEntityId());
            }
            return new ItemStack(Items.SPLASH_POTION);
        }
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.getDataTracker().set(ITEM_STACK, itemStack.copy());
    }

    @Override
    protected float getGravity() {
        return 0.05f;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        boolean bl;
        if (this.world.isClient) {
            return;
        }
        ItemStack itemStack = this.getStack();
        Potion potion = PotionUtil.getPotion(itemStack);
        List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
        boolean bl2 = bl = potion == Potions.WATER && list.isEmpty();
        if (hitResult.getType() == HitResult.Type.BLOCK && bl) {
            BlockHitResult blockHitResult = (BlockHitResult)hitResult;
            Direction direction = blockHitResult.getSide();
            BlockPos blockPos = blockHitResult.getBlockPos().offset(direction);
            this.extinguishFire(blockPos, direction);
            this.extinguishFire(blockPos.offset(direction.getOpposite()), direction);
            for (Direction direction2 : Direction.Type.HORIZONTAL) {
                this.extinguishFire(blockPos.offset(direction2), direction2);
            }
        }
        if (bl) {
            this.damageEntitiesHurtByWater();
        } else if (!list.isEmpty()) {
            if (this.isLingering()) {
                this.applyLingeringPotion(itemStack, potion);
            } else {
                this.applySplashPotion(list, hitResult.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)hitResult).getEntity() : null);
            }
        }
        int i = potion.hasInstantEffect() ? 2007 : 2002;
        this.world.playLevelEvent(i, new BlockPos(this), PotionUtil.getColor(itemStack));
        this.remove();
    }

    private void damageEntitiesHurtByWater() {
        Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
        List<LivingEntity> list = this.world.getEntities(LivingEntity.class, box, WATER_HURTS);
        if (!list.isEmpty()) {
            for (LivingEntity livingEntity : list) {
                double d = this.squaredDistanceTo(livingEntity);
                if (!(d < 16.0) || !ThrownPotionEntity.doesWaterHurt(livingEntity)) continue;
                livingEntity.damage(DamageSource.magic(livingEntity, this.getOwner()), 1.0f);
            }
        }
    }

    private void applySplashPotion(List<StatusEffectInstance> list, @Nullable Entity entity) {
        Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
        List<LivingEntity> list2 = this.world.getNonSpectatingEntities(LivingEntity.class, box);
        if (!list2.isEmpty()) {
            for (LivingEntity livingEntity : list2) {
                double d;
                if (!livingEntity.isAffectedBySplashPotions() || !((d = this.squaredDistanceTo(livingEntity)) < 16.0)) continue;
                double e = 1.0 - Math.sqrt(d) / 4.0;
                if (livingEntity == entity) {
                    e = 1.0;
                }
                for (StatusEffectInstance statusEffectInstance : list) {
                    StatusEffect statusEffect = statusEffectInstance.getEffectType();
                    if (statusEffect.isInstant()) {
                        statusEffect.applyInstantEffect(this, this.getOwner(), livingEntity, statusEffectInstance.getAmplifier(), e);
                        continue;
                    }
                    int i = (int)(e * (double)statusEffectInstance.getDuration() + 0.5);
                    if (i <= 20) continue;
                    livingEntity.addStatusEffect(new StatusEffectInstance(statusEffect, i, statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles()));
                }
            }
        }
    }

    private void applyLingeringPotion(ItemStack itemStack, Potion potion) {
        AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.world, this.x, this.y, this.z);
        areaEffectCloudEntity.setOwner(this.getOwner());
        areaEffectCloudEntity.setRadius(3.0f);
        areaEffectCloudEntity.setRadiusOnUse(-0.5f);
        areaEffectCloudEntity.setWaitTime(10);
        areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());
        areaEffectCloudEntity.setPotion(potion);
        for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(itemStack)) {
            areaEffectCloudEntity.addEffect(new StatusEffectInstance(statusEffectInstance));
        }
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null && compoundTag.contains("CustomPotionColor", 99)) {
            areaEffectCloudEntity.setColor(compoundTag.getInt("CustomPotionColor"));
        }
        this.world.spawnEntity(areaEffectCloudEntity);
    }

    private boolean isLingering() {
        return this.getStack().getItem() == Items.LINGERING_POTION;
    }

    private void extinguishFire(BlockPos blockPos, Direction direction) {
        BlockState blockState = this.world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block == Blocks.FIRE) {
            this.world.method_8506(null, blockPos.offset(direction), direction.getOpposite());
        } else if (block == Blocks.CAMPFIRE && blockState.get(CampfireBlock.LIT).booleanValue()) {
            this.world.playLevelEvent(null, 1009, blockPos, 0);
            this.world.setBlockState(blockPos, (BlockState)blockState.with(CampfireBlock.LIT, false));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        ItemStack itemStack = ItemStack.fromTag(tag.getCompound("Potion"));
        if (itemStack.isEmpty()) {
            this.remove();
        } else {
            this.setItemStack(itemStack);
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        ItemStack itemStack = this.getStack();
        if (!itemStack.isEmpty()) {
            tag.put("Potion", itemStack.toTag(new CompoundTag()));
        }
    }

    private static boolean doesWaterHurt(LivingEntity entityHit) {
        return entityHit instanceof EndermanEntity || entityHit instanceof BlazeEntity;
    }
}

