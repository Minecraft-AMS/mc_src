/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.util.Identifier;

public interface CriterionConditions {
    public Identifier getId();

    default public JsonElement toJson() {
        return JsonNull.INSTANCE;
    }
}

