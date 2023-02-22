/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public final class GlobalPos
implements DynamicSerializable {
    private final DimensionType dimension;
    private final BlockPos pos;

    private GlobalPos(DimensionType dimensionType, BlockPos blockPos) {
        this.dimension = dimensionType;
        this.pos = blockPos;
    }

    public static GlobalPos create(DimensionType dimensionType, BlockPos blockPos) {
        return new GlobalPos(dimensionType, blockPos);
    }

    public static GlobalPos deserialize(Dynamic<?> dynamic) {
        return (GlobalPos)dynamic.get("dimension").map(DimensionType::deserialize).flatMap(dimensionType -> dynamic.get("pos").map(BlockPos::deserialize).map(blockPos -> new GlobalPos((DimensionType)dimensionType, (BlockPos)blockPos))).orElseThrow(() -> new IllegalArgumentException("Could not parse GlobalPos"));
    }

    public DimensionType getDimension() {
        return this.dimension;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GlobalPos globalPos = (GlobalPos)o;
        return Objects.equals(this.dimension, globalPos.dimension) && Objects.equals(this.pos, globalPos.pos);
    }

    public int hashCode() {
        return Objects.hash(this.dimension, this.pos);
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        return (T)ops.createMap((Map)ImmutableMap.of((Object)ops.createString("dimension"), this.dimension.serialize(ops), (Object)ops.createString("pos"), this.pos.serialize(ops)));
    }

    public String toString() {
        return this.dimension.toString() + " " + this.pos;
    }
}

