/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.predicate.entity;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class EntityEffectPredicate {
    public static final EntityEffectPredicate EMPTY = new EntityEffectPredicate(Collections.emptyMap());
    private final Map<StatusEffect, EffectData> effects;

    public EntityEffectPredicate(Map<StatusEffect, EffectData> map) {
        this.effects = map;
    }

    public static EntityEffectPredicate create() {
        return new EntityEffectPredicate(Maps.newHashMap());
    }

    public EntityEffectPredicate withEffect(StatusEffect statusEffect) {
        this.effects.put(statusEffect, new EffectData());
        return this;
    }

    public boolean test(Entity entity) {
        if (this == EMPTY) {
            return true;
        }
        if (entity instanceof LivingEntity) {
            return this.test(((LivingEntity)entity).getActiveStatusEffects());
        }
        return false;
    }

    public boolean test(LivingEntity livingEntity) {
        if (this == EMPTY) {
            return true;
        }
        return this.test(livingEntity.getActiveStatusEffects());
    }

    public boolean test(Map<StatusEffect, StatusEffectInstance> map) {
        if (this == EMPTY) {
            return true;
        }
        for (Map.Entry<StatusEffect, EffectData> entry : this.effects.entrySet()) {
            StatusEffectInstance statusEffectInstance = map.get(entry.getKey());
            if (entry.getValue().test(statusEffectInstance)) continue;
            return false;
        }
        return true;
    }

    public static EntityEffectPredicate deserialize(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return EMPTY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "effects");
        HashMap map = Maps.newHashMap();
        for (Map.Entry entry : jsonObject.entrySet()) {
            Identifier identifier = new Identifier((String)entry.getKey());
            StatusEffect statusEffect = Registry.STATUS_EFFECT.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown effect '" + identifier + "'"));
            EffectData effectData = EffectData.deserialize(JsonHelper.asObject((JsonElement)entry.getValue(), (String)entry.getKey()));
            map.put(statusEffect, effectData);
        }
        return new EntityEffectPredicate(map);
    }

    public JsonElement serialize() {
        if (this == EMPTY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<StatusEffect, EffectData> entry : this.effects.entrySet()) {
            jsonObject.add(Registry.STATUS_EFFECT.getId(entry.getKey()).toString(), entry.getValue().serialize());
        }
        return jsonObject;
    }

    public static class EffectData {
        private final NumberRange.IntRange amplifier;
        private final NumberRange.IntRange duration;
        @Nullable
        private final Boolean ambient;
        @Nullable
        private final Boolean visible;

        public EffectData(NumberRange.IntRange amplifier, NumberRange.IntRange duration, @Nullable Boolean ambient, @Nullable Boolean boolean_) {
            this.amplifier = amplifier;
            this.duration = duration;
            this.ambient = ambient;
            this.visible = boolean_;
        }

        public EffectData() {
            this(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, null, null);
        }

        public boolean test(@Nullable StatusEffectInstance statusEffectInstance) {
            if (statusEffectInstance == null) {
                return false;
            }
            if (!this.amplifier.test(statusEffectInstance.getAmplifier())) {
                return false;
            }
            if (!this.duration.test(statusEffectInstance.getDuration())) {
                return false;
            }
            if (this.ambient != null && this.ambient.booleanValue() != statusEffectInstance.isAmbient()) {
                return false;
            }
            return this.visible == null || this.visible.booleanValue() == statusEffectInstance.shouldShowParticles();
        }

        public JsonElement serialize() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("amplifier", this.amplifier.toJson());
            jsonObject.add("duration", this.duration.toJson());
            jsonObject.addProperty("ambient", this.ambient);
            jsonObject.addProperty("visible", this.visible);
            return jsonObject;
        }

        public static EffectData deserialize(JsonObject json) {
            NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(json.get("amplifier"));
            NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(json.get("duration"));
            Boolean boolean_ = json.has("ambient") ? Boolean.valueOf(JsonHelper.getBoolean(json, "ambient")) : null;
            Boolean boolean2 = json.has("visible") ? Boolean.valueOf(JsonHelper.getBoolean(json, "visible")) : null;
            return new EffectData(intRange, intRange2, boolean_, boolean2);
        }
    }
}

