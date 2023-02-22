/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.advancement;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class AdvancementCriterion {
    private final CriterionConditions conditions;

    public AdvancementCriterion(CriterionConditions conditions) {
        this.conditions = conditions;
    }

    public AdvancementCriterion() {
        this.conditions = null;
    }

    public void toPacket(PacketByteBuf buf) {
    }

    public static AdvancementCriterion fromJson(JsonObject obj, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        Identifier identifier = new Identifier(JsonHelper.getString(obj, "trigger"));
        Criterion criterion = Criteria.getById(identifier);
        if (criterion == null) {
            throw new JsonSyntaxException("Invalid criterion trigger: " + identifier);
        }
        Object criterionConditions = criterion.conditionsFromJson(JsonHelper.getObject(obj, "conditions", new JsonObject()), predicateDeserializer);
        return new AdvancementCriterion((CriterionConditions)criterionConditions);
    }

    public static AdvancementCriterion fromPacket(PacketByteBuf buf) {
        return new AdvancementCriterion();
    }

    public static Map<String, AdvancementCriterion> criteriaFromJson(JsonObject obj, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        HashMap map = Maps.newHashMap();
        for (Map.Entry entry : obj.entrySet()) {
            map.put(entry.getKey(), AdvancementCriterion.fromJson(JsonHelper.asObject((JsonElement)entry.getValue(), "criterion"), predicateDeserializer));
        }
        return map;
    }

    public static Map<String, AdvancementCriterion> criteriaFromPacket(PacketByteBuf buf) {
        HashMap map = Maps.newHashMap();
        int i = buf.readVarInt();
        for (int j = 0; j < i; ++j) {
            map.put(buf.readString(Short.MAX_VALUE), AdvancementCriterion.fromPacket(buf));
        }
        return map;
    }

    public static void criteriaToPacket(Map<String, AdvancementCriterion> criteria, PacketByteBuf buf) {
        buf.writeVarInt(criteria.size());
        for (Map.Entry<String, AdvancementCriterion> entry : criteria.entrySet()) {
            buf.writeString(entry.getKey());
            entry.getValue().toPacket(buf);
        }
    }

    @Nullable
    public CriterionConditions getConditions() {
        return this.conditions;
    }

    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("trigger", this.conditions.getId().toString());
        JsonObject jsonObject2 = this.conditions.toJson(AdvancementEntityPredicateSerializer.INSTANCE);
        if (jsonObject2.size() != 0) {
            jsonObject.add("conditions", (JsonElement)jsonObject2);
        }
        return jsonObject;
    }
}

