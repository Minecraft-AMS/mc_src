/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.TypeAdapterFactory
 */
package net.minecraft.network.packet.s2c.query;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;

public class QueryResponseS2CPacket
implements Packet<ClientQueryPacketListener> {
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(ServerMetadata.Version.class, (Object)new ServerMetadata.Version.Serializer()).registerTypeAdapter(ServerMetadata.Players.class, (Object)new ServerMetadata.Players.Deserializer()).registerTypeAdapter(ServerMetadata.class, (Object)new ServerMetadata.Deserializer()).registerTypeHierarchyAdapter(Text.class, (Object)new Text.Serializer()).registerTypeHierarchyAdapter(Style.class, (Object)new Style.Serializer()).registerTypeAdapterFactory((TypeAdapterFactory)new LowercaseEnumTypeAdapterFactory()).create();
    private final ServerMetadata metadata;

    public QueryResponseS2CPacket(ServerMetadata metadata) {
        this.metadata = metadata;
    }

    public QueryResponseS2CPacket(PacketByteBuf buf) {
        this.metadata = JsonHelper.deserialize(GSON, buf.readString(Short.MAX_VALUE), ServerMetadata.class);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(GSON.toJson((Object)this.metadata));
    }

    @Override
    public void apply(ClientQueryPacketListener clientQueryPacketListener) {
        clientQueryPacketListener.onResponse(this);
    }

    public ServerMetadata getServerMetadata() {
        return this.metadata;
    }
}

