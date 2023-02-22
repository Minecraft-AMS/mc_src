/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.Hook$HookFunction
 *  com.mojang.datafixers.types.templates.TypeTemplate
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.datafixer.schema;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.util.Identifier;

public class Schema1451v6
extends IdentifierNormalizingSchema {
    public static final String SPECIAL_TYPE = "_special";
    protected static final Hook.HookFunction field_34014 = new Hook.HookFunction(){

        public <T> T apply(DynamicOps<T> ops, T value) {
            Dynamic dynamic = new Dynamic(ops, value);
            return (T)((Dynamic)DataFixUtils.orElse(dynamic.get("CriteriaName").asString().get().left().map(criteriaName -> {
                int i = criteriaName.indexOf(58);
                if (i < 0) {
                    return Pair.of((Object)Schema1451v6.SPECIAL_TYPE, (Object)criteriaName);
                }
                try {
                    Identifier identifier = Identifier.splitOn(criteriaName.substring(0, i), '.');
                    Identifier identifier2 = Identifier.splitOn(criteriaName.substring(i + 1), '.');
                    return Pair.of((Object)identifier.toString(), (Object)identifier2.toString());
                }
                catch (Exception exception) {
                    return Pair.of((Object)Schema1451v6.SPECIAL_TYPE, (Object)criteriaName);
                }
            }).map(pair -> dynamic.set("CriteriaType", dynamic.createMap((Map)ImmutableMap.of((Object)dynamic.createString("type"), (Object)dynamic.createString((String)pair.getFirst()), (Object)dynamic.createString("id"), (Object)dynamic.createString((String)pair.getSecond()))))), (Object)dynamic)).getValue();
        }
    };
    protected static final Hook.HookFunction field_34015 = new Hook.HookFunction(){

        private String normalize(String id) {
            Identifier identifier = Identifier.tryParse(id);
            return identifier != null ? identifier.getNamespace() + "." + identifier.getPath() : id;
        }

        public <T> T apply(DynamicOps<T> ops, T value) {
            Dynamic dynamic = new Dynamic(ops, value);
            Optional<Dynamic> optional = dynamic.get("CriteriaType").get().get().left().flatMap(criteriaType -> {
                Optional optional = criteriaType.get("type").asString().get().left();
                Optional optional2 = criteriaType.get("id").asString().get().left();
                if (optional.isPresent() && optional2.isPresent()) {
                    String string = (String)optional.get();
                    if (string.equals(Schema1451v6.SPECIAL_TYPE)) {
                        return Optional.of(dynamic.createString((String)optional2.get()));
                    }
                    return Optional.of(criteriaType.createString(this.normalize(string) + ":" + this.normalize((String)optional2.get())));
                }
                return Optional.empty();
            });
            return (T)((Dynamic)DataFixUtils.orElse(optional.map(criteriaName -> dynamic.set("CriteriaName", criteriaName).remove("CriteriaType")), (Object)dynamic)).getValue();
        }
    };

    public Schema1451v6(int i, Schema schema) {
        super(i, schema);
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
        super.registerTypes(schema, entityTypes, blockEntityTypes);
        Supplier<TypeTemplate> supplier = () -> DSL.compoundList((TypeTemplate)TypeReferences.ITEM_NAME.in(schema), (TypeTemplate)DSL.constType((Type)DSL.intType()));
        schema.registerType(false, TypeReferences.STATS, () -> DSL.optionalFields((String)"stats", (TypeTemplate)DSL.optionalFields((String)"minecraft:mined", (TypeTemplate)DSL.compoundList((TypeTemplate)TypeReferences.BLOCK_NAME.in(schema), (TypeTemplate)DSL.constType((Type)DSL.intType())), (String)"minecraft:crafted", (TypeTemplate)((TypeTemplate)supplier.get()), (String)"minecraft:used", (TypeTemplate)((TypeTemplate)supplier.get()), (String)"minecraft:broken", (TypeTemplate)((TypeTemplate)supplier.get()), (String)"minecraft:picked_up", (TypeTemplate)((TypeTemplate)supplier.get()), (TypeTemplate)DSL.optionalFields((String)"minecraft:dropped", (TypeTemplate)((TypeTemplate)supplier.get()), (String)"minecraft:killed", (TypeTemplate)DSL.compoundList((TypeTemplate)TypeReferences.ENTITY_NAME.in(schema), (TypeTemplate)DSL.constType((Type)DSL.intType())), (String)"minecraft:killed_by", (TypeTemplate)DSL.compoundList((TypeTemplate)TypeReferences.ENTITY_NAME.in(schema), (TypeTemplate)DSL.constType((Type)DSL.intType())), (String)"minecraft:custom", (TypeTemplate)DSL.compoundList((TypeTemplate)DSL.constType(Schema1451v6.getIdentifierType()), (TypeTemplate)DSL.constType((Type)DSL.intType()))))));
        Map<String, Supplier<TypeTemplate>> map = Schema1451v6.method_37389(schema);
        schema.registerType(false, TypeReferences.OBJECTIVE, () -> DSL.hook((TypeTemplate)DSL.optionalFields((String)"CriteriaType", (TypeTemplate)DSL.taggedChoiceLazy((String)"type", (Type)DSL.string(), (Map)map)), (Hook.HookFunction)field_34014, (Hook.HookFunction)field_34015));
    }

    protected static Map<String, Supplier<TypeTemplate>> method_37389(Schema schema) {
        Supplier<TypeTemplate> supplier = () -> DSL.optionalFields((String)"id", (TypeTemplate)TypeReferences.ITEM_NAME.in(schema));
        Supplier<TypeTemplate> supplier2 = () -> DSL.optionalFields((String)"id", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema));
        Supplier<TypeTemplate> supplier3 = () -> DSL.optionalFields((String)"id", (TypeTemplate)TypeReferences.ENTITY_NAME.in(schema));
        HashMap map = Maps.newHashMap();
        map.put("minecraft:mined", supplier2);
        map.put("minecraft:crafted", supplier);
        map.put("minecraft:used", supplier);
        map.put("minecraft:broken", supplier);
        map.put("minecraft:picked_up", supplier);
        map.put("minecraft:dropped", supplier);
        map.put("minecraft:killed", supplier3);
        map.put("minecraft:killed_by", supplier3);
        map.put("minecraft:custom", () -> DSL.optionalFields((String)"id", (TypeTemplate)DSL.constType(Schema1451v6.getIdentifierType())));
        map.put(SPECIAL_TYPE, () -> DSL.optionalFields((String)"id", (TypeTemplate)DSL.constType((Type)DSL.string())));
        return map;
    }
}

