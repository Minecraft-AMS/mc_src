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
import org.lwjgl.openal.AL10;

@Environment(value=EnvType.CLIENT)
public class Listener {
    public static final Vec3d field_18905 = new Vec3d(0.0, 1.0, 0.0);
    private float volume = 1.0f;

    public void setPosition(Vec3d position) {
        AL10.alListener3f((int)4100, (float)((float)position.x), (float)((float)position.y), (float)((float)position.z));
    }

    public void setOrientation(Vec3d from, Vec3d to) {
        AL10.alListenerfv((int)4111, (float[])new float[]{(float)from.x, (float)from.y, (float)from.z, (float)to.x, (float)to.y, (float)to.z});
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
        this.setOrientation(new Vec3d(0.0, 0.0, -1.0), field_18905);
    }
}

