/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class MissingSprite
extends Sprite {
    private static final Identifier MISSINGNO = new Identifier("missingno");
    @Nullable
    private static NativeImageBackedTexture texture;
    private static final Lazy<NativeImage> IMAGE;

    private MissingSprite() {
        super(MISSINGNO, 16, 16);
        this.images = new NativeImage[]{IMAGE.get()};
    }

    public static MissingSprite getMissingSprite() {
        return new MissingSprite();
    }

    public static Identifier getMissingSpriteId() {
        return MISSINGNO;
    }

    @Override
    public void destroy() {
        for (int i = 1; i < this.images.length; ++i) {
            this.images[i].close();
        }
        this.images = new NativeImage[]{IMAGE.get()};
    }

    public static NativeImageBackedTexture getMissingSpriteTexture() {
        if (texture == null) {
            texture = new NativeImageBackedTexture(IMAGE.get());
            MinecraftClient.getInstance().getTextureManager().registerTexture(MISSINGNO, texture);
        }
        return texture;
    }

    static {
        IMAGE = new Lazy<NativeImage>(() -> {
            NativeImage nativeImage = new NativeImage(16, 16, false);
            int i = -16777216;
            int j = -524040;
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (k < 8 ^ l < 8) {
                        nativeImage.setPixelRgba(l, k, -524040);
                        continue;
                    }
                    nativeImage.setPixelRgba(l, k, -16777216);
                }
            }
            nativeImage.untrack();
            return nativeImage;
        });
    }
}

