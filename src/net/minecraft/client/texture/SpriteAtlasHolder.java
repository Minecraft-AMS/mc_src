/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(value=EnvType.CLIENT)
public abstract class SpriteAtlasHolder
extends SinglePreparationResourceReloadListener<SpriteAtlasTexture.Data>
implements AutoCloseable {
    private final SpriteAtlasTexture atlas;

    public SpriteAtlasHolder(TextureManager textureManager, Identifier identifier, String string) {
        this.atlas = new SpriteAtlasTexture(string);
        textureManager.registerTextureUpdateable(identifier, this.atlas);
    }

    protected abstract Iterable<Identifier> getSprites();

    protected Sprite getSprite(Identifier objectId) {
        return this.atlas.getSprite(objectId);
    }

    @Override
    protected SpriteAtlasTexture.Data prepare(ResourceManager resourceManager, Profiler profiler) {
        profiler.startTick();
        profiler.push("stitching");
        SpriteAtlasTexture.Data data = this.atlas.stitch(resourceManager, this.getSprites(), profiler);
        profiler.pop();
        profiler.endTick();
        return data;
    }

    @Override
    protected void apply(SpriteAtlasTexture.Data data, ResourceManager resourceManager, Profiler profiler) {
        profiler.startTick();
        profiler.push("upload");
        this.atlas.upload(data);
        profiler.pop();
        profiler.endTick();
    }

    @Override
    public void close() {
        this.atlas.clear();
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }
}

