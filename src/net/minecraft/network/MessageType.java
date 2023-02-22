/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network;

public final class MessageType
extends Enum<MessageType> {
    public static final /* enum */ MessageType CHAT = new MessageType(0, false);
    public static final /* enum */ MessageType SYSTEM = new MessageType(1, true);
    public static final /* enum */ MessageType GAME_INFO = new MessageType(2, true);
    private final byte id;
    private final boolean interruptsNarration;
    private static final /* synthetic */ MessageType[] field_11734;

    public static MessageType[] values() {
        return (MessageType[])field_11734.clone();
    }

    public static MessageType valueOf(String string) {
        return Enum.valueOf(MessageType.class, string);
    }

    private MessageType(byte id, boolean interruptsNarration) {
        this.id = id;
        this.interruptsNarration = interruptsNarration;
    }

    public byte getId() {
        return this.id;
    }

    public static MessageType byId(byte id) {
        for (MessageType messageType : MessageType.values()) {
            if (id != messageType.id) continue;
            return messageType;
        }
        return CHAT;
    }

    public boolean interruptsNarration() {
        return this.interruptsNarration;
    }

    private static /* synthetic */ MessageType[] method_36944() {
        return new MessageType[]{CHAT, SYSTEM, GAME_INFO};
    }

    static {
        field_11734 = MessageType.method_36944();
    }
}

