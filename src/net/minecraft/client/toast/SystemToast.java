/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.toast;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SystemToast
implements Toast {
    private static final int MIN_WIDTH = 200;
    private static final int LINE_HEIGHT = 12;
    private static final int PADDING_Y = 10;
    private final Type type;
    private Text title;
    private List<OrderedText> lines;
    private long startTime;
    private boolean justUpdated;
    private final int width;

    public SystemToast(Type type, Text title, @Nullable Text description) {
        this(type, title, (List<OrderedText>)SystemToast.getTextAsList(description), Math.max(160, 30 + Math.max(MinecraftClient.getInstance().textRenderer.getWidth(title), description == null ? 0 : MinecraftClient.getInstance().textRenderer.getWidth(description))));
    }

    public static SystemToast create(MinecraftClient client, Type type, Text title, Text description) {
        TextRenderer textRenderer = client.textRenderer;
        List<OrderedText> list = textRenderer.wrapLines(description, 200);
        int i = Math.max(200, list.stream().mapToInt(textRenderer::getWidth).max().orElse(200));
        return new SystemToast(type, title, list, i + 30);
    }

    private SystemToast(Type type, Text title, List<OrderedText> lines, int width) {
        this.type = type;
        this.title = title;
        this.lines = lines;
        this.width = width;
    }

    private static ImmutableList<OrderedText> getTextAsList(@Nullable Text text) {
        return text == null ? ImmutableList.of() : ImmutableList.of((Object)text.asOrderedText());
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return 20 + Math.max(this.lines.size(), 1) * 12;
    }

    @Override
    public Toast.Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        int j;
        int i;
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }
        if ((i = this.getWidth()) == 160 && this.lines.size() <= 1) {
            context.drawTexture(TEXTURE, 0, 0, 0, 64, i, this.getHeight());
        } else {
            j = this.getHeight();
            int k = 28;
            int l = Math.min(4, j - 28);
            this.drawPart(context, manager, i, 0, 0, 28);
            for (int m = 28; m < j - l; m += 10) {
                this.drawPart(context, manager, i, 16, m, Math.min(16, j - m - l));
            }
            this.drawPart(context, manager, i, 32 - l, j - l, l);
        }
        if (this.lines == null) {
            context.drawText(manager.getClient().textRenderer, this.title, 18, 12, -256, false);
        } else {
            context.drawText(manager.getClient().textRenderer, this.title, 18, 7, -256, false);
            for (j = 0; j < this.lines.size(); ++j) {
                context.drawText(manager.getClient().textRenderer, this.lines.get(j), 18, 18 + j * 12, -1, false);
            }
        }
        return (double)(startTime - this.startTime) < (double)this.type.displayDuration * manager.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    private void drawPart(DrawContext context, ToastManager manager, int width, int textureV, int y, int height) {
        int i = textureV == 0 ? 20 : 5;
        int j = Math.min(60, width - i);
        context.drawTexture(TEXTURE, 0, y, 0, 64 + textureV, i, height);
        for (int k = i; k < width - j; k += 64) {
            context.drawTexture(TEXTURE, k, y, 32, 64 + textureV, Math.min(64, width - k - j), height);
        }
        context.drawTexture(TEXTURE, width - j, y, 160 - j, 64 + textureV, j, height);
    }

    public void setContent(Text title, @Nullable Text description) {
        this.title = title;
        this.lines = SystemToast.getTextAsList(description);
        this.justUpdated = true;
    }

    public Type getType() {
        return this.type;
    }

    public static void add(ToastManager manager, Type type, Text title, @Nullable Text description) {
        manager.add(new SystemToast(type, title, description));
    }

    public static void show(ToastManager manager, Type type, Text title, @Nullable Text description) {
        SystemToast systemToast = manager.getToast(SystemToast.class, (Object)type);
        if (systemToast == null) {
            SystemToast.add(manager, type, title, description);
        } else {
            systemToast.setContent(title, description);
        }
    }

    public static void addWorldAccessFailureToast(MinecraftClient client, String worldName) {
        SystemToast.add(client.getToastManager(), Type.WORLD_ACCESS_FAILURE, Text.translatable("selectWorld.access_failure"), Text.literal(worldName));
    }

    public static void addWorldDeleteFailureToast(MinecraftClient client, String worldName) {
        SystemToast.add(client.getToastManager(), Type.WORLD_ACCESS_FAILURE, Text.translatable("selectWorld.delete_failure"), Text.literal(worldName));
    }

    public static void addPackCopyFailure(MinecraftClient client, String directory) {
        SystemToast.add(client.getToastManager(), Type.PACK_COPY_FAILURE, Text.translatable("pack.copyFailure"), Text.literal(directory));
    }

    @Override
    public /* synthetic */ Object getType() {
        return this.getType();
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type TUTORIAL_HINT = new Type();
        public static final /* enum */ Type NARRATOR_TOGGLE = new Type();
        public static final /* enum */ Type WORLD_BACKUP = new Type();
        public static final /* enum */ Type PACK_LOAD_FAILURE = new Type();
        public static final /* enum */ Type WORLD_ACCESS_FAILURE = new Type();
        public static final /* enum */ Type PACK_COPY_FAILURE = new Type();
        public static final /* enum */ Type PERIODIC_NOTIFICATION = new Type();
        public static final /* enum */ Type UNSECURE_SERVER_WARNING = new Type(10000L);
        final long displayDuration;
        private static final /* synthetic */ Type[] field_2221;

        public static Type[] values() {
            return (Type[])field_2221.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(long displayDuration) {
            this.displayDuration = displayDuration;
        }

        private Type() {
            this(5000L);
        }

        private static /* synthetic */ Type[] method_36871() {
            return new Type[]{TUTORIAL_HINT, NARRATOR_TOGGLE, WORLD_BACKUP, PACK_LOAD_FAILURE, WORLD_ACCESS_FAILURE, PACK_COPY_FAILURE, PERIODIC_NOTIFICATION, UNSECURE_SERVER_WARNING};
        }

        static {
            field_2221 = Type.method_36871();
        }
    }
}

