/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.attribute;

import org.jetbrains.annotations.Nullable;

public interface EntityAttribute {
    public String getId();

    public double clamp(double var1);

    public double getDefaultValue();

    public boolean isTracked();

    @Nullable
    public EntityAttribute getParent();
}

