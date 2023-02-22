/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.entry;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.condition.LootCondition;

public abstract class LootEntry
implements EntryCombiner {
    protected final LootCondition[] conditions;
    private final Predicate<LootContext> conditionPredicate;

    protected LootEntry(LootCondition[] conditions) {
        this.conditions = conditions;
        this.conditionPredicate = LootConditions.joinAnd(conditions);
    }

    public void check(LootTableReporter reporter, Function<Identifier, LootTable> supplierGetter, Set<Identifier> parentLootTables, LootContextType contextType) {
        for (int i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].check(reporter.makeChild(".condition[" + i + "]"), supplierGetter, parentLootTables, contextType);
        }
    }

    protected final boolean test(LootContext context) {
        return this.conditionPredicate.test(context);
    }

    public static abstract class Serializer<T extends LootEntry> {
        private final Identifier id;
        private final Class<T> type;

        protected Serializer(Identifier id, Class<T> type) {
            this.id = id;
            this.type = type;
        }

        public Identifier getIdentifier() {
            return this.id;
        }

        public Class<T> getType() {
            return this.type;
        }

        public abstract void toJson(JsonObject var1, T var2, JsonSerializationContext var3);

        public abstract T fromJson(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3);
    }

    public static abstract class Builder<T extends Builder<T>>
    implements LootConditionConsumingBuilder<T> {
        private final List<LootCondition> children = Lists.newArrayList();

        protected abstract T getThisBuilder();

        @Override
        public T withCondition(LootCondition.Builder builder) {
            this.children.add(builder.build());
            return this.getThisBuilder();
        }

        @Override
        public final T getThis() {
            return this.getThisBuilder();
        }

        protected LootCondition[] getConditions() {
            return this.children.toArray(new LootCondition[0]);
        }

        public AlternativeEntry.Builder withChild(Builder<?> builder) {
            return new AlternativeEntry.Builder(this, builder);
        }

        public abstract LootEntry build();

        @Override
        public /* synthetic */ Object getThis() {
            return this.getThis();
        }

        @Override
        public /* synthetic */ Object withCondition(LootCondition.Builder builder) {
            return this.withCondition(builder);
        }
    }
}

