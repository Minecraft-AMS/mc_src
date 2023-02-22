/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class AxolotlEntityRenderer
extends MobEntityRenderer<AxolotlEntity, AxolotlEntityModel<AxolotlEntity>> {
    private static final Map<AxolotlEntity.Variant, Identifier> TEXTURES = Util.make(Maps.newHashMap(), variants -> {
        for (AxolotlEntity.Variant variant : AxolotlEntity.Variant.values()) {
            variants.put(variant, new Identifier(String.format(Locale.ROOT, "textures/entity/axolotl/axolotl_%s.png", variant.getName())));
        }
    });

    public AxolotlEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new AxolotlEntityModel(context.getPart(EntityModelLayers.AXOLOTL)), 0.5f);
    }

    @Override
    public Identifier getTexture(AxolotlEntity axolotlEntity) {
        return TEXTURES.get(axolotlEntity.getVariant());
    }
}

