/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;

@FunctionalInterface
public interface CharacterVisitor {
    @Environment(value=EnvType.CLIENT)
    public boolean accept(int var1, Style var2, int var3);
}

