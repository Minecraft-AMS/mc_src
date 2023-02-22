/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public final class GlobalPos {
    public static final Codec<GlobalPos> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)World.CODEC.fieldOf("dimension").forGetter(GlobalPos::getDimension), (App)BlockPos.CODEC.fieldOf("pos").forGetter(GlobalPos::getPos)).apply((Applicative)instance, GlobalPos::create));
    private final RegistryKey<World> dimension;
    private final BlockPos pos;

    private GlobalPos(RegistryKey<World> dimension, BlockPos pos) {
        this.dimension = dimension;
        this.pos = pos;
    }

    public static GlobalPos create(RegistryKey<World> dimension, BlockPos pos) {
        return new GlobalPos(dimension, pos);
    }

    public RegistryKey<World> getDimension() {
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

    public String toString() {
        return this.dimension.toString() + " " + this.pos;
    }
}

