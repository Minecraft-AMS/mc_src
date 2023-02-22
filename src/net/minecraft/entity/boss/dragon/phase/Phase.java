/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public interface Phase {
    public boolean method_6848();

    public void clientTick();

    public void serverTick();

    public void crystalDestroyed(EnderCrystalEntity var1, BlockPos var2, DamageSource var3, @Nullable PlayerEntity var4);

    public void beginPhase();

    public void endPhase();

    public float method_6846();

    public float method_6847();

    public PhaseType<? extends Phase> getType();

    @Nullable
    public Vec3d getTarget();

    public float modifyDamageTaken(DamageSource var1, float var2);
}

