/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.network;

import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ServerInfo {
    public String name;
    public String address;
    public Text playerCountLabel;
    public Text label;
    public long ping;
    public int protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
    public Text version = new LiteralText(SharedConstants.getGameVersion().getName());
    public boolean online;
    public List<Text> playerListSummary = Collections.emptyList();
    private ResourcePackState resourcePackPolicy = ResourcePackState.PROMPT;
    @Nullable
    private String icon;
    private boolean local;

    public ServerInfo(String name, String address, boolean local) {
        this.name = name;
        this.address = address;
        this.local = local;
    }

    public NbtCompound toNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("name", this.name);
        nbtCompound.putString("ip", this.address);
        if (this.icon != null) {
            nbtCompound.putString("icon", this.icon);
        }
        if (this.resourcePackPolicy == ResourcePackState.ENABLED) {
            nbtCompound.putBoolean("acceptTextures", true);
        } else if (this.resourcePackPolicy == ResourcePackState.DISABLED) {
            nbtCompound.putBoolean("acceptTextures", false);
        }
        return nbtCompound;
    }

    public ResourcePackState getResourcePackPolicy() {
        return this.resourcePackPolicy;
    }

    public void setResourcePackPolicy(ResourcePackState policy) {
        this.resourcePackPolicy = policy;
    }

    public static ServerInfo fromNbt(NbtCompound root) {
        ServerInfo serverInfo = new ServerInfo(root.getString("name"), root.getString("ip"), false);
        if (root.contains("icon", 8)) {
            serverInfo.setIcon(root.getString("icon"));
        }
        if (root.contains("acceptTextures", 1)) {
            if (root.getBoolean("acceptTextures")) {
                serverInfo.setResourcePackPolicy(ResourcePackState.ENABLED);
            } else {
                serverInfo.setResourcePackPolicy(ResourcePackState.DISABLED);
            }
        } else {
            serverInfo.setResourcePackPolicy(ResourcePackState.PROMPT);
        }
        return serverInfo;
    }

    @Nullable
    public String getIcon() {
        return this.icon;
    }

    public void setIcon(@Nullable String icon) {
        this.icon = icon;
    }

    public boolean isLocal() {
        return this.local;
    }

    public void copyFrom(ServerInfo serverInfo) {
        this.address = serverInfo.address;
        this.name = serverInfo.name;
        this.setResourcePackPolicy(serverInfo.getResourcePackPolicy());
        this.icon = serverInfo.icon;
        this.local = serverInfo.local;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum ResourcePackState {
        ENABLED("enabled"),
        DISABLED("disabled"),
        PROMPT("prompt");

        private final Text name;

        private ResourcePackState(String name) {
            this.name = new TranslatableText("addServer.resourcePack." + name);
        }

        public Text getName() {
            return this.name;
        }
    }
}

