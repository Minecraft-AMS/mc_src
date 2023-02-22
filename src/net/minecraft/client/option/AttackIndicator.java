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
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public final class AttackIndicator
extends Enum<AttackIndicator> {
    public static final /* enum */ AttackIndicator OFF = new AttackIndicator(0, "options.off");
    public static final /* enum */ AttackIndicator CROSSHAIR = new AttackIndicator(1, "options.attack.crosshair");
    public static final /* enum */ AttackIndicator HOTBAR = new AttackIndicator(2, "options.attack.hotbar");
    private static final AttackIndicator[] VALUES;
    private final int id;
    private final String translationKey;
    private static final /* synthetic */ AttackIndicator[] field_18157;

    public static AttackIndicator[] values() {
        return (AttackIndicator[])field_18157.clone();
    }

    public static AttackIndicator valueOf(String string) {
        return Enum.valueOf(AttackIndicator.class, string);
    }

    private AttackIndicator(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public int getId() {
        return this.id;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public static AttackIndicator byId(int id) {
        return VALUES[MathHelper.floorMod(id, VALUES.length)];
    }

    private static /* synthetic */ AttackIndicator[] method_36858() {
        return new AttackIndicator[]{OFF, CROSSHAIR, HOTBAR};
    }

    static {
        field_18157 = AttackIndicator.method_36858();
        VALUES = (AttackIndicator[])Arrays.stream(AttackIndicator.values()).sorted(Comparator.comparingInt(AttackIndicator::getId)).toArray(AttackIndicator[]::new);
    }
}

