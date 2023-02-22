/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;

@Environment(value=EnvType.CLIENT)
public class VertexFormats {
    public static final VertexFormatElement POSITION_ELEMENT = new VertexFormatElement(0, VertexFormatElement.ComponentType.FLOAT, VertexFormatElement.Type.POSITION, 3);
    public static final VertexFormatElement COLOR_ELEMENT = new VertexFormatElement(0, VertexFormatElement.ComponentType.UBYTE, VertexFormatElement.Type.COLOR, 4);
    public static final VertexFormatElement TEXTURE_ELEMENT = new VertexFormatElement(0, VertexFormatElement.ComponentType.FLOAT, VertexFormatElement.Type.UV, 2);
    public static final VertexFormatElement OVERLAY_ELEMENT = new VertexFormatElement(1, VertexFormatElement.ComponentType.SHORT, VertexFormatElement.Type.UV, 2);
    public static final VertexFormatElement LIGHT_ELEMENT = new VertexFormatElement(2, VertexFormatElement.ComponentType.SHORT, VertexFormatElement.Type.UV, 2);
    public static final VertexFormatElement NORMAL_ELEMENT = new VertexFormatElement(0, VertexFormatElement.ComponentType.BYTE, VertexFormatElement.Type.NORMAL, 3);
    public static final VertexFormatElement PADDING_ELEMENT = new VertexFormatElement(0, VertexFormatElement.ComponentType.BYTE, VertexFormatElement.Type.PADDING, 1);
    public static final VertexFormatElement UV_ELEMENT = TEXTURE_ELEMENT;
    public static final VertexFormat BLIT_SCREEN = new VertexFormat((ImmutableMap<String, VertexFormatElement>)ImmutableMap.builder().put((Object)"Position", (Object)POSITION_ELEMENT).put((Object)"UV", (Object)UV_ELEMENT).put((Object)"Color", (Object)COLOR_ELEMENT).build());
    public static final VertexFormat POSITION_COLOR_TEXTURE_LIGHT_NORMAL = new VertexFormat((ImmutableMap<String, VertexFormatElement>)ImmutableMap.builder().put((Object)"Position", (Object)POSITION_ELEMENT).put((Object)"Color", (Object)COLOR_ELEMENT).put((Object)"UV0", (Object)TEXTURE_ELEMENT).put((Object)"UV2", (Object)LIGHT_ELEMENT).put((Object)"Normal", (Object)NORMAL_ELEMENT).put((Object)"Padding", (Object)PADDING_ELEMENT).build());
    public static final VertexFormat POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL = new VertexFormat((ImmutableMap<String, VertexFormatElement>)ImmutableMap.builder().put((Object)"Position", (Object)POSITION_ELEMENT).put((Object)"Color", (Object)COLOR_ELEMENT).put((Object)"UV0", (Object)TEXTURE_ELEMENT).put((Object)"UV1", (Object)OVERLAY_ELEMENT).put((Object)"UV2", (Object)LIGHT_ELEMENT).put((Object)"Normal", (Object)NORMAL_ELEMENT).put((Object)"Padding", (Object)PADDING_ELEMENT).build());
    public static final VertexFormat POSITION_TEXTURE_COLOR_LIGHT = new VertexFormat((ImmutableMap<String, VertexFormatElement>)ImmutableMap.builder().put((Object)"Position", (Object)POSITION_ELEMENT).put((Object)"UV0", (Object)TEXTURE_ELEMENT).put((Object)"Color", (Object)COLOR_ELEMENT).put((Object)"UV2", (Object)LIGHT_ELEMENT).build());
    public static final VertexFormat POSITION = new VertexFormat((ImmutableMap<String, VertexFormatElement>)ImmutableMap.builder().put((Object)"Position", (Object)POSITION_ELEMENT).build());
    public static final VertexFormat POSITION_COLOR = new VertexFormat((ImmutableMap<String, VertexFormatElement>)ImmutableMap.builder().put((Object)"Position", (Object)POSITION_ELEMENT).put((Object)"Color", (Object)COLOR_ELEMENT).build());
    public static final VertexFormat LINES = new VertexFormat((ImmutableMap<String, VertexFormatElement>)ImmutableMap.builder().put((Object)"Position", (Object)POSITION_ELEMENT).put((Object)"Color", (Object)COLOR_ELEMENT).put((Object)"Normal", (Object)NORMAL_ELEMENT).put((Object)"Padding", (Object)PADDING_ELEMENT).build());
    public static final VertexFormat POSITION_COLOR_LIGHT = new VertexFormat((ImmutableMap<String, VertexFormatElement>)ImmutableMap.builder().put((Object)"Position", (Object)POSITION_ELEMENT).put((Object)"Color", (Object)COLOR_ELEMENT).put((Object)"UV2", (Object)LIGHT_ELEMENT).build());
    public static final VertexFormat POSITION_TEXTURE = new VertexFormat((ImmutableMap<String, VertexFormatElement>)ImmutableMap.builder().put((Object)"Position", (Object)POSITION_ELEMENT).put((Object)"UV0", (Object)TEXTURE_ELEMENT).build());
    public static final VertexFormat POSITION_COLOR_TEXTURE = new VertexFormat((ImmutableMap<String, VertexFormatElement>)ImmutableMap.builder().put((Object)"Position", (Object)POSITION_ELEMENT).put((Object)"Color", (Object)COLOR_ELEMENT).put((Object)"UV0", (Object)TEXTURE_ELEMENT).build());
    public static final VertexFormat POSITION_TEXTURE_COLOR = new VertexFormat((ImmutableMap<String, VertexFormatElement>)ImmutableMap.builder().put((Object)"Position", (Object)POSITION_ELEMENT).put((Object)"UV0", (Object)TEXTURE_ELEMENT).put((Object)"Color", (Object)COLOR_ELEMENT).build());
    public static final VertexFormat POSITION_COLOR_TEXTURE_LIGHT = new VertexFormat((ImmutableMap<String, VertexFormatElement>)ImmutableMap.builder().put((Object)"Position", (Object)POSITION_ELEMENT).put((Object)"Color", (Object)COLOR_ELEMENT).put((Object)"UV0", (Object)TEXTURE_ELEMENT).put((Object)"UV2", (Object)LIGHT_ELEMENT).build());
    public static final VertexFormat POSITION_TEXTURE_LIGHT_COLOR = new VertexFormat((ImmutableMap<String, VertexFormatElement>)ImmutableMap.builder().put((Object)"Position", (Object)POSITION_ELEMENT).put((Object)"UV0", (Object)TEXTURE_ELEMENT).put((Object)"UV2", (Object)LIGHT_ELEMENT).put((Object)"Color", (Object)COLOR_ELEMENT).build());
    public static final VertexFormat POSITION_TEXTURE_COLOR_NORMAL = new VertexFormat((ImmutableMap<String, VertexFormatElement>)ImmutableMap.builder().put((Object)"Position", (Object)POSITION_ELEMENT).put((Object)"UV0", (Object)TEXTURE_ELEMENT).put((Object)"Color", (Object)COLOR_ELEMENT).put((Object)"Normal", (Object)NORMAL_ELEMENT).put((Object)"Padding", (Object)PADDING_ELEMENT).build());
}

