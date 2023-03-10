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
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.HorseBaseEntityRenderer;
import net.minecraft.client.render.entity.model.DonkeyEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DonkeyEntityRenderer<T extends AbstractDonkeyEntity>
extends HorseBaseEntityRenderer<T, DonkeyEntityModel<T>> {
    private static final Map<EntityType<?>, Identifier> TEXTURES = Maps.newHashMap((Map)ImmutableMap.of(EntityType.DONKEY, (Object)new Identifier("textures/entity/horse/donkey.png"), EntityType.MULE, (Object)new Identifier("textures/entity/horse/mule.png")));

    public DonkeyEntityRenderer(EntityRendererFactory.Context ctx, float scale, EntityModelLayer layer) {
        super(ctx, new DonkeyEntityModel(ctx.getPart(layer)), scale);
    }

    @Override
    public Identifier getTexture(T abstractDonkeyEntity) {
        return TEXTURES.get(((Entity)abstractDonkeyEntity).getType());
    }
}

