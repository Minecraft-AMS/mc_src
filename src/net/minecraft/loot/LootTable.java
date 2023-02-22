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
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctions;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final LootTable EMPTY = new LootTable(LootContextTypes.EMPTY, new LootPool[0], new LootFunction[0]);
    public static final LootContextType GENERIC = LootContextTypes.GENERIC;
    private final LootContextType type;
    private final LootPool[] pools;
    private final LootFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> combinedFunction;

    private LootTable(LootContextType type, LootPool[] pools, LootFunction[] functions) {
        this.type = type;
        this.pools = pools;
        this.functions = functions;
        this.combinedFunction = LootFunctions.join(functions);
    }

    public static Consumer<ItemStack> limitedConsumer(Consumer<ItemStack> itemDropper) {
        return stack -> {
            if (stack.getCount() < stack.getMaxCount()) {
                itemDropper.accept((ItemStack)stack);
            } else {
                ItemStack itemStack;
                for (int i = stack.getCount(); i > 0; i -= itemStack.getCount()) {
                    itemStack = stack.copy();
                    itemStack.setCount(Math.min(stack.getMaxCount(), i));
                    itemDropper.accept(itemStack);
                }
            }
        };
    }

    public void drop(LootContext context, Consumer<ItemStack> itemDropper) {
        if (context.addDrop(this)) {
            Consumer<ItemStack> consumer = LootFunction.apply(this.combinedFunction, itemDropper, context);
            for (LootPool lootPool : this.pools) {
                lootPool.drop(consumer, context);
            }
            context.removeDrop(this);
        } else {
            LOGGER.warn("Detected infinite loop in loot tables");
        }
    }

    public void dropLimited(LootContext context, Consumer<ItemStack> dropItemConsumer) {
        this.drop(context, LootTable.limitedConsumer(dropItemConsumer));
    }

    public List<ItemStack> getDrops(LootContext context) {
        ArrayList list = Lists.newArrayList();
        this.dropLimited(context, list::add);
        return list;
    }

    public LootContextType getType() {
        return this.type;
    }

    public void check(LootTableReporter reporter, Function<Identifier, LootTable> supplierGetter, Set<Identifier> parentLootTables, LootContextType contextType) {
        int i;
        for (i = 0; i < this.pools.length; ++i) {
            this.pools[i].check(reporter.makeChild(".pools[" + i + "]"), supplierGetter, parentLootTables, contextType);
        }
        for (i = 0; i < this.functions.length; ++i) {
            this.functions[i].check(reporter.makeChild(".functions[" + i + "]"), supplierGetter, parentLootTables, contextType);
        }
    }

    public void supplyInventory(Inventory inventory, LootContext context) {
        List<ItemStack> list = this.getDrops(context);
        Random random = context.getRandom();
        List<Integer> list2 = this.getFreeSlots(inventory, random);
        this.shuffle(list, list2.size(), random);
        for (ItemStack itemStack : list) {
            if (list2.isEmpty()) {
                LOGGER.warn("Tried to over-fill a container");
                return;
            }
            if (itemStack.isEmpty()) {
                inventory.setInvStack(list2.remove(list2.size() - 1), ItemStack.EMPTY);
                continue;
            }
            inventory.setInvStack(list2.remove(list2.size() - 1), itemStack);
        }
    }

    private void shuffle(List<ItemStack> drops, int freeSlots, Random random) {
        ArrayList list = Lists.newArrayList();
        Iterator<ItemStack> iterator = drops.iterator();
        while (iterator.hasNext()) {
            ItemStack itemStack = iterator.next();
            if (itemStack.isEmpty()) {
                iterator.remove();
                continue;
            }
            if (itemStack.getCount() <= 1) continue;
            list.add(itemStack);
            iterator.remove();
        }
        while (freeSlots - drops.size() - list.size() > 0 && !list.isEmpty()) {
            ItemStack itemStack2 = (ItemStack)list.remove(MathHelper.nextInt(random, 0, list.size() - 1));
            int i = MathHelper.nextInt(random, 1, itemStack2.getCount() / 2);
            ItemStack itemStack3 = itemStack2.split(i);
            if (itemStack2.getCount() > 1 && random.nextBoolean()) {
                list.add(itemStack2);
            } else {
                drops.add(itemStack2);
            }
            if (itemStack3.getCount() > 1 && random.nextBoolean()) {
                list.add(itemStack3);
                continue;
            }
            drops.add(itemStack3);
        }
        drops.addAll(list);
        Collections.shuffle(drops, random);
    }

    private List<Integer> getFreeSlots(Inventory inventory, Random random) {
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i < inventory.getInvSize(); ++i) {
            if (!inventory.getInvStack(i).isEmpty()) continue;
            list.add(i);
        }
        Collections.shuffle(list, random);
        return list;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Serializer
    implements JsonDeserializer<LootTable>,
    JsonSerializer<LootTable> {
        public LootTable deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "loot table");
            LootPool[] lootPools = JsonHelper.deserialize(jsonObject, "pools", new LootPool[0], jsonDeserializationContext, LootPool[].class);
            LootContextType lootContextType = null;
            if (jsonObject.has("type")) {
                String string = JsonHelper.getString(jsonObject, "type");
                lootContextType = LootContextTypes.get(new Identifier(string));
            }
            LootFunction[] lootFunctions = JsonHelper.deserialize(jsonObject, "functions", new LootFunction[0], jsonDeserializationContext, LootFunction[].class);
            return new LootTable(lootContextType != null ? lootContextType : LootContextTypes.GENERIC, lootPools, lootFunctions);
        }

        public JsonElement serialize(LootTable lootTable, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            if (lootTable.type != GENERIC) {
                Identifier identifier = LootContextTypes.getId(lootTable.type);
                if (identifier != null) {
                    jsonObject.addProperty("type", identifier.toString());
                } else {
                    LOGGER.warn("Failed to find id for param set " + lootTable.type);
                }
            }
            if (lootTable.pools.length > 0) {
                jsonObject.add("pools", jsonSerializationContext.serialize((Object)lootTable.pools));
            }
            if (!ArrayUtils.isEmpty((Object[])lootTable.functions)) {
                jsonObject.add("functions", jsonSerializationContext.serialize((Object)lootTable.functions));
            }
            return jsonObject;
        }

        public /* synthetic */ JsonElement serialize(Object supplier, Type unused, JsonSerializationContext context) {
            return this.serialize((LootTable)supplier, unused, context);
        }

        public /* synthetic */ Object deserialize(JsonElement json, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(json, unused, context);
        }
    }

    public static class Builder
    implements LootFunctionConsumingBuilder<Builder> {
        private final List<LootPool> pools = Lists.newArrayList();
        private final List<LootFunction> functions = Lists.newArrayList();
        private LootContextType type = GENERIC;

        public Builder withPool(LootPool.Builder poolBuilder) {
            this.pools.add(poolBuilder.build());
            return this;
        }

        public Builder withType(LootContextType context) {
            this.type = context;
            return this;
        }

        @Override
        public Builder withFunction(LootFunction.Builder builder) {
            this.functions.add(builder.build());
            return this;
        }

        @Override
        public Builder getThis() {
            return this;
        }

        public LootTable create() {
            return new LootTable(this.type, this.pools.toArray(new LootPool[0]), this.functions.toArray(new LootFunction[0]));
        }

        @Override
        public /* synthetic */ Object getThis() {
            return this.getThis();
        }

        @Override
        public /* synthetic */ Object withFunction(LootFunction.Builder lootFunctionBuilder) {
            return this.withFunction(lootFunctionBuilder);
        }
    }
}

