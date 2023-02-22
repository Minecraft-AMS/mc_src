/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.RateLimiter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.compress.archivers.ArchiveEntry
 *  org.apache.commons.compress.archivers.tar.TarArchiveEntry
 *  org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
 *  org.apache.commons.compress.utils.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.FileUpload;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.SizeUnit;
import net.minecraft.client.realms.UploadStatus;
import net.minecraft.client.realms.dto.UploadInfo;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsResetWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.util.UploadTokenCache;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsUploadScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ReentrantLock UPLOAD_LOCK = new ReentrantLock();
    private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
    private static final Text field_26526 = new TranslatableText("mco.upload.verifying");
    private final RealmsResetWorldScreen parent;
    private final LevelSummary selectedLevel;
    private final long worldId;
    private final int slotId;
    private final UploadStatus uploadStatus;
    private final RateLimiter narrationRateLimiter;
    private volatile Text[] field_20503;
    private volatile Text status = new TranslatableText("mco.upload.preparing");
    private volatile String progress;
    private volatile boolean cancelled;
    private volatile boolean uploadFinished;
    private volatile boolean showDots = true;
    private volatile boolean uploadStarted;
    private ButtonWidget backButton;
    private ButtonWidget cancelButton;
    private int animTick;
    private Long previousWrittenBytes;
    private Long previousTimeSnapshot;
    private long bytesPerSecond;
    private final Runnable field_22728;

    public RealmsUploadScreen(long worldId, int slotId, RealmsResetWorldScreen parent, LevelSummary levelSummary, Runnable runnable) {
        this.worldId = worldId;
        this.slotId = slotId;
        this.parent = parent;
        this.selectedLevel = levelSummary;
        this.uploadStatus = new UploadStatus();
        this.narrationRateLimiter = RateLimiter.create((double)0.1f);
        this.field_22728 = runnable;
    }

    @Override
    public void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.backButton = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 42, 200, 20, ScreenTexts.BACK, buttonWidget -> this.onBack()));
        this.backButton.visible = false;
        this.cancelButton = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 42, 200, 20, ScreenTexts.CANCEL, buttonWidget -> this.onCancel()));
        if (!this.uploadStarted) {
            if (this.parent.slot == -1) {
                this.upload();
            } else {
                this.parent.switchSlot(() -> {
                    if (!this.uploadStarted) {
                        this.uploadStarted = true;
                        this.client.openScreen(this);
                        this.upload();
                    }
                });
            }
        }
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
    }

    private void onBack() {
        this.field_22728.run();
    }

    private void onCancel() {
        this.cancelled = true;
        this.client.openScreen(this.parent);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            if (this.showDots) {
                this.onCancel();
            } else {
                this.onBack();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        if (!this.uploadFinished && this.uploadStatus.bytesWritten != 0L && this.uploadStatus.bytesWritten == this.uploadStatus.totalBytes) {
            this.status = field_26526;
            this.cancelButton.active = false;
        }
        RealmsUploadScreen.drawCenteredText(matrices, this.textRenderer, this.status, this.width / 2, 50, 0xFFFFFF);
        if (this.showDots) {
            this.drawDots(matrices);
        }
        if (this.uploadStatus.bytesWritten != 0L && !this.cancelled) {
            this.drawProgressBar(matrices);
            this.drawUploadSpeed(matrices);
        }
        if (this.field_20503 != null) {
            for (int i = 0; i < this.field_20503.length; ++i) {
                RealmsUploadScreen.drawCenteredText(matrices, this.textRenderer, this.field_20503[i], this.width / 2, 110 + 12 * i, 0xFF0000);
            }
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void drawDots(MatrixStack matrices) {
        int i = this.textRenderer.getWidth(this.status);
        this.textRenderer.draw(matrices, DOTS[this.animTick / 10 % DOTS.length], (float)(this.width / 2 + i / 2 + 5), 50.0f, 0xFFFFFF);
    }

    private void drawProgressBar(MatrixStack matrices) {
        double d = Math.min((double)this.uploadStatus.bytesWritten / (double)this.uploadStatus.totalBytes, 1.0);
        this.progress = String.format(Locale.ROOT, "%.1f", d * 100.0);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableTexture();
        double e = this.width / 2 - 100;
        double f = 0.5;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(e - 0.5, 95.5, 0.0).color(217, 210, 210, 255).next();
        bufferBuilder.vertex(e + 200.0 * d + 0.5, 95.5, 0.0).color(217, 210, 210, 255).next();
        bufferBuilder.vertex(e + 200.0 * d + 0.5, 79.5, 0.0).color(217, 210, 210, 255).next();
        bufferBuilder.vertex(e - 0.5, 79.5, 0.0).color(217, 210, 210, 255).next();
        bufferBuilder.vertex(e, 95.0, 0.0).color(128, 128, 128, 255).next();
        bufferBuilder.vertex(e + 200.0 * d, 95.0, 0.0).color(128, 128, 128, 255).next();
        bufferBuilder.vertex(e + 200.0 * d, 80.0, 0.0).color(128, 128, 128, 255).next();
        bufferBuilder.vertex(e, 80.0, 0.0).color(128, 128, 128, 255).next();
        tessellator.draw();
        RenderSystem.enableTexture();
        RealmsUploadScreen.drawCenteredText(matrices, this.textRenderer, this.progress + " %", this.width / 2, 84, 0xFFFFFF);
    }

    private void drawUploadSpeed(MatrixStack matrices) {
        if (this.animTick % 20 == 0) {
            if (this.previousWrittenBytes != null) {
                long l = Util.getMeasuringTimeMs() - this.previousTimeSnapshot;
                if (l == 0L) {
                    l = 1L;
                }
                this.bytesPerSecond = 1000L * (this.uploadStatus.bytesWritten - this.previousWrittenBytes) / l;
                this.drawUploadSpeed0(matrices, this.bytesPerSecond);
            }
            this.previousWrittenBytes = this.uploadStatus.bytesWritten;
            this.previousTimeSnapshot = Util.getMeasuringTimeMs();
        } else {
            this.drawUploadSpeed0(matrices, this.bytesPerSecond);
        }
    }

    private void drawUploadSpeed0(MatrixStack matrices, long l) {
        if (l > 0L) {
            int i = this.textRenderer.getWidth(this.progress);
            String string = "(" + SizeUnit.getUserFriendlyString(l) + "/s)";
            this.textRenderer.draw(matrices, string, (float)(this.width / 2 + i / 2 + 15), 84.0f, 0xFFFFFF);
        }
    }

    @Override
    public void tick() {
        super.tick();
        ++this.animTick;
        if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
            ArrayList list = Lists.newArrayList();
            list.add(this.status.getString());
            if (this.progress != null) {
                list.add(this.progress + "%");
            }
            if (this.field_20503 != null) {
                Stream.of(this.field_20503).map(Text::getString).forEach(list::add);
            }
            Realms.narrateNow(String.join((CharSequence)System.lineSeparator(), list));
        }
    }

    private void upload() {
        this.uploadStarted = true;
        new Thread(() -> {
            File file = null;
            RealmsClient realmsClient = RealmsClient.createRealmsClient();
            long l = this.worldId;
            try {
                if (!UPLOAD_LOCK.tryLock(1L, TimeUnit.SECONDS)) {
                    this.status = new TranslatableText("mco.upload.close.failure");
                    return;
                }
                UploadInfo uploadInfo = null;
                for (int i = 0; i < 20; ++i) {
                    block35: {
                        if (!this.cancelled) break block35;
                        this.uploadCancelled();
                        return;
                    }
                    try {
                        uploadInfo = realmsClient.upload(l, UploadTokenCache.get(l));
                        if (uploadInfo == null) continue;
                        break;
                    }
                    catch (RetryCallException retryCallException) {
                        Thread.sleep(retryCallException.delaySeconds * 1000);
                    }
                }
                if (uploadInfo == null) {
                    this.status = new TranslatableText("mco.upload.close.failure");
                    return;
                }
                UploadTokenCache.put(l, uploadInfo.getToken());
                if (!uploadInfo.isWorldClosed()) {
                    this.status = new TranslatableText("mco.upload.close.failure");
                    return;
                }
                if (this.cancelled) {
                    this.uploadCancelled();
                    return;
                }
                File file2 = new File(this.client.runDirectory.getAbsolutePath(), "saves");
                file = this.tarGzipArchive(new File(file2, this.selectedLevel.getName()));
                if (this.cancelled) {
                    this.uploadCancelled();
                    return;
                }
                if (!this.verify(file)) {
                    long m = file.length();
                    SizeUnit sizeUnit = SizeUnit.getLargestUnit(m);
                    SizeUnit sizeUnit2 = SizeUnit.getLargestUnit(0x140000000L);
                    if (SizeUnit.humanReadableSize(m, sizeUnit).equals(SizeUnit.humanReadableSize(0x140000000L, sizeUnit2)) && sizeUnit != SizeUnit.B) {
                        SizeUnit sizeUnit3 = SizeUnit.values()[sizeUnit.ordinal() - 1];
                        this.method_27460(new TranslatableText("mco.upload.size.failure.line1", this.selectedLevel.getDisplayName()), new TranslatableText("mco.upload.size.failure.line2", SizeUnit.humanReadableSize(m, sizeUnit3), SizeUnit.humanReadableSize(0x140000000L, sizeUnit3)));
                        return;
                    }
                    this.method_27460(new TranslatableText("mco.upload.size.failure.line1", this.selectedLevel.getDisplayName()), new TranslatableText("mco.upload.size.failure.line2", SizeUnit.humanReadableSize(m, sizeUnit), SizeUnit.humanReadableSize(0x140000000L, sizeUnit2)));
                    return;
                }
                this.status = new TranslatableText("mco.upload.uploading", this.selectedLevel.getDisplayName());
                FileUpload fileUpload = new FileUpload(file, this.worldId, this.slotId, uploadInfo, this.client.getSession(), SharedConstants.getGameVersion().getName(), this.uploadStatus);
                fileUpload.upload(uploadResult -> {
                    if (uploadResult.statusCode >= 200 && uploadResult.statusCode < 300) {
                        this.uploadFinished = true;
                        this.status = new TranslatableText("mco.upload.done");
                        this.backButton.setMessage(ScreenTexts.DONE);
                        UploadTokenCache.invalidate(l);
                    } else if (uploadResult.statusCode == 400 && uploadResult.errorMessage != null) {
                        this.method_27460(new TranslatableText("mco.upload.failed", uploadResult.errorMessage));
                    } else {
                        this.method_27460(new TranslatableText("mco.upload.failed", uploadResult.statusCode));
                    }
                });
                while (!fileUpload.isFinished()) {
                    if (this.cancelled) {
                        fileUpload.cancel();
                        this.uploadCancelled();
                        return;
                    }
                    try {
                        Thread.sleep(500L);
                    }
                    catch (InterruptedException interruptedException) {
                        LOGGER.error("Failed to check Realms file upload status");
                    }
                }
            }
            catch (IOException iOException) {
                this.method_27460(new TranslatableText("mco.upload.failed", iOException.getMessage()));
            }
            catch (RealmsServiceException realmsServiceException) {
                this.method_27460(new TranslatableText("mco.upload.failed", realmsServiceException.toString()));
            }
            catch (InterruptedException interruptedException2) {
                LOGGER.error("Could not acquire upload lock");
            }
            finally {
                this.uploadFinished = true;
                if (!UPLOAD_LOCK.isHeldByCurrentThread()) {
                    return;
                }
                UPLOAD_LOCK.unlock();
                this.showDots = false;
                this.backButton.visible = true;
                this.cancelButton.visible = false;
                if (file != null) {
                    LOGGER.debug("Deleting file " + file.getAbsolutePath());
                    file.delete();
                }
            }
        }).start();
    }

    private void method_27460(Text ... texts) {
        this.field_20503 = texts;
    }

    private void uploadCancelled() {
        this.status = new TranslatableText("mco.upload.cancelled");
        LOGGER.debug("Upload was cancelled");
    }

    private boolean verify(File archive) {
        return archive.length() < 0x140000000L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private File tarGzipArchive(File pathToDirectoryFile) throws IOException {
        try (TarArchiveOutputStream tarArchiveOutputStream = null;){
            File file = File.createTempFile("realms-upload-file", ".tar.gz");
            tarArchiveOutputStream = new TarArchiveOutputStream((OutputStream)new GZIPOutputStream(new FileOutputStream(file)));
            tarArchiveOutputStream.setLongFileMode(3);
            this.addFileToTarGz(tarArchiveOutputStream, pathToDirectoryFile.getAbsolutePath(), "world", true);
            tarArchiveOutputStream.finish();
            File file2 = file;
            return file2;
        }
    }

    private void addFileToTarGz(TarArchiveOutputStream tOut, String path, String base, boolean root) throws IOException {
        if (this.cancelled) {
            return;
        }
        File file = new File(path);
        String string = root ? base : base + file.getName();
        TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(file, string);
        tOut.putArchiveEntry((ArchiveEntry)tarArchiveEntry);
        if (file.isFile()) {
            IOUtils.copy((InputStream)new FileInputStream(file), (OutputStream)tOut);
            tOut.closeArchiveEntry();
        } else {
            tOut.closeArchiveEntry();
            File[] files = file.listFiles();
            if (files != null) {
                for (File file2 : files) {
                    this.addFileToTarGz(tOut, file2.getAbsolutePath(), string + "/", false);
                }
            }
        }
    }
}

