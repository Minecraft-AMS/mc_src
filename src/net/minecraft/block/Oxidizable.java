/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableBiMap
 */
package net.minecraft.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Degradable;

public interface Oxidizable
extends Degradable<OxidationLevel> {
    public static final Supplier<BiMap<Block, Block>> OXIDATION_LEVEL_INCREASES = Suppliers.memoize(() -> ImmutableBiMap.builder().put((Object)Blocks.COPPER_BLOCK, (Object)Blocks.EXPOSED_COPPER).put((Object)Blocks.EXPOSED_COPPER, (Object)Blocks.WEATHERED_COPPER).put((Object)Blocks.WEATHERED_COPPER, (Object)Blocks.OXIDIZED_COPPER).put((Object)Blocks.CUT_COPPER, (Object)Blocks.EXPOSED_CUT_COPPER).put((Object)Blocks.EXPOSED_CUT_COPPER, (Object)Blocks.WEATHERED_CUT_COPPER).put((Object)Blocks.WEATHERED_CUT_COPPER, (Object)Blocks.OXIDIZED_CUT_COPPER).put((Object)Blocks.CUT_COPPER_SLAB, (Object)Blocks.EXPOSED_CUT_COPPER_SLAB).put((Object)Blocks.EXPOSED_CUT_COPPER_SLAB, (Object)Blocks.WEATHERED_CUT_COPPER_SLAB).put((Object)Blocks.WEATHERED_CUT_COPPER_SLAB, (Object)Blocks.OXIDIZED_CUT_COPPER_SLAB).put((Object)Blocks.CUT_COPPER_STAIRS, (Object)Blocks.EXPOSED_CUT_COPPER_STAIRS).put((Object)Blocks.EXPOSED_CUT_COPPER_STAIRS, (Object)Blocks.WEATHERED_CUT_COPPER_STAIRS).put((Object)Blocks.WEATHERED_CUT_COPPER_STAIRS, (Object)Blocks.OXIDIZED_CUT_COPPER_STAIRS).build());
    public static final Supplier<BiMap<Block, Block>> OXIDATION_LEVEL_DECREASES = Suppliers.memoize(() -> OXIDATION_LEVEL_INCREASES.get().inverse());

    public static Optional<Block> getDecreasedOxidationBlock(Block block) {
        return Optional.ofNullable((Block)OXIDATION_LEVEL_DECREASES.get().get((Object)block));
    }

    public static Block getUnaffectedOxidationBlock(Block block) {
        Block block2 = block;
        Block block3 = (Block)OXIDATION_LEVEL_DECREASES.get().get((Object)block2);
        while (block3 != null) {
            block2 = block3;
            block3 = (Block)OXIDATION_LEVEL_DECREASES.get().get((Object)block2);
        }
        return block2;
    }

    public static Optional<BlockState> getDecreasedOxidationState(BlockState state) {
        return Oxidizable.getDecreasedOxidationBlock(state.getBlock()).map(block -> block.getStateWithProperties(state));
    }

    public static Optional<Block> getIncreasedOxidationBlock(Block block) {
        return Optional.ofNullable((Block)OXIDATION_LEVEL_INCREASES.get().get((Object)block));
    }

    public static BlockState getUnaffectedOxidationState(BlockState state) {
        return Oxidizable.getUnaffectedOxidationBlock(state.getBlock()).getStateWithProperties(state);
    }

    @Override
    default public Optional<BlockState> getDegradationResult(BlockState state) {
        return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).map(block -> block.getStateWithProperties(state));
    }

    @Override
    default public float getDegradationChanceMultiplier() {
        if (this.getDegradationLevel() == OxidationLevel.UNAFFECTED) {
            return 0.75f;
        }
        return 1.0f;
    }

    public static final class OxidationLevel
    extends Enum<OxidationLevel> {
        public static final /* enum */ OxidationLevel UNAFFECTED = new OxidationLevel();
        public static final /* enum */ OxidationLevel EXPOSED = new OxidationLevel();
        public static final /* enum */ OxidationLevel WEATHERED = new OxidationLevel();
        public static final /* enum */ OxidationLevel OXIDIZED = new OxidationLevel();
        private static final /* synthetic */ OxidationLevel[] field_28708;

        public static OxidationLevel[] values() {
            return (OxidationLevel[])field_28708.clone();
        }

        public static OxidationLevel valueOf(String string) {
            return Enum.valueOf(OxidationLevel.class, string);
        }

        private static /* synthetic */ OxidationLevel[] method_36712() {
            return new OxidationLevel[]{UNAFFECTED, EXPOSED, WEATHERED, OXIDIZED};
        }

        static {
            field_28708 = OxidationLevel.method_36712();
        }
    }
}

