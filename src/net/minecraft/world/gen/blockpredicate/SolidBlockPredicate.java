/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.blockpredicate.OffsetPredicate;

@Deprecated
public class SolidBlockPredicate
extends OffsetPredicate {
    public static final Codec<SolidBlockPredicate> CODEC = RecordCodecBuilder.create(instance -> SolidBlockPredicate.registerOffsetField(instance).apply((Applicative)instance, SolidBlockPredicate::new));

    public SolidBlockPredicate(Vec3i vec3i) {
        super(vec3i);
    }

    @Override
    protected boolean test(BlockState state) {
        return state.isSolid();
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.SOLID;
    }
}

