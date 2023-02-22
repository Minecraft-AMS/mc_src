/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.entity.boss.dragon;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public abstract class EnderDragonSpawnState
extends Enum<EnderDragonSpawnState> {
    public static final /* enum */ EnderDragonSpawnState START = new EnderDragonSpawnState(){

        @Override
        public void run(ServerWorld world, EnderDragonFight fight, List<EndCrystalEntity> crystals, int tick, BlockPos pos) {
            BlockPos blockPos = new BlockPos(0, 128, 0);
            for (EndCrystalEntity endCrystalEntity : crystals) {
                endCrystalEntity.setBeamTarget(blockPos);
            }
            fight.setSpawnState(PREPARING_TO_SUMMON_PILLARS);
        }
    };
    public static final /* enum */ EnderDragonSpawnState PREPARING_TO_SUMMON_PILLARS = new EnderDragonSpawnState(){

        @Override
        public void run(ServerWorld world, EnderDragonFight fight, List<EndCrystalEntity> crystals, int tick, BlockPos pos) {
            if (tick < 100) {
                if (tick == 0 || tick == 50 || tick == 51 || tick == 52 || tick >= 95) {
                    world.syncWorldEvent(3001, new BlockPos(0, 128, 0), 0);
                }
            } else {
                fight.setSpawnState(SUMMONING_PILLARS);
            }
        }
    };
    public static final /* enum */ EnderDragonSpawnState SUMMONING_PILLARS = new EnderDragonSpawnState(){

        @Override
        public void run(ServerWorld world, EnderDragonFight fight, List<EndCrystalEntity> crystals, int tick, BlockPos pos) {
            boolean bl2;
            int i = 40;
            boolean bl = tick % 40 == 0;
            boolean bl3 = bl2 = tick % 40 == 39;
            if (bl || bl2) {
                int j = tick / 40;
                List<EndSpikeFeature.Spike> list = EndSpikeFeature.getSpikes(world);
                if (j < list.size()) {
                    EndSpikeFeature.Spike spike = list.get(j);
                    if (bl) {
                        for (EndCrystalEntity endCrystalEntity : crystals) {
                            endCrystalEntity.setBeamTarget(new BlockPos(spike.getCenterX(), spike.getHeight() + 1, spike.getCenterZ()));
                        }
                    } else {
                        int k = 10;
                        for (BlockPos blockPos : BlockPos.iterate(new BlockPos(spike.getCenterX() - 10, spike.getHeight() - 10, spike.getCenterZ() - 10), new BlockPos(spike.getCenterX() + 10, spike.getHeight() + 10, spike.getCenterZ() + 10))) {
                            world.removeBlock(blockPos, false);
                        }
                        world.createExplosion(null, (float)spike.getCenterX() + 0.5f, spike.getHeight(), (float)spike.getCenterZ() + 0.5f, 5.0f, Explosion.DestructionType.DESTROY);
                        EndSpikeFeatureConfig endSpikeFeatureConfig = new EndSpikeFeatureConfig(true, (List<EndSpikeFeature.Spike>)ImmutableList.of((Object)spike), new BlockPos(0, 128, 0));
                        Feature.END_SPIKE.generateIfValid(endSpikeFeatureConfig, world, world.getChunkManager().getChunkGenerator(), new Random(), new BlockPos(spike.getCenterX(), 45, spike.getCenterZ()));
                    }
                } else if (bl) {
                    fight.setSpawnState(SUMMONING_DRAGON);
                }
            }
        }
    };
    public static final /* enum */ EnderDragonSpawnState SUMMONING_DRAGON = new EnderDragonSpawnState(){

        @Override
        public void run(ServerWorld world, EnderDragonFight fight, List<EndCrystalEntity> crystals, int tick, BlockPos pos) {
            if (tick >= 100) {
                fight.setSpawnState(END);
                fight.resetEndCrystals();
                for (EndCrystalEntity endCrystalEntity : crystals) {
                    endCrystalEntity.setBeamTarget(null);
                    world.createExplosion(endCrystalEntity, endCrystalEntity.getX(), endCrystalEntity.getY(), endCrystalEntity.getZ(), 6.0f, Explosion.DestructionType.NONE);
                    endCrystalEntity.discard();
                }
            } else if (tick >= 80) {
                world.syncWorldEvent(3001, new BlockPos(0, 128, 0), 0);
            } else if (tick == 0) {
                for (EndCrystalEntity endCrystalEntity : crystals) {
                    endCrystalEntity.setBeamTarget(new BlockPos(0, 128, 0));
                }
            } else if (tick < 5) {
                world.syncWorldEvent(3001, new BlockPos(0, 128, 0), 0);
            }
        }
    };
    public static final /* enum */ EnderDragonSpawnState END = new EnderDragonSpawnState(){

        @Override
        public void run(ServerWorld world, EnderDragonFight fight, List<EndCrystalEntity> crystals, int tick, BlockPos pos) {
        }
    };
    private static final /* synthetic */ EnderDragonSpawnState[] field_13096;

    public static EnderDragonSpawnState[] values() {
        return (EnderDragonSpawnState[])field_13096.clone();
    }

    public static EnderDragonSpawnState valueOf(String string) {
        return Enum.valueOf(EnderDragonSpawnState.class, string);
    }

    public abstract void run(ServerWorld var1, EnderDragonFight var2, List<EndCrystalEntity> var3, int var4, BlockPos var5);

    private static /* synthetic */ EnderDragonSpawnState[] method_36745() {
        return new EnderDragonSpawnState[]{START, PREPARING_TO_SUMMON_PILLARS, SUMMONING_PILLARS, SUMMONING_DRAGON, END};
    }

    static {
        field_13096 = EnderDragonSpawnState.method_36745();
    }
}

