/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class DirectConnectScreen
extends Screen {
    private ButtonWidget selectServerButton;
    private final ServerInfo serverEntry;
    private TextFieldWidget addressField;
    private final BooleanConsumer callback;

    public DirectConnectScreen(BooleanConsumer callback, ServerInfo serverEntry) {
        super(new TranslatableText("selectServer.direct", new Object[0]));
        this.serverEntry = serverEntry;
        this.callback = callback;
    }

    @Override
    public void tick() {
        this.addressField.tick();
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (this.getFocused() == this.addressField && (i == 257 || i == 335)) {
            this.saveAndClose();
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.selectServerButton = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, I18n.translate("selectServer.select", new Object[0]), buttonWidget -> this.saveAndClose()));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> this.callback.accept(false)));
        this.addressField = new TextFieldWidget(this.font, this.width / 2 - 100, 116, 200, 20, I18n.translate("addServer.enterIp", new Object[0]));
        this.addressField.setMaxLength(128);
        this.addressField.method_1876(true);
        this.addressField.setText(this.minecraft.options.lastServer);
        this.addressField.setChangedListener(text -> this.onAddressFieldChanged());
        this.children.add(this.addressField);
        this.setInitialFocus(this.addressField);
        this.onAddressFieldChanged();
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.addressField.getText();
        this.init(client, width, height);
        this.addressField.setText(string);
    }

    private void saveAndClose() {
        this.serverEntry.address = this.addressField.getText();
        this.callback.accept(true);
    }

    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
        this.minecraft.options.lastServer = this.addressField.getText();
        this.minecraft.options.write();
    }

    private void onAddressFieldChanged() {
        this.selectServerButton.active = !this.addressField.getText().isEmpty() && this.addressField.getText().split(":").length > 0;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 0xFFFFFF);
        this.drawString(this.font, I18n.translate("addServer.enterIp", new Object[0]), this.width / 2 - 100, 100, 0xA0A0A0);
        this.addressField.render(mouseX, mouseY, delta);
        super.render(mouseX, mouseY, delta);
    }
}
