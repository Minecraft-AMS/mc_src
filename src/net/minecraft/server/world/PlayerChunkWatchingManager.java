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

    public void add(long l, ServerPlayerEntity serverPlayerEntity, boolean watchDisabled) {
        this.watchingPlayers.put((Object)serverPlayerEntity, watchDisabled);
    }

    public void remove(long l, ServerPlayerEntity serverPlayerEntity) {
        this.watchingPlayers.removeBoolean((Object)serverPlayerEntity);
    }

    public void disableWatch(ServerPlayerEntity serverPlayerEntity) {
        this.watchingPlayers.replace((Object)serverPlayerEntity, true);
    }

    public void enableWatch(ServerPlayerEntity serverPlayerEntity) {
        this.watchingPlayers.replace((Object)serverPlayerEntity, false);
    }

    public boolean method_21715(ServerPlayerEntity serverPlayerEntity) {
        return this.watchingPlayers.getOrDefault((Object)serverPlayerEntity, true);
    }

    public boolean isWatchDisabled(ServerPlayerEntity serverPlayerEntity) {
        return this.watchingPlayers.getBoolean((Object)serverPlayerEntity);
    }

    public void movePlayer(long prevPos, long currentPos, ServerPlayerEntity serverPlayerEntity) {
    }
}

