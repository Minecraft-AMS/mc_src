/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture.atlas;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.metadata.BlockEntry;

@Environment(value=EnvType.CLIENT)
public class FilterAtlasSource
implements AtlasSource {
    public static final Codec<FilterAtlasSource> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockEntry.CODEC.fieldOf("pattern").forGetter(filterAtlasSource -> filterAtlasSource.pattern)).apply((Applicative)instance, FilterAtlasSource::new));
    private final BlockEntry pattern;

    public FilterAtlasSource(BlockEntry pattern) {
        this.pattern = pattern;
    }

    @Override
    public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
        regions.method_47671(this.pattern.getIdentifierPredicate());
    }

    @Override
    public AtlasSourceType getType() {
        return AtlasSourceManager.FILTER;
    }
}

