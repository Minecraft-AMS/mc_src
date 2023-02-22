/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.metadata;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadataReader;

@Environment(value=EnvType.CLIENT)
public class AnimationResourceMetadata {
    public static final AnimationResourceMetadataReader READER = new AnimationResourceMetadataReader();
    public static final String KEY = "animation";
    public static final int EMPTY_FRAME_TIME = 1;
    public static final int UNDEFINED = -1;
    public static final AnimationResourceMetadata EMPTY = new AnimationResourceMetadata((List)Lists.newArrayList(), -1, -1, 1, false){

        @Override
        public Pair<Integer, Integer> ensureImageSize(int x, int y) {
            return Pair.of((Object)x, (Object)y);
        }
    };
    private final List<AnimationFrameResourceMetadata> frames;
    private final int width;
    private final int height;
    private final int defaultFrameTime;
    private final boolean interpolate;

    public AnimationResourceMetadata(List<AnimationFrameResourceMetadata> frames, int width, int height, int defaultFrameTime, boolean interpolate) {
        this.frames = frames;
        this.width = width;
        this.height = height;
        this.defaultFrameTime = defaultFrameTime;
        this.interpolate = interpolate;
    }

    private static boolean isMultipleOf(int dividend, int divisor) {
        return dividend / divisor * divisor == dividend;
    }

    public Pair<Integer, Integer> ensureImageSize(int x, int y) {
        Pair<Integer, Integer> pair = this.getSize(x, y);
        int i = (Integer)pair.getFirst();
        int j = (Integer)pair.getSecond();
        if (!AnimationResourceMetadata.isMultipleOf(x, i) || !AnimationResourceMetadata.isMultipleOf(y, j)) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Image size %s,%s is not multiply of frame size %s,%s", x, y, i, j));
        }
        return pair;
    }

    private Pair<Integer, Integer> getSize(int defaultWidth, int defaultHeight) {
        if (this.width != -1) {
            if (this.height != -1) {
                return Pair.of((Object)this.width, (Object)this.height);
            }
            return Pair.of((Object)this.width, (Object)defaultHeight);
        }
        if (this.height != -1) {
            return Pair.of((Object)defaultWidth, (Object)this.height);
        }
        int i = Math.min(defaultWidth, defaultHeight);
        return Pair.of((Object)i, (Object)i);
    }

    public int getHeight(int defaultHeight) {
        return this.height == -1 ? defaultHeight : this.height;
    }

    public int getWidth(int defaultWidth) {
        return this.width == -1 ? defaultWidth : this.width;
    }

    public int getDefaultFrameTime() {
        return this.defaultFrameTime;
    }

    public boolean shouldInterpolate() {
        return this.interpolate;
    }

    public void forEachFrame(FrameConsumer consumer) {
        for (AnimationFrameResourceMetadata animationFrameResourceMetadata : this.frames) {
            consumer.accept(animationFrameResourceMetadata.getIndex(), animationFrameResourceMetadata.getTime(this.defaultFrameTime));
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface FrameConsumer {
        public void accept(int var1, int var2);
    }
}

