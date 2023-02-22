/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.server;

import java.nio.file.Path;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FluidTagsProvider
extends AbstractTagProvider<Fluid> {
    public FluidTagsProvider(DataGenerator dataGenerator) {
        super(dataGenerator, Registry.FLUID);
    }

    @Override
    protected void configure() {
        this.method_10512(FluidTags.WATER).add((Fluid[])new Fluid[]{Fluids.WATER, Fluids.FLOWING_WATER});
        this.method_10512(FluidTags.LAVA).add((Fluid[])new Fluid[]{Fluids.LAVA, Fluids.FLOWING_LAVA});
    }

    @Override
    protected Path getOutput(Identifier identifier) {
        return this.root.getOutput().resolve("data/" + identifier.getNamespace() + "/tags/fluids/" + identifier.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Fluid Tags";
    }

    @Override
    protected void method_10511(TagContainer<Fluid> tagContainer) {
        FluidTags.setContainer(tagContainer);
    }
}

