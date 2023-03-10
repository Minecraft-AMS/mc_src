/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.structure.pool;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.PlacedFeature;

public class FeaturePoolElement
extends StructurePoolElement {
    public static final Codec<FeaturePoolElement> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)PlacedFeature.REGISTRY_CODEC.fieldOf("feature").forGetter(featurePoolElement -> featurePoolElement.feature), FeaturePoolElement.method_28883()).apply((Applicative)instance, FeaturePoolElement::new));
    private final RegistryEntry<PlacedFeature> feature;
    private final NbtCompound nbt;

    protected FeaturePoolElement(RegistryEntry<PlacedFeature> feature, StructurePool.Projection projection) {
        super(projection);
        this.feature = feature;
        this.nbt = this.createDefaultJigsawNbt();
    }

    private NbtCompound createDefaultJigsawNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("name", "minecraft:bottom");
        nbtCompound.putString("final_state", "minecraft:air");
        nbtCompound.putString("pool", "minecraft:empty");
        nbtCompound.putString("target", "minecraft:empty");
        nbtCompound.putString("joint", JigsawBlockEntity.Joint.ROLLABLE.asString());
        return nbtCompound;
    }

    @Override
    public Vec3i getStart(StructureManager structureManager, BlockRotation rotation) {
        return Vec3i.ZERO;
    }

    @Override
    public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random) {
        ArrayList list = Lists.newArrayList();
        list.add(new Structure.StructureBlockInfo(pos, (BlockState)Blocks.JIGSAW.getDefaultState().with(JigsawBlock.ORIENTATION, JigsawOrientation.byDirections(Direction.DOWN, Direction.SOUTH)), this.nbt));
        return list;
    }

    @Override
    public BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
        Vec3i vec3i = this.getStart(structureManager, rotation);
        return new BlockBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + vec3i.getX(), pos.getY() + vec3i.getY(), pos.getZ() + vec3i.getZ());
    }

    @Override
    public boolean generate(StructureManager structureManager, StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, BlockPos pos, BlockPos blockPos, BlockRotation rotation, BlockBox box, Random random, boolean keepJigsaws) {
        return this.feature.value().generateUnregistered(world, chunkGenerator, random, pos);
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return StructurePoolElementType.FEATURE_POOL_ELEMENT;
    }

    public String toString() {
        return "Feature[" + this.feature + "]";
    }
}

