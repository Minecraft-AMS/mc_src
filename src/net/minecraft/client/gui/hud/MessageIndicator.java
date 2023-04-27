/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.hud;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record MessageIndicator(int indicatorColor, @Nullable Icon icon, @Nullable Text text, @Nullable String loggedName) {
    private static final Text SYSTEM_TEXT = Text.translatable("chat.tag.system");
    private static final Text SINGLE_PLAYER_TEXT = Text.translatable("chat.tag.system_single_player");
    private static final Text NOT_SECURE_TEXT = Text.translatable("chat.tag.not_secure");
    private static final Text MODIFIED_TEXT = Text.translatable("chat.tag.modified");
    private static final int NOT_SECURE_COLOR = 0xD0D0D0;
    private static final int MODIFIED_COLOR = 0x606060;
    private static final MessageIndicator SYSTEM = new MessageIndicator(0xD0D0D0, null, SYSTEM_TEXT, "System");
    private static final MessageIndicator SINGLE_PLAYER = new MessageIndicator(0xD0D0D0, null, SINGLE_PLAYER_TEXT, "System");
    private static final MessageIndicator NOT_SECURE = new MessageIndicator(0xD0D0D0, null, NOT_SECURE_TEXT, "Not Secure");
    static final Identifier CHAT_TAGS_TEXTURE = new Identifier("textures/gui/chat_tags.png");

    public static MessageIndicator system() {
        return SYSTEM;
    }

    public static MessageIndicator singlePlayer() {
        return SINGLE_PLAYER;
    }

    public static MessageIndicator notSecure() {
        return NOT_SECURE;
    }

    public static MessageIndicator modified(String originalText) {
        MutableText text = Text.literal(originalText).formatted(Formatting.GRAY);
        MutableText text2 = Text.empty().append(MODIFIED_TEXT).append(ScreenTexts.LINE_BREAK).append(text);
        return new MessageIndicator(0x606060, Icon.CHAT_MODIFIED, text2, "Modified");
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MessageIndicator.class, "indicatorColor;icon;text;logTag", "indicatorColor", "icon", "text", "loggedName"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MessageIndicator.class, "indicatorColor;icon;text;logTag", "indicatorColor", "icon", "text", "loggedName"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MessageIndicator.class, "indicatorColor;icon;text;logTag", "indicatorColor", "icon", "text", "loggedName"}, this, object);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Icon
    extends Enum<Icon> {
        public static final /* enum */ Icon CHAT_MODIFIED = new Icon(0, 0, 9, 9);
        public final int u;
        public final int v;
        public final int width;
        public final int height;
        private static final /* synthetic */ Icon[] field_39768;

        public static Icon[] values() {
            return (Icon[])field_39768.clone();
        }

        public static Icon valueOf(String string) {
            return Enum.valueOf(Icon.class, string);
        }

        private Icon(int u, int v, int width, int height) {
            this.u = u;
            this.v = v;
            this.width = width;
            this.height = height;
        }

        public void draw(DrawContext context, int x, int y) {
            context.drawTexture(CHAT_TAGS_TEXTURE, x, y, this.u, this.v, this.width, this.height, 32, 32);
        }

        private static /* synthetic */ Icon[] method_44711() {
            return new Icon[]{CHAT_MODIFIED};
        }

        static {
            field_39768 = Icon.method_44711();
        }
    }
}

