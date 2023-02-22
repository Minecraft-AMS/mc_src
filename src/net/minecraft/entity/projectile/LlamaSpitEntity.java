/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile;

import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class LlamaSpitEntity
extends Entity
implements Projectile {
    public LlamaEntity owner;
    private CompoundTag tag;

    public LlamaSpitEntity(EntityType<? extends LlamaSpitEntity> entityType, World world) {
        super(entityType, world);
    }

    public LlamaSpitEntity(World world, LlamaEntity owner) {
        this((EntityType<? extends LlamaSpitEntity>)EntityType.LLAMA_SPIT, world);
        this.owner = owner;
        this.updatePosition(owner.x - (double)(owner.getWidth() + 1.0f) * 0.5 * (double)MathHelper.sin(owner.field_6283 * ((float)Math.PI / 180)), owner.y + (double)owner.getStandingEyeHeight() - (double)0.1f, owner.z + (double)(owner.getWidth() + 1.0f) * 0.5 * (double)MathHelper.cos(owner.field_6283 * ((float)Math.PI / 180)));
    }

    @Environment(value=EnvType.CLIENT)
    public LlamaSpitEntity(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this((EntityType<? extends LlamaSpitEntity>)EntityType.LLAMA_SPIT, world);
        this.updatePosition(x, y, z);
        for (int i = 0; i < 7; ++i) {
            double d = 0.4 + 0.1 * (double)i;
            world.addParticle(ParticleTypes.SPIT, x, y, z, velocityX * d, velocityY, velocityZ * d);
        }
        this.setVelocity(velocityX, velocityY, velocityZ);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tag != null) {
            this.readTag();
        }
        Vec3d vec3d = this.getVelocity();
        HitResult hitResult = ProjectileUtil.getCollision((Entity)this, this.getBoundingBox().stretch(vec3d).expand(1.0), entity -> !entity.isSpectator() && entity != this.owner, RayTraceContext.ShapeType.OUTLINE, true);
        if (hitResult != null) {
            this.method_7481(hitResult);
        }
        this.x += vec3d.x;
        this.y += vec3d.y;
        this.z += vec3d.z;
        float f = MathHelper.sqrt(LlamaSpitEntity.squaredHorizontalLength(vec3d));
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
        float g = 0.99f;
        float h = 0.06f;
        if (!this.world.containsBlockWithMaterial(this.getBoundingBox(), Material.AIR)) {
            this.remove();
            return;
        }
        if (this.isInsideWaterOrBubbleColumn()) {
            this.remove();
            return;
        }
        this.setVelocity(vec3d.multiply(0.99f));
        if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.06f, 0.0));
        }
        this.updatePosition(this.x, this.y, this.z);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setVelocityClient(double x, double y, double z) {
        this.setVelocity(x, y, z);
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            float f = MathHelper.sqrt(x * x + z * z);
            this.pitch = (float)(MathHelper.atan2(y, f) * 57.2957763671875);
            this.yaw = (float)(MathHelper.atan2(x, z) * 57.2957763671875);
            this.prevPitch = this.pitch;
            this.prevYaw = this.yaw;
            this.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
        }
    }

    @Override
    public void setVelocity(double x, double y, double z, float speed, float divergence) {
        Vec3d vec3d = new Vec3d(x, y, z).normalize().add(this.random.nextGaussian() * (double)0.0075f * (double)divergence, this.random.nextGaussian() * (double)0.0075f * (double)divergence, this.random.nextGaussian() * (double)0.0075f * (double)divergence).multiply(speed);
        this.setVelocity(vec3d);
        float f = MathHelper.sqrt(LlamaSpitEntity.squaredHorizontalLength(vec3d));
        this.yaw = (float)(MathHelper.atan2(vec3d.x, z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(vec3d.y, f) * 57.2957763671875);
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
    }

    public void method_7481(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.ENTITY && this.owner != null) {
            ((EntityHitResult)hitResult).getEntity().damage(DamageSource.mobProjectile(this, this.owner).setProjectile(), 1.0f);
        } else if (type == HitResult.Type.BLOCK && !this.world.isClient) {
            this.remove();
        }
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        if (tag.contains("Owner", 10)) {
            this.tag = tag.getCompound("Owner");
        }
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        if (this.owner != null) {
            CompoundTag compoundTag = new CompoundTag();
            UUID uUID = this.owner.getUuid();
            compoundTag.putUuid("OwnerUUID", uUID);
            tag.put("Owner", compoundTag);
        }
    }

    private void readTag() {
        if (this.tag != null && this.tag.containsUuid("OwnerUUID")) {
            UUID uUID = this.tag.getUuid("OwnerUUID");
            List<LlamaEntity> list = this.world.getNonSpectatingEntities(LlamaEntity.class, this.getBoundingBox().expand(15.0));
            for (LlamaEntity llamaEntity : list) {
                if (!llamaEntity.getUuid().equals(uUID)) continue;
                this.owner = llamaEntity;
                break;
            }
        }
        this.tag = null;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
