/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.option;

import java.util.function.IntFunction;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

public final class ChatVisibility
extends Enum<ChatVisibility>
implements TranslatableOption {
    public static final /* enum */ ChatVisibility FULL = new ChatVisibility(0, "options.chat.visibility.full");
    public static final /* enum */ ChatVisibility SYSTEM = new ChatVisibility(1, "options.chat.visibility.system");
    public static final /* enum */ ChatVisibility HIDDEN = new ChatVisibility(2, "options.chat.visibility.hidden");
    private static final IntFunction<ChatVisibility> BY_ID;
    private final int id;
    private final String translationKey;
    private static final /* synthetic */ ChatVisibility[] field_7537;

    public static ChatVisibility[] values() {
        return (ChatVisibility[])field_7537.clone();
    }

    public static ChatVisibility valueOf(String string) {
        return Enum.valueOf(ChatVisibility.class, string);
    }

    private ChatVisibility(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    public static ChatVisibility byId(int id) {
        return BY_ID.apply(id);
    }

    private static /* synthetic */ ChatVisibility[] method_36660() {
        return new ChatVisibility[]{FULL, SYSTEM, HIDDEN};
    }

    static {
        field_7537 = ChatVisibility.method_36660();
        BY_ID = ValueLists.createIdToValueFunction(ChatVisibility::getId, ChatVisibility.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }
}

