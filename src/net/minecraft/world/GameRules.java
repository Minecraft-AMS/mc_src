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
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Comparator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class GameRules {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<RuleKey<?>, RuleType<?>> RULE_TYPES = Maps.newTreeMap(Comparator.comparing(key -> RuleKey.method_20772(key)));
    public static final RuleKey<BooleanRule> DO_FIRE_TICK = GameRules.register("doFireTick", BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> MOB_GRIEFING = GameRules.register("mobGriefing", BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> KEEP_INVENTORY = GameRules.register("keepInventory", BooleanRule.method_20755(false));
    public static final RuleKey<BooleanRule> DO_MOB_SPAWNING = GameRules.register("doMobSpawning", BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_MOB_LOOT = GameRules.register("doMobLoot", BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_TILE_DROPS = GameRules.register("doTileDrops", BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_ENTITY_DROPS = GameRules.register("doEntityDrops", BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> COMMAND_BLOCK_OUTPUT = GameRules.register("commandBlockOutput", BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> NATURAL_REGENERATION = GameRules.register("naturalRegeneration", BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_DAYLIGHT_CYCLE = GameRules.register("doDaylightCycle", BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> LOG_ADMIN_COMMANDS = GameRules.register("logAdminCommands", BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> SHOW_DEATH_MESSAGES = GameRules.register("showDeathMessages", BooleanRule.method_20755(true));
    public static final RuleKey<IntRule> RANDOM_TICK_SPEED = GameRules.register("randomTickSpeed", IntRule.method_20764(3));
    public static final RuleKey<BooleanRule> SEND_COMMAND_FEEDBACK = GameRules.register("sendCommandFeedback", BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> REDUCED_DEBUG_INFO = GameRules.register("reducedDebugInfo", BooleanRule.method_20757(false, (server, rule) -> {
        byte b = rule.get() ? (byte)22 : (byte)23;
        for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
            serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, b));
        }
    }));
    public static final RuleKey<BooleanRule> SPECTATORS_GENERATE_CHUNKS = GameRules.register("spectatorsGenerateChunks", BooleanRule.method_20755(true));
    public static final RuleKey<IntRule> SPAWN_RADIUS = GameRules.register("spawnRadius", IntRule.method_20764(10));
    public static final RuleKey<BooleanRule> DISABLE_ELYTRA_MOVEMENT_CHECK = GameRules.register("disableElytraMovementCheck", BooleanRule.method_20755(false));
    public static final RuleKey<IntRule> MAX_ENTITY_CRAMMING = GameRules.register("maxEntityCramming", IntRule.method_20764(24));
    public static final RuleKey<BooleanRule> DO_WEATHER_CYCLE = GameRules.register("doWeatherCycle", BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_LIMITED_CRAFTING = GameRules.register("doLimitedCrafting", BooleanRule.method_20755(false));
    public static final RuleKey<IntRule> MAX_COMMAND_CHAIN_LENGTH = GameRules.register("maxCommandChainLength", IntRule.method_20764(65536));
    public static final RuleKey<BooleanRule> ANNOUNCE_ADVANCEMENTS = GameRules.register("announceAdvancements", BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DISABLE_RAIDS = GameRules.register("disableRaids", BooleanRule.method_20755(false));
    private final Map<RuleKey<?>, Rule<?>> rules = (Map)RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, e -> ((RuleType)e.getValue()).newRule()));

    private static <T extends Rule<T>> RuleKey<T> register(String name, RuleType<T> type) {
        RuleKey ruleKey = new RuleKey(name);
        RuleType<T> ruleType = RULE_TYPES.put(ruleKey, type);
        if (ruleType != null) {
            throw new IllegalStateException("Duplicate game rule registration for " + name);
        }
        return ruleKey;
    }

    public <T extends Rule<T>> T get(RuleKey<T> key) {
        return (T)this.rules.get(key);
    }

    public CompoundTag toNbt() {
        CompoundTag compoundTag = new CompoundTag();
        this.rules.forEach((key, rule) -> compoundTag.putString(((RuleKey)key).name, rule.valueToString()));
        return compoundTag;
    }

    public void load(CompoundTag nbt) {
        this.rules.forEach((key, rule) -> rule.setFromString(nbt.getString(((RuleKey)key).name)));
    }

    public static void forEachType(RuleConsumer action) {
        RULE_TYPES.forEach((key, type) -> GameRules.accept(action, key, type));
    }

    private static <T extends Rule<T>> void accept(RuleConsumer consumer, RuleKey<?> key, RuleType<?> type) {
        RuleKey<?> ruleKey = key;
        RuleType<?> ruleType = type;
        consumer.accept(ruleKey, ruleType);
    }

    public boolean getBoolean(RuleKey<BooleanRule> rule) {
        return this.get(rule).get();
    }

    public int getInt(RuleKey<IntRule> rule) {
        return this.get(rule).get();
    }

    public static class BooleanRule
    extends Rule<BooleanRule> {
        private boolean value;

        private static RuleType<BooleanRule> of(boolean value, BiConsumer<MinecraftServer, BooleanRule> notifier) {
            return new RuleType<BooleanRule>(BoolArgumentType::bool, type -> new BooleanRule((RuleType<BooleanRule>)type, value), notifier);
        }

        private static RuleType<BooleanRule> of(boolean value) {
            return BooleanRule.of(value, (server, rule) -> {});
        }

        public BooleanRule(RuleType<BooleanRule> type, boolean value) {
            super(type);
            this.value = value;
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
            this.notify(server);
        }

        @Override
        protected String valueToString() {
            return Boolean.toString(this.value);
        }

        @Override
        protected void setFromString(String value) {
            this.value = Boolean.parseBoolean(value);
        }

        @Override
        public int toCommandResult() {
            return this.value ? 1 : 0;
        }

        @Override
        protected BooleanRule getThis() {
            return this;
        }

        @Override
        protected /* synthetic */ Rule getThis() {
            return this.getThis();
        }

        static /* synthetic */ RuleType method_20755(boolean bl) {
            return BooleanRule.of(bl);
        }

        static /* synthetic */ RuleType method_20757(boolean bl, BiConsumer biConsumer) {
            return BooleanRule.of(bl, biConsumer);
        }
    }

    public static class IntRule
    extends Rule<IntRule> {
        private int value;

        private static RuleType<IntRule> of(int value, BiConsumer<MinecraftServer, IntRule> notifier) {
            return new RuleType<IntRule>(IntegerArgumentType::integer, type -> new IntRule((RuleType<IntRule>)type, value), notifier);
        }

        private static RuleType<IntRule> of(int value) {
            return IntRule.of(value, (server, rule) -> {});
        }

        public IntRule(RuleType<IntRule> rule, int value) {
            super(rule);
            this.value = value;
        }

        @Override
        protected void setFromArgument(CommandContext<ServerCommandSource> context, String name) {
            this.value = IntegerArgumentType.getInteger(context, (String)name);
        }

        public int get() {
            return this.value;
        }

        @Override
        protected String valueToString() {
            return Integer.toString(this.value);
        }

        @Override
        protected void setFromString(String value) {
            this.value = IntRule.parseInt(value);
        }

        private static int parseInt(String string) {
            if (!string.isEmpty()) {
                try {
                    return Integer.parseInt(string);
                }
                catch (NumberFormatException numberFormatException) {
                    LOGGER.warn("Failed to parse integer {}", (Object)string);
                }
            }
            return 0;
        }

        @Override
        public int toCommandResult() {
            return this.value;
        }

        @Override
        protected IntRule getThis() {
            return this;
        }

        @Override
        protected /* synthetic */ Rule getThis() {
            return this.getThis();
        }

        static /* synthetic */ RuleType method_20764(int i) {
            return IntRule.of(i);
        }
    }

    public static abstract class Rule<T extends Rule<T>> {
        private final RuleType<T> type;

        public Rule(RuleType<T> type) {
            this.type = type;
        }

        protected abstract void setFromArgument(CommandContext<ServerCommandSource> var1, String var2);

        public void set(CommandContext<ServerCommandSource> context, String name) {
            this.setFromArgument(context, name);
            this.notify(((ServerCommandSource)context.getSource()).getMinecraftServer());
        }

        protected void notify(@Nullable MinecraftServer server) {
            if (server != null) {
                ((RuleType)this.type).notifier.accept(server, this.getThis());
            }
        }

        protected abstract void setFromString(String var1);

        protected abstract String valueToString();

        public String toString() {
            return this.valueToString();
        }

        public abstract int toCommandResult();

        protected abstract T getThis();
    }

    public static class RuleType<T extends Rule<T>> {
        private final Supplier<ArgumentType<?>> argumentType;
        private final Function<RuleType<T>, T> factory;
        private final BiConsumer<MinecraftServer, T> notifier;

        private RuleType(Supplier<ArgumentType<?>> argumentType, Function<RuleType<T>, T> factory, BiConsumer<MinecraftServer, T> notifier) {
            this.argumentType = argumentType;
            this.factory = factory;
            this.notifier = notifier;
        }

        public RequiredArgumentBuilder<ServerCommandSource, ?> argument(String name) {
            return CommandManager.argument(name, this.argumentType.get());
        }

        public T newRule() {
            return (T)((Rule)this.factory.apply(this));
        }
    }

    public static final class RuleKey<T extends Rule<T>> {
        private final String name;

        public RuleKey(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            return obj instanceof RuleKey && ((RuleKey)obj).name.equals(this.name);
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public String getName() {
            return this.name;
        }
    }

    @FunctionalInterface
    public static interface RuleConsumer {
        public <T extends Rule<T>> void accept(RuleKey<T> var1, RuleType<T> var2);
    }
}

