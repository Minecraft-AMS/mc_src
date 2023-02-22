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
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.FloatRangeArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.predicate.NumberRange;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;

public class EntitySelectorOptions {
    private static final Map<String, SelectorOption> options = Maps.newHashMap();
    public static final DynamicCommandExceptionType UNKNOWN_OPTION_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.entity.options.unknown", object));
    public static final DynamicCommandExceptionType INAPPLICABLE_OPTION_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.entity.options.inapplicable", object));
    public static final SimpleCommandExceptionType NEGATIVE_DISTANCE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.entity.options.distance.negative", new Object[0]));
    public static final SimpleCommandExceptionType NEGATIVE_LEVEL_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.entity.options.level.negative", new Object[0]));
    public static final SimpleCommandExceptionType TOO_SMALL_LEVEL_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.entity.options.limit.toosmall", new Object[0]));
    public static final DynamicCommandExceptionType IRREVERSIBLE_SORT_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.entity.options.sort.irreversible", object));
    public static final DynamicCommandExceptionType INVALID_MODE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.entity.options.mode.invalid", object));
    public static final DynamicCommandExceptionType INVALID_TYPE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.entity.options.type.invalid", object));

    private static void putOption(String id, SelectorHandler handler, Predicate<EntitySelectorReader> condition, Text description) {
        options.put(id, new SelectorOption(handler, condition, description));
    }

    public static void register() {
        if (!options.isEmpty()) {
            return;
        }
        EntitySelectorOptions.putOption("name", entitySelectorReader -> {
            int i = entitySelectorReader.getReader().getCursor();
            boolean bl = entitySelectorReader.readNegationCharacter();
            String string = entitySelectorReader.getReader().readString();
            if (entitySelectorReader.method_9844() && !bl) {
                entitySelectorReader.getReader().setCursor(i);
                throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext((ImmutableStringReader)entitySelectorReader.getReader(), (Object)"name");
            }
            if (bl) {
                entitySelectorReader.method_9913(true);
            } else {
                entitySelectorReader.method_9899(true);
            }
            entitySelectorReader.setPredicate(entity -> entity.getName().asString().equals(string) != bl);
        }, entitySelectorReader -> !entitySelectorReader.method_9912(), new TranslatableText("argument.entity.options.name.description", new Object[0]));
        EntitySelectorOptions.putOption("distance", entitySelectorReader -> {
            int i = entitySelectorReader.getReader().getCursor();
            NumberRange.FloatRange floatRange = NumberRange.FloatRange.parse(entitySelectorReader.getReader());
            if (floatRange.getMin() != null && ((Float)floatRange.getMin()).floatValue() < 0.0f || floatRange.getMax() != null && ((Float)floatRange.getMax()).floatValue() < 0.0f) {
                entitySelectorReader.getReader().setCursor(i);
                throw NEGATIVE_DISTANCE_EXCEPTION.createWithContext((ImmutableStringReader)entitySelectorReader.getReader());
            }
            entitySelectorReader.setDistance(floatRange);
            entitySelectorReader.setLocalWorldOnly();
        }, entitySelectorReader -> entitySelectorReader.getDistance().isDummy(), new TranslatableText("argument.entity.options.distance.description", new Object[0]));
        EntitySelectorOptions.putOption("level", entitySelectorReader -> {
            int i = entitySelectorReader.getReader().getCursor();
            NumberRange.IntRange intRange = NumberRange.IntRange.parse(entitySelectorReader.getReader());
            if (intRange.getMin() != null && (Integer)intRange.getMin() < 0 || intRange.getMax() != null && (Integer)intRange.getMax() < 0) {
                entitySelectorReader.getReader().setCursor(i);
                throw NEGATIVE_LEVEL_EXCEPTION.createWithContext((ImmutableStringReader)entitySelectorReader.getReader());
            }
            entitySelectorReader.setLevelRange(intRange);
            entitySelectorReader.setIncludesNonPlayers(false);
        }, entitySelectorReader -> entitySelectorReader.getLevelRange().isDummy(), new TranslatableText("argument.entity.options.level.description", new Object[0]));
        EntitySelectorOptions.putOption("x", entitySelectorReader -> {
            entitySelectorReader.setLocalWorldOnly();
            entitySelectorReader.setX(entitySelectorReader.getReader().readDouble());
        }, entitySelectorReader -> entitySelectorReader.getX() == null, new TranslatableText("argument.entity.options.x.description", new Object[0]));
        EntitySelectorOptions.putOption("y", entitySelectorReader -> {
            entitySelectorReader.setLocalWorldOnly();
            entitySelectorReader.setY(entitySelectorReader.getReader().readDouble());
        }, entitySelectorReader -> entitySelectorReader.getY() == null, new TranslatableText("argument.entity.options.y.description", new Object[0]));
        EntitySelectorOptions.putOption("z", entitySelectorReader -> {
            entitySelectorReader.setLocalWorldOnly();
            entitySelectorReader.setZ(entitySelectorReader.getReader().readDouble());
        }, entitySelectorReader -> entitySelectorReader.getZ() == null, new TranslatableText("argument.entity.options.z.description", new Object[0]));
        EntitySelectorOptions.putOption("dx", entitySelectorReader -> {
            entitySelectorReader.setLocalWorldOnly();
            entitySelectorReader.setDx(entitySelectorReader.getReader().readDouble());
        }, entitySelectorReader -> entitySelectorReader.getDx() == null, new TranslatableText("argument.entity.options.dx.description", new Object[0]));
        EntitySelectorOptions.putOption("dy", entitySelectorReader -> {
            entitySelectorReader.setLocalWorldOnly();
            entitySelectorReader.setDy(entitySelectorReader.getReader().readDouble());
        }, entitySelectorReader -> entitySelectorReader.getDy() == null, new TranslatableText("argument.entity.options.dy.description", new Object[0]));
        EntitySelectorOptions.putOption("dz", entitySelectorReader -> {
            entitySelectorReader.setLocalWorldOnly();
            entitySelectorReader.setDz(entitySelectorReader.getReader().readDouble());
        }, entitySelectorReader -> entitySelectorReader.getDz() == null, new TranslatableText("argument.entity.options.dz.description", new Object[0]));
        EntitySelectorOptions.putOption("x_rotation", entitySelectorReader -> entitySelectorReader.setPitchRange(FloatRangeArgument.parse(entitySelectorReader.getReader(), true, MathHelper::wrapDegrees)), entitySelectorReader -> entitySelectorReader.getPitchRange() == FloatRangeArgument.ANY, new TranslatableText("argument.entity.options.x_rotation.description", new Object[0]));
        EntitySelectorOptions.putOption("y_rotation", entitySelectorReader -> entitySelectorReader.setYawRange(FloatRangeArgument.parse(entitySelectorReader.getReader(), true, MathHelper::wrapDegrees)), entitySelectorReader -> entitySelectorReader.getYawRange() == FloatRangeArgument.ANY, new TranslatableText("argument.entity.options.y_rotation.description", new Object[0]));
        EntitySelectorOptions.putOption("limit", entitySelectorReader -> {
            int i = entitySelectorReader.getReader().getCursor();
            int j = entitySelectorReader.getReader().readInt();
            if (j < 1) {
                entitySelectorReader.getReader().setCursor(i);
                throw TOO_SMALL_LEVEL_EXCEPTION.createWithContext((ImmutableStringReader)entitySelectorReader.getReader());
            }
            entitySelectorReader.setLimit(j);
            entitySelectorReader.method_9877(true);
        }, entitySelectorReader -> !entitySelectorReader.isSenderOnly() && !entitySelectorReader.method_9866(), new TranslatableText("argument.entity.options.limit.description", new Object[0]));
        EntitySelectorOptions.putOption("sort", entitySelectorReader -> {
            BiConsumer<Vec3d, List<? extends Entity>> biConsumer;
            int i = entitySelectorReader.getReader().getCursor();
            String string = entitySelectorReader.getReader().readUnquotedString();
            entitySelectorReader.setSuggestionProvider((suggestionsBuilder, consumer) -> CommandSource.suggestMatching(Arrays.asList("nearest", "furthest", "random", "arbitrary"), suggestionsBuilder));
            switch (string) {
                case "nearest": {
                    biConsumer = EntitySelectorReader.NEAREST;
                    break;
                }
                case "furthest": {
                    biConsumer = EntitySelectorReader.FURTHEST;
                    break;
                }
                case "random": {
                    biConsumer = EntitySelectorReader.RANDOM;
                    break;
                }
                case "arbitrary": {
                    biConsumer = EntitySelectorReader.ARBITRARY;
                    break;
                }
                default: {
                    entitySelectorReader.getReader().setCursor(i);
                    throw IRREVERSIBLE_SORT_EXCEPTION.createWithContext((ImmutableStringReader)entitySelectorReader.getReader(), (Object)string);
                }
            }
            entitySelectorReader.setSorter(biConsumer);
            entitySelectorReader.method_9887(true);
        }, entitySelectorReader -> !entitySelectorReader.isSenderOnly() && !entitySelectorReader.method_9889(), new TranslatableText("argument.entity.options.sort.description", new Object[0]));
        EntitySelectorOptions.putOption("gamemode", entitySelectorReader -> {
            entitySelectorReader.setSuggestionProvider((suggestionsBuilder, consumer) -> {
                String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
                boolean bl = !entitySelectorReader.method_9837();
                boolean bl2 = true;
                if (!string.isEmpty()) {
                    if (string.charAt(0) == '!') {
                        bl = false;
                        string = string.substring(1);
                    } else {
                        bl2 = false;
                    }
                }
                for (GameMode gameMode : GameMode.values()) {
                    if (gameMode == GameMode.NOT_SET || !gameMode.getName().toLowerCase(Locale.ROOT).startsWith(string)) continue;
                    if (bl2) {
                        suggestionsBuilder.suggest('!' + gameMode.getName());
                    }
                    if (!bl) continue;
                    suggestionsBuilder.suggest(gameMode.getName());
                }
                return suggestionsBuilder.buildFuture();
            });
            int i = entitySelectorReader.getReader().getCursor();
            boolean bl = entitySelectorReader.readNegationCharacter();
            if (entitySelectorReader.method_9837() && !bl) {
                entitySelectorReader.getReader().setCursor(i);
                throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext((ImmutableStringReader)entitySelectorReader.getReader(), (Object)"gamemode");
            }
            String string = entitySelectorReader.getReader().readUnquotedString();
            GameMode gameMode = GameMode.byName(string, GameMode.NOT_SET);
            if (gameMode == GameMode.NOT_SET) {
                entitySelectorReader.getReader().setCursor(i);
                throw INVALID_MODE_EXCEPTION.createWithContext((ImmutableStringReader)entitySelectorReader.getReader(), (Object)string);
            }
            entitySelectorReader.setIncludesNonPlayers(false);
            entitySelectorReader.setPredicate(entity -> {
                if (!(entity instanceof ServerPlayerEntity)) {
                    return false;
                }
                GameMode gameMode2 = ((ServerPlayerEntity)entity).interactionManager.getGameMode();
                return bl ? gameMode2 != gameMode : gameMode2 == gameMode;
            });
            if (bl) {
                entitySelectorReader.method_9857(true);
            } else {
                entitySelectorReader.method_9890(true);
            }
        }, entitySelectorReader -> !entitySelectorReader.method_9839(), new TranslatableText("argument.entity.options.gamemode.description", new Object[0]));
        EntitySelectorOptions.putOption("team", entitySelectorReader -> {
            boolean bl = entitySelectorReader.readNegationCharacter();
            String string = entitySelectorReader.getReader().readUnquotedString();
            entitySelectorReader.setPredicate(entity -> {
                if (!(entity instanceof LivingEntity)) {
                    return false;
                }
                AbstractTeam abstractTeam = entity.getScoreboardTeam();
                String string2 = abstractTeam == null ? "" : abstractTeam.getName();
                return string2.equals(string) != bl;
            });
            if (bl) {
                entitySelectorReader.method_9833(true);
            } else {
                entitySelectorReader.method_9865(true);
            }
        }, entitySelectorReader -> !entitySelectorReader.method_9904(), new TranslatableText("argument.entity.options.team.description", new Object[0]));
        EntitySelectorOptions.putOption("type", entitySelectorReader -> {
            entitySelectorReader.setSuggestionProvider((suggestionsBuilder, consumer) -> {
                CommandSource.suggestIdentifiers(Registry.ENTITY_TYPE.getIds(), suggestionsBuilder, String.valueOf('!'));
                CommandSource.suggestIdentifiers(EntityTypeTags.getContainer().getKeys(), suggestionsBuilder, "!#");
                if (!entitySelectorReader.method_9910()) {
                    CommandSource.suggestIdentifiers(Registry.ENTITY_TYPE.getIds(), suggestionsBuilder);
                    CommandSource.suggestIdentifiers(EntityTypeTags.getContainer().getKeys(), suggestionsBuilder, String.valueOf('#'));
                }
                return suggestionsBuilder.buildFuture();
            });
            int i = entitySelectorReader.getReader().getCursor();
            boolean bl = entitySelectorReader.readNegationCharacter();
            if (entitySelectorReader.method_9910() && !bl) {
                entitySelectorReader.getReader().setCursor(i);
                throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext((ImmutableStringReader)entitySelectorReader.getReader(), (Object)"type");
            }
            if (bl) {
                entitySelectorReader.method_9860();
            }
            if (entitySelectorReader.readTagCharacter()) {
                Identifier identifier = Identifier.fromCommandInput(entitySelectorReader.getReader());
                Tag<EntityType<?>> tag = EntityTypeTags.getContainer().get(identifier);
                if (tag == null) {
                    entitySelectorReader.getReader().setCursor(i);
                    throw INVALID_TYPE_EXCEPTION.createWithContext((ImmutableStringReader)entitySelectorReader.getReader(), (Object)identifier.toString());
                }
                entitySelectorReader.setPredicate(entity -> tag.contains(entity.getType()) != bl);
            } else {
                Identifier identifier = Identifier.fromCommandInput(entitySelectorReader.getReader());
                EntityType entityType = (EntityType)Registry.ENTITY_TYPE.getOrEmpty(identifier).orElseThrow(() -> {
                    entitySelectorReader.getReader().setCursor(i);
                    return INVALID_TYPE_EXCEPTION.createWithContext((ImmutableStringReader)entitySelectorReader.getReader(), (Object)identifier.toString());
                });
                if (Objects.equals(EntityType.PLAYER, entityType) && !bl) {
                    entitySelectorReader.setIncludesNonPlayers(false);
                }
                entitySelectorReader.setPredicate(entity -> Objects.equals(entityType, entity.getType()) != bl);
                if (!bl) {
                    entitySelectorReader.setEntityType(entityType);
                }
            }
        }, entitySelectorReader -> !entitySelectorReader.selectsEntityType(), new TranslatableText("argument.entity.options.type.description", new Object[0]));
        EntitySelectorOptions.putOption("tag", entitySelectorReader -> {
            boolean bl = entitySelectorReader.readNegationCharacter();
            String string = entitySelectorReader.getReader().readUnquotedString();
            entitySelectorReader.setPredicate(entity -> {
                if ("".equals(string)) {
                    return entity.getScoreboardTags().isEmpty() != bl;
                }
                return entity.getScoreboardTags().contains(string) != bl;
            });
        }, entitySelectorReader -> true, new TranslatableText("argument.entity.options.tag.description", new Object[0]));
        EntitySelectorOptions.putOption("nbt", entitySelectorReader -> {
            boolean bl = entitySelectorReader.readNegationCharacter();
            CompoundTag compoundTag = new StringNbtReader(entitySelectorReader.getReader()).parseCompoundTag();
            entitySelectorReader.setPredicate(entity -> {
                ItemStack itemStack;
                CompoundTag compoundTag2 = entity.toTag(new CompoundTag());
                if (entity instanceof ServerPlayerEntity && !(itemStack = ((ServerPlayerEntity)entity).inventory.getMainHandStack()).isEmpty()) {
                    compoundTag2.put("SelectedItem", itemStack.toTag(new CompoundTag()));
                }
                return NbtHelper.matches(compoundTag, compoundTag2, true) != bl;
            });
        }, entitySelectorReader -> true, new TranslatableText("argument.entity.options.nbt.description", new Object[0]));
        EntitySelectorOptions.putOption("scores", entitySelectorReader -> {
            StringReader stringReader = entitySelectorReader.getReader();
            HashMap map = Maps.newHashMap();
            stringReader.expect('{');
            stringReader.skipWhitespace();
            while (stringReader.canRead() && stringReader.peek() != '}') {
                stringReader.skipWhitespace();
                String string = stringReader.readUnquotedString();
                stringReader.skipWhitespace();
                stringReader.expect('=');
                stringReader.skipWhitespace();
                NumberRange.IntRange intRange = NumberRange.IntRange.parse(stringReader);
                map.put(string, intRange);
                stringReader.skipWhitespace();
                if (!stringReader.canRead() || stringReader.peek() != ',') continue;
                stringReader.skip();
            }
            stringReader.expect('}');
            if (!map.isEmpty()) {
                entitySelectorReader.setPredicate(entity -> {
                    ServerScoreboard scoreboard = entity.getServer().getScoreboard();
                    String string = entity.getEntityName();
                    for (Map.Entry entry : map.entrySet()) {
                        ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective((String)entry.getKey());
                        if (scoreboardObjective == null) {
                            return false;
                        }
                        if (!scoreboard.playerHasObjective(string, scoreboardObjective)) {
                            return false;
                        }
                        ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
                        int i = scoreboardPlayerScore.getScore();
                        if (((NumberRange.IntRange)entry.getValue()).test(i)) continue;
                        return false;
                    }
                    return true;
                });
            }
            entitySelectorReader.method_9848(true);
        }, entitySelectorReader -> !entitySelectorReader.method_9843(), new TranslatableText("argument.entity.options.scores.description", new Object[0]));
        EntitySelectorOptions.putOption("advancements", entitySelectorReader -> {
            StringReader stringReader = entitySelectorReader.getReader();
            HashMap map = Maps.newHashMap();
            stringReader.expect('{');
            stringReader.skipWhitespace();
            while (stringReader.canRead() && stringReader.peek() != '}') {
                stringReader.skipWhitespace();
                Identifier identifier = Identifier.fromCommandInput(stringReader);
                stringReader.skipWhitespace();
                stringReader.expect('=');
                stringReader.skipWhitespace();
                if (stringReader.canRead() && stringReader.peek() == '{') {
                    HashMap map2 = Maps.newHashMap();
                    stringReader.skipWhitespace();
                    stringReader.expect('{');
                    stringReader.skipWhitespace();
                    while (stringReader.canRead() && stringReader.peek() != '}') {
                        stringReader.skipWhitespace();
                        String string = stringReader.readUnquotedString();
                        stringReader.skipWhitespace();
                        stringReader.expect('=');
                        stringReader.skipWhitespace();
                        boolean bl = stringReader.readBoolean();
                        map2.put(string, criterionProgress -> criterionProgress.isObtained() == bl);
                        stringReader.skipWhitespace();
                        if (!stringReader.canRead() || stringReader.peek() != ',') continue;
                        stringReader.skip();
                    }
                    stringReader.skipWhitespace();
                    stringReader.expect('}');
                    stringReader.skipWhitespace();
                    map.put(identifier, advancementProgress -> {
                        for (Map.Entry entry : map2.entrySet()) {
                            CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
                            if (criterionProgress != null && ((Predicate)entry.getValue()).test(criterionProgress)) continue;
                            return false;
                        }
                        return true;
                    });
                } else {
                    boolean bl2 = stringReader.readBoolean();
                    map.put(identifier, advancementProgress -> advancementProgress.isDone() == bl2);
                }
                stringReader.skipWhitespace();
                if (!stringReader.canRead() || stringReader.peek() != ',') continue;
                stringReader.skip();
            }
            stringReader.expect('}');
            if (!map.isEmpty()) {
                entitySelectorReader.setPredicate(entity -> {
                    if (!(entity instanceof ServerPlayerEntity)) {
                        return false;
                    }
                    ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
                    PlayerAdvancementTracker playerAdvancementTracker = serverPlayerEntity.getAdvancementTracker();
                    ServerAdvancementLoader serverAdvancementLoader = serverPlayerEntity.getServer().getAdvancementManager();
                    for (Map.Entry entry : map.entrySet()) {
                        Advancement advancement = serverAdvancementLoader.get((Identifier)entry.getKey());
                        if (advancement != null && ((Predicate)entry.getValue()).test(playerAdvancementTracker.getProgress(advancement))) continue;
                        return false;
                    }
                    return true;
                });
                entitySelectorReader.setIncludesNonPlayers(false);
            }
            entitySelectorReader.method_9906(true);
        }, entitySelectorReader -> !entitySelectorReader.method_9861(), new TranslatableText("argument.entity.options.advancements.description", new Object[0]));
    }

    public static SelectorHandler getHandler(EntitySelectorReader reader, String option, int restoreCursor) throws CommandSyntaxException {
        SelectorOption selectorOption = options.get(option);
        if (selectorOption != null) {
            if (selectorOption.applicable.test(reader)) {
                return selectorOption.handler;
            }
            throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext((ImmutableStringReader)reader.getReader(), (Object)option);
        }
        reader.getReader().setCursor(restoreCursor);
        throw UNKNOWN_OPTION_EXCEPTION.createWithContext((ImmutableStringReader)reader.getReader(), (Object)option);
    }

    public static void suggestOptions(EntitySelectorReader reader, SuggestionsBuilder suggestionBuilder) {
        String string = suggestionBuilder.getRemaining().toLowerCase(Locale.ROOT);
        for (Map.Entry<String, SelectorOption> entry : options.entrySet()) {
            if (!entry.getValue().applicable.test(reader) || !entry.getKey().toLowerCase(Locale.ROOT).startsWith(string)) continue;
            suggestionBuilder.suggest(entry.getKey() + '=', (Message)entry.getValue().description);
        }
    }

    static class SelectorOption {
        public final SelectorHandler handler;
        public final Predicate<EntitySelectorReader> applicable;
        public final Text description;

        private SelectorOption(SelectorHandler selectorHandler, Predicate<EntitySelectorReader> predicate, Text text) {
            this.handler = selectorHandler;
            this.applicable = predicate;
            this.description = text;
        }
    }

    public static interface SelectorHandler {
        public void handle(EntitySelectorReader var1) throws CommandSyntaxException;
    }
}
