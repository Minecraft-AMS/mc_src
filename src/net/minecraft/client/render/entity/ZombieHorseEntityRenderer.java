/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.HorseBaseEntityRenderer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ZombieHorseEntityRenderer
extends HorseBaseEntityRenderer<HorseBaseEntity, HorseEntityModel<HorseBaseEntity>> {
    private static final Map<Class<?>, Identifier> TEXTURES = Maps.newHashMap((Map)ImmutableMap.of(ZombieHorseEntity.class, (Object)new Identifier("textures/entity/horse/horse_zombie.png"), SkeletonHorseEntity.class, (Object)new Identifier("textures/entity/horse/horse_skeleton.png")));

    public ZombieHorseEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new HorseEntityModel(0.0f), 1.0f);
    }

    @Override
    protected Identifier getTexture(HorseBaseEntity horseBaseEntity) {
        return TEXTURES.get(horseBaseEntity.getClass());
    }
}

