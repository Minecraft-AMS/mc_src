/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class CarvingMaskDecoratorConfig
implements DecoratorConfig {
    public static final Codec<CarvingMaskDecoratorConfig> CODEC = GenerationStep.Carver.CODEC.fieldOf("step").xmap(CarvingMaskDecoratorConfig::new, config -> config.carver).codec();
    protected final GenerationStep.Carver carver;

    public CarvingMaskDecoratorConfig(GenerationStep.Carver carver) {
        this.carver = carver;
    }
}

