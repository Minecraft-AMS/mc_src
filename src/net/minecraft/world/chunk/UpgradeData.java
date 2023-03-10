/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  org.slf4j.Logger
 */
package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.GourdBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EightWayDirection;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.tick.Tick;
import org.slf4j.Logger;

public class UpgradeData {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final UpgradeData NO_UPGRADE_DATA = new UpgradeData(EmptyBlockView.INSTANCE);
    private static final String INDICES_KEY = "Indices";
    private static final EightWayDirection[] EIGHT_WAYS = EightWayDirection.values();
    private final EnumSet<EightWayDirection> sidesToUpgrade = EnumSet.noneOf(EightWayDirection.class);
    private final List<Tick<Block>> blockTicks = Lists.newArrayList();
    private final List<Tick<Fluid>> fluidTicks = Lists.newArrayList();
    private final int[][] centerIndicesToUpgrade;
    static final Map<Block, Logic> BLOCK_TO_LOGIC = new IdentityHashMap<Block, Logic>();
    static final Set<Logic> CALLBACK_LOGICS = Sets.newHashSet();

    private UpgradeData(HeightLimitView world) {
        this.centerIndicesToUpgrade = new int[world.countVerticalSections()][];
    }

    public UpgradeData(NbtCompound nbt, HeightLimitView world) {
        this(world);
        if (nbt.contains(INDICES_KEY, 10)) {
            NbtCompound nbtCompound = nbt.getCompound(INDICES_KEY);
            for (int i = 0; i < this.centerIndicesToUpgrade.length; ++i) {
                String string = String.valueOf(i);
                if (!nbtCompound.contains(string, 11)) continue;
                this.centerIndicesToUpgrade[i] = nbtCompound.getIntArray(string);
            }
        }
        int j = nbt.getInt("Sides");
        for (EightWayDirection eightWayDirection : EightWayDirection.values()) {
            if ((j & 1 << eightWayDirection.ordinal()) == 0) continue;
            this.sidesToUpgrade.add(eightWayDirection);
        }
        UpgradeData.addNeighborTicks(nbt, "neighbor_block_ticks", id -> Registry.BLOCK.getOrEmpty(Identifier.tryParse(id)).or(() -> Optional.of(Blocks.AIR)), this.blockTicks);
        UpgradeData.addNeighborTicks(nbt, "neighbor_fluid_ticks", id -> Registry.FLUID.getOrEmpty(Identifier.tryParse(id)).or(() -> Optional.of(Fluids.EMPTY)), this.fluidTicks);
    }

    private static <T> void addNeighborTicks(NbtCompound nbt, String key, Function<String, Optional<T>> nameToType, List<Tick<T>> ticks) {
        if (nbt.contains(key, 9)) {
            NbtList nbtList = nbt.getList(key, 10);
            for (NbtElement nbtElement : nbtList) {
                Tick.fromNbt((NbtCompound)nbtElement, nameToType).ifPresent(ticks::add);
            }
        }
    }

    public void upgrade(WorldChunk chunk) {
        this.upgradeCenter(chunk);
        for (EightWayDirection eightWayDirection : EIGHT_WAYS) {
            UpgradeData.upgradeSide(chunk, eightWayDirection);
        }
        World world = chunk.getWorld();
        this.blockTicks.forEach(tick -> {
            Block block = tick.type() == Blocks.AIR ? world.getBlockState(tick.pos()).getBlock() : (Block)tick.type();
            world.createAndScheduleBlockTick(tick.pos(), block, tick.delay(), tick.priority());
        });
        this.fluidTicks.forEach(tick -> {
            Fluid fluid = tick.type() == Fluids.EMPTY ? world.getFluidState(tick.pos()).getFluid() : (Fluid)tick.type();
            world.createAndScheduleFluidTick(tick.pos(), fluid, tick.delay(), tick.priority());
        });
        CALLBACK_LOGICS.forEach(logic -> logic.postUpdate(world));
    }

    private static void upgradeSide(WorldChunk chunk, EightWayDirection side) {
        World world = chunk.getWorld();
        if (!chunk.getUpgradeData().sidesToUpgrade.remove((Object)side)) {
            return;
        }
        Set<Direction> set = side.getDirections();
        boolean i = false;
        int j = 15;
        boolean bl = set.contains(Direction.EAST);
        boolean bl2 = set.contains(Direction.WEST);
        boolean bl3 = set.contains(Direction.SOUTH);
        boolean bl4 = set.contains(Direction.NORTH);
        boolean bl5 = set.size() == 1;
        ChunkPos chunkPos = chunk.getPos();
        int k = chunkPos.getStartX() + (bl5 && (bl4 || bl3) ? 1 : (bl2 ? 0 : 15));
        int l = chunkPos.getStartX() + (bl5 && (bl4 || bl3) ? 14 : (bl2 ? 0 : 15));
        int m = chunkPos.getStartZ() + (bl5 && (bl || bl2) ? 1 : (bl4 ? 0 : 15));
        int n = chunkPos.getStartZ() + (bl5 && (bl || bl2) ? 14 : (bl4 ? 0 : 15));
        Direction[] directions = Direction.values();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (BlockPos blockPos : BlockPos.iterate(k, world.getBottomY(), m, l, world.getTopY() - 1, n)) {
            BlockState blockState;
            BlockState blockState2 = blockState = world.getBlockState(blockPos);
            for (Direction direction : directions) {
                mutable.set((Vec3i)blockPos, direction);
                blockState2 = UpgradeData.applyAdjacentBlock(blockState2, direction, world, blockPos, mutable);
            }
            Block.replace(blockState, blockState2, world, blockPos, 18);
        }
    }

    private static BlockState applyAdjacentBlock(BlockState oldState, Direction dir, WorldAccess world, BlockPos currentPos, BlockPos otherPos) {
        return BLOCK_TO_LOGIC.getOrDefault(oldState.getBlock(), BuiltinLogic.DEFAULT).getUpdatedState(oldState, dir, world.getBlockState(otherPos), world, currentPos, otherPos);
    }

    private void upgradeCenter(WorldChunk chunk) {
        int i;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos.Mutable mutable2 = new BlockPos.Mutable();
        ChunkPos chunkPos = chunk.getPos();
        World worldAccess = chunk.getWorld();
        for (i = 0; i < this.centerIndicesToUpgrade.length; ++i) {
            ChunkSection chunkSection = chunk.getSection(i);
            int[] is = this.centerIndicesToUpgrade[i];
            this.centerIndicesToUpgrade[i] = null;
            if (is == null || is.length <= 0) continue;
            Direction[] directions = Direction.values();
            PalettedContainer<BlockState> palettedContainer = chunkSection.getBlockStateContainer();
            for (int j : is) {
                BlockState blockState;
                int k = j & 0xF;
                int l = j >> 8 & 0xF;
                int m = j >> 4 & 0xF;
                mutable.set(chunkPos.getStartX() + k, chunkSection.getYOffset() + l, chunkPos.getStartZ() + m);
                BlockState blockState2 = blockState = palettedContainer.get(j);
                for (Direction direction : directions) {
                    mutable2.set((Vec3i)mutable, direction);
                    if (ChunkSectionPos.getSectionCoord(mutable.getX()) != chunkPos.x || ChunkSectionPos.getSectionCoord(mutable.getZ()) != chunkPos.z) continue;
                    blockState2 = UpgradeData.applyAdjacentBlock(blockState2, direction, worldAccess, mutable, mutable2);
                }
                Block.replace(blockState, blockState2, worldAccess, mutable, 18);
            }
        }
        for (i = 0; i < this.centerIndicesToUpgrade.length; ++i) {
            if (this.centerIndicesToUpgrade[i] != null) {
                LOGGER.warn("Discarding update data for section {} for chunk ({} {})", new Object[]{worldAccess.sectionIndexToCoord(i), chunkPos.x, chunkPos.z});
            }
            this.centerIndicesToUpgrade[i] = null;
        }
    }

    public boolean isDone() {
        for (int[] is : this.centerIndicesToUpgrade) {
            if (is == null) continue;
            return false;
        }
        return this.sidesToUpgrade.isEmpty();
    }

    public NbtCompound toNbt() {
        NbtList nbtList;
        int i;
        NbtCompound nbtCompound = new NbtCompound();
        NbtCompound nbtCompound2 = new NbtCompound();
        for (i = 0; i < this.centerIndicesToUpgrade.length; ++i) {
            String string = String.valueOf(i);
            if (this.centerIndicesToUpgrade[i] == null || this.centerIndicesToUpgrade[i].length == 0) continue;
            nbtCompound2.putIntArray(string, this.centerIndicesToUpgrade[i]);
        }
        if (!nbtCompound2.isEmpty()) {
            nbtCompound.put(INDICES_KEY, nbtCompound2);
        }
        i = 0;
        for (EightWayDirection eightWayDirection : this.sidesToUpgrade) {
            i |= 1 << eightWayDirection.ordinal();
        }
        nbtCompound.putByte("Sides", (byte)i);
        if (!this.blockTicks.isEmpty()) {
            nbtList = new NbtList();
            this.blockTicks.forEach(tick2 -> nbtList.add(tick2.toNbt(block -> Registry.BLOCK.getId((Block)block).toString())));
            nbtCompound.put("neighbor_block_ticks", nbtList);
        }
        if (!this.fluidTicks.isEmpty()) {
            nbtList = new NbtList();
            this.fluidTicks.forEach(tick2 -> nbtList.add(tick2.toNbt(fluid -> Registry.FLUID.getId((Fluid)fluid).toString())));
            nbtCompound.put("neighbor_fluid_ticks", nbtList);
        }
        return nbtCompound;
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    static abstract class BuiltinLogic
    extends Enum<BuiltinLogic>
    implements Logic {
        public static final /* enum */ BuiltinLogic BLACKLIST = new BuiltinLogic(new Block[]{Blocks.OBSERVER, Blocks.NETHER_PORTAL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN}){

            @Override
            public BlockState getUpdatedState(BlockState oldState, Direction direction, BlockState otherState, WorldAccess world, BlockPos currentPos, BlockPos otherPos) {
                return oldState;
            }
        };
        public static final /* enum */ BuiltinLogic DEFAULT = new BuiltinLogic(new Block[0]){

            @Override
            public BlockState getUpdatedState(BlockState oldState, Direction direction, BlockState otherState, WorldAccess world, BlockPos currentPos, BlockPos otherPos) {
                return oldState.getStateForNeighborUpdate(direction, world.getBlockState(otherPos), world, currentPos, otherPos);
            }
        };
        public static final /* enum */ BuiltinLogic CHEST = new BuiltinLogic(new Block[]{Blocks.CHEST, Blocks.TRAPPED_CHEST}){

            @Override
            public BlockState getUpdatedState(BlockState oldState, Direction direction, BlockState otherState, WorldAccess world, BlockPos currentPos, BlockPos otherPos) {
                if (otherState.isOf(oldState.getBlock()) && direction.getAxis().isHorizontal() && oldState.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE && otherState.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE) {
                    Direction direction2 = oldState.get(ChestBlock.FACING);
                    if (direction.getAxis() != direction2.getAxis() && direction2 == otherState.get(ChestBlock.FACING)) {
                        ChestType chestType = direction == direction2.rotateYClockwise() ? ChestType.LEFT : ChestType.RIGHT;
                        world.setBlockState(otherPos, (BlockState)otherState.with(ChestBlock.CHEST_TYPE, chestType.getOpposite()), 18);
                        if (direction2 == Direction.NORTH || direction2 == Direction.EAST) {
                            BlockEntity blockEntity = world.getBlockEntity(currentPos);
                            BlockEntity blockEntity2 = world.getBlockEntity(otherPos);
                            if (blockEntity instanceof ChestBlockEntity && blockEntity2 instanceof ChestBlockEntity) {
                                ChestBlockEntity.copyInventory((ChestBlockEntity)blockEntity, (ChestBlockEntity)blockEntity2);
                            }
                        }
                        return (BlockState)oldState.with(ChestBlock.CHEST_TYPE, chestType);
                    }
                }
                return oldState;
            }
        };
        public static final /* enum */ BuiltinLogic LEAVES = new BuiltinLogic(true, new Block[]{Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES}){
            private final ThreadLocal<List<ObjectSet<BlockPos>>> distanceToPositions = ThreadLocal.withInitial(() -> Lists.newArrayListWithCapacity((int)7));

            @Override
            public BlockState getUpdatedState(BlockState oldState, Direction direction, BlockState otherState, WorldAccess world, BlockPos currentPos, BlockPos otherPos) {
                BlockState blockState = oldState.getStateForNeighborUpdate(direction, world.getBlockState(otherPos), world, currentPos, otherPos);
                if (oldState != blockState) {
                    int i = blockState.get(Properties.DISTANCE_1_7);
                    List<ObjectSet<BlockPos>> list = this.distanceToPositions.get();
                    if (list.isEmpty()) {
                        for (int j = 0; j < 7; ++j) {
                            list.add((ObjectSet<BlockPos>)new ObjectOpenHashSet());
                        }
                    }
                    list.get(i).add((Object)currentPos.toImmutable());
                }
                return oldState;
            }

            @Override
            public void postUpdate(WorldAccess world) {
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                List<ObjectSet<BlockPos>> list = this.distanceToPositions.get();
                for (int i = 2; i < list.size(); ++i) {
                    int j = i - 1;
                    ObjectSet<BlockPos> objectSet = list.get(j);
                    ObjectSet<BlockPos> objectSet2 = list.get(i);
                    for (BlockPos blockPos : objectSet) {
                        BlockState blockState = world.getBlockState(blockPos);
                        if (blockState.get(Properties.DISTANCE_1_7) < j) continue;
                        world.setBlockState(blockPos, (BlockState)blockState.with(Properties.DISTANCE_1_7, j), 18);
                        if (i == 7) continue;
                        for (Direction direction : DIRECTIONS) {
                            mutable.set((Vec3i)blockPos, direction);
                            BlockState blockState2 = world.getBlockState(mutable);
                            if (!blockState2.contains(Properties.DISTANCE_1_7) || blockState.get(Properties.DISTANCE_1_7) <= i) continue;
                            objectSet2.add((Object)mutable.toImmutable());
                        }
                    }
                }
                list.clear();
            }
        };
        public static final /* enum */ BuiltinLogic STEM_BLOCK = new BuiltinLogic(new Block[]{Blocks.MELON_STEM, Blocks.PUMPKIN_STEM}){

            @Override
            public BlockState getUpdatedState(BlockState oldState, Direction direction, BlockState otherState, WorldAccess world, BlockPos currentPos, BlockPos otherPos) {
                GourdBlock gourdBlock;
                if (oldState.get(StemBlock.AGE) == 7 && otherState.isOf(gourdBlock = ((StemBlock)oldState.getBlock()).getGourdBlock())) {
                    return (BlockState)gourdBlock.getAttachedStem().getDefaultState().with(HorizontalFacingBlock.FACING, direction);
                }
                return oldState;
            }
        };
        public static final Direction[] DIRECTIONS;
        private static final /* synthetic */ BuiltinLogic[] field_12961;

        public static BuiltinLogic[] values() {
            return (BuiltinLogic[])field_12961.clone();
        }

        public static BuiltinLogic valueOf(String string) {
            return Enum.valueOf(BuiltinLogic.class, string);
        }

        BuiltinLogic(Block ... blocks) {
            this(false, blocks);
        }

        BuiltinLogic(boolean addCallback, Block ... blocks) {
            for (Block block : blocks) {
                BLOCK_TO_LOGIC.put(block, this);
            }
            if (addCallback) {
                CALLBACK_LOGICS.add(this);
            }
        }

        private static /* synthetic */ BuiltinLogic[] method_36743() {
            return new BuiltinLogic[]{BLACKLIST, DEFAULT, CHEST, LEAVES, STEM_BLOCK};
        }

        static {
            field_12961 = BuiltinLogic.method_36743();
            DIRECTIONS = Direction.values();
        }
    }

    public static interface Logic {
        public BlockState getUpdatedState(BlockState var1, Direction var2, BlockState var3, WorldAccess var4, BlockPos var5, BlockPos var6);

        default public void postUpdate(WorldAccess world) {
        }
    }
}

