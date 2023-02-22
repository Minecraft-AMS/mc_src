/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.entity.feature;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class ArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>>
extends FeatureRenderer<T, M> {
    protected static final Identifier SKIN = new Identifier("textures/misc/enchanted_item_glint.png");
    protected final A modelLeggings;
    protected final A modelBody;
    private float alpha = 1.0f;
    private float red = 1.0f;
    private float green = 1.0f;
    private float blue = 1.0f;
    private boolean ignoreGlint;
    private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();

    protected ArmorFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext, A bipedEntityModel, A bipedEntityModel2) {
        super(featureRendererContext);
        this.modelLeggings = bipedEntityModel;
        this.modelBody = bipedEntityModel2;
    }

    @Override
    public void render(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
        this.renderArmor(livingEntity, f, g, h, i, j, k, l, EquipmentSlot.CHEST);
        this.renderArmor(livingEntity, f, g, h, i, j, k, l, EquipmentSlot.LEGS);
        this.renderArmor(livingEntity, f, g, h, i, j, k, l, EquipmentSlot.FEET);
        this.renderArmor(livingEntity, f, g, h, i, j, k, l, EquipmentSlot.HEAD);
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }

    private void renderArmor(T livingEntity, float f, float g, float h, float i, float j, float k, float l, EquipmentSlot equipmentSlot) {
        ItemStack itemStack = ((LivingEntity)livingEntity).getEquippedStack(equipmentSlot);
        if (!(itemStack.getItem() instanceof ArmorItem)) {
            return;
        }
        ArmorItem armorItem = (ArmorItem)itemStack.getItem();
        if (armorItem.getSlotType() != equipmentSlot) {
            return;
        }
        A bipedEntityModel = this.getArmor(equipmentSlot);
        ((BipedEntityModel)this.getContextModel()).setAttributes(bipedEntityModel);
        ((BipedEntityModel)bipedEntityModel).animateModel(livingEntity, f, g, h);
        this.method_4170(bipedEntityModel, equipmentSlot);
        boolean bl = this.isLegs(equipmentSlot);
        this.bindTexture(this.getArmorTexture(armorItem, bl));
        if (armorItem instanceof DyeableArmorItem) {
            int m = ((DyeableArmorItem)armorItem).getColor(itemStack);
            float n = (float)(m >> 16 & 0xFF) / 255.0f;
            float o = (float)(m >> 8 & 0xFF) / 255.0f;
            float p = (float)(m & 0xFF) / 255.0f;
            GlStateManager.color4f(this.red * n, this.green * o, this.blue * p, this.alpha);
            ((BipedEntityModel)bipedEntityModel).render(livingEntity, f, g, i, j, k, l);
            this.bindTexture(this.method_4174(armorItem, bl, "overlay"));
        }
        GlStateManager.color4f(this.red, this.green, this.blue, this.alpha);
        ((BipedEntityModel)bipedEntityModel).render(livingEntity, f, g, i, j, k, l);
        if (!this.ignoreGlint && itemStack.hasEnchantments()) {
            ArmorFeatureRenderer.renderEnchantedGlint(this::bindTexture, livingEntity, bipedEntityModel, f, g, h, i, j, k, l);
        }
    }

    public A getArmor(EquipmentSlot equipmentSlot) {
        return this.isLegs(equipmentSlot) ? this.modelLeggings : this.modelBody;
    }

    private boolean isLegs(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.LEGS;
    }

    public static <T extends Entity> void renderEnchantedGlint(Consumer<Identifier> consumer, T entity, EntityModel<T> entityModel, float f, float g, float h, float i, float j, float k, float l) {
        float m = (float)entity.age + h;
        consumer.accept(SKIN);
        GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
        gameRenderer.setFogBlack(true);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        float n = 0.5f;
        GlStateManager.color4f(0.5f, 0.5f, 0.5f, 1.0f);
        for (int o = 0; o < 2; ++o) {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
            float p = 0.76f;
            GlStateManager.color4f(0.38f, 0.19f, 0.608f, 1.0f);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float q = 0.33333334f;
            GlStateManager.scalef(0.33333334f, 0.33333334f, 0.33333334f);
            GlStateManager.rotatef(30.0f - (float)o * 60.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.translatef(0.0f, m * (0.001f + (float)o * 0.003f) * 20.0f, 0.0f);
            GlStateManager.matrixMode(5888);
            entityModel.render(entity, f, g, i, j, k, l);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
        gameRenderer.setFogBlack(false);
    }

    private Identifier getArmorTexture(ArmorItem armor, boolean bl) {
        return this.method_4174(armor, bl, null);
    }

    private Identifier method_4174(ArmorItem armorItem, boolean bl, @Nullable String string) {
        String string2 = "textures/models/armor/" + armorItem.getMaterial().getName() + "_layer_" + (bl ? 2 : 1) + (string == null ? "" : "_" + string) + ".png";
        return ARMOR_TEXTURE_CACHE.computeIfAbsent(string2, Identifier::new);
    }

    protected abstract void method_4170(A var1, EquipmentSlot var2);

    protected abstract void method_4190(A var1);
}

