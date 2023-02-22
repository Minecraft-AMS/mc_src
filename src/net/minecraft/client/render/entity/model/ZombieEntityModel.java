/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.AbstractZombieModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(value=EnvType.CLIENT)
public class ZombieEntityModel<T extends ZombieEntity>
extends AbstractZombieModel<T> {
    public ZombieEntityModel() {
        this(0.0f, false);
    }

    public ZombieEntityModel(float f, boolean bl) {
        super(f, 0.0f, 64, bl ? 32 : 64);
    }

    protected ZombieEntityModel(float scale, float f, int textureWidth, int i) {
        super(scale, f, textureWidth, i);
    }

    @Override
    public boolean method_17790(T zombieEntity) {
        return ((MobEntity)zombieEntity).isAttacking();
    }
}

