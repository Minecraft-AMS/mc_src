/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.LootTableRanges;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctions;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.loot.condition.LootCondition;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool {
    private final LootEntry[] entries;
    private final LootCondition[] conditions;
    private final Predicate<LootContext> predicate;
    private final LootFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> javaFunctions;
    private final LootTableRange rollsRange;
    private final UniformLootTableRange bonusRollsRange;

    private LootPool(LootEntry[] entries, LootCondition[] conditions, LootFunction[] functions, LootTableRange rollsRange, UniformLootTableRange bonusRollsRange) {
        this.entries = entries;
        this.conditions = conditions;
        this.predicate = LootConditions.joinAnd(conditions);
        this.functions = functions;
        this.javaFunctions = LootFunctions.join(functions);
        this.rollsRange = rollsRange;
        this.bonusRollsRange = bonusRollsRange;
    }

    private void supplyOnce(Consumer<ItemStack> itemDropper, LootContext context) {
        Random random = context.getRandom();
        ArrayList list = Lists.newArrayList();
        MutableInt mutableInt = new MutableInt();
        for (LootEntry lootEntry : this.entries) {
            lootEntry.expand(context, choice -> {
                int i = choice.getWeight(context.getLuck());
                if (i > 0) {
                    list.add(choice);
                    mutableInt.add(i);
                }
            });
        }
        int i = list.size();
        if (mutableInt.intValue() == 0 || i == 0) {
            return;
        }
        if (i == 1) {
            ((LootChoice)list.get(0)).drop(itemDropper, context);
            return;
        }
        int j = random.nextInt(mutableInt.intValue());
        for (LootChoice lootChoice : list) {
            if ((j -= lootChoice.getWeight(context.getLuck())) >= 0) continue;
            lootChoice.drop(itemDropper, context);
            return;
        }
    }

    public void drop(Consumer<ItemStack> itemDropper, LootContext context) {
        if (!this.predicate.test(context)) {
            return;
        }
        Consumer<ItemStack> consumer = LootFunction.apply(this.javaFunctions, itemDropper, context);
        Random random = context.getRandom();
        int i = this.rollsRange.next(random) + MathHelper.floor(this.bonusRollsRange.nextFloat(random) * context.getLuck());
        for (int j = 0; j < i; ++j) {
            this.supplyOnce(consumer, context);
        }
    }

    public void check(LootTableReporter reporter, Function<Identifier, LootTable> supplierGetter, Set<Identifier> parentLootTables, LootContextType contextType) {
        int i;
        for (i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].check(reporter.makeChild(".condition[" + i + "]"), supplierGetter, parentLootTables, contextType);
        }
        for (i = 0; i < this.functions.length; ++i) {
            this.functions[i].check(reporter.makeChild(".functions[" + i + "]"), supplierGetter, parentLootTables, contextType);
        }
        for (i = 0; i < this.entries.length; ++i) {
            this.entries[i].check(reporter.makeChild(".entries[" + i + "]"), supplierGetter, parentLootTables, contextType);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Serializer
    implements JsonDeserializer<LootPool>,
    JsonSerializer<LootPool> {
        public LootPool deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "loot pool");
            LootEntry[] lootEntrys = JsonHelper.deserialize(jsonObject, "entries", jsonDeserializationContext, LootEntry[].class);
            LootCondition[] lootConditions = JsonHelper.deserialize(jsonObject, "conditions", new LootCondition[0], jsonDeserializationContext, LootCondition[].class);
            LootFunction[] lootFunctions = JsonHelper.deserialize(jsonObject, "functions", new LootFunction[0], jsonDeserializationContext, LootFunction[].class);
            LootTableRange lootTableRange = LootTableRanges.fromJson(jsonObject.get("rolls"), jsonDeserializationContext);
            UniformLootTableRange uniformLootTableRange = JsonHelper.deserialize(jsonObject, "bonus_rolls", new UniformLootTableRange(0.0f, 0.0f), jsonDeserializationContext, UniformLootTableRange.class);
            return new LootPool(lootEntrys, lootConditions, lootFunctions, lootTableRange, uniformLootTableRange);
        }

        public JsonElement serialize(LootPool lootPool, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("rolls", LootTableRanges.toJson(lootPool.rollsRange, jsonSerializationContext));
            jsonObject.add("entries", jsonSerializationContext.serialize((Object)lootPool.entries));
            if (lootPool.bonusRollsRange.getMinValue() != 0.0f && lootPool.bonusRollsRange.getMaxValue() != 0.0f) {
                jsonObject.add("bonus_rolls", jsonSerializationContext.serialize((Object)lootPool.bonusRollsRange));
            }
            if (!ArrayUtils.isEmpty((Object[])lootPool.conditions)) {
                jsonObject.add("conditions", jsonSerializationContext.serialize((Object)lootPool.conditions));
            }
            if (!ArrayUtils.isEmpty((Object[])lootPool.functions)) {
                jsonObject.add("functions", jsonSerializationContext.serialize((Object)lootPool.functions));
            }
            return jsonObject;
        }

        public /* synthetic */ JsonElement serialize(Object entry, Type unused, JsonSerializationContext context) {
            return this.serialize((LootPool)entry, unused, context);
        }

        public /* synthetic */ Object deserialize(JsonElement json, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(json, unused, context);
        }
    }

    public static class Builder
    implements LootFunctionConsumingBuilder<Builder>,
    LootConditionConsumingBuilder<Builder> {
        private final List<LootEntry> entries = Lists.newArrayList();
        private final List<LootCondition> conditions = Lists.newArrayList();
        private final List<LootFunction> functions = Lists.newArrayList();
        private LootTableRange rollsRange = new UniformLootTableRange(1.0f);
        private UniformLootTableRange bonusRollsRange = new UniformLootTableRange(0.0f, 0.0f);

        public Builder withRolls(LootTableRange rollsRange) {
            this.rollsRange = rollsRange;
            return this;
        }

        @Override
        public Builder getThis() {
            return this;
        }

        public Builder withEntry(LootEntry.Builder<?> entryBuilder) {
            this.entries.add(entryBuilder.build());
            return this;
        }

        @Override
        public Builder withCondition(LootCondition.Builder builder) {
            this.conditions.add(builder.build());
            return this;
        }

        @Override
        public Builder withFunction(LootFunction.Builder builder) {
            this.functions.add(builder.build());
            return this;
        }

        public LootPool build() {
            if (this.rollsRange == null) {
                throw new IllegalArgumentException("Rolls not set");
            }
            return new LootPool(this.entries.toArray(new LootEntry[0]), this.conditions.toArray(new LootCondition[0]), this.functions.toArray(new LootFunction[0]), this.rollsRange, this.bonusRollsRange);
        }

        @Override
        public /* synthetic */ Object getThis() {
            return this.getThis();
        }

        @Override
        public /* synthetic */ Object withFunction(LootFunction.Builder lootFunctionBuilder) {
            return this.withFunction(lootFunctionBuilder);
        }

        @Override
        public /* synthetic */ Object withCondition(LootCondition.Builder builder) {
            return this.withCondition(builder);
        }
    }
}

