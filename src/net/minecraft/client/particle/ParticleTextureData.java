/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Streams
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ParticleTextureData {
    @Nullable
    private final List<Identifier> textureList;

    private ParticleTextureData(@Nullable List<Identifier> textureList) {
        this.textureList = textureList;
    }

    @Nullable
    public List<Identifier> getTextureList() {
        return this.textureList;
    }

    public static ParticleTextureData load(JsonObject jsonObject) {
        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "textures", null);
        List list = jsonArray != null ? (List)Streams.stream((Iterable)jsonArray).map(jsonElement -> JsonHelper.asString(jsonElement, "texture")).map(Identifier::new).collect(ImmutableList.toImmutableList()) : null;
        return new ParticleTextureData(list);
    }
}

