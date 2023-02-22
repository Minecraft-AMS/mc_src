/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import org.jetbrains.annotations.Nullable;

public class WoodlandMansionGenerator {
    public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, List<Piece> pieces, Random random) {
        MansionParameters mansionParameters = new MansionParameters(random);
        LayoutGenerator layoutGenerator = new LayoutGenerator(manager, random);
        layoutGenerator.generate(pos, rotation, pieces, mansionParameters);
    }

    static class ThirdFloorRoomPool
    extends SecondFloorRoomPool {
        private ThirdFloorRoomPool() {
        }
    }

    static class SecondFloorRoomPool
    extends RoomPool {
        private SecondFloorRoomPool() {
        }

        @Override
        public String getSmallRoom(Random random) {
            return "1x1_b" + (random.nextInt(4) + 1);
        }

        @Override
        public String getSmallSecretRoom(Random random) {
            return "1x1_as" + (random.nextInt(4) + 1);
        }

        @Override
        public String getMediumFunctionalRoom(Random random, boolean staircase) {
            if (staircase) {
                return "1x2_c_stairs";
            }
            return "1x2_c" + (random.nextInt(4) + 1);
        }

        @Override
        public String getMediumGenericRoom(Random random, boolean staircase) {
            if (staircase) {
                return "1x2_d_stairs";
            }
            return "1x2_d" + (random.nextInt(5) + 1);
        }

        @Override
        public String getMediumSecretRoom(Random random) {
            return "1x2_se" + (random.nextInt(1) + 1);
        }

        @Override
        public String getBigRoom(Random random) {
            return "2x2_b" + (random.nextInt(5) + 1);
        }

        @Override
        public String getBigSecretRoom(Random random) {
            return "2x2_s1";
        }
    }

    static class FirstFloorRoomPool
    extends RoomPool {
        private FirstFloorRoomPool() {
        }

        @Override
        public String getSmallRoom(Random random) {
            return "1x1_a" + (random.nextInt(5) + 1);
        }

        @Override
        public String getSmallSecretRoom(Random random) {
            return "1x1_as" + (random.nextInt(4) + 1);
        }

        @Override
        public String getMediumFunctionalRoom(Random random, boolean staircase) {
            return "1x2_a" + (random.nextInt(9) + 1);
        }

        @Override
        public String getMediumGenericRoom(Random random, boolean staircase) {
            return "1x2_b" + (random.nextInt(5) + 1);
        }

        @Override
        public String getMediumSecretRoom(Random random) {
            return "1x2_s" + (random.nextInt(2) + 1);
        }

        @Override
        public String getBigRoom(Random random) {
            return "2x2_a" + (random.nextInt(4) + 1);
        }

        @Override
        public String getBigSecretRoom(Random random) {
            return "2x2_s1";
        }
    }

    static abstract class RoomPool {
        private RoomPool() {
        }

        public abstract String getSmallRoom(Random var1);

        public abstract String getSmallSecretRoom(Random var1);

        public abstract String getMediumFunctionalRoom(Random var1, boolean var2);

        public abstract String getMediumGenericRoom(Random var1, boolean var2);

        public abstract String getMediumSecretRoom(Random var1);

        public abstract String getBigRoom(Random var1);

        public abstract String getBigSecretRoom(Random var1);
    }

    static class class_3478 {
        private final int[][] field_15451;
        private final int field_15454;
        private final int field_15453;
        private final int field_15452;

        public class_3478(int i, int j, int k) {
            this.field_15454 = i;
            this.field_15453 = j;
            this.field_15452 = k;
            this.field_15451 = new int[i][j];
        }

        public void method_15065(int i, int j, int k) {
            if (i >= 0 && i < this.field_15454 && j >= 0 && j < this.field_15453) {
                this.field_15451[i][j] = k;
            }
        }

        public void method_15062(int i, int j, int k, int l, int m) {
            for (int n = j; n <= l; ++n) {
                for (int o = i; o <= k; ++o) {
                    this.method_15065(o, n, m);
                }
            }
        }

        public int method_15066(int i, int j) {
            if (i >= 0 && i < this.field_15454 && j >= 0 && j < this.field_15453) {
                return this.field_15451[i][j];
            }
            return this.field_15452;
        }

        public void method_15061(int i, int j, int k, int l) {
            if (this.method_15066(i, j) == k) {
                this.method_15065(i, j, l);
            }
        }

        public boolean method_15067(int i, int j, int k) {
            return this.method_15066(i - 1, j) == k || this.method_15066(i + 1, j) == k || this.method_15066(i, j + 1) == k || this.method_15066(i, j - 1) == k;
        }
    }

    static class MansionParameters {
        private final Random random;
        private final class_3478 field_15440;
        private final class_3478 field_15439;
        private final class_3478[] field_15443;
        private final int field_15442;
        private final int field_15441;

        public MansionParameters(Random random) {
            this.random = random;
            int i = 11;
            this.field_15442 = 7;
            this.field_15441 = 4;
            this.field_15440 = new class_3478(11, 11, 5);
            this.field_15440.method_15062(this.field_15442, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 3);
            this.field_15440.method_15062(this.field_15442 - 1, this.field_15441, this.field_15442 - 1, this.field_15441 + 1, 2);
            this.field_15440.method_15062(this.field_15442 + 2, this.field_15441 - 2, this.field_15442 + 3, this.field_15441 + 3, 5);
            this.field_15440.method_15062(this.field_15442 + 1, this.field_15441 - 2, this.field_15442 + 1, this.field_15441 - 1, 1);
            this.field_15440.method_15062(this.field_15442 + 1, this.field_15441 + 2, this.field_15442 + 1, this.field_15441 + 3, 1);
            this.field_15440.method_15065(this.field_15442 - 1, this.field_15441 - 1, 1);
            this.field_15440.method_15065(this.field_15442 - 1, this.field_15441 + 2, 1);
            this.field_15440.method_15062(0, 0, 11, 1, 5);
            this.field_15440.method_15062(0, 9, 11, 11, 5);
            this.method_15045(this.field_15440, this.field_15442, this.field_15441 - 2, Direction.WEST, 6);
            this.method_15045(this.field_15440, this.field_15442, this.field_15441 + 3, Direction.WEST, 6);
            this.method_15045(this.field_15440, this.field_15442 - 2, this.field_15441 - 1, Direction.WEST, 3);
            this.method_15045(this.field_15440, this.field_15442 - 2, this.field_15441 + 2, Direction.WEST, 3);
            while (this.method_15046(this.field_15440)) {
            }
            this.field_15443 = new class_3478[3];
            this.field_15443[0] = new class_3478(11, 11, 5);
            this.field_15443[1] = new class_3478(11, 11, 5);
            this.field_15443[2] = new class_3478(11, 11, 5);
            this.method_15042(this.field_15440, this.field_15443[0]);
            this.method_15042(this.field_15440, this.field_15443[1]);
            this.field_15443[0].method_15062(this.field_15442 + 1, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 0x800000);
            this.field_15443[1].method_15062(this.field_15442 + 1, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 0x800000);
            this.field_15439 = new class_3478(this.field_15440.field_15454, this.field_15440.field_15453, 5);
            this.method_15048();
            this.method_15042(this.field_15439, this.field_15443[2]);
        }

        public static boolean method_15047(class_3478 arg, int i, int j) {
            int k = arg.method_15066(i, j);
            return k == 1 || k == 2 || k == 3 || k == 4;
        }

        public boolean method_15039(class_3478 arg, int i, int j, int k, int l) {
            return (this.field_15443[k].method_15066(i, j) & 0xFFFF) == l;
        }

        @Nullable
        public Direction method_15040(class_3478 arg, int i, int j, int k, int l) {
            for (Direction direction : Direction.Type.HORIZONTAL) {
                if (!this.method_15039(arg, i + direction.getOffsetX(), j + direction.getOffsetZ(), k, l)) continue;
                return direction;
            }
            return null;
        }

        private void method_15045(class_3478 arg, int i, int j, Direction direction, int k) {
            Direction direction2;
            if (k <= 0) {
                return;
            }
            arg.method_15065(i, j, 1);
            arg.method_15061(i + direction.getOffsetX(), j + direction.getOffsetZ(), 0, 1);
            for (int l = 0; l < 8; ++l) {
                direction2 = Direction.fromHorizontal(this.random.nextInt(4));
                if (direction2 == direction.getOpposite() || direction2 == Direction.EAST && this.random.nextBoolean()) continue;
                int m = i + direction.getOffsetX();
                int n = j + direction.getOffsetZ();
                if (arg.method_15066(m + direction2.getOffsetX(), n + direction2.getOffsetZ()) != 0 || arg.method_15066(m + direction2.getOffsetX() * 2, n + direction2.getOffsetZ() * 2) != 0) continue;
                this.method_15045(arg, i + direction.getOffsetX() + direction2.getOffsetX(), j + direction.getOffsetZ() + direction2.getOffsetZ(), direction2, k - 1);
                break;
            }
            Direction direction3 = direction.rotateYClockwise();
            direction2 = direction.rotateYCounterclockwise();
            arg.method_15061(i + direction3.getOffsetX(), j + direction3.getOffsetZ(), 0, 2);
            arg.method_15061(i + direction2.getOffsetX(), j + direction2.getOffsetZ(), 0, 2);
            arg.method_15061(i + direction.getOffsetX() + direction3.getOffsetX(), j + direction.getOffsetZ() + direction3.getOffsetZ(), 0, 2);
            arg.method_15061(i + direction.getOffsetX() + direction2.getOffsetX(), j + direction.getOffsetZ() + direction2.getOffsetZ(), 0, 2);
            arg.method_15061(i + direction.getOffsetX() * 2, j + direction.getOffsetZ() * 2, 0, 2);
            arg.method_15061(i + direction3.getOffsetX() * 2, j + direction3.getOffsetZ() * 2, 0, 2);
            arg.method_15061(i + direction2.getOffsetX() * 2, j + direction2.getOffsetZ() * 2, 0, 2);
        }

        private boolean method_15046(class_3478 arg) {
            boolean bl = false;
            for (int i = 0; i < arg.field_15453; ++i) {
                for (int j = 0; j < arg.field_15454; ++j) {
                    if (arg.method_15066(j, i) != 0) continue;
                    int k = 0;
                    k += MansionParameters.method_15047(arg, j + 1, i) ? 1 : 0;
                    k += MansionParameters.method_15047(arg, j - 1, i) ? 1 : 0;
                    k += MansionParameters.method_15047(arg, j, i + 1) ? 1 : 0;
                    if ((k += MansionParameters.method_15047(arg, j, i - 1) ? 1 : 0) >= 3) {
                        arg.method_15065(j, i, 2);
                        bl = true;
                        continue;
                    }
                    if (k != 2) continue;
                    int l = 0;
                    l += MansionParameters.method_15047(arg, j + 1, i + 1) ? 1 : 0;
                    l += MansionParameters.method_15047(arg, j - 1, i + 1) ? 1 : 0;
                    l += MansionParameters.method_15047(arg, j + 1, i - 1) ? 1 : 0;
                    if ((l += MansionParameters.method_15047(arg, j - 1, i - 1) ? 1 : 0) > 1) continue;
                    arg.method_15065(j, i, 2);
                    bl = true;
                }
            }
            return bl;
        }

        private void method_15048() {
            int l;
            int j;
            ArrayList list = Lists.newArrayList();
            class_3478 lv = this.field_15443[1];
            for (int i = 0; i < this.field_15439.field_15453; ++i) {
                for (j = 0; j < this.field_15439.field_15454; ++j) {
                    int k = lv.method_15066(j, i);
                    l = k & 0xF0000;
                    if (l != 131072 || (k & 0x200000) != 0x200000) continue;
                    list.add(new Pair<Integer, Integer>(j, i));
                }
            }
            if (list.isEmpty()) {
                this.field_15439.method_15062(0, 0, this.field_15439.field_15454, this.field_15439.field_15453, 5);
                return;
            }
            Pair pair = (Pair)list.get(this.random.nextInt(list.size()));
            j = lv.method_15066((Integer)pair.getLeft(), (Integer)pair.getRight());
            lv.method_15065((Integer)pair.getLeft(), (Integer)pair.getRight(), j | 0x400000);
            Direction direction = this.method_15040(this.field_15440, (Integer)pair.getLeft(), (Integer)pair.getRight(), 1, j & 0xFFFF);
            l = (Integer)pair.getLeft() + direction.getOffsetX();
            int m = (Integer)pair.getRight() + direction.getOffsetZ();
            for (int n = 0; n < this.field_15439.field_15453; ++n) {
                for (int o = 0; o < this.field_15439.field_15454; ++o) {
                    if (!MansionParameters.method_15047(this.field_15440, o, n)) {
                        this.field_15439.method_15065(o, n, 5);
                        continue;
                    }
                    if (o == (Integer)pair.getLeft() && n == (Integer)pair.getRight()) {
                        this.field_15439.method_15065(o, n, 3);
                        continue;
                    }
                    if (o != l || n != m) continue;
                    this.field_15439.method_15065(o, n, 3);
                    this.field_15443[2].method_15065(o, n, 0x800000);
                }
            }
            ArrayList list2 = Lists.newArrayList();
            for (Direction direction2 : Direction.Type.HORIZONTAL) {
                if (this.field_15439.method_15066(l + direction2.getOffsetX(), m + direction2.getOffsetZ()) != 0) continue;
                list2.add(direction2);
            }
            if (list2.isEmpty()) {
                this.field_15439.method_15062(0, 0, this.field_15439.field_15454, this.field_15439.field_15453, 5);
                lv.method_15065((Integer)pair.getLeft(), (Integer)pair.getRight(), j);
                return;
            }
            Direction direction3 = (Direction)list2.get(this.random.nextInt(list2.size()));
            this.method_15045(this.field_15439, l + direction3.getOffsetX(), m + direction3.getOffsetZ(), direction3, 4);
            while (this.method_15046(this.field_15439)) {
            }
        }

        private void method_15042(class_3478 arg, class_3478 arg2) {
            int i;
            ArrayList list = Lists.newArrayList();
            for (i = 0; i < arg.field_15453; ++i) {
                for (int j = 0; j < arg.field_15454; ++j) {
                    if (arg.method_15066(j, i) != 2) continue;
                    list.add(new Pair<Integer, Integer>(j, i));
                }
            }
            Collections.shuffle(list, this.random);
            i = 10;
            for (Pair pair : list) {
                int l;
                int k = (Integer)pair.getLeft();
                if (arg2.method_15066(k, l = ((Integer)pair.getRight()).intValue()) != 0) continue;
                int m = k;
                int n = k;
                int o = l;
                int p = l;
                int q = 65536;
                if (arg2.method_15066(k + 1, l) == 0 && arg2.method_15066(k, l + 1) == 0 && arg2.method_15066(k + 1, l + 1) == 0 && arg.method_15066(k + 1, l) == 2 && arg.method_15066(k, l + 1) == 2 && arg.method_15066(k + 1, l + 1) == 2) {
                    ++n;
                    ++p;
                    q = 262144;
                } else if (arg2.method_15066(k - 1, l) == 0 && arg2.method_15066(k, l + 1) == 0 && arg2.method_15066(k - 1, l + 1) == 0 && arg.method_15066(k - 1, l) == 2 && arg.method_15066(k, l + 1) == 2 && arg.method_15066(k - 1, l + 1) == 2) {
                    --m;
                    ++p;
                    q = 262144;
                } else if (arg2.method_15066(k - 1, l) == 0 && arg2.method_15066(k, l - 1) == 0 && arg2.method_15066(k - 1, l - 1) == 0 && arg.method_15066(k - 1, l) == 2 && arg.method_15066(k, l - 1) == 2 && arg.method_15066(k - 1, l - 1) == 2) {
                    --m;
                    --o;
                    q = 262144;
                } else if (arg2.method_15066(k + 1, l) == 0 && arg.method_15066(k + 1, l) == 2) {
                    ++n;
                    q = 131072;
                } else if (arg2.method_15066(k, l + 1) == 0 && arg.method_15066(k, l + 1) == 2) {
                    ++p;
                    q = 131072;
                } else if (arg2.method_15066(k - 1, l) == 0 && arg.method_15066(k - 1, l) == 2) {
                    --m;
                    q = 131072;
                } else if (arg2.method_15066(k, l - 1) == 0 && arg.method_15066(k, l - 1) == 2) {
                    --o;
                    q = 131072;
                }
                int r = this.random.nextBoolean() ? m : n;
                int s = this.random.nextBoolean() ? o : p;
                int t = 0x200000;
                if (!arg.method_15067(r, s, 1)) {
                    r = r == m ? n : m;
                    int n2 = s = s == o ? p : o;
                    if (!arg.method_15067(r, s, 1)) {
                        int n3 = s = s == o ? p : o;
                        if (!arg.method_15067(r, s, 1)) {
                            r = r == m ? n : m;
                            int n4 = s = s == o ? p : o;
                            if (!arg.method_15067(r, s, 1)) {
                                t = 0;
                                r = m;
                                s = o;
                            }
                        }
                    }
                }
                for (int u = o; u <= p; ++u) {
                    for (int v = m; v <= n; ++v) {
                        if (v == r && u == s) {
                            arg2.method_15065(v, u, 0x100000 | t | q | i);
                            continue;
                        }
                        arg2.method_15065(v, u, q | i);
                    }
                }
                ++i;
            }
        }
    }

    static class LayoutGenerator {
        private final StructureManager manager;
        private final Random random;
        private int field_15446;
        private int field_15445;

        public LayoutGenerator(StructureManager manager, Random random) {
            this.manager = manager;
            this.random = random;
        }

        public void generate(BlockPos pos, BlockRotation rotation, List<Piece> pieces, MansionParameters mansionParameters) {
            int l;
            GenerationPiece generationPiece = new GenerationPiece();
            generationPiece.position = pos;
            generationPiece.rotation = rotation;
            generationPiece.template = "wall_flat";
            GenerationPiece generationPiece2 = new GenerationPiece();
            this.addEntrance(pieces, generationPiece);
            generationPiece2.position = generationPiece.position.up(8);
            generationPiece2.rotation = generationPiece.rotation;
            generationPiece2.template = "wall_window";
            if (!pieces.isEmpty()) {
                // empty if block
            }
            class_3478 lv = mansionParameters.field_15440;
            class_3478 lv2 = mansionParameters.field_15439;
            this.field_15446 = mansionParameters.field_15442 + 1;
            this.field_15445 = mansionParameters.field_15441 + 1;
            int i = mansionParameters.field_15442 + 1;
            int j = mansionParameters.field_15441;
            this.addRoof(pieces, generationPiece, lv, Direction.SOUTH, this.field_15446, this.field_15445, i, j);
            this.addRoof(pieces, generationPiece2, lv, Direction.SOUTH, this.field_15446, this.field_15445, i, j);
            GenerationPiece generationPiece3 = new GenerationPiece();
            generationPiece3.position = generationPiece.position.up(19);
            generationPiece3.rotation = generationPiece.rotation;
            generationPiece3.template = "wall_window";
            boolean bl = false;
            for (int k = 0; k < lv2.field_15453 && !bl; ++k) {
                for (l = lv2.field_15454 - 1; l >= 0 && !bl; --l) {
                    if (!MansionParameters.method_15047(lv2, l, k)) continue;
                    generationPiece3.position = generationPiece3.position.offset(rotation.rotate(Direction.SOUTH), 8 + (k - this.field_15445) * 8);
                    generationPiece3.position = generationPiece3.position.offset(rotation.rotate(Direction.EAST), (l - this.field_15446) * 8);
                    this.method_15052(pieces, generationPiece3);
                    this.addRoof(pieces, generationPiece3, lv2, Direction.SOUTH, l, k, l, k);
                    bl = true;
                }
            }
            this.method_15055(pieces, pos.up(16), rotation, lv, lv2);
            this.method_15055(pieces, pos.up(27), rotation, lv2, null);
            if (!pieces.isEmpty()) {
                // empty if block
            }
            RoomPool[] roomPools = new RoomPool[]{new FirstFloorRoomPool(), new SecondFloorRoomPool(), new ThirdFloorRoomPool()};
            for (l = 0; l < 3; ++l) {
                BlockPos blockPos = pos.up(8 * l + (l == 2 ? 3 : 0));
                class_3478 lv3 = mansionParameters.field_15443[l];
                class_3478 lv4 = l == 2 ? lv2 : lv;
                String string = l == 0 ? "carpet_south_1" : "carpet_south_2";
                String string2 = l == 0 ? "carpet_west_1" : "carpet_west_2";
                for (int m = 0; m < lv4.field_15453; ++m) {
                    for (int n = 0; n < lv4.field_15454; ++n) {
                        if (lv4.method_15066(n, m) != 1) continue;
                        BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (m - this.field_15445) * 8);
                        blockPos2 = blockPos2.offset(rotation.rotate(Direction.EAST), (n - this.field_15446) * 8);
                        pieces.add(new Piece(this.manager, "corridor_floor", blockPos2, rotation));
                        if (lv4.method_15066(n, m - 1) == 1 || (lv3.method_15066(n, m - 1) & 0x800000) == 0x800000) {
                            pieces.add(new Piece(this.manager, "carpet_north", blockPos2.offset(rotation.rotate(Direction.EAST), 1).up(), rotation));
                        }
                        if (lv4.method_15066(n + 1, m) == 1 || (lv3.method_15066(n + 1, m) & 0x800000) == 0x800000) {
                            pieces.add(new Piece(this.manager, "carpet_east", blockPos2.offset(rotation.rotate(Direction.SOUTH), 1).offset(rotation.rotate(Direction.EAST), 5).up(), rotation));
                        }
                        if (lv4.method_15066(n, m + 1) == 1 || (lv3.method_15066(n, m + 1) & 0x800000) == 0x800000) {
                            pieces.add(new Piece(this.manager, string, blockPos2.offset(rotation.rotate(Direction.SOUTH), 5).offset(rotation.rotate(Direction.WEST), 1), rotation));
                        }
                        if (lv4.method_15066(n - 1, m) != 1 && (lv3.method_15066(n - 1, m) & 0x800000) != 0x800000) continue;
                        pieces.add(new Piece(this.manager, string2, blockPos2.offset(rotation.rotate(Direction.WEST), 1).offset(rotation.rotate(Direction.NORTH), 1), rotation));
                    }
                }
                String string3 = l == 0 ? "indoors_wall_1" : "indoors_wall_2";
                String string4 = l == 0 ? "indoors_door_1" : "indoors_door_2";
                ArrayList list = Lists.newArrayList();
                for (int o = 0; o < lv4.field_15453; ++o) {
                    for (int p = 0; p < lv4.field_15454; ++p) {
                        Direction direction3;
                        BlockPos blockPos4;
                        boolean bl2;
                        boolean bl3 = bl2 = l == 2 && lv4.method_15066(p, o) == 3;
                        if (lv4.method_15066(p, o) != 2 && !bl2) continue;
                        int q = lv3.method_15066(p, o);
                        int r = q & 0xF0000;
                        int s = q & 0xFFFF;
                        bl2 = bl2 && (q & 0x800000) == 0x800000;
                        list.clear();
                        if ((q & 0x200000) == 0x200000) {
                            for (Direction direction : Direction.Type.HORIZONTAL) {
                                if (lv4.method_15066(p + direction.getOffsetX(), o + direction.getOffsetZ()) != 1) continue;
                                list.add(direction);
                            }
                        }
                        Direction direction2 = null;
                        if (!list.isEmpty()) {
                            direction2 = (Direction)list.get(this.random.nextInt(list.size()));
                        } else if ((q & 0x100000) == 0x100000) {
                            direction2 = Direction.UP;
                        }
                        BlockPos blockPos3 = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (o - this.field_15445) * 8);
                        blockPos3 = blockPos3.offset(rotation.rotate(Direction.EAST), -1 + (p - this.field_15446) * 8);
                        if (MansionParameters.method_15047(lv4, p - 1, o) && !mansionParameters.method_15039(lv4, p - 1, o, l, s)) {
                            pieces.add(new Piece(this.manager, direction2 == Direction.WEST ? string4 : string3, blockPos3, rotation));
                        }
                        if (lv4.method_15066(p + 1, o) == 1 && !bl2) {
                            blockPos4 = blockPos3.offset(rotation.rotate(Direction.EAST), 8);
                            pieces.add(new Piece(this.manager, direction2 == Direction.EAST ? string4 : string3, blockPos4, rotation));
                        }
                        if (MansionParameters.method_15047(lv4, p, o + 1) && !mansionParameters.method_15039(lv4, p, o + 1, l, s)) {
                            blockPos4 = blockPos3.offset(rotation.rotate(Direction.SOUTH), 7);
                            blockPos4 = blockPos4.offset(rotation.rotate(Direction.EAST), 7);
                            pieces.add(new Piece(this.manager, direction2 == Direction.SOUTH ? string4 : string3, blockPos4, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                        }
                        if (lv4.method_15066(p, o - 1) == 1 && !bl2) {
                            blockPos4 = blockPos3.offset(rotation.rotate(Direction.NORTH), 1);
                            blockPos4 = blockPos4.offset(rotation.rotate(Direction.EAST), 7);
                            pieces.add(new Piece(this.manager, direction2 == Direction.NORTH ? string4 : string3, blockPos4, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                        }
                        if (r == 65536) {
                            this.addSmallRoom(pieces, blockPos3, rotation, direction2, roomPools[l]);
                            continue;
                        }
                        if (r == 131072 && direction2 != null) {
                            direction3 = mansionParameters.method_15040(lv4, p, o, l, s);
                            boolean bl32 = (q & 0x400000) == 0x400000;
                            this.addMediumRoom(pieces, blockPos3, rotation, direction3, direction2, roomPools[l], bl32);
                            continue;
                        }
                        if (r == 262144 && direction2 != null && direction2 != Direction.UP) {
                            direction3 = direction2.rotateYClockwise();
                            if (!mansionParameters.method_15039(lv4, p + direction3.getOffsetX(), o + direction3.getOffsetZ(), l, s)) {
                                direction3 = direction3.getOpposite();
                            }
                            this.addBigRoom(pieces, blockPos3, rotation, direction3, direction2, roomPools[l]);
                            continue;
                        }
                        if (r != 262144 || direction2 != Direction.UP) continue;
                        this.addBigSecretRoom(pieces, blockPos3, rotation, roomPools[l]);
                    }
                }
            }
        }

        private void addRoof(List<Piece> list, GenerationPiece generationPiece, class_3478 arg, Direction direction, int i, int j, int k, int l) {
            int m = i;
            int n = j;
            Direction direction2 = direction;
            do {
                if (!MansionParameters.method_15047(arg, m + direction.getOffsetX(), n + direction.getOffsetZ())) {
                    this.method_15058(list, generationPiece);
                    direction = direction.rotateYClockwise();
                    if (m == k && n == l && direction2 == direction) continue;
                    this.method_15052(list, generationPiece);
                    continue;
                }
                if (MansionParameters.method_15047(arg, m + direction.getOffsetX(), n + direction.getOffsetZ()) && MansionParameters.method_15047(arg, m + direction.getOffsetX() + direction.rotateYCounterclockwise().getOffsetX(), n + direction.getOffsetZ() + direction.rotateYCounterclockwise().getOffsetZ())) {
                    this.method_15060(list, generationPiece);
                    m += direction.getOffsetX();
                    n += direction.getOffsetZ();
                    direction = direction.rotateYCounterclockwise();
                    continue;
                }
                if ((m += direction.getOffsetX()) == k && (n += direction.getOffsetZ()) == l && direction2 == direction) continue;
                this.method_15052(list, generationPiece);
            } while (m != k || n != l || direction2 != direction);
        }

        private void method_15055(List<Piece> list, BlockPos blockPos, BlockRotation blockRotation, class_3478 arg, @Nullable class_3478 arg2) {
            BlockPos blockPos3;
            boolean bl;
            BlockPos blockPos2;
            int j;
            int i;
            for (i = 0; i < arg.field_15453; ++i) {
                for (j = 0; j < arg.field_15454; ++j) {
                    blockPos2 = blockPos;
                    blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 8 + (i - this.field_15445) * 8);
                    blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.EAST), (j - this.field_15446) * 8);
                    boolean bl2 = bl = arg2 != null && MansionParameters.method_15047(arg2, j, i);
                    if (!MansionParameters.method_15047(arg, j, i) || bl) continue;
                    list.add(new Piece(this.manager, "roof", blockPos2.up(3), blockRotation));
                    if (!MansionParameters.method_15047(arg, j + 1, i)) {
                        blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 6);
                        list.add(new Piece(this.manager, "roof_front", blockPos3, blockRotation));
                    }
                    if (!MansionParameters.method_15047(arg, j - 1, i)) {
                        blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 0);
                        blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 7);
                        list.add(new Piece(this.manager, "roof_front", blockPos3, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
                    }
                    if (!MansionParameters.method_15047(arg, j, i - 1)) {
                        blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.WEST), 1);
                        list.add(new Piece(this.manager, "roof_front", blockPos3, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                    }
                    if (MansionParameters.method_15047(arg, j, i + 1)) continue;
                    blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 6);
                    blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 6);
                    list.add(new Piece(this.manager, "roof_front", blockPos3, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
                }
            }
            if (arg2 != null) {
                for (i = 0; i < arg.field_15453; ++i) {
                    for (j = 0; j < arg.field_15454; ++j) {
                        blockPos2 = blockPos;
                        blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 8 + (i - this.field_15445) * 8);
                        blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.EAST), (j - this.field_15446) * 8);
                        bl = MansionParameters.method_15047(arg2, j, i);
                        if (!MansionParameters.method_15047(arg, j, i) || !bl) continue;
                        if (!MansionParameters.method_15047(arg, j + 1, i)) {
                            blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 7);
                            list.add(new Piece(this.manager, "small_wall", blockPos3, blockRotation));
                        }
                        if (!MansionParameters.method_15047(arg, j - 1, i)) {
                            blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.WEST), 1);
                            blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 6);
                            list.add(new Piece(this.manager, "small_wall", blockPos3, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
                        }
                        if (!MansionParameters.method_15047(arg, j, i - 1)) {
                            blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.WEST), 0);
                            blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.NORTH), 1);
                            list.add(new Piece(this.manager, "small_wall", blockPos3, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                        }
                        if (!MansionParameters.method_15047(arg, j, i + 1)) {
                            blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 6);
                            blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 7);
                            list.add(new Piece(this.manager, "small_wall", blockPos3, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
                        }
                        if (!MansionParameters.method_15047(arg, j + 1, i)) {
                            if (!MansionParameters.method_15047(arg, j, i - 1)) {
                                blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 7);
                                blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.NORTH), 2);
                                list.add(new Piece(this.manager, "small_wall_corner", blockPos3, blockRotation));
                            }
                            if (!MansionParameters.method_15047(arg, j, i + 1)) {
                                blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 8);
                                blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 7);
                                list.add(new Piece(this.manager, "small_wall_corner", blockPos3, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
                            }
                        }
                        if (MansionParameters.method_15047(arg, j - 1, i)) continue;
                        if (!MansionParameters.method_15047(arg, j, i - 1)) {
                            blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.WEST), 2);
                            blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.NORTH), 1);
                            list.add(new Piece(this.manager, "small_wall_corner", blockPos3, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                        }
                        if (MansionParameters.method_15047(arg, j, i + 1)) continue;
                        blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.WEST), 1);
                        blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 8);
                        list.add(new Piece(this.manager, "small_wall_corner", blockPos3, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
                    }
                }
            }
            for (i = 0; i < arg.field_15453; ++i) {
                for (j = 0; j < arg.field_15454; ++j) {
                    BlockPos blockPos4;
                    blockPos2 = blockPos;
                    blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 8 + (i - this.field_15445) * 8);
                    blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.EAST), (j - this.field_15446) * 8);
                    boolean bl3 = bl = arg2 != null && MansionParameters.method_15047(arg2, j, i);
                    if (!MansionParameters.method_15047(arg, j, i) || bl) continue;
                    if (!MansionParameters.method_15047(arg, j + 1, i)) {
                        blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 6);
                        if (!MansionParameters.method_15047(arg, j, i + 1)) {
                            blockPos4 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 6);
                            list.add(new Piece(this.manager, "roof_corner", blockPos4, blockRotation));
                        } else if (MansionParameters.method_15047(arg, j + 1, i + 1)) {
                            blockPos4 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 5);
                            list.add(new Piece(this.manager, "roof_inner_corner", blockPos4, blockRotation));
                        }
                        if (!MansionParameters.method_15047(arg, j, i - 1)) {
                            list.add(new Piece(this.manager, "roof_corner", blockPos3, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                        } else if (MansionParameters.method_15047(arg, j + 1, i - 1)) {
                            blockPos4 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 9);
                            blockPos4 = blockPos4.offset(blockRotation.rotate(Direction.NORTH), 2);
                            list.add(new Piece(this.manager, "roof_inner_corner", blockPos4, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
                        }
                    }
                    if (MansionParameters.method_15047(arg, j - 1, i)) continue;
                    blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 0);
                    blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 0);
                    if (!MansionParameters.method_15047(arg, j, i + 1)) {
                        blockPos4 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 6);
                        list.add(new Piece(this.manager, "roof_corner", blockPos4, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
                    } else if (MansionParameters.method_15047(arg, j - 1, i + 1)) {
                        blockPos4 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 8);
                        blockPos4 = blockPos4.offset(blockRotation.rotate(Direction.WEST), 3);
                        list.add(new Piece(this.manager, "roof_inner_corner", blockPos4, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                    }
                    if (!MansionParameters.method_15047(arg, j, i - 1)) {
                        list.add(new Piece(this.manager, "roof_corner", blockPos3, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
                        continue;
                    }
                    if (!MansionParameters.method_15047(arg, j - 1, i - 1)) continue;
                    blockPos4 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 1);
                    list.add(new Piece(this.manager, "roof_inner_corner", blockPos4, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
                }
            }
        }

        private void addEntrance(List<Piece> list, GenerationPiece generationPiece) {
            Direction direction = generationPiece.rotation.rotate(Direction.WEST);
            list.add(new Piece(this.manager, "entrance", generationPiece.position.offset(direction, 9), generationPiece.rotation));
            generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.SOUTH), 16);
        }

        private void method_15052(List<Piece> list, GenerationPiece generationPiece) {
            list.add(new Piece(this.manager, generationPiece.template, generationPiece.position.offset(generationPiece.rotation.rotate(Direction.EAST), 7), generationPiece.rotation));
            generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.SOUTH), 8);
        }

        private void method_15058(List<Piece> list, GenerationPiece generationPiece) {
            generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.SOUTH), -1);
            list.add(new Piece(this.manager, "wall_corner", generationPiece.position, generationPiece.rotation));
            generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.SOUTH), -7);
            generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.WEST), -6);
            generationPiece.rotation = generationPiece.rotation.rotate(BlockRotation.CLOCKWISE_90);
        }

        private void method_15060(List<Piece> list, GenerationPiece generationPiece) {
            generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.SOUTH), 6);
            generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.EAST), 8);
            generationPiece.rotation = generationPiece.rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
        }

        private void addSmallRoom(List<Piece> list, BlockPos blockPos, BlockRotation blockRotation, Direction direction, RoomPool roomPool) {
            BlockRotation blockRotation2 = BlockRotation.NONE;
            String string = roomPool.getSmallRoom(this.random);
            if (direction != Direction.EAST) {
                if (direction == Direction.NORTH) {
                    blockRotation2 = blockRotation2.rotate(BlockRotation.COUNTERCLOCKWISE_90);
                } else if (direction == Direction.WEST) {
                    blockRotation2 = blockRotation2.rotate(BlockRotation.CLOCKWISE_180);
                } else if (direction == Direction.SOUTH) {
                    blockRotation2 = blockRotation2.rotate(BlockRotation.CLOCKWISE_90);
                } else {
                    string = roomPool.getSmallSecretRoom(this.random);
                }
            }
            BlockPos blockPos2 = Structure.method_15162(new BlockPos(1, 0, 0), BlockMirror.NONE, blockRotation2, 7, 7);
            blockRotation2 = blockRotation2.rotate(blockRotation);
            blockPos2 = blockPos2.rotate(blockRotation);
            BlockPos blockPos3 = blockPos.add(blockPos2.getX(), 0, blockPos2.getZ());
            list.add(new Piece(this.manager, string, blockPos3, blockRotation2));
        }

        private void addMediumRoom(List<Piece> list, BlockPos blockPos, BlockRotation blockRotation, Direction direction, Direction direction2, RoomPool roomPool, boolean staircase) {
            if (direction2 == Direction.EAST && direction == Direction.SOUTH) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
                list.add(new Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos2, blockRotation));
            } else if (direction2 == Direction.EAST && direction == Direction.NORTH) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
                blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 6);
                list.add(new Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos2, blockRotation, BlockMirror.LEFT_RIGHT));
            } else if (direction2 == Direction.WEST && direction == Direction.NORTH) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 7);
                blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 6);
                list.add(new Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos2, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
            } else if (direction2 == Direction.WEST && direction == Direction.SOUTH) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 7);
                list.add(new Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos2, blockRotation, BlockMirror.FRONT_BACK));
            } else if (direction2 == Direction.SOUTH && direction == Direction.EAST) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
                list.add(new Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos2, blockRotation.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.LEFT_RIGHT));
            } else if (direction2 == Direction.SOUTH && direction == Direction.WEST) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 7);
                list.add(new Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos2, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
            } else if (direction2 == Direction.NORTH && direction == Direction.WEST) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 7);
                blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 6);
                list.add(new Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos2, blockRotation.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.FRONT_BACK));
            } else if (direction2 == Direction.NORTH && direction == Direction.EAST) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
                blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 6);
                list.add(new Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos2, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
            } else if (direction2 == Direction.SOUTH && direction == Direction.NORTH) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
                blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.NORTH), 8);
                list.add(new Piece(this.manager, roomPool.getMediumGenericRoom(this.random, staircase), blockPos2, blockRotation));
            } else if (direction2 == Direction.NORTH && direction == Direction.SOUTH) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 7);
                blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 14);
                list.add(new Piece(this.manager, roomPool.getMediumGenericRoom(this.random, staircase), blockPos2, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
            } else if (direction2 == Direction.WEST && direction == Direction.EAST) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 15);
                list.add(new Piece(this.manager, roomPool.getMediumGenericRoom(this.random, staircase), blockPos2, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
            } else if (direction2 == Direction.EAST && direction == Direction.WEST) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.WEST), 7);
                blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 6);
                list.add(new Piece(this.manager, roomPool.getMediumGenericRoom(this.random, staircase), blockPos2, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
            } else if (direction2 == Direction.UP && direction == Direction.EAST) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 15);
                list.add(new Piece(this.manager, roomPool.getMediumSecretRoom(this.random), blockPos2, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
            } else if (direction2 == Direction.UP && direction == Direction.SOUTH) {
                BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
                blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.NORTH), 0);
                list.add(new Piece(this.manager, roomPool.getMediumSecretRoom(this.random), blockPos2, blockRotation));
            }
        }

        private void addBigRoom(List<Piece> list, BlockPos blockPos, BlockRotation blockRotation, Direction direction, Direction direction2, RoomPool roomPool) {
            int i = 0;
            int j = 0;
            BlockRotation blockRotation2 = blockRotation;
            BlockMirror blockMirror = BlockMirror.NONE;
            if (direction2 == Direction.EAST && direction == Direction.SOUTH) {
                i = -7;
            } else if (direction2 == Direction.EAST && direction == Direction.NORTH) {
                i = -7;
                j = 6;
                blockMirror = BlockMirror.LEFT_RIGHT;
            } else if (direction2 == Direction.NORTH && direction == Direction.EAST) {
                i = 1;
                j = 14;
                blockRotation2 = blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
            } else if (direction2 == Direction.NORTH && direction == Direction.WEST) {
                i = 7;
                j = 14;
                blockRotation2 = blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
                blockMirror = BlockMirror.LEFT_RIGHT;
            } else if (direction2 == Direction.SOUTH && direction == Direction.WEST) {
                i = 7;
                j = -8;
                blockRotation2 = blockRotation.rotate(BlockRotation.CLOCKWISE_90);
            } else if (direction2 == Direction.SOUTH && direction == Direction.EAST) {
                i = 1;
                j = -8;
                blockRotation2 = blockRotation.rotate(BlockRotation.CLOCKWISE_90);
                blockMirror = BlockMirror.LEFT_RIGHT;
            } else if (direction2 == Direction.WEST && direction == Direction.NORTH) {
                i = 15;
                j = 6;
                blockRotation2 = blockRotation.rotate(BlockRotation.CLOCKWISE_180);
            } else if (direction2 == Direction.WEST && direction == Direction.SOUTH) {
                i = 15;
                blockMirror = BlockMirror.FRONT_BACK;
            }
            BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), i);
            blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), j);
            list.add(new Piece(this.manager, roomPool.getBigRoom(this.random), blockPos2, blockRotation2, blockMirror));
        }

        private void addBigSecretRoom(List<Piece> list, BlockPos blockPos, BlockRotation blockRotation, RoomPool roomPool) {
            BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
            list.add(new Piece(this.manager, roomPool.getBigSecretRoom(this.random), blockPos2, blockRotation, BlockMirror.NONE));
        }
    }

    static class GenerationPiece {
        public BlockRotation rotation;
        public BlockPos position;
        public String template;

        private GenerationPiece() {
        }
    }

    public static class Piece
    extends SimpleStructurePiece {
        private final String template;
        private final BlockRotation rotation;
        private final BlockMirror mirror;

        public Piece(StructureManager structureManager, String string, BlockPos blockPos, BlockRotation blockRotation) {
            this(structureManager, string, blockPos, blockRotation, BlockMirror.NONE);
        }

        public Piece(StructureManager structureManager, String string, BlockPos blockPos, BlockRotation blockRotation, BlockMirror blockMirror) {
            super(StructurePieceType.WOODLAND_MANSION, 0);
            this.template = string;
            this.pos = blockPos;
            this.rotation = blockRotation;
            this.mirror = blockMirror;
            this.method_15068(structureManager);
        }

        public Piece(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.WOODLAND_MANSION, compoundTag);
            this.template = compoundTag.getString("Template");
            this.rotation = BlockRotation.valueOf(compoundTag.getString("Rot"));
            this.mirror = BlockMirror.valueOf(compoundTag.getString("Mi"));
            this.method_15068(structureManager);
        }

        private void method_15068(StructureManager structureManager) {
            Structure structure = structureManager.getStructureOrBlank(new Identifier("woodland_mansion/" + this.template));
            StructurePlacementData structurePlacementData = new StructurePlacementData().setIgnoreEntities(true).setRotation(this.rotation).setMirrored(this.mirror).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
            this.setStructureData(structure, this.pos, structurePlacementData);
        }

        @Override
        protected void toNbt(CompoundTag tag) {
            super.toNbt(tag);
            tag.putString("Template", this.template);
            tag.putString("Rot", this.placementData.getRotation().name());
            tag.putString("Mi", this.placementData.getMirror().name());
        }

        @Override
        protected void handleMetadata(String metadata, BlockPos pos, IWorld world, Random random, BlockBox boundingBox) {
            if (metadata.startsWith("Chest")) {
                BlockRotation blockRotation = this.placementData.getRotation();
                BlockState blockState = Blocks.CHEST.getDefaultState();
                if ("ChestWest".equals(metadata)) {
                    blockState = (BlockState)blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.WEST));
                } else if ("ChestEast".equals(metadata)) {
                    blockState = (BlockState)blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.EAST));
                } else if ("ChestSouth".equals(metadata)) {
                    blockState = (BlockState)blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.SOUTH));
                } else if ("ChestNorth".equals(metadata)) {
                    blockState = (BlockState)blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.NORTH));
                }
                this.addChest(world, boundingBox, random, pos, LootTables.WOODLAND_MANSION_CHEST, blockState);
            } else {
                IllagerEntity illagerEntity;
                switch (metadata) {
                    case "Mage": {
                        illagerEntity = EntityType.EVOKER.create(world.getWorld());
                        break;
                    }
                    case "Warrior": {
                        illagerEntity = EntityType.VINDICATOR.create(world.getWorld());
                        break;
                    }
                    default: {
                        return;
                    }
                }
                illagerEntity.setPersistent();
                illagerEntity.refreshPositionAndAngles(pos, 0.0f, 0.0f);
                illagerEntity.initialize(world, world.getLocalDifficulty(new BlockPos(illagerEntity)), SpawnType.STRUCTURE, null, null);
                world.spawnEntity(illagerEntity);
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
            }
        }
    }
}

