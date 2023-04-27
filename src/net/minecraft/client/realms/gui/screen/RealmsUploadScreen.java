/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.RateLimiter
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.compress.archivers.ArchiveEntry
 *  org.apache.commons.compress.archivers.tar.TarArchiveEntry
 *  org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
 *  org.apache.commons.compress.utils.IOUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPOutputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.FileUpload;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.SizeUnit;
import net.minecraft.client.realms.UploadStatus;
import net.minecraft.client.realms.dto.UploadInfo;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsResetWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.util.UploadTokenCache;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsUploadScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ReentrantLock UPLOAD_LOCK = new ReentrantLock();
    private static final int field_41776 = 200;
    private static final int field_41773 = 80;
    private static final int field_41774 = 95;
    private static final int field_41775 = 1;
    private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
    private static final Text VERIFYING_TEXT = Text.translatable("mco.upload.verifying");
    private final RealmsResetWorldScreen parent;
    private final LevelSummary selectedLevel;
    private final long worldId;
    private final int slotId;
    private final UploadStatus uploadStatus;
    private final RateLimiter narrationRateLimiter;
    @Nullable
    private volatile Text[] statusTexts;
    private volatile Text status = Text.translatable("mco.upload.preparing");
    private volatile String progress;
    private volatile boolean cancelled;
    private volatile boolean uploadFinished;
    private volatile boolean showDots = true;
    private volatile boolean uploadStarted;
    private ButtonWidget backButton;
    private ButtonWidget cancelButton;
    private int animTick;
    @Nullable
    private Long previousWrittenBytes;
    @Nullable
    private Long previousTimeSnapshot;
    private long bytesPerSecond;
    private final Runnable onBack;

    public RealmsUploadScreen(long worldId, int slotId, RealmsResetWorldScreen parent, LevelSummary selectedLevel, Runnable onBack) {
        super(NarratorManager.EMPTY);
        this.worldId = worldId;
        this.slotId = slotId;
        this.parent = parent;
        this.selectedLevel = selectedLevel;
        this.uploadStatus = new UploadStatus();
        this.narrationRateLimiter = RateLimiter.create((double)0.1f);
        this.onBack = onBack;
    }

    @Override
    public void init() {
        this.backButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.onBack()).dimensions((this.width - 200) / 2, this.height - 42, 200, 20).build());
        this.backButton.visible = false;
        this.cancelButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.onCancel()).dimensions((this.width - 200) / 2, this.height - 42, 200, 20).build());
        if (!this.uploadStarted) {
            if (this.parent.slot == -1) {
                this.upload();
            } else {
                this.parent.switchSlot(() -> {
                    if (!this.uploadStarted) {
                        this.uploadStarted = true;
                        this.client.setScreen(this);
                        this.upload();
                    }
                });
            }
        }
    }

    private void onBack() {
        this.onBack.run();
    }

    private void onCancel() {
        this.cancelled = true;
        this.client.setScreen(this.parent);
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        if (!this.uploadFinished && this.uploadStatus.bytesWritten != 0L && this.uploadStatus.bytesWritten == this.uploadStatus.totalBytes) {
            this.status = VERIFYING_TEXT;
            this.cancelButton.active = false;
        }
        context.drawCenteredTextWithShadow(this.textRenderer, this.status, this.width / 2, 50, 0xFFFFFF);
        if (this.showDots) {
            this.drawDots(context);
        }
        if (this.uploadStatus.bytesWritten != 0L && !this.cancelled) {
            this.drawProgressBar(context);
            this.drawUploadSpeed(context);
        }
        if (this.statusTexts != null) {
            for (int i = 0; i < this.statusTexts.length; ++i) {
                context.drawCenteredTextWithShadow(this.textRenderer, this.statusTexts[i], this.width / 2, 110 + 12 * i, 0xFF0000);
            }
        }
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawDots(DrawContext context) {
        int i = this.textRenderer.getWidth(this.status);
        context.drawText(this.textRenderer, DOTS[this.animTick / 10 % DOTS.length], this.width / 2 + i / 2 + 5, 50, 0xFFFFFF, false);
    }

    private void drawProgressBar(DrawContext context) {
        double d = Math.min((double)this.uploadStatus.bytesWritten / (double)this.uploadStatus.totalBytes, 1.0);
        this.progress = String.format(Locale.ROOT, "%.1f", d * 100.0);
        int i = (this.width - 200) / 2;
        int j = i + (int)Math.round(200.0 * d);
        context.fill(i - 1, 79, j + 1, 96, -2501934);
        context.fill(i, 80, j, 95, -8355712);
        context.drawCenteredTextWithShadow(this.textRenderer, this.progress + " %", this.width / 2, 84, 0xFFFFFF);
    }

    private void drawUploadSpeed(DrawContext context) {
        if (this.animTick % 20 == 0) {
            if (this.previousWrittenBytes != null) {
                long l = Util.getMeasuringTimeMs() - this.previousTimeSnapshot;
                if (l == 0L) {
                    l = 1L;
                }
                this.bytesPerSecond = 1000L * (this.uploadStatus.bytesWritten - this.previousWrittenBytes) / l;
                this.drawUploadSpeed0(context, this.bytesPerSecond);
            }
            this.previousWrittenBytes = this.uploadStatus.bytesWritten;
            this.previousTimeSnapshot = Util.getMeasuringTimeMs();
        } else {
            this.drawUploadSpeed0(context, this.bytesPerSecond);
        }
    }

    private void drawUploadSpeed0(DrawContext context, long bytesPerSecond) {
        if (bytesPerSecond > 0L) {
            int i = this.textRenderer.getWidth(this.progress);
            String string = "(" + SizeUnit.getUserFriendlyString(bytesPerSecond) + "/s)";
            context.drawText(this.textRenderer, string, this.width / 2 + i / 2 + 15, 84, 0xFFFFFF, false);
        }
    }

    @Override
    public void tick() {
        super.tick();
        ++this.animTick;
        if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
            Text text = this.getNarration();
            this.client.getNarratorManager().narrate(text);
        }
    }

    private Text getNarration() {
        ArrayList list = Lists.newArrayList();
        list.add(this.status);
        if (this.progress != null) {
            list.add(Text.literal(this.progress + "%"));
        }
        if (this.statusTexts != null) {
            list.addAll(Arrays.asList(this.statusTexts));
        }
        return ScreenTexts.joinLines(list);
    }

    private void upload() {
        this.uploadStarted = true;
        new Thread(() -> {
            File file = null;
            RealmsClient realmsClient = RealmsClient.create();
            long l = this.worldId;
            try {
                if (!UPLOAD_LOCK.tryLock(1L, TimeUnit.SECONDS)) {
                    this.status = Text.translatable("mco.upload.close.failure");
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
                    this.status = Text.translatable("mco.upload.close.failure");
                    return;
                }
                UploadTokenCache.put(l, uploadInfo.getToken());
                if (!uploadInfo.isWorldClosed()) {
                    this.status = Text.translatable("mco.upload.close.failure");
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
                        this.setStatusTexts(Text.translatable("mco.upload.size.failure.line1", this.selectedLevel.getDisplayName()), Text.translatable("mco.upload.size.failure.line2", SizeUnit.humanReadableSize(m, sizeUnit3), SizeUnit.humanReadableSize(0x140000000L, sizeUnit3)));
                        return;
                    }
                    this.setStatusTexts(Text.translatable("mco.upload.size.failure.line1", this.selectedLevel.getDisplayName()), Text.translatable("mco.upload.size.failure.line2", SizeUnit.humanReadableSize(m, sizeUnit), SizeUnit.humanReadableSize(0x140000000L, sizeUnit2)));
                    return;
                }
                this.status = Text.translatable("mco.upload.uploading", this.selectedLevel.getDisplayName());
                FileUpload fileUpload = new FileUpload(file, this.worldId, this.slotId, uploadInfo, this.client.getSession(), SharedConstants.getGameVersion().getName(), this.uploadStatus);
                fileUpload.upload(result -> {
                    if (result.statusCode >= 200 && result.statusCode < 300) {
                        this.uploadFinished = true;
                        this.status = Text.translatable("mco.upload.done");
                        this.backButton.setMessage(ScreenTexts.DONE);
                        UploadTokenCache.invalidate(l);
                    } else if (result.statusCode == 400 && result.errorMessage != null) {
                        this.setStatusTexts(Text.translatable("mco.upload.failed", result.errorMessage));
                    } else {
                        this.setStatusTexts(Text.translatable("mco.upload.failed", result.statusCode));
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
                this.setStatusTexts(Text.translatable("mco.upload.failed", iOException.getMessage()));
            }
            catch (RealmsServiceException realmsServiceException) {
                this.setStatusTexts(Text.translatable("mco.upload.failed", realmsServiceException.toString()));
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
                    LOGGER.debug("Deleting file {}", (Object)file.getAbsolutePath());
                    file.delete();
                }
            }
        }).start();
    }

    private void setStatusTexts(Text ... statusTexts) {
        this.statusTexts = statusTexts;
    }

    private void uploadCancelled() {
        this.status = Text.translatable("mco.upload.cancelled");
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

