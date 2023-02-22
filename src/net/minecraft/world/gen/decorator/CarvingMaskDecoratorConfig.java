/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class CarvingMaskDecoratorConfig
implements DecoratorConfig {
    protected final GenerationStep.Carver step;
    protected final float probability;

    public CarvingMaskDecoratorConfig(GenerationStep.Carver step, float probability) {
        this.step = step;
        this.probability = probability;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic(ops, ops.createMap((Map)ImmutableMap.of((Object)ops.createString("step"), (Object)ops.createString(this.step.toString()), (Object)ops.createString("probability"), (Object)ops.createFloat(this.probability))));
    }

    public static CarvingMaskDecoratorConfig deserialize(Dynamic<?> dynamic) {
        GenerationStep.Carver carver = GenerationStep.Carver.valueOf(dynamic.get("step").asString(""));
        float f = dynamic.get("probability").asFloat(0.0f);
        return new CarvingMaskDecoratorConfig(carver, f);
    }
}

