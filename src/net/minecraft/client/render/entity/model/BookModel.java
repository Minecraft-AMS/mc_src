/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BookModel
extends Model {
    private final ModelPart leftCover = new ModelPart(this).setTextureOffset(0, 0).addCuboid(-6.0f, -5.0f, 0.0f, 6, 10, 0);
    private final ModelPart rightCover = new ModelPart(this).setTextureOffset(16, 0).addCuboid(0.0f, -5.0f, 0.0f, 6, 10, 0);
    private final ModelPart leftBlock;
    private final ModelPart rightBlock;
    private final ModelPart leftPage;
    private final ModelPart rightPage;
    private final ModelPart spine = new ModelPart(this).setTextureOffset(12, 0).addCuboid(-1.0f, -5.0f, 0.0f, 2, 10, 0);

    public BookModel() {
        this.leftBlock = new ModelPart(this).setTextureOffset(0, 10).addCuboid(0.0f, -4.0f, -0.99f, 5, 8, 1);
        this.rightBlock = new ModelPart(this).setTextureOffset(12, 10).addCuboid(0.0f, -4.0f, -0.01f, 5, 8, 1);
        this.leftPage = new ModelPart(this).setTextureOffset(24, 10).addCuboid(0.0f, -4.0f, 0.0f, 5, 8, 0);
        this.rightPage = new ModelPart(this).setTextureOffset(24, 10).addCuboid(0.0f, -4.0f, 0.0f, 5, 8, 0);
        this.leftCover.setPivot(0.0f, 0.0f, -1.0f);
        this.rightCover.setPivot(0.0f, 0.0f, 1.0f);
        this.spine.yaw = 1.5707964f;
    }

    public void render(float ticks, float leftPageAngle, float rightPageAngle, float pageTurningSpeed, float f, float g) {
        this.setPageAngles(ticks, leftPageAngle, rightPageAngle, pageTurningSpeed, f, g);
        this.leftCover.render(g);
        this.rightCover.render(g);
        this.spine.render(g);
        this.leftBlock.render(g);
        this.rightBlock.render(g);
        this.leftPage.render(g);
        this.rightPage.render(g);
    }

    private void setPageAngles(float ticks, float leftPageAngle, float rightPageAngle, float pageTurningSpeed, float f, float g) {
        float h = (MathHelper.sin(ticks * 0.02f) * 0.1f + 1.25f) * pageTurningSpeed;
        this.leftCover.yaw = (float)Math.PI + h;
        this.rightCover.yaw = -h;
        this.leftBlock.yaw = h;
        this.rightBlock.yaw = -h;
        this.leftPage.yaw = h - h * 2.0f * leftPageAngle;
        this.rightPage.yaw = h - h * 2.0f * rightPageAngle;
        this.leftBlock.pivotX = MathHelper.sin(h);
        this.rightBlock.pivotX = MathHelper.sin(h);
        this.leftPage.pivotX = MathHelper.sin(h);
        this.rightPage.pivotX = MathHelper.sin(h);
    }
}

