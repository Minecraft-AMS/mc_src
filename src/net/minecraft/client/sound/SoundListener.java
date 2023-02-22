/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.openal.AL10
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.openal.AL10;

@Environment(value=EnvType.CLIENT)
public class SoundListener {
    private float volume = 1.0f;
    private Vec3d pos = Vec3d.ZERO;

    public void setPosition(Vec3d position) {
        this.pos = position;
        AL10.alListener3f((int)4100, (float)((float)position.x), (float)((float)position.y), (float)((float)position.z));
    }

    public Vec3d getPos() {
        return this.pos;
    }

    public void setOrientation(Vec3f at, Vec3f up) {
        AL10.alListenerfv((int)4111, (float[])new float[]{at.getX(), at.getY(), at.getZ(), up.getX(), up.getY(), up.getZ()});
    }

    public void setVolume(float volume) {
        AL10.alListenerf((int)4106, (float)volume);
        this.volume = volume;
    }

    public float getVolume() {
        return this.volume;
    }

    public void init() {
        this.setPosition(Vec3d.ZERO);
        this.setOrientation(Vec3f.NEGATIVE_Z, Vec3f.POSITIVE_Y);
    }
}

