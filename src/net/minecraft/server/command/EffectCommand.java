/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.StatusEffectArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

public class EffectCommand {
    private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.effect.give.failed"));
    private static final SimpleCommandExceptionType CLEAR_EVERYTHING_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.effect.clear.everything.failed"));
    private static final SimpleCommandExceptionType CLEAR_SPECIFIC_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.effect.clear.specific.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("effect").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)CommandManager.literal("clear").executes(commandContext -> EffectCommand.executeClear((ServerCommandSource)commandContext.getSource(), (Collection<? extends Entity>)ImmutableList.of((Object)((ServerCommandSource)commandContext.getSource()).getEntityOrThrow())))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.entities()).executes(commandContext -> EffectCommand.executeClear((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets")))).then(CommandManager.argument("effect", StatusEffectArgumentType.statusEffect()).executes(commandContext -> EffectCommand.executeClear((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), StatusEffectArgumentType.getStatusEffect((CommandContext<ServerCommandSource>)commandContext, "effect"))))))).then(CommandManager.literal("give").then(CommandManager.argument("targets", EntityArgumentType.entities()).then(((RequiredArgumentBuilder)CommandManager.argument("effect", StatusEffectArgumentType.statusEffect()).executes(commandContext -> EffectCommand.executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), StatusEffectArgumentType.getStatusEffect((CommandContext<ServerCommandSource>)commandContext, "effect"), null, 0, true))).then(((RequiredArgumentBuilder)CommandManager.argument("seconds", IntegerArgumentType.integer((int)1, (int)1000000)).executes(commandContext -> EffectCommand.executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), StatusEffectArgumentType.getStatusEffect((CommandContext<ServerCommandSource>)commandContext, "effect"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"seconds"), 0, true))).then(((RequiredArgumentBuilder)CommandManager.argument("amplifier", IntegerArgumentType.integer((int)0, (int)255)).executes(commandContext -> EffectCommand.executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), StatusEffectArgumentType.getStatusEffect((CommandContext<ServerCommandSource>)commandContext, "effect"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"seconds"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amplifier"), true))).then(CommandManager.argument("hideParticles", BoolArgumentType.bool()).executes(commandContext -> EffectCommand.executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), StatusEffectArgumentType.getStatusEffect((CommandContext<ServerCommandSource>)commandContext, "effect"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"seconds"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amplifier"), !BoolArgumentType.getBool((CommandContext)commandContext, (String)"hideParticles"))))))))));
    }

    private static int executeGive(ServerCommandSource source, Collection<? extends Entity> targets, StatusEffect effect, @Nullable Integer seconds, int amplifier, boolean showParticles) throws CommandSyntaxException {
        int i = 0;
        int j = seconds != null ? (effect.isInstant() ? seconds : seconds * 20) : (effect.isInstant() ? 1 : 600);
        for (Entity entity : targets) {
            StatusEffectInstance statusEffectInstance;
            if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).addStatusEffect(statusEffectInstance = new StatusEffectInstance(effect, j, amplifier, false, showParticles))) continue;
            ++i;
        }
        if (i == 0) {
            throw GIVE_FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.effect.give.success.single", effect.getName(), targets.iterator().next().getDisplayName(), j / 20), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.effect.give.success.multiple", effect.getName(), targets.size(), j / 20), true);
        }
        return i;
    }

    private static int executeClear(ServerCommandSource source, Collection<? extends Entity> targets) throws CommandSyntaxException {
        int i = 0;
        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).clearStatusEffects()) continue;
            ++i;
        }
        if (i == 0) {
            throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.effect.clear.everything.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.effect.clear.everything.success.multiple", targets.size()), true);
        }
        return i;
    }

    private static int executeClear(ServerCommandSource source, Collection<? extends Entity> targets, StatusEffect effect) throws CommandSyntaxException {
        int i = 0;
        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).removeStatusEffect(effect)) continue;
            ++i;
        }
        if (i == 0) {
            throw CLEAR_SPECIFIC_FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.effect.clear.specific.success.single", effect.getName(), targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.effect.clear.specific.success.multiple", effect.getName(), targets.size()), true);
        }
        return i;
    }
}

