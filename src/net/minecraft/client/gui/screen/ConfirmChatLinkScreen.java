/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class ConfirmChatLinkScreen
extends ConfirmScreen {
    private final String warning;
    private final String copy;
    private final String link;
    private final boolean drawWarning;

    public ConfirmChatLinkScreen(BooleanConsumer callback, String link, boolean trusted) {
        super(callback, new TranslatableText(trusted ? "chat.link.confirmTrusted" : "chat.link.confirm", new Object[0]), new LiteralText(link));
        this.yesTranslated = I18n.translate(trusted ? "chat.link.open" : "gui.yes", new Object[0]);
        this.noTranslated = I18n.translate(trusted ? "gui.cancel" : "gui.no", new Object[0]);
        this.copy = I18n.translate("chat.copy", new Object[0]);
        this.warning = I18n.translate("chat.link.warning", new Object[0]);
        this.drawWarning = !trusted;
        this.link = link;
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();
        this.children.clear();
        this.addButton(new ButtonWidget(this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.yesTranslated, buttonWidget -> this.callback.accept(true)));
        this.addButton(new ButtonWidget(this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copy, buttonWidget -> {
            this.copyToClipboard();
            this.callback.accept(false);
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.noTranslated, buttonWidget -> this.callback.accept(false)));
    }

    public void copyToClipboard() {
        this.minecraft.keyboard.setClipboard(this.link);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        if (this.drawWarning) {
            this.drawCenteredString(this.font, this.warning, this.width / 2, 110, 0xFFCCCC);
        }
    }
}

