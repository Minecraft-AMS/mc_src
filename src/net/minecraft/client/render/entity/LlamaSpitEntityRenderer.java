/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.LlamaSpitEntityModel;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class LlamaSpitEntityRenderer
extends EntityRenderer<LlamaSpitEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/llama/spit.png");
    private final LlamaSpitEntityModel<LlamaSpitEntity> model = new LlamaSpitEntityModel();

    public LlamaSpitEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public void render(LlamaSpitEntity llamaSpitEntity, double d, double e, double f, float g, float h) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)d, (float)e + 0.15f, (float)f);
        GlStateManager.rotatef(MathHelper.lerp(h, llamaSpitEntity.prevYaw, llamaSpitEntity.yaw) - 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(MathHelper.lerp(h, llamaSpitEntity.prevPitch, llamaSpitEntity.pitch), 0.0f, 0.0f, 1.0f);
        this.bindEntityTexture(llamaSpitEntity);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(llamaSpitEntity));
        }
        this.model.render(llamaSpitEntity, h, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        super.render(llamaSpitEntity, d, e, f, g, h);
    }

    @Override
    protected Identifier getTexture(LlamaSpitEntity llamaSpitEntity) {
        return SKIN;
    }
}

