/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class Model {
    protected final Function<Identifier, RenderLayer> layerFactory;

    public Model(Function<Identifier, RenderLayer> layerFactory) {
        this.layerFactory = layerFactory;
    }

    public final RenderLayer getLayer(Identifier texture) {
        return this.layerFactory.apply(texture);
    }

    public abstract void render(MatrixStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8);
}

