/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class LayeredTexture
extends AbstractTexture {
    private static final Logger LOGGER = LogManager.getLogger();
    public final List<String> locations;

    public LayeredTexture(String ... locations) {
        this.locations = Lists.newArrayList((Object[])locations);
        if (this.locations.isEmpty()) {
            throw new IllegalStateException("Layered texture with no layers.");
        }
    }

    @Override
    public void load(ResourceManager manager) throws IOException {
        Iterator<String> iterator = this.locations.iterator();
        String string = iterator.next();
        try (Resource resource = manager.getResource(new Identifier(string));){
            NativeImage nativeImage = NativeImage.read(resource.getInputStream());
            while (iterator.hasNext()) {
                String string2 = iterator.next();
                if (string2 == null) continue;
                Resource resource2 = manager.getResource(new Identifier(string2));
                Throwable throwable = null;
                try {
                    NativeImage nativeImage2 = NativeImage.read(resource2.getInputStream());
                    Throwable throwable2 = null;
                    try {
                        for (int i = 0; i < nativeImage2.getHeight(); ++i) {
                            for (int j = 0; j < nativeImage2.getWidth(); ++j) {
                                nativeImage.blendPixel(j, i, nativeImage2.getPixelRgba(j, i));
                            }
                        }
                    }
                    catch (Throwable throwable3) {
                        throwable2 = throwable3;
                        throw throwable3;
                    }
                    finally {
                        if (nativeImage2 == null) continue;
                        if (throwable2 != null) {
                            try {
                                nativeImage2.close();
                            }
                            catch (Throwable throwable4) {
                                throwable2.addSuppressed(throwable4);
                            }
                            continue;
                        }
                        nativeImage2.close();
                    }
                }
                catch (Throwable throwable5) {
                    throwable = throwable5;
                    throw throwable5;
                }
                finally {
                    if (resource2 == null) continue;
                    if (throwable != null) {
                        try {
                            resource2.close();
                        }
                        catch (Throwable throwable6) {
                            throwable.addSuppressed(throwable6);
                        }
                        continue;
                    }
                    resource2.close();
                }
            }
            if (!RenderSystem.isOnRenderThreadOrInit()) {
                RenderSystem.recordRenderCall(() -> this.method_22805(nativeImage));
            } else {
                this.method_22805(nativeImage);
            }
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't load layered image", (Throwable)iOException);
        }
    }

    private void method_22805(NativeImage nativeImage) {
        TextureUtil.prepareImage(this.getGlId(), nativeImage.getWidth(), nativeImage.getHeight());
        nativeImage.upload(0, 0, 0, true);
    }
}

