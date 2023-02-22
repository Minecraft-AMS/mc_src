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
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.option.SimpleOptionsScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;

@Environment(value=EnvType.CLIENT)
public class OnlineOptionsScreen
extends SimpleOptionsScreen {
    public OnlineOptionsScreen(Screen parent, GameOptions gameOptions) {
        super(parent, gameOptions, Text.translatable("options.online.title"), new SimpleOption[]{gameOptions.getRealmsNotifications(), gameOptions.getAllowServerListing()});
    }

    @Override
    protected void initFooter() {
        if (this.client.world != null) {
            CyclingButtonWidget<Difficulty> cyclingButtonWidget = this.addDrawableChild(OptionsScreen.createDifficultyButtonWidget(this.options.length, this.width, this.height, "options.difficulty.online", this.client));
            cyclingButtonWidget.active = false;
        }
        super.initFooter();
    }
}

