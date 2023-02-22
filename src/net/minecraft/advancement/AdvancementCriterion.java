/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.advancement;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public class AdvancementCriterion {
    private final CriterionConditions conditions;

    public AdvancementCriterion(CriterionConditions conditions) {
        this.conditions = conditions;
    }

    public AdvancementCriterion() {
        this.conditions = null;
    }

    public void serialize(PacketByteBuf packetByteBuf) {
    }

    public static AdvancementCriterion deserialize(JsonObject obj, JsonDeserializationContext context) {
        Identifier identifier = new Identifier(JsonHelper.getString(obj, "trigger"));
        Criterion criterion = Criterions.getById(identifier);
        if (criterion == null) {
            throw new JsonSyntaxException("Invalid criterion trigger: " + identifier);
        }
        Object criterionConditions = criterion.conditionsFromJson(JsonHelper.getObject(obj, "conditions", new JsonObject()), context);
        return new AdvancementCriterion((CriterionConditions)criterionConditions);
    }

    public static AdvancementCriterion createNew(PacketByteBuf buf) {
        return new AdvancementCriterion();
    }

    public static Map<String, AdvancementCriterion> fromJson(JsonObject obj, JsonDeserializationContext context) {
        HashMap map = Maps.newHashMap();
        for (Map.Entry entry : obj.entrySet()) {
            map.put(entry.getKey(), AdvancementCriterion.deserialize(JsonHelper.asObject((JsonElement)entry.getValue(), "criterion"), context));
        }
        return map;
    }

    public static Map<String, AdvancementCriterion> fromPacket(PacketByteBuf buf) {
        HashMap map = Maps.newHashMap();
        int i = buf.readVarInt();
        for (int j = 0; j < i; ++j) {
            map.put(buf.readString(Short.MAX_VALUE), AdvancementCriterion.createNew(buf));
        }
        return map;
    }

    public static void serialize(Map<String, AdvancementCriterion> criteria, PacketByteBuf buf) {
        buf.writeVarInt(criteria.size());
        for (Map.Entry<String, AdvancementCriterion> entry : criteria.entrySet()) {
            buf.writeString(entry.getKey());
            entry.getValue().serialize(buf);
        }
    }

    @Nullable
    public CriterionConditions getConditions() {
        return this.conditions;
    }

    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("trigger", this.conditions.getId().toString());
        jsonObject.add("conditions", this.conditions.toJson());
        return jsonObject;
    }
}

