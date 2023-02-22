/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.village;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.village.Merchant;
import net.minecraft.village.MerchantInventory;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SimpleMerchant
implements Merchant {
    private final MerchantInventory merchantInventory;
    private final PlayerEntity player;
    private TradeOfferList offers = new TradeOfferList();
    private int experience;

    public SimpleMerchant(PlayerEntity playerEntity) {
        this.player = playerEntity;
        this.merchantInventory = new MerchantInventory(this);
    }

    @Override
    @Nullable
    public PlayerEntity getCurrentCustomer() {
        return this.player;
    }

    @Override
    public void setCurrentCustomer(@Nullable PlayerEntity customer) {
    }

    @Override
    public TradeOfferList getOffers() {
        return this.offers;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setOffersFromServer(@Nullable TradeOfferList offers) {
        this.offers = offers;
    }

    @Override
    public void trade(TradeOffer offer) {
        offer.use();
    }

    @Override
    public void onSellingItem(ItemStack stack) {
    }

    @Override
    public World getMerchantWorld() {
        return this.player.world;
    }

    @Override
    public int getExperience() {
        return this.experience;
    }

    @Override
    public void setExperienceFromServer(int experience) {
        this.experience = experience;
    }

    @Override
    public boolean isLeveledMerchant() {
        return true;
    }

    @Override
    public SoundEvent getYesSound() {
        return SoundEvents.ENTITY_VILLAGER_YES;
    }
}

