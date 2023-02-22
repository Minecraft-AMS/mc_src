/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.dimension;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.io.File;
import java.util.function.BiFunction;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.dimension.TheNetherDimension;
import org.jetbrains.annotations.Nullable;

public class DimensionType
implements DynamicSerializable {
    public static final DimensionType OVERWORLD = DimensionType.register("overworld", new DimensionType(1, "", "", OverworldDimension::new, true));
    public static final DimensionType THE_NETHER = DimensionType.register("the_nether", new DimensionType(0, "_nether", "DIM-1", TheNetherDimension::new, false));
    public static final DimensionType THE_END = DimensionType.register("the_end", new DimensionType(2, "_end", "DIM1", TheEndDimension::new, false));
    private final int id;
    private final String suffix;
    private final String saveDir;
    private final BiFunction<World, DimensionType, ? extends Dimension> factory;
    private final boolean hasSkyLight;

    private static DimensionType register(String id, DimensionType dimension) {
        return Registry.register(Registry.DIMENSION, dimension.id, id, dimension);
    }

    protected DimensionType(int dimensionId, String suffix, String saveDir, BiFunction<World, DimensionType, ? extends Dimension> factory, boolean hasSkylight) {
        this.id = dimensionId;
        this.suffix = suffix;
        this.saveDir = saveDir;
        this.factory = factory;
        this.hasSkyLight = hasSkylight;
    }

    public static DimensionType deserialize(Dynamic<?> dynamic) {
        return Registry.DIMENSION.get(new Identifier(dynamic.asString("")));
    }

    public static Iterable<DimensionType> getAll() {
        return Registry.DIMENSION;
    }

    public int getRawId() {
        return this.id + -1;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public File getSaveDirectory(File root) {
        if (this.saveDir.isEmpty()) {
            return root;
        }
        return new File(root, this.saveDir);
    }

    public Dimension create(World world) {
        return this.factory.apply(world, this);
    }

    public String toString() {
        return DimensionType.getId(this).toString();
    }

    @Nullable
    public static DimensionType byRawId(int i) {
        return (DimensionType)Registry.DIMENSION.get(i - -1);
    }

    @Nullable
    public static DimensionType byId(Identifier identifier) {
        return Registry.DIMENSION.get(identifier);
    }

    @Nullable
    public static Identifier getId(DimensionType dimensionType) {
        return Registry.DIMENSION.getId(dimensionType);
    }

    public boolean hasSkyLight() {
        return this.hasSkyLight;
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        return (T)ops.createString(Registry.DIMENSION.getId(this).toString());
    }
}
