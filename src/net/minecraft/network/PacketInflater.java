/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.ByteToMessageDecoder
 *  io.netty.handler.codec.DecoderException
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.Inflater;
import net.minecraft.util.PacketByteBuf;

public class PacketInflater
extends ByteToMessageDecoder {
    private final Inflater inflater;
    private int compressionThreshold;

    public PacketInflater(int i) {
        this.compressionThreshold = i;
        this.inflater = new Inflater();
    }

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() == 0) {
            return;
        }
        PacketByteBuf packetByteBuf = new PacketByteBuf(byteBuf);
        int i = packetByteBuf.readVarInt();
        if (i == 0) {
            list.add(packetByteBuf.readBytes(packetByteBuf.readableBytes()));
        } else {
            if (i < this.compressionThreshold) {
                throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.compressionThreshold);
            }
            if (i > 0x200000) {
                throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of " + 0x200000);
            }
            byte[] bs = new byte[packetByteBuf.readableBytes()];
            packetByteBuf.readBytes(bs);
            this.inflater.setInput(bs);
            byte[] cs = new byte[i];
            this.inflater.inflate(cs);
            list.add(Unpooled.wrappedBuffer((byte[])cs));
            this.inflater.reset();
        }
    }

    public void setCompressionThreshold(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }
}

