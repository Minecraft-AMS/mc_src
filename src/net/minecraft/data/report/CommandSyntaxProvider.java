/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.mojang.brigadier.CommandDispatcher
 */
package net.minecraft.data.report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class CommandSyntaxProvider
implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator generator;

    public CommandSyntaxProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void run(DataCache cache) throws IOException {
        Path path = this.generator.getOutput().resolve("reports/commands.json");
        CommandDispatcher<ServerCommandSource> commandDispatcher = new CommandManager(CommandManager.RegistrationEnvironment.ALL).getDispatcher();
        DataProvider.writeToPath(GSON, cache, (JsonElement)ArgumentTypes.toJson(commandDispatcher, commandDispatcher.getRoot()), path);
    }

    @Override
    public String getName() {
        return "Command Syntax";
    }
}

