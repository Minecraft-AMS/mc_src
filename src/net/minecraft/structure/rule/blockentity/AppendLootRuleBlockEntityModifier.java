/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.structure.rule.blockentity;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.structure.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.structure.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class AppendLootRuleBlockEntityModifier
implements RuleBlockEntityModifier {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<AppendLootRuleBlockEntityModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Identifier.CODEC.fieldOf("loot_table").forGetter(modifier -> modifier.lootTable)).apply((Applicative)instance, AppendLootRuleBlockEntityModifier::new));
    private final Identifier lootTable;

    public AppendLootRuleBlockEntityModifier(Identifier lootTable) {
        this.lootTable = lootTable;
    }

    @Override
    public NbtCompound modifyBlockEntityNbt(Random random, @Nullable NbtCompound nbt2) {
        NbtCompound nbtCompound = nbt2 == null ? new NbtCompound() : nbt2.copy();
        Identifier.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.lootTable).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent(nbt -> nbtCompound.put("LootTable", (NbtElement)nbt));
        nbtCompound.putLong("LootTableSeed", random.nextLong());
        return nbtCompound;
    }

    @Override
    public RuleBlockEntityModifierType<?> getType() {
        return RuleBlockEntityModifierType.APPEND_LOOT;
    }
}

