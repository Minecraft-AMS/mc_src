/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.tab;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public interface Tab {
    public Text getTitle();

    public void forEachChild(Consumer<ClickableWidget> var1);

    public void refreshGrid(ScreenRect var1);

    default public void tick() {
    }
}

