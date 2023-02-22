/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.structure.pool;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.block.JigsawBlock;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureFeatures;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolRegistry;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructurePoolBasedGenerator {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final StructurePoolRegistry REGISTRY = new StructurePoolRegistry();

    public static void addPieces(Identifier startPoolId, int size, PieceFactory pieceFactory, ChunkGenerator<?> chunkGenerator, StructureManager structureManager, BlockPos pos, List<StructurePiece> pieces, Random random) {
        StructureFeatures.initialize();
        new class_4182(startPoolId, size, pieceFactory, chunkGenerator, structureManager, pos, pieces, random);
    }

    static {
        REGISTRY.add(StructurePool.EMPTY);
    }

    public static interface PieceFactory {
        public PoolStructurePiece create(StructureManager var1, StructurePoolElement var2, BlockPos var3, int var4, BlockRotation var5, BlockBox var6);
    }

    static final class class_4182 {
        private final int field_18700;
        private final PieceFactory field_18701;
        private final ChunkGenerator<?> field_18702;
        private final StructureManager field_18703;
        private final List<StructurePiece> field_18704;
        private final Random field_18705;
        private final Deque<class_4181> field_18706 = Queues.newArrayDeque();

        public class_4182(Identifier identifier, int i, PieceFactory pieceFactory, ChunkGenerator<?> chunkGenerator, StructureManager structureManager, BlockPos blockPos, List<StructurePiece> list, Random random) {
            this.field_18700 = i;
            this.field_18701 = pieceFactory;
            this.field_18702 = chunkGenerator;
            this.field_18703 = structureManager;
            this.field_18704 = list;
            this.field_18705 = random;
            BlockRotation blockRotation = BlockRotation.random(random);
            StructurePool structurePool = REGISTRY.get(identifier);
            StructurePoolElement structurePoolElement = structurePool.getRandomElement(random);
            PoolStructurePiece poolStructurePiece = pieceFactory.create(structureManager, structurePoolElement, blockPos, structurePoolElement.method_19308(), blockRotation, structurePoolElement.getBoundingBox(structureManager, blockPos, blockRotation));
            BlockBox blockBox = poolStructurePiece.getBoundingBox();
            int j = (blockBox.maxX + blockBox.minX) / 2;
            int k = (blockBox.maxZ + blockBox.minZ) / 2;
            int l = chunkGenerator.method_20402(j, k, Heightmap.Type.WORLD_SURFACE_WG);
            poolStructurePiece.translate(0, l - (blockBox.minY + poolStructurePiece.getGroundLevelDelta()), 0);
            list.add(poolStructurePiece);
            if (i <= 0) {
                return;
            }
            int m = 80;
            Box box = new Box(j - 80, l - 80, k - 80, j + 80 + 1, l + 80 + 1, k + 80 + 1);
            this.field_18706.addLast(new class_4181(poolStructurePiece, new AtomicReference<VoxelShape>(VoxelShapes.combineAndSimplify(VoxelShapes.cuboid(box), VoxelShapes.cuboid(Box.from(blockBox)), BooleanBiFunction.ONLY_FIRST)), l + 80, 0));
            while (!this.field_18706.isEmpty()) {
                class_4181 lv = this.field_18706.removeFirst();
                this.method_19306(lv.field_18696, lv.field_18697, lv.field_18698, lv.field_18699);
            }
        }

        private void method_19306(PoolStructurePiece poolStructurePiece, AtomicReference<VoxelShape> atomicReference, int i, int j) {
            StructurePoolElement structurePoolElement = poolStructurePiece.getPoolElement();
            BlockPos blockPos = poolStructurePiece.getPos();
            BlockRotation blockRotation = poolStructurePiece.getRotation();
            StructurePool.Projection projection = structurePoolElement.getProjection();
            boolean bl = projection == StructurePool.Projection.RIGID;
            AtomicReference<VoxelShape> atomicReference2 = new AtomicReference<VoxelShape>();
            BlockBox blockBox = poolStructurePiece.getBoundingBox();
            int k = blockBox.minY;
            block0: for (Structure.StructureBlockInfo structureBlockInfo2 : structurePoolElement.getStructureBlockInfos(this.field_18703, blockPos, blockRotation, this.field_18705)) {
                StructurePoolElement structurePoolElement2;
                int n;
                AtomicReference<Object> atomicReference3;
                Direction direction = structureBlockInfo2.state.get(JigsawBlock.FACING);
                BlockPos blockPos2 = structureBlockInfo2.pos;
                BlockPos blockPos3 = blockPos2.offset(direction);
                int l = blockPos2.getY() - k;
                int m = -1;
                StructurePool structurePool = REGISTRY.get(new Identifier(structureBlockInfo2.tag.getString("target_pool")));
                StructurePool structurePool2 = REGISTRY.get(structurePool.getTerminatorsId());
                if (structurePool == StructurePool.INVALID || structurePool.getElementCount() == 0 && structurePool != StructurePool.EMPTY) {
                    LOGGER.warn("Empty or none existent pool: {}", (Object)structureBlockInfo2.tag.getString("target_pool"));
                    continue;
                }
                boolean bl2 = blockBox.contains(blockPos3);
                if (bl2) {
                    atomicReference3 = atomicReference2;
                    n = k;
                    if (atomicReference2.get() == null) {
                        atomicReference2.set(VoxelShapes.cuboid(Box.from(blockBox)));
                    }
                } else {
                    atomicReference3 = atomicReference;
                    n = i;
                }
                ArrayList list = Lists.newArrayList();
                if (j != this.field_18700) {
                    list.addAll(structurePool.getElementIndicesInRandomOrder(this.field_18705));
                }
                list.addAll(structurePool2.getElementIndicesInRandomOrder(this.field_18705));
                Iterator iterator = list.iterator();
                while (iterator.hasNext() && (structurePoolElement2 = (StructurePoolElement)iterator.next()) != EmptyPoolElement.INSTANCE) {
                    for (BlockRotation blockRotation2 : BlockRotation.randomRotationOrder(this.field_18705)) {
                        List<Structure.StructureBlockInfo> list2 = structurePoolElement2.getStructureBlockInfos(this.field_18703, BlockPos.ORIGIN, blockRotation2, this.field_18705);
                        BlockBox blockBox2 = structurePoolElement2.getBoundingBox(this.field_18703, BlockPos.ORIGIN, blockRotation2);
                        int o = blockBox2.getBlockCountY() > 16 ? 0 : list2.stream().mapToInt(structureBlockInfo -> {
                            if (!blockBox2.contains(structureBlockInfo.pos.offset(structureBlockInfo.state.get(JigsawBlock.FACING)))) {
                                return 0;
                            }
                            Identifier identifier = new Identifier(structureBlockInfo.tag.getString("target_pool"));
                            StructurePool structurePool = REGISTRY.get(identifier);
                            StructurePool structurePool2 = REGISTRY.get(structurePool.getTerminatorsId());
                            return Math.max(structurePool.method_19309(this.field_18703), structurePool2.method_19309(this.field_18703));
                        }).max().orElse(0);
                        for (Structure.StructureBlockInfo structureBlockInfo22 : list2) {
                            int w;
                            int u;
                            int s;
                            if (!JigsawBlock.attachmentMatches(structureBlockInfo2, structureBlockInfo22)) continue;
                            BlockPos blockPos4 = structureBlockInfo22.pos;
                            BlockPos blockPos5 = new BlockPos(blockPos3.getX() - blockPos4.getX(), blockPos3.getY() - blockPos4.getY(), blockPos3.getZ() - blockPos4.getZ());
                            BlockBox blockBox3 = structurePoolElement2.getBoundingBox(this.field_18703, blockPos5, blockRotation2);
                            int p = blockBox3.minY;
                            StructurePool.Projection projection2 = structurePoolElement2.getProjection();
                            boolean bl3 = projection2 == StructurePool.Projection.RIGID;
                            int q = blockPos4.getY();
                            int r = l - q + structureBlockInfo2.state.get(JigsawBlock.FACING).getOffsetY();
                            if (bl && bl3) {
                                s = k + r;
                            } else {
                                if (m == -1) {
                                    m = this.field_18702.method_20402(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
                                }
                                s = m - q;
                            }
                            int t = s - p;
                            BlockBox blockBox4 = blockBox3.method_19311(0, t, 0);
                            BlockPos blockPos6 = blockPos5.add(0, t, 0);
                            if (o > 0) {
                                u = Math.max(o + 1, blockBox4.maxY - blockBox4.minY);
                                blockBox4.maxY = blockBox4.minY + u;
                            }
                            if (VoxelShapes.matchesAnywhere((VoxelShape)atomicReference3.get(), VoxelShapes.cuboid(Box.from(blockBox4).contract(0.25)), BooleanBiFunction.ONLY_SECOND)) continue;
                            atomicReference3.set(VoxelShapes.combine((VoxelShape)atomicReference3.get(), VoxelShapes.cuboid(Box.from(blockBox4)), BooleanBiFunction.ONLY_FIRST));
                            u = poolStructurePiece.getGroundLevelDelta();
                            int v = bl3 ? u - r : structurePoolElement2.method_19308();
                            PoolStructurePiece poolStructurePiece2 = this.field_18701.create(this.field_18703, structurePoolElement2, blockPos6, v, blockRotation2, blockBox4);
                            if (bl) {
                                w = k + l;
                            } else if (bl3) {
                                w = s + q;
                            } else {
                                if (m == -1) {
                                    m = this.field_18702.method_20402(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
                                }
                                w = m + r / 2;
                            }
                            poolStructurePiece.addJunction(new JigsawJunction(blockPos3.getX(), w - l + u, blockPos3.getZ(), r, projection2));
                            poolStructurePiece2.addJunction(new JigsawJunction(blockPos2.getX(), w - q + v, blockPos2.getZ(), -r, projection));
                            this.field_18704.add(poolStructurePiece2);
                            if (j + 1 > this.field_18700) continue block0;
                            this.field_18706.addLast(new class_4181(poolStructurePiece2, atomicReference3, n, j + 1));
                            continue block0;
                        }
                    }
                }
            }
        }
    }

    static final class class_4181 {
        private final PoolStructurePiece field_18696;
        private final AtomicReference<VoxelShape> field_18697;
        private final int field_18698;
        private final int field_18699;

        private class_4181(PoolStructurePiece poolStructurePiece, AtomicReference<VoxelShape> atomicReference, int i, int j) {
            this.field_18696 = poolStructurePiece;
            this.field_18697 = atomicReference;
            this.field_18698 = i;
            this.field_18699 = j;
        }
    }
}

