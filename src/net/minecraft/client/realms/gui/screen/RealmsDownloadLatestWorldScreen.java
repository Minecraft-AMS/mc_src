/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.RateLimiter
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.FileDownload;
import net.minecraft.client.realms.SizeUnit;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.gui.screen.RealmsLongConfirmationScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsDownloadLatestWorldScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ReentrantLock DOWNLOAD_LOCK = new ReentrantLock();
    private final Screen parent;
    private final WorldDownload worldDownload;
    private final Text downloadTitle;
    private final RateLimiter narrationRateLimiter;
    private ButtonWidget cancelButton;
    private final String worldName;
    private final DownloadStatus downloadStatus;
    @Nullable
    private volatile Text downloadError;
    private volatile Text status = new TranslatableText("mco.download.preparing");
    @Nullable
    private volatile String progress;
    private volatile boolean cancelled;
    private volatile boolean showDots = true;
    private volatile boolean finished;
    private volatile boolean extracting;
    @Nullable
    private Long previousWrittenBytes;
    @Nullable
    private Long previousTimeSnapshot;
    private long bytesPerSecond;
    private int animTick;
    private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
    private int dotIndex;
    private boolean checked;
    private final BooleanConsumer onBack;

    public RealmsDownloadLatestWorldScreen(Screen parent, WorldDownload worldDownload, String worldName, BooleanConsumer onBack) {
        super(NarratorManager.EMPTY);
        this.onBack = onBack;
        this.parent = parent;
        this.worldName = worldName;
        this.worldDownload = worldDownload;
        this.downloadStatus = new DownloadStatus();
        this.downloadTitle = new TranslatableText("mco.download.title");
        this.narrationRateLimiter = RateLimiter.create((double)0.1f);
    }

    @Override
    public void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.cancelButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 42, 200, 20, ScreenTexts.CANCEL, button -> {
            this.cancelled = true;
            this.backButtonClicked();
        }));
        this.checkDownloadSize();
    }

    private void checkDownloadSize() {
        if (this.finished) {
            return;
        }
        if (!this.checked && this.getContentLength(this.worldDownload.downloadLink) >= 0x140000000L) {
            TranslatableText text = new TranslatableText("mco.download.confirmation.line1", SizeUnit.getUserFriendlyString(0x140000000L));
            TranslatableText text2 = new TranslatableText("mco.download.confirmation.line2");
            this.client.setScreen(new RealmsLongConfirmationScreen(confirmed -> {
                this.checked = true;
                this.client.setScreen(this);
                this.downloadSave();
            }, RealmsLongConfirmationScreen.Type.WARNING, text, text2, false));
        } else {
            this.downloadSave();
        }
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
            Text text = this.getNarration();
            NarratorManager.INSTANCE.narrate(text);
        }
    }

    private Text getNarration() {
        ArrayList list = Lists.newArrayList();
        list.add(this.downloadTitle);
        list.add(this.status);
        if (this.progress != null) {
            list.add(new LiteralText(this.progress + "%"));
            list.add(new LiteralText(SizeUnit.getUserFriendlyString(this.bytesPerSecond) + "/s"));
        }
        if (this.downloadError != null) {
            list.add(this.downloadError);
        }
        return ScreenTexts.joinLines(list);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.cancelled = true;
            this.backButtonClicked();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void backButtonClicked() {
        if (this.finished && this.onBack != null && this.downloadError == null) {
            this.onBack.accept(true);
        }
        this.client.setScreen(this.parent);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        RealmsDownloadLatestWorldScreen.drawCenteredText(matrices, this.textRenderer, this.downloadTitle, this.width / 2, 20, 0xFFFFFF);
        RealmsDownloadLatestWorldScreen.drawCenteredText(matrices, this.textRenderer, this.status, this.width / 2, 50, 0xFFFFFF);
        if (this.showDots) {
            this.drawDots(matrices);
        }
        if (this.downloadStatus.bytesWritten != 0L && !this.cancelled) {
            this.drawProgressBar(matrices);
            this.drawDownloadSpeed(matrices);
        }
        if (this.downloadError != null) {
            RealmsDownloadLatestWorldScreen.drawCenteredText(matrices, this.textRenderer, this.downloadError, this.width / 2, 110, 0xFF0000);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void drawDots(MatrixStack matrices) {
        int i = this.textRenderer.getWidth(this.status);
        if (this.animTick % 10 == 0) {
            ++this.dotIndex;
        }
        this.textRenderer.draw(matrices, DOTS[this.dotIndex % DOTS.length], (float)(this.width / 2 + i / 2 + 5), 50.0f, 0xFFFFFF);
    }

    private void drawProgressBar(MatrixStack matrices) {
        double d = Math.min((double)this.downloadStatus.bytesWritten / (double)this.downloadStatus.totalBytes, 1.0);
        this.progress = String.format(Locale.ROOT, "%.1f", d * 100.0);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        double e = this.width / 2 - 100;
        double f = 0.5;
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
        RealmsDownloadLatestWorldScreen.drawCenteredText(matrices, this.textRenderer, this.progress + " %", this.width / 2, 84, 0xFFFFFF);
    }

    private void drawDownloadSpeed(MatrixStack matrices) {
        if (this.animTick % 20 == 0) {
            if (this.previousWrittenBytes != null) {
                long l = Util.getMeasuringTimeMs() - this.previousTimeSnapshot;
                if (l == 0L) {
                    l = 1L;
                }
                this.bytesPerSecond = 1000L * (this.downloadStatus.bytesWritten - this.previousWrittenBytes) / l;
                this.drawDownloadSpeed0(matrices, this.bytesPerSecond);
            }
            this.previousWrittenBytes = this.downloadStatus.bytesWritten;
            this.previousTimeSnapshot = Util.getMeasuringTimeMs();
        } else {
            this.drawDownloadSpeed0(matrices, this.bytesPerSecond);
        }
    }

    private void drawDownloadSpeed0(MatrixStack matrices, long bytesPerSecond) {
        if (bytesPerSecond > 0L) {
            int i = this.textRenderer.getWidth(this.progress);
            String string = "(" + SizeUnit.getUserFriendlyString(bytesPerSecond) + "/s)";
            this.textRenderer.draw(matrices, string, (float)(this.width / 2 + i / 2 + 15), 84.0f, 0xFFFFFF);
        }
    }

    private void downloadSave() {
        new Thread(() -> {
            try {
                if (!DOWNLOAD_LOCK.tryLock(1L, TimeUnit.SECONDS)) {
                    this.status = new TranslatableText("mco.download.failed");
                    return;
                }
                if (this.cancelled) {
                    this.downloadCancelled();
                    return;
                }
                this.status = new TranslatableText("mco.download.downloading", this.worldName);
                FileDownload fileDownload = new FileDownload();
                fileDownload.contentLength(this.worldDownload.downloadLink);
                fileDownload.downloadWorld(this.worldDownload, this.worldName, this.downloadStatus, this.client.getLevelStorage());
                while (!fileDownload.isFinished()) {
                    if (fileDownload.isError()) {
                        fileDownload.cancel();
                        this.downloadError = new TranslatableText("mco.download.failed");
                        this.cancelButton.setMessage(ScreenTexts.DONE);
                        return;
                    }
                    if (fileDownload.isExtracting()) {
                        if (!this.extracting) {
                            this.status = new TranslatableText("mco.download.extracting");
                        }
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
                this.status = new TranslatableText("mco.download.done");
                this.cancelButton.setMessage(ScreenTexts.DONE);
            }
            catch (InterruptedException interruptedException2) {
                LOGGER.error("Could not acquire upload lock");
            }
            catch (Exception exception) {
                this.downloadError = new TranslatableText("mco.download.failed");
                exception.printStackTrace();
            }
            finally {
                if (!DOWNLOAD_LOCK.isHeldByCurrentThread()) {
                    return;
                }
                DOWNLOAD_LOCK.unlock();
                this.showDots = false;
                this.finished = true;
            }
        }).start();
    }

    private void downloadCancelled() {
        this.status = new TranslatableText("mco.download.cancelled");
    }

    @Environment(value=EnvType.CLIENT)
    public static class DownloadStatus {
        public volatile long bytesWritten;
        public volatile long totalBytes;
    }
}

