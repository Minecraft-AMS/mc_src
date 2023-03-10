/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureStitcher;
import net.minecraft.client.texture.TextureStitcherCannotFitException;
import net.minecraft.client.texture.TextureTickListener;
import net.minecraft.client.util.PngFile;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SpriteAtlasTexture
extends AbstractTexture
implements TextureTickListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Deprecated
    public static final Identifier BLOCK_ATLAS_TEXTURE = PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
    @Deprecated
    public static final Identifier PARTICLE_ATLAS_TEXTURE = new Identifier("textures/atlas/particles.png");
    private static final String PNG_EXTENSION = ".png";
    private final List<TextureTickListener> animatedSprites = Lists.newArrayList();
    private final Set<Identifier> spritesToLoad = Sets.newHashSet();
    private final Map<Identifier, Sprite> sprites = Maps.newHashMap();
    private final Identifier id;
    private final int maxTextureSize;

    public SpriteAtlasTexture(Identifier id) {
        this.id = id;
        this.maxTextureSize = RenderSystem.maxSupportedTextureSize();
    }

    @Override
    public void load(ResourceManager manager) {
    }

    public void upload(Data data) {
        this.spritesToLoad.clear();
        this.spritesToLoad.addAll(data.spriteIds);
        LOGGER.info("Created: {}x{}x{} {}-atlas", new Object[]{data.width, data.height, data.maxLevel, this.id});
        TextureUtil.prepareImage(this.getGlId(), data.maxLevel, data.width, data.height);
        this.clear();
        for (Sprite sprite : data.sprites) {
            this.sprites.put(sprite.getId(), sprite);
            try {
                sprite.upload();
            }
            catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Stitching texture atlas");
                CrashReportSection crashReportSection = crashReport.addElement("Texture being stitched together");
                crashReportSection.add("Atlas path", this.id);
                crashReportSection.add("Sprite", sprite);
                throw new CrashException(crashReport);
            }
            TextureTickListener textureTickListener = sprite.getAnimation();
            if (textureTickListener == null) continue;
            this.animatedSprites.add(textureTickListener);
        }
    }

    public Data stitch(ResourceManager resourceManager, Stream<Identifier> idStream, Profiler profiler, int mipmapLevel) {
        int l;
        profiler.push("preparing");
        Set<Identifier> set = idStream.peek(id -> {
            if (id == null) {
                throw new IllegalArgumentException("Location cannot be null!");
            }
        }).collect(Collectors.toSet());
        int i = this.maxTextureSize;
        TextureStitcher textureStitcher = new TextureStitcher(i, i, mipmapLevel);
        int j = Integer.MAX_VALUE;
        int k = 1 << mipmapLevel;
        profiler.swap("extracting_frames");
        for (Sprite.Info info : this.loadSprites(resourceManager, set)) {
            j = Math.min(j, Math.min(info.getWidth(), info.getHeight()));
            l = Math.min(Integer.lowestOneBit(info.getWidth()), Integer.lowestOneBit(info.getHeight()));
            if (l < k) {
                LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", new Object[]{info.getId(), info.getWidth(), info.getHeight(), MathHelper.floorLog2(k), MathHelper.floorLog2(l)});
                k = l;
            }
            textureStitcher.add(info);
        }
        int m = Math.min(j, k);
        int n = MathHelper.floorLog2(m);
        if (n < mipmapLevel) {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", new Object[]{this.id, mipmapLevel, n, m});
            l = n;
        } else {
            l = mipmapLevel;
        }
        profiler.swap("register");
        textureStitcher.add(MissingSprite.getMissingInfo());
        profiler.swap("stitching");
        try {
            textureStitcher.stitch();
        }
        catch (TextureStitcherCannotFitException textureStitcherCannotFitException) {
            CrashReport crashReport = CrashReport.create(textureStitcherCannotFitException, "Stitching");
            CrashReportSection crashReportSection = crashReport.addElement("Stitcher");
            crashReportSection.add("Sprites", textureStitcherCannotFitException.getSprites().stream().map(sprite -> String.format("%s[%dx%d]", sprite.getId(), sprite.getWidth(), sprite.getHeight())).collect(Collectors.joining(",")));
            crashReportSection.add("Max Texture Size", i);
            throw new CrashException(crashReport);
        }
        profiler.swap("loading");
        List<Sprite> list = this.loadSprites(resourceManager, textureStitcher, l);
        profiler.pop();
        return new Data(set, textureStitcher.getWidth(), textureStitcher.getHeight(), l, list);
    }

    private Collection<Sprite.Info> loadSprites(ResourceManager resourceManager, Set<Identifier> ids) {
        ArrayList list = Lists.newArrayList();
        ConcurrentLinkedQueue<Sprite.Info> queue = new ConcurrentLinkedQueue<Sprite.Info>();
        for (Identifier identifier : ids) {
            if (MissingSprite.getMissingSpriteId().equals(identifier)) continue;
            list.add(CompletableFuture.runAsync(() -> {
                Sprite.Info info;
                Identifier identifier2 = this.getTexturePath(identifier);
                try (Resource resource = resourceManager.getResource(identifier2);){
                    PngFile pngFile = new PngFile(resource.toString(), resource.getInputStream());
                    AnimationResourceMetadata animationResourceMetadata = resource.getMetadata(AnimationResourceMetadata.READER);
                    if (animationResourceMetadata == null) {
                        animationResourceMetadata = AnimationResourceMetadata.EMPTY;
                    }
                    Pair<Integer, Integer> pair = animationResourceMetadata.ensureImageSize(pngFile.width, pngFile.height);
                    info = new Sprite.Info(identifier, (Integer)pair.getFirst(), (Integer)pair.getSecond(), animationResourceMetadata);
                }
                catch (RuntimeException runtimeException) {
                    LOGGER.error("Unable to parse metadata from {} : {}", (Object)identifier2, (Object)runtimeException);
                    return;
                }
                catch (IOException iOException) {
                    LOGGER.error("Using missing texture, unable to load {} : {}", (Object)identifier2, (Object)iOException);
                    return;
                }
                queue.add(info);
            }, Util.getMainWorkerExecutor()));
        }
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        return queue;
    }

    private List<Sprite> loadSprites(ResourceManager resourceManager, TextureStitcher textureStitcher, int maxLevel) {
        ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
        ArrayList list = Lists.newArrayList();
        textureStitcher.getStitchedSprites((info, atlasWidth, atlasHeight, x, y) -> {
            if (info == MissingSprite.getMissingInfo()) {
                MissingSprite missingSprite = MissingSprite.getMissingSprite(this, maxLevel, atlasWidth, atlasHeight, x, y);
                queue.add(missingSprite);
            } else {
                list.add(CompletableFuture.runAsync(() -> {
                    Sprite sprite = this.loadSprite(resourceManager, info, atlasWidth, atlasHeight, maxLevel, x, y);
                    if (sprite != null) {
                        queue.add(sprite);
                    }
                }, Util.getMainWorkerExecutor()));
            }
        });
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        return Lists.newArrayList(queue);
    }

    @Nullable
    private Sprite loadSprite(ResourceManager container, Sprite.Info info, int atlasWidth, int atlasHeight, int maxLevel, int x, int y) {
        Sprite sprite;
        block9: {
            Identifier identifier = this.getTexturePath(info.getId());
            Resource resource = container.getResource(identifier);
            try {
                NativeImage nativeImage = NativeImage.read(resource.getInputStream());
                sprite = new Sprite(this, info, maxLevel, atlasWidth, atlasHeight, x, y, nativeImage);
                if (resource == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (resource != null) {
                        try {
                            resource.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (RuntimeException runtimeException) {
                    LOGGER.error("Unable to parse metadata from {}", (Object)identifier, (Object)runtimeException);
                    return null;
                }
                catch (IOException iOException) {
                    LOGGER.error("Using missing texture, unable to load {}", (Object)identifier, (Object)iOException);
                    return null;
                }
            }
            resource.close();
        }
        return sprite;
    }

    private Identifier getTexturePath(Identifier id) {
        return new Identifier(id.getNamespace(), String.format("textures/%s%s", id.getPath(), PNG_EXTENSION));
    }

    public void tickAnimatedSprites() {
        this.bindTexture();
        for (TextureTickListener textureTickListener : this.animatedSprites) {
            textureTickListener.tick();
        }
    }

    @Override
    public void tick() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::tickAnimatedSprites);
        } else {
            this.tickAnimatedSprites();
        }
    }

    public Sprite getSprite(Identifier id) {
        Sprite sprite = this.sprites.get(id);
        if (sprite == null) {
            return this.sprites.get(MissingSprite.getMissingSpriteId());
        }
        return sprite;
    }

    public void clear() {
        for (Sprite sprite : this.sprites.values()) {
            sprite.close();
        }
        this.sprites.clear();
        this.animatedSprites.clear();
    }

    public Identifier getId() {
        return this.id;
    }

    public void applyTextureFilter(Data data) {
        this.setFilter(false, data.maxLevel > 0);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Data {
        final Set<Identifier> spriteIds;
        final int width;
        final int height;
        final int maxLevel;
        final List<Sprite> sprites;

        public Data(Set<Identifier> spriteIds, int width, int height, int maxLevel, List<Sprite> sprites) {
            this.spriteIds = spriteIds;
            this.width = width;
            this.height = height;
            this.maxLevel = maxLevel;
            this.sprites = sprites;
        }
    }
}

