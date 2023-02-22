/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class AccessibilityScreen
extends Screen {
    private static final Option[] OPTIONS = new Option[]{Option.NARRATOR, Option.SUBTITLES, Option.TEXT_BACKGROUND_OPACITY, Option.TEXT_BACKGROUND, Option.CHAT_OPACITY, Option.AUTO_JUMP};
    private final Screen parent;
    private final GameOptions gameOptions;
    private AbstractButtonWidget narratorButton;

    public AccessibilityScreen(Screen parent, GameOptions gameOptions) {
        super(new TranslatableText("options.accessibility.title", new Object[0]));
        this.parent = parent;
        this.gameOptions = gameOptions;
    }

    @Override
    protected void init() {
        int i = 0;
        for (Option option : OPTIONS) {
            int j = this.width / 2 - 155 + i % 2 * 160;
            int k = this.height / 6 + 24 * (i >> 1);
            AbstractButtonWidget abstractButtonWidget = this.addButton(option.createButton(this.minecraft.options, j, k, 150));
            if (option == Option.NARRATOR) {
                this.narratorButton = abstractButtonWidget;
                abstractButtonWidget.active = NarratorManager.INSTANCE.isActive();
            }
            ++i;
        }
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 144, 200, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> this.minecraft.openScreen(this.parent)));
    }

    @Override
    public void removed() {
        this.minecraft.options.write();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }

    public void method_19366() {
        this.narratorButton.setMessage(Option.NARRATOR.getMessage(this.gameOptions));
    }
}

