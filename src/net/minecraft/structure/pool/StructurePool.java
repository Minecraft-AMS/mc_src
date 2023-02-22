/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrays
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructurePool {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int field_31523 = Integer.MIN_VALUE;
    public static final Codec<StructurePool> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Identifier.CODEC.fieldOf("name").forGetter(StructurePool::getId), (App)Identifier.CODEC.fieldOf("fallback").forGetter(StructurePool::getTerminatorsId), (App)Codec.mapPair((MapCodec)StructurePoolElement.CODEC.fieldOf("element"), (MapCodec)Codec.intRange((int)1, (int)150).fieldOf("weight")).codec().listOf().fieldOf("elements").forGetter(structurePool -> structurePool.elementCounts)).apply((Applicative)instance, StructurePool::new));
    public static final Codec<Supplier<StructurePool>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.STRUCTURE_POOL_KEY, CODEC);
    private final Identifier id;
    private final List<Pair<StructurePoolElement, Integer>> elementCounts;
    private final List<StructurePoolElement> elements;
    private final Identifier terminatorsId;
    private int highestY = Integer.MIN_VALUE;

    public StructurePool(Identifier id, Identifier terminatorsId, List<Pair<StructurePoolElement, Integer>> elementCounts) {
        this.id = id;
        this.elementCounts = elementCounts;
        this.elements = Lists.newArrayList();
        for (Pair<StructurePoolElement, Integer> pair : elementCounts) {
            StructurePoolElement structurePoolElement = (StructurePoolElement)pair.getFirst();
            for (int i = 0; i < (Integer)pair.getSecond(); ++i) {
                this.elements.add(structurePoolElement);
            }
        }
        this.terminatorsId = terminatorsId;
    }

    public StructurePool(Identifier id, Identifier terminatorsId, List<Pair<Function<Projection, ? extends StructurePoolElement>, Integer>> elementCounts, Projection projection) {
        this.id = id;
        this.elementCounts = Lists.newArrayList();
        this.elements = Lists.newArrayList();
        for (Pair<Function<Projection, ? extends StructurePoolElement>, Integer> pair : elementCounts) {
            StructurePoolElement structurePoolElement = (StructurePoolElement)((Function)pair.getFirst()).apply(projection);
            this.elementCounts.add((Pair<StructurePoolElement, Integer>)Pair.of((Object)structurePoolElement, (Object)((Integer)pair.getSecond())));
            for (int i = 0; i < (Integer)pair.getSecond(); ++i) {
                this.elements.add(structurePoolElement);
            }
        }
        this.terminatorsId = terminatorsId;
    }

    public int getHighestY(StructureManager structureManager) {
        if (this.highestY == Integer.MIN_VALUE) {
            this.highestY = this.elements.stream().filter(structurePoolElement -> structurePoolElement != EmptyPoolElement.INSTANCE).mapToInt(structurePoolElement -> structurePoolElement.getBoundingBox(structureManager, BlockPos.ORIGIN, BlockRotation.NONE).getBlockCountY()).max().orElse(0);
        }
        return this.highestY;
    }

    public Identifier getTerminatorsId() {
        return this.terminatorsId;
    }

    public StructurePoolElement getRandomElement(Random random) {
        return this.elements.get(random.nextInt(this.elements.size()));
    }

    public List<StructurePoolElement> getElementIndicesInRandomOrder(Random random) {
        return ImmutableList.copyOf((Object[])((StructurePoolElement[])ObjectArrays.shuffle((Object[])this.elements.toArray(new StructurePoolElement[0]), (Random)random)));
    }

    public Identifier getId() {
        return this.id;
    }

    public int getElementCount() {
        return this.elements.size();
    }

    public static final class Projection
    extends Enum<Projection>
    implements StringIdentifiable {
        public static final /* enum */ Projection TERRAIN_MATCHING = new Projection("terrain_matching", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new GravityStructureProcessor(Heightmap.Type.WORLD_SURFACE_WG, -1)));
        public static final /* enum */ Projection RIGID = new Projection("rigid", (ImmutableList<StructureProcessor>)ImmutableList.of());
        public static final Codec<Projection> CODEC;
        private static final Map<String, Projection> PROJECTIONS_BY_ID;
        private final String id;
        private final ImmutableList<StructureProcessor> processors;
        private static final /* synthetic */ Projection[] field_16683;

        public static Projection[] values() {
            return (Projection[])field_16683.clone();
        }

        public static Projection valueOf(String string) {
            return Enum.valueOf(Projection.class, string);
        }

        private Projection(String id, ImmutableList<StructureProcessor> processors) {
            this.id = id;
            this.processors = processors;
        }

        public String getId() {
            return this.id;
        }

        public static Projection getById(String id) {
            return PROJECTIONS_BY_ID.get(id);
        }

        public ImmutableList<StructureProcessor> getProcessors() {
            return this.processors;
        }

        @Override
        public String asString() {
            return this.id;
        }

        private static /* synthetic */ Projection[] method_36758() {
            return new Projection[]{TERRAIN_MATCHING, RIGID};
        }

        static {
            field_16683 = Projection.method_36758();
            CODEC = StringIdentifiable.createCodec(Projection::values, Projection::getById);
            PROJECTIONS_BY_ID = Arrays.stream(Projection.values()).collect(Collectors.toMap(Projection::getId, projection -> projection));
        }
    }
}

