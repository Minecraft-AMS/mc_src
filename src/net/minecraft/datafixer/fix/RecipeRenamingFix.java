/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Map;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;

public class RecipeRenamingFix
extends DataFix {
    private static final Map<String, String> recipes = ImmutableMap.builder().put((Object)"minecraft:acacia_bark", (Object)"minecraft:acacia_wood").put((Object)"minecraft:birch_bark", (Object)"minecraft:birch_wood").put((Object)"minecraft:dark_oak_bark", (Object)"minecraft:dark_oak_wood").put((Object)"minecraft:jungle_bark", (Object)"minecraft:jungle_wood").put((Object)"minecraft:oak_bark", (Object)"minecraft:oak_wood").put((Object)"minecraft:spruce_bark", (Object)"minecraft:spruce_wood").build();

    public RecipeRenamingFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    protected TypeRewriteRule makeRule() {
        Type type = DSL.named((String)TypeReferences.RECIPE.typeName(), (Type)DSL.namespacedString());
        if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.RECIPE))) {
            throw new IllegalStateException("Recipe type is not what was expected.");
        }
        return this.fixTypeEverywhere("Recipes renamening fix", type, dynamicOps -> pair -> pair.mapSecond(string -> recipes.getOrDefault(string, (String)string)));
    }
}

