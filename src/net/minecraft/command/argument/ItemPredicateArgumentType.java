/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.datafixers.util.Either
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandRegistryWrapper;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

public class ItemPredicateArgumentType
implements ArgumentType<ItemStackPredicateArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
    private final CommandRegistryWrapper<Item> registryWrapper;

    public ItemPredicateArgumentType(CommandRegistryAccess commandRegistryAccess) {
        this.registryWrapper = commandRegistryAccess.createWrapper(Registry.ITEM_KEY);
    }

    public static ItemPredicateArgumentType itemPredicate(CommandRegistryAccess commandRegistryAccess) {
        return new ItemPredicateArgumentType(commandRegistryAccess);
    }

    public ItemStackPredicateArgument parse(StringReader stringReader) throws CommandSyntaxException {
        Either<ItemStringReader.ItemResult, ItemStringReader.TagResult> either = ItemStringReader.itemOrTag(this.registryWrapper, stringReader);
        return (ItemStackPredicateArgument)either.map(item -> ItemPredicateArgumentType.getItemStackPredicate((RegistryEntry<Item> item2) -> item2 == item.item(), item.nbt()), tag -> ItemPredicateArgumentType.getItemStackPredicate(tag.tag()::contains, tag.nbt()));
    }

    public static Predicate<ItemStack> getItemStackPredicate(CommandContext<ServerCommandSource> context, String name) {
        return (Predicate)context.getArgument(name, ItemStackPredicateArgument.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ItemStringReader.getSuggestions(this.registryWrapper, builder, true);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static ItemStackPredicateArgument getItemStackPredicate(Predicate<RegistryEntry<Item>> predicate, @Nullable NbtCompound nbt) {
        return nbt != null ? stack -> stack.itemMatches(predicate) && NbtHelper.matches(nbt, stack.getNbt(), true) : stack -> stack.itemMatches(predicate);
    }

    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }

    public static interface ItemStackPredicateArgument
    extends Predicate<ItemStack> {
    }
}

