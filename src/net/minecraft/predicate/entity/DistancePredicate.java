/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class DistancePredicate {
    public static final DistancePredicate ANY = new DistancePredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY);
    private final NumberRange.FloatRange x;
    private final NumberRange.FloatRange y;
    private final NumberRange.FloatRange z;
    private final NumberRange.FloatRange horizontal;
    private final NumberRange.FloatRange absolute;

    public DistancePredicate(NumberRange.FloatRange x, NumberRange.FloatRange y, NumberRange.FloatRange z, NumberRange.FloatRange horizontal, NumberRange.FloatRange floatRange) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.horizontal = horizontal;
        this.absolute = floatRange;
    }

    public static DistancePredicate horizontal(NumberRange.FloatRange horizontal) {
        return new DistancePredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, horizontal, NumberRange.FloatRange.ANY);
    }

    public static DistancePredicate y(NumberRange.FloatRange y) {
        return new DistancePredicate(NumberRange.FloatRange.ANY, y, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY);
    }

    public boolean test(double d, double e, double f, double g, double h, double i) {
        float j = (float)(d - g);
        float k = (float)(e - h);
        float l = (float)(f - i);
        if (!(this.x.test(MathHelper.abs(j)) && this.y.test(MathHelper.abs(k)) && this.z.test(MathHelper.abs(l)))) {
            return false;
        }
        if (!this.horizontal.testSqrt(j * j + l * l)) {
            return false;
        }
        return this.absolute.testSqrt(j * j + k * k + l * l);
    }

    public static DistancePredicate deserialize(@Nullable JsonElement el) {
        if (el == null || el.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(el, "distance");
        NumberRange.FloatRange floatRange = NumberRange.FloatRange.fromJson(jsonObject.get("x"));
        NumberRange.FloatRange floatRange2 = NumberRange.FloatRange.fromJson(jsonObject.get("y"));
        NumberRange.FloatRange floatRange3 = NumberRange.FloatRange.fromJson(jsonObject.get("z"));
        NumberRange.FloatRange floatRange4 = NumberRange.FloatRange.fromJson(jsonObject.get("horizontal"));
        NumberRange.FloatRange floatRange5 = NumberRange.FloatRange.fromJson(jsonObject.get("absolute"));
        return new DistancePredicate(floatRange, floatRange2, floatRange3, floatRange4, floatRange5);
    }

    public JsonElement serialize() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("x", this.x.toJson());
        jsonObject.add("y", this.y.toJson());
        jsonObject.add("z", this.z.toJson());
        jsonObject.add("horizontal", this.horizontal.toJson());
        jsonObject.add("absolute", this.absolute.toJson());
        return jsonObject;
    }
}

