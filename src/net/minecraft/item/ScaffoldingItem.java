/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ScaffoldingItem
extends BlockItem {
    public ScaffoldingItem(Block block, Item.Settings settings) {
        super(block, settings);
    }

    @Override
    @Nullable
    public ItemPlacementContext getPlacementContext(ItemPlacementContext context) {
        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = this.getBlock();
        if (blockState.getBlock() == block) {
            Direction direction = context.shouldCancelInteraction() ? (context.method_17699() ? context.getSide().getOpposite() : context.getSide()) : (context.getSide() == Direction.UP ? context.getPlayerFacing() : Direction.UP);
            int i = 0;
            BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos).setOffset(direction);
            while (i < 7) {
                if (!world.isClient && !World.isValid(mutable)) {
                    PlayerEntity playerEntity = context.getPlayer();
                    int j = world.getHeight();
                    if (!(playerEntity instanceof ServerPlayerEntity) || mutable.getY() < j) break;
                    ChatMessageS2CPacket chatMessageS2CPacket = new ChatMessageS2CPacket(new TranslatableText("build.tooHigh", j).formatted(Formatting.RED), MessageType.GAME_INFO);
                    ((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(chatMessageS2CPacket);
                    break;
                }
                blockState = world.getBlockState(mutable);
                if (blockState.getBlock() != this.getBlock()) {
                    if (!blockState.canReplace(context)) break;
                    return ItemPlacementContext.offset(context, mutable, direction);
                }
                mutable.setOffset(direction);
                if (!direction.getAxis().isHorizontal()) continue;
                ++i;
            }
            return null;
        }
        if (ScaffoldingBlock.calculateDistance(world, blockPos) == 7) {
            return null;
        }
        return context;
    }

    @Override
    protected boolean checkStatePlacement() {
        return false;
    }
}

