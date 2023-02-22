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
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ItemStackArgument;
import net.minecraft.command.arguments.ItemStackArgumentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;

public class GiveCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("give").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("targets", EntityArgumentType.players()).then(((RequiredArgumentBuilder)CommandManager.argument("item", ItemStackArgumentType.itemStack()).executes(commandContext -> GiveCommand.execute((ServerCommandSource)commandContext.getSource(), ItemStackArgumentType.getItemStackArgument(commandContext, "item"), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), 1))).then(CommandManager.argument("count", IntegerArgumentType.integer((int)1)).executes(commandContext -> GiveCommand.execute((ServerCommandSource)commandContext.getSource(), ItemStackArgumentType.getItemStackArgument(commandContext, "item"), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count")))))));
    }

    private static int execute(ServerCommandSource source, ItemStackArgument item, Collection<ServerPlayerEntity> targets, int count) throws CommandSyntaxException {
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            int i = count;
            while (i > 0) {
                ItemEntity itemEntity;
                int j = Math.min(item.getItem().getMaxCount(), i);
                i -= j;
                ItemStack itemStack = item.createStack(j, false);
                boolean bl = serverPlayerEntity.inventory.insertStack(itemStack);
                if (!bl || !itemStack.isEmpty()) {
                    itemEntity = serverPlayerEntity.dropItem(itemStack, false);
                    if (itemEntity == null) continue;
                    itemEntity.resetPickupDelay();
                    itemEntity.setOwner(serverPlayerEntity.getUuid());
                    continue;
                }
                itemStack.setCount(1);
                itemEntity = serverPlayerEntity.dropItem(itemStack, false);
                if (itemEntity != null) {
                    itemEntity.setDespawnImmediately();
                }
                serverPlayerEntity.world.playSound(null, serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((serverPlayerEntity.getRandom().nextFloat() - serverPlayerEntity.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                serverPlayerEntity.playerContainer.sendContentUpdates();
            }
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.give.success.single", count, item.createStack(count, false).toHoverableText(), targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.give.success.single", count, item.createStack(count, false).toHoverableText(), targets.size()), true);
        }
        return targets.size();
    }
}

