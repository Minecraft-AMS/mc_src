/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.world.gen.feature.FeatureConfig;

public class OreFeatureConfig
implements FeatureConfig {
    public static final Codec<OreFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.list(Target.CODEC).fieldOf("targets").forGetter(oreFeatureConfig -> oreFeatureConfig.targets), (App)Codec.intRange((int)0, (int)64).fieldOf("size").forGetter(oreFeatureConfig -> oreFeatureConfig.size), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("discard_chance_on_air_exposure").forGetter(oreFeatureConfig -> Float.valueOf(oreFeatureConfig.discardOnAirChance))).apply((Applicative)instance, OreFeatureConfig::new));
    public final List<Target> targets;
    public final int size;
    public final float discardOnAirChance;

    public OreFeatureConfig(List<Target> targets, int size, float discardOnAirChance) {
        this.size = size;
        this.targets = targets;
        this.discardOnAirChance = discardOnAirChance;
    }

    public OreFeatureConfig(List<Target> targets, int size) {
        this(targets, size, 0.0f);
    }

    public OreFeatureConfig(RuleTest test, BlockState state, int size, float discardOnAirChance) {
        this((List<Target>)ImmutableList.of((Object)new Target(test, state)), size, discardOnAirChance);
    }

    public OreFeatureConfig(RuleTest test, BlockState state, int size) {
        this((List<Target>)ImmutableList.of((Object)new Target(test, state)), size, 0.0f);
    }

    public static Target createTarget(RuleTest test, BlockState state) {
        return new Target(test, state);
    }

    public static class Target {
        public static final Codec<Target> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)RuleTest.TYPE_CODEC.fieldOf("target").forGetter(target -> target.target), (App)BlockState.CODEC.fieldOf("state").forGetter(target -> target.state)).apply((Applicative)instance, Target::new));
        public final RuleTest target;
        public final BlockState state;

        Target(RuleTest ruleTest, BlockState blockState) {
            this.target = ruleTest;
            this.state = blockState;
        }
    }

    public static final class Rules {
        public static final RuleTest BASE_STONE_OVERWORLD = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
        public static final RuleTest STONE_ORE_REPLACEABLES = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        public static final RuleTest DEEPSLATE_ORE_REPLACEABLES = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        public static final RuleTest NETHERRACK = new BlockMatchRuleTest(Blocks.NETHERRACK);
        public static final RuleTest BASE_STONE_NETHER = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);
    }
}

