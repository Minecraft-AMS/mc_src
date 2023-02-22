/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.datafixers.util.Either
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ItemStringReader {
    private static final SimpleCommandExceptionType TAG_DISALLOWED_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("argument.item.tag.disallowed"));
    private static final DynamicCommandExceptionType ID_INVALID_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("argument.item.id.invalid", id));
    private static final DynamicCommandExceptionType UNKNOWN_TAG_EXCEPTION = new DynamicCommandExceptionType(tag -> Text.translatable("arguments.item.tag.unknown", tag));
    private static final char LEFT_CURLY_BRACKET = '{';
    private static final char HASH_SIGN = '#';
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> NBT_SUGGESTION_PROVIDER = SuggestionsBuilder::buildFuture;
    private final RegistryWrapper<Item> registryWrapper;
    private final StringReader reader;
    private final boolean allowTag;
    private Either<RegistryEntry<Item>, RegistryEntryList<Item>> result;
    @Nullable
    private NbtCompound nbt;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = NBT_SUGGESTION_PROVIDER;

    private ItemStringReader(RegistryWrapper<Item> registryWrapper, StringReader reader, boolean allowTag) {
        this.registryWrapper = registryWrapper;
        this.reader = reader;
        this.allowTag = allowTag;
    }

    public static ItemResult item(RegistryWrapper<Item> registryWrapper, StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        try {
            ItemStringReader itemStringReader = new ItemStringReader(registryWrapper, reader, false);
            itemStringReader.consume();
            RegistryEntry registryEntry = (RegistryEntry)itemStringReader.result.left().orElseThrow(() -> new IllegalStateException("Parser returned unexpected tag name"));
            return new ItemResult(registryEntry, itemStringReader.nbt);
        }
        catch (CommandSyntaxException commandSyntaxException) {
            reader.setCursor(i);
            throw commandSyntaxException;
        }
    }

    public static Either<ItemResult, TagResult> itemOrTag(RegistryWrapper<Item> registryWrapper, StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        try {
            ItemStringReader itemStringReader = new ItemStringReader(registryWrapper, reader, true);
            itemStringReader.consume();
            return itemStringReader.result.mapBoth(item -> new ItemResult((RegistryEntry<Item>)item, itemStringReader.nbt), tag -> new TagResult((RegistryEntryList<Item>)tag, itemStringReader.nbt));
        }
        catch (CommandSyntaxException commandSyntaxException) {
            reader.setCursor(i);
            throw commandSyntaxException;
        }
    }

    public static CompletableFuture<Suggestions> getSuggestions(RegistryWrapper<Item> registryWrapper, SuggestionsBuilder builder, boolean allowTag) {
        StringReader stringReader = new StringReader(builder.getInput());
        stringReader.setCursor(builder.getStart());
        ItemStringReader itemStringReader = new ItemStringReader(registryWrapper, stringReader, allowTag);
        try {
            itemStringReader.consume();
        }
        catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return itemStringReader.suggestions.apply(builder.createOffset(stringReader.getCursor()));
    }

    private void readItem() throws CommandSyntaxException {
        int i = this.reader.getCursor();
        Identifier identifier = Identifier.fromCommandInput(this.reader);
        Optional<RegistryEntry.Reference<Item>> optional = this.registryWrapper.getOptional(RegistryKey.of(RegistryKeys.ITEM, identifier));
        this.result = Either.left((Object)optional.orElseThrow(() -> {
            this.reader.setCursor(i);
            return ID_INVALID_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, (Object)identifier);
        }));
    }

    private void readTag() throws CommandSyntaxException {
        if (!this.allowTag) {
            throw TAG_DISALLOWED_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
        }
        int i = this.reader.getCursor();
        this.reader.expect('#');
        this.suggestions = this::suggestTag;
        Identifier identifier = Identifier.fromCommandInput(this.reader);
        Optional<RegistryEntryList.Named<Item>> optional = this.registryWrapper.getOptional(TagKey.of(RegistryKeys.ITEM, identifier));
        this.result = Either.right((Object)optional.orElseThrow(() -> {
            this.reader.setCursor(i);
            return UNKNOWN_TAG_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, (Object)identifier);
        }));
    }

    private void readNbt() throws CommandSyntaxException {
        this.nbt = new StringNbtReader(this.reader).parseCompound();
    }

    private void consume() throws CommandSyntaxException {
        this.suggestions = this.allowTag ? this::suggestItemOrTagId : this::suggestItemId;
        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.readTag();
        } else {
            this.readItem();
        }
        this.suggestions = this::suggestItem;
        if (this.reader.canRead() && this.reader.peek() == '{') {
            this.suggestions = NBT_SUGGESTION_PROVIDER;
            this.readNbt();
        }
    }

    private CompletableFuture<Suggestions> suggestItem(SuggestionsBuilder builder) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf('{'));
        }
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(this.registryWrapper.streamTagKeys().map(TagKey::id), builder, String.valueOf('#'));
    }

    private CompletableFuture<Suggestions> suggestItemId(SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(this.registryWrapper.streamKeys().map(RegistryKey::getValue), builder);
    }

    private CompletableFuture<Suggestions> suggestItemOrTagId(SuggestionsBuilder builder) {
        this.suggestTag(builder);
        return this.suggestItemId(builder);
    }

    public record ItemResult(RegistryEntry<Item> item, @Nullable NbtCompound nbt) {
    }

    public record TagResult(RegistryEntryList<Item> tag, @Nullable NbtCompound nbt) {
    }
}

