/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntComparator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.navigation;

import it.unimi.dsi.fastutil.ints.IntComparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.NavigationAxis;

@Environment(value=EnvType.CLIENT)
public final class NavigationDirection
extends Enum<NavigationDirection> {
    public static final /* enum */ NavigationDirection UP = new NavigationDirection();
    public static final /* enum */ NavigationDirection DOWN = new NavigationDirection();
    public static final /* enum */ NavigationDirection LEFT = new NavigationDirection();
    public static final /* enum */ NavigationDirection RIGHT = new NavigationDirection();
    private final IntComparator comparator = (a, b) -> a == b ? 0 : (this.isBefore(a, b) ? -1 : 1);
    private static final /* synthetic */ NavigationDirection[] field_41831;

    public static NavigationDirection[] values() {
        return (NavigationDirection[])field_41831.clone();
    }

    public static NavigationDirection valueOf(String string) {
        return Enum.valueOf(NavigationDirection.class, string);
    }

    public NavigationAxis getAxis() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case UP, DOWN -> NavigationAxis.VERTICAL;
            case LEFT, RIGHT -> NavigationAxis.HORIZONTAL;
        };
    }

    public NavigationDirection getOpposite() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    public boolean isPositive() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case UP, LEFT -> false;
            case DOWN, RIGHT -> true;
        };
    }

    public boolean isAfter(int a, int b) {
        if (this.isPositive()) {
            return a > b;
        }
        return b > a;
    }

    public boolean isBefore(int a, int b) {
        if (this.isPositive()) {
            return a < b;
        }
        return b < a;
    }

    public IntComparator getComparator() {
        return this.comparator;
    }

    private static /* synthetic */ NavigationDirection[] method_48244() {
        return new NavigationDirection[]{UP, DOWN, LEFT, RIGHT};
    }

    static {
        field_41831 = NavigationDirection.method_48244();
    }
}

