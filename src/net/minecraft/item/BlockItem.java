/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item;

import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockItem
extends Item {
    @Deprecated
    private final Block block;

    public BlockItem(Block block, Item.Settings settings) {
        super(settings);
        this.block = block;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ActionResult actionResult = this.place(new ItemPlacementContext(context));
        if (actionResult != ActionResult.SUCCESS && this.isFood()) {
            return this.use(context.world, context.player, context.hand).getResult();
        }
        return actionResult;
    }

    public ActionResult place(ItemPlacementContext context) {
        if (!context.canPlace()) {
            return ActionResult.FAIL;
        }
        ItemPlacementContext itemPlacementContext = this.getPlacementContext(context);
        if (itemPlacementContext == null) {
            return ActionResult.FAIL;
        }
        BlockState blockState = this.getPlacementState(itemPlacementContext);
        if (blockState == null) {
            return ActionResult.FAIL;
        }
        if (!this.place(itemPlacementContext, blockState)) {
            return ActionResult.FAIL;
        }
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        World world = itemPlacementContext.getWorld();
        PlayerEntity playerEntity = itemPlacementContext.getPlayer();
        ItemStack itemStack = itemPlacementContext.getStack();
        BlockState blockState2 = world.getBlockState(blockPos);
        Block block = blockState2.getBlock();
        if (block == blockState.getBlock()) {
            blockState2 = this.placeFromTag(blockPos, world, itemStack, blockState2);
            this.postPlacement(blockPos, world, playerEntity, itemStack, blockState2);
            block.onPlaced(world, blockPos, blockState2, playerEntity, itemStack);
            if (playerEntity instanceof ServerPlayerEntity) {
                Criterions.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
            }
        }
        BlockSoundGroup blockSoundGroup = blockState2.getSoundGroup();
        world.playSound(playerEntity, blockPos, this.getPlaceSound(blockState2), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0f) / 2.0f, blockSoundGroup.getPitch() * 0.8f);
        itemStack.decrement(1);
        return ActionResult.SUCCESS;
    }

    protected SoundEvent getPlaceSound(BlockState state) {
        return state.getSoundGroup().getPlaceSound();
    }

    @Nullable
    public ItemPlacementContext getPlacementContext(ItemPlacementContext context) {
        return context;
    }

    protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        return BlockItem.writeTagToBlockEntity(world, player, pos, stack);
    }

    @Nullable
    protected BlockState getPlacementState(ItemPlacementContext context) {
        BlockState blockState = this.getBlock().getPlacementState(context);
        return blockState != null && this.canPlace(context, blockState) ? blockState : null;
    }

    private BlockState placeFromTag(BlockPos pos, World world, ItemStack stack, BlockState state) {
        BlockState blockState = state;
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag != null) {
            CompoundTag compoundTag2 = compoundTag.getCompound("BlockStateTag");
            StateManager<Block, BlockState> stateManager = blockState.getBlock().getStateManager();
            for (String string : compoundTag2.getKeys()) {
                Property<?> property = stateManager.getProperty(string);
                if (property == null) continue;
                String string2 = compoundTag2.get(string).asString();
                blockState = BlockItem.with(blockState, property, string2);
            }
        }
        if (blockState != state) {
            world.setBlockState(pos, blockState, 2);
        }
        return blockState;
    }

    private static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, String name) {
        return property.parse(name).map(value -> (BlockState)state.with(property, value)).orElse(state);
    }

    protected boolean canPlace(ItemPlacementContext context, BlockState state) {
        PlayerEntity playerEntity = context.getPlayer();
        EntityContext entityContext = playerEntity == null ? EntityContext.absent() : EntityContext.of(playerEntity);
        return (!this.checkStatePlacement() || state.canPlaceAt(context.getWorld(), context.getBlockPos())) && context.getWorld().canPlace(state, context.getBlockPos(), entityContext);
    }

    protected boolean checkStatePlacement() {
        return true;
    }

    protected boolean place(ItemPlacementContext context, BlockState state) {
        return context.getWorld().setBlockState(context.getBlockPos(), state, 11);
    }

    public static boolean writeTagToBlockEntity(World world, @Nullable PlayerEntity player, BlockPos pos, ItemStack stack) {
        BlockEntity blockEntity;
        MinecraftServer minecraftServer = world.getServer();
        if (minecraftServer == null) {
            return false;
        }
        CompoundTag compoundTag = stack.getSubTag("BlockEntityTag");
        if (compoundTag != null && (blockEntity = world.getBlockEntity(pos)) != null) {
            if (!(world.isClient || !blockEntity.shouldNotCopyTagFromItem() || player != null && player.isCreativeLevelTwoOp())) {
                return false;
            }
            CompoundTag compoundTag2 = blockEntity.toTag(new CompoundTag());
            CompoundTag compoundTag3 = compoundTag2.copy();
            compoundTag2.copyFrom(compoundTag);
            compoundTag2.putInt("x", pos.getX());
            compoundTag2.putInt("y", pos.getY());
            compoundTag2.putInt("z", pos.getZ());
            if (!compoundTag2.equals(compoundTag3)) {
                blockEntity.fromTag(compoundTag2);
                blockEntity.markDirty();
                return true;
            }
        }
        return false;
    }

    @Override
    public String getTranslationKey() {
        return this.getBlock().getTranslationKey();
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            this.getBlock().addStacksForDisplay(group, stacks);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        this.getBlock().buildTooltip(stack, world, tooltip, context);
    }

    public Block getBlock() {
        return this.block;
    }

    public void appendBlocks(Map<Block, Item> map, Item item) {
        map.put(this.getBlock(), item);
    }
}

