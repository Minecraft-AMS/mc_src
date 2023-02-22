/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.filter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TextStream {
    public void onConnect();

    public void onDisconnect();

    public CompletableFuture<Optional<String>> filterText(String var1);

    public CompletableFuture<Optional<List<String>>> filterTexts(List<String> var1);
}

