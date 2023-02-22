/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Unit;

public interface ResourceReload {
    public CompletableFuture<Unit> whenComplete();

    @Environment(value=EnvType.CLIENT)
    public float getProgress();

    @Environment(value=EnvType.CLIENT)
    public boolean isPrepareStageComplete();

    @Environment(value=EnvType.CLIENT)
    public boolean isComplete();

    @Environment(value=EnvType.CLIENT)
    public void throwException();
}

