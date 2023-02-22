/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class CommandFunctionArgumentType
implements ArgumentType<FunctionArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
    private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_TAG_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.function.tag.unknown", object));
    private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.function.unknown", object));

    public static CommandFunctionArgumentType commandFunction() {
        return new CommandFunctionArgumentType();
    }

    public FunctionArgument parse(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '#') {
            stringReader.skip();
            final Identifier identifier = Identifier.fromCommandInput(stringReader);
            return new FunctionArgument(){

                @Override
                public Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
                    Tag tag = CommandFunctionArgumentType.getFunctionTag((CommandContext<ServerCommandSource>)commandContext, identifier);
                    return tag.values();
                }

                @Override
                public Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> getFunctionOrTag(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
                    return Pair.of((Object)identifier, (Object)Either.right((Object)CommandFunctionArgumentType.getFunctionTag((CommandContext<ServerCommandSource>)commandContext, identifier)));
                }
            };
        }
        final Identifier identifier = Identifier.fromCommandInput(stringReader);
        return new FunctionArgument(){

            @Override
            public Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
                return Collections.singleton(CommandFunctionArgumentType.getFunction((CommandContext<ServerCommandSource>)commandContext, identifier));
            }

            @Override
            public Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> getFunctionOrTag(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
                return Pair.of((Object)identifier, (Object)Either.left((Object)CommandFunctionArgumentType.getFunction((CommandContext<ServerCommandSource>)commandContext, identifier)));
            }
        };
    }

    private static CommandFunction getFunction(CommandContext<ServerCommandSource> context, Identifier id) throws CommandSyntaxException {
        return ((ServerCommandSource)context.getSource()).getMinecraftServer().getCommandFunctionManager().getFunction(id).orElseThrow(() -> UNKNOWN_FUNCTION_EXCEPTION.create((Object)id.toString()));
    }

    private static Tag<CommandFunction> getFunctionTag(CommandContext<ServerCommandSource> context, Identifier id) throws CommandSyntaxException {
        Tag<CommandFunction> tag = ((ServerCommandSource)context.getSource()).getMinecraftServer().getCommandFunctionManager().method_29462(id);
        if (tag == null) {
            throw UNKNOWN_FUNCTION_TAG_EXCEPTION.create((Object)id.toString());
        }
        return tag;
    }

    public static Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ((FunctionArgument)context.getArgument(name, FunctionArgument.class)).getFunctions(context);
    }

    public static Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> getFunctionOrTag(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return ((FunctionArgument)commandContext.getArgument(string, FunctionArgument.class)).getFunctionOrTag(commandContext);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    public static interface FunctionArgument {
        public Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;

        public Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> getFunctionOrTag(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;
    }
}

