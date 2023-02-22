/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class OceanMonumentGenerator {

    static class class_3370
    implements class_3375 {
        private class_3370() {
        }

        @Override
        public boolean method_14769(class_3388 arg) {
            if (arg.field_14482[Direction.NORTH.getId()] && !arg.field_14487[Direction.NORTH.getId()].field_14485 && arg.field_14482[Direction.UP.getId()] && !arg.field_14487[Direction.UP.getId()].field_14485) {
                class_3388 lv = arg.field_14487[Direction.NORTH.getId()];
                return lv.field_14482[Direction.UP.getId()] && !lv.field_14487[Direction.UP.getId()].field_14485;
            }
            return false;
        }

        @Override
        public Piece method_14768(Direction direction, class_3388 arg, Random random) {
            arg.field_14485 = true;
            arg.field_14487[Direction.NORTH.getId()].field_14485 = true;
            arg.field_14487[Direction.UP.getId()].field_14485 = true;
            arg.field_14487[Direction.NORTH.getId()].field_14487[Direction.UP.getId()].field_14485 = true;
            return new DoubleYZRoom(direction, arg);
        }
    }

    static class class_3368
    implements class_3375 {
        private class_3368() {
        }

        @Override
        public boolean method_14769(class_3388 arg) {
            if (arg.field_14482[Direction.EAST.getId()] && !arg.field_14487[Direction.EAST.getId()].field_14485 && arg.field_14482[Direction.UP.getId()] && !arg.field_14487[Direction.UP.getId()].field_14485) {
                class_3388 lv = arg.field_14487[Direction.EAST.getId()];
                return lv.field_14482[Direction.UP.getId()] && !lv.field_14487[Direction.UP.getId()].field_14485;
            }
            return false;
        }

        @Override
        public Piece method_14768(Direction direction, class_3388 arg, Random random) {
            arg.field_14485 = true;
            arg.field_14487[Direction.EAST.getId()].field_14485 = true;
            arg.field_14487[Direction.UP.getId()].field_14485 = true;
            arg.field_14487[Direction.EAST.getId()].field_14487[Direction.UP.getId()].field_14485 = true;
            return new DoubleXYRoom(direction, arg);
        }
    }

    static class class_3371
    implements class_3375 {
        private class_3371() {
        }

        @Override
        public boolean method_14769(class_3388 arg) {
            return arg.field_14482[Direction.NORTH.getId()] && !arg.field_14487[Direction.NORTH.getId()].field_14485;
        }

        @Override
        public Piece method_14768(Direction direction, class_3388 arg, Random random) {
            class_3388 lv = arg;
            if (!arg.field_14482[Direction.NORTH.getId()] || arg.field_14487[Direction.NORTH.getId()].field_14485) {
                lv = arg.field_14487[Direction.SOUTH.getId()];
            }
            lv.field_14485 = true;
            lv.field_14487[Direction.NORTH.getId()].field_14485 = true;
            return new DoubleZRoom(direction, lv);
        }
    }

    static class class_3367
    implements class_3375 {
        private class_3367() {
        }

        @Override
        public boolean method_14769(class_3388 arg) {
            return arg.field_14482[Direction.EAST.getId()] && !arg.field_14487[Direction.EAST.getId()].field_14485;
        }

        @Override
        public Piece method_14768(Direction direction, class_3388 arg, Random random) {
            arg.field_14485 = true;
            arg.field_14487[Direction.EAST.getId()].field_14485 = true;
            return new DoubleXRoom(direction, arg);
        }
    }

    static class class_3369
    implements class_3375 {
        private class_3369() {
        }

        @Override
        public boolean method_14769(class_3388 arg) {
            return arg.field_14482[Direction.UP.getId()] && !arg.field_14487[Direction.UP.getId()].field_14485;
        }

        @Override
        public Piece method_14768(Direction direction, class_3388 arg, Random random) {
            arg.field_14485 = true;
            arg.field_14487[Direction.UP.getId()].field_14485 = true;
            return new DoubleYRoom(direction, arg);
        }
    }

    static class class_3373
    implements class_3375 {
        private class_3373() {
        }

        @Override
        public boolean method_14769(class_3388 arg) {
            return !arg.field_14482[Direction.WEST.getId()] && !arg.field_14482[Direction.EAST.getId()] && !arg.field_14482[Direction.NORTH.getId()] && !arg.field_14482[Direction.SOUTH.getId()] && !arg.field_14482[Direction.UP.getId()];
        }

        @Override
        public Piece method_14768(Direction direction, class_3388 arg, Random random) {
            arg.field_14485 = true;
            return new SimpleRoomTop(direction, arg);
        }
    }

    static class class_3372
    implements class_3375 {
        private class_3372() {
        }

        @Override
        public boolean method_14769(class_3388 arg) {
            return true;
        }

        @Override
        public Piece method_14768(Direction direction, class_3388 arg, Random random) {
            arg.field_14485 = true;
            return new SimpleRoom(direction, arg, random);
        }
    }

    static interface class_3375 {
        public boolean method_14769(class_3388 var1);

        public Piece method_14768(Direction var1, class_3388 var2, Random var3);
    }

    static class class_3388 {
        private final int field_14486;
        private final class_3388[] field_14487 = new class_3388[6];
        private final boolean[] field_14482 = new boolean[6];
        private boolean field_14485;
        private boolean field_14484;
        private int field_14483;

        public class_3388(int i) {
            this.field_14486 = i;
        }

        public void method_14786(Direction direction, class_3388 arg) {
            this.field_14487[direction.getId()] = arg;
            arg.field_14487[direction.getOpposite().getId()] = this;
        }

        public void method_14780() {
            for (int i = 0; i < 6; ++i) {
                this.field_14482[i] = this.field_14487[i] != null;
            }
        }

        public boolean method_14783(int i) {
            if (this.field_14484) {
                return true;
            }
            this.field_14483 = i;
            for (int j = 0; j < 6; ++j) {
                if (this.field_14487[j] == null || !this.field_14482[j] || this.field_14487[j].field_14483 == i || !this.field_14487[j].method_14783(i)) continue;
                return true;
            }
            return false;
        }

        public boolean method_14785() {
            return this.field_14486 >= 75;
        }

        public int method_14781() {
            int i = 0;
            for (int j = 0; j < 6; ++j) {
                if (!this.field_14482[j]) continue;
                ++i;
            }
            return i;
        }
    }

    public static class Penthouse
    extends Piece {
        public Penthouse(Direction direction, BlockBox blockBox) {
            super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, direction, blockBox);
        }

        public Penthouse(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, compoundTag);
        }

        @Override
        public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos) {
            int i;
            this.fillWithOutline(world, box, 2, -1, 2, 11, -1, 11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 0, -1, 0, 1, -1, 11, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 12, -1, 0, 13, -1, 11, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 2, -1, 0, 11, -1, 1, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 2, -1, 12, 11, -1, 13, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 0, 0, 0, 0, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 13, 0, 0, 13, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 0, 0, 12, 0, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 0, 13, 12, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            for (i = 2; i <= 11; i += 3) {
                this.addBlock(world, SEA_LANTERN, 0, 0, i, box);
                this.addBlock(world, SEA_LANTERN, 13, 0, i, box);
                this.addBlock(world, SEA_LANTERN, i, 0, 0, box);
            }
            this.fillWithOutline(world, box, 2, 0, 3, 4, 0, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 9, 0, 3, 11, 0, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 4, 0, 9, 9, 0, 11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.addBlock(world, PRISMARINE_BRICKS, 5, 0, 8, box);
            this.addBlock(world, PRISMARINE_BRICKS, 8, 0, 8, box);
            this.addBlock(world, PRISMARINE_BRICKS, 10, 0, 10, box);
            this.addBlock(world, PRISMARINE_BRICKS, 3, 0, 10, box);
            this.fillWithOutline(world, box, 3, 0, 3, 3, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, box, 10, 0, 3, 10, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, box, 6, 0, 10, 7, 0, 10, DARK_PRISMARINE, DARK_PRISMARINE, false);
            i = 3;
            for (int j = 0; j < 2; ++j) {
                for (int k = 2; k <= 8; k += 3) {
                    this.fillWithOutline(world, box, i, 0, k, i, 2, k, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                i = 10;
            }
            this.fillWithOutline(world, box, 5, 0, 10, 5, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 8, 0, 10, 8, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 6, -1, 7, 7, -1, 8, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.setAirAndWater(world, box, 6, -1, 3, 7, -1, 4);
            this.method_14772(world, box, 6, 1, 6);
            return true;
        }
    }

    public static class WingRoom
    extends Piece {
        private int field_14481;

        public WingRoom(Direction direction, BlockBox blockBox, int i) {
            super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, direction, blockBox);
            this.field_14481 = i & 1;
        }

        public WingRoom(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, compoundTag);
        }

        @Override
        public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos) {
            if (this.field_14481 == 0) {
                int i;
                for (i = 0; i < 4; ++i) {
                    this.fillWithOutline(world, box, 10 - i, 3 - i, 20 - i, 12 + i, 3 - i, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                this.fillWithOutline(world, box, 7, 0, 6, 15, 0, 16, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 6, 0, 6, 6, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 16, 0, 6, 16, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 7, 1, 7, 7, 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 15, 1, 7, 15, 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 7, 1, 6, 9, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 13, 1, 6, 15, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 8, 1, 7, 9, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 13, 1, 7, 14, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 9, 0, 5, 13, 0, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 10, 0, 7, 12, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline(world, box, 8, 0, 10, 8, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline(world, box, 14, 0, 10, 14, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);
                for (i = 18; i >= 7; i -= 3) {
                    this.addBlock(world, SEA_LANTERN, 6, 3, i, box);
                    this.addBlock(world, SEA_LANTERN, 16, 3, i, box);
                }
                this.addBlock(world, SEA_LANTERN, 10, 0, 10, box);
                this.addBlock(world, SEA_LANTERN, 12, 0, 10, box);
                this.addBlock(world, SEA_LANTERN, 10, 0, 12, box);
                this.addBlock(world, SEA_LANTERN, 12, 0, 12, box);
                this.addBlock(world, SEA_LANTERN, 8, 3, 6, box);
                this.addBlock(world, SEA_LANTERN, 14, 3, 6, box);
                this.addBlock(world, PRISMARINE_BRICKS, 4, 2, 4, box);
                this.addBlock(world, SEA_LANTERN, 4, 1, 4, box);
                this.addBlock(world, PRISMARINE_BRICKS, 4, 0, 4, box);
                this.addBlock(world, PRISMARINE_BRICKS, 18, 2, 4, box);
                this.addBlock(world, SEA_LANTERN, 18, 1, 4, box);
                this.addBlock(world, PRISMARINE_BRICKS, 18, 0, 4, box);
                this.addBlock(world, PRISMARINE_BRICKS, 4, 2, 18, box);
                this.addBlock(world, SEA_LANTERN, 4, 1, 18, box);
                this.addBlock(world, PRISMARINE_BRICKS, 4, 0, 18, box);
                this.addBlock(world, PRISMARINE_BRICKS, 18, 2, 18, box);
                this.addBlock(world, SEA_LANTERN, 18, 1, 18, box);
                this.addBlock(world, PRISMARINE_BRICKS, 18, 0, 18, box);
                this.addBlock(world, PRISMARINE_BRICKS, 9, 7, 20, box);
                this.addBlock(world, PRISMARINE_BRICKS, 13, 7, 20, box);
                this.fillWithOutline(world, box, 6, 0, 21, 7, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 15, 0, 21, 16, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.method_14772(world, box, 11, 2, 16);
            } else if (this.field_14481 == 1) {
                int l;
                this.fillWithOutline(world, box, 9, 3, 18, 13, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 9, 0, 18, 9, 2, 18, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 13, 0, 18, 13, 2, 18, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                int i = 9;
                int j = 20;
                int k = 5;
                for (l = 0; l < 2; ++l) {
                    this.addBlock(world, PRISMARINE_BRICKS, i, 6, 20, box);
                    this.addBlock(world, SEA_LANTERN, i, 5, 20, box);
                    this.addBlock(world, PRISMARINE_BRICKS, i, 4, 20, box);
                    i = 13;
                }
                this.fillWithOutline(world, box, 7, 3, 7, 15, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                i = 10;
                for (l = 0; l < 2; ++l) {
                    this.fillWithOutline(world, box, i, 0, 10, i, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, i, 0, 12, i, 6, 12, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.addBlock(world, SEA_LANTERN, i, 0, 10, box);
                    this.addBlock(world, SEA_LANTERN, i, 0, 12, box);
                    this.addBlock(world, SEA_LANTERN, i, 4, 10, box);
                    this.addBlock(world, SEA_LANTERN, i, 4, 12, box);
                    i = 12;
                }
                i = 8;
                for (l = 0; l < 2; ++l) {
                    this.fillWithOutline(world, box, i, 0, 7, i, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, i, 0, 14, i, 2, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    i = 14;
                }
                this.fillWithOutline(world, box, 8, 3, 8, 8, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline(world, box, 14, 3, 8, 14, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.method_14772(world, box, 11, 5, 13);
            }
            return true;
        }
    }

    public static class CoreRoom
    extends Piece {
        public CoreRoom(Direction direction, class_3388 arg) {
            super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, 1, direction, arg, 2, 2, 2);
        }

        public CoreRoom(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, compoundTag);
        }

        @Override
        public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos) {
            this.method_14771(world, box, 1, 8, 0, 14, 8, 14, PRISMARINE);
            int i = 7;
            BlockState blockState = PRISMARINE_BRICKS;
            this.fillWithOutline(world, box, 0, 7, 0, 0, 7, 15, blockState, blockState, false);
            this.fillWithOutline(world, box, 15, 7, 0, 15, 7, 15, blockState, blockState, false);
            this.fillWithOutline(world, box, 1, 7, 0, 15, 7, 0, blockState, blockState, false);
            this.fillWithOutline(world, box, 1, 7, 15, 14, 7, 15, blockState, blockState, false);
            for (i = 1; i <= 6; ++i) {
                blockState = PRISMARINE_BRICKS;
                if (i == 2 || i == 6) {
                    blockState = PRISMARINE;
                }
                for (int j = 0; j <= 15; j += 15) {
                    this.fillWithOutline(world, box, j, i, 0, j, i, 1, blockState, blockState, false);
                    this.fillWithOutline(world, box, j, i, 6, j, i, 9, blockState, blockState, false);
                    this.fillWithOutline(world, box, j, i, 14, j, i, 15, blockState, blockState, false);
                }
                this.fillWithOutline(world, box, 1, i, 0, 1, i, 0, blockState, blockState, false);
                this.fillWithOutline(world, box, 6, i, 0, 9, i, 0, blockState, blockState, false);
                this.fillWithOutline(world, box, 14, i, 0, 14, i, 0, blockState, blockState, false);
                this.fillWithOutline(world, box, 1, i, 15, 14, i, 15, blockState, blockState, false);
            }
            this.fillWithOutline(world, box, 6, 3, 6, 9, 6, 9, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, box, 7, 4, 7, 8, 5, 8, Blocks.GOLD_BLOCK.getDefaultState(), Blocks.GOLD_BLOCK.getDefaultState(), false);
            for (i = 3; i <= 6; i += 3) {
                for (int k = 6; k <= 9; k += 3) {
                    this.addBlock(world, SEA_LANTERN, k, i, 6, box);
                    this.addBlock(world, SEA_LANTERN, k, i, 9, box);
                }
            }
            this.fillWithOutline(world, box, 5, 1, 6, 5, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 5, 1, 9, 5, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 10, 1, 6, 10, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 10, 1, 9, 10, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 6, 1, 5, 6, 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 9, 1, 5, 9, 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 6, 1, 10, 6, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 9, 1, 10, 9, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 5, 2, 5, 5, 6, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 5, 2, 10, 5, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 10, 2, 5, 10, 6, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 10, 2, 10, 10, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 5, 7, 1, 5, 7, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 10, 7, 1, 10, 7, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 5, 7, 9, 5, 7, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 10, 7, 9, 10, 7, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 7, 5, 6, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 7, 10, 6, 7, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 9, 7, 5, 14, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 9, 7, 10, 14, 7, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 2, 1, 2, 2, 1, 3, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 3, 1, 2, 3, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 13, 1, 2, 13, 1, 3, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 12, 1, 2, 12, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 2, 1, 12, 2, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 3, 1, 13, 3, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 13, 1, 12, 13, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 12, 1, 13, 12, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            return true;
        }
    }

    public static class DoubleYZRoom
    extends Piece {
        public DoubleYZRoom(Direction direction, class_3388 arg) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_Z_ROOM, 1, direction, arg, 1, 2, 2);
        }

        public DoubleYZRoom(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_Z_ROOM, compoundTag);
        }

        @Override
        public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos) {
            BlockState blockState;
            int i;
            class_3388 lv = this.field_14479.field_14487[Direction.NORTH.getId()];
            class_3388 lv2 = this.field_14479;
            class_3388 lv3 = lv.field_14487[Direction.UP.getId()];
            class_3388 lv4 = lv2.field_14487[Direction.UP.getId()];
            if (this.field_14479.field_14486 / 25 > 0) {
                this.method_14774(world, box, 0, 8, lv.field_14482[Direction.DOWN.getId()]);
                this.method_14774(world, box, 0, 0, lv2.field_14482[Direction.DOWN.getId()]);
            }
            if (lv4.field_14487[Direction.UP.getId()] == null) {
                this.method_14771(world, box, 1, 8, 1, 6, 8, 7, PRISMARINE);
            }
            if (lv3.field_14487[Direction.UP.getId()] == null) {
                this.method_14771(world, box, 1, 8, 8, 6, 8, 14, PRISMARINE);
            }
            for (i = 1; i <= 7; ++i) {
                blockState = PRISMARINE_BRICKS;
                if (i == 2 || i == 6) {
                    blockState = PRISMARINE;
                }
                this.fillWithOutline(world, box, 0, i, 0, 0, i, 15, blockState, blockState, false);
                this.fillWithOutline(world, box, 7, i, 0, 7, i, 15, blockState, blockState, false);
                this.fillWithOutline(world, box, 1, i, 0, 6, i, 0, blockState, blockState, false);
                this.fillWithOutline(world, box, 1, i, 15, 6, i, 15, blockState, blockState, false);
            }
            for (i = 1; i <= 7; ++i) {
                blockState = DARK_PRISMARINE;
                if (i == 2 || i == 6) {
                    blockState = SEA_LANTERN;
                }
                this.fillWithOutline(world, box, 3, i, 7, 4, i, 8, blockState, blockState, false);
            }
            if (lv2.field_14482[Direction.SOUTH.getId()]) {
                this.setAirAndWater(world, box, 3, 1, 0, 4, 2, 0);
            }
            if (lv2.field_14482[Direction.EAST.getId()]) {
                this.setAirAndWater(world, box, 7, 1, 3, 7, 2, 4);
            }
            if (lv2.field_14482[Direction.WEST.getId()]) {
                this.setAirAndWater(world, box, 0, 1, 3, 0, 2, 4);
            }
            if (lv.field_14482[Direction.NORTH.getId()]) {
                this.setAirAndWater(world, box, 3, 1, 15, 4, 2, 15);
            }
            if (lv.field_14482[Direction.WEST.getId()]) {
                this.setAirAndWater(world, box, 0, 1, 11, 0, 2, 12);
            }
            if (lv.field_14482[Direction.EAST.getId()]) {
                this.setAirAndWater(world, box, 7, 1, 11, 7, 2, 12);
            }
            if (lv4.field_14482[Direction.SOUTH.getId()]) {
                this.setAirAndWater(world, box, 3, 5, 0, 4, 6, 0);
            }
            if (lv4.field_14482[Direction.EAST.getId()]) {
                this.setAirAndWater(world, box, 7, 5, 3, 7, 6, 4);
                this.fillWithOutline(world, box, 5, 4, 2, 6, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 6, 1, 2, 6, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 6, 1, 5, 6, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            if (lv4.field_14482[Direction.WEST.getId()]) {
                this.setAirAndWater(world, box, 0, 5, 3, 0, 6, 4);
                this.fillWithOutline(world, box, 1, 4, 2, 2, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 1, 1, 2, 1, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 1, 1, 5, 1, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            if (lv3.field_14482[Direction.NORTH.getId()]) {
                this.setAirAndWater(world, box, 3, 5, 15, 4, 6, 15);
            }
            if (lv3.field_14482[Direction.WEST.getId()]) {
                this.setAirAndWater(world, box, 0, 5, 11, 0, 6, 12);
                this.fillWithOutline(world, box, 1, 4, 10, 2, 4, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 1, 1, 10, 1, 3, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 1, 1, 13, 1, 3, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            if (lv3.field_14482[Direction.EAST.getId()]) {
                this.setAirAndWater(world, box, 7, 5, 11, 7, 6, 12);
                this.fillWithOutline(world, box, 5, 4, 10, 6, 4, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 6, 1, 10, 6, 3, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 6, 1, 13, 6, 3, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            return true;
        }
    }

    public static class DoubleXYRoom
    extends Piece {
        public DoubleXYRoom(Direction direction, class_3388 arg) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_Y_ROOM, 1, direction, arg, 2, 2, 1);
        }

        public DoubleXYRoom(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_Y_ROOM, compoundTag);
        }

        @Override
        public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos) {
            class_3388 lv = this.field_14479.field_14487[Direction.EAST.getId()];
            class_3388 lv2 = this.field_14479;
            class_3388 lv3 = lv2.field_14487[Direction.UP.getId()];
            class_3388 lv4 = lv.field_14487[Direction.UP.getId()];
            if (this.field_14479.field_14486 / 25 > 0) {
                this.method_14774(world, box, 8, 0, lv.field_14482[Direction.DOWN.getId()]);
                this.method_14774(world, box, 0, 0, lv2.field_14482[Direction.DOWN.getId()]);
            }
            if (lv3.field_14487[Direction.UP.getId()] == null) {
                this.method_14771(world, box, 1, 8, 1, 7, 8, 6, PRISMARINE);
            }
            if (lv4.field_14487[Direction.UP.getId()] == null) {
                this.method_14771(world, box, 8, 8, 1, 14, 8, 6, PRISMARINE);
            }
            for (int i = 1; i <= 7; ++i) {
                BlockState blockState = PRISMARINE_BRICKS;
                if (i == 2 || i == 6) {
                    blockState = PRISMARINE;
                }
                this.fillWithOutline(world, box, 0, i, 0, 0, i, 7, blockState, blockState, false);
                this.fillWithOutline(world, box, 15, i, 0, 15, i, 7, blockState, blockState, false);
                this.fillWithOutline(world, box, 1, i, 0, 15, i, 0, blockState, blockState, false);
                this.fillWithOutline(world, box, 1, i, 7, 14, i, 7, blockState, blockState, false);
            }
            this.fillWithOutline(world, box, 2, 1, 3, 2, 7, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 3, 1, 2, 4, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 3, 1, 5, 4, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 13, 1, 3, 13, 7, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 11, 1, 2, 12, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 11, 1, 5, 12, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 5, 1, 3, 5, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 10, 1, 3, 10, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 5, 7, 2, 10, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 5, 5, 2, 5, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 10, 5, 2, 10, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 5, 5, 5, 5, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 10, 5, 5, 10, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.addBlock(world, PRISMARINE_BRICKS, 6, 6, 2, box);
            this.addBlock(world, PRISMARINE_BRICKS, 9, 6, 2, box);
            this.addBlock(world, PRISMARINE_BRICKS, 6, 6, 5, box);
            this.addBlock(world, PRISMARINE_BRICKS, 9, 6, 5, box);
            this.fillWithOutline(world, box, 5, 4, 3, 6, 4, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 9, 4, 3, 10, 4, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.addBlock(world, SEA_LANTERN, 5, 4, 2, box);
            this.addBlock(world, SEA_LANTERN, 5, 4, 5, box);
            this.addBlock(world, SEA_LANTERN, 10, 4, 2, box);
            this.addBlock(world, SEA_LANTERN, 10, 4, 5, box);
            if (lv2.field_14482[Direction.SOUTH.getId()]) {
                this.setAirAndWater(world, box, 3, 1, 0, 4, 2, 0);
            }
            if (lv2.field_14482[Direction.NORTH.getId()]) {
                this.setAirAndWater(world, box, 3, 1, 7, 4, 2, 7);
            }
            if (lv2.field_14482[Direction.WEST.getId()]) {
                this.setAirAndWater(world, box, 0, 1, 3, 0, 2, 4);
            }
            if (lv.field_14482[Direction.SOUTH.getId()]) {
                this.setAirAndWater(world, box, 11, 1, 0, 12, 2, 0);
            }
            if (lv.field_14482[Direction.NORTH.getId()]) {
                this.setAirAndWater(world, box, 11, 1, 7, 12, 2, 7);
            }
            if (lv.field_14482[Direction.EAST.getId()]) {
                this.setAirAndWater(world, box, 15, 1, 3, 15, 2, 4);
            }
            if (lv3.field_14482[Direction.SOUTH.getId()]) {
                this.setAirAndWater(world, box, 3, 5, 0, 4, 6, 0);
            }
            if (lv3.field_14482[Direction.NORTH.getId()]) {
                this.setAirAndWater(world, box, 3, 5, 7, 4, 6, 7);
            }
            if (lv3.field_14482[Direction.WEST.getId()]) {
                this.setAirAndWater(world, box, 0, 5, 3, 0, 6, 4);
            }
            if (lv4.field_14482[Direction.SOUTH.getId()]) {
                this.setAirAndWater(world, box, 11, 5, 0, 12, 6, 0);
            }
            if (lv4.field_14482[Direction.NORTH.getId()]) {
                this.setAirAndWater(world, box, 11, 5, 7, 12, 6, 7);
            }
            if (lv4.field_14482[Direction.EAST.getId()]) {
                this.setAirAndWater(world, box, 15, 5, 3, 15, 6, 4);
            }
            return true;
        }
    }

    public static class DoubleZRoom
    extends Piece {
        public DoubleZRoom(Direction direction, class_3388 arg) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, 1, direction, arg, 1, 1, 2);
        }

        public DoubleZRoom(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, compoundTag);
        }

        @Override
        public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos) {
            class_3388 lv = this.field_14479.field_14487[Direction.NORTH.getId()];
            class_3388 lv2 = this.field_14479;
            if (this.field_14479.field_14486 / 25 > 0) {
                this.method_14774(world, box, 0, 8, lv.field_14482[Direction.DOWN.getId()]);
                this.method_14774(world, box, 0, 0, lv2.field_14482[Direction.DOWN.getId()]);
            }
            if (lv2.field_14487[Direction.UP.getId()] == null) {
                this.method_14771(world, box, 1, 4, 1, 6, 4, 7, PRISMARINE);
            }
            if (lv.field_14487[Direction.UP.getId()] == null) {
                this.method_14771(world, box, 1, 4, 8, 6, 4, 14, PRISMARINE);
            }
            this.fillWithOutline(world, box, 0, 3, 0, 0, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 7, 3, 0, 7, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 3, 0, 7, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 3, 15, 6, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 0, 2, 0, 0, 2, 15, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 7, 2, 0, 7, 2, 15, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 1, 2, 0, 7, 2, 0, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 1, 2, 15, 6, 2, 15, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 0, 1, 0, 0, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 7, 1, 0, 7, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 1, 0, 7, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 1, 15, 6, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 1, 1, 1, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 6, 1, 1, 6, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 3, 1, 1, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 6, 3, 1, 6, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 1, 13, 1, 1, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 6, 1, 13, 6, 1, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 3, 13, 1, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 6, 3, 13, 6, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 2, 1, 6, 2, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 5, 1, 6, 5, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 2, 1, 9, 2, 3, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 5, 1, 9, 5, 3, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 3, 2, 6, 4, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 3, 2, 9, 4, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 2, 2, 7, 2, 2, 8, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 5, 2, 7, 5, 2, 8, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.addBlock(world, SEA_LANTERN, 2, 2, 5, box);
            this.addBlock(world, SEA_LANTERN, 5, 2, 5, box);
            this.addBlock(world, SEA_LANTERN, 2, 2, 10, box);
            this.addBlock(world, SEA_LANTERN, 5, 2, 10, box);
            this.addBlock(world, PRISMARINE_BRICKS, 2, 3, 5, box);
            this.addBlock(world, PRISMARINE_BRICKS, 5, 3, 5, box);
            this.addBlock(world, PRISMARINE_BRICKS, 2, 3, 10, box);
            this.addBlock(world, PRISMARINE_BRICKS, 5, 3, 10, box);
            if (lv2.field_14482[Direction.SOUTH.getId()]) {
                this.setAirAndWater(world, box, 3, 1, 0, 4, 2, 0);
            }
            if (lv2.field_14482[Direction.EAST.getId()]) {
                this.setAirAndWater(world, box, 7, 1, 3, 7, 2, 4);
            }
            if (lv2.field_14482[Direction.WEST.getId()]) {
                this.setAirAndWater(world, box, 0, 1, 3, 0, 2, 4);
            }
            if (lv.field_14482[Direction.NORTH.getId()]) {
                this.setAirAndWater(world, box, 3, 1, 15, 4, 2, 15);
            }
            if (lv.field_14482[Direction.WEST.getId()]) {
                this.setAirAndWater(world, box, 0, 1, 11, 0, 2, 12);
            }
            if (lv.field_14482[Direction.EAST.getId()]) {
                this.setAirAndWater(world, box, 7, 1, 11, 7, 2, 12);
            }
            return true;
        }
    }

    public static class DoubleXRoom
    extends Piece {
        public DoubleXRoom(Direction direction, class_3388 arg) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, 1, direction, arg, 2, 1, 1);
        }

        public DoubleXRoom(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, compoundTag);
        }

        @Override
        public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos) {
            class_3388 lv = this.field_14479.field_14487[Direction.EAST.getId()];
            class_3388 lv2 = this.field_14479;
            if (this.field_14479.field_14486 / 25 > 0) {
                this.method_14774(world, box, 8, 0, lv.field_14482[Direction.DOWN.getId()]);
                this.method_14774(world, box, 0, 0, lv2.field_14482[Direction.DOWN.getId()]);
            }
            if (lv2.field_14487[Direction.UP.getId()] == null) {
                this.method_14771(world, box, 1, 4, 1, 7, 4, 6, PRISMARINE);
            }
            if (lv.field_14487[Direction.UP.getId()] == null) {
                this.method_14771(world, box, 8, 4, 1, 14, 4, 6, PRISMARINE);
            }
            this.fillWithOutline(world, box, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 15, 3, 0, 15, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 3, 0, 15, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 3, 7, 14, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 0, 2, 0, 0, 2, 7, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 15, 2, 0, 15, 2, 7, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 1, 2, 0, 15, 2, 0, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 1, 2, 7, 14, 2, 7, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 15, 1, 0, 15, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 1, 0, 15, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 1, 7, 14, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 5, 1, 0, 10, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 6, 2, 0, 9, 2, 3, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 5, 3, 0, 10, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.addBlock(world, SEA_LANTERN, 6, 2, 3, box);
            this.addBlock(world, SEA_LANTERN, 9, 2, 3, box);
            if (lv2.field_14482[Direction.SOUTH.getId()]) {
                this.setAirAndWater(world, box, 3, 1, 0, 4, 2, 0);
            }
            if (lv2.field_14482[Direction.NORTH.getId()]) {
                this.setAirAndWater(world, box, 3, 1, 7, 4, 2, 7);
            }
            if (lv2.field_14482[Direction.WEST.getId()]) {
                this.setAirAndWater(world, box, 0, 1, 3, 0, 2, 4);
            }
            if (lv.field_14482[Direction.SOUTH.getId()]) {
                this.setAirAndWater(world, box, 11, 1, 0, 12, 2, 0);
            }
            if (lv.field_14482[Direction.NORTH.getId()]) {
                this.setAirAndWater(world, box, 11, 1, 7, 12, 2, 7);
            }
            if (lv.field_14482[Direction.EAST.getId()]) {
                this.setAirAndWater(world, box, 15, 1, 3, 15, 2, 4);
            }
            return true;
        }
    }

    public static class DoubleYRoom
    extends Piece {
        public DoubleYRoom(Direction direction, class_3388 arg) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, 1, direction, arg, 1, 2, 1);
        }

        public DoubleYRoom(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, compoundTag);
        }

        @Override
        public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos) {
            class_3388 lv;
            if (this.field_14479.field_14486 / 25 > 0) {
                this.method_14774(world, box, 0, 0, this.field_14479.field_14482[Direction.DOWN.getId()]);
            }
            if ((lv = this.field_14479.field_14487[Direction.UP.getId()]).field_14487[Direction.UP.getId()] == null) {
                this.method_14771(world, box, 1, 8, 1, 6, 8, 6, PRISMARINE);
            }
            this.fillWithOutline(world, box, 0, 4, 0, 0, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 7, 4, 0, 7, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 4, 0, 6, 4, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 4, 7, 6, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 2, 4, 1, 2, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 4, 2, 1, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 5, 4, 1, 5, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 6, 4, 2, 6, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 2, 4, 5, 2, 4, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 4, 5, 1, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 5, 4, 5, 5, 4, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 6, 4, 5, 6, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            class_3388 lv2 = this.field_14479;
            for (int i = 1; i <= 5; i += 4) {
                int j = 0;
                if (lv2.field_14482[Direction.SOUTH.getId()]) {
                    this.fillWithOutline(world, box, 2, i, j, 2, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, 5, i, j, 5, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, 3, i + 2, j, 4, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                } else {
                    this.fillWithOutline(world, box, 0, i, j, 7, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, 0, i + 1, j, 7, i + 1, j, PRISMARINE, PRISMARINE, false);
                }
                j = 7;
                if (lv2.field_14482[Direction.NORTH.getId()]) {
                    this.fillWithOutline(world, box, 2, i, j, 2, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, 5, i, j, 5, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, 3, i + 2, j, 4, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                } else {
                    this.fillWithOutline(world, box, 0, i, j, 7, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, 0, i + 1, j, 7, i + 1, j, PRISMARINE, PRISMARINE, false);
                }
                int k = 0;
                if (lv2.field_14482[Direction.WEST.getId()]) {
                    this.fillWithOutline(world, box, k, i, 2, k, i + 2, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, k, i, 5, k, i + 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, k, i + 2, 3, k, i + 2, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                } else {
                    this.fillWithOutline(world, box, k, i, 0, k, i + 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, k, i + 1, 0, k, i + 1, 7, PRISMARINE, PRISMARINE, false);
                }
                k = 7;
                if (lv2.field_14482[Direction.EAST.getId()]) {
                    this.fillWithOutline(world, box, k, i, 2, k, i + 2, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, k, i, 5, k, i + 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, k, i + 2, 3, k, i + 2, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                } else {
                    this.fillWithOutline(world, box, k, i, 0, k, i + 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, k, i + 1, 0, k, i + 1, 7, PRISMARINE, PRISMARINE, false);
                }
                lv2 = lv;
            }
            return true;
        }
    }

    public static class SimpleRoomTop
    extends Piece {
        public SimpleRoomTop(Direction direction, class_3388 arg) {
            super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, 1, direction, arg, 1, 1, 1);
        }

        public SimpleRoomTop(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, compoundTag);
        }

        @Override
        public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos) {
            if (this.field_14479.field_14486 / 25 > 0) {
                this.method_14774(world, box, 0, 0, this.field_14479.field_14482[Direction.DOWN.getId()]);
            }
            if (this.field_14479.field_14487[Direction.UP.getId()] == null) {
                this.method_14771(world, box, 1, 4, 1, 6, 4, 6, PRISMARINE);
            }
            for (int i = 1; i <= 6; ++i) {
                for (int j = 1; j <= 6; ++j) {
                    if (random.nextInt(3) == 0) continue;
                    int k = 2 + (random.nextInt(4) == 0 ? 0 : 1);
                    BlockState blockState = Blocks.WET_SPONGE.getDefaultState();
                    this.fillWithOutline(world, box, i, k, j, i, 3, j, blockState, blockState, false);
                }
            }
            this.fillWithOutline(world, box, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 0, 2, 0, 0, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, box, 7, 2, 0, 7, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, box, 1, 2, 0, 6, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, box, 1, 2, 7, 6, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, box, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 7, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 0, 1, 3, 0, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, box, 7, 1, 3, 7, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, box, 3, 1, 0, 4, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, box, 3, 1, 7, 4, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            if (this.field_14479.field_14482[Direction.SOUTH.getId()]) {
                this.setAirAndWater(world, box, 3, 1, 0, 4, 2, 0);
            }
            return true;
        }
    }

    public static class SimpleRoom
    extends Piece {
        private int field_14480;

        public SimpleRoom(Direction direction, class_3388 arg, Random random) {
            super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, 1, direction, arg, 1, 1, 1);
            this.field_14480 = random.nextInt(3);
        }

        public SimpleRoom(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, compoundTag);
        }

        @Override
        public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos) {
            boolean bl;
            if (this.field_14479.field_14486 / 25 > 0) {
                this.method_14774(world, box, 0, 0, this.field_14479.field_14482[Direction.DOWN.getId()]);
            }
            if (this.field_14479.field_14487[Direction.UP.getId()] == null) {
                this.method_14771(world, box, 1, 4, 1, 6, 4, 6, PRISMARINE);
            }
            boolean bl2 = bl = this.field_14480 != 0 && random.nextBoolean() && !this.field_14479.field_14482[Direction.DOWN.getId()] && !this.field_14479.field_14482[Direction.UP.getId()] && this.field_14479.method_14781() > 1;
            if (this.field_14480 == 0) {
                this.fillWithOutline(world, box, 0, 1, 0, 2, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 0, 3, 0, 2, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 0, 2, 0, 0, 2, 2, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(world, box, 1, 2, 0, 2, 2, 0, PRISMARINE, PRISMARINE, false);
                this.addBlock(world, SEA_LANTERN, 1, 2, 1, box);
                this.fillWithOutline(world, box, 5, 1, 0, 7, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 5, 3, 0, 7, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 7, 2, 0, 7, 2, 2, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(world, box, 5, 2, 0, 6, 2, 0, PRISMARINE, PRISMARINE, false);
                this.addBlock(world, SEA_LANTERN, 6, 2, 1, box);
                this.fillWithOutline(world, box, 0, 1, 5, 2, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 0, 3, 5, 2, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 0, 2, 5, 0, 2, 7, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(world, box, 1, 2, 7, 2, 2, 7, PRISMARINE, PRISMARINE, false);
                this.addBlock(world, SEA_LANTERN, 1, 2, 6, box);
                this.fillWithOutline(world, box, 5, 1, 5, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 5, 3, 5, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 7, 2, 5, 7, 2, 7, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(world, box, 5, 2, 7, 6, 2, 7, PRISMARINE, PRISMARINE, false);
                this.addBlock(world, SEA_LANTERN, 6, 2, 6, box);
                if (this.field_14479.field_14482[Direction.SOUTH.getId()]) {
                    this.fillWithOutline(world, box, 3, 3, 0, 4, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                } else {
                    this.fillWithOutline(world, box, 3, 3, 0, 4, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, 3, 2, 0, 4, 2, 0, PRISMARINE, PRISMARINE, false);
                    this.fillWithOutline(world, box, 3, 1, 0, 4, 1, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                if (this.field_14479.field_14482[Direction.NORTH.getId()]) {
                    this.fillWithOutline(world, box, 3, 3, 7, 4, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                } else {
                    this.fillWithOutline(world, box, 3, 3, 6, 4, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, 3, 2, 7, 4, 2, 7, PRISMARINE, PRISMARINE, false);
                    this.fillWithOutline(world, box, 3, 1, 6, 4, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                if (this.field_14479.field_14482[Direction.WEST.getId()]) {
                    this.fillWithOutline(world, box, 0, 3, 3, 0, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                } else {
                    this.fillWithOutline(world, box, 0, 3, 3, 1, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, 0, 2, 3, 0, 2, 4, PRISMARINE, PRISMARINE, false);
                    this.fillWithOutline(world, box, 0, 1, 3, 1, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                if (this.field_14479.field_14482[Direction.EAST.getId()]) {
                    this.fillWithOutline(world, box, 7, 3, 3, 7, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                } else {
                    this.fillWithOutline(world, box, 6, 3, 3, 7, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, 7, 2, 3, 7, 2, 4, PRISMARINE, PRISMARINE, false);
                    this.fillWithOutline(world, box, 6, 1, 3, 7, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
            } else if (this.field_14480 == 1) {
                this.fillWithOutline(world, box, 2, 1, 2, 2, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 2, 1, 5, 2, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 5, 1, 5, 5, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 5, 1, 2, 5, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.addBlock(world, SEA_LANTERN, 2, 2, 2, box);
                this.addBlock(world, SEA_LANTERN, 2, 2, 5, box);
                this.addBlock(world, SEA_LANTERN, 5, 2, 5, box);
                this.addBlock(world, SEA_LANTERN, 5, 2, 2, box);
                this.fillWithOutline(world, box, 0, 1, 0, 1, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 0, 1, 1, 0, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 0, 1, 7, 1, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 0, 1, 6, 0, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 6, 1, 7, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 7, 1, 6, 7, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 6, 1, 0, 7, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 7, 1, 1, 7, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.addBlock(world, PRISMARINE, 1, 2, 0, box);
                this.addBlock(world, PRISMARINE, 0, 2, 1, box);
                this.addBlock(world, PRISMARINE, 1, 2, 7, box);
                this.addBlock(world, PRISMARINE, 0, 2, 6, box);
                this.addBlock(world, PRISMARINE, 6, 2, 7, box);
                this.addBlock(world, PRISMARINE, 7, 2, 6, box);
                this.addBlock(world, PRISMARINE, 6, 2, 0, box);
                this.addBlock(world, PRISMARINE, 7, 2, 1, box);
                if (!this.field_14479.field_14482[Direction.SOUTH.getId()]) {
                    this.fillWithOutline(world, box, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, 1, 2, 0, 6, 2, 0, PRISMARINE, PRISMARINE, false);
                    this.fillWithOutline(world, box, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                if (!this.field_14479.field_14482[Direction.NORTH.getId()]) {
                    this.fillWithOutline(world, box, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, 1, 2, 7, 6, 2, 7, PRISMARINE, PRISMARINE, false);
                    this.fillWithOutline(world, box, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                if (!this.field_14479.field_14482[Direction.WEST.getId()]) {
                    this.fillWithOutline(world, box, 0, 3, 1, 0, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, 0, 2, 1, 0, 2, 6, PRISMARINE, PRISMARINE, false);
                    this.fillWithOutline(world, box, 0, 1, 1, 0, 1, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                if (!this.field_14479.field_14482[Direction.EAST.getId()]) {
                    this.fillWithOutline(world, box, 7, 3, 1, 7, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(world, box, 7, 2, 1, 7, 2, 6, PRISMARINE, PRISMARINE, false);
                    this.fillWithOutline(world, box, 7, 1, 1, 7, 1, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
            } else if (this.field_14480 == 2) {
                this.fillWithOutline(world, box, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 0, 2, 0, 0, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline(world, box, 7, 2, 0, 7, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline(world, box, 1, 2, 0, 6, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline(world, box, 1, 2, 7, 6, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline(world, box, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 7, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 0, 1, 3, 0, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline(world, box, 7, 1, 3, 7, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline(world, box, 3, 1, 0, 4, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
                this.fillWithOutline(world, box, 3, 1, 7, 4, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
                if (this.field_14479.field_14482[Direction.SOUTH.getId()]) {
                    this.setAirAndWater(world, box, 3, 1, 0, 4, 2, 0);
                }
                if (this.field_14479.field_14482[Direction.NORTH.getId()]) {
                    this.setAirAndWater(world, box, 3, 1, 7, 4, 2, 7);
                }
                if (this.field_14479.field_14482[Direction.WEST.getId()]) {
                    this.setAirAndWater(world, box, 0, 1, 3, 0, 2, 4);
                }
                if (this.field_14479.field_14482[Direction.EAST.getId()]) {
                    this.setAirAndWater(world, box, 7, 1, 3, 7, 2, 4);
                }
            }
            if (bl) {
                this.fillWithOutline(world, box, 3, 1, 3, 4, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 3, 2, 3, 4, 2, 4, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(world, box, 3, 3, 3, 4, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            return true;
        }
    }

    public static class Entry
    extends Piece {
        public Entry(Direction direction, class_3388 arg) {
            super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, 1, direction, arg, 1, 1, 1);
        }

        public Entry(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, compoundTag);
        }

        @Override
        public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos) {
            this.fillWithOutline(world, box, 0, 3, 0, 2, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 5, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 0, 2, 0, 1, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 6, 2, 0, 7, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 0, 1, 7, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 1, 1, 0, 2, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 5, 1, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            if (this.field_14479.field_14482[Direction.NORTH.getId()]) {
                this.setAirAndWater(world, box, 3, 1, 7, 4, 2, 7);
            }
            if (this.field_14479.field_14482[Direction.WEST.getId()]) {
                this.setAirAndWater(world, box, 0, 1, 3, 1, 2, 4);
            }
            if (this.field_14479.field_14482[Direction.EAST.getId()]) {
                this.setAirAndWater(world, box, 6, 1, 3, 7, 2, 4);
            }
            return true;
        }
    }

    public static class Base
    extends Piece {
        private class_3388 field_14464;
        private class_3388 field_14466;
        private final List<Piece> field_14465 = Lists.newArrayList();

        public Base(Random random, int i, int j, Direction direction) {
            super(StructurePieceType.OCEAN_MONUMENT_BASE, 0);
            this.setOrientation(direction);
            Direction direction2 = this.getFacing();
            this.boundingBox = direction2.getAxis() == Direction.Axis.Z ? new BlockBox(i, 39, j, i + 58 - 1, 61, j + 58 - 1) : new BlockBox(i, 39, j, i + 58 - 1, 61, j + 58 - 1);
            List<class_3388> list = this.method_14760(random);
            this.field_14464.field_14485 = true;
            this.field_14465.add(new Entry(direction2, this.field_14464));
            this.field_14465.add(new CoreRoom(direction2, this.field_14466));
            ArrayList list2 = Lists.newArrayList();
            list2.add(new class_3368());
            list2.add(new class_3370());
            list2.add(new class_3371());
            list2.add(new class_3367());
            list2.add(new class_3369());
            list2.add(new class_3373());
            list2.add(new class_3372());
            block0: for (class_3388 lv : list) {
                if (lv.field_14485 || lv.method_14785()) continue;
                for (Object lv2 : list2) {
                    if (!lv2.method_14769(lv)) continue;
                    this.field_14465.add(lv2.method_14768(direction2, lv, random));
                    continue block0;
                }
            }
            int k = this.boundingBox.minY;
            int l = this.applyXTransform(9, 22);
            int m = this.applyZTransform(9, 22);
            for (Piece piece : this.field_14465) {
                piece.getBoundingBox().offset(l, k, m);
            }
            BlockBox blockBox = BlockBox.create(this.applyXTransform(1, 1), this.applyYTransform(1), this.applyZTransform(1, 1), this.applyXTransform(23, 21), this.applyYTransform(8), this.applyZTransform(23, 21));
            BlockBox blockBox2 = BlockBox.create(this.applyXTransform(34, 1), this.applyYTransform(1), this.applyZTransform(34, 1), this.applyXTransform(56, 21), this.applyYTransform(8), this.applyZTransform(56, 21));
            BlockBox blockBox3 = BlockBox.create(this.applyXTransform(22, 22), this.applyYTransform(13), this.applyZTransform(22, 22), this.applyXTransform(35, 35), this.applyYTransform(17), this.applyZTransform(35, 35));
            int n = random.nextInt();
            this.field_14465.add(new WingRoom(direction2, blockBox, n++));
            this.field_14465.add(new WingRoom(direction2, blockBox2, n++));
            this.field_14465.add(new Penthouse(direction2, blockBox3));
        }

        public Base(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_MONUMENT_BASE, compoundTag);
        }

        private List<class_3388> method_14760(Random random) {
            int o;
            int n;
            int m;
            int l;
            int k;
            int j;
            int i;
            class_3388[] lvs = new class_3388[75];
            for (i = 0; i < 5; ++i) {
                for (j = 0; j < 4; ++j) {
                    k = 0;
                    l = Base.method_14770(i, 0, j);
                    lvs[l] = new class_3388(l);
                }
            }
            for (i = 0; i < 5; ++i) {
                for (j = 0; j < 4; ++j) {
                    k = 1;
                    l = Base.method_14770(i, 1, j);
                    lvs[l] = new class_3388(l);
                }
            }
            for (i = 1; i < 4; ++i) {
                for (j = 0; j < 2; ++j) {
                    k = 2;
                    l = Base.method_14770(i, 2, j);
                    lvs[l] = new class_3388(l);
                }
            }
            this.field_14464 = lvs[field_14469];
            for (i = 0; i < 5; ++i) {
                for (j = 0; j < 5; ++j) {
                    for (k = 0; k < 3; ++k) {
                        l = Base.method_14770(i, k, j);
                        if (lvs[l] == null) continue;
                        for (Direction direction : Direction.values()) {
                            int p;
                            m = i + direction.getOffsetX();
                            n = k + direction.getOffsetY();
                            o = j + direction.getOffsetZ();
                            if (m < 0 || m >= 5 || o < 0 || o >= 5 || n < 0 || n >= 3 || lvs[p = Base.method_14770(m, n, o)] == null) continue;
                            if (o == j) {
                                lvs[l].method_14786(direction, lvs[p]);
                                continue;
                            }
                            lvs[l].method_14786(direction.getOpposite(), lvs[p]);
                        }
                    }
                }
            }
            class_3388 lv = new class_3388(1003);
            class_3388 lv2 = new class_3388(1001);
            class_3388 lv3 = new class_3388(1002);
            lvs[field_14468].method_14786(Direction.UP, lv);
            lvs[field_14478].method_14786(Direction.SOUTH, lv2);
            lvs[field_14477].method_14786(Direction.SOUTH, lv3);
            lv.field_14485 = true;
            lv2.field_14485 = true;
            lv3.field_14485 = true;
            this.field_14464.field_14484 = true;
            this.field_14466 = lvs[Base.method_14770(random.nextInt(4), 0, 2)];
            this.field_14466.field_14485 = true;
            this.field_14466.field_14487[Direction.EAST.getId()].field_14485 = true;
            this.field_14466.field_14487[Direction.NORTH.getId()].field_14485 = true;
            this.field_14466.field_14487[Direction.EAST.getId()].field_14487[Direction.NORTH.getId()].field_14485 = true;
            this.field_14466.field_14487[Direction.UP.getId()].field_14485 = true;
            this.field_14466.field_14487[Direction.EAST.getId()].field_14487[Direction.UP.getId()].field_14485 = true;
            this.field_14466.field_14487[Direction.NORTH.getId()].field_14487[Direction.UP.getId()].field_14485 = true;
            this.field_14466.field_14487[Direction.EAST.getId()].field_14487[Direction.NORTH.getId()].field_14487[Direction.UP.getId()].field_14485 = true;
            ArrayList list = Lists.newArrayList();
            for (class_3388 lv4 : lvs) {
                if (lv4 == null) continue;
                lv4.method_14780();
                list.add(lv4);
            }
            lv.method_14780();
            Collections.shuffle(list, random);
            int q = 1;
            for (class_3388 lv5 : list) {
                int r = 0;
                for (m = 0; r < 2 && m < 5; ++m) {
                    n = random.nextInt(6);
                    if (!lv5.field_14482[n]) continue;
                    o = Direction.byId(n).getOpposite().getId();
                    ((class_3388)lv5).field_14482[n] = false;
                    ((class_3388)((class_3388)lv5).field_14487[n]).field_14482[o] = false;
                    if (lv5.method_14783(q++) && lv5.field_14487[n].method_14783(q++)) {
                        ++r;
                        continue;
                    }
                    ((class_3388)lv5).field_14482[n] = true;
                    ((class_3388)((class_3388)lv5).field_14487[n]).field_14482[o] = true;
                }
            }
            list.add(lv);
            list.add(lv2);
            list.add(lv3);
            return list;
        }

        @Override
        public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos) {
            int j;
            int i = Math.max(world.getSeaLevel(), 64) - this.boundingBox.minY;
            this.setAirAndWater(world, box, 0, 0, 0, 58, i, 58);
            this.method_14761(false, 0, world, random, box);
            this.method_14761(true, 33, world, random, box);
            this.method_14763(world, random, box);
            this.method_14762(world, random, box);
            this.method_14765(world, random, box);
            this.method_14764(world, random, box);
            this.method_14766(world, random, box);
            this.method_14767(world, random, box);
            for (j = 0; j < 7; ++j) {
                int k = 0;
                while (k < 7) {
                    if (k == 0 && j == 3) {
                        k = 6;
                    }
                    int l = j * 9;
                    int m = k * 9;
                    for (int n = 0; n < 4; ++n) {
                        for (int o = 0; o < 4; ++o) {
                            this.addBlock(world, PRISMARINE_BRICKS, l + n, 0, m + o, box);
                            this.method_14936(world, PRISMARINE_BRICKS, l + n, -1, m + o, box);
                        }
                    }
                    if (j == 0 || j == 6) {
                        ++k;
                        continue;
                    }
                    k += 6;
                }
            }
            for (j = 0; j < 5; ++j) {
                this.setAirAndWater(world, box, -1 - j, 0 + j * 2, -1 - j, -1 - j, 23, 58 + j);
                this.setAirAndWater(world, box, 58 + j, 0 + j * 2, -1 - j, 58 + j, 23, 58 + j);
                this.setAirAndWater(world, box, 0 - j, 0 + j * 2, -1 - j, 57 + j, 23, -1 - j);
                this.setAirAndWater(world, box, 0 - j, 0 + j * 2, 58 + j, 57 + j, 23, 58 + j);
            }
            for (Piece piece : this.field_14465) {
                if (!piece.getBoundingBox().intersects(box)) continue;
                piece.generate(world, generator, random, box, pos);
            }
            return true;
        }

        private void method_14761(boolean bl, int i, IWorld iWorld, Random random, BlockBox blockBox) {
            int j = 24;
            if (this.method_14775(blockBox, i, 0, i + 23, 20)) {
                int m;
                int k;
                this.fillWithOutline(iWorld, blockBox, i + 0, 0, 0, i + 24, 0, 20, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(iWorld, blockBox, i + 0, 1, 0, i + 24, 10, 20);
                for (k = 0; k < 4; ++k) {
                    this.fillWithOutline(iWorld, blockBox, i + k, k + 1, k, i + k, k + 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, blockBox, i + k + 7, k + 5, k + 7, i + k + 7, k + 5, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, blockBox, i + 17 - k, k + 5, k + 7, i + 17 - k, k + 5, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, blockBox, i + 24 - k, k + 1, k, i + 24 - k, k + 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, blockBox, i + k + 1, k + 1, k, i + 23 - k, k + 1, k, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, blockBox, i + k + 8, k + 5, k + 7, i + 16 - k, k + 5, k + 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                this.fillWithOutline(iWorld, blockBox, i + 4, 4, 4, i + 6, 4, 20, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, i + 7, 4, 4, i + 17, 4, 6, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, i + 18, 4, 4, i + 20, 4, 20, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, i + 11, 8, 11, i + 13, 8, 20, PRISMARINE, PRISMARINE, false);
                this.addBlock(iWorld, field_14470, i + 12, 9, 12, blockBox);
                this.addBlock(iWorld, field_14470, i + 12, 9, 15, blockBox);
                this.addBlock(iWorld, field_14470, i + 12, 9, 18, blockBox);
                k = i + (bl ? 19 : 5);
                int l = i + (bl ? 5 : 19);
                for (m = 20; m >= 5; m -= 3) {
                    this.addBlock(iWorld, field_14470, k, 5, m, blockBox);
                }
                for (m = 19; m >= 7; m -= 3) {
                    this.addBlock(iWorld, field_14470, l, 5, m, blockBox);
                }
                for (m = 0; m < 4; ++m) {
                    int n = bl ? i + 24 - (17 - m * 3) : i + 17 - m * 3;
                    this.addBlock(iWorld, field_14470, n, 5, 5, blockBox);
                }
                this.addBlock(iWorld, field_14470, l, 5, 5, blockBox);
                this.fillWithOutline(iWorld, blockBox, i + 11, 1, 12, i + 13, 7, 12, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, i + 12, 1, 11, i + 12, 7, 13, PRISMARINE, PRISMARINE, false);
            }
        }

        private void method_14763(IWorld iWorld, Random random, BlockBox blockBox) {
            if (this.method_14775(blockBox, 22, 5, 35, 17)) {
                this.setAirAndWater(iWorld, blockBox, 25, 0, 0, 32, 8, 20);
                for (int i = 0; i < 4; ++i) {
                    this.fillWithOutline(iWorld, blockBox, 24, 2, 5 + i * 4, 24, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, blockBox, 22, 4, 5 + i * 4, 23, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.addBlock(iWorld, PRISMARINE_BRICKS, 25, 5, 5 + i * 4, blockBox);
                    this.addBlock(iWorld, PRISMARINE_BRICKS, 26, 6, 5 + i * 4, blockBox);
                    this.addBlock(iWorld, SEA_LANTERN, 26, 5, 5 + i * 4, blockBox);
                    this.fillWithOutline(iWorld, blockBox, 33, 2, 5 + i * 4, 33, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, blockBox, 34, 4, 5 + i * 4, 35, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.addBlock(iWorld, PRISMARINE_BRICKS, 32, 5, 5 + i * 4, blockBox);
                    this.addBlock(iWorld, PRISMARINE_BRICKS, 31, 6, 5 + i * 4, blockBox);
                    this.addBlock(iWorld, SEA_LANTERN, 31, 5, 5 + i * 4, blockBox);
                    this.fillWithOutline(iWorld, blockBox, 27, 6, 5 + i * 4, 30, 6, 5 + i * 4, PRISMARINE, PRISMARINE, false);
                }
            }
        }

        private void method_14762(IWorld iWorld, Random random, BlockBox blockBox) {
            if (this.method_14775(blockBox, 15, 20, 42, 21)) {
                int i;
                this.fillWithOutline(iWorld, blockBox, 15, 0, 21, 42, 0, 21, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(iWorld, blockBox, 26, 1, 21, 31, 3, 21);
                this.fillWithOutline(iWorld, blockBox, 21, 12, 21, 36, 12, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 17, 11, 21, 40, 11, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 16, 10, 21, 41, 10, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 15, 7, 21, 42, 9, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 16, 6, 21, 41, 6, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 17, 5, 21, 40, 5, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 21, 4, 21, 36, 4, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 22, 3, 21, 26, 3, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 31, 3, 21, 35, 3, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 23, 2, 21, 25, 2, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 32, 2, 21, 34, 2, 21, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 28, 4, 20, 29, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.addBlock(iWorld, PRISMARINE_BRICKS, 27, 3, 21, blockBox);
                this.addBlock(iWorld, PRISMARINE_BRICKS, 30, 3, 21, blockBox);
                this.addBlock(iWorld, PRISMARINE_BRICKS, 26, 2, 21, blockBox);
                this.addBlock(iWorld, PRISMARINE_BRICKS, 31, 2, 21, blockBox);
                this.addBlock(iWorld, PRISMARINE_BRICKS, 25, 1, 21, blockBox);
                this.addBlock(iWorld, PRISMARINE_BRICKS, 32, 1, 21, blockBox);
                for (i = 0; i < 7; ++i) {
                    this.addBlock(iWorld, DARK_PRISMARINE, 28 - i, 6 + i, 21, blockBox);
                    this.addBlock(iWorld, DARK_PRISMARINE, 29 + i, 6 + i, 21, blockBox);
                }
                for (i = 0; i < 4; ++i) {
                    this.addBlock(iWorld, DARK_PRISMARINE, 28 - i, 9 + i, 21, blockBox);
                    this.addBlock(iWorld, DARK_PRISMARINE, 29 + i, 9 + i, 21, blockBox);
                }
                this.addBlock(iWorld, DARK_PRISMARINE, 28, 12, 21, blockBox);
                this.addBlock(iWorld, DARK_PRISMARINE, 29, 12, 21, blockBox);
                for (i = 0; i < 3; ++i) {
                    this.addBlock(iWorld, DARK_PRISMARINE, 22 - i * 2, 8, 21, blockBox);
                    this.addBlock(iWorld, DARK_PRISMARINE, 22 - i * 2, 9, 21, blockBox);
                    this.addBlock(iWorld, DARK_PRISMARINE, 35 + i * 2, 8, 21, blockBox);
                    this.addBlock(iWorld, DARK_PRISMARINE, 35 + i * 2, 9, 21, blockBox);
                }
                this.setAirAndWater(iWorld, blockBox, 15, 13, 21, 42, 15, 21);
                this.setAirAndWater(iWorld, blockBox, 15, 1, 21, 15, 6, 21);
                this.setAirAndWater(iWorld, blockBox, 16, 1, 21, 16, 5, 21);
                this.setAirAndWater(iWorld, blockBox, 17, 1, 21, 20, 4, 21);
                this.setAirAndWater(iWorld, blockBox, 21, 1, 21, 21, 3, 21);
                this.setAirAndWater(iWorld, blockBox, 22, 1, 21, 22, 2, 21);
                this.setAirAndWater(iWorld, blockBox, 23, 1, 21, 24, 1, 21);
                this.setAirAndWater(iWorld, blockBox, 42, 1, 21, 42, 6, 21);
                this.setAirAndWater(iWorld, blockBox, 41, 1, 21, 41, 5, 21);
                this.setAirAndWater(iWorld, blockBox, 37, 1, 21, 40, 4, 21);
                this.setAirAndWater(iWorld, blockBox, 36, 1, 21, 36, 3, 21);
                this.setAirAndWater(iWorld, blockBox, 33, 1, 21, 34, 1, 21);
                this.setAirAndWater(iWorld, blockBox, 35, 1, 21, 35, 2, 21);
            }
        }

        private void method_14765(IWorld iWorld, Random random, BlockBox blockBox) {
            if (this.method_14775(blockBox, 21, 21, 36, 36)) {
                this.fillWithOutline(iWorld, blockBox, 21, 0, 22, 36, 0, 36, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(iWorld, blockBox, 21, 1, 22, 36, 23, 36);
                for (int i = 0; i < 4; ++i) {
                    this.fillWithOutline(iWorld, blockBox, 21 + i, 13 + i, 21 + i, 36 - i, 13 + i, 21 + i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, blockBox, 21 + i, 13 + i, 36 - i, 36 - i, 13 + i, 36 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, blockBox, 21 + i, 13 + i, 22 + i, 21 + i, 13 + i, 35 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                    this.fillWithOutline(iWorld, blockBox, 36 - i, 13 + i, 22 + i, 36 - i, 13 + i, 35 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                this.fillWithOutline(iWorld, blockBox, 25, 16, 25, 32, 16, 32, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 25, 17, 25, 25, 19, 25, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(iWorld, blockBox, 32, 17, 25, 32, 19, 25, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(iWorld, blockBox, 25, 17, 32, 25, 19, 32, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(iWorld, blockBox, 32, 17, 32, 32, 19, 32, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.addBlock(iWorld, PRISMARINE_BRICKS, 26, 20, 26, blockBox);
                this.addBlock(iWorld, PRISMARINE_BRICKS, 27, 21, 27, blockBox);
                this.addBlock(iWorld, SEA_LANTERN, 27, 20, 27, blockBox);
                this.addBlock(iWorld, PRISMARINE_BRICKS, 26, 20, 31, blockBox);
                this.addBlock(iWorld, PRISMARINE_BRICKS, 27, 21, 30, blockBox);
                this.addBlock(iWorld, SEA_LANTERN, 27, 20, 30, blockBox);
                this.addBlock(iWorld, PRISMARINE_BRICKS, 31, 20, 31, blockBox);
                this.addBlock(iWorld, PRISMARINE_BRICKS, 30, 21, 30, blockBox);
                this.addBlock(iWorld, SEA_LANTERN, 30, 20, 30, blockBox);
                this.addBlock(iWorld, PRISMARINE_BRICKS, 31, 20, 26, blockBox);
                this.addBlock(iWorld, PRISMARINE_BRICKS, 30, 21, 27, blockBox);
                this.addBlock(iWorld, SEA_LANTERN, 30, 20, 27, blockBox);
                this.fillWithOutline(iWorld, blockBox, 28, 21, 27, 29, 21, 27, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 27, 21, 28, 27, 21, 29, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 28, 21, 30, 29, 21, 30, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 30, 21, 28, 30, 21, 29, PRISMARINE, PRISMARINE, false);
            }
        }

        private void method_14764(IWorld iWorld, Random random, BlockBox blockBox) {
            int i;
            if (this.method_14775(blockBox, 0, 21, 6, 58)) {
                this.fillWithOutline(iWorld, blockBox, 0, 0, 21, 6, 0, 57, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(iWorld, blockBox, 0, 1, 21, 6, 7, 57);
                this.fillWithOutline(iWorld, blockBox, 4, 4, 21, 6, 4, 53, PRISMARINE, PRISMARINE, false);
                for (i = 0; i < 4; ++i) {
                    this.fillWithOutline(iWorld, blockBox, i, i + 1, 21, i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                for (i = 23; i < 53; i += 3) {
                    this.addBlock(iWorld, field_14470, 5, 5, i, blockBox);
                }
                this.addBlock(iWorld, field_14470, 5, 5, 52, blockBox);
                for (i = 0; i < 4; ++i) {
                    this.fillWithOutline(iWorld, blockBox, i, i + 1, 21, i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                this.fillWithOutline(iWorld, blockBox, 4, 1, 52, 6, 3, 52, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 5, 1, 51, 5, 3, 53, PRISMARINE, PRISMARINE, false);
            }
            if (this.method_14775(blockBox, 51, 21, 58, 58)) {
                this.fillWithOutline(iWorld, blockBox, 51, 0, 21, 57, 0, 57, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(iWorld, blockBox, 51, 1, 21, 57, 7, 57);
                this.fillWithOutline(iWorld, blockBox, 51, 4, 21, 53, 4, 53, PRISMARINE, PRISMARINE, false);
                for (i = 0; i < 4; ++i) {
                    this.fillWithOutline(iWorld, blockBox, 57 - i, i + 1, 21, 57 - i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                for (i = 23; i < 53; i += 3) {
                    this.addBlock(iWorld, field_14470, 52, 5, i, blockBox);
                }
                this.addBlock(iWorld, field_14470, 52, 5, 52, blockBox);
                this.fillWithOutline(iWorld, blockBox, 51, 1, 52, 53, 3, 52, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 52, 1, 51, 52, 3, 53, PRISMARINE, PRISMARINE, false);
            }
            if (this.method_14775(blockBox, 0, 51, 57, 57)) {
                this.fillWithOutline(iWorld, blockBox, 7, 0, 51, 50, 0, 57, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(iWorld, blockBox, 7, 1, 51, 50, 10, 57);
                for (i = 0; i < 4; ++i) {
                    this.fillWithOutline(iWorld, blockBox, i + 1, i + 1, 57 - i, 56 - i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
            }
        }

        private void method_14766(IWorld iWorld, Random random, BlockBox blockBox) {
            int i;
            if (this.method_14775(blockBox, 7, 21, 13, 50)) {
                this.fillWithOutline(iWorld, blockBox, 7, 0, 21, 13, 0, 50, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(iWorld, blockBox, 7, 1, 21, 13, 10, 50);
                this.fillWithOutline(iWorld, blockBox, 11, 8, 21, 13, 8, 53, PRISMARINE, PRISMARINE, false);
                for (i = 0; i < 4; ++i) {
                    this.fillWithOutline(iWorld, blockBox, i + 7, i + 5, 21, i + 7, i + 5, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                for (i = 21; i <= 45; i += 3) {
                    this.addBlock(iWorld, field_14470, 12, 9, i, blockBox);
                }
            }
            if (this.method_14775(blockBox, 44, 21, 50, 54)) {
                this.fillWithOutline(iWorld, blockBox, 44, 0, 21, 50, 0, 50, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(iWorld, blockBox, 44, 1, 21, 50, 10, 50);
                this.fillWithOutline(iWorld, blockBox, 44, 8, 21, 46, 8, 53, PRISMARINE, PRISMARINE, false);
                for (i = 0; i < 4; ++i) {
                    this.fillWithOutline(iWorld, blockBox, 50 - i, i + 5, 21, 50 - i, i + 5, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                for (i = 21; i <= 45; i += 3) {
                    this.addBlock(iWorld, field_14470, 45, 9, i, blockBox);
                }
            }
            if (this.method_14775(blockBox, 8, 44, 49, 54)) {
                this.fillWithOutline(iWorld, blockBox, 14, 0, 44, 43, 0, 50, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(iWorld, blockBox, 14, 1, 44, 43, 10, 50);
                for (i = 12; i <= 45; i += 3) {
                    this.addBlock(iWorld, field_14470, i, 9, 45, blockBox);
                    this.addBlock(iWorld, field_14470, i, 9, 52, blockBox);
                    if (i != 12 && i != 18 && i != 24 && i != 33 && i != 39 && i != 45) continue;
                    this.addBlock(iWorld, field_14470, i, 9, 47, blockBox);
                    this.addBlock(iWorld, field_14470, i, 9, 50, blockBox);
                    this.addBlock(iWorld, field_14470, i, 10, 45, blockBox);
                    this.addBlock(iWorld, field_14470, i, 10, 46, blockBox);
                    this.addBlock(iWorld, field_14470, i, 10, 51, blockBox);
                    this.addBlock(iWorld, field_14470, i, 10, 52, blockBox);
                    this.addBlock(iWorld, field_14470, i, 11, 47, blockBox);
                    this.addBlock(iWorld, field_14470, i, 11, 50, blockBox);
                    this.addBlock(iWorld, field_14470, i, 12, 48, blockBox);
                    this.addBlock(iWorld, field_14470, i, 12, 49, blockBox);
                }
                for (i = 0; i < 3; ++i) {
                    this.fillWithOutline(iWorld, blockBox, 8 + i, 5 + i, 54, 49 - i, 5 + i, 54, PRISMARINE, PRISMARINE, false);
                }
                this.fillWithOutline(iWorld, blockBox, 11, 8, 54, 46, 8, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(iWorld, blockBox, 14, 8, 44, 43, 8, 53, PRISMARINE, PRISMARINE, false);
            }
        }

        private void method_14767(IWorld iWorld, Random random, BlockBox blockBox) {
            int i;
            if (this.method_14775(blockBox, 14, 21, 20, 43)) {
                this.fillWithOutline(iWorld, blockBox, 14, 0, 21, 20, 0, 43, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(iWorld, blockBox, 14, 1, 22, 20, 14, 43);
                this.fillWithOutline(iWorld, blockBox, 18, 12, 22, 20, 12, 39, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 18, 12, 21, 20, 12, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                for (i = 0; i < 4; ++i) {
                    this.fillWithOutline(iWorld, blockBox, i + 14, i + 9, 21, i + 14, i + 9, 43 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                for (i = 23; i <= 39; i += 3) {
                    this.addBlock(iWorld, field_14470, 19, 13, i, blockBox);
                }
            }
            if (this.method_14775(blockBox, 37, 21, 43, 43)) {
                this.fillWithOutline(iWorld, blockBox, 37, 0, 21, 43, 0, 43, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(iWorld, blockBox, 37, 1, 22, 43, 14, 43);
                this.fillWithOutline(iWorld, blockBox, 37, 12, 22, 39, 12, 39, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, 37, 12, 21, 39, 12, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                for (i = 0; i < 4; ++i) {
                    this.fillWithOutline(iWorld, blockBox, 43 - i, i + 9, 21, 43 - i, i + 9, 43 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                for (i = 23; i <= 39; i += 3) {
                    this.addBlock(iWorld, field_14470, 38, 13, i, blockBox);
                }
            }
            if (this.method_14775(blockBox, 15, 37, 42, 43)) {
                this.fillWithOutline(iWorld, blockBox, 21, 0, 37, 36, 0, 43, PRISMARINE, PRISMARINE, false);
                this.setAirAndWater(iWorld, blockBox, 21, 1, 37, 36, 14, 43);
                this.fillWithOutline(iWorld, blockBox, 21, 12, 37, 36, 12, 39, PRISMARINE, PRISMARINE, false);
                for (i = 0; i < 4; ++i) {
                    this.fillWithOutline(iWorld, blockBox, 15 + i, i + 9, 43 - i, 42 - i, i + 9, 43 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                }
                for (i = 21; i <= 36; i += 3) {
                    this.addBlock(iWorld, field_14470, i, 13, 38, blockBox);
                }
            }
        }
    }

    public static abstract class Piece
    extends StructurePiece {
        protected static final BlockState PRISMARINE = Blocks.PRISMARINE.getDefaultState();
        protected static final BlockState PRISMARINE_BRICKS = Blocks.PRISMARINE_BRICKS.getDefaultState();
        protected static final BlockState DARK_PRISMARINE = Blocks.DARK_PRISMARINE.getDefaultState();
        protected static final BlockState field_14470 = PRISMARINE_BRICKS;
        protected static final BlockState SEA_LANTERN = Blocks.SEA_LANTERN.getDefaultState();
        protected static final BlockState WATER = Blocks.WATER.getDefaultState();
        protected static final Set<Block> ICE_BLOCKS = ImmutableSet.builder().add((Object)Blocks.ICE).add((Object)Blocks.PACKED_ICE).add((Object)Blocks.BLUE_ICE).add((Object)WATER.getBlock()).build();
        protected static final int field_14469 = Piece.method_14770(2, 0, 0);
        protected static final int field_14468 = Piece.method_14770(2, 2, 0);
        protected static final int field_14478 = Piece.method_14770(0, 1, 0);
        protected static final int field_14477 = Piece.method_14770(4, 1, 0);
        protected class_3388 field_14479;

        protected static final int method_14770(int i, int j, int k) {
            return j * 25 + k * 5 + i;
        }

        public Piece(StructurePieceType structurePieceType, int i) {
            super(structurePieceType, i);
        }

        public Piece(StructurePieceType structurePieceType, Direction direction, BlockBox blockBox) {
            super(structurePieceType, 1);
            this.setOrientation(direction);
            this.boundingBox = blockBox;
        }

        protected Piece(StructurePieceType structurePieceType, int i, Direction direction, class_3388 arg, int j, int k, int l) {
            super(structurePieceType, i);
            this.setOrientation(direction);
            this.field_14479 = arg;
            int m = arg.field_14486;
            int n = m % 5;
            int o = m / 5 % 5;
            int p = m / 25;
            this.boundingBox = direction == Direction.NORTH || direction == Direction.SOUTH ? new BlockBox(0, 0, 0, j * 8 - 1, k * 4 - 1, l * 8 - 1) : new BlockBox(0, 0, 0, l * 8 - 1, k * 4 - 1, j * 8 - 1);
            switch (direction) {
                case NORTH: {
                    this.boundingBox.offset(n * 8, p * 4, -(o + l) * 8 + 1);
                    break;
                }
                case SOUTH: {
                    this.boundingBox.offset(n * 8, p * 4, o * 8);
                    break;
                }
                case WEST: {
                    this.boundingBox.offset(-(o + l) * 8 + 1, p * 4, n * 8);
                    break;
                }
                default: {
                    this.boundingBox.offset(o * 8, p * 4, n * 8);
                }
            }
        }

        public Piece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
            super(structurePieceType, compoundTag);
        }

        @Override
        protected void toNbt(CompoundTag tag) {
        }

        protected void setAirAndWater(IWorld world, BlockBox blockBox, int x, int y, int z, int width, int height, int depth) {
            for (int i = y; i <= height; ++i) {
                for (int j = x; j <= width; ++j) {
                    for (int k = z; k <= depth; ++k) {
                        BlockState blockState = this.getBlockAt(world, j, i, k, blockBox);
                        if (ICE_BLOCKS.contains(blockState.getBlock())) continue;
                        if (this.applyYTransform(i) >= world.getSeaLevel() && blockState != WATER) {
                            this.addBlock(world, Blocks.AIR.getDefaultState(), j, i, k, blockBox);
                            continue;
                        }
                        this.addBlock(world, WATER, j, i, k, blockBox);
                    }
                }
            }
        }

        protected void method_14774(IWorld iWorld, BlockBox blockBox, int i, int j, boolean bl) {
            if (bl) {
                this.fillWithOutline(iWorld, blockBox, i + 0, 0, j + 0, i + 2, 0, j + 8 - 1, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, i + 5, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, i + 3, 0, j + 0, i + 4, 0, j + 2, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, i + 3, 0, j + 5, i + 4, 0, j + 8 - 1, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(iWorld, blockBox, i + 3, 0, j + 2, i + 4, 0, j + 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(iWorld, blockBox, i + 3, 0, j + 5, i + 4, 0, j + 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(iWorld, blockBox, i + 2, 0, j + 3, i + 2, 0, j + 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(iWorld, blockBox, i + 5, 0, j + 3, i + 5, 0, j + 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
                this.fillWithOutline(iWorld, blockBox, i + 0, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, PRISMARINE, PRISMARINE, false);
            }
        }

        protected void method_14771(IWorld iWorld, BlockBox blockBox, int i, int j, int k, int l, int m, int n, BlockState blockState) {
            for (int o = j; o <= m; ++o) {
                for (int p = i; p <= l; ++p) {
                    for (int q = k; q <= n; ++q) {
                        if (this.getBlockAt(iWorld, p, o, q, blockBox) != WATER) continue;
                        this.addBlock(iWorld, blockState, p, o, q, blockBox);
                    }
                }
            }
        }

        protected boolean method_14775(BlockBox blockBox, int i, int j, int k, int l) {
            int m = this.applyXTransform(i, j);
            int n = this.applyZTransform(i, j);
            int o = this.applyXTransform(k, l);
            int p = this.applyZTransform(k, l);
            return blockBox.intersectsXZ(Math.min(m, o), Math.min(n, p), Math.max(m, o), Math.max(n, p));
        }

        protected boolean method_14772(IWorld iWorld, BlockBox blockBox, int i, int j, int k) {
            int n;
            int m;
            int l = this.applyXTransform(i, k);
            if (blockBox.contains(new BlockPos(l, m = this.applyYTransform(j), n = this.applyZTransform(i, k)))) {
                ElderGuardianEntity elderGuardianEntity = EntityType.ELDER_GUARDIAN.create(iWorld.getWorld());
                elderGuardianEntity.heal(elderGuardianEntity.getMaximumHealth());
                elderGuardianEntity.refreshPositionAndAngles((double)l + 0.5, m, (double)n + 0.5, 0.0f, 0.0f);
                elderGuardianEntity.initialize(iWorld, iWorld.getLocalDifficulty(new BlockPos(elderGuardianEntity)), SpawnType.STRUCTURE, null, null);
                iWorld.spawnEntity(elderGuardianEntity);
                return true;
            }
            return false;
        }
    }
}

