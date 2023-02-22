/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class StatusEffectSpriteManager
extends SpriteAtlasHolder {
    public StatusEffectSpriteManager(TextureManager textureManager) {
        super(textureManager, new Identifier("textures/atlas/mob_effects.png"), new Identifier("mob_effects"));
    }

    public Sprite getSprite(StatusEffect effect) {
        return this.getSprite(Registries.STATUS_EFFECT.getId(effect));
    }
}

