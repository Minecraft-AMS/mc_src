/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.resource;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourceNotFoundException;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

public class ZipResourcePack
extends AbstractFileResourcePack {
    public static final Splitter TYPE_NAMESPACE_SPLITTER = Splitter.on((char)'/').omitEmptyStrings().limit(3);
    private ZipFile file;

    public ZipResourcePack(File file) {
        super(file);
    }

    private ZipFile getZipFile() throws IOException {
        if (this.file == null) {
            this.file = new ZipFile(this.base);
        }
        return this.file;
    }

    @Override
    protected InputStream openFile(String name) throws IOException {
        ZipFile zipFile = this.getZipFile();
        ZipEntry zipEntry = zipFile.getEntry(name);
        if (zipEntry == null) {
            throw new ResourceNotFoundException(this.base, name);
        }
        return zipFile.getInputStream(zipEntry);
    }

    @Override
    public boolean containsFile(String name) {
        try {
            return this.getZipFile().getEntry(name) != null;
        }
        catch (IOException iOException) {
            return false;
        }
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        ZipFile zipFile;
        try {
            zipFile = this.getZipFile();
        }
        catch (IOException iOException) {
            return Collections.emptySet();
        }
        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        HashSet set = Sets.newHashSet();
        while (enumeration.hasMoreElements()) {
            ArrayList list;
            ZipEntry zipEntry = enumeration.nextElement();
            String string = zipEntry.getName();
            if (!string.startsWith(type.getDirectory() + "/") || (list = Lists.newArrayList((Iterable)TYPE_NAMESPACE_SPLITTER.split((CharSequence)string))).size() <= 1) continue;
            String string2 = (String)list.get(1);
            if (string2.equals(string2.toLowerCase(Locale.ROOT))) {
                set.add(string2);
                continue;
            }
            this.warnNonLowerCaseNamespace(string2);
        }
        return set;
    }

    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    @Override
    public void close() {
        if (this.file != null) {
            IOUtils.closeQuietly((Closeable)this.file);
            this.file = null;
        }
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
        ZipFile zipFile;
        try {
            zipFile = this.getZipFile();
        }
        catch (IOException iOException) {
            return Collections.emptySet();
        }
        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        ArrayList list = Lists.newArrayList();
        String string = type.getDirectory() + "/" + namespace + "/";
        String string2 = string + prefix + "/";
        while (enumeration.hasMoreElements()) {
            String string4;
            String[] strings;
            String string3;
            ZipEntry zipEntry = enumeration.nextElement();
            if (zipEntry.isDirectory() || (string3 = zipEntry.getName()).endsWith(".mcmeta") || !string3.startsWith(string2) || (strings = (string4 = string3.substring(string.length())).split("/")).length < maxDepth + 1 || !pathFilter.test(strings[strings.length - 1])) continue;
            list.add(new Identifier(namespace, string4));
        }
        return list;
    }
}

