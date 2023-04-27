/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.EncoderException
 *  io.netty.handler.codec.MessageToMessageEncoder
 */
package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketBundleHandler;
import net.minecraft.network.packet.Packet;

public class PacketUnbundler
extends MessageToMessageEncoder<Packet<?>> {
    private final NetworkSide side;

    public PacketUnbundler(NetworkSide side) {
        this.side = side;
    }

    protected void encode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, List<Object> list) throws Exception {
        PacketBundleHandler.BundlerGetter bundlerGetter = (PacketBundleHandler.BundlerGetter)channelHandlerContext.channel().attr(PacketBundleHandler.KEY).get();
        if (bundlerGetter == null) {
            throw new EncoderException("Bundler not configured: " + packet);
        }
        bundlerGetter.getBundler(this.side).forEachPacket(packet, list::add);
    }

    protected /* synthetic */ void encode(ChannelHandlerContext context, Object packet, List packets) throws Exception {
        this.encode(context, (Packet)packet, (List<Object>)packets);
    }
}

