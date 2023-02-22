/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
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

import com.google.common.collect.Maps;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.state.property.Property;
import net.minecraft.tag.TagGroup;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class ItemStringReader {
    public static final SimpleCommandExceptionType TAG_DISALLOWED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.item.tag.disallowed"));
    public static final DynamicCommandExceptionType ID_INVALID_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.item.id.invalid", object));
    private static final BiFunction<SuggestionsBuilder, TagGroup<Item>, CompletableFuture<Suggestions>> NBT_SUGGESTION_PROVIDER = (suggestionsBuilder, tagGroup) -> suggestionsBuilder.buildFuture();
    private final StringReader reader;
    private final boolean allowTag;
    private final Map<Property<?>, Comparable<?>> field_10801 = Maps.newHashMap();
    private Item item;
    @Nullable
    private NbtCompound nbt;
    private Identifier id = new Identifier("");
    private int cursor;
    private BiFunction<SuggestionsBuilder, TagGroup<Item>, CompletableFuture<Suggestions>> suggestions = NBT_SUGGESTION_PROVIDER;

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

    public Identifier getId() {
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
        this.id = Identifier.fromCommandInput(this.reader);
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

    private CompletableFuture<Suggestions> suggestItem(SuggestionsBuilder suggestionsBuilder, TagGroup<Item> tagGroup) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf('{'));
        }
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder suggestionsBuilder, TagGroup<Item> tagGroup) {
        return CommandSource.suggestIdentifiers(tagGroup.getTagIds(), suggestionsBuilder.createOffset(this.cursor));
    }

    private CompletableFuture<Suggestions> suggestAny(SuggestionsBuilder suggestionsBuilder, TagGroup<Item> tagGroup) {
        if (this.allowTag) {
            CommandSource.suggestIdentifiers(tagGroup.getTagIds(), suggestionsBuilder, String.valueOf('#'));
        }
        return CommandSource.suggestIdentifiers(Registry.ITEM.getIds(), suggestionsBuilder);
    }

    public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder, TagGroup<Item> tagGroup) {
        return this.suggestions.apply(builder.createOffset(this.reader.getCursor()), tagGroup);
    }
}
