/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface ResourceFactory {
    public Optional<Resource> getResource(Identifier var1);

    default public Resource getResourceOrThrow(Identifier identifier) throws FileNotFoundException {
        return this.getResource(identifier).orElseThrow(() -> new FileNotFoundException(identifier.toString()));
    }

    default public InputStream open(Identifier identifier) throws IOException {
        return this.getResourceOrThrow(identifier).getInputStream();
    }

    default public BufferedReader openAsReader(Identifier identifier) throws IOException {
        return this.getResourceOrThrow(identifier).getReader();
    }
}

