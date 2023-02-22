/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

public final class Difficulty
extends Enum<Difficulty> {
    public static final /* enum */ Difficulty PEACEFUL = new Difficulty(0, "peaceful");
    public static final /* enum */ Difficulty EASY = new Difficulty(1, "easy");
    public static final /* enum */ Difficulty NORMAL = new Difficulty(2, "normal");
    public static final /* enum */ Difficulty HARD = new Difficulty(3, "hard");
    private static final Difficulty[] BY_NAME;
    private final int id;
    private final String name;
    private static final /* synthetic */ Difficulty[] field_5804;

    public static Difficulty[] values() {
        return (Difficulty[])field_5804.clone();
    }

    public static Difficulty valueOf(String string) {
        return Enum.valueOf(Difficulty.class, string);
    }

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

    private static /* synthetic */ Difficulty[] method_36597() {
        return new Difficulty[]{PEACEFUL, EASY, NORMAL, HARD};
    }

    static {
        field_5804 = Difficulty.method_36597();
        BY_NAME = (Difficulty[])Arrays.stream(Difficulty.values()).sorted(Comparator.comparingInt(Difficulty::getId)).toArray(Difficulty[]::new);
    }
}

