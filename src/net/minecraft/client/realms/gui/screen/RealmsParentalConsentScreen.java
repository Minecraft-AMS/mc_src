/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class RealmsParentalConsentScreen
extends RealmsScreen {
    private static final Text field_26491 = new TranslatableText("mco.account.privacyinfo");
    private final Screen parent;
    private MultilineText field_26492 = MultilineText.EMPTY;

    public RealmsParentalConsentScreen(Screen screen) {
        this.parent = screen;
    }

    @Override
    public void init() {
        Realms.narrateNow(field_26491.getString());
        TranslatableText text = new TranslatableText("mco.account.update");
        Text text2 = ScreenTexts.BACK;
        int i = Math.max(this.textRenderer.getWidth(text), this.textRenderer.getWidth(text2)) + 30;
        TranslatableText text3 = new TranslatableText("mco.account.privacy.info");
        int j = (int)((double)this.textRenderer.getWidth(text3) * 1.2);
        this.addButton(new ButtonWidget(this.width / 2 - j / 2, RealmsParentalConsentScreen.row(11), j, 20, text3, buttonWidget -> Util.getOperatingSystem().open("https://aka.ms/MinecraftGDPR")));
        this.addButton(new ButtonWidget(this.width / 2 - (i + 5), RealmsParentalConsentScreen.row(13), i, 20, text, buttonWidget -> Util.getOperatingSystem().open("https://aka.ms/UpdateMojangAccount")));
        this.addButton(new ButtonWidget(this.width / 2 + 5, RealmsParentalConsentScreen.row(13), i, 20, text2, buttonWidget -> this.client.openScreen(this.parent)));
        this.field_26492 = MultilineText.create(this.textRenderer, (StringVisitable)field_26491, (int)Math.round((double)this.width * 0.9));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.field_26492.drawCenterWithShadow(matrices, this.width / 2, 15, 15, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
