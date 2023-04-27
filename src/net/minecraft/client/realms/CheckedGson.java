/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.realms;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CheckedGson {
    private final Gson GSON = new Gson();

    public String toJson(RealmsSerializable serializable) {
        return this.GSON.toJson((Object)serializable);
    }

    public String toJson(JsonElement json) {
        return this.GSON.toJson(json);
    }

    @Nullable
    public <T extends RealmsSerializable> T fromJson(String json, Class<T> type) {
        return (T)((RealmsSerializable)this.GSON.fromJson(json, type));
    }
}

