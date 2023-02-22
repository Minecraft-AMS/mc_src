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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
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
    static final Logger LOGGER = LogManager.getLogger();
    private final WorldChunk emptyChunk;
    private final LightingProvider lightingProvider;
    volatile ClientChunkMap chunks;
    final ClientWorld world;

    public ClientChunkManager(ClientWorld world, int loadDistance) {
        this.world = world;
        this.emptyChunk = new EmptyChunk(world, new ChunkPos(0, 0));
        this.lightingProvider = new LightingProvider(this, true, world.getDimension().hasSkyLight());
        this.chunks = new ClientChunkMap(ClientChunkManager.getChunkMapRadius(loadDistance));
    }

    @Override
    public LightingProvider getLightingProvider() {
        return this.lightingProvider;
    }

    private static boolean positionEquals(@Nullable WorldChunk chunk, int x, int z) {
        if (chunk == null) {
            return false;
        }
        ChunkPos chunkPos = chunk.getPos();
        return chunkPos.x == x && chunkPos.z == z;
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
    public WorldChunk loadChunkFromPacket(int x, int z, BiomeArray biomes, PacketByteBuf buf, NbtCompound nbt, BitSet bitSet) {
        if (!this.chunks.isInRadius(x, z)) {
            LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", (Object)x, (Object)z);
            return null;
        }
        int i = this.chunks.getIndex(x, z);
        WorldChunk worldChunk = this.chunks.chunks.get(i);
        ChunkPos chunkPos = new ChunkPos(x, z);
        if (!ClientChunkManager.positionEquals(worldChunk, x, z)) {
            worldChunk = new WorldChunk(this.world, chunkPos, biomes);
            worldChunk.loadFromPacket(biomes, buf, nbt, bitSet);
            this.chunks.set(i, worldChunk);
        } else {
            worldChunk.loadFromPacket(biomes, buf, nbt, bitSet);
        }
        ChunkSection[] chunkSections = worldChunk.getSectionArray();
        LightingProvider lightingProvider = this.getLightingProvider();
        lightingProvider.setColumnEnabled(chunkPos, true);
        for (int j = 0; j < chunkSections.length; ++j) {
            ChunkSection chunkSection = chunkSections[j];
            int k = this.world.sectionIndexToCoord(j);
            lightingProvider.setSectionStatus(ChunkSectionPos.from(x, k, z), ChunkSection.isEmpty(chunkSection));
        }
        this.world.resetChunkColor(chunkPos);
        return worldChunk;
    }

    @Override
    public void tick(BooleanSupplier booleanSupplier) {
    }

    public void setChunkMapCenter(int x, int z) {
        this.chunks.centerChunkX = x;
        this.chunks.centerChunkZ = z;
    }

    public void updateLoadDistance(int loadDistance) {
        int i = this.chunks.radius;
        int j = ClientChunkManager.getChunkMapRadius(loadDistance);
        if (i != j) {
            ClientChunkMap clientChunkMap = new ClientChunkMap(j);
            clientChunkMap.centerChunkX = this.chunks.centerChunkX;
            clientChunkMap.centerChunkZ = this.chunks.centerChunkZ;
            for (int k = 0; k < this.chunks.chunks.length(); ++k) {
                WorldChunk worldChunk = this.chunks.chunks.get(k);
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
        return this.chunks.chunks.length() + ", " + this.getLoadedChunkCount();
    }

    @Override
    public int getLoadedChunkCount() {
        return this.chunks.loadedChunkCount;
    }

    @Override
    public void onLightUpdate(LightType type, ChunkSectionPos pos) {
        MinecraftClient.getInstance().worldRenderer.scheduleBlockRender(pos.getSectionX(), pos.getSectionY(), pos.getSectionZ());
    }

    @Override
    @Nullable
    public /* synthetic */ Chunk getChunk(int x, int z, ChunkStatus leastStatus, boolean create) {
        return this.getChunk(x, z, leastStatus, create);
    }

    @Environment(value=EnvType.CLIENT)
    final class ClientChunkMap {
        final AtomicReferenceArray<WorldChunk> chunks;
        final int radius;
        private final int diameter;
        volatile int centerChunkX;
        volatile int centerChunkZ;
        int loadedChunkCount;

        ClientChunkMap(int radius) {
            this.radius = radius;
            this.diameter = radius * 2 + 1;
            this.chunks = new AtomicReferenceArray(this.diameter * this.diameter);
        }

        int getIndex(int chunkX, int chunkZ) {
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

        boolean isInRadius(int chunkX, int chunkZ) {
            return Math.abs(chunkX - this.centerChunkX) <= this.radius && Math.abs(chunkZ - this.centerChunkZ) <= this.radius;
        }

        @Nullable
        protected WorldChunk getChunk(int index) {
            return this.chunks.get(index);
        }

        private void writePositions(String fileName) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(new File(fileName));){
                int i = ClientChunkManager.this.chunks.radius;
                for (int j = this.centerChunkZ - i; j <= this.centerChunkZ + i; ++j) {
                    for (int k = this.centerChunkX - i; k <= this.centerChunkX + i; ++k) {
                        WorldChunk worldChunk = ClientChunkManager.this.chunks.chunks.get(ClientChunkManager.this.chunks.getIndex(k, j));
                        if (worldChunk == null) continue;
                        ChunkPos chunkPos = worldChunk.getPos();
                        fileOutputStream.write((chunkPos.x + "\t" + chunkPos.z + "\t" + worldChunk.isEmpty() + "\n").getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
            catch (IOException iOException) {
                LOGGER.error((Object)iOException);
            }
        }
    }
}

