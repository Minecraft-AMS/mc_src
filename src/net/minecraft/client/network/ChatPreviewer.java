/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.network;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ChatPreviewRequester;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChatPreviewer {
    private static final long CONSUME_COOLDOWN = 200L;
    @Nullable
    private String lastPreviewedMessage;
    @Nullable
    private String pendingRequestMessage;
    private final ChatPreviewRequester requester;
    @Nullable
    private Response lastResponse;

    public ChatPreviewer(MinecraftClient client) {
        this.requester = new ChatPreviewRequester(client);
    }

    public void tryRequestPending() {
        String string = this.pendingRequestMessage;
        if (string != null && this.requester.tryRequest(string, Util.getMeasuringTimeMs())) {
            this.pendingRequestMessage = null;
        }
    }

    public void tryRequest(String message) {
        if (!(message = ChatPreviewer.normalize(message)).isEmpty()) {
            if (!message.equals(this.lastPreviewedMessage)) {
                this.lastPreviewedMessage = message;
                this.tryRequestInternal(message);
            }
        } else {
            this.clear();
        }
    }

    private void tryRequestInternal(String message) {
        this.pendingRequestMessage = !this.requester.tryRequest(message, Util.getMeasuringTimeMs()) ? message : null;
    }

    public void disablePreview() {
        this.clear();
    }

    private void clear() {
        this.lastPreviewedMessage = null;
        this.pendingRequestMessage = null;
        this.lastResponse = null;
        this.requester.clear();
    }

    public void onResponse(int id, @Nullable Text previewText) {
        String string = this.requester.handleResponse(id);
        if (string != null) {
            this.lastResponse = new Response(Util.getMeasuringTimeMs(), string, previewText);
        }
    }

    public boolean cannotConsumePreview() {
        return this.pendingRequestMessage != null || this.lastResponse != null && !this.lastResponse.hasCooldownPassed();
    }

    public boolean equalsLastPreviewed(String text) {
        return ChatPreviewer.normalize(text).equals(this.lastPreviewedMessage);
    }

    @Nullable
    public Response getPreviewText() {
        return this.lastResponse;
    }

    @Nullable
    public Response tryConsumeResponse(String message) {
        if (this.lastResponse != null && this.lastResponse.canConsume(message)) {
            Response response = this.lastResponse;
            this.lastResponse = null;
            return response;
        }
        return null;
    }

    static String normalize(String message) {
        return StringUtils.normalizeSpace((String)message.trim());
    }

    @Environment(value=EnvType.CLIENT)
    public record Response(long receptionTimestamp, String query, @Nullable Text previewText) {
        public Response {
            string = ChatPreviewer.normalize(string);
        }

        private boolean queryEquals(String query) {
            return this.query.equals(ChatPreviewer.normalize(query));
        }

        boolean canConsume(String message) {
            if (this.queryEquals(message)) {
                return this.hasCooldownPassed();
            }
            return false;
        }

        boolean hasCooldownPassed() {
            long l = this.receptionTimestamp + 200L;
            return Util.getMeasuringTimeMs() >= l;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Response.class, "receivedTimeStamp;query;response", "receptionTimestamp", "query", "previewText"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Response.class, "receivedTimeStamp;query;response", "receptionTimestamp", "query", "previewText"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Response.class, "receivedTimeStamp;query;response", "receptionTimestamp", "query", "previewText"}, this, object);
        }
    }
}

