/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SculkChargeParticleEffect;

@Environment(value=EnvType.CLIENT)
public class SculkChargeParticle
extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    SculkChargeParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.velocityMultiplier = 0.96f;
        this.spriteProvider = spriteProvider;
        this.scale(1.5f);
        this.collidesWithWorld = false;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public int getBrightness(float tint) {
        return 240;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
    }

    @Environment(value=EnvType.CLIENT)
    public record Factory(SpriteProvider spriteProvider) implements ParticleFactory<SculkChargeParticleEffect>
    {
        @Override
        public Particle createParticle(SculkChargeParticleEffect sculkChargeParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            SculkChargeParticle sculkChargeParticle = new SculkChargeParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            sculkChargeParticle.setAlpha(1.0f);
            sculkChargeParticle.setVelocity(g, h, i);
            sculkChargeParticle.prevAngle = sculkChargeParticleEffect.roll();
            sculkChargeParticle.angle = sculkChargeParticleEffect.roll();
            sculkChargeParticle.setMaxAge(clientWorld.random.nextInt(12) + 8);
            return sculkChargeParticle;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Factory.class, "sprite", "spriteProvider"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Factory.class, "sprite", "spriteProvider"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Factory.class, "sprite", "spriteProvider"}, this, object);
        }
    }
}

