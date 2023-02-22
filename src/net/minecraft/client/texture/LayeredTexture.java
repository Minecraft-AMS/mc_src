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
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
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

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void load(ResourceManager resourceManager) throws IOException {
        Iterator<String> iterator = this.locations.iterator();
        String string = iterator.next();
        try (Resource resource = resourceManager.getResource(new Identifier(string));
             NativeImage nativeImage = NativeImage.read(resource.getInputStream());){
            while (iterator.hasNext()) {
                Resource resource2;
                block51: {
                    String string2 = iterator.next();
                    if (string2 == null) continue;
                    resource2 = resourceManager.getResource(new Identifier(string2));
                    Throwable throwable = null;
                    try {
                        try (NativeImage nativeImage2 = NativeImage.read(resource2.getInputStream());){
                            for (int i = 0; i < nativeImage2.getHeight(); ++i) {
                                for (int j = 0; j < nativeImage2.getWidth(); ++j) {
                                    nativeImage.blendPixel(j, i, nativeImage2.getPixelRgba(j, i));
                                }
                            }
                        }
                        if (resource2 == null) continue;
                        if (throwable == null) break block51;
                    }
                    catch (Throwable throwable2) {
                        try {
                            throwable = throwable2;
                            throw throwable2;
                        }
                        catch (Throwable throwable3) {
                            if (resource2 == null) throw throwable3;
                            if (throwable == null) {
                                resource2.close();
                                throw throwable3;
                            }
                            try {
                                resource2.close();
                                throw throwable3;
                            }
                            catch (Throwable throwable4) {
                                throwable.addSuppressed(throwable4);
                                throw throwable3;
                            }
                        }
                    }
                    try {
                        resource2.close();
                        continue;
                    }
                    catch (Throwable throwable5) {
                        throwable.addSuppressed(throwable5);
                        continue;
                    }
                }
                resource2.close();
            }
            TextureUtil.prepareImage(this.getGlId(), nativeImage.getWidth(), nativeImage.getHeight());
            nativeImage.upload(0, 0, 0, false);
            return;
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't load layered image", (Throwable)iOException);
        }
    }
}

