/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 */
package net.minecraft.command.arguments.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.arguments.BrigadierArgumentTypes;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.util.PacketByteBuf;

public class IntegerArgumentSerializer
implements ArgumentSerializer<IntegerArgumentType> {
    @Override
    public void toPacket(IntegerArgumentType integerArgumentType, PacketByteBuf packetByteBuf) {
        boolean bl = integerArgumentType.getMinimum() != Integer.MIN_VALUE;
        boolean bl2 = integerArgumentType.getMaximum() != Integer.MAX_VALUE;
        packetByteBuf.writeByte(BrigadierArgumentTypes.createFlag(bl, bl2));
        if (bl) {
            packetByteBuf.writeInt(integerArgumentType.getMinimum());
        }
        if (bl2) {
            packetByteBuf.writeInt(integerArgumentType.getMaximum());
        }
    }

    @Override
    public IntegerArgumentType fromPacket(PacketByteBuf packetByteBuf) {
        byte b = packetByteBuf.readByte();
        int i = BrigadierArgumentTypes.hasMin(b) ? packetByteBuf.readInt() : Integer.MIN_VALUE;
        int j = BrigadierArgumentTypes.hasMax(b) ? packetByteBuf.readInt() : Integer.MAX_VALUE;
        return IntegerArgumentType.integer((int)i, (int)j);
    }

    @Override
    public void toJson(IntegerArgumentType integerArgumentType, JsonObject jsonObject) {
        if (integerArgumentType.getMinimum() != Integer.MIN_VALUE) {
            jsonObject.addProperty("min", (Number)integerArgumentType.getMinimum());
        }
        if (integerArgumentType.getMaximum() != Integer.MAX_VALUE) {
            jsonObject.addProperty("max", (Number)integerArgumentType.getMaximum());
        }
    }

    @Override
    public /* synthetic */ ArgumentType fromPacket(PacketByteBuf packetByteBuf) {
        return this.fromPacket(packetByteBuf);
    }
}

