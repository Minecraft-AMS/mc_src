/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class HandledScreen<T extends ScreenHandler>
extends Screen
implements ScreenHandlerProvider<T> {
    public static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/container/inventory.png");
    private static final float field_32318 = 100.0f;
    private static final int field_32319 = 500;
    public static final int field_32322 = 100;
    private static final int field_32321 = 200;
    protected int backgroundWidth = 176;
    protected int backgroundHeight = 166;
    protected int titleX;
    protected int titleY;
    protected int playerInventoryTitleX;
    protected int playerInventoryTitleY;
    protected final T handler;
    protected final Text playerInventoryTitle;
    @Nullable
    protected Slot focusedSlot;
    @Nullable
    private Slot touchDragSlotStart;
    @Nullable
    private Slot touchDropOriginSlot;
    @Nullable
    private Slot touchHoveredSlot;
    @Nullable
    private Slot lastClickedSlot;
    protected int x;
    protected int y;
    private boolean touchIsRightClickDrag;
    private ItemStack touchDragStack = ItemStack.EMPTY;
    private int touchDropX;
    private int touchDropY;
    private long touchDropTime;
    private ItemStack touchDropReturningStack = ItemStack.EMPTY;
    private long touchDropTimer;
    protected final Set<Slot> cursorDragSlots = Sets.newHashSet();
    protected boolean cursorDragging;
    private int heldButtonType;
    private int heldButtonCode;
    private boolean cancelNextRelease;
    private int draggedStackRemainder;
    private long lastButtonClickTime;
    private int lastClickedButton;
    private boolean doubleClicking;
    private ItemStack quickMovingStack = ItemStack.EMPTY;

    public HandledScreen(T handler, PlayerInventory inventory, Text title) {
        super(title);
        this.handler = handler;
        this.playerInventoryTitle = inventory.getDisplayName();
        this.cancelNextRelease = true;
        this.titleX = 8;
        this.titleY = 6;
        this.playerInventoryTitleX = 8;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void init() {
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        ItemStack itemStack;
        int l;
        int i = this.x;
        int j = this.y;
        this.drawBackground(matrices, delta, mouseX, mouseY);
        RenderSystem.disableDepthTest();
        super.render(matrices, mouseX, mouseY, delta);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(i, j, 0.0f);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.focusedSlot = null;
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        for (int k = 0; k < ((ScreenHandler)this.handler).slots.size(); ++k) {
            Slot slot = ((ScreenHandler)this.handler).slots.get(k);
            if (slot.isEnabled()) {
                RenderSystem.setShader(GameRenderer::getPositionTexProgram);
                this.drawSlot(matrices, slot);
            }
            if (!this.isPointOverSlot(slot, mouseX, mouseY) || !slot.isEnabled()) continue;
            this.focusedSlot = slot;
            l = slot.x;
            int m = slot.y;
            HandledScreen.drawSlotHighlight(matrices, l, m, this.getZOffset());
        }
        this.drawForeground(matrices, mouseX, mouseY);
        ItemStack itemStack2 = itemStack = this.touchDragStack.isEmpty() ? ((ScreenHandler)this.handler).getCursorStack() : this.touchDragStack;
        if (!itemStack.isEmpty()) {
            int n = 8;
            l = this.touchDragStack.isEmpty() ? 8 : 16;
            String string = null;
            if (!this.touchDragStack.isEmpty() && this.touchIsRightClickDrag) {
                itemStack = itemStack.copy();
                itemStack.setCount(MathHelper.ceil((float)itemStack.getCount() / 2.0f));
            } else if (this.cursorDragging && this.cursorDragSlots.size() > 1) {
                itemStack = itemStack.copy();
                itemStack.setCount(this.draggedStackRemainder);
                if (itemStack.isEmpty()) {
                    string = Formatting.YELLOW + "0";
                }
            }
            this.drawItem(itemStack, mouseX - i - 8, mouseY - j - l, string);
        }
        if (!this.touchDropReturningStack.isEmpty()) {
            float f = (float)(Util.getMeasuringTimeMs() - this.touchDropTime) / 100.0f;
            if (f >= 1.0f) {
                f = 1.0f;
                this.touchDropReturningStack = ItemStack.EMPTY;
            }
            l = this.touchDropOriginSlot.x - this.touchDropX;
            int m = this.touchDropOriginSlot.y - this.touchDropY;
            int o = this.touchDropX + (int)((float)l * f);
            int p = this.touchDropY + (int)((float)m * f);
            this.drawItem(this.touchDropReturningStack, o, p, null);
        }
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableDepthTest();
    }

    public static void drawSlotHighlight(MatrixStack matrices, int x, int y, int z) {
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        HandledScreen.fillGradient(matrices, x, y, x + 16, y + 16, -2130706433, -2130706433, z);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }

    protected void drawMouseoverTooltip(MatrixStack matrices, int x, int y) {
        if (((ScreenHandler)this.handler).getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
            this.renderTooltip(matrices, this.focusedSlot.getStack(), x, y);
        }
    }

    private void drawItem(ItemStack stack, int x, int y, String amountText) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.translate(0.0f, 0.0f, 32.0f);
        RenderSystem.applyModelViewMatrix();
        this.setZOffset(200);
        this.itemRenderer.zOffset = 200.0f;
        this.itemRenderer.renderInGuiWithOverrides(stack, x, y);
        this.itemRenderer.renderGuiItemOverlay(this.textRenderer, stack, x, y - (this.touchDragStack.isEmpty() ? 0 : 8), amountText);
        this.setZOffset(0);
        this.itemRenderer.zOffset = 0.0f;
    }

    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        this.textRenderer.draw(matrices, this.title, (float)this.titleX, (float)this.titleY, 0x404040);
        this.textRenderer.draw(matrices, this.playerInventoryTitle, (float)this.playerInventoryTitleX, (float)this.playerInventoryTitleY, 0x404040);
    }

    protected abstract void drawBackground(MatrixStack var1, float var2, int var3, int var4);

    private void drawSlot(MatrixStack matrices, Slot slot) {
        Pair<Identifier, Identifier> pair;
        int i = slot.x;
        int j = slot.y;
        ItemStack itemStack = slot.getStack();
        boolean bl = false;
        boolean bl2 = slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && !this.touchIsRightClickDrag;
        ItemStack itemStack2 = ((ScreenHandler)this.handler).getCursorStack();
        String string = null;
        if (slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && this.touchIsRightClickDrag && !itemStack.isEmpty()) {
            itemStack = itemStack.copy();
            itemStack.setCount(itemStack.getCount() / 2);
        } else if (this.cursorDragging && this.cursorDragSlots.contains(slot) && !itemStack2.isEmpty()) {
            if (this.cursorDragSlots.size() == 1) {
                return;
            }
            if (ScreenHandler.canInsertItemIntoSlot(slot, itemStack2, true) && ((ScreenHandler)this.handler).canInsertIntoSlot(slot)) {
                itemStack = itemStack2.copy();
                bl = true;
                ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack, slot.getStack().isEmpty() ? 0 : slot.getStack().getCount());
                int k = Math.min(itemStack.getMaxCount(), slot.getMaxItemCount(itemStack));
                if (itemStack.getCount() > k) {
                    string = Formatting.YELLOW.toString() + k;
                    itemStack.setCount(k);
                }
            } else {
                this.cursorDragSlots.remove(slot);
                this.calculateOffset();
            }
        }
        this.setZOffset(100);
        this.itemRenderer.zOffset = 100.0f;
        if (itemStack.isEmpty() && slot.isEnabled() && (pair = slot.getBackgroundSprite()) != null) {
            Sprite sprite = this.client.getSpriteAtlas((Identifier)pair.getFirst()).apply((Identifier)pair.getSecond());
            RenderSystem.setShaderTexture(0, sprite.getAtlasId());
            HandledScreen.drawSprite(matrices, i, j, this.getZOffset(), 16, 16, sprite);
            bl2 = true;
        }
        if (!bl2) {
            if (bl) {
                HandledScreen.fill(matrices, i, j, i + 16, j + 16, -2130706433);
            }
            RenderSystem.enableDepthTest();
            this.itemRenderer.renderInGuiWithOverrides(this.client.player, itemStack, i, j, slot.x + slot.y * this.backgroundWidth);
            this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, i, j, string);
        }
        this.itemRenderer.zOffset = 0.0f;
        this.setZOffset(0);
    }

    private void calculateOffset() {
        ItemStack itemStack = ((ScreenHandler)this.handler).getCursorStack();
        if (itemStack.isEmpty() || !this.cursorDragging) {
            return;
        }
        if (this.heldButtonType == 2) {
            this.draggedStackRemainder = itemStack.getMaxCount();
            return;
        }
        this.draggedStackRemainder = itemStack.getCount();
        for (Slot slot : this.cursorDragSlots) {
            ItemStack itemStack2 = itemStack.copy();
            ItemStack itemStack3 = slot.getStack();
            int i = itemStack3.isEmpty() ? 0 : itemStack3.getCount();
            ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack2, i);
            int j = Math.min(itemStack2.getMaxCount(), slot.getMaxItemCount(itemStack2));
            if (itemStack2.getCount() > j) {
                itemStack2.setCount(j);
            }
            this.draggedStackRemainder -= itemStack2.getCount() - i;
        }
    }

    @Nullable
    private Slot getSlotAt(double x, double y) {
        for (int i = 0; i < ((ScreenHandler)this.handler).slots.size(); ++i) {
            Slot slot = ((ScreenHandler)this.handler).slots.get(i);
            if (!this.isPointOverSlot(slot, x, y) || !slot.isEnabled()) continue;
            return slot;
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        boolean bl = this.client.options.pickItemKey.matchesMouse(button) && this.client.interactionManager.hasCreativeInventory();
        Slot slot = this.getSlotAt(mouseX, mouseY);
        long l = Util.getMeasuringTimeMs();
        this.doubleClicking = this.lastClickedSlot == slot && l - this.lastButtonClickTime < 250L && this.lastClickedButton == button;
        this.cancelNextRelease = false;
        if (button == 0 || button == 1 || bl) {
            int i = this.x;
            int j = this.y;
            boolean bl2 = this.isClickOutsideBounds(mouseX, mouseY, i, j, button);
            int k = -1;
            if (slot != null) {
                k = slot.id;
            }
            if (bl2) {
                k = -999;
            }
            if (this.client.options.getTouchscreen().getValue().booleanValue() && bl2 && ((ScreenHandler)this.handler).getCursorStack().isEmpty()) {
                this.close();
                return true;
            }
            if (k != -1) {
                if (this.client.options.getTouchscreen().getValue().booleanValue()) {
                    if (slot != null && slot.hasStack()) {
                        this.touchDragSlotStart = slot;
                        this.touchDragStack = ItemStack.EMPTY;
                        this.touchIsRightClickDrag = button == 1;
                    } else {
                        this.touchDragSlotStart = null;
                    }
                } else if (!this.cursorDragging) {
                    if (((ScreenHandler)this.handler).getCursorStack().isEmpty()) {
                        if (bl) {
                            this.onMouseClick(slot, k, button, SlotActionType.CLONE);
                        } else {
                            boolean bl3 = k != -999 && (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 340) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 344));
                            SlotActionType slotActionType = SlotActionType.PICKUP;
                            if (bl3) {
                                this.quickMovingStack = slot != null && slot.hasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                                slotActionType = SlotActionType.QUICK_MOVE;
                            } else if (k == -999) {
                                slotActionType = SlotActionType.THROW;
                            }
                            this.onMouseClick(slot, k, button, slotActionType);
                        }
                        this.cancelNextRelease = true;
                    } else {
                        this.cursorDragging = true;
                        this.heldButtonCode = button;
                        this.cursorDragSlots.clear();
                        if (button == 0) {
                            this.heldButtonType = 0;
                        } else if (button == 1) {
                            this.heldButtonType = 1;
                        } else if (bl) {
                            this.heldButtonType = 2;
                        }
                    }
                }
            }
        } else {
            this.onMouseClick(button);
        }
        this.lastClickedSlot = slot;
        this.lastButtonClickTime = l;
        this.lastClickedButton = button;
        return true;
    }

    private void onMouseClick(int button) {
        if (this.focusedSlot != null && ((ScreenHandler)this.handler).getCursorStack().isEmpty()) {
            if (this.client.options.swapHandsKey.matchesMouse(button)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 40, SlotActionType.SWAP);
                return;
            }
            for (int i = 0; i < 9; ++i) {
                if (!this.client.options.hotbarKeys[i].matchesMouse(button)) continue;
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, i, SlotActionType.SWAP);
            }
        }
    }

    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        Slot slot = this.getSlotAt(mouseX, mouseY);
        ItemStack itemStack = ((ScreenHandler)this.handler).getCursorStack();
        if (this.touchDragSlotStart != null && this.client.options.getTouchscreen().getValue().booleanValue()) {
            if (button == 0 || button == 1) {
                if (this.touchDragStack.isEmpty()) {
                    if (slot != this.touchDragSlotStart && !this.touchDragSlotStart.getStack().isEmpty()) {
                        this.touchDragStack = this.touchDragSlotStart.getStack().copy();
                    }
                } else if (this.touchDragStack.getCount() > 1 && slot != null && ScreenHandler.canInsertItemIntoSlot(slot, this.touchDragStack, false)) {
                    long l = Util.getMeasuringTimeMs();
                    if (this.touchHoveredSlot == slot) {
                        if (l - this.touchDropTimer > 500L) {
                            this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, SlotActionType.PICKUP);
                            this.onMouseClick(slot, slot.id, 1, SlotActionType.PICKUP);
                            this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, SlotActionType.PICKUP);
                            this.touchDropTimer = l + 750L;
                            this.touchDragStack.decrement(1);
                        }
                    } else {
                        this.touchHoveredSlot = slot;
                        this.touchDropTimer = l;
                    }
                }
            }
        } else if (this.cursorDragging && slot != null && !itemStack.isEmpty() && (itemStack.getCount() > this.cursorDragSlots.size() || this.heldButtonType == 2) && ScreenHandler.canInsertItemIntoSlot(slot, itemStack, true) && slot.canInsert(itemStack) && ((ScreenHandler)this.handler).canInsertIntoSlot(slot)) {
            this.cursorDragSlots.add(slot);
            this.calculateOffset();
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Slot slot = this.getSlotAt(mouseX, mouseY);
        int i = this.x;
        int j = this.y;
        boolean bl = this.isClickOutsideBounds(mouseX, mouseY, i, j, button);
        int k = -1;
        if (slot != null) {
            k = slot.id;
        }
        if (bl) {
            k = -999;
        }
        if (this.doubleClicking && slot != null && button == 0 && ((ScreenHandler)this.handler).canInsertIntoSlot(ItemStack.EMPTY, slot)) {
            if (HandledScreen.hasShiftDown()) {
                if (!this.quickMovingStack.isEmpty()) {
                    for (Slot slot2 : ((ScreenHandler)this.handler).slots) {
                        if (slot2 == null || !slot2.canTakeItems(this.client.player) || !slot2.hasStack() || slot2.inventory != slot.inventory || !ScreenHandler.canInsertItemIntoSlot(slot2, this.quickMovingStack, true)) continue;
                        this.onMouseClick(slot2, slot2.id, button, SlotActionType.QUICK_MOVE);
                    }
                }
            } else {
                this.onMouseClick(slot, k, button, SlotActionType.PICKUP_ALL);
            }
            this.doubleClicking = false;
            this.lastButtonClickTime = 0L;
        } else {
            if (this.cursorDragging && this.heldButtonCode != button) {
                this.cursorDragging = false;
                this.cursorDragSlots.clear();
                this.cancelNextRelease = true;
                return true;
            }
            if (this.cancelNextRelease) {
                this.cancelNextRelease = false;
                return true;
            }
            if (this.touchDragSlotStart != null && this.client.options.getTouchscreen().getValue().booleanValue()) {
                if (button == 0 || button == 1) {
                    if (this.touchDragStack.isEmpty() && slot != this.touchDragSlotStart) {
                        this.touchDragStack = this.touchDragSlotStart.getStack();
                    }
                    boolean bl2 = ScreenHandler.canInsertItemIntoSlot(slot, this.touchDragStack, false);
                    if (k != -1 && !this.touchDragStack.isEmpty() && bl2) {
                        this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, button, SlotActionType.PICKUP);
                        this.onMouseClick(slot, k, 0, SlotActionType.PICKUP);
                        if (((ScreenHandler)this.handler).getCursorStack().isEmpty()) {
                            this.touchDropReturningStack = ItemStack.EMPTY;
                        } else {
                            this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, button, SlotActionType.PICKUP);
                            this.touchDropX = MathHelper.floor(mouseX - (double)i);
                            this.touchDropY = MathHelper.floor(mouseY - (double)j);
                            this.touchDropOriginSlot = this.touchDragSlotStart;
                            this.touchDropReturningStack = this.touchDragStack;
                            this.touchDropTime = Util.getMeasuringTimeMs();
                        }
                    } else if (!this.touchDragStack.isEmpty()) {
                        this.touchDropX = MathHelper.floor(mouseX - (double)i);
                        this.touchDropY = MathHelper.floor(mouseY - (double)j);
                        this.touchDropOriginSlot = this.touchDragSlotStart;
                        this.touchDropReturningStack = this.touchDragStack;
                        this.touchDropTime = Util.getMeasuringTimeMs();
                    }
                    this.endTouchDrag();
                }
            } else if (this.cursorDragging && !this.cursorDragSlots.isEmpty()) {
                this.onMouseClick(null, -999, ScreenHandler.packQuickCraftData(0, this.heldButtonType), SlotActionType.QUICK_CRAFT);
                for (Slot slot2 : this.cursorDragSlots) {
                    this.onMouseClick(slot2, slot2.id, ScreenHandler.packQuickCraftData(1, this.heldButtonType), SlotActionType.QUICK_CRAFT);
                }
                this.onMouseClick(null, -999, ScreenHandler.packQuickCraftData(2, this.heldButtonType), SlotActionType.QUICK_CRAFT);
            } else if (!((ScreenHandler)this.handler).getCursorStack().isEmpty()) {
                if (this.client.options.pickItemKey.matchesMouse(button)) {
                    this.onMouseClick(slot, k, button, SlotActionType.CLONE);
                } else {
                    boolean bl2;
                    boolean bl3 = bl2 = k != -999 && (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 340) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 344));
                    if (bl2) {
                        this.quickMovingStack = slot != null && slot.hasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                    }
                    this.onMouseClick(slot, k, button, bl2 ? SlotActionType.QUICK_MOVE : SlotActionType.PICKUP);
                }
            }
        }
        if (((ScreenHandler)this.handler).getCursorStack().isEmpty()) {
            this.lastButtonClickTime = 0L;
        }
        this.cursorDragging = false;
        return true;
    }

    public void endTouchDrag() {
        this.touchDragStack = ItemStack.EMPTY;
        this.touchDragSlotStart = null;
    }

    private boolean isPointOverSlot(Slot slot, double pointX, double pointY) {
        return this.isPointWithinBounds(slot.x, slot.y, 16, 16, pointX, pointY);
    }

    protected boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
        int i = this.x;
        int j = this.y;
        return (pointX -= (double)i) >= (double)(x - 1) && pointX < (double)(x + width + 1) && (pointY -= (double)j) >= (double)(y - 1) && pointY < (double)(y + height + 1);
    }

    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        if (slot != null) {
            slotId = slot.id;
        }
        this.client.interactionManager.clickSlot(((ScreenHandler)this.handler).syncId, slotId, button, actionType, this.client.player);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        this.handleHotbarKeyPressed(keyCode, scanCode);
        if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            if (this.client.options.pickItemKey.matchesKey(keyCode, scanCode)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 0, SlotActionType.CLONE);
            } else if (this.client.options.dropKey.matchesKey(keyCode, scanCode)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, HandledScreen.hasControlDown() ? 1 : 0, SlotActionType.THROW);
            }
        }
        return true;
    }

    protected boolean handleHotbarKeyPressed(int keyCode, int scanCode) {
        if (((ScreenHandler)this.handler).getCursorStack().isEmpty() && this.focusedSlot != null) {
            if (this.client.options.swapHandsKey.matchesKey(keyCode, scanCode)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 40, SlotActionType.SWAP);
                return true;
            }
            for (int i = 0; i < 9; ++i) {
                if (!this.client.options.hotbarKeys[i].matchesKey(keyCode, scanCode)) continue;
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, i, SlotActionType.SWAP);
                return true;
            }
        }
        return false;
    }

    @Override
    public void removed() {
        if (this.client.player == null) {
            return;
        }
        ((ScreenHandler)this.handler).close(this.client.player);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public final void tick() {
        super.tick();
        if (!this.client.player.isAlive() || this.client.player.isRemoved()) {
            this.client.player.closeHandledScreen();
        } else {
            this.handledScreenTick();
        }
    }

    protected void handledScreenTick() {
    }

    @Override
    public T getScreenHandler() {
        return this.handler;
    }

    @Override
    public void close() {
        this.client.player.closeHandledScreen();
        super.close();
    }
}

