/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture.atlas;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.client.texture.atlas.DirectoryAtlasSource;
import net.minecraft.client.texture.atlas.FilterAtlasSource;
import net.minecraft.client.texture.atlas.SingleAtlasSource;
import net.minecraft.client.texture.atlas.UnstitchAtlasSource;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class AtlasSourceManager {
    private static final BiMap<Identifier, AtlasSourceType> SOURCE_TYPE_BY_ID = HashBiMap.create();
    public static final AtlasSourceType SINGLE = AtlasSourceManager.register("single", SingleAtlasSource.CODEC);
    public static final AtlasSourceType DIRECTORY = AtlasSourceManager.register("directory", DirectoryAtlasSource.CODEC);
    public static final AtlasSourceType FILTER = AtlasSourceManager.register("filter", FilterAtlasSource.CODEC);
    public static final AtlasSourceType UNSTITCH = AtlasSourceManager.register("unstitch", UnstitchAtlasSource.CODEC);
    public static Codec<AtlasSourceType> CODEC = Identifier.CODEC.flatXmap(id -> {
        AtlasSourceType atlasSourceType = (AtlasSourceType)SOURCE_TYPE_BY_ID.get(id);
        return atlasSourceType != null ? DataResult.success((Object)atlasSourceType) : DataResult.error((String)("Unknown type " + id));
    }, type -> {
        Identifier identifier = (Identifier)SOURCE_TYPE_BY_ID.inverse().get(type);
        return type != null ? DataResult.success((Object)identifier) : DataResult.error((String)("Unknown type " + identifier));
    });
    public static Codec<AtlasSource> TYPE_CODEC = CODEC.dispatch(AtlasSource::getType, AtlasSourceType::codec);
    public static Codec<List<AtlasSource>> LIST_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)TYPE_CODEC.listOf().fieldOf("sources").forGetter(sources -> sources)).apply((Applicative)instance, list -> list));

    private static AtlasSourceType register(String id, Codec<? extends AtlasSource> codec) {
        Identifier identifier = new Identifier(id);
        AtlasSourceType atlasSourceType = new AtlasSourceType(codec);
        AtlasSourceType atlasSourceType2 = (AtlasSourceType)SOURCE_TYPE_BY_ID.putIfAbsent((Object)identifier, (Object)atlasSourceType);
        if (atlasSourceType2 != null) {
            throw new IllegalStateException("Duplicate registration " + identifier);
        }
        return atlasSourceType;
    }
}

