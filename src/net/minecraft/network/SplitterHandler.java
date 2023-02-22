/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.ByteToMessageDecoder
 *  io.netty.handler.codec.CorruptedFrameException
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import net.minecraft.network.PacketByteBuf;

public class SplitterHandler
extends ByteToMessageDecoder {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> objects) {
        buf.markReaderIndex();
        byte[] bs = new byte[3];
        for (int i = 0; i < bs.length; ++i) {
            if (!buf.isReadable()) {
                buf.resetReaderIndex();
                return;
            }
            bs[i] = buf.readByte();
            if (bs[i] < 0) continue;
            PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.wrappedBuffer((byte[])bs));
            try {
                int j = packetByteBuf.readVarInt();
                if (buf.readableBytes() < j) {
                    buf.resetReaderIndex();
                    return;
                }
                objects.add(buf.readBytes(j));
                return;
            }
            finally {
                packetByteBuf.release();
            }
        }
        throw new CorruptedFrameException("length wider than 21-bit");
    }
}

