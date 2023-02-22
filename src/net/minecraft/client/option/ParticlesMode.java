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
public final class ParticlesMode
extends Enum<ParticlesMode> {
    public static final /* enum */ ParticlesMode ALL = new ParticlesMode(0, "options.particles.all");
    public static final /* enum */ ParticlesMode DECREASED = new ParticlesMode(1, "options.particles.decreased");
    public static final /* enum */ ParticlesMode MINIMAL = new ParticlesMode(2, "options.particles.minimal");
    private static final ParticlesMode[] VALUES;
    private final int id;
    private final String translationKey;
    private static final /* synthetic */ ParticlesMode[] field_18203;

    public static ParticlesMode[] values() {
        return (ParticlesMode[])field_18203.clone();
    }

    public static ParticlesMode valueOf(String string) {
        return Enum.valueOf(ParticlesMode.class, string);
    }

    private ParticlesMode(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public int getId() {
        return this.id;
    }

    public static ParticlesMode byId(int id) {
        return VALUES[MathHelper.floorMod(id, VALUES.length)];
    }

    private static /* synthetic */ ParticlesMode[] method_36865() {
        return new ParticlesMode[]{ALL, DECREASED, MINIMAL};
    }

    static {
        field_18203 = ParticlesMode.method_36865();
        VALUES = (ParticlesMode[])Arrays.stream(ParticlesMode.values()).sorted(Comparator.comparingInt(ParticlesMode::getId)).toArray(ParticlesMode[]::new);
    }
}

