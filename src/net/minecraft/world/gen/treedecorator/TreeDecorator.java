/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public abstract class TreeDecorator {
    public static final Codec<TreeDecorator> TYPE_CODEC = Registry.TREE_DECORATOR_TYPE.getCodec().dispatch(TreeDecorator::getType, TreeDecoratorType::getCodec);

    protected abstract TreeDecoratorType<?> getType();

    public abstract void generate(TestableWorld var1, BiConsumer<BlockPos, BlockState> var2, Random var3, List<BlockPos> var4, List<BlockPos> var5);

    protected static void placeVine(BiConsumer<BlockPos, BlockState> replacer, BlockPos pos, BooleanProperty facing) {
        replacer.accept(pos, (BlockState)Blocks.VINE.getDefaultState().with(facing, true));
    }
}

