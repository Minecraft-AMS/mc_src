/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.exceptions.AuthenticationUnavailableException
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.SecretKey;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.UncaughtExceptionLogger;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ServerLoginNetworkHandler
implements ServerLoginPacketListener {
    private static final AtomicInteger authenticatorThreadId = new AtomicInteger(0);
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RANDOM = new Random();
    private final byte[] nonce = new byte[4];
    private final MinecraftServer server;
    public final ClientConnection connection;
    private State state = State.HELLO;
    private int loginTicks;
    private GameProfile profile;
    private final String field_14165 = "";
    private SecretKey secretKey;
    private ServerPlayerEntity clientEntity;

    public ServerLoginNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection) {
        this.server = minecraftServer;
        this.connection = clientConnection;
        RANDOM.nextBytes(this.nonce);
    }

    public void tick() {
        ServerPlayerEntity serverPlayerEntity;
        if (this.state == State.READY_TO_ACCEPT) {
            this.acceptPlayer();
        } else if (this.state == State.DELAY_ACCEPT && (serverPlayerEntity = this.server.getPlayerManager().getPlayer(this.profile.getId())) == null) {
            this.state = State.READY_TO_ACCEPT;
            this.server.getPlayerManager().onPlayerConnect(this.connection, this.clientEntity);
            this.clientEntity = null;
        }
        if (this.loginTicks++ == 600) {
            this.disconnect(new TranslatableText("multiplayer.disconnect.slow_login", new Object[0]));
        }
    }

    @Override
    public ClientConnection getConnection() {
        return this.connection;
    }

    public void disconnect(Text reason) {
        try {
            LOGGER.info("Disconnecting {}: {}", (Object)this.getConnectionInfo(), (Object)reason.getString());
            this.connection.send(new LoginDisconnectS2CPacket(reason));
            this.connection.disconnect(reason);
        }
        catch (Exception exception) {
            LOGGER.error("Error whilst disconnecting player", (Throwable)exception);
        }
    }

    public void acceptPlayer() {
        Text text;
        if (!this.profile.isComplete()) {
            this.profile = this.toOfflineProfile(this.profile);
        }
        if ((text = this.server.getPlayerManager().checkCanJoin(this.connection.getAddress(), this.profile)) != null) {
            this.disconnect(text);
        } else {
            this.state = State.ACCEPTED;
            if (this.server.getNetworkCompressionThreshold() >= 0 && !this.connection.isLocal()) {
                this.connection.send(new LoginCompressionS2CPacket(this.server.getNetworkCompressionThreshold()), (GenericFutureListener<? extends Future<? super Void>>)((ChannelFutureListener)channelFuture -> this.connection.setCompressionThreshold(this.server.getNetworkCompressionThreshold())));
            }
            this.connection.send(new LoginSuccessS2CPacket(this.profile));
            ServerPlayerEntity serverPlayerEntity = this.server.getPlayerManager().getPlayer(this.profile.getId());
            if (serverPlayerEntity != null) {
                this.state = State.DELAY_ACCEPT;
                this.clientEntity = this.server.getPlayerManager().createPlayer(this.profile);
            } else {
                this.server.getPlayerManager().onPlayerConnect(this.connection, this.server.getPlayerManager().createPlayer(this.profile));
            }
        }
    }

    @Override
    public void onDisconnected(Text reason) {
        LOGGER.info("{} lost connection: {}", (Object)this.getConnectionInfo(), (Object)reason.getString());
    }

    public String getConnectionInfo() {
        if (this.profile != null) {
            return this.profile + " (" + this.connection.getAddress() + ")";
        }
        return String.valueOf(this.connection.getAddress());
    }

    @Override
    public void onHello(LoginHelloC2SPacket packet) {
        Validate.validState((this.state == State.HELLO ? 1 : 0) != 0, (String)"Unexpected hello packet", (Object[])new Object[0]);
        this.profile = packet.getProfile();
        if (this.server.isOnlineMode() && !this.connection.isLocal()) {
            this.state = State.KEY;
            this.connection.send(new LoginHelloS2CPacket("", this.server.getKeyPair().getPublic(), this.nonce));
        } else {
            this.state = State.READY_TO_ACCEPT;
        }
    }

    @Override
    public void onKey(LoginKeyC2SPacket packet) {
        Validate.validState((this.state == State.KEY ? 1 : 0) != 0, (String)"Unexpected key packet", (Object[])new Object[0]);
        PrivateKey privateKey = this.server.getKeyPair().getPrivate();
        if (!Arrays.equals(this.nonce, packet.decryptNonce(privateKey))) {
            throw new IllegalStateException("Invalid nonce!");
        }
        this.secretKey = packet.decryptSecretKey(privateKey);
        this.state = State.AUTHENTICATING;
        this.connection.setupEncryption(this.secretKey);
        Thread thread = new Thread("User Authenticator #" + authenticatorThreadId.incrementAndGet()){

            @Override
            public void run() {
                GameProfile gameProfile = ServerLoginNetworkHandler.this.profile;
                try {
                    String string = new BigInteger(NetworkEncryptionUtils.generateServerId("", ServerLoginNetworkHandler.this.server.getKeyPair().getPublic(), ServerLoginNetworkHandler.this.secretKey)).toString(16);
                    ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.server.getSessionService().hasJoinedServer(new GameProfile(null, gameProfile.getName()), string, this.method_14386());
                    if (ServerLoginNetworkHandler.this.profile != null) {
                        LOGGER.info("UUID of player {} is {}", (Object)ServerLoginNetworkHandler.this.profile.getName(), (Object)ServerLoginNetworkHandler.this.profile.getId());
                        ServerLoginNetworkHandler.this.state = State.READY_TO_ACCEPT;
                    } else if (ServerLoginNetworkHandler.this.server.isSinglePlayer()) {
                        LOGGER.warn("Failed to verify username but will let them in anyway!");
                        ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.toOfflineProfile(gameProfile);
                        ServerLoginNetworkHandler.this.state = State.READY_TO_ACCEPT;
                    } else {
                        ServerLoginNetworkHandler.this.disconnect(new TranslatableText("multiplayer.disconnect.unverified_username", new Object[0]));
                        LOGGER.error("Username '{}' tried to join with an invalid session", (Object)gameProfile.getName());
                    }
                }
                catch (AuthenticationUnavailableException authenticationUnavailableException) {
                    if (ServerLoginNetworkHandler.this.server.isSinglePlayer()) {
                        LOGGER.warn("Authentication servers are down but will let them in anyway!");
                        ServerLoginNetworkHandler.this.profile = ServerLoginNetworkHandler.this.toOfflineProfile(gameProfile);
                        ServerLoginNetworkHandler.this.state = State.READY_TO_ACCEPT;
                    }
                    ServerLoginNetworkHandler.this.disconnect(new TranslatableText("multiplayer.disconnect.authservers_down", new Object[0]));
                    LOGGER.error("Couldn't verify username because servers are unavailable");
                }
            }

            @Nullable
            private InetAddress method_14386() {
                SocketAddress socketAddress = ServerLoginNetworkHandler.this.connection.getAddress();
                return ServerLoginNetworkHandler.this.server.shouldPreventProxyConnections() && socketAddress instanceof InetSocketAddress ? ((InetSocketAddress)socketAddress).getAddress() : null;
            }
        };
        thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
        thread.start();
    }

    @Override
    public void onQueryResponse(LoginQueryResponseC2SPacket packet) {
        this.disconnect(new TranslatableText("multiplayer.disconnect.unexpected_query_response", new Object[0]));
    }

    protected GameProfile toOfflineProfile(GameProfile profile) {
        UUID uUID = PlayerEntity.getOfflinePlayerUuid(profile.getName());
        return new GameProfile(uUID, profile.getName());
    }

    static enum State {
        HELLO,
        KEY,
        AUTHENTICATING,
        NEGOTIATING,
        READY_TO_ACCEPT,
        DELAY_ACCEPT,
        ACCEPTED;

    }
}

