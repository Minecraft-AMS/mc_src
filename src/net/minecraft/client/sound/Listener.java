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
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.openal.AL10;

@Environment(value=EnvType.CLIENT)
public class Listener {
    private float volume = 1.0f;

    public void setPosition(Vec3d position) {
        AL10.alListener3f((int)4100, (float)((float)position.x), (float)((float)position.y), (float)((float)position.z));
    }

    public void setOrientation(Vector3f vector3f, Vector3f vector3f2) {
        AL10.alListenerfv((int)4111, (float[])new float[]{vector3f.getX(), vector3f.getY(), vector3f.getZ(), vector3f2.getX(), vector3f2.getY(), vector3f2.getZ()});
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
        this.setOrientation(Vector3f.NEGATIVE_Z, Vector3f.POSITIVE_Y);
    }
}

