/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.command.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class OperationArgumentType
implements ArgumentType<Operation> {
    private static final Collection<String> EXAMPLES = Arrays.asList("=", ">", "<");
    private static final SimpleCommandExceptionType INVALID_OPERATION = new SimpleCommandExceptionType((Message)new TranslatableText("arguments.operation.invalid", new Object[0]));
    private static final SimpleCommandExceptionType DIVISION_ZERO_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("arguments.operation.div0", new Object[0]));

    public static OperationArgumentType operation() {
        return new OperationArgumentType();
    }

    public static Operation getOperation(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return (Operation)commandContext.getArgument(string, Operation.class);
    }

    public Operation parse(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead()) {
            int i = stringReader.getCursor();
            while (stringReader.canRead() && stringReader.peek() != ' ') {
                stringReader.skip();
            }
            return OperationArgumentType.getOperator(stringReader.getString().substring(i, stringReader.getCursor()));
        }
        throw INVALID_OPERATION.create();
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, builder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static Operation getOperator(String string) throws CommandSyntaxException {
        if (string.equals("><")) {
            return (scoreboardPlayerScore, scoreboardPlayerScore2) -> {
                int i = scoreboardPlayerScore.getScore();
                scoreboardPlayerScore.setScore(scoreboardPlayerScore2.getScore());
                scoreboardPlayerScore2.setScore(i);
            };
        }
        return OperationArgumentType.getIntOperator(string);
    }

    private static IntOperator getIntOperator(String string) throws CommandSyntaxException {
        switch (string) {
            case "=": {
                return (i, j) -> j;
            }
            case "+=": {
                return (i, j) -> i + j;
            }
            case "-=": {
                return (i, j) -> i - j;
            }
            case "*=": {
                return (i, j) -> i * j;
            }
            case "/=": {
                return (i, j) -> {
                    if (j == 0) {
                        throw DIVISION_ZERO_EXCEPTION.create();
                    }
                    return MathHelper.floorDiv(i, j);
                };
            }
            case "%=": {
                return (i, j) -> {
                    if (j == 0) {
                        throw DIVISION_ZERO_EXCEPTION.create();
                    }
                    return MathHelper.floorMod(i, j);
                };
            }
            case "<": {
                return Math::min;
            }
            case ">": {
                return Math::max;
            }
        }
        throw INVALID_OPERATION.create();
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    @FunctionalInterface
    static interface IntOperator
    extends Operation {
        public int apply(int var1, int var2) throws CommandSyntaxException;

        @Override
        default public void apply(ScoreboardPlayerScore scoreboardPlayerScore, ScoreboardPlayerScore scoreboardPlayerScore2) throws CommandSyntaxException {
            scoreboardPlayerScore.setScore(this.apply(scoreboardPlayerScore.getScore(), scoreboardPlayerScore2.getScore()));
        }
    }

    @FunctionalInterface
    public static interface Operation {
        public void apply(ScoreboardPlayerScore var1, ScoreboardPlayerScore var2) throws CommandSyntaxException;
    }
}

