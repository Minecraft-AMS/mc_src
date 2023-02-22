/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;
import org.jetbrains.annotations.Nullable;

public class CommandBlockBlockEntity
extends BlockEntity {
    private boolean powered;
    private boolean auto;
    private boolean conditionMet;
    private boolean needsUpdatePacket;
    private final CommandBlockExecutor commandExecutor = new CommandBlockExecutor(){

        @Override
        public void setCommand(String string) {
            super.setCommand(string);
            CommandBlockBlockEntity.this.markDirty();
        }

        @Override
        public ServerWorld getWorld() {
            return (ServerWorld)CommandBlockBlockEntity.this.world;
        }

        @Override
        public void markDirty() {
            BlockState blockState = CommandBlockBlockEntity.this.world.getBlockState(CommandBlockBlockEntity.this.pos);
            this.getWorld().updateListeners(CommandBlockBlockEntity.this.pos, blockState, blockState, 3);
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public Vec3d getPos() {
            return new Vec3d((double)CommandBlockBlockEntity.this.pos.getX() + 0.5, (double)CommandBlockBlockEntity.this.pos.getY() + 0.5, (double)CommandBlockBlockEntity.this.pos.getZ() + 0.5);
        }

        @Override
        public ServerCommandSource getSource() {
            return new ServerCommandSource(this, new Vec3d((double)CommandBlockBlockEntity.this.pos.getX() + 0.5, (double)CommandBlockBlockEntity.this.pos.getY() + 0.5, (double)CommandBlockBlockEntity.this.pos.getZ() + 0.5), Vec2f.ZERO, this.getWorld(), 2, this.getCustomName().getString(), this.getCustomName(), this.getWorld().getServer(), null);
        }
    };

    public CommandBlockBlockEntity() {
        super(BlockEntityType.COMMAND_BLOCK);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        this.commandExecutor.serialize(tag);
        tag.putBoolean("powered", this.isPowered());
        tag.putBoolean("conditionMet", this.isConditionMet());
        tag.putBoolean("auto", this.isAuto());
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.commandExecutor.deserialize(tag);
        this.powered = tag.getBoolean("powered");
        this.conditionMet = tag.getBoolean("conditionMet");
        this.setAuto(tag.getBoolean("auto"));
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        if (this.needsUpdatePacket()) {
            this.setNeedsUpdatePacket(false);
            CompoundTag compoundTag = this.toTag(new CompoundTag());
            return new BlockEntityUpdateS2CPacket(this.pos, 2, compoundTag);
        }
        return null;
    }

    @Override
    public boolean shouldNotCopyTagFromItem() {
        return true;
    }

    public CommandBlockExecutor getCommandExecutor() {
        return this.commandExecutor;
    }

    public void setPowered(boolean bl) {
        this.powered = bl;
    }

    public boolean isPowered() {
        return this.powered;
    }

    public boolean isAuto() {
        return this.auto;
    }

    public void setAuto(boolean bl) {
        Block block;
        boolean bl2 = this.auto;
        this.auto = bl;
        if (!bl2 && bl && !this.powered && this.world != null && this.getCommandBlockType() != Type.SEQUENCE && (block = this.getCachedState().getBlock()) instanceof CommandBlock) {
            this.updateConditionMet();
            this.world.getBlockTickScheduler().schedule(this.pos, block, block.getTickRate(this.world));
        }
    }

    public boolean isConditionMet() {
        return this.conditionMet;
    }

    public boolean updateConditionMet() {
        this.conditionMet = true;
        if (this.isConditionalCommandBlock()) {
            BlockEntity blockEntity;
            BlockPos blockPos = this.pos.offset(this.world.getBlockState(this.pos).get(CommandBlock.FACING).getOpposite());
            this.conditionMet = this.world.getBlockState(blockPos).getBlock() instanceof CommandBlock ? (blockEntity = this.world.getBlockEntity(blockPos)) instanceof CommandBlockBlockEntity && ((CommandBlockBlockEntity)blockEntity).getCommandExecutor().getSuccessCount() > 0 : false;
        }
        return this.conditionMet;
    }

    public boolean needsUpdatePacket() {
        return this.needsUpdatePacket;
    }

    public void setNeedsUpdatePacket(boolean needsUpdatePacket) {
        this.needsUpdatePacket = needsUpdatePacket;
    }

    public Type getCommandBlockType() {
        Block block = this.getCachedState().getBlock();
        if (block == Blocks.COMMAND_BLOCK) {
            return Type.REDSTONE;
        }
        if (block == Blocks.REPEATING_COMMAND_BLOCK) {
            return Type.AUTO;
        }
        if (block == Blocks.CHAIN_COMMAND_BLOCK) {
            return Type.SEQUENCE;
        }
        return Type.REDSTONE;
    }

    public boolean isConditionalCommandBlock() {
        BlockState blockState = this.world.getBlockState(this.getPos());
        if (blockState.getBlock() instanceof CommandBlock) {
            return blockState.get(CommandBlock.CONDITIONAL);
        }
        return false;
    }

    @Override
    public void cancelRemoval() {
        this.resetBlock();
        super.cancelRemoval();
    }

    public static enum Type {
        SEQUENCE,
        AUTO,
        REDSTONE;

    }
}

