/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.test;

import net.minecraft.test.GameTestState;

public interface TestListener {
    public void onStarted(GameTestState var1);

    public void onFailed(GameTestState var1);
}

