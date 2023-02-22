/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.booleans.BooleanArrayList
 *  it.unimi.dsi.fastutil.booleans.BooleanList
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 */
package net.minecraft.util.math;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisTransformation;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;

public final class DirectionTransformation
extends Enum<DirectionTransformation>
implements StringIdentifiable {
    public static final /* enum */ DirectionTransformation IDENTITY = new DirectionTransformation("identity", AxisTransformation.P123, false, false, false);
    public static final /* enum */ DirectionTransformation ROT_180_FACE_XY = new DirectionTransformation("rot_180_face_xy", AxisTransformation.P123, true, true, false);
    public static final /* enum */ DirectionTransformation ROT_180_FACE_XZ = new DirectionTransformation("rot_180_face_xz", AxisTransformation.P123, true, false, true);
    public static final /* enum */ DirectionTransformation ROT_180_FACE_YZ = new DirectionTransformation("rot_180_face_yz", AxisTransformation.P123, false, true, true);
    public static final /* enum */ DirectionTransformation ROT_120_NNN = new DirectionTransformation("rot_120_nnn", AxisTransformation.P231, false, false, false);
    public static final /* enum */ DirectionTransformation ROT_120_NNP = new DirectionTransformation("rot_120_nnp", AxisTransformation.P312, true, false, true);
    public static final /* enum */ DirectionTransformation ROT_120_NPN = new DirectionTransformation("rot_120_npn", AxisTransformation.P312, false, true, true);
    public static final /* enum */ DirectionTransformation ROT_120_NPP = new DirectionTransformation("rot_120_npp", AxisTransformation.P231, true, false, true);
    public static final /* enum */ DirectionTransformation ROT_120_PNN = new DirectionTransformation("rot_120_pnn", AxisTransformation.P312, true, true, false);
    public static final /* enum */ DirectionTransformation ROT_120_PNP = new DirectionTransformation("rot_120_pnp", AxisTransformation.P231, true, true, false);
    public static final /* enum */ DirectionTransformation ROT_120_PPN = new DirectionTransformation("rot_120_ppn", AxisTransformation.P231, false, true, true);
    public static final /* enum */ DirectionTransformation ROT_120_PPP = new DirectionTransformation("rot_120_ppp", AxisTransformation.P312, false, false, false);
    public static final /* enum */ DirectionTransformation ROT_180_EDGE_XY_NEG = new DirectionTransformation("rot_180_edge_xy_neg", AxisTransformation.P213, true, true, true);
    public static final /* enum */ DirectionTransformation ROT_180_EDGE_XY_POS = new DirectionTransformation("rot_180_edge_xy_pos", AxisTransformation.P213, false, false, true);
    public static final /* enum */ DirectionTransformation ROT_180_EDGE_XZ_NEG = new DirectionTransformation("rot_180_edge_xz_neg", AxisTransformation.P321, true, true, true);
    public static final /* enum */ DirectionTransformation ROT_180_EDGE_XZ_POS = new DirectionTransformation("rot_180_edge_xz_pos", AxisTransformation.P321, false, true, false);
    public static final /* enum */ DirectionTransformation ROT_180_EDGE_YZ_NEG = new DirectionTransformation("rot_180_edge_yz_neg", AxisTransformation.P132, true, true, true);
    public static final /* enum */ DirectionTransformation ROT_180_EDGE_YZ_POS = new DirectionTransformation("rot_180_edge_yz_pos", AxisTransformation.P132, true, false, false);
    public static final /* enum */ DirectionTransformation ROT_90_X_NEG = new DirectionTransformation("rot_90_x_neg", AxisTransformation.P132, false, false, true);
    public static final /* enum */ DirectionTransformation ROT_90_X_POS = new DirectionTransformation("rot_90_x_pos", AxisTransformation.P132, false, true, false);
    public static final /* enum */ DirectionTransformation ROT_90_Y_NEG = new DirectionTransformation("rot_90_y_neg", AxisTransformation.P321, true, false, false);
    public static final /* enum */ DirectionTransformation ROT_90_Y_POS = new DirectionTransformation("rot_90_y_pos", AxisTransformation.P321, false, false, true);
    public static final /* enum */ DirectionTransformation ROT_90_Z_NEG = new DirectionTransformation("rot_90_z_neg", AxisTransformation.P213, false, true, false);
    public static final /* enum */ DirectionTransformation ROT_90_Z_POS = new DirectionTransformation("rot_90_z_pos", AxisTransformation.P213, true, false, false);
    public static final /* enum */ DirectionTransformation INVERSION = new DirectionTransformation("inversion", AxisTransformation.P123, true, true, true);
    public static final /* enum */ DirectionTransformation INVERT_X = new DirectionTransformation("invert_x", AxisTransformation.P123, true, false, false);
    public static final /* enum */ DirectionTransformation INVERT_Y = new DirectionTransformation("invert_y", AxisTransformation.P123, false, true, false);
    public static final /* enum */ DirectionTransformation INVERT_Z = new DirectionTransformation("invert_z", AxisTransformation.P123, false, false, true);
    public static final /* enum */ DirectionTransformation ROT_60_REF_NNN = new DirectionTransformation("rot_60_ref_nnn", AxisTransformation.P312, true, true, true);
    public static final /* enum */ DirectionTransformation ROT_60_REF_NNP = new DirectionTransformation("rot_60_ref_nnp", AxisTransformation.P231, true, false, false);
    public static final /* enum */ DirectionTransformation ROT_60_REF_NPN = new DirectionTransformation("rot_60_ref_npn", AxisTransformation.P231, false, false, true);
    public static final /* enum */ DirectionTransformation ROT_60_REF_NPP = new DirectionTransformation("rot_60_ref_npp", AxisTransformation.P312, false, false, true);
    public static final /* enum */ DirectionTransformation ROT_60_REF_PNN = new DirectionTransformation("rot_60_ref_pnn", AxisTransformation.P231, false, true, false);
    public static final /* enum */ DirectionTransformation ROT_60_REF_PNP = new DirectionTransformation("rot_60_ref_pnp", AxisTransformation.P312, true, false, false);
    public static final /* enum */ DirectionTransformation ROT_60_REF_PPN = new DirectionTransformation("rot_60_ref_ppn", AxisTransformation.P312, false, true, false);
    public static final /* enum */ DirectionTransformation ROT_60_REF_PPP = new DirectionTransformation("rot_60_ref_ppp", AxisTransformation.P231, true, true, true);
    public static final /* enum */ DirectionTransformation SWAP_XY = new DirectionTransformation("swap_xy", AxisTransformation.P213, false, false, false);
    public static final /* enum */ DirectionTransformation SWAP_YZ = new DirectionTransformation("swap_yz", AxisTransformation.P132, false, false, false);
    public static final /* enum */ DirectionTransformation SWAP_XZ = new DirectionTransformation("swap_xz", AxisTransformation.P321, false, false, false);
    public static final /* enum */ DirectionTransformation SWAP_NEG_XY = new DirectionTransformation("swap_neg_xy", AxisTransformation.P213, true, true, false);
    public static final /* enum */ DirectionTransformation SWAP_NEG_YZ = new DirectionTransformation("swap_neg_yz", AxisTransformation.P132, false, true, true);
    public static final /* enum */ DirectionTransformation SWAP_NEG_XZ = new DirectionTransformation("swap_neg_xz", AxisTransformation.P321, true, false, true);
    public static final /* enum */ DirectionTransformation ROT_90_REF_X_NEG = new DirectionTransformation("rot_90_ref_x_neg", AxisTransformation.P132, true, false, true);
    public static final /* enum */ DirectionTransformation ROT_90_REF_X_POS = new DirectionTransformation("rot_90_ref_x_pos", AxisTransformation.P132, true, true, false);
    public static final /* enum */ DirectionTransformation ROT_90_REF_Y_NEG = new DirectionTransformation("rot_90_ref_y_neg", AxisTransformation.P321, true, true, false);
    public static final /* enum */ DirectionTransformation ROT_90_REF_Y_POS = new DirectionTransformation("rot_90_ref_y_pos", AxisTransformation.P321, false, true, true);
    public static final /* enum */ DirectionTransformation ROT_90_REF_Z_NEG = new DirectionTransformation("rot_90_ref_z_neg", AxisTransformation.P213, false, true, true);
    public static final /* enum */ DirectionTransformation ROT_90_REF_Z_POS = new DirectionTransformation("rot_90_ref_z_pos", AxisTransformation.P213, true, false, true);
    private final Matrix3f matrix;
    private final String name;
    @Nullable
    private Map<Direction, Direction> mappings;
    private final boolean flipX;
    private final boolean flipY;
    private final boolean flipZ;
    private final AxisTransformation axisTransformation;
    private static final DirectionTransformation[][] COMBINATIONS;
    private static final DirectionTransformation[] INVERSES;
    private static final /* synthetic */ DirectionTransformation[] field_23298;

    public static DirectionTransformation[] values() {
        return (DirectionTransformation[])field_23298.clone();
    }

    public static DirectionTransformation valueOf(String string) {
        return Enum.valueOf(DirectionTransformation.class, string);
    }

    private DirectionTransformation(String name, AxisTransformation axisTransformation, boolean flipX, boolean flipY, boolean flipZ) {
        this.name = name;
        this.flipX = flipX;
        this.flipY = flipY;
        this.flipZ = flipZ;
        this.axisTransformation = axisTransformation;
        this.matrix = new Matrix3f().scaling(flipX ? -1.0f : 1.0f, flipY ? -1.0f : 1.0f, flipZ ? -1.0f : 1.0f);
        this.matrix.mul((Matrix3fc)axisTransformation.getMatrix());
    }

    private BooleanList getAxisFlips() {
        return new BooleanArrayList(new boolean[]{this.flipX, this.flipY, this.flipZ});
    }

    public DirectionTransformation prepend(DirectionTransformation transformation) {
        return COMBINATIONS[this.ordinal()][transformation.ordinal()];
    }

    public DirectionTransformation inverse() {
        return INVERSES[this.ordinal()];
    }

    public Matrix3f getMatrix() {
        return new Matrix3f((Matrix3fc)this.matrix);
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public Direction map(Direction direction) {
        if (this.mappings == null) {
            this.mappings = Maps.newEnumMap(Direction.class);
            Direction.Axis[] axiss = Direction.Axis.values();
            for (Direction direction2 : Direction.values()) {
                Direction.Axis axis = direction2.getAxis();
                Direction.AxisDirection axisDirection = direction2.getDirection();
                Direction.Axis axis2 = axiss[this.axisTransformation.map(axis.ordinal())];
                Direction.AxisDirection axisDirection2 = this.shouldFlipDirection(axis2) ? axisDirection.getOpposite() : axisDirection;
                Direction direction3 = Direction.from(axis2, axisDirection2);
                this.mappings.put(direction2, direction3);
            }
        }
        return this.mappings.get(direction);
    }

    public boolean shouldFlipDirection(Direction.Axis axis) {
        switch (axis) {
            case X: {
                return this.flipX;
            }
            case Y: {
                return this.flipY;
            }
        }
        return this.flipZ;
    }

    public JigsawOrientation mapJigsawOrientation(JigsawOrientation orientation) {
        return JigsawOrientation.byDirections(this.map(orientation.getFacing()), this.map(orientation.getRotation()));
    }

    private static /* synthetic */ DirectionTransformation[] method_36928() {
        return new DirectionTransformation[]{IDENTITY, ROT_180_FACE_XY, ROT_180_FACE_XZ, ROT_180_FACE_YZ, ROT_120_NNN, ROT_120_NNP, ROT_120_NPN, ROT_120_NPP, ROT_120_PNN, ROT_120_PNP, ROT_120_PPN, ROT_120_PPP, ROT_180_EDGE_XY_NEG, ROT_180_EDGE_XY_POS, ROT_180_EDGE_XZ_NEG, ROT_180_EDGE_XZ_POS, ROT_180_EDGE_YZ_NEG, ROT_180_EDGE_YZ_POS, ROT_90_X_NEG, ROT_90_X_POS, ROT_90_Y_NEG, ROT_90_Y_POS, ROT_90_Z_NEG, ROT_90_Z_POS, INVERSION, INVERT_X, INVERT_Y, INVERT_Z, ROT_60_REF_NNN, ROT_60_REF_NNP, ROT_60_REF_NPN, ROT_60_REF_NPP, ROT_60_REF_PNN, ROT_60_REF_PNP, ROT_60_REF_PPN, ROT_60_REF_PPP, SWAP_XY, SWAP_YZ, SWAP_XZ, SWAP_NEG_XY, SWAP_NEG_YZ, SWAP_NEG_XZ, ROT_90_REF_X_NEG, ROT_90_REF_X_POS, ROT_90_REF_Y_NEG, ROT_90_REF_Y_POS, ROT_90_REF_Z_NEG, ROT_90_REF_Z_POS};
    }

    static {
        field_23298 = DirectionTransformation.method_36928();
        COMBINATIONS = Util.make(new DirectionTransformation[DirectionTransformation.values().length][DirectionTransformation.values().length], directionTransformations -> {
            Map<Pair, DirectionTransformation> map = Arrays.stream(DirectionTransformation.values()).collect(Collectors.toMap(directionTransformation -> Pair.of((Object)((Object)directionTransformation.axisTransformation), (Object)directionTransformation.getAxisFlips()), directionTransformation -> directionTransformation));
            for (DirectionTransformation directionTransformation2 : DirectionTransformation.values()) {
                for (DirectionTransformation directionTransformation22 : DirectionTransformation.values()) {
                    BooleanList booleanList = directionTransformation2.getAxisFlips();
                    BooleanList booleanList2 = directionTransformation22.getAxisFlips();
                    AxisTransformation axisTransformation = directionTransformation22.axisTransformation.prepend(directionTransformation2.axisTransformation);
                    BooleanArrayList booleanArrayList = new BooleanArrayList(3);
                    for (int i = 0; i < 3; ++i) {
                        booleanArrayList.add(booleanList.getBoolean(i) ^ booleanList2.getBoolean(directionTransformation2.axisTransformation.map(i)));
                    }
                    directionTransformations[directionTransformation2.ordinal()][directionTransformation22.ordinal()] = map.get(Pair.of((Object)((Object)axisTransformation), (Object)booleanArrayList));
                }
            }
        });
        INVERSES = (DirectionTransformation[])Arrays.stream(DirectionTransformation.values()).map((? super T directionTransformation) -> Arrays.stream(DirectionTransformation.values()).filter(directionTransformation2 -> directionTransformation.prepend((DirectionTransformation)directionTransformation2) == IDENTITY).findAny().get()).toArray(DirectionTransformation[]::new);
    }
}

