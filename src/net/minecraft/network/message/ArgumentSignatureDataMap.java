/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.message;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.command.argument.SignedArgumentList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.MessageSignatureData;
import org.jetbrains.annotations.Nullable;

public record ArgumentSignatureDataMap(List<Entry> entries) {
    public static final ArgumentSignatureDataMap EMPTY = new ArgumentSignatureDataMap(List.of());
    private static final int MAX_ARGUMENTS = 8;
    private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

    public ArgumentSignatureDataMap(PacketByteBuf buf) {
        this(buf.readCollection(PacketByteBuf.getMaxValidator(ArrayList::new, 8), Entry::new));
    }

    @Nullable
    public MessageSignatureData get(String argumentName) {
        for (Entry entry : this.entries) {
            if (!entry.name.equals(argumentName)) continue;
            return entry.signature;
        }
        return null;
    }

    public void write(PacketByteBuf buf) {
        buf.writeCollection(this.entries, (buf2, entry) -> entry.write((PacketByteBuf)((Object)buf2)));
    }

    public static ArgumentSignatureDataMap sign(SignedArgumentList<?> arguments, ArgumentSigner signer) {
        List<Entry> list = arguments.arguments().stream().map(argument -> {
            MessageSignatureData messageSignatureData = signer.sign(argument.value());
            if (messageSignatureData != null) {
                return new Entry(argument.getNodeName(), messageSignatureData);
            }
            return null;
        }).filter(Objects::nonNull).toList();
        return new ArgumentSignatureDataMap(list);
    }

    public static final class Entry
    extends Record {
        final String name;
        final MessageSignatureData signature;

        public Entry(PacketByteBuf buf) {
            this(buf.readString(16), MessageSignatureData.fromBuf(buf));
        }

        public Entry(String string, MessageSignatureData messageSignatureData) {
            this.name = string;
            this.signature = messageSignatureData;
        }

        public void write(PacketByteBuf buf) {
            buf.writeString(this.name, 16);
            MessageSignatureData.write(buf, this.signature);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entry.class, "name;signature", "name", "signature"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entry.class, "name;signature", "name", "signature"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entry.class, "name;signature", "name", "signature"}, this, object);
        }

        public String name() {
            return this.name;
        }

        public MessageSignatureData signature() {
            return this.signature;
        }
    }

    @FunctionalInterface
    public static interface ArgumentSigner {
        @Nullable
        public MessageSignatureData sign(String var1);
    }
}

