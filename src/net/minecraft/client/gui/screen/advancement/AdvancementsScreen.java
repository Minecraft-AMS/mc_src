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
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class AdvancementsScreen
extends Screen
implements ClientAdvancementManager.Listener {
    private static final Identifier WINDOW_TEXTURE = new Identifier("textures/gui/advancements/window.png");
    private static final Identifier TABS_TEXTURE = new Identifier("textures/gui/advancements/tabs.png");
    private static final Text SAD_LABEL_TEXT = new TranslatableText("advancements.sad_label");
    private static final Text EMPTY_TEXT = new TranslatableText("advancements.empty");
    private static final Text ADVANCEMENTS_TEXT = new TranslatableText("gui.advancements");
    private final ClientAdvancementManager advancementHandler;
    private final Map<Advancement, AdvancementTab> tabs = Maps.newLinkedHashMap();
    private AdvancementTab selectedTab;
    private boolean movingTab;

    public AdvancementsScreen(ClientAdvancementManager advancementHandler) {
        super(NarratorManager.EMPTY);
        this.advancementHandler = advancementHandler;
    }

    @Override
    protected void init() {
        this.tabs.clear();
        this.selectedTab = null;
        this.advancementHandler.setListener(this);
        if (this.selectedTab == null && !this.tabs.isEmpty()) {
            this.advancementHandler.selectTab(this.tabs.values().iterator().next().getRoot(), true);
        } else {
            this.advancementHandler.selectTab(this.selectedTab == null ? null : this.selectedTab.getRoot(), true);
        }
    }

    @Override
    public void removed() {
        this.advancementHandler.setListener(null);
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
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
                if (!advancementTab.isClickOnTab(i, j, mouseX, mouseY)) continue;
                this.advancementHandler.selectTab(advancementTab.getRoot(), true);
                break;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.client.options.keyAdvancements.matchesKey(keyCode, scanCode)) {
            this.client.openScreen(null);
            this.client.mouse.lockCursor();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int i = (this.width - 252) / 2;
        int j = (this.height - 140) / 2;
        this.renderBackground(matrices);
        this.drawAdvancementTree(matrices, mouseX, mouseY, i, j);
        this.drawWidgets(matrices, i, j);
        this.drawWidgetTooltip(matrices, mouseX, mouseY, i, j);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button != 0) {
            this.movingTab = false;
            return false;
        }
        if (!this.movingTab) {
            this.movingTab = true;
        } else if (this.selectedTab != null) {
            this.selectedTab.move(deltaX, deltaY);
        }
        return true;
    }

    private void drawAdvancementTree(MatrixStack matrices, int mouseY, int i, int j, int k) {
        AdvancementTab advancementTab = this.selectedTab;
        if (advancementTab == null) {
            AdvancementsScreen.fill(matrices, j + 9, k + 18, j + 9 + 234, k + 18 + 113, -16777216);
            int l = j + 9 + 117;
            AdvancementsScreen.drawCenteredText(matrices, this.textRenderer, EMPTY_TEXT, l, k + 18 + 56 - this.textRenderer.fontHeight / 2, -1);
            AdvancementsScreen.drawCenteredText(matrices, this.textRenderer, SAD_LABEL_TEXT, l, k + 18 + 113 - this.textRenderer.fontHeight, -1);
            return;
        }
        RenderSystem.pushMatrix();
        RenderSystem.translatef(j + 9, k + 18, 0.0f);
        advancementTab.render(matrices);
        RenderSystem.popMatrix();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
    }

    public void drawWidgets(MatrixStack matrices, int i, int j) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        this.client.getTextureManager().bindTexture(WINDOW_TEXTURE);
        this.drawTexture(matrices, i, j, 0, 0, 252, 140);
        if (this.tabs.size() > 1) {
            this.client.getTextureManager().bindTexture(TABS_TEXTURE);
            for (AdvancementTab advancementTab : this.tabs.values()) {
                advancementTab.drawBackground(matrices, i, j, advancementTab == this.selectedTab);
            }
            RenderSystem.enableRescaleNormal();
            RenderSystem.defaultBlendFunc();
            for (AdvancementTab advancementTab : this.tabs.values()) {
                advancementTab.drawIcon(i, j, this.itemRenderer);
            }
            RenderSystem.disableBlend();
        }
        this.textRenderer.draw(matrices, ADVANCEMENTS_TEXT, (float)(i + 8), (float)(j + 6), 0x404040);
    }

    private void drawWidgetTooltip(MatrixStack matrices, int i, int j, int k, int l) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.selectedTab != null) {
            RenderSystem.pushMatrix();
            RenderSystem.enableDepthTest();
            RenderSystem.translatef(k + 9, l + 18, 400.0f);
            this.selectedTab.drawWidgetTooltip(matrices, i - k - 9, j - l - 18, k, l);
            RenderSystem.disableDepthTest();
            RenderSystem.popMatrix();
        }
        if (this.tabs.size() > 1) {
            for (AdvancementTab advancementTab : this.tabs.values()) {
                if (!advancementTab.isClickOnTab(k, l, i, j)) continue;
                this.renderTooltip(matrices, advancementTab.getTitle(), i, j);
            }
        }
    }

    @Override
    public void onRootAdded(Advancement root) {
        AdvancementTab advancementTab = AdvancementTab.create(this.client, this, this.tabs.size(), root);
        if (advancementTab == null) {
            return;
        }
        this.tabs.put(root, advancementTab);
    }

    @Override
    public void onRootRemoved(Advancement root) {
    }

    @Override
    public void onDependentAdded(Advancement dependent) {
        AdvancementTab advancementTab = this.getTab(dependent);
        if (advancementTab != null) {
            advancementTab.addAdvancement(dependent);
        }
    }

    @Override
    public void onDependentRemoved(Advancement dependent) {
    }

    @Override
    public void setProgress(Advancement advancement, AdvancementProgress progress) {
        AdvancementWidget advancementWidget = this.getAdvancementWidget(advancement);
        if (advancementWidget != null) {
            advancementWidget.setProgress(progress);
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

