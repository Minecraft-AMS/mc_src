/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.tag;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class FluidTags {
    public static final TagKey<Fluid> WATER = FluidTags.of("water");
    public static final TagKey<Fluid> LAVA = FluidTags.of("lava");

    private FluidTags() {
    }

    private static TagKey<Fluid> of(String id) {
        return TagKey.of(Registry.FLUID_KEY, new Identifier(id));
    }
}

