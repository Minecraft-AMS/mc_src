/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChatScreen
extends Screen {
    public static final double SHIFT_SCROLL_AMOUNT = 7.0;
    private static final Text USAGE_TEXT = Text.translatable("chat_screen.usage");
    private static final int MAX_INDICATOR_TOOLTIP_WIDTH = 210;
    private String chatLastMessage = "";
    private int messageHistorySize = -1;
    protected TextFieldWidget chatField;
    private String originalChatText;
    ChatInputSuggestor chatInputSuggestor;

    public ChatScreen(String originalChatText) {
        super(Text.translatable("chat_screen.title"));
        this.originalChatText = originalChatText;
    }

    @Override
    protected void init() {
        this.messageHistorySize = this.client.inGameHud.getChatHud().getMessageHistory().size();
        this.chatField = new TextFieldWidget(this.client.advanceValidatingTextRenderer, 4, this.height - 12, this.width - 4, 12, (Text)Text.translatable("chat.editBox")){

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(ChatScreen.this.chatInputSuggestor.getNarration());
            }
        };
        this.chatField.setMaxLength(256);
        this.chatField.setDrawsBackground(false);
        this.chatField.setText(this.originalChatText);
        this.chatField.setChangedListener(this::onChatFieldUpdate);
        this.chatField.setFocusUnlocked(false);
        this.addSelectableChild(this.chatField);
        this.chatInputSuggestor = new ChatInputSuggestor(this.client, this, this.chatField, this.textRenderer, false, false, 1, 10, true, -805306368);
        this.chatInputSuggestor.refresh();
        this.setInitialFocus(this.chatField);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.chatField.getText();
        this.init(client, width, height);
        this.setText(string);
        this.chatInputSuggestor.refresh();
    }

    @Override
    public void removed() {
        this.client.inGameHud.getChatHud().resetScroll();
    }

    @Override
    public void tick() {
        this.chatField.tick();
    }

    private void onChatFieldUpdate(String chatText) {
        String string = this.chatField.getText();
        this.chatInputSuggestor.setWindowActive(!string.equals(this.originalChatText));
        this.chatInputSuggestor.refresh();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.chatInputSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 256) {
            this.client.setScreen(null);
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            if (this.sendMessage(this.chatField.getText(), true)) {
                this.client.setScreen(null);
            }
            return true;
        }
        if (keyCode == 265) {
            this.setChatFromHistory(-1);
            return true;
        }
        if (keyCode == 264) {
            this.setChatFromHistory(1);
            return true;
        }
        if (keyCode == 266) {
            this.client.inGameHud.getChatHud().scroll(this.client.inGameHud.getChatHud().getVisibleLineCount() - 1);
            return true;
        }
        if (keyCode == 267) {
            this.client.inGameHud.getChatHud().scroll(-this.client.inGameHud.getChatHud().getVisibleLineCount() + 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (this.chatInputSuggestor.mouseScrolled(amount = MathHelper.clamp(amount, -1.0, 1.0))) {
            return true;
        }
        if (!ChatScreen.hasShiftDown()) {
            amount *= 7.0;
        }
        this.client.inGameHud.getChatHud().scroll((int)amount);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.chatInputSuggestor.mouseClicked((int)mouseX, (int)mouseY, button)) {
            return true;
        }
        if (button == 0) {
            ChatHud chatHud = this.client.inGameHud.getChatHud();
            if (chatHud.mouseClicked(mouseX, mouseY)) {
                return true;
            }
            Style style = this.getTextStyleAt(mouseX, mouseY);
            if (style != null && this.handleTextClick(style)) {
                this.originalChatText = this.chatField.getText();
                return true;
            }
        }
        if (this.chatField.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void insertText(String text, boolean override) {
        if (override) {
            this.chatField.setText(text);
        } else {
            this.chatField.write(text);
        }
    }

    public void setChatFromHistory(int offset) {
        int i = this.messageHistorySize + offset;
        int j = this.client.inGameHud.getChatHud().getMessageHistory().size();
        if ((i = MathHelper.clamp(i, 0, j)) == this.messageHistorySize) {
            return;
        }
        if (i == j) {
            this.messageHistorySize = j;
            this.chatField.setText(this.chatLastMessage);
            return;
        }
        if (this.messageHistorySize == j) {
            this.chatLastMessage = this.chatField.getText();
        }
        this.chatField.setText(this.client.inGameHud.getChatHud().getMessageHistory().get(i));
        this.chatInputSuggestor.setWindowActive(false);
        this.messageHistorySize = i;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(2, this.height - 14, this.width - 2, this.height - 2, this.client.options.getTextBackgroundColor(Integer.MIN_VALUE));
        this.chatField.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.chatInputSuggestor.render(context, mouseX, mouseY);
        MessageIndicator messageIndicator = this.client.inGameHud.getChatHud().getIndicatorAt(mouseX, mouseY);
        if (messageIndicator != null && messageIndicator.text() != null) {
            context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(messageIndicator.text(), 210), mouseX, mouseY);
        } else {
            Style style = this.getTextStyleAt(mouseX, mouseY);
            if (style != null && style.getHoverEvent() != null) {
                context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void setText(String text) {
        this.chatField.setText(text);
    }

    @Override
    protected void addScreenNarrations(NarrationMessageBuilder messageBuilder) {
        messageBuilder.put(NarrationPart.TITLE, this.getTitle());
        messageBuilder.put(NarrationPart.USAGE, USAGE_TEXT);
        String string = this.chatField.getText();
        if (!string.isEmpty()) {
            messageBuilder.nextMessage().put(NarrationPart.TITLE, (Text)Text.translatable("chat_screen.message", string));
        }
    }

    @Nullable
    private Style getTextStyleAt(double x, double y) {
        return this.client.inGameHud.getChatHud().getTextStyleAt(x, y);
    }

    public boolean sendMessage(String chatText, boolean addToHistory) {
        if ((chatText = this.normalize(chatText)).isEmpty()) {
            return true;
        }
        if (addToHistory) {
            this.client.inGameHud.getChatHud().addToMessageHistory(chatText);
        }
        if (chatText.startsWith("/")) {
            this.client.player.networkHandler.sendChatCommand(chatText.substring(1));
        } else {
            this.client.player.networkHandler.sendChatMessage(chatText);
        }
        return true;
    }

    public String normalize(String chatText) {
        return StringHelper.truncateChat(StringUtils.normalizeSpace((String)chatText.trim()));
    }
}

