/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

@Environment(value=EnvType.CLIENT)
public final class ParticlesMode
extends Enum<ParticlesMode>
implements TranslatableOption {
    public static final /* enum */ ParticlesMode ALL = new ParticlesMode(0, "options.particles.all");
    public static final /* enum */ ParticlesMode DECREASED = new ParticlesMode(1, "options.particles.decreased");
    public static final /* enum */ ParticlesMode MINIMAL = new ParticlesMode(2, "options.particles.minimal");
    private static final IntFunction<ParticlesMode> BY_ID;
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

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public static ParticlesMode byId(int id) {
        return BY_ID.apply(id);
    }

    private static /* synthetic */ ParticlesMode[] method_36865() {
        return new ParticlesMode[]{ALL, DECREASED, MINIMAL};
    }

    static {
        field_18203 = ParticlesMode.method_36865();
        BY_ID = ValueLists.createIdToValueFunction(ParticlesMode::getId, ParticlesMode.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }
}

