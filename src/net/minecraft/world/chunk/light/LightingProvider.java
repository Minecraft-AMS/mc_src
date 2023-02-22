/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.chunk.light;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.ChunkBlockLightProvider;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.ChunkLightingView;
import net.minecraft.world.chunk.light.ChunkSkyLightProvider;
import net.minecraft.world.chunk.light.LightingView;
import org.jetbrains.annotations.Nullable;

public class LightingProvider
implements LightingView {
    @Nullable
    private final ChunkLightProvider<?, ?> blockLightProvider;
    @Nullable
    private final ChunkLightProvider<?, ?> skyLightProvider;

    public LightingProvider(ChunkProvider chunkProvider, boolean hasBlockLight, boolean hasSkyLight) {
        this.blockLightProvider = hasBlockLight ? new ChunkBlockLightProvider(chunkProvider) : null;
        this.skyLightProvider = hasSkyLight ? new ChunkSkyLightProvider(chunkProvider) : null;
    }

    public void checkBlock(BlockPos pos) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.checkBlock(pos);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.checkBlock(pos);
        }
    }

    public void addLightSource(BlockPos pos, int level) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.method_15514(pos, level);
        }
    }

    public boolean hasUpdates() {
        if (this.skyLightProvider != null && this.skyLightProvider.hasUpdates()) {
            return true;
        }
        return this.blockLightProvider != null && this.blockLightProvider.hasUpdates();
    }

    public int doLightUpdates(int maxUpdateCount, boolean doSkylight, boolean skipEdgeLightPropagation) {
        if (this.blockLightProvider != null && this.skyLightProvider != null) {
            int i = maxUpdateCount / 2;
            int j = this.blockLightProvider.doLightUpdates(i, doSkylight, skipEdgeLightPropagation);
            int k = maxUpdateCount - i + j;
            int l = this.skyLightProvider.doLightUpdates(k, doSkylight, skipEdgeLightPropagation);
            if (j == 0 && l > 0) {
                return this.blockLightProvider.doLightUpdates(l, doSkylight, skipEdgeLightPropagation);
            }
            return l;
        }
        if (this.blockLightProvider != null) {
            return this.blockLightProvider.doLightUpdates(maxUpdateCount, doSkylight, skipEdgeLightPropagation);
        }
        if (this.skyLightProvider != null) {
            return this.skyLightProvider.doLightUpdates(maxUpdateCount, doSkylight, skipEdgeLightPropagation);
        }
        return maxUpdateCount;
    }

    @Override
    public void updateSectionStatus(ChunkSectionPos pos, boolean status) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.updateSectionStatus(pos, status);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.updateSectionStatus(pos, status);
        }
    }

    public void setLightEnabled(ChunkPos pos, boolean lightEnabled) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.method_15512(pos, lightEnabled);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.method_15512(pos, lightEnabled);
        }
    }

    public ChunkLightingView get(LightType lightType) {
        if (lightType == LightType.BLOCK) {
            if (this.blockLightProvider == null) {
                return ChunkLightingView.Empty.INSTANCE;
            }
            return this.blockLightProvider;
        }
        if (this.skyLightProvider == null) {
            return ChunkLightingView.Empty.INSTANCE;
        }
        return this.skyLightProvider;
    }

    @Environment(value=EnvType.CLIENT)
    public String method_15564(LightType lightType, ChunkSectionPos chunkSectionPos) {
        if (lightType == LightType.BLOCK) {
            if (this.blockLightProvider != null) {
                return this.blockLightProvider.method_15520(chunkSectionPos.asLong());
            }
        } else if (this.skyLightProvider != null) {
            return this.skyLightProvider.method_15520(chunkSectionPos.asLong());
        }
        return "n/a";
    }

    public void queueData(LightType lightType, ChunkSectionPos chunkSectionPos, @Nullable ChunkNibbleArray chunkNibbleArray) {
        if (lightType == LightType.BLOCK) {
            if (this.blockLightProvider != null) {
                this.blockLightProvider.setLightArray(chunkSectionPos.asLong(), chunkNibbleArray);
            }
        } else if (this.skyLightProvider != null) {
            this.skyLightProvider.setLightArray(chunkSectionPos.asLong(), chunkNibbleArray);
        }
    }

    public void method_20601(ChunkPos chunkPos, boolean bl) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.method_20599(chunkPos, bl);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.method_20599(chunkPos, bl);
        }
    }
}

