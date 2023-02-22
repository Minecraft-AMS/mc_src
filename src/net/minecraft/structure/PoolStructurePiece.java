/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  org.slf4j.Logger
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Locale;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.slf4j.Logger;

public class PoolStructurePiece
extends StructurePiece {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final StructurePoolElement poolElement;
    protected BlockPos pos;
    private final int groundLevelDelta;
    protected final BlockRotation rotation;
    private final List<JigsawJunction> junctions = Lists.newArrayList();
    private final StructureTemplateManager structureTemplateManager;

    public PoolStructurePiece(StructureTemplateManager structureTemplateManager, StructurePoolElement poolElement, BlockPos pos, int groundLevelDelta, BlockRotation rotation, BlockBox boundingBox) {
        super(StructurePieceType.JIGSAW, 0, boundingBox);
        this.structureTemplateManager = structureTemplateManager;
        this.poolElement = poolElement;
        this.pos = pos;
        this.groundLevelDelta = groundLevelDelta;
        this.rotation = rotation;
    }

    public PoolStructurePiece(StructureContext context, NbtCompound nbt) {
        super(StructurePieceType.JIGSAW, nbt);
        this.structureTemplateManager = context.structureTemplateManager();
        this.pos = new BlockPos(nbt.getInt("PosX"), nbt.getInt("PosY"), nbt.getInt("PosZ"));
        this.groundLevelDelta = nbt.getInt("ground_level_delta");
        RegistryOps<NbtElement> dynamicOps = RegistryOps.of(NbtOps.INSTANCE, context.registryManager());
        this.poolElement = (StructurePoolElement)StructurePoolElement.CODEC.parse(dynamicOps, (Object)nbt.getCompound("pool_element")).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).orElseThrow(() -> new IllegalStateException("Invalid pool element found"));
        this.rotation = BlockRotation.valueOf(nbt.getString("rotation"));
        this.boundingBox = this.poolElement.getBoundingBox(this.structureTemplateManager, this.pos, this.rotation);
        NbtList nbtList = nbt.getList("junctions", 10);
        this.junctions.clear();
        nbtList.forEach(junctionTag -> this.junctions.add(JigsawJunction.deserialize(new Dynamic(dynamicOps, junctionTag))));
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        nbt.putInt("PosX", this.pos.getX());
        nbt.putInt("PosY", this.pos.getY());
        nbt.putInt("PosZ", this.pos.getZ());
        nbt.putInt("ground_level_delta", this.groundLevelDelta);
        RegistryOps<NbtElement> dynamicOps = RegistryOps.of(NbtOps.INSTANCE, context.registryManager());
        StructurePoolElement.CODEC.encodeStart(dynamicOps, (Object)this.poolElement).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent(nbtElement -> nbt.put("pool_element", (NbtElement)nbtElement));
        nbt.putString("rotation", this.rotation.name());
        NbtList nbtList = new NbtList();
        for (JigsawJunction jigsawJunction : this.junctions) {
            nbtList.add((NbtElement)jigsawJunction.serialize(dynamicOps).getValue());
        }
        nbt.put("junctions", nbtList);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        this.generate(world, structureAccessor, chunkGenerator, random, chunkBox, pivot, false);
    }

    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, BlockPos pivot, boolean keepJigsaws) {
        this.poolElement.generate(this.structureTemplateManager, world, structureAccessor, chunkGenerator, this.pos, pivot, this.rotation, boundingBox, random, keepJigsaws);
    }

    @Override
    public void translate(int x, int y, int z) {
        super.translate(x, y, z);
        this.pos = this.pos.add(x, y, z);
    }

    @Override
    public BlockRotation getRotation() {
        return this.rotation;
    }

    public String toString() {
        return String.format(Locale.ROOT, "<%s | %s | %s | %s>", this.getClass().getSimpleName(), this.pos, this.rotation, this.poolElement);
    }

    public StructurePoolElement getPoolElement() {
        return this.poolElement;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getGroundLevelDelta() {
        return this.groundLevelDelta;
    }

    public void addJunction(JigsawJunction junction) {
        this.junctions.add(junction);
    }

    public List<JigsawJunction> getJunctions() {
        return this.junctions;
    }
}

