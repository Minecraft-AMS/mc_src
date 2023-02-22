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
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ElderGuardianEntityRenderer
extends GuardianEntityRenderer {
    private static final Identifier SKIN = new Identifier("textures/entity/guardian_elder.png");

    public ElderGuardianEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, 1.2f);
    }

    @Override
    protected void scale(GuardianEntity guardianEntity, float f) {
        GlStateManager.scalef(ElderGuardianEntity.field_17492, ElderGuardianEntity.field_17492, ElderGuardianEntity.field_17492);
    }

    @Override
    protected Identifier getTexture(GuardianEntity guardianEntity) {
        return SKIN;
    }
}

