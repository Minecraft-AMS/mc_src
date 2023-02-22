/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public interface Texture {
    public void pushFilter(boolean var1, boolean var2);

    public void popFilter();

    public void load(ResourceManager var1) throws IOException;

    public int getGlId();

    default public void bindTexture() {
        GlStateManager.bindTexture(this.getGlId());
    }

    default public void registerTexture(TextureManager textureManager, ResourceManager resourceManager, Identifier identifier, Executor executor) {
        textureManager.registerTexture(identifier, this);
    }
}

