/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class BlockEntity {
    private static final Logger LOGGER = LogManager.getLogger();
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

    public NbtCompound writeNbt(NbtCompound nbt) {
        return this.writeIdentifyingData(nbt);
    }

    private NbtCompound writeIdentifyingData(NbtCompound nbt) {
        Identifier identifier = BlockEntityType.getId(this.getType());
        if (identifier == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        }
        nbt.putString("id", identifier.toString());
        nbt.putInt("x", this.pos.getX());
        nbt.putInt("y", this.pos.getY());
        nbt.putInt("z", this.pos.getZ());
        return nbt;
    }

    @Nullable
    public static BlockEntity createFromNbt(BlockPos pos, BlockState state, NbtCompound nbt) {
        String string = nbt.getString("id");
        Identifier identifier = Identifier.tryParse(string);
        if (identifier == null) {
            LOGGER.error("Block entity has invalid type: {}", (Object)string);
            return null;
        }
        return Registry.BLOCK_ENTITY_TYPE.getOrEmpty(identifier).map(blockEntityType -> {
            try {
                return blockEntityType.instantiate(pos, state);
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
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return null;
    }

    public NbtCompound toInitialChunkDataNbt() {
        return this.writeIdentifyingData(new NbtCompound());
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
        crashReportSection.add("Name", () -> Registry.BLOCK_ENTITY_TYPE.getId(this.getType()) + " // " + this.getClass().getCanonicalName());
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

