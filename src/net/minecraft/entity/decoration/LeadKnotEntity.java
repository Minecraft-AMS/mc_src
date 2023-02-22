/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.decoration;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LeadKnotEntity
extends AbstractDecorationEntity {
    public LeadKnotEntity(EntityType<? extends LeadKnotEntity> entityType, World world) {
        super((EntityType<? extends AbstractDecorationEntity>)entityType, world);
    }

    public LeadKnotEntity(World world, BlockPos blockPos) {
        super(EntityType.LEASH_KNOT, world, blockPos);
        this.updatePosition((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
        float f = 0.125f;
        float g = 0.1875f;
        float h = 0.25f;
        this.setBoundingBox(new Box(this.getX() - 0.1875, this.getY() - 0.25 + 0.125, this.getZ() - 0.1875, this.getX() + 0.1875, this.getY() + 0.25 + 0.125, this.getZ() + 0.1875));
        this.teleporting = true;
    }

    @Override
    public void updatePosition(double x, double y, double z) {
        super.updatePosition((double)MathHelper.floor(x) + 0.5, (double)MathHelper.floor(y) + 0.5, (double)MathHelper.floor(z) + 0.5);
    }

    @Override
    protected void updateAttachmentPosition() {
        this.setPos((double)this.attachmentPos.getX() + 0.5, (double)this.attachmentPos.getY() + 0.5, (double)this.attachmentPos.getZ() + 0.5);
    }

    @Override
    public void setFacing(Direction facing) {
    }

    @Override
    public int getWidthPixels() {
        return 9;
    }

    @Override
    public int getHeightPixels() {
        return 9;
    }

    @Override
    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return -0.0625f;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        return distance < 1024.0;
    }

    @Override
    public void onBreak(@Nullable Entity entity) {
        this.playSound(SoundEvents.ENTITY_LEASH_KNOT_BREAK, 1.0f, 1.0f);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
    }

    @Override
    public boolean interact(PlayerEntity player, Hand hand) {
        if (this.world.isClient) {
            return true;
        }
        boolean bl = false;
        double d = 7.0;
        List<MobEntity> list = this.world.getNonSpectatingEntities(MobEntity.class, new Box(this.getX() - 7.0, this.getY() - 7.0, this.getZ() - 7.0, this.getX() + 7.0, this.getY() + 7.0, this.getZ() + 7.0));
        for (MobEntity mobEntity : list) {
            if (mobEntity.getHoldingEntity() != player) continue;
            mobEntity.attachLeash(this, true);
            bl = true;
        }
        if (!bl) {
            this.remove();
            if (player.abilities.creativeMode) {
                for (MobEntity mobEntity : list) {
                    if (!mobEntity.isLeashed() || mobEntity.getHoldingEntity() != this) continue;
                    mobEntity.detachLeash(true, false);
                }
            }
        }
        return true;
    }

    @Override
    public boolean canStayAttached() {
        return this.world.getBlockState(this.attachmentPos).getBlock().matches(BlockTags.FENCES);
    }

    public static LeadKnotEntity getOrCreate(World world, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        List<LeadKnotEntity> list = world.getNonSpectatingEntities(LeadKnotEntity.class, new Box((double)i - 1.0, (double)j - 1.0, (double)k - 1.0, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0));
        for (LeadKnotEntity leadKnotEntity : list) {
            if (!leadKnotEntity.getDecorationBlockPos().equals(pos)) continue;
            return leadKnotEntity;
        }
        LeadKnotEntity leadKnotEntity2 = new LeadKnotEntity(world, pos);
        world.spawnEntity(leadKnotEntity2);
        leadKnotEntity2.onPlace();
        return leadKnotEntity2;
    }

    @Override
    public void onPlace() {
        this.playSound(SoundEvents.ENTITY_LEASH_KNOT_PLACE, 1.0f, 1.0f);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, this.getType(), 0, this.getDecorationBlockPos());
    }
}

