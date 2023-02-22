/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.TrueTypeFontLoader;
import net.minecraft.client.font.UnicodeTextureFont;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public final class FontType
extends Enum<FontType> {
    public static final /* enum */ FontType BITMAP = new FontType("bitmap", BitmapFont.Loader::fromJson);
    public static final /* enum */ FontType TTF = new FontType("ttf", TrueTypeFontLoader::fromJson);
    public static final /* enum */ FontType LEGACY_UNICODE = new FontType("legacy_unicode", UnicodeTextureFont.Loader::fromJson);
    private static final Map<String, FontType> REGISTRY;
    private final String id;
    private final Function<JsonObject, FontLoader> loaderFactory;
    private static final /* synthetic */ FontType[] field_2316;

    public static FontType[] values() {
        return (FontType[])field_2316.clone();
    }

    public static FontType valueOf(String string) {
        return Enum.valueOf(FontType.class, string);
    }

    private FontType(String id, Function<JsonObject, FontLoader> factory) {
        this.id = id;
        this.loaderFactory = factory;
    }

    public static FontType byId(String id) {
        FontType fontType = REGISTRY.get(id);
        if (fontType == null) {
            throw new IllegalArgumentException("Invalid type: " + id);
        }
        return fontType;
    }

    public FontLoader createLoader(JsonObject json) {
        return this.loaderFactory.apply(json);
    }

    private static /* synthetic */ FontType[] method_36876() {
        return new FontType[]{BITMAP, TTF, LEGACY_UNICODE};
    }

    static {
        field_2316 = FontType.method_36876();
        REGISTRY = Util.make(Maps.newHashMap(), hashMap -> {
            for (FontType fontType : FontType.values()) {
                hashMap.put(fontType.id, fontType);
            }
        });
    }
}

