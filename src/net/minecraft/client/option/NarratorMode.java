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
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public final class NarratorMode
extends Enum<NarratorMode> {
    public static final /* enum */ NarratorMode OFF = new NarratorMode(0, "options.narrator.off");
    public static final /* enum */ NarratorMode ALL = new NarratorMode(1, "options.narrator.all");
    public static final /* enum */ NarratorMode CHAT = new NarratorMode(2, "options.narrator.chat");
    public static final /* enum */ NarratorMode SYSTEM = new NarratorMode(3, "options.narrator.system");
    private static final NarratorMode[] VALUES;
    private final int id;
    private final Text name;
    private static final /* synthetic */ NarratorMode[] field_18183;

    public static NarratorMode[] values() {
        return (NarratorMode[])field_18183.clone();
    }

    public static NarratorMode valueOf(String string) {
        return Enum.valueOf(NarratorMode.class, string);
    }

    private NarratorMode(int id, String name) {
        this.id = id;
        this.name = Text.translatable(name);
    }

    public int getId() {
        return this.id;
    }

    public Text getName() {
        return this.name;
    }

    public static NarratorMode byId(int id) {
        return VALUES[MathHelper.floorMod(id, VALUES.length)];
    }

    public boolean shouldNarrateChat() {
        return this == ALL || this == CHAT;
    }

    public boolean shouldNarrateSystem() {
        return this == ALL || this == SYSTEM;
    }

    private static /* synthetic */ NarratorMode[] method_36864() {
        return new NarratorMode[]{OFF, ALL, CHAT, SYSTEM};
    }

    static {
        field_18183 = NarratorMode.method_36864();
        VALUES = (NarratorMode[])Arrays.stream(NarratorMode.values()).sorted(Comparator.comparingInt(NarratorMode::getId)).toArray(NarratorMode[]::new);
    }
}

