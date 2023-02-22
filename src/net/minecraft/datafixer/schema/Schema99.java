/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.Hook$HookFunction
 *  com.mojang.datafixers.types.templates.TypeTemplate
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.datafixer.schema;

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
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Schema99
extends Schema {
    private static final Logger LOGGER = LogManager.getLogger();
    static final Map<String, String> field_5748 = (Map)DataFixUtils.make((Object)Maps.newHashMap(), hashMap -> {
        hashMap.put("minecraft:furnace", "Furnace");
        hashMap.put("minecraft:lit_furnace", "Furnace");
        hashMap.put("minecraft:chest", "Chest");
        hashMap.put("minecraft:trapped_chest", "Chest");
        hashMap.put("minecraft:ender_chest", "EnderChest");
        hashMap.put("minecraft:jukebox", "RecordPlayer");
        hashMap.put("minecraft:dispenser", "Trap");
        hashMap.put("minecraft:dropper", "Dropper");
        hashMap.put("minecraft:sign", "Sign");
        hashMap.put("minecraft:mob_spawner", "MobSpawner");
        hashMap.put("minecraft:noteblock", "Music");
        hashMap.put("minecraft:brewing_stand", "Cauldron");
        hashMap.put("minecraft:enhanting_table", "EnchantTable");
        hashMap.put("minecraft:command_block", "CommandBlock");
        hashMap.put("minecraft:beacon", "Beacon");
        hashMap.put("minecraft:skull", "Skull");
        hashMap.put("minecraft:daylight_detector", "DLDetector");
        hashMap.put("minecraft:hopper", "Hopper");
        hashMap.put("minecraft:banner", "Banner");
        hashMap.put("minecraft:flower_pot", "FlowerPot");
        hashMap.put("minecraft:repeating_command_block", "CommandBlock");
        hashMap.put("minecraft:chain_command_block", "CommandBlock");
        hashMap.put("minecraft:standing_sign", "Sign");
        hashMap.put("minecraft:wall_sign", "Sign");
        hashMap.put("minecraft:piston_head", "Piston");
        hashMap.put("minecraft:daylight_detector_inverted", "DLDetector");
        hashMap.put("minecraft:unpowered_comparator", "Comparator");
        hashMap.put("minecraft:powered_comparator", "Comparator");
        hashMap.put("minecraft:wall_banner", "Banner");
        hashMap.put("minecraft:standing_banner", "Banner");
        hashMap.put("minecraft:structure_block", "Structure");
        hashMap.put("minecraft:end_portal", "Airportal");
        hashMap.put("minecraft:end_gateway", "EndGateway");
        hashMap.put("minecraft:shield", "Banner");
    });
    protected static final Hook.HookFunction field_5747 = new Hook.HookFunction(){

        public <T> T apply(DynamicOps<T> dynamicOps, T object) {
            return Schema99.method_5359(new Dynamic(dynamicOps, object), field_5748, "ArmorStand");
        }
    };

    public Schema99(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    protected static TypeTemplate targetEquipment(Schema schema) {
        return DSL.optionalFields((String)"Equipment", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)));
    }

    protected static void targetEquipment(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {
        schema.register(map, entityId, () -> Schema99.targetEquipment(schema));
    }

    protected static void targetInTile(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {
        schema.register(map, entityId, () -> DSL.optionalFields((String)"inTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)));
    }

    protected static void targetDisplayTile(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {
        schema.register(map, entityId, () -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)));
    }

    protected static void targetItems(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {
        schema.register(map, entityId, () -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema))));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        HashMap map = Maps.newHashMap();
        schema.register((Map)map, "Item", string -> DSL.optionalFields((String)"Item", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema)));
        schema.registerSimple((Map)map, "XPOrb");
        Schema99.targetInTile(schema, map, "ThrownEgg");
        schema.registerSimple((Map)map, "LeashKnot");
        schema.registerSimple((Map)map, "Painting");
        schema.register((Map)map, "Arrow", string -> DSL.optionalFields((String)"inTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)));
        schema.register((Map)map, "TippedArrow", string -> DSL.optionalFields((String)"inTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)));
        schema.register((Map)map, "SpectralArrow", string -> DSL.optionalFields((String)"inTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)));
        Schema99.targetInTile(schema, map, "Snowball");
        Schema99.targetInTile(schema, map, "Fireball");
        Schema99.targetInTile(schema, map, "SmallFireball");
        Schema99.targetInTile(schema, map, "ThrownEnderpearl");
        schema.registerSimple((Map)map, "EyeOfEnderSignal");
        schema.register((Map)map, "ThrownPotion", string -> DSL.optionalFields((String)"inTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema), (String)"Potion", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema)));
        Schema99.targetInTile(schema, map, "ThrownExpBottle");
        schema.register((Map)map, "ItemFrame", string -> DSL.optionalFields((String)"Item", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema)));
        Schema99.targetInTile(schema, map, "WitherSkull");
        schema.registerSimple((Map)map, "PrimedTnt");
        schema.register((Map)map, "FallingSand", string -> DSL.optionalFields((String)"Block", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema), (String)"TileEntityData", (TypeTemplate)TypeReferences.BLOCK_ENTITY.in(schema)));
        schema.register((Map)map, "FireworksRocketEntity", string -> DSL.optionalFields((String)"FireworksItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema)));
        schema.registerSimple((Map)map, "Boat");
        schema.register((Map)map, "Minecart", () -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema))));
        Schema99.targetDisplayTile(schema, map, "MinecartRideable");
        schema.register((Map)map, "MinecartChest", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema))));
        Schema99.targetDisplayTile(schema, map, "MinecartFurnace");
        Schema99.targetDisplayTile(schema, map, "MinecartTNT");
        schema.register((Map)map, "MinecartSpawner", () -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema), (TypeTemplate)TypeReferences.UNTAGGED_SPAWNER.in(schema)));
        schema.register((Map)map, "MinecartHopper", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema))));
        Schema99.targetDisplayTile(schema, map, "MinecartCommandBlock");
        Schema99.targetEquipment(schema, map, "ArmorStand");
        Schema99.targetEquipment(schema, map, "Creeper");
        Schema99.targetEquipment(schema, map, "Skeleton");
        Schema99.targetEquipment(schema, map, "Spider");
        Schema99.targetEquipment(schema, map, "Giant");
        Schema99.targetEquipment(schema, map, "Zombie");
        Schema99.targetEquipment(schema, map, "Slime");
        Schema99.targetEquipment(schema, map, "Ghast");
        Schema99.targetEquipment(schema, map, "PigZombie");
        schema.register((Map)map, "Enderman", string -> DSL.optionalFields((String)"carried", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema), (TypeTemplate)Schema99.targetEquipment(schema)));
        Schema99.targetEquipment(schema, map, "CaveSpider");
        Schema99.targetEquipment(schema, map, "Silverfish");
        Schema99.targetEquipment(schema, map, "Blaze");
        Schema99.targetEquipment(schema, map, "LavaSlime");
        Schema99.targetEquipment(schema, map, "EnderDragon");
        Schema99.targetEquipment(schema, map, "WitherBoss");
        Schema99.targetEquipment(schema, map, "Bat");
        Schema99.targetEquipment(schema, map, "Witch");
        Schema99.targetEquipment(schema, map, "Endermite");
        Schema99.targetEquipment(schema, map, "Guardian");
        Schema99.targetEquipment(schema, map, "Pig");
        Schema99.targetEquipment(schema, map, "Sheep");
        Schema99.targetEquipment(schema, map, "Cow");
        Schema99.targetEquipment(schema, map, "Chicken");
        Schema99.targetEquipment(schema, map, "Squid");
        Schema99.targetEquipment(schema, map, "Wolf");
        Schema99.targetEquipment(schema, map, "MushroomCow");
        Schema99.targetEquipment(schema, map, "SnowMan");
        Schema99.targetEquipment(schema, map, "Ozelot");
        Schema99.targetEquipment(schema, map, "VillagerGolem");
        schema.register((Map)map, "EntityHorse", string -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)), (String)"ArmorItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (String)"SaddleItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (TypeTemplate)Schema99.targetEquipment(schema)));
        Schema99.targetEquipment(schema, map, "Rabbit");
        schema.register((Map)map, "Villager", string -> DSL.optionalFields((String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)), (String)"Offers", (TypeTemplate)DSL.optionalFields((String)"Recipes", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"buy", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (String)"buyB", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (String)"sell", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema)))), (TypeTemplate)Schema99.targetEquipment(schema)));
        schema.registerSimple((Map)map, "EnderCrystal");
        schema.registerSimple((Map)map, "AreaEffectCloud");
        schema.registerSimple((Map)map, "ShulkerBullet");
        Schema99.targetEquipment(schema, map, "Shulker");
        return map;
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        HashMap map = Maps.newHashMap();
        Schema99.targetItems(schema, map, "Furnace");
        Schema99.targetItems(schema, map, "Chest");
        schema.registerSimple((Map)map, "EnderChest");
        schema.register((Map)map, "RecordPlayer", string -> DSL.optionalFields((String)"RecordItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema)));
        Schema99.targetItems(schema, map, "Trap");
        Schema99.targetItems(schema, map, "Dropper");
        schema.registerSimple((Map)map, "Sign");
        schema.register((Map)map, "MobSpawner", string -> TypeReferences.UNTAGGED_SPAWNER.in(schema));
        schema.registerSimple((Map)map, "Music");
        schema.registerSimple((Map)map, "Piston");
        Schema99.targetItems(schema, map, "Cauldron");
        schema.registerSimple((Map)map, "EnchantTable");
        schema.registerSimple((Map)map, "Airportal");
        schema.registerSimple((Map)map, "Control");
        schema.registerSimple((Map)map, "Beacon");
        schema.registerSimple((Map)map, "Skull");
        schema.registerSimple((Map)map, "DLDetector");
        Schema99.targetItems(schema, map, "Hopper");
        schema.registerSimple((Map)map, "Comparator");
        schema.register((Map)map, "FlowerPot", string -> DSL.optionalFields((String)"Item", (TypeTemplate)DSL.or((TypeTemplate)DSL.constType((Type)DSL.intType()), (TypeTemplate)TypeReferences.ITEM_NAME.in(schema))));
        schema.registerSimple((Map)map, "Banner");
        schema.registerSimple((Map)map, "Structure");
        schema.registerSimple((Map)map, "EndGateway");
        return map;
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
        schema.registerType(false, TypeReferences.LEVEL, DSL::remainder);
        schema.registerType(false, TypeReferences.PLAYER, () -> DSL.optionalFields((String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)), (String)"EnderItems", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema))));
        schema.registerType(false, TypeReferences.CHUNK, () -> DSL.fields((String)"Level", (TypeTemplate)DSL.optionalFields((String)"Entities", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ENTITY_TREE.in(schema)), (String)"TileEntities", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.BLOCK_ENTITY.in(schema)), (String)"TileTicks", (TypeTemplate)DSL.list((TypeTemplate)DSL.fields((String)"i", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema))))));
        schema.registerType(true, TypeReferences.BLOCK_ENTITY, () -> DSL.taggedChoiceLazy((String)"id", (Type)DSL.string(), (Map)blockEntityTypes));
        schema.registerType(true, TypeReferences.ENTITY_TREE, () -> DSL.optionalFields((String)"Riding", (TypeTemplate)TypeReferences.ENTITY_TREE.in(schema), (TypeTemplate)TypeReferences.ENTITY.in(schema)));
        schema.registerType(false, TypeReferences.ENTITY_NAME, () -> DSL.constType(IdentifierNormalizingSchema.getIdentifierType()));
        schema.registerType(true, TypeReferences.ENTITY, () -> DSL.taggedChoiceLazy((String)"id", (Type)DSL.string(), (Map)entityTypes));
        schema.registerType(true, TypeReferences.ITEM_STACK, () -> DSL.hook((TypeTemplate)DSL.optionalFields((String)"id", (TypeTemplate)DSL.or((TypeTemplate)DSL.constType((Type)DSL.intType()), (TypeTemplate)TypeReferences.ITEM_NAME.in(schema)), (String)"tag", (TypeTemplate)DSL.optionalFields((String)"EntityTag", (TypeTemplate)TypeReferences.ENTITY_TREE.in(schema), (String)"BlockEntityTag", (TypeTemplate)TypeReferences.BLOCK_ENTITY.in(schema), (String)"CanDestroy", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)), (String)"CanPlaceOn", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)))), (Hook.HookFunction)field_5747, (Hook.HookFunction)Hook.HookFunction.IDENTITY));
        schema.registerType(false, TypeReferences.OPTIONS, DSL::remainder);
        schema.registerType(false, TypeReferences.BLOCK_NAME, () -> DSL.or((TypeTemplate)DSL.constType((Type)DSL.intType()), (TypeTemplate)DSL.constType(IdentifierNormalizingSchema.getIdentifierType())));
        schema.registerType(false, TypeReferences.ITEM_NAME, () -> DSL.constType(IdentifierNormalizingSchema.getIdentifierType()));
        schema.registerType(false, TypeReferences.STATS, DSL::remainder);
        schema.registerType(false, TypeReferences.SAVED_DATA, () -> DSL.optionalFields((String)"data", (TypeTemplate)DSL.optionalFields((String)"Features", (TypeTemplate)DSL.compoundList((TypeTemplate)TypeReferences.STRUCTURE_FEATURE.in(schema)), (String)"Objectives", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.OBJECTIVE.in(schema)), (String)"Teams", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.TEAM.in(schema)))));
        schema.registerType(false, TypeReferences.STRUCTURE_FEATURE, DSL::remainder);
        schema.registerType(false, TypeReferences.OBJECTIVE, DSL::remainder);
        schema.registerType(false, TypeReferences.TEAM, DSL::remainder);
        schema.registerType(true, TypeReferences.UNTAGGED_SPAWNER, DSL::remainder);
        schema.registerType(false, TypeReferences.POI_CHUNK, DSL::remainder);
        schema.registerType(true, TypeReferences.CHUNK_GENERATOR_SETTINGS, DSL::remainder);
        schema.registerType(false, TypeReferences.ENTITY_CHUNK, () -> DSL.optionalFields((String)"Entities", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ENTITY_TREE.in(schema))));
    }

    protected static <T> T method_5359(Dynamic<T> dynamic, Map<String, String> map, String string) {
        return (T)dynamic.update("tag", dynamic22 -> dynamic22.update("BlockEntityTag", dynamic2 -> {
            String string = dynamic.get("id").asString().result().map(IdentifierNormalizingSchema::normalize).orElse("minecraft:air");
            if (!"minecraft:air".equals(string)) {
                String string2 = (String)map.get(string);
                if (string2 == null) {
                    LOGGER.warn("Unable to resolve BlockEntity for ItemStack: {}", (Object)string);
                } else {
                    return dynamic2.set("id", dynamic.createString(string2));
                }
            }
            return dynamic2;
        }).update("EntityTag", dynamic2 -> {
            String string2 = dynamic.get("id").asString("");
            if ("minecraft:armor_stand".equals(IdentifierNormalizingSchema.normalize(string2))) {
                return dynamic2.set("id", dynamic.createString(string));
            }
            return dynamic2;
        })).getValue();
    }
}

