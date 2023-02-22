/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.command.CommandException;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class AdvancementCommand {
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
        Collection<Advancement> collection = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getAdvancementManager().getAdvancements();
        return CommandSource.suggestIdentifiers(collection.stream().map(Advancement::getId), suggestionsBuilder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("advancement").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("grant").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.literal("only").then(((RequiredArgumentBuilder)CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.ONLY)))).then(CommandManager.argument("criterion", StringArgumentType.greedyString()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder)).executes(commandContext -> AdvancementCommand.executeCriterion((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), StringArgumentType.getString((CommandContext)commandContext, (String)"criterion"))))))).then(CommandManager.literal("from").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.FROM)))))).then(CommandManager.literal("until").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.UNTIL)))))).then(CommandManager.literal("through").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.THROUGH)))))).then(CommandManager.literal("everything").executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getAdvancementManager().getAdvancements())))))).then(CommandManager.literal("revoke").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.literal("only").then(((RequiredArgumentBuilder)CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.ONLY)))).then(CommandManager.argument("criterion", StringArgumentType.greedyString()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder)).executes(commandContext -> AdvancementCommand.executeCriterion((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), StringArgumentType.getString((CommandContext)commandContext, (String)"criterion"))))))).then(CommandManager.literal("from").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.FROM)))))).then(CommandManager.literal("until").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.UNTIL)))))).then(CommandManager.literal("through").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.THROUGH)))))).then(CommandManager.literal("everything").executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getAdvancementManager().getAdvancements()))))));
    }

    private static int executeAdvancement(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Operation operation, Collection<Advancement> selection) {
        int i = 0;
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            i += operation.processAll(serverPlayerEntity, selection);
        }
        if (i == 0) {
            if (selection.size() == 1) {
                if (targets.size() == 1) {
                    throw new CommandException(new TranslatableText(operation.getCommandPrefix() + ".one.to.one.failure", selection.iterator().next().toHoverableText(), targets.iterator().next().getDisplayName()));
                }
                throw new CommandException(new TranslatableText(operation.getCommandPrefix() + ".one.to.many.failure", selection.iterator().next().toHoverableText(), targets.size()));
            }
            if (targets.size() == 1) {
                throw new CommandException(new TranslatableText(operation.getCommandPrefix() + ".many.to.one.failure", selection.size(), targets.iterator().next().getDisplayName()));
            }
            throw new CommandException(new TranslatableText(operation.getCommandPrefix() + ".many.to.many.failure", selection.size(), targets.size()));
        }
        if (selection.size() == 1) {
            if (targets.size() == 1) {
                source.sendFeedback(new TranslatableText(operation.getCommandPrefix() + ".one.to.one.success", selection.iterator().next().toHoverableText(), targets.iterator().next().getDisplayName()), true);
            } else {
                source.sendFeedback(new TranslatableText(operation.getCommandPrefix() + ".one.to.many.success", selection.iterator().next().toHoverableText(), targets.size()), true);
            }
        } else if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText(operation.getCommandPrefix() + ".many.to.one.success", selection.size(), targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText(operation.getCommandPrefix() + ".many.to.many.success", selection.size(), targets.size()), true);
        }
        return i;
    }

    private static int executeCriterion(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Operation operation, Advancement advancement, String criterion) {
        int i = 0;
        if (!advancement.getCriteria().containsKey(criterion)) {
            throw new CommandException(new TranslatableText("commands.advancement.criterionNotFound", advancement.toHoverableText(), criterion));
        }
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            if (!operation.processEachCriterion(serverPlayerEntity, advancement, criterion)) continue;
            ++i;
        }
        if (i == 0) {
            if (targets.size() == 1) {
                throw new CommandException(new TranslatableText(operation.getCommandPrefix() + ".criterion.to.one.failure", criterion, advancement.toHoverableText(), targets.iterator().next().getDisplayName()));
            }
            throw new CommandException(new TranslatableText(operation.getCommandPrefix() + ".criterion.to.many.failure", criterion, advancement.toHoverableText(), targets.size()));
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText(operation.getCommandPrefix() + ".criterion.to.one.success", criterion, advancement.toHoverableText(), targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText(operation.getCommandPrefix() + ".criterion.to.many.success", criterion, advancement.toHoverableText(), targets.size()), true);
        }
        return i;
    }

    private static List<Advancement> select(Advancement advancement, Selection selection) {
        ArrayList list = Lists.newArrayList();
        if (selection.before) {
            for (Advancement advancement2 = advancement.getParent(); advancement2 != null; advancement2 = advancement2.getParent()) {
                list.add(advancement2);
            }
        }
        list.add(advancement);
        if (selection.after) {
            AdvancementCommand.addChildrenRecursivelyToList(advancement, list);
        }
        return list;
    }

    private static void addChildrenRecursivelyToList(Advancement parent, List<Advancement> childList) {
        for (Advancement advancement : parent.getChildren()) {
            childList.add(advancement);
            AdvancementCommand.addChildrenRecursivelyToList(advancement, childList);
        }
    }

    static enum Selection {
        ONLY(false, false),
        THROUGH(true, true),
        FROM(false, true),
        UNTIL(true, false),
        EVERYTHING(true, true);

        private final boolean before;
        private final boolean after;

        private Selection(boolean bl, boolean bl2) {
            this.before = bl;
            this.after = bl2;
        }
    }

    static enum Operation {
        GRANT("grant"){

            @Override
            protected boolean processEach(ServerPlayerEntity serverPlayerEntity, Advancement advancement) {
                AdvancementProgress advancementProgress = serverPlayerEntity.getAdvancementTracker().getProgress(advancement);
                if (advancementProgress.isDone()) {
                    return false;
                }
                for (String string : advancementProgress.getUnobtainedCriteria()) {
                    serverPlayerEntity.getAdvancementTracker().grantCriterion(advancement, string);
                }
                return true;
            }

            @Override
            protected boolean processEachCriterion(ServerPlayerEntity serverPlayerEntity, Advancement advancement, String criterion) {
                return serverPlayerEntity.getAdvancementTracker().grantCriterion(advancement, criterion);
            }
        }
        ,
        REVOKE("revoke"){

            @Override
            protected boolean processEach(ServerPlayerEntity serverPlayerEntity, Advancement advancement) {
                AdvancementProgress advancementProgress = serverPlayerEntity.getAdvancementTracker().getProgress(advancement);
                if (!advancementProgress.isAnyObtained()) {
                    return false;
                }
                for (String string : advancementProgress.getObtainedCriteria()) {
                    serverPlayerEntity.getAdvancementTracker().revokeCriterion(advancement, string);
                }
                return true;
            }

            @Override
            protected boolean processEachCriterion(ServerPlayerEntity serverPlayerEntity, Advancement advancement, String criterion) {
                return serverPlayerEntity.getAdvancementTracker().revokeCriterion(advancement, criterion);
            }
        };

        private final String commandPrefix;

        private Operation(String string2) {
            this.commandPrefix = "commands.advancement." + string2;
        }

        public int processAll(ServerPlayerEntity serverPlayerEntity, Iterable<Advancement> iterable) {
            int i = 0;
            for (Advancement advancement : iterable) {
                if (!this.processEach(serverPlayerEntity, advancement)) continue;
                ++i;
            }
            return i;
        }

        protected abstract boolean processEach(ServerPlayerEntity var1, Advancement var2);

        protected abstract boolean processEachCriterion(ServerPlayerEntity var1, Advancement var2, String var3);

        protected String getCommandPrefix() {
            return this.commandPrefix;
        }
    }
}

