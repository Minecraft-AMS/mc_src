/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.explosion;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class EntityExplosionBehavior
extends ExplosionBehavior {
    private final Entity entity;

    public EntityExplosionBehavior(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
        return super.getBlastResistance(explosion, world, pos, blockState, fluidState).map(float_ -> Float.valueOf(this.entity.getEffectiveExplosionResistance(explosion, world, pos, blockState, fluidState, float_.floatValue())));
    }

    @Override
    public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
        return this.entity.canExplosionDestroyBlock(explosion, world, pos, state, power);
    }
}

