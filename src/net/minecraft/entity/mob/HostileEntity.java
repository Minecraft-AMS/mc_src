/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public abstract class HostileEntity
extends MobEntityWithAi
implements Monster {
    protected HostileEntity(EntityType<? extends HostileEntity> type, World world) {
        super((EntityType<? extends MobEntityWithAi>)type, world);
        this.experiencePoints = 5;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    public void tickMovement() {
        this.tickHandSwing();
        this.updateDespawnCounter();
        super.tickMovement();
    }

    protected void updateDespawnCounter() {
        float f = this.getBrightnessAtEyes();
        if (f > 0.5f) {
            this.despawnCounter += 2;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient && this.world.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
        }
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_HOSTILE_SWIM;
    }

    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_HOSTILE_SPLASH;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_HOSTILE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_HOSTILE_DEATH;
    }

    @Override
    protected SoundEvent getFallSound(int distance) {
        if (distance > 4) {
            return SoundEvents.ENTITY_HOSTILE_BIG_FALL;
        }
        return SoundEvents.ENTITY_HOSTILE_SMALL_FALL;
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, CollisionView world) {
        return 0.5f - world.getBrightness(pos);
    }

    public static boolean method_20679(IWorld iWorld, BlockPos blockPos, Random random) {
        if (iWorld.getLightLevel(LightType.SKY, blockPos) > random.nextInt(32)) {
            return false;
        }
        int i = iWorld.getWorld().isThundering() ? iWorld.method_8603(blockPos, 10) : iWorld.getLightLevel(blockPos);
        return i <= random.nextInt(8);
    }

    public static boolean method_20680(EntityType<? extends HostileEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
        return iWorld.getDifficulty() != Difficulty.PEACEFUL && HostileEntity.method_20679(iWorld, blockPos, random) && HostileEntity.method_20636(entityType, iWorld, spawnType, blockPos, random);
    }

    public static boolean method_20681(EntityType<? extends HostileEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
        return iWorld.getDifficulty() != Difficulty.PEACEFUL && HostileEntity.method_20636(entityType, iWorld, spawnType, blockPos, random);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributes().register(EntityAttributes.ATTACK_DAMAGE);
    }

    @Override
    protected boolean canDropLootAndXp() {
        return true;
    }

    public boolean isAngryAt(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack getArrowType(ItemStack itemStack) {
        if (itemStack.getItem() instanceof RangedWeaponItem) {
            Predicate<ItemStack> predicate = ((RangedWeaponItem)itemStack.getItem()).getHeldProjectiles();
            ItemStack itemStack2 = RangedWeaponItem.getHeldProjectile(this, predicate);
            return itemStack2.isEmpty() ? new ItemStack(Items.ARROW) : itemStack2;
        }
        return ItemStack.EMPTY;
    }
}
