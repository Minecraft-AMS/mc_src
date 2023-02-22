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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public abstract class PressableWidget
extends ClickableWidget {
    public PressableWidget(int i, int j, int k, int l, Text text) {
        super(i, j, k, l, text);
    }

    public abstract void onPress();

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onPress();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.active || !this.visible) {
            return false;
        }
        if (keyCode == 257 || keyCode == 32 || keyCode == 335) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.onPress();
            return true;
        }
        return false;
    }
}

