/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.util.Either
 *  it.unimi.dsi.fastutil.ints.Int2FloatMap
 *  it.unimi.dsi.fastutil.ints.Int2FloatMaps
 *  it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.ints.IntSets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.font;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatMaps;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.Glyph;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SpaceFont
implements Font {
    private final Int2ObjectMap<Glyph.EmptyGlyph> codePointsToGlyphs;

    public SpaceFont(Int2FloatMap codePointsToAdvances) {
        this.codePointsToGlyphs = new Int2ObjectOpenHashMap(codePointsToAdvances.size());
        Int2FloatMaps.fastForEach((Int2FloatMap)codePointsToAdvances, entry -> {
            float f = entry.getFloatValue();
            this.codePointsToGlyphs.put(entry.getIntKey(), () -> f);
        });
    }

    @Override
    @Nullable
    public Glyph getGlyph(int codePoint) {
        return (Glyph)this.codePointsToGlyphs.get(codePoint);
    }

    @Override
    public IntSet getProvidedGlyphs() {
        return IntSets.unmodifiable((IntSet)this.codePointsToGlyphs.keySet());
    }

    public static FontLoader fromJson(JsonObject json) {
        Int2FloatOpenHashMap int2FloatMap = new Int2FloatOpenHashMap();
        JsonObject jsonObject = JsonHelper.getObject(json, "advances");
        for (Map.Entry entry : jsonObject.entrySet()) {
            int[] is = ((String)entry.getKey()).codePoints().toArray();
            if (is.length != 1) {
                throw new JsonParseException("Expected single codepoint, got " + Arrays.toString(is));
            }
            float f = JsonHelper.asFloat((JsonElement)entry.getValue(), "advance");
            int2FloatMap.put(is[0], f);
        }
        FontLoader.Loadable loadable = arg_0 -> SpaceFont.method_41717((Int2FloatMap)int2FloatMap, arg_0);
        return () -> Either.left((Object)loadable);
    }

    private static /* synthetic */ Font method_41717(Int2FloatMap manager, ResourceManager resourceManager) throws IOException {
        return new SpaceFont(manager);
    }
}

