/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicLike
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicLike;
import java.util.Comparator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class GameRules {
    public static final int DEFAULT_RANDOM_TICK_SPEED = 3;
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Key<?>, Type<?>> RULE_TYPES = Maps.newTreeMap(Comparator.comparing(key -> key.name));
    public static final Key<BooleanRule> DO_FIRE_TICK = GameRules.register("doFireTick", Category.UPDATES, BooleanRule.create(true));
    public static final Key<BooleanRule> DO_MOB_GRIEFING = GameRules.register("mobGriefing", Category.MOBS, BooleanRule.create(true));
    public static final Key<BooleanRule> KEEP_INVENTORY = GameRules.register("keepInventory", Category.PLAYER, BooleanRule.create(false));
    public static final Key<BooleanRule> DO_MOB_SPAWNING = GameRules.register("doMobSpawning", Category.SPAWNING, BooleanRule.create(true));
    public static final Key<BooleanRule> DO_MOB_LOOT = GameRules.register("doMobLoot", Category.DROPS, BooleanRule.create(true));
    public static final Key<BooleanRule> DO_TILE_DROPS = GameRules.register("doTileDrops", Category.DROPS, BooleanRule.create(true));
    public static final Key<BooleanRule> DO_ENTITY_DROPS = GameRules.register("doEntityDrops", Category.DROPS, BooleanRule.create(true));
    public static final Key<BooleanRule> COMMAND_BLOCK_OUTPUT = GameRules.register("commandBlockOutput", Category.CHAT, BooleanRule.create(true));
    public static final Key<BooleanRule> NATURAL_REGENERATION = GameRules.register("naturalRegeneration", Category.PLAYER, BooleanRule.create(true));
    public static final Key<BooleanRule> DO_DAYLIGHT_CYCLE = GameRules.register("doDaylightCycle", Category.UPDATES, BooleanRule.create(true));
    public static final Key<BooleanRule> LOG_ADMIN_COMMANDS = GameRules.register("logAdminCommands", Category.CHAT, BooleanRule.create(true));
    public static final Key<BooleanRule> SHOW_DEATH_MESSAGES = GameRules.register("showDeathMessages", Category.CHAT, BooleanRule.create(true));
    public static final Key<IntRule> RANDOM_TICK_SPEED = GameRules.register("randomTickSpeed", Category.UPDATES, IntRule.create(3));
    public static final Key<BooleanRule> SEND_COMMAND_FEEDBACK = GameRules.register("sendCommandFeedback", Category.CHAT, BooleanRule.create(true));
    public static final Key<BooleanRule> REDUCED_DEBUG_INFO = GameRules.register("reducedDebugInfo", Category.MISC, BooleanRule.create(false, (server, rule) -> {
        byte b = rule.get() ? (byte)22 : (byte)23;
        for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
            serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, b));
        }
    }));
    public static final Key<BooleanRule> SPECTATORS_GENERATE_CHUNKS = GameRules.register("spectatorsGenerateChunks", Category.PLAYER, BooleanRule.create(true));
    public static final Key<IntRule> SPAWN_RADIUS = GameRules.register("spawnRadius", Category.PLAYER, IntRule.create(10));
    public static final Key<BooleanRule> DISABLE_ELYTRA_MOVEMENT_CHECK = GameRules.register("disableElytraMovementCheck", Category.PLAYER, BooleanRule.create(false));
    public static final Key<IntRule> MAX_ENTITY_CRAMMING = GameRules.register("maxEntityCramming", Category.MOBS, IntRule.create(24));
    public static final Key<BooleanRule> DO_WEATHER_CYCLE = GameRules.register("doWeatherCycle", Category.UPDATES, BooleanRule.create(true));
    public static final Key<BooleanRule> DO_LIMITED_CRAFTING = GameRules.register("doLimitedCrafting", Category.PLAYER, BooleanRule.create(false));
    public static final Key<IntRule> MAX_COMMAND_CHAIN_LENGTH = GameRules.register("maxCommandChainLength", Category.MISC, IntRule.create(65536));
    public static final Key<BooleanRule> ANNOUNCE_ADVANCEMENTS = GameRules.register("announceAdvancements", Category.CHAT, BooleanRule.create(true));
    public static final Key<BooleanRule> DISABLE_RAIDS = GameRules.register("disableRaids", Category.MOBS, BooleanRule.create(false));
    public static final Key<BooleanRule> DO_INSOMNIA = GameRules.register("doInsomnia", Category.SPAWNING, BooleanRule.create(true));
    public static final Key<BooleanRule> DO_IMMEDIATE_RESPAWN = GameRules.register("doImmediateRespawn", Category.PLAYER, BooleanRule.create(false, (server, rule) -> {
        for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
            serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.IMMEDIATE_RESPAWN, rule.get() ? 1.0f : 0.0f));
        }
    }));
    public static final Key<BooleanRule> DROWNING_DAMAGE = GameRules.register("drowningDamage", Category.PLAYER, BooleanRule.create(true));
    public static final Key<BooleanRule> FALL_DAMAGE = GameRules.register("fallDamage", Category.PLAYER, BooleanRule.create(true));
    public static final Key<BooleanRule> FIRE_DAMAGE = GameRules.register("fireDamage", Category.PLAYER, BooleanRule.create(true));
    public static final Key<BooleanRule> FREEZE_DAMAGE = GameRules.register("freezeDamage", Category.PLAYER, BooleanRule.create(true));
    public static final Key<BooleanRule> DO_PATROL_SPAWNING = GameRules.register("doPatrolSpawning", Category.SPAWNING, BooleanRule.create(true));
    public static final Key<BooleanRule> DO_TRADER_SPAWNING = GameRules.register("doTraderSpawning", Category.SPAWNING, BooleanRule.create(true));
    public static final Key<BooleanRule> FORGIVE_DEAD_PLAYERS = GameRules.register("forgiveDeadPlayers", Category.MOBS, BooleanRule.create(true));
    public static final Key<BooleanRule> UNIVERSAL_ANGER = GameRules.register("universalAnger", Category.MOBS, BooleanRule.create(false));
    public static final Key<IntRule> PLAYERS_SLEEPING_PERCENTAGE = GameRules.register("playersSleepingPercentage", Category.PLAYER, IntRule.create(100));
    private final Map<Key<?>, Rule<?>> rules;

    private static <T extends Rule<T>> Key<T> register(String name, Category category, Type<T> type) {
        Key key = new Key(name, category);
        Type<T> type2 = RULE_TYPES.put(key, type);
        if (type2 != null) {
            throw new IllegalStateException("Duplicate game rule registration for " + name);
        }
        return key;
    }

    public GameRules(DynamicLike<?> dynamicLike) {
        this();
        this.load(dynamicLike);
    }

    public GameRules() {
        this.rules = (Map)RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, e -> ((Type)e.getValue()).createRule()));
    }

    private GameRules(Map<Key<?>, Rule<?>> rules) {
        this.rules = rules;
    }

    public <T extends Rule<T>> T get(Key<T> key) {
        return (T)this.rules.get(key);
    }

    public NbtCompound toNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        this.rules.forEach((key, rule) -> nbtCompound.putString(key.name, rule.serialize()));
        return nbtCompound;
    }

    private void load(DynamicLike<?> dynamicLike) {
        this.rules.forEach((key, rule) -> dynamicLike.get(key.name).asString().result().ifPresent(rule::deserialize));
    }

    public GameRules copy() {
        return new GameRules((Map)this.rules.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((Rule)entry.getValue()).copy())));
    }

    public static void accept(Visitor visitor) {
        RULE_TYPES.forEach((key, type) -> GameRules.accept(visitor, key, type));
    }

    private static <T extends Rule<T>> void accept(Visitor consumer, Key<?> key, Type<?> type) {
        Key<?> key2 = key;
        Type<?> type2 = type;
        consumer.visit(key2, type2);
        type2.accept(consumer, key2);
    }

    public void setAllValues(GameRules rules, @Nullable MinecraftServer server) {
        rules.rules.keySet().forEach(key -> this.setValue((Key)key, rules, server));
    }

    private <T extends Rule<T>> void setValue(Key<T> key, GameRules rules, @Nullable MinecraftServer server) {
        T rule = rules.get(key);
        ((Rule)this.get(key)).setValue(rule, server);
    }

    public boolean getBoolean(Key<BooleanRule> rule) {
        return this.get(rule).get();
    }

    public int getInt(Key<IntRule> rule) {
        return this.get(rule).get();
    }

    public static final class Key<T extends Rule<T>> {
        final String name;
        private final Category category;

        public Key(String name, Category category) {
            this.name = name;
            this.category = category;
        }

        public String toString() {
            return this.name;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            return o instanceof Key && ((Key)o).name.equals(this.name);
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public String getName() {
            return this.name;
        }

        public String getTranslationKey() {
            return "gamerule." + this.name;
        }

        public Category getCategory() {
            return this.category;
        }
    }

    public static final class Category
    extends Enum<Category> {
        public static final /* enum */ Category PLAYER = new Category("gamerule.category.player");
        public static final /* enum */ Category MOBS = new Category("gamerule.category.mobs");
        public static final /* enum */ Category SPAWNING = new Category("gamerule.category.spawning");
        public static final /* enum */ Category DROPS = new Category("gamerule.category.drops");
        public static final /* enum */ Category UPDATES = new Category("gamerule.category.updates");
        public static final /* enum */ Category CHAT = new Category("gamerule.category.chat");
        public static final /* enum */ Category MISC = new Category("gamerule.category.misc");
        private final String category;
        private static final /* synthetic */ Category[] field_24102;

        public static Category[] values() {
            return (Category[])field_24102.clone();
        }

        public static Category valueOf(String string) {
            return Enum.valueOf(Category.class, string);
        }

        private Category(String category) {
            this.category = category;
        }

        public String getCategory() {
            return this.category;
        }

        private static /* synthetic */ Category[] method_36694() {
            return new Category[]{PLAYER, MOBS, SPAWNING, DROPS, UPDATES, CHAT, MISC};
        }

        static {
            field_24102 = Category.method_36694();
        }
    }

    public static class Type<T extends Rule<T>> {
        private final Supplier<ArgumentType<?>> argumentType;
        private final Function<Type<T>, T> ruleFactory;
        final BiConsumer<MinecraftServer, T> changeCallback;
        private final Acceptor<T> ruleAcceptor;

        Type(Supplier<ArgumentType<?>> argumentType, Function<Type<T>, T> ruleFactory, BiConsumer<MinecraftServer, T> changeCallback, Acceptor<T> ruleAcceptor) {
            this.argumentType = argumentType;
            this.ruleFactory = ruleFactory;
            this.changeCallback = changeCallback;
            this.ruleAcceptor = ruleAcceptor;
        }

        public RequiredArgumentBuilder<ServerCommandSource, ?> argument(String name) {
            return CommandManager.argument(name, this.argumentType.get());
        }

        public T createRule() {
            return (T)((Rule)this.ruleFactory.apply(this));
        }

        public void accept(Visitor consumer, Key<T> key) {
            this.ruleAcceptor.call(consumer, key, this);
        }
    }

    public static abstract class Rule<T extends Rule<T>> {
        protected final Type<T> type;

        public Rule(Type<T> type) {
            this.type = type;
        }

        protected abstract void setFromArgument(CommandContext<ServerCommandSource> var1, String var2);

        public void set(CommandContext<ServerCommandSource> context, String name) {
            this.setFromArgument(context, name);
            this.changed(((ServerCommandSource)context.getSource()).getServer());
        }

        protected void changed(@Nullable MinecraftServer server) {
            if (server != null) {
                this.type.changeCallback.accept(server, (MinecraftServer)this.getThis());
            }
        }

        protected abstract void deserialize(String var1);

        public abstract String serialize();

        public String toString() {
            return this.serialize();
        }

        public abstract int getCommandResult();

        protected abstract T getThis();

        protected abstract T copy();

        public abstract void setValue(T var1, @Nullable MinecraftServer var2);
    }

    public static interface Visitor {
        default public <T extends Rule<T>> void visit(Key<T> key, Type<T> type) {
        }

        default public void visitBoolean(Key<BooleanRule> key, Type<BooleanRule> type) {
        }

        default public void visitInt(Key<IntRule> key, Type<IntRule> type) {
        }
    }

    public static class BooleanRule
    extends Rule<BooleanRule> {
        private boolean value;

        static Type<BooleanRule> create(boolean initialValue, BiConsumer<MinecraftServer, BooleanRule> changeCallback) {
            return new Type<BooleanRule>(BoolArgumentType::bool, type -> new BooleanRule((Type<BooleanRule>)type, initialValue), changeCallback, Visitor::visitBoolean);
        }

        static Type<BooleanRule> create(boolean initialValue) {
            return BooleanRule.create(initialValue, (server, rule) -> {});
        }

        public BooleanRule(Type<BooleanRule> type, boolean initialValue) {
            super(type);
            this.value = initialValue;
        }

        @Override
        protected void setFromArgument(CommandContext<ServerCommandSource> context, String name) {
            this.value = BoolArgumentType.getBool(context, (String)name);
        }

        public boolean get() {
            return this.value;
        }

        public void set(boolean value, @Nullable MinecraftServer server) {
            this.value = value;
            this.changed(server);
        }

        @Override
        public String serialize() {
            return Boolean.toString(this.value);
        }

        @Override
        protected void deserialize(String value) {
            this.value = Boolean.parseBoolean(value);
        }

        @Override
        public int getCommandResult() {
            return this.value ? 1 : 0;
        }

        @Override
        protected BooleanRule getThis() {
            return this;
        }

        @Override
        protected BooleanRule copy() {
            return new BooleanRule(this.type, this.value);
        }

        @Override
        public void setValue(BooleanRule booleanRule, @Nullable MinecraftServer minecraftServer) {
            this.value = booleanRule.value;
            this.changed(minecraftServer);
        }

        @Override
        protected /* synthetic */ Rule copy() {
            return this.copy();
        }

        @Override
        protected /* synthetic */ Rule getThis() {
            return this.getThis();
        }
    }

    public static class IntRule
    extends Rule<IntRule> {
        private int value;

        private static Type<IntRule> create(int initialValue, BiConsumer<MinecraftServer, IntRule> changeCallback) {
            return new Type<IntRule>(IntegerArgumentType::integer, type -> new IntRule((Type<IntRule>)type, initialValue), changeCallback, Visitor::visitInt);
        }

        static Type<IntRule> create(int initialValue) {
            return IntRule.create(initialValue, (server, rule) -> {});
        }

        public IntRule(Type<IntRule> rule, int initialValue) {
            super(rule);
            this.value = initialValue;
        }

        @Override
        protected void setFromArgument(CommandContext<ServerCommandSource> context, String name) {
            this.value = IntegerArgumentType.getInteger(context, (String)name);
        }

        public int get() {
            return this.value;
        }

        public void set(int value, @Nullable MinecraftServer server) {
            this.value = value;
            this.changed(server);
        }

        @Override
        public String serialize() {
            return Integer.toString(this.value);
        }

        @Override
        protected void deserialize(String value) {
            this.value = IntRule.parseInt(value);
        }

        public boolean validate(String input) {
            try {
                this.value = Integer.parseInt(input);
                return true;
            }
            catch (NumberFormatException numberFormatException) {
                return false;
            }
        }

        private static int parseInt(String input) {
            if (!input.isEmpty()) {
                try {
                    return Integer.parseInt(input);
                }
                catch (NumberFormatException numberFormatException) {
                    LOGGER.warn("Failed to parse integer {}", (Object)input);
                }
            }
            return 0;
        }

        @Override
        public int getCommandResult() {
            return this.value;
        }

        @Override
        protected IntRule getThis() {
            return this;
        }

        @Override
        protected IntRule copy() {
            return new IntRule(this.type, this.value);
        }

        @Override
        public void setValue(IntRule intRule, @Nullable MinecraftServer minecraftServer) {
            this.value = intRule.value;
            this.changed(minecraftServer);
        }

        @Override
        protected /* synthetic */ Rule copy() {
            return this.copy();
        }

        @Override
        protected /* synthetic */ Rule getThis() {
            return this.getThis();
        }
    }

    static interface Acceptor<T extends Rule<T>> {
        public void call(Visitor var1, Key<T> var2, Type<T> var3);
    }
}

