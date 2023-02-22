/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.PngFile;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Sprite {
    private final Identifier id;
    protected final int width;
    protected final int height;
    protected NativeImage[] images;
    @Nullable
    protected int[] frameXs;
    @Nullable
    protected int[] frameYs;
    protected NativeImage[] interpolatedImages;
    private AnimationResourceMetadata animationMetadata;
    protected int x;
    protected int y;
    private float uMin;
    private float uMax;
    private float vMin;
    private float vMax;
    protected int frameIndex;
    protected int frameTicks;
    private static final float[] srgbLinearMap = Util.make(new float[256], fs -> {
        for (int i = 0; i < ((float[])fs).length; ++i) {
            fs[i] = (float)Math.pow((float)i / 255.0f, 2.2);
        }
    });

    protected Sprite(Identifier identifier, int i, int j) {
        this.id = identifier;
        this.width = i;
        this.height = j;
    }

    protected Sprite(Identifier identifier, PngFile pngFile, @Nullable AnimationResourceMetadata animationResourceMetadata) {
        this.id = identifier;
        if (animationResourceMetadata != null) {
            Pair<Integer, Integer> pair = Sprite.getDimensions(animationResourceMetadata.getWidth(), animationResourceMetadata.getHeight(), pngFile.width, pngFile.height);
            this.width = (Integer)pair.getFirst();
            this.height = (Integer)pair.getSecond();
            if (!Sprite.isDivisibleBy(pngFile.width, this.width) || !Sprite.isDivisibleBy(pngFile.height, this.height)) {
                throw new IllegalArgumentException(String.format("Image size %s,%s is not multiply of frame size %s,%s", this.width, this.height, pngFile.width, pngFile.height));
            }
        } else {
            this.width = pngFile.width;
            this.height = pngFile.height;
        }
        this.animationMetadata = animationResourceMetadata;
    }

    private static Pair<Integer, Integer> getDimensions(int animationMetadataWidth, int animationMetadataHeight, int width, int height) {
        if (animationMetadataWidth != -1) {
            if (animationMetadataHeight != -1) {
                return Pair.of((Object)animationMetadataWidth, (Object)animationMetadataHeight);
            }
            return Pair.of((Object)animationMetadataWidth, (Object)height);
        }
        if (animationMetadataHeight != -1) {
            return Pair.of((Object)width, (Object)animationMetadataHeight);
        }
        int i = Math.min(width, height);
        return Pair.of((Object)i, (Object)i);
    }

    private static boolean isDivisibleBy(int number, int divisor) {
        return number / divisor * divisor == number;
    }

    private void generateMipmapsInternal(int mipLevels) {
        NativeImage[] nativeImages = new NativeImage[mipLevels + 1];
        nativeImages[0] = this.images[0];
        if (mipLevels > 0) {
            int i;
            boolean bl = false;
            block0: for (i = 0; i < this.images[0].getWidth(); ++i) {
                for (int j = 0; j < this.images[0].getHeight(); ++j) {
                    if (this.images[0].getPixelRgba(i, j) >> 24 != 0) continue;
                    bl = true;
                    break block0;
                }
            }
            for (i = 1; i <= mipLevels; ++i) {
                if (this.images.length > i && this.images[i] != null) {
                    nativeImages[i] = this.images[i];
                    continue;
                }
                NativeImage nativeImage = nativeImages[i - 1];
                NativeImage nativeImage2 = new NativeImage(nativeImage.getWidth() >> 1, nativeImage.getHeight() >> 1, false);
                int k = nativeImage2.getWidth();
                int l = nativeImage2.getHeight();
                for (int m = 0; m < k; ++m) {
                    for (int n = 0; n < l; ++n) {
                        nativeImage2.setPixelRgba(m, n, Sprite.blendPixels(nativeImage.getPixelRgba(m * 2 + 0, n * 2 + 0), nativeImage.getPixelRgba(m * 2 + 1, n * 2 + 0), nativeImage.getPixelRgba(m * 2 + 0, n * 2 + 1), nativeImage.getPixelRgba(m * 2 + 1, n * 2 + 1), bl));
                    }
                }
                nativeImages[i] = nativeImage2;
            }
            for (i = mipLevels + 1; i < this.images.length; ++i) {
                if (this.images[i] == null) continue;
                this.images[i].close();
            }
        }
        this.images = nativeImages;
    }

    private static int blendPixels(int colorTopLeft, int colorTopRight, int colorBottomLeft, int colorBottomRight, boolean hasTransparency) {
        if (hasTransparency) {
            float f = 0.0f;
            float g = 0.0f;
            float h = 0.0f;
            float i = 0.0f;
            if (colorTopLeft >> 24 != 0) {
                f += Sprite.srgbToLinear(colorTopLeft >> 24);
                g += Sprite.srgbToLinear(colorTopLeft >> 16);
                h += Sprite.srgbToLinear(colorTopLeft >> 8);
                i += Sprite.srgbToLinear(colorTopLeft >> 0);
            }
            if (colorTopRight >> 24 != 0) {
                f += Sprite.srgbToLinear(colorTopRight >> 24);
                g += Sprite.srgbToLinear(colorTopRight >> 16);
                h += Sprite.srgbToLinear(colorTopRight >> 8);
                i += Sprite.srgbToLinear(colorTopRight >> 0);
            }
            if (colorBottomLeft >> 24 != 0) {
                f += Sprite.srgbToLinear(colorBottomLeft >> 24);
                g += Sprite.srgbToLinear(colorBottomLeft >> 16);
                h += Sprite.srgbToLinear(colorBottomLeft >> 8);
                i += Sprite.srgbToLinear(colorBottomLeft >> 0);
            }
            if (colorBottomRight >> 24 != 0) {
                f += Sprite.srgbToLinear(colorBottomRight >> 24);
                g += Sprite.srgbToLinear(colorBottomRight >> 16);
                h += Sprite.srgbToLinear(colorBottomRight >> 8);
                i += Sprite.srgbToLinear(colorBottomRight >> 0);
            }
            int j = (int)(Math.pow(f /= 4.0f, 0.45454545454545453) * 255.0);
            int k = (int)(Math.pow(g /= 4.0f, 0.45454545454545453) * 255.0);
            int l = (int)(Math.pow(h /= 4.0f, 0.45454545454545453) * 255.0);
            int m = (int)(Math.pow(i /= 4.0f, 0.45454545454545453) * 255.0);
            if (j < 96) {
                j = 0;
            }
            return j << 24 | k << 16 | l << 8 | m;
        }
        int n = Sprite.blendPixelsComponent(colorTopLeft, colorTopRight, colorBottomLeft, colorBottomRight, 24);
        int o = Sprite.blendPixelsComponent(colorTopLeft, colorTopRight, colorBottomLeft, colorBottomRight, 16);
        int p = Sprite.blendPixelsComponent(colorTopLeft, colorTopRight, colorBottomLeft, colorBottomRight, 8);
        int q = Sprite.blendPixelsComponent(colorTopLeft, colorTopRight, colorBottomLeft, colorBottomRight, 0);
        return n << 24 | o << 16 | p << 8 | q;
    }

    private static int blendPixelsComponent(int colorTopLeft, int colorTopRight, int colorBottomLeft, int colorBottomRight, int componentShift) {
        float f = Sprite.srgbToLinear(colorTopLeft >> componentShift);
        float g = Sprite.srgbToLinear(colorTopRight >> componentShift);
        float h = Sprite.srgbToLinear(colorBottomLeft >> componentShift);
        float i = Sprite.srgbToLinear(colorBottomRight >> componentShift);
        float j = (float)((double)((float)Math.pow((double)(f + g + h + i) * 0.25, 0.45454545454545453)));
        return (int)((double)j * 255.0);
    }

    private static float srgbToLinear(int i) {
        return srgbLinearMap[i & 0xFF];
    }

    private void upload(int frame) {
        int i = 0;
        int j = 0;
        if (this.frameXs != null) {
            i = this.frameXs[frame] * this.width;
            j = this.frameYs[frame] * this.height;
        }
        this.upload(i, j, this.images);
    }

    private void upload(int frameX, int frameY, NativeImage[] output) {
        for (int i = 0; i < this.images.length; ++i) {
            output[i].upload(i, this.x >> i, this.y >> i, frameX >> i, frameY >> i, this.width >> i, this.height >> i, this.images.length > 1);
        }
    }

    public void init(int width, int height, int x, int y) {
        this.x = x;
        this.y = y;
        this.uMin = (float)x / (float)width;
        this.uMax = (float)(x + this.width) / (float)width;
        this.vMin = (float)y / (float)height;
        this.vMax = (float)(y + this.height) / (float)height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public float getMinU() {
        return this.uMin;
    }

    public float getMaxU() {
        return this.uMax;
    }

    public float getFrameU(double frame) {
        float f = this.uMax - this.uMin;
        return this.uMin + f * (float)frame / 16.0f;
    }

    public float getXFromU(float f) {
        float g = this.uMax - this.uMin;
        return (f - this.uMin) / g * 16.0f;
    }

    public float getMinV() {
        return this.vMin;
    }

    public float getMaxV() {
        return this.vMax;
    }

    public float getFrameV(double frame) {
        float f = this.vMax - this.vMin;
        return this.vMin + f * (float)frame / 16.0f;
    }

    public float getYFromV(float f) {
        float g = this.vMax - this.vMin;
        return (f - this.vMin) / g * 16.0f;
    }

    public Identifier getId() {
        return this.id;
    }

    public void tickAnimation() {
        ++this.frameTicks;
        if (this.frameTicks >= this.animationMetadata.getFrameTime(this.frameIndex)) {
            int i = this.animationMetadata.getFrameIndex(this.frameIndex);
            int j = this.animationMetadata.getFrameCount() == 0 ? this.getFrameCount() : this.animationMetadata.getFrameCount();
            this.frameIndex = (this.frameIndex + 1) % j;
            this.frameTicks = 0;
            int k = this.animationMetadata.getFrameIndex(this.frameIndex);
            if (i != k && k >= 0 && k < this.getFrameCount()) {
                this.upload(k);
            }
        } else if (this.animationMetadata.shouldInterpolate()) {
            this.interpolateFrames();
        }
    }

    private void interpolateFrames() {
        int j;
        int k;
        double d = 1.0 - (double)this.frameTicks / (double)this.animationMetadata.getFrameTime(this.frameIndex);
        int i = this.animationMetadata.getFrameIndex(this.frameIndex);
        if (i != (k = this.animationMetadata.getFrameIndex((this.frameIndex + 1) % (j = this.animationMetadata.getFrameCount() == 0 ? this.getFrameCount() : this.animationMetadata.getFrameCount()))) && k >= 0 && k < this.getFrameCount()) {
            if (this.interpolatedImages == null || this.interpolatedImages.length != this.images.length) {
                if (this.interpolatedImages != null) {
                    for (NativeImage nativeImage : this.interpolatedImages) {
                        if (nativeImage == null) continue;
                        nativeImage.close();
                    }
                }
                this.interpolatedImages = new NativeImage[this.images.length];
            }
            for (int l = 0; l < this.images.length; ++l) {
                int m = this.width >> l;
                int n = this.height >> l;
                if (this.interpolatedImages[l] == null) {
                    this.interpolatedImages[l] = new NativeImage(m, n, false);
                }
                for (int o = 0; o < n; ++o) {
                    for (int p = 0; p < m; ++p) {
                        int q = this.getFramePixel(i, l, p, o);
                        int r = this.getFramePixel(k, l, p, o);
                        int s = this.lerp(d, q >> 16 & 0xFF, r >> 16 & 0xFF);
                        int t = this.lerp(d, q >> 8 & 0xFF, r >> 8 & 0xFF);
                        int u = this.lerp(d, q & 0xFF, r & 0xFF);
                        this.interpolatedImages[l].setPixelRgba(p, o, q & 0xFF000000 | s << 16 | t << 8 | u);
                    }
                }
            }
            this.upload(0, 0, this.interpolatedImages);
        }
    }

    private int lerp(double multiplier, int first, int second) {
        return (int)(multiplier * (double)first + (1.0 - multiplier) * (double)second);
    }

    public int getFrameCount() {
        return this.frameXs == null ? 0 : this.frameXs.length;
    }

    public void load(Resource resource, int i) throws IOException {
        NativeImage nativeImage = NativeImage.read(resource.getInputStream());
        this.images = new NativeImage[i];
        this.images[0] = nativeImage;
        int j = this.animationMetadata != null && this.animationMetadata.getWidth() != -1 ? nativeImage.getWidth() / this.animationMetadata.getWidth() : nativeImage.getWidth() / this.width;
        int k = this.animationMetadata != null && this.animationMetadata.getHeight() != -1 ? nativeImage.getHeight() / this.animationMetadata.getHeight() : nativeImage.getHeight() / this.height;
        if (this.animationMetadata != null && this.animationMetadata.getFrameCount() > 0) {
            int l = (Integer)this.animationMetadata.getFrameIndexSet().stream().max(Integer::compareTo).get() + 1;
            this.frameXs = new int[l];
            this.frameYs = new int[l];
            Arrays.fill(this.frameXs, -1);
            Arrays.fill(this.frameYs, -1);
            for (int m : this.animationMetadata.getFrameIndexSet()) {
                int o;
                if (m >= j * k) {
                    throw new RuntimeException("invalid frameindex " + m);
                }
                int n = m / j;
                this.frameXs[m] = o = m % j;
                this.frameYs[m] = n;
            }
        } else {
            int m;
            ArrayList list = Lists.newArrayList();
            int p = j * k;
            this.frameXs = new int[p];
            this.frameYs = new int[p];
            for (m = 0; m < k; ++m) {
                int n = 0;
                while (n < j) {
                    int o = m * j + n;
                    this.frameXs[o] = n++;
                    this.frameYs[o] = m;
                    list.add(new AnimationFrameResourceMetadata(o, -1));
                }
            }
            m = 1;
            boolean bl = false;
            if (this.animationMetadata != null) {
                m = this.animationMetadata.getDefaultFrameTime();
                bl = this.animationMetadata.shouldInterpolate();
            }
            this.animationMetadata = new AnimationResourceMetadata(list, this.width, this.height, m, bl);
        }
    }

    public void generateMipmaps(int i) {
        try {
            this.generateMipmapsInternal(i);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Generating mipmaps for frame");
            CrashReportSection crashReportSection = crashReport.addElement("Frame being iterated");
            crashReportSection.add("Frame sizes", () -> {
                StringBuilder stringBuilder = new StringBuilder();
                for (NativeImage nativeImage : this.images) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(nativeImage == null ? "null" : nativeImage.getWidth() + "x" + nativeImage.getHeight());
                }
                return stringBuilder.toString();
            });
            throw new CrashException(crashReport);
        }
    }

    public void destroy() {
        if (this.images != null) {
            for (NativeImage nativeImage : this.images) {
                if (nativeImage == null) continue;
                nativeImage.close();
            }
        }
        this.images = null;
        if (this.interpolatedImages != null) {
            for (NativeImage nativeImage : this.interpolatedImages) {
                if (nativeImage == null) continue;
                nativeImage.close();
            }
        }
        this.interpolatedImages = null;
    }

    public boolean isAnimated() {
        return this.animationMetadata != null && this.animationMetadata.getFrameCount() > 1;
    }

    public String toString() {
        int i = this.frameXs == null ? 0 : this.frameXs.length;
        return "TextureAtlasSprite{name='" + this.id + '\'' + ", frameCount=" + i + ", x=" + this.x + ", y=" + this.y + ", height=" + this.height + ", width=" + this.width + ", u0=" + this.uMin + ", u1=" + this.uMax + ", v0=" + this.vMin + ", v1=" + this.vMax + '}';
    }

    private int getFramePixel(int frame, int image, int x, int y) {
        return this.images[image].getPixelRgba(x + (this.frameXs[frame] * this.width >> image), y + (this.frameYs[frame] * this.height >> image));
    }

    public boolean isPixelTransparent(int frame, int x, int y) {
        return (this.images[0].getPixelRgba(x + this.frameXs[frame] * this.width, y + this.frameYs[frame] * this.height) >> 24 & 0xFF) == 0;
    }

    public void upload() {
        this.upload(0);
    }
}

