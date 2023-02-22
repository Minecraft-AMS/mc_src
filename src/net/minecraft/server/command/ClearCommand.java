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
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ClearCommand {
    private static final DynamicCommandExceptionType FAILED_SINGLE_EXCEPTION = new DynamicCommandExceptionType(playerName -> Text.translatable("clear.failed.single", playerName));
    private static final DynamicCommandExceptionType FAILED_MULTIPLE_EXCEPTION = new DynamicCommandExceptionType(playerCount -> Text.translatable("clear.failed.multiple", playerCount));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("clear").requires(source -> source.hasPermissionLevel(2))).executes(context -> ClearCommand.execute((ServerCommandSource)context.getSource(), Collections.singleton(((ServerCommandSource)context.getSource()).getPlayerOrThrow()), stack -> true, -1))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).executes(context -> ClearCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), stack -> true, -1))).then(((RequiredArgumentBuilder)CommandManager.argument("item", ItemPredicateArgumentType.itemPredicate(commandRegistryAccess)).executes(context -> ClearCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), ItemPredicateArgumentType.getItemStackPredicate((CommandContext<ServerCommandSource>)context, "item"), -1))).then(CommandManager.argument("maxCount", IntegerArgumentType.integer((int)0)).executes(context -> ClearCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), ItemPredicateArgumentType.getItemStackPredicate((CommandContext<ServerCommandSource>)context, "item"), IntegerArgumentType.getInteger((CommandContext)context, (String)"maxCount")))))));
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Predicate<ItemStack> item, int maxCount) throws CommandSyntaxException {
        int i = 0;
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            i += serverPlayerEntity.getInventory().remove(item, maxCount, serverPlayerEntity.playerScreenHandler.getCraftingInput());
            serverPlayerEntity.currentScreenHandler.sendContentUpdates();
            serverPlayerEntity.playerScreenHandler.onContentChanged(serverPlayerEntity.getInventory());
        }
        if (i == 0) {
            if (targets.size() == 1) {
                throw FAILED_SINGLE_EXCEPTION.create((Object)targets.iterator().next().getName());
            }
            throw FAILED_MULTIPLE_EXCEPTION.create((Object)targets.size());
        }
        if (maxCount == 0) {
            if (targets.size() == 1) {
                source.sendFeedback(Text.translatable("commands.clear.test.single", i, targets.iterator().next().getDisplayName()), true);
            } else {
                source.sendFeedback(Text.translatable("commands.clear.test.multiple", i, targets.size()), true);
            }
        } else if (targets.size() == 1) {
            source.sendFeedback(Text.translatable("commands.clear.success.single", i, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(Text.translatable("commands.clear.success.multiple", i, targets.size()), true);
        }
        return i;
    }
}

