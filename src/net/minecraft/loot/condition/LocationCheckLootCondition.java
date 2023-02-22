/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;

public class LocationCheckLootCondition
implements LootCondition {
    private final LocationPredicate predicate;
    private final BlockPos offset;

    public LocationCheckLootCondition(LocationPredicate predicate, BlockPos offset) {
        this.predicate = predicate;
        this.offset = offset;
    }

    @Override
    public boolean test(LootContext lootContext) {
        BlockPos blockPos = lootContext.get(LootContextParameters.POSITION);
        return blockPos != null && this.predicate.test(lootContext.getWorld(), blockPos.getX() + this.offset.getX(), blockPos.getY() + this.offset.getY(), blockPos.getZ() + this.offset.getZ());
    }

    public static LootCondition.Builder builder(LocationPredicate.Builder predicateBuilder) {
        return () -> new LocationCheckLootCondition(predicateBuilder.build(), BlockPos.ORIGIN);
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Factory
    extends LootCondition.Factory<LocationCheckLootCondition> {
        public Factory() {
            super(new Identifier("location_check"), LocationCheckLootCondition.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, LocationCheckLootCondition locationCheckLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("predicate", locationCheckLootCondition.predicate.toJson());
            if (locationCheckLootCondition.offset.getX() != 0) {
                jsonObject.addProperty("offsetX", (Number)locationCheckLootCondition.offset.getX());
            }
            if (locationCheckLootCondition.offset.getY() != 0) {
                jsonObject.addProperty("offsetY", (Number)locationCheckLootCondition.offset.getY());
            }
            if (locationCheckLootCondition.offset.getZ() != 0) {
                jsonObject.addProperty("offsetZ", (Number)locationCheckLootCondition.offset.getZ());
            }
        }

        @Override
        public LocationCheckLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("predicate"));
            int i = JsonHelper.getInt(jsonObject, "offsetX", 0);
            int j = JsonHelper.getInt(jsonObject, "offsetY", 0);
            int k = JsonHelper.getInt(jsonObject, "offsetZ", 0);
            return new LocationCheckLootCondition(locationPredicate, new BlockPos(i, j, k));
        }

        @Override
        public /* synthetic */ LootCondition fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}

