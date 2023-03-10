/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancement.Advancement;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionManager;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class IdentifierArgumentType
implements ArgumentType<Identifier> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
    private static final DynamicCommandExceptionType UNKNOWN_ADVANCEMENT_EXCEPTION = new DynamicCommandExceptionType(id -> new TranslatableText("advancement.advancementNotFound", id));
    private static final DynamicCommandExceptionType UNKNOWN_RECIPE_EXCEPTION = new DynamicCommandExceptionType(id -> new TranslatableText("recipe.notFound", id));
    private static final DynamicCommandExceptionType UNKNOWN_PREDICATE_EXCEPTION = new DynamicCommandExceptionType(id -> new TranslatableText("predicate.unknown", id));
    private static final DynamicCommandExceptionType UNKNOWN_ITEM_MODIFIER_EXCEPTION = new DynamicCommandExceptionType(id -> new TranslatableText("item_modifier.unknown", id));

    public static IdentifierArgumentType identifier() {
        return new IdentifierArgumentType();
    }

    public static Advancement getAdvancementArgument(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
        Identifier identifier = IdentifierArgumentType.getIdentifier(context, argumentName);
        Advancement advancement = ((ServerCommandSource)context.getSource()).getServer().getAdvancementLoader().get(identifier);
        if (advancement == null) {
            throw UNKNOWN_ADVANCEMENT_EXCEPTION.create((Object)identifier);
        }
        return advancement;
    }

    public static Recipe<?> getRecipeArgument(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
        RecipeManager recipeManager = ((ServerCommandSource)context.getSource()).getServer().getRecipeManager();
        Identifier identifier = IdentifierArgumentType.getIdentifier(context, argumentName);
        return recipeManager.get(identifier).orElseThrow(() -> UNKNOWN_RECIPE_EXCEPTION.create((Object)identifier));
    }

    public static LootCondition getPredicateArgument(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
        Identifier identifier = IdentifierArgumentType.getIdentifier(context, argumentName);
        LootConditionManager lootConditionManager = ((ServerCommandSource)context.getSource()).getServer().getPredicateManager();
        LootCondition lootCondition = lootConditionManager.get(identifier);
        if (lootCondition == null) {
            throw UNKNOWN_PREDICATE_EXCEPTION.create((Object)identifier);
        }
        return lootCondition;
    }

    public static LootFunction getItemModifierArgument(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
        Identifier identifier = IdentifierArgumentType.getIdentifier(context, argumentName);
        LootFunctionManager lootFunctionManager = ((ServerCommandSource)context.getSource()).getServer().getItemModifierManager();
        LootFunction lootFunction = lootFunctionManager.get(identifier);
        if (lootFunction == null) {
            throw UNKNOWN_ITEM_MODIFIER_EXCEPTION.create((Object)identifier);
        }
        return lootFunction;
    }

    public static Identifier getIdentifier(CommandContext<ServerCommandSource> context, String name) {
        return (Identifier)context.getArgument(name, Identifier.class);
    }

    public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
        return Identifier.fromCommandInput(stringReader);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }
}

