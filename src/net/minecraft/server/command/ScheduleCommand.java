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
 *  com.mojang.datafixers.util.Pair
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
import com.mojang.datafixers.util.Pair;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
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
    private static final SimpleCommandExceptionType SAME_TICK_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.schedule.same_tick"));
    private static final DynamicCommandExceptionType CLEARED_FAILURE_EXCEPTION = new DynamicCommandExceptionType(eventName -> new TranslatableText("commands.schedule.cleared.failure", eventName));
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestMatching(((ServerCommandSource)context.getSource()).getServer().getSaveProperties().getMainWorldProperties().getScheduledEvents().method_22592(), builder);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("schedule").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.literal("function").then(CommandManager.argument("function", CommandFunctionArgumentType.commandFunction()).suggests(FunctionCommand.SUGGESTION_PROVIDER).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("time", TimeArgumentType.time()).executes(context -> ScheduleCommand.execute((ServerCommandSource)context.getSource(), CommandFunctionArgumentType.getFunctionOrTag((CommandContext<ServerCommandSource>)context, "function"), IntegerArgumentType.getInteger((CommandContext)context, (String)"time"), true))).then(CommandManager.literal("append").executes(context -> ScheduleCommand.execute((ServerCommandSource)context.getSource(), CommandFunctionArgumentType.getFunctionOrTag((CommandContext<ServerCommandSource>)context, "function"), IntegerArgumentType.getInteger((CommandContext)context, (String)"time"), false)))).then(CommandManager.literal("replace").executes(context -> ScheduleCommand.execute((ServerCommandSource)context.getSource(), CommandFunctionArgumentType.getFunctionOrTag((CommandContext<ServerCommandSource>)context, "function"), IntegerArgumentType.getInteger((CommandContext)context, (String)"time"), true))))))).then(CommandManager.literal("clear").then(CommandManager.argument("function", StringArgumentType.greedyString()).suggests(SUGGESTION_PROVIDER).executes(context -> ScheduleCommand.clearEvent((ServerCommandSource)context.getSource(), StringArgumentType.getString((CommandContext)context, (String)"function"))))));
    }

    private static int execute(ServerCommandSource source, Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> function2, int time, boolean replace) throws CommandSyntaxException {
        if (time == 0) {
            throw SAME_TICK_EXCEPTION.create();
        }
        long l = source.getWorld().getTime() + (long)time;
        Identifier identifier = (Identifier)function2.getFirst();
        Timer<MinecraftServer> timer = source.getServer().getSaveProperties().getMainWorldProperties().getScheduledEvents();
        ((Either)function2.getSecond()).ifLeft(function -> {
            String string = identifier.toString();
            if (replace) {
                timer.method_22593(string);
            }
            timer.setEvent(string, l, new FunctionTimerCallback(identifier));
            source.sendFeedback(new TranslatableText("commands.schedule.created.function", identifier, time, l), true);
        }).ifRight(tag -> {
            String string = "#" + identifier;
            if (replace) {
                timer.method_22593(string);
            }
            timer.setEvent(string, l, new FunctionTagTimerCallback(identifier));
            source.sendFeedback(new TranslatableText("commands.schedule.created.tag", identifier, time, l), true);
        });
        return Math.floorMod(l, Integer.MAX_VALUE);
    }

    private static int clearEvent(ServerCommandSource source, String eventName) throws CommandSyntaxException {
        int i = source.getServer().getSaveProperties().getMainWorldProperties().getScheduledEvents().method_22593(eventName);
        if (i == 0) {
            throw CLEARED_FAILURE_EXCEPTION.create((Object)eventName);
        }
        source.sendFeedback(new TranslatableText("commands.schedule.cleared.success", i, eventName), true);
        return i;
    }
}

