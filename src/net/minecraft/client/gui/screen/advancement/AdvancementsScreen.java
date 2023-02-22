/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.advancement;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class AdvancementsScreen
extends Screen
implements ClientAdvancementManager.Listener {
    private static final Identifier WINDOW_TEXTURE = new Identifier("textures/gui/advancements/window.png");
    private static final Identifier TABS_TEXTURE = new Identifier("textures/gui/advancements/tabs.png");
    private final ClientAdvancementManager advancementHandler;
    private final Map<Advancement, AdvancementTab> tabs = Maps.newLinkedHashMap();
    private AdvancementTab selectedTab;
    private boolean field_2718;

    public AdvancementsScreen(ClientAdvancementManager clientAdvancementManager) {
        super(NarratorManager.EMPTY);
        this.advancementHandler = clientAdvancementManager;
    }

    @Override
    protected void init() {
        this.tabs.clear();
        this.selectedTab = null;
        this.advancementHandler.setListener(this);
        if (this.selectedTab == null && !this.tabs.isEmpty()) {
            this.advancementHandler.selectTab(this.tabs.values().iterator().next().method_2307(), true);
        } else {
            this.advancementHandler.selectTab(this.selectedTab == null ? null : this.selectedTab.method_2307(), true);
        }
    }

    @Override
    public void removed() {
        this.advancementHandler.setListener(null);
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.minecraft.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            clientPlayNetworkHandler.sendPacket(AdvancementTabC2SPacket.close());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int i = (this.width - 252) / 2;
            int j = (this.height - 140) / 2;
            for (AdvancementTab advancementTab : this.tabs.values()) {
                if (!advancementTab.method_2316(i, j, mouseX, mouseY)) continue;
                this.advancementHandler.selectTab(advancementTab.method_2307(), true);
                break;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.minecraft.options.keyAdvancements.matchesKey(keyCode, scanCode)) {
            this.minecraft.openScreen(null);
            this.minecraft.mouse.lockCursor();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        int i = (this.width - 252) / 2;
        int j = (this.height - 140) / 2;
        this.renderBackground();
        this.drawAdvancementTree(mouseX, mouseY, i, j);
        this.drawWidgets(i, j);
        this.drawWidgetTooltip(mouseX, mouseY, i, j);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button != 0) {
            this.field_2718 = false;
            return false;
        }
        if (!this.field_2718) {
            this.field_2718 = true;
        } else if (this.selectedTab != null) {
            this.selectedTab.method_2313(deltaX, deltaY);
        }
        return true;
    }

    private void drawAdvancementTree(int mouseX, int mouseY, int x, int y) {
        AdvancementTab advancementTab = this.selectedTab;
        if (advancementTab == null) {
            AdvancementsScreen.fill(x + 9, y + 18, x + 9 + 234, y + 18 + 113, -16777216);
            String string = I18n.translate("advancements.empty", new Object[0]);
            int i = this.font.getStringWidth(string);
            this.font.draw(string, x + 9 + 117 - i / 2, y + 18 + 56 - this.font.fontHeight / 2, -1);
            this.font.draw(":(", x + 9 + 117 - this.font.getStringWidth(":(") / 2, y + 18 + 113 - this.font.fontHeight, -1);
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(x + 9, y + 18, -400.0f);
        GlStateManager.enableDepthTest();
        advancementTab.method_2310();
        GlStateManager.popMatrix();
        GlStateManager.depthFunc(515);
        GlStateManager.disableDepthTest();
    }

    public void drawWidgets(int x, int y) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        DiffuseLighting.disable();
        this.minecraft.getTextureManager().bindTexture(WINDOW_TEXTURE);
        this.blit(x, y, 0, 0, 252, 140);
        if (this.tabs.size() > 1) {
            this.minecraft.getTextureManager().bindTexture(TABS_TEXTURE);
            for (AdvancementTab advancementTab : this.tabs.values()) {
                advancementTab.drawBackground(x, y, advancementTab == this.selectedTab);
            }
            GlStateManager.enableRescaleNormal();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            DiffuseLighting.enableForItems();
            for (AdvancementTab advancementTab : this.tabs.values()) {
                advancementTab.drawIcon(x, y, this.itemRenderer);
            }
            GlStateManager.disableBlend();
        }
        this.font.draw(I18n.translate("gui.advancements", new Object[0]), x + 8, y + 6, 0x404040);
    }

    private void drawWidgetTooltip(int mouseX, int mouseY, int x, int y) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.selectedTab != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableDepthTest();
            GlStateManager.translatef(x + 9, y + 18, 400.0f);
            this.selectedTab.method_2314(mouseX - x - 9, mouseY - y - 18, x, y);
            GlStateManager.disableDepthTest();
            GlStateManager.popMatrix();
        }
        if (this.tabs.size() > 1) {
            for (AdvancementTab advancementTab : this.tabs.values()) {
                if (!advancementTab.method_2316(x, y, mouseX, mouseY)) continue;
                this.renderTooltip(advancementTab.method_2309(), mouseX, mouseY);
            }
        }
    }

    @Override
    public void onRootAdded(Advancement advancement) {
        AdvancementTab advancementTab = AdvancementTab.create(this.minecraft, this, this.tabs.size(), advancement);
        if (advancementTab == null) {
            return;
        }
        this.tabs.put(advancement, advancementTab);
    }

    @Override
    public void onRootRemoved(Advancement advancement) {
    }

    @Override
    public void onDependentAdded(Advancement advancement) {
        AdvancementTab advancementTab = this.getTab(advancement);
        if (advancementTab != null) {
            advancementTab.method_2318(advancement);
        }
    }

    @Override
    public void onDependentRemoved(Advancement advancement) {
    }

    @Override
    public void setProgress(Advancement advancement, AdvancementProgress advancementProgress) {
        AdvancementWidget advancementWidget = this.getAdvancementWidget(advancement);
        if (advancementWidget != null) {
            advancementWidget.setProgress(advancementProgress);
        }
    }

    @Override
    public void selectTab(@Nullable Advancement advancement) {
        this.selectedTab = this.tabs.get(advancement);
    }

    @Override
    public void onClear() {
        this.tabs.clear();
        this.selectedTab = null;
    }

    @Nullable
    public AdvancementWidget getAdvancementWidget(Advancement advancement) {
        AdvancementTab advancementTab = this.getTab(advancement);
        return advancementTab == null ? null : advancementTab.getWidget(advancement);
    }

    @Nullable
    private AdvancementTab getTab(Advancement advancement) {
        while (advancement.getParent() != null) {
            advancement = advancement.getParent();
        }
        return this.tabs.get(advancement);
    }
}

