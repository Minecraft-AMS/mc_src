/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.tag;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class RegistryTagContainer<T>
extends TagContainer<T> {
    private final Registry<T> registry;

    public RegistryTagContainer(Registry<T> registry, String path, String type) {
        super(registry::getOrEmpty, path, false, type);
        this.registry = registry;
    }

    public void toPacket(PacketByteBuf buf) {
        Map map = this.getEntries();
        buf.writeVarInt(map.size());
        for (Map.Entry entry : map.entrySet()) {
            buf.writeIdentifier(entry.getKey());
            buf.writeVarInt(entry.getValue().values().size());
            for (Object object : entry.getValue().values()) {
                buf.writeVarInt(this.registry.getRawId(object));
            }
        }
    }

    public void fromPacket(PacketByteBuf buf) {
        HashMap map = Maps.newHashMap();
        int i = buf.readVarInt();
        for (int j = 0; j < i; ++j) {
            Identifier identifier = buf.readIdentifier();
            int k = buf.readVarInt();
            Tag.Builder builder = Tag.Builder.create();
            for (int l = 0; l < k; ++l) {
                builder.add(this.registry.get(buf.readVarInt()));
            }
            map.put(identifier, builder.build(identifier));
        }
        this.method_20735(map);
    }
}

