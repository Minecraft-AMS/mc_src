/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkLoadingDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private double lastUpdateTime = Double.MIN_VALUE;
    private final int field_4511 = 12;
    @Nullable
    private ServerData serverData;

    public ChunkLoadingDebugRenderer(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    @Override
    public void render(long l) {
        double d = Util.getMeasuringTimeNano();
        if (d - this.lastUpdateTime > 3.0E9) {
            this.lastUpdateTime = d;
            IntegratedServer integratedServer = this.client.getServer();
            this.serverData = integratedServer != null ? new ServerData(integratedServer) : null;
        }
        if (this.serverData != null) {
            GlStateManager.disableFog();
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.lineWidth(2.0f);
            GlStateManager.disableTexture();
            GlStateManager.depthMask(false);
            Map map = this.serverData.field_4514.getNow(null);
            double e = this.client.gameRenderer.getCamera().getPos().y * 0.85;
            for (Map.Entry entry : this.serverData.field_4515.entrySet()) {
                ChunkPos chunkPos = (ChunkPos)entry.getKey();
                String string = (String)entry.getValue();
                if (map != null) {
                    string = string + (String)map.get(chunkPos);
                }
                String[] strings = string.split("\n");
                int i = 0;
                for (String string2 : strings) {
                    DebugRenderer.method_19429(string2, (chunkPos.x << 4) + 8, e + (double)i, (chunkPos.z << 4) + 8, -1, 0.15f);
                    i -= 2;
                }
            }
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture();
            GlStateManager.disableBlend();
            GlStateManager.enableFog();
        }
    }

    @Environment(value=EnvType.CLIENT)
    final class ServerData {
        private final Map<ChunkPos, String> field_4515;
        private final CompletableFuture<Map<ChunkPos, String>> field_4514;

        private ServerData(IntegratedServer integratedServer) {
            ClientWorld clientWorld = ((ChunkLoadingDebugRenderer)ChunkLoadingDebugRenderer.this).client.world;
            DimensionType dimensionType = ((ChunkLoadingDebugRenderer)ChunkLoadingDebugRenderer.this).client.world.dimension.getType();
            ServerWorld serverWorld = integratedServer.getWorld(dimensionType) != null ? integratedServer.getWorld(dimensionType) : null;
            Camera camera = ((ChunkLoadingDebugRenderer)ChunkLoadingDebugRenderer.this).client.gameRenderer.getCamera();
            int i = (int)camera.getPos().x >> 4;
            int j = (int)camera.getPos().z >> 4;
            ImmutableMap.Builder builder = ImmutableMap.builder();
            ClientChunkManager clientChunkManager = clientWorld.getChunkManager();
            for (int k = i - 12; k <= i + 12; ++k) {
                for (int l = j - 12; l <= j + 12; ++l) {
                    ChunkPos chunkPos = new ChunkPos(k, l);
                    String string = "";
                    WorldChunk worldChunk = clientChunkManager.getWorldChunk(k, l, false);
                    string = string + "Client: ";
                    if (worldChunk == null) {
                        string = string + "0n/a\n";
                    } else {
                        string = string + (worldChunk.isEmpty() ? " E" : "");
                        string = string + "\n";
                    }
                    builder.put((Object)chunkPos, (Object)string);
                }
            }
            this.field_4515 = builder.build();
            this.field_4514 = integratedServer.submit(() -> {
                ImmutableMap.Builder builder = ImmutableMap.builder();
                ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
                for (int k = i - 12; k <= i + 12; ++k) {
                    for (int l = j - 12; l <= j + 12; ++l) {
                        ChunkPos chunkPos = new ChunkPos(k, l);
                        builder.put((Object)chunkPos, (Object)("Server: " + serverChunkManager.getDebugString(chunkPos)));
                    }
                }
                return builder.build();
            });
        }
    }
}

