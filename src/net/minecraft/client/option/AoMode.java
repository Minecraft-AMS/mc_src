/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public final class AoMode
extends Enum<AoMode>
implements TranslatableOption {
    public static final /* enum */ AoMode OFF = new AoMode(0, "options.ao.off");
    public static final /* enum */ AoMode MIN = new AoMode(1, "options.ao.min");
    public static final /* enum */ AoMode MAX = new AoMode(2, "options.ao.max");
    private static final AoMode[] VALUES;
    private final int id;
    private final String translationKey;
    private static final /* synthetic */ AoMode[] field_18150;

    public static AoMode[] values() {
        return (AoMode[])field_18150.clone();
    }

    public static AoMode valueOf(String string) {
        return Enum.valueOf(AoMode.class, string);
    }

    private AoMode(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    public static AoMode byId(int id) {
        return VALUES[MathHelper.floorMod(id, VALUES.length)];
    }

    private static /* synthetic */ AoMode[] method_36857() {
        return new AoMode[]{OFF, MIN, MAX};
    }

    static {
        field_18150 = AoMode.method_36857();
        VALUES = (AoMode[])Arrays.stream(AoMode.values()).sorted(Comparator.comparingInt(AoMode::getId)).toArray(AoMode[]::new);
    }
}

