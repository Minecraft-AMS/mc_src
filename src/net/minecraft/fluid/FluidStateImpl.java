/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.fluid;

import com.google.common.collect.ImmutableMap;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.AbstractState;
import net.minecraft.state.property.Property;

public class FluidStateImpl
extends AbstractState<Fluid, FluidState>
implements FluidState {
    public FluidStateImpl(Fluid fluid, ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
        super(fluid, immutableMap);
    }

    @Override
    public Fluid getFluid() {
        return (Fluid)this.owner;
    }
}

