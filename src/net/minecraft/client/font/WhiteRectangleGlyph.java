/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public final class WhiteRectangleGlyph
extends Enum<WhiteRectangleGlyph>
implements RenderableGlyph {
    public static final /* enum */ WhiteRectangleGlyph INSTANCE = new WhiteRectangleGlyph();
    private static final int field_32230 = 5;
    private static final int field_32231 = 8;
    private static final NativeImage IMAGE;
    private static final /* synthetic */ WhiteRectangleGlyph[] field_20914;

    public static WhiteRectangleGlyph[] values() {
        return (WhiteRectangleGlyph[])field_20914.clone();
    }

    public static WhiteRectangleGlyph valueOf(String string) {
        return Enum.valueOf(WhiteRectangleGlyph.class, string);
    }

    @Override
    public int getWidth() {
        return 5;
    }

    @Override
    public int getHeight() {
        return 8;
    }

    @Override
    public float getAdvance() {
        return 6.0f;
    }

    @Override
    public float getOversample() {
        return 1.0f;
    }

    @Override
    public void upload(int x, int y) {
        IMAGE.upload(0, x, y, false);
    }

    @Override
    public boolean hasColor() {
        return true;
    }

    private static /* synthetic */ WhiteRectangleGlyph[] method_36875() {
        return new WhiteRectangleGlyph[]{INSTANCE};
    }

    static {
        field_20914 = WhiteRectangleGlyph.method_36875();
        IMAGE = Util.make(new NativeImage(NativeImage.Format.RGBA, 5, 8, false), nativeImage -> {
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 5; ++j) {
                    boolean bl = j == 0 || j + 1 == 5 || i == 0 || i + 1 == 8;
                    nativeImage.setColor(j, i, -1);
                }
            }
            nativeImage.untrack();
        });
    }
}

