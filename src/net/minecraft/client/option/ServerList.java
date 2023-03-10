/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ServerList {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final MinecraftClient client;
    private final List<ServerInfo> servers = Lists.newArrayList();

    public ServerList(MinecraftClient client) {
        this.client = client;
        this.loadFile();
    }

    public void loadFile() {
        try {
            this.servers.clear();
            NbtCompound nbtCompound = NbtIo.read(new File(this.client.runDirectory, "servers.dat"));
            if (nbtCompound == null) {
                return;
            }
            NbtList nbtList = nbtCompound.getList("servers", 10);
            for (int i = 0; i < nbtList.size(); ++i) {
                this.servers.add(ServerInfo.fromNbt(nbtList.getCompound(i)));
            }
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't load server list", (Throwable)exception);
        }
    }

    public void saveFile() {
        try {
            NbtList nbtList = new NbtList();
            for (ServerInfo serverInfo : this.servers) {
                nbtList.add(serverInfo.toNbt());
            }
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.put("servers", nbtList);
            File file = File.createTempFile("servers", ".dat", this.client.runDirectory);
            NbtIo.write(nbtCompound, file);
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

    public void remove(ServerInfo serverInfo) {
        this.servers.remove(serverInfo);
    }

    public void add(ServerInfo serverInfo) {
        this.servers.add(serverInfo);
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

    public static void updateServerListEntry(ServerInfo e) {
        ServerList serverList = new ServerList(MinecraftClient.getInstance());
        serverList.loadFile();
        for (int i = 0; i < serverList.size(); ++i) {
            ServerInfo serverInfo = serverList.get(i);
            if (!serverInfo.name.equals(e.name) || !serverInfo.address.equals(e.address)) continue;
            serverList.set(i, e);
            break;
        }
        serverList.saveFile();
    }
}

