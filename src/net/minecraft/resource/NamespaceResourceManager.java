/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class NamespaceResourceManager
implements ResourceManager {
    static final Logger LOGGER = LogUtils.getLogger();
    protected final List<ResourcePack> packList = Lists.newArrayList();
    private final ResourceType type;
    private final String namespace;

    public NamespaceResourceManager(ResourceType type, String namespace) {
        this.type = type;
        this.namespace = namespace;
    }

    public void addPack(ResourcePack pack) {
        this.packList.add(pack);
    }

    @Override
    public Set<String> getAllNamespaces() {
        return ImmutableSet.of((Object)this.namespace);
    }

    @Override
    public Resource getResource(Identifier identifier) throws IOException {
        this.validate(identifier);
        ResourcePack resourcePack = null;
        Identifier identifier2 = NamespaceResourceManager.getMetadataPath(identifier);
        for (int i = this.packList.size() - 1; i >= 0; --i) {
            ResourcePack resourcePack2 = this.packList.get(i);
            if (resourcePack == null && resourcePack2.contains(this.type, identifier2)) {
                resourcePack = resourcePack2;
            }
            if (!resourcePack2.contains(this.type, identifier)) continue;
            InputStream inputStream = null;
            if (resourcePack != null) {
                inputStream = this.open(identifier2, resourcePack);
            }
            return new ResourceImpl(resourcePack2.getName(), identifier, this.open(identifier, resourcePack2), inputStream);
        }
        throw new FileNotFoundException(identifier.toString());
    }

    @Override
    public boolean containsResource(Identifier id) {
        if (!this.isPathAbsolute(id)) {
            return false;
        }
        for (int i = this.packList.size() - 1; i >= 0; --i) {
            ResourcePack resourcePack = this.packList.get(i);
            if (!resourcePack.contains(this.type, id)) continue;
            return true;
        }
        return false;
    }

    protected InputStream open(Identifier id, ResourcePack pack) throws IOException {
        InputStream inputStream = pack.open(this.type, id);
        return LOGGER.isDebugEnabled() ? new DebugInputStream(inputStream, id, pack.getName()) : inputStream;
    }

    private void validate(Identifier id) throws IOException {
        if (!this.isPathAbsolute(id)) {
            throw new IOException("Invalid relative path to resource: " + id);
        }
    }

    private boolean isPathAbsolute(Identifier id) {
        return !id.getPath().contains("..");
    }

    @Override
    public List<Resource> getAllResources(Identifier id) throws IOException {
        this.validate(id);
        ArrayList list = Lists.newArrayList();
        Identifier identifier = NamespaceResourceManager.getMetadataPath(id);
        for (ResourcePack resourcePack : this.packList) {
            if (!resourcePack.contains(this.type, id)) continue;
            InputStream inputStream = resourcePack.contains(this.type, identifier) ? this.open(identifier, resourcePack) : null;
            list.add(new ResourceImpl(resourcePack.getName(), id, this.open(id, resourcePack), inputStream));
        }
        if (list.isEmpty()) {
            throw new FileNotFoundException(id.toString());
        }
        return list;
    }

    @Override
    public Collection<Identifier> findResources(String startingPath, Predicate<String> pathPredicate) {
        ArrayList list = Lists.newArrayList();
        for (ResourcePack resourcePack : this.packList) {
            list.addAll(resourcePack.findResources(this.type, this.namespace, startingPath, Integer.MAX_VALUE, pathPredicate));
        }
        Collections.sort(list);
        return list;
    }

    @Override
    public Stream<ResourcePack> streamResourcePacks() {
        return this.packList.stream();
    }

    static Identifier getMetadataPath(Identifier id) {
        return new Identifier(id.getNamespace(), id.getPath() + ".mcmeta");
    }

    static class DebugInputStream
    extends FilterInputStream {
        private final String leakMessage;
        private boolean closed;

        public DebugInputStream(InputStream parent, Identifier id, String packName) {
            super(parent);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new Exception().printStackTrace(new PrintStream(byteArrayOutputStream));
            this.leakMessage = "Leaked resource: '" + id + "' loaded from pack: '" + packName + "'\n" + byteArrayOutputStream;
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.closed = true;
        }

        protected void finalize() throws Throwable {
            if (!this.closed) {
                LOGGER.warn(this.leakMessage);
            }
            super.finalize();
        }
    }
}

