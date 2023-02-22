/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.Validate
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.resource.metadata;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class AnimationResourceMetadataReader
implements ResourceMetadataReader<AnimationResourceMetadata> {
    @Override
    public AnimationResourceMetadata fromJson(JsonObject jsonObject) {
        int j;
        ImmutableList.Builder builder = ImmutableList.builder();
        int i = JsonHelper.getInt(jsonObject, "frametime", 1);
        if (i != 1) {
            Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)i, (String)"Invalid default frame time");
        }
        if (jsonObject.has("frames")) {
            try {
                JsonArray jsonArray = JsonHelper.getArray(jsonObject, "frames");
                for (j = 0; j < jsonArray.size(); ++j) {
                    JsonElement jsonElement = jsonArray.get(j);
                    AnimationFrameResourceMetadata animationFrameResourceMetadata = this.readFrameMetadata(j, jsonElement);
                    if (animationFrameResourceMetadata == null) continue;
                    builder.add((Object)animationFrameResourceMetadata);
                }
            }
            catch (ClassCastException classCastException) {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + jsonObject.get("frames"), (Throwable)classCastException);
            }
        }
        int k = JsonHelper.getInt(jsonObject, "width", -1);
        j = JsonHelper.getInt(jsonObject, "height", -1);
        if (k != -1) {
            Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)k, (String)"Invalid width");
        }
        if (j != -1) {
            Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)j, (String)"Invalid height");
        }
        boolean bl = JsonHelper.getBoolean(jsonObject, "interpolate", false);
        return new AnimationResourceMetadata((List<AnimationFrameResourceMetadata>)builder.build(), k, j, i, bl);
    }

    @Nullable
    private AnimationFrameResourceMetadata readFrameMetadata(int frame, JsonElement json) {
        if (json.isJsonPrimitive()) {
            return new AnimationFrameResourceMetadata(JsonHelper.asInt(json, "frames[" + frame + "]"));
        }
        if (json.isJsonObject()) {
            JsonObject jsonObject = JsonHelper.asObject(json, "frames[" + frame + "]");
            int i = JsonHelper.getInt(jsonObject, "time", -1);
            if (jsonObject.has("time")) {
                Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)i, (String)"Invalid frame time");
            }
            int j = JsonHelper.getInt(jsonObject, "index");
            Validate.inclusiveBetween((long)0L, (long)Integer.MAX_VALUE, (long)j, (String)"Invalid frame index");
            return new AnimationFrameResourceMetadata(j, i);
        }
        return null;
    }

    @Override
    public String getKey() {
        return "animation";
    }

    @Override
    public /* synthetic */ Object fromJson(JsonObject json) {
        return this.fromJson(json);
    }
}

