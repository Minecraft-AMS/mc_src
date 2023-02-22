/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 */
package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.PacketByteBuf;

public class ScoreHolderArgumentType
implements ArgumentType<ScoreHolder> {
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder2) -> {
        StringReader stringReader = new StringReader(suggestionsBuilder2.getInput());
        stringReader.setCursor(suggestionsBuilder2.getStart());
        EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);
        try {
            entitySelectorReader.read();
        }
        catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return entitySelectorReader.listSuggestions(suggestionsBuilder2, suggestionsBuilder -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getPlayerNames(), suggestionsBuilder));
    };
    private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "*", "@e");
    private static final SimpleCommandExceptionType EMPTY_SCORE_HOLDER_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.scoreHolder.empty", new Object[0]));
    private final boolean multiple;

    public ScoreHolderArgumentType(boolean multiple) {
        this.multiple = multiple;
    }

    public static String getScoreHolder(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ScoreHolderArgumentType.getScoreHolders(context, name).iterator().next();
    }

    public static Collection<String> getScoreHolders(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ScoreHolderArgumentType.getScoreHolders(context, name, Collections::emptyList);
    }

    public static Collection<String> getScoreboardScoreHolders(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ScoreHolderArgumentType.getScoreHolders(context, name, ((ServerCommandSource)context.getSource()).getMinecraftServer().getScoreboard()::getKnownPlayers);
    }

    public static Collection<String> getScoreHolders(CommandContext<ServerCommandSource> context, String name, Supplier<Collection<String>> players) throws CommandSyntaxException {
        Collection<String> collection = ((ScoreHolder)context.getArgument(name, ScoreHolder.class)).getNames((ServerCommandSource)context.getSource(), players);
        if (collection.isEmpty()) {
            throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
        }
        return collection;
    }

    public static ScoreHolderArgumentType scoreHolder() {
        return new ScoreHolderArgumentType(false);
    }

    public static ScoreHolderArgumentType scoreHolders() {
        return new ScoreHolderArgumentType(true);
    }

    public ScoreHolder parse(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '@') {
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);
            EntitySelector entitySelector = entitySelectorReader.read();
            if (!this.multiple && entitySelector.getLimit() > 1) {
                throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
            }
            return new SelectorScoreHolder(entitySelector);
        }
        int i = stringReader.getCursor();
        while (stringReader.canRead() && stringReader.peek() != ' ') {
            stringReader.skip();
        }
        String string = stringReader.getString().substring(i, stringReader.getCursor());
        if (string.equals("*")) {
            return (serverCommandSource, supplier) -> {
                Collection collection = (Collection)supplier.get();
                if (collection.isEmpty()) {
                    throw EMPTY_SCORE_HOLDER_EXCEPTION.create();
                }
                return collection;
            };
        }
        Set<String> collection = Collections.singleton(string);
        return (serverCommandSource, supplier) -> collection;
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    public static class Serializer
    implements ArgumentSerializer<ScoreHolderArgumentType> {
        @Override
        public void toPacket(ScoreHolderArgumentType scoreHolderArgumentType, PacketByteBuf packetByteBuf) {
            int b = 0;
            if (scoreHolderArgumentType.multiple) {
                b = (byte)(b | 1);
            }
            packetByteBuf.writeByte(b);
        }

        @Override
        public ScoreHolderArgumentType fromPacket(PacketByteBuf packetByteBuf) {
            byte b = packetByteBuf.readByte();
            boolean bl = (b & 1) != 0;
            return new ScoreHolderArgumentType(bl);
        }

        @Override
        public void toJson(ScoreHolderArgumentType scoreHolderArgumentType, JsonObject jsonObject) {
            jsonObject.addProperty("amount", scoreHolderArgumentType.multiple ? "multiple" : "single");
        }

        @Override
        public /* synthetic */ ArgumentType fromPacket(PacketByteBuf packetByteBuf) {
            return this.fromPacket(packetByteBuf);
        }
    }

    public static class SelectorScoreHolder
    implements ScoreHolder {
        private final EntitySelector selector;

        public SelectorScoreHolder(EntitySelector entitySelector) {
            this.selector = entitySelector;
        }

        @Override
        public Collection<String> getNames(ServerCommandSource source, Supplier<Collection<String>> supplier) throws CommandSyntaxException {
            List<? extends Entity> list = this.selector.getEntities(source);
            if (list.isEmpty()) {
                throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
            }
            ArrayList list2 = Lists.newArrayList();
            for (Entity entity : list) {
                list2.add(entity.getEntityName());
            }
            return list2;
        }
    }

    @FunctionalInterface
    public static interface ScoreHolder {
        public Collection<String> getNames(ServerCommandSource var1, Supplier<Collection<String>> var2) throws CommandSyntaxException;
    }
}

