/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.option;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Util;
import net.minecraft.util.thread.TaskExecutor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ServerList {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final TaskExecutor<Runnable> IO_EXECUTOR = TaskExecutor.create(Util.getMainWorkerExecutor(), "server-list-io");
    private static final int MAX_HIDDEN_ENTRIES = 16;
    private final MinecraftClient client;
    private final List<ServerInfo> servers = Lists.newArrayList();
    private final List<ServerInfo> hiddenServers = Lists.newArrayList();

    public ServerList(MinecraftClient client) {
        this.client = client;
        this.loadFile();
    }

    public void loadFile() {
        try {
            this.servers.clear();
            this.hiddenServers.clear();
            NbtCompound nbtCompound = NbtIo.read(new File(this.client.runDirectory, "servers.dat"));
            if (nbtCompound == null) {
                return;
            }
            NbtList nbtList = nbtCompound.getList("servers", 10);
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound2 = nbtList.getCompound(i);
                ServerInfo serverInfo = ServerInfo.fromNbt(nbtCompound2);
                if (nbtCompound2.getBoolean("hidden")) {
                    this.hiddenServers.add(serverInfo);
                    continue;
                }
                this.servers.add(serverInfo);
            }
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't load server list", (Throwable)exception);
        }
    }

    public void saveFile() {
        try {
            NbtCompound nbtCompound;
            NbtList nbtList = new NbtList();
            for (ServerInfo serverInfo : this.servers) {
                nbtCompound = serverInfo.toNbt();
                nbtCompound.putBoolean("hidden", false);
                nbtList.add(nbtCompound);
            }
            for (ServerInfo serverInfo : this.hiddenServers) {
                nbtCompound = serverInfo.toNbt();
                nbtCompound.putBoolean("hidden", true);
                nbtList.add(nbtCompound);
            }
            NbtCompound nbtCompound2 = new NbtCompound();
            nbtCompound2.put("servers", nbtList);
            File file = File.createTempFile("servers", ".dat", this.client.runDirectory);
            NbtIo.write(nbtCompound2, file);
            File file2 = new File(this.client.runDirectory, "servers.dat_old");
            File file3 = new File(this.client.runDirectory, "servers.dat");
            Util.backupAndReplace(file3, file, file2);
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't save server list", (Throwable)exception);
        }
    }

    public ServerInfo get(int index) {
        return this.servers.get(index);
    }

    @Nullable
    public ServerInfo get(String address) {
        for (ServerInfo serverInfo : this.servers) {
            if (!serverInfo.address.equals(address)) continue;
            return serverInfo;
        }
        for (ServerInfo serverInfo : this.hiddenServers) {
            if (!serverInfo.address.equals(address)) continue;
            return serverInfo;
        }
        return null;
    }

    @Nullable
    public ServerInfo tryUnhide(String address) {
        for (int i = 0; i < this.hiddenServers.size(); ++i) {
            ServerInfo serverInfo = this.hiddenServers.get(i);
            if (!serverInfo.address.equals(address)) continue;
            this.hiddenServers.remove(i);
            this.servers.add(serverInfo);
            return serverInfo;
        }
        return null;
    }

    public void remove(ServerInfo serverInfo) {
        if (!this.servers.remove(serverInfo)) {
            this.hiddenServers.remove(serverInfo);
        }
    }

    public void add(ServerInfo serverInfo, boolean hidden) {
        if (hidden) {
            this.hiddenServers.add(0, serverInfo);
            while (this.hiddenServers.size() > 16) {
                this.hiddenServers.remove(this.hiddenServers.size() - 1);
            }
        } else {
            this.servers.add(serverInfo);
        }
    }

    public int size() {
        return this.servers.size();
    }

    public void swapEntries(int index1, int index2) {
        ServerInfo serverInfo = this.get(index1);
        this.servers.set(index1, this.get(index2));
        this.servers.set(index2, serverInfo);
        this.saveFile();
    }

    public void set(int index, ServerInfo serverInfo) {
        this.servers.set(index, serverInfo);
    }

    private static boolean replace(ServerInfo serverInfo, List<ServerInfo> serverInfos) {
        for (int i = 0; i < serverInfos.size(); ++i) {
            ServerInfo serverInfo2 = serverInfos.get(i);
            if (!serverInfo2.name.equals(serverInfo.name) || !serverInfo2.address.equals(serverInfo.address)) continue;
            serverInfos.set(i, serverInfo);
            return true;
        }
        return false;
    }

    public static void updateServerListEntry(ServerInfo serverInfo) {
        IO_EXECUTOR.send(() -> {
            ServerList serverList = new ServerList(MinecraftClient.getInstance());
            serverList.loadFile();
            if (!ServerList.replace(serverInfo, serverList.servers)) {
                ServerList.replace(serverInfo, serverList.hiddenServers);
            }
            serverList.saveFile();
        });
    }
}

