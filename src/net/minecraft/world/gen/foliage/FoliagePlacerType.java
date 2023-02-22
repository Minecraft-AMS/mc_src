/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.foliage.AcaciaFoliagePlacer;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.PineFoliagePlacer;
import net.minecraft.world.gen.foliage.SpruceFoliagePlacer;

public class FoliagePlacerType<P extends FoliagePlacer> {
    public static final FoliagePlacerType<BlobFoliagePlacer> BLOB_FOLIAGE_PLACER = FoliagePlacerType.register("blob_foliage_placer", BlobFoliagePlacer::new);
    public static final FoliagePlacerType<SpruceFoliagePlacer> SPRUCE_FOLIAGE_PLACER = FoliagePlacerType.register("spruce_foliage_placer", SpruceFoliagePlacer::new);
    public static final FoliagePlacerType<PineFoliagePlacer> PINE_FOLIAGE_PLACER = FoliagePlacerType.register("pine_foliage_placer", PineFoliagePlacer::new);
    public static final FoliagePlacerType<AcaciaFoliagePlacer> ACACIA_FOLIAGE_PLACER = FoliagePlacerType.register("acacia_foliage_placer", AcaciaFoliagePlacer::new);
    private final Function<Dynamic<?>, P> deserializer;

    private static <P extends FoliagePlacer> FoliagePlacerType<P> register(String id, Function<Dynamic<?>, P> deserializer) {
        return Registry.register(Registry.FOLIAGE_PLACER_TYPE, id, new FoliagePlacerType<P>(deserializer));
    }

    private FoliagePlacerType(Function<Dynamic<?>, P> deserializer) {
        this.deserializer = deserializer;
    }

    public P deserialize(Dynamic<?> dynamic) {
        return (P)((FoliagePlacer)this.deserializer.apply(dynamic));
    }
}

