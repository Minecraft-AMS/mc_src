/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.metadata;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadataReader;

@Environment(value=EnvType.CLIENT)
public class AnimationResourceMetadata {
    public static final AnimationResourceMetadataReader READER = new AnimationResourceMetadataReader();
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

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getFrameCount() {
        return this.frames.size();
    }

    public int getDefaultFrameTime() {
        return this.defaultFrameTime;
    }

    public boolean shouldInterpolate() {
        return this.interpolate;
    }

    private AnimationFrameResourceMetadata getFrame(int frameIndex) {
        return this.frames.get(frameIndex);
    }

    public int getFrameTime(int frameIndex) {
        AnimationFrameResourceMetadata animationFrameResourceMetadata = this.getFrame(frameIndex);
        if (animationFrameResourceMetadata.usesDefaultFrameTime()) {
            return this.defaultFrameTime;
        }
        return animationFrameResourceMetadata.getTime();
    }

    public int getFrameIndex(int frameIndex) {
        return this.frames.get(frameIndex).getIndex();
    }

    public Set<Integer> getFrameIndexSet() {
        HashSet set = Sets.newHashSet();
        for (AnimationFrameResourceMetadata animationFrameResourceMetadata : this.frames) {
            set.add(animationFrameResourceMetadata.getIndex());
        }
        return set;
    }
}

