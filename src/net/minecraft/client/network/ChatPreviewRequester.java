/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.network;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.RequestChatPreviewC2SPacket;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChatPreviewRequester {
    private static final long EARLIEST_NEXT_QUERY_DELAY = 100L;
    private static final long LATEST_NEXT_QUERY_DELAY = 1000L;
    private final MinecraftClient client;
    private final IdIncrementor idIncrementor = new IdIncrementor();
    @Nullable
    private Query pendingResponseQuery;
    private long queryTime;

    public ChatPreviewRequester(MinecraftClient client) {
        this.client = client;
    }

    public boolean tryRequest(String message, long currentTime) {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        if (clientPlayNetworkHandler == null) {
            this.clear();
            return true;
        }
        if (this.pendingResponseQuery != null && this.pendingResponseQuery.messageEquals(message)) {
            return true;
        }
        if (this.client.isInSingleplayer() || this.shouldRequest(currentTime)) {
            Query query;
            this.pendingResponseQuery = query = new Query(this.idIncrementor.next(), message);
            this.queryTime = currentTime;
            clientPlayNetworkHandler.sendPacket(new RequestChatPreviewC2SPacket(query.id(), query.message()));
            return true;
        }
        return false;
    }

    @Nullable
    public String handleResponse(int id) {
        if (this.pendingResponseQuery != null && this.pendingResponseQuery.idEquals(id)) {
            String string = this.pendingResponseQuery.message;
            this.pendingResponseQuery = null;
            return string;
        }
        return null;
    }

    private boolean shouldRequest(long currentTime) {
        long l = this.queryTime + 100L;
        if (currentTime >= l) {
            long m = this.queryTime + 1000L;
            return this.pendingResponseQuery == null || currentTime >= m;
        }
        return false;
    }

    public void clear() {
        this.pendingResponseQuery = null;
        this.queryTime = 0L;
    }

    public boolean hasPendingResponseQuery() {
        return this.pendingResponseQuery != null;
    }

    @Environment(value=EnvType.CLIENT)
    static class IdIncrementor {
        private static final int MAX_INCREMENT = 100;
        private final Random random = Random.createLocal();
        private int current;

        IdIncrementor() {
        }

        public int next() {
            int i;
            this.current = i = this.current + this.random.nextInt(100);
            return i;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Query
    extends Record {
        private final int id;
        final String message;

        Query(int i, String string) {
            this.id = i;
            this.message = string;
        }

        public boolean idEquals(int id) {
            return this.id == id;
        }

        public boolean messageEquals(String message) {
            return this.message.equals(message);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Query.class, "id;query", "id", "message"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Query.class, "id;query", "id", "message"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Query.class, "id;query", "id", "message"}, this, object);
        }

        public int id() {
            return this.id;
        }

        public String message() {
            return this.message;
        }
    }
}

