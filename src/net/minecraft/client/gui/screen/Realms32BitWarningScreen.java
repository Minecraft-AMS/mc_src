/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public class Realms32BitWarningScreen
extends WarningScreen {
    private static final Text HEADER = new TranslatableText("title.32bit.deprecation.realms.header").formatted(Formatting.BOLD);
    private static final Text MESSAGE = new TranslatableText("title.32bit.deprecation.realms");
    private static final Text CHECK_MESSAGE = new TranslatableText("title.32bit.deprecation.realms.check");
    private static final Text NARRATED_TEXT = HEADER.shallowCopy().append("\n").append(MESSAGE);

    public Realms32BitWarningScreen(Screen parent) {
        super(HEADER, MESSAGE, CHECK_MESSAGE, NARRATED_TEXT, parent);
    }

    @Override
    protected void initButtons(int yOffset) {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 75, 100 + yOffset, 150, 20, ScreenTexts.DONE, button -> {
            if (this.checkbox.isChecked()) {
                this.client.options.skipRealms32BitWarning = true;
                this.client.options.write();
            }
            this.client.setScreen(this.parent);
        }));
    }
}

