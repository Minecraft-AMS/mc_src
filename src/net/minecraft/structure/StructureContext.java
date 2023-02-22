/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.registry.DynamicRegistryManager;

public record StructureContext(ResourceManager resourceManager, DynamicRegistryManager registryManager, StructureManager structureManager) {
    public static StructureContext from(ServerWorld world) {
        MinecraftServer minecraftServer = world.getServer();
        return new StructureContext(minecraftServer.getResourceManager(), minecraftServer.getRegistryManager(), minecraftServer.getStructureManager());
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{StructureContext.class, "resourceManager;registryAccess;structureManager", "resourceManager", "registryManager", "structureManager"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StructureContext.class, "resourceManager;registryAccess;structureManager", "resourceManager", "registryManager", "structureManager"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StructureContext.class, "resourceManager;registryAccess;structureManager", "resourceManager", "registryManager", "structureManager"}, this, object);
    }
}

