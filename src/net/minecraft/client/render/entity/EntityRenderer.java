/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class EntityRenderer<T extends Entity> {
    private static final Identifier SHADOW_TEX = new Identifier("textures/misc/shadow.png");
    protected final EntityRenderDispatcher renderManager;
    protected float field_4673;
    protected float field_4672 = 1.0f;
    protected boolean renderOutlines;

    protected EntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        this.renderManager = entityRenderDispatcher;
    }

    public void setRenderOutlines(boolean renderOutlines) {
        this.renderOutlines = renderOutlines;
    }

    public boolean isVisible(T entity, VisibleRegion visibleRegion, double d, double e, double f) {
        if (!((Entity)entity).shouldRender(d, e, f)) {
            return false;
        }
        if (((Entity)entity).ignoreCameraFrustum) {
            return true;
        }
        Box box = ((Entity)entity).getVisibilityBoundingBox().expand(0.5);
        if (box.isValid() || box.getAverageSideLength() == 0.0) {
            box = new Box(((Entity)entity).x - 2.0, ((Entity)entity).y - 2.0, ((Entity)entity).z - 2.0, ((Entity)entity).x + 2.0, ((Entity)entity).y + 2.0, ((Entity)entity).z + 2.0);
        }
        return visibleRegion.intersects(box);
    }

    public void render(T entity, double x, double y, double z, float yaw, float tickDelta) {
        if (!this.renderOutlines) {
            this.renderLabelIfPresent(entity, x, y, z);
        }
    }

    protected int getOutlineColor(T entity) {
        Team team = (Team)((Entity)entity).getScoreboardTeam();
        if (team != null && team.getColor().getColorValue() != null) {
            return team.getColor().getColorValue();
        }
        return 0xFFFFFF;
    }

    protected void renderLabelIfPresent(T entity, double x, double y, double z) {
        if (!this.hasLabel(entity)) {
            return;
        }
        this.renderLabel(entity, ((Entity)entity).getDisplayName().asFormattedString(), x, y, z, 64);
    }

    protected boolean hasLabel(T entity) {
        return ((Entity)entity).shouldRenderName() && ((Entity)entity).hasCustomName();
    }

    protected void renderLabel(T entity, double x, double y, double z, String text, double d) {
        this.renderLabel(entity, text, x, y, z, 64);
    }

    @Nullable
    protected abstract Identifier getTexture(T var1);

    protected boolean bindEntityTexture(T entity) {
        Identifier identifier = this.getTexture(entity);
        if (identifier == null) {
            return false;
        }
        this.bindTexture(identifier);
        return true;
    }

    public void bindTexture(Identifier textureId) {
        this.renderManager.textureManager.bindTexture(textureId);
    }

    private void renderEntityOnFire(Entity entity, double x, double y, double z, float f) {
        GlStateManager.disableLighting();
        SpriteAtlasTexture spriteAtlasTexture = MinecraftClient.getInstance().getSpriteAtlas();
        Sprite sprite = spriteAtlasTexture.getSprite(ModelLoader.FIRE_0);
        Sprite sprite2 = spriteAtlasTexture.getSprite(ModelLoader.FIRE_1);
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)x, (float)y, (float)z);
        float g = entity.getWidth() * 1.4f;
        GlStateManager.scalef(g, g, g);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        float h = 0.5f;
        float i = 0.0f;
        float j = entity.getHeight() / g;
        float k = (float)(entity.y - entity.getBoundingBox().y1);
        GlStateManager.rotatef(-this.renderManager.cameraYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.0f, 0.0f, -0.3f + (float)((int)j) * 0.02f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float l = 0.0f;
        int m = 0;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        while (j > 0.0f) {
            Sprite sprite3 = m % 2 == 0 ? sprite : sprite2;
            this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            float n = sprite3.getMinU();
            float o = sprite3.getMinV();
            float p = sprite3.getMaxU();
            float q = sprite3.getMaxV();
            if (m / 2 % 2 == 0) {
                float r = p;
                p = n;
                n = r;
            }
            bufferBuilder.vertex(h - 0.0f, 0.0f - k, l).texture(p, q).next();
            bufferBuilder.vertex(-h - 0.0f, 0.0f - k, l).texture(n, q).next();
            bufferBuilder.vertex(-h - 0.0f, 1.4f - k, l).texture(n, o).next();
            bufferBuilder.vertex(h - 0.0f, 1.4f - k, l).texture(p, o).next();
            j -= 0.45f;
            k -= 0.45f;
            h *= 0.9f;
            l += 0.03f;
            ++m;
        }
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }

    private void renderShadow(Entity entity, double d, double e, double f, float g, float h) {
        MobEntity mobEntity;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.renderManager.textureManager.bindTexture(SHADOW_TEX);
        CollisionView collisionView = this.getWorld();
        GlStateManager.depthMask(false);
        float i = this.field_4673;
        if (entity instanceof MobEntity && (mobEntity = (MobEntity)entity).isBaby()) {
            i *= 0.5f;
        }
        double j = MathHelper.lerp((double)h, entity.lastRenderX, entity.x);
        double k = MathHelper.lerp((double)h, entity.lastRenderY, entity.y);
        double l = MathHelper.lerp((double)h, entity.lastRenderZ, entity.z);
        int m = MathHelper.floor(j - (double)i);
        int n = MathHelper.floor(j + (double)i);
        int o = MathHelper.floor(k - (double)i);
        int p = MathHelper.floor(k);
        int q = MathHelper.floor(l - (double)i);
        int r = MathHelper.floor(l + (double)i);
        double s = d - j;
        double t = e - k;
        double u = f - l;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        for (BlockPos blockPos : BlockPos.iterate(new BlockPos(m, o, q), new BlockPos(n, p, r))) {
            BlockPos blockPos2 = blockPos.down();
            BlockState blockState = collisionView.getBlockState(blockPos2);
            if (blockState.getRenderType() == BlockRenderType.INVISIBLE || collisionView.getLightLevel(blockPos) <= 3) continue;
            this.projectShadow(blockState, collisionView, blockPos2, d, e, f, blockPos, g, i, s, t, u);
        }
        tessellator.draw();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }

    private CollisionView getWorld() {
        return this.renderManager.world;
    }

    private void projectShadow(BlockState blockState, CollisionView collisionView, BlockPos blockPos, double d, double e, double f, BlockPos blockPos2, float g, float h, double i, double j, double k) {
        if (!blockState.method_21743(collisionView, blockPos)) {
            return;
        }
        VoxelShape voxelShape = blockState.getOutlineShape(this.getWorld(), blockPos2.down());
        if (voxelShape.isEmpty()) {
            return;
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        double l = ((double)g - (e - ((double)blockPos2.getY() + j)) / 2.0) * 0.5 * (double)this.getWorld().getBrightness(blockPos2);
        if (l < 0.0) {
            return;
        }
        if (l > 1.0) {
            l = 1.0;
        }
        Box box = voxelShape.getBoundingBox();
        double m = (double)blockPos2.getX() + box.x1 + i;
        double n = (double)blockPos2.getX() + box.x2 + i;
        double o = (double)blockPos2.getY() + box.y1 + j + 0.015625;
        double p = (double)blockPos2.getZ() + box.z1 + k;
        double q = (double)blockPos2.getZ() + box.z2 + k;
        float r = (float)((d - m) / 2.0 / (double)h + 0.5);
        float s = (float)((d - n) / 2.0 / (double)h + 0.5);
        float t = (float)((f - p) / 2.0 / (double)h + 0.5);
        float u = (float)((f - q) / 2.0 / (double)h + 0.5);
        bufferBuilder.vertex(m, o, p).texture(r, t).color(1.0f, 1.0f, 1.0f, (float)l).next();
        bufferBuilder.vertex(m, o, q).texture(r, u).color(1.0f, 1.0f, 1.0f, (float)l).next();
        bufferBuilder.vertex(n, o, q).texture(s, u).color(1.0f, 1.0f, 1.0f, (float)l).next();
        bufferBuilder.vertex(n, o, p).texture(s, t).color(1.0f, 1.0f, 1.0f, (float)l).next();
    }

    public static void renderBox(Box box, double x, double y, double z) {
        GlStateManager.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        bufferBuilder.setOffset(x, y, z);
        bufferBuilder.begin(7, VertexFormats.POSITION_NORMAL);
        bufferBuilder.vertex(box.x1, box.y2, box.z1).normal(0.0f, 0.0f, -1.0f).next();
        bufferBuilder.vertex(box.x2, box.y2, box.z1).normal(0.0f, 0.0f, -1.0f).next();
        bufferBuilder.vertex(box.x2, box.y1, box.z1).normal(0.0f, 0.0f, -1.0f).next();
        bufferBuilder.vertex(box.x1, box.y1, box.z1).normal(0.0f, 0.0f, -1.0f).next();
        bufferBuilder.vertex(box.x1, box.y1, box.z2).normal(0.0f, 0.0f, 1.0f).next();
        bufferBuilder.vertex(box.x2, box.y1, box.z2).normal(0.0f, 0.0f, 1.0f).next();
        bufferBuilder.vertex(box.x2, box.y2, box.z2).normal(0.0f, 0.0f, 1.0f).next();
        bufferBuilder.vertex(box.x1, box.y2, box.z2).normal(0.0f, 0.0f, 1.0f).next();
        bufferBuilder.vertex(box.x1, box.y1, box.z1).normal(0.0f, -1.0f, 0.0f).next();
        bufferBuilder.vertex(box.x2, box.y1, box.z1).normal(0.0f, -1.0f, 0.0f).next();
        bufferBuilder.vertex(box.x2, box.y1, box.z2).normal(0.0f, -1.0f, 0.0f).next();
        bufferBuilder.vertex(box.x1, box.y1, box.z2).normal(0.0f, -1.0f, 0.0f).next();
        bufferBuilder.vertex(box.x1, box.y2, box.z2).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder.vertex(box.x2, box.y2, box.z2).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder.vertex(box.x2, box.y2, box.z1).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder.vertex(box.x1, box.y2, box.z1).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder.vertex(box.x1, box.y1, box.z2).normal(-1.0f, 0.0f, 0.0f).next();
        bufferBuilder.vertex(box.x1, box.y2, box.z2).normal(-1.0f, 0.0f, 0.0f).next();
        bufferBuilder.vertex(box.x1, box.y2, box.z1).normal(-1.0f, 0.0f, 0.0f).next();
        bufferBuilder.vertex(box.x1, box.y1, box.z1).normal(-1.0f, 0.0f, 0.0f).next();
        bufferBuilder.vertex(box.x2, box.y1, box.z1).normal(1.0f, 0.0f, 0.0f).next();
        bufferBuilder.vertex(box.x2, box.y2, box.z1).normal(1.0f, 0.0f, 0.0f).next();
        bufferBuilder.vertex(box.x2, box.y2, box.z2).normal(1.0f, 0.0f, 0.0f).next();
        bufferBuilder.vertex(box.x2, box.y1, box.z2).normal(1.0f, 0.0f, 0.0f).next();
        tessellator.draw();
        bufferBuilder.setOffset(0.0, 0.0, 0.0);
        GlStateManager.enableTexture();
    }

    public void postRender(Entity entity, double x, double y, double z, float yaw, float tickDelta) {
        double d;
        float f;
        if (this.renderManager.gameOptions == null) {
            return;
        }
        if (this.renderManager.gameOptions.entityShadows && this.field_4673 > 0.0f && !entity.isInvisible() && this.renderManager.shouldRenderShadows() && (f = (float)((1.0 - (d = this.renderManager.getSquaredDistanceToCamera(entity.x, entity.y, entity.z)) / 256.0) * (double)this.field_4672)) > 0.0f) {
            this.renderShadow(entity, x, y, z, f, tickDelta);
        }
        if (entity.doesRenderOnFire() && !entity.isSpectator()) {
            this.renderEntityOnFire(entity, x, y, z, tickDelta);
        }
    }

    public TextRenderer getFontRenderer() {
        return this.renderManager.getTextRenderer();
    }

    protected void renderLabel(T entity, String text, double x, double y, double z, int maxDistance) {
        double d = ((Entity)entity).squaredDistanceTo(this.renderManager.camera.getPos());
        if (d > (double)(maxDistance * maxDistance)) {
            return;
        }
        boolean bl = ((Entity)entity).isInSneakingPose();
        float f = this.renderManager.cameraYaw;
        float g = this.renderManager.cameraPitch;
        float h = ((Entity)entity).getHeight() + 0.5f - (bl ? 0.25f : 0.0f);
        int i = "deadmau5".equals(text) ? -10 : 0;
        GameRenderer.renderFloatingText(this.getFontRenderer(), text, (float)x, (float)y + h, (float)z, i, f, g, bl);
    }

    public EntityRenderDispatcher getRenderManager() {
        return this.renderManager;
    }

    public boolean hasSecondPass() {
        return false;
    }

    public void renderSecondPass(T boat, double x, double y, double z, float yaw, float tickDelta) {
    }

    public void applyLightmapCoordinates(T entity) {
        int i = ((Entity)entity).getLightmapCoordinates();
        int j = i % 65536;
        int k = i / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, j, k);
    }
}

