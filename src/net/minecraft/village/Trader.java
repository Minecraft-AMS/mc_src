/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.village;

import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.MerchantContainer;
import net.minecraft.container.SimpleNamedContainerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface Trader {
    public void setCurrentCustomer(@Nullable PlayerEntity var1);

    @Nullable
    public PlayerEntity getCurrentCustomer();

    public TraderOfferList getOffers();

    @Environment(value=EnvType.CLIENT)
    public void setOffersFromServer(@Nullable TraderOfferList var1);

    public void trade(TradeOffer var1);

    public void onSellingItem(ItemStack var1);

    public World getTraderWorld();

    public int getExperience();

    public void setExperienceFromServer(int var1);

    public boolean isLevelledTrader();

    public SoundEvent method_18010();

    default public boolean canRefreshTrades() {
        return false;
    }

    default public void sendOffers(PlayerEntity playerEntity2, Text text, int i2) {
        TraderOfferList traderOfferList;
        OptionalInt optionalInt = playerEntity2.openContainer(new SimpleNamedContainerFactory((i, playerInventory, playerEntity) -> new MerchantContainer(i, playerInventory, this), text));
        if (optionalInt.isPresent() && !(traderOfferList = this.getOffers()).isEmpty()) {
            playerEntity2.sendTradeOffers(optionalInt.getAsInt(), traderOfferList, i2, this.getExperience(), this.isLevelledTrader(), this.canRefreshTrades());
        }
    }
}

