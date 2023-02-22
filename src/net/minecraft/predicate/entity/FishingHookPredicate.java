/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.predicate.entity.TypeSpecificPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class FishingHookPredicate
implements TypeSpecificPredicate {
    public static final FishingHookPredicate ALL = new FishingHookPredicate(false);
    private static final String IN_OPEN_WATER = "in_open_water";
    private final boolean inOpenWater;

    private FishingHookPredicate(boolean inOpenWater) {
        this.inOpenWater = inOpenWater;
    }

    public static FishingHookPredicate of(boolean inOpenWater) {
        return new FishingHookPredicate(inOpenWater);
    }

    public static FishingHookPredicate fromJson(JsonObject json) {
        JsonElement jsonElement = json.get(IN_OPEN_WATER);
        if (jsonElement != null) {
            return new FishingHookPredicate(JsonHelper.asBoolean(jsonElement, IN_OPEN_WATER));
        }
        return ALL;
    }

    @Override
    public JsonObject typeSpecificToJson() {
        if (this == ALL) {
            return new JsonObject();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(IN_OPEN_WATER, (JsonElement)new JsonPrimitive(Boolean.valueOf(this.inOpenWater)));
        return jsonObject;
    }

    @Override
    public TypeSpecificPredicate.Deserializer getDeserializer() {
        return TypeSpecificPredicate.Deserializers.FISHING_HOOK;
    }

    @Override
    public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
        if (this == ALL) {
            return true;
        }
        if (!(entity instanceof FishingBobberEntity)) {
            return false;
        }
        FishingBobberEntity fishingBobberEntity = (FishingBobberEntity)entity;
        return this.inOpenWater == fishingBobberEntity.isInOpenWater();
    }
}

