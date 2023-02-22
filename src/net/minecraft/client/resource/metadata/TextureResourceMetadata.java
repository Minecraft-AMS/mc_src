/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.metadata;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.TextureResourceMetadataReader;

@Environment(value=EnvType.CLIENT)
public class TextureResourceMetadata {
    public static final TextureResourceMetadataReader READER = new TextureResourceMetadataReader();
    private final boolean blur;
    private final boolean clamp;

    public TextureResourceMetadata(boolean blur, boolean bl) {
        this.blur = blur;
        this.clamp = bl;
    }

    public boolean shouldBlur() {
        return this.blur;
    }

    public boolean shouldClamp() {
        return this.clamp;
    }
}

