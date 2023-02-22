/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.resource.metadata.ResourceMetadata;
import org.jetbrains.annotations.Nullable;

public class Resource {
    private final String resourcePackName;
    private final InputSupplier<InputStream> inputSupplier;
    private final InputSupplier<ResourceMetadata> metadataSupplier;
    @Nullable
    private ResourceMetadata metadata;

    public Resource(String resourcePackName, InputSupplier<InputStream> inputSupplier, InputSupplier<ResourceMetadata> metadataSupplier) {
        this.resourcePackName = resourcePackName;
        this.inputSupplier = inputSupplier;
        this.metadataSupplier = metadataSupplier;
    }

    public Resource(String resourcePackName, InputSupplier<InputStream> inputSupplier) {
        this.resourcePackName = resourcePackName;
        this.inputSupplier = inputSupplier;
        this.metadataSupplier = () -> ResourceMetadata.NONE;
        this.metadata = ResourceMetadata.NONE;
    }

    public String getResourcePackName() {
        return this.resourcePackName;
    }

    public InputStream getInputStream() throws IOException {
        return this.inputSupplier.get();
    }

    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream(), StandardCharsets.UTF_8));
    }

    public ResourceMetadata getMetadata() throws IOException {
        if (this.metadata == null) {
            this.metadata = this.metadataSupplier.get();
        }
        return this.metadata;
    }

    @FunctionalInterface
    public static interface InputSupplier<T> {
        public T get() throws IOException;
    }
}

