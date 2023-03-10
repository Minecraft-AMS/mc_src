/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.command.argument;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class EntityArgumentType
implements ArgumentType<EntitySelector> {
    private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
    public static final SimpleCommandExceptionType TOO_MANY_ENTITIES_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.entity.toomany"));
    public static final SimpleCommandExceptionType TOO_MANY_PLAYERS_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.player.toomany"));
    public static final SimpleCommandExceptionType PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.player.entities"));
    public static final SimpleCommandExceptionType ENTITY_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.entity.notfound.entity"));
    public static final SimpleCommandExceptionType PLAYER_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.entity.notfound.player"));
    public static final SimpleCommandExceptionType NOT_ALLOWED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.entity.selector.not_allowed"));
    private static final byte SINGLE_TARGET_MASK = 1;
    private static final byte PLAYERS_ONLY_MASK = 2;
    final boolean singleTarget;
    final boolean playersOnly;

    protected EntityArgumentType(boolean singleTarget, boolean playersOnly) {
        this.singleTarget = singleTarget;
        this.playersOnly = playersOnly;
    }

    public static EntityArgumentType entity() {
        return new EntityArgumentType(true, false);
    }

    public static Entity getEntity(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ((EntitySelector)context.getArgument(name, EntitySelector.class)).getEntity((ServerCommandSource)context.getSource());
    }

    public static EntityArgumentType entities() {
        return new EntityArgumentType(false, false);
    }

    public static Collection<? extends Entity> getEntities(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        Collection<? extends Entity> collection = EntityArgumentType.getOptionalEntities(context, name);
        if (collection.isEmpty()) {
            throw ENTITY_NOT_FOUND_EXCEPTION.create();
        }
        return collection;
    }

    public static Collection<? extends Entity> getOptionalEntities(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ((EntitySelector)context.getArgument(name, EntitySelector.class)).getEntities((ServerCommandSource)context.getSource());
    }

    public static Collection<ServerPlayerEntity> getOptionalPlayers(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ((EntitySelector)context.getArgument(name, EntitySelector.class)).getPlayers((ServerCommandSource)context.getSource());
    }

    public static EntityArgumentType player() {
        return new EntityArgumentType(true, true);
    }

    public static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ((EntitySelector)context.getArgument(name, EntitySelector.class)).getPlayer((ServerCommandSource)context.getSource());
    }

    public static EntityArgumentType players() {
        return new EntityArgumentType(false, true);
    }

    public static Collection<ServerPlayerEntity> getPlayers(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        List<ServerPlayerEntity> list = ((EntitySelector)context.getArgument(name, EntitySelector.class)).getPlayers((ServerCommandSource)context.getSource());
        if (list.isEmpty()) {
            throw PLAYER_NOT_FOUND_EXCEPTION.create();
        }
        return list;
    }

    public EntitySelector parse(StringReader stringReader) throws CommandSyntaxException {
        boolean i = false;
        EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);
        EntitySelector entitySelector = entitySelectorReader.read();
        if (entitySelector.getLimit() > 1 && this.singleTarget) {
            if (this.playersOnly) {
                stringReader.setCursor(0);
                throw TOO_MANY_PLAYERS_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
            }
            stringReader.setCursor(0);
            throw TOO_MANY_ENTITIES_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        if (entitySelector.includesNonPlayers() && this.playersOnly && !entitySelector.isSenderOnly()) {
            stringReader.setCursor(0);
            throw PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        return entitySelector;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder2) {
        if (context.getSource() instanceof CommandSource) {
            StringReader stringReader = new StringReader(builder2.getInput());
            stringReader.setCursor(builder2.getStart());
            CommandSource commandSource = (CommandSource)context.getSource();
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader, commandSource.hasPermissionLevel(2));
            try {
                entitySelectorReader.read();
            }
            catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
            return entitySelectorReader.listSuggestions(builder2, (SuggestionsBuilder builder) -> {
                Collection<String> collection = commandSource.getPlayerNames();
                Collection<String> iterable = this.playersOnly ? collection : Iterables.concat(collection, commandSource.getEntitySuggestions());
                CommandSource.suggestMatching(iterable, builder);
            });
        }
        return Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }

    public static class Serializer
    implements ArgumentSerializer<EntityArgumentType> {
        @Override
        public void toPacket(EntityArgumentType entityArgumentType, PacketByteBuf packetByteBuf) {
            byte b = 0;
            if (entityArgumentType.singleTarget) {
                b = (byte)(b | 1);
            }
            if (entityArgumentType.playersOnly) {
                b = (byte)(b | 2);
            }
            packetByteBuf.writeByte(b);
        }

        @Override
        public EntityArgumentType fromPacket(PacketByteBuf packetByteBuf) {
            byte b = packetByteBuf.readByte();
            return new EntityArgumentType((b & 1) != 0, (b & 2) != 0);
        }

        @Override
        public void toJson(EntityArgumentType entityArgumentType, JsonObject jsonObject) {
            jsonObject.addProperty("amount", entityArgumentType.singleTarget ? "single" : "multiple");
            jsonObject.addProperty("type", entityArgumentType.playersOnly ? "players" : "entities");
        }

        @Override
        public /* synthetic */ ArgumentType fromPacket(PacketByteBuf buf) {
            return this.fromPacket(buf);
        }
    }
}

