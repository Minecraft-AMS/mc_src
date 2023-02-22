/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.village;

import com.google.common.collect.ImmutableSet;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.PointOfInterestTypeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.Nullable;

public record VillagerProfession(String id, Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation, Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound) {
    public static final Predicate<RegistryEntry<PointOfInterestType>> IS_ACQUIRABLE_JOB_SITE = poiType -> poiType.isIn(PointOfInterestTypeTags.ACQUIRABLE_JOB_SITE);
    public static final VillagerProfession NONE = VillagerProfession.register("none", PointOfInterestType.NONE, IS_ACQUIRABLE_JOB_SITE, null);
    public static final VillagerProfession ARMORER = VillagerProfession.register("armorer", PointOfInterestTypes.ARMORER, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER);
    public static final VillagerProfession BUTCHER = VillagerProfession.register("butcher", PointOfInterestTypes.BUTCHER, SoundEvents.ENTITY_VILLAGER_WORK_BUTCHER);
    public static final VillagerProfession CARTOGRAPHER = VillagerProfession.register("cartographer", PointOfInterestTypes.CARTOGRAPHER, SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER);
    public static final VillagerProfession CLERIC = VillagerProfession.register("cleric", PointOfInterestTypes.CLERIC, SoundEvents.ENTITY_VILLAGER_WORK_CLERIC);
    public static final VillagerProfession FARMER = VillagerProfession.register("farmer", PointOfInterestTypes.FARMER, (ImmutableSet<Item>)ImmutableSet.of((Object)Items.WHEAT, (Object)Items.WHEAT_SEEDS, (Object)Items.BEETROOT_SEEDS, (Object)Items.BONE_MEAL), (ImmutableSet<Block>)ImmutableSet.of((Object)Blocks.FARMLAND), SoundEvents.ENTITY_VILLAGER_WORK_FARMER);
    public static final VillagerProfession FISHERMAN = VillagerProfession.register("fisherman", PointOfInterestTypes.FISHERMAN, SoundEvents.ENTITY_VILLAGER_WORK_FISHERMAN);
    public static final VillagerProfession FLETCHER = VillagerProfession.register("fletcher", PointOfInterestTypes.FLETCHER, SoundEvents.ENTITY_VILLAGER_WORK_FLETCHER);
    public static final VillagerProfession LEATHERWORKER = VillagerProfession.register("leatherworker", PointOfInterestTypes.LEATHERWORKER, SoundEvents.ENTITY_VILLAGER_WORK_LEATHERWORKER);
    public static final VillagerProfession LIBRARIAN = VillagerProfession.register("librarian", PointOfInterestTypes.LIBRARIAN, SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN);
    public static final VillagerProfession MASON = VillagerProfession.register("mason", PointOfInterestTypes.MASON, SoundEvents.ENTITY_VILLAGER_WORK_MASON);
    public static final VillagerProfession NITWIT = VillagerProfession.register("nitwit", PointOfInterestType.NONE, PointOfInterestType.NONE, null);
    public static final VillagerProfession SHEPHERD = VillagerProfession.register("shepherd", PointOfInterestTypes.SHEPHERD, SoundEvents.ENTITY_VILLAGER_WORK_SHEPHERD);
    public static final VillagerProfession TOOLSMITH = VillagerProfession.register("toolsmith", PointOfInterestTypes.TOOLSMITH, SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH);
    public static final VillagerProfession WEAPONSMITH = VillagerProfession.register("weaponsmith", PointOfInterestTypes.WEAPONSMITH, SoundEvents.ENTITY_VILLAGER_WORK_WEAPONSMITH);

    @Override
    public String toString() {
        return this.id;
    }

    private static VillagerProfession register(String id, RegistryKey<PointOfInterestType> heldWorkstation, @Nullable SoundEvent workSound) {
        return VillagerProfession.register(id, entry -> entry.matchesKey(heldWorkstation), entry -> entry.matchesKey(heldWorkstation), workSound);
    }

    private static VillagerProfession register(String id, Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation, Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation, @Nullable SoundEvent workSound) {
        return VillagerProfession.register(id, heldWorkstation, acquirableWorkstation, (ImmutableSet<Item>)ImmutableSet.of(), (ImmutableSet<Block>)ImmutableSet.of(), workSound);
    }

    private static VillagerProfession register(String id, RegistryKey<PointOfInterestType> heldWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound) {
        return VillagerProfession.register(id, entry -> entry.matchesKey(heldWorkstation), entry -> entry.matchesKey(heldWorkstation), gatherableItems, secondaryJobSites, workSound);
    }

    private static VillagerProfession register(String id, Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation, Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound) {
        return Registry.register(Registry.VILLAGER_PROFESSION, new Identifier(id), new VillagerProfession(id, heldWorkstation, acquirableWorkstation, gatherableItems, secondaryJobSites, workSound));
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{VillagerProfession.class, "name;heldJobSite;acquirableJobSite;requestedItems;secondaryPoi;workSound", "id", "heldWorkstation", "acquirableWorkstation", "gatherableItems", "secondaryJobSites", "workSound"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{VillagerProfession.class, "name;heldJobSite;acquirableJobSite;requestedItems;secondaryPoi;workSound", "id", "heldWorkstation", "acquirableWorkstation", "gatherableItems", "secondaryJobSites", "workSound"}, this, object);
    }
}

