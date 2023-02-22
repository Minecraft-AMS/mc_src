/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.arguments.ObjectiveArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class TriggerCommand {
    private static final SimpleCommandExceptionType FAILED_UMPRIMED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.trigger.failed.unprimed", new Object[0]));
    private static final SimpleCommandExceptionType FAILED_INVALID_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.trigger.failed.invalid", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("trigger").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("objective", ObjectiveArgumentType.objective()).suggests((commandContext, suggestionsBuilder) -> TriggerCommand.suggestObjectives((ServerCommandSource)commandContext.getSource(), suggestionsBuilder)).executes(commandContext -> TriggerCommand.executeSimple((ServerCommandSource)commandContext.getSource(), TriggerCommand.getScore(((ServerCommandSource)commandContext.getSource()).getPlayer(), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective"))))).then(CommandManager.literal("add").then(CommandManager.argument("value", IntegerArgumentType.integer()).executes(commandContext -> TriggerCommand.executeAdd((ServerCommandSource)commandContext.getSource(), TriggerCommand.getScore(((ServerCommandSource)commandContext.getSource()).getPlayer(), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective")), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"value")))))).then(CommandManager.literal("set").then(CommandManager.argument("value", IntegerArgumentType.integer()).executes(commandContext -> TriggerCommand.executeSet((ServerCommandSource)commandContext.getSource(), TriggerCommand.getScore(((ServerCommandSource)commandContext.getSource()).getPlayer(), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective")), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"value")))))));
    }

    public static CompletableFuture<Suggestions> suggestObjectives(ServerCommandSource source, SuggestionsBuilder suggestionsBuilder) {
        Entity entity = source.getEntity();
        ArrayList list = Lists.newArrayList();
        if (entity != null) {
            ServerScoreboard scoreboard = source.getMinecraftServer().getScoreboard();
            String string = entity.getEntityName();
            for (ScoreboardObjective scoreboardObjective : scoreboard.getObjectives()) {
                ScoreboardPlayerScore scoreboardPlayerScore;
                if (scoreboardObjective.getCriterion() != ScoreboardCriterion.TRIGGER || !scoreboard.playerHasObjective(string, scoreboardObjective) || (scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective)).isLocked()) continue;
                list.add(scoreboardObjective.getName());
            }
        }
        return CommandSource.suggestMatching(list, suggestionsBuilder);
    }

    private static int executeAdd(ServerCommandSource source, ScoreboardPlayerScore score, int value) {
        score.incrementScore(value);
        source.sendFeedback(new TranslatableText("commands.trigger.add.success", score.getObjective().toHoverableText(), value), true);
        return score.getScore();
    }

    private static int executeSet(ServerCommandSource serverCommandSource, ScoreboardPlayerScore scoreboardPlayerScore, int value) {
        scoreboardPlayerScore.setScore(value);
        serverCommandSource.sendFeedback(new TranslatableText("commands.trigger.set.success", scoreboardPlayerScore.getObjective().toHoverableText(), value), true);
        return value;
    }

    private static int executeSimple(ServerCommandSource source, ScoreboardPlayerScore score) {
        score.incrementScore(1);
        source.sendFeedback(new TranslatableText("commands.trigger.simple.success", score.getObjective().toHoverableText()), true);
        return score.getScore();
    }

    private static ScoreboardPlayerScore getScore(ServerPlayerEntity serverPlayerEntity, ScoreboardObjective scoreboardObjective) throws CommandSyntaxException {
        String string;
        if (scoreboardObjective.getCriterion() != ScoreboardCriterion.TRIGGER) {
            throw FAILED_INVALID_EXCEPTION.create();
        }
        Scoreboard scoreboard = serverPlayerEntity.getScoreboard();
        if (!scoreboard.playerHasObjective(string = serverPlayerEntity.getEntityName(), scoreboardObjective)) {
            throw FAILED_UMPRIMED_EXCEPTION.create();
        }
        ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
        if (scoreboardPlayerScore.isLocked()) {
            throw FAILED_UMPRIMED_EXCEPTION.create();
        }
        scoreboardPlayerScore.setLocked(true);
        return scoreboardPlayerScore;
    }
}

