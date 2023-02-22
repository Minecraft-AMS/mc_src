/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class AsyncTexture
extends ResourceTexture {
    private CompletableFuture<ResourceTexture.TextureData> future;

    public AsyncTexture(ResourceManager resourceManager, Identifier identifier, Executor executor) {
        super(identifier);
        this.future = CompletableFuture.supplyAsync(() -> ResourceTexture.TextureData.load(resourceManager, identifier), executor);
    }

    @Override
    protected ResourceTexture.TextureData loadTextureData(ResourceManager resourceManager) {
        if (this.future != null) {
            ResourceTexture.TextureData textureData = this.future.join();
            this.future = null;
            return textureData;
        }
        return ResourceTexture.TextureData.load(resourceManager, this.location);
    }

    public CompletableFuture<Void> getLoadCompleteFuture() {
        return this.future == null ? CompletableFuture.completedFuture(null) : this.future.thenApply(textureData -> null);
    }

    @Override
    public void registerTexture(TextureManager textureManager, ResourceManager resourceManager, Identifier identifier, Executor executor) {
        this.future = CompletableFuture.supplyAsync(() -> ResourceTexture.TextureData.load(resourceManager, this.location), Util.getServerWorkerExecutor());
        this.future.thenRunAsync(() -> textureManager.registerTexture(this.location, this), executor);
    }
}
