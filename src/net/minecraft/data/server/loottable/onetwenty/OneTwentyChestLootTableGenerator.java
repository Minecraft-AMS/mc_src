/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.server.loottable.onetwenty;

import java.util.function.BiConsumer;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.data.server.loottable.vanilla.VanillaChestLootTableGenerator;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

public class OneTwentyChestLootTableGenerator
implements LootTableGenerator {
    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> exporter) {
        exporter.accept(LootTables.PILLAGER_OUTPOST_CHEST, VanillaChestLootTableGenerator.createPillagerOutpostChestTableBuilder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(3)).with((LootPoolEntry.Builder<?>)((Object)((LeafEntry.Builder)ItemEntry.builder(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1)).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0f)))))));
        exporter.accept(LootTables.DESERT_PYRAMID_CHEST, VanillaChestLootTableGenerator.createDesertPyramidChestTableBuilder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(6)).with((LootPoolEntry.Builder<?>)((Object)((LeafEntry.Builder)ItemEntry.builder(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1)).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0f)))))));
        exporter.accept(LootTables.SHIPWRECK_MAP_CHEST, VanillaChestLootTableGenerator.createShipwreckMapChestTableBuilder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(5)).with((LootPoolEntry.Builder<?>)((Object)((LeafEntry.Builder)ItemEntry.builder(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1)).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0f)))))));
        exporter.accept(LootTables.SHIPWRECK_SUPPLY_CHEST, VanillaChestLootTableGenerator.createShipwreckSupplyChestTableBuilder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(5)).with((LootPoolEntry.Builder<?>)((Object)((LeafEntry.Builder)ItemEntry.builder(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1)).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0f)))))));
        exporter.accept(LootTables.SHIPWRECK_TREASURE_CHEST, VanillaChestLootTableGenerator.createShipwreckTreasureChestTableBuilder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(5)).with((LootPoolEntry.Builder<?>)((Object)((LeafEntry.Builder)ItemEntry.builder(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1)).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0f)))))));
        exporter.accept(LootTables.JUNGLE_TEMPLE_CHEST, VanillaChestLootTableGenerator.createJungleTempleChestTableBuilder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(2)).with((LootPoolEntry.Builder<?>)((Object)((LeafEntry.Builder)ItemEntry.builder(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1)).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0f)))))));
        exporter.accept(LootTables.ANCIENT_CITY_CHEST, VanillaChestLootTableGenerator.createAncientCityChestTableBuilder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(19)).with((LootPoolEntry.Builder<?>)ItemEntry.builder(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))));
        exporter.accept(LootTables.STRONGHOLD_CORRIDOR_CHEST, VanillaChestLootTableGenerator.createStrongholdCorridorChestTableBuilder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(9)).with((LootPoolEntry.Builder<?>)ItemEntry.builder(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))));
        exporter.accept(LootTables.STRONGHOLD_LIBRARY_CHEST, VanillaChestLootTableGenerator.createStrongholdLibraryChestTableBuilder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)ItemEntry.builder(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))));
        exporter.accept(LootTables.WOODLAND_MANSION_CHEST, VanillaChestLootTableGenerator.createWoodlandMansionChestTableBuilder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(1)).with((LootPoolEntry.Builder<?>)ItemEntry.builder(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))));
        exporter.accept(LootTables.BASTION_HOGLIN_STABLE_CHEST, VanillaChestLootTableGenerator.createBastionHoglinStableChestTableBuilder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(11)).with((LootPoolEntry.Builder<?>)ItemEntry.builder(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))).pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(9)).with((LootPoolEntry.Builder<?>)ItemEntry.builder(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).weight(1))));
        exporter.accept(LootTables.BASTION_BRIDGE_CHEST, VanillaChestLootTableGenerator.createBastionBridgeChestTableBuilder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(11)).with((LootPoolEntry.Builder<?>)ItemEntry.builder(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))).pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(9)).with((LootPoolEntry.Builder<?>)ItemEntry.builder(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).weight(1))));
        exporter.accept(LootTables.BASTION_OTHER_CHEST, VanillaChestLootTableGenerator.createBastionOtherChestTableBuilder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(11)).with((LootPoolEntry.Builder<?>)ItemEntry.builder(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))).pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(9)).with((LootPoolEntry.Builder<?>)ItemEntry.builder(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).weight(1))));
        exporter.accept(LootTables.BASTION_TREASURE_CHEST, VanillaChestLootTableGenerator.createBastionTreasureChestTableBuilder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(11)).with((LootPoolEntry.Builder<?>)ItemEntry.builder(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))).pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)ItemEntry.builder(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).weight(1))));
        exporter.accept(LootTables.NETHER_BRIDGE_CHEST, VanillaChestLootTableGenerator.createNetherBridgeChestTableBuilder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(14)).with((LootPoolEntry.Builder<?>)ItemEntry.builder(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))));
        exporter.accept(LootTables.END_CITY_TREASURE_CHEST, VanillaChestLootTableGenerator.createEndCityTreasureChestTableBuilder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)EmptyEntry.builder().weight(14)).with((LootPoolEntry.Builder<?>)ItemEntry.builder(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))));
    }
}

