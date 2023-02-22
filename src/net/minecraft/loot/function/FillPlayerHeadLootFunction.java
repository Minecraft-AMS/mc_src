/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.authlib.GameProfile;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class FillPlayerHeadLootFunction
extends ConditionalLootFunction {
    private final LootContext.EntityTarget entity;

    public FillPlayerHeadLootFunction(LootCondition[] conditions, LootContext.EntityTarget entity) {
        super(conditions);
        this.entity = entity;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(this.entity.getParameter());
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        Entity entity;
        if (stack.getItem() == Items.PLAYER_HEAD && (entity = context.get(this.entity.getParameter())) instanceof PlayerEntity) {
            GameProfile gameProfile = ((PlayerEntity)entity).getGameProfile();
            stack.getOrCreateTag().put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
        }
        return stack;
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<FillPlayerHeadLootFunction> {
        public Factory() {
            super(new Identifier("fill_player_head"), FillPlayerHeadLootFunction.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, FillPlayerHeadLootFunction fillPlayerHeadLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, fillPlayerHeadLootFunction, jsonSerializationContext);
            jsonObject.add("entity", jsonSerializationContext.serialize((Object)fillPlayerHeadLootFunction.entity));
        }

        @Override
        public FillPlayerHeadLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            LootContext.EntityTarget entityTarget = JsonHelper.deserialize(jsonObject, "entity", jsonDeserializationContext, LootContext.EntityTarget.class);
            return new FillPlayerHeadLootFunction(lootConditions, entityTarget);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

