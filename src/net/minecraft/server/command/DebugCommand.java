/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.ProfileResult;
import org.slf4j.Logger;

public class DebugCommand {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final SimpleCommandExceptionType NOT_RUNNING_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.debug.notRunning"));
    private static final SimpleCommandExceptionType ALREADY_RUNNING_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.debug.alreadyRunning"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("debug").requires(source -> source.hasPermissionLevel(3))).then(CommandManager.literal("start").executes(context -> DebugCommand.executeStart((ServerCommandSource)context.getSource())))).then(CommandManager.literal("stop").executes(context -> DebugCommand.executeStop((ServerCommandSource)context.getSource())))).then(((LiteralArgumentBuilder)CommandManager.literal("function").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))).then(CommandManager.argument("name", CommandFunctionArgumentType.commandFunction()).suggests(FunctionCommand.SUGGESTION_PROVIDER).executes(context -> DebugCommand.executeFunction((ServerCommandSource)context.getSource(), CommandFunctionArgumentType.getFunctions((CommandContext<ServerCommandSource>)context, "name"))))));
    }

    private static int executeStart(ServerCommandSource source) throws CommandSyntaxException {
        MinecraftServer minecraftServer = source.getServer();
        if (minecraftServer.isDebugRunning()) {
            throw ALREADY_RUNNING_EXCEPTION.create();
        }
        minecraftServer.startDebug();
        source.sendFeedback(new TranslatableText("commands.debug.started"), true);
        return 0;
    }

    private static int executeStop(ServerCommandSource source) throws CommandSyntaxException {
        MinecraftServer minecraftServer = source.getServer();
        if (!minecraftServer.isDebugRunning()) {
            throw NOT_RUNNING_EXCEPTION.create();
        }
        ProfileResult profileResult = minecraftServer.stopDebug();
        double d = (double)profileResult.getTimeSpan() / (double)TimeHelper.SECOND_IN_NANOS;
        double e = (double)profileResult.getTickSpan() / d;
        source.sendFeedback(new TranslatableText("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", d), profileResult.getTickSpan(), String.format("%.2f", e)), true);
        return (int)e;
    }

    private static int executeFunction(ServerCommandSource source, Collection<CommandFunction> functions) {
        int i = 0;
        MinecraftServer minecraftServer = source.getServer();
        String string = "debug-trace-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt";
        try {
            Path path = minecraftServer.getFile("debug").toPath();
            Files.createDirectories(path, new FileAttribute[0]);
            try (BufferedWriter writer = Files.newBufferedWriter(path.resolve(string), StandardCharsets.UTF_8, new OpenOption[0]);){
                PrintWriter printWriter = new PrintWriter(writer);
                for (CommandFunction commandFunction : functions) {
                    printWriter.println(commandFunction.getId());
                    Tracer tracer = new Tracer(printWriter);
                    i += source.getServer().getCommandFunctionManager().execute(commandFunction, source.withOutput(tracer).withMaxLevel(2), tracer);
                }
            }
        }
        catch (IOException | UncheckedIOException exception) {
            LOGGER.warn("Tracing failed", (Throwable)exception);
            source.sendError(new TranslatableText("commands.debug.function.traceFailed"));
        }
        if (functions.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.debug.function.success.single", i, functions.iterator().next().getId(), string), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.debug.function.success.multiple", i, functions.size(), string), true);
        }
        return i;
    }

    static class Tracer
    implements CommandOutput,
    CommandFunctionManager.Tracer {
        public static final int MARGIN = 1;
        private final PrintWriter writer;
        private int lastIndentWidth;
        private boolean expectsCommandResult;

        Tracer(PrintWriter writer) {
            this.writer = writer;
        }

        private void writeIndent(int width) {
            this.writeIndentWithoutRememberingWidth(width);
            this.lastIndentWidth = width;
        }

        private void writeIndentWithoutRememberingWidth(int width) {
            for (int i = 0; i < width + 1; ++i) {
                this.writer.write("    ");
            }
        }

        private void writeNewLine() {
            if (this.expectsCommandResult) {
                this.writer.println();
                this.expectsCommandResult = false;
            }
        }

        @Override
        public void traceCommandStart(int depth, String command) {
            this.writeNewLine();
            this.writeIndent(depth);
            this.writer.print("[C] ");
            this.writer.print(command);
            this.expectsCommandResult = true;
        }

        @Override
        public void traceCommandEnd(int depth, String command, int result) {
            if (this.expectsCommandResult) {
                this.writer.print(" -> ");
                this.writer.println(result);
                this.expectsCommandResult = false;
            } else {
                this.writeIndent(depth);
                this.writer.print("[R = ");
                this.writer.print(result);
                this.writer.print("] ");
                this.writer.println(command);
            }
        }

        @Override
        public void traceFunctionCall(int depth, Identifier function, int size) {
            this.writeNewLine();
            this.writeIndent(depth);
            this.writer.print("[F] ");
            this.writer.print(function);
            this.writer.print(" size=");
            this.writer.println(size);
        }

        @Override
        public void traceError(int depth, String message) {
            this.writeNewLine();
            this.writeIndent(depth + 1);
            this.writer.print("[E] ");
            this.writer.print(message);
        }

        @Override
        public void sendSystemMessage(Text message, UUID sender) {
            this.writeNewLine();
            this.writeIndentWithoutRememberingWidth(this.lastIndentWidth + 1);
            this.writer.print("[M] ");
            if (sender != Util.NIL_UUID) {
                this.writer.print(sender);
                this.writer.print(": ");
            }
            this.writer.println(message.getString());
        }

        @Override
        public boolean shouldReceiveFeedback() {
            return true;
        }

        @Override
        public boolean shouldTrackOutput() {
            return true;
        }

        @Override
        public boolean shouldBroadcastConsoleToOps() {
            return false;
        }

        @Override
        public boolean cannotBeSilenced() {
            return true;
        }
    }
}

