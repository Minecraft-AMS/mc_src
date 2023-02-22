/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class Vertex {
    public final Vec3d pos;
    public final float u;
    public final float v;

    public Vertex(float x, float y, float z, float u, float v) {
        this(new Vec3d(x, y, z), u, v);
    }

    public Vertex remap(float u, float v) {
        return new Vertex(this, u, v);
    }

    public Vertex(Vertex vertex, float u, float v) {
        this.pos = vertex.pos;
        this.u = u;
        this.v = v;
    }

    public Vertex(Vec3d pos, float u, float v) {
        this.pos = pos;
        this.u = u;
        this.v = v;
    }
}
