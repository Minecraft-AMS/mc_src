/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.container;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockContext {
    public static final BlockContext EMPTY = new BlockContext(){

        @Override
        public <T> Optional<T> run(BiFunction<World, BlockPos, T> function) {
            return Optional.empty();
        }
    };

    public static BlockContext create(final World world, final BlockPos pos) {
        return new BlockContext(){

            @Override
            public <T> Optional<T> run(BiFunction<World, BlockPos, T> function) {
                return Optional.of(function.apply(world, pos));
            }
        };
    }

    public <T> Optional<T> run(BiFunction<World, BlockPos, T> var1);

    default public <T> T run(BiFunction<World, BlockPos, T> function, T defaultValue) {
        return this.run(function).orElse(defaultValue);
    }

    default public void run(BiConsumer<World, BlockPos> function) {
        this.run((World world, BlockPos blockPos) -> {
            function.accept((World)world, (BlockPos)blockPos);
            return Optional.empty();
        });
    }
}

