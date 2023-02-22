/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShulkerBulletEntity
extends Entity {
    private LivingEntity owner;
    private Entity target;
    @Nullable
    private Direction direction;
    private int field_7627;
    private double field_7635;
    private double field_7633;
    private double field_7625;
    @Nullable
    private UUID ownerUuid;
    private BlockPos ownerPos;
    @Nullable
    private UUID targetUuid;
    private BlockPos targetPos;

    public ShulkerBulletEntity(EntityType<? extends ShulkerBulletEntity> entityType, World world) {
        super(entityType, world);
        this.noClip = true;
    }

    @Environment(value=EnvType.CLIENT)
    public ShulkerBulletEntity(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this((EntityType<? extends ShulkerBulletEntity>)EntityType.SHULKER_BULLET, world);
        this.refreshPositionAndAngles(x, y, z, this.yaw, this.pitch);
        this.setVelocity(velocityX, velocityY, velocityZ);
    }

    public ShulkerBulletEntity(World world, LivingEntity owner, Entity target, Direction.Axis axis) {
        this((EntityType<? extends ShulkerBulletEntity>)EntityType.SHULKER_BULLET, world);
        this.owner = owner;
        BlockPos blockPos = new BlockPos(owner);
        double d = (double)blockPos.getX() + 0.5;
        double e = (double)blockPos.getY() + 0.5;
        double f = (double)blockPos.getZ() + 0.5;
        this.refreshPositionAndAngles(d, e, f, this.yaw, this.pitch);
        this.target = target;
        this.direction = Direction.UP;
        this.method_7486(axis);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        CompoundTag compoundTag;
        BlockPos blockPos;
        if (this.owner != null) {
            blockPos = new BlockPos(this.owner);
            compoundTag = NbtHelper.fromUuid(this.owner.getUuid());
            compoundTag.putInt("X", blockPos.getX());
            compoundTag.putInt("Y", blockPos.getY());
            compoundTag.putInt("Z", blockPos.getZ());
            tag.put("Owner", compoundTag);
        }
        if (this.target != null) {
            blockPos = new BlockPos(this.target);
            compoundTag = NbtHelper.fromUuid(this.target.getUuid());
            compoundTag.putInt("X", blockPos.getX());
            compoundTag.putInt("Y", blockPos.getY());
            compoundTag.putInt("Z", blockPos.getZ());
            tag.put("Target", compoundTag);
        }
        if (this.direction != null) {
            tag.putInt("Dir", this.direction.getId());
        }
        tag.putInt("Steps", this.field_7627);
        tag.putDouble("TXD", this.field_7635);
        tag.putDouble("TYD", this.field_7633);
        tag.putDouble("TZD", this.field_7625);
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        CompoundTag compoundTag;
        this.field_7627 = tag.getInt("Steps");
        this.field_7635 = tag.getDouble("TXD");
        this.field_7633 = tag.getDouble("TYD");
        this.field_7625 = tag.getDouble("TZD");
        if (tag.contains("Dir", 99)) {
            this.direction = Direction.byId(tag.getInt("Dir"));
        }
        if (tag.contains("Owner", 10)) {
            compoundTag = tag.getCompound("Owner");
            this.ownerUuid = NbtHelper.toUuid(compoundTag);
            this.ownerPos = new BlockPos(compoundTag.getInt("X"), compoundTag.getInt("Y"), compoundTag.getInt("Z"));
        }
        if (tag.contains("Target", 10)) {
            compoundTag = tag.getCompound("Target");
            this.targetUuid = NbtHelper.toUuid(compoundTag);
            this.targetPos = new BlockPos(compoundTag.getInt("X"), compoundTag.getInt("Y"), compoundTag.getInt("Z"));
        }
    }

    @Override
    protected void initDataTracker() {
    }

    private void setDirection(@Nullable Direction direction) {
        this.direction = direction;
    }

    private void method_7486(@Nullable Direction.Axis axis) {
        BlockPos blockPos;
        double d = 0.5;
        if (this.target == null) {
            blockPos = new BlockPos(this).down();
        } else {
            d = (double)this.target.getHeight() * 0.5;
            blockPos = new BlockPos(this.target.x, this.target.y + d, this.target.z);
        }
        double e = (double)blockPos.getX() + 0.5;
        double f = (double)blockPos.getY() + d;
        double g = (double)blockPos.getZ() + 0.5;
        Direction direction = null;
        if (!blockPos.isWithinDistance(this.getPos(), 2.0)) {
            BlockPos blockPos2 = new BlockPos(this);
            ArrayList list = Lists.newArrayList();
            if (axis != Direction.Axis.X) {
                if (blockPos2.getX() < blockPos.getX() && this.world.isAir(blockPos2.east())) {
                    list.add(Direction.EAST);
                } else if (blockPos2.getX() > blockPos.getX() && this.world.isAir(blockPos2.west())) {
                    list.add(Direction.WEST);
                }
            }
            if (axis != Direction.Axis.Y) {
                if (blockPos2.getY() < blockPos.getY() && this.world.isAir(blockPos2.up())) {
                    list.add(Direction.UP);
                } else if (blockPos2.getY() > blockPos.getY() && this.world.isAir(blockPos2.down())) {
                    list.add(Direction.DOWN);
                }
            }
            if (axis != Direction.Axis.Z) {
                if (blockPos2.getZ() < blockPos.getZ() && this.world.isAir(blockPos2.south())) {
                    list.add(Direction.SOUTH);
                } else if (blockPos2.getZ() > blockPos.getZ() && this.world.isAir(blockPos2.north())) {
                    list.add(Direction.NORTH);
                }
            }
            direction = Direction.random(this.random);
            if (list.isEmpty()) {
                for (int i = 5; !this.world.isAir(blockPos2.offset(direction)) && i > 0; --i) {
                    direction = Direction.random(this.random);
                }
            } else {
                direction = (Direction)list.get(this.random.nextInt(list.size()));
            }
            e = this.x + (double)direction.getOffsetX();
            f = this.y + (double)direction.getOffsetY();
            g = this.z + (double)direction.getOffsetZ();
        }
        this.setDirection(direction);
        double h = e - this.x;
        double j = f - this.y;
        double k = g - this.z;
        double l = MathHelper.sqrt(h * h + j * j + k * k);
        if (l == 0.0) {
            this.field_7635 = 0.0;
            this.field_7633 = 0.0;
            this.field_7625 = 0.0;
        } else {
            this.field_7635 = h / l * 0.15;
            this.field_7633 = j / l * 0.15;
            this.field_7625 = k / l * 0.15;
        }
        this.velocityDirty = true;
        this.field_7627 = 10 + this.random.nextInt(5) * 10;
    }

    @Override
    public void tick() {
        Vec3d vec3d;
        if (!this.world.isClient && this.world.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
            return;
        }
        super.tick();
        if (!this.world.isClient) {
            List<LivingEntity> list;
            if (this.target == null && this.targetUuid != null) {
                list = this.world.getNonSpectatingEntities(LivingEntity.class, new Box(this.targetPos.add(-2, -2, -2), this.targetPos.add(2, 2, 2)));
                for (LivingEntity livingEntity : list) {
                    if (!livingEntity.getUuid().equals(this.targetUuid)) continue;
                    this.target = livingEntity;
                    break;
                }
                this.targetUuid = null;
            }
            if (this.owner == null && this.ownerUuid != null) {
                list = this.world.getNonSpectatingEntities(LivingEntity.class, new Box(this.ownerPos.add(-2, -2, -2), this.ownerPos.add(2, 2, 2)));
                for (LivingEntity livingEntity : list) {
                    if (!livingEntity.getUuid().equals(this.ownerUuid)) continue;
                    this.owner = livingEntity;
                    break;
                }
                this.ownerUuid = null;
            }
            if (!(this.target == null || !this.target.isAlive() || this.target instanceof PlayerEntity && ((PlayerEntity)this.target).isSpectator())) {
                this.field_7635 = MathHelper.clamp(this.field_7635 * 1.025, -1.0, 1.0);
                this.field_7633 = MathHelper.clamp(this.field_7633 * 1.025, -1.0, 1.0);
                this.field_7625 = MathHelper.clamp(this.field_7625 * 1.025, -1.0, 1.0);
                vec3d = this.getVelocity();
                this.setVelocity(vec3d.add((this.field_7635 - vec3d.x) * 0.2, (this.field_7633 - vec3d.y) * 0.2, (this.field_7625 - vec3d.z) * 0.2));
            } else if (!this.hasNoGravity()) {
                this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
            }
            HitResult hitResult = ProjectileUtil.getCollision((Entity)this, true, false, this.owner, RayTraceContext.ShapeType.COLLIDER);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onHit(hitResult);
            }
        }
        vec3d = this.getVelocity();
        this.updatePosition(this.x + vec3d.x, this.y + vec3d.y, this.z + vec3d.z);
        ProjectileUtil.method_7484(this, 0.5f);
        if (this.world.isClient) {
            this.world.addParticle(ParticleTypes.END_ROD, this.x - vec3d.x, this.y - vec3d.y + 0.15, this.z - vec3d.z, 0.0, 0.0, 0.0);
        } else if (this.target != null && !this.target.removed) {
            if (this.field_7627 > 0) {
                --this.field_7627;
                if (this.field_7627 == 0) {
                    this.method_7486(this.direction == null ? null : this.direction.getAxis());
                }
            }
            if (this.direction != null) {
                BlockPos blockPos = new BlockPos(this);
                Direction.Axis axis = this.direction.getAxis();
                if (this.world.isTopSolid(blockPos.offset(this.direction), this)) {
                    this.method_7486(axis);
                } else {
                    BlockPos blockPos2 = new BlockPos(this.target);
                    if (axis == Direction.Axis.X && blockPos.getX() == blockPos2.getX() || axis == Direction.Axis.Z && blockPos.getZ() == blockPos2.getZ() || axis == Direction.Axis.Y && blockPos.getY() == blockPos2.getY()) {
                        this.method_7486(axis);
                    }
                }
            }
        }
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        return distance < 16384.0;
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

    protected void onHit(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult)hitResult).getEntity();
            boolean bl = entity.damage(DamageSource.mobProjectile(this, this.owner).setProjectile(), 4.0f);
            if (bl) {
                this.dealDamage(this.owner, entity);
                if (entity instanceof LivingEntity) {
                    ((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 200));
                }
            }
        } else {
            ((ServerWorld)this.world).spawnParticles(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 2, 0.2, 0.2, 0.2, 0.0);
            this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HIT, 1.0f, 1.0f);
        }
        this.remove();
    }

    @Override
    public boolean collides() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.world.isClient) {
            this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HURT, 1.0f, 1.0f);
            ((ServerWorld)this.world).spawnParticles(ParticleTypes.CRIT, this.x, this.y, this.z, 15, 0.2, 0.2, 0.2, 0.0);
            this.remove();
        }
        return true;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
