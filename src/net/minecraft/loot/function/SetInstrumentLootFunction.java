/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.Instrument;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class SetInstrumentLootFunction
extends ConditionalLootFunction {
    final TagKey<Instrument> options;

    SetInstrumentLootFunction(LootCondition[] conditions, TagKey<Instrument> options) {
        super(conditions);
        this.options = options;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.SET_INSTRUMENT;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        GoatHornItem.setRandomInstrumentFromTag(stack, this.options, context.getRandom());
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder(TagKey<Instrument> options) {
        return SetInstrumentLootFunction.builder((LootCondition[] conditions) -> new SetInstrumentLootFunction((LootCondition[])conditions, options));
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<SetInstrumentLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetInstrumentLootFunction setInstrumentLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, setInstrumentLootFunction, jsonSerializationContext);
            jsonObject.addProperty("options", "#" + setInstrumentLootFunction.options.id());
        }

        @Override
        public SetInstrumentLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            String string = JsonHelper.getString(jsonObject, "options");
            if (!string.startsWith("#")) {
                throw new JsonSyntaxException("Inline tag value not supported: " + string);
            }
            return new SetInstrumentLootFunction(lootConditions, TagKey.of(RegistryKeys.INSTRUMENT, new Identifier(string.substring(1))));
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

