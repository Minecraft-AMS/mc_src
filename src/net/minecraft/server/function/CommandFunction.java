/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server.function;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class CommandFunction {
    private final Element[] elements;
    private final Identifier id;

    public CommandFunction(Identifier id, Element[] elements) {
        this.id = id;
        this.elements = elements;
    }

    public Identifier getId() {
        return this.id;
    }

    public Element[] getElements() {
        return this.elements;
    }

    public static CommandFunction create(Identifier id, CommandFunctionManager commandFunctionManager, List<String> fileLines) {
        ArrayList list = Lists.newArrayListWithCapacity((int)fileLines.size());
        for (int i = 0; i < fileLines.size(); ++i) {
            int j = i + 1;
            String string = fileLines.get(i).trim();
            StringReader stringReader = new StringReader(string);
            if (!stringReader.canRead() || stringReader.peek() == '#') continue;
            if (stringReader.peek() == '/') {
                stringReader.skip();
                if (stringReader.peek() == '/') {
                    throw new IllegalArgumentException("Unknown or invalid command '" + string + "' on line " + j + " (if you intended to make a comment, use '#' not '//')");
                }
                String string2 = stringReader.readUnquotedString();
                throw new IllegalArgumentException("Unknown or invalid command '" + string + "' on line " + j + " (did you mean '" + string2 + "'? Do not use a preceding forwards slash.)");
            }
            try {
                ParseResults parseResults = commandFunctionManager.getServer().getCommandManager().getDispatcher().parse(stringReader, (Object)commandFunctionManager.method_20796());
                if (parseResults.getReader().canRead()) {
                    if (parseResults.getExceptions().size() == 1) {
                        throw (CommandSyntaxException)((Object)parseResults.getExceptions().values().iterator().next());
                    }
                    if (parseResults.getContext().getRange().isEmpty()) {
                        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseResults.getReader());
                    }
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parseResults.getReader());
                }
                list.add(new CommandElement((ParseResults<ServerCommandSource>)parseResults));
                continue;
            }
            catch (CommandSyntaxException commandSyntaxException) {
                throw new IllegalArgumentException("Whilst parsing command on line " + j + ": " + commandSyntaxException.getMessage());
            }
        }
        return new CommandFunction(id, list.toArray(new Element[0]));
    }

    public static class LazyContainer {
        public static final LazyContainer EMPTY = new LazyContainer((Identifier)null);
        @Nullable
        private final Identifier id;
        private boolean initialized;
        private Optional<CommandFunction> function = Optional.empty();

        public LazyContainer(@Nullable Identifier id) {
            this.id = id;
        }

        public LazyContainer(CommandFunction commandFunction) {
            this.initialized = true;
            this.id = null;
            this.function = Optional.of(commandFunction);
        }

        public Optional<CommandFunction> get(CommandFunctionManager commandFunctionManager) {
            if (!this.initialized) {
                if (this.id != null) {
                    this.function = commandFunctionManager.getFunction(this.id);
                }
                this.initialized = true;
            }
            return this.function;
        }

        @Nullable
        public Identifier getId() {
            return this.function.map(commandFunction -> ((CommandFunction)commandFunction).id).orElse(this.id);
        }
    }

    public static class FunctionElement
    implements Element {
        private final LazyContainer function;

        public FunctionElement(CommandFunction commandFunction) {
            this.function = new LazyContainer(commandFunction);
        }

        @Override
        public void execute(CommandFunctionManager commandFunctionManager, ServerCommandSource serverCommandSource, ArrayDeque<CommandFunctionManager.Entry> arrayDeque, int i) {
            this.function.get(commandFunctionManager).ifPresent(commandFunction -> {
                Element[] elements = commandFunction.getElements();
                int j = i - arrayDeque.size();
                int k = Math.min(elements.length, j);
                for (int l = k - 1; l >= 0; --l) {
                    arrayDeque.addFirst(new CommandFunctionManager.Entry(commandFunctionManager, serverCommandSource, elements[l]));
                }
            });
        }

        public String toString() {
            return "function " + this.function.getId();
        }
    }

    public static class CommandElement
    implements Element {
        private final ParseResults<ServerCommandSource> parsed;

        public CommandElement(ParseResults<ServerCommandSource> parseResults) {
            this.parsed = parseResults;
        }

        @Override
        public void execute(CommandFunctionManager commandFunctionManager, ServerCommandSource serverCommandSource, ArrayDeque<CommandFunctionManager.Entry> arrayDeque, int i) throws CommandSyntaxException {
            commandFunctionManager.getDispatcher().execute(new ParseResults(this.parsed.getContext().withSource((Object)serverCommandSource), this.parsed.getReader(), this.parsed.getExceptions()));
        }

        public String toString() {
            return this.parsed.getReader().getString();
        }
    }

    public static interface Element {
        public void execute(CommandFunctionManager var1, ServerCommandSource var2, ArrayDeque<CommandFunctionManager.Entry> var3, int var4) throws CommandSyntaxException;
    }
}

