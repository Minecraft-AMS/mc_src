/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.gen.stateprovider;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import org.jetbrains.annotations.Nullable;

public class RandomizedIntBlockStateProvider
extends BlockStateProvider {
    public static final Codec<RandomizedIntBlockStateProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockStateProvider.TYPE_CODEC.fieldOf("source").forGetter(randomizedIntBlockStateProvider -> randomizedIntBlockStateProvider.source), (App)Codec.STRING.fieldOf("property").forGetter(randomizedIntBlockStateProvider -> randomizedIntBlockStateProvider.propertyName), (App)IntProvider.VALUE_CODEC.fieldOf("values").forGetter(randomizedIntBlockStateProvider -> randomizedIntBlockStateProvider.values)).apply((Applicative)instance, RandomizedIntBlockStateProvider::new));
    private final BlockStateProvider source;
    private final String propertyName;
    @Nullable
    private IntProperty property;
    private final IntProvider values;

    public RandomizedIntBlockStateProvider(BlockStateProvider source, IntProperty property, IntProvider values) {
        this.source = source;
        this.property = property;
        this.propertyName = property.getName();
        this.values = values;
        Collection<Integer> collection = property.getValues();
        for (int i = values.getMin(); i <= values.getMax(); ++i) {
            if (collection.contains(i)) continue;
            throw new IllegalArgumentException("Property value out of range: " + property.getName() + ": " + i);
        }
    }

    public RandomizedIntBlockStateProvider(BlockStateProvider source, String propertyName, IntProvider values) {
        this.source = source;
        this.propertyName = propertyName;
        this.values = values;
    }

    @Override
    protected BlockStateProviderType<?> getType() {
        return BlockStateProviderType.RANDOMIZED_INT_STATE_PROVIDER;
    }

    @Override
    public BlockState getBlockState(Random random, BlockPos pos) {
        BlockState blockState = this.source.getBlockState(random, pos);
        if (this.property == null || !blockState.contains(this.property)) {
            this.property = RandomizedIntBlockStateProvider.getIntPropertyByName(blockState, this.propertyName);
        }
        return (BlockState)blockState.with(this.property, this.values.get(random));
    }

    private static IntProperty getIntPropertyByName(BlockState state, String propertyName) {
        Collection<Property<?>> collection = state.getProperties();
        Optional<IntProperty> optional = collection.stream().filter(property -> property.getName().equals(propertyName)).filter(property -> property instanceof IntProperty).map(property -> (IntProperty)property).findAny();
        return optional.orElseThrow(() -> new IllegalArgumentException("Illegal property: " + propertyName));
    }
}

