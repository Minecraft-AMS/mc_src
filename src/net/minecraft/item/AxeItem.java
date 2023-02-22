/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Sets
 */
package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AxeItem
extends MiningToolItem {
    private static final Set<Material> field_23139 = Sets.newHashSet((Object[])new Material[]{Material.WOOD, Material.NETHER_WOOD, Material.PLANT, Material.REPLACEABLE_PLANT, Material.BAMBOO, Material.GOURD});
    private static final Set<Block> EFFECTIVE_BLOCKS = Sets.newHashSet((Object[])new Block[]{Blocks.LADDER, Blocks.SCAFFOLDING, Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON, Blocks.DARK_OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.CRIMSON_BUTTON, Blocks.WARPED_BUTTON});
    protected static final Map<Block, Block> STRIPPED_BLOCKS = new ImmutableMap.Builder().put((Object)Blocks.OAK_WOOD, (Object)Blocks.STRIPPED_OAK_WOOD).put((Object)Blocks.OAK_LOG, (Object)Blocks.STRIPPED_OAK_LOG).put((Object)Blocks.DARK_OAK_WOOD, (Object)Blocks.STRIPPED_DARK_OAK_WOOD).put((Object)Blocks.DARK_OAK_LOG, (Object)Blocks.STRIPPED_DARK_OAK_LOG).put((Object)Blocks.ACACIA_WOOD, (Object)Blocks.STRIPPED_ACACIA_WOOD).put((Object)Blocks.ACACIA_LOG, (Object)Blocks.STRIPPED_ACACIA_LOG).put((Object)Blocks.BIRCH_WOOD, (Object)Blocks.STRIPPED_BIRCH_WOOD).put((Object)Blocks.BIRCH_LOG, (Object)Blocks.STRIPPED_BIRCH_LOG).put((Object)Blocks.JUNGLE_WOOD, (Object)Blocks.STRIPPED_JUNGLE_WOOD).put((Object)Blocks.JUNGLE_LOG, (Object)Blocks.STRIPPED_JUNGLE_LOG).put((Object)Blocks.SPRUCE_WOOD, (Object)Blocks.STRIPPED_SPRUCE_WOOD).put((Object)Blocks.SPRUCE_LOG, (Object)Blocks.STRIPPED_SPRUCE_LOG).put((Object)Blocks.WARPED_STEM, (Object)Blocks.STRIPPED_WARPED_STEM).put((Object)Blocks.WARPED_HYPHAE, (Object)Blocks.STRIPPED_WARPED_HYPHAE).put((Object)Blocks.CRIMSON_STEM, (Object)Blocks.STRIPPED_CRIMSON_STEM).put((Object)Blocks.CRIMSON_HYPHAE, (Object)Blocks.STRIPPED_CRIMSON_HYPHAE).build();

    protected AxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Settings settings) {
        super(attackDamage, attackSpeed, material, EFFECTIVE_BLOCKS, settings);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        Material material = state.getMaterial();
        if (field_23139.contains(material)) {
            return this.miningSpeed;
        }
        return super.getMiningSpeedMultiplier(stack, state);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos;
        World world = context.getWorld();
        BlockState blockState = world.getBlockState(blockPos = context.getBlockPos());
        Block block = STRIPPED_BLOCKS.get(blockState.getBlock());
        if (block != null) {
            PlayerEntity playerEntity = context.getPlayer();
            world.playSound(playerEntity, blockPos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0f, 1.0f);
            if (!world.isClient) {
                world.setBlockState(blockPos, (BlockState)block.getDefaultState().with(PillarBlock.AXIS, blockState.get(PillarBlock.AXIS)), 11);
                if (playerEntity != null) {
                    context.getStack().damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
                }
            }
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }
}

