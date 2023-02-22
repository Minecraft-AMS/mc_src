/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  com.mojang.datafixers.util.Unit
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.report;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Unit;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TaskScreen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.screen.report.AbuseReportReasonScreen;
import net.minecraft.client.gui.screen.report.ChatSelectionScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.report.AbuseReportReason;
import net.minecraft.client.report.ChatAbuseReport;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.TextifiedException;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ChatReportScreen
extends Screen {
    private static final int BOTTOM_BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_TOP_MARGIN = 20;
    private static final int BUTTON_BOTTOM_MARGIN = 10;
    private static final int REASON_DESCRIPTION_TOP_MARGIN = 25;
    private static final int REASON_DESCRIPTION_TEXT_WIDTH = 280;
    private static final int SCREEN_HEIGHT = 300;
    private static final Text OBSERVED_WHAT_TEXT = Text.translatable("gui.chatReport.observed_what");
    private static final Text SELECT_REASON_TEXT = Text.translatable("gui.chatReport.select_reason");
    private static final Text MORE_COMMENTS_TEXT = Text.translatable("gui.chatReport.more_comments");
    private static final Text DESCRIBE_TEXT = Text.translatable("gui.chatReport.describe");
    private static final Text REPORT_SENT_MESSAGE_TEXT = Text.translatable("gui.chatReport.report_sent_msg");
    private static final Text SELECT_CHAT_TEXT = Text.translatable("gui.chatReport.select_chat");
    private static final Text SENDING_TEXT = Text.translatable("gui.abuseReport.sending.title").formatted(Formatting.BOLD);
    private static final Text REPORT_SENT_TITLE = Text.translatable("gui.abuseReport.sent.title").formatted(Formatting.BOLD);
    private static final Text REPORT_ERROR_TITLE = Text.translatable("gui.abuseReport.error.title").formatted(Formatting.BOLD);
    private static final Text GENERIC_ERROR_TEXT = Text.translatable("gui.abuseReport.send.generic_error");
    private static final Logger field_39577 = LogUtils.getLogger();
    @Nullable
    final Screen parent;
    private final AbuseReportContext reporter;
    @Nullable
    private MultilineText reasonDescription;
    @Nullable
    private EditBoxWidget editBox;
    private ButtonWidget sendButton;
    private ChatAbuseReport report;
    @Nullable
    ChatAbuseReport.ValidationError validationError;

    public ChatReportScreen(Screen parent, AbuseReportContext reporter, UUID reportedPlayerUuid) {
        super(Text.translatable("gui.chatReport.title"));
        this.parent = parent;
        this.reporter = reporter;
        this.report = new ChatAbuseReport(reportedPlayerUuid, reporter.sender().getLimits());
    }

    @Override
    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        AbuseReportLimits abuseReportLimits = this.reporter.sender().getLimits();
        int i = this.width / 2;
        AbuseReportReason abuseReportReason = this.report.getReason();
        this.reasonDescription = abuseReportReason != null ? MultilineText.create(this.textRenderer, (StringVisitable)abuseReportReason.getDescription(), 280) : null;
        IntSet intSet = this.report.getSelections();
        Text text = intSet.isEmpty() ? SELECT_CHAT_TEXT : Text.translatable("gui.chatReport.selected_chat", intSet.size());
        this.addDrawableChild(new ButtonWidget(this.getWidgetsLeft(), this.getSelectionButtonY(), 280, 20, text, button -> this.client.setScreen(new ChatSelectionScreen(this, this.reporter, this.report, report -> {
            this.report = report;
            this.onChange();
        }))));
        Text text2 = Util.mapOrElse(abuseReportReason, AbuseReportReason::getText, SELECT_REASON_TEXT);
        this.addDrawableChild(new ButtonWidget(this.getWidgetsLeft(), this.getReasonButtonY(), 280, 20, text2, button -> this.client.setScreen(new AbuseReportReasonScreen(this, this.report.getReason(), reason -> {
            this.report.setReason((AbuseReportReason)((Object)((Object)reason)));
            this.onChange();
        }))));
        this.editBox = this.addDrawableChild(new EditBoxWidget(this.client.textRenderer, this.getWidgetsLeft(), this.getEditBoxTop(), 280, this.getEditBoxBottom() - this.getEditBoxTop(), DESCRIBE_TEXT, Text.translatable("gui.chatReport.comments")));
        this.editBox.setText(this.report.getOpinionComments());
        this.editBox.setMaxLength(abuseReportLimits.maxOpinionCommentsLength());
        this.editBox.setChangeListener(opinionComments -> {
            this.report.setOpinionComments((String)opinionComments);
            this.onChange();
        });
        this.addDrawableChild(new ButtonWidget(i - 120, this.getBottomButtonsY(), 120, 20, ScreenTexts.BACK, button -> this.close()));
        this.sendButton = this.addDrawableChild(new ButtonWidget(i + 10, this.getBottomButtonsY(), 120, 20, Text.translatable("gui.chatReport.send"), button -> this.send(), new ValidationErrorTooltipSupplier()));
        this.onChange();
    }

    private void onChange() {
        this.validationError = this.report.validate();
        this.sendButton.active = this.validationError == null;
    }

    private void send() {
        this.report.finalizeReport(this.reporter).ifLeft(report -> {
            CompletableFuture<Unit> completableFuture = this.reporter.sender().send(report.id(), report.report());
            this.client.setScreen(TaskScreen.createRunningScreen(SENDING_TEXT, ScreenTexts.CANCEL, () -> {
                this.client.setScreen(this);
                completableFuture.cancel(true);
            }));
            completableFuture.handleAsync((unit, throwable) -> {
                if (throwable == null) {
                    this.onSubmissionFinished();
                } else {
                    if (throwable instanceof CancellationException) {
                        return null;
                    }
                    this.onSubmissionError((Throwable)throwable);
                }
                return null;
            }, (Executor)this.client);
        }).ifRight(validationError -> this.showErrorScreen(validationError.message()));
    }

    private void onSubmissionFinished() {
        this.client.setScreen(TaskScreen.createResultScreen(REPORT_SENT_TITLE, REPORT_SENT_MESSAGE_TEXT, ScreenTexts.DONE, () -> this.client.setScreen(null)));
    }

    private void onSubmissionError(Throwable throwable) {
        Text text;
        field_39577.error("Encountered error while sending abuse report", throwable);
        Throwable throwable2 = throwable.getCause();
        if (throwable2 instanceof TextifiedException) {
            TextifiedException textifiedException = (TextifiedException)throwable2;
            text = textifiedException.getMessageText();
        } else {
            text = GENERIC_ERROR_TEXT;
        }
        this.showErrorScreen(text);
    }

    private void showErrorScreen(Text message) {
        MutableText text = message.copy().formatted(Formatting.RED);
        this.client.setScreen(TaskScreen.createResultScreen(REPORT_ERROR_TITLE, text, ScreenTexts.BACK, () -> this.client.setScreen(this)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int i = this.width / 2;
        RenderSystem.disableDepthTest();
        this.renderBackground(matrices);
        ChatReportScreen.drawCenteredText(matrices, this.textRenderer, this.title, i, 10, 0xFFFFFF);
        ChatReportScreen.drawCenteredText(matrices, this.textRenderer, OBSERVED_WHAT_TEXT, i, this.getSelectionButtonY() - this.textRenderer.fontHeight - 6, 0xFFFFFF);
        if (this.reasonDescription != null) {
            this.reasonDescription.drawWithShadow(matrices, this.getWidgetsLeft(), this.getReasonButtonY() + 20 + 5, this.textRenderer.fontHeight, 0xFFFFFF);
        }
        ChatReportScreen.drawTextWithShadow(matrices, this.textRenderer, MORE_COMMENTS_TEXT, this.getWidgetsLeft(), this.getEditBoxTop() - this.textRenderer.fontHeight - 6, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
        RenderSystem.enableDepthTest();
    }

    @Override
    public void tick() {
        this.editBox.tick();
        super.tick();
    }

    @Override
    public void close() {
        if (!this.editBox.getText().isEmpty()) {
            this.client.setScreen(new DiscardWarningScreen());
        } else {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (super.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return this.editBox.mouseReleased(mouseX, mouseY, button);
    }

    private int getWidgetsLeft() {
        return this.width / 2 - 140;
    }

    private int getWidgetsRight() {
        return this.width / 2 + 140;
    }

    private int getTop() {
        return Math.max((this.height - 300) / 2, 0);
    }

    private int getBottom() {
        return Math.min((this.height + 300) / 2, this.height);
    }

    private int getSelectionButtonY() {
        return this.getTop() + 40;
    }

    private int getReasonButtonY() {
        return this.getSelectionButtonY() + 10 + 20;
    }

    private int getEditBoxTop() {
        int i = this.getReasonButtonY() + 20 + 25;
        if (this.reasonDescription != null) {
            i += (this.reasonDescription.count() + 1) * this.textRenderer.fontHeight;
        }
        return i;
    }

    private int getEditBoxBottom() {
        return this.getBottomButtonsY() - 20;
    }

    private int getBottomButtonsY() {
        return this.getBottom() - 20 - 10;
    }

    @Environment(value=EnvType.CLIENT)
    class ValidationErrorTooltipSupplier
    implements ButtonWidget.TooltipSupplier {
        ValidationErrorTooltipSupplier() {
        }

        @Override
        public void onTooltip(ButtonWidget buttonWidget, MatrixStack matrixStack, int i, int j) {
            if (ChatReportScreen.this.validationError != null) {
                Text text = ChatReportScreen.this.validationError.message();
                ChatReportScreen.this.renderOrderedTooltip(matrixStack, ChatReportScreen.this.textRenderer.wrapLines(text, Math.max(ChatReportScreen.this.width / 2 - 43, 170)), i, j);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class DiscardWarningScreen
    extends WarningScreen {
        private static final Text TITLE = Text.translatable("gui.chatReport.discard.title").formatted(Formatting.BOLD);
        private static final Text MESSAGE = Text.translatable("gui.chatReport.discard.content");
        private static final Text RETURN_BUTTON_TEXT = Text.translatable("gui.chatReport.discard.return");
        private static final Text DISCARD_BUTTON_TEXT = Text.translatable("gui.chatReport.discard.discard");

        protected DiscardWarningScreen() {
            super(TITLE, MESSAGE, MESSAGE);
        }

        @Override
        protected void initButtons(int yOffset) {
            this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, 100 + yOffset, 150, 20, RETURN_BUTTON_TEXT, button -> this.close()));
            this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, 100 + yOffset, 150, 20, DISCARD_BUTTON_TEXT, button -> this.client.setScreen(ChatReportScreen.this.parent)));
        }

        @Override
        public void close() {
            this.client.setScreen(ChatReportScreen.this);
        }

        @Override
        public boolean shouldCloseOnEsc() {
            return false;
        }

        @Override
        protected void drawTitle(MatrixStack matrices) {
            DiscardWarningScreen.drawTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2 - 155, 30, 0xFFFFFF);
        }
    }
}

