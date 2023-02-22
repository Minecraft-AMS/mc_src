/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class PassiveEntity
extends MobEntityWithAi {
    private static final TrackedData<Boolean> CHILD = DataTracker.registerData(PassiveEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected int breedingAge;
    protected int forcedAge;
    protected int happyTicksRemaining;

    protected PassiveEntity(EntityType<? extends PassiveEntity> type, World world) {
        super((EntityType<? extends MobEntityWithAi>)type, world);
    }

    @Nullable
    public abstract PassiveEntity createChild(PassiveEntity var1);

    protected void onPlayerSpawnedChild(PlayerEntity player, PassiveEntity child) {
    }

    @Override
    public boolean interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        if (item instanceof SpawnEggItem && ((SpawnEggItem)item).isOfSameEntityType(itemStack.getTag(), this.getType())) {
            PassiveEntity passiveEntity;
            if (!this.world.isClient && (passiveEntity = this.createChild(this)) != null) {
                passiveEntity.setBreedingAge(-24000);
                passiveEntity.refreshPositionAndAngles(this.x, this.y, this.z, 0.0f, 0.0f);
                this.world.spawnEntity(passiveEntity);
                if (itemStack.hasCustomName()) {
                    passiveEntity.setCustomName(itemStack.getName());
                }
                this.onPlayerSpawnedChild(player, passiveEntity);
                if (!player.abilities.creativeMode) {
                    itemStack.decrement(1);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CHILD, false);
    }

    public int getBreedingAge() {
        if (this.world.isClient) {
            return this.dataTracker.get(CHILD) != false ? -1 : 1;
        }
        return this.breedingAge;
    }

    public void growUp(int age, boolean overGrow) {
        int i;
        int j = i = this.getBreedingAge();
        if ((i += age * 20) > 0) {
            i = 0;
        }
        int k = i - j;
        this.setBreedingAge(i);
        if (overGrow) {
            this.forcedAge += k;
            if (this.happyTicksRemaining == 0) {
                this.happyTicksRemaining = 40;
            }
        }
        if (this.getBreedingAge() == 0) {
            this.setBreedingAge(this.forcedAge);
        }
    }

    public void growUp(int age) {
        this.growUp(age, false);
    }

    public void setBreedingAge(int age) {
        int i = this.breedingAge;
        this.breedingAge = age;
        if (i < 0 && age >= 0 || i >= 0 && age < 0) {
            this.dataTracker.set(CHILD, age < 0);
            this.onGrowUp();
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Age", this.getBreedingAge());
        tag.putInt("ForcedAge", this.forcedAge);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setBreedingAge(tag.getInt("Age"));
        this.forcedAge = tag.getInt("ForcedAge");
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (CHILD.equals(data)) {
            this.calculateDimensions();
        }
        super.onTrackedDataSet(data);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.world.isClient) {
            if (this.happyTicksRemaining > 0) {
                if (this.happyTicksRemaining % 4 == 0) {
                    this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0f) - (double)this.getWidth(), this.y + 0.5 + (double)(this.random.nextFloat() * this.getHeight()), this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0f) - (double)this.getWidth(), 0.0, 0.0, 0.0);
                }
                --this.happyTicksRemaining;
            }
        } else if (this.isAlive()) {
            int i = this.getBreedingAge();
            if (i < 0) {
                this.setBreedingAge(++i);
            } else if (i > 0) {
                this.setBreedingAge(--i);
            }
        }
    }

    protected void onGrowUp() {
    }

    @Override
    public boolean isBaby() {
        return this.getBreedingAge() < 0;
    }
}

