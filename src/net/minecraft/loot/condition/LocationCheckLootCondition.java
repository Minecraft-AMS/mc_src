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
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.loot.condition.LootCondition;

public class LocationCheckLootCondition
implements LootCondition {
    private final LocationPredicate predicate;

    private LocationCheckLootCondition(LocationPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(LootContext lootContext) {
        BlockPos blockPos = lootContext.get(LootContextParameters.POSITION);
        return blockPos != null && this.predicate.test(lootContext.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static LootCondition.Builder builder(LocationPredicate.Builder predicateBuilder) {
        return () -> new LocationCheckLootCondition(predicateBuilder.build());
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
        }

        @Override
        public LocationCheckLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("predicate"));
            return new LocationCheckLootCondition(locationPredicate);
        }

        @Override
        public /* synthetic */ LootCondition fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}

