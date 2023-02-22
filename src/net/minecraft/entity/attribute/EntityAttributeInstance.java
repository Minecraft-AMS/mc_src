/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.attribute;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import org.jetbrains.annotations.Nullable;

public interface EntityAttributeInstance {
    public EntityAttribute getAttribute();

    public double getBaseValue();

    public void setBaseValue(double var1);

    public Set<EntityAttributeModifier> getModifiers(EntityAttributeModifier.Operation var1);

    public Set<EntityAttributeModifier> getModifiers();

    public boolean hasModifier(EntityAttributeModifier var1);

    @Nullable
    public EntityAttributeModifier getModifier(UUID var1);

    public void addModifier(EntityAttributeModifier var1);

    public void removeModifier(EntityAttributeModifier var1);

    public void removeModifier(UUID var1);

    @Environment(value=EnvType.CLIENT)
    public void clearModifiers();

    public double getValue();

    @Environment(value=EnvType.CLIENT)
    default public void copyFrom(EntityAttributeInstance other) {
        this.setBaseValue(other.getBaseValue());
        Set<EntityAttributeModifier> set = other.getModifiers();
        Set<EntityAttributeModifier> set2 = this.getModifiers();
        ImmutableSet immutableSet = ImmutableSet.copyOf((Collection)Sets.difference(set, set2));
        ImmutableSet immutableSet2 = ImmutableSet.copyOf((Collection)Sets.difference(set2, set));
        immutableSet.forEach(this::addModifier);
        immutableSet2.forEach(this::removeModifier);
    }
}

