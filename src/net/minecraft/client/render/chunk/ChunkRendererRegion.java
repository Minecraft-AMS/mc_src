/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.chunk.RenderedChunk;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.level.ColorResolver;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkRendererRegion
implements BlockRenderView {
    private final int chunkXOffset;
    private final int chunkZOffset;
    protected final RenderedChunk[][] chunks;
    protected final World world;

    ChunkRendererRegion(World world, int chunkX, int chunkZ, RenderedChunk[][] chunks) {
        this.world = world;
        this.chunkXOffset = chunkX;
        this.chunkZOffset = chunkZ;
        this.chunks = chunks;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        int i = ChunkSectionPos.getSectionCoord(pos.getX()) - this.chunkXOffset;
        int j = ChunkSectionPos.getSectionCoord(pos.getZ()) - this.chunkZOffset;
        return this.chunks[i][j].getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        int i = ChunkSectionPos.getSectionCoord(pos.getX()) - this.chunkXOffset;
        int j = ChunkSectionPos.getSectionCoord(pos.getZ()) - this.chunkZOffset;
        return this.chunks[i][j].getBlockState(pos).getFluidState();
    }

    @Override
    public float getBrightness(Direction direction, boolean shaded) {
        return this.world.getBrightness(direction, shaded);
    }

    @Override
    public LightingProvider getLightingProvider() {
        return this.world.getLightingProvider();
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        int i = ChunkSectionPos.getSectionCoord(pos.getX()) - this.chunkXOffset;
        int j = ChunkSectionPos.getSectionCoord(pos.getZ()) - this.chunkZOffset;
        return this.chunks[i][j].getBlockEntity(pos);
    }

    @Override
    public int getColor(BlockPos pos, ColorResolver colorResolver) {
        return this.world.getColor(pos, colorResolver);
    }

    @Override
    public int getBottomY() {
        return this.world.getBottomY();
    }

    @Override
    public int getHeight() {
        return this.world.getHeight();
    }
}

