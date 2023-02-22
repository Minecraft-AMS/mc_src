/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 */
package net.minecraft.util;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.util.UserCache;

public record ApiServices(MinecraftSessionService sessionService, SignatureVerifier serviceSignatureVerifier, GameProfileRepository profileRepository, UserCache userCache) {
    private static final String USER_CACHE_FILE_NAME = "usercache.json";

    public static ApiServices create(YggdrasilAuthenticationService authenticationService, File rootDirectory) {
        MinecraftSessionService minecraftSessionService = authenticationService.createMinecraftSessionService();
        GameProfileRepository gameProfileRepository = authenticationService.createProfileRepository();
        UserCache userCache = new UserCache(gameProfileRepository, new File(rootDirectory, USER_CACHE_FILE_NAME));
        SignatureVerifier signatureVerifier = SignatureVerifier.create(authenticationService.getServicesKey());
        return new ApiServices(minecraftSessionService, signatureVerifier, gameProfileRepository, userCache);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ApiServices.class, "sessionService;serviceSignatureValidator;profileRepository;profileCache", "sessionService", "serviceSignatureVerifier", "profileRepository", "userCache"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ApiServices.class, "sessionService;serviceSignatureValidator;profileRepository;profileCache", "sessionService", "serviceSignatureVerifier", "profileRepository", "userCache"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ApiServices.class, "sessionService;serviceSignatureValidator;profileRepository;profileCache", "sessionService", "serviceSignatureVerifier", "profileRepository", "userCache"}, this, object);
    }
}

