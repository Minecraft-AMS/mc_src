/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;
import org.jetbrains.annotations.Nullable;

public class SetAttributesLootFunction
extends ConditionalLootFunction {
    private final List<Attribute> attributes;

    private SetAttributesLootFunction(LootCondition[] conditions, List<Attribute> attributes) {
        super(conditions);
        this.attributes = ImmutableList.copyOf(attributes);
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        Random random = context.getRandom();
        for (Attribute attribute : this.attributes) {
            UUID uUID = attribute.id;
            if (uUID == null) {
                uUID = UUID.randomUUID();
            }
            EquipmentSlot equipmentSlot = attribute.slots[random.nextInt(attribute.slots.length)];
            stack.addAttributeModifier(attribute.attribute, new EntityAttributeModifier(uUID, attribute.name, (double)attribute.amountRange.nextFloat(random), attribute.operation), equipmentSlot);
        }
        return stack;
    }

    static class Attribute {
        private final String name;
        private final String attribute;
        private final EntityAttributeModifier.Operation operation;
        private final UniformLootTableRange amountRange;
        @Nullable
        private final UUID id;
        private final EquipmentSlot[] slots;

        private Attribute(String name, String attribute, EntityAttributeModifier.Operation operation, UniformLootTableRange amountRange, EquipmentSlot[] slots, @Nullable UUID id) {
            this.name = name;
            this.attribute = attribute;
            this.operation = operation;
            this.amountRange = amountRange;
            this.id = id;
            this.slots = slots;
        }

        public JsonObject serialize(JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", this.name);
            jsonObject.addProperty("attribute", this.attribute);
            jsonObject.addProperty("operation", Attribute.getName(this.operation));
            jsonObject.add("amount", context.serialize((Object)this.amountRange));
            if (this.id != null) {
                jsonObject.addProperty("id", this.id.toString());
            }
            if (this.slots.length == 1) {
                jsonObject.addProperty("slot", this.slots[0].getName());
            } else {
                JsonArray jsonArray = new JsonArray();
                for (EquipmentSlot equipmentSlot : this.slots) {
                    jsonArray.add((JsonElement)new JsonPrimitive(equipmentSlot.getName()));
                }
                jsonObject.add("slot", (JsonElement)jsonArray);
            }
            return jsonObject;
        }

        public static Attribute deserialize(JsonObject json, JsonDeserializationContext context) {
            EquipmentSlot[] equipmentSlots;
            String string = JsonHelper.getString(json, "name");
            String string2 = JsonHelper.getString(json, "attribute");
            EntityAttributeModifier.Operation operation = Attribute.fromName(JsonHelper.getString(json, "operation"));
            UniformLootTableRange uniformLootTableRange = JsonHelper.deserialize(json, "amount", context, UniformLootTableRange.class);
            UUID uUID = null;
            if (JsonHelper.hasString(json, "slot")) {
                equipmentSlots = new EquipmentSlot[]{EquipmentSlot.byName(JsonHelper.getString(json, "slot"))};
            } else if (JsonHelper.hasArray(json, "slot")) {
                JsonArray jsonArray = JsonHelper.getArray(json, "slot");
                equipmentSlots = new EquipmentSlot[jsonArray.size()];
                int i = 0;
                for (JsonElement jsonElement : jsonArray) {
                    equipmentSlots[i++] = EquipmentSlot.byName(JsonHelper.asString(jsonElement, "slot"));
                }
                if (equipmentSlots.length == 0) {
                    throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
                }
            } else {
                throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
            }
            if (json.has("id")) {
                String string3 = JsonHelper.getString(json, "id");
                try {
                    uUID = UUID.fromString(string3);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    throw new JsonSyntaxException("Invalid attribute modifier id '" + string3 + "' (must be UUID format, with dashes)");
                }
            }
            return new Attribute(string, string2, operation, uniformLootTableRange, equipmentSlots, uUID);
        }

        private static String getName(EntityAttributeModifier.Operation operation) {
            switch (operation) {
                case ADDITION: {
                    return "addition";
                }
                case MULTIPLY_BASE: {
                    return "multiply_base";
                }
                case MULTIPLY_TOTAL: {
                    return "multiply_total";
                }
            }
            throw new IllegalArgumentException("Unknown operation " + (Object)((Object)operation));
        }

        private static EntityAttributeModifier.Operation fromName(String name) {
            switch (name) {
                case "addition": {
                    return EntityAttributeModifier.Operation.ADDITION;
                }
                case "multiply_base": {
                    return EntityAttributeModifier.Operation.MULTIPLY_BASE;
                }
                case "multiply_total": {
                    return EntityAttributeModifier.Operation.MULTIPLY_TOTAL;
                }
            }
            throw new JsonSyntaxException("Unknown attribute modifier operation " + name);
        }
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<SetAttributesLootFunction> {
        public Factory() {
            super(new Identifier("set_attributes"), SetAttributesLootFunction.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, SetAttributesLootFunction setAttributesLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, setAttributesLootFunction, jsonSerializationContext);
            JsonArray jsonArray = new JsonArray();
            for (Attribute attribute : setAttributesLootFunction.attributes) {
                jsonArray.add((JsonElement)attribute.serialize(jsonSerializationContext));
            }
            jsonObject.add("modifiers", (JsonElement)jsonArray);
        }

        @Override
        public SetAttributesLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "modifiers");
            ArrayList list = Lists.newArrayListWithExpectedSize((int)jsonArray.size());
            for (JsonElement jsonElement : jsonArray) {
                list.add(Attribute.deserialize(JsonHelper.asObject(jsonElement, "modifier"), jsonDeserializationContext));
            }
            if (list.isEmpty()) {
                throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
            }
            return new SetAttributesLootFunction(lootConditions, list);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

