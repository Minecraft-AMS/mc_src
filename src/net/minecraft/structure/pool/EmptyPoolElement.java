/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.structure.pool;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class EmptyPoolElement
extends StructurePoolElement {
    public static final EmptyPoolElement INSTANCE = new EmptyPoolElement();

    private EmptyPoolElement() {
        super(StructurePool.Projection.TERRAIN_MATCHING);
    }

    @Override
    public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random) {
        return Collections.emptyList();
    }

    @Override
    public BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
        return BlockBox.empty();
    }

    @Override
    public boolean generate(StructureManager structureManager, IWorld world, BlockPos pos, BlockRotation rotation, BlockBox boundingBox, Random random) {
        return true;
    }

    @Override
    public StructurePoolElementType getType() {
        return StructurePoolElementType.EMPTY_POOL_ELEMENT;
    }

    @Override
    public <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.emptyMap());
    }

    public String toString() {
        return "Empty";
    }
}
