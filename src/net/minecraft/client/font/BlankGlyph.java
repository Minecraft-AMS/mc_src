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
public final class BlankGlyph
extends Enum<BlankGlyph>
implements RenderableGlyph {
    public static final /* enum */ BlankGlyph INSTANCE = new BlankGlyph();
    private static final int WIDTH = 5;
    private static final int HEIGHT = 8;
    private static final NativeImage IMAGE;
    private static final /* synthetic */ BlankGlyph[] field_2282;

    public static BlankGlyph[] values() {
        return (BlankGlyph[])field_2282.clone();
    }

    public static BlankGlyph valueOf(String string) {
        return Enum.valueOf(BlankGlyph.class, string);
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

    private static /* synthetic */ BlankGlyph[] method_36874() {
        return new BlankGlyph[]{INSTANCE};
    }

    static {
        field_2282 = BlankGlyph.method_36874();
        IMAGE = Util.make(new NativeImage(NativeImage.Format.RGBA, 5, 8, false), image -> {
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 5; ++j) {
                    boolean bl = j == 0 || j + 1 == 5 || i == 0 || i + 1 == 8;
                    image.setColor(j, i, bl ? -1 : 0);
                }
            }
            image.untrack();
        });
    }
}

