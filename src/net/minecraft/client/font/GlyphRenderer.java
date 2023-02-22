/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GlyphRenderer {
    private final Identifier id;
    private final float uMin;
    private final float uMax;
    private final float vMin;
    private final float vMax;
    private final float xMin;
    private final float xMax;
    private final float yMin;
    private final float yMax;

    public GlyphRenderer(Identifier identifier, float uMin, float uMax, float vMin, float vMax, float xMin, float xMax, float yMin, float yMax) {
        this.id = identifier;
        this.uMin = uMin;
        this.uMax = uMax;
        this.vMin = vMin;
        this.vMax = vMax;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    public void draw(TextureManager textureManager, boolean italic, float x, float y, BufferBuilder buffer, float red, float green, float blue, float alpha) {
        int i = 3;
        float f = x + this.xMin;
        float g = x + this.xMax;
        float h = this.yMin - 3.0f;
        float j = this.yMax - 3.0f;
        float k = y + h;
        float l = y + j;
        float m = italic ? 1.0f - 0.25f * h : 0.0f;
        float n = italic ? 1.0f - 0.25f * j : 0.0f;
        buffer.vertex(f + m, k, 0.0).texture(this.uMin, this.vMin).color(red, green, blue, alpha).next();
        buffer.vertex(f + n, l, 0.0).texture(this.uMin, this.vMax).color(red, green, blue, alpha).next();
        buffer.vertex(g + n, l, 0.0).texture(this.uMax, this.vMax).color(red, green, blue, alpha).next();
        buffer.vertex(g + m, k, 0.0).texture(this.uMax, this.vMin).color(red, green, blue, alpha).next();
    }

    @Nullable
    public Identifier getId() {
        return this.id;
    }
}

