/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;

@Environment(value=EnvType.CLIENT)
public class FluidRenderer {
    private final Sprite[] lavaSprites = new Sprite[2];
    private final Sprite[] waterSprites = new Sprite[2];
    private Sprite waterOverlaySprite;

    protected void onResourceReload() {
        SpriteAtlasTexture spriteAtlasTexture = MinecraftClient.getInstance().getSpriteAtlas();
        this.lavaSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.LAVA.getDefaultState()).getSprite();
        this.lavaSprites[1] = spriteAtlasTexture.getSprite(ModelLoader.LAVA_FLOW);
        this.waterSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.WATER.getDefaultState()).getSprite();
        this.waterSprites[1] = spriteAtlasTexture.getSprite(ModelLoader.WATER_FLOW);
        this.waterOverlaySprite = spriteAtlasTexture.getSprite(ModelLoader.WATER_OVERLAY);
    }

    private static boolean isSameFluid(BlockView world, BlockPos pos, Direction side, FluidState state) {
        BlockPos blockPos = pos.offset(side);
        FluidState fluidState = world.getFluidState(blockPos);
        return fluidState.getFluid().matchesType(state.getFluid());
    }

    private static boolean method_3344(BlockView blockView, BlockPos blockPos, Direction direction, float f) {
        BlockPos blockPos2 = blockPos.offset(direction);
        BlockState blockState = blockView.getBlockState(blockPos2);
        if (blockState.isOpaque()) {
            VoxelShape voxelShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, f, 1.0);
            VoxelShape voxelShape2 = blockState.getCullingShape(blockView, blockPos2);
            return VoxelShapes.method_1083(voxelShape, voxelShape2, direction);
        }
        return false;
    }

    public boolean tesselate(BlockRenderView world, BlockPos pos, BufferBuilder bufferBuilder, FluidState state) {
        float al;
        float ak;
        float aj;
        float af;
        float aa;
        float z;
        float x;
        float v;
        float t;
        boolean bl7;
        boolean bl = state.matches(FluidTags.LAVA);
        Sprite[] sprites = bl ? this.lavaSprites : this.waterSprites;
        int i = bl ? 0xFFFFFF : BiomeColors.getWaterColor(world, pos);
        float f = (float)(i >> 16 & 0xFF) / 255.0f;
        float g = (float)(i >> 8 & 0xFF) / 255.0f;
        float h = (float)(i & 0xFF) / 255.0f;
        boolean bl2 = !FluidRenderer.isSameFluid(world, pos, Direction.UP, state);
        boolean bl3 = !FluidRenderer.isSameFluid(world, pos, Direction.DOWN, state) && !FluidRenderer.method_3344(world, pos, Direction.DOWN, 0.8888889f);
        boolean bl4 = !FluidRenderer.isSameFluid(world, pos, Direction.NORTH, state);
        boolean bl5 = !FluidRenderer.isSameFluid(world, pos, Direction.SOUTH, state);
        boolean bl6 = !FluidRenderer.isSameFluid(world, pos, Direction.WEST, state);
        boolean bl8 = bl7 = !FluidRenderer.isSameFluid(world, pos, Direction.EAST, state);
        if (!(bl2 || bl3 || bl7 || bl6 || bl4 || bl5)) {
            return false;
        }
        boolean bl82 = false;
        float j = 0.5f;
        float k = 1.0f;
        float l = 0.8f;
        float m = 0.6f;
        float n = this.getNorthWestCornerFluidHeight(world, pos, state.getFluid());
        float o = this.getNorthWestCornerFluidHeight(world, pos.south(), state.getFluid());
        float p = this.getNorthWestCornerFluidHeight(world, pos.east().south(), state.getFluid());
        float q = this.getNorthWestCornerFluidHeight(world, pos.east(), state.getFluid());
        double d = pos.getX();
        double e = pos.getY();
        double r = pos.getZ();
        float s = 0.001f;
        if (bl2 && !FluidRenderer.method_3344(world, pos, Direction.UP, Math.min(Math.min(n, o), Math.min(p, q)))) {
            float ae;
            float ad;
            float ac;
            float ab;
            float y;
            float w;
            float u;
            Sprite sprite;
            bl82 = true;
            n -= 0.001f;
            o -= 0.001f;
            p -= 0.001f;
            q -= 0.001f;
            Vec3d vec3d = state.getVelocity(world, pos);
            if (vec3d.x == 0.0 && vec3d.z == 0.0) {
                sprite = sprites[0];
                t = sprite.getFrameU(0.0);
                u = sprite.getFrameV(0.0);
                v = t;
                w = sprite.getFrameV(16.0);
                x = sprite.getFrameU(16.0);
                y = w;
                z = x;
                aa = u;
            } else {
                sprite = sprites[1];
                ab = (float)MathHelper.atan2(vec3d.z, vec3d.x) - 1.5707964f;
                ac = MathHelper.sin(ab) * 0.25f;
                ad = MathHelper.cos(ab) * 0.25f;
                ae = 8.0f;
                t = sprite.getFrameU(8.0f + (-ad - ac) * 16.0f);
                u = sprite.getFrameV(8.0f + (-ad + ac) * 16.0f);
                v = sprite.getFrameU(8.0f + (-ad + ac) * 16.0f);
                w = sprite.getFrameV(8.0f + (ad + ac) * 16.0f);
                x = sprite.getFrameU(8.0f + (ad + ac) * 16.0f);
                y = sprite.getFrameV(8.0f + (ad - ac) * 16.0f);
                z = sprite.getFrameU(8.0f + (ad - ac) * 16.0f);
                aa = sprite.getFrameV(8.0f + (-ad - ac) * 16.0f);
            }
            af = (t + v + x + z) / 4.0f;
            ab = (u + w + y + aa) / 4.0f;
            ac = (float)sprites[0].getWidth() / (sprites[0].getMaxU() - sprites[0].getMinU());
            ad = (float)sprites[0].getHeight() / (sprites[0].getMaxV() - sprites[0].getMinV());
            ae = 4.0f / Math.max(ad, ac);
            t = MathHelper.lerp(ae, t, af);
            v = MathHelper.lerp(ae, v, af);
            x = MathHelper.lerp(ae, x, af);
            z = MathHelper.lerp(ae, z, af);
            u = MathHelper.lerp(ae, u, ab);
            w = MathHelper.lerp(ae, w, ab);
            y = MathHelper.lerp(ae, y, ab);
            aa = MathHelper.lerp(ae, aa, ab);
            int ag = this.method_3343(world, pos);
            int ah = ag >> 16 & 0xFFFF;
            int ai = ag & 0xFFFF;
            aj = 1.0f * f;
            ak = 1.0f * g;
            al = 1.0f * h;
            bufferBuilder.vertex(d + 0.0, e + (double)n, r + 0.0).color(aj, ak, al, 1.0f).texture(t, u).texture(ah, ai).next();
            bufferBuilder.vertex(d + 0.0, e + (double)o, r + 1.0).color(aj, ak, al, 1.0f).texture(v, w).texture(ah, ai).next();
            bufferBuilder.vertex(d + 1.0, e + (double)p, r + 1.0).color(aj, ak, al, 1.0f).texture(x, y).texture(ah, ai).next();
            bufferBuilder.vertex(d + 1.0, e + (double)q, r + 0.0).color(aj, ak, al, 1.0f).texture(z, aa).texture(ah, ai).next();
            if (state.method_15756(world, pos.up())) {
                bufferBuilder.vertex(d + 0.0, e + (double)n, r + 0.0).color(aj, ak, al, 1.0f).texture(t, u).texture(ah, ai).next();
                bufferBuilder.vertex(d + 1.0, e + (double)q, r + 0.0).color(aj, ak, al, 1.0f).texture(z, aa).texture(ah, ai).next();
                bufferBuilder.vertex(d + 1.0, e + (double)p, r + 1.0).color(aj, ak, al, 1.0f).texture(x, y).texture(ah, ai).next();
                bufferBuilder.vertex(d + 0.0, e + (double)o, r + 1.0).color(aj, ak, al, 1.0f).texture(v, w).texture(ah, ai).next();
            }
        }
        if (bl3) {
            t = sprites[0].getMinU();
            v = sprites[0].getMaxU();
            x = sprites[0].getMinV();
            z = sprites[0].getMaxV();
            int am = this.method_3343(world, pos.down());
            int an = am >> 16 & 0xFFFF;
            int ao = am & 0xFFFF;
            aa = 0.5f * f;
            float ap = 0.5f * g;
            af = 0.5f * h;
            bufferBuilder.vertex(d, e, r + 1.0).color(aa, ap, af, 1.0f).texture(t, z).texture(an, ao).next();
            bufferBuilder.vertex(d, e, r).color(aa, ap, af, 1.0f).texture(t, x).texture(an, ao).next();
            bufferBuilder.vertex(d + 1.0, e, r).color(aa, ap, af, 1.0f).texture(v, x).texture(an, ao).next();
            bufferBuilder.vertex(d + 1.0, e, r + 1.0).color(aa, ap, af, 1.0f).texture(v, z).texture(an, ao).next();
            bl82 = true;
        }
        for (int aq = 0; aq < 4; ++aq) {
            Block block;
            boolean bl9;
            Direction direction;
            double au;
            double at;
            double as;
            double ar;
            if (aq == 0) {
                v = n;
                x = q;
                ar = d;
                as = d + 1.0;
                at = r + (double)0.001f;
                au = r + (double)0.001f;
                direction = Direction.NORTH;
                bl9 = bl4;
            } else if (aq == 1) {
                v = p;
                x = o;
                ar = d + 1.0;
                as = d;
                at = r + 1.0 - (double)0.001f;
                au = r + 1.0 - (double)0.001f;
                direction = Direction.SOUTH;
                bl9 = bl5;
            } else if (aq == 2) {
                v = o;
                x = n;
                ar = d + (double)0.001f;
                as = d + (double)0.001f;
                at = r + 1.0;
                au = r;
                direction = Direction.WEST;
                bl9 = bl6;
            } else {
                v = q;
                x = p;
                ar = d + 1.0 - (double)0.001f;
                as = d + 1.0 - (double)0.001f;
                at = r;
                au = r + 1.0;
                direction = Direction.EAST;
                bl9 = bl7;
            }
            if (!bl9 || FluidRenderer.method_3344(world, pos, direction, Math.max(v, x))) continue;
            bl82 = true;
            BlockPos blockPos = pos.offset(direction);
            Sprite sprite2 = sprites[1];
            if (!bl && ((block = world.getBlockState(blockPos).getBlock()) == Blocks.GLASS || block instanceof StainedGlassBlock)) {
                sprite2 = this.waterOverlaySprite;
            }
            float av = sprite2.getFrameU(0.0);
            float aw = sprite2.getFrameU(8.0);
            aj = sprite2.getFrameV((1.0f - v) * 16.0f * 0.5f);
            ak = sprite2.getFrameV((1.0f - x) * 16.0f * 0.5f);
            al = sprite2.getFrameV(8.0);
            int ax = this.method_3343(world, blockPos);
            int ay = ax >> 16 & 0xFFFF;
            int az = ax & 0xFFFF;
            float ba = aq < 2 ? 0.8f : 0.6f;
            float bb = 1.0f * ba * f;
            float bc = 1.0f * ba * g;
            float bd = 1.0f * ba * h;
            bufferBuilder.vertex(ar, e + (double)v, at).color(bb, bc, bd, 1.0f).texture(av, aj).texture(ay, az).next();
            bufferBuilder.vertex(as, e + (double)x, au).color(bb, bc, bd, 1.0f).texture(aw, ak).texture(ay, az).next();
            bufferBuilder.vertex(as, e + 0.0, au).color(bb, bc, bd, 1.0f).texture(aw, al).texture(ay, az).next();
            bufferBuilder.vertex(ar, e + 0.0, at).color(bb, bc, bd, 1.0f).texture(av, al).texture(ay, az).next();
            if (sprite2 == this.waterOverlaySprite) continue;
            bufferBuilder.vertex(ar, e + 0.0, at).color(bb, bc, bd, 1.0f).texture(av, al).texture(ay, az).next();
            bufferBuilder.vertex(as, e + 0.0, au).color(bb, bc, bd, 1.0f).texture(aw, al).texture(ay, az).next();
            bufferBuilder.vertex(as, e + (double)x, au).color(bb, bc, bd, 1.0f).texture(aw, ak).texture(ay, az).next();
            bufferBuilder.vertex(ar, e + (double)v, at).color(bb, bc, bd, 1.0f).texture(av, aj).texture(ay, az).next();
        }
        return bl82;
    }

    private int method_3343(BlockRenderView blockRenderView, BlockPos blockPos) {
        int i = blockRenderView.getLightmapIndex(blockPos, 0);
        int j = blockRenderView.getLightmapIndex(blockPos.up(), 0);
        int k = i & 0xFF;
        int l = j & 0xFF;
        int m = i >> 16 & 0xFF;
        int n = j >> 16 & 0xFF;
        return (k > l ? k : l) | (m > n ? m : n) << 16;
    }

    private float getNorthWestCornerFluidHeight(BlockView world, BlockPos pos, Fluid fluid) {
        int i = 0;
        float f = 0.0f;
        for (int j = 0; j < 4; ++j) {
            BlockPos blockPos = pos.add(-(j & 1), 0, -(j >> 1 & 1));
            if (world.getFluidState(blockPos.up()).getFluid().matchesType(fluid)) {
                return 1.0f;
            }
            FluidState fluidState = world.getFluidState(blockPos);
            if (fluidState.getFluid().matchesType(fluid)) {
                float g = fluidState.getHeight(world, blockPos);
                if (g >= 0.8f) {
                    f += g * 10.0f;
                    i += 10;
                    continue;
                }
                f += g;
                ++i;
                continue;
            }
            if (world.getBlockState(blockPos).getMaterial().isSolid()) continue;
            ++i;
        }
        return f / (float)i;
    }
}

