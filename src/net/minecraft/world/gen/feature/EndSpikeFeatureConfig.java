/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.jetbrains.annotations.Nullable;

public class EndSpikeFeatureConfig
implements FeatureConfig {
    private final boolean crystalInvulnerable;
    private final List<EndSpikeFeature.Spike> spikes;
    @Nullable
    private final BlockPos crystalBeamTarget;

    public EndSpikeFeatureConfig(boolean crystalInvulnerable, List<EndSpikeFeature.Spike> spikes, @Nullable BlockPos crystalBeamTarget) {
        this.crystalInvulnerable = crystalInvulnerable;
        this.spikes = spikes;
        this.crystalBeamTarget = crystalBeamTarget;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("crystalInvulnerable"), (Object)ops.createBoolean(this.crystalInvulnerable), (Object)ops.createString("spikes"), (Object)ops.createList(this.spikes.stream().map(spike -> spike.serialize(ops).getValue())), (Object)ops.createString("crystalBeamTarget"), (Object)(this.crystalBeamTarget == null ? ops.createList(Stream.empty()) : ops.createList(IntStream.of(this.crystalBeamTarget.getX(), this.crystalBeamTarget.getY(), this.crystalBeamTarget.getZ()).mapToObj(arg_0 -> ops.createInt(arg_0)))))));
    }

    public static <T> EndSpikeFeatureConfig deserialize(Dynamic<T> dynamic2) {
        List list = dynamic2.get("spikes").asList(EndSpikeFeature.Spike::deserialize);
        List list2 = dynamic2.get("crystalBeamTarget").asList(dynamic -> dynamic.asInt(0));
        BlockPos blockPos = list2.size() == 3 ? new BlockPos((Integer)list2.get(0), (Integer)list2.get(1), (Integer)list2.get(2)) : null;
        return new EndSpikeFeatureConfig(dynamic2.get("crystalInvulnerable").asBoolean(false), list, blockPos);
    }

    public boolean isCrystalInvulerable() {
        return this.crystalInvulnerable;
    }

    public List<EndSpikeFeature.Spike> getSpikes() {
        return this.spikes;
    }

    @Nullable
    public BlockPos getPos() {
        return this.crystalBeamTarget;
    }
}

