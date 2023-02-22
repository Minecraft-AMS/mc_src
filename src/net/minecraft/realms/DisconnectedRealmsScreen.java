/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class DisconnectedRealmsScreen
extends RealmsScreen {
    private final String title;
    private final Text reason;
    private List<String> lines;
    private final RealmsScreen parent;
    private int textHeight;

    public DisconnectedRealmsScreen(RealmsScreen realmsScreen, String string, Text text) {
        this.parent = realmsScreen;
        this.title = DisconnectedRealmsScreen.getLocalizedString(string);
        this.reason = text;
    }

    @Override
    public void init() {
        Realms.setConnectedToRealms(false);
        Realms.clearResourcePack();
        Realms.narrateNow(this.title + ": " + this.reason.getString());
        this.lines = this.fontSplit(this.reason.asFormattedString(), this.width() - 50);
        this.textHeight = this.lines.size() * this.fontLineHeight();
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, this.height() / 2 + this.textHeight / 2 + this.fontLineHeight(), DisconnectedRealmsScreen.getLocalizedString("gui.back")){

            @Override
            public void onPress() {
                Realms.setScreen(DisconnectedRealmsScreen.this.parent);
            }
        });
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            Realms.setScreen(this.parent);
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        this.drawCenteredString(this.title, this.width() / 2, this.height() / 2 - this.textHeight / 2 - this.fontLineHeight() * 2, 0xAAAAAA);
        int k = this.height() / 2 - this.textHeight / 2;
        if (this.lines != null) {
            for (String string : this.lines) {
                this.drawCenteredString(string, this.width() / 2, k, 0xFFFFFF);
                k += this.fontLineHeight();
            }
        }
        super.render(i, j, f);
    }
}

