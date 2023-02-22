/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  org.slf4j.Logger
 */
package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.slf4j.Logger;

public class RateLimitedConnection
extends ClientConnection {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text RATE_LIMIT_EXCEEDED_MESSAGE = new TranslatableText("disconnect.exceeded_packet_rate");
    private final int rateLimit;

    public RateLimitedConnection(int rateLimit) {
        super(NetworkSide.SERVERBOUND);
        this.rateLimit = rateLimit;
    }

    @Override
    protected void updateStats() {
        super.updateStats();
        float f = this.getAveragePacketsReceived();
        if (f > (float)this.rateLimit) {
            LOGGER.warn("Player exceeded rate-limit (sent {} packets per second)", (Object)Float.valueOf(f));
            this.send(new DisconnectS2CPacket(RATE_LIMIT_EXCEEDED_MESSAGE), (GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener)future -> this.disconnect(RATE_LIMIT_EXCEEDED_MESSAGE)));
            this.disableAutoRead();
        }
    }
}

