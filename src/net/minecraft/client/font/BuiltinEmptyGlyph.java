/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;

@Environment(value=EnvType.CLIENT)
public final class BuiltinEmptyGlyph
extends Enum<BuiltinEmptyGlyph>
implements Glyph {
    public static final /* enum */ BuiltinEmptyGlyph WHITE = new BuiltinEmptyGlyph(() -> BuiltinEmptyGlyph.createRectImage(5, 8, (x, y) -> -1));
    public static final /* enum */ BuiltinEmptyGlyph MISSING = new BuiltinEmptyGlyph(() -> {
        int i = 5;
        int j = 8;
        return BuiltinEmptyGlyph.createRectImage(5, 8, (x, y) -> {
            boolean bl = x == 0 || x + 1 == 5 || y == 0 || y + 1 == 8;
            return bl ? -1 : 0;
        });
    });
    final NativeImage image;
    private static final /* synthetic */ BuiltinEmptyGlyph[] field_37901;

    public static BuiltinEmptyGlyph[] values() {
        return (BuiltinEmptyGlyph[])field_37901.clone();
    }

    public static BuiltinEmptyGlyph valueOf(String string) {
        return Enum.valueOf(BuiltinEmptyGlyph.class, string);
    }

    private static NativeImage createRectImage(int width, int height, ColorSupplier colorSupplier) {
        NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                nativeImage.setColor(j, i, colorSupplier.getColor(j, i));
            }
        }
        nativeImage.untrack();
        return nativeImage;
    }

    private BuiltinEmptyGlyph(Supplier<NativeImage> imageSupplier) {
        this.image = imageSupplier.get();
    }

    @Override
    public float getAdvance() {
        return this.image.getWidth() + 1;
    }

    @Override
    public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> function) {
        return function.apply(new RenderableGlyph(){

            @Override
            public int getWidth() {
                return BuiltinEmptyGlyph.this.image.getWidth();
            }

            @Override
            public int getHeight() {
                return BuiltinEmptyGlyph.this.image.getHeight();
            }

            @Override
            public float getOversample() {
                return 1.0f;
            }

            @Override
            public void upload(int x, int y) {
                BuiltinEmptyGlyph.this.image.upload(0, x, y, false);
            }

            @Override
            public boolean hasColor() {
                return true;
            }
        });
    }

    private static /* synthetic */ BuiltinEmptyGlyph[] method_41838() {
        return new BuiltinEmptyGlyph[]{WHITE, MISSING};
    }

    static {
        field_37901 = BuiltinEmptyGlyph.method_41838();
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface ColorSupplier {
        public int getColor(int var1, int var2);
    }
}

