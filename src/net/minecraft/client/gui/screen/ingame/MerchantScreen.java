/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;

@Environment(value=EnvType.CLIENT)
public class MerchantScreen
extends HandledScreen<MerchantScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager2.png");
    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int field_32356 = 99;
    private static final int XP_BAR_X_OFFSET = 136;
    private static final int TRADE_LIST_AREA_Y_OFFSET = 16;
    private static final int FIRST_BUY_ITEM_X_OFFSET = 5;
    private static final int SECOND_BUY_ITEM_X_OFFSET = 35;
    private static final int SOLD_ITEM_X_OFFSET = 68;
    private static final int field_32362 = 6;
    private static final int MAX_TRADE_OFFERS = 7;
    private static final int field_32364 = 5;
    private static final int TRADE_OFFER_BUTTON_HEIGHT = 20;
    private static final int TRADE_OFFER_BUTTON_WIDTH = 89;
    private static final int SCROLLBAR_HEIGHT = 27;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_AREA_HEIGHT = 139;
    private static final int SCROLLBAR_OFFSET_Y = 18;
    private static final int SCROLLBAR_OFFSET_X = 94;
    private static final Text TRADES_TEXT = new TranslatableText("merchant.trades");
    private static final Text SEPARATOR_TEXT = new LiteralText(" - ");
    private static final Text DEPRECATED_TEXT = new TranslatableText("merchant.deprecated");
    private int selectedIndex;
    private final WidgetButtonPage[] offers = new WidgetButtonPage[7];
    int indexStartOffset;
    private boolean scrolling;

    public MerchantScreen(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 276;
        this.playerInventoryTitleX = 107;
    }

    private void syncRecipeIndex() {
        ((MerchantScreenHandler)this.handler).setRecipeIndex(this.selectedIndex);
        ((MerchantScreenHandler)this.handler).switchTo(this.selectedIndex);
        this.client.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(this.selectedIndex));
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        int k = j + 16 + 2;
        for (int l = 0; l < 7; ++l) {
            this.offers[l] = this.addDrawableChild(new WidgetButtonPage(i + 5, k, l, button -> {
                if (button instanceof WidgetButtonPage) {
                    this.selectedIndex = ((WidgetButtonPage)button).getIndex() + this.indexStartOffset;
                    this.syncRecipeIndex();
                }
            }));
            k += 20;
        }
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        int i = ((MerchantScreenHandler)this.handler).getLevelProgress();
        if (i > 0 && i <= 5 && ((MerchantScreenHandler)this.handler).isLeveled()) {
            MutableText text = this.title.shallowCopy().append(SEPARATOR_TEXT).append(new TranslatableText("merchant.level." + i));
            int j = this.textRenderer.getWidth(text);
            int k = 49 + this.backgroundWidth / 2 - j / 2;
            this.textRenderer.draw(matrices, text, (float)k, 6.0f, 0x404040);
        } else {
            this.textRenderer.draw(matrices, this.title, (float)(49 + this.backgroundWidth / 2 - this.textRenderer.getWidth(this.title) / 2), 6.0f, 0x404040);
        }
        this.textRenderer.draw(matrices, this.playerInventoryTitle, (float)this.playerInventoryTitleX, (float)this.playerInventoryTitleY, 0x404040);
        int l = this.textRenderer.getWidth(TRADES_TEXT);
        this.textRenderer.draw(matrices, TRADES_TEXT, (float)(5 - l / 2 + 48), 6.0f, 0x404040);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        MerchantScreen.drawTexture(matrices, i, j, this.getZOffset(), 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 512, 256);
        TradeOfferList tradeOfferList = ((MerchantScreenHandler)this.handler).getRecipes();
        if (!tradeOfferList.isEmpty()) {
            int k = this.selectedIndex;
            if (k < 0 || k >= tradeOfferList.size()) {
                return;
            }
            TradeOffer tradeOffer = (TradeOffer)tradeOfferList.get(k);
            if (tradeOffer.isDisabled()) {
                RenderSystem.setShaderTexture(0, TEXTURE);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                MerchantScreen.drawTexture(matrices, this.x + 83 + 99, this.y + 35, this.getZOffset(), 311.0f, 0.0f, 28, 21, 512, 256);
            }
        }
    }

    private void drawLevelInfo(MatrixStack matrices, int x, int y, TradeOffer tradeOffer) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = ((MerchantScreenHandler)this.handler).getLevelProgress();
        int j = ((MerchantScreenHandler)this.handler).getExperience();
        if (i >= 5) {
            return;
        }
        MerchantScreen.drawTexture(matrices, x + 136, y + 16, this.getZOffset(), 0.0f, 186.0f, 102, 5, 512, 256);
        int k = VillagerData.getLowerLevelExperience(i);
        if (j < k || !VillagerData.canLevelUp(i)) {
            return;
        }
        int l = 100;
        float f = 100.0f / (float)(VillagerData.getUpperLevelExperience(i) - k);
        int m = Math.min(MathHelper.floor(f * (float)(j - k)), 100);
        MerchantScreen.drawTexture(matrices, x + 136, y + 16, this.getZOffset(), 0.0f, 191.0f, m + 1, 5, 512, 256);
        int n = ((MerchantScreenHandler)this.handler).getMerchantRewardedExperience();
        if (n > 0) {
            int o = Math.min(MathHelper.floor((float)n * f), 100 - m);
            MerchantScreen.drawTexture(matrices, x + 136 + m + 1, y + 16 + 1, this.getZOffset(), 2.0f, 182.0f, o, 3, 512, 256);
        }
    }

    private void renderScrollbar(MatrixStack matrices, int x, int y, TradeOfferList tradeOffers) {
        int i = tradeOffers.size() + 1 - 7;
        if (i > 1) {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int l = 113;
            int m = Math.min(113, this.indexStartOffset * k);
            if (this.indexStartOffset == i - 1) {
                m = 113;
            }
            MerchantScreen.drawTexture(matrices, x + 94, y + 18 + m, this.getZOffset(), 0.0f, 199.0f, 6, 27, 512, 256);
        } else {
            MerchantScreen.drawTexture(matrices, x + 94, y + 18, this.getZOffset(), 6.0f, 199.0f, 6, 27, 512, 256);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        TradeOfferList tradeOfferList = ((MerchantScreenHandler)this.handler).getRecipes();
        if (!tradeOfferList.isEmpty()) {
            TradeOffer tradeOffer2;
            int i = (this.width - this.backgroundWidth) / 2;
            int j = (this.height - this.backgroundHeight) / 2;
            int k = j + 16 + 1;
            int l = i + 5 + 5;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            this.renderScrollbar(matrices, i, j, tradeOfferList);
            int m = 0;
            for (TradeOffer tradeOffer2 : tradeOfferList) {
                if (this.canScroll(tradeOfferList.size()) && (m < this.indexStartOffset || m >= 7 + this.indexStartOffset)) {
                    ++m;
                    continue;
                }
                ItemStack itemStack = tradeOffer2.getOriginalFirstBuyItem();
                ItemStack itemStack2 = tradeOffer2.getAdjustedFirstBuyItem();
                ItemStack itemStack3 = tradeOffer2.getSecondBuyItem();
                ItemStack itemStack4 = tradeOffer2.getSellItem();
                this.itemRenderer.zOffset = 100.0f;
                int n = k + 2;
                this.renderFirstBuyItem(matrices, itemStack2, itemStack, l, n);
                if (!itemStack3.isEmpty()) {
                    this.itemRenderer.renderInGui(itemStack3, i + 5 + 35, n);
                    this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack3, i + 5 + 35, n);
                }
                this.renderArrow(matrices, tradeOffer2, i, n);
                this.itemRenderer.renderInGui(itemStack4, i + 5 + 68, n);
                this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack4, i + 5 + 68, n);
                this.itemRenderer.zOffset = 0.0f;
                k += 20;
                ++m;
            }
            int o = this.selectedIndex;
            tradeOffer2 = (TradeOffer)tradeOfferList.get(o);
            if (((MerchantScreenHandler)this.handler).isLeveled()) {
                this.drawLevelInfo(matrices, i, j, tradeOffer2);
            }
            if (tradeOffer2.isDisabled() && this.isPointWithinBounds(186, 35, 22, 21, mouseX, mouseY) && ((MerchantScreenHandler)this.handler).canRefreshTrades()) {
                this.renderTooltip(matrices, DEPRECATED_TEXT, mouseX, mouseY);
            }
            for (WidgetButtonPage widgetButtonPage : this.offers) {
                if (widgetButtonPage.isHovered()) {
                    widgetButtonPage.renderTooltip(matrices, mouseX, mouseY);
                }
                widgetButtonPage.visible = widgetButtonPage.index < ((MerchantScreenHandler)this.handler).getRecipes().size();
            }
            RenderSystem.enableDepthTest();
        }
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    private void renderArrow(MatrixStack matrices, TradeOffer tradeOffer, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        if (tradeOffer.isDisabled()) {
            MerchantScreen.drawTexture(matrices, x + 5 + 35 + 20, y + 3, this.getZOffset(), 25.0f, 171.0f, 10, 9, 512, 256);
        } else {
            MerchantScreen.drawTexture(matrices, x + 5 + 35 + 20, y + 3, this.getZOffset(), 15.0f, 171.0f, 10, 9, 512, 256);
        }
    }

    private void renderFirstBuyItem(MatrixStack matrices, ItemStack adjustedFirstBuyItem, ItemStack originalFirstBuyItem, int x, int y) {
        this.itemRenderer.renderInGui(adjustedFirstBuyItem, x, y);
        if (originalFirstBuyItem.getCount() == adjustedFirstBuyItem.getCount()) {
            this.itemRenderer.renderGuiItemOverlay(this.textRenderer, adjustedFirstBuyItem, x, y);
        } else {
            this.itemRenderer.renderGuiItemOverlay(this.textRenderer, originalFirstBuyItem, x, y, originalFirstBuyItem.getCount() == 1 ? "1" : null);
            this.itemRenderer.renderGuiItemOverlay(this.textRenderer, adjustedFirstBuyItem, x + 14, y, adjustedFirstBuyItem.getCount() == 1 ? "1" : null);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            this.setZOffset(this.getZOffset() + 300);
            MerchantScreen.drawTexture(matrices, x + 7, y + 12, this.getZOffset(), 0.0f, 176.0f, 9, 2, 512, 256);
            this.setZOffset(this.getZOffset() - 300);
        }
    }

    private boolean canScroll(int listSize) {
        return listSize > 7;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int i = ((MerchantScreenHandler)this.handler).getRecipes().size();
        if (this.canScroll(i)) {
            int j = i - 7;
            this.indexStartOffset = MathHelper.clamp((int)((double)this.indexStartOffset - amount), 0, j);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        int i = ((MerchantScreenHandler)this.handler).getRecipes().size();
        if (this.scrolling) {
            int j = this.y + 18;
            int k = j + 139;
            int l = i - 7;
            float f = ((float)mouseY - (float)j - 13.5f) / ((float)(k - j) - 27.0f);
            f = f * (float)l + 0.5f;
            this.indexStartOffset = MathHelper.clamp((int)f, 0, l);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolling = false;
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        if (this.canScroll(((MerchantScreenHandler)this.handler).getRecipes().size()) && mouseX > (double)(i + 94) && mouseX < (double)(i + 94 + 6) && mouseY > (double)(j + 18) && mouseY <= (double)(j + 18 + 139 + 1)) {
            this.scrolling = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Environment(value=EnvType.CLIENT)
    class WidgetButtonPage
    extends ButtonWidget {
        final int index;

        public WidgetButtonPage(int x, int y, int index, ButtonWidget.PressAction onPress) {
            super(x, y, 89, 20, LiteralText.EMPTY, onPress);
            this.index = index;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }

        @Override
        public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
            if (this.hovered && ((MerchantScreenHandler)MerchantScreen.this.handler).getRecipes().size() > this.index + MerchantScreen.this.indexStartOffset) {
                if (mouseX < this.x + 20) {
                    ItemStack itemStack = ((TradeOffer)((MerchantScreenHandler)MerchantScreen.this.handler).getRecipes().get(this.index + MerchantScreen.this.indexStartOffset)).getAdjustedFirstBuyItem();
                    MerchantScreen.this.renderTooltip(matrices, itemStack, mouseX, mouseY);
                } else if (mouseX < this.x + 50 && mouseX > this.x + 30) {
                    ItemStack itemStack = ((TradeOffer)((MerchantScreenHandler)MerchantScreen.this.handler).getRecipes().get(this.index + MerchantScreen.this.indexStartOffset)).getSecondBuyItem();
                    if (!itemStack.isEmpty()) {
                        MerchantScreen.this.renderTooltip(matrices, itemStack, mouseX, mouseY);
                    }
                } else if (mouseX > this.x + 65) {
                    ItemStack itemStack = ((TradeOffer)((MerchantScreenHandler)MerchantScreen.this.handler).getRecipes().get(this.index + MerchantScreen.this.indexStartOffset)).getSellItem();
                    MerchantScreen.this.renderTooltip(matrices, itemStack, mouseX, mouseY);
                }
            }
        }
    }
}

