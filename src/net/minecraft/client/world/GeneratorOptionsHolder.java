/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.world;

import com.mojang.serialization.Lifecycle;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.DataPackContents;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;

@Environment(value=EnvType.CLIENT)
public record GeneratorOptionsHolder(GeneratorOptions generatorOptions, Lifecycle worldSettingsStability, DynamicRegistryManager.Immutable dynamicRegistryManager, DataPackContents dataPackContents) {
    public GeneratorOptionsHolder with(GeneratorOptions generatorOptions) {
        return new GeneratorOptionsHolder(generatorOptions, this.worldSettingsStability, this.dynamicRegistryManager, this.dataPackContents);
    }

    public GeneratorOptionsHolder apply(Modifier modifier) {
        GeneratorOptions generatorOptions = (GeneratorOptions)modifier.apply(this.generatorOptions);
        return this.with(generatorOptions);
    }

    public GeneratorOptionsHolder apply(RegistryAwareModifier modifier) {
        GeneratorOptions generatorOptions = (GeneratorOptions)modifier.apply(this.dynamicRegistryManager, this.generatorOptions);
        return this.with(generatorOptions);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{GeneratorOptionsHolder.class, "worldGenSettings;worldSettingsStability;registryAccess;dataPackResources", "generatorOptions", "worldSettingsStability", "dynamicRegistryManager", "dataPackContents"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GeneratorOptionsHolder.class, "worldGenSettings;worldSettingsStability;registryAccess;dataPackResources", "generatorOptions", "worldSettingsStability", "dynamicRegistryManager", "dataPackContents"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GeneratorOptionsHolder.class, "worldGenSettings;worldSettingsStability;registryAccess;dataPackResources", "generatorOptions", "worldSettingsStability", "dynamicRegistryManager", "dataPackContents"}, this, object);
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface Modifier
    extends UnaryOperator<GeneratorOptions> {
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface RegistryAwareModifier
    extends BiFunction<DynamicRegistryManager.Immutable, GeneratorOptions, GeneratorOptions> {
    }
}

