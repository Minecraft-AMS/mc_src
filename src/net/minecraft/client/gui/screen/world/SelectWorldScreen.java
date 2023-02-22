/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class SelectWorldScreen
extends Screen {
    protected final Screen parent;
    private String tooltipText;
    private ButtonWidget deleteButton;
    private ButtonWidget selectButton;
    private ButtonWidget editButton;
    private ButtonWidget recreateButton;
    protected TextFieldWidget searchBox;
    private WorldListWidget levelList;

    public SelectWorldScreen(Screen parent) {
        super(new TranslatableText("selectWorld.title", new Object[0]));
        this.parent = parent;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double amount) {
        return super.mouseScrolled(d, e, amount);
    }

    @Override
    public void tick() {
        this.searchBox.tick();
    }

    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.searchBox = new TextFieldWidget(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, I18n.translate("selectWorld.search", new Object[0]));
        this.searchBox.setChangedListener(string -> this.levelList.filter(() -> string, false));
        this.levelList = new WorldListWidget(this, this.minecraft, this.width, this.height, 48, this.height - 64, 36, () -> this.searchBox.getText(), this.levelList);
        this.children.add(this.searchBox);
        this.children.add(this.levelList);
        this.selectButton = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 52, 150, 20, I18n.translate("selectWorld.select", new Object[0]), buttonWidget -> this.levelList.method_20159().ifPresent(WorldListWidget.Entry::play)));
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 52, 150, 20, I18n.translate("selectWorld.create", new Object[0]), buttonWidget -> this.minecraft.openScreen(new CreateWorldScreen(this))));
        this.editButton = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 28, 72, 20, I18n.translate("selectWorld.edit", new Object[0]), buttonWidget -> this.levelList.method_20159().ifPresent(WorldListWidget.Entry::edit)));
        this.deleteButton = this.addButton(new ButtonWidget(this.width / 2 - 76, this.height - 28, 72, 20, I18n.translate("selectWorld.delete", new Object[0]), buttonWidget -> this.levelList.method_20159().ifPresent(WorldListWidget.Entry::delete)));
        this.recreateButton = this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 28, 72, 20, I18n.translate("selectWorld.recreate", new Object[0]), buttonWidget -> this.levelList.method_20159().ifPresent(WorldListWidget.Entry::recreate)));
        this.addButton(new ButtonWidget(this.width / 2 + 82, this.height - 28, 72, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> this.minecraft.openScreen(this.parent)));
        this.worldSelected(false);
        this.setInitialFocus(this.searchBox);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return this.searchBox.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        return this.searchBox.charTyped(chr, keyCode);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.tooltipText = null;
        this.levelList.render(mouseX, mouseY, delta);
        this.searchBox.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 8, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
        if (this.tooltipText != null) {
            this.renderTooltip(Lists.newArrayList((Iterable)Splitter.on((String)"\n").split((CharSequence)this.tooltipText)), mouseX, mouseY);
        }
    }

    public void setTooltip(String value) {
        this.tooltipText = value;
    }

    public void worldSelected(boolean active) {
        this.selectButton.active = active;
        this.deleteButton.active = active;
        this.editButton.active = active;
        this.recreateButton.active = active;
    }

    @Override
    public void removed() {
        if (this.levelList != null) {
            this.levelList.children().forEach(WorldListWidget.Entry::close);
        }
    }
}

