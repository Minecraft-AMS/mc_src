/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.listener;

import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;

public interface PacketListener {
    public void onDisconnected(Text var1);

    public ClientConnection getConnection();

    default public boolean shouldCrashOnException() {
        return true;
    }
}

