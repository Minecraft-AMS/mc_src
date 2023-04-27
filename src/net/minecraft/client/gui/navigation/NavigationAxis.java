/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.navigation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.NavigationDirection;

@Environment(value=EnvType.CLIENT)
public final class NavigationAxis
extends Enum<NavigationAxis> {
    public static final /* enum */ NavigationAxis HORIZONTAL = new NavigationAxis();
    public static final /* enum */ NavigationAxis VERTICAL = new NavigationAxis();
    private static final /* synthetic */ NavigationAxis[] field_41824;

    public static NavigationAxis[] values() {
        return (NavigationAxis[])field_41824.clone();
    }

    public static NavigationAxis valueOf(String string) {
        return Enum.valueOf(NavigationAxis.class, string);
    }

    public NavigationAxis getOther() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case HORIZONTAL -> VERTICAL;
            case VERTICAL -> HORIZONTAL;
        };
    }

    public NavigationDirection getPositiveDirection() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case HORIZONTAL -> NavigationDirection.RIGHT;
            case VERTICAL -> NavigationDirection.DOWN;
        };
    }

    public NavigationDirection getNegativeDirection() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case HORIZONTAL -> NavigationDirection.LEFT;
            case VERTICAL -> NavigationDirection.UP;
        };
    }

    public NavigationDirection getDirection(boolean positive) {
        return positive ? this.getPositiveDirection() : this.getNegativeDirection();
    }

    private static /* synthetic */ NavigationAxis[] method_48236() {
        return new NavigationAxis[]{HORIZONTAL, VERTICAL};
    }

    static {
        field_41824 = NavigationAxis.method_48236();
    }
}

