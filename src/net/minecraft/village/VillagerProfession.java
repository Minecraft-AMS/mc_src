/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.village;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.poi.PointOfInterestType;

public class VillagerProfession {
    public static final VillagerProfession NONE = VillagerProfession.register("none", PointOfInterestType.UNEMPLOYED);
    public static final VillagerProfession ARMORER = VillagerProfession.register("armorer", PointOfInterestType.ARMORER);
    public static final VillagerProfession BUTCHER = VillagerProfession.register("butcher", PointOfInterestType.BUTCHER);
    public static final VillagerProfession CARTOGRAPHER = VillagerProfession.register("cartographer", PointOfInterestType.CARTOGRAPHER);
    public static final VillagerProfession CLERIC = VillagerProfession.register("cleric", PointOfInterestType.CLERIC);
    public static final VillagerProfession FARMER = VillagerProfession.register("farmer", PointOfInterestType.FARMER, (ImmutableSet<Item>)ImmutableSet.of((Object)Items.WHEAT, (Object)Items.WHEAT_SEEDS, (Object)Items.BEETROOT_SEEDS), (ImmutableSet<Block>)ImmutableSet.of((Object)Blocks.FARMLAND));
    public static final VillagerProfession FISHERMAN = VillagerProfession.register("fisherman", PointOfInterestType.FISHERMAN);
    public static final VillagerProfession FLETCHER = VillagerProfession.register("fletcher", PointOfInterestType.FLETCHER);
    public static final VillagerProfession LEATHERWORKER = VillagerProfession.register("leatherworker", PointOfInterestType.LEATHERWORKER);
    public static final VillagerProfession LIBRARIAN = VillagerProfession.register("librarian", PointOfInterestType.LIBRARIAN);
    public static final VillagerProfession MASON = VillagerProfession.register("mason", PointOfInterestType.MASON);
    public static final VillagerProfession NITWIT = VillagerProfession.register("nitwit", PointOfInterestType.NITWIT);
    public static final VillagerProfession SHEPHERD = VillagerProfession.register("shepherd", PointOfInterestType.SHEPHERD);
    public static final VillagerProfession TOOLSMITH = VillagerProfession.register("toolsmith", PointOfInterestType.TOOLSMITH);
    public static final VillagerProfession WEAPONSMITH = VillagerProfession.register("weaponsmith", PointOfInterestType.WEAPONSMITH);
    private final String id;
    private final PointOfInterestType workStation;
    private final ImmutableSet<Item> gatherableItems;
    private final ImmutableSet<Block> secondaryJobSites;

    private VillagerProfession(String id, PointOfInterestType workStation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites) {
        this.id = id;
        this.workStation = workStation;
        this.gatherableItems = gatherableItems;
        this.secondaryJobSites = secondaryJobSites;
    }

    public PointOfInterestType getWorkStation() {
        return this.workStation;
    }

    public ImmutableSet<Item> getGatherableItems() {
        return this.gatherableItems;
    }

    public ImmutableSet<Block> getSecondaryJobSites() {
        return this.secondaryJobSites;
    }

    public String toString() {
        return this.id;
    }

    static VillagerProfession register(String key, PointOfInterestType pointOfInterestType) {
        return VillagerProfession.register(key, pointOfInterestType, (ImmutableSet<Item>)ImmutableSet.of(), (ImmutableSet<Block>)ImmutableSet.of());
    }

    static VillagerProfession register(String string, PointOfInterestType pointOfInterestType, ImmutableSet<Item> immutableSet, ImmutableSet<Block> immutableSet2) {
        return Registry.register(Registry.VILLAGER_PROFESSION, new Identifier(string), new VillagerProfession(string, pointOfInterestType, immutableSet, immutableSet2));
    }
}

