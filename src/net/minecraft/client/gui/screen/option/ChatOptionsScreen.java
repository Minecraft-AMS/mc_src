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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.SimpleOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class ChatOptionsScreen
extends SimpleOptionsScreen {
    private static final Option[] OPTIONS = new Option[]{Option.VISIBILITY, Option.CHAT_COLOR, Option.CHAT_LINKS, Option.CHAT_LINKS_PROMPT, Option.CHAT_OPACITY, Option.TEXT_BACKGROUND_OPACITY, Option.CHAT_SCALE, Option.CHAT_LINE_SPACING, Option.CHAT_DELAY_INSTANT, Option.CHAT_WIDTH, Option.CHAT_HEIGHT_FOCUSED, Option.SATURATION, Option.NARRATOR, Option.AUTO_SUGGESTIONS, Option.HIDE_MATCHED_NAMES, Option.REDUCED_DEBUG_INFO};

    public ChatOptionsScreen(Screen parent, GameOptions options) {
        super(parent, options, new TranslatableText("options.chat.title"), OPTIONS);
    }
}

