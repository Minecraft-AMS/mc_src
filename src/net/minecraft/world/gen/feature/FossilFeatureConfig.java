/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.FeatureConfig;

public class FossilFeatureConfig
implements FeatureConfig {
    public static final Codec<FossilFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Identifier.CODEC.listOf().fieldOf("fossil_structures").forGetter(config -> config.fossilStructures), (App)Identifier.CODEC.listOf().fieldOf("overlay_structures").forGetter(config -> config.overlayStructures), (App)StructureProcessorType.REGISTRY_CODEC.fieldOf("fossil_processors").forGetter(config -> config.fossilProcessors), (App)StructureProcessorType.REGISTRY_CODEC.fieldOf("overlay_processors").forGetter(config -> config.overlayProcessors), (App)Codec.intRange((int)0, (int)7).fieldOf("max_empty_corners_allowed").forGetter(config -> config.maxEmptyCorners)).apply((Applicative)instance, FossilFeatureConfig::new));
    public final List<Identifier> fossilStructures;
    public final List<Identifier> overlayStructures;
    public final RegistryEntry<StructureProcessorList> fossilProcessors;
    public final RegistryEntry<StructureProcessorList> overlayProcessors;
    public final int maxEmptyCorners;

    public FossilFeatureConfig(List<Identifier> fossilStructures, List<Identifier> overlayStructures, RegistryEntry<StructureProcessorList> fossilProcessors, RegistryEntry<StructureProcessorList> overlayProcessors, int maxEmptyCorners) {
        if (fossilStructures.isEmpty()) {
            throw new IllegalArgumentException("Fossil structure lists need at least one entry");
        }
        if (fossilStructures.size() != overlayStructures.size()) {
            throw new IllegalArgumentException("Fossil structure lists must be equal lengths");
        }
        this.fossilStructures = fossilStructures;
        this.overlayStructures = overlayStructures;
        this.fossilProcessors = fossilProcessors;
        this.overlayProcessors = overlayProcessors;
        this.maxEmptyCorners = maxEmptyCorners;
    }
}

