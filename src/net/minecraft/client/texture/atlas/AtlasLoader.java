/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.gson.JsonParser
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture.atlas;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class AtlasLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceFinder FINDER = new ResourceFinder("atlases", ".json");
    private final List<AtlasSource> sources;

    private AtlasLoader(List<AtlasSource> sources) {
        this.sources = sources;
    }

    public List<Supplier<SpriteContents>> loadSources(ResourceManager resourceManager) {
        final HashMap map = new HashMap();
        AtlasSource.SpriteRegions spriteRegions = new AtlasSource.SpriteRegions(){

            @Override
            public void add(Identifier arg, AtlasSource.SpriteRegion region) {
                AtlasSource.SpriteRegion spriteRegion = map.put(arg, region);
                if (spriteRegion != null) {
                    spriteRegion.close();
                }
            }

            @Override
            public void removeIf(Predicate<Identifier> predicate) {
                Iterator iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = iterator.next();
                    if (!predicate.test((Identifier)entry.getKey())) continue;
                    ((AtlasSource.SpriteRegion)entry.getValue()).close();
                    iterator.remove();
                }
            }
        };
        this.sources.forEach(source -> source.load(resourceManager, spriteRegions));
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add(MissingSprite::createSpriteContents);
        builder.addAll(map.values());
        return builder.build();
    }

    public static AtlasLoader of(ResourceManager resourceManager, Identifier id) {
        Identifier identifier = FINDER.toResourcePath(id);
        ArrayList<AtlasSource> list = new ArrayList<AtlasSource>();
        for (Resource resource : resourceManager.getAllResources(identifier)) {
            try {
                BufferedReader bufferedReader = resource.getReader();
                try {
                    Dynamic dynamic = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)JsonParser.parseReader((Reader)bufferedReader));
                    list.addAll((Collection)AtlasSourceManager.LIST_CODEC.parse(dynamic).getOrThrow(false, arg_0 -> ((Logger)LOGGER).error(arg_0)));
                }
                finally {
                    if (bufferedReader == null) continue;
                    bufferedReader.close();
                }
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to parse atlas definition {} in pack {}", new Object[]{identifier, resource.getResourcePackName(), exception});
            }
        }
        return new AtlasLoader(list);
    }
}

