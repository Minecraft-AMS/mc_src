/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.event;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.PositionSourceType;

public interface PositionSource {
    public static final Codec<PositionSource> CODEC = Registries.POSITION_SOURCE_TYPE.getCodec().dispatch(PositionSource::getType, PositionSourceType::getCodec);

    public Optional<Vec3d> getPos(World var1);

    public PositionSourceType<?> getType();
}

