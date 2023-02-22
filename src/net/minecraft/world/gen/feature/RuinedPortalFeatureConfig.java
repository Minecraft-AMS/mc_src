/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeature;

public class RuinedPortalFeatureConfig
implements FeatureConfig {
    public static final Codec<RuinedPortalFeatureConfig> CODEC = RuinedPortalFeature.Type.CODEC.fieldOf("portal_type").xmap(RuinedPortalFeatureConfig::new, ruinedPortalFeatureConfig -> ruinedPortalFeatureConfig.portalType).codec();
    public final RuinedPortalFeature.Type portalType;

    public RuinedPortalFeatureConfig(RuinedPortalFeature.Type portalType) {
        this.portalType = portalType;
    }
}

