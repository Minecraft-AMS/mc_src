/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Doubles
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
package net.minecraft.command;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorOptions;
import net.minecraft.command.FloatRangeArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class EntitySelectorReader {
    public static final SimpleCommandExceptionType INVALID_ENTITY_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.entity.invalid", new Object[0]));
    public static final DynamicCommandExceptionType UNKNOWN_SELECTOR_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.entity.selector.unknown", object));
    public static final SimpleCommandExceptionType NOT_ALLOWED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.entity.selector.not_allowed", new Object[0]));
    public static final SimpleCommandExceptionType MISSING_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.entity.selector.missing", new Object[0]));
    public static final SimpleCommandExceptionType UNTERMINATED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.entity.options.unterminated", new Object[0]));
    public static final DynamicCommandExceptionType VALUELESS_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.entity.options.valueless", object));
    public static final BiConsumer<Vec3d, List<? extends Entity>> ARBITRARY = (vec3d, list) -> {};
    public static final BiConsumer<Vec3d, List<? extends Entity>> NEAREST = (vec3d, list) -> list.sort((entity, entity2) -> Doubles.compare((double)entity.squaredDistanceTo((Vec3d)vec3d), (double)entity2.squaredDistanceTo((Vec3d)vec3d)));
    public static final BiConsumer<Vec3d, List<? extends Entity>> FURTHEST = (vec3d, list) -> list.sort((entity, entity2) -> Doubles.compare((double)entity2.squaredDistanceTo((Vec3d)vec3d), (double)entity.squaredDistanceTo((Vec3d)vec3d)));
    public static final BiConsumer<Vec3d, List<? extends Entity>> RANDOM = (vec3d, list) -> Collections.shuffle(list);
    public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> DEFAULT_SUGGESTION_PROVIDER = (suggestionsBuilder, consumer) -> suggestionsBuilder.buildFuture();
    private final StringReader reader;
    private final boolean field_10846;
    private int limit;
    private boolean includesNonPlayers;
    private boolean localWorldOnly;
    private NumberRange.FloatRange distance = NumberRange.FloatRange.ANY;
    private NumberRange.IntRange levelRange = NumberRange.IntRange.ANY;
    @Nullable
    private Double x;
    @Nullable
    private Double y;
    @Nullable
    private Double z;
    @Nullable
    private Double dx;
    @Nullable
    private Double dy;
    @Nullable
    private Double dz;
    private FloatRangeArgument pitchRange = FloatRangeArgument.ANY;
    private FloatRangeArgument yawRange = FloatRangeArgument.ANY;
    private Predicate<Entity> predicate = entity -> true;
    private BiConsumer<Vec3d, List<? extends Entity>> sorter = ARBITRARY;
    private boolean senderOnly;
    @Nullable
    private String playerName;
    private int startCursor;
    @Nullable
    private UUID uuid;
    private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestionProvider = DEFAULT_SUGGESTION_PROVIDER;
    private boolean field_10854;
    private boolean field_10874;
    private boolean field_10851;
    private boolean field_10873;
    private boolean field_10849;
    private boolean field_10871;
    private boolean field_10845;
    private boolean field_10868;
    @Nullable
    private EntityType<?> entityType;
    private boolean field_10865;
    private boolean field_10841;
    private boolean field_10864;
    private boolean usesAt;

    public EntitySelectorReader(StringReader stringReader) {
        this(stringReader, true);
    }

    public EntitySelectorReader(StringReader stringReader, boolean bl) {
        this.reader = stringReader;
        this.field_10846 = bl;
    }

    public EntitySelector build() {
        Box box;
        if (this.dx != null || this.dy != null || this.dz != null) {
            box = this.createBox(this.dx == null ? 0.0 : this.dx, this.dy == null ? 0.0 : this.dy, this.dz == null ? 0.0 : this.dz);
        } else if (this.distance.getMax() != null) {
            float f = ((Float)this.distance.getMax()).floatValue();
            box = new Box(-f, -f, -f, f + 1.0f, f + 1.0f, f + 1.0f);
        } else {
            box = null;
        }
        Function<Vec3d, Vec3d> function = this.x == null && this.y == null && this.z == null ? vec3d -> vec3d : vec3d -> new Vec3d(this.x == null ? vec3d.x : this.x, this.y == null ? vec3d.y : this.y, this.z == null ? vec3d.z : this.z);
        return new EntitySelector(this.limit, this.includesNonPlayers, this.localWorldOnly, this.predicate, this.distance, function, box, this.sorter, this.senderOnly, this.playerName, this.uuid, this.entityType, this.usesAt);
    }

    private Box createBox(double x, double y, double z) {
        boolean bl = x < 0.0;
        boolean bl2 = y < 0.0;
        boolean bl3 = z < 0.0;
        double d = bl ? x : 0.0;
        double e = bl2 ? y : 0.0;
        double f = bl3 ? z : 0.0;
        double g = (bl ? 0.0 : x) + 1.0;
        double h = (bl2 ? 0.0 : y) + 1.0;
        double i = (bl3 ? 0.0 : z) + 1.0;
        return new Box(d, e, f, g, h, i);
    }

    private void buildPredicate() {
        if (this.pitchRange != FloatRangeArgument.ANY) {
            this.predicate = this.predicate.and(this.rotationPredicate(this.pitchRange, entity -> entity.pitch));
        }
        if (this.yawRange != FloatRangeArgument.ANY) {
            this.predicate = this.predicate.and(this.rotationPredicate(this.yawRange, entity -> entity.yaw));
        }
        if (!this.levelRange.isDummy()) {
            this.predicate = this.predicate.and(entity -> {
                if (!(entity instanceof ServerPlayerEntity)) {
                    return false;
                }
                return this.levelRange.test(((ServerPlayerEntity)entity).experienceLevel);
            });
        }
    }

    private Predicate<Entity> rotationPredicate(FloatRangeArgument floatRangeArgument, ToDoubleFunction<Entity> toDoubleFunction) {
        double d = MathHelper.wrapDegrees(floatRangeArgument.getMin() == null ? 0.0f : floatRangeArgument.getMin().floatValue());
        double e = MathHelper.wrapDegrees(floatRangeArgument.getMax() == null ? 359.0f : floatRangeArgument.getMax().floatValue());
        return entity -> {
            double f = MathHelper.wrapDegrees(toDoubleFunction.applyAsDouble((Entity)entity));
            if (d > e) {
                return f >= d || f <= e;
            }
            return f >= d && f <= e;
        };
    }

    protected void readAtVariable() throws CommandSyntaxException {
        this.usesAt = true;
        this.suggestionProvider = this::suggestSelectorRest;
        if (!this.reader.canRead()) {
            throw MISSING_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
        }
        int i = this.reader.getCursor();
        char c = this.reader.read();
        if (c == 'p') {
            this.limit = 1;
            this.includesNonPlayers = false;
            this.sorter = NEAREST;
            this.setEntityType(EntityType.PLAYER);
        } else if (c == 'a') {
            this.limit = Integer.MAX_VALUE;
            this.includesNonPlayers = false;
            this.sorter = ARBITRARY;
            this.setEntityType(EntityType.PLAYER);
        } else if (c == 'r') {
            this.limit = 1;
            this.includesNonPlayers = false;
            this.sorter = RANDOM;
            this.setEntityType(EntityType.PLAYER);
        } else if (c == 's') {
            this.limit = 1;
            this.includesNonPlayers = true;
            this.senderOnly = true;
        } else if (c == 'e') {
            this.limit = Integer.MAX_VALUE;
            this.includesNonPlayers = true;
            this.sorter = ARBITRARY;
            this.predicate = Entity::isAlive;
        } else {
            this.reader.setCursor(i);
            throw UNKNOWN_SELECTOR_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, (Object)('@' + String.valueOf(c)));
        }
        this.suggestionProvider = this::suggestOpen;
        if (this.reader.canRead() && this.reader.peek() == '[') {
            this.reader.skip();
            this.suggestionProvider = this::suggestOptionOrEnd;
            this.readArguments();
        }
    }

    protected void readRegular() throws CommandSyntaxException {
        if (this.reader.canRead()) {
            this.suggestionProvider = this::suggestNormal;
        }
        int i = this.reader.getCursor();
        String string = this.reader.readString();
        try {
            this.uuid = UUID.fromString(string);
            this.includesNonPlayers = true;
        }
        catch (IllegalArgumentException illegalArgumentException) {
            if (string.isEmpty() || string.length() > 16) {
                this.reader.setCursor(i);
                throw INVALID_ENTITY_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
            }
            this.includesNonPlayers = false;
            this.playerName = string;
        }
        this.limit = 1;
    }

    protected void readArguments() throws CommandSyntaxException {
        this.suggestionProvider = this::suggestOption;
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int i = this.reader.getCursor();
            String string = this.reader.readString();
            EntitySelectorOptions.SelectorHandler selectorHandler = EntitySelectorOptions.getHandler(this, string, i);
            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
                this.reader.setCursor(i);
                throw VALUELESS_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, (Object)string);
            }
            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestionProvider = DEFAULT_SUGGESTION_PROVIDER;
            selectorHandler.handle(this);
            this.reader.skipWhitespace();
            this.suggestionProvider = this::suggestEndNext;
            if (!this.reader.canRead()) continue;
            if (this.reader.peek() == ',') {
                this.reader.skip();
                this.suggestionProvider = this::suggestOption;
                continue;
            }
            if (this.reader.peek() == ']') break;
            throw UNTERMINATED_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
        }
        if (!this.reader.canRead()) {
            throw UNTERMINATED_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
        }
        this.reader.skip();
        this.suggestionProvider = DEFAULT_SUGGESTION_PROVIDER;
    }

    public boolean readNegationCharacter() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == '!') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }
        return false;
    }

    public boolean readTagCharacter() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }
        return false;
    }

    public StringReader getReader() {
        return this.reader;
    }

    public void setPredicate(Predicate<Entity> predicate) {
        this.predicate = this.predicate.and(predicate);
    }

    public void setLocalWorldOnly() {
        this.localWorldOnly = true;
    }

    public NumberRange.FloatRange getDistance() {
        return this.distance;
    }

    public void setDistance(NumberRange.FloatRange distance) {
        this.distance = distance;
    }

    public NumberRange.IntRange getLevelRange() {
        return this.levelRange;
    }

    public void setLevelRange(NumberRange.IntRange experienceRange) {
        this.levelRange = experienceRange;
    }

    public FloatRangeArgument getPitchRange() {
        return this.pitchRange;
    }

    public void setPitchRange(FloatRangeArgument floatRangeArgument) {
        this.pitchRange = floatRangeArgument;
    }

    public FloatRangeArgument getYawRange() {
        return this.yawRange;
    }

    public void setYawRange(FloatRangeArgument floatRangeArgument) {
        this.yawRange = floatRangeArgument;
    }

    @Nullable
    public Double getX() {
        return this.x;
    }

    @Nullable
    public Double getY() {
        return this.y;
    }

    @Nullable
    public Double getZ() {
        return this.z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public void setDz(double dz) {
        this.dz = dz;
    }

    @Nullable
    public Double getDx() {
        return this.dx;
    }

    @Nullable
    public Double getDy() {
        return this.dy;
    }

    @Nullable
    public Double getDz() {
        return this.dz;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setIncludesNonPlayers(boolean includesNonPlayers) {
        this.includesNonPlayers = includesNonPlayers;
    }

    public void setSorter(BiConsumer<Vec3d, List<? extends Entity>> sorter) {
        this.sorter = sorter;
    }

    public EntitySelector read() throws CommandSyntaxException {
        this.startCursor = this.reader.getCursor();
        this.suggestionProvider = this::suggestSelector;
        if (this.reader.canRead() && this.reader.peek() == '@') {
            if (!this.field_10846) {
                throw NOT_ALLOWED_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
            }
            this.reader.skip();
            this.readAtVariable();
        } else {
            this.readRegular();
        }
        this.buildPredicate();
        return this.build();
    }

    private static void suggestSelector(SuggestionsBuilder builder) {
        builder.suggest("@p", (Message)new TranslatableText("argument.entity.selector.nearestPlayer", new Object[0]));
        builder.suggest("@a", (Message)new TranslatableText("argument.entity.selector.allPlayers", new Object[0]));
        builder.suggest("@r", (Message)new TranslatableText("argument.entity.selector.randomPlayer", new Object[0]));
        builder.suggest("@s", (Message)new TranslatableText("argument.entity.selector.self", new Object[0]));
        builder.suggest("@e", (Message)new TranslatableText("argument.entity.selector.allEntities", new Object[0]));
    }

    private CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        consumer.accept(builder);
        if (this.field_10846) {
            EntitySelectorReader.suggestSelector(builder);
        }
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestNormal(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        SuggestionsBuilder suggestionsBuilder = builder.createOffset(this.startCursor);
        consumer.accept(suggestionsBuilder);
        return builder.add(suggestionsBuilder).buildFuture();
    }

    private CompletableFuture<Suggestions> suggestSelectorRest(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        SuggestionsBuilder suggestionsBuilder = builder.createOffset(builder.getStart() - 1);
        EntitySelectorReader.suggestSelector(suggestionsBuilder);
        builder.add(suggestionsBuilder);
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOpen(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        builder.suggest(String.valueOf('['));
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOptionOrEnd(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        builder.suggest(String.valueOf(']'));
        EntitySelectorOptions.suggestOptions(this, builder);
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOption(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        EntitySelectorOptions.suggestOptions(this, builder);
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestEndNext(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        builder.suggest(String.valueOf(','));
        builder.suggest(String.valueOf(']'));
        return builder.buildFuture();
    }

    public boolean isSenderOnly() {
        return this.senderOnly;
    }

    public void setSuggestionProvider(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> biFunction) {
        this.suggestionProvider = biFunction;
    }

    public CompletableFuture<Suggestions> listSuggestions(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        return this.suggestionProvider.apply(builder.createOffset(this.reader.getCursor()), consumer);
    }

    public boolean method_9912() {
        return this.field_10854;
    }

    public void method_9899(boolean bl) {
        this.field_10854 = bl;
    }

    public boolean method_9844() {
        return this.field_10874;
    }

    public void method_9913(boolean bl) {
        this.field_10874 = bl;
    }

    public boolean method_9866() {
        return this.field_10851;
    }

    public void method_9877(boolean bl) {
        this.field_10851 = bl;
    }

    public boolean method_9889() {
        return this.field_10873;
    }

    public void method_9887(boolean bl) {
        this.field_10873 = bl;
    }

    public boolean method_9839() {
        return this.field_10849;
    }

    public void method_9890(boolean bl) {
        this.field_10849 = bl;
    }

    public boolean method_9837() {
        return this.field_10871;
    }

    public void method_9857(boolean bl) {
        this.field_10871 = bl;
    }

    public boolean method_9904() {
        return this.field_10845;
    }

    public void method_9865(boolean bl) {
        this.field_10845 = bl;
    }

    public void method_9833(boolean bl) {
        this.field_10868 = bl;
    }

    public void setEntityType(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    public void method_9860() {
        this.field_10865 = true;
    }

    public boolean selectsEntityType() {
        return this.entityType != null;
    }

    public boolean method_9910() {
        return this.field_10865;
    }

    public boolean method_9843() {
        return this.field_10841;
    }

    public void method_9848(boolean bl) {
        this.field_10841 = bl;
    }

    public boolean method_9861() {
        return this.field_10864;
    }

    public void method_9906(boolean bl) {
        this.field_10864 = bl;
    }
}
