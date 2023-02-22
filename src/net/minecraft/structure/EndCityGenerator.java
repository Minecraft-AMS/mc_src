/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class EndCityGenerator {
    private static final StructurePlacementData PLACEMENT_DATA = new StructurePlacementData().setIgnoreEntities(true).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
    private static final StructurePlacementData IGNORE_AIR_PLACEMENT_DATA = new StructurePlacementData().setIgnoreEntities(true).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
    private static final Part BUILDING = new Part(){

        @Override
        public void init() {
        }

        @Override
        public boolean create(StructureManager manager, int depth, Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
            if (depth > 8) {
                return false;
            }
            BlockRotation blockRotation = root.placementData.getRotation();
            Piece piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, root, pos, "base_floor", blockRotation, true));
            int i = random.nextInt(3);
            if (i == 0) {
                piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 4, -1), "base_roof", blockRotation, true));
            } else if (i == 1) {
                piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 0, -1), "second_floor_2", blockRotation, false));
                piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 8, -1), "second_roof", blockRotation, false));
                EndCityGenerator.createPart(manager, SMALL_TOWER, depth + 1, piece, null, pieces, random);
            } else if (i == 2) {
                piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 0, -1), "second_floor_2", blockRotation, false));
                piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 4, -1), "third_floor_2", blockRotation, false));
                piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 8, -1), "third_roof", blockRotation, true));
                EndCityGenerator.createPart(manager, SMALL_TOWER, depth + 1, piece, null, pieces, random);
            }
            return true;
        }
    };
    private static final List<Pair<BlockRotation, BlockPos>> SMALL_TOWER_BRIDGE_ATTACHMENTS = Lists.newArrayList((Object[])new Pair[]{new Pair<BlockRotation, BlockPos>(BlockRotation.NONE, new BlockPos(1, -1, 0)), new Pair<BlockRotation, BlockPos>(BlockRotation.CLOCKWISE_90, new BlockPos(6, -1, 1)), new Pair<BlockRotation, BlockPos>(BlockRotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 5)), new Pair<BlockRotation, BlockPos>(BlockRotation.CLOCKWISE_180, new BlockPos(5, -1, 6))});
    private static final Part SMALL_TOWER = new Part(){

        @Override
        public void init() {
        }

        @Override
        public boolean create(StructureManager manager, int depth, Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
            BlockRotation blockRotation = root.placementData.getRotation();
            Piece piece = root;
            piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(3 + random.nextInt(2), -3, 3 + random.nextInt(2)), "tower_base", blockRotation, true));
            piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, 7, 0), "tower_piece", blockRotation, true));
            Piece piece2 = random.nextInt(3) == 0 ? piece : null;
            int i = 1 + random.nextInt(3);
            for (int j = 0; j < i; ++j) {
                piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, 4, 0), "tower_piece", blockRotation, true));
                if (j >= i - 1 || !random.nextBoolean()) continue;
                piece2 = piece;
            }
            if (piece2 != null) {
                for (Pair pair : SMALL_TOWER_BRIDGE_ATTACHMENTS) {
                    if (!random.nextBoolean()) continue;
                    Piece piece3 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece2, (BlockPos)pair.getRight(), "bridge_end", blockRotation.rotate((BlockRotation)((Object)pair.getLeft())), true));
                    EndCityGenerator.createPart(manager, BRIDGE_PIECE, depth + 1, piece3, null, pieces, random);
                }
                piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 4, -1), "tower_top", blockRotation, true));
            } else if (depth == 7) {
                piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 4, -1), "tower_top", blockRotation, true));
            } else {
                return EndCityGenerator.createPart(manager, FAT_TOWER, depth + 1, piece, null, pieces, random);
            }
            return true;
        }
    };
    private static final Part BRIDGE_PIECE = new Part(){
        public boolean shipGenerated;

        @Override
        public void init() {
            this.shipGenerated = false;
        }

        @Override
        public boolean create(StructureManager manager, int depth, Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
            BlockRotation blockRotation = root.placementData.getRotation();
            int i = random.nextInt(4) + 1;
            Piece piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, root, new BlockPos(0, 0, -4), "bridge_piece", blockRotation, true));
            piece.field_15316 = -1;
            int j = 0;
            for (int k = 0; k < i; ++k) {
                if (random.nextBoolean()) {
                    piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, j, -4), "bridge_piece", blockRotation, true));
                    j = 0;
                    continue;
                }
                piece = random.nextBoolean() ? EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, j, -4), "bridge_steep_stairs", blockRotation, true)) : EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, j, -8), "bridge_gentle_stairs", blockRotation, true));
                j = 4;
            }
            if (this.shipGenerated || random.nextInt(10 - depth) != 0) {
                if (!EndCityGenerator.createPart(manager, BUILDING, depth + 1, piece, new BlockPos(-3, j + 1, -11), pieces, random)) {
                    return false;
                }
            } else {
                EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-8 + random.nextInt(8), j, -70 + random.nextInt(10)), "ship", blockRotation, true));
                this.shipGenerated = true;
            }
            piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(4, j, 0), "bridge_end", blockRotation.rotate(BlockRotation.CLOCKWISE_180), true));
            piece.field_15316 = -1;
            return true;
        }
    };
    private static final List<Pair<BlockRotation, BlockPos>> FAT_TOWER_BRIDGE_ATTACHMENTS = Lists.newArrayList((Object[])new Pair[]{new Pair<BlockRotation, BlockPos>(BlockRotation.NONE, new BlockPos(4, -1, 0)), new Pair<BlockRotation, BlockPos>(BlockRotation.CLOCKWISE_90, new BlockPos(12, -1, 4)), new Pair<BlockRotation, BlockPos>(BlockRotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 8)), new Pair<BlockRotation, BlockPos>(BlockRotation.CLOCKWISE_180, new BlockPos(8, -1, 12))});
    private static final Part FAT_TOWER = new Part(){

        @Override
        public void init() {
        }

        @Override
        public boolean create(StructureManager manager, int depth, Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
            BlockRotation blockRotation = root.placementData.getRotation();
            Piece piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, root, new BlockPos(-3, 4, -3), "fat_tower_base", blockRotation, true));
            piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, 4, 0), "fat_tower_middle", blockRotation, true));
            for (int i = 0; i < 2 && random.nextInt(3) != 0; ++i) {
                piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, 8, 0), "fat_tower_middle", blockRotation, true));
                for (Pair pair : FAT_TOWER_BRIDGE_ATTACHMENTS) {
                    if (!random.nextBoolean()) continue;
                    Piece piece2 = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, (BlockPos)pair.getRight(), "bridge_end", blockRotation.rotate((BlockRotation)((Object)pair.getLeft())), true));
                    EndCityGenerator.createPart(manager, BRIDGE_PIECE, depth + 1, piece2, null, pieces, random);
                }
            }
            piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-2, 8, -2), "fat_tower_top", blockRotation, true));
            return true;
        }
    };

    private static Piece createPiece(StructureManager structureManager, Piece lastPiece, BlockPos relativePosition, String template, BlockRotation rotation, boolean ignoreAir) {
        Piece piece = new Piece(structureManager, template, lastPiece.pos, rotation, ignoreAir);
        BlockPos blockPos = lastPiece.structure.method_15180(lastPiece.placementData, relativePosition, piece.placementData, BlockPos.ORIGIN);
        piece.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        return piece;
    }

    public static void addPieces(StructureManager structureManager, BlockPos pos, BlockRotation rotation, List<StructurePiece> pieces, Random random) {
        FAT_TOWER.init();
        BUILDING.init();
        BRIDGE_PIECE.init();
        SMALL_TOWER.init();
        Piece piece = EndCityGenerator.addPiece(pieces, new Piece(structureManager, "base_floor", pos, rotation, true));
        piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(structureManager, piece, new BlockPos(-1, 0, -1), "second_floor_1", rotation, false));
        piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(structureManager, piece, new BlockPos(-1, 4, -1), "third_floor_1", rotation, false));
        piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(structureManager, piece, new BlockPos(-1, 8, -1), "third_roof", rotation, true));
        EndCityGenerator.createPart(structureManager, SMALL_TOWER, 1, piece, null, pieces, random);
    }

    private static Piece addPiece(List<StructurePiece> pieces, Piece piece) {
        pieces.add(piece);
        return piece;
    }

    private static boolean createPart(StructureManager manager, Part piece, int depth, Piece parent, BlockPos pos, List<StructurePiece> pieces, Random random) {
        if (depth > 8) {
            return false;
        }
        ArrayList list = Lists.newArrayList();
        if (piece.create(manager, depth, parent, pos, list, random)) {
            boolean bl = false;
            int i = random.nextInt();
            for (StructurePiece structurePiece : list) {
                structurePiece.field_15316 = i;
                StructurePiece structurePiece2 = StructurePiece.method_14932(pieces, structurePiece.getBoundingBox());
                if (structurePiece2 == null || structurePiece2.field_15316 == parent.field_15316) continue;
                bl = true;
                break;
            }
            if (!bl) {
                pieces.addAll(list);
                return true;
            }
        }
        return false;
    }

    static interface Part {
        public void init();

        public boolean create(StructureManager var1, int var2, Piece var3, BlockPos var4, List<StructurePiece> var5, Random var6);
    }

    public static class Piece
    extends SimpleStructurePiece {
        private final String template;
        private final BlockRotation rotation;
        private final boolean ignoreAir;

        public Piece(StructureManager manager, String template, BlockPos pos, BlockRotation rotation, boolean ignoreAir) {
            super(StructurePieceType.END_CITY, 0);
            this.template = template;
            this.pos = pos;
            this.rotation = rotation;
            this.ignoreAir = ignoreAir;
            this.initializeStructureData(manager);
        }

        public Piece(StructureManager manager, CompoundTag tag) {
            super(StructurePieceType.END_CITY, tag);
            this.template = tag.getString("Template");
            this.rotation = BlockRotation.valueOf(tag.getString("Rot"));
            this.ignoreAir = tag.getBoolean("OW");
            this.initializeStructureData(manager);
        }

        private void initializeStructureData(StructureManager manager) {
            Structure structure = manager.getStructureOrBlank(new Identifier("end_city/" + this.template));
            StructurePlacementData structurePlacementData = (this.ignoreAir ? PLACEMENT_DATA : IGNORE_AIR_PLACEMENT_DATA).copy().setRotation(this.rotation);
            this.setStructureData(structure, this.pos, structurePlacementData);
        }

        @Override
        protected void toNbt(CompoundTag tag) {
            super.toNbt(tag);
            tag.putString("Template", this.template);
            tag.putString("Rot", this.rotation.name());
            tag.putBoolean("OW", this.ignoreAir);
        }

        @Override
        protected void handleMetadata(String metadata, BlockPos pos, IWorld world, Random random, BlockBox boundingBox) {
            if (metadata.startsWith("Chest")) {
                BlockPos blockPos = pos.down();
                if (boundingBox.contains(blockPos)) {
                    LootableContainerBlockEntity.setLootTable(world, random, blockPos, LootTables.END_CITY_TREASURE_CHEST);
                }
            } else if (metadata.startsWith("Sentry")) {
                ShulkerEntity shulkerEntity = EntityType.SHULKER.create(world.getWorld());
                shulkerEntity.updatePosition((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
                shulkerEntity.setAttachedBlock(pos);
                world.spawnEntity(shulkerEntity);
            } else if (metadata.startsWith("Elytra")) {
                ItemFrameEntity itemFrameEntity = new ItemFrameEntity(world.getWorld(), pos, this.rotation.rotate(Direction.SOUTH));
                itemFrameEntity.setHeldItemStack(new ItemStack(Items.ELYTRA), false);
                world.spawnEntity(itemFrameEntity);
            }
        }
    }
}
