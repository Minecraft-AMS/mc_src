/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Sets;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Set;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

public record GameJoinS2CPacket(int playerEntityId, boolean hardcore, GameMode gameMode, @Nullable GameMode previousGameMode, Set<RegistryKey<World>> dimensionIds, DynamicRegistryManager.Immutable registryManager, RegistryEntry<DimensionType> dimensionType, RegistryKey<World> dimensionId, long sha256Seed, int maxPlayers, int viewDistance, int simulationDistance, boolean reducedDebugInfo, boolean showDeathScreen, boolean debugWorld, boolean flatWorld) implements Packet<ClientPlayPacketListener>
{
    public GameJoinS2CPacket(PacketByteBuf buf) {
        this(buf.readInt(), buf.readBoolean(), GameMode.byId(buf.readByte()), GameMode.getOrNull(buf.readByte()), buf.readCollection(Sets::newHashSetWithExpectedSize, b -> RegistryKey.of(Registry.WORLD_KEY, b.readIdentifier())), buf.decode(DynamicRegistryManager.CODEC).toImmutable(), buf.decode(DimensionType.REGISTRY_CODEC), RegistryKey.of(Registry.WORLD_KEY, buf.readIdentifier()), buf.readLong(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.playerEntityId);
        buf.writeBoolean(this.hardcore);
        buf.writeByte(this.gameMode.getId());
        buf.writeByte(GameMode.getId(this.previousGameMode));
        buf.writeCollection(this.dimensionIds, (b, dimension) -> b.writeIdentifier(dimension.getValue()));
        buf.encode(DynamicRegistryManager.CODEC, this.registryManager);
        buf.encode(DimensionType.REGISTRY_CODEC, this.dimensionType);
        buf.writeIdentifier(this.dimensionId.getValue());
        buf.writeLong(this.sha256Seed);
        buf.writeVarInt(this.maxPlayers);
        buf.writeVarInt(this.viewDistance);
        buf.writeVarInt(this.simulationDistance);
        buf.writeBoolean(this.reducedDebugInfo);
        buf.writeBoolean(this.showDeathScreen);
        buf.writeBoolean(this.debugWorld);
        buf.writeBoolean(this.flatWorld);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onGameJoin(this);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{GameJoinS2CPacket.class, "playerId;hardcore;gameType;previousGameType;levels;registryHolder;dimensionType;dimension;seed;maxPlayers;chunkRadius;simulationDistance;reducedDebugInfo;showDeathScreen;isDebug;isFlat", "playerEntityId", "hardcore", "gameMode", "previousGameMode", "dimensionIds", "registryManager", "dimensionType", "dimensionId", "sha256Seed", "maxPlayers", "viewDistance", "simulationDistance", "reducedDebugInfo", "showDeathScreen", "debugWorld", "flatWorld"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GameJoinS2CPacket.class, "playerId;hardcore;gameType;previousGameType;levels;registryHolder;dimensionType;dimension;seed;maxPlayers;chunkRadius;simulationDistance;reducedDebugInfo;showDeathScreen;isDebug;isFlat", "playerEntityId", "hardcore", "gameMode", "previousGameMode", "dimensionIds", "registryManager", "dimensionType", "dimensionId", "sha256Seed", "maxPlayers", "viewDistance", "simulationDistance", "reducedDebugInfo", "showDeathScreen", "debugWorld", "flatWorld"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GameJoinS2CPacket.class, "playerId;hardcore;gameType;previousGameType;levels;registryHolder;dimensionType;dimension;seed;maxPlayers;chunkRadius;simulationDistance;reducedDebugInfo;showDeathScreen;isDebug;isFlat", "playerEntityId", "hardcore", "gameMode", "previousGameMode", "dimensionIds", "registryManager", "dimensionType", "dimensionId", "sha256Seed", "maxPlayers", "viewDistance", "simulationDistance", "reducedDebugInfo", "showDeathScreen", "debugWorld", "flatWorld"}, this, object);
    }
}

