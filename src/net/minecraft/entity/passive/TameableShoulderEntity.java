/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public abstract class TameableShoulderEntity
extends TameableEntity {
    private int ticks;

    protected TameableShoulderEntity(EntityType<? extends TameableShoulderEntity> type, World world) {
        super((EntityType<? extends TameableEntity>)type, world);
    }

    public boolean mountOnto(ServerPlayerEntity player) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("id", this.getSavedEntityId());
        this.toTag(compoundTag);
        if (player.addShoulderEntity(compoundTag)) {
            this.remove();
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        ++this.ticks;
        super.tick();
    }

    public boolean isReadyToSitOnPlayer() {
        return this.ticks > 100;
    }
}

