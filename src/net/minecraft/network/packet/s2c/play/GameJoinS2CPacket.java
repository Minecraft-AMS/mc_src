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
import java.util.Optional;
import java.util.Set;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

public record GameJoinS2CPacket(int playerEntityId, boolean hardcore, GameMode gameMode, @Nullable GameMode previousGameMode, Set<RegistryKey<World>> dimensionIds, DynamicRegistryManager.Immutable registryManager, RegistryKey<DimensionType> dimensionType, RegistryKey<World> dimensionId, long sha256Seed, int maxPlayers, int viewDistance, int simulationDistance, boolean reducedDebugInfo, boolean showDeathScreen, boolean debugWorld, boolean flatWorld, Optional<GlobalPos> lastDeathLocation) implements Packet<ClientPlayPacketListener>
{
    private static final RegistryOps<NbtElement> REGISTRY_OPS = RegistryOps.of(NbtOps.INSTANCE, DynamicRegistryManager.of(Registries.REGISTRIES));

    public GameJoinS2CPacket(PacketByteBuf buf) {
        this(buf.readInt(), buf.readBoolean(), GameMode.byId(buf.readByte()), GameMode.getOrNull(buf.readByte()), buf.readCollection(Sets::newHashSetWithExpectedSize, b -> b.readRegistryKey(RegistryKeys.WORLD)), buf.decode(REGISTRY_OPS, SerializableRegistries.CODEC).toImmutable(), buf.readRegistryKey(RegistryKeys.DIMENSION_TYPE), buf.readRegistryKey(RegistryKeys.WORLD), buf.readLong(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readOptional(PacketByteBuf::readGlobalPos));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.playerEntityId);
        buf.writeBoolean(this.hardcore);
        buf.writeByte(this.gameMode.getId());
        buf.writeByte(GameMode.getId(this.previousGameMode));
        buf.writeCollection(this.dimensionIds, PacketByteBuf::writeRegistryKey);
        buf.encode(REGISTRY_OPS, SerializableRegistries.CODEC, this.registryManager);
        buf.writeRegistryKey(this.dimensionType);
        buf.writeRegistryKey(this.dimensionId);
        buf.writeLong(this.sha256Seed);
        buf.writeVarInt(this.maxPlayers);
        buf.writeVarInt(this.viewDistance);
        buf.writeVarInt(this.simulationDistance);
        buf.writeBoolean(this.reducedDebugInfo);
        buf.writeBoolean(this.showDeathScreen);
        buf.writeBoolean(this.debugWorld);
        buf.writeBoolean(this.flatWorld);
        buf.writeOptional(this.lastDeathLocation, PacketByteBuf::writeGlobalPos);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onGameJoin(this);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{GameJoinS2CPacket.class, "playerId;hardcore;gameType;previousGameType;levels;registryHolder;dimensionType;dimension;seed;maxPlayers;chunkRadius;simulationDistance;reducedDebugInfo;showDeathScreen;isDebug;isFlat;lastDeathLocation", "playerEntityId", "hardcore", "gameMode", "previousGameMode", "dimensionIds", "registryManager", "dimensionType", "dimensionId", "sha256Seed", "maxPlayers", "viewDistance", "simulationDistance", "reducedDebugInfo", "showDeathScreen", "debugWorld", "flatWorld", "lastDeathLocation"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GameJoinS2CPacket.class, "playerId;hardcore;gameType;previousGameType;levels;registryHolder;dimensionType;dimension;seed;maxPlayers;chunkRadius;simulationDistance;reducedDebugInfo;showDeathScreen;isDebug;isFlat;lastDeathLocation", "playerEntityId", "hardcore", "gameMode", "previousGameMode", "dimensionIds", "registryManager", "dimensionType", "dimensionId", "sha256Seed", "maxPlayers", "viewDistance", "simulationDistance", "reducedDebugInfo", "showDeathScreen", "debugWorld", "flatWorld", "lastDeathLocation"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GameJoinS2CPacket.class, "playerId;hardcore;gameType;previousGameType;levels;registryHolder;dimensionType;dimension;seed;maxPlayers;chunkRadius;simulationDistance;reducedDebugInfo;showDeathScreen;isDebug;isFlat;lastDeathLocation", "playerEntityId", "hardcore", "gameMode", "previousGameMode", "dimensionIds", "registryManager", "dimensionType", "dimensionId", "sha256Seed", "maxPlayers", "viewDistance", "simulationDistance", "reducedDebugInfo", "showDeathScreen", "debugWorld", "flatWorld", "lastDeathLocation"}, this, object);
    }
}

