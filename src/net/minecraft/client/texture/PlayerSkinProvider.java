/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.hash.Hashing
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.InsecureTextureException
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.ImageFilter;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.SkinRemappingImageFilter;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinProvider {
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
    private final TextureManager textureManager;
    private final File skinCacheDir;
    private final MinecraftSessionService sessionService;
    private final LoadingCache<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> skinCache;

    public PlayerSkinProvider(TextureManager textureManager, File file, MinecraftSessionService minecraftSessionService) {
        this.textureManager = textureManager;
        this.skinCacheDir = file;
        this.sessionService = minecraftSessionService;
        this.skinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build((CacheLoader)new CacheLoader<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>(){

            public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(GameProfile gameProfile) throws Exception {
                try {
                    return MinecraftClient.getInstance().getSessionService().getTextures(gameProfile, false);
                }
                catch (Throwable throwable) {
                    return Maps.newHashMap();
                }
            }

            public /* synthetic */ Object load(Object object) throws Exception {
                return this.load((GameProfile)object);
            }
        });
    }

    public Identifier loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type type) {
        return this.loadSkin(profileTexture, type, null);
    }

    public Identifier loadSkin(final MinecraftProfileTexture profileTexture, final MinecraftProfileTexture.Type type, final @Nullable SkinTextureAvailableCallback callback) {
        String string = Hashing.sha1().hashUnencodedChars((CharSequence)profileTexture.getHash()).toString();
        final Identifier identifier = new Identifier("skins/" + string);
        Texture texture = this.textureManager.getTexture(identifier);
        if (texture != null) {
            if (callback != null) {
                callback.onSkinTextureAvailable(type, identifier, profileTexture);
            }
        } else {
            File file = new File(this.skinCacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
            File file2 = new File(file, string);
            final SkinRemappingImageFilter imageFilter = type == MinecraftProfileTexture.Type.SKIN ? new SkinRemappingImageFilter() : null;
            PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(file2, profileTexture.getUrl(), DefaultSkinHelper.getTexture(), new ImageFilter(){

                @Override
                public NativeImage filterImage(NativeImage nativeImage) {
                    if (imageFilter != null) {
                        return imageFilter.filterImage(nativeImage);
                    }
                    return nativeImage;
                }

                @Override
                public void method_3238() {
                    if (imageFilter != null) {
                        imageFilter.method_3238();
                    }
                    if (callback != null) {
                        callback.onSkinTextureAvailable(type, identifier, profileTexture);
                    }
                }
            });
            this.textureManager.registerTexture(identifier, playerSkinTexture);
        }
        return identifier;
    }

    public void loadSkin(GameProfile profile, SkinTextureAvailableCallback callback, boolean requireSecure) {
        EXECUTOR_SERVICE.submit(() -> {
            HashMap map = Maps.newHashMap();
            try {
                map.putAll(this.sessionService.getTextures(profile, requireSecure));
            }
            catch (InsecureTextureException insecureTextureException) {
                // empty catch block
            }
            if (map.isEmpty()) {
                profile.getProperties().clear();
                if (profile.getId().equals(MinecraftClient.getInstance().getSession().getProfile().getId())) {
                    profile.getProperties().putAll((Multimap)MinecraftClient.getInstance().getSessionProperties());
                    map.putAll(this.sessionService.getTextures(profile, false));
                } else {
                    this.sessionService.fillProfileProperties(profile, requireSecure);
                    try {
                        map.putAll(this.sessionService.getTextures(profile, requireSecure));
                    }
                    catch (InsecureTextureException insecureTextureException) {
                        // empty catch block
                    }
                }
            }
            MinecraftClient.getInstance().execute(() -> {
                if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                    this.loadSkin((MinecraftProfileTexture)map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN, callback);
                }
                if (map.containsKey(MinecraftProfileTexture.Type.CAPE)) {
                    this.loadSkin((MinecraftProfileTexture)map.get(MinecraftProfileTexture.Type.CAPE), MinecraftProfileTexture.Type.CAPE, callback);
                }
            });
        });
    }

    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile) {
        return (Map)this.skinCache.getUnchecked((Object)profile);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface SkinTextureAvailableCallback {
        public void onSkinTextureAvailable(MinecraftProfileTexture.Type var1, Identifier var2, MinecraftProfileTexture var3);
    }
}

