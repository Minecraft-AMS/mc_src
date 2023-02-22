/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.event.listener;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;

public interface GameEventListener {
    default public boolean shouldListenImmediately() {
        return false;
    }

    public PositionSource getPositionSource();

    public int getRange();

    public boolean listen(ServerWorld var1, GameEvent.Message var2);
}

