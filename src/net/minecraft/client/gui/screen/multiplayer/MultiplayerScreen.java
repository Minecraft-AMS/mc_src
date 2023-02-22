/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MultiplayerScreen
extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MultiplayerServerListPinger field_3037 = new MultiplayerServerListPinger();
    private final Screen parent;
    protected MultiplayerServerListWidget serverListWidget;
    private ServerList serverList;
    private ButtonWidget buttonEdit;
    private ButtonWidget buttonJoin;
    private ButtonWidget buttonDelete;
    private String tooltipText;
    private ServerInfo selectedEntry;
    private LanServerQueryManager.LanServerEntryList lanServers;
    private LanServerQueryManager.LanServerDetector lanServerDetector;
    private boolean initialized;

    public MultiplayerScreen(Screen parent) {
        super(new TranslatableText("multiplayer.title", new Object[0]));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboard.enableRepeatEvents(true);
        if (this.initialized) {
            this.serverListWidget.updateSize(this.width, this.height, 32, this.height - 64);
        } else {
            this.initialized = true;
            this.serverList = new ServerList(this.minecraft);
            this.serverList.loadFile();
            this.lanServers = new LanServerQueryManager.LanServerEntryList();
            try {
                this.lanServerDetector = new LanServerQueryManager.LanServerDetector(this.lanServers);
                this.lanServerDetector.start();
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to start LAN server detection: {}", (Object)exception.getMessage());
            }
            this.serverListWidget = new MultiplayerServerListWidget(this, this.minecraft, this.width, this.height, 32, this.height - 64, 36);
            this.serverListWidget.setServers(this.serverList);
        }
        this.children.add(this.serverListWidget);
        this.buttonJoin = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 52, 100, 20, I18n.translate("selectServer.select", new Object[0]), buttonWidget -> this.connect()));
        this.addButton(new ButtonWidget(this.width / 2 - 50, this.height - 52, 100, 20, I18n.translate("selectServer.direct", new Object[0]), buttonWidget -> {
            this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName", new Object[0]), "", false);
            this.minecraft.openScreen(new DirectConnectScreen(this::directConnect, this.selectedEntry));
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.translate("selectServer.add", new Object[0]), buttonWidget -> {
            this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName", new Object[0]), "", false);
            this.minecraft.openScreen(new AddServerScreen(this::addEntry, this.selectedEntry));
        }));
        this.buttonEdit = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 28, 70, 20, I18n.translate("selectServer.edit", new Object[0]), buttonWidget -> {
            MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelected();
            if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
                ServerInfo serverInfo = ((MultiplayerServerListWidget.ServerEntry)entry).getServer();
                this.selectedEntry = new ServerInfo(serverInfo.name, serverInfo.address, false);
                this.selectedEntry.copyFrom(serverInfo);
                this.minecraft.openScreen(new AddServerScreen(this::editEntry, this.selectedEntry));
            }
        }));
        this.buttonDelete = this.addButton(new ButtonWidget(this.width / 2 - 74, this.height - 28, 70, 20, I18n.translate("selectServer.delete", new Object[0]), buttonWidget -> {
            String string;
            MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelected();
            if (entry instanceof MultiplayerServerListWidget.ServerEntry && (string = ((MultiplayerServerListWidget.ServerEntry)entry).getServer().name) != null) {
                TranslatableText text = new TranslatableText("selectServer.deleteQuestion", new Object[0]);
                TranslatableText text2 = new TranslatableText("selectServer.deleteWarning", string);
                String string2 = I18n.translate("selectServer.deleteButton", new Object[0]);
                String string3 = I18n.translate("gui.cancel", new Object[0]);
                this.minecraft.openScreen(new ConfirmScreen(this::removeEntry, text, text2, string2, string3));
            }
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 28, 70, 20, I18n.translate("selectServer.refresh", new Object[0]), buttonWidget -> this.refresh()));
        this.addButton(new ButtonWidget(this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> this.minecraft.openScreen(this.parent)));
        this.updateButtonActivationStates();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.lanServers.needsUpdate()) {
            List<LanServerInfo> list = this.lanServers.getServers();
            this.lanServers.markClean();
            this.serverListWidget.setLanServers(list);
        }
        this.field_3037.method_3000();
    }

    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
        if (this.lanServerDetector != null) {
            this.lanServerDetector.interrupt();
            this.lanServerDetector = null;
        }
        this.field_3037.method_3004();
    }

    private void refresh() {
        this.minecraft.openScreen(new MultiplayerScreen(this.parent));
    }

    private void removeEntry(boolean confirmedAction) {
        MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelected();
        if (confirmedAction && entry instanceof MultiplayerServerListWidget.ServerEntry) {
            this.serverList.remove(((MultiplayerServerListWidget.ServerEntry)entry).getServer());
            this.serverList.saveFile();
            this.serverListWidget.setSelected((MultiplayerServerListWidget.Entry)null);
            this.serverListWidget.setServers(this.serverList);
        }
        this.minecraft.openScreen(this);
    }

    private void editEntry(boolean confirmedAction) {
        MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelected();
        if (confirmedAction && entry instanceof MultiplayerServerListWidget.ServerEntry) {
            ServerInfo serverInfo = ((MultiplayerServerListWidget.ServerEntry)entry).getServer();
            serverInfo.name = this.selectedEntry.name;
            serverInfo.address = this.selectedEntry.address;
            serverInfo.copyFrom(this.selectedEntry);
            this.serverList.saveFile();
            this.serverListWidget.setServers(this.serverList);
        }
        this.minecraft.openScreen(this);
    }

    private void addEntry(boolean confirmedAction) {
        if (confirmedAction) {
            this.serverList.add(this.selectedEntry);
            this.serverList.saveFile();
            this.serverListWidget.setSelected((MultiplayerServerListWidget.Entry)null);
            this.serverListWidget.setServers(this.serverList);
        }
        this.minecraft.openScreen(this);
    }

    private void directConnect(boolean confirmedAction) {
        if (confirmedAction) {
            this.connect(this.selectedEntry);
        } else {
            this.minecraft.openScreen(this);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 294) {
            this.refresh();
            return true;
        }
        if (this.serverListWidget.getSelected() != null && (keyCode == 257 || keyCode == 335)) {
            this.connect();
            return true;
        }
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.tooltipText = null;
        this.renderBackground();
        this.serverListWidget.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
        if (this.tooltipText != null) {
            this.renderTooltip(Lists.newArrayList((Iterable)Splitter.on((String)"\n").split((CharSequence)this.tooltipText)), mouseX, mouseY);
        }
    }

    public void connect() {
        MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelected();
        if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
            this.connect(((MultiplayerServerListWidget.ServerEntry)entry).getServer());
        } else if (entry instanceof MultiplayerServerListWidget.LanServerListEntry) {
            LanServerInfo lanServerInfo = ((MultiplayerServerListWidget.LanServerListEntry)entry).getLanServerEntry();
            this.connect(new ServerInfo(lanServerInfo.getMotd(), lanServerInfo.getAddressPort(), true));
        }
    }

    private void connect(ServerInfo entry) {
        this.minecraft.openScreen(new ConnectScreen(this, this.minecraft, entry));
    }

    public void select(MultiplayerServerListWidget.Entry entry) {
        this.serverListWidget.setSelected(entry);
        this.updateButtonActivationStates();
    }

    protected void updateButtonActivationStates() {
        this.buttonJoin.active = false;
        this.buttonEdit.active = false;
        this.buttonDelete.active = false;
        MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelected();
        if (entry != null && !(entry instanceof MultiplayerServerListWidget.ScanningEntry)) {
            this.buttonJoin.active = true;
            if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
                this.buttonEdit.active = true;
                this.buttonDelete.active = true;
            }
        }
    }

    public MultiplayerServerListPinger method_2538() {
        return this.field_3037;
    }

    public void setTooltip(String text) {
        this.tooltipText = text;
    }

    public ServerList getServerList() {
        return this.serverList;
    }
}

