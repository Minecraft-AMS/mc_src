/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.mojang.logging.LogUtils;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ServerInfo {
    private static final Logger LOGGER = LogUtils.getLogger();
    public String name;
    public String address;
    public Text playerCountLabel;
    public Text label;
    @Nullable
    public ServerMetadata.Players players;
    public long ping;
    public int protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
    public Text version = Text.literal(SharedConstants.getGameVersion().getName());
    public boolean online;
    public List<Text> playerListSummary = Collections.emptyList();
    private ResourcePackPolicy resourcePackPolicy = ResourcePackPolicy.PROMPT;
    @Nullable
    private byte[] favicon;
    private boolean local;
    private boolean secureChatEnforced;

    public ServerInfo(String name, String address, boolean local) {
        this.name = name;
        this.address = address;
        this.local = local;
    }

    public NbtCompound toNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("name", this.name);
        nbtCompound.putString("ip", this.address);
        if (this.favicon != null) {
            nbtCompound.putString("icon", Base64.getEncoder().encodeToString(this.favicon));
        }
        if (this.resourcePackPolicy == ResourcePackPolicy.ENABLED) {
            nbtCompound.putBoolean("acceptTextures", true);
        } else if (this.resourcePackPolicy == ResourcePackPolicy.DISABLED) {
            nbtCompound.putBoolean("acceptTextures", false);
        }
        return nbtCompound;
    }

    public ResourcePackPolicy getResourcePackPolicy() {
        return this.resourcePackPolicy;
    }

    public void setResourcePackPolicy(ResourcePackPolicy resourcePackPolicy) {
        this.resourcePackPolicy = resourcePackPolicy;
    }

    public static ServerInfo fromNbt(NbtCompound root) {
        ServerInfo serverInfo = new ServerInfo(root.getString("name"), root.getString("ip"), false);
        if (root.contains("icon", 8)) {
            try {
                serverInfo.setFavicon(Base64.getDecoder().decode(root.getString("icon")));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                LOGGER.warn("Malformed base64 server icon", (Throwable)illegalArgumentException);
            }
        }
        if (root.contains("acceptTextures", 1)) {
            if (root.getBoolean("acceptTextures")) {
                serverInfo.setResourcePackPolicy(ResourcePackPolicy.ENABLED);
            } else {
                serverInfo.setResourcePackPolicy(ResourcePackPolicy.DISABLED);
            }
        } else {
            serverInfo.setResourcePackPolicy(ResourcePackPolicy.PROMPT);
        }
        return serverInfo;
    }

    @Nullable
    public byte[] getFavicon() {
        return this.favicon;
    }

    public void setFavicon(@Nullable byte[] favicon) {
        this.favicon = favicon;
    }

    public boolean isLocal() {
        return this.local;
    }

    public void setSecureChatEnforced(boolean secureChatEnforced) {
        this.secureChatEnforced = secureChatEnforced;
    }

    public boolean isSecureChatEnforced() {
        return this.secureChatEnforced;
    }

    public void copyFrom(ServerInfo serverInfo) {
        this.address = serverInfo.address;
        this.name = serverInfo.name;
        this.favicon = serverInfo.favicon;
    }

    public void copyWithSettingsFrom(ServerInfo serverInfo) {
        this.copyFrom(serverInfo);
        this.setResourcePackPolicy(serverInfo.getResourcePackPolicy());
        this.local = serverInfo.local;
        this.secureChatEnforced = serverInfo.secureChatEnforced;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class ResourcePackPolicy
    extends Enum<ResourcePackPolicy> {
        public static final /* enum */ ResourcePackPolicy ENABLED = new ResourcePackPolicy("enabled");
        public static final /* enum */ ResourcePackPolicy DISABLED = new ResourcePackPolicy("disabled");
        public static final /* enum */ ResourcePackPolicy PROMPT = new ResourcePackPolicy("prompt");
        private final Text name;
        private static final /* synthetic */ ResourcePackPolicy[] RESOURCE_PACK_POLICIES;

        public static ResourcePackPolicy[] values() {
            return (ResourcePackPolicy[])RESOURCE_PACK_POLICIES.clone();
        }

        public static ResourcePackPolicy valueOf(String string) {
            return Enum.valueOf(ResourcePackPolicy.class, string);
        }

        private ResourcePackPolicy(String name) {
            this.name = Text.translatable("addServer.resourcePack." + name);
        }

        public Text getName() {
            return this.name;
        }

        private static /* synthetic */ ResourcePackPolicy[] method_36896() {
            return new ResourcePackPolicy[]{ENABLED, DISABLED, PROMPT};
        }

        static {
            RESOURCE_PACK_POLICIES = ResourcePackPolicy.method_36896();
        }
    }
}

