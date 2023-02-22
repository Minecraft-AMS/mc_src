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
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class BubbleColumnUpParticle
extends SpriteBillboardParticle {
    private BubbleColumnUpParticle(World world, double x, double y, double z, double d, double e, double f) {
        super(world, x, y, z);
        this.setBoundingBoxSpacing(0.02f, 0.02f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.2f;
        this.velocityX = d * (double)0.2f + (Math.random() * 2.0 - 1.0) * (double)0.02f;
        this.velocityY = e * (double)0.2f + (Math.random() * 2.0 - 1.0) * (double)0.02f;
        this.velocityZ = f * (double)0.2f + (Math.random() * 2.0 - 1.0) * (double)0.02f;
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.maxAge-- <= 0) {
            this.markDead();
            return;
        }
        this.velocityY += 0.002;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= (double)0.85f;
        this.velocityY *= (double)0.85f;
        this.velocityZ *= (double)0.85f;
        if (!this.world.getFluidState(new BlockPos(this.x, this.y, this.z)).matches(FluidTags.WATER)) {
            this.markDead();
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17785;

        public Factory(SpriteProvider spriteProvider) {
            this.field_17785 = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            BubbleColumnUpParticle bubbleColumnUpParticle = new BubbleColumnUpParticle(world, d, e, f, g, h, i);
            bubbleColumnUpParticle.setSprite(this.field_17785);
            return bubbleColumnUpParticle;
        }
    }
}

