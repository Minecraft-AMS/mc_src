/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ItemPredicateArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class ClearCommand {
    private static final DynamicCommandExceptionType FAILED_SINGLE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("clear.failed.single", object));
    private static final DynamicCommandExceptionType FAILED_MULTIPLE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("clear.failed.multiple", object));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("clear").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).executes(commandContext -> ClearCommand.execute((ServerCommandSource)commandContext.getSource(), Collections.singleton(((ServerCommandSource)commandContext.getSource()).getPlayer()), itemStack -> true, -1))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).executes(commandContext -> ClearCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), itemStack -> true, -1))).then(((RequiredArgumentBuilder)CommandManager.argument("item", ItemPredicateArgumentType.itemPredicate()).executes(commandContext -> ClearCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), ItemPredicateArgumentType.getItemPredicate((CommandContext<ServerCommandSource>)commandContext, "item"), -1))).then(CommandManager.argument("maxCount", IntegerArgumentType.integer((int)0)).executes(commandContext -> ClearCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), ItemPredicateArgumentType.getItemPredicate((CommandContext<ServerCommandSource>)commandContext, "item"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"maxCount")))))));
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Predicate<ItemStack> item, int maxCount) throws CommandSyntaxException {
        int i = 0;
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            i += serverPlayerEntity.inventory.method_7369(item, maxCount);
            serverPlayerEntity.container.sendContentUpdates();
            serverPlayerEntity.method_14241();
        }
        if (i == 0) {
            if (targets.size() == 1) {
                throw FAILED_SINGLE_EXCEPTION.create((Object)targets.iterator().next().getName().asFormattedString());
            }
            throw FAILED_MULTIPLE_EXCEPTION.create((Object)targets.size());
        }
        if (maxCount == 0) {
            if (targets.size() == 1) {
                source.sendFeedback(new TranslatableText("commands.clear.test.single", i, targets.iterator().next().getDisplayName()), true);
            } else {
                source.sendFeedback(new TranslatableText("commands.clear.test.multiple", i, targets.size()), true);
            }
        } else if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.clear.success.single", i, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.clear.success.multiple", i, targets.size()), true);
        }
        return i;
    }
}
