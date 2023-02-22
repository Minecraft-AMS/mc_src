/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.advancement;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public final class AdvancementFrame
extends Enum<AdvancementFrame> {
    public static final /* enum */ AdvancementFrame TASK = new AdvancementFrame("task", 0, Formatting.GREEN);
    public static final /* enum */ AdvancementFrame CHALLENGE = new AdvancementFrame("challenge", 26, Formatting.DARK_PURPLE);
    public static final /* enum */ AdvancementFrame GOAL = new AdvancementFrame("goal", 52, Formatting.GREEN);
    private final String id;
    private final int textureV;
    private final Formatting titleFormat;
    private final Text toastText;
    private static final /* synthetic */ AdvancementFrame[] field_1253;

    public static AdvancementFrame[] values() {
        return (AdvancementFrame[])field_1253.clone();
    }

    public static AdvancementFrame valueOf(String string) {
        return Enum.valueOf(AdvancementFrame.class, string);
    }

    private AdvancementFrame(String id, int texV, Formatting titleFormat) {
        this.id = id;
        this.textureV = texV;
        this.titleFormat = titleFormat;
        this.toastText = new TranslatableText("advancements.toast." + id);
    }

    public String getId() {
        return this.id;
    }

    public int getTextureV() {
        return this.textureV;
    }

    public static AdvancementFrame forName(String name) {
        for (AdvancementFrame advancementFrame : AdvancementFrame.values()) {
            if (!advancementFrame.id.equals(name)) continue;
            return advancementFrame;
        }
        throw new IllegalArgumentException("Unknown frame type '" + name + "'");
    }

    public Formatting getTitleFormat() {
        return this.titleFormat;
    }

    public Text getToastText() {
        return this.toastText;
    }

    private static /* synthetic */ AdvancementFrame[] method_36593() {
        return new AdvancementFrame[]{TASK, CHALLENGE, GOAL};
    }

    static {
        field_1253 = AdvancementFrame.method_36593();
    }
}

