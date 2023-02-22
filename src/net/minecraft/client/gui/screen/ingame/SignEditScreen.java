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
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class SignEditScreen
extends Screen {
    private final SignBlockEntity sign;
    private int ticksSinceOpened;
    private int currentRow;
    private SelectionManager selectionManager;

    public SignEditScreen(SignBlockEntity sign) {
        super(new TranslatableText("sign.edit", new Object[0]));
        this.sign = sign;
    }

    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120, 200, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> this.finishEditing()));
        this.sign.setEditable(false);
        this.selectionManager = new SelectionManager(this.minecraft, () -> this.sign.getTextOnRow(this.currentRow).getString(), string -> this.sign.setTextOnRow(this.currentRow, new LiteralText((String)string)), 90);
    }

    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.minecraft.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            clientPlayNetworkHandler.sendPacket(new UpdateSignC2SPacket(this.sign.getPos(), this.sign.getTextOnRow(0), this.sign.getTextOnRow(1), this.sign.getTextOnRow(2), this.sign.getTextOnRow(3)));
        }
        this.sign.setEditable(true);
    }

    @Override
    public void tick() {
        ++this.ticksSinceOpened;
        if (!this.sign.getType().supports(this.sign.getCachedState().getBlock())) {
            this.finishEditing();
        }
    }

    private void finishEditing() {
        this.sign.markDirty();
        this.minecraft.openScreen(null);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        this.selectionManager.insert(chr);
        return true;
    }

    @Override
    public void onClose() {
        this.finishEditing();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 265) {
            this.currentRow = this.currentRow - 1 & 3;
            this.selectionManager.moveCaretToEnd();
            return true;
        }
        if (keyCode == 264 || keyCode == 257 || keyCode == 335) {
            this.currentRow = this.currentRow + 1 & 3;
            this.selectionManager.moveCaretToEnd();
            return true;
        }
        if (this.selectionManager.handleSpecialKey(keyCode)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 40, 0xFFFFFF);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(this.width / 2, 0.0f, 50.0f);
        float f = 93.75f;
        GlStateManager.scalef(-93.75f, -93.75f, -93.75f);
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        BlockState blockState = this.sign.getCachedState();
        float g = blockState.getBlock() instanceof SignBlock ? (float)(blockState.get(SignBlock.ROTATION) * 360) / 16.0f : blockState.get(WallSignBlock.FACING).asRotation();
        GlStateManager.rotatef(g, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.0f, -1.0625f, 0.0f);
        this.sign.setSelectionState(this.currentRow, this.selectionManager.getSelectionStart(), this.selectionManager.getSelectionEnd(), this.ticksSinceOpened / 6 % 2 == 0);
        BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.sign, -0.5, -0.75, -0.5, 0.0f);
        this.sign.resetSelectionState();
        GlStateManager.popMatrix();
        super.render(mouseX, mouseY, delta);
    }
}

