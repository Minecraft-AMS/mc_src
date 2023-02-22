/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import net.minecraft.util.Unit;

public interface ResourceReload {
    public CompletableFuture<Unit> whenComplete();

    public float getProgress();

    public boolean isComplete();

    public void throwException();
}

