/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.OptionalInt;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableObject;

public class FunctionCommand {
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
        CommandFunctionManager commandFunctionManager = ((ServerCommandSource)context.getSource()).getServer().getCommandFunctionManager();
        CommandSource.suggestIdentifiers(commandFunctionManager.getFunctionTags(), builder, "#");
        return CommandSource.suggestIdentifiers(commandFunctionManager.getAllFunctions(), builder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("function").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("name", CommandFunctionArgumentType.commandFunction()).suggests(SUGGESTION_PROVIDER).executes(context -> FunctionCommand.execute((ServerCommandSource)context.getSource(), CommandFunctionArgumentType.getFunctions((CommandContext<ServerCommandSource>)context, "name")))));
    }

    private static int execute(ServerCommandSource source, Collection<CommandFunction> functions) {
        int i = 0;
        boolean bl = false;
        for (CommandFunction commandFunction : functions) {
            MutableObject mutableObject = new MutableObject((Object)OptionalInt.empty());
            int j = source.getServer().getCommandFunctionManager().execute(commandFunction, source.withSilent().withMaxLevel(2).withReturnValueConsumer(value -> mutableObject.setValue((Object)OptionalInt.of(value))));
            OptionalInt optionalInt = (OptionalInt)mutableObject.getValue();
            i += optionalInt.orElse(j);
            bl |= optionalInt.isPresent();
        }
        if (functions.size() == 1) {
            if (bl) {
                source.sendFeedback(Text.translatable("commands.function.success.single.result", i, functions.iterator().next().getId()), true);
            } else {
                source.sendFeedback(Text.translatable("commands.function.success.single", i, functions.iterator().next().getId()), true);
            }
        } else if (bl) {
            source.sendFeedback(Text.translatable("commands.function.success.multiple.result", functions.size()), true);
        } else {
            source.sendFeedback(Text.translatable("commands.function.success.multiple", i, functions.size()), true);
        }
        return i;
    }
}

