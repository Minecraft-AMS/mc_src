/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.comparator.LastModifiedFileComparator
 *  org.apache.commons.io.filefilter.IOFileFilter
 *  org.apache.commons.io.filefilter.TrueFileFilter
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.client.resource.DefaultClientResourcePack;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ClientBuiltinResourcePackProvider
implements ResourcePackProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern ALPHANUMERAL = Pattern.compile("^[a-fA-F0-9]{40}$");
    private final DefaultResourcePack pack;
    private final File serverPacksRoot;
    private final ReentrantLock lock = new ReentrantLock();
    private final ResourceIndex index;
    @Nullable
    private CompletableFuture<?> downloadTask;
    @Nullable
    private ClientResourcePackProfile serverContainer;

    public ClientBuiltinResourcePackProvider(File serverPacksRoot, ResourceIndex index) {
        this.serverPacksRoot = serverPacksRoot;
        this.index = index;
        this.pack = new DefaultClientResourcePack(index);
    }

    @Override
    public <T extends ResourcePackProfile> void register(Map<String, T> registry, ResourcePackProfile.Factory<T> factory) {
        T resourcePackProfile2;
        File file;
        T resourcePackProfile = ResourcePackProfile.of("vanilla", true, () -> this.pack, factory, ResourcePackProfile.InsertionPosition.BOTTOM);
        if (resourcePackProfile != null) {
            registry.put("vanilla", resourcePackProfile);
        }
        if (this.serverContainer != null) {
            registry.put("server", this.serverContainer);
        }
        if ((file = this.index.getResource(new Identifier("resourcepacks/programmer_art.zip"))) != null && file.isFile() && (resourcePackProfile2 = ResourcePackProfile.of("programer_art", false, () -> new ZipResourcePack(file){

            @Override
            public String getName() {
                return "Programmer Art";
            }
        }, factory, ResourcePackProfile.InsertionPosition.TOP)) != null) {
            registry.put("programer_art", resourcePackProfile2);
        }
    }

    public DefaultResourcePack getPack() {
        return this.pack;
    }

    public static Map<String, String> getDownloadHeaders() {
        HashMap map = Maps.newHashMap();
        map.put("X-Minecraft-Username", MinecraftClient.getInstance().getSession().getUsername());
        map.put("X-Minecraft-UUID", MinecraftClient.getInstance().getSession().getUuid());
        map.put("X-Minecraft-Version", SharedConstants.getGameVersion().getName());
        map.put("X-Minecraft-Version-ID", SharedConstants.getGameVersion().getId());
        map.put("X-Minecraft-Pack-Format", String.valueOf(SharedConstants.getGameVersion().getPackVersion()));
        map.put("User-Agent", "Minecraft Java/" + SharedConstants.getGameVersion().getName());
        return map;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CompletableFuture<?> download(String string, String string2) {
        String string3 = DigestUtils.sha1Hex((String)string);
        String string4 = ALPHANUMERAL.matcher(string2).matches() ? string2 : "";
        this.lock.lock();
        try {
            CompletableFuture<String> completableFuture;
            this.clear();
            this.deleteOldServerPack();
            File file = new File(this.serverPacksRoot, string3);
            if (file.exists()) {
                completableFuture = CompletableFuture.completedFuture("");
            } else {
                ProgressScreen progressScreen = new ProgressScreen();
                Map<String, String> map = ClientBuiltinResourcePackProvider.getDownloadHeaders();
                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                minecraftClient.submitAndJoin(() -> minecraftClient.openScreen(progressScreen));
                completableFuture = NetworkUtils.download(file, string, map, 0x6400000, progressScreen, minecraftClient.getNetworkProxy());
            }
            CompletableFuture<?> completableFuture2 = this.downloadTask = ((CompletableFuture)completableFuture.thenCompose(object -> {
                if (!this.verifyFile(string4, file)) {
                    return Util.completeExceptionally(new RuntimeException("Hash check failure for file " + file + ", see log"));
                }
                return this.loadServerPack(file);
            })).whenComplete((void_, throwable) -> {
                if (throwable != null) {
                    LOGGER.warn("Pack application failed: {}, deleting file {}", (Object)throwable.getMessage(), (Object)file);
                    ClientBuiltinResourcePackProvider.delete(file);
                }
            });
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

    public void clear() {
        this.lock.lock();
        try {
            if (this.downloadTask != null) {
                this.downloadTask.cancel(true);
            }
            this.downloadTask = null;
            if (this.serverContainer != null) {
                this.serverContainer = null;
                MinecraftClient.getInstance().reloadResourcesConcurrently();
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    private boolean verifyFile(String expectedSha1, File rfile) {
        try {
            String string;
            try (FileInputStream fileInputStream = new FileInputStream(rfile);){
                string = DigestUtils.sha1Hex((InputStream)fileInputStream);
            }
            if (expectedSha1.isEmpty()) {
                LOGGER.info("Found file {} without verification hash", (Object)rfile);
                return true;
            }
            if (string.toLowerCase(Locale.ROOT).equals(expectedSha1.toLowerCase(Locale.ROOT))) {
                LOGGER.info("Found file {} matching requested hash {}", (Object)rfile, (Object)expectedSha1);
                return true;
            }
            LOGGER.warn("File {} had wrong hash (expected {}, found {}).", (Object)rfile, (Object)expectedSha1, (Object)string);
        }
        catch (IOException iOException) {
            LOGGER.warn("File {} couldn't be hashed.", (Object)rfile, (Object)iOException);
        }
        return false;
    }

    private void deleteOldServerPack() {
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
        catch (IllegalArgumentException illegalArgumentException) {
            LOGGER.error("Error while deleting old server resource pack : {}", (Object)illegalArgumentException.getMessage());
        }
    }

    public CompletableFuture<Void> loadServerPack(File packZip) {
        PackResourceMetadata packResourceMetadata = null;
        NativeImage nativeImage = null;
        String string = null;
        try (ZipResourcePack zipResourcePack = new ZipResourcePack(packZip);){
            packResourceMetadata = zipResourcePack.parseMetadata(PackResourceMetadata.READER);
            try (InputStream inputStream = zipResourcePack.openRoot("pack.png");){
                nativeImage = NativeImage.read(inputStream);
            }
            catch (IOException | IllegalArgumentException exception) {
                LOGGER.info("Could not read pack.png: {}", (Object)exception.getMessage());
            }
        }
        catch (IOException iOException) {
            string = iOException.getMessage();
        }
        if (string != null) {
            return Util.completeExceptionally(new RuntimeException(String.format("Invalid resourcepack at %s: %s", packZip, string)));
        }
        LOGGER.info("Applying server pack {}", (Object)packZip);
        this.serverContainer = new ClientResourcePackProfile("server", true, () -> new ZipResourcePack(packZip), new TranslatableText("resourcePack.server.name", new Object[0]), packResourceMetadata.getDescription(), ResourcePackCompatibility.from(packResourceMetadata.getPackFormat()), ResourcePackProfile.InsertionPosition.TOP, true, nativeImage);
        return MinecraftClient.getInstance().reloadResourcesConcurrently();
    }
}

