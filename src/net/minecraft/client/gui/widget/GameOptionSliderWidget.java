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
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;

@Environment(value=EnvType.CLIENT)
public class GameOptionSliderWidget
extends SliderWidget {
    private final DoubleOption option;

    public GameOptionSliderWidget(GameOptions gameOptions, int x, int y, int width, int height, DoubleOption option) {
        super(gameOptions, x, y, width, height, (float)option.method_18611(option.get(gameOptions)));
        this.option = option;
        this.updateMessage();
    }

    @Override
    protected void applyValue() {
        this.option.set(this.options, this.option.method_18616(this.value));
        this.options.write();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(this.option.getDisplayString(this.options));
    }
}
