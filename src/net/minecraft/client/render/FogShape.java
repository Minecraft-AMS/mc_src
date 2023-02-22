/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class FogShape
extends Enum<FogShape> {
    public static final /* enum */ FogShape SPHERE = new FogShape(0);
    public static final /* enum */ FogShape CYLINDER = new FogShape(1);
    private final int id;
    private static final /* synthetic */ FogShape[] field_36353;

    public static FogShape[] values() {
        return (FogShape[])field_36353.clone();
    }

    public static FogShape valueOf(String string) {
        return Enum.valueOf(FogShape.class, string);
    }

    private FogShape(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    private static /* synthetic */ FogShape[] method_40037() {
        return new FogShape[]{SPHERE, CYLINDER};
    }

    static {
        field_36353 = FogShape.method_40037();
    }
}

