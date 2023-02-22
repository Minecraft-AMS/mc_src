/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.root;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.root.MangroveRootPlacer;
import net.minecraft.world.gen.root.RootPlacer;

public class RootPlacerType<P extends RootPlacer> {
    public static final RootPlacerType<MangroveRootPlacer> MANGROVE_ROOT_PLACER = RootPlacerType.register("mangrove_root_placer", MangroveRootPlacer.CODEC);
    private final Codec<P> codec;

    private static <P extends RootPlacer> RootPlacerType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registries.ROOT_PLACER_TYPE, id, new RootPlacerType<P>(codec));
    }

    private RootPlacerType(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> getCodec() {
        return this.codec;
    }
}

