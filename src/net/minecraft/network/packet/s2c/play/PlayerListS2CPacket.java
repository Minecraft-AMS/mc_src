/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.authlib.properties.PropertyMap
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
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
            this.entries.add(new Entry(serverPlayerEntity.getGameProfile(), serverPlayerEntity.pingMilliseconds, serverPlayerEntity.interactionManager.getGameMode(), serverPlayerEntity.getPlayerListName()));
        }
    }

    public PlayerListS2CPacket(Action action, Collection<ServerPlayerEntity> players) {
        this.action = action;
        this.entries = Lists.newArrayListWithCapacity((int)players.size());
        for (ServerPlayerEntity serverPlayerEntity : players) {
            this.entries.add(new Entry(serverPlayerEntity.getGameProfile(), serverPlayerEntity.pingMilliseconds, serverPlayerEntity.interactionManager.getGameMode(), serverPlayerEntity.getPlayerListName()));
        }
    }

    public PlayerListS2CPacket(PacketByteBuf buf) {
        this.action = buf.readEnumConstant(Action.class);
        this.entries = buf.readList(this.action::read);
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

    @Nullable
    static Text readOptionalText(PacketByteBuf buf) {
        return buf.readBoolean() ? buf.readText() : null;
    }

    static void writeOptionalText(PacketByteBuf buf, @Nullable Text text) {
        if (text == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeText(text);
        }
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
            protected Entry read(PacketByteBuf buf2) {
                GameProfile gameProfile = new GameProfile(buf2.readUuid(), buf2.readString(16));
                PropertyMap propertyMap = gameProfile.getProperties();
                buf2.forEachInCollection(buf -> {
                    String string = buf.readString();
                    String string2 = buf.readString();
                    if (buf.readBoolean()) {
                        String string3 = buf.readString();
                        propertyMap.put((Object)string, (Object)new Property(string, string2, string3));
                    } else {
                        propertyMap.put((Object)string, (Object)new Property(string, string2));
                    }
                });
                GameMode gameMode = GameMode.byId(buf2.readVarInt());
                int i = buf2.readVarInt();
                Text text = PlayerListS2CPacket.readOptionalText(buf2);
                return new Entry(gameProfile, i, gameMode, text);
            }

            @Override
            protected void write(PacketByteBuf buf2, Entry entry) {
                buf2.writeUuid(entry.getProfile().getId());
                buf2.writeString(entry.getProfile().getName());
                buf2.writeCollection(entry.getProfile().getProperties().values(), (buf, property) -> {
                    buf.writeString(property.getName());
                    buf.writeString(property.getValue());
                    if (property.hasSignature()) {
                        buf.writeBoolean(true);
                        buf.writeString(property.getSignature());
                    } else {
                        buf.writeBoolean(false);
                    }
                });
                buf2.writeVarInt(entry.getGameMode().getId());
                buf2.writeVarInt(entry.getLatency());
                PlayerListS2CPacket.writeOptionalText(buf2, entry.getDisplayName());
            }
        };
        public static final /* enum */ Action UPDATE_GAME_MODE = new Action(){

            @Override
            protected Entry read(PacketByteBuf buf) {
                GameProfile gameProfile = new GameProfile(buf.readUuid(), null);
                GameMode gameMode = GameMode.byId(buf.readVarInt());
                return new Entry(gameProfile, 0, gameMode, null);
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
                return new Entry(gameProfile, i, null, null);
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
                Text text = PlayerListS2CPacket.readOptionalText(buf);
                return new Entry(gameProfile, 0, null, text);
            }

            @Override
            protected void write(PacketByteBuf buf, Entry entry) {
                buf.writeUuid(entry.getProfile().getId());
                PlayerListS2CPacket.writeOptionalText(buf, entry.getDisplayName());
            }
        };
        public static final /* enum */ Action REMOVE_PLAYER = new Action(){

            @Override
            protected Entry read(PacketByteBuf buf) {
                GameProfile gameProfile = new GameProfile(buf.readUuid(), null);
                return new Entry(gameProfile, 0, null, null);
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

        public Entry(GameProfile profile, int latency, @Nullable GameMode gameMode, @Nullable Text displayName) {
            this.profile = profile;
            this.latency = latency;
            this.gameMode = gameMode;
            this.displayName = displayName;
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

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("latency", this.latency).add("gameMode", (Object)this.gameMode).add("profile", (Object)this.profile).add("displayName", this.displayName == null ? null : Text.Serializer.toJson(this.displayName)).toString();
        }
    }
}

