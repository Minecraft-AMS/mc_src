/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.util;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class UploadTokenCache {
    private static final Map<Long, String> tokenCache = new HashMap<Long, String>();

    public static String get(long worldId) {
        return tokenCache.get(worldId);
    }

    public static void invalidate(long world) {
        tokenCache.remove(world);
    }

    public static void put(long wid, String token) {
        tokenCache.put(wid, token);
    }
}

