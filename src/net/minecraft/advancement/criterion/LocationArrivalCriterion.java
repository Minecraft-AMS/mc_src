/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class LocationArrivalCriterion
extends AbstractCriterion<Conditions> {
    private final Identifier id;

    public LocationArrivalCriterion(Identifier id) {
        this.id = id;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        LocationPredicate locationPredicate = LocationPredicate.fromJson((JsonElement)jsonObject);
        return new Conditions(this.id, locationPredicate);
    }

    public void trigger(ServerPlayerEntity player) {
        this.test(player.getAdvancementTracker(), conditions -> conditions.matches(player.getServerWorld(), player.getX(), player.getY(), player.getZ()));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final LocationPredicate location;

        public Conditions(Identifier id, LocationPredicate location) {
            super(id);
            this.location = location;
        }

        public static Conditions create(LocationPredicate location) {
            return new Conditions(Criterions.LOCATION.id, location);
        }

        public static Conditions createSleptInBed() {
            return new Conditions(Criterions.SLEPT_IN_BED.id, LocationPredicate.ANY);
        }

        public static Conditions createHeroOfTheVillage() {
            return new Conditions(Criterions.HERO_OF_THE_VILLAGE.id, LocationPredicate.ANY);
        }

        public boolean matches(ServerWorld world, double x, double y, double z) {
            return this.location.test(world, x, y, z);
        }

        @Override
        public JsonElement toJson() {
            return this.location.toJson();
        }
    }
}

