/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.hash.Hashing
 *  com.google.common.io.Files
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.comparator.LastModifiedFileComparator
 *  org.apache.commons.io.filefilter.IOFileFilter
 *  org.apache.commons.io.filefilter.TrueFileFilter
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.DefaultClientResourcePack;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientBuiltinResourcePackProvider
implements ResourcePackProvider {
    private static final PackResourceMetadata DEFAULT_PACK_METADATA = new PackResourceMetadata(Text.translatable("resourcePack.vanilla.description"), ResourceType.CLIENT_RESOURCES.getPackVersion(SharedConstants.getGameVersion()));
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Pattern SHA1_PATTERN = Pattern.compile("^[a-fA-F0-9]{40}$");
    private static final int MAX_FILE_SIZE = 0xFA00000;
    private static final int MAX_SAVED_PACKS = 10;
    private static final String VANILLA = "vanilla";
    private static final String SERVER = "server";
    private static final String PROGRAMER_ART_ID = "programer_art";
    private static final String PROGRAMMER_ART_NAME = "Programmer Art";
    private static final Text APPLYING_PACK_TEXT = Text.translatable("multiplayer.applyingPack");
    private final DefaultResourcePack pack;
    private final File serverPacksRoot;
    private final ReentrantLock lock = new ReentrantLock();
    private final ResourceIndex index;
    @Nullable
    private CompletableFuture<?> downloadTask;
    @Nullable
    private ResourcePackProfile serverContainer;

    public ClientBuiltinResourcePackProvider(File serverPacksRoot, ResourceIndex index) {
        this.serverPacksRoot = serverPacksRoot;
        this.index = index;
        this.pack = new DefaultClientResourcePack(DEFAULT_PACK_METADATA, index);
    }

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder, ResourcePackProfile.Factory factory) {
        ResourcePackProfile resourcePackProfile2;
        ResourcePackProfile resourcePackProfile = ResourcePackProfile.of(VANILLA, true, () -> this.pack, factory, ResourcePackProfile.InsertionPosition.BOTTOM, ResourcePackSource.PACK_SOURCE_BUILTIN);
        if (resourcePackProfile != null) {
            profileAdder.accept(resourcePackProfile);
        }
        if (this.serverContainer != null) {
            profileAdder.accept(this.serverContainer);
        }
        if ((resourcePackProfile2 = this.getProgrammerArtResourcePackProfile(factory)) != null) {
            profileAdder.accept(resourcePackProfile2);
        }
    }

    public DefaultResourcePack getPack() {
        return this.pack;
    }

    private static Map<String, String> getDownloadHeaders() {
        HashMap map = Maps.newHashMap();
        map.put("X-Minecraft-Username", MinecraftClient.getInstance().getSession().getUsername());
        map.put("X-Minecraft-UUID", MinecraftClient.getInstance().getSession().getUuid());
        map.put("X-Minecraft-Version", SharedConstants.getGameVersion().getName());
        map.put("X-Minecraft-Version-ID", SharedConstants.getGameVersion().getId());
        map.put("X-Minecraft-Pack-Format", String.valueOf(ResourceType.CLIENT_RESOURCES.getPackVersion(SharedConstants.getGameVersion())));
        map.put("User-Agent", "Minecraft Java/" + SharedConstants.getGameVersion().getName());
        return map;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CompletableFuture<?> download(URL url, String packSha1, boolean closeAfterDownload) {
        String string = Hashing.sha1().hashString((CharSequence)url.toString(), StandardCharsets.UTF_8).toString();
        String string2 = SHA1_PATTERN.matcher(packSha1).matches() ? packSha1 : "";
        this.lock.lock();
        try {
            CompletableFuture<String> completableFuture;
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            File file = new File(this.serverPacksRoot, string);
            if (file.exists()) {
                completableFuture = CompletableFuture.completedFuture("");
            } else {
                ProgressScreen progressScreen = new ProgressScreen(closeAfterDownload);
                Map<String, String> map = ClientBuiltinResourcePackProvider.getDownloadHeaders();
                minecraftClient.submitAndJoin(() -> minecraftClient.setScreen(progressScreen));
                completableFuture = NetworkUtils.downloadResourcePack(file, url, map, 0xFA00000, progressScreen, minecraftClient.getNetworkProxy());
            }
            CompletableFuture<?> completableFuture2 = this.downloadTask = ((CompletableFuture)((CompletableFuture)completableFuture.thenCompose(object -> {
                if (!this.verifyFile(string2, file)) {
                    return Util.completeExceptionally(new RuntimeException("Hash check failure for file " + file + ", see log"));
                }
                minecraftClient.execute(() -> {
                    if (!closeAfterDownload) {
                        minecraftClient.setScreen(new MessageScreen(APPLYING_PACK_TEXT));
                    }
                });
                return this.loadServerPack(file, ResourcePackSource.PACK_SOURCE_SERVER);
            })).exceptionallyCompose(throwable -> ((CompletableFuture)this.clear().thenAcceptAsync(void_ -> {
                LOGGER.warn("Pack application failed: {}, deleting file {}", (Object)throwable.getMessage(), (Object)file);
                ClientBuiltinResourcePackProvider.delete(file);
            }, (Executor)Util.getIoWorkerExecutor())).thenAcceptAsync(void_ -> minecraftClient.setScreen(new ConfirmScreen(confirmed -> {
                if (confirmed) {
                    minecraftClient.setScreen(null);
                } else {
                    ClientPlayNetworkHandler clientPlayNetworkHandler = minecraftClient.getNetworkHandler();
                    if (clientPlayNetworkHandler != null) {
                        clientPlayNetworkHandler.getConnection().disconnect(Text.translatable("connect.aborted"));
                    }
                }
            }, Text.translatable("multiplayer.texturePrompt.failure.line1"), Text.translatable("multiplayer.texturePrompt.failure.line2"), ScreenTexts.PROCEED, Text.translatable("menu.disconnect"))), (Executor)minecraftClient))).thenAcceptAsync(void_ -> this.deleteOldServerPack(), (Executor)Util.getIoWorkerExecutor());
            return completableFuture2;
        }
        finally {
            this.lock.unlock();
        }
    }

    private static void delete(File file) {
        try {
            Files.delete(file.toPath());
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to delete file {}: {}", (Object)file, (Object)iOException.getMessage());
        }
    }

    public CompletableFuture<Void> clear() {
        this.lock.lock();
        try {
            if (this.downloadTask != null) {
                this.downloadTask.cancel(true);
            }
            this.downloadTask = null;
            if (this.serverContainer != null) {
                this.serverContainer = null;
                CompletableFuture<Void> completableFuture = MinecraftClient.getInstance().reloadResourcesConcurrently();
                return completableFuture;
            }
        }
        finally {
            this.lock.unlock();
        }
        return CompletableFuture.completedFuture(null);
    }

    private boolean verifyFile(String expectedSha1, File file) {
        try {
            String string = com.google.common.io.Files.asByteSource((File)file).hash(Hashing.sha1()).toString();
            if (expectedSha1.isEmpty()) {
                LOGGER.info("Found file {} without verification hash", (Object)file);
                return true;
            }
            if (string.toLowerCase(Locale.ROOT).equals(expectedSha1.toLowerCase(Locale.ROOT))) {
                LOGGER.info("Found file {} matching requested hash {}", (Object)file, (Object)expectedSha1);
                return true;
            }
            LOGGER.warn("File {} had wrong hash (expected {}, found {}).", new Object[]{file, expectedSha1, string});
        }
        catch (IOException iOException) {
            LOGGER.warn("File {} couldn't be hashed.", (Object)file, (Object)iOException);
        }
        return false;
    }

    private void deleteOldServerPack() {
        if (!this.serverPacksRoot.isDirectory()) {
            return;
        }
        try {
            ArrayList list = Lists.newArrayList((Iterable)FileUtils.listFiles((File)this.serverPacksRoot, (IOFileFilter)TrueFileFilter.TRUE, null));
            list.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            int i = 0;
            for (File file : list) {
                if (i++ < 10) continue;
                LOGGER.info("Deleting old server resource pack {}", (Object)file.getName());
                FileUtils.deleteQuietly((File)file);
            }
        }
        catch (Exception exception) {
            LOGGER.error("Error while deleting old server resource pack : {}", (Object)exception.getMessage());
        }
    }

    public CompletableFuture<Void> loadServerPack(LevelStorage.Session session) {
        Path path = session.getDirectory(WorldSavePath.RESOURCES_ZIP);
        if (Files.exists(path, new LinkOption[0]) && !Files.isDirectory(path, new LinkOption[0])) {
            return this.loadServerPack(path.toFile(), ResourcePackSource.PACK_SOURCE_WORLD);
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> loadServerPack(File packZip, ResourcePackSource packSource) {
        PackResourceMetadata packResourceMetadata;
        try (ZipResourcePack zipResourcePack = new ZipResourcePack(packZip);){
            packResourceMetadata = zipResourcePack.parseMetadata(PackResourceMetadata.READER);
        }
        catch (IOException iOException) {
            return Util.completeExceptionally(new IOException(String.format(Locale.ROOT, "Invalid resourcepack at %s", packZip), iOException));
        }
        LOGGER.info("Applying server pack {}", (Object)packZip);
        this.serverContainer = new ResourcePackProfile(SERVER, true, () -> new ZipResourcePack(packZip), Text.translatable("resourcePack.server.name"), packResourceMetadata.getDescription(), ResourcePackCompatibility.from(packResourceMetadata, ResourceType.CLIENT_RESOURCES), ResourcePackProfile.InsertionPosition.TOP, true, packSource);
        return MinecraftClient.getInstance().reloadResourcesConcurrently();
    }

    @Nullable
    private ResourcePackProfile getProgrammerArtResourcePackProfile(ResourcePackProfile.Factory factory) {
        File file2;
        ResourcePackProfile resourcePackProfile = null;
        File file = this.index.getResource(new Identifier("resourcepacks/programmer_art.zip"));
        if (file != null && file.isFile()) {
            resourcePackProfile = ClientBuiltinResourcePackProvider.getProgrammerArtResourcePackProfile(factory, () -> ClientBuiltinResourcePackProvider.getProgrammerArtResourcePackFromZipFile(file));
        }
        if (resourcePackProfile == null && SharedConstants.isDevelopment && (file2 = this.index.findFile("../resourcepacks/programmer_art")) != null && file2.isDirectory()) {
            resourcePackProfile = ClientBuiltinResourcePackProvider.getProgrammerArtResourcePackProfile(factory, () -> ClientBuiltinResourcePackProvider.getProgrammerArtResourcePackFromDirectory(file2));
        }
        return resourcePackProfile;
    }

    @Nullable
    private static ResourcePackProfile getProgrammerArtResourcePackProfile(ResourcePackProfile.Factory factory, Supplier<ResourcePack> packSupplier) {
        return ResourcePackProfile.of(PROGRAMER_ART_ID, false, packSupplier, factory, ResourcePackProfile.InsertionPosition.TOP, ResourcePackSource.PACK_SOURCE_BUILTIN);
    }

    private static DirectoryResourcePack getProgrammerArtResourcePackFromDirectory(File packDirectory) {
        return new DirectoryResourcePack(packDirectory){

            @Override
            public String getName() {
                return ClientBuiltinResourcePackProvider.PROGRAMMER_ART_NAME;
            }
        };
    }

    private static ResourcePack getProgrammerArtResourcePackFromZipFile(File zipFile) {
        return new ZipResourcePack(zipFile){

            @Override
            public String getName() {
                return ClientBuiltinResourcePackProvider.PROGRAMMER_ART_NAME;
            }
        };
    }
}

