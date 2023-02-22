/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.DataFixUtils
 *  it.unimi.dsi.fastutil.longs.LongSets
 *  it.unimi.dsi.fastutil.longs.LongSets$EmptySet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.hud;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.DataFixUtils;
import it.unimi.dsi.fastutil.longs.LongSets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetricsData;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DebugHud
extends DrawableHelper {
    private static final Map<Heightmap.Type, String> HEIGHT_MAP_TYPES = Util.make(new EnumMap(Heightmap.Type.class), enumMap -> {
        enumMap.put(Heightmap.Type.WORLD_SURFACE_WG, "SW");
        enumMap.put(Heightmap.Type.WORLD_SURFACE, "S");
        enumMap.put(Heightmap.Type.OCEAN_FLOOR_WG, "OW");
        enumMap.put(Heightmap.Type.OCEAN_FLOOR, "O");
        enumMap.put(Heightmap.Type.MOTION_BLOCKING, "M");
        enumMap.put(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, "ML");
    });
    private final MinecraftClient client;
    private final TextRenderer fontRenderer;
    private HitResult blockHit;
    private HitResult fluidHit;
    @Nullable
    private ChunkPos pos;
    @Nullable
    private WorldChunk chunk;
    @Nullable
    private CompletableFuture<WorldChunk> chunkFuture;

    public DebugHud(MinecraftClient client) {
        this.client = client;
        this.fontRenderer = client.textRenderer;
    }

    public void resetChunk() {
        this.chunkFuture = null;
        this.chunk = null;
    }

    public void render() {
        this.client.getProfiler().push("debug");
        GlStateManager.pushMatrix();
        Entity entity = this.client.getCameraEntity();
        this.blockHit = entity.rayTrace(20.0, 0.0f, false);
        this.fluidHit = entity.rayTrace(20.0, 0.0f, true);
        this.renderLeftText();
        this.renderRightText();
        GlStateManager.popMatrix();
        if (this.client.options.debugTpsEnabled) {
            int i = this.client.window.getScaledWidth();
            this.drawMetricsData(this.client.getMetricsData(), 0, i / 2, true);
            IntegratedServer integratedServer = this.client.getServer();
            if (integratedServer != null) {
                this.drawMetricsData(integratedServer.getMetricsData(), i - Math.min(i / 2, 240), i / 2, false);
            }
        }
        this.client.getProfiler().pop();
    }

    protected void renderLeftText() {
        List<String> list = this.getLeftText();
        list.add("");
        boolean bl = this.client.getServer() != null;
        list.add("Debug: Pie [shift]: " + (this.client.options.debugProfilerEnabled ? "visible" : "hidden") + (bl ? " FPS + TPS" : " FPS") + " [alt]: " + (this.client.options.debugTpsEnabled ? "visible" : "hidden"));
        list.add("For help: press F3 + Q");
        for (int i = 0; i < list.size(); ++i) {
            String string = list.get(i);
            if (Strings.isNullOrEmpty((String)string)) continue;
            int j = this.fontRenderer.fontHeight;
            int k = this.fontRenderer.getStringWidth(string);
            int l = 2;
            int m = 2 + j * i;
            DebugHud.fill(1, m - 1, 2 + k + 1, m + j - 1, -1873784752);
            this.fontRenderer.draw(string, 2.0f, m, 0xE0E0E0);
        }
    }

    protected void renderRightText() {
        List<String> list = this.getRightText();
        for (int i = 0; i < list.size(); ++i) {
            String string = list.get(i);
            if (Strings.isNullOrEmpty((String)string)) continue;
            int j = this.fontRenderer.fontHeight;
            int k = this.fontRenderer.getStringWidth(string);
            int l = this.client.window.getScaledWidth() - 2 - k;
            int m = 2 + j * i;
            DebugHud.fill(l - 1, m - 1, l + k + 1, m + j - 1, -1873784752);
            this.fontRenderer.draw(string, l, m, 0xE0E0E0);
        }
    }

    protected List<String> getLeftText() {
        BlockPos blockPos2;
        World world;
        String string2;
        IntegratedServer integratedServer = this.client.getServer();
        ClientConnection clientConnection = this.client.getNetworkHandler().getConnection();
        float f = clientConnection.getAveragePacketsSent();
        float g = clientConnection.getAveragePacketsReceived();
        String string = integratedServer != null ? String.format("Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", Float.valueOf(integratedServer.getTickTime()), Float.valueOf(f), Float.valueOf(g)) : String.format("\"%s\" server, %.0f tx, %.0f rx", this.client.player.getServerBrand(), Float.valueOf(f), Float.valueOf(g));
        BlockPos blockPos = new BlockPos(this.client.getCameraEntity().x, this.client.getCameraEntity().getBoundingBox().y1, this.client.getCameraEntity().z);
        if (this.client.hasReducedDebugInfo()) {
            return Lists.newArrayList((Object[])new String[]{"Minecraft " + SharedConstants.getGameVersion().getName() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.client.fpsDebugString, string, this.client.worldRenderer.getChunksDebugString(), this.client.worldRenderer.getEntitiesDebugString(), "P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getRegularEntityCount(), this.client.world.getDebugString(), "", String.format("Chunk-relative: %d %d %d", blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF)});
        }
        Entity entity = this.client.getCameraEntity();
        Direction direction = entity.getHorizontalFacing();
        switch (direction) {
            case NORTH: {
                string2 = "Towards negative Z";
                break;
            }
            case SOUTH: {
                string2 = "Towards positive Z";
                break;
            }
            case WEST: {
                string2 = "Towards negative X";
                break;
            }
            case EAST: {
                string2 = "Towards positive X";
                break;
            }
            default: {
                string2 = "Invalid";
            }
        }
        ChunkPos chunkPos = new ChunkPos(blockPos);
        if (!Objects.equals(this.pos, chunkPos)) {
            this.pos = chunkPos;
            this.resetChunk();
        }
        LongSets.EmptySet longSet = (world = this.getWorld()) instanceof ServerWorld ? ((ServerWorld)world).getForcedChunks() : LongSets.EMPTY_SET;
        ArrayList list = Lists.newArrayList((Object[])new String[]{"Minecraft " + SharedConstants.getGameVersion().getName() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType()) + ")", this.client.fpsDebugString, string, this.client.worldRenderer.getChunksDebugString(), this.client.worldRenderer.getEntitiesDebugString(), "P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getRegularEntityCount(), this.client.world.getDebugString()});
        String string3 = this.method_20603();
        if (string3 != null) {
            list.add(string3);
        }
        list.add(DimensionType.getId(this.client.world.dimension.getType()).toString() + " FC: " + Integer.toString(longSet.size()));
        list.add("");
        list.add(String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.client.getCameraEntity().x, this.client.getCameraEntity().getBoundingBox().y1, this.client.getCameraEntity().z));
        list.add(String.format("Block: %d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        list.add(String.format("Chunk: %d %d %d in %d %d %d", blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF, blockPos.getX() >> 4, blockPos.getY() >> 4, blockPos.getZ() >> 4));
        list.add(String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, string2, Float.valueOf(MathHelper.wrapDegrees(entity.yaw)), Float.valueOf(MathHelper.wrapDegrees(entity.pitch))));
        if (this.client.world != null) {
            if (this.client.world.isBlockLoaded(blockPos)) {
                WorldChunk worldChunk = this.getClientChunk();
                if (worldChunk.isEmpty()) {
                    list.add("Waiting for chunk...");
                } else {
                    list.add("Client Light: " + worldChunk.getLightLevel(blockPos, 0) + " (" + this.client.world.getLightLevel(LightType.SKY, blockPos) + " sky, " + this.client.world.getLightLevel(LightType.BLOCK, blockPos) + " block)");
                    WorldChunk worldChunk2 = this.getChunk();
                    if (worldChunk2 != null) {
                        LightingProvider lightingProvider = world.getChunkManager().getLightingProvider();
                        list.add("Server Light: (" + lightingProvider.get(LightType.SKY).getLightLevel(blockPos) + " sky, " + lightingProvider.get(LightType.BLOCK).getLightLevel(blockPos) + " block)");
                    }
                    StringBuilder stringBuilder = new StringBuilder("CH");
                    for (Heightmap.Type type : Heightmap.Type.values()) {
                        if (!type.shouldSendToClient()) continue;
                        stringBuilder.append(" ").append(HEIGHT_MAP_TYPES.get((Object)type)).append(": ").append(worldChunk.sampleHeightmap(type, blockPos.getX(), blockPos.getZ()));
                    }
                    list.add(stringBuilder.toString());
                    if (worldChunk2 != null) {
                        stringBuilder.setLength(0);
                        stringBuilder.append("SH");
                        for (Heightmap.Type type : Heightmap.Type.values()) {
                            if (!type.isStoredServerSide()) continue;
                            stringBuilder.append(" ").append(HEIGHT_MAP_TYPES.get((Object)type)).append(": ").append(worldChunk2.sampleHeightmap(type, blockPos.getX(), blockPos.getZ()));
                        }
                        list.add(stringBuilder.toString());
                    }
                    if (blockPos.getY() >= 0 && blockPos.getY() < 256) {
                        list.add("Biome: " + Registry.BIOME.getId(worldChunk.getBiome(blockPos)));
                        long l = 0L;
                        float h = 0.0f;
                        if (worldChunk2 != null) {
                            h = world.getMoonSize();
                            l = worldChunk2.getInhabitedTime();
                        }
                        LocalDifficulty localDifficulty = new LocalDifficulty(world.getDifficulty(), world.getTimeOfDay(), l, h);
                        list.add(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)", Float.valueOf(localDifficulty.getLocalDifficulty()), Float.valueOf(localDifficulty.getClampedLocalDifficulty()), this.client.world.getTimeOfDay() / 24000L));
                    }
                }
            } else {
                list.add("Outside of world...");
            }
        } else {
            list.add("Outside of world...");
        }
        if (this.client.gameRenderer != null && this.client.gameRenderer.isShaderEnabled()) {
            list.add("Shader: " + this.client.gameRenderer.getShader().getName());
        }
        if (this.blockHit.getType() == HitResult.Type.BLOCK) {
            blockPos2 = ((BlockHitResult)this.blockHit).getBlockPos();
            list.add(String.format("Looking at block: %d %d %d", blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()));
        }
        if (this.fluidHit.getType() == HitResult.Type.BLOCK) {
            blockPos2 = ((BlockHitResult)this.fluidHit).getBlockPos();
            list.add(String.format("Looking at liquid: %d %d %d", blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()));
        }
        list.add(this.client.getSoundManager().getDebugString());
        return list;
    }

    @Nullable
    private String method_20603() {
        ServerWorld serverWorld;
        IntegratedServer integratedServer = this.client.getServer();
        if (integratedServer != null && (serverWorld = integratedServer.getWorld(this.client.world.getDimension().getType())) != null) {
            return serverWorld.getDebugString();
        }
        return null;
    }

    private World getWorld() {
        return (World)DataFixUtils.orElse(Optional.ofNullable(this.client.getServer()).map(integratedServer -> integratedServer.getWorld(this.client.world.dimension.getType())), (Object)this.client.world);
    }

    @Nullable
    private WorldChunk getChunk() {
        if (this.chunkFuture == null) {
            ServerWorld serverWorld;
            IntegratedServer integratedServer = this.client.getServer();
            if (integratedServer != null && (serverWorld = integratedServer.getWorld(this.client.world.dimension.getType())) != null) {
                this.chunkFuture = serverWorld.getChunkManager().getChunkFutureSyncOnMainThread(this.pos.x, this.pos.z, ChunkStatus.FULL, false).thenApply(either -> (WorldChunk)either.map(chunk -> (WorldChunk)chunk, unloaded -> null));
            }
            if (this.chunkFuture == null) {
                this.chunkFuture = CompletableFuture.completedFuture(this.getClientChunk());
            }
        }
        return this.chunkFuture.getNow(null);
    }

    private WorldChunk getClientChunk() {
        if (this.chunk == null) {
            this.chunk = this.client.world.getChunk(this.pos.x, this.pos.z);
        }
        return this.chunk;
    }

    protected List<String> getRightText() {
        Entity entity;
        BlockPos blockPos;
        long l = Runtime.getRuntime().maxMemory();
        long m = Runtime.getRuntime().totalMemory();
        long n = Runtime.getRuntime().freeMemory();
        long o = m - n;
        ArrayList list = Lists.newArrayList((Object[])new String[]{String.format("Java: %s %dbit", System.getProperty("java.version"), this.client.is64Bit() ? 64 : 32), String.format("Mem: % 2d%% %03d/%03dMB", o * 100L / l, DebugHud.method_1838(o), DebugHud.method_1838(l)), String.format("Allocated: % 2d%% %03dMB", m * 100L / l, DebugHud.method_1838(m)), "", String.format("CPU: %s", GLX.getCpuInfo()), "", String.format("Display: %dx%d (%s)", MinecraftClient.getInstance().window.getFramebufferWidth(), MinecraftClient.getInstance().window.getFramebufferHeight(), GLX.getVendor()), GLX.getRenderer(), GLX.getOpenGLVersion()});
        if (this.client.hasReducedDebugInfo()) {
            return list;
        }
        if (this.blockHit.getType() == HitResult.Type.BLOCK) {
            blockPos = ((BlockHitResult)this.blockHit).getBlockPos();
            BlockState blockState = this.client.world.getBlockState(blockPos);
            list.add("");
            list.add((Object)((Object)Formatting.UNDERLINE) + "Targeted Block");
            list.add(String.valueOf(Registry.BLOCK.getId(blockState.getBlock())));
            for (Map.Entry entry : blockState.getEntries().entrySet()) {
                list.add(this.propertyToString(entry));
            }
            for (Identifier identifier : this.client.getNetworkHandler().getTagManager().blocks().getTagsFor(blockState.getBlock())) {
                list.add("#" + identifier);
            }
        }
        if (this.fluidHit.getType() == HitResult.Type.BLOCK) {
            blockPos = ((BlockHitResult)this.fluidHit).getBlockPos();
            FluidState fluidState = this.client.world.getFluidState(blockPos);
            list.add("");
            list.add((Object)((Object)Formatting.UNDERLINE) + "Targeted Fluid");
            list.add(String.valueOf(Registry.FLUID.getId(fluidState.getFluid())));
            for (Map.Entry entry : fluidState.getEntries().entrySet()) {
                list.add(this.propertyToString(entry));
            }
            for (Identifier identifier : this.client.getNetworkHandler().getTagManager().fluids().getTagsFor(fluidState.getFluid())) {
                list.add("#" + identifier);
            }
        }
        if ((entity = this.client.targetedEntity) != null) {
            list.add("");
            list.add((Object)((Object)Formatting.UNDERLINE) + "Targeted Entity");
            list.add(String.valueOf(Registry.ENTITY_TYPE.getId(entity.getType())));
        }
        return list;
    }

    private String propertyToString(Map.Entry<Property<?>, Comparable<?>> propEntry) {
        Property<?> property = propEntry.getKey();
        Comparable<?> comparable = propEntry.getValue();
        String string = Util.getValueAsString(property, comparable);
        if (Boolean.TRUE.equals(comparable)) {
            string = (Object)((Object)Formatting.GREEN) + string;
        } else if (Boolean.FALSE.equals(comparable)) {
            string = (Object)((Object)Formatting.RED) + string;
        }
        return property.getName() + ": " + string;
    }

    private void drawMetricsData(MetricsData metricsData, int startY, int firstSample, boolean isClient) {
        int s;
        int r;
        GlStateManager.disableDepthTest();
        int i = metricsData.getStartIndex();
        int j = metricsData.getCurrentIndex();
        long[] ls = metricsData.getSamples();
        int k = i;
        int l = startY;
        int m = Math.max(0, ls.length - firstSample);
        int n = ls.length - m;
        k = metricsData.wrapIndex(k + m);
        long o = 0L;
        int p = Integer.MAX_VALUE;
        int q = Integer.MIN_VALUE;
        for (r = 0; r < n; ++r) {
            s = (int)(ls[metricsData.wrapIndex(k + r)] / 1000000L);
            p = Math.min(p, s);
            q = Math.max(q, s);
            o += (long)s;
        }
        r = this.client.window.getScaledHeight();
        DebugHud.fill(startY, r - 60, startY + n, r, -1873784752);
        while (k != j) {
            s = metricsData.method_15248(ls[k], isClient ? 30 : 60, isClient ? 60 : 20);
            int t = isClient ? 100 : 60;
            int u = this.method_1833(MathHelper.clamp(s, 0, t), 0, t / 2, t);
            this.vLine(l, r, r - s, u);
            ++l;
            k = metricsData.wrapIndex(k + 1);
        }
        if (isClient) {
            DebugHud.fill(startY + 1, r - 30 + 1, startY + 14, r - 30 + 10, -1873784752);
            this.fontRenderer.draw("60 FPS", startY + 2, r - 30 + 2, 0xE0E0E0);
            this.hLine(startY, startY + n - 1, r - 30, -1);
            DebugHud.fill(startY + 1, r - 60 + 1, startY + 14, r - 60 + 10, -1873784752);
            this.fontRenderer.draw("30 FPS", startY + 2, r - 60 + 2, 0xE0E0E0);
            this.hLine(startY, startY + n - 1, r - 60, -1);
        } else {
            DebugHud.fill(startY + 1, r - 60 + 1, startY + 14, r - 60 + 10, -1873784752);
            this.fontRenderer.draw("20 TPS", startY + 2, r - 60 + 2, 0xE0E0E0);
            this.hLine(startY, startY + n - 1, r - 60, -1);
        }
        this.hLine(startY, startY + n - 1, r - 1, -1);
        this.vLine(startY, r - 60, r, -1);
        this.vLine(startY + n - 1, r - 60, r, -1);
        if (isClient && this.client.options.maxFps > 0 && this.client.options.maxFps <= 250) {
            this.hLine(startY, startY + n - 1, r - 1 - (int)(1800.0 / (double)this.client.options.maxFps), -16711681);
        }
        String string = p + " ms min";
        String string2 = o / (long)n + " ms avg";
        String string3 = q + " ms max";
        this.fontRenderer.drawWithShadow(string, startY + 2, r - 60 - this.fontRenderer.fontHeight, 0xE0E0E0);
        this.fontRenderer.drawWithShadow(string2, startY + n / 2 - this.fontRenderer.getStringWidth(string2) / 2, r - 60 - this.fontRenderer.fontHeight, 0xE0E0E0);
        this.fontRenderer.drawWithShadow(string3, startY + n - this.fontRenderer.getStringWidth(string3), r - 60 - this.fontRenderer.fontHeight, 0xE0E0E0);
        GlStateManager.enableDepthTest();
    }

    private int method_1833(int i, int j, int k, int l) {
        if (i < k) {
            return this.interpolateColor(-16711936, -256, (float)i / (float)k);
        }
        return this.interpolateColor(-256, -65536, (float)(i - k) / (float)(l - k));
    }

    private int interpolateColor(int color1, int color2, float dt) {
        int i = color1 >> 24 & 0xFF;
        int j = color1 >> 16 & 0xFF;
        int k = color1 >> 8 & 0xFF;
        int l = color1 & 0xFF;
        int m = color2 >> 24 & 0xFF;
        int n = color2 >> 16 & 0xFF;
        int o = color2 >> 8 & 0xFF;
        int p = color2 & 0xFF;
        int q = MathHelper.clamp((int)MathHelper.lerp(dt, i, m), 0, 255);
        int r = MathHelper.clamp((int)MathHelper.lerp(dt, j, n), 0, 255);
        int s = MathHelper.clamp((int)MathHelper.lerp(dt, k, o), 0, 255);
        int t = MathHelper.clamp((int)MathHelper.lerp(dt, l, p), 0, 255);
        return q << 24 | r << 16 | s << 8 | t;
    }

    private static long method_1838(long l) {
        return l / 1024L / 1024L;
    }
}

