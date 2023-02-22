/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class InventoryScreen
extends AbstractInventoryScreen<PlayerContainer>
implements RecipeBookProvider {
    private static final Identifier RECIPE_BUTTON_TEX = new Identifier("textures/gui/recipe_button.png");
    private float mouseX;
    private float mouseY;
    private final RecipeBookWidget recipeBook = new RecipeBookWidget();
    private boolean isOpen;
    private boolean isNarrow;
    private boolean isMouseDown;

    public InventoryScreen(PlayerEntity player) {
        super(player.playerContainer, player.inventory, new TranslatableText("container.crafting", new Object[0]));
        this.passEvents = true;
    }

    @Override
    public void tick() {
        if (this.minecraft.interactionManager.hasCreativeInventory()) {
            this.minecraft.openScreen(new CreativeInventoryScreen(this.minecraft.player));
            return;
        }
        this.recipeBook.update();
    }

    @Override
    protected void init() {
        if (this.minecraft.interactionManager.hasCreativeInventory()) {
            this.minecraft.openScreen(new CreativeInventoryScreen(this.minecraft.player));
            return;
        }
        super.init();
        this.isNarrow = this.width < 379;
        this.recipeBook.initialize(this.width, this.height, this.minecraft, this.isNarrow, (CraftingContainer)this.container);
        this.isOpen = true;
        this.x = this.recipeBook.findLeftEdge(this.isNarrow, this.width, this.containerWidth);
        this.children.add(this.recipeBook);
        this.setInitialFocus(this.recipeBook);
        this.addButton(new TexturedButtonWidget(this.x + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEX, buttonWidget -> {
            this.recipeBook.reset(this.isNarrow);
            this.recipeBook.toggleOpen();
            this.x = this.recipeBook.findLeftEdge(this.isNarrow, this.width, this.containerWidth);
            ((TexturedButtonWidget)buttonWidget).setPos(this.x + 104, this.height / 2 - 22);
            this.isMouseDown = true;
        }));
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        this.font.draw(this.title.asFormattedString(), 97.0f, 8.0f, 0x404040);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        boolean bl = this.offsetGuiForEffects = !this.recipeBook.isOpen();
        if (this.recipeBook.isOpen() && this.isNarrow) {
            this.drawBackground(delta, mouseX, mouseY);
            this.recipeBook.render(mouseX, mouseY, delta);
        } else {
            this.recipeBook.render(mouseX, mouseY, delta);
            super.render(mouseX, mouseY, delta);
            this.recipeBook.drawGhostSlots(this.x, this.y, false, delta);
        }
        this.drawMouseoverTooltip(mouseX, mouseY);
        this.recipeBook.drawTooltip(this.x, this.y, mouseX, mouseY);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.focusOn(this.recipeBook);
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        int i = this.x;
        int j = this.y;
        this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
        InventoryScreen.drawEntity(i + 51, j + 75, 30, (float)(i + 51) - this.mouseX, (float)(j + 75 - 50) - this.mouseY, this.minecraft.player);
    }

    public static void drawEntity(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(x, y, 50.0f);
        GlStateManager.scalef(-size, size, size);
        GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
        float f = entity.field_6283;
        float g = entity.yaw;
        float h = entity.pitch;
        float i = entity.prevHeadYaw;
        float j = entity.headYaw;
        GlStateManager.rotatef(135.0f, 0.0f, 1.0f, 0.0f);
        DiffuseLighting.enable();
        GlStateManager.rotatef(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(-((float)Math.atan(mouseY / 40.0f)) * 20.0f, 1.0f, 0.0f, 0.0f);
        entity.field_6283 = (float)Math.atan(mouseX / 40.0f) * 20.0f;
        entity.yaw = (float)Math.atan(mouseX / 40.0f) * 40.0f;
        entity.pitch = -((float)Math.atan(mouseY / 40.0f)) * 20.0f;
        entity.headYaw = entity.yaw;
        entity.prevHeadYaw = entity.yaw;
        GlStateManager.translatef(0.0f, 0.0f, 0.0f);
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
        entityRenderDispatcher.method_3945(180.0f);
        entityRenderDispatcher.setRenderShadows(false);
        entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        entityRenderDispatcher.setRenderShadows(true);
        entity.field_6283 = f;
        entity.yaw = g;
        entity.pitch = h;
        entity.prevHeadYaw = i;
        entity.headYaw = j;
        GlStateManager.popMatrix();
        DiffuseLighting.disable();
        GlStateManager.disableRescaleNormal();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }

    @Override
    protected boolean isPointWithinBounds(int xPosition, int yPosition, int width, int height, double pointX, double pointY) {
        return (!this.isNarrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(xPosition, yPosition, width, height, pointX, pointY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.isNarrow && this.recipeBook.isOpen()) {
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.isMouseDown) {
            this.isMouseDown = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.containerWidth) || mouseY >= (double)(top + this.containerHeight);
        return this.recipeBook.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.containerWidth, this.containerHeight, button) && bl;
    }

    @Override
    protected void onMouseClick(Slot slot, int invSlot, int button, SlotActionType slotActionType) {
        super.onMouseClick(slot, invSlot, button, slotActionType);
        this.recipeBook.slotClicked(slot);
    }

    @Override
    public void refreshRecipeBook() {
        this.recipeBook.refresh();
    }

    @Override
    public void removed() {
        if (this.isOpen) {
            this.recipeBook.close();
        }
        super.removed();
    }

    @Override
    public RecipeBookWidget getRecipeBookWidget() {
        return this.recipeBook;
    }
}

