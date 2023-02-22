/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public final class StructureStart {
    public static final String INVALID = "INVALID";
    public static final StructureStart DEFAULT = new StructureStart(null, new ChunkPos(0, 0), 0, new StructurePiecesList(List.of()));
    private final ConfiguredStructureFeature<?, ?> feature;
    private final StructurePiecesList children;
    private final ChunkPos pos;
    private int references;
    @Nullable
    private volatile BlockBox boundingBox;

    public StructureStart(ConfiguredStructureFeature<?, ?> configuredStructureFeature, ChunkPos pos, int references, StructurePiecesList children) {
        this.feature = configuredStructureFeature;
        this.pos = pos;
        this.references = references;
        this.children = children;
    }

    public BlockBox getBoundingBox() {
        BlockBox blockBox = this.boundingBox;
        if (blockBox == null) {
            this.boundingBox = blockBox = this.feature.method_41129(this.children.getBoundingBox());
        }
        return blockBox;
    }

    public void place(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos) {
        List<StructurePiece> list = this.children.pieces();
        if (list.isEmpty()) {
            return;
        }
        BlockBox blockBox = list.get((int)0).boundingBox;
        BlockPos blockPos = blockBox.getCenter();
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), blockBox.getMinY(), blockPos.getZ());
        for (StructurePiece structurePiece : list) {
            if (!structurePiece.getBoundingBox().intersects(chunkBox)) continue;
            structurePiece.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, blockPos2);
        }
        ((StructureFeature)this.feature.feature).getPostProcessor().afterPlace(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, this.children);
    }

    public NbtCompound toNbt(StructureContext context, ChunkPos chunkPos) {
        NbtCompound nbtCompound = new NbtCompound();
        if (!this.hasChildren()) {
            nbtCompound.putString("id", INVALID);
            return nbtCompound;
        }
        nbtCompound.putString("id", context.registryManager().get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY).getId(this.feature).toString());
        nbtCompound.putInt("ChunkX", chunkPos.x);
        nbtCompound.putInt("ChunkZ", chunkPos.z);
        nbtCompound.putInt("references", this.references);
        nbtCompound.put("Children", this.children.toNbt(context));
        return nbtCompound;
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public ChunkPos getPos() {
        return this.pos;
    }

    public boolean isInExistingChunk() {
        return this.references < this.getReferenceCountToBeInExistingChunk();
    }

    public void incrementReferences() {
        ++this.references;
    }

    public int getReferences() {
        return this.references;
    }

    protected int getReferenceCountToBeInExistingChunk() {
        return 1;
    }

    public ConfiguredStructureFeature<?, ?> getFeature() {
        return this.feature;
    }

    public List<StructurePiece> getChildren() {
        return this.children.pieces();
    }
}

