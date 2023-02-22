/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.resource;

import java.io.Closeable;
import java.io.InputStream;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface Resource
extends Closeable {
    public Identifier getId();

    public InputStream getInputStream();

    public boolean hasMetadata();

    @Nullable
    public <T> T getMetadata(ResourceMetadataReader<T> var1);

    public String getResourcePackName();
}

