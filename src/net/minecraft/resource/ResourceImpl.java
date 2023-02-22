/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.resource;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.UncaughtExceptionLogger;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ResourceImpl
implements Resource {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Executor RESOURCE_IO_EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Resource IO {0}").setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new UncaughtExceptionLogger(LOGGER)).build());
    private final String packName;
    private final Identifier id;
    private final InputStream inputStream;
    private final InputStream metaInputStream;
    @Environment(value=EnvType.CLIENT)
    private boolean readMetadata;
    @Environment(value=EnvType.CLIENT)
    private JsonObject metadata;

    public ResourceImpl(String packName, Identifier id, InputStream inputStream, @Nullable InputStream metaInputStream) {
        this.packName = packName;
        this.id = id;
        this.inputStream = inputStream;
        this.metaInputStream = metaInputStream;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Identifier getId() {
        return this.id;
    }

    @Override
    public InputStream getInputStream() {
        return this.inputStream;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasMetadata() {
        return this.metaInputStream != null;
    }

    @Override
    @Nullable
    @Environment(value=EnvType.CLIENT)
    public <T> T getMetadata(ResourceMetadataReader<T> metaReader) {
        if (!this.hasMetadata()) {
            return null;
        }
        if (this.metadata == null && !this.readMetadata) {
            this.readMetadata = true;
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(this.metaInputStream, StandardCharsets.UTF_8));
                this.metadata = JsonHelper.deserialize(bufferedReader);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(bufferedReader);
                throw throwable;
            }
            IOUtils.closeQuietly((Reader)bufferedReader);
        }
        if (this.metadata == null) {
            return null;
        }
        String string = metaReader.getKey();
        return this.metadata.has(string) ? (T)metaReader.fromJson(JsonHelper.getObject(this.metadata, string)) : null;
    }

    @Override
    public String getResourcePackName() {
        return this.packName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceImpl)) {
            return false;
        }
        ResourceImpl resourceImpl = (ResourceImpl)o;
        if (this.id != null ? !this.id.equals(resourceImpl.id) : resourceImpl.id != null) {
            return false;
        }
        return !(this.packName != null ? !this.packName.equals(resourceImpl.packName) : resourceImpl.packName != null);
    }

    public int hashCode() {
        int i = this.packName != null ? this.packName.hashCode() : 0;
        i = 31 * i + (this.id != null ? this.id.hashCode() : 0);
        return i;
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
        if (this.metaInputStream != null) {
            this.metaInputStream.close();
        }
    }
}

