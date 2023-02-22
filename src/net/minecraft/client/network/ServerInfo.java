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
    private ResourcePackPolicy resourcePackPolicy = ResourcePackPolicy.PROMPT;
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

    public void setResourcePackPolicy(ResourcePackPolicy policy) {
        this.resourcePackPolicy = policy;
    }

    public static ServerInfo fromNbt(NbtCompound root) {
        ServerInfo serverInfo = new ServerInfo(root.getString("name"), root.getString("ip"), false);
        if (root.contains("icon", 8)) {
            serverInfo.setIcon(root.getString("icon"));
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
            this.name = new TranslatableText("addServer.resourcePack." + name);
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

