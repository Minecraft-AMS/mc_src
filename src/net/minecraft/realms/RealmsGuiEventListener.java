/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;

@Environment(value=EnvType.CLIENT)
public abstract class RealmsGuiEventListener {
    public boolean mouseClicked(double d, double e, int i) {
        return false;
    }

    public boolean mouseReleased(double d, double e, int i) {
        return false;
    }

    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        return false;
    }

    public boolean mouseScrolled(double d, double e, double f) {
        return false;
    }

    public boolean keyPressed(int i, int j, int k) {
        return false;
    }

    public boolean keyReleased(int i, int j, int k) {
        return false;
    }

    public boolean charTyped(char c, int i) {
        return false;
    }

    public abstract Element getProxy();
}
