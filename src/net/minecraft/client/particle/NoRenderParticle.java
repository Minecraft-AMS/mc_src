/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class NoRenderParticle
extends Particle {
    protected NoRenderParticle(World world, double d, double e, double f) {
        super(world, d, e, f);
    }

    protected NoRenderParticle(World world, double d, double e, double f, double g, double h, double i) {
        super(world, d, e, f, g, h, i);
    }

    @Override
    public final void buildGeometry(BufferBuilder bufferBuilder, Camera camera, float tickDelta, float f, float g, float h, float i, float j) {
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.NO_RENDER;
    }
}

