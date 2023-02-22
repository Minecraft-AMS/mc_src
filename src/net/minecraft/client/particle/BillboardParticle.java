/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public abstract class BillboardParticle
extends Particle {
    protected float scale;

    protected BillboardParticle(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }

    protected BillboardParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h, i);
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Quaternionf quaternionf;
        Vec3d vec3d = camera.getPos();
        float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
        if (this.angle == 0.0f) {
            quaternionf = camera.getRotation();
        } else {
            quaternionf = new Quaternionf((Quaternionfc)camera.getRotation());
            quaternionf.rotateZ(MathHelper.lerp(tickDelta, this.prevAngle, this.angle));
        }
        Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};
        float i = this.getSize(tickDelta);
        for (int j = 0; j < 4; ++j) {
            Vector3f vector3f = vector3fs[j];
            vector3f.rotate((Quaternionfc)quaternionf);
            vector3f.mul(i);
            vector3f.add(f, g, h);
        }
        float k = this.getMinU();
        float l = this.getMaxU();
        float m = this.getMinV();
        float n = this.getMaxV();
        int o = this.getBrightness(tickDelta);
        vertexConsumer.vertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z()).texture(l, n).color(this.red, this.green, this.blue, this.alpha).light(o).next();
        vertexConsumer.vertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z()).texture(l, m).color(this.red, this.green, this.blue, this.alpha).light(o).next();
        vertexConsumer.vertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z()).texture(k, m).color(this.red, this.green, this.blue, this.alpha).light(o).next();
        vertexConsumer.vertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z()).texture(k, n).color(this.red, this.green, this.blue, this.alpha).light(o).next();
    }

    public float getSize(float tickDelta) {
        return this.scale;
    }

    @Override
    public Particle scale(float scale) {
        this.scale *= scale;
        return super.scale(scale);
    }

    protected abstract float getMinU();

    protected abstract float getMaxU();

    protected abstract float getMinV();

    protected abstract float getMaxV();
}

