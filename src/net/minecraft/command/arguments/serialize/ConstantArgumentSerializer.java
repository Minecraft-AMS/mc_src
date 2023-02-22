/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package net.minecraft.command.arguments.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Supplier;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.util.PacketByteBuf;

public class ConstantArgumentSerializer<T extends ArgumentType<?>>
implements ArgumentSerializer<T> {
    private final Supplier<T> supplier;

    public ConstantArgumentSerializer(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public void toPacket(T argumentType, PacketByteBuf packetByteBuf) {
    }

    @Override
    public T fromPacket(PacketByteBuf packetByteBuf) {
        return (T)((ArgumentType)this.supplier.get());
    }

    @Override
    public void toJson(T argumentType, JsonObject jsonObject) {
    }
}

