/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.server.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimMaterials;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.item.trim.ArmorTrimPatterns;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnArmorTrimsCommand {
    private static final Map<Pair<ArmorMaterial, EquipmentSlot>, Item> ARMOR_PIECES = Util.make(Maps.newHashMap(), map -> {
        map.put(Pair.of((Object)ArmorMaterials.CHAIN, (Object)((Object)EquipmentSlot.HEAD)), Items.CHAINMAIL_HELMET);
        map.put(Pair.of((Object)ArmorMaterials.CHAIN, (Object)((Object)EquipmentSlot.CHEST)), Items.CHAINMAIL_CHESTPLATE);
        map.put(Pair.of((Object)ArmorMaterials.CHAIN, (Object)((Object)EquipmentSlot.LEGS)), Items.CHAINMAIL_LEGGINGS);
        map.put(Pair.of((Object)ArmorMaterials.CHAIN, (Object)((Object)EquipmentSlot.FEET)), Items.CHAINMAIL_BOOTS);
        map.put(Pair.of((Object)ArmorMaterials.IRON, (Object)((Object)EquipmentSlot.HEAD)), Items.IRON_HELMET);
        map.put(Pair.of((Object)ArmorMaterials.IRON, (Object)((Object)EquipmentSlot.CHEST)), Items.IRON_CHESTPLATE);
        map.put(Pair.of((Object)ArmorMaterials.IRON, (Object)((Object)EquipmentSlot.LEGS)), Items.IRON_LEGGINGS);
        map.put(Pair.of((Object)ArmorMaterials.IRON, (Object)((Object)EquipmentSlot.FEET)), Items.IRON_BOOTS);
        map.put(Pair.of((Object)ArmorMaterials.GOLD, (Object)((Object)EquipmentSlot.HEAD)), Items.GOLDEN_HELMET);
        map.put(Pair.of((Object)ArmorMaterials.GOLD, (Object)((Object)EquipmentSlot.CHEST)), Items.GOLDEN_CHESTPLATE);
        map.put(Pair.of((Object)ArmorMaterials.GOLD, (Object)((Object)EquipmentSlot.LEGS)), Items.GOLDEN_LEGGINGS);
        map.put(Pair.of((Object)ArmorMaterials.GOLD, (Object)((Object)EquipmentSlot.FEET)), Items.GOLDEN_BOOTS);
        map.put(Pair.of((Object)ArmorMaterials.NETHERITE, (Object)((Object)EquipmentSlot.HEAD)), Items.NETHERITE_HELMET);
        map.put(Pair.of((Object)ArmorMaterials.NETHERITE, (Object)((Object)EquipmentSlot.CHEST)), Items.NETHERITE_CHESTPLATE);
        map.put(Pair.of((Object)ArmorMaterials.NETHERITE, (Object)((Object)EquipmentSlot.LEGS)), Items.NETHERITE_LEGGINGS);
        map.put(Pair.of((Object)ArmorMaterials.NETHERITE, (Object)((Object)EquipmentSlot.FEET)), Items.NETHERITE_BOOTS);
        map.put(Pair.of((Object)ArmorMaterials.DIAMOND, (Object)((Object)EquipmentSlot.HEAD)), Items.DIAMOND_HELMET);
        map.put(Pair.of((Object)ArmorMaterials.DIAMOND, (Object)((Object)EquipmentSlot.CHEST)), Items.DIAMOND_CHESTPLATE);
        map.put(Pair.of((Object)ArmorMaterials.DIAMOND, (Object)((Object)EquipmentSlot.LEGS)), Items.DIAMOND_LEGGINGS);
        map.put(Pair.of((Object)ArmorMaterials.DIAMOND, (Object)((Object)EquipmentSlot.FEET)), Items.DIAMOND_BOOTS);
        map.put(Pair.of((Object)ArmorMaterials.TURTLE, (Object)((Object)EquipmentSlot.HEAD)), Items.TURTLE_HELMET);
    });
    private static final List<RegistryKey<ArmorTrimPattern>> PATTERNS = List.of(ArmorTrimPatterns.SENTRY, ArmorTrimPatterns.DUNE, ArmorTrimPatterns.COAST, ArmorTrimPatterns.WILD, ArmorTrimPatterns.WARD, ArmorTrimPatterns.EYE, ArmorTrimPatterns.VEX, ArmorTrimPatterns.TIDE, ArmorTrimPatterns.SNOUT, ArmorTrimPatterns.RIB, ArmorTrimPatterns.SPIRE);
    private static final List<RegistryKey<ArmorTrimMaterial>> MATERIALS = List.of(ArmorTrimMaterials.QUARTZ, ArmorTrimMaterials.IRON, ArmorTrimMaterials.NETHERITE, ArmorTrimMaterials.REDSTONE, ArmorTrimMaterials.COPPER, ArmorTrimMaterials.GOLD, ArmorTrimMaterials.EMERALD, ArmorTrimMaterials.DIAMOND, ArmorTrimMaterials.LAPIS, ArmorTrimMaterials.AMETHYST);
    private static final ToIntFunction<RegistryKey<ArmorTrimPattern>> PATTERN_INDEX_GETTER = Util.lastIndexGetter(PATTERNS);
    private static final ToIntFunction<RegistryKey<ArmorTrimMaterial>> MATERIAL_INDEX_GETTER = Util.lastIndexGetter(MATERIALS);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("spawn_armor_trims").requires(source -> source.hasPermissionLevel(2) && source.getWorld().getEnabledFeatures().contains(FeatureFlags.UPDATE_1_20))).executes(context -> SpawnArmorTrimsCommand.execute((ServerCommandSource)context.getSource(), ((ServerCommandSource)context.getSource()).getPlayerOrThrow())));
    }

    private static int execute(ServerCommandSource source, PlayerEntity player) {
        World world = player.getWorld();
        DefaultedList<ArmorTrim> defaultedList = DefaultedList.of();
        Registry<ArmorTrimPattern> registry = world.getRegistryManager().get(RegistryKeys.TRIM_PATTERN);
        Registry<ArmorTrimMaterial> registry2 = world.getRegistryManager().get(RegistryKeys.TRIM_MATERIAL);
        registry.stream().sorted(Comparator.comparing(pattern -> PATTERN_INDEX_GETTER.applyAsInt(registry.getKey((ArmorTrimPattern)pattern).orElse(null)))).forEachOrdered(pattern -> registry2.stream().sorted(Comparator.comparing(material -> MATERIAL_INDEX_GETTER.applyAsInt(registry2.getKey((ArmorTrimMaterial)material).orElse(null)))).forEachOrdered(material -> defaultedList.add(new ArmorTrim(registry2.getEntry((ArmorTrimMaterial)material), registry.getEntry((ArmorTrimPattern)pattern)))));
        BlockPos blockPos = player.getBlockPos().offset(player.getHorizontalFacing(), 5);
        int i = ArmorMaterials.values().length - 1;
        double d = 3.0;
        int j = 0;
        int k = 0;
        for (ArmorTrim armorTrim : defaultedList) {
            for (ArmorMaterials armorMaterial : ArmorMaterials.values()) {
                if (armorMaterial == ArmorMaterials.LEATHER) continue;
                double e = (double)blockPos.getX() + 0.5 - (double)(j % registry2.size()) * 3.0;
                double f = (double)blockPos.getY() + 0.5 + (double)(k % i) * 3.0;
                double g = (double)blockPos.getZ() + 0.5 + (double)(j / registry2.size() * 10);
                ArmorStandEntity armorStandEntity = new ArmorStandEntity(world, e, f, g);
                armorStandEntity.setYaw(180.0f);
                armorStandEntity.setNoGravity(true);
                for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                    ArmorItem armorItem;
                    Item item = ARMOR_PIECES.get(Pair.of((Object)armorMaterial, (Object)((Object)equipmentSlot)));
                    if (item == null) continue;
                    ItemStack itemStack = new ItemStack(item);
                    ArmorTrim.apply(world.getRegistryManager(), itemStack, armorTrim);
                    armorStandEntity.equipStack(equipmentSlot, itemStack);
                    if (item instanceof ArmorItem && (armorItem = (ArmorItem)item).getMaterial() == ArmorMaterials.TURTLE) {
                        armorStandEntity.setCustomName(armorTrim.getPattern().value().getDescription(armorTrim.getMaterial()).copy().append(" ").append(armorTrim.getMaterial().value().description()));
                        armorStandEntity.setCustomNameVisible(true);
                        continue;
                    }
                    armorStandEntity.setInvisible(true);
                }
                world.spawnEntity(armorStandEntity);
                ++k;
            }
            ++j;
        }
        source.sendFeedback(Text.literal("Armorstands with trimmed armor spawned around you"), true);
        return 1;
    }
}

