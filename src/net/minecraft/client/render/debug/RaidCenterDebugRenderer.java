/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class RaidCenterDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private Collection<BlockPos> raidCenters = Lists.newArrayList();

    public RaidCenterDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void setRaidCenters(Collection<BlockPos> centers) {
        this.raidCenters = centers;
    }

    @Override
    public void render(long l) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        this.drawRaidCenters();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void drawRaidCenters() {
        BlockPos blockPos = this.getCamera().getBlockPos();
        for (BlockPos blockPos2 : this.raidCenters) {
            if (!blockPos.isWithinDistance(blockPos2, 160.0)) continue;
            RaidCenterDebugRenderer.draw(blockPos2);
        }
    }

    private static void draw(BlockPos blockPos) {
        DebugRenderer.method_19697(blockPos.add(-0.5, -0.5, -0.5), blockPos.add(1.5, 1.5, 1.5), 1.0f, 0.0f, 0.0f, 0.15f);
        int i = -65536;
        RaidCenterDebugRenderer.showText("Raid center", blockPos, -65536);
    }

    private static void showText(String string, BlockPos blockPos, int i) {
        double d = (double)blockPos.getX() + 0.5;
        double e = (double)blockPos.getY() + 1.3;
        double f = (double)blockPos.getZ() + 0.5;
        DebugRenderer.method_3712(string, d, e, f, i, 0.04f, true, 0.0f, true);
    }

    private Camera getCamera() {
        return this.client.gameRenderer.getCamera();
    }
}
