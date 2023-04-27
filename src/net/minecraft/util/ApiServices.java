/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.yggdrasil.ServicesKeySet
 *  com.mojang.authlib.yggdrasil.ServicesKeyType
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.util.UserCache;
import org.jetbrains.annotations.Nullable;

/*
 * Duplicate member names - consider using --renamedupmembers true
 */
public final class ApiServices
extends Record {
    private final MinecraftSessionService sessionService;
    private final ServicesKeySet serviceSignatureVerifier;
    private final GameProfileRepository profileRepository;
    private final UserCache userCache;
    private static final String USER_CACHE_FILE_NAME = "usercache.json";

    public ApiServices(MinecraftSessionService minecraftSessionService, ServicesKeySet servicesKeySet, GameProfileRepository gameProfileRepository, UserCache userCache) {
        this.sessionService = minecraftSessionService;
        this.serviceSignatureVerifier = servicesKeySet;
        this.profileRepository = gameProfileRepository;
        this.userCache = userCache;
    }

    public static ApiServices create(YggdrasilAuthenticationService authenticationService, File rootDirectory) {
        MinecraftSessionService minecraftSessionService = authenticationService.createMinecraftSessionService();
        GameProfileRepository gameProfileRepository = authenticationService.createProfileRepository();
        UserCache userCache = new UserCache(gameProfileRepository, new File(rootDirectory, USER_CACHE_FILE_NAME));
        return new ApiServices(minecraftSessionService, authenticationService.getServicesKeySet(), gameProfileRepository, userCache);
    }

    @Nullable
    public SignatureVerifier serviceSignatureVerifier() {
        return SignatureVerifier.create(this.serviceSignatureVerifier, ServicesKeyType.PROFILE_KEY);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ApiServices.class, "sessionService;servicesKeySet;profileRepository;profileCache", "sessionService", "serviceSignatureVerifier", "profileRepository", "userCache"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ApiServices.class, "sessionService;servicesKeySet;profileRepository;profileCache", "sessionService", "serviceSignatureVerifier", "profileRepository", "userCache"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ApiServices.class, "sessionService;servicesKeySet;profileRepository;profileCache", "sessionService", "serviceSignatureVerifier", "profileRepository", "userCache"}, this, object);
    }

    public MinecraftSessionService sessionService() {
        return this.sessionService;
    }

    public ServicesKeySet serviceSignatureVerifier() {
        return this.serviceSignatureVerifier;
    }

    public GameProfileRepository profileRepository() {
        return this.profileRepository;
    }

    public UserCache userCache() {
        return this.userCache;
    }
}

