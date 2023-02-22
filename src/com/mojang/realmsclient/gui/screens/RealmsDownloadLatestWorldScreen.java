/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.RateLimiter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.FileDownload;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsDownloadLatestWorldScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final RealmsScreen lastScreen;
    private final WorldDownload worldDownload;
    private final String downloadTitle;
    private final RateLimiter narrationRateLimiter;
    private RealmsButton cancelButton;
    private final String worldName;
    private final DownloadStatus downloadStatus;
    private volatile String field_20494;
    private volatile String status;
    private volatile String progress;
    private volatile boolean cancelled;
    private volatile boolean showDots = true;
    private volatile boolean finished;
    private volatile boolean extracting;
    private Long previousWrittenBytes;
    private Long previousTimeSnapshot;
    private long bytesPersSecond;
    private int animTick;
    private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
    private int dotIndex;
    private final int WARNING_ID = 100;
    private int confirmationId = -1;
    private boolean checked;
    private static final ReentrantLock downloadLock = new ReentrantLock();

    public RealmsDownloadLatestWorldScreen(RealmsScreen lastScreen, WorldDownload worldDownload, String worldName) {
        this.lastScreen = lastScreen;
        this.worldName = worldName;
        this.worldDownload = worldDownload;
        this.downloadStatus = new DownloadStatus();
        this.downloadTitle = RealmsDownloadLatestWorldScreen.getLocalizedString("mco.download.title");
        this.narrationRateLimiter = RateLimiter.create((double)0.1f);
    }

    public void setConfirmationId(int confirmationId) {
        this.confirmationId = confirmationId;
    }

    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.cancelButton = new RealmsButton(0, this.width() / 2 - 100, this.height() - 42, 200, 20, RealmsDownloadLatestWorldScreen.getLocalizedString("gui.cancel")){

            @Override
            public void onPress() {
                RealmsDownloadLatestWorldScreen.this.cancelled = true;
                RealmsDownloadLatestWorldScreen.this.backButtonClicked();
            }
        };
        this.buttonsAdd(this.cancelButton);
        this.checkDownloadSize();
    }

    private void checkDownloadSize() {
        if (this.finished) {
            return;
        }
        if (!this.checked && this.getContentLength(this.worldDownload.downloadLink) >= 0x140000000L) {
            String string = RealmsDownloadLatestWorldScreen.getLocalizedString("mco.download.confirmation.line1", RealmsDownloadLatestWorldScreen.humanReadableSize(0x140000000L));
            String string2 = RealmsDownloadLatestWorldScreen.getLocalizedString("mco.download.confirmation.line2");
            Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Warning, string, string2, false, 100));
        } else {
            this.downloadSave();
        }
    }

    @Override
    public void confirmResult(boolean result, int id) {
        this.checked = true;
        Realms.setScreen(this);
        this.downloadSave();
    }

    private long getContentLength(String downloadLink) {
        FileDownload fileDownload = new FileDownload();
        return fileDownload.contentLength(downloadLink);
    }

    @Override
    public void tick() {
        super.tick();
        ++this.animTick;
        if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
            ArrayList list = Lists.newArrayList();
            list.add(this.downloadTitle);
            list.add(this.status);
            if (this.progress != null) {
                list.add(this.progress + "%");
                list.add(RealmsDownloadLatestWorldScreen.humanReadableSpeed(this.bytesPersSecond));
            }
            if (this.field_20494 != null) {
                list.add(this.field_20494);
            }
            String string = String.join((CharSequence)System.lineSeparator(), list);
            Realms.narrateNow(string);
        }
    }

    @Override
    public boolean keyPressed(int eventKey, int scancode, int mods) {
        if (eventKey == 256) {
            this.cancelled = true;
            this.backButtonClicked();
            return true;
        }
        return super.keyPressed(eventKey, scancode, mods);
    }

    private void backButtonClicked() {
        if (this.finished && this.confirmationId != -1 && this.field_20494 == null) {
            this.lastScreen.confirmResult(true, this.confirmationId);
        }
        Realms.setScreen(this.lastScreen);
    }

    @Override
    public void render(int xm, int ym, float a) {
        this.renderBackground();
        if (this.extracting && !this.finished) {
            this.status = RealmsDownloadLatestWorldScreen.getLocalizedString("mco.download.extracting");
        }
        this.drawCenteredString(this.downloadTitle, this.width() / 2, 20, 0xFFFFFF);
        this.drawCenteredString(this.status, this.width() / 2, 50, 0xFFFFFF);
        if (this.showDots) {
            this.drawDots();
        }
        if (this.downloadStatus.bytesWritten != 0L && !this.cancelled) {
            this.drawProgressBar();
            this.drawDownloadSpeed();
        }
        if (this.field_20494 != null) {
            this.drawCenteredString(this.field_20494, this.width() / 2, 110, 0xFF0000);
        }
        super.render(xm, ym, a);
    }

    private void drawDots() {
        int i = this.fontWidth(this.status);
        if (this.animTick % 10 == 0) {
            ++this.dotIndex;
        }
        this.drawString(DOTS[this.dotIndex % DOTS.length], this.width() / 2 + i / 2 + 5, 50, 0xFFFFFF);
    }

    private void drawProgressBar() {
        double d = this.downloadStatus.bytesWritten.doubleValue() / this.downloadStatus.totalBytes.doubleValue() * 100.0;
        this.progress = String.format(Locale.ROOT, "%.1f", d);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableTexture();
        Tezzelator tezzelator = Tezzelator.instance;
        tezzelator.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
        double e = this.width() / 2 - 100;
        double f = 0.5;
        tezzelator.vertex(e - 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator.vertex(e + 200.0 * d / 100.0 + 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator.vertex(e + 200.0 * d / 100.0 + 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator.vertex(e - 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator.vertex(e, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator.vertex(e + 200.0 * d / 100.0, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator.vertex(e + 200.0 * d / 100.0, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator.vertex(e, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator.end();
        RenderSystem.enableTexture();
        this.drawCenteredString(this.progress + " %", this.width() / 2, 84, 0xFFFFFF);
    }

    private void drawDownloadSpeed() {
        if (this.animTick % 20 == 0) {
            if (this.previousWrittenBytes != null) {
                long l = System.currentTimeMillis() - this.previousTimeSnapshot;
                if (l == 0L) {
                    l = 1L;
                }
                this.bytesPersSecond = 1000L * (this.downloadStatus.bytesWritten - this.previousWrittenBytes) / l;
                this.drawDownloadSpeed0(this.bytesPersSecond);
            }
            this.previousWrittenBytes = this.downloadStatus.bytesWritten;
            this.previousTimeSnapshot = System.currentTimeMillis();
        } else {
            this.drawDownloadSpeed0(this.bytesPersSecond);
        }
    }

    private void drawDownloadSpeed0(long bytesPersSecond) {
        if (bytesPersSecond > 0L) {
            int i = this.fontWidth(this.progress);
            String string = "(" + RealmsDownloadLatestWorldScreen.humanReadableSpeed(bytesPersSecond) + ")";
            this.drawString(string, this.width() / 2 + i / 2 + 15, 84, 0xFFFFFF);
        }
    }

    public static String humanReadableSpeed(long bytes) {
        int i = 1024;
        if (bytes < 1024L) {
            return bytes + " B/s";
        }
        int j = (int)(Math.log(bytes) / Math.log(1024.0));
        String string = "KMGTPE".charAt(j - 1) + "";
        return String.format(Locale.ROOT, "%.1f %sB/s", (double)bytes / Math.pow(1024.0, j), string);
    }

    public static String humanReadableSize(long bytes) {
        int i = 1024;
        if (bytes < 1024L) {
            return bytes + " B";
        }
        int j = (int)(Math.log(bytes) / Math.log(1024.0));
        String string = "KMGTPE".charAt(j - 1) + "";
        return String.format(Locale.ROOT, "%.0f %sB", (double)bytes / Math.pow(1024.0, j), string);
    }

    private void downloadSave() {
        new Thread(() -> {
            try {
                if (!downloadLock.tryLock(1L, TimeUnit.SECONDS)) {
                    return;
                }
                this.status = RealmsDownloadLatestWorldScreen.getLocalizedString("mco.download.preparing");
                if (this.cancelled) {
                    this.downloadCancelled();
                    return;
                }
                this.status = RealmsDownloadLatestWorldScreen.getLocalizedString("mco.download.downloading", this.worldName);
                FileDownload fileDownload = new FileDownload();
                fileDownload.contentLength(this.worldDownload.downloadLink);
                fileDownload.method_22100(this.worldDownload, this.worldName, this.downloadStatus, this.getLevelStorageSource());
                while (!fileDownload.isFinished()) {
                    if (fileDownload.isError()) {
                        fileDownload.cancel();
                        this.field_20494 = RealmsDownloadLatestWorldScreen.getLocalizedString("mco.download.failed");
                        this.cancelButton.setMessage(RealmsDownloadLatestWorldScreen.getLocalizedString("gui.done"));
                        return;
                    }
                    if (fileDownload.isExtracting()) {
                        this.extracting = true;
                    }
                    if (this.cancelled) {
                        fileDownload.cancel();
                        this.downloadCancelled();
                        return;
                    }
                    try {
                        Thread.sleep(500L);
                    }
                    catch (InterruptedException interruptedException) {
                        LOGGER.error("Failed to check Realms backup download status");
                    }
                }
                this.finished = true;
                this.status = RealmsDownloadLatestWorldScreen.getLocalizedString("mco.download.done");
                this.cancelButton.setMessage(RealmsDownloadLatestWorldScreen.getLocalizedString("gui.done"));
            }
            catch (InterruptedException interruptedException2) {
                LOGGER.error("Could not acquire upload lock");
            }
            catch (Exception exception) {
                this.field_20494 = RealmsDownloadLatestWorldScreen.getLocalizedString("mco.download.failed");
                exception.printStackTrace();
            }
            finally {
                if (!downloadLock.isHeldByCurrentThread()) {
                    return;
                }
                downloadLock.unlock();
                this.showDots = false;
                this.finished = true;
            }
        }).start();
    }

    private void downloadCancelled() {
        this.status = RealmsDownloadLatestWorldScreen.getLocalizedString("mco.download.cancelled");
    }

    @Environment(value=EnvType.CLIENT)
    public class DownloadStatus {
        public volatile Long bytesWritten = 0L;
        public volatile Long totalBytes = 0L;
    }
}

