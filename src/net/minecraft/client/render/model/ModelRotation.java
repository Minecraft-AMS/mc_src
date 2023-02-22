/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public final class ModelRotation
extends Enum<ModelRotation>
implements ModelBakeSettings {
    public static final /* enum */ ModelRotation X0_Y0 = new ModelRotation(0, 0);
    public static final /* enum */ ModelRotation X0_Y90 = new ModelRotation(0, 90);
    public static final /* enum */ ModelRotation X0_Y180 = new ModelRotation(0, 180);
    public static final /* enum */ ModelRotation X0_Y270 = new ModelRotation(0, 270);
    public static final /* enum */ ModelRotation X90_Y0 = new ModelRotation(90, 0);
    public static final /* enum */ ModelRotation X90_Y90 = new ModelRotation(90, 90);
    public static final /* enum */ ModelRotation X90_Y180 = new ModelRotation(90, 180);
    public static final /* enum */ ModelRotation X90_Y270 = new ModelRotation(90, 270);
    public static final /* enum */ ModelRotation X180_Y0 = new ModelRotation(180, 0);
    public static final /* enum */ ModelRotation X180_Y90 = new ModelRotation(180, 90);
    public static final /* enum */ ModelRotation X180_Y180 = new ModelRotation(180, 180);
    public static final /* enum */ ModelRotation X180_Y270 = new ModelRotation(180, 270);
    public static final /* enum */ ModelRotation X270_Y0 = new ModelRotation(270, 0);
    public static final /* enum */ ModelRotation X270_Y90 = new ModelRotation(270, 90);
    public static final /* enum */ ModelRotation X270_Y180 = new ModelRotation(270, 180);
    public static final /* enum */ ModelRotation X270_Y270 = new ModelRotation(270, 270);
    private static final int MAX_ROTATION = 360;
    private static final Map<Integer, ModelRotation> BY_INDEX;
    private final AffineTransformation rotation;
    private final DirectionTransformation directionTransformation;
    private final int index;
    private static final /* synthetic */ ModelRotation[] field_5365;

    public static ModelRotation[] values() {
        return (ModelRotation[])field_5365.clone();
    }

    public static ModelRotation valueOf(String string) {
        return Enum.valueOf(ModelRotation.class, string);
    }

    private static int getIndex(int x, int y) {
        return x * 360 + y;
    }

    private ModelRotation(int x, int y) {
        int j;
        this.index = ModelRotation.getIndex(x, y);
        Quaternion quaternion = Vec3f.POSITIVE_Y.getDegreesQuaternion(-y);
        quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getDegreesQuaternion(-x));
        DirectionTransformation directionTransformation = DirectionTransformation.IDENTITY;
        for (j = 0; j < y; j += 90) {
            directionTransformation = directionTransformation.prepend(DirectionTransformation.ROT_90_Y_NEG);
        }
        for (j = 0; j < x; j += 90) {
            directionTransformation = directionTransformation.prepend(DirectionTransformation.ROT_90_X_NEG);
        }
        this.rotation = new AffineTransformation(null, quaternion, null, null);
        this.directionTransformation = directionTransformation;
    }

    @Override
    public AffineTransformation getRotation() {
        return this.rotation;
    }

    public static ModelRotation get(int x, int y) {
        return BY_INDEX.get(ModelRotation.getIndex(MathHelper.floorMod(x, 360), MathHelper.floorMod(y, 360)));
    }

    public DirectionTransformation getDirectionTransformation() {
        return this.directionTransformation;
    }

    private static /* synthetic */ ModelRotation[] method_36925() {
        return new ModelRotation[]{X0_Y0, X0_Y90, X0_Y180, X0_Y270, X90_Y0, X90_Y90, X90_Y180, X90_Y270, X180_Y0, X180_Y90, X180_Y180, X180_Y270, X270_Y0, X270_Y90, X270_Y180, X270_Y270};
    }

    static {
        field_5365 = ModelRotation.method_36925();
        BY_INDEX = Arrays.stream(ModelRotation.values()).collect(Collectors.toMap(rotation -> rotation.index, modelRotation -> modelRotation));
    }
}

