/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.datafixers.util.Either
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Either;
import net.minecraft.command.arguments.FunctionArgumentType;
import net.minecraft.command.arguments.TimeArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.timer.FunctionTagTimerCallback;
import net.minecraft.world.timer.FunctionTimerCallback;
import net.minecraft.world.timer.Timer;

public class ScheduleCommand {
    private static final SimpleCommandExceptionType SAME_TICK_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.schedule.same_tick", new Object[0]));
    private static final DynamicCommandExceptionType field_20853 = new DynamicCommandExceptionType(object -> new TranslatableText("commands.schedule.cleared.failure", object));
    private static final SuggestionProvider<ServerCommandSource> field_20854 = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getWorld().getLevelProperties().getScheduledEvents().method_22592(), suggestionsBuilder);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("schedule").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("function").then(CommandManager.argument("function", FunctionArgumentType.function()).suggests(FunctionCommand.SUGGESTION_PROVIDER).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("time", TimeArgumentType.time()).executes(commandContext -> ScheduleCommand.execute((ServerCommandSource)commandContext.getSource(), FunctionArgumentType.getFunctionOrTag((CommandContext<ServerCommandSource>)commandContext, "function"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time"), true))).then(CommandManager.literal("append").executes(commandContext -> ScheduleCommand.execute((ServerCommandSource)commandContext.getSource(), FunctionArgumentType.getFunctionOrTag((CommandContext<ServerCommandSource>)commandContext, "function"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time"), false)))).then(CommandManager.literal("replace").executes(commandContext -> ScheduleCommand.execute((ServerCommandSource)commandContext.getSource(), FunctionArgumentType.getFunctionOrTag((CommandContext<ServerCommandSource>)commandContext, "function"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time"), true))))))).then(CommandManager.literal("clear").then(CommandManager.argument("function", StringArgumentType.greedyString()).suggests(field_20854).executes(commandContext -> ScheduleCommand.method_22833((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"function"))))));
    }

    private static int execute(ServerCommandSource serverCommandSource, Either<CommandFunction, Tag<CommandFunction>> either, int time, boolean bl) throws CommandSyntaxException {
        if (time == 0) {
            throw SAME_TICK_EXCEPTION.create();
        }
        long l = serverCommandSource.getWorld().getTime() + (long)time;
        Timer<MinecraftServer> timer = serverCommandSource.getWorld().getLevelProperties().getScheduledEvents();
        either.ifLeft(commandFunction -> {
            Identifier identifier = commandFunction.getId();
            String string = identifier.toString();
            if (bl) {
                timer.method_22593(string);
            }
            timer.setEvent(string, l, new FunctionTimerCallback(identifier));
            serverCommandSource.sendFeedback(new TranslatableText("commands.schedule.created.function", identifier, time, l), true);
        }).ifRight(tag -> {
            Identifier identifier = tag.getId();
            String string = "#" + identifier.toString();
            if (bl) {
                timer.method_22593(string);
            }
            timer.setEvent(string, l, new FunctionTagTimerCallback(identifier));
            serverCommandSource.sendFeedback(new TranslatableText("commands.schedule.created.tag", identifier, time, l), true);
        });
        return (int)Math.floorMod(l, Integer.MAX_VALUE);
    }

    private static int method_22833(ServerCommandSource serverCommandSource, String string) throws CommandSyntaxException {
        int i = serverCommandSource.getWorld().getLevelProperties().getScheduledEvents().method_22593(string);
        if (i == 0) {
            throw field_20853.create((Object)string);
        }
        serverCommandSource.sendFeedback(new TranslatableText("commands.schedule.cleared.success", i, string), true);
        return i;
    }
}

