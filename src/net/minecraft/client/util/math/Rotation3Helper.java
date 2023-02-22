/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.util.math;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Rotation3;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class Rotation3Helper {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final EnumMap<Direction, Rotation3> DIRECTION_ROTATIONS = Util.make(Maps.newEnumMap(Direction.class), enumMap -> {
        enumMap.put(Direction.SOUTH, Rotation3.identity());
        enumMap.put(Direction.EAST, new Rotation3(null, new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), 90.0f, true), null, null));
        enumMap.put(Direction.WEST, new Rotation3(null, new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), -90.0f, true), null, null));
        enumMap.put(Direction.NORTH, new Rotation3(null, new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), 180.0f, true), null, null));
        enumMap.put(Direction.UP, new Rotation3(null, new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), -90.0f, true), null, null));
        enumMap.put(Direction.DOWN, new Rotation3(null, new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), 90.0f, true), null, null));
    });
    public static final EnumMap<Direction, Rotation3> INVERSED_DIRECTION_ROTATIONS = Util.make(Maps.newEnumMap(Direction.class), enumMap -> {
        for (Direction direction : Direction.values()) {
            enumMap.put(direction, DIRECTION_ROTATIONS.get(direction).invert());
        }
    });

    public static Rotation3 setupUvLock(Rotation3 rotation3) {
        Matrix4f matrix4f = Matrix4f.translate(0.5f, 0.5f, 0.5f);
        matrix4f.multiply(rotation3.getMatrix());
        matrix4f.multiply(Matrix4f.translate(-0.5f, -0.5f, -0.5f));
        return new Rotation3(matrix4f);
    }

    public static Rotation3 uvLock(Rotation3 rotation3, Direction direction, Supplier<String> supplier) {
        Direction direction2 = Direction.transform(rotation3.getMatrix(), direction);
        Rotation3 rotation32 = rotation3.invert();
        if (rotation32 == null) {
            LOGGER.warn(supplier.get());
            return new Rotation3(null, null, new Vector3f(0.0f, 0.0f, 0.0f), null);
        }
        Rotation3 rotation33 = INVERSED_DIRECTION_ROTATIONS.get(direction).multiply(rotation32).multiply(DIRECTION_ROTATIONS.get(direction2));
        return Rotation3Helper.setupUvLock(rotation33);
    }
}

