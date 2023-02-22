/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.structure.pool;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import org.jetbrains.annotations.Nullable;

public abstract class StructurePoolElement {
    @Nullable
    private volatile StructurePool.Projection projection;

    protected StructurePoolElement(StructurePool.Projection projection) {
        this.projection = projection;
    }

    protected StructurePoolElement(Dynamic<?> dynamic) {
        this.projection = StructurePool.Projection.getById(dynamic.get("projection").asString(StructurePool.Projection.RIGID.getId()));
    }

    public abstract List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager var1, BlockPos var2, BlockRotation var3, Random var4);

    public abstract BlockBox getBoundingBox(StructureManager var1, BlockPos var2, BlockRotation var3);

    public abstract boolean generate(StructureManager var1, IWorld var2, BlockPos var3, BlockRotation var4, BlockBox var5, Random var6);

    public abstract StructurePoolElementType getType();

    public void method_16756(IWorld iWorld, Structure.StructureBlockInfo structureBlockInfo, BlockPos blockPos, BlockRotation blockRotation, Random random, BlockBox blockBox) {
    }

    public StructurePoolElement setProjection(StructurePool.Projection projection) {
        this.projection = projection;
        return this;
    }

    public StructurePool.Projection getProjection() {
        StructurePool.Projection projection = this.projection;
        if (projection == null) {
            throw new IllegalStateException();
        }
        return projection;
    }

    protected abstract <T> Dynamic<T> method_16625(DynamicOps<T> var1);

    public <T> Dynamic<T> method_16755(DynamicOps<T> dynamicOps) {
        Object object = this.method_16625(dynamicOps).getValue();
        Object object2 = dynamicOps.mergeInto(object, dynamicOps.createString("element_type"), dynamicOps.createString(Registry.STRUCTURE_POOL_ELEMENT.getId(this.getType()).toString()));
        return new Dynamic(dynamicOps, dynamicOps.mergeInto(object2, dynamicOps.createString("projection"), dynamicOps.createString(this.projection.getId())));
    }

    public int method_19308() {
        return 1;
    }
}

