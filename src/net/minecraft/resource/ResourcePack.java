/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface ResourcePack
extends AutoCloseable {
    public static final String METADATA_PATH_SUFFIX = ".mcmeta";
    public static final String PACK_METADATA_NAME = "pack.mcmeta";

    @Nullable
    public InputStream openRoot(String var1) throws IOException;

    public InputStream open(ResourceType var1, Identifier var2) throws IOException;

    public Collection<Identifier> findResources(ResourceType var1, String var2, String var3, int var4, Predicate<String> var5);

    public boolean contains(ResourceType var1, Identifier var2);

    public Set<String> getNamespaces(ResourceType var1);

    @Nullable
    public <T> T parseMetadata(ResourceMetadataReader<T> var1) throws IOException;

    public String getName();

    @Override
    public void close();
}

