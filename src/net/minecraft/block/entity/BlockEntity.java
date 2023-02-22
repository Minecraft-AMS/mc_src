/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final BlockEntityType<?> type;
    @Nullable
    protected World world;
    protected final BlockPos pos;
    protected boolean removed;
    private BlockState cachedState;

    public BlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this.type = type;
        this.pos = pos.toImmutable();
        this.cachedState = state;
    }

    public static BlockPos posFromNbt(NbtCompound nbt) {
        return new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    }

    @Nullable
    public World getWorld() {
        return this.world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public boolean hasWorld() {
        return this.world != null;
    }

    public void readNbt(NbtCompound nbt) {
    }

    protected void writeNbt(NbtCompound nbt) {
    }

    public final NbtCompound createNbtWithIdentifyingData() {
        NbtCompound nbtCompound = this.createNbt();
        this.writeIdentifyingData(nbtCompound);
        return nbtCompound;
    }

    public final NbtCompound createNbtWithId() {
        NbtCompound nbtCompound = this.createNbt();
        this.writeIdToNbt(nbtCompound);
        return nbtCompound;
    }

    public final NbtCompound createNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        this.writeNbt(nbtCompound);
        return nbtCompound;
    }

    private void writeIdToNbt(NbtCompound nbt) {
        Identifier identifier = BlockEntityType.getId(this.getType());
        if (identifier == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        }
        nbt.putString("id", identifier.toString());
    }

    public static void writeIdToNbt(NbtCompound nbt, BlockEntityType<?> type) {
        nbt.putString("id", BlockEntityType.getId(type).toString());
    }

    public void setStackNbt(ItemStack stack) {
        BlockItem.setBlockEntityNbt(stack, this.getType(), this.createNbt());
    }

    private void writeIdentifyingData(NbtCompound nbt) {
        this.writeIdToNbt(nbt);
        nbt.putInt("x", this.pos.getX());
        nbt.putInt("y", this.pos.getY());
        nbt.putInt("z", this.pos.getZ());
    }

    @Nullable
    public static BlockEntity createFromNbt(BlockPos pos, BlockState state, NbtCompound nbt) {
        String string = nbt.getString("id");
        Identifier identifier = Identifier.tryParse(string);
        if (identifier == null) {
            LOGGER.error("Block entity has invalid type: {}", (Object)string);
            return null;
        }
        return Registries.BLOCK_ENTITY_TYPE.getOrEmpty(identifier).map(type -> {
            try {
                return type.instantiate(pos, state);
            }
            catch (Throwable throwable) {
                LOGGER.error("Failed to create block entity {}", (Object)string, (Object)throwable);
                return null;
            }
        }).map(blockEntity -> {
            try {
                blockEntity.readNbt(nbt);
                return blockEntity;
            }
            catch (Throwable throwable) {
                LOGGER.error("Failed to load data for block entity {}", (Object)string, (Object)throwable);
                return null;
            }
        }).orElseGet(() -> {
            LOGGER.warn("Skipping BlockEntity with id {}", (Object)string);
            return null;
        });
    }

    public void markDirty() {
        if (this.world != null) {
            BlockEntity.markDirty(this.world, this.pos, this.cachedState);
        }
    }

    protected static void markDirty(World world, BlockPos pos, BlockState state) {
        world.markDirty(pos);
        if (!state.isAir()) {
            world.updateComparators(pos, state.getBlock());
        }
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getCachedState() {
        return this.cachedState;
    }

    @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return null;
    }

    public NbtCompound toInitialChunkDataNbt() {
        return new NbtCompound();
    }

    public boolean isRemoved() {
        return this.removed;
    }

    public void markRemoved() {
        this.removed = true;
    }

    public void cancelRemoval() {
        this.removed = false;
    }

    public boolean onSyncedBlockEvent(int type, int data) {
        return false;
    }

    public void populateCrashReport(CrashReportSection crashReportSection) {
        crashReportSection.add("Name", () -> Registries.BLOCK_ENTITY_TYPE.getId(this.getType()) + " // " + this.getClass().getCanonicalName());
        if (this.world == null) {
            return;
        }
        CrashReportSection.addBlockInfo(crashReportSection, this.world, this.pos, this.getCachedState());
        CrashReportSection.addBlockInfo(crashReportSection, this.world, this.pos, this.world.getBlockState(this.pos));
    }

    public boolean copyItemDataRequiresOperator() {
        return false;
    }

    public BlockEntityType<?> getType() {
        return this.type;
    }

    @Deprecated
    public void setCachedState(BlockState state) {
        this.cachedState = state;
    }
}

