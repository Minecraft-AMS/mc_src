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
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.HorseBaseEntityRenderer;
import net.minecraft.client.render.entity.feature.HorseArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HorseMarkingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public final class HorseEntityRenderer
extends HorseBaseEntityRenderer<HorseEntity, HorseEntityModel<HorseEntity>> {
    private static final Map<HorseColor, Identifier> TEXTURES = Util.make(Maps.newEnumMap(HorseColor.class), enumMap -> {
        enumMap.put(HorseColor.WHITE, new Identifier("textures/entity/horse/horse_white.png"));
        enumMap.put(HorseColor.CREAMY, new Identifier("textures/entity/horse/horse_creamy.png"));
        enumMap.put(HorseColor.CHESTNUT, new Identifier("textures/entity/horse/horse_chestnut.png"));
        enumMap.put(HorseColor.BROWN, new Identifier("textures/entity/horse/horse_brown.png"));
        enumMap.put(HorseColor.BLACK, new Identifier("textures/entity/horse/horse_black.png"));
        enumMap.put(HorseColor.GRAY, new Identifier("textures/entity/horse/horse_gray.png"));
        enumMap.put(HorseColor.DARKBROWN, new Identifier("textures/entity/horse/horse_darkbrown.png"));
    });

    public HorseEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new HorseEntityModel(context.getPart(EntityModelLayers.HORSE)), 1.1f);
        this.addFeature(new HorseMarkingFeatureRenderer(this));
        this.addFeature(new HorseArmorFeatureRenderer(this, context.getModelLoader()));
    }

    @Override
    public Identifier getTexture(HorseEntity horseEntity) {
        return TEXTURES.get((Object)horseEntity.getColor());
    }
}

