/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.option.SimpleOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class AccessibilityOptionsScreen
extends SimpleOptionsScreen {
    private static final Option[] OPTIONS = new Option[]{Option.NARRATOR, Option.SUBTITLES, Option.TEXT_BACKGROUND_OPACITY, Option.TEXT_BACKGROUND, Option.CHAT_OPACITY, Option.CHAT_LINE_SPACING, Option.CHAT_DELAY_INSTANT, Option.AUTO_JUMP, Option.SNEAK_TOGGLED, Option.SPRINT_TOGGLED, Option.DISTORTION_EFFECT_SCALE, Option.FOV_EFFECT_SCALE, Option.MONOCHROME_LOGO, Option.HIDE_LIGHTNING_FLASHES};
    private static final String GUIDE_URL = "https://aka.ms/MinecraftJavaAccessibility";

    public AccessibilityOptionsScreen(Screen parent, GameOptions gameOptions) {
        super(parent, gameOptions, new TranslatableText("options.accessibility.title"), OPTIONS);
    }

    @Override
    protected void initFooter() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 27, 150, 20, new TranslatableText("options.accessibility.link"), button -> this.client.setScreen(new ConfirmChatLinkScreen(openInBrowser -> {
            if (openInBrowser) {
                Util.getOperatingSystem().open(GUIDE_URL);
            }
            this.client.setScreen(this);
        }, GUIDE_URL, true))));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 27, 150, 20, ScreenTexts.DONE, button -> this.client.setScreen(this.parent)));
    }
}

