/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface Tameable {
    @Nullable
    public UUID getOwnerUuid();

    @Nullable
    public Entity getOwner();
}

