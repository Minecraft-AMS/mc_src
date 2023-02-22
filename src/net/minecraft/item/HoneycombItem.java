/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableBiMap
 */
package net.minecraft.item;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class HoneycombItem
extends Item {
    public static final Supplier<BiMap<Block, Block>> UNWAXED_TO_WAXED_BLOCKS = Suppliers.memoize(() -> ImmutableBiMap.builder().put((Object)Blocks.COPPER_BLOCK, (Object)Blocks.WAXED_COPPER_BLOCK).put((Object)Blocks.EXPOSED_COPPER, (Object)Blocks.WAXED_EXPOSED_COPPER).put((Object)Blocks.WEATHERED_COPPER, (Object)Blocks.WAXED_WEATHERED_COPPER).put((Object)Blocks.OXIDIZED_COPPER, (Object)Blocks.WAXED_OXIDIZED_COPPER).put((Object)Blocks.CUT_COPPER, (Object)Blocks.WAXED_CUT_COPPER).put((Object)Blocks.EXPOSED_CUT_COPPER, (Object)Blocks.WAXED_EXPOSED_CUT_COPPER).put((Object)Blocks.WEATHERED_CUT_COPPER, (Object)Blocks.WAXED_WEATHERED_CUT_COPPER).put((Object)Blocks.OXIDIZED_CUT_COPPER, (Object)Blocks.WAXED_OXIDIZED_CUT_COPPER).put((Object)Blocks.CUT_COPPER_SLAB, (Object)Blocks.WAXED_CUT_COPPER_SLAB).put((Object)Blocks.EXPOSED_CUT_COPPER_SLAB, (Object)Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB).put((Object)Blocks.WEATHERED_CUT_COPPER_SLAB, (Object)Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB).put((Object)Blocks.OXIDIZED_CUT_COPPER_SLAB, (Object)Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB).put((Object)Blocks.CUT_COPPER_STAIRS, (Object)Blocks.WAXED_CUT_COPPER_STAIRS).put((Object)Blocks.EXPOSED_CUT_COPPER_STAIRS, (Object)Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS).put((Object)Blocks.WEATHERED_CUT_COPPER_STAIRS, (Object)Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS).put((Object)Blocks.OXIDIZED_CUT_COPPER_STAIRS, (Object)Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS).build());
    public static final Supplier<BiMap<Block, Block>> WAXED_TO_UNWAXED_BLOCKS = Suppliers.memoize(() -> UNWAXED_TO_WAXED_BLOCKS.get().inverse());

    public HoneycombItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        return HoneycombItem.getWaxedState(blockState).map(state -> {
            PlayerEntity playerEntity = context.getPlayer();
            ItemStack itemStack = context.getStack();
            if (playerEntity instanceof ServerPlayerEntity) {
                Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
            }
            itemStack.decrement(1);
            world.setBlockState(blockPos, (BlockState)state, 11);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, state));
            world.syncWorldEvent(playerEntity, 3003, blockPos, 0);
            return ActionResult.success(world.isClient);
        }).orElse(ActionResult.PASS);
    }

    public static Optional<BlockState> getWaxedState(BlockState state) {
        return Optional.ofNullable((Block)UNWAXED_TO_WAXED_BLOCKS.get().get((Object)state.getBlock())).map(block -> block.getStateWithProperties(state));
    }
}

