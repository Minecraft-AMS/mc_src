/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.datafixers.util.Either
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Either;
import net.minecraft.command.arguments.FunctionArgumentType;
import net.minecraft.command.arguments.TimeArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.timer.FunctionTagTimerCallback;
import net.minecraft.world.timer.FunctionTimerCallback;

public class ScheduleCommand {
    private static final SimpleCommandExceptionType SAME_TICK_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.schedule.same_tick", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("schedule").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("function").then(CommandManager.argument("function", FunctionArgumentType.function()).suggests(FunctionCommand.SUGGESTION_PROVIDER).then(CommandManager.argument("time", TimeArgumentType.time()).executes(commandContext -> ScheduleCommand.execute((ServerCommandSource)commandContext.getSource(), FunctionArgumentType.getFunctionOrTag((CommandContext<ServerCommandSource>)commandContext, "function"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time")))))));
    }

    private static int execute(ServerCommandSource source, Either<CommandFunction, Tag<CommandFunction>> function, int time) throws CommandSyntaxException {
        if (time == 0) {
            throw SAME_TICK_EXCEPTION.create();
        }
        long l = source.getWorld().getTime() + (long)time;
        function.ifLeft(commandFunction -> {
            Identifier identifier = commandFunction.getId();
            source.getWorld().getLevelProperties().getScheduledEvents().replaceEvent(identifier.toString(), l, new FunctionTimerCallback(identifier));
            source.sendFeedback(new TranslatableText("commands.schedule.created.function", identifier, time, l), true);
        }).ifRight(tag -> {
            Identifier identifier = tag.getId();
            source.getWorld().getLevelProperties().getScheduledEvents().replaceEvent("#" + identifier.toString(), l, new FunctionTagTimerCallback(identifier));
            source.sendFeedback(new TranslatableText("commands.schedule.created.tag", identifier, time, l), true);
        });
        return (int)Math.floorMod(l, Integer.MAX_VALUE);
    }
}

