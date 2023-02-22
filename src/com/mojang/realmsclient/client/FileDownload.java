/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  com.google.common.io.Files
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
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsSharedConstants;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class FileDownload {
    private static final Logger LOGGER = LogManager.getLogger();
    private volatile boolean cancelled;
    private volatile boolean finished;
    private volatile boolean error;
    private volatile boolean extracting;
    private volatile File field_20490;
    private volatile File resourcePackPath;
    private volatile HttpGet field_20491;
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

    public void method_22100(WorldDownload worldDownload, String string, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus, RealmsAnvilLevelStorageSource realmsAnvilLevelStorageSource) {
        if (this.currentThread != null) {
            return;
        }
        this.currentThread = new Thread(() -> {
            CloseableHttpClient closeableHttpClient = null;
            try {
                this.field_20490 = File.createTempFile("backup", ".tar.gz");
                this.field_20491 = new HttpGet(worldDownload.downloadLink);
                closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
                CloseableHttpResponse httpResponse = closeableHttpClient.execute((HttpUriRequest)this.field_20491);
                downloadStatus.totalBytes = Long.parseLong(httpResponse.getFirstHeader("Content-Length").getValue());
                if (httpResponse.getStatusLine().getStatusCode() != 200) {
                    this.error = true;
                    this.field_20491.abort();
                    return;
                }
                FileOutputStream outputStream2 = new FileOutputStream(this.field_20490);
                ProgressListener progressListener = new ProgressListener(string.trim(), this.field_20490, realmsAnvilLevelStorageSource, downloadStatus, worldDownload);
                DownloadCountingOutputStream downloadCountingOutputStream2 = new DownloadCountingOutputStream(outputStream2);
                downloadCountingOutputStream2.setListener(progressListener);
                IOUtils.copy((InputStream)httpResponse.getEntity().getContent(), (OutputStream)((Object)downloadCountingOutputStream2));
                return;
            }
            catch (Exception exception2) {
                LOGGER.error("Caught exception while downloading: " + exception2.getMessage());
                this.error = true;
                return;
            }
            finally {
                block40: {
                    block41: {
                        CloseableHttpResponse httpResponse;
                        this.field_20491.releaseConnection();
                        if (this.field_20490 != null) {
                            this.field_20490.delete();
                        }
                        if (this.error) break block40;
                        if (worldDownload.resourcePackUrl.isEmpty() || worldDownload.resourcePackHash.isEmpty()) break block41;
                        try {
                            this.field_20490 = File.createTempFile("resources", ".tar.gz");
                            this.field_20491 = new HttpGet(worldDownload.resourcePackUrl);
                            httpResponse = closeableHttpClient.execute((HttpUriRequest)this.field_20491);
                            downloadStatus.totalBytes = Long.parseLong(httpResponse.getFirstHeader("Content-Length").getValue());
                            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                                this.error = true;
                                this.field_20491.abort();
                                return;
                            }
                        }
                        catch (Exception exception2) {
                            LOGGER.error("Caught exception while downloading: " + exception2.getMessage());
                            this.error = true;
                        }
                        FileOutputStream outputStream2 = new FileOutputStream(this.field_20490);
                        ResourcePackProgressListener resourcePackProgressListener2 = new ResourcePackProgressListener(this.field_20490, downloadStatus, worldDownload);
                        DownloadCountingOutputStream downloadCountingOutputStream2 = new DownloadCountingOutputStream(outputStream2);
                        downloadCountingOutputStream2.setListener(resourcePackProgressListener2);
                        IOUtils.copy((InputStream)httpResponse.getEntity().getContent(), (OutputStream)((Object)downloadCountingOutputStream2));
                        break block40;
                        finally {
                            this.field_20491.releaseConnection();
                            if (this.field_20490 != null) {
                                this.field_20490.delete();
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
        if (this.field_20491 != null) {
            this.field_20491.abort();
        }
        if (this.field_20490 != null) {
            this.field_20490.delete();
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
        folder = folder.replaceAll("[\\./\"]", "_");
        for (String string : INVALID_FILE_NAMES) {
            if (!folder.equalsIgnoreCase(string)) continue;
            folder = "_" + folder + "_";
        }
        return folder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void untarGzipArchive(String name, File file, RealmsAnvilLevelStorageSource levelStorageSource) throws IOException {
        String string;
        Pattern pattern = Pattern.compile(".*-([0-9]+)$");
        int i = 1;
        for (char c : RealmsSharedConstants.ILLEGAL_FILE_CHARACTERS) {
            name = name.replace(c, '_');
        }
        if (StringUtils.isEmpty((CharSequence)name)) {
            name = "Realm";
        }
        name = FileDownload.findAvailableFolderName(name);
        try {
            Object object = levelStorageSource.getLevelList().iterator();
            while (object.hasNext()) {
                RealmsLevelSummary realmsLevelSummary = (RealmsLevelSummary)object.next();
                if (!realmsLevelSummary.getLevelId().toLowerCase(Locale.ROOT).startsWith(name.toLowerCase(Locale.ROOT))) continue;
                Matcher matcher = pattern.matcher(realmsLevelSummary.getLevelId());
                if (matcher.matches()) {
                    if (Integer.valueOf(matcher.group(1)) <= i) continue;
                    i = Integer.valueOf(matcher.group(1));
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
        if (!levelStorageSource.isNewLevelIdAcceptable(name) || i > 1) {
            string = name + (i == 1 ? "" : "-" + i);
            if (!levelStorageSource.isNewLevelIdAcceptable(string)) {
                boolean bl = false;
                while (!bl) {
                    string = name + (++i == 1 ? "" : "-" + i);
                    if (!levelStorageSource.isNewLevelIdAcceptable(string)) continue;
                    bl = true;
                }
            }
        } else {
            string = name;
        }
        TarArchiveInputStream tarArchiveInputStream = null;
        File file2 = new File(Realms.getGameDirectoryPath(), "saves");
        try {
            file2.mkdir();
            tarArchiveInputStream = new TarArchiveInputStream((InputStream)new GzipCompressorInputStream((InputStream)new BufferedInputStream(new FileInputStream(file))));
            TarArchiveEntry tarArchiveEntry = tarArchiveInputStream.getNextTarEntry();
            while (tarArchiveEntry != null) {
                File file3 = new File(file2, tarArchiveEntry.getName().replace("world", string));
                if (tarArchiveEntry.isDirectory()) {
                    file3.mkdirs();
                } else {
                    file3.createNewFile();
                    byte[] bs = new byte[1024];
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file3));
                    int j = 0;
                    while ((j = tarArchiveInputStream.read(bs)) != -1) {
                        bufferedOutputStream.write(bs, 0, j);
                    }
                    bufferedOutputStream.close();
                    bs = null;
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
            if (file != null) {
                file.delete();
            }
            RealmsAnvilLevelStorageSource realmsAnvilLevelStorageSource = levelStorageSource;
            realmsAnvilLevelStorageSource.renameLevel(string, string.trim());
            File file3 = new File(file2, string + File.separator + "level.dat");
            Realms.deletePlayerTag(file3);
            this.resourcePackPath = new File(file2, string + File.separator + "resources.zip");
        }
    }

    @Environment(value=EnvType.CLIENT)
    class DownloadCountingOutputStream
    extends CountingOutputStream {
        private ActionListener listener;

        public DownloadCountingOutputStream(OutputStream out) {
            super(out);
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
    class ResourcePackProgressListener
    implements ActionListener {
        private final File tempFile;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
        private final WorldDownload worldDownload;

        private ResourcePackProgressListener(File tempFile, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus, WorldDownload worldDownload) {
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
                        LOGGER.error("Resourcepack had wrong hash (expected " + this.worldDownload.resourcePackHash + ", found " + string + "). Deleting it.");
                        FileUtils.deleteQuietly((File)this.tempFile);
                        FileDownload.this.error = true;
                    }
                }
                catch (IOException iOException) {
                    LOGGER.error("Error copying resourcepack file", (Object)iOException.getMessage());
                    FileDownload.this.error = true;
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ProgressListener
    implements ActionListener {
        private final String worldName;
        private final File tempFile;
        private final RealmsAnvilLevelStorageSource levelStorageSource;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
        private final WorldDownload worldDownload;

        private ProgressListener(String worldName, File tempFile, RealmsAnvilLevelStorageSource levelStorageSource, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus, WorldDownload worldDownload) {
            this.worldName = worldName;
            this.tempFile = tempFile;
            this.levelStorageSource = levelStorageSource;
            this.downloadStatus = downloadStatus;
            this.worldDownload = worldDownload;
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

