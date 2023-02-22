/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.profiler;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;

public interface ReadableProfiler
extends Profiler {
    @Override
    public void push(String var1);

    @Override
    public void push(Supplier<String> var1);

    @Override
    public void pop();

    @Override
    public void swap(String var1);

    @Override
    @Environment(value=EnvType.CLIENT)
    public void swap(Supplier<String> var1);

    public ProfileResult getResult();
}

