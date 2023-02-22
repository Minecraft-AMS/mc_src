/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.Hook$HookFunction
 *  com.mojang.datafixers.types.templates.TypeTemplate
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
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.datafixer.schema.Schema99;

public class Schema704
extends Schema {
    protected static final Map<String, String> BLOCK_RENAMES = (Map)DataFixUtils.make(() -> {
        HashMap map = Maps.newHashMap();
        map.put("minecraft:furnace", "minecraft:furnace");
        map.put("minecraft:lit_furnace", "minecraft:furnace");
        map.put("minecraft:chest", "minecraft:chest");
        map.put("minecraft:trapped_chest", "minecraft:chest");
        map.put("minecraft:ender_chest", "minecraft:ender_chest");
        map.put("minecraft:jukebox", "minecraft:jukebox");
        map.put("minecraft:dispenser", "minecraft:dispenser");
        map.put("minecraft:dropper", "minecraft:dropper");
        map.put("minecraft:sign", "minecraft:sign");
        map.put("minecraft:mob_spawner", "minecraft:mob_spawner");
        map.put("minecraft:spawner", "minecraft:mob_spawner");
        map.put("minecraft:noteblock", "minecraft:noteblock");
        map.put("minecraft:brewing_stand", "minecraft:brewing_stand");
        map.put("minecraft:enhanting_table", "minecraft:enchanting_table");
        map.put("minecraft:command_block", "minecraft:command_block");
        map.put("minecraft:beacon", "minecraft:beacon");
        map.put("minecraft:skull", "minecraft:skull");
        map.put("minecraft:daylight_detector", "minecraft:daylight_detector");
        map.put("minecraft:hopper", "minecraft:hopper");
        map.put("minecraft:banner", "minecraft:banner");
        map.put("minecraft:flower_pot", "minecraft:flower_pot");
        map.put("minecraft:repeating_command_block", "minecraft:command_block");
        map.put("minecraft:chain_command_block", "minecraft:command_block");
        map.put("minecraft:shulker_box", "minecraft:shulker_box");
        map.put("minecraft:white_shulker_box", "minecraft:shulker_box");
        map.put("minecraft:orange_shulker_box", "minecraft:shulker_box");
        map.put("minecraft:magenta_shulker_box", "minecraft:shulker_box");
        map.put("minecraft:light_blue_shulker_box", "minecraft:shulker_box");
        map.put("minecraft:yellow_shulker_box", "minecraft:shulker_box");
        map.put("minecraft:lime_shulker_box", "minecraft:shulker_box");
        map.put("minecraft:pink_shulker_box", "minecraft:shulker_box");
        map.put("minecraft:gray_shulker_box", "minecraft:shulker_box");
        map.put("minecraft:silver_shulker_box", "minecraft:shulker_box");
        map.put("minecraft:cyan_shulker_box", "minecraft:shulker_box");
        map.put("minecraft:purple_shulker_box", "minecraft:shulker_box");
        map.put("minecraft:blue_shulker_box", "minecraft:shulker_box");
        map.put("minecraft:brown_shulker_box", "minecraft:shulker_box");
        map.put("minecraft:green_shulker_box", "minecraft:shulker_box");
        map.put("minecraft:red_shulker_box", "minecraft:shulker_box");
        map.put("minecraft:black_shulker_box", "minecraft:shulker_box");
        map.put("minecraft:bed", "minecraft:bed");
        map.put("minecraft:light_gray_shulker_box", "minecraft:shulker_box");
        map.put("minecraft:banner", "minecraft:banner");
        map.put("minecraft:white_banner", "minecraft:banner");
        map.put("minecraft:orange_banner", "minecraft:banner");
        map.put("minecraft:magenta_banner", "minecraft:banner");
        map.put("minecraft:light_blue_banner", "minecraft:banner");
        map.put("minecraft:yellow_banner", "minecraft:banner");
        map.put("minecraft:lime_banner", "minecraft:banner");
        map.put("minecraft:pink_banner", "minecraft:banner");
        map.put("minecraft:gray_banner", "minecraft:banner");
        map.put("minecraft:silver_banner", "minecraft:banner");
        map.put("minecraft:light_gray_banner", "minecraft:banner");
        map.put("minecraft:cyan_banner", "minecraft:banner");
        map.put("minecraft:purple_banner", "minecraft:banner");
        map.put("minecraft:blue_banner", "minecraft:banner");
        map.put("minecraft:brown_banner", "minecraft:banner");
        map.put("minecraft:green_banner", "minecraft:banner");
        map.put("minecraft:red_banner", "minecraft:banner");
        map.put("minecraft:black_banner", "minecraft:banner");
        map.put("minecraft:standing_sign", "minecraft:sign");
        map.put("minecraft:wall_sign", "minecraft:sign");
        map.put("minecraft:piston_head", "minecraft:piston");
        map.put("minecraft:daylight_detector_inverted", "minecraft:daylight_detector");
        map.put("minecraft:unpowered_comparator", "minecraft:comparator");
        map.put("minecraft:powered_comparator", "minecraft:comparator");
        map.put("minecraft:wall_banner", "minecraft:banner");
        map.put("minecraft:standing_banner", "minecraft:banner");
        map.put("minecraft:structure_block", "minecraft:structure_block");
        map.put("minecraft:end_portal", "minecraft:end_portal");
        map.put("minecraft:end_gateway", "minecraft:end_gateway");
        map.put("minecraft:sign", "minecraft:sign");
        map.put("minecraft:shield", "minecraft:banner");
        map.put("minecraft:white_bed", "minecraft:bed");
        map.put("minecraft:orange_bed", "minecraft:bed");
        map.put("minecraft:magenta_bed", "minecraft:bed");
        map.put("minecraft:light_blue_bed", "minecraft:bed");
        map.put("minecraft:yellow_bed", "minecraft:bed");
        map.put("minecraft:lime_bed", "minecraft:bed");
        map.put("minecraft:pink_bed", "minecraft:bed");
        map.put("minecraft:gray_bed", "minecraft:bed");
        map.put("minecraft:silver_bed", "minecraft:bed");
        map.put("minecraft:light_gray_bed", "minecraft:bed");
        map.put("minecraft:cyan_bed", "minecraft:bed");
        map.put("minecraft:purple_bed", "minecraft:bed");
        map.put("minecraft:blue_bed", "minecraft:bed");
        map.put("minecraft:brown_bed", "minecraft:bed");
        map.put("minecraft:green_bed", "minecraft:bed");
        map.put("minecraft:red_bed", "minecraft:bed");
        map.put("minecraft:black_bed", "minecraft:bed");
        map.put("minecraft:oak_sign", "minecraft:sign");
        map.put("minecraft:spruce_sign", "minecraft:sign");
        map.put("minecraft:birch_sign", "minecraft:sign");
        map.put("minecraft:jungle_sign", "minecraft:sign");
        map.put("minecraft:acacia_sign", "minecraft:sign");
        map.put("minecraft:dark_oak_sign", "minecraft:sign");
        map.put("minecraft:crimson_sign", "minecraft:sign");
        map.put("minecraft:warped_sign", "minecraft:sign");
        map.put("minecraft:skeleton_skull", "minecraft:skull");
        map.put("minecraft:wither_skeleton_skull", "minecraft:skull");
        map.put("minecraft:zombie_head", "minecraft:skull");
        map.put("minecraft:player_head", "minecraft:skull");
        map.put("minecraft:creeper_head", "minecraft:skull");
        map.put("minecraft:dragon_head", "minecraft:skull");
        map.put("minecraft:barrel", "minecraft:barrel");
        map.put("minecraft:conduit", "minecraft:conduit");
        map.put("minecraft:smoker", "minecraft:smoker");
        map.put("minecraft:blast_furnace", "minecraft:blast_furnace");
        map.put("minecraft:lectern", "minecraft:lectern");
        map.put("minecraft:bell", "minecraft:bell");
        map.put("minecraft:jigsaw", "minecraft:jigsaw");
        map.put("minecraft:campfire", "minecraft:campfire");
        map.put("minecraft:bee_nest", "minecraft:beehive");
        map.put("minecraft:beehive", "minecraft:beehive");
        map.put("minecraft:sculk_sensor", "minecraft:sculk_sensor");
        return ImmutableMap.copyOf((Map)map);
    });
    protected static final Hook.HookFunction field_5745 = new Hook.HookFunction(){

        public <T> T apply(DynamicOps<T> dynamicOps, T object) {
            return Schema99.method_5359(new Dynamic(dynamicOps, object), BLOCK_RENAMES, "ArmorStand");
        }
    };

    public Schema704(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    protected static void targetItems(Schema schema, Map<String, Supplier<TypeTemplate>> map, String blockEntityId) {
        schema.register(map, blockEntityId, () -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema))));
    }

    public Type<?> getChoiceType(DSL.TypeReference typeReference, String string) {
        if (Objects.equals(typeReference.typeName(), TypeReferences.BLOCK_ENTITY.typeName())) {
            return super.getChoiceType(typeReference, IdentifierNormalizingSchema.normalize(string));
        }
        return super.getChoiceType(typeReference, string);
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        HashMap map = Maps.newHashMap();
        Schema704.targetItems(schema, map, "minecraft:furnace");
        Schema704.targetItems(schema, map, "minecraft:chest");
        schema.registerSimple((Map)map, "minecraft:ender_chest");
        schema.register((Map)map, "minecraft:jukebox", string -> DSL.optionalFields((String)"RecordItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema)));
        Schema704.targetItems(schema, map, "minecraft:dispenser");
        Schema704.targetItems(schema, map, "minecraft:dropper");
        schema.registerSimple((Map)map, "minecraft:sign");
        schema.register((Map)map, "minecraft:mob_spawner", string -> TypeReferences.UNTAGGED_SPAWNER.in(schema));
        schema.registerSimple((Map)map, "minecraft:noteblock");
        schema.registerSimple((Map)map, "minecraft:piston");
        Schema704.targetItems(schema, map, "minecraft:brewing_stand");
        schema.registerSimple((Map)map, "minecraft:enchanting_table");
        schema.registerSimple((Map)map, "minecraft:end_portal");
        schema.registerSimple((Map)map, "minecraft:beacon");
        schema.registerSimple((Map)map, "minecraft:skull");
        schema.registerSimple((Map)map, "minecraft:daylight_detector");
        Schema704.targetItems(schema, map, "minecraft:hopper");
        schema.registerSimple((Map)map, "minecraft:comparator");
        schema.register((Map)map, "minecraft:flower_pot", string -> DSL.optionalFields((String)"Item", (TypeTemplate)DSL.or((TypeTemplate)DSL.constType((Type)DSL.intType()), (TypeTemplate)TypeReferences.ITEM_NAME.in(schema))));
        schema.registerSimple((Map)map, "minecraft:banner");
        schema.registerSimple((Map)map, "minecraft:structure_block");
        schema.registerSimple((Map)map, "minecraft:end_gateway");
        schema.registerSimple((Map)map, "minecraft:command_block");
        return map;
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
        super.registerTypes(schema, entityTypes, blockEntityTypes);
        schema.registerType(false, TypeReferences.BLOCK_ENTITY, () -> DSL.taggedChoiceLazy((String)"id", IdentifierNormalizingSchema.getIdentifierType(), (Map)blockEntityTypes));
        schema.registerType(true, TypeReferences.ITEM_STACK, () -> DSL.hook((TypeTemplate)DSL.optionalFields((String)"id", (TypeTemplate)TypeReferences.ITEM_NAME.in(schema), (String)"tag", (TypeTemplate)DSL.optionalFields((String)"EntityTag", (TypeTemplate)TypeReferences.ENTITY_TREE.in(schema), (String)"BlockEntityTag", (TypeTemplate)TypeReferences.BLOCK_ENTITY.in(schema), (String)"CanDestroy", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)), (String)"CanPlaceOn", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)))), (Hook.HookFunction)field_5745, (Hook.HookFunction)Hook.HookFunction.IDENTITY));
    }
}

