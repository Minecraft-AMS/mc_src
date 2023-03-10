/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.context.ParsedCommandNode
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.tree.CommandNode
 */
package net.minecraft.server.command;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Map;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class HelpCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.help.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("help").executes(context -> {
            Map map = dispatcher.getSmartUsage((CommandNode)dispatcher.getRoot(), (Object)((ServerCommandSource)context.getSource()));
            for (String string : map.values()) {
                ((ServerCommandSource)context.getSource()).sendFeedback(new LiteralText("/" + string), false);
            }
            return map.size();
        })).then(CommandManager.argument("command", StringArgumentType.greedyString()).executes(context -> {
            ParseResults parseResults = dispatcher.parse(StringArgumentType.getString((CommandContext)context, (String)"command"), (Object)((ServerCommandSource)context.getSource()));
            if (parseResults.getContext().getNodes().isEmpty()) {
                throw FAILED_EXCEPTION.create();
            }
            Map map = dispatcher.getSmartUsage(((ParsedCommandNode)Iterables.getLast((Iterable)parseResults.getContext().getNodes())).getNode(), (Object)((ServerCommandSource)context.getSource()));
            for (String string : map.values()) {
                ((ServerCommandSource)context.getSource()).sendFeedback(new LiteralText("/" + parseResults.getReader().getString() + " " + string), false);
            }
            return map.size();
        })));
    }
}

