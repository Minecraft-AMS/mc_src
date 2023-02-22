/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.potion.Potion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class BrewedPotionCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("brewed_potion");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        Potion potion = null;
        if (jsonObject.has("potion")) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "potion"));
            potion = (Potion)Registry.POTION.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + identifier + "'"));
        }
        return new Conditions(potion);
    }

    public void trigger(ServerPlayerEntity player, Potion potion) {
        this.test(player.getAdvancementTracker(), conditions -> conditions.matches(potion));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Potion potion;

        public Conditions(@Nullable Potion potion) {
            super(ID);
            this.potion = potion;
        }

        public static Conditions any() {
            return new Conditions(null);
        }

        public boolean matches(Potion potion) {
            return this.potion == null || this.potion == potion;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            if (this.potion != null) {
                jsonObject.addProperty("potion", Registry.POTION.getId(this.potion).toString());
            }
            return jsonObject;
        }
    }
}

