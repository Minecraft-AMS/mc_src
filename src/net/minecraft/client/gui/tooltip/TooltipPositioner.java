/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector2ic
 */
package net.minecraft.client.gui.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import org.joml.Vector2ic;

@Environment(value=EnvType.CLIENT)
public interface TooltipPositioner {
    public Vector2ic getPosition(Screen var1, int var2, int var3, int var4, int var5);
}

