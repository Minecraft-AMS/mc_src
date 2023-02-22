/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.realms.RealmsVertexFormat;

@Environment(value=EnvType.CLIENT)
public class RealmsDefaultVertexFormat {
    public static final RealmsVertexFormat POSITION_COLOR = new RealmsVertexFormat(VertexFormats.POSITION_COLOR);
    public static final RealmsVertexFormat POSITION_TEX_COLOR = new RealmsVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR);
}

