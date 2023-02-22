/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.api.EnvironmentInterface
 *  net.fabricmc.api.EnvironmentInterfaces
 */
package net.minecraft.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@EnvironmentInterfaces(value={@EnvironmentInterface(value=EnvType.CLIENT, itf=FlyingItemEntity.class)})
public class EnderEyeEntity
extends Entity
implements FlyingItemEntity {
    private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(EnderEyeEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private double velocityX;
    private double velocityY;
    private double velocityZ;
    private int useCount;
    private boolean dropsItem;

    public EnderEyeEntity(EntityType<? extends EnderEyeEntity> entityType, World world) {
        super(entityType, world);
    }

    public EnderEyeEntity(World world, double x, double y, double z) {
        this((EntityType<? extends EnderEyeEntity>)EntityType.EYE_OF_ENDER, world);
        this.useCount = 0;
        this.updatePosition(x, y, z);
    }

    public void setItem(ItemStack stack2) {
        if (stack2.getItem() != Items.ENDER_EYE || stack2.hasTag()) {
            this.getDataTracker().set(ITEM, Util.make(stack2.copy(), stack -> stack.setCount(1)));
        }
    }

    private ItemStack getTrackedItem() {
        return this.getDataTracker().get(ITEM);
    }

    @Override
    public ItemStack getStack() {
        ItemStack itemStack = this.getTrackedItem();
        return itemStack.isEmpty() ? new ItemStack(Items.ENDER_EYE) : itemStack;
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(ITEM, ItemStack.EMPTY);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        double d = this.getBoundingBox().getAverageSideLength() * 4.0;
        if (Double.isNaN(d)) {
            d = 4.0;
        }
        return distance < (d *= 64.0) * d;
    }

    public void moveTowards(BlockPos pos) {
        double d = pos.getX();
        int i = pos.getY();
        double f = d - this.x;
        double e = pos.getZ();
        double g = e - this.z;
        float h = MathHelper.sqrt(f * f + g * g);
        if (h > 12.0f) {
            this.velocityX = this.x + f / (double)h * 12.0;
            this.velocityZ = this.z + g / (double)h * 12.0;
            this.velocityY = this.y + 8.0;
        } else {
            this.velocityX = d;
            this.velocityY = i;
            this.velocityZ = e;
        }
        this.useCount = 0;
        this.dropsItem = this.random.nextInt(5) > 0;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setVelocityClient(double x, double y, double z) {
        this.setVelocity(x, y, z);
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            float f = MathHelper.sqrt(x * x + z * z);
            this.yaw = (float)(MathHelper.atan2(x, z) * 57.2957763671875);
            this.pitch = (float)(MathHelper.atan2(y, f) * 57.2957763671875);
            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;
        }
    }

    @Override
    public void tick() {
        this.lastRenderX = this.x;
        this.lastRenderY = this.y;
        this.lastRenderZ = this.z;
        super.tick();
        Vec3d vec3d = this.getVelocity();
        this.x += vec3d.x;
        this.y += vec3d.y;
        this.z += vec3d.z;
        float f = MathHelper.sqrt(EnderEyeEntity.squaredHorizontalLength(vec3d));
        this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(vec3d.y, f) * 57.2957763671875);
        while (this.pitch - this.prevPitch < -180.0f) {
            this.prevPitch -= 360.0f;
        }
        while (this.pitch - this.prevPitch >= 180.0f) {
            this.prevPitch += 360.0f;
        }
        while (this.yaw - this.prevYaw < -180.0f) {
            this.prevYaw -= 360.0f;
        }
        while (this.yaw - this.prevYaw >= 180.0f) {
            this.prevYaw += 360.0f;
        }
        this.pitch = MathHelper.lerp(0.2f, this.prevPitch, this.pitch);
        this.yaw = MathHelper.lerp(0.2f, this.prevYaw, this.yaw);
        if (!this.world.isClient) {
            double d = this.velocityX - this.x;
            double e = this.velocityZ - this.z;
            float g = (float)Math.sqrt(d * d + e * e);
            float h = (float)MathHelper.atan2(e, d);
            double i = MathHelper.lerp(0.0025, (double)f, (double)g);
            double j = vec3d.y;
            if (g < 1.0f) {
                i *= 0.8;
                j *= 0.8;
            }
            int k = this.y < this.velocityY ? 1 : -1;
            vec3d = new Vec3d(Math.cos(h) * i, j + ((double)k - j) * (double)0.015f, Math.sin(h) * i);
            this.setVelocity(vec3d);
        }
        float l = 0.25f;
        if (this.isTouchingWater()) {
            for (int m = 0; m < 4; ++m) {
                this.world.addParticle(ParticleTypes.BUBBLE, this.x - vec3d.x * 0.25, this.y - vec3d.y * 0.25, this.z - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
            }
        } else {
            this.world.addParticle(ParticleTypes.PORTAL, this.x - vec3d.x * 0.25 + this.random.nextDouble() * 0.6 - 0.3, this.y - vec3d.y * 0.25 - 0.5, this.z - vec3d.z * 0.25 + this.random.nextDouble() * 0.6 - 0.3, vec3d.x, vec3d.y, vec3d.z);
        }
        if (!this.world.isClient) {
            this.updatePosition(this.x, this.y, this.z);
            ++this.useCount;
            if (this.useCount > 80 && !this.world.isClient) {
                this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0f, 1.0f);
                this.remove();
                if (this.dropsItem) {
                    this.world.spawnEntity(new ItemEntity(this.world, this.x, this.y, this.z, this.getStack()));
                } else {
                    this.world.playLevelEvent(2003, new BlockPos(this), 0);
                }
            }
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        ItemStack itemStack = this.getTrackedItem();
        if (!itemStack.isEmpty()) {
            tag.put("Item", itemStack.toTag(new CompoundTag()));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        ItemStack itemStack = ItemStack.fromTag(tag.getCompound("Item"));
        this.setItem(itemStack);
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getLightmapCoordinates() {
        return 0xF000F0;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

