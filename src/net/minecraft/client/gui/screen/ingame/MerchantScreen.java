/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.MerchantContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.SelectVillagerTradeC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;
import net.minecraft.village.VillagerData;

@Environment(value=EnvType.CLIENT)
public class MerchantScreen
extends ContainerScreen<MerchantContainer> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager2.png");
    private int field_19161;
    private final WidgetButtonPage[] field_19162 = new WidgetButtonPage[7];
    private int field_19163;
    private boolean field_19164;

    public MerchantScreen(MerchantContainer merchantContainer, PlayerInventory playerInventory, Text text) {
        super(merchantContainer, playerInventory, text);
        this.containerWidth = 276;
    }

    private void syncRecipeIndex() {
        ((MerchantContainer)this.container).setRecipeIndex(this.field_19161);
        ((MerchantContainer)this.container).switchTo(this.field_19161);
        this.minecraft.getNetworkHandler().sendPacket(new SelectVillagerTradeC2SPacket(this.field_19161));
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.containerWidth) / 2;
        int j = (this.height - this.containerHeight) / 2;
        int k = j + 16 + 2;
        for (int l = 0; l < 7; ++l) {
            this.field_19162[l] = this.addButton(new WidgetButtonPage(i + 5, k, l, buttonWidget -> {
                if (buttonWidget instanceof WidgetButtonPage) {
                    this.field_19161 = ((WidgetButtonPage)buttonWidget).method_20228() + this.field_19163;
                    this.syncRecipeIndex();
                }
            }));
            k += 20;
        }
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        String string;
        int i = ((MerchantContainer)this.container).getLevelProgress();
        int j = this.containerHeight - 94;
        if (i > 0 && i <= 5 && ((MerchantContainer)this.container).isLevelled()) {
            string = this.title.asFormattedString();
            String string2 = "- " + I18n.translate("merchant.level." + i, new Object[0]);
            int k = this.font.getStringWidth(string);
            int l = this.font.getStringWidth(string2);
            int m = k + l + 3;
            int n = 49 + this.containerWidth / 2 - m / 2;
            this.font.draw(string, n, 6.0f, 0x404040);
            this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 107.0f, j, 0x404040);
            this.font.draw(string2, n + k + 3, 6.0f, 0x404040);
        } else {
            string = this.title.asFormattedString();
            this.font.draw(string, 49 + this.containerWidth / 2 - this.font.getStringWidth(string) / 2, 6.0f, 0x404040);
            this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 107.0f, j, 0x404040);
        }
        string = I18n.translate("merchant.trades", new Object[0]);
        int o = this.font.getStringWidth(string);
        this.font.draw(string, 5 - o / 2 + 48, 6.0f, 0x404040);
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.containerWidth) / 2;
        int j = (this.height - this.containerHeight) / 2;
        MerchantScreen.blit(i, j, this.blitOffset, 0.0f, 0.0f, this.containerWidth, this.containerHeight, 256, 512);
        TraderOfferList traderOfferList = ((MerchantContainer)this.container).getRecipes();
        if (!traderOfferList.isEmpty()) {
            int k = this.field_19161;
            if (k < 0 || k >= traderOfferList.size()) {
                return;
            }
            TradeOffer tradeOffer = (TradeOffer)traderOfferList.get(k);
            if (tradeOffer.isDisabled()) {
                this.minecraft.getTextureManager().bindTexture(TEXTURE);
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.disableLighting();
                MerchantScreen.blit(this.x + 83 + 99, this.y + 35, this.blitOffset, 311.0f, 0.0f, 28, 21, 256, 512);
            }
        }
    }

    private void method_19413(int i, int j, TradeOffer tradeOffer) {
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int k = ((MerchantContainer)this.container).getLevelProgress();
        int l = ((MerchantContainer)this.container).getExperience();
        if (k >= 5) {
            return;
        }
        MerchantScreen.blit(i + 136, j + 16, this.blitOffset, 0.0f, 186.0f, 102, 5, 256, 512);
        int m = VillagerData.getLowerLevelExperience(k);
        if (l < m || !VillagerData.canLevelUp(k)) {
            return;
        }
        int n = 100;
        float f = 100 / (VillagerData.getUpperLevelExperience(k) - m);
        int o = MathHelper.floor(f * (float)(l - m));
        MerchantScreen.blit(i + 136, j + 16, this.blitOffset, 0.0f, 191.0f, o + 1, 5, 256, 512);
        int p = ((MerchantContainer)this.container).getTraderRewardedExperience();
        if (p > 0) {
            int q = Math.min(MathHelper.floor((float)p * f), 100 - o);
            MerchantScreen.blit(i + 136 + o + 1, j + 16 + 1, this.blitOffset, 2.0f, 182.0f, q, 3, 256, 512);
        }
    }

    private void method_20221(int i, int j, TraderOfferList traderOfferList) {
        DiffuseLighting.disable();
        int k = traderOfferList.size() + 1 - 7;
        if (k > 1) {
            int l = 139 - (27 + (k - 1) * 139 / k);
            int m = 1 + l / k + 139 / k;
            int n = 113;
            int o = Math.min(113, this.field_19163 * m);
            if (this.field_19163 == k - 1) {
                o = 113;
            }
            MerchantScreen.blit(i + 94, j + 18 + o, this.blitOffset, 0.0f, 199.0f, 6, 27, 256, 512);
        } else {
            MerchantScreen.blit(i + 94, j + 18, this.blitOffset, 6.0f, 199.0f, 6, 27, 256, 512);
        }
        DiffuseLighting.enableForItems();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        TraderOfferList traderOfferList = ((MerchantContainer)this.container).getRecipes();
        if (!traderOfferList.isEmpty()) {
            TradeOffer tradeOffer2;
            int i = (this.width - this.containerWidth) / 2;
            int j = (this.height - this.containerHeight) / 2;
            int k = j + 16 + 1;
            int l = i + 5 + 5;
            GlStateManager.pushMatrix();
            DiffuseLighting.enableForItems();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            this.method_20221(i, j, traderOfferList);
            int m = 0;
            for (TradeOffer tradeOffer2 : traderOfferList) {
                if (this.method_20220(traderOfferList.size()) && (m < this.field_19163 || m >= 7 + this.field_19163)) {
                    ++m;
                    continue;
                }
                ItemStack itemStack = tradeOffer2.getOriginalFirstBuyItem();
                ItemStack itemStack2 = tradeOffer2.getAdjustedFirstBuyItem();
                ItemStack itemStack3 = tradeOffer2.getSecondBuyItem();
                ItemStack itemStack4 = tradeOffer2.getMutableSellItem();
                this.itemRenderer.zOffset = 100.0f;
                int n = k + 2;
                this.method_20222(itemStack2, itemStack, l, n);
                if (!itemStack3.isEmpty()) {
                    this.itemRenderer.renderGuiItem(itemStack3, i + 5 + 35, n);
                    this.itemRenderer.renderGuiItemOverlay(this.font, itemStack3, i + 5 + 35, n);
                }
                this.method_20223(tradeOffer2, i, n);
                this.itemRenderer.renderGuiItem(itemStack4, i + 5 + 68, n);
                this.itemRenderer.renderGuiItemOverlay(this.font, itemStack4, i + 5 + 68, n);
                this.itemRenderer.zOffset = 0.0f;
                k += 20;
                ++m;
            }
            int o = this.field_19161;
            tradeOffer2 = (TradeOffer)traderOfferList.get(o);
            GlStateManager.disableLighting();
            if (((MerchantContainer)this.container).isLevelled()) {
                this.method_19413(i, j, tradeOffer2);
            }
            if (tradeOffer2.isDisabled() && this.isPointWithinBounds(186, 35, 22, 21, mouseX, mouseY) && ((MerchantContainer)this.container).canRefreshTrades()) {
                this.renderTooltip(I18n.translate("merchant.deprecated", new Object[0]), mouseX, mouseY);
            }
            for (WidgetButtonPage widgetButtonPage : this.field_19162) {
                if (widgetButtonPage.isHovered()) {
                    widgetButtonPage.renderToolTip(mouseX, mouseY);
                }
                widgetButtonPage.visible = widgetButtonPage.field_19165 < ((MerchantContainer)this.container).getRecipes().size();
            }
            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
            DiffuseLighting.enable();
        }
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    private void method_20223(TradeOffer tradeOffer, int i, int j) {
        DiffuseLighting.disable();
        GlStateManager.enableBlend();
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        if (tradeOffer.isDisabled()) {
            MerchantScreen.blit(i + 5 + 35 + 20, j + 3, this.blitOffset, 25.0f, 171.0f, 10, 9, 256, 512);
        } else {
            MerchantScreen.blit(i + 5 + 35 + 20, j + 3, this.blitOffset, 15.0f, 171.0f, 10, 9, 256, 512);
        }
        DiffuseLighting.enableForItems();
    }

    private void method_20222(ItemStack itemStack, ItemStack itemStack2, int i, int j) {
        this.itemRenderer.renderGuiItem(itemStack, i, j);
        if (itemStack2.getCount() == itemStack.getCount()) {
            this.itemRenderer.renderGuiItemOverlay(this.font, itemStack, i, j);
        } else {
            this.itemRenderer.renderGuiItemOverlay(this.font, itemStack2, i, j, itemStack2.getCount() == 1 ? "1" : null);
            this.itemRenderer.renderGuiItemOverlay(this.font, itemStack, i + 14, j, itemStack.getCount() == 1 ? "1" : null);
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            this.blitOffset += 300;
            DiffuseLighting.disable();
            MerchantScreen.blit(i + 7, j + 12, this.blitOffset, 0.0f, 176.0f, 9, 2, 256, 512);
            DiffuseLighting.enableForItems();
            this.blitOffset -= 300;
        }
    }

    private boolean method_20220(int i) {
        return i > 7;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double amount) {
        int i = ((MerchantContainer)this.container).getRecipes().size();
        if (this.method_20220(i)) {
            int j = i - 7;
            this.field_19163 = (int)((double)this.field_19163 - amount);
            this.field_19163 = MathHelper.clamp(this.field_19163, 0, j);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        int i = ((MerchantContainer)this.container).getRecipes().size();
        if (this.field_19164) {
            int j = this.y + 18;
            int k = j + 139;
            int l = i - 7;
            float f = ((float)mouseY - (float)j - 13.5f) / ((float)(k - j) - 27.0f);
            f = f * (float)l + 0.5f;
            this.field_19163 = MathHelper.clamp((int)f, 0, l);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.field_19164 = false;
        int i = (this.width - this.containerWidth) / 2;
        int j = (this.height - this.containerHeight) / 2;
        if (this.method_20220(((MerchantContainer)this.container).getRecipes().size()) && mouseX > (double)(i + 94) && mouseX < (double)(i + 94 + 6) && mouseY > (double)(j + 18) && mouseY <= (double)(j + 18 + 139 + 1)) {
            this.field_19164 = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Environment(value=EnvType.CLIENT)
    class WidgetButtonPage
    extends ButtonWidget {
        final int field_19165;

        public WidgetButtonPage(int i, int j, int k, ButtonWidget.PressAction pressAction) {
            super(i, j, 89, 20, "", pressAction);
            this.field_19165 = k;
            this.visible = false;
        }

        public int method_20228() {
            return this.field_19165;
        }

        @Override
        public void renderToolTip(int mouseX, int mouseY) {
            if (this.isHovered && ((MerchantContainer)MerchantScreen.this.container).getRecipes().size() > this.field_19165 + MerchantScreen.this.field_19163) {
                if (mouseX < this.x + 20) {
                    ItemStack itemStack = ((TradeOffer)((MerchantContainer)MerchantScreen.this.container).getRecipes().get(this.field_19165 + MerchantScreen.this.field_19163)).getAdjustedFirstBuyItem();
                    MerchantScreen.this.renderTooltip(itemStack, mouseX, mouseY);
                } else if (mouseX < this.x + 50 && mouseX > this.x + 30) {
                    ItemStack itemStack = ((TradeOffer)((MerchantContainer)MerchantScreen.this.container).getRecipes().get(this.field_19165 + MerchantScreen.this.field_19163)).getSecondBuyItem();
                    if (!itemStack.isEmpty()) {
                        MerchantScreen.this.renderTooltip(itemStack, mouseX, mouseY);
                    }
                } else if (mouseX > this.x + 65) {
                    ItemStack itemStack = ((TradeOffer)((MerchantContainer)MerchantScreen.this.container).getRecipes().get(this.field_19165 + MerchantScreen.this.field_19163)).getMutableSellItem();
                    MerchantScreen.this.renderTooltip(itemStack, mouseX, mouseY);
                }
            }
        }
    }
}

