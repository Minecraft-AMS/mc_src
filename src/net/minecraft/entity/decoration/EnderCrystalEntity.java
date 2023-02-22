/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.decoration;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class EnderCrystalEntity
extends Entity {
    private static final TrackedData<Optional<BlockPos>> BEAM_TARGET = DataTracker.registerData(EnderCrystalEntity.class, TrackedDataHandlerRegistry.OPTIONA_BLOCK_POS);
    private static final TrackedData<Boolean> SHOW_BOTTOM = DataTracker.registerData(EnderCrystalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public int field_7034;

    public EnderCrystalEntity(EntityType<? extends EnderCrystalEntity> entityType, World world) {
        super(entityType, world);
        this.inanimate = true;
        this.field_7034 = this.random.nextInt(100000);
    }

    public EnderCrystalEntity(World world, double d, double e, double f) {
        this((EntityType<? extends EnderCrystalEntity>)EntityType.END_CRYSTAL, world);
        this.updatePosition(d, e, f);
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(BEAM_TARGET, Optional.empty());
        this.getDataTracker().startTracking(SHOW_BOTTOM, true);
    }

    @Override
    public void tick() {
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        ++this.field_7034;
        if (!this.world.isClient) {
            BlockPos blockPos = new BlockPos(this);
            if (this.world.dimension instanceof TheEndDimension && this.world.getBlockState(blockPos).isAir()) {
                this.world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
            }
        }
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        if (this.getBeamTarget() != null) {
            tag.put("BeamTarget", NbtHelper.fromBlockPos(this.getBeamTarget()));
        }
        tag.putBoolean("ShowBottom", this.getShowBottom());
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        if (tag.contains("BeamTarget", 10)) {
            this.setBeamTarget(NbtHelper.toBlockPos(tag.getCompound("BeamTarget")));
        }
        if (tag.contains("ShowBottom", 1)) {
            this.setShowBottom(tag.getBoolean("ShowBottom"));
        }
    }

    @Override
    public boolean collides() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (source.getAttacker() instanceof EnderDragonEntity) {
            return false;
        }
        if (!this.removed && !this.world.isClient) {
            this.remove();
            if (!source.isExplosive()) {
                this.world.createExplosion(null, this.x, this.y, this.z, 6.0f, Explosion.DestructionType.DESTROY);
            }
            this.crystalDestroyed(source);
        }
        return true;
    }

    @Override
    public void kill() {
        this.crystalDestroyed(DamageSource.GENERIC);
        super.kill();
    }

    private void crystalDestroyed(DamageSource source) {
        TheEndDimension theEndDimension;
        EnderDragonFight enderDragonFight;
        if (this.world.dimension instanceof TheEndDimension && (enderDragonFight = (theEndDimension = (TheEndDimension)this.world.dimension).method_12513()) != null) {
            enderDragonFight.crystalDestroyed(this, source);
        }
    }

    public void setBeamTarget(@Nullable BlockPos blockPos) {
        this.getDataTracker().set(BEAM_TARGET, Optional.ofNullable(blockPos));
    }

    @Nullable
    public BlockPos getBeamTarget() {
        return this.getDataTracker().get(BEAM_TARGET).orElse(null);
    }

    public void setShowBottom(boolean bl) {
        this.getDataTracker().set(SHOW_BOTTOM, bl);
    }

    public boolean getShowBottom() {
        return this.getDataTracker().get(SHOW_BOTTOM);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        return super.shouldRender(distance) || this.getBeamTarget() != null;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
