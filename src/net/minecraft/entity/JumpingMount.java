/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import net.minecraft.entity.Mount;

public interface JumpingMount
extends Mount {
    public void setJumpStrength(int var1);

    public boolean canJump();

    public void startJumping(int var1);

    public void stopJumping();

    default public int getJumpCooldown() {
        return 0;
    }
}

