/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class StopSoundCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        RequiredArgumentBuilder requiredArgumentBuilder = (RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).executes(context -> StopSoundCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), null, null))).then(CommandManager.literal("*").then(CommandManager.argument("sound", IdentifierArgumentType.identifier()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes(context -> StopSoundCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), null, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)context, "sound")))));
        for (SoundCategory soundCategory : SoundCategory.values()) {
            requiredArgumentBuilder.then(((LiteralArgumentBuilder)CommandManager.literal(soundCategory.getName()).executes(context -> StopSoundCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), soundCategory, null))).then(CommandManager.argument("sound", IdentifierArgumentType.identifier()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes(context -> StopSoundCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)context, "targets"), soundCategory, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)context, "sound")))));
        }
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("stopsound").requires(source -> source.hasPermissionLevel(2))).then((ArgumentBuilder)requiredArgumentBuilder));
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, @Nullable SoundCategory category, @Nullable Identifier sound) {
        StopSoundS2CPacket stopSoundS2CPacket = new StopSoundS2CPacket(sound, category);
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            serverPlayerEntity.networkHandler.sendPacket(stopSoundS2CPacket);
        }
        if (category != null) {
            if (sound != null) {
                source.sendFeedback(Text.translatable("commands.stopsound.success.source.sound", sound, category.getName()), true);
            } else {
                source.sendFeedback(Text.translatable("commands.stopsound.success.source.any", category.getName()), true);
            }
        } else if (sound != null) {
            source.sendFeedback(Text.translatable("commands.stopsound.success.sourceless.sound", sound), true);
        } else {
            source.sendFeedback(Text.translatable("commands.stopsound.success.sourceless.any"), true);
        }
        return targets.size();
    }
}

