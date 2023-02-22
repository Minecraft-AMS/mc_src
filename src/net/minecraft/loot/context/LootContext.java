/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.google.gson.TypeAdapter
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.loot.context;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class LootContext {
    private final Random random;
    private final float luck;
    private final ServerWorld world;
    private final LootManager manager;
    private final Set<LootTable> suppliers = Sets.newLinkedHashSet();
    private final Map<LootContextParameter<?>, Object> parameters;
    private final Map<Identifier, Dropper> drops;

    private LootContext(Random random, float luck, ServerWorld world, LootManager manager, Map<LootContextParameter<?>, Object> parameters, Map<Identifier, Dropper> drops) {
        this.random = random;
        this.luck = luck;
        this.world = world;
        this.manager = manager;
        this.parameters = ImmutableMap.copyOf(parameters);
        this.drops = ImmutableMap.copyOf(drops);
    }

    public boolean hasParameter(LootContextParameter<?> parameter) {
        return this.parameters.containsKey(parameter);
    }

    public void drop(Identifier id, Consumer<ItemStack> itemDropper) {
        Dropper dropper = this.drops.get(id);
        if (dropper != null) {
            dropper.add(this, itemDropper);
        }
    }

    @Nullable
    public <T> T get(LootContextParameter<T> parameter) {
        return (T)this.parameters.get(parameter);
    }

    public boolean addDrop(LootTable supplier) {
        return this.suppliers.add(supplier);
    }

    public void removeDrop(LootTable supplier) {
        this.suppliers.remove(supplier);
    }

    public LootManager getLootManager() {
        return this.manager;
    }

    public Random getRandom() {
        return this.random;
    }

    public float getLuck() {
        return this.luck;
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    public static enum EntityTarget {
        THIS("this", LootContextParameters.THIS_ENTITY),
        KILLER("killer", LootContextParameters.KILLER_ENTITY),
        DIRECT_KILLER("direct_killer", LootContextParameters.DIRECT_KILLER_ENTITY),
        KILLER_PLAYER("killer_player", LootContextParameters.LAST_DAMAGE_PLAYER);

        private final String type;
        private final LootContextParameter<? extends Entity> identifier;

        private EntityTarget(String type, LootContextParameter<? extends Entity> parameter) {
            this.type = type;
            this.identifier = parameter;
        }

        public LootContextParameter<? extends Entity> getIdentifier() {
            return this.identifier;
        }

        public static EntityTarget fromString(String type) {
            for (EntityTarget entityTarget : EntityTarget.values()) {
                if (!entityTarget.type.equals(type)) continue;
                return entityTarget;
            }
            throw new IllegalArgumentException("Invalid entity target " + type);
        }

        public static class Serializer
        extends TypeAdapter<EntityTarget> {
            public void write(JsonWriter jsonWriter, EntityTarget entityTarget) throws IOException {
                jsonWriter.value(entityTarget.type);
            }

            public EntityTarget read(JsonReader jsonReader) throws IOException {
                return EntityTarget.fromString(jsonReader.nextString());
            }

            public /* synthetic */ Object read(JsonReader reader) throws IOException {
                return this.read(reader);
            }

            public /* synthetic */ void write(JsonWriter writer, Object entity) throws IOException {
                this.write(writer, (EntityTarget)((Object)entity));
            }
        }
    }

    public static class Builder {
        private final ServerWorld world;
        private final Map<LootContextParameter<?>, Object> parameters = Maps.newIdentityHashMap();
        private final Map<Identifier, Dropper> drops = Maps.newHashMap();
        private Random random;
        private float luck;

        public Builder(ServerWorld world) {
            this.world = world;
        }

        public Builder setRandom(Random random) {
            this.random = random;
            return this;
        }

        public Builder setRandom(long seed) {
            if (seed != 0L) {
                this.random = new Random(seed);
            }
            return this;
        }

        public Builder setRandom(long seed, Random random) {
            this.random = seed == 0L ? random : new Random(seed);
            return this;
        }

        public Builder setLuck(float luck) {
            this.luck = luck;
            return this;
        }

        public <T> Builder put(LootContextParameter<T> key, T value) {
            this.parameters.put(key, value);
            return this;
        }

        public <T> Builder putNullable(LootContextParameter<T> key, @Nullable T value) {
            if (value == null) {
                this.parameters.remove(key);
            } else {
                this.parameters.put(key, value);
            }
            return this;
        }

        public Builder putDrop(Identifier id, Dropper value) {
            Dropper dropper = this.drops.put(id, value);
            if (dropper != null) {
                throw new IllegalStateException("Duplicated dynamic drop '" + this.drops + "'");
            }
            return this;
        }

        public ServerWorld getWorld() {
            return this.world;
        }

        public <T> T get(LootContextParameter<T> parameter) {
            Object object = this.parameters.get(parameter);
            if (object == null) {
                throw new IllegalArgumentException("No parameter " + parameter);
            }
            return (T)object;
        }

        @Nullable
        public <T> T getNullable(LootContextParameter<T> parameter) {
            return (T)this.parameters.get(parameter);
        }

        public LootContext build(LootContextType type) {
            Sets.SetView set = Sets.difference(this.parameters.keySet(), type.getAllowed());
            if (!set.isEmpty()) {
                throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + set);
            }
            Sets.SetView set2 = Sets.difference(type.getRequired(), this.parameters.keySet());
            if (!set2.isEmpty()) {
                throw new IllegalArgumentException("Missing required parameters: " + set2);
            }
            Random random = this.random;
            if (random == null) {
                random = new Random();
            }
            return new LootContext(random, this.luck, this.world, this.world.getServer().getLootManager(), this.parameters, this.drops);
        }
    }

    @FunctionalInterface
    public static interface Dropper {
        public void add(LootContext var1, Consumer<ItemStack> var2);
    }
}

