/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.network.PacketByteBuf;

public interface ArgumentSerializer<T extends ArgumentType<?>> {
    public void toPacket(T var1, PacketByteBuf var2);

    public T fromPacket(PacketByteBuf var1);

    public void toJson(T var1, JsonObject var2);
}

