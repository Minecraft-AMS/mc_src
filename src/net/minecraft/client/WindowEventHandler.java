/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface WindowEventHandler {
    public void onWindowFocusChanged(boolean var1);

    public void onResolutionChanged();

    public void onCursorEnterChanged();
}

