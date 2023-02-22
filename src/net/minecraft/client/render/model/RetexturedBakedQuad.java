/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.texture.Sprite;

@Environment(value=EnvType.CLIENT)
public class RetexturedBakedQuad
extends BakedQuad {
    private final Sprite spriteRetextured;

    public RetexturedBakedQuad(BakedQuad parent, Sprite sprite) {
        super(Arrays.copyOf(parent.getVertexData(), parent.getVertexData().length), parent.colorIndex, BakedQuadFactory.method_3467(parent.getVertexData()), parent.getSprite());
        this.spriteRetextured = sprite;
        this.recalculateUvs();
    }

    private void recalculateUvs() {
        for (int i = 0; i < 4; ++i) {
            int j = 7 * i;
            this.vertexData[j + 4] = Float.floatToRawIntBits(this.spriteRetextured.getFrameU(this.sprite.getXFromU(Float.intBitsToFloat(this.vertexData[j + 4]))));
            this.vertexData[j + 4 + 1] = Float.floatToRawIntBits(this.spriteRetextured.getFrameV(this.sprite.getYFromV(Float.intBitsToFloat(this.vertexData[j + 4 + 1]))));
        }
    }
}

