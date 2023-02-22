/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class NativeImageBackedTexture
extends AbstractTexture
implements AutoCloseable {
    private NativeImage image;

    public NativeImageBackedTexture(NativeImage nativeImage) {
        this.image = nativeImage;
        TextureUtil.prepareImage(this.getGlId(), this.image.getWidth(), this.image.getHeight());
        this.upload();
    }

    public NativeImageBackedTexture(int i, int j, boolean bl) {
        this.image = new NativeImage(i, j, bl);
        TextureUtil.prepareImage(this.getGlId(), this.image.getWidth(), this.image.getHeight());
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {
    }

    public void upload() {
        this.bindTexture();
        this.image.upload(0, 0, 0, false);
    }

    @Nullable
    public NativeImage getImage() {
        return this.image;
    }

    public void setImage(NativeImage image) throws Exception {
        this.image.close();
        this.image = image;
    }

    @Override
    public void close() {
        this.image.close();
        this.clearGlId();
        this.image = null;
    }
}

