/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnGlobalS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LightningEntity
extends Entity {
    private int ambientTick;
    public long seed;
    private int remainingActions;
    private final boolean cosmetic;
    @Nullable
    private ServerPlayerEntity channeller;

    public LightningEntity(World world, double x, double y, double z, boolean cosmetic) {
        super(EntityType.LIGHTNING_BOLT, world);
        this.ignoreCameraFrustum = true;
        this.refreshPositionAndAngles(x, y, z, 0.0f, 0.0f);
        this.ambientTick = 2;
        this.seed = this.random.nextLong();
        this.remainingActions = this.random.nextInt(3) + 1;
        this.cosmetic = cosmetic;
        Difficulty difficulty = world.getDifficulty();
        if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD) {
            this.spawnFire(4);
        }
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.WEATHER;
    }

    public void setChanneller(@Nullable ServerPlayerEntity channeller) {
        this.channeller = channeller;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.ambientTick == 2) {
            this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10000.0f, 0.8f + this.random.nextFloat() * 0.2f);
            this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0f, 0.5f + this.random.nextFloat() * 0.2f);
        }
        --this.ambientTick;
        if (this.ambientTick < 0) {
            if (this.remainingActions == 0) {
                this.remove();
            } else if (this.ambientTick < -this.random.nextInt(10)) {
                --this.remainingActions;
                this.ambientTick = 1;
                this.seed = this.random.nextLong();
                this.spawnFire(0);
            }
        }
        if (this.ambientTick >= 0) {
            if (this.world.isClient) {
                this.world.setLightningTicksLeft(2);
            } else if (!this.cosmetic) {
                double d = 3.0;
                List<Entity> list = this.world.getEntities(this, new Box(this.x - 3.0, this.y - 3.0, this.z - 3.0, this.x + 3.0, this.y + 6.0 + 3.0, this.z + 3.0), Entity::isAlive);
                for (Entity entity : list) {
                    entity.onStruckByLightning(this);
                }
                if (this.channeller != null) {
                    Criterions.CHANNELED_LIGHTNING.trigger(this.channeller, list);
                }
            }
        }
    }

    private void spawnFire(int spreadAttempts) {
        if (this.cosmetic || this.world.isClient || !this.world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            return;
        }
        BlockState blockState = Blocks.FIRE.getDefaultState();
        BlockPos blockPos = new BlockPos(this);
        if (this.world.getBlockState(blockPos).isAir() && blockState.canPlaceAt(this.world, blockPos)) {
            this.world.setBlockState(blockPos, blockState);
        }
        for (int i = 0; i < spreadAttempts; ++i) {
            BlockPos blockPos2 = blockPos.add(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
            if (!this.world.getBlockState(blockPos2).isAir() || !blockState.canPlaceAt(this.world, blockPos2)) continue;
            this.world.setBlockState(blockPos2, blockState);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        double d = 64.0 * LightningEntity.getRenderDistanceMultiplier();
        return distance < d * d;
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnGlobalS2CPacket(this);
    }
}

