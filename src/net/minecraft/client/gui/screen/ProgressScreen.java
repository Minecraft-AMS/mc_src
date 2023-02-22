/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ProgressListener;

@Environment(value=EnvType.CLIENT)
public class ProgressScreen
extends Screen
implements ProgressListener {
    private String title = "";
    private String task = "";
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
        this.title = text.asFormattedString();
        this.method_15414(new TranslatableText("progress.working", new Object[0]));
    }

    @Override
    public void method_15414(Text text) {
        this.task = text.asFormattedString();
        this.progressStagePercentage(0);
    }

    @Override
    public void progressStagePercentage(int i) {
        this.progress = i;
    }

    @Override
    public void setDone() {
        this.done = true;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (this.done) {
            if (!this.minecraft.isConnectedToRealms()) {
                this.minecraft.openScreen(null);
            }
            return;
        }
        this.renderBackground();
        this.drawCenteredString(this.font, this.title, this.width / 2, 70, 0xFFFFFF);
        if (!Objects.equals(this.task, "") && this.progress != 0) {
            this.drawCenteredString(this.font, this.task + " " + this.progress + "%", this.width / 2, 90, 0xFFFFFF);
        }
        super.render(mouseX, mouseY, delta);
    }
}

