/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.util.UUIDTypeAdapter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Proxy;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Session;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.MessageType;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RepeatedNarrator;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class Realms {
    private static final RepeatedNarrator REPEATED_NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));

    public static boolean isTouchScreen() {
        return MinecraftClient.getInstance().options.touchscreen;
    }

    public static Proxy getProxy() {
        return MinecraftClient.getInstance().getNetworkProxy();
    }

    public static String sessionId() {
        Session session = MinecraftClient.getInstance().getSession();
        if (session == null) {
            return null;
        }
        return session.getSessionId();
    }

    public static String userName() {
        Session session = MinecraftClient.getInstance().getSession();
        if (session == null) {
            return null;
        }
        return session.getUsername();
    }

    public static long currentTimeMillis() {
        return Util.getMeasuringTimeMs();
    }

    public static String getSessionId() {
        return MinecraftClient.getInstance().getSession().getSessionId();
    }

    public static String getUUID() {
        return MinecraftClient.getInstance().getSession().getUuid();
    }

    public static String getName() {
        return MinecraftClient.getInstance().getSession().getUsername();
    }

    public static String uuidToName(String string) {
        return MinecraftClient.getInstance().getSessionService().fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString((String)string), null), false).getName();
    }

    public static <V> CompletableFuture<V> execute(Supplier<V> supplier) {
        return MinecraftClient.getInstance().submit(supplier);
    }

    public static void execute(Runnable runnable) {
        MinecraftClient.getInstance().execute(runnable);
    }

    public static void setScreen(RealmsScreen realmsScreen) {
        Realms.execute(() -> {
            Realms.setScreenDirect(realmsScreen);
            return null;
        });
    }

    public static void setScreenDirect(RealmsScreen realmsScreen) {
        MinecraftClient.getInstance().openScreen(realmsScreen.getProxy());
    }

    public static String getGameDirectoryPath() {
        return MinecraftClient.getInstance().runDirectory.getAbsolutePath();
    }

    public static int survivalId() {
        return GameMode.SURVIVAL.getId();
    }

    public static int creativeId() {
        return GameMode.CREATIVE.getId();
    }

    public static int adventureId() {
        return GameMode.ADVENTURE.getId();
    }

    public static int spectatorId() {
        return GameMode.SPECTATOR.getId();
    }

    public static void setConnectedToRealms(boolean bl) {
        MinecraftClient.getInstance().setConnectedToRealms(bl);
    }

    public static CompletableFuture<?> downloadResourcePack(String string, String string2) {
        return MinecraftClient.getInstance().getResourcePackDownloader().download(string, string2);
    }

    public static void clearResourcePack() {
        MinecraftClient.getInstance().getResourcePackDownloader().clear();
    }

    public static boolean getRealmsNotificationsEnabled() {
        return MinecraftClient.getInstance().options.realmsNotifications;
    }

    public static boolean inTitleScreen() {
        return MinecraftClient.getInstance().currentScreen != null && MinecraftClient.getInstance().currentScreen instanceof TitleScreen;
    }

    public static void deletePlayerTag(File file) {
        if (file.exists()) {
            try {
                CompoundTag compoundTag = NbtIo.readCompressed(new FileInputStream(file));
                CompoundTag compoundTag2 = compoundTag.getCompound("Data");
                compoundTag2.remove("Player");
                NbtIo.writeCompressed(compoundTag, new FileOutputStream(file));
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void openUri(String string) {
        Util.getOperatingSystem().open(string);
    }

    public static void setClipboard(String string) {
        MinecraftClient.getInstance().keyboard.setClipboard(string);
    }

    public static String getMinecraftVersionString() {
        return SharedConstants.getGameVersion().getName();
    }

    public static Identifier resourceLocation(String string) {
        return new Identifier(string);
    }

    public static String getLocalizedString(String string, Object ... objects) {
        return I18n.translate(string, objects);
    }

    public static void bind(String string) {
        Identifier identifier = new Identifier(string);
        MinecraftClient.getInstance().getTextureManager().bindTexture(identifier);
    }

    public static void narrateNow(String string) {
        NarratorManager narratorManager = NarratorManager.INSTANCE;
        narratorManager.clear();
        narratorManager.onChatMessage(MessageType.SYSTEM, new LiteralText(Realms.fixNarrationNewlines(string)));
    }

    private static String fixNarrationNewlines(String string) {
        return string.replace("\\n", System.lineSeparator());
    }

    public static void narrateNow(String ... strings) {
        Realms.narrateNow(Arrays.asList(strings));
    }

    public static void narrateNow(Iterable<String> iterable) {
        Realms.narrateNow(Realms.joinNarrations(iterable));
    }

    public static String joinNarrations(Iterable<String> iterable) {
        return String.join((CharSequence)System.lineSeparator(), iterable);
    }

    public static void narrateRepeatedly(String string) {
        REPEATED_NARRATOR.narrate(Realms.fixNarrationNewlines(string));
    }
}
