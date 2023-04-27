/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class EnchantmentScreen
extends HandledScreen<EnchantmentScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/enchanting_table.png");
    private static final Identifier BOOK_TEXTURE = new Identifier("textures/entity/enchanting_table_book.png");
    private final Random random = Random.create();
    private BookModel BOOK_MODEL;
    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float approximatePageAngle;
    public float pageRotationSpeed;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    private ItemStack stack = ItemStack.EMPTY;

    public EnchantmentScreen(EnchantmentScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.BOOK_MODEL = new BookModel(this.client.getEntityModelLoader().getModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        this.doTick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        for (int k = 0; k < 3; ++k) {
            double d = mouseX - (double)(i + 60);
            double e = mouseY - (double)(j + 14 + 19 * k);
            if (!(d >= 0.0) || !(e >= 0.0) || !(d < 108.0) || !(e < 19.0) || !((EnchantmentScreenHandler)this.handler).onButtonClick(this.client.player, k)) continue;
            this.client.interactionManager.clickButton(((EnchantmentScreenHandler)this.handler).syncId, k);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        DiffuseLighting.disableGuiDepthLighting();
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int k = (int)this.client.getWindow().getScaleFactor();
        RenderSystem.viewport((this.width - 320) / 2 * k, (this.height - 240) / 2 * k, 320 * k, 240 * k);
        Matrix4f matrix4f = new Matrix4f().translation(-0.34f, 0.23f, 0.0f).perspective(1.5707964f, 1.3333334f, 9.0f, 80.0f);
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(matrix4f, VertexSorter.BY_DISTANCE);
        context.getMatrices().push();
        context.getMatrices().loadIdentity();
        context.getMatrices().translate(0.0f, 3.3f, 1984.0f);
        float f = 5.0f;
        context.getMatrices().scale(5.0f, 5.0f, 5.0f);
        context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(20.0f));
        float g = MathHelper.lerp(delta, this.pageTurningSpeed, this.nextPageTurningSpeed);
        context.getMatrices().translate((1.0f - g) * 0.2f, (1.0f - g) * 0.1f, (1.0f - g) * 0.25f);
        float h = -(1.0f - g) * 90.0f - 90.0f;
        context.getMatrices().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(h));
        context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
        float l = MathHelper.lerp(delta, this.pageAngle, this.nextPageAngle) + 0.25f;
        float m = MathHelper.lerp(delta, this.pageAngle, this.nextPageAngle) + 0.75f;
        l = (l - (float)MathHelper.floor(l)) * 1.6f - 0.3f;
        m = (m - (float)MathHelper.floor(m)) * 1.6f - 0.3f;
        if (l < 0.0f) {
            l = 0.0f;
        }
        if (m < 0.0f) {
            m = 0.0f;
        }
        if (l > 1.0f) {
            l = 1.0f;
        }
        if (m > 1.0f) {
            m = 1.0f;
        }
        this.BOOK_MODEL.setPageAngles(0.0f, l, m, g);
        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(this.BOOK_MODEL.getLayer(BOOK_TEXTURE));
        this.BOOK_MODEL.render(context.getMatrices(), vertexConsumer, 0xF000F0, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        context.draw();
        context.getMatrices().pop();
        RenderSystem.viewport(0, 0, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
        RenderSystem.restoreProjectionMatrix();
        DiffuseLighting.enableGuiDepthLighting();
        EnchantingPhrases.getInstance().setSeed(((EnchantmentScreenHandler)this.handler).getSeed());
        int n = ((EnchantmentScreenHandler)this.handler).getLapisCount();
        for (int o = 0; o < 3; ++o) {
            int p = i + 60;
            int q = p + 20;
            int r = ((EnchantmentScreenHandler)this.handler).enchantmentPower[o];
            if (r == 0) {
                context.drawTexture(TEXTURE, p, j + 14 + 19 * o, 0, 185, 108, 19);
                continue;
            }
            String string = "" + r;
            int s = 86 - this.textRenderer.getWidth(string);
            StringVisitable stringVisitable = EnchantingPhrases.getInstance().generatePhrase(this.textRenderer, s);
            int t = 6839882;
            if (!(n >= o + 1 && this.client.player.experienceLevel >= r || this.client.player.getAbilities().creativeMode)) {
                context.drawTexture(TEXTURE, p, j + 14 + 19 * o, 0, 185, 108, 19);
                context.drawTexture(TEXTURE, p + 1, j + 15 + 19 * o, 16 * o, 239, 16, 16);
                context.drawTextWrapped(this.textRenderer, stringVisitable, q, j + 16 + 19 * o, s, (t & 0xFEFEFE) >> 1);
                t = 4226832;
            } else {
                int u = mouseX - (i + 60);
                int v = mouseY - (j + 14 + 19 * o);
                if (u >= 0 && v >= 0 && u < 108 && v < 19) {
                    context.drawTexture(TEXTURE, p, j + 14 + 19 * o, 0, 204, 108, 19);
                    t = 0xFFFF80;
                } else {
                    context.drawTexture(TEXTURE, p, j + 14 + 19 * o, 0, 166, 108, 19);
                }
                context.drawTexture(TEXTURE, p + 1, j + 15 + 19 * o, 16 * o, 223, 16, 16);
                context.drawTextWrapped(this.textRenderer, stringVisitable, q, j + 16 + 19 * o, s, t);
                t = 8453920;
            }
            context.drawTextWithShadow(this.textRenderer, string, q + 86 - this.textRenderer.getWidth(string), j + 16 + 19 * o + 7, t);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        delta = this.client.getTickDelta();
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
        boolean bl = this.client.player.getAbilities().creativeMode;
        int i = ((EnchantmentScreenHandler)this.handler).getLapisCount();
        for (int j = 0; j < 3; ++j) {
            int k = ((EnchantmentScreenHandler)this.handler).enchantmentPower[j];
            Enchantment enchantment = Enchantment.byRawId(((EnchantmentScreenHandler)this.handler).enchantmentId[j]);
            int l = ((EnchantmentScreenHandler)this.handler).enchantmentLevel[j];
            int m = j + 1;
            if (!this.isPointWithinBounds(60, 14 + 19 * j, 108, 17, mouseX, mouseY) || k <= 0 || l < 0 || enchantment == null) continue;
            ArrayList list = Lists.newArrayList();
            list.add(Text.translatable("container.enchant.clue", enchantment.getName(l)).formatted(Formatting.WHITE));
            if (!bl) {
                list.add(ScreenTexts.EMPTY);
                if (this.client.player.experienceLevel < k) {
                    list.add(Text.translatable("container.enchant.level.requirement", ((EnchantmentScreenHandler)this.handler).enchantmentPower[j]).formatted(Formatting.RED));
                } else {
                    MutableText mutableText = m == 1 ? Text.translatable("container.enchant.lapis.one") : Text.translatable("container.enchant.lapis.many", m);
                    list.add(mutableText.formatted(i >= m ? Formatting.GRAY : Formatting.RED));
                    MutableText mutableText2 = m == 1 ? Text.translatable("container.enchant.level.one") : Text.translatable("container.enchant.level.many", m);
                    list.add(mutableText2.formatted(Formatting.GRAY));
                }
            }
            context.drawTooltip(this.textRenderer, list, mouseX, mouseY);
            break;
        }
    }

    public void doTick() {
        ItemStack itemStack = ((EnchantmentScreenHandler)this.handler).getSlot(0).getStack();
        if (!ItemStack.areEqual(itemStack, this.stack)) {
            this.stack = itemStack;
            do {
                this.approximatePageAngle += (float)(this.random.nextInt(4) - this.random.nextInt(4));
            } while (this.nextPageAngle <= this.approximatePageAngle + 1.0f && this.nextPageAngle >= this.approximatePageAngle - 1.0f);
        }
        ++this.ticks;
        this.pageAngle = this.nextPageAngle;
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        boolean bl = false;
        for (int i = 0; i < 3; ++i) {
            if (((EnchantmentScreenHandler)this.handler).enchantmentPower[i] == 0) continue;
            bl = true;
        }
        this.nextPageTurningSpeed = bl ? (this.nextPageTurningSpeed += 0.2f) : (this.nextPageTurningSpeed -= 0.2f);
        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0f, 1.0f);
        float f = (this.approximatePageAngle - this.nextPageAngle) * 0.4f;
        float g = 0.2f;
        f = MathHelper.clamp(f, -0.2f, 0.2f);
        this.pageRotationSpeed += (f - this.pageRotationSpeed) * 0.9f;
        this.nextPageAngle += this.pageRotationSpeed;
    }
}

