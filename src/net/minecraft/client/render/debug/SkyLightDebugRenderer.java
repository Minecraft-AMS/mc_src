/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class SkyLightDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;

    public SkyLightDebugRenderer(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    @Override
    public void render(long l) {
        Camera camera = this.client.gameRenderer.getCamera();
        ClientWorld world = this.client.world;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        BlockPos blockPos = new BlockPos(camera.getPos());
        LongOpenHashSet longSet = new LongOpenHashSet();
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
            int i = world.getLightLevel(LightType.SKY, blockPos2);
            float f = (float)(15 - i) / 15.0f * 0.5f + 0.16f;
            int j = MathHelper.hsvToRgb(f, 0.9f, 0.9f);
            long m = ChunkSectionPos.fromGlobalPos(blockPos2.asLong());
            if (longSet.add(m)) {
                DebugRenderer.method_19429(((World)world).getChunkManager().getLightingProvider().method_15564(LightType.SKY, ChunkSectionPos.from(m)), ChunkSectionPos.getX(m) * 16 + 8, ChunkSectionPos.getY(m) * 16 + 8, ChunkSectionPos.getZ(m) * 16 + 8, 0xFF0000, 0.3f);
            }
            if (i == 15) continue;
            DebugRenderer.method_3714(String.valueOf(i), (double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.25, (double)blockPos2.getZ() + 0.5, j);
        }
        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }
}

