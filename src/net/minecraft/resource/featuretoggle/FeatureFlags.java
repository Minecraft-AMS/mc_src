/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.resource.featuretoggle;

import com.mojang.serialization.Codec;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureManager;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

public class FeatureFlags {
    public static final FeatureFlag VANILLA;
    public static final FeatureFlag BUNDLE;
    public static final FeatureManager FEATURE_MANAGER;
    public static final Codec<FeatureSet> CODEC;
    public static final FeatureSet VANILLA_FEATURES;
    public static final FeatureSet DEFAULT_ENABLED_FEATURES;

    public static String printMissingFlags(FeatureSet featuresToCheck, FeatureSet features) {
        return FeatureFlags.printMissingFlags(FEATURE_MANAGER, featuresToCheck, features);
    }

    public static String printMissingFlags(FeatureManager featureManager, FeatureSet featuresToCheck, FeatureSet features) {
        Set<Identifier> set = featureManager.toId(features);
        Set<Identifier> set2 = featureManager.toId(featuresToCheck);
        return set.stream().filter(id -> !set2.contains(id)).map(Identifier::toString).collect(Collectors.joining(", "));
    }

    public static boolean isNotVanilla(FeatureSet features) {
        return !features.isSubsetOf(VANILLA_FEATURES);
    }

    static {
        FeatureManager.Builder builder = new FeatureManager.Builder("main");
        VANILLA = builder.addVanillaFlag("vanilla");
        BUNDLE = builder.addVanillaFlag("bundle");
        FEATURE_MANAGER = builder.build();
        CODEC = FEATURE_MANAGER.getCodec();
        DEFAULT_ENABLED_FEATURES = VANILLA_FEATURES = FeatureSet.of(VANILLA);
    }
}

