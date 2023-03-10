/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public final class RealmsWorldGeneratorType
extends Enum<RealmsWorldGeneratorType> {
    public static final /* enum */ RealmsWorldGeneratorType DEFAULT = new RealmsWorldGeneratorType(0, new TranslatableText("generator.default"));
    public static final /* enum */ RealmsWorldGeneratorType FLAT = new RealmsWorldGeneratorType(1, new TranslatableText("generator.flat"));
    public static final /* enum */ RealmsWorldGeneratorType LARGE_BIOMES = new RealmsWorldGeneratorType(2, new TranslatableText("generator.large_biomes"));
    public static final /* enum */ RealmsWorldGeneratorType AMPLIFIED = new RealmsWorldGeneratorType(3, new TranslatableText("generator.amplified"));
    private final int id;
    private final Text text;
    private static final /* synthetic */ RealmsWorldGeneratorType[] field_27950;

    public static RealmsWorldGeneratorType[] values() {
        return (RealmsWorldGeneratorType[])field_27950.clone();
    }

    public static RealmsWorldGeneratorType valueOf(String string) {
        return Enum.valueOf(RealmsWorldGeneratorType.class, string);
    }

    private RealmsWorldGeneratorType(int id, Text text) {
        this.id = id;
        this.text = text;
    }

    public Text getText() {
        return this.text;
    }

    public int getId() {
        return this.id;
    }

    private static /* synthetic */ RealmsWorldGeneratorType[] method_36856() {
        return new RealmsWorldGeneratorType[]{DEFAULT, FLAT, LARGE_BIOMES, AMPLIFIED};
    }

    static {
        field_27950 = RealmsWorldGeneratorType.method_36856();
    }
}

