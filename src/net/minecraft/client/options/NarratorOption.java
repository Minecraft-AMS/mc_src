/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public enum NarratorOption {
    OFF(0, "options.narrator.off"),
    ALL(1, "options.narrator.all"),
    CHAT(2, "options.narrator.chat"),
    SYSTEM(3, "options.narrator.system");

    private static final NarratorOption[] VALUES;
    private final int id;
    private final String translationKey;

    private NarratorOption(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public int getId() {
        return this.id;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public static NarratorOption byId(int id) {
        return VALUES[MathHelper.floorMod(id, VALUES.length)];
    }

    static {
        VALUES = (NarratorOption[])Arrays.stream(NarratorOption.values()).sorted(Comparator.comparingInt(NarratorOption::getId)).toArray(NarratorOption[]::new);
    }
}

