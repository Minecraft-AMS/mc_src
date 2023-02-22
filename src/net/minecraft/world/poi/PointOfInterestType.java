/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BedPart;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;
import org.jetbrains.annotations.Nullable;

public class PointOfInterestType {
    private static final Predicate<PointOfInterestType> IS_USED_BY_PROFESSION = pointOfInterestType -> Registry.VILLAGER_PROFESSION.stream().map(VillagerProfession::getWorkStation).collect(Collectors.toSet()).contains(pointOfInterestType);
    public static final Predicate<PointOfInterestType> ALWAYS_TRUE = pointOfInterestType -> true;
    private static final Set<BlockState> BED_STATES = (Set)ImmutableList.of((Object)Blocks.RED_BED, (Object)Blocks.BLACK_BED, (Object)Blocks.BLUE_BED, (Object)Blocks.BROWN_BED, (Object)Blocks.CYAN_BED, (Object)Blocks.GRAY_BED, (Object)Blocks.GREEN_BED, (Object)Blocks.LIGHT_BLUE_BED, (Object)Blocks.LIGHT_GRAY_BED, (Object)Blocks.LIME_BED, (Object)Blocks.MAGENTA_BED, (Object)Blocks.ORANGE_BED, (Object[])new Block[]{Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED}).stream().flatMap(block -> block.getStateManager().getStates().stream()).filter(blockState -> blockState.get(BedBlock.PART) == BedPart.HEAD).collect(ImmutableSet.toImmutableSet());
    private static final Map<BlockState, PointOfInterestType> BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE = Maps.newHashMap();
    public static final PointOfInterestType UNEMPLOYED = PointOfInterestType.register("unemployed", (Set<BlockState>)ImmutableSet.of(), 1, null, IS_USED_BY_PROFESSION, 1);
    public static final PointOfInterestType ARMORER = PointOfInterestType.register("armorer", PointOfInterestType.getAllStatesOf(Blocks.BLAST_FURNACE), 1, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER, 1);
    public static final PointOfInterestType BUTCHER = PointOfInterestType.register("butcher", PointOfInterestType.getAllStatesOf(Blocks.SMOKER), 1, SoundEvents.ENTITY_VILLAGER_WORK_BUTCHER, 1);
    public static final PointOfInterestType CARTOGRAPHER = PointOfInterestType.register("cartographer", PointOfInterestType.getAllStatesOf(Blocks.CARTOGRAPHY_TABLE), 1, SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER, 1);
    public static final PointOfInterestType CLERIC = PointOfInterestType.register("cleric", PointOfInterestType.getAllStatesOf(Blocks.BREWING_STAND), 1, SoundEvents.ENTITY_VILLAGER_WORK_CLERIC, 1);
    public static final PointOfInterestType FARMER = PointOfInterestType.register("farmer", PointOfInterestType.getAllStatesOf(Blocks.COMPOSTER), 1, SoundEvents.ENTITY_VILLAGER_WORK_FARMER, 1);
    public static final PointOfInterestType FISHERMAN = PointOfInterestType.register("fisherman", PointOfInterestType.getAllStatesOf(Blocks.BARREL), 1, SoundEvents.ENTITY_VILLAGER_WORK_FISHERMAN, 1);
    public static final PointOfInterestType FLETCHER = PointOfInterestType.register("fletcher", PointOfInterestType.getAllStatesOf(Blocks.FLETCHING_TABLE), 1, SoundEvents.ENTITY_VILLAGER_WORK_FLETCHER, 1);
    public static final PointOfInterestType LEATHERWORKER = PointOfInterestType.register("leatherworker", PointOfInterestType.getAllStatesOf(Blocks.CAULDRON), 1, SoundEvents.ENTITY_VILLAGER_WORK_LEATHERWORKER, 1);
    public static final PointOfInterestType LIBRARIAN = PointOfInterestType.register("librarian", PointOfInterestType.getAllStatesOf(Blocks.LECTERN), 1, SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN, 1);
    public static final PointOfInterestType MASON = PointOfInterestType.register("mason", PointOfInterestType.getAllStatesOf(Blocks.STONECUTTER), 1, SoundEvents.ENTITY_VILLAGER_WORK_MASON, 1);
    public static final PointOfInterestType NITWIT = PointOfInterestType.register("nitwit", (Set<BlockState>)ImmutableSet.of(), 1, null, 1);
    public static final PointOfInterestType SHEPHERD = PointOfInterestType.register("shepherd", PointOfInterestType.getAllStatesOf(Blocks.LOOM), 1, SoundEvents.ENTITY_VILLAGER_WORK_SHEPHERD, 1);
    public static final PointOfInterestType TOOLSMITH = PointOfInterestType.register("toolsmith", PointOfInterestType.getAllStatesOf(Blocks.SMITHING_TABLE), 1, SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH, 1);
    public static final PointOfInterestType WEAPONSMITH = PointOfInterestType.register("weaponsmith", PointOfInterestType.getAllStatesOf(Blocks.GRINDSTONE), 1, SoundEvents.ENTITY_VILLAGER_WORK_WEAPONSMITH, 1);
    public static final PointOfInterestType HOME = PointOfInterestType.register("home", BED_STATES, 1, null, 1);
    public static final PointOfInterestType MEETING = PointOfInterestType.register("meeting", PointOfInterestType.getAllStatesOf(Blocks.BELL), 32, null, 6);
    private final String id;
    private final Set<BlockState> blockStates;
    private final int ticketCount;
    @Nullable
    private final SoundEvent sound;
    private final Predicate<PointOfInterestType> completionCondition;
    private final int field_20298;

    private static Set<BlockState> getAllStatesOf(Block block) {
        return ImmutableSet.copyOf(block.getStateManager().getStates());
    }

    private PointOfInterestType(String string, Set<BlockState> set, int i, @Nullable SoundEvent soundEvent, Predicate<PointOfInterestType> predicate, int j) {
        this.id = string;
        this.blockStates = ImmutableSet.copyOf(set);
        this.ticketCount = i;
        this.sound = soundEvent;
        this.completionCondition = predicate;
        this.field_20298 = j;
    }

    private PointOfInterestType(String string, Set<BlockState> set, int i, @Nullable SoundEvent soundEvent, int j) {
        this.id = string;
        this.blockStates = ImmutableSet.copyOf(set);
        this.ticketCount = i;
        this.sound = soundEvent;
        this.completionCondition = pointOfInterestType -> pointOfInterestType == this;
        this.field_20298 = j;
    }

    public int getTicketCount() {
        return this.ticketCount;
    }

    public Predicate<PointOfInterestType> getCompletionCondition() {
        return this.completionCondition;
    }

    public int method_21648() {
        return this.field_20298;
    }

    public String toString() {
        return this.id;
    }

    @Nullable
    public SoundEvent getSound() {
        return this.sound;
    }

    private static PointOfInterestType register(String id, Set<BlockState> set, int i, @Nullable SoundEvent soundEvent, int j) {
        return PointOfInterestType.setup(Registry.POINT_OF_INTEREST_TYPE.add(new Identifier(id), new PointOfInterestType(id, set, i, soundEvent, j)));
    }

    private static PointOfInterestType register(String id, Set<BlockState> set, int i, @Nullable SoundEvent soundEvent, Predicate<PointOfInterestType> predicate, int j) {
        return PointOfInterestType.setup(Registry.POINT_OF_INTEREST_TYPE.add(new Identifier(id), new PointOfInterestType(id, set, i, soundEvent, predicate, j)));
    }

    private static PointOfInterestType setup(PointOfInterestType pointOfInterestType) {
        pointOfInterestType.blockStates.forEach(blockState -> {
            PointOfInterestType pointOfInterestType2 = BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.put((BlockState)blockState, pointOfInterestType);
            if (pointOfInterestType2 != null) {
                throw new IllegalStateException(String.format("%s is defined in too many tags", blockState));
            }
        });
        return pointOfInterestType;
    }

    public static Optional<PointOfInterestType> from(BlockState state) {
        return Optional.ofNullable(BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.get(state));
    }

    public static Stream<BlockState> getAllAssociatedStates() {
        return BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.keySet().stream();
    }
}

