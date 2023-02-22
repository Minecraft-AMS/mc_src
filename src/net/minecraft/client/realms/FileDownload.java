/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  com.google.common.io.Files
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.compress.archivers.tar.TarArchiveEntry
 *  org.apache.commons.compress.archivers.tar.TarArchiveInputStream
 *  org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.output.CountingOutputStream
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.logging.LogUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.exception.RealmsDefaultUncaughtExceptionHandler;
import net.minecraft.client.realms.gui.screen.RealmsDownloadLatestWorldScreen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class FileDownload {
    static final Logger LOGGER = LogUtils.getLogger();
    volatile boolean cancelled;
    volatile boolean finished;
    volatile boolean error;
    volatile boolean extracting;
    @Nullable
    private volatile File backupFile;
    volatile File resourcePackPath;
    @Nullable
    private volatile HttpGet httpRequest;
    @Nullable
    private Thread currentThread;
    private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
    private static final String[] INVALID_FILE_NAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long contentLength(String downloadLink) {
        CloseableHttpClient closeableHttpClient = null;
        HttpGet httpGet = null;
        try {
            httpGet = new HttpGet(downloadLink);
            closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute((HttpUriRequest)httpGet);
            long l = Long.parseLong(closeableHttpResponse.getFirstHeader("Content-Length").getValue());
            return l;
        }
        catch (Throwable throwable) {
            LOGGER.error("Unable to get content length for download");
            long l = 0L;
            return l;
        }
        finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
            if (closeableHttpClient != null) {
                try {
                    closeableHttpClient.close();
                }
                catch (IOException iOException) {
                    LOGGER.error("Could not close http client", (Throwable)iOException);
                }
            }
        }
    }

    public void downloadWorld(WorldDownload download, String message, RealmsDownloadLatestWorldScreen.DownloadStatus status, LevelStorage storage) {
        if (this.currentThread != null) {
            return;
        }
        this.currentThread = new Thread(() -> {
            CloseableHttpClient closeableHttpClient = null;
            try {
                this.backupFile = File.createTempFile("backup", ".tar.gz");
                this.httpRequest = new HttpGet(worldDownload.downloadLink);
                closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
                CloseableHttpResponse httpResponse = closeableHttpClient.execute((HttpUriRequest)this.httpRequest);
                downloadStatus.totalBytes = Long.parseLong(httpResponse.getFirstHeader("Content-Length").getValue());
                if (httpResponse.getStatusLine().getStatusCode() != 200) {
                    this.error = true;
                    this.httpRequest.abort();
                    return;
                }
                FileOutputStream outputStream2 = new FileOutputStream(this.backupFile);
                ProgressListener progressListener = new ProgressListener(message.trim(), this.backupFile, storage, status);
                DownloadCountingOutputStream downloadCountingOutputStream2 = new DownloadCountingOutputStream(outputStream2);
                downloadCountingOutputStream2.setListener(progressListener);
                IOUtils.copy((InputStream)httpResponse.getEntity().getContent(), (OutputStream)((Object)downloadCountingOutputStream2));
                return;
            }
            catch (Exception exception2) {
                LOGGER.error("Caught exception while downloading: {}", (Object)exception2.getMessage());
                this.error = true;
                return;
            }
            finally {
                block40: {
                    block41: {
                        CloseableHttpResponse httpResponse;
                        this.httpRequest.releaseConnection();
                        if (this.backupFile != null) {
                            this.backupFile.delete();
                        }
                        if (this.error) break block40;
                        if (worldDownload.resourcePackUrl.isEmpty() || worldDownload.resourcePackHash.isEmpty()) break block41;
                        try {
                            this.backupFile = File.createTempFile("resources", ".tar.gz");
                            this.httpRequest = new HttpGet(worldDownload.resourcePackUrl);
                            httpResponse = closeableHttpClient.execute((HttpUriRequest)this.httpRequest);
                            downloadStatus.totalBytes = Long.parseLong(httpResponse.getFirstHeader("Content-Length").getValue());
                            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                                this.error = true;
                                this.httpRequest.abort();
                                return;
                            }
                        }
                        catch (Exception exception2) {
                            LOGGER.error("Caught exception while downloading: {}", (Object)exception2.getMessage());
                            this.error = true;
                        }
                        FileOutputStream outputStream2 = new FileOutputStream(this.backupFile);
                        ResourcePackProgressListener resourcePackProgressListener2 = new ResourcePackProgressListener(this.backupFile, status, download);
                        DownloadCountingOutputStream downloadCountingOutputStream2 = new DownloadCountingOutputStream(outputStream2);
                        downloadCountingOutputStream2.setListener(resourcePackProgressListener2);
                        IOUtils.copy((InputStream)httpResponse.getEntity().getContent(), (OutputStream)((Object)downloadCountingOutputStream2));
                        break block40;
                        finally {
                            this.httpRequest.releaseConnection();
                            if (this.backupFile != null) {
                                this.backupFile.delete();
                            }
                        }
                    }
                    this.finished = true;
                }
                if (closeableHttpClient != null) {
                    try {
                        closeableHttpClient.close();
                    }
                    catch (IOException iOException2) {
                        LOGGER.error("Failed to close Realms download client");
                    }
                }
            }
        });
        this.currentThread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
        this.currentThread.start();
    }

    public void cancel() {
        if (this.httpRequest != null) {
            this.httpRequest.abort();
        }
        if (this.backupFile != null) {
            this.backupFile.delete();
        }
        this.cancelled = true;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean isError() {
        return this.error;
    }

    public boolean isExtracting() {
        return this.extracting;
    }

    public static String findAvailableFolderName(String folder) {
        folder = ((String)folder).replaceAll("[\\./\"]", "_");
        for (String string : INVALID_FILE_NAMES) {
            if (!((String)folder).equalsIgnoreCase(string)) continue;
            folder = "_" + (String)folder + "_";
        }
        return folder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void untarGzipArchive(String name, @Nullable File archive, LevelStorage storage) throws IOException {
        Object string2;
        Pattern pattern = Pattern.compile(".*-([0-9]+)$");
        int i = 1;
        for (char c : SharedConstants.INVALID_CHARS_LEVEL_NAME) {
            name = name.replace(c, '_');
        }
        if (StringUtils.isEmpty((CharSequence)name)) {
            name = "Realm";
        }
        name = FileDownload.findAvailableFolderName(name);
        try {
            Object object = storage.getLevelList().iterator();
            while (object.hasNext()) {
                LevelStorage.LevelSave levelSave = (LevelStorage.LevelSave)object.next();
                String string = levelSave.getRootPath();
                if (!string.toLowerCase(Locale.ROOT).startsWith(name.toLowerCase(Locale.ROOT))) continue;
                Matcher matcher = pattern.matcher(string);
                if (matcher.matches()) {
                    int j = Integer.parseInt(matcher.group(1));
                    if (j <= i) continue;
                    i = j;
                    continue;
                }
                ++i;
            }
        }
        catch (Exception exception) {
            LOGGER.error("Error getting level list", (Throwable)exception);
            this.error = true;
            return;
        }
        if (!storage.isLevelNameValid(name) || i > 1) {
            string2 = name + (String)(i == 1 ? "" : "-" + i);
            if (!storage.isLevelNameValid((String)string2)) {
                boolean bl = false;
                while (!bl) {
                    if (!storage.isLevelNameValid((String)(string2 = name + (String)(++i == 1 ? "" : "-" + i)))) continue;
                    bl = true;
                }
            }
        } else {
            string2 = name;
        }
        TarArchiveInputStream tarArchiveInputStream = null;
        File file = new File(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), "saves");
        try {
            file.mkdir();
            tarArchiveInputStream = new TarArchiveInputStream((InputStream)new GzipCompressorInputStream((InputStream)new BufferedInputStream(new FileInputStream(archive))));
            TarArchiveEntry tarArchiveEntry = tarArchiveInputStream.getNextTarEntry();
            while (tarArchiveEntry != null) {
                File file2 = new File(file, tarArchiveEntry.getName().replace("world", (CharSequence)string2));
                if (tarArchiveEntry.isDirectory()) {
                    file2.mkdirs();
                } else {
                    file2.createNewFile();
                    try (FileOutputStream fileOutputStream = new FileOutputStream(file2);){
                        IOUtils.copy((InputStream)tarArchiveInputStream, (OutputStream)fileOutputStream);
                    }
                }
                tarArchiveEntry = tarArchiveInputStream.getNextTarEntry();
            }
        }
        catch (Exception exception2) {
            LOGGER.error("Error extracting world", (Throwable)exception2);
            this.error = true;
        }
        finally {
            if (tarArchiveInputStream != null) {
                tarArchiveInputStream.close();
            }
            if (archive != null) {
                archive.delete();
            }
            try (LevelStorage.Session session = storage.createSession((String)string2);){
                session.save(((String)string2).trim());
                Path path = session.getDirectory(WorldSavePath.LEVEL_DAT);
                FileDownload.readNbtFile(path.toFile());
            }
            catch (IOException iOException) {
                LOGGER.error("Failed to rename unpacked realms level {}", string2, (Object)iOException);
            }
            this.resourcePackPath = new File(file, (String)string2 + File.separator + "resources.zip");
        }
    }

    private static void readNbtFile(File file) {
        if (file.exists()) {
            try {
                NbtCompound nbtCompound = NbtIo.readCompressed(file);
                NbtCompound nbtCompound2 = nbtCompound.getCompound("Data");
                nbtCompound2.remove("Player");
                NbtIo.writeCompressed(nbtCompound, file);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ResourcePackProgressListener
    implements ActionListener {
        private final File tempFile;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
        private final WorldDownload worldDownload;

        ResourcePackProgressListener(File tempFile, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus, WorldDownload worldDownload) {
            this.tempFile = tempFile;
            this.downloadStatus = downloadStatus;
            this.worldDownload = worldDownload;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)((Object)e.getSource())).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled) {
                try {
                    String string = Hashing.sha1().hashBytes(Files.toByteArray((File)this.tempFile)).toString();
                    if (string.equals(this.worldDownload.resourcePackHash)) {
                        FileUtils.copyFile((File)this.tempFile, (File)FileDownload.this.resourcePackPath);
                        FileDownload.this.finished = true;
                    } else {
                        LOGGER.error("Resourcepack had wrong hash (expected {}, found {}). Deleting it.", (Object)this.worldDownload.resourcePackHash, (Object)string);
                        FileUtils.deleteQuietly((File)this.tempFile);
                        FileDownload.this.error = true;
                    }
                }
                catch (IOException iOException) {
                    LOGGER.error("Error copying resourcepack file: {}", (Object)iOException.getMessage());
                    FileDownload.this.error = true;
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class DownloadCountingOutputStream
    extends CountingOutputStream {
        @Nullable
        private ActionListener listener;

        public DownloadCountingOutputStream(OutputStream stream) {
            super(stream);
        }

        public void setListener(ActionListener listener) {
            this.listener = listener;
        }

        protected void afterWrite(int n) throws IOException {
            super.afterWrite(n);
            if (this.listener != null) {
                this.listener.actionPerformed(new ActionEvent((Object)this, 0, null));
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ProgressListener
    implements ActionListener {
        private final String worldName;
        private final File tempFile;
        private final LevelStorage levelStorageSource;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;

        ProgressListener(String worldName, File tempFile, LevelStorage levelStorageSource, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus) {
            this.worldName = worldName;
            this.tempFile = tempFile;
            this.levelStorageSource = levelStorageSource;
            this.downloadStatus = downloadStatus;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)((Object)e.getSource())).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled && !FileDownload.this.error) {
                try {
                    FileDownload.this.extracting = true;
                    FileDownload.this.untarGzipArchive(this.worldName, this.tempFile, this.levelStorageSource);
                }
                catch (IOException iOException) {
                    LOGGER.error("Error extracting archive", (Throwable)iOException);
                    FileDownload.this.error = true;
                }
            }
        }
    }
}

