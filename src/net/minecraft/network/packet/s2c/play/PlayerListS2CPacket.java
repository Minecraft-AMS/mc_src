/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class PlayerListS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final Action action;
    private final List<Entry> entries;

    public PlayerListS2CPacket(Action action, ServerPlayerEntity ... players) {
        this.action = action;
        this.entries = Lists.newArrayListWithCapacity((int)players.length);
        for (ServerPlayerEntity serverPlayerEntity : players) {
            this.entries.add(PlayerListS2CPacket.entryFromPlayer(serverPlayerEntity));
        }
    }

    public PlayerListS2CPacket(Action action, Collection<ServerPlayerEntity> players) {
        this.action = action;
        this.entries = Lists.newArrayListWithCapacity((int)players.size());
        for (ServerPlayerEntity serverPlayerEntity : players) {
            this.entries.add(PlayerListS2CPacket.entryFromPlayer(serverPlayerEntity));
        }
    }

    public PlayerListS2CPacket(PacketByteBuf buf) {
        this.action = buf.readEnumConstant(Action.class);
        this.entries = buf.readList(this.action::read);
    }

    private static Entry entryFromPlayer(ServerPlayerEntity player) {
        PlayerPublicKey playerPublicKey = player.getPublicKey();
        PlayerPublicKey.PublicKeyData publicKeyData = playerPublicKey != null ? playerPublicKey.data() : null;
        return new Entry(player.getGameProfile(), player.pingMilliseconds, player.interactionManager.getGameMode(), player.getPlayerListName(), publicKeyData);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.action);
        buf.writeCollection(this.entries, this.action::write);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlayerList(this);
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    public Action getAction() {
        return this.action;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("action", (Object)this.action).add("entries", this.entries).toString();
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    public static abstract class Action
    extends Enum<Action> {
        public static final /* enum */ Action ADD_PLAYER = new Action(){

            @Override
            protected Entry read(PacketByteBuf buf) {
                GameProfile gameProfile = buf.readGameProfile();
                GameMode gameMode = GameMode.byId(buf.readVarInt());
                int i = buf.readVarInt();
                Text text = (Text)buf.readNullable(PacketByteBuf::readText);
                PlayerPublicKey.PublicKeyData publicKeyData = (PlayerPublicKey.PublicKeyData)buf.readNullable(PlayerPublicKey.PublicKeyData::new);
                return new Entry(gameProfile, i, gameMode, text, publicKeyData);
            }

            @Override
            protected void write(PacketByteBuf buf, Entry entry) {
                buf.writeGameProfile(entry.getProfile());
                buf.writeVarInt(entry.getGameMode().getId());
                buf.writeVarInt(entry.getLatency());
                buf.writeNullable(entry.getDisplayName(), PacketByteBuf::writeText);
                buf.writeNullable(entry.getPublicKeyData(), (buf2, publicKeyData) -> publicKeyData.write((PacketByteBuf)((Object)buf2)));
            }
        };
        public static final /* enum */ Action UPDATE_GAME_MODE = new Action(){

            @Override
            protected Entry read(PacketByteBuf buf) {
                GameProfile gameProfile = new GameProfile(buf.readUuid(), null);
                GameMode gameMode = GameMode.byId(buf.readVarInt());
                return new Entry(gameProfile, 0, gameMode, null, null);
            }

            @Override
            protected void write(PacketByteBuf buf, Entry entry) {
                buf.writeUuid(entry.getProfile().getId());
                buf.writeVarInt(entry.getGameMode().getId());
            }
        };
        public static final /* enum */ Action UPDATE_LATENCY = new Action(){

            @Override
            protected Entry read(PacketByteBuf buf) {
                GameProfile gameProfile = new GameProfile(buf.readUuid(), null);
                int i = buf.readVarInt();
                return new Entry(gameProfile, i, null, null, null);
            }

            @Override
            protected void write(PacketByteBuf buf, Entry entry) {
                buf.writeUuid(entry.getProfile().getId());
                buf.writeVarInt(entry.getLatency());
            }
        };
        public static final /* enum */ Action UPDATE_DISPLAY_NAME = new Action(){

            @Override
            protected Entry read(PacketByteBuf buf) {
                GameProfile gameProfile = new GameProfile(buf.readUuid(), null);
                Text text = (Text)buf.readNullable(PacketByteBuf::readText);
                return new Entry(gameProfile, 0, null, text, null);
            }

            @Override
            protected void write(PacketByteBuf buf, Entry entry) {
                buf.writeUuid(entry.getProfile().getId());
                buf.writeNullable(entry.getDisplayName(), PacketByteBuf::writeText);
            }
        };
        public static final /* enum */ Action REMOVE_PLAYER = new Action(){

            @Override
            protected Entry read(PacketByteBuf buf) {
                GameProfile gameProfile = new GameProfile(buf.readUuid(), null);
                return new Entry(gameProfile, 0, null, null, null);
            }

            @Override
            protected void write(PacketByteBuf buf, Entry entry) {
                buf.writeUuid(entry.getProfile().getId());
            }
        };
        private static final /* synthetic */ Action[] field_29141;

        public static Action[] values() {
            return (Action[])field_29141.clone();
        }

        public static Action valueOf(String string) {
            return Enum.valueOf(Action.class, string);
        }

        protected abstract Entry read(PacketByteBuf var1);

        protected abstract void write(PacketByteBuf var1, Entry var2);

        private static /* synthetic */ Action[] method_36951() {
            return new Action[]{ADD_PLAYER, UPDATE_GAME_MODE, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, REMOVE_PLAYER};
        }

        static {
            field_29141 = Action.method_36951();
        }
    }

    public static class Entry {
        private final int latency;
        private final GameMode gameMode;
        private final GameProfile profile;
        @Nullable
        private final Text displayName;
        @Nullable
        private final PlayerPublicKey.PublicKeyData publicKeyData;

        public Entry(GameProfile profile, int latency, @Nullable GameMode gameMode, @Nullable Text displayName, @Nullable PlayerPublicKey.PublicKeyData publicKeyData) {
            this.profile = profile;
            this.latency = latency;
            this.gameMode = gameMode;
            this.displayName = displayName;
            this.publicKeyData = publicKeyData;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public int getLatency() {
            return this.latency;
        }

        public GameMode getGameMode() {
            return this.gameMode;
        }

        @Nullable
        public Text getDisplayName() {
            return this.displayName;
        }

        @Nullable
        public PlayerPublicKey.PublicKeyData getPublicKeyData() {
            return this.publicKeyData;
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("latency", this.latency).add("gameMode", (Object)this.gameMode).add("profile", (Object)this.profile).add("displayName", this.displayName == null ? null : Text.Serializer.toJson(this.displayName)).add("profilePublicKey", (Object)this.publicKeyData).toString();
        }
    }
}

