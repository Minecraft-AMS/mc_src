/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public final class ChatPreviewMode
extends Enum<ChatPreviewMode>
implements TranslatableOption {
    public static final /* enum */ ChatPreviewMode OFF = new ChatPreviewMode(0, "options.off");
    public static final /* enum */ ChatPreviewMode LIVE = new ChatPreviewMode(1, "options.chatPreview.live");
    public static final /* enum */ ChatPreviewMode CONFIRM = new ChatPreviewMode(2, "options.chatPreview.confirm");
    private static final ChatPreviewMode[] VALUES;
    private final int id;
    private final String translationKey;
    private static final /* synthetic */ ChatPreviewMode[] field_39879;

    public static ChatPreviewMode[] values() {
        return (ChatPreviewMode[])field_39879.clone();
    }

    public static ChatPreviewMode valueOf(String string) {
        return Enum.valueOf(ChatPreviewMode.class, string);
    }

    private ChatPreviewMode(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public static ChatPreviewMode byId(int id) {
        return VALUES[MathHelper.floorMod(id, VALUES.length)];
    }

    private static /* synthetic */ ChatPreviewMode[] method_44954() {
        return new ChatPreviewMode[]{OFF, LIVE, CONFIRM};
    }

    static {
        field_39879 = ChatPreviewMode.method_44954();
        VALUES = (ChatPreviewMode[])Arrays.stream(ChatPreviewMode.values()).sorted(Comparator.comparingInt(ChatPreviewMode::getId)).toArray(ChatPreviewMode[]::new);
    }
}

