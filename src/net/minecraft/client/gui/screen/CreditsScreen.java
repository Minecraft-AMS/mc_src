/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.resource.Resource;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class CreditsScreen
extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
    private static final Identifier EDITION_TITLE_TEXTURE = new Identifier("textures/gui/title/edition.png");
    private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");
    private final boolean endCredits;
    private final Runnable finishAction;
    private float field_2628;
    private List<String> field_2634;
    private int field_2629;
    private float field_2635 = 0.5f;

    public CreditsScreen(boolean endCredits, Runnable finishAction) {
        super(NarratorManager.EMPTY);
        this.endCredits = endCredits;
        this.finishAction = finishAction;
        if (!endCredits) {
            this.field_2635 = 0.75f;
        }
    }

    @Override
    public void tick() {
        this.minecraft.getMusicTracker().tick();
        this.minecraft.getSoundManager().tick(false);
        float f = (float)(this.field_2629 + this.height + this.height + 24) / this.field_2635;
        if (this.field_2628 > f) {
            this.close();
        }
    }

    @Override
    public void onClose() {
        this.close();
    }

    private void close() {
        this.finishAction.run();
        this.minecraft.openScreen(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void init() {
        if (this.field_2634 != null) {
            return;
        }
        this.field_2634 = Lists.newArrayList();
        Resource resource = null;
        try {
            String string5;
            BufferedReader bufferedReader;
            InputStream inputStream;
            String string = "" + (Object)((Object)Formatting.WHITE) + (Object)((Object)Formatting.OBFUSCATED) + (Object)((Object)Formatting.GREEN) + (Object)((Object)Formatting.AQUA);
            int i = 274;
            if (this.endCredits) {
                int j;
                String string2;
                resource = this.minecraft.getResourceManager().getResource(new Identifier("texts/end.txt"));
                inputStream = resource.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                Random random = new Random(8124371L);
                while ((string2 = bufferedReader.readLine()) != null) {
                    string2 = string2.replaceAll("PLAYERNAME", this.minecraft.getSession().getUsername());
                    while (string2.contains(string)) {
                        j = string2.indexOf(string);
                        String string3 = string2.substring(0, j);
                        String string4 = string2.substring(j + string.length());
                        string2 = string3 + (Object)((Object)Formatting.WHITE) + (Object)((Object)Formatting.OBFUSCATED) + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + string4;
                    }
                    this.field_2634.addAll(this.minecraft.textRenderer.wrapStringToWidthAsList(string2, 274));
                    this.field_2634.add("");
                }
                inputStream.close();
                for (j = 0; j < 8; ++j) {
                    this.field_2634.add("");
                }
            }
            inputStream = this.minecraft.getResourceManager().getResource(new Identifier("texts/credits.txt")).getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            while ((string5 = bufferedReader.readLine()) != null) {
                string5 = string5.replaceAll("PLAYERNAME", this.minecraft.getSession().getUsername());
                string5 = string5.replaceAll("\t", "    ");
                this.field_2634.addAll(this.minecraft.textRenderer.wrapStringToWidthAsList(string5, 274));
                this.field_2634.add("");
            }
            inputStream.close();
            this.field_2629 = this.field_2634.size() * 12;
            IOUtils.closeQuietly((Closeable)resource);
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't load credits", (Throwable)exception);
        }
        finally {
            IOUtils.closeQuietly(resource);
        }
    }

    private void method_2258(int i, int j, float f) {
        this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
        int k = this.width;
        float g = -this.field_2628 * 0.5f * this.field_2635;
        float h = (float)this.height - this.field_2628 * 0.5f * this.field_2635;
        float l = 0.015625f;
        float m = this.field_2628 * 0.02f;
        float n = (float)(this.field_2629 + this.height + this.height + 24) / this.field_2635;
        float o = (n - 20.0f - this.field_2628) * 0.005f;
        if (o < m) {
            m = o;
        }
        if (m > 1.0f) {
            m = 1.0f;
        }
        m *= m;
        m = m * 96.0f / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0, this.height, this.blitOffset).texture(0.0, g * 0.015625f).color(m, m, m, 1.0f).next();
        bufferBuilder.vertex(k, this.height, this.blitOffset).texture((float)k * 0.015625f, g * 0.015625f).color(m, m, m, 1.0f).next();
        bufferBuilder.vertex(k, 0.0, this.blitOffset).texture((float)k * 0.015625f, h * 0.015625f).color(m, m, m, 1.0f).next();
        bufferBuilder.vertex(0.0, 0.0, this.blitOffset).texture(0.0, h * 0.015625f).color(m, m, m, 1.0f).next();
        tessellator.draw();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        int m;
        this.method_2258(mouseX, mouseY, delta);
        int i = 274;
        int j = this.width / 2 - 137;
        int k = this.height + 50;
        this.field_2628 += delta;
        float f = -this.field_2628 * this.field_2635;
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, f, 0.0f);
        this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURE);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableAlphaTest();
        this.blit(j, k, 0, 0, 155, 44);
        this.blit(j + 155, k, 0, 45, 155, 44);
        this.minecraft.getTextureManager().bindTexture(EDITION_TITLE_TEXTURE);
        CreditsScreen.blit(j + 88, k + 37, 0.0f, 0.0f, 98, 14, 128, 16);
        GlStateManager.disableAlphaTest();
        int l = k + 100;
        for (m = 0; m < this.field_2634.size(); ++m) {
            float g;
            if (m == this.field_2634.size() - 1 && (g = (float)l + f - (float)(this.height / 2 - 6)) < 0.0f) {
                GlStateManager.translatef(0.0f, -g, 0.0f);
            }
            if ((float)l + f + 12.0f + 8.0f > 0.0f && (float)l + f < (float)this.height) {
                String string = this.field_2634.get(m);
                if (string.startsWith("[C]")) {
                    this.font.drawWithShadow(string.substring(3), j + (274 - this.font.getStringWidth(string.substring(3))) / 2, l, 0xFFFFFF);
                } else {
                    this.font.random.setSeed((long)((float)((long)m * 4238972211L) + this.field_2628 / 4.0f));
                    this.font.drawWithShadow(string, j, l, 0xFFFFFF);
                }
            }
            l += 12;
        }
        GlStateManager.popMatrix();
        this.minecraft.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        m = this.width;
        int n = this.height;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0, n, this.blitOffset).texture(0.0, 1.0).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(m, n, this.blitOffset).texture(1.0, 1.0).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(m, 0.0, this.blitOffset).texture(1.0, 0.0).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(0.0, 0.0, this.blitOffset).texture(0.0, 0.0).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        tessellator.draw();
        GlStateManager.disableBlend();
        super.render(mouseX, mouseY, delta);
    }
}

