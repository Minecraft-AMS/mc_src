/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server.network;

import java.util.Collection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class DebugInfoSender {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void method_19775(ServerWorld serverWorld, ChunkPos chunkPos) {
    }

    public static void method_19776(ServerWorld serverWorld, BlockPos blockPos) {
    }

    public static void method_19777(ServerWorld serverWorld, BlockPos blockPos) {
    }

    public static void sendPointOfInterest(ServerWorld world, BlockPos pos) {
    }

    public static void sendPathfindingData(World world, MobEntity mob, @Nullable Path path, float nodeReachProximity) {
    }

    public static void sendNeighborUpdate(World world, BlockPos pos) {
    }

    public static void sendStructureStart(IWorld world, StructureStart structureStart) {
    }

    public static void sendGoalSelector(World world, MobEntity mob, GoalSelector goalSelector) {
    }

    public static void sendRaids(ServerWorld server, Collection<Raid> raids) {
    }

    public static void sendVillagerAiDebugData(LivingEntity living) {
    }
}

