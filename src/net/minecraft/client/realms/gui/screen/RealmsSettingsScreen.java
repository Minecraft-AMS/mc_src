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
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongConfirmationScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RealmsSettingsScreen
extends RealmsScreen {
    private static final int TEXT_FIELD_WIDTH = 212;
    private static final Text WORLD_NAME_TEXT = Text.translatable("mco.configure.world.name");
    private static final Text WORLD_DESCRIPTION_TEXT = Text.translatable("mco.configure.world.description");
    private final RealmsConfigureWorldScreen parent;
    private final RealmsServer serverData;
    private ButtonWidget doneButton;
    private TextFieldWidget descEdit;
    private TextFieldWidget nameEdit;

    public RealmsSettingsScreen(RealmsConfigureWorldScreen parent, RealmsServer serverData) {
        super(Text.translatable("mco.configure.world.settings.title"));
        this.parent = parent;
        this.serverData = serverData;
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
        this.descEdit.tick();
        this.doneButton.active = !this.nameEdit.getText().trim().isEmpty();
    }

    @Override
    public void init() {
        int i = this.width / 2 - 106;
        this.doneButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.configure.world.buttons.done"), button -> this.save()).dimensions(i - 2, RealmsSettingsScreen.row(12), 106, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 + 2, RealmsSettingsScreen.row(12), 106, 20).build());
        String string = this.serverData.state == RealmsServer.State.OPEN ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open";
        ButtonWidget buttonWidget = ButtonWidget.builder(Text.translatable(string), button -> {
            if (this.serverData.state == RealmsServer.State.OPEN) {
                MutableText text = Text.translatable("mco.configure.world.close.question.line1");
                MutableText text2 = Text.translatable("mco.configure.world.close.question.line2");
                this.client.setScreen(new RealmsLongConfirmationScreen(confirmed -> {
                    if (confirmed) {
                        this.parent.closeTheWorld(this);
                    } else {
                        this.client.setScreen(this);
                    }
                }, RealmsLongConfirmationScreen.Type.INFO, text, text2, true));
            } else {
                this.parent.openTheWorld(false, this);
            }
        }).dimensions(this.width / 2 - 53, RealmsSettingsScreen.row(0), 106, 20).build();
        this.addDrawableChild(buttonWidget);
        this.nameEdit = new TextFieldWidget(this.client.textRenderer, i, RealmsSettingsScreen.row(4), 212, 20, null, Text.translatable("mco.configure.world.name"));
        this.nameEdit.setMaxLength(32);
        this.nameEdit.setText(this.serverData.getName());
        this.addSelectableChild(this.nameEdit);
        this.focusOn(this.nameEdit);
        this.descEdit = new TextFieldWidget(this.client.textRenderer, i, RealmsSettingsScreen.row(8), 212, 20, null, Text.translatable("mco.configure.world.description"));
        this.descEdit.setMaxLength(32);
        this.descEdit.setText(this.serverData.getDescription());
        this.addSelectableChild(this.descEdit);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.setScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        RealmsSettingsScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 17, 0xFFFFFF);
        this.textRenderer.draw(matrices, WORLD_NAME_TEXT, (float)(this.width / 2 - 106), (float)RealmsSettingsScreen.row(3), 0xA0A0A0);
        this.textRenderer.draw(matrices, WORLD_DESCRIPTION_TEXT, (float)(this.width / 2 - 106), (float)RealmsSettingsScreen.row(7), 0xA0A0A0);
        this.nameEdit.render(matrices, mouseX, mouseY, delta);
        this.descEdit.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void save() {
        this.parent.saveSettings(this.nameEdit.getText(), this.descEdit.getText());
    }
}

