/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.apache.commons.io.IOUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.chase;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ChaseCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ChaseServer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String ip;
    private final int port;
    private final PlayerManager playerManager;
    private final int interval;
    private volatile boolean running;
    @Nullable
    private ServerSocket socket;
    private final CopyOnWriteArrayList<Socket> clientSockets = new CopyOnWriteArrayList();

    public ChaseServer(String ip, int port, PlayerManager playerManager, int interval) {
        this.ip = ip;
        this.port = port;
        this.playerManager = playerManager;
        this.interval = interval;
    }

    public void start() throws IOException {
        if (this.socket != null && !this.socket.isClosed()) {
            LOGGER.warn("Remote control server was asked to start, but it is already running. Will ignore.");
            return;
        }
        this.running = true;
        this.socket = new ServerSocket(this.port, 50, InetAddress.getByName(this.ip));
        Thread thread = new Thread(this::runAcceptor, "chase-server-acceptor");
        thread.setDaemon(true);
        thread.start();
        Thread thread2 = new Thread(this::runSender, "chase-server-sender");
        thread2.setDaemon(true);
        thread2.start();
    }

    private void runSender() {
        TeleportPos teleportPos = null;
        while (this.running) {
            if (!this.clientSockets.isEmpty()) {
                TeleportPos teleportPos2 = this.getTeleportPosition();
                if (teleportPos2 != null && !teleportPos2.equals(teleportPos)) {
                    teleportPos = teleportPos2;
                    byte[] bs = teleportPos2.getTeleportCommand().getBytes(StandardCharsets.US_ASCII);
                    for (Socket socket : this.clientSockets) {
                        if (socket.isClosed()) continue;
                        Util.getIoWorkerExecutor().submit(() -> {
                            try {
                                OutputStream outputStream = socket.getOutputStream();
                                outputStream.write(bs);
                                outputStream.flush();
                            }
                            catch (IOException iOException) {
                                LOGGER.info("Remote control client socket got an IO exception and will be closed", (Throwable)iOException);
                                IOUtils.closeQuietly((Socket)socket);
                            }
                        });
                    }
                }
                List list = this.clientSockets.stream().filter(Socket::isClosed).collect(Collectors.toList());
                this.clientSockets.removeAll(list);
            }
            if (!this.running) continue;
            try {
                Thread.sleep(this.interval);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    public void stop() {
        this.running = false;
        IOUtils.closeQuietly((ServerSocket)this.socket);
        this.socket = null;
    }

    private void runAcceptor() {
        try {
            while (this.running) {
                if (this.socket == null) continue;
                LOGGER.info("Remote control server is listening for connections on port {}", (Object)this.port);
                Socket socket = this.socket.accept();
                LOGGER.info("Remote control server received client connection on port {}", (Object)socket.getPort());
                this.clientSockets.add(socket);
            }
        }
        catch (ClosedByInterruptException closedByInterruptException) {
            if (this.running) {
                LOGGER.info("Remote control server closed by interrupt");
            }
        }
        catch (IOException iOException) {
            if (this.running) {
                LOGGER.error("Remote control server closed because of an IO exception", (Throwable)iOException);
            }
        }
        finally {
            IOUtils.closeQuietly((ServerSocket)this.socket);
        }
        LOGGER.info("Remote control server is now stopped");
        this.running = false;
    }

    @Nullable
    private TeleportPos getTeleportPosition() {
        List<ServerPlayerEntity> list = this.playerManager.getPlayerList();
        if (list.isEmpty()) {
            return null;
        }
        ServerPlayerEntity serverPlayerEntity = list.get(0);
        String string = (String)ChaseCommand.DIMENSIONS.inverse().get(serverPlayerEntity.getWorld().getRegistryKey());
        if (string == null) {
            return null;
        }
        return new TeleportPos(string, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), serverPlayerEntity.getYaw(), serverPlayerEntity.getPitch());
    }

    record TeleportPos(String dimensionName, double x, double y, double z, float yaw, float pitch) {
        String getTeleportCommand() {
            return String.format(Locale.ROOT, "t %s %.2f %.2f %.2f %.2f %.2f\n", this.dimensionName, this.x, this.y, this.z, Float.valueOf(this.yaw), Float.valueOf(this.pitch));
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{TeleportPos.class, "dimensionName;x;y;z;yRot;xRot", "dimensionName", "x", "y", "z", "yaw", "pitch"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TeleportPos.class, "dimensionName;x;y;z;yRot;xRot", "dimensionName", "x", "y", "z", "yaw", "pitch"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TeleportPos.class, "dimensionName;x;y;z;yRot;xRot", "dimensionName", "x", "y", "z", "yaw", "pitch"}, this, object);
        }
    }
}

