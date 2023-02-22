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
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;

@Environment(value=EnvType.CLIENT)
public abstract class RealmListEntry
extends AlwaysSelectedEntryListWidget.Entry<RealmListEntry> {
    @Override
    public abstract void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9);

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }
}

