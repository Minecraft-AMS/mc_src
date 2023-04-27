/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.util.UUIDTypeAdapter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RealmsUtil {
    static final MinecraftSessionService SESSION_SERVICE = MinecraftClient.getInstance().getSessionService();
    private static final LoadingCache<String, GameProfile> gameProfileCache = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.MINUTES).build((CacheLoader)new CacheLoader<String, GameProfile>(){

        public GameProfile load(String string) {
            return SESSION_SERVICE.fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString((String)string), null), false);
        }

        public /* synthetic */ Object load(Object uuid) throws Exception {
            return this.load((String)uuid);
        }
    });
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int SECONDS_PER_DAY = 86400;

    public static String uuidToName(String uuid) {
        return ((GameProfile)gameProfileCache.getUnchecked((Object)uuid)).getName();
    }

    public static GameProfile uuidToProfile(String uuid) {
        return (GameProfile)gameProfileCache.getUnchecked((Object)uuid);
    }

    public static String convertToAgePresentation(long milliseconds) {
        if (milliseconds < 0L) {
            return "right now";
        }
        long l = milliseconds / 1000L;
        if (l < 60L) {
            return (String)(l == 1L ? "1 second" : l + " seconds") + " ago";
        }
        if (l < 3600L) {
            long m = l / 60L;
            return (String)(m == 1L ? "1 minute" : m + " minutes") + " ago";
        }
        if (l < 86400L) {
            long m = l / 3600L;
            return (String)(m == 1L ? "1 hour" : m + " hours") + " ago";
        }
        long m = l / 86400L;
        return (String)(m == 1L ? "1 day" : m + " days") + " ago";
    }

    public static String convertToAgePresentation(Date date) {
        return RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - date.getTime());
    }

    public static void drawPlayerHead(MatrixStack matrices, int x, int y, int size, String uuid) {
        GameProfile gameProfile = RealmsUtil.uuidToProfile(uuid);
        Identifier identifier = MinecraftClient.getInstance().getSkinProvider().loadSkin(gameProfile);
        RenderSystem.setShaderTexture(0, identifier);
        PlayerSkinDrawer.draw(matrices, x, y, size);
    }
}

