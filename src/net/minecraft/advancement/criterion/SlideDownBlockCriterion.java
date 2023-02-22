/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class SlideDownBlockCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("slide_down_block");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        Block block = SlideDownBlockCriterion.getBlock(jsonObject);
        StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
        if (block != null) {
            statePredicate.check(block.getStateManager(), key -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + key);
            });
        }
        return new Conditions(extended, block, statePredicate);
    }

    @Nullable
    private static Block getBlock(JsonObject root) {
        if (root.has("block")) {
            Identifier identifier = new Identifier(JsonHelper.getString(root, "block"));
            return Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
        }
        return null;
    }

    public void test(ServerPlayerEntity player, BlockState state) {
        this.test(player, (T conditions) -> conditions.test(state));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Block block;
        private final StatePredicate state;

        public Conditions(EntityPredicate.Extended player, @Nullable Block block, StatePredicate state) {
            super(ID, player);
            this.block = block;
            this.state = state;
        }

        public static Conditions create(Block block) {
            return new Conditions(EntityPredicate.Extended.EMPTY, block, StatePredicate.ANY);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            if (this.block != null) {
                jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
            }
            jsonObject.add("state", this.state.toJson());
            return jsonObject;
        }

        public boolean test(BlockState state) {
            if (this.block != null && !state.isOf(this.block)) {
                return false;
            }
            return this.state.test(state);
        }
    }
}

