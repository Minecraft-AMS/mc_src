/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.report.log;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.ints.IntList;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Collection;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Spliterators;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.log.ChatLogEntry;
import net.minecraft.client.report.log.ReceivedMessage;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface ChatLog {
    public static final int MISSING_NEXT_INDEX = -1;

    public void add(ChatLogEntry var1);

    @Nullable
    public ChatLogEntry get(int var1);

    @Nullable
    default public IndexedEntry<ChatLogEntry> getIndexed(int index) {
        ChatLogEntry chatLogEntry = this.get(index);
        return chatLogEntry != null ? new IndexedEntry<ChatLogEntry>(index, chatLogEntry) : null;
    }

    default public boolean contains(int index) {
        return this.get(index) != null;
    }

    public int getOffsetIndex(int var1, int var2);

    default public int getPreviousIndex(int index) {
        return this.getOffsetIndex(index, -1);
    }

    default public int getNextIndex(int index) {
        return this.getOffsetIndex(index, 1);
    }

    public int getMaxIndex();

    public int getMinIndex();

    default public Streams streamForward() {
        return this.streamForward(this.getMinIndex());
    }

    default public Streams streamBackward() {
        return this.streamBackward(this.getMaxIndex());
    }

    default public Streams streamForward(int startIndex) {
        return this.stream(startIndex, this::getNextIndex);
    }

    default public Streams streamBackward(int startIndex) {
        return this.stream(startIndex, this::getPreviousIndex);
    }

    default public Streams streamForward(int startIndex, int endIndex) {
        if (!this.contains(startIndex) || !this.contains(endIndex)) {
            return this.emptyStreams();
        }
        return this.stream(startIndex, currentIndex -> {
            if (currentIndex == endIndex) {
                return -1;
            }
            return this.getNextIndex(currentIndex);
        });
    }

    default public Streams stream(final int startIndex, final IntUnaryOperator nextIndexGetter) {
        if (!this.contains(startIndex)) {
            return this.emptyStreams();
        }
        return new Streams(this, new PrimitiveIterator.OfInt(){
            private int nextIndex;
            {
                this.nextIndex = startIndex;
            }

            @Override
            public int nextInt() {
                int i = this.nextIndex;
                this.nextIndex = nextIndexGetter.applyAsInt(i);
                return i;
            }

            @Override
            public boolean hasNext() {
                return this.nextIndex != -1;
            }
        });
    }

    private Streams emptyStreams() {
        return new Streams(this, (PrimitiveIterator.OfInt)IntList.of().iterator());
    }

    @Environment(value=EnvType.CLIENT)
    public record IndexedEntry<T extends ChatLogEntry>(int index, T entry) {
        @Nullable
        public <U extends ChatLogEntry> IndexedEntry<U> cast(Class<U> clazz) {
            if (clazz.isInstance(this.entry)) {
                return new IndexedEntry<ChatLogEntry>(this.index, (ChatLogEntry)clazz.cast(this.entry));
            }
            return null;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{IndexedEntry.class, "id;event", "index", "entry"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{IndexedEntry.class, "id;event", "index", "entry"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{IndexedEntry.class, "id;event", "index", "entry"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Streams {
        private static final int CHARACTERISTICS = 1041;
        private final ChatLog log;
        private final PrimitiveIterator.OfInt indicesIterator;

        Streams(ChatLog log, PrimitiveIterator.OfInt indicesIterator) {
            this.log = log;
            this.indicesIterator = indicesIterator;
        }

        public IntStream streamIndices() {
            return StreamSupport.intStream(Spliterators.spliteratorUnknownSize(this.indicesIterator, 1041), false);
        }

        public Stream<ChatLogEntry> streamLogEntries() {
            return this.streamIndices().mapToObj(this.log::get).filter(Objects::nonNull);
        }

        public Collection<GameProfile> collectSenderProfiles() {
            return this.streamLogEntries().map(message -> {
                ReceivedMessage.ChatMessage chatMessage;
                if (message instanceof ReceivedMessage.ChatMessage && (chatMessage = (ReceivedMessage.ChatMessage)message).isSentFrom(chatMessage.profile().getId())) {
                    return chatMessage.profile();
                }
                return null;
            }).filter(Objects::nonNull).distinct().toList();
        }

        public Stream<IndexedEntry<ChatLogEntry>> streamIndexedEntries() {
            return this.streamIndices().mapToObj(this.log::getIndexed).filter(Objects::nonNull);
        }
    }
}

