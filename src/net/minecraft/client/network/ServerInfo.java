/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ServerInfo {
    private static final Logger LOGGER = LogUtils.getLogger();
    public String name;
    public String address;
    public Text playerCountLabel;
    public Text label;
    public long ping;
    public int protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
    public Text version = Text.literal(SharedConstants.getGameVersion().getName());
    public boolean online;
    public List<Text> playerListSummary = Collections.emptyList();
    private ResourcePackPolicy resourcePackPolicy = ResourcePackPolicy.PROMPT;
    @Nullable
    private String icon;
    private boolean local;
    @Nullable
    private ChatPreview chatPreview;
    private boolean temporaryChatPreviewState = true;
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
        if (this.icon != null) {
            nbtCompound.putString("icon", this.icon);
        }
        if (this.resourcePackPolicy == ResourcePackPolicy.ENABLED) {
            nbtCompound.putBoolean("acceptTextures", true);
        } else if (this.resourcePackPolicy == ResourcePackPolicy.DISABLED) {
            nbtCompound.putBoolean("acceptTextures", false);
        }
        if (this.chatPreview != null) {
            ChatPreview.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.chatPreview).result().ifPresent(chatPreview -> nbtCompound.put("chatPreview", (NbtElement)chatPreview));
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
        if (root.contains("chatPreview", 10)) {
            ChatPreview.CODEC.parse((DynamicOps)NbtOps.INSTANCE, (Object)root.getCompound("chatPreview")).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent(chatPreview -> {
                serverInfo.chatPreview = chatPreview;
            });
        }
        return serverInfo;
    }

    @Nullable
    public String getIcon() {
        return this.icon;
    }

    public static String parseFavicon(String favicon) throws ParseException {
        if (favicon.startsWith("data:image/png;base64,")) {
            return favicon.substring("data:image/png;base64,".length());
        }
        throw new ParseException("Unknown format", 0);
    }

    public void setIcon(@Nullable String icon) {
        this.icon = icon;
    }

    public boolean isLocal() {
        return this.local;
    }

    public void setPreviewsChat(boolean enabled) {
        if (enabled && this.chatPreview == null) {
            this.chatPreview = new ChatPreview(false, false);
        } else if (!enabled && this.chatPreview != null) {
            this.chatPreview = null;
        }
    }

    @Nullable
    public ChatPreview getChatPreview() {
        return this.chatPreview;
    }

    public void setTemporaryChatPreviewState(boolean temporaryChatPreviewState) {
        this.temporaryChatPreviewState = temporaryChatPreviewState;
    }

    public boolean shouldPreviewChat() {
        return this.temporaryChatPreviewState && this.chatPreview != null;
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
        this.icon = serverInfo.icon;
    }

    public void copyWithSettingsFrom(ServerInfo serverInfo) {
        this.copyFrom(serverInfo);
        this.setResourcePackPolicy(serverInfo.getResourcePackPolicy());
        this.local = serverInfo.local;
        this.chatPreview = Util.map(serverInfo.chatPreview, ChatPreview::copy);
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

    @Environment(value=EnvType.CLIENT)
    public static class ChatPreview {
        public static final Codec<ChatPreview> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.BOOL.optionalFieldOf("acknowledged", (Object)false).forGetter(chatPreview -> chatPreview.acknowledged), (App)Codec.BOOL.optionalFieldOf("toastShown", (Object)false).forGetter(chatPreview -> chatPreview.toastShown)).apply((Applicative)instance, ChatPreview::new));
        private boolean acknowledged;
        private boolean toastShown;

        ChatPreview(boolean acknowledged, boolean toastShown) {
            this.acknowledged = acknowledged;
            this.toastShown = toastShown;
        }

        public void setAcknowledged() {
            this.acknowledged = true;
        }

        public boolean showToast() {
            if (!this.toastShown) {
                this.toastShown = true;
                return true;
            }
            return false;
        }

        public boolean isAcknowledged() {
            return this.acknowledged;
        }

        private ChatPreview copy() {
            return new ChatPreview(this.acknowledged, this.toastShown);
        }
    }
}

