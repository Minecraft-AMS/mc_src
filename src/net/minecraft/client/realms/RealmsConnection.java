/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms;

import java.net.InetSocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.DisconnectedRealmsScreen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsConnection {
    static final Logger LOGGER = LogManager.getLogger();
    final Screen onlineScreen;
    volatile boolean aborted;
    ClientConnection connection;

    public RealmsConnection(Screen onlineScreen) {
        this.onlineScreen = onlineScreen;
    }

    public void connect(final RealmsServer server, ServerAddress address) {
        final MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.setConnectedToRealms(true);
        NarratorManager.INSTANCE.narrate(new TranslatableText("mco.connect.success"));
        final String string = address.getAddress();
        final int i = address.getPort();
        new Thread("Realms-connect-task"){

            @Override
            public void run() {
                InetSocketAddress inetSocketAddress = null;
                try {
                    inetSocketAddress = new InetSocketAddress(string, i);
                    if (RealmsConnection.this.aborted) {
                        return;
                    }
                    RealmsConnection.this.connection = ClientConnection.connect(inetSocketAddress, minecraftClient.options.shouldUseNativeTransport());
                    if (RealmsConnection.this.aborted) {
                        return;
                    }
                    RealmsConnection.this.connection.setPacketListener(new ClientLoginNetworkHandler(RealmsConnection.this.connection, minecraftClient, RealmsConnection.this.onlineScreen, status -> {}));
                    if (RealmsConnection.this.aborted) {
                        return;
                    }
                    RealmsConnection.this.connection.send(new HandshakeC2SPacket(string, i, NetworkState.LOGIN));
                    if (RealmsConnection.this.aborted) {
                        return;
                    }
                    RealmsConnection.this.connection.send(new LoginHelloC2SPacket(minecraftClient.getSession().getProfile()));
                    minecraftClient.setCurrentServerEntry(server.createServerInfo(string));
                }
                catch (Exception exception) {
                    minecraftClient.getResourcePackProvider().clear();
                    if (RealmsConnection.this.aborted) {
                        return;
                    }
                    LOGGER.error("Couldn't connect to world", (Throwable)exception);
                    String string3 = exception.toString();
                    if (inetSocketAddress != null) {
                        String string2 = inetSocketAddress + ":" + i;
                        string3 = string3.replaceAll(string2, "");
                    }
                    DisconnectedRealmsScreen disconnectedRealmsScreen = new DisconnectedRealmsScreen(RealmsConnection.this.onlineScreen, ScreenTexts.CONNECT_FAILED, new TranslatableText("disconnect.genericReason", string3));
                    minecraftClient.execute(() -> minecraftClient.setScreen(disconnectedRealmsScreen));
                }
            }
        }.start();
    }

    public void abort() {
        this.aborted = true;
        if (this.connection != null && this.connection.isOpen()) {
            this.connection.disconnect(new TranslatableText("disconnect.genericReason"));
            this.connection.handleDisconnection();
        }
    }

    public void tick() {
        if (this.connection != null) {
            if (this.connection.isOpen()) {
                this.connection.tick();
            } else {
                this.connection.handleDisconnection();
            }
        }
    }
}

