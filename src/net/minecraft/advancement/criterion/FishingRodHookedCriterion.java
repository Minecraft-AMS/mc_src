/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Collection;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FishingRodHookedCriterion
extends AbstractCriterion<Conditions> {
    static final Identifier ID = new Identifier("fishing_rod_hooked");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("rod"));
        EntityPredicate.Extended extended2 = EntityPredicate.Extended.getInJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
        ItemPredicate itemPredicate2 = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(extended, itemPredicate, extended2, itemPredicate2);
    }

    public void trigger(ServerPlayerEntity player, ItemStack rod, FishingBobberEntity bobber, Collection<ItemStack> fishingLoots) {
        LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, bobber.getHookedEntity() != null ? bobber.getHookedEntity() : bobber);
        this.trigger(player, conditions -> conditions.matches(rod, lootContext, fishingLoots));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final ItemPredicate rod;
        private final EntityPredicate.Extended hookedEntity;
        private final ItemPredicate caughtItem;

        public Conditions(EntityPredicate.Extended player, ItemPredicate rod, EntityPredicate.Extended hookedEntity, ItemPredicate caughtItem) {
            super(ID, player);
            this.rod = rod;
            this.hookedEntity = hookedEntity;
            this.caughtItem = caughtItem;
        }

        public static Conditions create(ItemPredicate rod, EntityPredicate bobber, ItemPredicate item) {
            return new Conditions(EntityPredicate.Extended.EMPTY, rod, EntityPredicate.Extended.ofLegacy(bobber), item);
        }

        public boolean matches(ItemStack rod, LootContext hookedEntityContext, Collection<ItemStack> fishingLoots) {
            if (!this.rod.test(rod)) {
                return false;
            }
            if (!this.hookedEntity.test(hookedEntityContext)) {
                return false;
            }
            if (this.caughtItem != ItemPredicate.ANY) {
                ItemEntity itemEntity;
                boolean bl = false;
                Entity entity = hookedEntityContext.get(LootContextParameters.THIS_ENTITY);
                if (entity instanceof ItemEntity && this.caughtItem.test((itemEntity = (ItemEntity)entity).getStack())) {
                    bl = true;
                }
                for (ItemStack itemStack : fishingLoots) {
                    if (!this.caughtItem.test(itemStack)) continue;
                    bl = true;
                    break;
                }
                if (!bl) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("rod", this.rod.toJson());
            jsonObject.add("entity", this.hookedEntity.toJson(predicateSerializer));
            jsonObject.add("item", this.caughtItem.toJson());
            return jsonObject;
        }
    }
}

