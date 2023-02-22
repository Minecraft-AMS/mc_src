/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.templates.TypeTemplate
 */
package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema100
extends Schema {
    public Schema100(int i, Schema schema) {
        super(i, schema);
    }

    protected static TypeTemplate method_5196(Schema schema) {
        return DSL.optionalFields((String)"ArmorItems", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)), (String)"HandItems", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)));
    }

    protected static void method_5195(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> Schema100.method_5196(schema));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map map = super.registerEntities(schema);
        Schema100.method_5195(schema, map, "ArmorStand");
        Schema100.method_5195(schema, map, "Creeper");
        Schema100.method_5195(schema, map, "Skeleton");
        Schema100.method_5195(schema, map, "Spider");
        Schema100.method_5195(schema, map, "Giant");
        Schema100.method_5195(schema, map, "Zombie");
        Schema100.method_5195(schema, map, "Slime");
        Schema100.method_5195(schema, map, "Ghast");
        Schema100.method_5195(schema, map, "PigZombie");
        schema.register(map, "Enderman", string -> DSL.optionalFields((String)"carried", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema), (TypeTemplate)Schema100.method_5196(schema)));
        Schema100.method_5195(schema, map, "CaveSpider");
        Schema100.method_5195(schema, map, "Silverfish");
        Schema100.method_5195(schema, map, "Blaze");
        Schema100.method_5195(schema, map, "LavaSlime");
        Schema100.method_5195(schema, map, "EnderDragon");
        Schema100.method_5195(schema, map, "WitherBoss");
        Schema100.method_5195(schema, map, "Bat");
        Schema100.method_5195(schema, map, "Witch");
        Schema100.method_5195(schema, map, "Endermite");
        Schema100.method_5195(schema, map, "Guardian");
        Schema100.method_5195(schema, map, "Pig");
        Schema100.method_5195(schema, map, "Sheep");
        Schema100.method_5195(schema, map, "Cow");
        Schema100.method_5195(schema, map, "Chicken");
        Schema100.method_5195(schema, map, "Squid");
        Schema100.method_5195(schema, map, "Wolf");
        Schema100.method_5195(schema, map, "MushroomCow");
        Schema100.method_5195(schema, map, "SnowMan");
        Schema100.method_5195(schema, map, "Ozelot");
        Schema100.method_5195(schema, map, "VillagerGolem");
        schema.register(map, "EntityHorse", string -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)), (String)"ArmorItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (String)"SaddleItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (TypeTemplate)Schema100.method_5196(schema)));
        Schema100.method_5195(schema, map, "Rabbit");
        schema.register(map, "Villager", string -> DSL.optionalFields((String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)), (String)"Offers", (TypeTemplate)DSL.optionalFields((String)"Recipes", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"buy", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (String)"buyB", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (String)"sell", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema)))), (TypeTemplate)Schema100.method_5196(schema)));
        Schema100.method_5195(schema, map, "Shulker");
        schema.registerSimple(map, "AreaEffectCloud");
        schema.registerSimple(map, "ShulkerBullet");
        return map;
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
        super.registerTypes(schema, map, map2);
        schema.registerType(false, TypeReferences.STRUCTURE, () -> DSL.optionalFields((String)"entities", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"nbt", (TypeTemplate)TypeReferences.ENTITY_TREE.in(schema))), (String)"blocks", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"nbt", (TypeTemplate)TypeReferences.BLOCK_ENTITY.in(schema))), (String)"palette", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.BLOCK_STATE.in(schema))));
        schema.registerType(false, TypeReferences.BLOCK_STATE, DSL::remainder);
    }
}

