/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.tag;

import java.util.List;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;

public final class FluidTags {
    protected static final RequiredTagList<Fluid> REQUIRED_TAGS = RequiredTagListRegistry.register(new Identifier("fluid"), TagManager::getFluids);
    public static final Tag.Identified<Fluid> WATER = FluidTags.register("water");
    public static final Tag.Identified<Fluid> LAVA = FluidTags.register("lava");

    private static Tag.Identified<Fluid> register(String id) {
        return REQUIRED_TAGS.add(id);
    }

    public static List<? extends Tag.Identified<Fluid>> getRequiredTags() {
        return REQUIRED_TAGS.getTags();
    }
}

