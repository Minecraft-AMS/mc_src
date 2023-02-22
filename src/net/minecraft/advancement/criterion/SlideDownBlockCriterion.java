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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.StatePredicate;
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
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        Block block = SlideDownBlockCriterion.getBlock(jsonObject);
        StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
        if (block != null) {
            statePredicate.check(block.getStateManager(), key -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + key);
            });
        }
        return new Conditions(block, statePredicate);
    }

    @Nullable
    private static Block getBlock(JsonObject root) {
        if (root.has("block")) {
            Identifier identifier = new Identifier(JsonHelper.getString(root, "block"));
            return (Block)Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
        }
        return null;
    }

    public void test(ServerPlayerEntity player, BlockState state) {
        this.test(player.getAdvancementTracker(), (T conditions) -> conditions.test(state));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Block block;
        private final StatePredicate state;

        public Conditions(@Nullable Block block, StatePredicate state) {
            super(ID);
            this.block = block;
            this.state = state;
        }

        public static Conditions create(Block block) {
            return new Conditions(block, StatePredicate.ANY);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            if (this.block != null) {
                jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
            }
            jsonObject.add("state", this.state.toJson());
            return jsonObject;
        }

        public boolean test(BlockState state) {
            if (this.block != null && state.getBlock() != this.block) {
                return false;
            }
            return this.state.test(state);
        }
    }
}

