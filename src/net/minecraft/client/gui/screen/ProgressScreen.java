/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ProgressListener;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ProgressScreen
extends Screen
implements ProgressListener {
    @Nullable
    private Text title;
    @Nullable
    private Text task;
    private int progress;
    private boolean done;

    public ProgressScreen() {
        super(NarratorManager.EMPTY);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void method_15412(Text text) {
        this.method_15413(text);
    }

    @Override
    public void method_15413(Text text) {
        this.title = text;
        this.method_15414(new TranslatableText("progress.working"));
    }

    @Override
    public void method_15414(Text text) {
        this.task = text;
        this.progressStagePercentage(0);
    }

    @Override
    public void progressStagePercentage(int percentage) {
        this.progress = percentage;
    }

    @Override
    public void setDone() {
        this.done = true;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.done) {
            if (!this.client.isConnectedToRealms()) {
                this.client.openScreen(null);
            }
            return;
        }
        this.renderBackground(matrices);
        if (this.title != null) {
            ProgressScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 70, 0xFFFFFF);
        }
        if (this.task != null && this.progress != 0) {
            ProgressScreen.drawCenteredText(matrices, this.textRenderer, new LiteralText("").append(this.task).append(" " + this.progress + "%"), this.width / 2, 90, 0xFFFFFF);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
}

