/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.color.block;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.ReplaceableTallPlantBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.state.property.Property;
import net.minecraft.util.IdList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockColors {
    private final IdList<BlockColorProvider> providers = new IdList(32);
    private final Map<Block, Set<Property<?>>> field_20271 = Maps.newHashMap();

    public static BlockColors create() {
        BlockColors blockColors = new BlockColors();
        blockColors.registerColorProvider((state, view, pos, tintIndex) -> {
            if (view == null || pos == null) {
                return -1;
            }
            return BiomeColors.getGrassColor(view, state.get(ReplaceableTallPlantBlock.HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos);
        }, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
        blockColors.method_21593(ReplaceableTallPlantBlock.HALF, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
        blockColors.registerColorProvider((state, view, pos, tintIndex) -> {
            if (view == null || pos == null) {
                return GrassColors.getColor(0.5, 1.0);
            }
            return BiomeColors.getGrassColor(view, pos);
        }, Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.GRASS, Blocks.POTTED_FERN);
        blockColors.registerColorProvider((state, view, pos, tintIndex) -> FoliageColors.getSpruceColor(), Blocks.SPRUCE_LEAVES);
        blockColors.registerColorProvider((state, view, pos, tintIndex) -> FoliageColors.getBirchColor(), Blocks.BIRCH_LEAVES);
        blockColors.registerColorProvider((state, view, pos, tintIndex) -> {
            if (view == null || pos == null) {
                return FoliageColors.getDefaultColor();
            }
            return BiomeColors.getFoliageColor(view, pos);
        }, Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE);
        blockColors.registerColorProvider((state, view, pos, tintIndex) -> {
            if (view == null || pos == null) {
                return -1;
            }
            return BiomeColors.getWaterColor(view, pos);
        }, Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.CAULDRON);
        blockColors.registerColorProvider((state, view, pos, tintIndex) -> RedstoneWireBlock.getWireColor(state.get(RedstoneWireBlock.POWER)), Blocks.REDSTONE_WIRE);
        blockColors.method_21593(RedstoneWireBlock.POWER, Blocks.REDSTONE_WIRE);
        blockColors.registerColorProvider((state, view, pos, tintIndex) -> {
            if (view == null || pos == null) {
                return -1;
            }
            return BiomeColors.getGrassColor(view, pos);
        }, Blocks.SUGAR_CANE);
        blockColors.registerColorProvider((state, view, pos, tintIndex) -> 14731036, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
        blockColors.registerColorProvider((state, view, pos, tintIndex) -> {
            int i = state.get(StemBlock.AGE);
            int j = i * 32;
            int k = 255 - i * 8;
            int l = i * 4;
            return j << 16 | k << 8 | l;
        }, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
        blockColors.method_21593(StemBlock.AGE, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
        blockColors.registerColorProvider((state, view, pos, tintIndex) -> {
            if (view == null || pos == null) {
                return 7455580;
            }
            return 2129968;
        }, Blocks.LILY_PAD);
        return blockColors;
    }

    public int getColor(BlockState state, World world, BlockPos pos) {
        BlockColorProvider blockColorProvider = this.providers.get(Registry.BLOCK.getRawId(state.getBlock()));
        if (blockColorProvider != null) {
            return blockColorProvider.getColor(state, null, null, 0);
        }
        MaterialColor materialColor = state.getTopMaterialColor(world, pos);
        return materialColor != null ? materialColor.color : -1;
    }

    public int getColor(BlockState state, @Nullable BlockRenderView view, @Nullable BlockPos pos, int tint) {
        BlockColorProvider blockColorProvider = this.providers.get(Registry.BLOCK.getRawId(state.getBlock()));
        return blockColorProvider == null ? -1 : blockColorProvider.getColor(state, view, pos, tint);
    }

    public void registerColorProvider(BlockColorProvider provider, Block ... blocks) {
        for (Block block : blocks) {
            this.providers.set(provider, Registry.BLOCK.getRawId(block));
        }
    }

    private void method_21594(Set<Property<?>> set, Block ... blocks) {
        for (Block block : blocks) {
            this.field_20271.put(block, set);
        }
    }

    private void method_21593(Property<?> property, Block ... blocks) {
        this.method_21594((Set<Property<?>>)ImmutableSet.of(property), blocks);
    }

    public Set<Property<?>> method_21592(Block block) {
        return (Set)this.field_20271.getOrDefault(block, (Set<Property<?>>)ImmutableSet.of());
    }
}
