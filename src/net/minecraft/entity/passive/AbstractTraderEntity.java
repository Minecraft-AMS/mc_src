/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.HashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Npc;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTraderEntity
extends PassiveEntity
implements Npc,
Trader {
    private static final TrackedData<Integer> HEAD_ROLLING_TIME_LEFT = DataTracker.registerData(AbstractTraderEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Nullable
    private PlayerEntity customer;
    @Nullable
    protected TraderOfferList offers;
    private final BasicInventory inventory = new BasicInventory(8);

    public AbstractTraderEntity(EntityType<? extends AbstractTraderEntity> entityType, World world) {
        super((EntityType<? extends PassiveEntity>)entityType, world);
    }

    public int getHeadRollingTimeLeft() {
        return this.dataTracker.get(HEAD_ROLLING_TIME_LEFT);
    }

    public void setHeadRollingTimeLeft(int ticks) {
        this.dataTracker.set(HEAD_ROLLING_TIME_LEFT, ticks);
    }

    @Override
    public int getExperience() {
        return 0;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        if (this.isBaby()) {
            return 0.81f;
        }
        return 1.62f;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HEAD_ROLLING_TIME_LEFT, 0);
    }

    @Override
    public void setCurrentCustomer(@Nullable PlayerEntity customer) {
        this.customer = customer;
    }

    @Override
    @Nullable
    public PlayerEntity getCurrentCustomer() {
        return this.customer;
    }

    public boolean hasCustomer() {
        return this.customer != null;
    }

    @Override
    public TraderOfferList getOffers() {
        if (this.offers == null) {
            this.offers = new TraderOfferList();
            this.fillRecipes();
        }
        return this.offers;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setOffersFromServer(@Nullable TraderOfferList traderOfferList) {
    }

    @Override
    public void setExperienceFromServer(int experience) {
    }

    @Override
    public void trade(TradeOffer tradeOffer) {
        tradeOffer.use();
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
        this.afterUsing(tradeOffer);
        if (this.customer instanceof ServerPlayerEntity) {
            Criterions.VILLAGER_TRADE.handle((ServerPlayerEntity)this.customer, this, tradeOffer.getMutableSellItem());
        }
    }

    protected abstract void afterUsing(TradeOffer var1);

    @Override
    public boolean isLevelledTrader() {
        return true;
    }

    @Override
    public void onSellingItem(ItemStack itemStack) {
        if (!this.world.isClient && this.ambientSoundChance > -this.getMinAmbientSoundDelay() + 20) {
            this.ambientSoundChance = -this.getMinAmbientSoundDelay();
            this.playSound(this.getTradingSound(!itemStack.isEmpty()), this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public SoundEvent method_18010() {
        return SoundEvents.ENTITY_VILLAGER_YES;
    }

    protected SoundEvent getTradingSound(boolean sold) {
        return sold ? SoundEvents.ENTITY_VILLAGER_YES : SoundEvents.ENTITY_VILLAGER_NO;
    }

    public void playCelebrateSound() {
        this.playSound(SoundEvents.ENTITY_VILLAGER_CELEBRATE, this.getSoundVolume(), this.getSoundPitch());
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        TraderOfferList traderOfferList = this.getOffers();
        if (!traderOfferList.isEmpty()) {
            tag.put("Offers", traderOfferList.toTag());
        }
        ListTag listTag = new ListTag();
        for (int i = 0; i < this.inventory.getInvSize(); ++i) {
            ItemStack itemStack = this.inventory.getInvStack(i);
            if (itemStack.isEmpty()) continue;
            listTag.add(itemStack.toTag(new CompoundTag()));
        }
        tag.put("Inventory", listTag);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.contains("Offers", 10)) {
            this.offers = new TraderOfferList(tag.getCompound("Offers"));
        }
        ListTag listTag = tag.getList("Inventory", 10);
        for (int i = 0; i < listTag.size(); ++i) {
            ItemStack itemStack = ItemStack.fromTag(listTag.getCompound(i));
            if (itemStack.isEmpty()) continue;
            this.inventory.add(itemStack);
        }
    }

    @Override
    @Nullable
    public Entity changeDimension(DimensionType newDimension) {
        this.resetCustomer();
        return super.changeDimension(newDimension);
    }

    protected void resetCustomer() {
        this.setCurrentCustomer(null);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.resetCustomer();
    }

    @Environment(value=EnvType.CLIENT)
    protected void produceParticles(ParticleEffect parameters) {
        for (int i = 0; i < 5; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.world.addParticle(parameters, this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0f) - (double)this.getWidth(), this.y + 1.0 + (double)(this.random.nextFloat() * this.getHeight()), this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0f) - (double)this.getWidth(), d, e, f);
        }
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }

    public BasicInventory getInventory() {
        return this.inventory;
    }

    @Override
    public boolean equip(int slot, ItemStack item) {
        if (super.equip(slot, item)) {
            return true;
        }
        int i = slot - 300;
        if (i >= 0 && i < this.inventory.getInvSize()) {
            this.inventory.setInvStack(i, item);
            return true;
        }
        return false;
    }

    @Override
    public World getTraderWorld() {
        return this.world;
    }

    protected abstract void fillRecipes();

    protected void fillRecipesFromPool(TraderOfferList recipeList, TradeOffers.Factory[] pool, int count) {
        HashSet set = Sets.newHashSet();
        if (pool.length > count) {
            while (set.size() < count) {
                set.add(this.random.nextInt(pool.length));
            }
        } else {
            for (int i = 0; i < pool.length; ++i) {
                set.add(i);
            }
        }
        for (Integer integer : set) {
            TradeOffers.Factory factory = pool[integer];
            TradeOffer tradeOffer = factory.create(this, this.random);
            if (tradeOffer == null) continue;
            recipeList.add(tradeOffer);
        }
    }
}

