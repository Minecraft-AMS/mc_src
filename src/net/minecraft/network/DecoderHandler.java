/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.ByteToMessageDecoder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class DecoderHandler
extends ByteToMessageDecoder {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker((String)"PACKET_RECEIVED", (Marker)ClientConnection.NETWORK_PACKETS_MARKER);
    private final NetworkSide side;

    public DecoderHandler(NetworkSide side) {
        this.side = side;
    }

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() == 0) {
            return;
        }
        PacketByteBuf packetByteBuf = new PacketByteBuf(byteBuf);
        int i = packetByteBuf.readVarInt();
        Packet<?> packet = ((NetworkState)((Object)channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get())).getPacketHandler(this.side, i, packetByteBuf);
        if (packet == null) {
            throw new IOException("Bad packet id " + i);
        }
        if (packetByteBuf.readableBytes() > 0) {
            throw new IOException("Packet " + ((NetworkState)((Object)channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get())).getId() + "/" + i + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + packetByteBuf.readableBytes() + " bytes extra whilst reading packet " + i);
        }
        list.add(packet);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(MARKER, " IN: [{}:{}] {}", channelHandlerContext.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get(), (Object)i, (Object)packet.getClass().getName());
        }
    }
}

