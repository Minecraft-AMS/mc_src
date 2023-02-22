/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.gui.FetchRateLimiter;

@Environment(value=EnvType.CLIENT)
public class DummyFetchRateLimiter
implements FetchRateLimiter {
    @Override
    public void onRun() {
    }

    @Override
    public long getRemainingPeriod() {
        return 0L;
    }
}

