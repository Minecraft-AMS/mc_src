/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DirectResourceIndex
extends ResourceIndex {
    private final File assetDir;

    public DirectResourceIndex(File assetDir) {
        this.assetDir = assetDir;
    }

    @Override
    public File getResource(Identifier identifier) {
        return new File(this.assetDir, identifier.toString().replace(':', '/'));
    }

    @Override
    public File findFile(String path) {
        return new File(this.assetDir, path);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Collection<String> getFilesRecursively(String namespace, int maxDepth, Predicate<String> filter) {
        Path path2 = this.assetDir.toPath().resolve("minecraft/");
        try (Stream<Path> stream2222 = Files.walk(path2.resolve(namespace), maxDepth, new FileVisitOption[0]);){
            Collection collection = stream2222.filter(path -> Files.isRegularFile(path, new LinkOption[0])).filter(path -> !path.endsWith(".mcmeta")).map(path2::relativize).map(Object::toString).map(string -> string.replaceAll("\\\\", "/")).filter(filter).collect(Collectors.toList());
            return collection;
        }
        catch (NoSuchFileException stream2222) {
            return Collections.emptyList();
        }
        catch (IOException iOException) {
            LOGGER.warn("Unable to getFiles on {}", (Object)namespace, (Object)iOException);
        }
        return Collections.emptyList();
    }
}

