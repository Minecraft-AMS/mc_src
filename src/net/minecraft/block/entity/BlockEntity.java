/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.util.Supplier
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;
import org.jetbrains.annotations.Nullable;

public abstract class BlockEntity {
    private static final Logger LOGGER = LogManager.getLogger();
    private final BlockEntityType<?> type;
    @Nullable
    protected World world;
    protected BlockPos pos = BlockPos.ORIGIN;
    protected boolean removed;
    @Nullable
    private BlockState cachedState;
    private boolean markedInvalid;

    public BlockEntity(BlockEntityType<?> type) {
        this.type = type;
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

    public void fromTag(CompoundTag tag) {
        this.pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }

    public CompoundTag toTag(CompoundTag tag) {
        return this.writeIdentifyingData(tag);
    }

    private CompoundTag writeIdentifyingData(CompoundTag compoundTag) {
        Identifier identifier = BlockEntityType.getId(this.getType());
        if (identifier == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        }
        compoundTag.putString("id", identifier.toString());
        compoundTag.putInt("x", this.pos.getX());
        compoundTag.putInt("y", this.pos.getY());
        compoundTag.putInt("z", this.pos.getZ());
        return compoundTag;
    }

    @Nullable
    public static BlockEntity createFromTag(CompoundTag compoundTag) {
        String string = compoundTag.getString("id");
        return Registry.BLOCK_ENTITY.getOrEmpty(new Identifier(string)).map(blockEntityType -> {
            try {
                return blockEntityType.instantiate();
            }
            catch (Throwable throwable) {
                LOGGER.error("Failed to create block entity {}", (Object)string, (Object)throwable);
                return null;
            }
        }).map(blockEntity -> {
            try {
                blockEntity.fromTag(compoundTag);
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
            this.cachedState = this.world.getBlockState(this.pos);
            this.world.markDirty(this.pos, this);
            if (!this.cachedState.isAir()) {
                this.world.updateHorizontalAdjacent(this.pos, this.cachedState.getBlock());
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public double getSquaredDistance(double x, double y, double z) {
        double d = (double)this.pos.getX() + 0.5 - x;
        double e = (double)this.pos.getY() + 0.5 - y;
        double f = (double)this.pos.getZ() + 0.5 - z;
        return d * d + e * e + f * f;
    }

    @Environment(value=EnvType.CLIENT)
    public double getSquaredRenderDistance() {
        return 4096.0;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getCachedState() {
        if (this.cachedState == null) {
            this.cachedState = this.world.getBlockState(this.pos);
        }
        return this.cachedState;
    }

    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return null;
    }

    public CompoundTag toInitialChunkDataTag() {
        return this.writeIdentifyingData(new CompoundTag());
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

    public boolean onBlockAction(int i, int j) {
        return false;
    }

    public void resetBlock() {
        this.cachedState = null;
    }

    public void populateCrashReport(CrashReportSection crashReportSection) {
        crashReportSection.add("Name", () -> Registry.BLOCK_ENTITY.getId(this.getType()) + " // " + this.getClass().getCanonicalName());
        if (this.world == null) {
            return;
        }
        CrashReportSection.addBlockInfo(crashReportSection, this.pos, this.getCachedState());
        CrashReportSection.addBlockInfo(crashReportSection, this.pos, this.world.getBlockState(this.pos));
    }

    public void setPos(BlockPos blockPos) {
        this.pos = blockPos.toImmutable();
    }

    public boolean shouldNotCopyTagFromItem() {
        return false;
    }

    public void applyRotation(BlockRotation blockRotation) {
    }

    public void applyMirror(BlockMirror blockMirror) {
    }

    public BlockEntityType<?> getType() {
        return this.type;
    }

    public void markInvalid() {
        if (this.markedInvalid) {
            return;
        }
        this.markedInvalid = true;
        LOGGER.warn("Block entity invalid: {} @ {}", new Supplier[]{() -> Registry.BLOCK_ENTITY.getId(this.getType()), this::getPos});
    }
}

