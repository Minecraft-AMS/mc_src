/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.village;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.MathHelper;

public class TradeOffer {
    private final ItemStack firstBuyItem;
    private final ItemStack secondBuyItem;
    private final ItemStack sellItem;
    private int uses;
    private final int maxUses;
    private boolean rewardingPlayerExperience = true;
    private int specialPrice;
    private int demandBonus;
    private float priceMultiplier;
    private int merchantExperience = 1;

    public TradeOffer(NbtCompound nbt) {
        this.firstBuyItem = ItemStack.fromNbt(nbt.getCompound("buy"));
        this.secondBuyItem = ItemStack.fromNbt(nbt.getCompound("buyB"));
        this.sellItem = ItemStack.fromNbt(nbt.getCompound("sell"));
        this.uses = nbt.getInt("uses");
        this.maxUses = nbt.contains("maxUses", 99) ? nbt.getInt("maxUses") : 4;
        if (nbt.contains("rewardExp", 1)) {
            this.rewardingPlayerExperience = nbt.getBoolean("rewardExp");
        }
        if (nbt.contains("xp", 3)) {
            this.merchantExperience = nbt.getInt("xp");
        }
        if (nbt.contains("priceMultiplier", 5)) {
            this.priceMultiplier = nbt.getFloat("priceMultiplier");
        }
        this.specialPrice = nbt.getInt("specialPrice");
        this.demandBonus = nbt.getInt("demand");
    }

    public TradeOffer(ItemStack buyItem, ItemStack sellItem, int maxUses, int merchantExperience, float priceMultiplier) {
        this(buyItem, ItemStack.EMPTY, sellItem, maxUses, merchantExperience, priceMultiplier);
    }

    public TradeOffer(ItemStack firstBuyItem, ItemStack secondBuyItem, ItemStack sellItem, int maxUses, int merchantExperience, float priceMultiplier) {
        this(firstBuyItem, secondBuyItem, sellItem, 0, maxUses, merchantExperience, priceMultiplier);
    }

    public TradeOffer(ItemStack firstBuyItem, ItemStack secondBuyItem, ItemStack sellItem, int uses, int maxUses, int merchantExperience, float priceMultiplier) {
        this(firstBuyItem, secondBuyItem, sellItem, uses, maxUses, merchantExperience, priceMultiplier, 0);
    }

    public TradeOffer(ItemStack firstBuyItem, ItemStack secondBuyItem, ItemStack sellItem, int uses, int maxUses, int merchantExperience, float priceMultiplier, int demandBonus) {
        this.firstBuyItem = firstBuyItem;
        this.secondBuyItem = secondBuyItem;
        this.sellItem = sellItem;
        this.uses = uses;
        this.maxUses = maxUses;
        this.merchantExperience = merchantExperience;
        this.priceMultiplier = priceMultiplier;
        this.demandBonus = demandBonus;
    }

    public ItemStack getOriginalFirstBuyItem() {
        return this.firstBuyItem;
    }

    public ItemStack getAdjustedFirstBuyItem() {
        int i = this.firstBuyItem.getCount();
        ItemStack itemStack = this.firstBuyItem.copy();
        int j = Math.max(0, MathHelper.floor((float)(i * this.demandBonus) * this.priceMultiplier));
        itemStack.setCount(MathHelper.clamp(i + j + this.specialPrice, 1, this.firstBuyItem.getItem().getMaxCount()));
        return itemStack;
    }

    public ItemStack getSecondBuyItem() {
        return this.secondBuyItem;
    }

    public ItemStack getSellItem() {
        return this.sellItem;
    }

    public void updateDemandBonus() {
        this.demandBonus = this.demandBonus + this.uses - (this.maxUses - this.uses);
    }

    public ItemStack copySellItem() {
        return this.sellItem.copy();
    }

    public int getUses() {
        return this.uses;
    }

    public void resetUses() {
        this.uses = 0;
    }

    public int getMaxUses() {
        return this.maxUses;
    }

    public void use() {
        ++this.uses;
    }

    public int getDemandBonus() {
        return this.demandBonus;
    }

    public void increaseSpecialPrice(int increment) {
        this.specialPrice += increment;
    }

    public void clearSpecialPrice() {
        this.specialPrice = 0;
    }

    public int getSpecialPrice() {
        return this.specialPrice;
    }

    public void setSpecialPrice(int specialPrice) {
        this.specialPrice = specialPrice;
    }

    public float getPriceMultiplier() {
        return this.priceMultiplier;
    }

    public int getMerchantExperience() {
        return this.merchantExperience;
    }

    public boolean isDisabled() {
        return this.uses >= this.maxUses;
    }

    public void disable() {
        this.uses = this.maxUses;
    }

    public boolean hasBeenUsed() {
        return this.uses > 0;
    }

    public boolean shouldRewardPlayerExperience() {
        return this.rewardingPlayerExperience;
    }

    public NbtCompound toNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.put("buy", this.firstBuyItem.writeNbt(new NbtCompound()));
        nbtCompound.put("sell", this.sellItem.writeNbt(new NbtCompound()));
        nbtCompound.put("buyB", this.secondBuyItem.writeNbt(new NbtCompound()));
        nbtCompound.putInt("uses", this.uses);
        nbtCompound.putInt("maxUses", this.maxUses);
        nbtCompound.putBoolean("rewardExp", this.rewardingPlayerExperience);
        nbtCompound.putInt("xp", this.merchantExperience);
        nbtCompound.putFloat("priceMultiplier", this.priceMultiplier);
        nbtCompound.putInt("specialPrice", this.specialPrice);
        nbtCompound.putInt("demand", this.demandBonus);
        return nbtCompound;
    }

    public boolean matchesBuyItems(ItemStack first, ItemStack second) {
        return this.acceptsBuy(first, this.getAdjustedFirstBuyItem()) && first.getCount() >= this.getAdjustedFirstBuyItem().getCount() && this.acceptsBuy(second, this.secondBuyItem) && second.getCount() >= this.secondBuyItem.getCount();
    }

    private boolean acceptsBuy(ItemStack given, ItemStack sample) {
        if (sample.isEmpty() && given.isEmpty()) {
            return true;
        }
        ItemStack itemStack = given.copy();
        if (itemStack.getItem().isDamageable()) {
            itemStack.setDamage(itemStack.getDamage());
        }
        return ItemStack.areItemsEqual(itemStack, sample) && (!sample.hasNbt() || itemStack.hasNbt() && NbtHelper.matches(sample.getNbt(), itemStack.getNbt(), false));
    }

    public boolean depleteBuyItems(ItemStack firstBuyStack, ItemStack secondBuyStack) {
        if (!this.matchesBuyItems(firstBuyStack, secondBuyStack)) {
            return false;
        }
        firstBuyStack.decrement(this.getAdjustedFirstBuyItem().getCount());
        if (!this.getSecondBuyItem().isEmpty()) {
            secondBuyStack.decrement(this.getSecondBuyItem().getCount());
        }
        return true;
    }
}

