/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.MaterialColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MapRenderer
implements AutoCloseable {
    private static final Identifier MAP_ICONS_TEXTURE = new Identifier("textures/map/map_icons.png");
    private final TextureManager textureManager;
    private final Map<String, MapTexture> mapTextures = Maps.newHashMap();

    public MapRenderer(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public void updateTexture(MapState mapState) {
        this.getMapTexture(mapState).updateTexture();
    }

    public void draw(MapState mapState, boolean hidePlayerSpecific) {
        this.getMapTexture(mapState).draw(hidePlayerSpecific);
    }

    private MapTexture getMapTexture(MapState mapState) {
        MapTexture mapTexture = this.mapTextures.get(mapState.getId());
        if (mapTexture == null) {
            mapTexture = new MapTexture(mapState);
            this.mapTextures.put(mapState.getId(), mapTexture);
        }
        return mapTexture;
    }

    @Nullable
    public MapTexture getTexture(String string) {
        return this.mapTextures.get(string);
    }

    public void clearStateTextures() {
        for (MapTexture mapTexture : this.mapTextures.values()) {
            mapTexture.close();
        }
        this.mapTextures.clear();
    }

    @Nullable
    public MapState getState(@Nullable MapTexture texture) {
        if (texture != null) {
            return texture.mapState;
        }
        return null;
    }

    @Override
    public void close() {
        this.clearStateTextures();
    }

    @Environment(value=EnvType.CLIENT)
    class MapTexture
    implements AutoCloseable {
        private final MapState mapState;
        private final NativeImageBackedTexture texture;
        private final Identifier id;

        private MapTexture(MapState mapState) {
            this.mapState = mapState;
            this.texture = new NativeImageBackedTexture(128, 128, true);
            this.id = MapRenderer.this.textureManager.registerDynamicTexture("map/" + mapState.getId(), this.texture);
        }

        private void updateTexture() {
            for (int i = 0; i < 128; ++i) {
                for (int j = 0; j < 128; ++j) {
                    int k = j + i * 128;
                    int l = this.mapState.colors[k] & 0xFF;
                    if (l / 4 == 0) {
                        this.texture.getImage().setPixelRgba(j, i, (k + k / 128 & 1) * 8 + 16 << 24);
                        continue;
                    }
                    this.texture.getImage().setPixelRgba(j, i, MaterialColor.COLORS[l / 4].getRenderColor(l & 3));
                }
            }
            this.texture.upload();
        }

        private void draw(boolean hidePlayerSpecific) {
            boolean i = false;
            boolean j = false;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            float f = 0.0f;
            MapRenderer.this.textureManager.bindTexture(this.id);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            GlStateManager.disableAlphaTest();
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(0.0, 128.0, -0.01f).texture(0.0, 1.0).next();
            bufferBuilder.vertex(128.0, 128.0, -0.01f).texture(1.0, 1.0).next();
            bufferBuilder.vertex(128.0, 0.0, -0.01f).texture(1.0, 0.0).next();
            bufferBuilder.vertex(0.0, 0.0, -0.01f).texture(0.0, 0.0).next();
            tessellator.draw();
            GlStateManager.enableAlphaTest();
            GlStateManager.disableBlend();
            int k = 0;
            for (MapIcon mapIcon : this.mapState.icons.values()) {
                if (hidePlayerSpecific && !mapIcon.isAlwaysRendered()) continue;
                MapRenderer.this.textureManager.bindTexture(MAP_ICONS_TEXTURE);
                GlStateManager.pushMatrix();
                GlStateManager.translatef(0.0f + (float)mapIcon.getX() / 2.0f + 64.0f, 0.0f + (float)mapIcon.getZ() / 2.0f + 64.0f, -0.02f);
                GlStateManager.rotatef((float)(mapIcon.getRotation() * 360) / 16.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.scalef(4.0f, 4.0f, 3.0f);
                GlStateManager.translatef(-0.125f, 0.125f, 0.0f);
                byte b = mapIcon.getTypeId();
                float g = (float)(b % 16 + 0) / 16.0f;
                float h = (float)(b / 16 + 0) / 16.0f;
                float l = (float)(b % 16 + 1) / 16.0f;
                float m = (float)(b / 16 + 1) / 16.0f;
                bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                float n = -0.001f;
                bufferBuilder.vertex(-1.0, 1.0, (float)k * -0.001f).texture(g, h).next();
                bufferBuilder.vertex(1.0, 1.0, (float)k * -0.001f).texture(l, h).next();
                bufferBuilder.vertex(1.0, -1.0, (float)k * -0.001f).texture(l, m).next();
                bufferBuilder.vertex(-1.0, -1.0, (float)k * -0.001f).texture(g, m).next();
                tessellator.draw();
                GlStateManager.popMatrix();
                if (mapIcon.getText() != null) {
                    TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                    String string = mapIcon.getText().asFormattedString();
                    float o = textRenderer.getStringWidth(string);
                    float f2 = 25.0f / o;
                    textRenderer.getClass();
                    float p = MathHelper.clamp(f2, 0.0f, 6.0f / 9.0f);
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef(0.0f + (float)mapIcon.getX() / 2.0f + 64.0f - o * p / 2.0f, 0.0f + (float)mapIcon.getZ() / 2.0f + 64.0f + 4.0f, -0.025f);
                    GlStateManager.scalef(p, p, 1.0f);
                    DrawableHelper.fill(-1, -1, (int)o, textRenderer.fontHeight - 1, Integer.MIN_VALUE);
                    GlStateManager.translatef(0.0f, 0.0f, -0.1f);
                    textRenderer.draw(string, 0.0f, 0.0f, -1);
                    GlStateManager.popMatrix();
                }
                ++k;
            }
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 0.0f, -0.04f);
            GlStateManager.scalef(1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
        }

        @Override
        public void close() {
            this.texture.close();
        }
    }
}

