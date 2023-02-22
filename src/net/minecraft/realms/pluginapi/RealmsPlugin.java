/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms.pluginapi;

import com.mojang.datafixers.util.Either;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.pluginapi.LoadedRealmsPlugin;

@Environment(value=EnvType.CLIENT)
public interface RealmsPlugin {
    public Either<LoadedRealmsPlugin, String> tryLoad(String var1);
}

