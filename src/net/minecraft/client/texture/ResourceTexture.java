/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import java.io.Closeable;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ResourceTexture
extends AbstractTexture {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final Identifier location;

    public ResourceTexture(Identifier location) {
        this.location = location;
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {
        try (TextureData textureData = this.loadTextureData(resourceManager);){
            boolean bl = false;
            boolean bl2 = false;
            textureData.checkException();
            TextureResourceMetadata textureResourceMetadata = textureData.getMetadata();
            if (textureResourceMetadata != null) {
                bl = textureResourceMetadata.shouldBlur();
                bl2 = textureResourceMetadata.shouldClamp();
            }
            this.bindTexture();
            TextureUtil.prepareImage(this.getGlId(), 0, textureData.getImage().getWidth(), textureData.getImage().getHeight());
            textureData.getImage().upload(0, 0, 0, 0, 0, textureData.getImage().getWidth(), textureData.getImage().getHeight(), bl, bl2, false);
        }
    }

    protected TextureData loadTextureData(ResourceManager resourceManager) {
        return TextureData.load(resourceManager, this.location);
    }

    @Environment(value=EnvType.CLIENT)
    public static class TextureData
    implements Closeable {
        private final TextureResourceMetadata metadata;
        private final NativeImage image;
        private final IOException exception;

        public TextureData(IOException exception) {
            this.exception = exception;
            this.metadata = null;
            this.image = null;
        }

        public TextureData(@Nullable TextureResourceMetadata metadata, NativeImage image) {
            this.exception = null;
            this.metadata = metadata;
            this.image = image;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public static TextureData load(ResourceManager resourceManager, Identifier identifier) {
            try (Resource resource = resourceManager.getResource(identifier);){
                NativeImage nativeImage = NativeImage.read(resource.getInputStream());
                TextureResourceMetadata textureResourceMetadata = null;
                try {
                    textureResourceMetadata = resource.getMetadata(TextureResourceMetadata.READER);
                }
                catch (RuntimeException runtimeException) {
                    LOGGER.warn("Failed reading metadata of: {}", (Object)identifier, (Object)runtimeException);
                }
                TextureData textureData = new TextureData(textureResourceMetadata, nativeImage);
                return textureData;
            }
            catch (IOException iOException) {
                return new TextureData(iOException);
            }
        }

        @Nullable
        public TextureResourceMetadata getMetadata() {
            return this.metadata;
        }

        public NativeImage getImage() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
            return this.image;
        }

        @Override
        public void close() {
            if (this.image != null) {
                this.image.close();
            }
        }

        public void checkException() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
        }
    }
}

