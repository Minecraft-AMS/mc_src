/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.loot.provider.score;

import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.score.LootScoreProviderType;
import org.jetbrains.annotations.Nullable;

public interface LootScoreProvider {
    @Nullable
    public String getName(LootContext var1);

    public LootScoreProviderType getType();

    public Set<LootContextParameter<?>> getRequiredParameters();
}

