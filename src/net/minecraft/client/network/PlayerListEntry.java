/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Maps
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.network;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerListEntry {
    private final GameProfile profile;
    private final Map<MinecraftProfileTexture.Type, Identifier> textures = Maps.newEnumMap(MinecraftProfileTexture.Type.class);
    private GameMode gameMode;
    private int latency;
    private boolean texturesLoaded;
    private String model;
    private Text displayName;
    private int field_3738;
    private int field_3736;
    private long field_3737;
    private long field_3747;
    private long field_3746;

    public PlayerListEntry(GameProfile profile) {
        this.profile = profile;
    }

    public PlayerListEntry(PlayerListS2CPacket.Entry playerListPacketEntry) {
        this.profile = playerListPacketEntry.getProfile();
        this.gameMode = playerListPacketEntry.getGameMode();
        this.latency = playerListPacketEntry.getLatency();
        this.displayName = playerListPacketEntry.getDisplayName();
    }

    public GameProfile getProfile() {
        return this.profile;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    protected void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public int getLatency() {
        return this.latency;
    }

    protected void setLatency(int latency) {
        this.latency = latency;
    }

    public boolean hasSkinTexture() {
        return this.getSkinTexture() != null;
    }

    public String getModel() {
        if (this.model == null) {
            return DefaultSkinHelper.getModel(this.profile.getId());
        }
        return this.model;
    }

    public Identifier getSkinTexture() {
        this.loadTextures();
        return (Identifier)MoreObjects.firstNonNull((Object)this.textures.get(MinecraftProfileTexture.Type.SKIN), (Object)DefaultSkinHelper.getTexture(this.profile.getId()));
    }

    @Nullable
    public Identifier getCapeTexture() {
        this.loadTextures();
        return this.textures.get(MinecraftProfileTexture.Type.CAPE);
    }

    @Nullable
    public Identifier getElytraTexture() {
        this.loadTextures();
        return this.textures.get(MinecraftProfileTexture.Type.ELYTRA);
    }

    @Nullable
    public Team getScoreboardTeam() {
        return MinecraftClient.getInstance().world.getScoreboard().getPlayerTeam(this.getProfile().getName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void loadTextures() {
        PlayerListEntry playerListEntry = this;
        synchronized (playerListEntry) {
            if (!this.texturesLoaded) {
                this.texturesLoaded = true;
                MinecraftClient.getInstance().getSkinProvider().loadSkin(this.profile, (type, identifier, minecraftProfileTexture) -> {
                    switch (type) {
                        case SKIN: {
                            this.textures.put(MinecraftProfileTexture.Type.SKIN, identifier);
                            this.model = minecraftProfileTexture.getMetadata("model");
                            if (this.model != null) break;
                            this.model = "default";
                            break;
                        }
                        case CAPE: {
                            this.textures.put(MinecraftProfileTexture.Type.CAPE, identifier);
                            break;
                        }
                        case ELYTRA: {
                            this.textures.put(MinecraftProfileTexture.Type.ELYTRA, identifier);
                        }
                    }
                }, true);
            }
        }
    }

    public void setDisplayName(@Nullable Text displayName) {
        this.displayName = displayName;
    }

    @Nullable
    public Text getDisplayName() {
        return this.displayName;
    }

    public int method_2973() {
        return this.field_3738;
    }

    public void method_2972(int i) {
        this.field_3738 = i;
    }

    public int method_2960() {
        return this.field_3736;
    }

    public void method_2965(int i) {
        this.field_3736 = i;
    }

    public long method_2974() {
        return this.field_3737;
    }

    public void method_2978(long l) {
        this.field_3737 = l;
    }

    public long method_2961() {
        return this.field_3747;
    }

    public void method_2975(long l) {
        this.field_3747 = l;
    }

    public long method_2976() {
        return this.field_3746;
    }

    public void method_2964(long l) {
        this.field_3746 = l;
    }
}
