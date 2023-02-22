/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.gen.heightprovider;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.HeightProviderType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UniformHeightProvider
extends HeightProvider {
    public static final Codec<UniformHeightProvider> UNIFORM_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)YOffset.OFFSET_CODEC.fieldOf("min_inclusive").forGetter(uniformHeightProvider -> uniformHeightProvider.minOffset), (App)YOffset.OFFSET_CODEC.fieldOf("max_inclusive").forGetter(uniformHeightProvider -> uniformHeightProvider.maxOffset)).apply((Applicative)instance, UniformHeightProvider::new));
    private static final Logger LOGGER = LogManager.getLogger();
    private final YOffset minOffset;
    private final YOffset maxOffset;

    private UniformHeightProvider(YOffset minOffset, YOffset maxOffset) {
        this.minOffset = minOffset;
        this.maxOffset = maxOffset;
    }

    public static UniformHeightProvider create(YOffset minOffset, YOffset maxOffset) {
        return new UniformHeightProvider(minOffset, maxOffset);
    }

    @Override
    public int get(Random random, HeightContext context) {
        int j;
        int i = this.minOffset.getY(context);
        if (i > (j = this.maxOffset.getY(context))) {
            LOGGER.warn("Empty height range: {}", (Object)this);
            return i;
        }
        return MathHelper.nextBetween(random, i, j);
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.UNIFORM;
    }

    public String toString() {
        return "[" + this.minOffset + "-" + this.maxOffset + "]";
    }
}

