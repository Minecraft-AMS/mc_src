/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.resourcepack;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.screen.resourcepack.AvailableResourcePackListWidget;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackListWidget;
import net.minecraft.client.gui.screen.resourcepack.SelectedResourcePackListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class ResourcePackOptionsScreen
extends GameOptionsScreen {
    private AvailableResourcePackListWidget availablePacks;
    private SelectedResourcePackListWidget enabledPacks;
    private boolean dirty;

    public ResourcePackOptionsScreen(Screen parent, GameOptions gameOptions) {
        super(parent, gameOptions, new TranslatableText("resourcePack.title", new Object[0]));
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 48, 150, 20, I18n.translate("resourcePack.openFolder", new Object[0]), buttonWidget -> Util.getOperatingSystem().open(this.minecraft.getResourcePackDir())));
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 48, 150, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> {
            if (this.dirty) {
                ArrayList list = Lists.newArrayList();
                for (ResourcePackListWidget.ResourcePackEntry resourcePackEntry : this.enabledPacks.children()) {
                    list.add(resourcePackEntry.getPack());
                }
                Collections.reverse(list);
                this.minecraft.getResourcePackManager().setEnabledProfiles(list);
                this.gameOptions.resourcePacks.clear();
                this.gameOptions.incompatibleResourcePacks.clear();
                for (ClientResourcePackProfile clientResourcePackProfile : list) {
                    if (clientResourcePackProfile.isPinned()) continue;
                    this.gameOptions.resourcePacks.add(clientResourcePackProfile.getName());
                    if (clientResourcePackProfile.getCompatibility().isCompatible()) continue;
                    this.gameOptions.incompatibleResourcePacks.add(clientResourcePackProfile.getName());
                }
                this.gameOptions.write();
                this.minecraft.openScreen(this.parent);
                this.minecraft.reloadResources();
            } else {
                this.minecraft.openScreen(this.parent);
            }
        }));
        AvailableResourcePackListWidget availableResourcePackListWidget = this.availablePacks;
        SelectedResourcePackListWidget selectedResourcePackListWidget = this.enabledPacks;
        this.availablePacks = new AvailableResourcePackListWidget(this.minecraft, 200, this.height);
        this.availablePacks.setLeftPos(this.width / 2 - 4 - 200);
        if (availableResourcePackListWidget != null) {
            this.availablePacks.children().addAll(availableResourcePackListWidget.children());
        }
        this.children.add(this.availablePacks);
        this.enabledPacks = new SelectedResourcePackListWidget(this.minecraft, 200, this.height);
        this.enabledPacks.setLeftPos(this.width / 2 + 4);
        if (selectedResourcePackListWidget != null) {
            selectedResourcePackListWidget.children().forEach(resourcePackEntry -> {
                this.enabledPacks.children().add((ResourcePackListWidget.ResourcePackEntry)resourcePackEntry);
                resourcePackEntry.method_24232(this.enabledPacks);
            });
        }
        this.children.add(this.enabledPacks);
        if (!this.dirty) {
            this.availablePacks.children().clear();
            this.enabledPacks.children().clear();
            ResourcePackManager<ClientResourcePackProfile> resourcePackManager = this.minecraft.getResourcePackManager();
            resourcePackManager.scanPacks();
            ArrayList list = Lists.newArrayList(resourcePackManager.getProfiles());
            list.removeAll(resourcePackManager.getEnabledProfiles());
            for (ClientResourcePackProfile clientResourcePackProfile : list) {
                this.availablePacks.add(new ResourcePackListWidget.ResourcePackEntry(this.availablePacks, this, clientResourcePackProfile));
            }
            for (ClientResourcePackProfile clientResourcePackProfile : Lists.reverse((List)Lists.newArrayList(resourcePackManager.getEnabledProfiles()))) {
                this.enabledPacks.add(new ResourcePackListWidget.ResourcePackEntry(this.enabledPacks, this, clientResourcePackProfile));
            }
        }
    }

    public void enable(ResourcePackListWidget.ResourcePackEntry resourcePack) {
        this.availablePacks.children().remove(resourcePack);
        resourcePack.enable(this.enabledPacks);
        this.markDirty();
    }

    public void disable(ResourcePackListWidget.ResourcePackEntry resourcePack) {
        this.enabledPacks.children().remove(resourcePack);
        this.availablePacks.add(resourcePack);
        this.markDirty();
    }

    public boolean isEnabled(ResourcePackListWidget.ResourcePackEntry resourcePack) {
        return this.enabledPacks.children().contains(resourcePack);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderDirtBackground(0);
        this.availablePacks.render(mouseX, mouseY, delta);
        this.enabledPacks.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 16, 0xFFFFFF);
        this.drawCenteredString(this.font, I18n.translate("resourcePack.folderInfo", new Object[0]), this.width / 2 - 77, this.height - 26, 0x808080);
        super.render(mouseX, mouseY, delta);
    }

    public void markDirty() {
        this.dirty = true;
    }
}

