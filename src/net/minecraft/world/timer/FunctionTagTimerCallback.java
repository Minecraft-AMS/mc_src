/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.timer;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.timer.TimerCallback;

public class FunctionTagTimerCallback
implements TimerCallback<MinecraftServer> {
    private final Identifier name;

    public FunctionTagTimerCallback(Identifier identifier) {
        this.name = identifier;
    }

    @Override
    public void call(MinecraftServer minecraftServer, Timer<MinecraftServer> timer, long l) {
        CommandFunctionManager commandFunctionManager = minecraftServer.getCommandFunctionManager();
        Tag<CommandFunction> tag = commandFunctionManager.method_29462(this.name);
        for (CommandFunction commandFunction : tag.values()) {
            commandFunctionManager.execute(commandFunction, commandFunctionManager.getTaggedFunctionSource());
        }
    }

    public static class Serializer
    extends TimerCallback.Serializer<MinecraftServer, FunctionTagTimerCallback> {
        public Serializer() {
            super(new Identifier("function_tag"), FunctionTagTimerCallback.class);
        }

        @Override
        public void serialize(NbtCompound nbtCompound, FunctionTagTimerCallback functionTagTimerCallback) {
            nbtCompound.putString("Name", functionTagTimerCallback.name.toString());
        }

        @Override
        public FunctionTagTimerCallback deserialize(NbtCompound nbtCompound) {
            Identifier identifier = new Identifier(nbtCompound.getString("Name"));
            return new FunctionTagTimerCallback(identifier);
        }

        @Override
        public /* synthetic */ TimerCallback deserialize(NbtCompound tag) {
            return this.deserialize(tag);
        }
    }
}

