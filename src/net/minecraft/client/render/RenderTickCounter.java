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

@Environment(value=EnvType.CLIENT)
public class RenderTickCounter {
    public int ticksThisFrame;
    public float tickDelta;
    public float lastFrameDuration;
    private long prevTimeMillis;
    private final float tickTime;

    public RenderTickCounter(float tps, long timeMillis) {
        this.tickTime = 1000.0f / tps;
        this.prevTimeMillis = timeMillis;
    }

    public void beginRenderTick(long timeMillis) {
        this.lastFrameDuration = (float)(timeMillis - this.prevTimeMillis) / this.tickTime;
        this.prevTimeMillis = timeMillis;
        this.tickDelta += this.lastFrameDuration;
        this.ticksThisFrame = (int)this.tickDelta;
        this.tickDelta -= (float)this.ticksThisFrame;
    }
}

