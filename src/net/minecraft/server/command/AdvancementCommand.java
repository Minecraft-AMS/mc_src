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
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class AdvancementCommand {
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
        Collection<Advancement> collection = ((ServerCommandSource)context.getSource()).getServer().getAdvancementLoader().getAdvancements();
        return CommandSource.suggestIdentifiers(collection.stream().map(Advancement::getId), builder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("advancement").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.literal("grant").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.literal("only").then(((RequiredArgumentBuilder)CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(context -> AdvancementCommand.executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), Operation.GRANT, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)context, "advancement"), Selection.ONLY)))).then(CommandManager.argument("criterion", StringArgumentType.greedyString()).suggests((context, builder) -> CommandSource.suggestMatching(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)context, "advancement").getCriteria().keySet(), builder)).executes(context -> AdvancementCommand.executeCriterion((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), Operation.GRANT, IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)context, "advancement"), StringArgumentType.getString((CommandContext)context, (String)"criterion"))))))).then(CommandManager.literal("from").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(context -> AdvancementCommand.executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), Operation.GRANT, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)context, "advancement"), Selection.FROM)))))).then(CommandManager.literal("until").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(context -> AdvancementCommand.executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), Operation.GRANT, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)context, "advancement"), Selection.UNTIL)))))).then(CommandManager.literal("through").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(context -> AdvancementCommand.executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), Operation.GRANT, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)context, "advancement"), Selection.THROUGH)))))).then(CommandManager.literal("everything").executes(context -> AdvancementCommand.executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), Operation.GRANT, ((ServerCommandSource)context.getSource()).getServer().getAdvancementLoader().getAdvancements())))))).then(CommandManager.literal("revoke").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.literal("only").then(((RequiredArgumentBuilder)CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(context -> AdvancementCommand.executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), Operation.REVOKE, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)context, "advancement"), Selection.ONLY)))).then(CommandManager.argument("criterion", StringArgumentType.greedyString()).suggests((context, builder) -> CommandSource.suggestMatching(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)context, "advancement").getCriteria().keySet(), builder)).executes(context -> AdvancementCommand.executeCriterion((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), Operation.REVOKE, IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)context, "advancement"), StringArgumentType.getString((CommandContext)context, (String)"criterion"))))))).then(CommandManager.literal("from").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(context -> AdvancementCommand.executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), Operation.REVOKE, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)context, "advancement"), Selection.FROM)))))).then(CommandManager.literal("until").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(context -> AdvancementCommand.executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), Operation.REVOKE, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)context, "advancement"), Selection.UNTIL)))))).then(CommandManager.literal("through").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(context -> AdvancementCommand.executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), Operation.REVOKE, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)context, "advancement"), Selection.THROUGH)))))).then(CommandManager.literal("everything").executes(context -> AdvancementCommand.executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), Operation.REVOKE, ((ServerCommandSource)context.getSource()).getServer().getAdvancementLoader().getAdvancements()))))));
    }

    private static int executeAdvancement(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Operation operation, Collection<Advancement> selection) {
        int i = 0;
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            i += operation.processAll(serverPlayerEntity, selection);
        }
        if (i == 0) {
            if (selection.size() == 1) {
                if (targets.size() == 1) {
                    throw new CommandException(Text.translatable(operation.getCommandPrefix() + ".one.to.one.failure", selection.iterator().next().toHoverableText(), targets.iterator().next().getDisplayName()));
                }
                throw new CommandException(Text.translatable(operation.getCommandPrefix() + ".one.to.many.failure", selection.iterator().next().toHoverableText(), targets.size()));
            }
            if (targets.size() == 1) {
                throw new CommandException(Text.translatable(operation.getCommandPrefix() + ".many.to.one.failure", selection.size(), targets.iterator().next().getDisplayName()));
            }
            throw new CommandException(Text.translatable(operation.getCommandPrefix() + ".many.to.many.failure", selection.size(), targets.size()));
        }
        if (selection.size() == 1) {
            if (targets.size() == 1) {
                source.sendFeedback(Text.translatable(operation.getCommandPrefix() + ".one.to.one.success", selection.iterator().next().toHoverableText(), targets.iterator().next().getDisplayName()), true);
            } else {
                source.sendFeedback(Text.translatable(operation.getCommandPrefix() + ".one.to.many.success", selection.iterator().next().toHoverableText(), targets.size()), true);
            }
        } else if (targets.size() == 1) {
            source.sendFeedback(Text.translatable(operation.getCommandPrefix() + ".many.to.one.success", selection.size(), targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(Text.translatable(operation.getCommandPrefix() + ".many.to.many.success", selection.size(), targets.size()), true);
        }
        return i;
    }

    private static int executeCriterion(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Operation operation, Advancement advancement, String criterion) {
        int i = 0;
        if (!advancement.getCriteria().containsKey(criterion)) {
            throw new CommandException(Text.translatable("commands.advancement.criterionNotFound", advancement.toHoverableText(), criterion));
        }
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            if (!operation.processEachCriterion(serverPlayerEntity, advancement, criterion)) continue;
            ++i;
        }
        if (i == 0) {
            if (targets.size() == 1) {
                throw new CommandException(Text.translatable(operation.getCommandPrefix() + ".criterion.to.one.failure", criterion, advancement.toHoverableText(), targets.iterator().next().getDisplayName()));
            }
            throw new CommandException(Text.translatable(operation.getCommandPrefix() + ".criterion.to.many.failure", criterion, advancement.toHoverableText(), targets.size()));
        }
        if (targets.size() == 1) {
            source.sendFeedback(Text.translatable(operation.getCommandPrefix() + ".criterion.to.one.success", criterion, advancement.toHoverableText(), targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(Text.translatable(operation.getCommandPrefix() + ".criterion.to.many.success", criterion, advancement.toHoverableText(), targets.size()), true);
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

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    static abstract class Operation
    extends Enum<Operation> {
        public static final /* enum */ Operation GRANT = new Operation("grant"){

            @Override
            protected boolean processEach(ServerPlayerEntity player, Advancement advancement) {
                AdvancementProgress advancementProgress = player.getAdvancementTracker().getProgress(advancement);
                if (advancementProgress.isDone()) {
                    return false;
                }
                for (String string : advancementProgress.getUnobtainedCriteria()) {
                    player.getAdvancementTracker().grantCriterion(advancement, string);
                }
                return true;
            }

            @Override
            protected boolean processEachCriterion(ServerPlayerEntity player, Advancement advancement, String criterion) {
                return player.getAdvancementTracker().grantCriterion(advancement, criterion);
            }
        };
        public static final /* enum */ Operation REVOKE = new Operation("revoke"){

            @Override
            protected boolean processEach(ServerPlayerEntity player, Advancement advancement) {
                AdvancementProgress advancementProgress = player.getAdvancementTracker().getProgress(advancement);
                if (!advancementProgress.isAnyObtained()) {
                    return false;
                }
                for (String string : advancementProgress.getObtainedCriteria()) {
                    player.getAdvancementTracker().revokeCriterion(advancement, string);
                }
                return true;
            }

            @Override
            protected boolean processEachCriterion(ServerPlayerEntity player, Advancement advancement, String criterion) {
                return player.getAdvancementTracker().revokeCriterion(advancement, criterion);
            }
        };
        private final String commandPrefix;
        private static final /* synthetic */ Operation[] field_13455;

        public static Operation[] values() {
            return (Operation[])field_13455.clone();
        }

        public static Operation valueOf(String string) {
            return Enum.valueOf(Operation.class, string);
        }

        Operation(String name) {
            this.commandPrefix = "commands.advancement." + name;
        }

        public int processAll(ServerPlayerEntity player, Iterable<Advancement> advancements) {
            int i = 0;
            for (Advancement advancement : advancements) {
                if (!this.processEach(player, advancement)) continue;
                ++i;
            }
            return i;
        }

        protected abstract boolean processEach(ServerPlayerEntity var1, Advancement var2);

        protected abstract boolean processEachCriterion(ServerPlayerEntity var1, Advancement var2, String var3);

        protected String getCommandPrefix() {
            return this.commandPrefix;
        }

        private static /* synthetic */ Operation[] method_36964() {
            return new Operation[]{GRANT, REVOKE};
        }

        static {
            field_13455 = Operation.method_36964();
        }
    }

    static final class Selection
    extends Enum<Selection> {
        public static final /* enum */ Selection ONLY = new Selection(false, false);
        public static final /* enum */ Selection THROUGH = new Selection(true, true);
        public static final /* enum */ Selection FROM = new Selection(false, true);
        public static final /* enum */ Selection UNTIL = new Selection(true, false);
        public static final /* enum */ Selection EVERYTHING = new Selection(true, true);
        final boolean before;
        final boolean after;
        private static final /* synthetic */ Selection[] field_13463;

        public static Selection[] values() {
            return (Selection[])field_13463.clone();
        }

        public static Selection valueOf(String string) {
            return Enum.valueOf(Selection.class, string);
        }

        private Selection(boolean before, boolean after) {
            this.before = before;
            this.after = after;
        }

        private static /* synthetic */ Selection[] method_36965() {
            return new Selection[]{ONLY, THROUGH, FROM, UNTIL, EVERYTHING};
        }

        static {
            field_13463 = Selection.method_36965();
        }
    }
}

