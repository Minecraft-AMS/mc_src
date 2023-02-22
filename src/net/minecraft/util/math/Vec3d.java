/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.math;

import java.util.EnumSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3i;

public class Vec3d
implements Position {
    public static final Vec3d ZERO = new Vec3d(0.0, 0.0, 0.0);
    public final double x;
    public final double y;
    public final double z;

    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3d(Vec3i vec3i) {
        this(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    public Vec3d reverseSubtract(Vec3d vec3d) {
        return new Vec3d(vec3d.x - this.x, vec3d.y - this.y, vec3d.z - this.z);
    }

    public Vec3d normalize() {
        double d = MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        if (d < 1.0E-4) {
            return ZERO;
        }
        return new Vec3d(this.x / d, this.y / d, this.z / d);
    }

    public double dotProduct(Vec3d vec3d) {
        return this.x * vec3d.x + this.y * vec3d.y + this.z * vec3d.z;
    }

    public Vec3d crossProduct(Vec3d vec3d) {
        return new Vec3d(this.y * vec3d.z - this.z * vec3d.y, this.z * vec3d.x - this.x * vec3d.z, this.x * vec3d.y - this.y * vec3d.x);
    }

    public Vec3d subtract(Vec3d vec3d) {
        return this.subtract(vec3d.x, vec3d.y, vec3d.z);
    }

    public Vec3d subtract(double x, double y, double z) {
        return this.add(-x, -y, -z);
    }

    public Vec3d add(Vec3d vec3d) {
        return this.add(vec3d.x, vec3d.y, vec3d.z);
    }

    public Vec3d add(double x, double y, double z) {
        return new Vec3d(this.x + x, this.y + y, this.z + z);
    }

    public double distanceTo(Vec3d vec3d) {
        double d = vec3d.x - this.x;
        double e = vec3d.y - this.y;
        double f = vec3d.z - this.z;
        return MathHelper.sqrt(d * d + e * e + f * f);
    }

    public double squaredDistanceTo(Vec3d vec3d) {
        double d = vec3d.x - this.x;
        double e = vec3d.y - this.y;
        double f = vec3d.z - this.z;
        return d * d + e * e + f * f;
    }

    public double squaredDistanceTo(double x, double y, double z) {
        double d = x - this.x;
        double e = y - this.y;
        double f = z - this.z;
        return d * d + e * e + f * f;
    }

    public Vec3d multiply(double mult) {
        return this.multiply(mult, mult, mult);
    }

    @Environment(value=EnvType.CLIENT)
    public Vec3d negate() {
        return this.multiply(-1.0);
    }

    public Vec3d multiply(Vec3d mult) {
        return this.multiply(mult.x, mult.y, mult.z);
    }

    public Vec3d multiply(double multX, double multY, double multZ) {
        return new Vec3d(this.x * multX, this.y * multY, this.z * multZ);
    }

    public double length() {
        return MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vec3d)) {
            return false;
        }
        Vec3d vec3d = (Vec3d)o;
        if (Double.compare(vec3d.x, this.x) != 0) {
            return false;
        }
        if (Double.compare(vec3d.y, this.y) != 0) {
            return false;
        }
        return Double.compare(vec3d.z, this.z) == 0;
    }

    public int hashCode() {
        long l = Double.doubleToLongBits(this.x);
        int i = (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.y);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.z);
        i = 31 * i + (int)(l ^ l >>> 32);
        return i;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public Vec3d rotateX(float f) {
        float g = MathHelper.cos(f);
        float h = MathHelper.sin(f);
        double d = this.x;
        double e = this.y * (double)g + this.z * (double)h;
        double i = this.z * (double)g - this.y * (double)h;
        return new Vec3d(d, e, i);
    }

    public Vec3d rotateY(float f) {
        float g = MathHelper.cos(f);
        float h = MathHelper.sin(f);
        double d = this.x * (double)g + this.z * (double)h;
        double e = this.y;
        double i = this.z * (double)g - this.x * (double)h;
        return new Vec3d(d, e, i);
    }

    @Environment(value=EnvType.CLIENT)
    public static Vec3d fromPolar(Vec2f polar) {
        return Vec3d.fromPolar(polar.x, polar.y);
    }

    @Environment(value=EnvType.CLIENT)
    public static Vec3d fromPolar(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * ((float)Math.PI / 180) - (float)Math.PI);
        float g = MathHelper.sin(-yaw * ((float)Math.PI / 180) - (float)Math.PI);
        float h = -MathHelper.cos(-pitch * ((float)Math.PI / 180));
        float i = MathHelper.sin(-pitch * ((float)Math.PI / 180));
        return new Vec3d(g * h, i, f * h);
    }

    public Vec3d floorAlongAxes(EnumSet<Direction.Axis> axes) {
        double d = axes.contains(Direction.Axis.X) ? (double)MathHelper.floor(this.x) : this.x;
        double e = axes.contains(Direction.Axis.Y) ? (double)MathHelper.floor(this.y) : this.y;
        double f = axes.contains(Direction.Axis.Z) ? (double)MathHelper.floor(this.z) : this.z;
        return new Vec3d(d, e, f);
    }

    public double getComponentAlongAxis(Direction.Axis axis) {
        return axis.choose(this.x, this.y, this.z);
    }

    @Override
    public final double getX() {
        return this.x;
    }

    @Override
    public final double getY() {
        return this.y;
    }

    @Override
    public final double getZ() {
        return this.z;
    }
}
