/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.structure;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.slf4j.Logger;

public abstract class SimpleStructurePiece
extends StructurePiece {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final String templateIdString;
    protected StructureTemplate template;
    protected StructurePlacementData placementData;
    protected BlockPos pos;

    public SimpleStructurePiece(StructurePieceType type, int length, StructureTemplateManager structureTemplateManager, Identifier id, String template, StructurePlacementData placementData, BlockPos pos) {
        super(type, length, structureTemplateManager.getTemplateOrBlank(id).calculateBoundingBox(placementData, pos));
        this.setOrientation(Direction.NORTH);
        this.templateIdString = template;
        this.pos = pos;
        this.template = structureTemplateManager.getTemplateOrBlank(id);
        this.placementData = placementData;
    }

    public SimpleStructurePiece(StructurePieceType type, NbtCompound nbt, StructureTemplateManager structureTemplateManager, Function<Identifier, StructurePlacementData> placementDataGetter) {
        super(type, nbt);
        this.setOrientation(Direction.NORTH);
        this.templateIdString = nbt.getString("Template");
        this.pos = new BlockPos(nbt.getInt("TPX"), nbt.getInt("TPY"), nbt.getInt("TPZ"));
        Identifier identifier = this.getId();
        this.template = structureTemplateManager.getTemplateOrBlank(identifier);
        this.placementData = placementDataGetter.apply(identifier);
        this.boundingBox = this.template.calculateBoundingBox(this.placementData, this.pos);
    }

    protected Identifier getId() {
        return new Identifier(this.templateIdString);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        nbt.putInt("TPX", this.pos.getX());
        nbt.putInt("TPY", this.pos.getY());
        nbt.putInt("TPZ", this.pos.getZ());
        nbt.putString("Template", this.templateIdString);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        this.placementData.setBoundingBox(chunkBox);
        this.boundingBox = this.template.calculateBoundingBox(this.placementData, this.pos);
        if (this.template.place(world, this.pos, pivot, this.placementData, random, 2)) {
            List<StructureTemplate.StructureBlockInfo> list = this.template.getInfosForBlock(this.pos, this.placementData, Blocks.STRUCTURE_BLOCK);
            for (StructureTemplate.StructureBlockInfo structureBlockInfo : list) {
                StructureBlockMode structureBlockMode;
                if (structureBlockInfo.nbt() == null || (structureBlockMode = StructureBlockMode.valueOf(structureBlockInfo.nbt().getString("mode"))) != StructureBlockMode.DATA) continue;
                this.handleMetadata(structureBlockInfo.nbt().getString("metadata"), structureBlockInfo.pos(), world, random, chunkBox);
            }
            List<StructureTemplate.StructureBlockInfo> list2 = this.template.getInfosForBlock(this.pos, this.placementData, Blocks.JIGSAW);
            for (StructureTemplate.StructureBlockInfo structureBlockInfo2 : list2) {
                if (structureBlockInfo2.nbt() == null) continue;
                String string = structureBlockInfo2.nbt().getString("final_state");
                BlockState blockState = Blocks.AIR.getDefaultState();
                try {
                    blockState = BlockArgumentParser.block(world.createCommandRegistryWrapper(RegistryKeys.BLOCK), string, true).blockState();
                }
                catch (CommandSyntaxException commandSyntaxException) {
                    LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", (Object)string, (Object)structureBlockInfo2.pos());
                }
                world.setBlockState(structureBlockInfo2.pos(), blockState, 3);
            }
        }
    }

    protected abstract void handleMetadata(String var1, BlockPos var2, ServerWorldAccess var3, Random var4, BlockBox var5);

    @Override
    @Deprecated
    public void translate(int x, int y, int z) {
        super.translate(x, y, z);
        this.pos = this.pos.add(x, y, z);
    }

    @Override
    public BlockRotation getRotation() {
        return this.placementData.getRotation();
    }

    public StructureTemplate getTemplate() {
        return this.template;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public StructurePlacementData getPlacementData() {
        return this.placementData;
    }
}

