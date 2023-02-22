/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.JsonHelper;

public class SetBannerPatternFunction
extends ConditionalLootFunction {
    final List<Pair<BannerPattern, DyeColor>> patterns;
    final boolean append;

    SetBannerPatternFunction(LootCondition[] conditions, List<Pair<BannerPattern, DyeColor>> patterns, boolean append) {
        super(conditions);
        this.patterns = patterns;
        this.append = append;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        NbtList nbtList2;
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if (nbtCompound == null) {
            nbtCompound = new NbtCompound();
        }
        BannerPattern.Patterns patterns = new BannerPattern.Patterns();
        this.patterns.forEach(patterns::add);
        NbtList nbtList = patterns.toNbt();
        if (this.append) {
            nbtList2 = nbtCompound.getList("Patterns", 10).copy();
            nbtList2.addAll(nbtList);
        } else {
            nbtList2 = nbtList;
        }
        nbtCompound.put("Patterns", nbtList2);
        BlockItem.setBlockEntityNbt(stack, BlockEntityType.BANNER, nbtCompound);
        return stack;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.SET_BANNER_PATTERN;
    }

    public static Builder builder(boolean append) {
        return new Builder(append);
    }

    public static class Builder
    extends ConditionalLootFunction.Builder<Builder> {
        private final ImmutableList.Builder<Pair<BannerPattern, DyeColor>> patterns = ImmutableList.builder();
        private final boolean append;

        Builder(boolean append) {
            this.append = append;
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        @Override
        public LootFunction build() {
            return new SetBannerPatternFunction(this.getConditions(), (List<Pair<BannerPattern, DyeColor>>)this.patterns.build(), this.append);
        }

        public Builder pattern(BannerPattern pattern, DyeColor color) {
            this.patterns.add((Object)Pair.of((Object)((Object)pattern), (Object)color));
            return this;
        }

        @Override
        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<SetBannerPatternFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetBannerPatternFunction setBannerPatternFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, setBannerPatternFunction, jsonSerializationContext);
            JsonArray jsonArray = new JsonArray();
            setBannerPatternFunction.patterns.forEach(pair -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("pattern", ((BannerPattern)((Object)((Object)pair.getFirst()))).getName());
                jsonObject.addProperty("color", ((DyeColor)pair.getSecond()).getName());
                jsonArray.add((JsonElement)jsonObject);
            });
            jsonObject.add("patterns", (JsonElement)jsonArray);
            jsonObject.addProperty("append", Boolean.valueOf(setBannerPatternFunction.append));
        }

        @Override
        public SetBannerPatternFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            ImmutableList.Builder builder = ImmutableList.builder();
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "patterns");
            for (int i = 0; i < jsonArray.size(); ++i) {
                JsonObject jsonObject2 = JsonHelper.asObject(jsonArray.get(i), "pattern[" + i + "]");
                String string = JsonHelper.getString(jsonObject2, "pattern");
                BannerPattern bannerPattern = BannerPattern.byName(string);
                if (bannerPattern == null) {
                    throw new JsonSyntaxException("Unknown pattern: " + string);
                }
                String string2 = JsonHelper.getString(jsonObject2, "color");
                DyeColor dyeColor = DyeColor.byName(string2, null);
                if (dyeColor == null) {
                    throw new JsonSyntaxException("Unknown color: " + string2);
                }
                builder.add((Object)Pair.of((Object)((Object)bannerPattern), (Object)dyeColor));
            }
            boolean bl = JsonHelper.getBoolean(jsonObject, "append");
            return new SetBannerPatternFunction(lootConditions, (List<Pair<BannerPattern, DyeColor>>)builder.build(), bl);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

