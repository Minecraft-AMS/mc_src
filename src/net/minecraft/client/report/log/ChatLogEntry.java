/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.report.log;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.log.ReceivedMessage;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public interface ChatLogEntry {
    public static final Codec<ChatLogEntry> CODEC = StringIdentifiable.createCodec(Type::values).dispatch(ChatLogEntry::getType, Type::getCodec);

    public Type getType();

    @Environment(value=EnvType.CLIENT)
    public static final class Type
    extends Enum<Type>
    implements StringIdentifiable {
        public static final /* enum */ Type PLAYER = new Type("player", () -> ReceivedMessage.ChatMessage.CHAT_MESSAGE_CODEC);
        public static final /* enum */ Type SYSTEM = new Type("system", () -> ReceivedMessage.GameMessage.GAME_MESSAGE_CODEC);
        private final String id;
        private final Supplier<Codec<? extends ChatLogEntry>> codecSupplier;
        private static final /* synthetic */ Type[] field_40808;

        public static Type[] values() {
            return (Type[])field_40808.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(String id, Supplier<Codec<? extends ChatLogEntry>> codecSupplier) {
            this.id = id;
            this.codecSupplier = codecSupplier;
        }

        private Codec<? extends ChatLogEntry> getCodec() {
            return this.codecSupplier.get();
        }

        @Override
        public String asString() {
            return this.id;
        }

        private static /* synthetic */ Type[] method_46542() {
            return new Type[]{PLAYER, SYSTEM};
        }

        static {
            field_40808 = Type.method_46542();
        }
    }
}

