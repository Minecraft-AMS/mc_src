/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Ordering
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.container.Container;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractInventoryScreen<T extends Container>
extends ContainerScreen<T> {
    protected boolean offsetGuiForEffects;

    public AbstractInventoryScreen(T container, PlayerInventory playerInventory, Text text) {
        super(container, playerInventory, text);
    }

    @Override
    protected void init() {
        super.init();
        this.method_2476();
    }

    protected void method_2476() {
        if (this.minecraft.player.getStatusEffects().isEmpty()) {
            this.x = (this.width - this.containerWidth) / 2;
            this.offsetGuiForEffects = false;
        } else {
            this.x = 160 + (this.width - this.containerWidth - 200) / 2;
            this.offsetGuiForEffects = true;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        if (this.offsetGuiForEffects) {
            this.drawStatusEffects();
        }
    }

    private void drawStatusEffects() {
        int i = this.x - 124;
        Collection<StatusEffectInstance> collection = this.minecraft.player.getStatusEffects();
        if (collection.isEmpty()) {
            return;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableLighting();
        int j = 33;
        if (collection.size() > 5) {
            j = 132 / (collection.size() - 1);
        }
        List iterable = Ordering.natural().sortedCopy(collection);
        this.method_18642(i, j, iterable);
        this.method_18643(i, j, iterable);
        this.method_18644(i, j, iterable);
    }

    private void method_18642(int i, int j, Iterable<StatusEffectInstance> iterable) {
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        int k = this.y;
        for (StatusEffectInstance statusEffectInstance : iterable) {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.blit(i, k, 0, 166, 140, 32);
            k += j;
        }
    }

    private void method_18643(int i, int j, Iterable<StatusEffectInstance> iterable) {
        this.minecraft.getTextureManager().bindTexture(SpriteAtlasTexture.STATUS_EFFECT_ATLAS_TEX);
        StatusEffectSpriteManager statusEffectSpriteManager = this.minecraft.getStatusEffectSpriteManager();
        int k = this.y;
        for (StatusEffectInstance statusEffectInstance : iterable) {
            StatusEffect statusEffect = statusEffectInstance.getEffectType();
            AbstractInventoryScreen.blit(i + 6, k + 7, this.blitOffset, 18, 18, statusEffectSpriteManager.getSprite(statusEffect));
            k += j;
        }
    }

    private void method_18644(int i, int j, Iterable<StatusEffectInstance> iterable) {
        int k = this.y;
        for (StatusEffectInstance statusEffectInstance : iterable) {
            String string = I18n.translate(statusEffectInstance.getEffectType().getTranslationKey(), new Object[0]);
            if (statusEffectInstance.getAmplifier() >= 1 && statusEffectInstance.getAmplifier() <= 9) {
                string = string + ' ' + I18n.translate("enchantment.level." + (statusEffectInstance.getAmplifier() + 1), new Object[0]);
            }
            this.font.drawWithShadow(string, i + 10 + 18, k + 6, 0xFFFFFF);
            String string2 = StatusEffectUtil.durationToString(statusEffectInstance, 1.0f);
            this.font.drawWithShadow(string2, i + 10 + 18, k + 6 + 10, 0x7F7F7F);
            k += j;
        }
    }
}

