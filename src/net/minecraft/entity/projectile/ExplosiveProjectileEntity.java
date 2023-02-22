/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public abstract class ExplosiveProjectileEntity
extends Entity {
    public LivingEntity owner;
    private int life;
    private int ticks;
    public double posX;
    public double posY;
    public double posZ;

    protected ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> type, World world) {
        super(type, world);
    }

    public ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> type, double x, double y, double z, double directionX, double directionY, double directionZ, World world) {
        this(type, world);
        this.refreshPositionAndAngles(x, y, z, this.yaw, this.pitch);
        this.updatePosition(x, y, z);
        double d = MathHelper.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
        this.posX = directionX / d * 0.1;
        this.posY = directionY / d * 0.1;
        this.posZ = directionZ / d * 0.1;
    }

    public ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> type, LivingEntity owner, double directionX, double directionY, double directionZ, World world) {
        this(type, world);
        this.owner = owner;
        this.refreshPositionAndAngles(owner.x, owner.y, owner.z, owner.yaw, owner.pitch);
        this.updatePosition(this.x, this.y, this.z);
        this.setVelocity(Vec3d.ZERO);
        double d = MathHelper.sqrt((directionX += this.random.nextGaussian() * 0.4) * directionX + (directionY += this.random.nextGaussian() * 0.4) * directionY + (directionZ += this.random.nextGaussian() * 0.4) * directionZ);
        this.posX = directionX / d * 0.1;
        this.posY = directionY / d * 0.1;
        this.posZ = directionZ / d * 0.1;
    }

    @Override
    protected void initDataTracker() {
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

    @Override
    public void tick() {
        if (!this.world.isClient && (this.owner != null && this.owner.removed || !this.world.isBlockLoaded(new BlockPos(this)))) {
            this.remove();
            return;
        }
        super.tick();
        if (this.isBurning()) {
            this.setOnFireFor(1);
        }
        ++this.ticks;
        HitResult hitResult = ProjectileUtil.getCollision((Entity)this, true, this.ticks >= 25, this.owner, RayTraceContext.ShapeType.COLLIDER);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onCollision(hitResult);
        }
        Vec3d vec3d = this.getVelocity();
        this.x += vec3d.x;
        this.y += vec3d.y;
        this.z += vec3d.z;
        ProjectileUtil.method_7484(this, 0.2f);
        float f = this.getDrag();
        if (this.isTouchingWater()) {
            for (int i = 0; i < 4; ++i) {
                float g = 0.25f;
                this.world.addParticle(ParticleTypes.BUBBLE, this.x - vec3d.x * 0.25, this.y - vec3d.y * 0.25, this.z - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
            }
            f = 0.8f;
        }
        this.setVelocity(vec3d.add(this.posX, this.posY, this.posZ).multiply(f));
        this.world.addParticle(this.getParticleType(), this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
        this.updatePosition(this.x, this.y, this.z);
    }

    protected boolean isBurning() {
        return true;
    }

    protected ParticleEffect getParticleType() {
        return ParticleTypes.SMOKE;
    }

    protected float getDrag() {
        return 0.95f;
    }

    protected abstract void onCollision(HitResult var1);

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        Vec3d vec3d = this.getVelocity();
        tag.put("direction", this.toListTag(vec3d.x, vec3d.y, vec3d.z));
        tag.put("power", this.toListTag(this.posX, this.posY, this.posZ));
        tag.putInt("life", this.life);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        ListTag listTag;
        if (tag.contains("power", 9) && (listTag = tag.getList("power", 6)).size() == 3) {
            this.posX = listTag.getDouble(0);
            this.posY = listTag.getDouble(1);
            this.posZ = listTag.getDouble(2);
        }
        this.life = tag.getInt("life");
        if (tag.contains("direction", 9) && tag.getList("direction", 6).size() == 3) {
            listTag = tag.getList("direction", 6);
            this.setVelocity(listTag.getDouble(0), listTag.getDouble(1), listTag.getDouble(2));
        } else {
            this.remove();
        }
    }

    @Override
    public boolean collides() {
        return true;
    }

    @Override
    public float getTargetingMargin() {
        return 1.0f;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.scheduleVelocityUpdate();
        if (source.getAttacker() != null) {
            Vec3d vec3d = source.getAttacker().getRotationVector();
            this.setVelocity(vec3d);
            this.posX = vec3d.x * 0.1;
            this.posY = vec3d.y * 0.1;
            this.posZ = vec3d.z * 0.1;
            if (source.getAttacker() instanceof LivingEntity) {
                this.owner = (LivingEntity)source.getAttacker();
            }
            return true;
        }
        return false;
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
    public Packet<?> createSpawnPacket() {
        int i = this.owner == null ? 0 : this.owner.getEntityId();
        return new EntitySpawnS2CPacket(this.getEntityId(), this.getUuid(), this.x, this.y, this.z, this.pitch, this.yaw, this.getType(), i, new Vec3d(this.posX, this.posY, this.posZ));
    }
}
