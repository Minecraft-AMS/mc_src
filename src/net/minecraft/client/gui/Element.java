/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface Element {
    public static final long MAX_DOUBLE_CLICK_INTERVAL = 250L;

    default public void mouseMoved(double mouseX, double mouseY) {
    }

    default public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    default public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    default public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    default public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return false;
    }

    default public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    default public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    default public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    default public boolean changeFocus(boolean lookForwards) {
        return false;
    }

    default public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }
}

