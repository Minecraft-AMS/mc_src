/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.predicate.item;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class ItemPredicate {
    public static final ItemPredicate ANY = new ItemPredicate();
    @Nullable
    private final Tag<Item> tag;
    @Nullable
    private final Item item;
    private final NumberRange.IntRange count;
    private final NumberRange.IntRange durability;
    private final EnchantmentPredicate[] enchantments;
    @Nullable
    private final Potion potion;
    private final NbtPredicate nbt;

    public ItemPredicate() {
        this.tag = null;
        this.item = null;
        this.potion = null;
        this.count = NumberRange.IntRange.ANY;
        this.durability = NumberRange.IntRange.ANY;
        this.enchantments = new EnchantmentPredicate[0];
        this.nbt = NbtPredicate.ANY;
    }

    public ItemPredicate(@Nullable Tag<Item> tag, @Nullable Item item, NumberRange.IntRange count, NumberRange.IntRange durability, EnchantmentPredicate[] enchantments, @Nullable Potion potion, NbtPredicate nbtPredicate) {
        this.tag = tag;
        this.item = item;
        this.count = count;
        this.durability = durability;
        this.enchantments = enchantments;
        this.potion = potion;
        this.nbt = nbtPredicate;
    }

    public boolean test(ItemStack stack) {
        if (this == ANY) {
            return true;
        }
        if (this.tag != null && !this.tag.contains(stack.getItem())) {
            return false;
        }
        if (this.item != null && stack.getItem() != this.item) {
            return false;
        }
        if (!this.count.test(stack.getCount())) {
            return false;
        }
        if (!this.durability.isDummy() && !stack.isDamageable()) {
            return false;
        }
        if (!this.durability.test(stack.getMaxDamage() - stack.getDamage())) {
            return false;
        }
        if (!this.nbt.test(stack)) {
            return false;
        }
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
        for (int i = 0; i < this.enchantments.length; ++i) {
            if (this.enchantments[i].test(map)) continue;
            return false;
        }
        Potion potion = PotionUtil.getPotion(stack);
        return this.potion == null || this.potion == potion;
    }

    public static ItemPredicate fromJson(@Nullable JsonElement el) {
        if (el == null || el.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(el, "item");
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("count"));
        NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject.get("durability"));
        if (jsonObject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        NbtPredicate nbtPredicate = NbtPredicate.fromJson(jsonObject.get("nbt"));
        Item item = null;
        if (jsonObject.has("item")) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "item"));
            item = (Item)Registry.ITEM.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown item id '" + identifier + "'"));
        }
        Tag<Item> tag = null;
        if (jsonObject.has("tag")) {
            Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "tag"));
            tag = ItemTags.getContainer().get(identifier2);
            if (tag == null) {
                throw new JsonSyntaxException("Unknown item tag '" + identifier2 + "'");
            }
        }
        EnchantmentPredicate[] enchantmentPredicates = EnchantmentPredicate.deserializeAll(jsonObject.get("enchantments"));
        Potion potion = null;
        if (jsonObject.has("potion")) {
            Identifier identifier3 = new Identifier(JsonHelper.getString(jsonObject, "potion"));
            potion = (Potion)Registry.POTION.getOrEmpty(identifier3).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + identifier3 + "'"));
        }
        return new ItemPredicate(tag, item, intRange, intRange2, enchantmentPredicates, potion, nbtPredicate);
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (this.item != null) {
            jsonObject.addProperty("item", Registry.ITEM.getId(this.item).toString());
        }
        if (this.tag != null) {
            jsonObject.addProperty("tag", this.tag.getId().toString());
        }
        jsonObject.add("count", this.count.toJson());
        jsonObject.add("durability", this.durability.toJson());
        jsonObject.add("nbt", this.nbt.toJson());
        if (this.enchantments.length > 0) {
            JsonArray jsonArray = new JsonArray();
            for (EnchantmentPredicate enchantmentPredicate : this.enchantments) {
                jsonArray.add(enchantmentPredicate.serialize());
            }
            jsonObject.add("enchantments", (JsonElement)jsonArray);
        }
        if (this.potion != null) {
            jsonObject.addProperty("potion", Registry.POTION.getId(this.potion).toString());
        }
        return jsonObject;
    }

    public static ItemPredicate[] deserializeAll(@Nullable JsonElement el) {
        if (el == null || el.isJsonNull()) {
            return new ItemPredicate[0];
        }
        JsonArray jsonArray = JsonHelper.asArray(el, "items");
        ItemPredicate[] itemPredicates = new ItemPredicate[jsonArray.size()];
        for (int i = 0; i < itemPredicates.length; ++i) {
            itemPredicates[i] = ItemPredicate.fromJson(jsonArray.get(i));
        }
        return itemPredicates;
    }

    public static class Builder {
        private final List<EnchantmentPredicate> enchantments = Lists.newArrayList();
        @Nullable
        private Item item;
        @Nullable
        private Tag<Item> tag;
        private NumberRange.IntRange count = NumberRange.IntRange.ANY;
        private NumberRange.IntRange durability = NumberRange.IntRange.ANY;
        @Nullable
        private Potion potion;
        private NbtPredicate nbt = NbtPredicate.ANY;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder item(ItemConvertible itemConvertible) {
            this.item = itemConvertible.asItem();
            return this;
        }

        public Builder tag(Tag<Item> tag) {
            this.tag = tag;
            return this;
        }

        public Builder count(NumberRange.IntRange intRange) {
            this.count = intRange;
            return this;
        }

        public Builder nbt(CompoundTag nbt) {
            this.nbt = new NbtPredicate(nbt);
            return this;
        }

        public Builder enchantment(EnchantmentPredicate enchantmentPredicate) {
            this.enchantments.add(enchantmentPredicate);
            return this;
        }

        public ItemPredicate build() {
            return new ItemPredicate(this.tag, this.item, this.count, this.durability, this.enchantments.toArray(new EnchantmentPredicate[0]), this.potion, this.nbt);
        }
    }
}
