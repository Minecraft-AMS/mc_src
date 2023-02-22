/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.LlamaDecorFeatureRenderer;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class LlamaEntityRenderer
extends MobEntityRenderer<LlamaEntity, LlamaEntityModel<LlamaEntity>> {
    private static final Identifier[] TEXTURES = new Identifier[]{new Identifier("textures/entity/llama/creamy.png"), new Identifier("textures/entity/llama/white.png"), new Identifier("textures/entity/llama/brown.png"), new Identifier("textures/entity/llama/gray.png")};

    public LlamaEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new LlamaEntityModel(0.0f), 0.7f);
        this.addFeature(new LlamaDecorFeatureRenderer(this));
    }

    @Override
    protected Identifier getTexture(LlamaEntity llamaEntity) {
        return TEXTURES[llamaEntity.getVariant()];
    }
}

