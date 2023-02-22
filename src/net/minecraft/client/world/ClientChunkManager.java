/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.world;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ClientChunkManager
extends ChunkManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final WorldChunk emptyChunk;
    private final LightingProvider lightingProvider;
    private volatile ClientChunkMap chunks;
    private final ClientWorld world;

    public ClientChunkManager(ClientWorld world, int loadDistance) {
        this.world = world;
        this.emptyChunk = new EmptyChunk((World)world, new ChunkPos(0, 0));
        this.lightingProvider = new LightingProvider(this, true, world.getDimension().hasSkyLight());
        this.chunks = new ClientChunkMap(ClientChunkManager.getChunkMapRadius(loadDistance));
    }

    @Override
    public LightingProvider getLightingProvider() {
        return this.lightingProvider;
    }

    private static boolean positionEquals(@Nullable WorldChunk chunk, int x, int y) {
        if (chunk == null) {
            return false;
        }
        ChunkPos chunkPos = chunk.getPos();
        return chunkPos.x == x && chunkPos.z == y;
    }

    public void unload(int chunkX, int chunkZ) {
        if (!this.chunks.isInRadius(chunkX, chunkZ)) {
            return;
        }
        int i = this.chunks.getIndex(chunkX, chunkZ);
        WorldChunk worldChunk = this.chunks.getChunk(i);
        if (ClientChunkManager.positionEquals(worldChunk, chunkX, chunkZ)) {
            this.chunks.compareAndSet(i, worldChunk, null);
        }
    }

    @Override
    @Nullable
    public WorldChunk getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl) {
        WorldChunk worldChunk;
        if (this.chunks.isInRadius(i, j) && ClientChunkManager.positionEquals(worldChunk = this.chunks.getChunk(this.chunks.getIndex(i, j)), i, j)) {
            return worldChunk;
        }
        if (bl) {
            return this.emptyChunk;
        }
        return null;
    }

    @Override
    public BlockView getWorld() {
        return this.world;
    }

    @Nullable
    public WorldChunk loadChunkFromPacket(int i, int j, @Nullable BiomeArray biomeArray, PacketByteBuf packetByteBuf, CompoundTag compoundTag, int k) {
        if (!this.chunks.isInRadius(i, j)) {
            LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", (Object)i, (Object)j);
            return null;
        }
        int l = this.chunks.getIndex(i, j);
        WorldChunk worldChunk = (WorldChunk)this.chunks.chunks.get(l);
        if (!ClientChunkManager.positionEquals(worldChunk, i, j)) {
            if (biomeArray == null) {
                LOGGER.warn("Ignoring chunk since we don't have complete data: {}, {}", (Object)i, (Object)j);
                return null;
            }
            worldChunk = new WorldChunk(this.world, new ChunkPos(i, j), biomeArray);
            worldChunk.loadFromPacket(biomeArray, packetByteBuf, compoundTag, k);
            this.chunks.set(l, worldChunk);
        } else {
            worldChunk.loadFromPacket(biomeArray, packetByteBuf, compoundTag, k);
        }
        ChunkSection[] chunkSections = worldChunk.getSectionArray();
        LightingProvider lightingProvider = this.getLightingProvider();
        lightingProvider.setLightEnabled(new ChunkPos(i, j), true);
        for (int m = 0; m < chunkSections.length; ++m) {
            ChunkSection chunkSection = chunkSections[m];
            lightingProvider.updateSectionStatus(ChunkSectionPos.from(i, m, j), ChunkSection.isEmpty(chunkSection));
        }
        this.world.resetChunkColor(i, j);
        return worldChunk;
    }

    @Override
    public void tick(BooleanSupplier shouldKeepTicking) {
    }

    public void setChunkMapCenter(int x, int z) {
        this.chunks.centerChunkX = x;
        this.chunks.centerChunkZ = z;
    }

    public void updateLoadDistance(int loadDistance) {
        int j;
        int i = this.chunks.radius;
        if (i != (j = ClientChunkManager.getChunkMapRadius(loadDistance))) {
            ClientChunkMap clientChunkMap = new ClientChunkMap(j);
            clientChunkMap.centerChunkX = this.chunks.centerChunkX;
            clientChunkMap.centerChunkZ = this.chunks.centerChunkZ;
            for (int k = 0; k < this.chunks.chunks.length(); ++k) {
                WorldChunk worldChunk = (WorldChunk)this.chunks.chunks.get(k);
                if (worldChunk == null) continue;
                ChunkPos chunkPos = worldChunk.getPos();
                if (!clientChunkMap.isInRadius(chunkPos.x, chunkPos.z)) continue;
                clientChunkMap.set(clientChunkMap.getIndex(chunkPos.x, chunkPos.z), worldChunk);
            }
            this.chunks = clientChunkMap;
        }
    }

    private static int getChunkMapRadius(int loadDistance) {
        return Math.max(2, loadDistance) + 3;
    }

    @Override
    public String getDebugString() {
        return "Client Chunk Cache: " + this.chunks.chunks.length() + ", " + this.getLoadedChunkCount();
    }

    public int getLoadedChunkCount() {
        return this.chunks.loadedChunkCount;
    }

    @Override
    public void onLightUpdate(LightType type, ChunkSectionPos chunkSectionPos) {
        MinecraftClient.getInstance().worldRenderer.scheduleBlockRender(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionY(), chunkSectionPos.getSectionZ());
    }

    @Override
    public boolean shouldTickBlock(BlockPos pos) {
        return this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4);
    }

    @Override
    public boolean shouldTickChunk(ChunkPos pos) {
        return this.isChunkLoaded(pos.x, pos.z);
    }

    @Override
    public boolean shouldTickEntity(Entity entity) {
        return this.isChunkLoaded(MathHelper.floor(entity.getX()) >> 4, MathHelper.floor(entity.getZ()) >> 4);
    }

    @Override
    @Nullable
    public /* synthetic */ Chunk getChunk(int x, int z, ChunkStatus leastStatus, boolean create) {
        return this.getChunk(x, z, leastStatus, create);
    }

    @Environment(value=EnvType.CLIENT)
    final class ClientChunkMap {
        private final AtomicReferenceArray<WorldChunk> chunks;
        private final int radius;
        private final int diameter;
        private volatile int centerChunkX;
        private volatile int centerChunkZ;
        private int loadedChunkCount;

        private ClientChunkMap(int loadDistance) {
            this.radius = loadDistance;
            this.diameter = loadDistance * 2 + 1;
            this.chunks = new AtomicReferenceArray(this.diameter * this.diameter);
        }

        private int getIndex(int chunkX, int chunkZ) {
            return Math.floorMod(chunkZ, this.diameter) * this.diameter + Math.floorMod(chunkX, this.diameter);
        }

        protected void set(int index, @Nullable WorldChunk chunk) {
            WorldChunk worldChunk = this.chunks.getAndSet(index, chunk);
            if (worldChunk != null) {
                --this.loadedChunkCount;
                ClientChunkManager.this.world.unloadBlockEntities(worldChunk);
            }
            if (chunk != null) {
                ++this.loadedChunkCount;
            }
        }

        protected WorldChunk compareAndSet(int index, WorldChunk expect, @Nullable WorldChunk update) {
            if (this.chunks.compareAndSet(index, expect, update) && update == null) {
                --this.loadedChunkCount;
            }
            ClientChunkManager.this.world.unloadBlockEntities(expect);
            return expect;
        }

        private boolean isInRadius(int chunkX, int chunkZ) {
            return Math.abs(chunkX - this.centerChunkX) <= this.radius && Math.abs(chunkZ - this.centerChunkZ) <= this.radius;
        }

        @Nullable
        protected WorldChunk getChunk(int index) {
            return this.chunks.get(index);
        }
    }
}

