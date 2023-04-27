/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.pack;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

@Environment(value=EnvType.CLIENT)
public class PackListWidget
extends AlwaysSelectedEntryListWidget<ResourcePackEntry> {
    static final Identifier RESOURCE_PACKS_TEXTURE = new Identifier("textures/gui/resource_packs.png");
    static final Text INCOMPATIBLE = Text.translatable("pack.incompatible");
    static final Text INCOMPATIBLE_CONFIRM = Text.translatable("pack.incompatible.confirm.title");
    private final Text title;
    final PackScreen screen;

    public PackListWidget(MinecraftClient client, PackScreen screen, int width, int height, Text title) {
        super(client, width, height, 32, height - 55 + 4, 36);
        this.screen = screen;
        this.title = title;
        this.centerListVertically = false;
        Objects.requireNonNull(client.textRenderer);
        this.setRenderHeader(true, (int)(9.0f * 1.5f));
    }

    @Override
    protected void renderHeader(DrawContext context, int x, int y) {
        MutableText text = Text.empty().append(this.title).formatted(Formatting.UNDERLINE, Formatting.BOLD);
        context.drawText(this.client.textRenderer, text, x + this.width / 2 - this.client.textRenderer.getWidth(text) / 2, Math.min(this.top + 3, y), 0xFFFFFF, false);
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.right - 6;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.getSelectedOrNull() != null) {
            switch (keyCode) {
                case 32: 
                case 257: {
                    ((ResourcePackEntry)this.getSelectedOrNull()).toggle();
                    return true;
                }
            }
            if (Screen.hasShiftDown()) {
                switch (keyCode) {
                    case 265: {
                        ((ResourcePackEntry)this.getSelectedOrNull()).moveTowardStart();
                        return true;
                    }
                    case 264: {
                        ((ResourcePackEntry)this.getSelectedOrNull()).moveTowardEnd();
                        return true;
                    }
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Environment(value=EnvType.CLIENT)
    public static class ResourcePackEntry
    extends AlwaysSelectedEntryListWidget.Entry<ResourcePackEntry> {
        private static final int field_32397 = 0;
        private static final int field_32398 = 32;
        private static final int field_32399 = 64;
        private static final int field_32400 = 96;
        private static final int field_32401 = 0;
        private static final int field_32402 = 32;
        private static final int field_32403 = 157;
        private static final int field_32404 = 157;
        private static final String ELLIPSIS = "...";
        private final PackListWidget widget;
        protected final MinecraftClient client;
        private final ResourcePackOrganizer.Pack pack;
        private final OrderedText displayName;
        private final MultilineText description;
        private final OrderedText incompatibleText;
        private final MultilineText compatibilityNotificationText;

        public ResourcePackEntry(MinecraftClient client, PackListWidget widget, ResourcePackOrganizer.Pack pack) {
            this.client = client;
            this.pack = pack;
            this.widget = widget;
            this.displayName = ResourcePackEntry.trimTextToWidth(client, pack.getDisplayName());
            this.description = ResourcePackEntry.createMultilineText(client, pack.getDecoratedDescription());
            this.incompatibleText = ResourcePackEntry.trimTextToWidth(client, INCOMPATIBLE);
            this.compatibilityNotificationText = ResourcePackEntry.createMultilineText(client, pack.getCompatibility().getNotification());
        }

        private static OrderedText trimTextToWidth(MinecraftClient client, Text text) {
            int i = client.textRenderer.getWidth(text);
            if (i > 157) {
                StringVisitable stringVisitable = StringVisitable.concat(client.textRenderer.trimToWidth(text, 157 - client.textRenderer.getWidth(ELLIPSIS)), StringVisitable.plain(ELLIPSIS));
                return Language.getInstance().reorder(stringVisitable);
            }
            return text.asOrderedText();
        }

        private static MultilineText createMultilineText(MinecraftClient client, Text text) {
            return MultilineText.create(client.textRenderer, (StringVisitable)text, 157, 2);
        }

        @Override
        public Text getNarration() {
            return Text.translatable("narrator.select", this.pack.getDisplayName());
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            ResourcePackCompatibility resourcePackCompatibility = this.pack.getCompatibility();
            if (!resourcePackCompatibility.isCompatible()) {
                context.fill(x - 1, y - 1, x + entryWidth - 9, y + entryHeight + 1, -8978432);
            }
            context.drawTexture(this.pack.getIconId(), x, y, 0.0f, 0.0f, 32, 32, 32, 32);
            OrderedText orderedText = this.displayName;
            MultilineText multilineText = this.description;
            if (this.isSelectable() && (this.client.options.getTouchscreen().getValue().booleanValue() || hovered || this.widget.getSelectedOrNull() == this && this.widget.isFocused())) {
                context.fill(x, y, x + 32, y + 32, -1601138544);
                int i = mouseX - x;
                int j = mouseY - y;
                if (!this.pack.getCompatibility().isCompatible()) {
                    orderedText = this.incompatibleText;
                    multilineText = this.compatibilityNotificationText;
                }
                if (this.pack.canBeEnabled()) {
                    if (i < 32) {
                        context.drawTexture(RESOURCE_PACKS_TEXTURE, x, y, 0.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        context.drawTexture(RESOURCE_PACKS_TEXTURE, x, y, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                } else {
                    if (this.pack.canBeDisabled()) {
                        if (i < 16) {
                            context.drawTexture(RESOURCE_PACKS_TEXTURE, x, y, 32.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            context.drawTexture(RESOURCE_PACKS_TEXTURE, x, y, 32.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.pack.canMoveTowardStart()) {
                        if (i < 32 && i > 16 && j < 16) {
                            context.drawTexture(RESOURCE_PACKS_TEXTURE, x, y, 96.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            context.drawTexture(RESOURCE_PACKS_TEXTURE, x, y, 96.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.pack.canMoveTowardEnd()) {
                        if (i < 32 && i > 16 && j > 16) {
                            context.drawTexture(RESOURCE_PACKS_TEXTURE, x, y, 64.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            context.drawTexture(RESOURCE_PACKS_TEXTURE, x, y, 64.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                }
            }
            context.drawTextWithShadow(this.client.textRenderer, orderedText, x + 32 + 2, y + 1, 0xFFFFFF);
            multilineText.drawWithShadow(context, x + 32 + 2, y + 12, 10, 0x808080);
        }

        public String getName() {
            return this.pack.getName();
        }

        private boolean isSelectable() {
            return !this.pack.isPinned() || !this.pack.isAlwaysEnabled();
        }

        public void toggle() {
            if (this.pack.canBeEnabled() && this.enable()) {
                this.widget.screen.switchFocusedList(this.widget);
            } else if (this.pack.canBeDisabled()) {
                this.pack.disable();
                this.widget.screen.switchFocusedList(this.widget);
            }
        }

        void moveTowardStart() {
            if (this.pack.canMoveTowardStart()) {
                this.pack.moveTowardStart();
            }
        }

        void moveTowardEnd() {
            if (this.pack.canMoveTowardEnd()) {
                this.pack.moveTowardEnd();
            }
        }

        private boolean enable() {
            if (this.pack.getCompatibility().isCompatible()) {
                this.pack.enable();
                return true;
            }
            Text text = this.pack.getCompatibility().getConfirmMessage();
            this.client.setScreen(new ConfirmScreen(confirmed -> {
                this.client.setScreen(this.widget.screen);
                if (confirmed) {
                    this.pack.enable();
                }
            }, INCOMPATIBLE_CONFIRM, text));
            return false;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button != 0) {
                return false;
            }
            double d = mouseX - (double)this.widget.getRowLeft();
            double e = mouseY - (double)this.widget.getRowTop(this.widget.children().indexOf(this));
            if (this.isSelectable() && d <= 32.0) {
                this.widget.screen.clearSelection();
                if (this.pack.canBeEnabled()) {
                    this.enable();
                    return true;
                }
                if (d < 16.0 && this.pack.canBeDisabled()) {
                    this.pack.disable();
                    return true;
                }
                if (d > 16.0 && e < 16.0 && this.pack.canMoveTowardStart()) {
                    this.pack.moveTowardStart();
                    return true;
                }
                if (d > 16.0 && e > 16.0 && this.pack.canMoveTowardEnd()) {
                    this.pack.moveTowardEnd();
                    return true;
                }
            }
            return false;
        }
    }
}

