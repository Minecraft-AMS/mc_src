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
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class Vec2ArgumentType
implements ArgumentType<PosArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "0.1 -0.5", "~1 ~-2");
    public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.pos2d.incomplete"));
    private final boolean centerIntegers;

    public Vec2ArgumentType(boolean centerIntegers) {
        this.centerIntegers = centerIntegers;
    }

    public static Vec2ArgumentType vec2() {
        return new Vec2ArgumentType(true);
    }

    public static Vec2ArgumentType vec2(boolean centerIntegers) {
        return new Vec2ArgumentType(centerIntegers);
    }

    public static Vec2f getVec2(CommandContext<ServerCommandSource> context, String name) {
        Vec3d vec3d = ((PosArgument)context.getArgument(name, PosArgument.class)).toAbsolutePos((ServerCommandSource)context.getSource());
        return new Vec2f((float)vec3d.x, (float)vec3d.z);
    }

    public PosArgument parse(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        if (!stringReader.canRead()) {
            throw INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        CoordinateArgument coordinateArgument = CoordinateArgument.parse(stringReader, this.centerIntegers);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(i);
            throw INCOMPLETE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(stringReader, this.centerIntegers);
        return new DefaultPosArgument(coordinateArgument, new CoordinateArgument(true, 0.0), coordinateArgument2);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSource) {
            String string = builder.getRemaining();
            Collection<CommandSource.RelativePosition> collection = !string.isEmpty() && string.charAt(0) == '^' ? Collections.singleton(CommandSource.RelativePosition.ZERO_LOCAL) : ((CommandSource)context.getSource()).getPositionSuggestions();
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

