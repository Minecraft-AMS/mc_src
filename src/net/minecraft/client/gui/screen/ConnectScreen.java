/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screen;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.ServerAddress;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ConnectScreen
extends Screen {
    private static final AtomicInteger CONNECTOR_THREADS_COUNT = new AtomicInteger(0);
    private static final Logger LOGGER = LogManager.getLogger();
    private ClientConnection connection;
    private boolean connectingCancelled;
    private final Screen parent;
    private Text status = new TranslatableText("connect.connecting", new Object[0]);
    private long field_19097 = -1L;

    public ConnectScreen(Screen parent, MinecraftClient client, ServerInfo entry) {
        super(NarratorManager.EMPTY);
        this.minecraft = client;
        this.parent = parent;
        ServerAddress serverAddress = ServerAddress.parse(entry.address);
        client.disconnect();
        client.setCurrentServerEntry(entry);
        this.connect(serverAddress.getAddress(), serverAddress.getPort());
    }

    public ConnectScreen(Screen parent, MinecraftClient client, String address, int port) {
        super(NarratorManager.EMPTY);
        this.minecraft = client;
        this.parent = parent;
        client.disconnect();
        this.connect(address, port);
    }

    private void connect(final String address, final int port) {
        LOGGER.info("Connecting to {}, {}", (Object)address, (Object)port);
        Thread thread = new Thread("Server Connector #" + CONNECTOR_THREADS_COUNT.incrementAndGet()){

            @Override
            public void run() {
                InetAddress inetAddress = null;
                try {
                    if (ConnectScreen.this.connectingCancelled) {
                        return;
                    }
                    inetAddress = InetAddress.getByName(address);
                    ConnectScreen.this.connection = ClientConnection.connect(inetAddress, port, ConnectScreen.this.minecraft.options.shouldUseNativeTransport());
                    ConnectScreen.this.connection.setPacketListener(new ClientLoginNetworkHandler(ConnectScreen.this.connection, ConnectScreen.this.minecraft, ConnectScreen.this.parent, text -> ConnectScreen.this.setStatus(text)));
                    ConnectScreen.this.connection.send(new HandshakeC2SPacket(address, port, NetworkState.LOGIN));
                    ConnectScreen.this.connection.send(new LoginHelloC2SPacket(ConnectScreen.this.minecraft.getSession().getProfile()));
                }
                catch (UnknownHostException unknownHostException) {
                    if (ConnectScreen.this.connectingCancelled) {
                        return;
                    }
                    LOGGER.error("Couldn't connect to server", (Throwable)unknownHostException);
                    ConnectScreen.this.minecraft.execute(() -> ConnectScreen.this.minecraft.openScreen(new DisconnectedScreen(ConnectScreen.this.parent, "connect.failed", new TranslatableText("disconnect.genericReason", "Unknown host"))));
                }
                catch (Exception exception) {
                    if (ConnectScreen.this.connectingCancelled) {
                        return;
                    }
                    LOGGER.error("Couldn't connect to server", (Throwable)exception);
                    String string = inetAddress == null ? exception.toString() : exception.toString().replaceAll(inetAddress + ":" + port, "");
                    ConnectScreen.this.minecraft.execute(() -> ConnectScreen.this.minecraft.openScreen(new DisconnectedScreen(ConnectScreen.this.parent, "connect.failed", new TranslatableText("disconnect.genericReason", string))));
                }
            }
        };
        thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
        thread.start();
    }

    private void setStatus(Text status) {
        this.status = status;
    }

    @Override
    public void tick() {
        if (this.connection != null) {
            if (this.connection.isOpen()) {
                this.connection.tick();
            } else {
                this.connection.handleDisconnection();
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> {
            this.connectingCancelled = true;
            if (this.connection != null) {
                this.connection.disconnect(new TranslatableText("connect.aborted", new Object[0]));
            }
            this.minecraft.openScreen(this.parent);
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        long l = Util.getMeasuringTimeMs();
        if (l - this.field_19097 > 2000L) {
            this.field_19097 = l;
            NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.joining", new Object[0]).getString());
        }
        this.drawCenteredString(this.font, this.status.asFormattedString(), this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }
}

