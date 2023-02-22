/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 */
package net.minecraft.world.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BedPart;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.poi.PointOfInterestType;

public class PointOfInterestTypes {
    public static final RegistryKey<PointOfInterestType> ARMORER = PointOfInterestTypes.of("armorer");
    public static final RegistryKey<PointOfInterestType> BUTCHER = PointOfInterestTypes.of("butcher");
    public static final RegistryKey<PointOfInterestType> CARTOGRAPHER = PointOfInterestTypes.of("cartographer");
    public static final RegistryKey<PointOfInterestType> CLERIC = PointOfInterestTypes.of("cleric");
    public static final RegistryKey<PointOfInterestType> FARMER = PointOfInterestTypes.of("farmer");
    public static final RegistryKey<PointOfInterestType> FISHERMAN = PointOfInterestTypes.of("fisherman");
    public static final RegistryKey<PointOfInterestType> FLETCHER = PointOfInterestTypes.of("fletcher");
    public static final RegistryKey<PointOfInterestType> LEATHERWORKER = PointOfInterestTypes.of("leatherworker");
    public static final RegistryKey<PointOfInterestType> LIBRARIAN = PointOfInterestTypes.of("librarian");
    public static final RegistryKey<PointOfInterestType> MASON = PointOfInterestTypes.of("mason");
    public static final RegistryKey<PointOfInterestType> SHEPHERD = PointOfInterestTypes.of("shepherd");
    public static final RegistryKey<PointOfInterestType> TOOLSMITH = PointOfInterestTypes.of("toolsmith");
    public static final RegistryKey<PointOfInterestType> WEAPONSMITH = PointOfInterestTypes.of("weaponsmith");
    public static final RegistryKey<PointOfInterestType> HOME = PointOfInterestTypes.of("home");
    public static final RegistryKey<PointOfInterestType> MEETING = PointOfInterestTypes.of("meeting");
    public static final RegistryKey<PointOfInterestType> BEEHIVE = PointOfInterestTypes.of("beehive");
    public static final RegistryKey<PointOfInterestType> BEE_NEST = PointOfInterestTypes.of("bee_nest");
    public static final RegistryKey<PointOfInterestType> NETHER_PORTAL = PointOfInterestTypes.of("nether_portal");
    public static final RegistryKey<PointOfInterestType> LODESTONE = PointOfInterestTypes.of("lodestone");
    public static final RegistryKey<PointOfInterestType> LIGHTNING_ROD = PointOfInterestTypes.of("lightning_rod");
    private static final Set<BlockState> BED_HEADS = (Set)ImmutableList.of((Object)Blocks.RED_BED, (Object)Blocks.BLACK_BED, (Object)Blocks.BLUE_BED, (Object)Blocks.BROWN_BED, (Object)Blocks.CYAN_BED, (Object)Blocks.GRAY_BED, (Object)Blocks.GREEN_BED, (Object)Blocks.LIGHT_BLUE_BED, (Object)Blocks.LIGHT_GRAY_BED, (Object)Blocks.LIME_BED, (Object)Blocks.MAGENTA_BED, (Object)Blocks.ORANGE_BED, (Object[])new Block[]{Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED}).stream().flatMap(block -> block.getStateManager().getStates().stream()).filter(blockState -> blockState.get(BedBlock.PART) == BedPart.HEAD).collect(ImmutableSet.toImmutableSet());
    private static final Set<BlockState> CAULDRONS = (Set)ImmutableList.of((Object)Blocks.CAULDRON, (Object)Blocks.LAVA_CAULDRON, (Object)Blocks.WATER_CAULDRON, (Object)Blocks.POWDER_SNOW_CAULDRON).stream().flatMap(block -> block.getStateManager().getStates().stream()).collect(ImmutableSet.toImmutableSet());
    private static final Map<BlockState, RegistryEntry<PointOfInterestType>> POI_STATES_TO_TYPE = Maps.newHashMap();
    protected static final Set<BlockState> POI_STATES = new ObjectOpenHashSet(POI_STATES_TO_TYPE.keySet());

    private static Set<BlockState> getStatesOfBlock(Block block) {
        return ImmutableSet.copyOf(block.getStateManager().getStates());
    }

    private static RegistryKey<PointOfInterestType> of(String id) {
        return RegistryKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY, new Identifier(id));
    }

    private static PointOfInterestType register(Registry<PointOfInterestType> registry, RegistryKey<PointOfInterestType> key, Set<BlockState> states, int ticketCount, int searchDistance) {
        PointOfInterestType pointOfInterestType = new PointOfInterestType(states, ticketCount, searchDistance);
        Registry.register(registry, key, pointOfInterestType);
        PointOfInterestTypes.registerStates(registry.entryOf(key));
        return pointOfInterestType;
    }

    private static void registerStates(RegistryEntry<PointOfInterestType> poiType) {
        poiType.value().blockStates().forEach(state -> {
            RegistryEntry<PointOfInterestType> registryEntry2 = POI_STATES_TO_TYPE.put((BlockState)state, poiType);
            if (registryEntry2 != null) {
                throw Util.throwOrPause(new IllegalStateException(String.format(Locale.ROOT, "%s is defined in more than one PoI type", state)));
            }
        });
    }

    public static Optional<RegistryEntry<PointOfInterestType>> getTypeForState(BlockState state) {
        return Optional.ofNullable(POI_STATES_TO_TYPE.get(state));
    }

    public static PointOfInterestType registerAndGetDefault(Registry<PointOfInterestType> registry) {
        PointOfInterestTypes.register(registry, ARMORER, PointOfInterestTypes.getStatesOfBlock(Blocks.BLAST_FURNACE), 1, 1);
        PointOfInterestTypes.register(registry, BUTCHER, PointOfInterestTypes.getStatesOfBlock(Blocks.SMOKER), 1, 1);
        PointOfInterestTypes.register(registry, CARTOGRAPHER, PointOfInterestTypes.getStatesOfBlock(Blocks.CARTOGRAPHY_TABLE), 1, 1);
        PointOfInterestTypes.register(registry, CLERIC, PointOfInterestTypes.getStatesOfBlock(Blocks.BREWING_STAND), 1, 1);
        PointOfInterestTypes.register(registry, FARMER, PointOfInterestTypes.getStatesOfBlock(Blocks.COMPOSTER), 1, 1);
        PointOfInterestTypes.register(registry, FISHERMAN, PointOfInterestTypes.getStatesOfBlock(Blocks.BARREL), 1, 1);
        PointOfInterestTypes.register(registry, FLETCHER, PointOfInterestTypes.getStatesOfBlock(Blocks.FLETCHING_TABLE), 1, 1);
        PointOfInterestTypes.register(registry, LEATHERWORKER, CAULDRONS, 1, 1);
        PointOfInterestTypes.register(registry, LIBRARIAN, PointOfInterestTypes.getStatesOfBlock(Blocks.LECTERN), 1, 1);
        PointOfInterestTypes.register(registry, MASON, PointOfInterestTypes.getStatesOfBlock(Blocks.STONECUTTER), 1, 1);
        PointOfInterestTypes.register(registry, SHEPHERD, PointOfInterestTypes.getStatesOfBlock(Blocks.LOOM), 1, 1);
        PointOfInterestTypes.register(registry, TOOLSMITH, PointOfInterestTypes.getStatesOfBlock(Blocks.SMITHING_TABLE), 1, 1);
        PointOfInterestTypes.register(registry, WEAPONSMITH, PointOfInterestTypes.getStatesOfBlock(Blocks.GRINDSTONE), 1, 1);
        PointOfInterestTypes.register(registry, HOME, BED_HEADS, 1, 1);
        PointOfInterestTypes.register(registry, MEETING, PointOfInterestTypes.getStatesOfBlock(Blocks.BELL), 32, 6);
        PointOfInterestTypes.register(registry, BEEHIVE, PointOfInterestTypes.getStatesOfBlock(Blocks.BEEHIVE), 0, 1);
        PointOfInterestTypes.register(registry, BEE_NEST, PointOfInterestTypes.getStatesOfBlock(Blocks.BEE_NEST), 0, 1);
        PointOfInterestTypes.register(registry, NETHER_PORTAL, PointOfInterestTypes.getStatesOfBlock(Blocks.NETHER_PORTAL), 0, 1);
        PointOfInterestTypes.register(registry, LODESTONE, PointOfInterestTypes.getStatesOfBlock(Blocks.LODESTONE), 0, 1);
        return PointOfInterestTypes.register(registry, LIGHTNING_ROD, PointOfInterestTypes.getStatesOfBlock(Blocks.LIGHTNING_ROD), 0, 1);
    }
}
