/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class IronGolemCrackFeatureRenderer
extends FeatureRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {
    private static final Map<IronGolemEntity.Crack, Identifier> DAMAGE_TO_TEXTURE = ImmutableMap.of((Object)((Object)IronGolemEntity.Crack.LOW), (Object)new Identifier("textures/entity/iron_golem/iron_golem_crackiness_low.png"), (Object)((Object)IronGolemEntity.Crack.MEDIUM), (Object)new Identifier("textures/entity/iron_golem/iron_golem_crackiness_medium.png"), (Object)((Object)IronGolemEntity.Crack.HIGH), (Object)new Identifier("textures/entity/iron_golem/iron_golem_crackiness_high.png"));

    public IronGolemCrackFeatureRenderer(FeatureRendererContext<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, IronGolemEntity ironGolemEntity, float f, float g, float h, float j, float k, float l) {
        if (ironGolemEntity.isInvisible()) {
            return;
        }
        IronGolemEntity.Crack crack = ironGolemEntity.getCrack();
        if (crack == IronGolemEntity.Crack.NONE) {
            return;
        }
        Identifier identifier = DAMAGE_TO_TEXTURE.get((Object)crack);
        IronGolemCrackFeatureRenderer.renderModel(this.getContextModel(), identifier, matrixStack, vertexConsumerProvider, i, ironGolemEntity, 1.0f, 1.0f, 1.0f);
    }
}

