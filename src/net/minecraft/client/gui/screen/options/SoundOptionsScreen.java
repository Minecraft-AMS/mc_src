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
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.SoundSliderWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class SoundOptionsScreen
extends Screen {
    private final Screen parent;
    private final GameOptions options;

    public SoundOptionsScreen(Screen parent, GameOptions options) {
        super(new TranslatableText("options.sounds.title", new Object[0]));
        this.parent = parent;
        this.options = options;
    }

    @Override
    protected void init() {
        int i = 0;
        this.addButton(new SoundSliderWidget(this.minecraft, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.MASTER, 310));
        i += 2;
        for (SoundCategory soundCategory : SoundCategory.values()) {
            if (soundCategory == SoundCategory.MASTER) continue;
            this.addButton(new SoundSliderWidget(this.minecraft, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundCategory, 150));
            ++i;
        }
        this.addButton(new OptionButtonWidget(this.width / 2 - 75, this.height / 6 - 12 + 24 * (++i >> 1), 150, 20, Option.SUBTITLES, Option.SUBTITLES.getDisplayString(this.options), buttonWidget -> {
            Option.SUBTITLES.set(this.minecraft.options);
            buttonWidget.setMessage(Option.SUBTITLES.getDisplayString(this.minecraft.options));
            this.minecraft.options.write();
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> this.minecraft.openScreen(this.parent)));
    }

    @Override
    public void removed() {
        this.minecraft.options.write();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 15, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }
}

