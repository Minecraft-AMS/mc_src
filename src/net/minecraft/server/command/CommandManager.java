/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  com.mojang.logging.LogUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.server.command.AdvancementCommand;
import net.minecraft.server.command.AttributeCommand;
import net.minecraft.server.command.BossBarCommand;
import net.minecraft.server.command.ClearCommand;
import net.minecraft.server.command.CloneCommand;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.DatapackCommand;
import net.minecraft.server.command.DebugCommand;
import net.minecraft.server.command.DefaultGameModeCommand;
import net.minecraft.server.command.DifficultyCommand;
import net.minecraft.server.command.EffectCommand;
import net.minecraft.server.command.EnchantCommand;
import net.minecraft.server.command.ExecuteCommand;
import net.minecraft.server.command.ExperienceCommand;
import net.minecraft.server.command.FillCommand;
import net.minecraft.server.command.ForceLoadCommand;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.GameModeCommand;
import net.minecraft.server.command.GameRuleCommand;
import net.minecraft.server.command.GiveCommand;
import net.minecraft.server.command.HelpCommand;
import net.minecraft.server.command.ItemCommand;
import net.minecraft.server.command.JfrCommand;
import net.minecraft.server.command.KickCommand;
import net.minecraft.server.command.KillCommand;
import net.minecraft.server.command.ListCommand;
import net.minecraft.server.command.LocateBiomeCommand;
import net.minecraft.server.command.LocateCommand;
import net.minecraft.server.command.LootCommand;
import net.minecraft.server.command.MeCommand;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ParticleCommand;
import net.minecraft.server.command.PlaceFeatureCommand;
import net.minecraft.server.command.PlaySoundCommand;
import net.minecraft.server.command.PublishCommand;
import net.minecraft.server.command.RecipeCommand;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.SayCommand;
import net.minecraft.server.command.ScheduleCommand;
import net.minecraft.server.command.ScoreboardCommand;
import net.minecraft.server.command.SeedCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.server.command.SetWorldSpawnCommand;
import net.minecraft.server.command.SpawnPointCommand;
import net.minecraft.server.command.SpectateCommand;
import net.minecraft.server.command.SpreadPlayersCommand;
import net.minecraft.server.command.StopSoundCommand;
import net.minecraft.server.command.SummonCommand;
import net.minecraft.server.command.TagCommand;
import net.minecraft.server.command.TeamCommand;
import net.minecraft.server.command.TeamMsgCommand;
import net.minecraft.server.command.TeleportCommand;
import net.minecraft.server.command.TellRawCommand;
import net.minecraft.server.command.TestCommand;
import net.minecraft.server.command.TimeCommand;
import net.minecraft.server.command.TitleCommand;
import net.minecraft.server.command.TriggerCommand;
import net.minecraft.server.command.WeatherCommand;
import net.minecraft.server.command.WorldBorderCommand;
import net.minecraft.server.dedicated.command.BanCommand;
import net.minecraft.server.dedicated.command.BanIpCommand;
import net.minecraft.server.dedicated.command.BanListCommand;
import net.minecraft.server.dedicated.command.DeOpCommand;
import net.minecraft.server.dedicated.command.OpCommand;
import net.minecraft.server.dedicated.command.PardonCommand;
import net.minecraft.server.dedicated.command.PardonIpCommand;
import net.minecraft.server.dedicated.command.PerfCommand;
import net.minecraft.server.dedicated.command.SaveAllCommand;
import net.minecraft.server.dedicated.command.SaveOffCommand;
import net.minecraft.server.dedicated.command.SaveOnCommand;
import net.minecraft.server.dedicated.command.SetIdleTimeoutCommand;
import net.minecraft.server.dedicated.command.StopCommand;
import net.minecraft.server.dedicated.command.WhitelistCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class CommandManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int field_31837 = 0;
    public static final int field_31838 = 1;
    public static final int field_31839 = 2;
    public static final int field_31840 = 3;
    public static final int field_31841 = 4;
    private final CommandDispatcher<ServerCommandSource> dispatcher = new CommandDispatcher();

    public CommandManager(RegistrationEnvironment environment) {
        AdvancementCommand.register(this.dispatcher);
        AttributeCommand.register(this.dispatcher);
        ExecuteCommand.register(this.dispatcher);
        BossBarCommand.register(this.dispatcher);
        ClearCommand.register(this.dispatcher);
        CloneCommand.register(this.dispatcher);
        DataCommand.register(this.dispatcher);
        DatapackCommand.register(this.dispatcher);
        DebugCommand.register(this.dispatcher);
        DefaultGameModeCommand.register(this.dispatcher);
        DifficultyCommand.register(this.dispatcher);
        EffectCommand.register(this.dispatcher);
        MeCommand.register(this.dispatcher);
        EnchantCommand.register(this.dispatcher);
        ExperienceCommand.register(this.dispatcher);
        FillCommand.register(this.dispatcher);
        ForceLoadCommand.register(this.dispatcher);
        FunctionCommand.register(this.dispatcher);
        GameModeCommand.register(this.dispatcher);
        GameRuleCommand.register(this.dispatcher);
        GiveCommand.register(this.dispatcher);
        HelpCommand.register(this.dispatcher);
        ItemCommand.register(this.dispatcher);
        KickCommand.register(this.dispatcher);
        KillCommand.register(this.dispatcher);
        ListCommand.register(this.dispatcher);
        LocateCommand.register(this.dispatcher);
        LocateBiomeCommand.register(this.dispatcher);
        LootCommand.register(this.dispatcher);
        MessageCommand.register(this.dispatcher);
        ParticleCommand.register(this.dispatcher);
        PlaceFeatureCommand.register(this.dispatcher);
        PlaySoundCommand.register(this.dispatcher);
        ReloadCommand.register(this.dispatcher);
        RecipeCommand.register(this.dispatcher);
        SayCommand.register(this.dispatcher);
        ScheduleCommand.register(this.dispatcher);
        ScoreboardCommand.register(this.dispatcher);
        SeedCommand.register(this.dispatcher, environment != RegistrationEnvironment.INTEGRATED);
        SetBlockCommand.register(this.dispatcher);
        SpawnPointCommand.register(this.dispatcher);
        SetWorldSpawnCommand.register(this.dispatcher);
        SpectateCommand.register(this.dispatcher);
        SpreadPlayersCommand.register(this.dispatcher);
        StopSoundCommand.register(this.dispatcher);
        SummonCommand.register(this.dispatcher);
        TagCommand.register(this.dispatcher);
        TeamCommand.register(this.dispatcher);
        TeamMsgCommand.register(this.dispatcher);
        TeleportCommand.register(this.dispatcher);
        TellRawCommand.register(this.dispatcher);
        TimeCommand.register(this.dispatcher);
        TitleCommand.register(this.dispatcher);
        TriggerCommand.register(this.dispatcher);
        WeatherCommand.register(this.dispatcher);
        WorldBorderCommand.register(this.dispatcher);
        if (FlightProfiler.INSTANCE.isAvailable()) {
            JfrCommand.register(this.dispatcher);
        }
        if (SharedConstants.isDevelopment) {
            TestCommand.register(this.dispatcher);
        }
        if (environment.dedicated) {
            BanIpCommand.register(this.dispatcher);
            BanListCommand.register(this.dispatcher);
            BanCommand.register(this.dispatcher);
            DeOpCommand.register(this.dispatcher);
            OpCommand.register(this.dispatcher);
            PardonCommand.register(this.dispatcher);
            PardonIpCommand.register(this.dispatcher);
            PerfCommand.register(this.dispatcher);
            SaveAllCommand.register(this.dispatcher);
            SaveOffCommand.register(this.dispatcher);
            SaveOnCommand.register(this.dispatcher);
            SetIdleTimeoutCommand.register(this.dispatcher);
            StopCommand.register(this.dispatcher);
            WhitelistCommand.register(this.dispatcher);
        }
        if (environment.integrated) {
            PublishCommand.register(this.dispatcher);
        }
        this.dispatcher.findAmbiguities((parent, child, sibling, inputs) -> LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", new Object[]{this.dispatcher.getPath(child), this.dispatcher.getPath(sibling), inputs}));
        this.dispatcher.setConsumer((context, success, result) -> ((ServerCommandSource)context.getSource()).onCommandComplete((CommandContext<ServerCommandSource>)context, success, result));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int execute(ServerCommandSource commandSource, String command) {
        StringReader stringReader = new StringReader(command);
        if (stringReader.canRead() && stringReader.peek() == '/') {
            stringReader.skip();
        }
        commandSource.getServer().getProfiler().push(command);
        try {
            int n = this.dispatcher.execute(stringReader, (Object)commandSource);
            return n;
        }
        catch (CommandException commandException) {
            commandSource.sendError(commandException.getTextMessage());
            int n = 0;
            return n;
        }
        catch (CommandSyntaxException commandSyntaxException) {
            int i;
            commandSource.sendError(Texts.toText(commandSyntaxException.getRawMessage()));
            if (commandSyntaxException.getInput() != null && commandSyntaxException.getCursor() >= 0) {
                i = Math.min(commandSyntaxException.getInput().length(), commandSyntaxException.getCursor());
                MutableText mutableText = new LiteralText("").formatted(Formatting.GRAY).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
                if (i > 10) {
                    mutableText.append("...");
                }
                mutableText.append(commandSyntaxException.getInput().substring(Math.max(0, i - 10), i));
                if (i < commandSyntaxException.getInput().length()) {
                    MutableText text = new LiteralText(commandSyntaxException.getInput().substring(i)).formatted(Formatting.RED, Formatting.UNDERLINE);
                    mutableText.append(text);
                }
                mutableText.append(new TranslatableText("command.context.here").formatted(Formatting.RED, Formatting.ITALIC));
                commandSource.sendError(mutableText);
            }
            i = 0;
            return i;
        }
        catch (Exception exception) {
            LiteralText mutableText2 = new LiteralText(exception.getMessage() == null ? exception.getClass().getName() : exception.getMessage());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error("Command exception: {}", (Object)command, (Object)exception);
                StackTraceElement[] stackTraceElements = exception.getStackTrace();
                for (int j = 0; j < Math.min(stackTraceElements.length, 3); ++j) {
                    mutableText2.append("\n\n").append(stackTraceElements[j].getMethodName()).append("\n ").append(stackTraceElements[j].getFileName()).append(":").append(String.valueOf(stackTraceElements[j].getLineNumber()));
                }
            }
            commandSource.sendError(new TranslatableText("command.failed").styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, mutableText2))));
            if (SharedConstants.isDevelopment) {
                commandSource.sendError(new LiteralText(Util.getInnermostMessage(exception)));
                LOGGER.error("'{}' threw an exception", (Object)command, (Object)exception);
            }
            int n = 0;
            return n;
        }
        finally {
            commandSource.getServer().getProfiler().pop();
        }
    }

    public void sendCommandTree(ServerPlayerEntity player) {
        HashMap map = Maps.newHashMap();
        RootCommandNode rootCommandNode = new RootCommandNode();
        map.put(this.dispatcher.getRoot(), rootCommandNode);
        this.makeTreeForSource((CommandNode<ServerCommandSource>)this.dispatcher.getRoot(), (CommandNode<CommandSource>)rootCommandNode, player.getCommandSource(), map);
        player.networkHandler.sendPacket(new CommandTreeS2CPacket((RootCommandNode<CommandSource>)rootCommandNode));
    }

    private void makeTreeForSource(CommandNode<ServerCommandSource> tree, CommandNode<CommandSource> result, ServerCommandSource source2, Map<CommandNode<ServerCommandSource>, CommandNode<CommandSource>> resultNodes) {
        for (CommandNode commandNode : tree.getChildren()) {
            RequiredArgumentBuilder requiredArgumentBuilder;
            if (!commandNode.canUse((Object)source2)) continue;
            ArgumentBuilder argumentBuilder = commandNode.createBuilder();
            argumentBuilder.requires(source -> true);
            if (argumentBuilder.getCommand() != null) {
                argumentBuilder.executes(context -> 0);
            }
            if (argumentBuilder instanceof RequiredArgumentBuilder && (requiredArgumentBuilder = (RequiredArgumentBuilder)argumentBuilder).getSuggestionsProvider() != null) {
                requiredArgumentBuilder.suggests(SuggestionProviders.getLocalProvider((SuggestionProvider<CommandSource>)requiredArgumentBuilder.getSuggestionsProvider()));
            }
            if (argumentBuilder.getRedirect() != null) {
                argumentBuilder.redirect(resultNodes.get(argumentBuilder.getRedirect()));
            }
            CommandNode commandNode2 = argumentBuilder.build();
            resultNodes.put((CommandNode<ServerCommandSource>)commandNode, (CommandNode<CommandSource>)commandNode2);
            result.addChild(commandNode2);
            if (commandNode.getChildren().isEmpty()) continue;
            this.makeTreeForSource((CommandNode<ServerCommandSource>)commandNode, (CommandNode<CommandSource>)commandNode2, source2, resultNodes);
        }
    }

    public static LiteralArgumentBuilder<ServerCommandSource> literal(String literal) {
        return LiteralArgumentBuilder.literal((String)literal);
    }

    public static <T> RequiredArgumentBuilder<ServerCommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument((String)name, type);
    }

    public static Predicate<String> getCommandValidator(CommandParser parser) {
        return string -> {
            try {
                parser.parse(new StringReader(string));
                return true;
            }
            catch (CommandSyntaxException commandSyntaxException) {
                return false;
            }
        };
    }

    public CommandDispatcher<ServerCommandSource> getDispatcher() {
        return this.dispatcher;
    }

    @Nullable
    public static <S> CommandSyntaxException getException(ParseResults<S> parse) {
        if (!parse.getReader().canRead()) {
            return null;
        }
        if (parse.getExceptions().size() == 1) {
            return (CommandSyntaxException)((Object)parse.getExceptions().values().iterator().next());
        }
        if (parse.getContext().getRange().isEmpty()) {
            return CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parse.getReader());
        }
        return CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parse.getReader());
    }

    public static void checkMissing() {
        RootCommandNode rootCommandNode = new CommandManager(RegistrationEnvironment.ALL).getDispatcher().getRoot();
        Set<ArgumentType<?>> set = ArgumentTypes.getAllArgumentTypes(rootCommandNode);
        Set set2 = set.stream().filter(type -> !ArgumentTypes.hasClass(type)).collect(Collectors.toSet());
        if (!set2.isEmpty()) {
            LOGGER.warn("Missing type registration for following arguments:\n {}", (Object)set2.stream().map(type -> "\t" + type).collect(Collectors.joining(",\n")));
            throw new IllegalStateException("Unregistered argument types");
        }
    }

    public static final class RegistrationEnvironment
    extends Enum<RegistrationEnvironment> {
        public static final /* enum */ RegistrationEnvironment ALL = new RegistrationEnvironment(true, true);
        public static final /* enum */ RegistrationEnvironment DEDICATED = new RegistrationEnvironment(false, true);
        public static final /* enum */ RegistrationEnvironment INTEGRATED = new RegistrationEnvironment(true, false);
        final boolean integrated;
        final boolean dedicated;
        private static final /* synthetic */ RegistrationEnvironment[] field_25424;

        public static RegistrationEnvironment[] values() {
            return (RegistrationEnvironment[])field_25424.clone();
        }

        public static RegistrationEnvironment valueOf(String string) {
            return Enum.valueOf(RegistrationEnvironment.class, string);
        }

        private RegistrationEnvironment(boolean integrated, boolean dedicated) {
            this.integrated = integrated;
            this.dedicated = dedicated;
        }

        private static /* synthetic */ RegistrationEnvironment[] method_36791() {
            return new RegistrationEnvironment[]{ALL, DEDICATED, INTEGRATED};
        }

        static {
            field_25424 = RegistrationEnvironment.method_36791();
        }
    }

    @FunctionalInterface
    public static interface CommandParser {
        public void parse(StringReader var1) throws CommandSyntaxException;
    }
}

