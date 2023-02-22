/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.entity.model;

import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public abstract class SinglePartEntityModel<E extends Entity>
extends EntityModel<E> {
    private static final Vector3f field_39195 = new Vector3f();

    public SinglePartEntityModel() {
        this(RenderLayer::getEntityCutoutNoCull);
    }

    public SinglePartEntityModel(Function<Identifier, RenderLayer> function) {
        super(function);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.getPart().render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    public abstract ModelPart getPart();

    public Optional<ModelPart> getChild(String name) {
        if (name.equals("root")) {
            return Optional.of(this.getPart());
        }
        return this.getPart().traverse().filter(part -> part.hasChild(name)).findFirst().map(part -> part.getChild(name));
    }

    protected void updateAnimation(AnimationState animationState, Animation animation, float animationProgress) {
        this.updateAnimation(animationState, animation, animationProgress, 1.0f);
    }

    protected void updateAnimation(AnimationState animationState, Animation animation, float animationProgress, float speedMultiplier) {
        animationState.update(animationProgress, speedMultiplier);
        animationState.run(state -> AnimationHelper.animate(this, animation, state.getTimeRunning(), 1.0f, field_39195));
    }
}

