/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class StructureBlockBlockEntity
extends BlockEntity {
    private Identifier structureName;
    private String author = "";
    private String metadata = "";
    private BlockPos offset = new BlockPos(0, 1, 0);
    private BlockPos size = BlockPos.ORIGIN;
    private BlockMirror mirror = BlockMirror.NONE;
    private BlockRotation rotation = BlockRotation.NONE;
    private StructureBlockMode mode = StructureBlockMode.DATA;
    private boolean ignoreEntities = true;
    private boolean powered;
    private boolean showAir;
    private boolean showBoundingBox = true;
    private float integrity = 1.0f;
    private long seed;

    public StructureBlockBlockEntity() {
        super(BlockEntityType.STRUCTURE_BLOCK);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public double getRenderDistance() {
        return 96.0;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("name", this.getStructureName());
        nbt.putString("author", this.author);
        nbt.putString("metadata", this.metadata);
        nbt.putInt("posX", this.offset.getX());
        nbt.putInt("posY", this.offset.getY());
        nbt.putInt("posZ", this.offset.getZ());
        nbt.putInt("sizeX", this.size.getX());
        nbt.putInt("sizeY", this.size.getY());
        nbt.putInt("sizeZ", this.size.getZ());
        nbt.putString("rotation", this.rotation.toString());
        nbt.putString("mirror", this.mirror.toString());
        nbt.putString("mode", this.mode.toString());
        nbt.putBoolean("ignoreEntities", this.ignoreEntities);
        nbt.putBoolean("powered", this.powered);
        nbt.putBoolean("showair", this.showAir);
        nbt.putBoolean("showboundingbox", this.showBoundingBox);
        nbt.putFloat("integrity", this.integrity);
        nbt.putLong("seed", this.seed);
        return nbt;
    }

    @Override
    public void fromTag(BlockState state, NbtCompound tag) {
        super.fromTag(state, tag);
        this.setStructureName(tag.getString("name"));
        this.author = tag.getString("author");
        this.metadata = tag.getString("metadata");
        int i = MathHelper.clamp(tag.getInt("posX"), -48, 48);
        int j = MathHelper.clamp(tag.getInt("posY"), -48, 48);
        int k = MathHelper.clamp(tag.getInt("posZ"), -48, 48);
        this.offset = new BlockPos(i, j, k);
        int l = MathHelper.clamp(tag.getInt("sizeX"), 0, 48);
        int m = MathHelper.clamp(tag.getInt("sizeY"), 0, 48);
        int n = MathHelper.clamp(tag.getInt("sizeZ"), 0, 48);
        this.size = new BlockPos(l, m, n);
        try {
            this.rotation = BlockRotation.valueOf(tag.getString("rotation"));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            this.rotation = BlockRotation.NONE;
        }
        try {
            this.mirror = BlockMirror.valueOf(tag.getString("mirror"));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            this.mirror = BlockMirror.NONE;
        }
        try {
            this.mode = StructureBlockMode.valueOf(tag.getString("mode"));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            this.mode = StructureBlockMode.DATA;
        }
        this.ignoreEntities = tag.getBoolean("ignoreEntities");
        this.powered = tag.getBoolean("powered");
        this.showAir = tag.getBoolean("showair");
        this.showBoundingBox = tag.getBoolean("showboundingbox");
        this.integrity = tag.contains("integrity") ? tag.getFloat("integrity") : 1.0f;
        this.seed = tag.getLong("seed");
        this.updateBlockMode();
    }

    private void updateBlockMode() {
        if (this.world == null) {
            return;
        }
        BlockPos blockPos = this.getPos();
        BlockState blockState = this.world.getBlockState(blockPos);
        if (blockState.isOf(Blocks.STRUCTURE_BLOCK)) {
            this.world.setBlockState(blockPos, (BlockState)blockState.with(StructureBlock.MODE, this.mode), 2);
        }
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 7, this.toInitialChunkDataNbt());
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.writeNbt(new NbtCompound());
    }

    public boolean openScreen(PlayerEntity player) {
        if (!player.isCreativeLevelTwoOp()) {
            return false;
        }
        if (player.getEntityWorld().isClient) {
            player.openStructureBlockScreen(this);
        }
        return true;
    }

    public String getStructureName() {
        return this.structureName == null ? "" : this.structureName.toString();
    }

    public String getStructurePath() {
        return this.structureName == null ? "" : this.structureName.getPath();
    }

    public boolean hasStructureName() {
        return this.structureName != null;
    }

    public void setStructureName(@Nullable String name) {
        this.setStructureName(ChatUtil.isEmpty(name) ? null : Identifier.tryParse(name));
    }

    public void setStructureName(@Nullable Identifier structureName) {
        this.structureName = structureName;
    }

    public void setAuthor(LivingEntity entity) {
        this.author = entity.getName().getString();
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getOffset() {
        return this.offset;
    }

    public void setOffset(BlockPos pos) {
        this.offset = pos;
    }

    public BlockPos getSize() {
        return this.size;
    }

    public void setSize(BlockPos pos) {
        this.size = pos;
    }

    @Environment(value=EnvType.CLIENT)
    public BlockMirror getMirror() {
        return this.mirror;
    }

    public void setMirror(BlockMirror mirror) {
        this.mirror = mirror;
    }

    public BlockRotation getRotation() {
        return this.rotation;
    }

    public void setRotation(BlockRotation rotation) {
        this.rotation = rotation;
    }

    @Environment(value=EnvType.CLIENT)
    public String getMetadata() {
        return this.metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public StructureBlockMode getMode() {
        return this.mode;
    }

    public void setMode(StructureBlockMode mode) {
        this.mode = mode;
        BlockState blockState = this.world.getBlockState(this.getPos());
        if (blockState.isOf(Blocks.STRUCTURE_BLOCK)) {
            this.world.setBlockState(this.getPos(), (BlockState)blockState.with(StructureBlock.MODE, mode), 2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public void cycleMode() {
        switch (this.getMode()) {
            case SAVE: {
                this.setMode(StructureBlockMode.LOAD);
                break;
            }
            case LOAD: {
                this.setMode(StructureBlockMode.CORNER);
                break;
            }
            case CORNER: {
                this.setMode(StructureBlockMode.DATA);
                break;
            }
            case DATA: {
                this.setMode(StructureBlockMode.SAVE);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldIgnoreEntities() {
        return this.ignoreEntities;
    }

    public void setIgnoreEntities(boolean ignoreEntities) {
        this.ignoreEntities = ignoreEntities;
    }

    @Environment(value=EnvType.CLIENT)
    public float getIntegrity() {
        return this.integrity;
    }

    public void setIntegrity(float integrity) {
        this.integrity = integrity;
    }

    @Environment(value=EnvType.CLIENT)
    public long getSeed() {
        return this.seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public boolean detectStructureSize() {
        BlockPos blockPos3;
        if (this.mode != StructureBlockMode.SAVE) {
            return false;
        }
        BlockPos blockPos = this.getPos();
        int i = 80;
        BlockPos blockPos2 = new BlockPos(blockPos.getX() - 80, 0, blockPos.getZ() - 80);
        List<StructureBlockBlockEntity> list = this.findStructureBlockEntities(blockPos2, blockPos3 = new BlockPos(blockPos.getX() + 80, 255, blockPos.getZ() + 80));
        List<StructureBlockBlockEntity> list2 = this.findCorners(list);
        if (list2.size() < 1) {
            return false;
        }
        BlockBox blockBox = this.makeBoundingBox(blockPos, list2);
        if (blockBox.maxX - blockBox.minX > 1 && blockBox.maxY - blockBox.minY > 1 && blockBox.maxZ - blockBox.minZ > 1) {
            this.offset = new BlockPos(blockBox.minX - blockPos.getX() + 1, blockBox.minY - blockPos.getY() + 1, blockBox.minZ - blockPos.getZ() + 1);
            this.size = new BlockPos(blockBox.maxX - blockBox.minX - 1, blockBox.maxY - blockBox.minY - 1, blockBox.maxZ - blockBox.minZ - 1);
            this.markDirty();
            BlockState blockState = this.world.getBlockState(blockPos);
            this.world.updateListeners(blockPos, blockState, blockState, 3);
            return true;
        }
        return false;
    }

    private List<StructureBlockBlockEntity> findCorners(List<StructureBlockBlockEntity> structureBlockEntities) {
        Predicate<StructureBlockBlockEntity> predicate = structureBlockBlockEntity -> structureBlockBlockEntity.mode == StructureBlockMode.CORNER && Objects.equals(this.structureName, structureBlockBlockEntity.structureName);
        return structureBlockEntities.stream().filter(predicate).collect(Collectors.toList());
    }

    private List<StructureBlockBlockEntity> findStructureBlockEntities(BlockPos pos1, BlockPos pos2) {
        ArrayList list = Lists.newArrayList();
        for (BlockPos blockPos : BlockPos.iterate(pos1, pos2)) {
            BlockEntity blockEntity;
            BlockState blockState = this.world.getBlockState(blockPos);
            if (!blockState.isOf(Blocks.STRUCTURE_BLOCK) || (blockEntity = this.world.getBlockEntity(blockPos)) == null || !(blockEntity instanceof StructureBlockBlockEntity)) continue;
            list.add((StructureBlockBlockEntity)blockEntity);
        }
        return list;
    }

    private BlockBox makeBoundingBox(BlockPos center, List<StructureBlockBlockEntity> corners) {
        BlockBox blockBox;
        if (corners.size() > 1) {
            BlockPos blockPos = corners.get(0).getPos();
            blockBox = new BlockBox(blockPos, blockPos);
        } else {
            blockBox = new BlockBox(center, center);
        }
        for (StructureBlockBlockEntity structureBlockBlockEntity : corners) {
            BlockPos blockPos2 = structureBlockBlockEntity.getPos();
            if (blockPos2.getX() < blockBox.minX) {
                blockBox.minX = blockPos2.getX();
            } else if (blockPos2.getX() > blockBox.maxX) {
                blockBox.maxX = blockPos2.getX();
            }
            if (blockPos2.getY() < blockBox.minY) {
                blockBox.minY = blockPos2.getY();
            } else if (blockPos2.getY() > blockBox.maxY) {
                blockBox.maxY = blockPos2.getY();
            }
            if (blockPos2.getZ() < blockBox.minZ) {
                blockBox.minZ = blockPos2.getZ();
                continue;
            }
            if (blockPos2.getZ() <= blockBox.maxZ) continue;
            blockBox.maxZ = blockPos2.getZ();
        }
        return blockBox;
    }

    public boolean saveStructure() {
        return this.saveStructure(true);
    }

    public boolean saveStructure(boolean bl) {
        Structure structure;
        if (this.mode != StructureBlockMode.SAVE || this.world.isClient || this.structureName == null) {
            return false;
        }
        BlockPos blockPos = this.getPos().add(this.offset);
        ServerWorld serverWorld = (ServerWorld)this.world;
        StructureManager structureManager = serverWorld.getStructureManager();
        try {
            structure = structureManager.getStructureOrBlank(this.structureName);
        }
        catch (InvalidIdentifierException invalidIdentifierException) {
            return false;
        }
        structure.saveFromWorld(this.world, blockPos, this.size, !this.ignoreEntities, Blocks.STRUCTURE_VOID);
        structure.setAuthor(this.author);
        if (bl) {
            try {
                return structureManager.saveStructure(this.structureName);
            }
            catch (InvalidIdentifierException invalidIdentifierException) {
                return false;
            }
        }
        return true;
    }

    public boolean loadStructure(ServerWorld world) {
        return this.loadStructure(world, true);
    }

    private static Random createRandom(long seed) {
        if (seed == 0L) {
            return new Random(Util.getMeasuringTimeMs());
        }
        return new Random(seed);
    }

    public boolean loadStructure(ServerWorld world, boolean bl) {
        Structure structure;
        if (this.mode != StructureBlockMode.LOAD || this.structureName == null) {
            return false;
        }
        StructureManager structureManager = world.getStructureManager();
        try {
            structure = structureManager.getStructure(this.structureName);
        }
        catch (InvalidIdentifierException invalidIdentifierException) {
            return false;
        }
        if (structure == null) {
            return false;
        }
        return this.place(world, bl, structure);
    }

    public boolean place(ServerWorld world, boolean bl, Structure structure) {
        BlockPos blockPos2;
        boolean bl2;
        BlockPos blockPos = this.getPos();
        if (!ChatUtil.isEmpty(structure.getAuthor())) {
            this.author = structure.getAuthor();
        }
        if (!(bl2 = this.size.equals(blockPos2 = structure.getSize()))) {
            this.size = blockPos2;
            this.markDirty();
            BlockState blockState = world.getBlockState(blockPos);
            world.updateListeners(blockPos, blockState, blockState, 3);
        }
        if (!bl || bl2) {
            StructurePlacementData structurePlacementData = new StructurePlacementData().setMirror(this.mirror).setRotation(this.rotation).setIgnoreEntities(this.ignoreEntities).setChunkPosition(null);
            if (this.integrity < 1.0f) {
                structurePlacementData.clearProcessors().addProcessor(new BlockRotStructureProcessor(MathHelper.clamp(this.integrity, 0.0f, 1.0f))).setRandom(StructureBlockBlockEntity.createRandom(this.seed));
            }
            BlockPos blockPos3 = blockPos.add(this.offset);
            structure.place(world, blockPos3, structurePlacementData, StructureBlockBlockEntity.createRandom(this.seed));
            return true;
        }
        return false;
    }

    public void unloadStructure() {
        if (this.structureName == null) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld)this.world;
        StructureManager structureManager = serverWorld.getStructureManager();
        structureManager.unloadStructure(this.structureName);
    }

    public boolean isStructureAvailable() {
        if (this.mode != StructureBlockMode.LOAD || this.world.isClient || this.structureName == null) {
            return false;
        }
        ServerWorld serverWorld = (ServerWorld)this.world;
        StructureManager structureManager = serverWorld.getStructureManager();
        try {
            return structureManager.getStructure(this.structureName) != null;
        }
        catch (InvalidIdentifierException invalidIdentifierException) {
            return false;
        }
    }

    public boolean isPowered() {
        return this.powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldShowAir() {
        return this.showAir;
    }

    public void setShowAir(boolean showAir) {
        this.showAir = showAir;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldShowBoundingBox() {
        return this.showBoundingBox;
    }

    public void setShowBoundingBox(boolean showBoundingBox) {
        this.showBoundingBox = showBoundingBox;
    }

    public static enum Action {
        UPDATE_DATA,
        SAVE_AREA,
        LOAD_AREA,
        SCAN_AREA;

    }
}

