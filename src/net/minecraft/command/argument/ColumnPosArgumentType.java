/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
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
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.command.argument.DefaultPosArgument;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColumnPos;

public class ColumnPosArgumentType
implements ArgumentType<PosArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0");
    public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.pos2d.incomplete"));

    public static ColumnPosArgumentType columnPos() {
        return new ColumnPosArgumentType();
    }

    public static ColumnPos getColumnPos(CommandContext<ServerCommandSource> context, String name) {
        BlockPos blockPos = ((PosArgument)context.getArgument(name, PosArgument.class)).toAbsoluteBlockPos((ServerCommandSource)context.getSource());
        return new ColumnPos(blockPos.getX(), blockPos.getZ());
    }

    public PosArgument parse(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        if (!stringReader.canRead()) {
            throw INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        CoordinateArgument coordinateArgument = CoordinateArgument.parse(stringReader);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(i);
            throw INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(stringReader);
        return new DefaultPosArgument(coordinateArgument, new CoordinateArgument(true, 0.0), coordinateArgument2);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSource) {
            String string = builder.getRemaining();
            Collection<CommandSource.RelativePosition> collection = !string.isEmpty() && string.charAt(0) == '^' ? Collections.singleton(CommandSource.RelativePosition.ZERO_LOCAL) : ((CommandSource)context.getSource()).getBlockPositionSuggestions();
            return CommandSource.suggestColumnPositions(string, collection, builder, CommandManager.getCommandValidator(this::parse));
        }
        return Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }
}

