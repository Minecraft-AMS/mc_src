/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RidingMinecartSoundInstance
extends MovingSoundInstance {
    private final AbstractMinecartEntity minecart;
    private float distance = 0.0f;

    public RidingMinecartSoundInstance(AbstractMinecartEntity minecart) {
        super(SoundEvents.ENTITY_MINECART_RIDING, SoundCategory.NEUTRAL);
        this.minecart = minecart;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.0f;
        this.x = (float)minecart.x;
        this.y = (float)minecart.y;
        this.z = (float)minecart.z;
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    @Override
    public void tick() {
        if (this.minecart.removed) {
            this.done = true;
            return;
        }
        this.x = (float)this.minecart.x;
        this.y = (float)this.minecart.y;
        this.z = (float)this.minecart.z;
        float f = MathHelper.sqrt(Entity.squaredHorizontalLength(this.minecart.getVelocity()));
        if ((double)f >= 0.01) {
            this.distance = MathHelper.clamp(this.distance + 0.0025f, 0.0f, 1.0f);
            this.volume = MathHelper.lerp(MathHelper.clamp(f, 0.0f, 0.5f), 0.0f, 0.7f);
        } else {
            this.distance = 0.0f;
            this.volume = 0.0f;
        }
    }
}

