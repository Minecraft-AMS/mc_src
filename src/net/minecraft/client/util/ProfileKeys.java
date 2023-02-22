/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.UserApiService
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import com.mojang.authlib.minecraft.UserApiService;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ProfileKeysImpl;
import net.minecraft.client.util.Session;
import net.minecraft.network.encryption.PlayerKeyPair;

@Environment(value=EnvType.CLIENT)
public interface ProfileKeys {
    public static final ProfileKeys MISSING = new ProfileKeys(){

        @Override
        public CompletableFuture<Optional<PlayerKeyPair>> fetchKeyPair() {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        @Override
        public boolean isExpired() {
            return false;
        }
    };

    public static ProfileKeys create(UserApiService userApiService, Session session, Path root) {
        if (session.getAccountType() == Session.AccountType.MSA) {
            return new ProfileKeysImpl(userApiService, session.getProfile().getId(), root);
        }
        return MISSING;
    }

    public CompletableFuture<Optional<PlayerKeyPair>> fetchKeyPair();

    public boolean isExpired();
}

