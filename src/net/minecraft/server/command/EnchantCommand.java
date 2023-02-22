/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.argument.EnchantmentArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class EnchantCommand {
    private static final DynamicCommandExceptionType FAILED_ENTITY_EXCEPTION = new DynamicCommandExceptionType(entityName -> Text.translatable("commands.enchant.failed.entity", entityName));
    private static final DynamicCommandExceptionType FAILED_ITEMLESS_EXCEPTION = new DynamicCommandExceptionType(entityName -> Text.translatable("commands.enchant.failed.itemless", entityName));
    private static final DynamicCommandExceptionType FAILED_INCOMPATIBLE_EXCEPTION = new DynamicCommandExceptionType(itemName -> Text.translatable("commands.enchant.failed.incompatible", itemName));
    private static final Dynamic2CommandExceptionType FAILED_LEVEL_EXCEPTION = new Dynamic2CommandExceptionType((level, maxLevel) -> Text.translatable("commands.enchant.failed.level", level, maxLevel));
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("commands.enchant.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("enchant").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("targets", EntityArgumentType.entities()).then(((RequiredArgumentBuilder)CommandManager.argument("enchantment", EnchantmentArgumentType.enchantment()).executes(context -> EnchantCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)context, "targets"), EnchantmentArgumentType.getEnchantment((CommandContext<ServerCommandSource>)context, "enchantment"), 1))).then(CommandManager.argument("level", IntegerArgumentType.integer((int)0)).executes(context -> EnchantCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)context, "targets"), EnchantmentArgumentType.getEnchantment((CommandContext<ServerCommandSource>)context, "enchantment"), IntegerArgumentType.getInteger((CommandContext)context, (String)"level")))))));
    }

    private static int execute(ServerCommandSource source, Collection<? extends Entity> targets, Enchantment enchantment, int level) throws CommandSyntaxException {
        if (level > enchantment.getMaxLevel()) {
            throw FAILED_LEVEL_EXCEPTION.create((Object)level, (Object)enchantment.getMaxLevel());
        }
        int i = 0;
        for (Entity entity : targets) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                ItemStack itemStack = livingEntity.getMainHandStack();
                if (!itemStack.isEmpty()) {
                    if (enchantment.isAcceptableItem(itemStack) && EnchantmentHelper.isCompatible(EnchantmentHelper.get(itemStack).keySet(), enchantment)) {
                        itemStack.addEnchantment(enchantment, level);
                        ++i;
                        continue;
                    }
                    if (targets.size() != 1) continue;
                    throw FAILED_INCOMPATIBLE_EXCEPTION.create((Object)itemStack.getItem().getName(itemStack).getString());
                }
                if (targets.size() != 1) continue;
                throw FAILED_ITEMLESS_EXCEPTION.create((Object)livingEntity.getName().getString());
            }
            if (targets.size() != 1) continue;
            throw FAILED_ENTITY_EXCEPTION.create((Object)entity.getName().getString());
        }
        if (i == 0) {
            throw FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(Text.translatable("commands.enchant.success.single", enchantment.getName(level), targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(Text.translatable("commands.enchant.success.multiple", enchantment.getName(level), targets.size()), true);
        }
        return i;
    }
}

