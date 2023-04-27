/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ReturnCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("return").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("value", IntegerArgumentType.integer()).executes(context -> ReturnCommand.execute((ServerCommandSource)context.getSource(), IntegerArgumentType.getInteger((CommandContext)context, (String)"value")))));
    }

    private static int execute(ServerCommandSource source, int value) {
        source.getReturnValueConsumer().accept(value);
        return value;
    }
}

