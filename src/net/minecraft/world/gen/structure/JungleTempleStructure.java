/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.structure.JungleTempleGenerator;
import net.minecraft.world.gen.structure.BasicTempleStructure;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class JungleTempleStructure
extends BasicTempleStructure {
    public static final Codec<JungleTempleStructure> CODEC = JungleTempleStructure.createCodec(JungleTempleStructure::new);

    public JungleTempleStructure(Structure.Config config) {
        super(JungleTempleGenerator::new, 12, 15, config);
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.JUNGLE_TEMPLE;
    }
}

