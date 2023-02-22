/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.SpriteTexturedVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.MipmapHelper;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Sprite
implements AutoCloseable {
    private final SpriteAtlasTexture atlas;
    private final Info info;
    private final AnimationResourceMetadata animationMetadata;
    protected final NativeImage[] images;
    private final int[] frameXs;
    private final int[] frameYs;
    @Nullable
    private final Interpolation interpolation;
    private final int x;
    private final int y;
    private final float uMin;
    private final float uMax;
    private final float vMin;
    private final float vMax;
    private int frameIndex;
    private int frameTicks;

    protected Sprite(SpriteAtlasTexture atlas, Info info, int maxLevel, int atlasWidth, int atlasHeight, int x, int y, NativeImage nativeImage) {
        CrashReport crashReport;
        this.atlas = atlas;
        AnimationResourceMetadata animationResourceMetadata = info.animationData;
        int i = info.width;
        int j = info.height;
        this.x = x;
        this.y = y;
        this.uMin = (float)x / (float)atlasWidth;
        this.uMax = (float)(x + i) / (float)atlasWidth;
        this.vMin = (float)y / (float)atlasHeight;
        this.vMax = (float)(y + j) / (float)atlasHeight;
        int k = nativeImage.getWidth() / animationResourceMetadata.getWidth(i);
        int l = nativeImage.getHeight() / animationResourceMetadata.getHeight(j);
        if (animationResourceMetadata.getFrameCount() > 0) {
            int m = (Integer)animationResourceMetadata.getFrameIndexSet().stream().max(Integer::compareTo).get() + 1;
            this.frameXs = new int[m];
            this.frameYs = new int[m];
            Arrays.fill(this.frameXs, -1);
            Arrays.fill(this.frameYs, -1);
            for (int n : animationResourceMetadata.getFrameIndexSet()) {
                int p;
                if (n >= k * l) {
                    throw new RuntimeException("invalid frameindex " + n);
                }
                int o = n / k;
                this.frameXs[n] = p = n % k;
                this.frameYs[n] = o;
            }
        } else {
            ArrayList list = Lists.newArrayList();
            int q = k * l;
            this.frameXs = new int[q];
            this.frameYs = new int[q];
            for (int n = 0; n < l; ++n) {
                int o = 0;
                while (o < k) {
                    int p = n * k + o;
                    this.frameXs[p] = o++;
                    this.frameYs[p] = n;
                    list.add(new AnimationFrameResourceMetadata(p, -1));
                }
            }
            animationResourceMetadata = new AnimationResourceMetadata(list, i, j, animationResourceMetadata.getDefaultFrameTime(), animationResourceMetadata.shouldInterpolate());
        }
        this.info = new Info(info.id, i, j, animationResourceMetadata);
        this.animationMetadata = animationResourceMetadata;
        try {
            try {
                this.images = MipmapHelper.getMipmapLevelsImages(nativeImage, maxLevel);
            }
            catch (Throwable throwable) {
                crashReport = CrashReport.create(throwable, "Generating mipmaps for frame");
                CrashReportSection crashReportSection = crashReport.addElement("Frame being iterated");
                crashReportSection.add("First frame", () -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(nativeImage.getWidth()).append("x").append(nativeImage.getHeight());
                    return stringBuilder.toString();
                });
                throw new CrashException(crashReport);
            }
        }
        catch (Throwable throwable) {
            crashReport = CrashReport.create(throwable, "Applying mipmap");
            CrashReportSection crashReportSection = crashReport.addElement("Sprite being mipmapped");
            crashReportSection.add("Sprite name", () -> this.getId().toString());
            crashReportSection.add("Sprite size", () -> this.getWidth() + " x " + this.getHeight());
            crashReportSection.add("Sprite frames", () -> this.getFrameCount() + " frames");
            crashReportSection.add("Mipmap levels", maxLevel);
            throw new CrashException(crashReport);
        }
        this.interpolation = animationResourceMetadata.shouldInterpolate() ? new Interpolation(info, maxLevel) : null;
    }

    private void upload(int frame) {
        int i = this.frameXs[frame] * this.info.width;
        int j = this.frameYs[frame] * this.info.height;
        this.upload(i, j, this.images);
    }

    private void upload(int frameX, int frameY, NativeImage[] output) {
        for (int i = 0; i < this.images.length; ++i) {
            output[i].upload(i, this.x >> i, this.y >> i, frameX >> i, frameY >> i, this.info.width >> i, this.info.height >> i, this.images.length > 1, false);
        }
    }

    public int getWidth() {
        return this.info.width;
    }

    public int getHeight() {
        return this.info.height;
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

    public Identifier getId() {
        return this.info.id;
    }

    public SpriteAtlasTexture getAtlas() {
        return this.atlas;
    }

    public int getFrameCount() {
        return this.frameXs.length;
    }

    @Override
    public void close() {
        for (NativeImage nativeImage : this.images) {
            if (nativeImage == null) continue;
            nativeImage.close();
        }
        if (this.interpolation != null) {
            this.interpolation.close();
        }
    }

    public String toString() {
        int i = this.frameXs.length;
        return "TextureAtlasSprite{name='" + this.info.id + '\'' + ", frameCount=" + i + ", x=" + this.x + ", y=" + this.y + ", height=" + this.info.height + ", width=" + this.info.width + ", u0=" + this.uMin + ", u1=" + this.uMax + ", v0=" + this.vMin + ", v1=" + this.vMax + '}';
    }

    public boolean isPixelTransparent(int frame, int x, int y) {
        return (this.images[0].getPixelColor(x + this.frameXs[frame] * this.info.width, y + this.frameYs[frame] * this.info.height) >> 24 & 0xFF) == 0;
    }

    public void upload() {
        this.upload(0);
    }

    private float getFrameDeltaFactor() {
        float f = (float)this.info.width / (this.uMax - this.uMin);
        float g = (float)this.info.height / (this.vMax - this.vMin);
        return Math.max(g, f);
    }

    public float getAnimationFrameDelta() {
        return 4.0f / this.getFrameDeltaFactor();
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
        } else if (this.interpolation != null) {
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(() -> this.interpolation.apply());
            } else {
                this.interpolation.apply();
            }
        }
    }

    public boolean isAnimated() {
        return this.animationMetadata.getFrameCount() > 1;
    }

    public VertexConsumer getTextureSpecificVertexConsumer(VertexConsumer vertexConsumer) {
        return new SpriteTexturedVertexConsumer(vertexConsumer, this);
    }

    @Environment(value=EnvType.CLIENT)
    final class Interpolation
    implements AutoCloseable {
        private final NativeImage[] images;

        private Interpolation(Info info, int mipmap) {
            this.images = new NativeImage[mipmap + 1];
            for (int i = 0; i < this.images.length; ++i) {
                int j = info.width >> i;
                int k = info.height >> i;
                if (this.images[i] != null) continue;
                this.images[i] = new NativeImage(j, k, false);
            }
        }

        private void apply() {
            double d = 1.0 - (double)Sprite.this.frameTicks / (double)Sprite.this.animationMetadata.getFrameTime(Sprite.this.frameIndex);
            int i = Sprite.this.animationMetadata.getFrameIndex(Sprite.this.frameIndex);
            int j = Sprite.this.animationMetadata.getFrameCount() == 0 ? Sprite.this.getFrameCount() : Sprite.this.animationMetadata.getFrameCount();
            int k = Sprite.this.animationMetadata.getFrameIndex((Sprite.this.frameIndex + 1) % j);
            if (i != k && k >= 0 && k < Sprite.this.getFrameCount()) {
                for (int l = 0; l < this.images.length; ++l) {
                    int m = Sprite.this.info.width >> l;
                    int n = Sprite.this.info.height >> l;
                    for (int o = 0; o < n; ++o) {
                        for (int p = 0; p < m; ++p) {
                            int q = this.getPixelColor(i, l, p, o);
                            int r = this.getPixelColor(k, l, p, o);
                            int s = this.lerp(d, q >> 16 & 0xFF, r >> 16 & 0xFF);
                            int t = this.lerp(d, q >> 8 & 0xFF, r >> 8 & 0xFF);
                            int u = this.lerp(d, q & 0xFF, r & 0xFF);
                            this.images[l].setPixelColor(p, o, q & 0xFF000000 | s << 16 | t << 8 | u);
                        }
                    }
                }
                Sprite.this.upload(0, 0, this.images);
            }
        }

        private int getPixelColor(int frameIndex, int layer, int x, int y) {
            return Sprite.this.images[layer].getPixelColor(x + (Sprite.this.frameXs[frameIndex] * Sprite.this.info.width >> layer), y + (Sprite.this.frameYs[frameIndex] * Sprite.this.info.height >> layer));
        }

        private int lerp(double delta, int to, int from) {
            return (int)(delta * (double)to + (1.0 - delta) * (double)from);
        }

        @Override
        public void close() {
            for (NativeImage nativeImage : this.images) {
                if (nativeImage == null) continue;
                nativeImage.close();
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Info {
        private final Identifier id;
        private final int width;
        private final int height;
        private final AnimationResourceMetadata animationData;

        public Info(Identifier id, int width, int height, AnimationResourceMetadata animationData) {
            this.id = id;
            this.width = width;
            this.height = height;
            this.animationData = animationData;
        }

        public Identifier getId() {
            return this.id;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }
    }
}

