/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TridentRiptideFeatureRenderer<T extends LivingEntity>
extends FeatureRenderer<T, PlayerEntityModel<T>> {
    public static final Identifier TEXTURE = new Identifier("textures/entity/trident_riptide.png");
    private final TridentRiptideModel model = new TridentRiptideModel();

    public TridentRiptideFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> context) {
        super(context);
    }

    @Override
    public void render(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (!((LivingEntity)livingEntity).isUsingRiptide()) {
            return;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(TEXTURE);
        for (int m = 0; m < 3; ++m) {
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(i * (float)(-(45 + m * 5)), 0.0f, 1.0f, 0.0f);
            float n = 0.75f * (float)m;
            GlStateManager.scalef(n, n, n);
            GlStateManager.translatef(0.0f, -0.2f + 0.6f * (float)m, 0.0f);
            this.model.method_17166(f, g, i, j, k, l);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    static class TridentRiptideModel
    extends Model {
        private final ModelPart field_4900;

        public TridentRiptideModel() {
            this.textureWidth = 64;
            this.textureHeight = 64;
            this.field_4900 = new ModelPart(this, 0, 0);
            this.field_4900.addCuboid(-8.0f, -16.0f, -8.0f, 16, 32, 16);
        }

        public void method_17166(float f, float g, float h, float i, float j, float k) {
            this.field_4900.render(k);
        }
    }
}

