/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;

@Environment(value=EnvType.CLIENT)
public class Tessellator {
    private final BufferBuilder buffer;
    private final BufferRenderer renderer = new BufferRenderer();
    private static final Tessellator INSTANCE = new Tessellator(0x200000);

    public static Tessellator getInstance() {
        return INSTANCE;
    }

    public Tessellator(int bufferCapacity) {
        this.buffer = new BufferBuilder(bufferCapacity);
    }

    public void draw() {
        this.buffer.end();
        this.renderer.draw(this.buffer);
    }

    public BufferBuilder getBuffer() {
        return this.buffer;
    }
}

