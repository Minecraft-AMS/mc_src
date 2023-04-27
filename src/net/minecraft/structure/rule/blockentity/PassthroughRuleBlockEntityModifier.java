/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.structure.rule.blockentity;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.structure.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class PassthroughRuleBlockEntityModifier
implements RuleBlockEntityModifier {
    public static final PassthroughRuleBlockEntityModifier INSTANCE = new PassthroughRuleBlockEntityModifier();
    public static final Codec<PassthroughRuleBlockEntityModifier> CODEC = Codec.unit((Object)INSTANCE);

    @Override
    @Nullable
    public NbtCompound modifyBlockEntityNbt(Random random, @Nullable NbtCompound nbt) {
        return nbt;
    }

    @Override
    public RuleBlockEntityModifierType<?> getType() {
        return RuleBlockEntityModifierType.PASSTHROUGH;
    }
}

