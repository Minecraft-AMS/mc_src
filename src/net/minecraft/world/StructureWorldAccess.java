/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

import java.util.stream.Stream;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.feature.StructureFeature;

public interface StructureWorldAccess
extends ServerWorldAccess {
    public long getSeed();

    public Stream<? extends StructureStart<?>> getStructures(ChunkSectionPos var1, StructureFeature<?> var2);
}

