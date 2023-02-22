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
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class ExplosionLargeParticle
extends SpriteBillboardParticle {
    private final SpriteProvider field_17815;

    private ExplosionLargeParticle(World world, double x, double y, double z, double d, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        float f;
        this.maxAge = 6 + this.random.nextInt(4);
        this.colorRed = f = this.random.nextFloat() * 0.6f + 0.4f;
        this.colorGreen = f;
        this.colorBlue = f;
        this.scale = 2.0f * (1.0f - (float)d * 0.5f);
        this.field_17815 = spriteProvider;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public int getColorMultiplier(float tint) {
        return 0xF000F0;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        this.setSpriteForAge(this.field_17815);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_17816;

        public Factory(SpriteProvider spriteProvider) {
            this.field_17816 = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
            return new ExplosionLargeParticle(world, d, e, f, g, this.field_17816);
        }
    }
}

