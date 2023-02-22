/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.structure;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class ShipwreckStructure
extends Structure {
    public static final Codec<ShipwreckStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(ShipwreckStructure.configCodecBuilder(instance), (App)Codec.BOOL.fieldOf("is_beached").forGetter(shipwreckStructure -> shipwreckStructure.beached)).apply((Applicative)instance, ShipwreckStructure::new));
    public final boolean beached;

    public ShipwreckStructure(Structure.Config config, boolean beached) {
        super(config);
        this.beached = beached;
    }

    @Override
    public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
        Heightmap.Type type = this.beached ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG;
        return ShipwreckStructure.getStructurePosition(context, type, collector -> this.addPieces((StructurePiecesCollector)collector, context));
    }

    private void addPieces(StructurePiecesCollector collector, Structure.Context context) {
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 90, context.chunkPos().getStartZ());
        ShipwreckGenerator.addParts(context.structureTemplateManager(), blockPos, blockRotation, collector, context.random(), this.beached);
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.SHIPWRECK;
    }
}

