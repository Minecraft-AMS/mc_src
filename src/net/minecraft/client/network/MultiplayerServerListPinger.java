/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  io.netty.bootstrap.Bootstrap
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelException
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelOption
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.SimpleChannelInboundHandler
 *  io.netty.channel.socket.nio.NioSocketChannel
 *  io.netty.util.concurrent.GenericFutureListener
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.network;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MultiplayerServerListPinger {
    private static final Splitter ZERO_SPLITTER = Splitter.on((char)'\u0000').limit(6);
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<ClientConnection> clientConnections = Collections.synchronizedList(Lists.newArrayList());

    public void add(final ServerInfo entry, final Runnable runnable) throws UnknownHostException {
        ServerAddress serverAddress = ServerAddress.parse(entry.address);
        final ClientConnection clientConnection = ClientConnection.connect(InetAddress.getByName(serverAddress.getAddress()), serverAddress.getPort(), false);
        this.clientConnections.add(clientConnection);
        entry.label = new TranslatableText("multiplayer.status.pinging");
        entry.ping = -1L;
        entry.playerListSummary = null;
        clientConnection.setPacketListener(new ClientQueryPacketListener(){
            private boolean sentQuery;
            private boolean received;
            private long startTime;

            @Override
            public void onResponse(QueryResponseS2CPacket packet) {
                if (this.received) {
                    clientConnection.disconnect(new TranslatableText("multiplayer.status.unrequested"));
                    return;
                }
                this.received = true;
                ServerMetadata serverMetadata = packet.getServerMetadata();
                entry.label = serverMetadata.getDescription() != null ? serverMetadata.getDescription() : LiteralText.EMPTY;
                if (serverMetadata.getVersion() != null) {
                    entry.version = new LiteralText(serverMetadata.getVersion().getGameVersion());
                    entry.protocolVersion = serverMetadata.getVersion().getProtocolVersion();
                } else {
                    entry.version = new TranslatableText("multiplayer.status.old");
                    entry.protocolVersion = 0;
                }
                if (serverMetadata.getPlayers() != null) {
                    entry.playerCountLabel = MultiplayerServerListPinger.method_27647(serverMetadata.getPlayers().getOnlinePlayerCount(), serverMetadata.getPlayers().getPlayerLimit());
                    ArrayList list = Lists.newArrayList();
                    if (ArrayUtils.isNotEmpty((Object[])serverMetadata.getPlayers().getSample())) {
                        for (GameProfile gameProfile : serverMetadata.getPlayers().getSample()) {
                            list.add(new LiteralText(gameProfile.getName()));
                        }
                        if (serverMetadata.getPlayers().getSample().length < serverMetadata.getPlayers().getOnlinePlayerCount()) {
                            list.add(new TranslatableText("multiplayer.status.and_more", serverMetadata.getPlayers().getOnlinePlayerCount() - serverMetadata.getPlayers().getSample().length));
                        }
                        entry.playerListSummary = list;
                    }
                } else {
                    entry.playerCountLabel = new TranslatableText("multiplayer.status.unknown").formatted(Formatting.DARK_GRAY);
                }
                String string = null;
                if (serverMetadata.getFavicon() != null) {
                    String string2 = serverMetadata.getFavicon();
                    if (string2.startsWith("data:image/png;base64,")) {
                        string = string2.substring("data:image/png;base64,".length());
                    } else {
                        LOGGER.error("Invalid server icon (unknown format)");
                    }
                }
                if (!Objects.equals(string, entry.getIcon())) {
                    entry.setIcon(string);
                    runnable.run();
                }
                this.startTime = Util.getMeasuringTimeMs();
                clientConnection.send(new QueryPingC2SPacket(this.startTime));
                this.sentQuery = true;
            }

            @Override
            public void onPong(QueryPongS2CPacket packet) {
                long l = this.startTime;
                long m = Util.getMeasuringTimeMs();
                entry.ping = m - l;
                clientConnection.disconnect(new TranslatableText("multiplayer.status.finished"));
            }

            @Override
            public void onDisconnected(Text reason) {
                if (!this.sentQuery) {
                    LOGGER.error("Can't ping {}: {}", (Object)entry.address, (Object)reason.getString());
                    entry.label = new TranslatableText("multiplayer.status.cannot_connect").formatted(Formatting.DARK_RED);
                    entry.playerCountLabel = LiteralText.EMPTY;
                    MultiplayerServerListPinger.this.ping(entry);
                }
            }

            @Override
            public ClientConnection getConnection() {
                return clientConnection;
            }
        });
        try {
            clientConnection.send(new HandshakeC2SPacket(serverAddress.getAddress(), serverAddress.getPort(), NetworkState.STATUS));
            clientConnection.send(new QueryRequestC2SPacket());
        }
        catch (Throwable throwable) {
            LOGGER.error((Object)throwable);
        }
    }

    private void ping(final ServerInfo serverInfo) {
        final ServerAddress serverAddress = ServerAddress.parse(serverInfo.address);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)ClientConnection.CLIENT_IO_GROUP.get())).handler((ChannelHandler)new ChannelInitializer<Channel>(){

            protected void initChannel(Channel channel) throws Exception {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, (Object)true);
                }
                catch (ChannelException channelException) {
                    // empty catch block
                }
                channel.pipeline().addLast(new ChannelHandler[]{new SimpleChannelInboundHandler<ByteBuf>(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
                        super.channelActive(channelHandlerContext);
                        ByteBuf byteBuf = Unpooled.buffer();
                        try {
                            byteBuf.writeByte(254);
                            byteBuf.writeByte(1);
                            byteBuf.writeByte(250);
                            char[] cs = "MC|PingHost".toCharArray();
                            byteBuf.writeShort(cs.length);
                            for (char c : cs) {
                                byteBuf.writeChar((int)c);
                            }
                            byteBuf.writeShort(7 + 2 * serverAddress.getAddress().length());
                            byteBuf.writeByte(127);
                            cs = serverAddress.getAddress().toCharArray();
                            byteBuf.writeShort(cs.length);
                            for (char c : cs) {
                                byteBuf.writeChar((int)c);
                            }
                            byteBuf.writeInt(serverAddress.getPort());
                            channelHandlerContext.channel().writeAndFlush((Object)byteBuf).addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
                        }
                        finally {
                            byteBuf.release();
                        }
                    }

                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                        short s = byteBuf.readUnsignedByte();
                        if (s == 255) {
                            String string = new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), StandardCharsets.UTF_16BE);
                            String[] strings = (String[])Iterables.toArray((Iterable)ZERO_SPLITTER.split((CharSequence)string), String.class);
                            if ("\u00a71".equals(strings[0])) {
                                int i = MathHelper.parseInt(strings[1], 0);
                                String string2 = strings[2];
                                String string3 = strings[3];
                                int j = MathHelper.parseInt(strings[4], -1);
                                int k = MathHelper.parseInt(strings[5], -1);
                                serverInfo.protocolVersion = -1;
                                serverInfo.version = new LiteralText(string2);
                                serverInfo.label = new LiteralText(string3);
                                serverInfo.playerCountLabel = MultiplayerServerListPinger.method_27647(j, k);
                            }
                        }
                        channelHandlerContext.close();
                    }

                    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
                        channelHandlerContext.close();
                    }

                    protected /* synthetic */ void channelRead0(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
                        this.channelRead0(channelHandlerContext, (ByteBuf)object);
                    }
                }});
            }
        })).channel(NioSocketChannel.class)).connect(serverAddress.getAddress(), serverAddress.getPort());
    }

    private static Text method_27647(int i, int j) {
        return new LiteralText(Integer.toString(i)).append(new LiteralText("/").formatted(Formatting.DARK_GRAY)).append(Integer.toString(j)).formatted(Formatting.GRAY);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tick() {
        List<ClientConnection> list = this.clientConnections;
        synchronized (list) {
            Iterator<ClientConnection> iterator = this.clientConnections.iterator();
            while (iterator.hasNext()) {
                ClientConnection clientConnection = iterator.next();
                if (clientConnection.isOpen()) {
                    clientConnection.tick();
                    continue;
                }
                iterator.remove();
                clientConnection.handleDisconnection();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cancel() {
        List<ClientConnection> list = this.clientConnections;
        synchronized (list) {
            Iterator<ClientConnection> iterator = this.clientConnections.iterator();
            while (iterator.hasNext()) {
                ClientConnection clientConnection = iterator.next();
                if (!clientConnection.isOpen()) continue;
                iterator.remove();
                clientConnection.disconnect(new TranslatableText("multiplayer.status.cancelled"));
            }
        }
    }
}

