/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class GameJoinS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int playerEntityId;
    private long sha256Seed;
    private boolean hardcore;
    private GameMode gameMode;
    private GameMode previousGameMode;
    private Set<RegistryKey<World>> dimensionIds;
    private DynamicRegistryManager.Impl registryManager;
    private DimensionType dimensionType;
    private RegistryKey<World> dimensionId;
    private int maxPlayers;
    private int viewDistance;
    private boolean reducedDebugInfo;
    private boolean showDeathScreen;
    private boolean debugWorld;
    private boolean flatWorld;

    public GameJoinS2CPacket() {
    }

    public GameJoinS2CPacket(int playerEntityId, GameMode gameMode, GameMode previousGameMode, long sha256Seed, boolean hardcore, Set<RegistryKey<World>> dimensionIds, DynamicRegistryManager.Impl registryManager, DimensionType dimensionType, RegistryKey<World> dimensionId, int maxPlayers, int chunkLoadDistance, boolean reducedDebugInfo, boolean showDeathScreen, boolean debugWorld, boolean flatWorld) {
        this.playerEntityId = playerEntityId;
        this.dimensionIds = dimensionIds;
        this.registryManager = registryManager;
        this.dimensionType = dimensionType;
        this.dimensionId = dimensionId;
        this.sha256Seed = sha256Seed;
        this.gameMode = gameMode;
        this.previousGameMode = previousGameMode;
        this.maxPlayers = maxPlayers;
        this.hardcore = hardcore;
        this.viewDistance = chunkLoadDistance;
        this.reducedDebugInfo = reducedDebugInfo;
        this.showDeathScreen = showDeathScreen;
        this.debugWorld = debugWorld;
        this.flatWorld = flatWorld;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.playerEntityId = buf.readInt();
        this.hardcore = buf.readBoolean();
        this.gameMode = GameMode.byId(buf.readByte());
        this.previousGameMode = GameMode.byId(buf.readByte());
        int i = buf.readVarInt();
        this.dimensionIds = Sets.newHashSet();
        for (int j = 0; j < i; ++j) {
            this.dimensionIds.add(RegistryKey.of(Registry.WORLD_KEY, buf.readIdentifier()));
        }
        this.registryManager = buf.decode(DynamicRegistryManager.Impl.CODEC);
        this.dimensionType = buf.decode(DimensionType.REGISTRY_CODEC).get();
        this.dimensionId = RegistryKey.of(Registry.WORLD_KEY, buf.readIdentifier());
        this.sha256Seed = buf.readLong();
        this.maxPlayers = buf.readVarInt();
        this.viewDistance = buf.readVarInt();
        this.reducedDebugInfo = buf.readBoolean();
        this.showDeathScreen = buf.readBoolean();
        this.debugWorld = buf.readBoolean();
        this.flatWorld = buf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeInt(this.playerEntityId);
        buf.writeBoolean(this.hardcore);
        buf.writeByte(this.gameMode.getId());
        buf.writeByte(this.previousGameMode.getId());
        buf.writeVarInt(this.dimensionIds.size());
        for (RegistryKey<World> registryKey : this.dimensionIds) {
            buf.writeIdentifier(registryKey.getValue());
        }
        buf.encode(DynamicRegistryManager.Impl.CODEC, this.registryManager);
        buf.encode(DimensionType.REGISTRY_CODEC, () -> this.dimensionType);
        buf.writeIdentifier(this.dimensionId.getValue());
        buf.writeLong(this.sha256Seed);
        buf.writeVarInt(this.maxPlayers);
        buf.writeVarInt(this.viewDistance);
        buf.writeBoolean(this.reducedDebugInfo);
        buf.writeBoolean(this.showDeathScreen);
        buf.writeBoolean(this.debugWorld);
        buf.writeBoolean(this.flatWorld);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onGameJoin(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getEntityId() {
        return this.playerEntityId;
    }

    @Environment(value=EnvType.CLIENT)
    public long getSha256Seed() {
        return this.sha256Seed;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isHardcore() {
        return this.hardcore;
    }

    @Environment(value=EnvType.CLIENT)
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Environment(value=EnvType.CLIENT)
    public GameMode getPreviousGameMode() {
        return this.previousGameMode;
    }

    @Environment(value=EnvType.CLIENT)
    public Set<RegistryKey<World>> getDimensionIds() {
        return this.dimensionIds;
    }

    @Environment(value=EnvType.CLIENT)
    public DynamicRegistryManager getRegistryManager() {
        return this.registryManager;
    }

    @Environment(value=EnvType.CLIENT)
    public DimensionType getDimensionType() {
        return this.dimensionType;
    }

    @Environment(value=EnvType.CLIENT)
    public RegistryKey<World> getDimensionId() {
        return this.dimensionId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getViewDistance() {
        return this.viewDistance;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean showsDeathScreen() {
        return this.showDeathScreen;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isDebugWorld() {
        return this.debugWorld;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFlatWorld() {
        return this.flatWorld;
    }
}

