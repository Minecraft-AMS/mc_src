/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.Option;

@Environment(value=EnvType.CLIENT)
public class OptionButtonWidget
extends ButtonWidget {
    private final Option option;

    public OptionButtonWidget(int x, int y, int width, int height, Option option, String text, ButtonWidget.PressAction pressAction) {
        super(x, y, width, height, text, pressAction);
        this.option = option;
    }
}

