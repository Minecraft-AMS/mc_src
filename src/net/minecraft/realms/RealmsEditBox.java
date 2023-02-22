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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.realms.RealmsGuiEventListener;

@Environment(value=EnvType.CLIENT)
public class RealmsEditBox
extends RealmsGuiEventListener {
    private final TextFieldWidget editBox;

    public RealmsEditBox(int i, int j, int k, int l, int m, String string) {
        this.editBox = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, j, k, l, m, null, string);
    }

    public String getValue() {
        return this.editBox.getText();
    }

    public void tick() {
        this.editBox.tick();
    }

    public void setValue(String string) {
        this.editBox.setText(string);
    }

    @Override
    public boolean charTyped(char c, int i) {
        return this.editBox.charTyped(c, i);
    }

    @Override
    public Element getProxy() {
        return this.editBox;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        return this.editBox.keyPressed(i, j, k);
    }

    public boolean isFocused() {
        return this.editBox.isFocused();
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        return this.editBox.mouseClicked(d, e, i);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        return this.editBox.mouseReleased(d, e, i);
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        return this.editBox.mouseDragged(d, e, i, f, g);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        return this.editBox.mouseScrolled(d, e, f);
    }

    public void render(int i, int j, float f) {
        this.editBox.render(i, j, f);
    }

    public void setMaxLength(int i) {
        this.editBox.setMaxLength(i);
    }

    public void setIsEditable(boolean bl) {
        this.editBox.setEditable(bl);
    }
}
