/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.ParsedArgument
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.network.message;

import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.datafixers.util.Pair;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.argument.DecoratableArgumentList;
import net.minecraft.command.argument.DecoratableArgumentType;
import net.minecraft.command.argument.SignedArgumentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.MessageSignatureData;

public record ArgumentSignatureDataMap(List<Entry> entries) {
    public static final ArgumentSignatureDataMap EMPTY = new ArgumentSignatureDataMap(List.of());
    private static final int MAX_ARGUMENTS = 8;
    private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

    public ArgumentSignatureDataMap(PacketByteBuf buf) {
        this(buf.readCollection(PacketByteBuf.getMaxValidator(ArrayList::new, 8), Entry::new));
    }

    public MessageSignatureData get(String argumentName) {
        for (Entry entry : this.entries) {
            if (!entry.name.equals(argumentName)) continue;
            return entry.signature;
        }
        return MessageSignatureData.EMPTY;
    }

    public void write(PacketByteBuf buf) {
        buf.writeCollection(this.entries, (buf2, entry) -> entry.write((PacketByteBuf)((Object)buf2)));
    }

    public static boolean hasSignedArgument(DecoratableArgumentList<?> arguments) {
        return arguments.arguments().stream().anyMatch(argument -> argument.argumentType() instanceof SignedArgumentType);
    }

    public static ArgumentSignatureDataMap sign(DecoratableArgumentList<?> arguments, ArgumentSigner signer) {
        List<Entry> list = ArgumentSignatureDataMap.toNameValuePairs(arguments).stream().map(entry -> {
            MessageSignatureData messageSignatureData = signer.sign((String)entry.getFirst(), (String)entry.getSecond());
            return new Entry((String)entry.getFirst(), messageSignatureData);
        }).toList();
        return new ArgumentSignatureDataMap(list);
    }

    public static List<Pair<String, String>> toNameValuePairs(DecoratableArgumentList<?> arguments) {
        ArrayList<Pair<String, String>> list = new ArrayList<Pair<String, String>>();
        for (DecoratableArgumentList.ParsedArgument<?> parsedArgument : arguments.arguments()) {
            DecoratableArgumentType<?> decoratableArgumentType = parsedArgument.argumentType();
            if (!(decoratableArgumentType instanceof SignedArgumentType)) continue;
            SignedArgumentType signedArgumentType = (SignedArgumentType)decoratableArgumentType;
            String string = ArgumentSignatureDataMap.resultToString(signedArgumentType, parsedArgument.parsedValue());
            list.add((Pair<String, String>)Pair.of((Object)parsedArgument.getNodeName(), (Object)string));
        }
        return list;
    }

    private static <T> String resultToString(SignedArgumentType<T> type, ParsedArgument<?, ?> argument) {
        return type.toSignedString(argument.getResult());
    }

    public static final class Entry
    extends Record {
        final String name;
        final MessageSignatureData signature;

        public Entry(PacketByteBuf buf) {
            this(buf.readString(16), new MessageSignatureData(buf));
        }

        public Entry(String string, MessageSignatureData messageSignatureData) {
            this.name = string;
            this.signature = messageSignatureData;
        }

        public void write(PacketByteBuf buf) {
            buf.writeString(this.name, 16);
            this.signature.write(buf);
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
        public MessageSignatureData sign(String var1, String var2);
    }
}

