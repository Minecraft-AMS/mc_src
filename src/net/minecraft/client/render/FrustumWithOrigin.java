/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GlMatrixFrustum;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.util.math.Box;

@Environment(value=EnvType.CLIENT)
public class FrustumWithOrigin
implements VisibleRegion {
    private final Frustum frustum;
    private double originX;
    private double originY;
    private double originZ;

    public FrustumWithOrigin() {
        this(GlMatrixFrustum.get());
    }

    public FrustumWithOrigin(Frustum frustum) {
        this.frustum = frustum;
    }

    @Override
    public void setOrigin(double x, double y, double z) {
        this.originX = x;
        this.originY = y;
        this.originZ = z;
    }

    public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return this.frustum.intersects(minX - this.originX, minY - this.originY, minZ - this.originZ, maxX - this.originX, maxY - this.originY, maxZ - this.originZ);
    }

    @Override
    public boolean intersects(Box boundingBox) {
        return this.intersects(boundingBox.x1, boundingBox.y1, boundingBox.z1, boundingBox.x2, boundingBox.y2, boundingBox.z2);
    }
}

