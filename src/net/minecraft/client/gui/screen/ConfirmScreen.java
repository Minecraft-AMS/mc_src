/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ConfirmScreen
extends Screen {
    private static final int TITLE_BOTTOM_MARGIN = 20;
    private final Text message;
    private MultilineText messageSplit = MultilineText.EMPTY;
    protected Text yesText;
    protected Text noText;
    private int buttonEnableTimer;
    protected final BooleanConsumer callback;
    private final List<ButtonWidget> buttons = Lists.newArrayList();

    public ConfirmScreen(BooleanConsumer callback, Text title, Text message) {
        this(callback, title, message, ScreenTexts.YES, ScreenTexts.NO);
    }

    public ConfirmScreen(BooleanConsumer callback, Text title, Text message, Text yesText, Text noText) {
        super(title);
        this.callback = callback;
        this.message = message;
        this.yesText = yesText;
        this.noText = noText;
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(super.getNarratedTitle(), this.message);
    }

    @Override
    protected void init() {
        super.init();
        this.messageSplit = MultilineText.create(this.textRenderer, (StringVisitable)this.message, this.width - 50);
        int i = MathHelper.clamp(this.getMessageY() + this.getMessagesHeight() + 20, this.height / 6 + 96, this.height - 24);
        this.buttons.clear();
        this.addButtons(i);
    }

    protected void addButtons(int y) {
        this.addButton(new ButtonWidget(this.width / 2 - 155, y, 150, 20, this.yesText, button -> this.callback.accept(true)));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, y, 150, 20, this.noText, button -> this.callback.accept(false)));
    }

    protected void addButton(ButtonWidget button) {
        this.buttons.add(this.addDrawableChild(button));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        ConfirmScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, this.getTitleY(), 0xFFFFFF);
        this.messageSplit.drawCenterWithShadow(matrices, this.width / 2, this.getMessageY());
        super.render(matrices, mouseX, mouseY, delta);
    }

    private int getTitleY() {
        int i = (this.height - this.getMessagesHeight()) / 2;
        return MathHelper.clamp(i - 20 - this.textRenderer.fontHeight, 10, 80);
    }

    private int getMessageY() {
        return this.getTitleY() + 20;
    }

    private int getMessagesHeight() {
        return this.messageSplit.count() * this.textRenderer.fontHeight;
    }

    public void disableButtons(int ticks) {
        this.buttonEnableTimer = ticks;
        for (ButtonWidget buttonWidget : this.buttons) {
            buttonWidget.active = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (--this.buttonEnableTimer == 0) {
            for (ButtonWidget buttonWidget : this.buttons) {
                buttonWidget.active = true;
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.callback.accept(false);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

