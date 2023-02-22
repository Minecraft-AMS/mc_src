/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain;

import java.util.List;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EntityPosWrapper
implements LookTarget {
    private final Entity entity;

    public EntityPosWrapper(Entity entity) {
        this.entity = entity;
    }

    @Override
    public BlockPos getBlockPos() {
        return new BlockPos(this.entity);
    }

    @Override
    public Vec3d getPos() {
        return new Vec3d(this.entity.x, this.entity.y + (double)this.entity.getStandingEyeHeight(), this.entity.z);
    }

    @Override
    public boolean isSeenBy(LivingEntity entity) {
        Optional<List<LivingEntity>> optional = entity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS);
        return this.entity.isAlive() && optional.isPresent() && optional.get().contains(this.entity);
    }

    public String toString() {
        return "EntityPosWrapper for " + this.entity;
    }
}
