/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.CubeFace;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BakedQuadFactory {
    private static final float field_4260 = 1.0f / (float)Math.cos(0.3926991f) - 1.0f;
    private static final float field_4259 = 1.0f / (float)Math.cos(0.7853981852531433) - 1.0f;
    private static final class_797[] field_4264 = new class_797[ModelRotation.values().length * Direction.values().length];
    private static final class_797 field_4258 = new class_797(){

        @Override
        ModelElementTexture method_3470(float f, float g, float h, float i) {
            return new ModelElementTexture(new float[]{f, g, h, i}, 0);
        }
    };
    private static final class_797 field_4261 = new class_797(){

        @Override
        ModelElementTexture method_3470(float f, float g, float h, float i) {
            return new ModelElementTexture(new float[]{i, 16.0f - f, g, 16.0f - h}, 270);
        }
    };
    private static final class_797 field_4262 = new class_797(){

        @Override
        ModelElementTexture method_3470(float f, float g, float h, float i) {
            return new ModelElementTexture(new float[]{16.0f - f, 16.0f - g, 16.0f - h, 16.0f - i}, 0);
        }
    };
    private static final class_797 field_4263 = new class_797(){

        @Override
        ModelElementTexture method_3470(float f, float g, float h, float i) {
            return new ModelElementTexture(new float[]{16.0f - g, h, 16.0f - i, f}, 90);
        }
    };

    public BakedQuad bake(Vector3f from, Vector3f to, ModelElementFace face, Sprite texture, Direction side, ModelBakeSettings settings, @Nullable net.minecraft.client.render.model.json.ModelRotation rotation, boolean shade) {
        ModelElementTexture modelElementTexture = face.textureData;
        if (settings.isShaded()) {
            modelElementTexture = this.uvLock(face.textureData, side, settings.getRotation());
        }
        float[] fs = new float[modelElementTexture.uvs.length];
        System.arraycopy(modelElementTexture.uvs, 0, fs, 0, fs.length);
        float f = (float)texture.getWidth() / (texture.getMaxU() - texture.getMinU());
        float g = (float)texture.getHeight() / (texture.getMaxV() - texture.getMinV());
        float h = 4.0f / Math.max(g, f);
        float i = (modelElementTexture.uvs[0] + modelElementTexture.uvs[0] + modelElementTexture.uvs[2] + modelElementTexture.uvs[2]) / 4.0f;
        float j = (modelElementTexture.uvs[1] + modelElementTexture.uvs[1] + modelElementTexture.uvs[3] + modelElementTexture.uvs[3]) / 4.0f;
        modelElementTexture.uvs[0] = MathHelper.lerp(h, modelElementTexture.uvs[0], i);
        modelElementTexture.uvs[2] = MathHelper.lerp(h, modelElementTexture.uvs[2], i);
        modelElementTexture.uvs[1] = MathHelper.lerp(h, modelElementTexture.uvs[1], j);
        modelElementTexture.uvs[3] = MathHelper.lerp(h, modelElementTexture.uvs[3], j);
        int[] is = this.method_3458(modelElementTexture, texture, side, this.method_3459(from, to), settings.getRotation(), rotation, shade);
        Direction direction = BakedQuadFactory.method_3467(is);
        System.arraycopy(fs, 0, modelElementTexture.uvs, 0, fs.length);
        if (rotation == null) {
            this.method_3462(is, direction);
        }
        return new BakedQuad(is, face.tintIndex, direction, texture);
    }

    private ModelElementTexture uvLock(ModelElementTexture modelElementTexture, Direction direction, ModelRotation modelRotation) {
        return field_4264[BakedQuadFactory.method_3465(modelRotation, direction)].method_3469(modelElementTexture);
    }

    private int[] method_3458(ModelElementTexture modelElementTexture, Sprite sprite, Direction direction, float[] fs, ModelRotation modelRotation, @Nullable net.minecraft.client.render.model.json.ModelRotation modelRotation2, boolean bl) {
        int[] is = new int[28];
        for (int i = 0; i < 4; ++i) {
            this.method_3461(is, i, direction, modelElementTexture, fs, sprite, modelRotation, modelRotation2, bl);
        }
        return is;
    }

    private int method_3457(Direction direction) {
        float f = this.method_3456(direction);
        int i = MathHelper.clamp((int)(f * 255.0f), 0, 255);
        return 0xFF000000 | i << 16 | i << 8 | i;
    }

    private float method_3456(Direction direction) {
        switch (direction) {
            case DOWN: {
                return 0.5f;
            }
            case UP: {
                return 1.0f;
            }
            case NORTH: 
            case SOUTH: {
                return 0.8f;
            }
            case WEST: 
            case EAST: {
                return 0.6f;
            }
        }
        return 1.0f;
    }

    private float[] method_3459(Vector3f vector3f, Vector3f vector3f2) {
        float[] fs = new float[Direction.values().length];
        fs[CubeFace.DirectionIds.WEST] = vector3f.getX() / 16.0f;
        fs[CubeFace.DirectionIds.DOWN] = vector3f.getY() / 16.0f;
        fs[CubeFace.DirectionIds.NORTH] = vector3f.getZ() / 16.0f;
        fs[CubeFace.DirectionIds.EAST] = vector3f2.getX() / 16.0f;
        fs[CubeFace.DirectionIds.UP] = vector3f2.getY() / 16.0f;
        fs[CubeFace.DirectionIds.SOUTH] = vector3f2.getZ() / 16.0f;
        return fs;
    }

    private void method_3461(int[] is, int i, Direction direction, ModelElementTexture modelElementTexture, float[] fs, Sprite sprite, ModelRotation modelRotation, @Nullable net.minecraft.client.render.model.json.ModelRotation modelRotation2, boolean bl) {
        Direction direction2 = modelRotation.apply(direction);
        int j = bl ? this.method_3457(direction2) : -1;
        CubeFace.Corner corner = CubeFace.method_3163(direction).getCorner(i);
        Vector3f vector3f = new Vector3f(fs[corner.xSide], fs[corner.ySide], fs[corner.zSide]);
        this.method_3463(vector3f, modelRotation2);
        int k = this.method_3455(vector3f, direction, i, modelRotation);
        this.method_3460(is, k, i, vector3f, j, sprite, modelElementTexture);
    }

    private void method_3460(int[] is, int i, int j, Vector3f vector3f, int k, Sprite sprite, ModelElementTexture modelElementTexture) {
        int l = i * 7;
        is[l] = Float.floatToRawIntBits(vector3f.getX());
        is[l + 1] = Float.floatToRawIntBits(vector3f.getY());
        is[l + 2] = Float.floatToRawIntBits(vector3f.getZ());
        is[l + 3] = k;
        is[l + 4] = Float.floatToRawIntBits(sprite.getFrameU(modelElementTexture.getU(j)));
        is[l + 4 + 1] = Float.floatToRawIntBits(sprite.getFrameV(modelElementTexture.getV(j)));
    }

    private void method_3463(Vector3f vector3f, @Nullable net.minecraft.client.render.model.json.ModelRotation modelRotation) {
        Vector3f vector3f3;
        Vector3f vector3f2;
        if (modelRotation == null) {
            return;
        }
        switch (modelRotation.axis) {
            case X: {
                vector3f2 = new Vector3f(1.0f, 0.0f, 0.0f);
                vector3f3 = new Vector3f(0.0f, 1.0f, 1.0f);
                break;
            }
            case Y: {
                vector3f2 = new Vector3f(0.0f, 1.0f, 0.0f);
                vector3f3 = new Vector3f(1.0f, 0.0f, 1.0f);
                break;
            }
            case Z: {
                vector3f2 = new Vector3f(0.0f, 0.0f, 1.0f);
                vector3f3 = new Vector3f(1.0f, 1.0f, 0.0f);
                break;
            }
            default: {
                throw new IllegalArgumentException("There are only 3 axes");
            }
        }
        Quaternion quaternion = new Quaternion(vector3f2, modelRotation.angle, true);
        if (modelRotation.rescale) {
            if (Math.abs(modelRotation.angle) == 22.5f) {
                vector3f3.scale(field_4260);
            } else {
                vector3f3.scale(field_4259);
            }
            vector3f3.add(1.0f, 1.0f, 1.0f);
        } else {
            vector3f3.set(1.0f, 1.0f, 1.0f);
        }
        this.method_3464(vector3f, new Vector3f(modelRotation.origin), quaternion, vector3f3);
    }

    public int method_3455(Vector3f vector3f, Direction direction, int i, ModelRotation modelRotation) {
        if (modelRotation == ModelRotation.X0_Y0) {
            return i;
        }
        this.method_3464(vector3f, new Vector3f(0.5f, 0.5f, 0.5f), modelRotation.getQuaternion(), new Vector3f(1.0f, 1.0f, 1.0f));
        return modelRotation.method_4706(direction, i);
    }

    private void method_3464(Vector3f vector3f, Vector3f vector3f2, Quaternion quaternion, Vector3f vector3f3) {
        Vector4f vector4f = new Vector4f(vector3f.getX() - vector3f2.getX(), vector3f.getY() - vector3f2.getY(), vector3f.getZ() - vector3f2.getZ(), 1.0f);
        vector4f.method_4959(quaternion);
        vector4f.multiplyComponentwise(vector3f3);
        vector3f.set(vector4f.getX() + vector3f2.getX(), vector4f.getY() + vector3f2.getY(), vector4f.getZ() + vector3f2.getZ());
    }

    public static Direction method_3467(int[] is) {
        Vector3f vector3f = new Vector3f(Float.intBitsToFloat(is[0]), Float.intBitsToFloat(is[1]), Float.intBitsToFloat(is[2]));
        Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(is[7]), Float.intBitsToFloat(is[8]), Float.intBitsToFloat(is[9]));
        Vector3f vector3f3 = new Vector3f(Float.intBitsToFloat(is[14]), Float.intBitsToFloat(is[15]), Float.intBitsToFloat(is[16]));
        Vector3f vector3f4 = new Vector3f(vector3f);
        vector3f4.subtract(vector3f2);
        Vector3f vector3f5 = new Vector3f(vector3f3);
        vector3f5.subtract(vector3f2);
        Vector3f vector3f6 = new Vector3f(vector3f5);
        vector3f6.cross(vector3f4);
        vector3f6.reciprocal();
        Direction direction = null;
        float f = 0.0f;
        for (Direction direction2 : Direction.values()) {
            Vec3i vec3i = direction2.getVector();
            Vector3f vector3f7 = new Vector3f(vec3i.getX(), vec3i.getY(), vec3i.getZ());
            float g = vector3f6.dot(vector3f7);
            if (!(g >= 0.0f) || !(g > f)) continue;
            f = g;
            direction = direction2;
        }
        if (direction == null) {
            return Direction.UP;
        }
        return direction;
    }

    private void method_3462(int[] is, Direction direction) {
        float h;
        int j;
        int[] js = new int[is.length];
        System.arraycopy(is, 0, js, 0, is.length);
        float[] fs = new float[Direction.values().length];
        fs[CubeFace.DirectionIds.WEST] = 999.0f;
        fs[CubeFace.DirectionIds.DOWN] = 999.0f;
        fs[CubeFace.DirectionIds.NORTH] = 999.0f;
        fs[CubeFace.DirectionIds.EAST] = -999.0f;
        fs[CubeFace.DirectionIds.UP] = -999.0f;
        fs[CubeFace.DirectionIds.SOUTH] = -999.0f;
        for (int i = 0; i < 4; ++i) {
            j = 7 * i;
            float f = Float.intBitsToFloat(js[j]);
            float g = Float.intBitsToFloat(js[j + 1]);
            h = Float.intBitsToFloat(js[j + 2]);
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
        CubeFace cubeFace = CubeFace.method_3163(direction);
        for (j = 0; j < 4; ++j) {
            int k = 7 * j;
            CubeFace.Corner corner = cubeFace.getCorner(j);
            h = fs[corner.xSide];
            float l = fs[corner.ySide];
            float m = fs[corner.zSide];
            is[k] = Float.floatToRawIntBits(h);
            is[k + 1] = Float.floatToRawIntBits(l);
            is[k + 2] = Float.floatToRawIntBits(m);
            for (int n = 0; n < 4; ++n) {
                int o = 7 * n;
                float p = Float.intBitsToFloat(js[o]);
                float q = Float.intBitsToFloat(js[o + 1]);
                float r = Float.intBitsToFloat(js[o + 2]);
                if (!MathHelper.approximatelyEquals(h, p) || !MathHelper.approximatelyEquals(l, q) || !MathHelper.approximatelyEquals(m, r)) continue;
                is[k + 4] = js[o + 4];
                is[k + 4 + 1] = js[o + 4 + 1];
            }
        }
    }

    private static void method_3466(ModelRotation modelRotation, Direction direction, class_797 arg) {
        BakedQuadFactory.field_4264[BakedQuadFactory.method_3465((ModelRotation)modelRotation, (Direction)direction)] = arg;
    }

    private static int method_3465(ModelRotation modelRotation, Direction direction) {
        return ModelRotation.values().length * direction.ordinal() + modelRotation.ordinal();
    }

    static {
        BakedQuadFactory.method_3466(ModelRotation.X0_Y0, Direction.DOWN, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y0, Direction.EAST, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y0, Direction.NORTH, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y0, Direction.SOUTH, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y0, Direction.UP, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y0, Direction.WEST, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y90, Direction.EAST, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y90, Direction.NORTH, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y90, Direction.SOUTH, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y90, Direction.WEST, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y180, Direction.EAST, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y180, Direction.NORTH, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y180, Direction.SOUTH, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y180, Direction.WEST, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y270, Direction.EAST, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y270, Direction.NORTH, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y270, Direction.SOUTH, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y270, Direction.WEST, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y0, Direction.DOWN, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y0, Direction.SOUTH, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y90, Direction.DOWN, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y180, Direction.DOWN, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y180, Direction.NORTH, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y270, Direction.DOWN, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y0, Direction.DOWN, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y0, Direction.UP, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y0, Direction.SOUTH, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y0, Direction.UP, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y90, Direction.UP, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y180, Direction.NORTH, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y180, Direction.UP, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y270, Direction.UP, field_4258);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y270, Direction.UP, field_4261);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y90, Direction.DOWN, field_4261);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y0, Direction.WEST, field_4261);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y90, Direction.WEST, field_4261);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y180, Direction.WEST, field_4261);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y270, Direction.NORTH, field_4261);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y270, Direction.SOUTH, field_4261);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y270, Direction.WEST, field_4261);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y90, Direction.UP, field_4261);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y270, Direction.DOWN, field_4261);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y0, Direction.EAST, field_4261);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y90, Direction.EAST, field_4261);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y90, Direction.NORTH, field_4261);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y90, Direction.SOUTH, field_4261);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y180, Direction.EAST, field_4261);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y270, Direction.EAST, field_4261);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y180, Direction.DOWN, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y180, Direction.UP, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y0, Direction.NORTH, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y0, Direction.UP, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y90, Direction.UP, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y180, Direction.SOUTH, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y180, Direction.UP, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y270, Direction.UP, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y0, Direction.EAST, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y0, Direction.NORTH, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y0, Direction.SOUTH, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y0, Direction.WEST, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y90, Direction.EAST, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y90, Direction.NORTH, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y90, Direction.SOUTH, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y90, Direction.WEST, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y180, Direction.DOWN, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y180, Direction.EAST, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y180, Direction.NORTH, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y180, Direction.SOUTH, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y180, Direction.UP, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y180, Direction.WEST, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y270, Direction.EAST, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y270, Direction.NORTH, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y270, Direction.SOUTH, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y270, Direction.WEST, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y0, Direction.DOWN, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y0, Direction.NORTH, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y90, Direction.DOWN, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y180, Direction.DOWN, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y180, Direction.SOUTH, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y270, Direction.DOWN, field_4262);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y90, Direction.UP, field_4263);
        BakedQuadFactory.method_3466(ModelRotation.X0_Y270, Direction.DOWN, field_4263);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y0, Direction.EAST, field_4263);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y90, Direction.EAST, field_4263);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y90, Direction.NORTH, field_4263);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y90, Direction.SOUTH, field_4263);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y180, Direction.EAST, field_4263);
        BakedQuadFactory.method_3466(ModelRotation.X90_Y270, Direction.EAST, field_4263);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y0, Direction.WEST, field_4263);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y90, Direction.DOWN, field_4263);
        BakedQuadFactory.method_3466(ModelRotation.X180_Y270, Direction.UP, field_4263);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y90, Direction.WEST, field_4263);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y180, Direction.WEST, field_4263);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y270, Direction.NORTH, field_4263);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y270, Direction.SOUTH, field_4263);
        BakedQuadFactory.method_3466(ModelRotation.X270_Y270, Direction.WEST, field_4263);
    }

    @Environment(value=EnvType.CLIENT)
    static abstract class class_797 {
        private class_797() {
        }

        public ModelElementTexture method_3469(ModelElementTexture modelElementTexture) {
            float f = modelElementTexture.getU(modelElementTexture.method_3414(0));
            float g = modelElementTexture.getV(modelElementTexture.method_3414(0));
            float h = modelElementTexture.getU(modelElementTexture.method_3414(2));
            float i = modelElementTexture.getV(modelElementTexture.method_3414(2));
            return this.method_3470(f, g, h, i);
        }

        abstract ModelElementTexture method_3470(float var1, float var2, float var3, float var4);
    }
}
