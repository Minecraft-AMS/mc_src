/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.hash.Hashing
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.InsecureTextureException
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.properties.Property
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinProvider {
    public static final String TEXTURES = "textures";
    private final TextureManager textureManager;
    private final File skinCacheDir;
    private final MinecraftSessionService sessionService;
    private final LoadingCache<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> skinCache;

    public PlayerSkinProvider(TextureManager textureManager, File skinCacheDir, final MinecraftSessionService sessionService) {
        this.textureManager = textureManager;
        this.skinCacheDir = skinCacheDir;
        this.sessionService = sessionService;
        this.skinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build((CacheLoader)new CacheLoader<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>(){

            public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(String string) {
                GameProfile gameProfile = new GameProfile(null, "dummy_mcdummyface");
                gameProfile.getProperties().put((Object)PlayerSkinProvider.TEXTURES, (Object)new Property(PlayerSkinProvider.TEXTURES, string, ""));
                try {
                    return sessionService.getTextures(gameProfile, false);
                }
                catch (Throwable throwable) {
                    return ImmutableMap.of();
                }
            }

            public /* synthetic */ Object load(Object value) throws Exception {
                return this.load((String)value);
            }
        });
    }

    public Identifier loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type type) {
        return this.loadSkin(profileTexture, type, null);
    }

    private Identifier loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type type, @Nullable SkinTextureAvailableCallback callback) {
        String string = Hashing.sha1().hashUnencodedChars((CharSequence)profileTexture.getHash()).toString();
        Identifier identifier = PlayerSkinProvider.getSkinId(type, string);
        AbstractTexture abstractTexture = this.textureManager.getOrDefault(identifier, MissingSprite.getMissingSpriteTexture());
        if (abstractTexture == MissingSprite.getMissingSpriteTexture()) {
            File file = new File(this.skinCacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
            File file2 = new File(file, string);
            PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(file2, profileTexture.getUrl(), DefaultSkinHelper.getTexture(), type == MinecraftProfileTexture.Type.SKIN, () -> {
                if (callback != null) {
                    callback.onSkinTextureAvailable(type, identifier, profileTexture);
                }
            });
            this.textureManager.registerTexture(identifier, playerSkinTexture);
        } else if (callback != null) {
            callback.onSkinTextureAvailable(type, identifier, profileTexture);
        }
        return identifier;
    }

    private static Identifier getSkinId(MinecraftProfileTexture.Type skinType, String hash) {
        String string = switch (skinType) {
            default -> throw new IncompatibleClassChangeError();
            case MinecraftProfileTexture.Type.SKIN -> "skins";
            case MinecraftProfileTexture.Type.CAPE -> "capes";
            case MinecraftProfileTexture.Type.ELYTRA -> "elytra";
        };
        return new Identifier(string + "/" + hash);
    }

    public void loadSkin(GameProfile profile, SkinTextureAvailableCallback callback, boolean requireSecure) {
        Runnable runnable = () -> {
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
            MinecraftClient.getInstance().execute(() -> RenderSystem.recordRenderCall(() -> ImmutableList.of((Object)MinecraftProfileTexture.Type.SKIN, (Object)MinecraftProfileTexture.Type.CAPE).forEach(textureType -> {
                if (map.containsKey(textureType)) {
                    this.loadSkin((MinecraftProfileTexture)map.get(textureType), (MinecraftProfileTexture.Type)textureType, callback);
                }
            })));
        };
        Util.getMainWorkerExecutor().execute(runnable);
    }

    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile) {
        Property property = (Property)Iterables.getFirst((Iterable)profile.getProperties().get((Object)TEXTURES), null);
        if (property == null) {
            return ImmutableMap.of();
        }
        return (Map)this.skinCache.getUnchecked((Object)property.getValue());
    }

    public Identifier loadSkin(GameProfile profile) {
        MinecraftProfileTexture minecraftProfileTexture = this.getTextures(profile).get(MinecraftProfileTexture.Type.SKIN);
        if (minecraftProfileTexture != null) {
            return this.loadSkin(minecraftProfileTexture, MinecraftProfileTexture.Type.SKIN);
        }
        return DefaultSkinHelper.getTexture(DynamicSerializableUuid.getUuidFromProfile(profile));
    }

    @Environment(value=EnvType.CLIENT)
    public static interface SkinTextureAvailableCallback {
        public void onSkinTextureAvailable(MinecraftProfileTexture.Type var1, Identifier var2, MinecraftProfileTexture var3);
    }
}

