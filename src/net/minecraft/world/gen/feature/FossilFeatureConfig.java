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
import java.util.function.Supplier;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;

public class FossilFeatureConfig
implements FeatureConfig {
    public static final Codec<FossilFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Identifier.CODEC.listOf().fieldOf("fossil_structures").forGetter(fossilFeatureConfig -> fossilFeatureConfig.fossilStructures), (App)Identifier.CODEC.listOf().fieldOf("overlay_structures").forGetter(fossilFeatureConfig -> fossilFeatureConfig.overlayStructures), (App)StructureProcessorType.REGISTRY_CODEC.fieldOf("fossil_processors").forGetter(fossilFeatureConfig -> fossilFeatureConfig.fossilProcessors), (App)StructureProcessorType.REGISTRY_CODEC.fieldOf("overlay_processors").forGetter(fossilFeatureConfig -> fossilFeatureConfig.overlayProcessors), (App)Codec.intRange((int)0, (int)7).fieldOf("max_empty_corners_allowed").forGetter(fossilFeatureConfig -> fossilFeatureConfig.maxEmptyCorners)).apply((Applicative)instance, FossilFeatureConfig::new));
    public final List<Identifier> fossilStructures;
    public final List<Identifier> overlayStructures;
    public final Supplier<StructureProcessorList> fossilProcessors;
    public final Supplier<StructureProcessorList> overlayProcessors;
    public final int maxEmptyCorners;

    public FossilFeatureConfig(List<Identifier> fossilStructures, List<Identifier> overlayStructures, Supplier<StructureProcessorList> fossilProcessors, Supplier<StructureProcessorList> overlayProcessors, int maxEmptyCorners) {
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

    public FossilFeatureConfig(List<Identifier> fossilStructures, List<Identifier> overlayStructures, StructureProcessorList fossilProcessors, StructureProcessorList overlayProcessors, int maxEmptyCorners) {
        this(fossilStructures, overlayStructures, () -> fossilProcessors, () -> overlayProcessors, maxEmptyCorners);
    }
}

