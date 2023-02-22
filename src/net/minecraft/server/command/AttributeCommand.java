/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.UUID;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

public class AttributeCommand {
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestIdentifiers(Registry.ATTRIBUTE.getIds(), builder);
    private static final DynamicCommandExceptionType ENTITY_FAILED_EXCEPTION = new DynamicCommandExceptionType(name -> new TranslatableText("commands.attribute.failed.entity", name));
    private static final Dynamic2CommandExceptionType NO_ATTRIBUTE_EXCEPTION = new Dynamic2CommandExceptionType((entityName, attributeName) -> new TranslatableText("commands.attribute.failed.no_attribute", entityName, attributeName));
    private static final Dynamic3CommandExceptionType NO_MODIFIER_EXCEPTION = new Dynamic3CommandExceptionType((entityName, attributeName, uuid) -> new TranslatableText("commands.attribute.failed.no_modifier", attributeName, entityName, uuid));
    private static final Dynamic3CommandExceptionType MODIFIER_ALREADY_PRESENT_EXCEPTION = new Dynamic3CommandExceptionType((entityName, attributeName, uuid) -> new TranslatableText("commands.attribute.failed.modifier_already_present", uuid, attributeName, entityName));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("attribute").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("target", EntityArgumentType.entity()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("attribute", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).then(((LiteralArgumentBuilder)CommandManager.literal("get").executes(context -> AttributeCommand.executeValueGet((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)context, "target"), IdentifierArgumentType.getAttributeArgument((CommandContext<ServerCommandSource>)context, "attribute"), 1.0))).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(context -> AttributeCommand.executeValueGet((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)context, "target"), IdentifierArgumentType.getAttributeArgument((CommandContext<ServerCommandSource>)context, "attribute"), DoubleArgumentType.getDouble((CommandContext)context, (String)"scale")))))).then(((LiteralArgumentBuilder)CommandManager.literal("base").then(CommandManager.literal("set").then(CommandManager.argument("value", DoubleArgumentType.doubleArg()).executes(context -> AttributeCommand.executeBaseValueSet((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)context, "target"), IdentifierArgumentType.getAttributeArgument((CommandContext<ServerCommandSource>)context, "attribute"), DoubleArgumentType.getDouble((CommandContext)context, (String)"value")))))).then(((LiteralArgumentBuilder)CommandManager.literal("get").executes(context -> AttributeCommand.executeBaseValueGet((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)context, "target"), IdentifierArgumentType.getAttributeArgument((CommandContext<ServerCommandSource>)context, "attribute"), 1.0))).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(context -> AttributeCommand.executeBaseValueGet((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)context, "target"), IdentifierArgumentType.getAttributeArgument((CommandContext<ServerCommandSource>)context, "attribute"), DoubleArgumentType.getDouble((CommandContext)context, (String)"scale"))))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("modifier").then(CommandManager.literal("add").then(CommandManager.argument("uuid", UuidArgumentType.uuid()).then(CommandManager.argument("name", StringArgumentType.string()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("value", DoubleArgumentType.doubleArg()).then(CommandManager.literal("add").executes(context -> AttributeCommand.executeModifierAdd((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)context, "target"), IdentifierArgumentType.getAttributeArgument((CommandContext<ServerCommandSource>)context, "attribute"), UuidArgumentType.getUuid((CommandContext<ServerCommandSource>)context, "uuid"), StringArgumentType.getString((CommandContext)context, (String)"name"), DoubleArgumentType.getDouble((CommandContext)context, (String)"value"), EntityAttributeModifier.Operation.ADDITION)))).then(CommandManager.literal("multiply").executes(context -> AttributeCommand.executeModifierAdd((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)context, "target"), IdentifierArgumentType.getAttributeArgument((CommandContext<ServerCommandSource>)context, "attribute"), UuidArgumentType.getUuid((CommandContext<ServerCommandSource>)context, "uuid"), StringArgumentType.getString((CommandContext)context, (String)"name"), DoubleArgumentType.getDouble((CommandContext)context, (String)"value"), EntityAttributeModifier.Operation.MULTIPLY_TOTAL)))).then(CommandManager.literal("multiply_base").executes(context -> AttributeCommand.executeModifierAdd((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)context, "target"), IdentifierArgumentType.getAttributeArgument((CommandContext<ServerCommandSource>)context, "attribute"), UuidArgumentType.getUuid((CommandContext<ServerCommandSource>)context, "uuid"), StringArgumentType.getString((CommandContext)context, (String)"name"), DoubleArgumentType.getDouble((CommandContext)context, (String)"value"), EntityAttributeModifier.Operation.MULTIPLY_BASE)))))))).then(CommandManager.literal("remove").then(CommandManager.argument("uuid", UuidArgumentType.uuid()).executes(context -> AttributeCommand.executeModifierRemove((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)context, "target"), IdentifierArgumentType.getAttributeArgument((CommandContext<ServerCommandSource>)context, "attribute"), UuidArgumentType.getUuid((CommandContext<ServerCommandSource>)context, "uuid")))))).then(CommandManager.literal("value").then(CommandManager.literal("get").then(((RequiredArgumentBuilder)CommandManager.argument("uuid", UuidArgumentType.uuid()).executes(context -> AttributeCommand.executeModifierValueGet((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)context, "target"), IdentifierArgumentType.getAttributeArgument((CommandContext<ServerCommandSource>)context, "attribute"), UuidArgumentType.getUuid((CommandContext<ServerCommandSource>)context, "uuid"), 1.0))).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(context -> AttributeCommand.executeModifierValueGet((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)context, "target"), IdentifierArgumentType.getAttributeArgument((CommandContext<ServerCommandSource>)context, "attribute"), UuidArgumentType.getUuid((CommandContext<ServerCommandSource>)context, "uuid"), DoubleArgumentType.getDouble((CommandContext)context, (String)"scale")))))))))));
    }

    private static EntityAttributeInstance getAttributeInstance(Entity entity, EntityAttribute attribute) throws CommandSyntaxException {
        EntityAttributeInstance entityAttributeInstance = AttributeCommand.getLivingEntity(entity).getAttributes().getCustomInstance(attribute);
        if (entityAttributeInstance == null) {
            throw NO_ATTRIBUTE_EXCEPTION.create((Object)entity.getName(), (Object)new TranslatableText(attribute.getTranslationKey()));
        }
        return entityAttributeInstance;
    }

    private static LivingEntity getLivingEntity(Entity entity) throws CommandSyntaxException {
        if (!(entity instanceof LivingEntity)) {
            throw ENTITY_FAILED_EXCEPTION.create((Object)entity.getName());
        }
        return (LivingEntity)entity;
    }

    private static LivingEntity getLivingEntityWithAttribute(Entity entity, EntityAttribute attribute) throws CommandSyntaxException {
        LivingEntity livingEntity = AttributeCommand.getLivingEntity(entity);
        if (!livingEntity.getAttributes().hasAttribute(attribute)) {
            throw NO_ATTRIBUTE_EXCEPTION.create((Object)entity.getName(), (Object)new TranslatableText(attribute.getTranslationKey()));
        }
        return livingEntity;
    }

    private static int executeValueGet(ServerCommandSource source, Entity target, EntityAttribute attribute, double multiplier) throws CommandSyntaxException {
        LivingEntity livingEntity = AttributeCommand.getLivingEntityWithAttribute(target, attribute);
        double d = livingEntity.getAttributeValue(attribute);
        source.sendFeedback(new TranslatableText("commands.attribute.value.get.success", new TranslatableText(attribute.getTranslationKey()), target.getName(), d), false);
        return (int)(d * multiplier);
    }

    private static int executeBaseValueGet(ServerCommandSource source, Entity target, EntityAttribute attribute, double multiplier) throws CommandSyntaxException {
        LivingEntity livingEntity = AttributeCommand.getLivingEntityWithAttribute(target, attribute);
        double d = livingEntity.getAttributeBaseValue(attribute);
        source.sendFeedback(new TranslatableText("commands.attribute.base_value.get.success", new TranslatableText(attribute.getTranslationKey()), target.getName(), d), false);
        return (int)(d * multiplier);
    }

    private static int executeModifierValueGet(ServerCommandSource source, Entity target, EntityAttribute attribute, UUID uuid, double multiplier) throws CommandSyntaxException {
        LivingEntity livingEntity = AttributeCommand.getLivingEntityWithAttribute(target, attribute);
        AttributeContainer attributeContainer = livingEntity.getAttributes();
        if (!attributeContainer.hasModifierForAttribute(attribute, uuid)) {
            throw NO_MODIFIER_EXCEPTION.create((Object)target.getName(), (Object)new TranslatableText(attribute.getTranslationKey()), (Object)uuid);
        }
        double d = attributeContainer.getModifierValue(attribute, uuid);
        source.sendFeedback(new TranslatableText("commands.attribute.modifier.value.get.success", uuid, new TranslatableText(attribute.getTranslationKey()), target.getName(), d), false);
        return (int)(d * multiplier);
    }

    private static int executeBaseValueSet(ServerCommandSource source, Entity target, EntityAttribute attribute, double value) throws CommandSyntaxException {
        AttributeCommand.getAttributeInstance(target, attribute).setBaseValue(value);
        source.sendFeedback(new TranslatableText("commands.attribute.base_value.set.success", new TranslatableText(attribute.getTranslationKey()), target.getName(), value), false);
        return 1;
    }

    private static int executeModifierAdd(ServerCommandSource source, Entity target, EntityAttribute attribute, UUID uuid, String name, double value, EntityAttributeModifier.Operation operation) throws CommandSyntaxException {
        EntityAttributeModifier entityAttributeModifier;
        EntityAttributeInstance entityAttributeInstance = AttributeCommand.getAttributeInstance(target, attribute);
        if (entityAttributeInstance.hasModifier(entityAttributeModifier = new EntityAttributeModifier(uuid, name, value, operation))) {
            throw MODIFIER_ALREADY_PRESENT_EXCEPTION.create((Object)target.getName(), (Object)new TranslatableText(attribute.getTranslationKey()), (Object)uuid);
        }
        entityAttributeInstance.addPersistentModifier(entityAttributeModifier);
        source.sendFeedback(new TranslatableText("commands.attribute.modifier.add.success", uuid, new TranslatableText(attribute.getTranslationKey()), target.getName()), false);
        return 1;
    }

    private static int executeModifierRemove(ServerCommandSource source, Entity target, EntityAttribute attribute, UUID uuid) throws CommandSyntaxException {
        EntityAttributeInstance entityAttributeInstance = AttributeCommand.getAttributeInstance(target, attribute);
        if (entityAttributeInstance.tryRemoveModifier(uuid)) {
            source.sendFeedback(new TranslatableText("commands.attribute.modifier.remove.success", uuid, new TranslatableText(attribute.getTranslationKey()), target.getName()), false);
            return 1;
        }
        throw NO_MODIFIER_EXCEPTION.create((Object)target.getName(), (Object)new TranslatableText(attribute.getTranslationKey()), (Object)uuid);
    }
}

