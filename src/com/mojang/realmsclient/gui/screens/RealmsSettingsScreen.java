/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;

@Environment(value=EnvType.CLIENT)
public class RealmsSettingsScreen
extends RealmsScreen {
    private final RealmsConfigureWorldScreen configureWorldScreen;
    private final RealmsServer serverData;
    private final int COMPONENT_WIDTH = 212;
    private RealmsButton doneButton;
    private RealmsEditBox descEdit;
    private RealmsEditBox nameEdit;
    private RealmsLabel titleLabel;

    public RealmsSettingsScreen(RealmsConfigureWorldScreen configureWorldScreen, RealmsServer serverData) {
        this.configureWorldScreen = configureWorldScreen;
        this.serverData = serverData;
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
        this.descEdit.tick();
        this.doneButton.active(this.nameEdit.getValue() != null && !this.nameEdit.getValue().trim().isEmpty());
    }

    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        int i = this.width() / 2 - 106;
        this.doneButton = new RealmsButton(1, i - 2, RealmsConstants.row(12), 106, 20, RealmsSettingsScreen.getLocalizedString("mco.configure.world.buttons.done")){

            @Override
            public void onPress() {
                RealmsSettingsScreen.this.save();
            }
        };
        this.buttonsAdd(this.doneButton);
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 2, RealmsConstants.row(12), 106, 20, RealmsSettingsScreen.getLocalizedString("gui.cancel")){

            @Override
            public void onPress() {
                Realms.setScreen(RealmsSettingsScreen.this.configureWorldScreen);
            }
        });
        this.buttonsAdd(new RealmsButton(5, this.width() / 2 - 53, RealmsConstants.row(0), 106, 20, RealmsSettingsScreen.getLocalizedString(this.serverData.state.equals((Object)RealmsServer.State.OPEN) ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open")){

            @Override
            public void onPress() {
                if (((RealmsSettingsScreen)RealmsSettingsScreen.this).serverData.state.equals((Object)RealmsServer.State.OPEN)) {
                    String string = RealmsScreen.getLocalizedString("mco.configure.world.close.question.line1");
                    String string2 = RealmsScreen.getLocalizedString("mco.configure.world.close.question.line2");
                    Realms.setScreen(new RealmsLongConfirmationScreen(RealmsSettingsScreen.this, RealmsLongConfirmationScreen.Type.Info, string, string2, true, 5));
                } else {
                    RealmsSettingsScreen.this.configureWorldScreen.openTheWorld(false, RealmsSettingsScreen.this);
                }
            }
        });
        this.nameEdit = this.newEditBox(2, i, RealmsConstants.row(4), 212, 20, RealmsSettingsScreen.getLocalizedString("mco.configure.world.name"));
        this.nameEdit.setMaxLength(32);
        if (this.serverData.getName() != null) {
            this.nameEdit.setValue(this.serverData.getName());
        }
        this.addWidget(this.nameEdit);
        this.focusOn(this.nameEdit);
        this.descEdit = this.newEditBox(3, i, RealmsConstants.row(8), 212, 20, RealmsSettingsScreen.getLocalizedString("mco.configure.world.description"));
        this.descEdit.setMaxLength(32);
        if (this.serverData.getDescription() != null) {
            this.descEdit.setValue(this.serverData.getDescription());
        }
        this.addWidget(this.descEdit);
        this.titleLabel = new RealmsLabel(RealmsSettingsScreen.getLocalizedString("mco.configure.world.settings.title"), this.width() / 2, 17, 0xFFFFFF);
        this.addWidget(this.titleLabel);
        this.narrateLabels();
    }

    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }

    @Override
    public void confirmResult(boolean result, int id) {
        switch (id) {
            case 5: {
                if (result) {
                    this.configureWorldScreen.closeTheWorld(this);
                    break;
                }
                Realms.setScreen(this);
            }
        }
    }

    @Override
    public boolean keyPressed(int eventKey, int scancode, int mods) {
        switch (eventKey) {
            case 256: {
                Realms.setScreen(this.configureWorldScreen);
                return true;
            }
        }
        return super.keyPressed(eventKey, scancode, mods);
    }

    @Override
    public void render(int xm, int ym, float a) {
        this.renderBackground();
        this.titleLabel.render(this);
        this.drawString(RealmsSettingsScreen.getLocalizedString("mco.configure.world.name"), this.width() / 2 - 106, RealmsConstants.row(3), 0xA0A0A0);
        this.drawString(RealmsSettingsScreen.getLocalizedString("mco.configure.world.description"), this.width() / 2 - 106, RealmsConstants.row(7), 0xA0A0A0);
        this.nameEdit.render(xm, ym, a);
        this.descEdit.render(xm, ym, a);
        super.render(xm, ym, a);
    }

    public void save() {
        this.configureWorldScreen.saveSettings(this.nameEdit.getValue(), this.descEdit.getValue());
    }
}

