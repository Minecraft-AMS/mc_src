/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server.command;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.ProfileResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class DebugCommand {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final SimpleCommandExceptionType NOT_RUNNING_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.debug.notRunning"));
    private static final SimpleCommandExceptionType ALREADY_RUNNING_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.debug.alreadyRunning"));
    @Nullable
    private static final FileSystemProvider FILE_SYSTEM_PROVIDER = FileSystemProvider.installedProviders().stream().filter(fileSystemProvider -> fileSystemProvider.getScheme().equalsIgnoreCase("jar")).findFirst().orElse(null);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("debug").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))).then(CommandManager.literal("start").executes(commandContext -> DebugCommand.executeStart((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("stop").executes(commandContext -> DebugCommand.executeStop((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("report").executes(commandContext -> DebugCommand.createDebugReport((ServerCommandSource)commandContext.getSource()))));
    }

    private static int executeStart(ServerCommandSource source) throws CommandSyntaxException {
        MinecraftServer minecraftServer = source.getMinecraftServer();
        if (minecraftServer.isDebugRunning()) {
            throw ALREADY_RUNNING_EXCEPTION.create();
        }
        minecraftServer.enableProfiler();
        source.sendFeedback(new TranslatableText("commands.debug.started", "Started the debug profiler. Type '/debug stop' to stop it."), true);
        return 0;
    }

    private static int executeStop(ServerCommandSource source) throws CommandSyntaxException {
        MinecraftServer minecraftServer = source.getMinecraftServer();
        if (!minecraftServer.isDebugRunning()) {
            throw NOT_RUNNING_EXCEPTION.create();
        }
        ProfileResult profileResult = minecraftServer.stopDebug();
        File file = new File(minecraftServer.getFile("debug"), "profile-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
        profileResult.save(file);
        float f = (float)profileResult.getTimeSpan() / 1.0E9f;
        float g = (float)profileResult.getTickSpan() / f;
        source.sendFeedback(new TranslatableText("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", Float.valueOf(f)), profileResult.getTickSpan(), String.format("%.2f", Float.valueOf(g))), true);
        return MathHelper.floor(g);
    }

    private static int createDebugReport(ServerCommandSource source) {
        MinecraftServer minecraftServer = source.getMinecraftServer();
        String string = "debug-report-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        try {
            Path path = minecraftServer.getFile("debug").toPath();
            Files.createDirectories(path, new FileAttribute[0]);
            if (SharedConstants.isDevelopment || FILE_SYSTEM_PROVIDER == null) {
                Path path2 = path.resolve(string);
                minecraftServer.dump(path2);
            } else {
                Path path2 = path.resolve(string + ".zip");
                try (FileSystem fileSystem = FILE_SYSTEM_PROVIDER.newFileSystem(path2, (Map<String, ?>)ImmutableMap.of((Object)"create", (Object)"true"));){
                    minecraftServer.dump(fileSystem.getPath("/", new String[0]));
                }
            }
            source.sendFeedback(new TranslatableText("commands.debug.reportSaved", string), false);
            return 1;
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to save debug dump", (Throwable)iOException);
            source.sendError(new TranslatableText("commands.debug.reportFailed"));
            return 0;
        }
    }
}

