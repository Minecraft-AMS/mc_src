/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.command;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public interface CommandSource {
    public Collection<String> getPlayerNames();

    default public Collection<String> getChatSuggestions() {
        return this.getPlayerNames();
    }

    default public Collection<String> getEntitySuggestions() {
        return Collections.emptyList();
    }

    public Collection<String> getTeamNames();

    public Stream<Identifier> getSoundIds();

    public Stream<Identifier> getRecipeIds();

    public CompletableFuture<Suggestions> getCompletions(CommandContext<?> var1);

    default public Collection<RelativePosition> getBlockPositionSuggestions() {
        return Collections.singleton(RelativePosition.ZERO_WORLD);
    }

    default public Collection<RelativePosition> getPositionSuggestions() {
        return Collections.singleton(RelativePosition.ZERO_WORLD);
    }

    public Set<RegistryKey<World>> getWorldKeys();

    public DynamicRegistryManager getRegistryManager();

    public FeatureSet getEnabledFeatures();

    default public void suggestIdentifiers(Registry<?> registry, SuggestedIdType suggestedIdType, SuggestionsBuilder builder) {
        if (suggestedIdType.canSuggestTags()) {
            CommandSource.suggestIdentifiers(registry.streamTags().map(TagKey::id), builder, "#");
        }
        if (suggestedIdType.canSuggestElements()) {
            CommandSource.suggestIdentifiers(registry.getIds(), builder);
        }
    }

    public CompletableFuture<Suggestions> listIdSuggestions(RegistryKey<? extends Registry<?>> var1, SuggestedIdType var2, SuggestionsBuilder var3, CommandContext<?> var4);

    public boolean hasPermissionLevel(int var1);

    public static <T> void forEachMatching(Iterable<T> candidates, String remaining, Function<T, Identifier> identifier, Consumer<T> action) {
        boolean bl = remaining.indexOf(58) > -1;
        for (T object : candidates) {
            Identifier identifier2 = identifier.apply(object);
            if (bl) {
                String string = identifier2.toString();
                if (!CommandSource.shouldSuggest(remaining, string)) continue;
                action.accept(object);
                continue;
            }
            if (!CommandSource.shouldSuggest(remaining, identifier2.getNamespace()) && (!identifier2.getNamespace().equals("minecraft") || !CommandSource.shouldSuggest(remaining, identifier2.getPath()))) continue;
            action.accept(object);
        }
    }

    public static <T> void forEachMatching(Iterable<T> candidates, String remaining, String prefix, Function<T, Identifier> identifier, Consumer<T> action) {
        if (remaining.isEmpty()) {
            candidates.forEach(action);
        } else {
            String string = Strings.commonPrefix((CharSequence)remaining, (CharSequence)prefix);
            if (!string.isEmpty()) {
                String string2 = remaining.substring(string.length());
                CommandSource.forEachMatching(candidates, string2, identifier, action);
            }
        }
    }

    public static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> candidates, SuggestionsBuilder builder, String prefix) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.forEachMatching(candidates, string, prefix, id -> id, id -> builder.suggest(prefix + id));
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestIdentifiers(Stream<Identifier> candidates, SuggestionsBuilder builder, String prefix) {
        return CommandSource.suggestIdentifiers(candidates::iterator, builder, prefix);
    }

    public static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> candidates, SuggestionsBuilder builder) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.forEachMatching(candidates, string, id -> id, id -> builder.suggest(id.toString()));
        return builder.buildFuture();
    }

    public static <T> CompletableFuture<Suggestions> suggestFromIdentifier(Iterable<T> candidates, SuggestionsBuilder builder, Function<T, Identifier> identifier, Function<T, Message> tooltip) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.forEachMatching(candidates, string, identifier, object -> builder.suggest(((Identifier)identifier.apply(object)).toString(), (Message)tooltip.apply(object)));
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestIdentifiers(Stream<Identifier> candidates, SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(candidates::iterator, builder);
    }

    public static <T> CompletableFuture<Suggestions> suggestFromIdentifier(Stream<T> candidates, SuggestionsBuilder builder, Function<T, Identifier> identifier, Function<T, Message> tooltip) {
        return CommandSource.suggestFromIdentifier(candidates::iterator, builder, identifier, tooltip);
    }

    public static CompletableFuture<Suggestions> suggestPositions(String remaining, Collection<RelativePosition> candidates, SuggestionsBuilder builder, Predicate<String> predicate) {
        ArrayList list;
        block4: {
            String[] strings;
            block5: {
                block3: {
                    list = Lists.newArrayList();
                    if (!Strings.isNullOrEmpty((String)remaining)) break block3;
                    for (RelativePosition relativePosition : candidates) {
                        String string = relativePosition.x + " " + relativePosition.y + " " + relativePosition.z;
                        if (!predicate.test(string)) continue;
                        list.add(relativePosition.x);
                        list.add(relativePosition.x + " " + relativePosition.y);
                        list.add(string);
                    }
                    break block4;
                }
                strings = remaining.split(" ");
                if (strings.length != 1) break block5;
                for (RelativePosition relativePosition2 : candidates) {
                    String string2 = strings[0] + " " + relativePosition2.y + " " + relativePosition2.z;
                    if (!predicate.test(string2)) continue;
                    list.add(strings[0] + " " + relativePosition2.y);
                    list.add(string2);
                }
                break block4;
            }
            if (strings.length != 2) break block4;
            for (RelativePosition relativePosition2 : candidates) {
                String string2 = strings[0] + " " + strings[1] + " " + relativePosition2.z;
                if (!predicate.test(string2)) continue;
                list.add(string2);
            }
        }
        return CommandSource.suggestMatching(list, builder);
    }

    public static CompletableFuture<Suggestions> suggestColumnPositions(String remaining, Collection<RelativePosition> candidates, SuggestionsBuilder builder, Predicate<String> predicate) {
        ArrayList list;
        block3: {
            block2: {
                list = Lists.newArrayList();
                if (!Strings.isNullOrEmpty((String)remaining)) break block2;
                for (RelativePosition relativePosition : candidates) {
                    String string = relativePosition.x + " " + relativePosition.z;
                    if (!predicate.test(string)) continue;
                    list.add(relativePosition.x);
                    list.add(string);
                }
                break block3;
            }
            String[] strings = remaining.split(" ");
            if (strings.length != 1) break block3;
            for (RelativePosition relativePosition2 : candidates) {
                String string2 = strings[0] + " " + relativePosition2.z;
                if (!predicate.test(string2)) continue;
                list.add(string2);
            }
        }
        return CommandSource.suggestMatching(list, builder);
    }

    public static CompletableFuture<Suggestions> suggestMatching(Iterable<String> candidates, SuggestionsBuilder builder) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (String string2 : candidates) {
            if (!CommandSource.shouldSuggest(string, string2.toLowerCase(Locale.ROOT))) continue;
            builder.suggest(string2);
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestMatching(Stream<String> candidates, SuggestionsBuilder builder) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        candidates.filter(candidate -> CommandSource.shouldSuggest(string, candidate.toLowerCase(Locale.ROOT))).forEach(arg_0 -> ((SuggestionsBuilder)builder).suggest(arg_0));
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestMatching(String[] candidates, SuggestionsBuilder builder) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (String string2 : candidates) {
            if (!CommandSource.shouldSuggest(string, string2.toLowerCase(Locale.ROOT))) continue;
            builder.suggest(string2);
        }
        return builder.buildFuture();
    }

    public static <T> CompletableFuture<Suggestions> suggestMatching(Iterable<T> candidates, SuggestionsBuilder builder, Function<T, String> suggestionText, Function<T, Message> tooltip) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (T object : candidates) {
            String string2 = suggestionText.apply(object);
            if (!CommandSource.shouldSuggest(string, string2.toLowerCase(Locale.ROOT))) continue;
            builder.suggest(string2, tooltip.apply(object));
        }
        return builder.buildFuture();
    }

    public static boolean shouldSuggest(String remaining, String candidate) {
        int i = 0;
        while (!candidate.startsWith(remaining, i)) {
            if ((i = candidate.indexOf(95, i)) < 0) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static class RelativePosition {
        public static final RelativePosition ZERO_LOCAL = new RelativePosition("^", "^", "^");
        public static final RelativePosition ZERO_WORLD = new RelativePosition("~", "~", "~");
        public final String x;
        public final String y;
        public final String z;

        public RelativePosition(String x, String y, String z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static final class SuggestedIdType
    extends Enum<SuggestedIdType> {
        public static final /* enum */ SuggestedIdType TAGS = new SuggestedIdType();
        public static final /* enum */ SuggestedIdType ELEMENTS = new SuggestedIdType();
        public static final /* enum */ SuggestedIdType ALL = new SuggestedIdType();
        private static final /* synthetic */ SuggestedIdType[] field_37265;

        public static SuggestedIdType[] values() {
            return (SuggestedIdType[])field_37265.clone();
        }

        public static SuggestedIdType valueOf(String string) {
            return Enum.valueOf(SuggestedIdType.class, string);
        }

        public boolean canSuggestTags() {
            return this == TAGS || this == ALL;
        }

        public boolean canSuggestElements() {
            return this == ELEMENTS || this == ALL;
        }

        private static /* synthetic */ SuggestedIdType[] method_41217() {
            return new SuggestedIdType[]{TAGS, ELEMENTS, ALL};
        }

        static {
            field_37265 = SuggestedIdType.method_41217();
        }
    }
}

