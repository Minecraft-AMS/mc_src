/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import java.util.EnumSet;
import java.util.Set;

public final class PositionFlag
extends Enum<PositionFlag> {
    public static final /* enum */ PositionFlag X = new PositionFlag(0);
    public static final /* enum */ PositionFlag Y = new PositionFlag(1);
    public static final /* enum */ PositionFlag Z = new PositionFlag(2);
    public static final /* enum */ PositionFlag Y_ROT = new PositionFlag(3);
    public static final /* enum */ PositionFlag X_ROT = new PositionFlag(4);
    public static final Set<PositionFlag> VALUES;
    public static final Set<PositionFlag> ROT;
    private final int shift;
    private static final /* synthetic */ PositionFlag[] field_12402;

    public static PositionFlag[] values() {
        return (PositionFlag[])field_12402.clone();
    }

    public static PositionFlag valueOf(String string) {
        return Enum.valueOf(PositionFlag.class, string);
    }

    private PositionFlag(int shift) {
        this.shift = shift;
    }

    private int getMask() {
        return 1 << this.shift;
    }

    private boolean isSet(int mask) {
        return (mask & this.getMask()) == this.getMask();
    }

    public static Set<PositionFlag> getFlags(int mask) {
        EnumSet<PositionFlag> set = EnumSet.noneOf(PositionFlag.class);
        for (PositionFlag positionFlag : PositionFlag.values()) {
            if (!positionFlag.isSet(mask)) continue;
            set.add(positionFlag);
        }
        return set;
    }

    public static int getBitfield(Set<PositionFlag> flags) {
        int i = 0;
        for (PositionFlag positionFlag : flags) {
            i |= positionFlag.getMask();
        }
        return i;
    }

    private static /* synthetic */ PositionFlag[] method_36952() {
        return new PositionFlag[]{X, Y, Z, Y_ROT, X_ROT};
    }

    static {
        field_12402 = PositionFlag.method_36952();
        VALUES = Set.of(PositionFlag.values());
        ROT = Set.of(X_ROT, Y_ROT);
    }
}

