/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.structure.pool;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ListPoolElement
extends StructurePoolElement {
    public static final Codec<ListPoolElement> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)StructurePoolElement.CODEC.listOf().fieldOf("elements").forGetter(listPoolElement -> listPoolElement.elements), ListPoolElement.method_28883()).apply((Applicative)instance, ListPoolElement::new));
    private final List<StructurePoolElement> elements;

    public ListPoolElement(List<StructurePoolElement> elements, StructurePool.Projection projection) {
        super(projection);
        if (elements.isEmpty()) {
            throw new IllegalArgumentException("Elements are empty");
        }
        this.elements = elements;
        this.setAllElementsProjection(projection);
    }

    @Override
    public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random) {
        return this.elements.get(0).getStructureBlockInfos(structureManager, pos, rotation, random);
    }

    @Override
    public BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
        BlockBox blockBox = BlockBox.empty();
        for (StructurePoolElement structurePoolElement : this.elements) {
            BlockBox blockBox2 = structurePoolElement.getBoundingBox(structureManager, pos, rotation);
            blockBox.encompass(blockBox2);
        }
        return blockBox;
    }

    @Override
    public boolean generate(StructureManager structureManager, StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, BlockPos pos, BlockPos blockPos, BlockRotation rotation, BlockBox box, Random random, boolean keepJigsaws) {
        for (StructurePoolElement structurePoolElement : this.elements) {
            if (structurePoolElement.generate(structureManager, world, structureAccessor, chunkGenerator, pos, blockPos, rotation, box, random, keepJigsaws)) continue;
            return false;
        }
        return true;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return StructurePoolElementType.LIST_POOL_ELEMENT;
    }

    @Override
    public StructurePoolElement setProjection(StructurePool.Projection projection) {
        super.setProjection(projection);
        this.setAllElementsProjection(projection);
        return this;
    }

    public String toString() {
        return "List[" + this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
    }

    private void setAllElementsProjection(StructurePool.Projection projection) {
        this.elements.forEach(structurePoolElement -> structurePoolElement.setProjection(projection));
    }
}

