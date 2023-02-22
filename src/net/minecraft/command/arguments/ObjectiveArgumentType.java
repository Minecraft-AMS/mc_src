/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class ObjectiveArgumentType
implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "*", "012");
    private static final DynamicCommandExceptionType UNKNOWN_OBJECTIVE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.objective.notFound", object));
    private static final DynamicCommandExceptionType READONLY_OBJECTIVE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.objective.readonly", object));
    public static final DynamicCommandExceptionType LONG_NAME_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.scoreboard.objectives.add.longName", object));

    public static ObjectiveArgumentType objective() {
        return new ObjectiveArgumentType();
    }

    public static ScoreboardObjective getObjective(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        String string = (String)context.getArgument(name, String.class);
        ServerScoreboard scoreboard = ((ServerCommandSource)context.getSource()).getMinecraftServer().getScoreboard();
        ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string);
        if (scoreboardObjective == null) {
            throw UNKNOWN_OBJECTIVE_EXCEPTION.create((Object)string);
        }
        return scoreboardObjective;
    }

    public static ScoreboardObjective getWritableObjective(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        ScoreboardObjective scoreboardObjective = ObjectiveArgumentType.getObjective(context, name);
        if (scoreboardObjective.getCriterion().isReadOnly()) {
            throw READONLY_OBJECTIVE_EXCEPTION.create((Object)scoreboardObjective.getName());
        }
        return scoreboardObjective;
    }

    public String parse(StringReader stringReader) throws CommandSyntaxException {
        String string = stringReader.readUnquotedString();
        if (string.length() > 16) {
            throw LONG_NAME_EXCEPTION.create((Object)16);
        }
        return string;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof ServerCommandSource) {
            return CommandSource.suggestMatching(((ServerCommandSource)context.getSource()).getMinecraftServer().getScoreboard().getObjectiveNames(), builder);
        }
        if (context.getSource() instanceof CommandSource) {
            CommandSource commandSource = (CommandSource)context.getSource();
            return commandSource.getCompletions(context, builder);
        }
        return Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}
