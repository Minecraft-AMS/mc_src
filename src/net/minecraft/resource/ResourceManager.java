/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.resource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

public interface ResourceManager
extends ResourceFactory {
    public Set<String> getAllNamespaces();

    public boolean containsResource(Identifier var1);

    public List<Resource> getAllResources(Identifier var1) throws IOException;

    public Collection<Identifier> findResources(String var1, Predicate<String> var2);

    public Stream<ResourcePack> streamResourcePacks();

    public static final class Empty
    extends Enum<Empty>
    implements ResourceManager {
        public static final /* enum */ Empty INSTANCE = new Empty();
        private static final /* synthetic */ Empty[] field_25352;

        public static Empty[] values() {
            return (Empty[])field_25352.clone();
        }

        public static Empty valueOf(String string) {
            return Enum.valueOf(Empty.class, string);
        }

        @Override
        public Set<String> getAllNamespaces() {
            return ImmutableSet.of();
        }

        @Override
        public Resource getResource(Identifier id) throws IOException {
            throw new FileNotFoundException(id.toString());
        }

        @Override
        public boolean containsResource(Identifier id) {
            return false;
        }

        @Override
        public List<Resource> getAllResources(Identifier id) {
            return ImmutableList.of();
        }

        @Override
        public Collection<Identifier> findResources(String startingPath, Predicate<String> pathPredicate) {
            return ImmutableSet.of();
        }

        @Override
        public Stream<ResourcePack> streamResourcePacks() {
            return Stream.of(new ResourcePack[0]);
        }

        private static /* synthetic */ Empty[] method_36585() {
            return new Empty[]{INSTANCE};
        }

        static {
            field_25352 = Empty.method_36585();
        }
    }
}

