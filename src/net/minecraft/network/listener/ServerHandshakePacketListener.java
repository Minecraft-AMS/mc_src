/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.listener;

import net.minecraft.network.listener.ServerPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;

public interface ServerHandshakePacketListener
extends ServerPacketListener {
    public void onHandshake(HandshakeC2SPacket var1);
}

