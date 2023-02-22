/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.loot.operator;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class BoundedIntUnaryOperator {
    @Nullable
    final LootNumberProvider min;
    @Nullable
    final LootNumberProvider max;
    private final Applier applier;
    private final Tester tester;

    public Set<LootContextParameter<?>> getRequiredParameters() {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        if (this.min != null) {
            builder.addAll(this.min.getRequiredParameters());
        }
        if (this.max != null) {
            builder.addAll(this.max.getRequiredParameters());
        }
        return builder.build();
    }

    BoundedIntUnaryOperator(@Nullable LootNumberProvider min, @Nullable LootNumberProvider max) {
        this.min = min;
        this.max = max;
        if (min == null) {
            if (max == null) {
                this.applier = (context, value) -> value;
                this.tester = (context, value) -> true;
            } else {
                this.applier = (context, value) -> Math.min(max.nextInt(context), value);
                this.tester = (context, value) -> value <= max.nextInt(context);
            }
        } else if (max == null) {
            this.applier = (context, value) -> Math.max(min.nextInt(context), value);
            this.tester = (context, value) -> value >= min.nextInt(context);
        } else {
            this.applier = (context, value) -> MathHelper.clamp(value, min.nextInt(context), max.nextInt(context));
            this.tester = (context, value) -> value >= min.nextInt(context) && value <= max.nextInt(context);
        }
    }

    public static BoundedIntUnaryOperator create(int value) {
        ConstantLootNumberProvider constantLootNumberProvider = ConstantLootNumberProvider.create(value);
        return new BoundedIntUnaryOperator(constantLootNumberProvider, constantLootNumberProvider);
    }

    public static BoundedIntUnaryOperator create(int min, int max) {
        return new BoundedIntUnaryOperator(ConstantLootNumberProvider.create(min), ConstantLootNumberProvider.create(max));
    }

    public static BoundedIntUnaryOperator createMin(int min) {
        return new BoundedIntUnaryOperator(ConstantLootNumberProvider.create(min), null);
    }

    public static BoundedIntUnaryOperator createMax(int max) {
        return new BoundedIntUnaryOperator(null, ConstantLootNumberProvider.create(max));
    }

    public int apply(LootContext context, int value) {
        return this.applier.apply(context, value);
    }

    public boolean test(LootContext context, int value) {
        return this.tester.test(context, value);
    }

    @FunctionalInterface
    static interface Applier {
        public int apply(LootContext var1, int var2);
    }

    @FunctionalInterface
    static interface Tester {
        public boolean test(LootContext var1, int var2);
    }

    public static class Serializer
    implements JsonDeserializer<BoundedIntUnaryOperator>,
    JsonSerializer<BoundedIntUnaryOperator> {
        public BoundedIntUnaryOperator deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            if (jsonElement.isJsonPrimitive()) {
                return BoundedIntUnaryOperator.create(jsonElement.getAsInt());
            }
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
            LootNumberProvider lootNumberProvider = jsonObject.has("min") ? JsonHelper.deserialize(jsonObject, "min", jsonDeserializationContext, LootNumberProvider.class) : null;
            LootNumberProvider lootNumberProvider2 = jsonObject.has("max") ? JsonHelper.deserialize(jsonObject, "max", jsonDeserializationContext, LootNumberProvider.class) : null;
            return new BoundedIntUnaryOperator(lootNumberProvider, lootNumberProvider2);
        }

        public JsonElement serialize(BoundedIntUnaryOperator boundedIntUnaryOperator, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            if (Objects.equals(boundedIntUnaryOperator.max, boundedIntUnaryOperator.min)) {
                return jsonSerializationContext.serialize((Object)boundedIntUnaryOperator.min);
            }
            if (boundedIntUnaryOperator.max != null) {
                jsonObject.add("max", jsonSerializationContext.serialize((Object)boundedIntUnaryOperator.max));
            }
            if (boundedIntUnaryOperator.min != null) {
                jsonObject.add("min", jsonSerializationContext.serialize((Object)boundedIntUnaryOperator.min));
            }
            return jsonObject;
        }

        public /* synthetic */ JsonElement serialize(Object entry, Type unused, JsonSerializationContext context) {
            return this.serialize((BoundedIntUnaryOperator)entry, unused, context);
        }

        public /* synthetic */ Object deserialize(JsonElement json, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(json, unused, context);
        }
    }
}

