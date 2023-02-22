/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;

@Environment(value=EnvType.CLIENT)
public class Tessellator {
    private static final int field_32051 = 0x800000;
    private static final int DEFAULT_BUFFER_CAPACITY = 0x200000;
    private final BufferBuilder buffer;
    private static final Tessellator INSTANCE = new Tessellator();

    public static Tessellator getInstance() {
        RenderSystem.assertOnGameThreadOrInit();
        return INSTANCE;
    }

    public Tessellator(int bufferCapacity) {
        this.buffer = new BufferBuilder(bufferCapacity);
    }

    public Tessellator() {
        this(0x200000);
    }

    public void draw() {
        BufferRenderer.drawWithGlobalProgram(this.buffer.end());
    }

    public BufferBuilder getBuffer() {
        return this.buffer;
    }
}

