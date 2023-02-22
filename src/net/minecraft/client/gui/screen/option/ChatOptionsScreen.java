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
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ChatOptionsScreen
extends SimpleOptionsScreen {
    public ChatOptionsScreen(Screen parent, GameOptions options) {
        super(parent, options, Text.translatable("options.chat.title"), new SimpleOption[]{options.getChatVisibility(), options.getChatColors(), options.getChatLinks(), options.getChatLinksPrompt(), options.getChatOpacity(), options.getTextBackgroundOpacity(), options.getChatScale(), options.getChatLineSpacing(), options.getChatDelay(), options.getChatWidth(), options.getChatHeightFocused(), options.getChatHeightUnfocused(), options.getNarrator(), options.getAutoSuggestions(), options.getHideMatchedNames(), options.getReducedDebugInfo(), options.getChatPreview(), options.getOnlyShowSecureChat()});
    }
}

