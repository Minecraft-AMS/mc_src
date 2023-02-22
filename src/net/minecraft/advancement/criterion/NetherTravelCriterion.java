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
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class NetherTravelCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("nether_travel");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("entered"));
        LocationPredicate locationPredicate2 = LocationPredicate.fromJson(jsonObject.get("exited"));
        DistancePredicate distancePredicate = DistancePredicate.deserialize(jsonObject.get("distance"));
        return new Conditions(locationPredicate, locationPredicate2, distancePredicate);
    }

    public void trigger(ServerPlayerEntity player, Vec3d enteredPos) {
        this.test(player.getAdvancementTracker(), conditions -> conditions.matches(player.getServerWorld(), enteredPos, player.getX(), player.getY(), player.getZ()));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final LocationPredicate enteredPos;
        private final LocationPredicate exitedPos;
        private final DistancePredicate distance;

        public Conditions(LocationPredicate entered, LocationPredicate exited, DistancePredicate distance) {
            super(ID);
            this.enteredPos = entered;
            this.exitedPos = exited;
            this.distance = distance;
        }

        public static Conditions distance(DistancePredicate distance) {
            return new Conditions(LocationPredicate.ANY, LocationPredicate.ANY, distance);
        }

        public boolean matches(ServerWorld world, Vec3d enteredPos, double exitedPosX, double exitedPosY, double exitedPosZ) {
            if (!this.enteredPos.test(world, enteredPos.x, enteredPos.y, enteredPos.z)) {
                return false;
            }
            if (!this.exitedPos.test(world, exitedPosX, exitedPosY, exitedPosZ)) {
                return false;
            }
            return this.distance.test(enteredPos.x, enteredPos.y, enteredPos.z, exitedPosX, exitedPosY, exitedPosZ);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("entered", this.enteredPos.toJson());
            jsonObject.add("exited", this.exitedPos.toJson());
            jsonObject.add("distance", this.distance.serialize());
            return jsonObject;
        }
    }
}

