/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.font;

import java.io.Closeable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.RenderableGlyph;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface Font
extends Closeable {
    @Override
    default public void close() {
    }

    @Nullable
    default public RenderableGlyph getGlyph(char character) {
        return null;
    }
}

