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
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.tag.TagKey;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class ItemStringReader {
    public static final SimpleCommandExceptionType TAG_DISALLOWED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.item.tag.disallowed"));
    public static final DynamicCommandExceptionType ID_INVALID_EXCEPTION = new DynamicCommandExceptionType(id -> new TranslatableText("argument.item.id.invalid", id));
    private static final char LEFT_CURLY_BRACKET = '{';
    private static final char HASH_SIGN = '#';
    private static final BiFunction<SuggestionsBuilder, Registry<Item>, CompletableFuture<Suggestions>> NBT_SUGGESTION_PROVIDER = (builder, registry) -> builder.buildFuture();
    private final StringReader reader;
    private final boolean allowTag;
    private Item item;
    @Nullable
    private NbtCompound nbt;
    @Nullable
    private TagKey<Item> id;
    private int cursor;
    private BiFunction<SuggestionsBuilder, Registry<Item>, CompletableFuture<Suggestions>> suggestions = NBT_SUGGESTION_PROVIDER;

    public ItemStringReader(StringReader reader, boolean allowTag) {
        this.reader = reader;
        this.allowTag = allowTag;
    }

    public Item getItem() {
        return this.item;
    }

    @Nullable
    public NbtCompound getNbt() {
        return this.nbt;
    }

    public TagKey<Item> getId() {
        return this.id;
    }

    public void readItem() throws CommandSyntaxException {
        int i = this.reader.getCursor();
        Identifier identifier = Identifier.fromCommandInput(this.reader);
        this.item = Registry.ITEM.getOrEmpty(identifier).orElseThrow(() -> {
            this.reader.setCursor(i);
            return ID_INVALID_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, (Object)identifier.toString());
        });
    }

    public void readTag() throws CommandSyntaxException {
        if (!this.allowTag) {
            throw TAG_DISALLOWED_EXCEPTION.create();
        }
        this.suggestions = this::suggestTag;
        this.reader.expect('#');
        this.cursor = this.reader.getCursor();
        this.id = TagKey.of(Registry.ITEM_KEY, Identifier.fromCommandInput(this.reader));
    }

    public void readNbt() throws CommandSyntaxException {
        this.nbt = new StringNbtReader(this.reader).parseCompound();
    }

    public ItemStringReader consume() throws CommandSyntaxException {
        this.suggestions = this::suggestAny;
        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.readTag();
        } else {
            this.readItem();
            this.suggestions = this::suggestItem;
        }
        if (this.reader.canRead() && this.reader.peek() == '{') {
            this.suggestions = NBT_SUGGESTION_PROVIDER;
            this.readNbt();
        }
        return this;
    }

    private CompletableFuture<Suggestions> suggestItem(SuggestionsBuilder builder, Registry<Item> registry) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf('{'));
        }
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder builder, Registry<Item> registry) {
        return CommandSource.suggestIdentifiers(registry.streamTags().map(TagKey::id), builder.createOffset(this.cursor));
    }

    private CompletableFuture<Suggestions> suggestAny(SuggestionsBuilder builder, Registry<Item> registry) {
        if (this.allowTag) {
            CommandSource.suggestIdentifiers(registry.streamTags().map(TagKey::id), builder, String.valueOf('#'));
        }
        return CommandSource.suggestIdentifiers(Registry.ITEM.getIds(), builder);
    }

    public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder, Registry<Item> registry) {
        return this.suggestions.apply(builder.createOffset(this.reader.getCursor()), registry);
    }
}

