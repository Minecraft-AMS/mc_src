/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.container.BlockContext;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.container.SimpleNamedContainerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CraftingTableBlock
extends Block {
    private static final Text CONTAINER_NAME = new TranslatableText("container.crafting", new Object[0]);

    protected CraftingTableBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        player.openContainer(state.createContainerFactory(world, pos));
        player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
        return true;
    }

    @Override
    public NameableContainerFactory createContainerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedContainerFactory((i, playerInventory, playerEntity) -> new CraftingTableContainer(i, playerInventory, BlockContext.create(world, pos)), CONTAINER_NAME);
    }
}
