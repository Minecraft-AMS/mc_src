/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.google.gson.JsonSyntaxException
 */
package net.minecraft.loot.function;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.CopyNameLootFunction;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.ExplorationMapLootFunction;
import net.minecraft.loot.function.ExplosionDecayLootFunction;
import net.minecraft.loot.function.FillPlayerHeadLootFunction;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.loot.function.LimitCountLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetAttributesLootFunction;
import net.minecraft.loot.function.SetContentsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.function.SetLootTableLootFunction;
import net.minecraft.loot.function.SetLoreLootFunction;
import net.minecraft.loot.function.SetNameLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.function.SetStewEffectLootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LootFunctions {
    private static final Map<Identifier, LootFunction.Factory<?>> byId = Maps.newHashMap();
    private static final Map<Class<? extends LootFunction>, LootFunction.Factory<?>> byClass = Maps.newHashMap();
    public static final BiFunction<ItemStack, LootContext, ItemStack> NOOP = (stack, context) -> stack;

    public static <T extends LootFunction> void register(LootFunction.Factory<? extends T> function) {
        Identifier identifier = function.getId();
        Class<T> class_ = function.getFunctionClass();
        if (byId.containsKey(identifier)) {
            throw new IllegalArgumentException("Can't re-register item function name " + identifier);
        }
        if (byClass.containsKey(class_)) {
            throw new IllegalArgumentException("Can't re-register item function class " + class_.getName());
        }
        byId.put(identifier, function);
        byClass.put(class_, function);
    }

    public static LootFunction.Factory<?> get(Identifier id) {
        LootFunction.Factory<?> factory = byId.get(id);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown loot item function '" + id + "'");
        }
        return factory;
    }

    public static <T extends LootFunction> LootFunction.Factory<T> getFactory(T function) {
        LootFunction.Factory<?> factory = byClass.get(function.getClass());
        if (factory == null) {
            throw new IllegalArgumentException("Unknown loot item function " + function);
        }
        return factory;
    }

    public static BiFunction<ItemStack, LootContext, ItemStack> join(BiFunction<ItemStack, LootContext, ItemStack>[] lootFunctions) {
        switch (lootFunctions.length) {
            case 0: {
                return NOOP;
            }
            case 1: {
                return lootFunctions[0];
            }
            case 2: {
                BiFunction<ItemStack, LootContext, ItemStack> biFunction = lootFunctions[0];
                BiFunction<ItemStack, LootContext, ItemStack> biFunction2 = lootFunctions[1];
                return (stack, context) -> (ItemStack)biFunction2.apply((ItemStack)biFunction.apply((ItemStack)stack, (LootContext)context), (LootContext)context);
            }
        }
        return (stack, context) -> {
            for (BiFunction biFunction : lootFunctions) {
                stack = (ItemStack)biFunction.apply(stack, context);
            }
            return stack;
        };
    }

    static {
        LootFunctions.register(new SetCountLootFunction.Factory());
        LootFunctions.register(new EnchantWithLevelsLootFunction.Factory());
        LootFunctions.register(new EnchantRandomlyLootFunction.Factory());
        LootFunctions.register(new SetNbtLootFunction.Builder());
        LootFunctions.register(new FurnaceSmeltLootFunction.Factory());
        LootFunctions.register(new LootingEnchantLootFunction.Factory());
        LootFunctions.register(new SetDamageLootFunction.Factory());
        LootFunctions.register(new SetAttributesLootFunction.Factory());
        LootFunctions.register(new SetNameLootFunction.Factory());
        LootFunctions.register(new ExplorationMapLootFunction.Factory());
        LootFunctions.register(new SetStewEffectLootFunction.Factory());
        LootFunctions.register(new CopyNameLootFunction.Factory());
        LootFunctions.register(new SetContentsLootFunction.Factory());
        LootFunctions.register(new LimitCountLootFunction.Factory());
        LootFunctions.register(new ApplyBonusLootFunction.Factory());
        LootFunctions.register(new SetLootTableLootFunction.Factory());
        LootFunctions.register(new ExplosionDecayLootFunction.Factory());
        LootFunctions.register(new SetLoreLootFunction.Factory());
        LootFunctions.register(new FillPlayerHeadLootFunction.Factory());
        LootFunctions.register(new CopyNbtLootFunction.Factory());
    }

    public static class Factory
    implements JsonDeserializer<LootFunction>,
    JsonSerializer<LootFunction> {
        public LootFunction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            LootFunction.Factory<?> factory;
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "function");
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "function"));
            try {
                factory = LootFunctions.get(identifier);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                throw new JsonSyntaxException("Unknown function '" + identifier + "'");
            }
            return factory.fromJson(jsonObject, jsonDeserializationContext);
        }

        public JsonElement serialize(LootFunction lootFunction, Type type, JsonSerializationContext jsonSerializationContext) {
            LootFunction.Factory<LootFunction> factory = LootFunctions.getFactory(lootFunction);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("function", factory.getId().toString());
            factory.toJson(jsonObject, lootFunction, jsonSerializationContext);
            return jsonObject;
        }

        public /* synthetic */ JsonElement serialize(Object function, Type unused, JsonSerializationContext context) {
            return this.serialize((LootFunction)function, unused, context);
        }

        public /* synthetic */ Object deserialize(JsonElement json, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(json, unused, context);
        }
    }
}

