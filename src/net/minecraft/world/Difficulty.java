/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

public enum Difficulty {
    PEACEFUL(0, "peaceful"),
    EASY(1, "easy"),
    NORMAL(2, "normal"),
    HARD(3, "hard");

    private static final Difficulty[] BY_NAME;
    private final int id;
    private final String name;

    private Difficulty(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public Text getTranslatableName() {
        return new TranslatableText("options.difficulty." + this.name);
    }

    public static Difficulty byOrdinal(int ordinal) {
        return BY_NAME[ordinal % BY_NAME.length];
    }

    @Nullable
    public static Difficulty byName(String name) {
        for (Difficulty difficulty : Difficulty.values()) {
            if (!difficulty.name.equals(name)) continue;
            return difficulty;
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    @Environment(value=EnvType.CLIENT)
    public Difficulty cycle() {
        return BY_NAME[(this.id + 1) % BY_NAME.length];
    }

    static {
        BY_NAME = (Difficulty[])Arrays.stream(Difficulty.values()).sorted(Comparator.comparingInt(Difficulty::getId)).toArray(Difficulty[]::new);
    }
}

