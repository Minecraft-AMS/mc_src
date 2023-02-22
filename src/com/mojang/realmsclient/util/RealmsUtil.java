/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.Maps
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.util.UUIDTypeAdapter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;

@Environment(value=EnvType.CLIENT)
public class RealmsUtil {
    private static final YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(Realms.getProxy(), UUID.randomUUID().toString());
    private static final MinecraftSessionService sessionService = authenticationService.createMinecraftSessionService();
    public static LoadingCache<String, GameProfile> gameProfileCache = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.MINUTES).build((CacheLoader)new CacheLoader<String, GameProfile>(){

        public GameProfile load(String string) throws Exception {
            GameProfile gameProfile = sessionService.fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString((String)string), null), false);
            if (gameProfile == null) {
                throw new Exception("Couldn't get profile");
            }
            return gameProfile;
        }

        public /* synthetic */ Object load(Object object) throws Exception {
            return this.load((String)object);
        }
    });

    public static String uuidToName(String uuid) throws Exception {
        GameProfile gameProfile = (GameProfile)gameProfileCache.get((Object)uuid);
        return gameProfile.getName();
    }

    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(String uuid) {
        try {
            GameProfile gameProfile = (GameProfile)gameProfileCache.get((Object)uuid);
            return sessionService.getTextures(gameProfile, false);
        }
        catch (Exception exception) {
            return Maps.newHashMap();
        }
    }

    public static void browseTo(String uri) {
        Realms.openUri(uri);
    }

    public static String convertToAgePresentation(Long timeDiff) {
        if (timeDiff < 0L) {
            return "right now";
        }
        long l = timeDiff / 1000L;
        if (l < 60L) {
            return (l == 1L ? "1 second" : l + " seconds") + " ago";
        }
        if (l < 3600L) {
            long m = l / 60L;
            return (m == 1L ? "1 minute" : m + " minutes") + " ago";
        }
        if (l < 86400L) {
            long m = l / 3600L;
            return (m == 1L ? "1 hour" : m + " hours") + " ago";
        }
        long m = l / 86400L;
        return (m == 1L ? "1 day" : m + " days") + " ago";
    }
}

