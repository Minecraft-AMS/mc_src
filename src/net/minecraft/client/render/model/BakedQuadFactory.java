/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.CubeFace;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.AffineTransformations;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

@Environment(value=EnvType.CLIENT)
public class BakedQuadFactory {
    public static final int field_32796 = 8;
    private static final float MIN_SCALE = 1.0f / (float)Math.cos(0.3926991f) - 1.0f;
    private static final float MAX_SCALE = 1.0f / (float)Math.cos(0.7853981852531433) - 1.0f;
    public static final int field_32797 = 4;
    private static final int field_32799 = 3;
    public static final int field_32798 = 4;

    public BakedQuad bake(Vector3f from, Vector3f to, ModelElementFace face, Sprite texture, Direction side, ModelBakeSettings settings, @Nullable ModelRotation rotation, boolean shade, Identifier modelId) {
        ModelElementTexture modelElementTexture = face.textureData;
        if (settings.isUvLocked()) {
            modelElementTexture = BakedQuadFactory.uvLock(face.textureData, side, settings.getRotation(), modelId);
        }
        float[] fs = new float[modelElementTexture.uvs.length];
        System.arraycopy(modelElementTexture.uvs, 0, fs, 0, fs.length);
        float f = texture.getAnimationFrameDelta();
        float g = (modelElementTexture.uvs[0] + modelElementTexture.uvs[0] + modelElementTexture.uvs[2] + modelElementTexture.uvs[2]) / 4.0f;
        float h = (modelElementTexture.uvs[1] + modelElementTexture.uvs[1] + modelElementTexture.uvs[3] + modelElementTexture.uvs[3]) / 4.0f;
        modelElementTexture.uvs[0] = MathHelper.lerp(f, modelElementTexture.uvs[0], g);
        modelElementTexture.uvs[2] = MathHelper.lerp(f, modelElementTexture.uvs[2], g);
        modelElementTexture.uvs[1] = MathHelper.lerp(f, modelElementTexture.uvs[1], h);
        modelElementTexture.uvs[3] = MathHelper.lerp(f, modelElementTexture.uvs[3], h);
        int[] is = this.packVertexData(modelElementTexture, texture, side, this.getPositionMatrix(from, to), settings.getRotation(), rotation, shade);
        Direction direction = BakedQuadFactory.decodeDirection(is);
        System.arraycopy(fs, 0, modelElementTexture.uvs, 0, fs.length);
        if (rotation == null) {
            this.encodeDirection(is, direction);
        }
        return new BakedQuad(is, face.tintIndex, direction, texture, shade);
    }

    public static ModelElementTexture uvLock(ModelElementTexture texture, Direction orientation, AffineTransformation rotation, Identifier modelId) {
        float q;
        float p;
        float o;
        float n;
        Matrix4f matrix4f = AffineTransformations.uvLock(rotation, orientation, () -> "Unable to resolve UVLock for model: " + modelId).getMatrix();
        float f = texture.getU(texture.getDirectionIndex(0));
        float g = texture.getV(texture.getDirectionIndex(0));
        Vector4f vector4f = matrix4f.transform(new Vector4f(f / 16.0f, g / 16.0f, 0.0f, 1.0f));
        float h = 16.0f * vector4f.x();
        float i = 16.0f * vector4f.y();
        float j = texture.getU(texture.getDirectionIndex(2));
        float k = texture.getV(texture.getDirectionIndex(2));
        Vector4f vector4f2 = matrix4f.transform(new Vector4f(j / 16.0f, k / 16.0f, 0.0f, 1.0f));
        float l = 16.0f * vector4f2.x();
        float m = 16.0f * vector4f2.y();
        if (Math.signum(j - f) == Math.signum(l - h)) {
            n = h;
            o = l;
        } else {
            n = l;
            o = h;
        }
        if (Math.signum(k - g) == Math.signum(m - i)) {
            p = i;
            q = m;
        } else {
            p = m;
            q = i;
        }
        float r = (float)Math.toRadians(texture.rotation);
        Matrix3f matrix3f = new Matrix3f((Matrix4fc)matrix4f);
        Vector3f vector3f = matrix3f.transform(new Vector3f(MathHelper.cos(r), MathHelper.sin(r), 0.0f));
        int s = Math.floorMod(-((int)Math.round(Math.toDegrees(Math.atan2(vector3f.y(), vector3f.x())) / 90.0)) * 90, 360);
        return new ModelElementTexture(new float[]{n, p, o, q}, s);
    }

    private int[] packVertexData(ModelElementTexture texture, Sprite sprite, Direction direction, float[] positionMatrix, AffineTransformation orientation, @Nullable ModelRotation rotation, boolean shaded) {
        int[] is = new int[32];
        for (int i = 0; i < 4; ++i) {
            this.packVertexData(is, i, direction, texture, positionMatrix, sprite, orientation, rotation, shaded);
        }
        return is;
    }

    private float[] getPositionMatrix(Vector3f from, Vector3f to) {
        float[] fs = new float[Direction.values().length];
        fs[CubeFace.DirectionIds.WEST] = from.x() / 16.0f;
        fs[CubeFace.DirectionIds.DOWN] = from.y() / 16.0f;
        fs[CubeFace.DirectionIds.NORTH] = from.z() / 16.0f;
        fs[CubeFace.DirectionIds.EAST] = to.x() / 16.0f;
        fs[CubeFace.DirectionIds.UP] = to.y() / 16.0f;
        fs[CubeFace.DirectionIds.SOUTH] = to.z() / 16.0f;
        return fs;
    }

    private void packVertexData(int[] vertices, int cornerIndex, Direction direction, ModelElementTexture texture, float[] positionMatrix, Sprite sprite, AffineTransformation orientation, @Nullable ModelRotation rotation, boolean shaded) {
        CubeFace.Corner corner = CubeFace.getFace(direction).getCorner(cornerIndex);
        Vector3f vector3f = new Vector3f(positionMatrix[corner.xSide], positionMatrix[corner.ySide], positionMatrix[corner.zSide]);
        this.rotateVertex(vector3f, rotation);
        this.transformVertex(vector3f, orientation);
        this.packVertexData(vertices, cornerIndex, vector3f, sprite, texture);
    }

    private void packVertexData(int[] vertices, int cornerIndex, Vector3f position, Sprite sprite, ModelElementTexture modelElementTexture) {
        int i = cornerIndex * 8;
        vertices[i] = Float.floatToRawIntBits(position.x());
        vertices[i + 1] = Float.floatToRawIntBits(position.y());
        vertices[i + 2] = Float.floatToRawIntBits(position.z());
        vertices[i + 3] = -1;
        vertices[i + 4] = Float.floatToRawIntBits(sprite.getFrameU(modelElementTexture.getU(cornerIndex)));
        vertices[i + 4 + 1] = Float.floatToRawIntBits(sprite.getFrameV(modelElementTexture.getV(cornerIndex)));
    }

    private void rotateVertex(Vector3f vector, @Nullable ModelRotation rotation) {
        Vector3f vector3f;
        if (rotation == null) {
            return;
        }
        Vector3f vector3f2 = switch (rotation.axis()) {
            case Direction.Axis.X -> {
                vector3f = new Vector3f(1.0f, 0.0f, 0.0f);
                yield new Vector3f(0.0f, 1.0f, 1.0f);
            }
            case Direction.Axis.Y -> {
                vector3f = new Vector3f(0.0f, 1.0f, 0.0f);
                yield new Vector3f(1.0f, 0.0f, 1.0f);
            }
            case Direction.Axis.Z -> {
                vector3f = new Vector3f(0.0f, 0.0f, 1.0f);
                yield new Vector3f(1.0f, 1.0f, 0.0f);
            }
            default -> throw new IllegalArgumentException("There are only 3 axes");
        };
        Quaternionf quaternionf = new Quaternionf().rotationAxis(rotation.angle() * ((float)Math.PI / 180), (Vector3fc)vector3f);
        if (rotation.rescale()) {
            if (Math.abs(rotation.angle()) == 22.5f) {
                vector3f2.mul(MIN_SCALE);
            } else {
                vector3f2.mul(MAX_SCALE);
            }
            vector3f2.add(1.0f, 1.0f, 1.0f);
        } else {
            vector3f2.set(1.0f, 1.0f, 1.0f);
        }
        this.transformVertex(vector, new Vector3f((Vector3fc)rotation.origin()), new Matrix4f().rotation((Quaternionfc)quaternionf), vector3f2);
    }

    public void transformVertex(Vector3f vertex, AffineTransformation transformation) {
        if (transformation == AffineTransformation.identity()) {
            return;
        }
        this.transformVertex(vertex, new Vector3f(0.5f, 0.5f, 0.5f), transformation.getMatrix(), new Vector3f(1.0f, 1.0f, 1.0f));
    }

    private void transformVertex(Vector3f vertex, Vector3f origin, Matrix4f transformationMatrix, Vector3f scale) {
        Vector4f vector4f = transformationMatrix.transform(new Vector4f(vertex.x() - origin.x(), vertex.y() - origin.y(), vertex.z() - origin.z(), 1.0f));
        vector4f.mul((Vector4fc)new Vector4f((Vector3fc)scale, 1.0f));
        vertex.set(vector4f.x() + origin.x(), vector4f.y() + origin.y(), vector4f.z() + origin.z());
    }

    public static Direction decodeDirection(int[] rotationMatrix) {
        Vector3f vector3f = new Vector3f(Float.intBitsToFloat(rotationMatrix[0]), Float.intBitsToFloat(rotationMatrix[1]), Float.intBitsToFloat(rotationMatrix[2]));
        Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(rotationMatrix[8]), Float.intBitsToFloat(rotationMatrix[9]), Float.intBitsToFloat(rotationMatrix[10]));
        Vector3f vector3f3 = new Vector3f(Float.intBitsToFloat(rotationMatrix[16]), Float.intBitsToFloat(rotationMatrix[17]), Float.intBitsToFloat(rotationMatrix[18]));
        Vector3f vector3f4 = new Vector3f((Vector3fc)vector3f).sub((Vector3fc)vector3f2);
        Vector3f vector3f5 = new Vector3f((Vector3fc)vector3f3).sub((Vector3fc)vector3f2);
        Vector3f vector3f6 = new Vector3f((Vector3fc)vector3f5).cross((Vector3fc)vector3f4).normalize();
        if (!vector3f6.isFinite()) {
            return Direction.UP;
        }
        Direction direction = null;
        float f = 0.0f;
        for (Direction direction2 : Direction.values()) {
            Vec3i vec3i = direction2.getVector();
            Vector3f vector3f7 = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
            float g = vector3f6.dot((Vector3fc)vector3f7);
            if (!(g >= 0.0f) || !(g > f)) continue;
            f = g;
            direction = direction2;
        }
        if (direction == null) {
            return Direction.UP;
        }
        return direction;
    }

    private void encodeDirection(int[] rotationMatrix, Direction direction) {
        float h;
        int j;
        int[] is = new int[rotationMatrix.length];
        System.arraycopy(rotationMatrix, 0, is, 0, rotationMatrix.length);
        float[] fs = new float[Direction.values().length];
        fs[CubeFace.DirectionIds.WEST] = 999.0f;
        fs[CubeFace.DirectionIds.DOWN] = 999.0f;
        fs[CubeFace.DirectionIds.NORTH] = 999.0f;
        fs[CubeFace.DirectionIds.EAST] = -999.0f;
        fs[CubeFace.DirectionIds.UP] = -999.0f;
        fs[CubeFace.DirectionIds.SOUTH] = -999.0f;
        for (int i = 0; i < 4; ++i) {
            j = 8 * i;
            float f = Float.intBitsToFloat(is[j]);
            float g = Float.intBitsToFloat(is[j + 1]);
            h = Float.intBitsToFloat(is[j + 2]);
            if (f < fs[CubeFace.DirectionIds.WEST]) {
                fs[CubeFace.DirectionIds.WEST] = f;
            }
            if (g < fs[CubeFace.DirectionIds.DOWN]) {
                fs[CubeFace.DirectionIds.DOWN] = g;
            }
            if (h < fs[CubeFace.DirectionIds.NORTH]) {
                fs[CubeFace.DirectionIds.NORTH] = h;
            }
            if (f > fs[CubeFace.DirectionIds.EAST]) {
                fs[CubeFace.DirectionIds.EAST] = f;
            }
            if (g > fs[CubeFace.DirectionIds.UP]) {
                fs[CubeFace.DirectionIds.UP] = g;
            }
            if (!(h > fs[CubeFace.DirectionIds.SOUTH])) continue;
            fs[CubeFace.DirectionIds.SOUTH] = h;
        }
        CubeFace cubeFace = CubeFace.getFace(direction);
        for (j = 0; j < 4; ++j) {
            int k = 8 * j;
            CubeFace.Corner corner = cubeFace.getCorner(j);
            h = fs[corner.xSide];
            float l = fs[corner.ySide];
            float m = fs[corner.zSide];
            rotationMatrix[k] = Float.floatToRawIntBits(h);
            rotationMatrix[k + 1] = Float.floatToRawIntBits(l);
            rotationMatrix[k + 2] = Float.floatToRawIntBits(m);
            for (int n = 0; n < 4; ++n) {
                int o = 8 * n;
                float p = Float.intBitsToFloat(is[o]);
                float q = Float.intBitsToFloat(is[o + 1]);
                float r = Float.intBitsToFloat(is[o + 2]);
                if (!MathHelper.approximatelyEquals(h, p) || !MathHelper.approximatelyEquals(l, q) || !MathHelper.approximatelyEquals(m, r)) continue;
                rotationMatrix[k + 4] = is[o + 4];
                rotationMatrix[k + 4 + 1] = is[o + 4 + 1];
            }
        }
    }
}

