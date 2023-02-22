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
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class BlockLeakParticle
extends SpriteBillboardParticle {
    private final Fluid fluid;
    protected boolean obsidianTear;

    private BlockLeakParticle(ClientWorld world, double x, double y, double z, Fluid fluid) {
        super(world, x, y, z);
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.gravityStrength = 0.06f;
        this.fluid = fluid;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getBrightness(float tint) {
        if (this.obsidianTear) {
            return 240;
        }
        return super.getBrightness(tint);
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        this.updateAge();
        if (this.dead) {
            return;
        }
        this.velocityY -= (double)this.gravityStrength;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.updateVelocity();
        if (this.dead) {
            return;
        }
        this.velocityX *= (double)0.98f;
        this.velocityY *= (double)0.98f;
        this.velocityZ *= (double)0.98f;
        BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
        FluidState fluidState = this.world.getFluidState(blockPos);
        if (fluidState.getFluid() == this.fluid && this.y < (double)((float)blockPos.getY() + fluidState.getHeight(this.world, blockPos))) {
            this.markDead();
        }
    }

    protected void updateAge() {
        if (this.maxAge-- <= 0) {
            this.markDead();
        }
    }

    protected void updateVelocity() {
    }

    @Environment(value=EnvType.CLIENT)
    public static class LandingObsidianTearFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public LandingObsidianTearFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Landing blockLeakParticle = new Landing(clientWorld, d, e, f, Fluids.EMPTY);
            blockLeakParticle.obsidianTear = true;
            blockLeakParticle.maxAge = (int)(28.0 / (Math.random() * 0.8 + 0.2));
            blockLeakParticle.setColor(0.51171875f, 0.03125f, 0.890625f);
            blockLeakParticle.setSprite(this.spriteProvider);
            return blockLeakParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class FallingObsidianTearFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public FallingObsidianTearFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            ContinuousFalling blockLeakParticle = new ContinuousFalling(clientWorld, d, e, f, Fluids.EMPTY, ParticleTypes.LANDING_OBSIDIAN_TEAR);
            blockLeakParticle.obsidianTear = true;
            blockLeakParticle.gravityStrength = 0.01f;
            blockLeakParticle.setColor(0.51171875f, 0.03125f, 0.890625f);
            blockLeakParticle.setSprite(this.spriteProvider);
            return blockLeakParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class DrippingObsidianTearFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public DrippingObsidianTearFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Dripping dripping = new Dripping(clientWorld, d, e, f, Fluids.EMPTY, ParticleTypes.FALLING_OBSIDIAN_TEAR);
            dripping.obsidianTear = true;
            dripping.gravityStrength *= 0.01f;
            dripping.maxAge = 100;
            dripping.setColor(0.51171875f, 0.03125f, 0.890625f);
            dripping.setSprite(this.spriteProvider);
            return dripping;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class FallingNectarFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public FallingNectarFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Falling blockLeakParticle = new Falling(clientWorld, d, e, f, Fluids.EMPTY);
            blockLeakParticle.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
            blockLeakParticle.gravityStrength = 0.007f;
            blockLeakParticle.setColor(0.92f, 0.782f, 0.72f);
            blockLeakParticle.setSprite(this.spriteProvider);
            return blockLeakParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class LandingHoneyFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public LandingHoneyFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Landing blockLeakParticle = new Landing(clientWorld, d, e, f, Fluids.EMPTY);
            blockLeakParticle.maxAge = (int)(128.0 / (Math.random() * 0.8 + 0.2));
            blockLeakParticle.setColor(0.522f, 0.408f, 0.082f);
            blockLeakParticle.setSprite(this.spriteProvider);
            return blockLeakParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class FallingHoneyFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public FallingHoneyFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            FallingHoney blockLeakParticle = new FallingHoney(clientWorld, d, e, f, Fluids.EMPTY, ParticleTypes.LANDING_HONEY);
            blockLeakParticle.gravityStrength = 0.01f;
            blockLeakParticle.setColor(0.582f, 0.448f, 0.082f);
            blockLeakParticle.setSprite(this.spriteProvider);
            return blockLeakParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class DrippingHoneyFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public DrippingHoneyFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Dripping dripping = new Dripping(clientWorld, d, e, f, Fluids.EMPTY, ParticleTypes.FALLING_HONEY);
            dripping.gravityStrength *= 0.01f;
            dripping.maxAge = 100;
            dripping.setColor(0.622f, 0.508f, 0.082f);
            dripping.setSprite(this.spriteProvider);
            return dripping;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class LandingLavaFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public LandingLavaFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Landing blockLeakParticle = new Landing(clientWorld, d, e, f, Fluids.LAVA);
            blockLeakParticle.setColor(1.0f, 0.2857143f, 0.083333336f);
            blockLeakParticle.setSprite(this.spriteProvider);
            return blockLeakParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class FallingLavaFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public FallingLavaFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            ContinuousFalling blockLeakParticle = new ContinuousFalling(clientWorld, d, e, f, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
            blockLeakParticle.setColor(1.0f, 0.2857143f, 0.083333336f);
            blockLeakParticle.setSprite(this.spriteProvider);
            return blockLeakParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class DrippingLavaFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public DrippingLavaFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            DrippingLava drippingLava = new DrippingLava(clientWorld, d, e, f, Fluids.LAVA, ParticleTypes.FALLING_LAVA);
            drippingLava.setSprite(this.spriteProvider);
            return drippingLava;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class FallingWaterFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public FallingWaterFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            ContinuousFalling blockLeakParticle = new ContinuousFalling(clientWorld, d, e, f, Fluids.WATER, ParticleTypes.SPLASH);
            blockLeakParticle.setColor(0.2f, 0.3f, 1.0f);
            blockLeakParticle.setSprite(this.spriteProvider);
            return blockLeakParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class DrippingWaterFactory
    implements ParticleFactory<DefaultParticleType> {
        protected final SpriteProvider spriteProvider;

        public DrippingWaterFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Dripping blockLeakParticle = new Dripping(clientWorld, d, e, f, Fluids.WATER, ParticleTypes.FALLING_WATER);
            blockLeakParticle.setColor(0.2f, 0.3f, 1.0f);
            blockLeakParticle.setSprite(this.spriteProvider);
            return blockLeakParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Landing
    extends BlockLeakParticle {
        private Landing(ClientWorld world, double x, double y, double z, Fluid fluid) {
            super(world, x, y, z, fluid);
            this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Falling
    extends BlockLeakParticle {
        private Falling(ClientWorld world, double x, double y, double z, Fluid fluid) {
            super(world, x, y, z, fluid);
            this.maxAge = (int)(64.0 / (Math.random() * 0.8 + 0.2));
        }

        @Override
        protected void updateVelocity() {
            if (this.onGround) {
                this.markDead();
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class FallingHoney
    extends ContinuousFalling {
        private FallingHoney(ClientWorld world, double x, double y, double z, Fluid fluid, ParticleEffect particleEffect) {
            super(world, x, y, z, fluid, particleEffect);
        }

        @Override
        protected void updateVelocity() {
            if (this.onGround) {
                this.markDead();
                this.world.addParticle(this.nextParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
                this.world.playSound(this.x + 0.5, this.y, this.z + 0.5, SoundEvents.BLOCK_BEEHIVE_DRIP, SoundCategory.BLOCKS, 0.3f + this.world.random.nextFloat() * 2.0f / 3.0f, 1.0f, false);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ContinuousFalling
    extends Falling {
        protected final ParticleEffect nextParticle;

        private ContinuousFalling(ClientWorld clientWorld, double x, double y, double z, Fluid fluid, ParticleEffect nextParticle) {
            super(clientWorld, x, y, z, fluid);
            this.nextParticle = nextParticle;
        }

        @Override
        protected void updateVelocity() {
            if (this.onGround) {
                this.markDead();
                this.world.addParticle(this.nextParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class DrippingLava
    extends Dripping {
        private DrippingLava(ClientWorld clientWorld, double x, double y, double z, Fluid fluid, ParticleEffect nextParticle) {
            super(clientWorld, x, y, z, fluid, nextParticle);
        }

        @Override
        protected void updateAge() {
            this.colorRed = 1.0f;
            this.colorGreen = 16.0f / (float)(40 - this.maxAge + 16);
            this.colorBlue = 4.0f / (float)(40 - this.maxAge + 8);
            super.updateAge();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Dripping
    extends BlockLeakParticle {
        private final ParticleEffect nextParticle;

        private Dripping(ClientWorld clientWorld, double x, double y, double z, Fluid fluid, ParticleEffect nextParticle) {
            super(clientWorld, x, y, z, fluid);
            this.nextParticle = nextParticle;
            this.gravityStrength *= 0.02f;
            this.maxAge = 40;
        }

        @Override
        protected void updateAge() {
            if (this.maxAge-- <= 0) {
                this.markDead();
                this.world.addParticle(this.nextParticle, this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ);
            }
        }

        @Override
        protected void updateVelocity() {
            this.velocityX *= 0.02;
            this.velocityY *= 0.02;
            this.velocityZ *= 0.02;
        }
    }
}

