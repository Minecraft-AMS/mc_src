/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.mojang.logging.LogUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class LootFunctionManager
extends JsonDataLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = LootGsons.getFunctionGsonBuilder().create();
    private final LootConditionManager lootConditionManager;
    private final LootManager lootManager;
    private Map<Identifier, LootFunction> functions = ImmutableMap.of();

    public LootFunctionManager(LootConditionManager lootConditionManager, LootManager lootManager) {
        super(GSON, "item_modifiers");
        this.lootConditionManager = lootConditionManager;
        this.lootManager = lootManager;
    }

    @Nullable
    public LootFunction get(Identifier id) {
        return this.functions.get(id);
    }

    public LootFunction getOrDefault(Identifier id, LootFunction fallback) {
        return this.functions.getOrDefault(id, fallback);
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        map.forEach((id, json) -> {
            try {
                if (json.isJsonArray()) {
                    LootFunction[] lootFunctions = (LootFunction[])GSON.fromJson(json, LootFunction[].class);
                    builder.put(id, (Object)new AndFunction(lootFunctions));
                } else {
                    LootFunction lootFunction = (LootFunction)GSON.fromJson(json, LootFunction.class);
                    builder.put(id, (Object)lootFunction);
                }
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't parse item modifier {}", id, (Object)exception);
            }
        });
        ImmutableMap map2 = builder.build();
        LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, this.lootConditionManager::get, this.lootManager::getTable);
        map2.forEach((id, function) -> function.validate(lootTableReporter));
        lootTableReporter.getMessages().forEach((name, message) -> LOGGER.warn("Found item modifier validation problem in {}: {}", name, message));
        this.functions = map2;
    }

    public Set<Identifier> getFunctionIds() {
        return Collections.unmodifiableSet(this.functions.keySet());
    }

    static class AndFunction
    implements LootFunction {
        protected final LootFunction[] functions;
        private final BiFunction<ItemStack, LootContext, ItemStack> applier;

        public AndFunction(LootFunction[] functions) {
            this.functions = functions;
            this.applier = LootFunctionTypes.join(functions);
        }

        @Override
        public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
            return this.applier.apply(itemStack, lootContext);
        }

        @Override
        public LootFunctionType getType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public /* synthetic */ Object apply(Object stack, Object context) {
            return this.apply((ItemStack)stack, (LootContext)context);
        }
    }
}

