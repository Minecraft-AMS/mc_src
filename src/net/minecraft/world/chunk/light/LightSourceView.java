/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.chunk.light;

import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.class_8528;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface LightSourceView
extends BlockView {
    public void forEachLightSource(BiConsumer<BlockPos, BlockState> var1);

    public class_8528 getLightSourcesStream();
}

