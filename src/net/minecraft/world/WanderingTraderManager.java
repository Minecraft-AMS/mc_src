/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import java.util.Optional;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;

public class WanderingTraderManager {
    private final Random random = new Random();
    private final ServerWorld world;
    private int field_17728;
    private int spawnDelay;
    private int spawnChance;

    public WanderingTraderManager(ServerWorld world) {
        this.world = world;
        this.field_17728 = 1200;
        LevelProperties levelProperties = world.getLevelProperties();
        this.spawnDelay = levelProperties.getWanderingTraderSpawnDelay();
        this.spawnChance = levelProperties.getWanderingTraderSpawnChance();
        if (this.spawnDelay == 0 && this.spawnChance == 0) {
            this.spawnDelay = 24000;
            levelProperties.setWanderingTraderSpawnDelay(this.spawnDelay);
            this.spawnChance = 25;
            levelProperties.setWanderingTraderSpawnChance(this.spawnChance);
        }
    }

    public void tick() {
        if (--this.field_17728 > 0) {
            return;
        }
        this.field_17728 = 1200;
        LevelProperties levelProperties = this.world.getLevelProperties();
        this.spawnDelay -= 1200;
        levelProperties.setWanderingTraderSpawnDelay(this.spawnDelay);
        if (this.spawnDelay > 0) {
            return;
        }
        this.spawnDelay = 24000;
        if (!this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
            return;
        }
        int i = this.spawnChance;
        this.spawnChance = MathHelper.clamp(this.spawnChance + 25, 25, 75);
        levelProperties.setWanderingTraderSpawnChance(this.spawnChance);
        if (this.random.nextInt(100) > i) {
            return;
        }
        if (this.method_18018()) {
            this.spawnChance = 25;
        }
    }

    private boolean method_18018() {
        ServerPlayerEntity playerEntity = this.world.getRandomAlivePlayer();
        if (playerEntity == null) {
            return true;
        }
        if (this.random.nextInt(10) != 0) {
            return false;
        }
        BlockPos blockPos2 = playerEntity.getBlockPos();
        int i = 48;
        PointOfInterestStorage pointOfInterestStorage = this.world.getPointOfInterestStorage();
        Optional<BlockPos> optional = pointOfInterestStorage.getPosition(PointOfInterestType.MEETING.getCompletionCondition(), blockPos -> true, blockPos2, 48, PointOfInterestStorage.OccupationStatus.ANY);
        BlockPos blockPos22 = optional.orElse(blockPos2);
        BlockPos blockPos3 = this.method_18017(blockPos22, 48);
        if (blockPos3 != null) {
            if (this.world.getBiome(blockPos3) == Biomes.THE_VOID) {
                return false;
            }
            WanderingTraderEntity wanderingTraderEntity = EntityType.WANDERING_TRADER.spawn(this.world, null, null, null, blockPos3, SpawnType.EVENT, false, false);
            if (wanderingTraderEntity != null) {
                for (int j = 0; j < 2; ++j) {
                    this.method_18016(wanderingTraderEntity, 4);
                }
                this.world.getLevelProperties().setWanderingTraderId(wanderingTraderEntity.getUuid());
                wanderingTraderEntity.setDespawnDelay(48000);
                wanderingTraderEntity.setWanderTarget(blockPos22);
                wanderingTraderEntity.setPositionTarget(blockPos22, 16);
                return true;
            }
        }
        return false;
    }

    private void method_18016(WanderingTraderEntity wanderingTraderEntity, int i) {
        BlockPos blockPos = this.method_18017(new BlockPos(wanderingTraderEntity), i);
        if (blockPos == null) {
            return;
        }
        TraderLlamaEntity traderLlamaEntity = EntityType.TRADER_LLAMA.spawn(this.world, null, null, null, blockPos, SpawnType.EVENT, false, false);
        if (traderLlamaEntity == null) {
            return;
        }
        traderLlamaEntity.attachLeash(wanderingTraderEntity, true);
    }

    @Nullable
    private BlockPos method_18017(BlockPos blockPos, int i) {
        BlockPos blockPos2 = null;
        for (int j = 0; j < 10; ++j) {
            int l;
            int m;
            int k = blockPos.getX() + this.random.nextInt(i * 2) - i;
            BlockPos blockPos3 = new BlockPos(k, m = this.world.getTop(Heightmap.Type.WORLD_SURFACE, k, l = blockPos.getZ() + this.random.nextInt(i * 2) - i), l);
            if (!SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, this.world, blockPos3, EntityType.WANDERING_TRADER)) continue;
            blockPos2 = blockPos3;
            break;
        }
        return blockPos2;
    }
}

