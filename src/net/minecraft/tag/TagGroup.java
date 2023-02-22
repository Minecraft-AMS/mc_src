/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableBiMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntListIterator
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.tag;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.SetTag;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public interface TagGroup<T> {
    public Map<Identifier, Tag<T>> getTags();

    @Nullable
    default public Tag<T> getTag(Identifier id) {
        return this.getTags().get(id);
    }

    public Tag<T> getTagOrEmpty(Identifier var1);

    @Nullable
    default public Identifier getId(Tag.Identified<T> tag) {
        return tag.getId();
    }

    @Nullable
    public Identifier getUncheckedTagId(Tag<T> var1);

    default public boolean contains(Identifier id) {
        return this.getTags().containsKey(id);
    }

    default public Collection<Identifier> getTagIds() {
        return this.getTags().keySet();
    }

    default public Collection<Identifier> getTagsFor(T object) {
        ArrayList list = Lists.newArrayList();
        for (Map.Entry<Identifier, Tag<T>> entry : this.getTags().entrySet()) {
            if (!entry.getValue().contains(object)) continue;
            list.add(entry.getKey());
        }
        return list;
    }

    default public Serialized serialize(Registry<T> registry) {
        Map<Identifier, Tag<T>> map = this.getTags();
        HashMap map2 = Maps.newHashMapWithExpectedSize((int)map.size());
        map.forEach((id, tag) -> {
            List list = tag.values();
            IntArrayList intList = new IntArrayList(list.size());
            for (Object object : list) {
                intList.add(registry.getRawId(object));
            }
            map2.put(id, intList);
        });
        return new Serialized(map2);
    }

    public static <T> TagGroup<T> deserialize(Serialized serialized, Registry<? extends T> registry) {
        HashMap map = Maps.newHashMapWithExpectedSize((int)serialized.contents.size());
        serialized.contents.forEach((id, entries) -> {
            ImmutableSet.Builder builder = ImmutableSet.builder();
            IntListIterator intListIterator = entries.iterator();
            while (intListIterator.hasNext()) {
                int i = (Integer)intListIterator.next();
                builder.add(registry.get(i));
            }
            map.put(id, Tag.of(builder.build()));
        });
        return TagGroup.create(map);
    }

    public static <T> TagGroup<T> createEmpty() {
        return TagGroup.create(ImmutableBiMap.of());
    }

    public static <T> TagGroup<T> create(Map<Identifier, Tag<T>> tags) {
        ImmutableBiMap biMap = ImmutableBiMap.copyOf(tags);
        return new TagGroup<T>((BiMap)biMap){
            private final Tag<T> emptyTag = SetTag.empty();
            final /* synthetic */ BiMap tags;
            {
                this.tags = biMap;
            }

            @Override
            public Tag<T> getTagOrEmpty(Identifier id) {
                return (Tag)this.tags.getOrDefault((Object)id, this.emptyTag);
            }

            @Override
            @Nullable
            public Identifier getUncheckedTagId(Tag<T> tag) {
                if (tag instanceof Tag.Identified) {
                    return ((Tag.Identified)tag).getId();
                }
                return (Identifier)this.tags.inverse().get(tag);
            }

            @Override
            public Map<Identifier, Tag<T>> getTags() {
                return this.tags;
            }
        };
    }

    public static class Serialized {
        final Map<Identifier, IntList> contents;

        Serialized(Map<Identifier, IntList> contents) {
            this.contents = contents;
        }

        public void writeBuf(PacketByteBuf buf) {
            buf.writeMap(this.contents, PacketByteBuf::writeIdentifier, PacketByteBuf::writeIntList);
        }

        public static Serialized fromBuf(PacketByteBuf buf) {
            return new Serialized(buf.readMap(PacketByteBuf::readIdentifier, PacketByteBuf::readIntList));
        }
    }
}

