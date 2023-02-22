/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.gui.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsScreen;

@Environment(value=EnvType.CLIENT)
public abstract class RealmsScreenWithCallback<T>
extends RealmsScreen {
    abstract void callback(T var1);
}

