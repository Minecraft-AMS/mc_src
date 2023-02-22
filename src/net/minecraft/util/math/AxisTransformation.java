/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math;

import java.util.Arrays;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix3f;

public final class AxisTransformation
extends Enum<AxisTransformation> {
    public static final /* enum */ AxisTransformation P123 = new AxisTransformation(0, 1, 2);
    public static final /* enum */ AxisTransformation P213 = new AxisTransformation(1, 0, 2);
    public static final /* enum */ AxisTransformation P132 = new AxisTransformation(0, 2, 1);
    public static final /* enum */ AxisTransformation P231 = new AxisTransformation(1, 2, 0);
    public static final /* enum */ AxisTransformation P312 = new AxisTransformation(2, 0, 1);
    public static final /* enum */ AxisTransformation P321 = new AxisTransformation(2, 1, 0);
    private final int[] mappings;
    private final Matrix3f matrix;
    private static final int field_33113 = 3;
    private static final AxisTransformation[][] COMBINATIONS;
    private static final /* synthetic */ AxisTransformation[] field_23371;

    public static AxisTransformation[] values() {
        return (AxisTransformation[])field_23371.clone();
    }

    public static AxisTransformation valueOf(String string) {
        return Enum.valueOf(AxisTransformation.class, string);
    }

    private AxisTransformation(int xMapping, int yMapping, int zMapping) {
        this.mappings = new int[]{xMapping, yMapping, zMapping};
        this.matrix = new Matrix3f();
        this.matrix.set(0, this.map(0), 1.0f);
        this.matrix.set(1, this.map(1), 1.0f);
        this.matrix.set(2, this.map(2), 1.0f);
    }

    public AxisTransformation prepend(AxisTransformation transformation) {
        return COMBINATIONS[this.ordinal()][transformation.ordinal()];
    }

    public int map(int oldAxis) {
        return this.mappings[oldAxis];
    }

    public Matrix3f getMatrix() {
        return this.matrix;
    }

    private static /* synthetic */ AxisTransformation[] method_36937() {
        return new AxisTransformation[]{P123, P213, P132, P231, P312, P321};
    }

    static {
        field_23371 = AxisTransformation.method_36937();
        COMBINATIONS = Util.make(new AxisTransformation[AxisTransformation.values().length][AxisTransformation.values().length], axisTransformations -> {
            for (AxisTransformation axisTransformation2 : AxisTransformation.values()) {
                for (AxisTransformation axisTransformation22 : AxisTransformation.values()) {
                    AxisTransformation axisTransformation3;
                    int[] is = new int[3];
                    for (int i = 0; i < 3; ++i) {
                        is[i] = axisTransformation2.mappings[axisTransformation22.mappings[i]];
                    }
                    axisTransformations[axisTransformation2.ordinal()][axisTransformation22.ordinal()] = axisTransformation3 = Arrays.stream(AxisTransformation.values()).filter(axisTransformation -> Arrays.equals(axisTransformation.mappings, is)).findFirst().get();
                }
            }
        });
    }
}

