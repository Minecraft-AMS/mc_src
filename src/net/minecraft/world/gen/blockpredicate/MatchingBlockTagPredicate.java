/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.blockpredicate.OffsetPredicate;

public class MatchingBlockTagPredicate
extends OffsetPredicate {
    final TagKey<Block> tag;
    public static final Codec<MatchingBlockTagPredicate> CODEC = RecordCodecBuilder.create(instance -> MatchingBlockTagPredicate.registerOffsetField(instance).and((App)TagKey.unprefixedCodec(RegistryKeys.BLOCK).fieldOf("tag").forGetter(predicate -> predicate.tag)).apply((Applicative)instance, MatchingBlockTagPredicate::new));

    protected MatchingBlockTagPredicate(Vec3i offset, TagKey<Block> tag) {
        super(offset);
        this.tag = tag;
    }

    @Override
    protected boolean test(BlockState state) {
        return state.isIn(this.tag);
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.MATCHING_BLOCK_TAG;
    }
}

