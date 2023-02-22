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
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.container.HorseContainer;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class HorseScreen
extends ContainerScreen<HorseContainer> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/horse.png");
    private final HorseBaseEntity entity;
    private float mouseX;
    private float mouseY;

    public HorseScreen(HorseContainer container, PlayerInventory inventory, HorseBaseEntity entity) {
        super(container, inventory, entity.getDisplayName());
        this.entity = entity;
        this.passEvents = false;
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        this.font.draw(this.title.asFormattedString(), 8.0f, 6.0f, 0x404040);
        this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0f, this.containerHeight - 96 + 2, 0x404040);
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        AbstractDonkeyEntity abstractDonkeyEntity;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.containerWidth) / 2;
        int j = (this.height - this.containerHeight) / 2;
        this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
        if (this.entity instanceof AbstractDonkeyEntity && (abstractDonkeyEntity = (AbstractDonkeyEntity)this.entity).hasChest()) {
            this.blit(i + 79, j + 17, 0, this.containerHeight, abstractDonkeyEntity.method_6702() * 18, 54);
        }
        if (this.entity.canBeSaddled()) {
            this.blit(i + 7, j + 35 - 18, 18, this.containerHeight + 54, 18, 18);
        }
        if (this.entity.canEquip()) {
            if (this.entity instanceof LlamaEntity) {
                this.blit(i + 7, j + 35, 36, this.containerHeight + 54, 18, 18);
            } else {
                this.blit(i + 7, j + 35, 0, this.containerHeight + 54, 18, 18);
            }
        }
        InventoryScreen.drawEntity(i + 51, j + 60, 17, (float)(i + 51) - this.mouseX, (float)(j + 75 - 50) - this.mouseY, this.entity);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
    }
}

