/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockFallingDustParticle
extends SpriteBillboardParticle {
    private final float field_3809;
    private final SpriteProvider field_17808;

    private BlockFallingDustParticle(World world, double x, double y, double z, float colorRed, float colorGreen, float colorBlue, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.field_17808 = spriteProvider;
        this.colorRed = colorRed;
        this.colorGreen = colorGreen;
        this.colorBlue = colorBlue;
        float f = 0.9f;
        this.scale *= 0.67499995f;
        int i = (int)(32.0 / (Math.random() * 0.8 + 0.2));
        this.maxAge = (int)Math.max((float)i * 0.9f, 1.0f);
        this.setSpriteForAge(spriteProvider);
        this.field_3809 = ((float)Math.random() - 0.5f) * 0.1f;
        this.angle = (float)Math.random() * ((float)Math.PI * 2);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getSize(float tickDelta) {
        return this.scale * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 32.0f, 0.0f, 1.0f);
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
        this.setSpriteForAge(this.field_17808);
        this.prevAngle = this.angle;
        this.angle += (float)Math.PI * this.field_3809 * 2.0f;
        if (this.onGround) {
            this.angle = 0.0f;
            this.prevAngle = 0.0f;
        }
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityY -= (double)0.003f;
        this.velocityY = Math.max(this.velocityY, (double)-0.14f);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<BlockStateParticleEffect> {
        private final SpriteProvider field_17809;

        public Factory(SpriteProvider spriteProvider) {
            this.field_17809 = spriteProvider;
        }

        @Override
        @Nullable
        public Particle createParticle(BlockStateParticleEffect blockStateParticleEffect, World world, double d, double e, double f, double g, double h, double i) {
            BlockState blockState = blockStateParticleEffect.getBlockState();
            if (!blockState.isAir() && blockState.getRenderType() == BlockRenderType.INVISIBLE) {
                return null;
            }
            int j = MinecraftClient.getInstance().getBlockColorMap().getColor(blockState, world, new BlockPos(d, e, f));
            if (blockState.getBlock() instanceof FallingBlock) {
                j = ((FallingBlock)blockState.getBlock()).getColor(blockState);
            }
            float k = (float)(j >> 16 & 0xFF) / 255.0f;
            float l = (float)(j >> 8 & 0xFF) / 255.0f;
            float m = (float)(j & 0xFF) / 255.0f;
            return new BlockFallingDustParticle(world, d, e, f, k, l, m, this.field_17809);
        }
    }
}

