/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.structure.Structure;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class StructurePlacementData {
    private BlockMirror mirror = BlockMirror.NONE;
    private BlockRotation rotation = BlockRotation.NONE;
    private BlockPos position = BlockPos.ORIGIN;
    private boolean ignoreEntities;
    @Nullable
    private ChunkPos chunkPosition;
    @Nullable
    private BlockBox boundingBox;
    private boolean placeFluids = true;
    @Nullable
    private Random random;
    @Nullable
    private int field_15575;
    private final List<StructureProcessor> processors = Lists.newArrayList();
    private boolean field_16587;

    public StructurePlacementData copy() {
        StructurePlacementData structurePlacementData = new StructurePlacementData();
        structurePlacementData.mirror = this.mirror;
        structurePlacementData.rotation = this.rotation;
        structurePlacementData.position = this.position;
        structurePlacementData.ignoreEntities = this.ignoreEntities;
        structurePlacementData.chunkPosition = this.chunkPosition;
        structurePlacementData.boundingBox = this.boundingBox;
        structurePlacementData.placeFluids = this.placeFluids;
        structurePlacementData.random = this.random;
        structurePlacementData.field_15575 = this.field_15575;
        structurePlacementData.processors.addAll(this.processors);
        structurePlacementData.field_16587 = this.field_16587;
        return structurePlacementData;
    }

    public StructurePlacementData setMirrored(BlockMirror blockMirror) {
        this.mirror = blockMirror;
        return this;
    }

    public StructurePlacementData setRotation(BlockRotation blockRotation) {
        this.rotation = blockRotation;
        return this;
    }

    public StructurePlacementData setPosition(BlockPos position) {
        this.position = position;
        return this;
    }

    public StructurePlacementData setIgnoreEntities(boolean bl) {
        this.ignoreEntities = bl;
        return this;
    }

    public StructurePlacementData setChunkPosition(ChunkPos chunkPosition) {
        this.chunkPosition = chunkPosition;
        return this;
    }

    public StructurePlacementData setBoundingBox(BlockBox boundingBox) {
        this.boundingBox = boundingBox;
        return this;
    }

    public StructurePlacementData setRandom(@Nullable Random random) {
        this.random = random;
        return this;
    }

    public StructurePlacementData method_15131(boolean bl) {
        this.field_16587 = bl;
        return this;
    }

    public StructurePlacementData clearProcessors() {
        this.processors.clear();
        return this;
    }

    public StructurePlacementData addProcessor(StructureProcessor processor) {
        this.processors.add(processor);
        return this;
    }

    public StructurePlacementData removeProcessor(StructureProcessor processor) {
        this.processors.remove(processor);
        return this;
    }

    public BlockMirror getMirror() {
        return this.mirror;
    }

    public BlockRotation getRotation() {
        return this.rotation;
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public Random getRandom(@Nullable BlockPos pos) {
        if (this.random != null) {
            return this.random;
        }
        if (pos == null) {
            return new Random(Util.getMeasuringTimeMs());
        }
        return new Random(MathHelper.hashCode(pos));
    }

    public boolean shouldIgnoreEntities() {
        return this.ignoreEntities;
    }

    @Nullable
    public BlockBox method_15124() {
        if (this.boundingBox == null && this.chunkPosition != null) {
            this.method_15132();
        }
        return this.boundingBox;
    }

    public boolean method_16444() {
        return this.field_16587;
    }

    public List<StructureProcessor> getProcessors() {
        return this.processors;
    }

    void method_15132() {
        if (this.chunkPosition != null) {
            this.boundingBox = this.method_15117(this.chunkPosition);
        }
    }

    public boolean shouldPlaceFluids() {
        return this.placeFluids;
    }

    public List<Structure.StructureBlockInfo> method_15121(List<List<Structure.StructureBlockInfo>> list, @Nullable BlockPos blockPos) {
        int i = list.size();
        return i > 0 ? list.get(this.getRandom(blockPos).nextInt(i)) : Collections.emptyList();
    }

    @Nullable
    private BlockBox method_15117(@Nullable ChunkPos chunkPos) {
        if (chunkPos == null) {
            return this.boundingBox;
        }
        int i = chunkPos.x * 16;
        int j = chunkPos.z * 16;
        return new BlockBox(i, 0, j, i + 16 - 1, 255, j + 16 - 1);
    }
}

