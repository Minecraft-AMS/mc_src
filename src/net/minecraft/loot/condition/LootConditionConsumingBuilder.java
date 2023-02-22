/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.condition;

import net.minecraft.world.loot.condition.LootCondition;

public interface LootConditionConsumingBuilder<T> {
    public T withCondition(LootCondition.Builder var1);

    public T getThis();
}

