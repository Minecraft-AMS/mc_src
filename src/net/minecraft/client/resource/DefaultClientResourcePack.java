/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DefaultClientResourcePack
extends DefaultResourcePack {
    private final ResourceIndex index;

    public DefaultClientResourcePack(ResourceIndex resourceIndex) {
        super("minecraft", "realms");
        this.index = resourceIndex;
    }

    @Override
    @Nullable
    protected InputStream findInputStream(ResourceType type, Identifier id) {
        File file;
        if (type == ResourceType.CLIENT_RESOURCES && (file = this.index.getResource(id)) != null && file.exists()) {
            try {
                return new FileInputStream(file);
            }
            catch (FileNotFoundException fileNotFoundException) {
                // empty catch block
            }
        }
        return super.findInputStream(type, id);
    }

    @Override
    public boolean contains(ResourceType type, Identifier id) {
        File file;
        if (type == ResourceType.CLIENT_RESOURCES && (file = this.index.getResource(id)) != null && file.exists()) {
            return true;
        }
        return super.contains(type, id);
    }

    @Override
    @Nullable
    protected InputStream getInputStream(String path) {
        File file = this.index.findFile(path);
        if (file != null && file.exists()) {
            try {
                return new FileInputStream(file);
            }
            catch (FileNotFoundException fileNotFoundException) {
                // empty catch block
            }
        }
        return super.getInputStream(path);
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, int maxDepth, Predicate<String> pathFilter) {
        Collection<Identifier> collection = super.findResources(type, namespace, maxDepth, pathFilter);
        collection.addAll(this.index.getFilesRecursively(namespace, maxDepth, pathFilter).stream().map(Identifier::new).collect(Collectors.toList()));
        return collection;
    }
}
