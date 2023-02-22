/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.predicate;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class PlayerPredicate {
    public static final PlayerPredicate ANY = new Builder().build();
    private final NumberRange.IntRange experienceLevel;
    private final GameMode gameMode;
    private final Map<Stat<?>, NumberRange.IntRange> stats;
    private final Object2BooleanMap<Identifier> recipes;
    private final Map<Identifier, AdvancementPredicate> advancements;

    private static AdvancementPredicate criterionFromJson(JsonElement json) {
        if (json.isJsonPrimitive()) {
            boolean bl = json.getAsBoolean();
            return new CompletedAdvancementPredicate(bl);
        }
        Object2BooleanOpenHashMap object2BooleanMap = new Object2BooleanOpenHashMap();
        JsonObject jsonObject = JsonHelper.asObject(json, "criterion data");
        jsonObject.entrySet().forEach(arg_0 -> PlayerPredicate.method_22502((Object2BooleanMap)object2BooleanMap, arg_0));
        return new AdvancementCriteriaPredicate((Object2BooleanMap<String>)object2BooleanMap);
    }

    private PlayerPredicate(NumberRange.IntRange experienceLevel, GameMode gameMode, Map<Stat<?>, NumberRange.IntRange> stats, Object2BooleanMap<Identifier> recipes, Map<Identifier, AdvancementPredicate> advancements) {
        this.experienceLevel = experienceLevel;
        this.gameMode = gameMode;
        this.stats = stats;
        this.recipes = recipes;
        this.advancements = advancements;
    }

    public boolean test(Entity entity) {
        if (this == ANY) {
            return true;
        }
        if (!(entity instanceof ServerPlayerEntity)) {
            return false;
        }
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
        if (!this.experienceLevel.test(serverPlayerEntity.experienceLevel)) {
            return false;
        }
        if (this.gameMode != GameMode.NOT_SET && this.gameMode != serverPlayerEntity.interactionManager.getGameMode()) {
            return false;
        }
        ServerStatHandler statHandler = serverPlayerEntity.getStatHandler();
        for (Map.Entry<Stat<?>, NumberRange.IntRange> entry : this.stats.entrySet()) {
            int i = statHandler.getStat(entry.getKey());
            if (entry.getValue().test(i)) continue;
            return false;
        }
        ServerRecipeBook recipeBook = serverPlayerEntity.getRecipeBook();
        for (Object2BooleanMap.Entry entry2 : this.recipes.object2BooleanEntrySet()) {
            if (recipeBook.contains((Identifier)entry2.getKey()) == entry2.getBooleanValue()) continue;
            return false;
        }
        if (!this.advancements.isEmpty()) {
            PlayerAdvancementTracker playerAdvancementTracker = serverPlayerEntity.getAdvancementTracker();
            ServerAdvancementLoader serverAdvancementLoader = serverPlayerEntity.getServer().getAdvancementLoader();
            for (Map.Entry<Identifier, AdvancementPredicate> entry3 : this.advancements.entrySet()) {
                Advancement advancement = serverAdvancementLoader.get(entry3.getKey());
                if (advancement != null && entry3.getValue().test(playerAdvancementTracker.getProgress(advancement))) continue;
                return false;
            }
        }
        return true;
    }

    public static PlayerPredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "player");
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("level"));
        String string = JsonHelper.getString(jsonObject, "gamemode", "");
        GameMode gameMode = GameMode.byName(string, GameMode.NOT_SET);
        HashMap map = Maps.newHashMap();
        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "stats", null);
        if (jsonArray != null) {
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "stats entry");
                Identifier identifier = new Identifier(JsonHelper.getString(jsonObject2, "type"));
                StatType<?> statType = Registry.STAT_TYPE.get(identifier);
                if (statType == null) {
                    throw new JsonParseException("Invalid stat type: " + identifier);
                }
                Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject2, "stat"));
                Stat<?> stat = PlayerPredicate.getStat(statType, identifier2);
                NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject2.get("value"));
                map.put(stat, intRange2);
            }
        }
        Object2BooleanOpenHashMap object2BooleanMap = new Object2BooleanOpenHashMap();
        JsonObject jsonObject3 = JsonHelper.getObject(jsonObject, "recipes", new JsonObject());
        for (Map.Entry entry : jsonObject3.entrySet()) {
            Identifier identifier3 = new Identifier((String)entry.getKey());
            boolean bl = JsonHelper.asBoolean((JsonElement)entry.getValue(), "recipe present");
            object2BooleanMap.put((Object)identifier3, bl);
        }
        HashMap map2 = Maps.newHashMap();
        JsonObject jsonObject4 = JsonHelper.getObject(jsonObject, "advancements", new JsonObject());
        for (Map.Entry entry2 : jsonObject4.entrySet()) {
            Identifier identifier4 = new Identifier((String)entry2.getKey());
            AdvancementPredicate advancementPredicate = PlayerPredicate.criterionFromJson((JsonElement)entry2.getValue());
            map2.put(identifier4, advancementPredicate);
        }
        return new PlayerPredicate(intRange, gameMode, map, (Object2BooleanMap<Identifier>)object2BooleanMap, map2);
    }

    private static <T> Stat<T> getStat(StatType<T> type, Identifier id) {
        Registry<T> registry = type.getRegistry();
        T object = registry.get(id);
        if (object == null) {
            throw new JsonParseException("Unknown object " + id + " for stat type " + Registry.STAT_TYPE.getId(type));
        }
        return type.getOrCreateStat(object);
    }

    private static <T> Identifier getStatId(Stat<T> stat) {
        return stat.getType().getRegistry().getId(stat.getValue());
    }

    public JsonElement toJson() {
        JsonObject jsonObject2;
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("level", this.experienceLevel.toJson());
        if (this.gameMode != GameMode.NOT_SET) {
            jsonObject.addProperty("gamemode", this.gameMode.getName());
        }
        if (!this.stats.isEmpty()) {
            JsonArray jsonArray = new JsonArray();
            this.stats.forEach((stat, intRange) -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("type", Registry.STAT_TYPE.getId(stat.getType()).toString());
                jsonObject.addProperty("stat", PlayerPredicate.getStatId(stat).toString());
                jsonObject.add("value", intRange.toJson());
                jsonArray.add((JsonElement)jsonObject);
            });
            jsonObject.add("stats", (JsonElement)jsonArray);
        }
        if (!this.recipes.isEmpty()) {
            jsonObject2 = new JsonObject();
            this.recipes.forEach((id, present) -> jsonObject2.addProperty(id.toString(), present));
            jsonObject.add("recipes", (JsonElement)jsonObject2);
        }
        if (!this.advancements.isEmpty()) {
            jsonObject2 = new JsonObject();
            this.advancements.forEach((id, advancementPredicate) -> jsonObject2.add(id.toString(), advancementPredicate.toJson()));
            jsonObject.add("advancements", (JsonElement)jsonObject2);
        }
        return jsonObject;
    }

    private static /* synthetic */ void method_22502(Object2BooleanMap object2BooleanMap, Map.Entry entry) {
        boolean bl = JsonHelper.asBoolean((JsonElement)entry.getValue(), "criterion test");
        object2BooleanMap.put(entry.getKey(), bl);
    }

    public static class Builder {
        private NumberRange.IntRange experienceLevel = NumberRange.IntRange.ANY;
        private GameMode gamemode = GameMode.NOT_SET;
        private final Map<Stat<?>, NumberRange.IntRange> stats = Maps.newHashMap();
        private final Object2BooleanMap<Identifier> recipes = new Object2BooleanOpenHashMap();
        private final Map<Identifier, AdvancementPredicate> advancements = Maps.newHashMap();

        public PlayerPredicate build() {
            return new PlayerPredicate(this.experienceLevel, this.gamemode, this.stats, this.recipes, this.advancements);
        }
    }

    static class AdvancementCriteriaPredicate
    implements AdvancementPredicate {
        private final Object2BooleanMap<String> criteria;

        public AdvancementCriteriaPredicate(Object2BooleanMap<String> criteria) {
            this.criteria = criteria;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            this.criteria.forEach((arg_0, arg_1) -> ((JsonObject)jsonObject).addProperty(arg_0, arg_1));
            return jsonObject;
        }

        @Override
        public boolean test(AdvancementProgress advancementProgress) {
            for (Object2BooleanMap.Entry entry : this.criteria.object2BooleanEntrySet()) {
                CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
                if (criterionProgress != null && criterionProgress.isObtained() == entry.getBooleanValue()) continue;
                return false;
            }
            return true;
        }

        @Override
        public /* synthetic */ boolean test(Object progress) {
            return this.test((AdvancementProgress)progress);
        }
    }

    static class CompletedAdvancementPredicate
    implements AdvancementPredicate {
        private final boolean done;

        public CompletedAdvancementPredicate(boolean done) {
            this.done = done;
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(Boolean.valueOf(this.done));
        }

        @Override
        public boolean test(AdvancementProgress advancementProgress) {
            return advancementProgress.isDone() == this.done;
        }

        @Override
        public /* synthetic */ boolean test(Object progress) {
            return this.test((AdvancementProgress)progress);
        }
    }

    static interface AdvancementPredicate
    extends Predicate<AdvancementProgress> {
        public JsonElement toJson();
    }
}

