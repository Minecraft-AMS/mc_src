/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Multimap
 *  com.mojang.authlib.GameProfile
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class PlayerListS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final EnumSet<Action> actions;
    private final List<Entry> entries;

    public PlayerListS2CPacket(EnumSet<Action> actions, Collection<ServerPlayerEntity> players) {
        this.actions = actions;
        this.entries = players.stream().map(Entry::new).toList();
    }

    public PlayerListS2CPacket(Action action, ServerPlayerEntity player) {
        this.actions = EnumSet.of(action);
        this.entries = List.of(new Entry(player));
    }

    public static PlayerListS2CPacket entryFromPlayer(Collection<ServerPlayerEntity> players) {
        EnumSet<Action[]> enumSet = EnumSet.of(Action.ADD_PLAYER, new Action[]{Action.INITIALIZE_CHAT, Action.UPDATE_GAME_MODE, Action.UPDATE_LISTED, Action.UPDATE_LATENCY, Action.UPDATE_DISPLAY_NAME});
        return new PlayerListS2CPacket(enumSet, players);
    }

    public PlayerListS2CPacket(PacketByteBuf buf) {
        this.actions = buf.readEnumSet(Action.class);
        this.entries = buf.readList(buf2 -> {
            Serialized serialized = new Serialized(buf2.readUuid());
            for (Action action : this.actions) {
                action.reader.read(serialized, (PacketByteBuf)((Object)buf2));
            }
            return serialized.toEntry();
        });
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumSet(this.actions, Action.class);
        buf.writeCollection(this.entries, (buf2, entry) -> {
            buf2.writeUuid(entry.profileId());
            for (Action action : this.actions) {
                action.writer.write((PacketByteBuf)((Object)buf2), (Entry)entry);
            }
        });
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlayerList(this);
    }

    public EnumSet<Action> getActions() {
        return this.actions;
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    public List<Entry> getPlayerAdditionEntries() {
        return this.actions.contains((Object)Action.ADD_PLAYER) ? this.entries : List.of();
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("actions", this.actions).add("entries", this.entries).toString();
    }

    public static final class Entry
    extends Record {
        private final UUID profileId;
        private final GameProfile profile;
        private final boolean listed;
        private final int latency;
        private final GameMode gameMode;
        @Nullable
        private final Text displayName;
        @Nullable
        final PublicPlayerSession.Serialized chatSession;

        Entry(ServerPlayerEntity player) {
            this(player.getUuid(), player.getGameProfile(), true, player.pingMilliseconds, player.interactionManager.getGameMode(), player.getPlayerListName(), Util.map(player.getSession(), PublicPlayerSession::toSerialized));
        }

        public Entry(UUID uUID, GameProfile gameProfile, boolean bl, int i, GameMode gameMode, @Nullable Text text, @Nullable PublicPlayerSession.Serialized serialized) {
            this.profileId = uUID;
            this.profile = gameProfile;
            this.listed = bl;
            this.latency = i;
            this.gameMode = gameMode;
            this.displayName = text;
            this.chatSession = serialized;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entry.class, "profileId;profile;listed;latency;gameMode;displayName;chatSession", "profileId", "profile", "listed", "latency", "gameMode", "displayName", "chatSession"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entry.class, "profileId;profile;listed;latency;gameMode;displayName;chatSession", "profileId", "profile", "listed", "latency", "gameMode", "displayName", "chatSession"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entry.class, "profileId;profile;listed;latency;gameMode;displayName;chatSession", "profileId", "profile", "listed", "latency", "gameMode", "displayName", "chatSession"}, this, object);
        }

        public UUID profileId() {
            return this.profileId;
        }

        public GameProfile profile() {
            return this.profile;
        }

        public boolean listed() {
            return this.listed;
        }

        public int latency() {
            return this.latency;
        }

        public GameMode gameMode() {
            return this.gameMode;
        }

        @Nullable
        public Text displayName() {
            return this.displayName;
        }

        @Nullable
        public PublicPlayerSession.Serialized chatSession() {
            return this.chatSession;
        }
    }

    public static final class Action
    extends Enum<Action> {
        public static final /* enum */ Action ADD_PLAYER = new Action((serialized, buf) -> {
            GameProfile gameProfile = new GameProfile(serialized.profileId, buf.readString(16));
            gameProfile.getProperties().putAll((Multimap)buf.readPropertyMap());
            serialized.gameProfile = gameProfile;
        }, (buf, entry) -> {
            buf.writeString(entry.profile().getName(), 16);
            buf.writePropertyMap(entry.profile().getProperties());
        });
        public static final /* enum */ Action INITIALIZE_CHAT = new Action((serialized, buf) -> {
            serialized.session = (PublicPlayerSession.Serialized)buf.readNullable(PublicPlayerSession.Serialized::fromBuf);
        }, (buf, entry) -> buf.writeNullable(entry.chatSession, PublicPlayerSession.Serialized::write));
        public static final /* enum */ Action UPDATE_GAME_MODE = new Action((serialized, buf) -> {
            serialized.gameMode = GameMode.byId(buf.readVarInt());
        }, (buf, entry) -> buf.writeVarInt(entry.gameMode().getId()));
        public static final /* enum */ Action UPDATE_LISTED = new Action((serialized, buf) -> {
            serialized.listed = buf.readBoolean();
        }, (buf, entry) -> buf.writeBoolean(entry.listed()));
        public static final /* enum */ Action UPDATE_LATENCY = new Action((serialized, buf) -> {
            serialized.latency = buf.readVarInt();
        }, (buf, entry) -> buf.writeVarInt(entry.latency()));
        public static final /* enum */ Action UPDATE_DISPLAY_NAME = new Action((serialized, buf) -> {
            serialized.displayName = (Text)buf.readNullable(PacketByteBuf::readText);
        }, (buf, entry) -> buf.writeNullable(entry.displayName(), PacketByteBuf::writeText));
        final Reader reader;
        final Writer writer;
        private static final /* synthetic */ Action[] field_29141;

        public static Action[] values() {
            return (Action[])field_29141.clone();
        }

        public static Action valueOf(String string) {
            return Enum.valueOf(Action.class, string);
        }

        private Action(Reader reader, Writer writer) {
            this.reader = reader;
            this.writer = writer;
        }

        private static /* synthetic */ Action[] method_36951() {
            return new Action[]{ADD_PLAYER, INITIALIZE_CHAT, UPDATE_GAME_MODE, UPDATE_LISTED, UPDATE_LATENCY, UPDATE_DISPLAY_NAME};
        }

        static {
            field_29141 = Action.method_36951();
        }

        public static interface Reader {
            public void read(Serialized var1, PacketByteBuf var2);
        }

        public static interface Writer {
            public void write(PacketByteBuf var1, Entry var2);
        }
    }

    static class Serialized {
        final UUID profileId;
        GameProfile gameProfile;
        boolean listed;
        int latency;
        GameMode gameMode = GameMode.DEFAULT;
        @Nullable
        Text displayName;
        @Nullable
        PublicPlayerSession.Serialized session;

        Serialized(UUID profileId) {
            this.profileId = profileId;
            this.gameProfile = new GameProfile(profileId, null);
        }

        Entry toEntry() {
            return new Entry(this.profileId, this.gameProfile, this.listed, this.latency, this.gameMode, this.displayName, this.session);
        }
    }
}

