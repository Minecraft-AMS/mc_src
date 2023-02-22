/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 */
package net.minecraft.server.world;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.stream.Stream;
import net.minecraft.server.network.ServerPlayerEntity;

public final class PlayerChunkWatchingManager {
    private final Object2BooleanMap<ServerPlayerEntity> watchingPlayers = new Object2BooleanOpenHashMap();

    public Stream<ServerPlayerEntity> getPlayersWatchingChunk(long l) {
        return this.watchingPlayers.keySet().stream();
    }

    public void add(long l, ServerPlayerEntity player, boolean watchDisabled) {
        this.watchingPlayers.put((Object)player, watchDisabled);
    }

    public void remove(long l, ServerPlayerEntity player) {
        this.watchingPlayers.removeBoolean((Object)player);
    }

    public void disableWatch(ServerPlayerEntity player) {
        this.watchingPlayers.replace((Object)player, true);
    }

    public void enableWatch(ServerPlayerEntity player) {
        this.watchingPlayers.replace((Object)player, false);
    }

    public boolean isWatchInactive(ServerPlayerEntity player) {
        return this.watchingPlayers.getOrDefault((Object)player, true);
    }

    public boolean isWatchDisabled(ServerPlayerEntity player) {
        return this.watchingPlayers.getBoolean((Object)player);
    }

    public void movePlayer(long prevPos, long currentPos, ServerPlayerEntity player) {
    }
}

