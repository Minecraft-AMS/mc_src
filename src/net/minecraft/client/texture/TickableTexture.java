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
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureTickListener;

@Environment(value=EnvType.CLIENT)
public interface TickableTexture
extends Texture,
TextureTickListener {
}

