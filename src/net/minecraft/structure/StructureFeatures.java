/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.structure;

import java.util.Locale;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class StructureFeatures {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final StructureFeature<?> MINESHAFT = StructureFeatures.register("Mineshaft", Feature.MINESHAFT);
    public static final StructureFeature<?> PILLAGER_OUTPOST = StructureFeatures.register("Pillager_Outpost", Feature.PILLAGER_OUTPOST);
    public static final StructureFeature<?> FORTRESS = StructureFeatures.register("Fortress", Feature.NETHER_BRIDGE);
    public static final StructureFeature<?> STRONGHOLD = StructureFeatures.register("Stronghold", Feature.STRONGHOLD);
    public static final StructureFeature<?> JUNGLE_PYRAMID = StructureFeatures.register("Jungle_Pyramid", Feature.JUNGLE_TEMPLE);
    public static final StructureFeature<?> OCEAN_RUIN = StructureFeatures.register("Ocean_Ruin", Feature.OCEAN_RUIN);
    public static final StructureFeature<?> DESERT_PYRAMID = StructureFeatures.register("Desert_Pyramid", Feature.DESERT_PYRAMID);
    public static final StructureFeature<?> IGLOO = StructureFeatures.register("Igloo", Feature.IGLOO);
    public static final StructureFeature<?> SWAMP_HUT = StructureFeatures.register("Swamp_Hut", Feature.SWAMP_HUT);
    public static final StructureFeature<?> MONUMENT = StructureFeatures.register("Monument", Feature.OCEAN_MONUMENT);
    public static final StructureFeature<?> END_CITY = StructureFeatures.register("EndCity", Feature.END_CITY);
    public static final StructureFeature<?> MANSION = StructureFeatures.register("Mansion", Feature.WOODLAND_MANSION);
    public static final StructureFeature<?> BURIED_TREASURE = StructureFeatures.register("Buried_Treasure", Feature.BURIED_TREASURE);
    public static final StructureFeature<?> SHIPWRECK = StructureFeatures.register("Shipwreck", Feature.SHIPWRECK);
    public static final StructureFeature<?> VILLAGE = StructureFeatures.register("Village", Feature.VILLAGE);

    private static StructureFeature<?> register(String name, StructureFeature<?> feature) {
        return Registry.register(Registry.STRUCTURE_FEATURE, name.toLowerCase(Locale.ROOT), feature);
    }

    public static void initialize() {
    }

    @Nullable
    public static StructureStart readStructureStart(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, BiomeSource biomeSource, CompoundTag tag) {
        String string = tag.getString("id");
        if ("INVALID".equals(string)) {
            return StructureStart.DEFAULT;
        }
        StructureFeature<?> structureFeature = Registry.STRUCTURE_FEATURE.get(new Identifier(string.toLowerCase(Locale.ROOT)));
        if (structureFeature == null) {
            LOGGER.error("Unknown feature id: {}", (Object)string);
            return null;
        }
        int i = tag.getInt("ChunkX");
        int j = tag.getInt("ChunkZ");
        Biome biome = tag.contains("biome") ? Registry.BIOME.get(new Identifier(tag.getString("biome"))) : biomeSource.getBiome(new BlockPos((i << 4) + 9, 0, (j << 4) + 9));
        BlockBox blockBox = tag.contains("BB") ? new BlockBox(tag.getIntArray("BB")) : BlockBox.empty();
        ListTag listTag = tag.getList("Children", 10);
        try {
            StructureStart structureStart = structureFeature.getStructureStartFactory().create(structureFeature, i, j, biome, blockBox, 0, chunkGenerator.getSeed());
            for (int k = 0; k < listTag.size(); ++k) {
                CompoundTag compoundTag = listTag.getCompound(k);
                String string2 = compoundTag.getString("id");
                StructurePieceType structurePieceType = Registry.STRUCTURE_PIECE.get(new Identifier(string2.toLowerCase(Locale.ROOT)));
                if (structurePieceType == null) {
                    LOGGER.error("Unknown structure piece id: {}", (Object)string2);
                    continue;
                }
                try {
                    StructurePiece structurePiece = structurePieceType.load(structureManager, compoundTag);
                    structureStart.children.add(structurePiece);
                    continue;
                }
                catch (Exception exception) {
                    LOGGER.error("Exception loading structure piece with id {}", (Object)string2, (Object)exception);
                }
            }
            return structureStart;
        }
        catch (Exception exception2) {
            LOGGER.error("Failed Start with id {}", (Object)string, (Object)exception2);
            return null;
        }
    }
}

