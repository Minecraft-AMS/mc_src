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
        new StructurePoolGenerator(startPoolId, size, pieceFactory, chunkGenerator, structureManager, pos, pieces, random);
    }

    static {
        REGISTRY.add(StructurePool.EMPTY);
    }

    public static interface PieceFactory {
        public PoolStructurePiece create(StructureManager var1, StructurePoolElement var2, BlockPos var3, int var4, BlockRotation var5, BlockBox var6);
    }

    static final class StructurePoolGenerator {
        private final int maxSize;
        private final PieceFactory pieceFactory;
        private final ChunkGenerator<?> chunkGenerator;
        private final StructureManager structureManager;
        private final List<StructurePiece> children;
        private final Random random;
        private final Deque<ShapedPoolStructurePiece> structurePieces = Queues.newArrayDeque();

        public StructurePoolGenerator(Identifier startingPool, int maxSize, PieceFactory pieceFactory, ChunkGenerator<?> chunkGenerator, StructureManager structureManager, BlockPos blockPos, List<StructurePiece> children, Random random) {
            this.maxSize = maxSize;
            this.pieceFactory = pieceFactory;
            this.chunkGenerator = chunkGenerator;
            this.structureManager = structureManager;
            this.children = children;
            this.random = random;
            BlockRotation blockRotation = BlockRotation.random(random);
            StructurePool structurePool = REGISTRY.get(startingPool);
            StructurePoolElement structurePoolElement = structurePool.getRandomElement(random);
            PoolStructurePiece poolStructurePiece = pieceFactory.create(structureManager, structurePoolElement, blockPos, structurePoolElement.method_19308(), blockRotation, structurePoolElement.getBoundingBox(structureManager, blockPos, blockRotation));
            BlockBox blockBox = poolStructurePiece.getBoundingBox();
            int i = (blockBox.maxX + blockBox.minX) / 2;
            int j = (blockBox.maxZ + blockBox.minZ) / 2;
            int k = chunkGenerator.method_20402(i, j, Heightmap.Type.WORLD_SURFACE_WG);
            poolStructurePiece.translate(0, k - (blockBox.minY + poolStructurePiece.getGroundLevelDelta()), 0);
            children.add(poolStructurePiece);
            if (maxSize <= 0) {
                return;
            }
            int l = 80;
            Box box = new Box(i - 80, k - 80, j - 80, i + 80 + 1, k + 80 + 1, j + 80 + 1);
            this.structurePieces.addLast(new ShapedPoolStructurePiece(poolStructurePiece, new AtomicReference<VoxelShape>(VoxelShapes.combineAndSimplify(VoxelShapes.cuboid(box), VoxelShapes.cuboid(Box.from(blockBox)), BooleanBiFunction.ONLY_FIRST)), k + 80, 0));
            while (!this.structurePieces.isEmpty()) {
                ShapedPoolStructurePiece shapedPoolStructurePiece = this.structurePieces.removeFirst();
                this.generatePiece(shapedPoolStructurePiece.piece, shapedPoolStructurePiece.pieceShape, shapedPoolStructurePiece.minY, shapedPoolStructurePiece.currentSize);
            }
        }

        private void generatePiece(PoolStructurePiece piece, AtomicReference<VoxelShape> pieceShape, int minY, int currentSize) {
            StructurePoolElement structurePoolElement = piece.getPoolElement();
            BlockPos blockPos = piece.getPos();
            BlockRotation blockRotation = piece.getRotation();
            StructurePool.Projection projection = structurePoolElement.getProjection();
            boolean bl = projection == StructurePool.Projection.RIGID;
            AtomicReference<VoxelShape> atomicReference = new AtomicReference<VoxelShape>();
            BlockBox blockBox = piece.getBoundingBox();
            int i = blockBox.minY;
            block0: for (Structure.StructureBlockInfo structureBlockInfo2 : structurePoolElement.getStructureBlockInfos(this.structureManager, blockPos, blockRotation, this.random)) {
                StructurePoolElement structurePoolElement2;
                int l;
                AtomicReference<Object> atomicReference2;
                Direction direction = structureBlockInfo2.state.get(JigsawBlock.FACING);
                BlockPos blockPos2 = structureBlockInfo2.pos;
                BlockPos blockPos3 = blockPos2.offset(direction);
                int j = blockPos2.getY() - i;
                int k = -1;
                StructurePool structurePool = REGISTRY.get(new Identifier(structureBlockInfo2.tag.getString("target_pool")));
                StructurePool structurePool2 = REGISTRY.get(structurePool.getTerminatorsId());
                if (structurePool == StructurePool.INVALID || structurePool.getElementCount() == 0 && structurePool != StructurePool.EMPTY) {
                    LOGGER.warn("Empty or none existent pool: {}", (Object)structureBlockInfo2.tag.getString("target_pool"));
                    continue;
                }
                boolean bl2 = blockBox.contains(blockPos3);
                if (bl2) {
                    atomicReference2 = atomicReference;
                    l = i;
                    if (atomicReference.get() == null) {
                        atomicReference.set(VoxelShapes.cuboid(Box.from(blockBox)));
                    }
                } else {
                    atomicReference2 = pieceShape;
                    l = minY;
                }
                ArrayList list = Lists.newArrayList();
                if (currentSize != this.maxSize) {
                    list.addAll(structurePool.getElementIndicesInRandomOrder(this.random));
                }
                list.addAll(structurePool2.getElementIndicesInRandomOrder(this.random));
                Iterator iterator = list.iterator();
                while (iterator.hasNext() && (structurePoolElement2 = (StructurePoolElement)iterator.next()) != EmptyPoolElement.INSTANCE) {
                    for (BlockRotation blockRotation2 : BlockRotation.randomRotationOrder(this.random)) {
                        List<Structure.StructureBlockInfo> list2 = structurePoolElement2.getStructureBlockInfos(this.structureManager, BlockPos.ORIGIN, blockRotation2, this.random);
                        BlockBox blockBox2 = structurePoolElement2.getBoundingBox(this.structureManager, BlockPos.ORIGIN, blockRotation2);
                        int m = blockBox2.getBlockCountY() > 16 ? 0 : list2.stream().mapToInt(structureBlockInfo -> {
                            if (!blockBox2.contains(structureBlockInfo.pos.offset(structureBlockInfo.state.get(JigsawBlock.FACING)))) {
                                return 0;
                            }
                            Identifier identifier = new Identifier(structureBlockInfo.tag.getString("target_pool"));
                            StructurePool structurePool = REGISTRY.get(identifier);
                            StructurePool structurePool2 = REGISTRY.get(structurePool.getTerminatorsId());
                            return Math.max(structurePool.method_19309(this.structureManager), structurePool2.method_19309(this.structureManager));
                        }).max().orElse(0);
                        for (Structure.StructureBlockInfo structureBlockInfo22 : list2) {
                            int u;
                            int s;
                            int q;
                            if (!JigsawBlock.attachmentMatches(structureBlockInfo2, structureBlockInfo22)) continue;
                            BlockPos blockPos4 = structureBlockInfo22.pos;
                            BlockPos blockPos5 = new BlockPos(blockPos3.getX() - blockPos4.getX(), blockPos3.getY() - blockPos4.getY(), blockPos3.getZ() - blockPos4.getZ());
                            BlockBox blockBox3 = structurePoolElement2.getBoundingBox(this.structureManager, blockPos5, blockRotation2);
                            int n = blockBox3.minY;
                            StructurePool.Projection projection2 = structurePoolElement2.getProjection();
                            boolean bl3 = projection2 == StructurePool.Projection.RIGID;
                            int o = blockPos4.getY();
                            int p = j - o + structureBlockInfo2.state.get(JigsawBlock.FACING).getOffsetY();
                            if (bl && bl3) {
                                q = i + p;
                            } else {
                                if (k == -1) {
                                    k = this.chunkGenerator.method_20402(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
                                }
                                q = k - o;
                            }
                            int r = q - n;
                            BlockBox blockBox4 = blockBox3.translated(0, r, 0);
                            BlockPos blockPos6 = blockPos5.add(0, r, 0);
                            if (m > 0) {
                                s = Math.max(m + 1, blockBox4.maxY - blockBox4.minY);
                                blockBox4.maxY = blockBox4.minY + s;
                            }
                            if (VoxelShapes.matchesAnywhere((VoxelShape)atomicReference2.get(), VoxelShapes.cuboid(Box.from(blockBox4).contract(0.25)), BooleanBiFunction.ONLY_SECOND)) continue;
                            atomicReference2.set(VoxelShapes.combine((VoxelShape)atomicReference2.get(), VoxelShapes.cuboid(Box.from(blockBox4)), BooleanBiFunction.ONLY_FIRST));
                            s = piece.getGroundLevelDelta();
                            int t = bl3 ? s - p : structurePoolElement2.method_19308();
                            PoolStructurePiece poolStructurePiece = this.pieceFactory.create(this.structureManager, structurePoolElement2, blockPos6, t, blockRotation2, blockBox4);
                            if (bl) {
                                u = i + j;
                            } else if (bl3) {
                                u = q + o;
                            } else {
                                if (k == -1) {
                                    k = this.chunkGenerator.method_20402(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
                                }
                                u = k + p / 2;
                            }
                            piece.addJunction(new JigsawJunction(blockPos3.getX(), u - j + s, blockPos3.getZ(), p, projection2));
                            poolStructurePiece.addJunction(new JigsawJunction(blockPos2.getX(), u - o + t, blockPos2.getZ(), -p, projection));
                            this.children.add(poolStructurePiece);
                            if (currentSize + 1 > this.maxSize) continue block0;
                            this.structurePieces.addLast(new ShapedPoolStructurePiece(poolStructurePiece, atomicReference2, l, currentSize + 1));
                            continue block0;
                        }
                    }
                }
            }
        }
    }

    static final class ShapedPoolStructurePiece {
        private final PoolStructurePiece piece;
        private final AtomicReference<VoxelShape> pieceShape;
        private final int minY;
        private final int currentSize;

        private ShapedPoolStructurePiece(PoolStructurePiece piece, AtomicReference<VoxelShape> pieceShape, int minY, int currentSize) {
            this.piece = piece;
            this.pieceShape = pieceShape;
            this.minY = minY;
            this.currentSize = currentSize;
        }
    }
}

