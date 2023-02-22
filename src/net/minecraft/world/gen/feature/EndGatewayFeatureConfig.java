/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.FeatureConfig;

public class EndGatewayFeatureConfig
implements FeatureConfig {
    private final Optional<BlockPos> exitPos;
    private final boolean exact;

    private EndGatewayFeatureConfig(Optional<BlockPos> exitPos, boolean exact) {
        this.exitPos = exitPos;
        this.exact = exact;
    }

    public static EndGatewayFeatureConfig createConfig(BlockPos exitPortalPosition, boolean exitsAtSpawn) {
        return new EndGatewayFeatureConfig(Optional.of(exitPortalPosition), exitsAtSpawn);
    }

    public static EndGatewayFeatureConfig createConfig() {
        return new EndGatewayFeatureConfig(Optional.empty(), false);
    }

    public Optional<BlockPos> getExitPos() {
        return this.exitPos;
    }

    public boolean isExact() {
        return this.exact;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, this.exitPos.map(blockPos -> ops.createMap((Map)ImmutableMap.of((Object)ops.createString("exit_x"), (Object)ops.createInt(blockPos.getX()), (Object)ops.createString("exit_y"), (Object)ops.createInt(blockPos.getY()), (Object)ops.createString("exit_z"), (Object)ops.createInt(blockPos.getZ()), (Object)ops.createString("exact"), (Object)ops.createBoolean(this.exact)))).orElse(ops.emptyMap()));
    }

    public static <T> EndGatewayFeatureConfig deserialize(Dynamic<T> dynamic) {
        Optional<BlockPos> optional = dynamic.get("exit_x").asNumber().flatMap(number -> dynamic.get("exit_y").asNumber().flatMap(number2 -> dynamic.get("exit_z").asNumber().map(number3 -> new BlockPos(number.intValue(), number2.intValue(), number3.intValue()))));
        boolean bl = dynamic.get("exact").asBoolean(false);
        return new EndGatewayFeatureConfig(optional, bl);
    }
}

