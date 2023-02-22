/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;

public class TernarySurfaceConfig
implements SurfaceConfig {
    public static final Codec<TernarySurfaceConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.CODEC.fieldOf("top_material").forGetter(config -> config.topMaterial), (App)BlockState.CODEC.fieldOf("under_material").forGetter(config -> config.underMaterial), (App)BlockState.CODEC.fieldOf("underwater_material").forGetter(config -> config.underwaterMaterial)).apply((Applicative)instance, TernarySurfaceConfig::new));
    private final BlockState topMaterial;
    private final BlockState underMaterial;
    private final BlockState underwaterMaterial;

    public TernarySurfaceConfig(BlockState topMaterial, BlockState underMaterial, BlockState underwaterMaterial) {
        this.topMaterial = topMaterial;
        this.underMaterial = underMaterial;
        this.underwaterMaterial = underwaterMaterial;
    }

    @Override
    public BlockState getTopMaterial() {
        return this.topMaterial;
    }

    @Override
    public BlockState getUnderMaterial() {
        return this.underMaterial;
    }

    @Override
    public BlockState getUnderwaterMaterial() {
        return this.underwaterMaterial;
    }
}

