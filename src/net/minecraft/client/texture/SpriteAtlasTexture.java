/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.DynamicTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.client.texture.TextureTickListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SpriteAtlasTexture
extends AbstractTexture
implements DynamicTexture,
TextureTickListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Deprecated
    public static final Identifier BLOCK_ATLAS_TEXTURE = PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
    @Deprecated
    public static final Identifier PARTICLE_ATLAS_TEXTURE = new Identifier("textures/atlas/particles.png");
    private List<SpriteContents> spritesToLoad = List.of();
    private List<Sprite.TickableAnimation> animatedSprites = List.of();
    private Map<Identifier, Sprite> sprites = Map.of();
    private final Identifier id;
    private final int maxTextureSize;
    private int width;
    private int height;
    private int mipLevel;

    public SpriteAtlasTexture(Identifier id) {
        this.id = id;
        this.maxTextureSize = RenderSystem.maxSupportedTextureSize();
    }

    @Override
    public void load(ResourceManager manager) {
    }

    public void upload(SpriteLoader.StitchResult stitchResult) {
        LOGGER.info("Created: {}x{}x{} {}-atlas", new Object[]{stitchResult.width(), stitchResult.height(), stitchResult.mipLevel(), this.id});
        TextureUtil.prepareImage(this.getGlId(), stitchResult.mipLevel(), stitchResult.width(), stitchResult.height());
        this.width = stitchResult.width();
        this.height = stitchResult.height();
        this.mipLevel = stitchResult.mipLevel();
        this.clear();
        this.sprites = Map.copyOf(stitchResult.regions());
        ArrayList<SpriteContents> list = new ArrayList<SpriteContents>();
        ArrayList<Sprite.TickableAnimation> list2 = new ArrayList<Sprite.TickableAnimation>();
        for (Sprite sprite : stitchResult.regions().values()) {
            list.add(sprite.getContents());
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
            Sprite.TickableAnimation tickableAnimation = sprite.createAnimation();
            if (tickableAnimation == null) continue;
            list2.add(tickableAnimation);
        }
        this.spritesToLoad = List.copyOf(list);
        this.animatedSprites = List.copyOf(list2);
    }

    @Override
    public void save(Identifier id, Path path) throws IOException {
        String string = id.toUnderscoreSeparatedString();
        TextureUtil.writeAsPNG(path, string, this.getGlId(), this.mipLevel, this.width, this.height);
        SpriteAtlasTexture.dumpAtlasInfos(path, string, this.sprites);
    }

    private static void dumpAtlasInfos(Path path, String id, Map<Identifier, Sprite> sprites) {
        Path path2 = path.resolve(id + ".txt");
        try (BufferedWriter writer = Files.newBufferedWriter(path2, new OpenOption[0]);){
            for (Map.Entry entry : sprites.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
                Sprite sprite = (Sprite)entry.getValue();
                writer.write(String.format(Locale.ROOT, "%s\tx=%d\ty=%d\tw=%d\th=%d%n", entry.getKey(), sprite.getX(), sprite.getY(), sprite.getContents().getWidth(), sprite.getContents().getHeight()));
            }
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to write file {}", (Object)path2, (Object)iOException);
        }
    }

    public void tickAnimatedSprites() {
        this.bindTexture();
        for (Sprite.TickableAnimation tickableAnimation : this.animatedSprites) {
            tickableAnimation.tick();
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
        this.spritesToLoad.forEach(SpriteContents::close);
        this.animatedSprites.forEach(Sprite.TickableAnimation::close);
        this.spritesToLoad = List.of();
        this.animatedSprites = List.of();
        this.sprites = Map.of();
    }

    public Identifier getId() {
        return this.id;
    }

    public int getMaxTextureSize() {
        return this.maxTextureSize;
    }

    int getWidth() {
        return this.width;
    }

    int getHeight() {
        return this.height;
    }

    public void applyTextureFilter(SpriteLoader.StitchResult data) {
        this.setFilter(false, data.mipLevel() > 0);
    }
}

