/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Box;

@Environment(value=EnvType.CLIENT)
public interface VisibleRegion {
    public boolean intersects(Box var1);

    public void setOrigin(double var1, double var3, double var5);
}

