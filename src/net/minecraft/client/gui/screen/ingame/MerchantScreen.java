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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
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
    private static final int TRADE_OFFER_BUTTON_WIDTH = 88;
    private static final int SCROLLBAR_HEIGHT = 27;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_AREA_HEIGHT = 139;
    private static final int SCROLLBAR_OFFSET_Y = 18;
    private static final int SCROLLBAR_OFFSET_X = 94;
    private static final Text TRADES_TEXT = Text.translatable("merchant.trades");
    private static final Text SEPARATOR_TEXT = Text.literal(" - ");
    private static final Text DEPRECATED_TEXT = Text.translatable("merchant.deprecated");
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
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int i = ((MerchantScreenHandler)this.handler).getLevelProgress();
        if (i > 0 && i <= 5 && ((MerchantScreenHandler)this.handler).isLeveled()) {
            MutableText text = this.title.copy().append(SEPARATOR_TEXT).append(Text.translatable("merchant.level." + i));
            int j = this.textRenderer.getWidth(text);
            int k = 49 + this.backgroundWidth / 2 - j / 2;
            context.drawText(this.textRenderer, text, k, 6, 0x404040, false);
        } else {
            context.drawText(this.textRenderer, this.title, 49 + this.backgroundWidth / 2 - this.textRenderer.getWidth(this.title) / 2, 6, 0x404040, false);
        }
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 0x404040, false);
        int l = this.textRenderer.getWidth(TRADES_TEXT);
        context.drawText(this.textRenderer, TRADES_TEXT, 5 - l / 2 + 48, 6, 0x404040, false);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 512, 256);
        TradeOfferList tradeOfferList = ((MerchantScreenHandler)this.handler).getRecipes();
        if (!tradeOfferList.isEmpty()) {
            int k = this.selectedIndex;
            if (k < 0 || k >= tradeOfferList.size()) {
                return;
            }
            TradeOffer tradeOffer = (TradeOffer)tradeOfferList.get(k);
            if (tradeOffer.isDisabled()) {
                context.drawTexture(TEXTURE, this.x + 83 + 99, this.y + 35, 0, 311.0f, 0.0f, 28, 21, 512, 256);
            }
        }
    }

    private void drawLevelInfo(DrawContext context, int x, int y, TradeOffer tradeOffer) {
        int i = ((MerchantScreenHandler)this.handler).getLevelProgress();
        int j = ((MerchantScreenHandler)this.handler).getExperience();
        if (i >= 5) {
            return;
        }
        context.drawTexture(TEXTURE, x + 136, y + 16, 0, 0.0f, 186.0f, 102, 5, 512, 256);
        int k = VillagerData.getLowerLevelExperience(i);
        if (j < k || !VillagerData.canLevelUp(i)) {
            return;
        }
        int l = 100;
        float f = 100.0f / (float)(VillagerData.getUpperLevelExperience(i) - k);
        int m = Math.min(MathHelper.floor(f * (float)(j - k)), 100);
        context.drawTexture(TEXTURE, x + 136, y + 16, 0, 0.0f, 191.0f, m + 1, 5, 512, 256);
        int n = ((MerchantScreenHandler)this.handler).getMerchantRewardedExperience();
        if (n > 0) {
            int o = Math.min(MathHelper.floor((float)n * f), 100 - m);
            context.drawTexture(TEXTURE, x + 136 + m + 1, y + 16 + 1, 0, 2.0f, 182.0f, o, 3, 512, 256);
        }
    }

    private void renderScrollbar(DrawContext context, int x, int y, TradeOfferList tradeOffers) {
        int i = tradeOffers.size() + 1 - 7;
        if (i > 1) {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int l = 113;
            int m = Math.min(113, this.indexStartOffset * k);
            if (this.indexStartOffset == i - 1) {
                m = 113;
            }
            context.drawTexture(TEXTURE, x + 94, y + 18 + m, 0, 0.0f, 199.0f, 6, 27, 512, 256);
        } else {
            context.drawTexture(TEXTURE, x + 94, y + 18, 0, 6.0f, 199.0f, 6, 27, 512, 256);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        TradeOfferList tradeOfferList = ((MerchantScreenHandler)this.handler).getRecipes();
        if (!tradeOfferList.isEmpty()) {
            TradeOffer tradeOffer2;
            int i = (this.width - this.backgroundWidth) / 2;
            int j = (this.height - this.backgroundHeight) / 2;
            int k = j + 16 + 1;
            int l = i + 5 + 5;
            this.renderScrollbar(context, i, j, tradeOfferList);
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
                context.getMatrices().push();
                context.getMatrices().translate(0.0f, 0.0f, 100.0f);
                int n = k + 2;
                this.renderFirstBuyItem(context, itemStack2, itemStack, l, n);
                if (!itemStack3.isEmpty()) {
                    context.drawItemWithoutEntity(itemStack3, i + 5 + 35, n);
                    context.drawItemInSlot(this.textRenderer, itemStack3, i + 5 + 35, n);
                }
                this.renderArrow(context, tradeOffer2, i, n);
                context.drawItemWithoutEntity(itemStack4, i + 5 + 68, n);
                context.drawItemInSlot(this.textRenderer, itemStack4, i + 5 + 68, n);
                context.getMatrices().pop();
                k += 20;
                ++m;
            }
            int o = this.selectedIndex;
            tradeOffer2 = (TradeOffer)tradeOfferList.get(o);
            if (((MerchantScreenHandler)this.handler).isLeveled()) {
                this.drawLevelInfo(context, i, j, tradeOffer2);
            }
            if (tradeOffer2.isDisabled() && this.isPointWithinBounds(186, 35, 22, 21, mouseX, mouseY) && ((MerchantScreenHandler)this.handler).canRefreshTrades()) {
                context.drawTooltip(this.textRenderer, DEPRECATED_TEXT, mouseX, mouseY);
            }
            for (WidgetButtonPage widgetButtonPage : this.offers) {
                if (widgetButtonPage.isSelected()) {
                    widgetButtonPage.renderTooltip(context, mouseX, mouseY);
                }
                widgetButtonPage.visible = widgetButtonPage.index < ((MerchantScreenHandler)this.handler).getRecipes().size();
            }
            RenderSystem.enableDepthTest();
        }
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private void renderArrow(DrawContext context, TradeOffer tradeOffer, int x, int y) {
        RenderSystem.enableBlend();
        if (tradeOffer.isDisabled()) {
            context.drawTexture(TEXTURE, x + 5 + 35 + 20, y + 3, 0, 25.0f, 171.0f, 10, 9, 512, 256);
        } else {
            context.drawTexture(TEXTURE, x + 5 + 35 + 20, y + 3, 0, 15.0f, 171.0f, 10, 9, 512, 256);
        }
    }

    private void renderFirstBuyItem(DrawContext context, ItemStack adjustedFirstBuyItem, ItemStack originalFirstBuyItem, int x, int y) {
        context.drawItemWithoutEntity(adjustedFirstBuyItem, x, y);
        if (originalFirstBuyItem.getCount() == adjustedFirstBuyItem.getCount()) {
            context.drawItemInSlot(this.textRenderer, adjustedFirstBuyItem, x, y);
        } else {
            context.drawItemInSlot(this.textRenderer, originalFirstBuyItem, x, y, originalFirstBuyItem.getCount() == 1 ? "1" : null);
            context.drawItemInSlot(this.textRenderer, adjustedFirstBuyItem, x + 14, y, adjustedFirstBuyItem.getCount() == 1 ? "1" : null);
            context.getMatrices().push();
            context.getMatrices().translate(0.0f, 0.0f, 300.0f);
            context.drawTexture(TEXTURE, x + 7, y + 12, 0, 0.0f, 176.0f, 9, 2, 512, 256);
            context.getMatrices().pop();
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
            super(x, y, 88, 20, ScreenTexts.EMPTY, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.index = index;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }

        public void renderTooltip(DrawContext context, int x, int y) {
            if (this.hovered && ((MerchantScreenHandler)MerchantScreen.this.handler).getRecipes().size() > this.index + MerchantScreen.this.indexStartOffset) {
                if (x < this.getX() + 20) {
                    ItemStack itemStack = ((TradeOffer)((MerchantScreenHandler)MerchantScreen.this.handler).getRecipes().get(this.index + MerchantScreen.this.indexStartOffset)).getAdjustedFirstBuyItem();
                    context.drawItemTooltip(MerchantScreen.this.textRenderer, itemStack, x, y);
                } else if (x < this.getX() + 50 && x > this.getX() + 30) {
                    ItemStack itemStack = ((TradeOffer)((MerchantScreenHandler)MerchantScreen.this.handler).getRecipes().get(this.index + MerchantScreen.this.indexStartOffset)).getSecondBuyItem();
                    if (!itemStack.isEmpty()) {
                        context.drawItemTooltip(MerchantScreen.this.textRenderer, itemStack, x, y);
                    }
                } else if (x > this.getX() + 65) {
                    ItemStack itemStack = ((TradeOffer)((MerchantScreenHandler)MerchantScreen.this.handler).getRecipes().get(this.index + MerchantScreen.this.indexStartOffset)).getSellItem();
                    context.drawItemTooltip(MerchantScreen.this.textRenderer, itemStack, x, y);
                }
            }
        }
    }
}

