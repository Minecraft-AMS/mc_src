/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.block;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockModelRenderer {
    private final BlockColors colorMap;
    private static final ThreadLocal<BrightnessCache> brightnessCache = ThreadLocal.withInitial(() -> new BrightnessCache());

    public BlockModelRenderer(BlockColors blockColors) {
        this.colorMap = blockColors;
    }

    public boolean tesselate(BlockRenderView view, BakedModel model, BlockState state, BlockPos pos, BufferBuilder buffer, boolean testSides, Random random, long l) {
        boolean bl = MinecraftClient.isAmbientOcclusionEnabled() && state.getLuminance() == 0 && model.useAmbientOcclusion();
        try {
            if (bl) {
                return this.tesselateSmooth(view, model, state, pos, buffer, testSides, random, l);
            }
            return this.tesselateFlat(view, model, state, pos, buffer, testSides, random, l);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Tesselating block model");
            CrashReportSection crashReportSection = crashReport.addElement("Block model being tesselated");
            CrashReportSection.addBlockInfo(crashReportSection, pos, state);
            crashReportSection.add("Using AO", bl);
            throw new CrashException(crashReport);
        }
    }

    public boolean tesselateSmooth(BlockRenderView view, BakedModel model, BlockState state, BlockPos pos, BufferBuilder buffer, boolean testSides, Random random, long l) {
        boolean bl = false;
        float[] fs = new float[Direction.values().length * 2];
        BitSet bitSet = new BitSet(3);
        AmbientOcclusionCalculator ambientOcclusionCalculator = new AmbientOcclusionCalculator();
        for (Direction direction : Direction.values()) {
            random.setSeed(l);
            List<BakedQuad> list = model.getQuads(state, direction, random);
            if (list.isEmpty() || testSides && !Block.shouldDrawSide(state, view, pos, direction)) continue;
            this.tesselateQuadsSmooth(view, state, pos, buffer, list, fs, bitSet, ambientOcclusionCalculator);
            bl = true;
        }
        random.setSeed(l);
        List<BakedQuad> list2 = model.getQuads(state, null, random);
        if (!list2.isEmpty()) {
            this.tesselateQuadsSmooth(view, state, pos, buffer, list2, fs, bitSet, ambientOcclusionCalculator);
            bl = true;
        }
        return bl;
    }

    public boolean tesselateFlat(BlockRenderView view, BakedModel model, BlockState state, BlockPos pos, BufferBuilder buffer, boolean testSides, Random random, long seed) {
        boolean bl = false;
        BitSet bitSet = new BitSet(3);
        for (Direction direction : Direction.values()) {
            random.setSeed(seed);
            List<BakedQuad> list = model.getQuads(state, direction, random);
            if (list.isEmpty() || testSides && !Block.shouldDrawSide(state, view, pos, direction)) continue;
            int i = state.getBlockBrightness(view, pos.offset(direction));
            this.tesselateQuadsFlat(view, state, pos, i, false, buffer, list, bitSet);
            bl = true;
        }
        random.setSeed(seed);
        List<BakedQuad> list2 = model.getQuads(state, null, random);
        if (!list2.isEmpty()) {
            this.tesselateQuadsFlat(view, state, pos, -1, true, buffer, list2, bitSet);
            bl = true;
        }
        return bl;
    }

    private void tesselateQuadsSmooth(BlockRenderView view, BlockState state, BlockPos pos, BufferBuilder buffer, List<BakedQuad> quads, float[] faceShape, BitSet shapeState, AmbientOcclusionCalculator ambientOcclusionCalculator) {
        Vec3d vec3d = state.getOffsetPos(view, pos);
        double d = (double)pos.getX() + vec3d.x;
        double e = (double)pos.getY() + vec3d.y;
        double f = (double)pos.getZ() + vec3d.z;
        int j = quads.size();
        for (int i = 0; i < j; ++i) {
            BakedQuad bakedQuad = quads.get(i);
            this.getQuadDimensions(view, state, pos, bakedQuad.getVertexData(), bakedQuad.getFace(), faceShape, shapeState);
            ambientOcclusionCalculator.apply(view, state, pos, bakedQuad.getFace(), faceShape, shapeState);
            buffer.putVertexData(bakedQuad.getVertexData());
            buffer.brightness(ambientOcclusionCalculator.brightness[0], ambientOcclusionCalculator.brightness[1], ambientOcclusionCalculator.brightness[2], ambientOcclusionCalculator.brightness[3]);
            if (bakedQuad.hasColor()) {
                int k = this.colorMap.getColor(state, view, pos, bakedQuad.getColorIndex());
                float g = (float)(k >> 16 & 0xFF) / 255.0f;
                float h = (float)(k >> 8 & 0xFF) / 255.0f;
                float l = (float)(k & 0xFF) / 255.0f;
                buffer.multiplyColor(ambientOcclusionCalculator.colorMultiplier[0] * g, ambientOcclusionCalculator.colorMultiplier[0] * h, ambientOcclusionCalculator.colorMultiplier[0] * l, 4);
                buffer.multiplyColor(ambientOcclusionCalculator.colorMultiplier[1] * g, ambientOcclusionCalculator.colorMultiplier[1] * h, ambientOcclusionCalculator.colorMultiplier[1] * l, 3);
                buffer.multiplyColor(ambientOcclusionCalculator.colorMultiplier[2] * g, ambientOcclusionCalculator.colorMultiplier[2] * h, ambientOcclusionCalculator.colorMultiplier[2] * l, 2);
                buffer.multiplyColor(ambientOcclusionCalculator.colorMultiplier[3] * g, ambientOcclusionCalculator.colorMultiplier[3] * h, ambientOcclusionCalculator.colorMultiplier[3] * l, 1);
            } else {
                buffer.multiplyColor(ambientOcclusionCalculator.colorMultiplier[0], ambientOcclusionCalculator.colorMultiplier[0], ambientOcclusionCalculator.colorMultiplier[0], 4);
                buffer.multiplyColor(ambientOcclusionCalculator.colorMultiplier[1], ambientOcclusionCalculator.colorMultiplier[1], ambientOcclusionCalculator.colorMultiplier[1], 3);
                buffer.multiplyColor(ambientOcclusionCalculator.colorMultiplier[2], ambientOcclusionCalculator.colorMultiplier[2], ambientOcclusionCalculator.colorMultiplier[2], 2);
                buffer.multiplyColor(ambientOcclusionCalculator.colorMultiplier[3], ambientOcclusionCalculator.colorMultiplier[3], ambientOcclusionCalculator.colorMultiplier[3], 1);
            }
            buffer.postPosition(d, e, f);
        }
    }

    private void getQuadDimensions(BlockRenderView world, BlockState state, BlockPos pos, int[] vertexData, Direction face, @Nullable float[] box, BitSet flags) {
        float m;
        int l;
        float f = 32.0f;
        float g = 32.0f;
        float h = 32.0f;
        float i = -32.0f;
        float j = -32.0f;
        float k = -32.0f;
        for (l = 0; l < 4; ++l) {
            m = Float.intBitsToFloat(vertexData[l * 7]);
            float n = Float.intBitsToFloat(vertexData[l * 7 + 1]);
            float o = Float.intBitsToFloat(vertexData[l * 7 + 2]);
            f = Math.min(f, m);
            g = Math.min(g, n);
            h = Math.min(h, o);
            i = Math.max(i, m);
            j = Math.max(j, n);
            k = Math.max(k, o);
        }
        if (box != null) {
            box[Direction.WEST.getId()] = f;
            box[Direction.EAST.getId()] = i;
            box[Direction.DOWN.getId()] = g;
            box[Direction.UP.getId()] = j;
            box[Direction.NORTH.getId()] = h;
            box[Direction.SOUTH.getId()] = k;
            l = Direction.values().length;
            box[Direction.WEST.getId() + l] = 1.0f - f;
            box[Direction.EAST.getId() + l] = 1.0f - i;
            box[Direction.DOWN.getId() + l] = 1.0f - g;
            box[Direction.UP.getId() + l] = 1.0f - j;
            box[Direction.NORTH.getId() + l] = 1.0f - h;
            box[Direction.SOUTH.getId() + l] = 1.0f - k;
        }
        float p = 1.0E-4f;
        m = 0.9999f;
        switch (face) {
            case DOWN: {
                flags.set(1, f >= 1.0E-4f || h >= 1.0E-4f || i <= 0.9999f || k <= 0.9999f);
                flags.set(0, g == j && (g < 1.0E-4f || state.method_21743(world, pos)));
                break;
            }
            case UP: {
                flags.set(1, f >= 1.0E-4f || h >= 1.0E-4f || i <= 0.9999f || k <= 0.9999f);
                flags.set(0, g == j && (j > 0.9999f || state.method_21743(world, pos)));
                break;
            }
            case NORTH: {
                flags.set(1, f >= 1.0E-4f || g >= 1.0E-4f || i <= 0.9999f || j <= 0.9999f);
                flags.set(0, h == k && (h < 1.0E-4f || state.method_21743(world, pos)));
                break;
            }
            case SOUTH: {
                flags.set(1, f >= 1.0E-4f || g >= 1.0E-4f || i <= 0.9999f || j <= 0.9999f);
                flags.set(0, h == k && (k > 0.9999f || state.method_21743(world, pos)));
                break;
            }
            case WEST: {
                flags.set(1, g >= 1.0E-4f || h >= 1.0E-4f || j <= 0.9999f || k <= 0.9999f);
                flags.set(0, f == i && (f < 1.0E-4f || state.method_21743(world, pos)));
                break;
            }
            case EAST: {
                flags.set(1, g >= 1.0E-4f || h >= 1.0E-4f || j <= 0.9999f || k <= 0.9999f);
                flags.set(0, f == i && (i > 0.9999f || state.method_21743(world, pos)));
            }
        }
    }

    private void tesselateQuadsFlat(BlockRenderView view, BlockState state, BlockPos pos, int brightness, boolean checkBrightness, BufferBuilder buffer, List<BakedQuad> quads, BitSet bitSet) {
        Vec3d vec3d = state.getOffsetPos(view, pos);
        double d = (double)pos.getX() + vec3d.x;
        double e = (double)pos.getY() + vec3d.y;
        double f = (double)pos.getZ() + vec3d.z;
        int j = quads.size();
        for (int i = 0; i < j; ++i) {
            BakedQuad bakedQuad = quads.get(i);
            if (checkBrightness) {
                this.getQuadDimensions(view, state, pos, bakedQuad.getVertexData(), bakedQuad.getFace(), null, bitSet);
                BlockPos blockPos = bitSet.get(0) ? pos.offset(bakedQuad.getFace()) : pos;
                brightness = state.getBlockBrightness(view, blockPos);
            }
            buffer.putVertexData(bakedQuad.getVertexData());
            buffer.brightness(brightness, brightness, brightness, brightness);
            if (bakedQuad.hasColor()) {
                int k = this.colorMap.getColor(state, view, pos, bakedQuad.getColorIndex());
                float g = (float)(k >> 16 & 0xFF) / 255.0f;
                float h = (float)(k >> 8 & 0xFF) / 255.0f;
                float l = (float)(k & 0xFF) / 255.0f;
                buffer.multiplyColor(g, h, l, 4);
                buffer.multiplyColor(g, h, l, 3);
                buffer.multiplyColor(g, h, l, 2);
                buffer.multiplyColor(g, h, l, 1);
            }
            buffer.postPosition(d, e, f);
        }
    }

    public void render(BakedModel model, float colorMultiplier, float red, float green, float f) {
        this.render(null, model, colorMultiplier, red, green, f);
    }

    public void render(@Nullable BlockState state, BakedModel model, float colorMultiplier, float red, float green, float f) {
        Random random = new Random();
        long l = 42L;
        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            this.renderQuad(colorMultiplier, red, green, f, model.getQuads(state, direction, random));
        }
        random.setSeed(42L);
        this.renderQuad(colorMultiplier, red, green, f, model.getQuads(state, null, random));
    }

    public void render(BakedModel model, BlockState state, float colorMultiplier, boolean bl) {
        GlStateManager.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
        int i = this.colorMap.getColor(state, null, null, 0);
        float f = (float)(i >> 16 & 0xFF) / 255.0f;
        float g = (float)(i >> 8 & 0xFF) / 255.0f;
        float h = (float)(i & 0xFF) / 255.0f;
        if (!bl) {
            GlStateManager.color4f(colorMultiplier, colorMultiplier, colorMultiplier, 1.0f);
        }
        this.render(state, model, colorMultiplier, f, g, h);
    }

    private void renderQuad(float colorMultiplier, float red, float green, float blue, List<BakedQuad> list) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        int j = list.size();
        for (int i = 0; i < j; ++i) {
            BakedQuad bakedQuad = list.get(i);
            bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
            bufferBuilder.putVertexData(bakedQuad.getVertexData());
            if (bakedQuad.hasColor()) {
                bufferBuilder.setQuadColor(red * colorMultiplier, green * colorMultiplier, blue * colorMultiplier);
            } else {
                bufferBuilder.setQuadColor(colorMultiplier, colorMultiplier, colorMultiplier);
            }
            Vec3i vec3i = bakedQuad.getFace().getVector();
            bufferBuilder.postNormal(vec3i.getX(), vec3i.getY(), vec3i.getZ());
            tessellator.draw();
        }
    }

    public static void enableBrightnessCache() {
        brightnessCache.get().enable();
    }

    public static void disableBrightnessCache() {
        brightnessCache.get().disable();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum NeighborData {
        DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5f, true, new NeighborOrientation[]{NeighborOrientation.FLIP_WEST, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.WEST, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_WEST, NeighborOrientation.NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.WEST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_EAST, NeighborOrientation.NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.EAST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_EAST, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.EAST, NeighborOrientation.SOUTH}),
        UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0f, true, new NeighborOrientation[]{NeighborOrientation.EAST, NeighborOrientation.SOUTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.EAST, NeighborOrientation.NORTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.WEST, NeighborOrientation.NORTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.WEST, NeighborOrientation.SOUTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.SOUTH}),
        NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8f, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.UP, NeighborOrientation.WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_WEST}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.UP, NeighborOrientation.EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_EAST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.DOWN, NeighborOrientation.EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_EAST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.DOWN, NeighborOrientation.WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_WEST}),
        SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8f, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.WEST, NeighborOrientation.UP, NeighborOrientation.WEST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.WEST, NeighborOrientation.DOWN, NeighborOrientation.WEST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.EAST, NeighborOrientation.DOWN, NeighborOrientation.EAST}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.EAST, NeighborOrientation.UP, NeighborOrientation.EAST}),
        WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6f, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.SOUTH, NeighborOrientation.UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.NORTH, NeighborOrientation.UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.NORTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.SOUTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.SOUTH}),
        EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6f, true, new NeighborOrientation[]{NeighborOrientation.FLIP_DOWN, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.DOWN, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_DOWN, NeighborOrientation.NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.DOWN, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_UP, NeighborOrientation.NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.UP, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_UP, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.UP, NeighborOrientation.SOUTH});

        private final Direction[] faces;
        private final boolean nonCubicWeight;
        private final NeighborOrientation[] field_4192;
        private final NeighborOrientation[] field_4185;
        private final NeighborOrientation[] field_4180;
        private final NeighborOrientation[] field_4188;
        private static final NeighborData[] field_4190;

        private NeighborData(Direction[] directions, float f, boolean bl, NeighborOrientation[] neighborOrientations, NeighborOrientation[] neighborOrientations2, NeighborOrientation[] neighborOrientations3, NeighborOrientation[] neighborOrientations4) {
            this.faces = directions;
            this.nonCubicWeight = bl;
            this.field_4192 = neighborOrientations;
            this.field_4185 = neighborOrientations2;
            this.field_4180 = neighborOrientations3;
            this.field_4188 = neighborOrientations4;
        }

        public static NeighborData getData(Direction direction) {
            return field_4190[direction.getId()];
        }

        static {
            field_4190 = Util.make(new NeighborData[6], neighborDatas -> {
                neighborDatas[Direction.DOWN.getId()] = DOWN;
                neighborDatas[Direction.UP.getId()] = UP;
                neighborDatas[Direction.NORTH.getId()] = NORTH;
                neighborDatas[Direction.SOUTH.getId()] = SOUTH;
                neighborDatas[Direction.WEST.getId()] = WEST;
                neighborDatas[Direction.EAST.getId()] = EAST;
            });
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum NeighborOrientation {
        DOWN(Direction.DOWN, false),
        UP(Direction.UP, false),
        NORTH(Direction.NORTH, false),
        SOUTH(Direction.SOUTH, false),
        WEST(Direction.WEST, false),
        EAST(Direction.EAST, false),
        FLIP_DOWN(Direction.DOWN, true),
        FLIP_UP(Direction.UP, true),
        FLIP_NORTH(Direction.NORTH, true),
        FLIP_SOUTH(Direction.SOUTH, true),
        FLIP_WEST(Direction.WEST, true),
        FLIP_EAST(Direction.EAST, true);

        private final int shape;

        private NeighborOrientation(Direction direction, boolean bl) {
            this.shape = direction.getId() + (bl ? Direction.values().length : 0);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class AmbientOcclusionCalculator {
        private final float[] colorMultiplier = new float[4];
        private final int[] brightness = new int[4];

        public void apply(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Direction direction, float[] fs, BitSet bitSet) {
            int u;
            float t;
            int s;
            float r;
            int q;
            float p;
            int o;
            float n;
            BlockState blockState6;
            boolean bl4;
            BlockPos blockPos2 = bitSet.get(0) ? blockPos.offset(direction) : blockPos;
            NeighborData neighborData = NeighborData.getData(direction);
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            BrightnessCache brightnessCache = (BrightnessCache)brightnessCache.get();
            mutable.set(blockPos2).setOffset(neighborData.faces[0]);
            BlockState blockState2 = blockRenderView.getBlockState(mutable);
            int i = brightnessCache.getInt(blockState2, blockRenderView, mutable);
            float f = brightnessCache.getFloat(blockState2, blockRenderView, mutable);
            mutable.set(blockPos2).setOffset(neighborData.faces[1]);
            BlockState blockState3 = blockRenderView.getBlockState(mutable);
            int j = brightnessCache.getInt(blockState3, blockRenderView, mutable);
            float g = brightnessCache.getFloat(blockState3, blockRenderView, mutable);
            mutable.set(blockPos2).setOffset(neighborData.faces[2]);
            BlockState blockState4 = blockRenderView.getBlockState(mutable);
            int k = brightnessCache.getInt(blockState4, blockRenderView, mutable);
            float h = brightnessCache.getFloat(blockState4, blockRenderView, mutable);
            mutable.set(blockPos2).setOffset(neighborData.faces[3]);
            BlockState blockState5 = blockRenderView.getBlockState(mutable);
            int l = brightnessCache.getInt(blockState5, blockRenderView, mutable);
            float m = brightnessCache.getFloat(blockState5, blockRenderView, mutable);
            mutable.set(blockPos2).setOffset(neighborData.faces[0]).setOffset(direction);
            boolean bl = blockRenderView.getBlockState(mutable).getOpacity(blockRenderView, mutable) == 0;
            mutable.set(blockPos2).setOffset(neighborData.faces[1]).setOffset(direction);
            boolean bl2 = blockRenderView.getBlockState(mutable).getOpacity(blockRenderView, mutable) == 0;
            mutable.set(blockPos2).setOffset(neighborData.faces[2]).setOffset(direction);
            boolean bl3 = blockRenderView.getBlockState(mutable).getOpacity(blockRenderView, mutable) == 0;
            mutable.set(blockPos2).setOffset(neighborData.faces[3]).setOffset(direction);
            boolean bl5 = bl4 = blockRenderView.getBlockState(mutable).getOpacity(blockRenderView, mutable) == 0;
            if (bl3 || bl) {
                mutable.set(blockPos2).setOffset(neighborData.faces[0]).setOffset(neighborData.faces[2]);
                blockState6 = blockRenderView.getBlockState(mutable);
                n = brightnessCache.getFloat(blockState6, blockRenderView, mutable);
                o = brightnessCache.getInt(blockState6, blockRenderView, mutable);
            } else {
                n = f;
                o = i;
            }
            if (bl4 || bl) {
                mutable.set(blockPos2).setOffset(neighborData.faces[0]).setOffset(neighborData.faces[3]);
                blockState6 = blockRenderView.getBlockState(mutable);
                p = brightnessCache.getFloat(blockState6, blockRenderView, mutable);
                q = brightnessCache.getInt(blockState6, blockRenderView, mutable);
            } else {
                p = f;
                q = i;
            }
            if (bl3 || bl2) {
                mutable.set(blockPos2).setOffset(neighborData.faces[1]).setOffset(neighborData.faces[2]);
                blockState6 = blockRenderView.getBlockState(mutable);
                r = brightnessCache.getFloat(blockState6, blockRenderView, mutable);
                s = brightnessCache.getInt(blockState6, blockRenderView, mutable);
            } else {
                r = f;
                s = i;
            }
            if (bl4 || bl2) {
                mutable.set(blockPos2).setOffset(neighborData.faces[1]).setOffset(neighborData.faces[3]);
                blockState6 = blockRenderView.getBlockState(mutable);
                t = brightnessCache.getFloat(blockState6, blockRenderView, mutable);
                u = brightnessCache.getInt(blockState6, blockRenderView, mutable);
            } else {
                t = f;
                u = i;
            }
            int v = brightnessCache.getInt(blockState, blockRenderView, blockPos);
            mutable.set(blockPos).setOffset(direction);
            BlockState blockState7 = blockRenderView.getBlockState(mutable);
            if (bitSet.get(0) || !blockState7.isFullOpaque(blockRenderView, mutable)) {
                v = brightnessCache.getInt(blockState7, blockRenderView, mutable);
            }
            float w = bitSet.get(0) ? brightnessCache.getFloat(blockRenderView.getBlockState(blockPos2), blockRenderView, blockPos2) : brightnessCache.getFloat(blockRenderView.getBlockState(blockPos), blockRenderView, blockPos);
            Translation translation = Translation.getTranslations(direction);
            if (!bitSet.get(1) || !neighborData.nonCubicWeight) {
                float x = (m + f + p + w) * 0.25f;
                float y = (h + f + n + w) * 0.25f;
                float z = (h + g + r + w) * 0.25f;
                float aa = (m + g + t + w) * 0.25f;
                this.brightness[((Translation)translation).firstCorner] = this.getAmbientOcclusionBrightness(l, i, q, v);
                this.brightness[((Translation)translation).secondCorner] = this.getAmbientOcclusionBrightness(k, i, o, v);
                this.brightness[((Translation)translation).thirdCorner] = this.getAmbientOcclusionBrightness(k, j, s, v);
                this.brightness[((Translation)translation).fourthCorner] = this.getAmbientOcclusionBrightness(l, j, u, v);
                this.colorMultiplier[((Translation)translation).firstCorner] = x;
                this.colorMultiplier[((Translation)translation).secondCorner] = y;
                this.colorMultiplier[((Translation)translation).thirdCorner] = z;
                this.colorMultiplier[((Translation)translation).fourthCorner] = aa;
            } else {
                float x = (m + f + p + w) * 0.25f;
                float y = (h + f + n + w) * 0.25f;
                float z = (h + g + r + w) * 0.25f;
                float aa = (m + g + t + w) * 0.25f;
                float ab = fs[neighborData.field_4192[0].shape] * fs[neighborData.field_4192[1].shape];
                float ac = fs[neighborData.field_4192[2].shape] * fs[neighborData.field_4192[3].shape];
                float ad = fs[neighborData.field_4192[4].shape] * fs[neighborData.field_4192[5].shape];
                float ae = fs[neighborData.field_4192[6].shape] * fs[neighborData.field_4192[7].shape];
                float af = fs[neighborData.field_4185[0].shape] * fs[neighborData.field_4185[1].shape];
                float ag = fs[neighborData.field_4185[2].shape] * fs[neighborData.field_4185[3].shape];
                float ah = fs[neighborData.field_4185[4].shape] * fs[neighborData.field_4185[5].shape];
                float ai = fs[neighborData.field_4185[6].shape] * fs[neighborData.field_4185[7].shape];
                float aj = fs[neighborData.field_4180[0].shape] * fs[neighborData.field_4180[1].shape];
                float ak = fs[neighborData.field_4180[2].shape] * fs[neighborData.field_4180[3].shape];
                float al = fs[neighborData.field_4180[4].shape] * fs[neighborData.field_4180[5].shape];
                float am = fs[neighborData.field_4180[6].shape] * fs[neighborData.field_4180[7].shape];
                float an = fs[neighborData.field_4188[0].shape] * fs[neighborData.field_4188[1].shape];
                float ao = fs[neighborData.field_4188[2].shape] * fs[neighborData.field_4188[3].shape];
                float ap = fs[neighborData.field_4188[4].shape] * fs[neighborData.field_4188[5].shape];
                float aq = fs[neighborData.field_4188[6].shape] * fs[neighborData.field_4188[7].shape];
                this.colorMultiplier[((Translation)translation).firstCorner] = x * ab + y * ac + z * ad + aa * ae;
                this.colorMultiplier[((Translation)translation).secondCorner] = x * af + y * ag + z * ah + aa * ai;
                this.colorMultiplier[((Translation)translation).thirdCorner] = x * aj + y * ak + z * al + aa * am;
                this.colorMultiplier[((Translation)translation).fourthCorner] = x * an + y * ao + z * ap + aa * aq;
                int ar = this.getAmbientOcclusionBrightness(l, i, q, v);
                int as = this.getAmbientOcclusionBrightness(k, i, o, v);
                int at = this.getAmbientOcclusionBrightness(k, j, s, v);
                int au = this.getAmbientOcclusionBrightness(l, j, u, v);
                this.brightness[((Translation)translation).firstCorner] = this.getBrightness(ar, as, at, au, ab, ac, ad, ae);
                this.brightness[((Translation)translation).secondCorner] = this.getBrightness(ar, as, at, au, af, ag, ah, ai);
                this.brightness[((Translation)translation).thirdCorner] = this.getBrightness(ar, as, at, au, aj, ak, al, am);
                this.brightness[((Translation)translation).fourthCorner] = this.getBrightness(ar, as, at, au, an, ao, ap, aq);
            }
        }

        private int getAmbientOcclusionBrightness(int i, int j, int k, int l) {
            if (i == 0) {
                i = l;
            }
            if (j == 0) {
                j = l;
            }
            if (k == 0) {
                k = l;
            }
            return i + j + k + l >> 2 & 0xFF00FF;
        }

        private int getBrightness(int i, int j, int k, int l, float f, float g, float h, float m) {
            int n = (int)((float)(i >> 16 & 0xFF) * f + (float)(j >> 16 & 0xFF) * g + (float)(k >> 16 & 0xFF) * h + (float)(l >> 16 & 0xFF) * m) & 0xFF;
            int o = (int)((float)(i & 0xFF) * f + (float)(j & 0xFF) * g + (float)(k & 0xFF) * h + (float)(l & 0xFF) * m) & 0xFF;
            return n << 16 | o;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class BrightnessCache {
        private boolean enabled;
        private final Long2IntLinkedOpenHashMap intCache = Util.make(() -> {
            Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap = new Long2IntLinkedOpenHashMap(100, 0.25f){

                protected void rehash(int i) {
                }
            };
            long2IntLinkedOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
            return long2IntLinkedOpenHashMap;
        });
        private final Long2FloatLinkedOpenHashMap floatCache = Util.make(() -> {
            Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(100, 0.25f){

                protected void rehash(int i) {
                }
            };
            long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
            return long2FloatLinkedOpenHashMap;
        });

        private BrightnessCache() {
        }

        public void enable() {
            this.enabled = true;
        }

        public void disable() {
            this.enabled = false;
            this.intCache.clear();
            this.floatCache.clear();
        }

        public int getInt(BlockState state, BlockRenderView blockView, BlockPos pos) {
            int i;
            long l = pos.asLong();
            if (this.enabled && (i = this.intCache.get(l)) != Integer.MAX_VALUE) {
                return i;
            }
            i = state.getBlockBrightness(blockView, pos);
            if (this.enabled) {
                if (this.intCache.size() == 100) {
                    this.intCache.removeFirstInt();
                }
                this.intCache.put(l, i);
            }
            return i;
        }

        public float getFloat(BlockState state, BlockRenderView blockView, BlockPos pos) {
            float f;
            long l = pos.asLong();
            if (this.enabled && !Float.isNaN(f = this.floatCache.get(l))) {
                return f;
            }
            f = state.getAmbientOcclusionLightLevel(blockView, pos);
            if (this.enabled) {
                if (this.floatCache.size() == 100) {
                    this.floatCache.removeFirstFloat();
                }
                this.floatCache.put(l, f);
            }
            return f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum Translation {
        DOWN(0, 1, 2, 3),
        UP(2, 3, 0, 1),
        NORTH(3, 0, 1, 2),
        SOUTH(0, 1, 2, 3),
        WEST(3, 0, 1, 2),
        EAST(1, 2, 3, 0);

        private final int firstCorner;
        private final int secondCorner;
        private final int thirdCorner;
        private final int fourthCorner;
        private static final Translation[] VALUES;

        private Translation(int j, int k, int l, int m) {
            this.firstCorner = j;
            this.secondCorner = k;
            this.thirdCorner = l;
            this.fourthCorner = m;
        }

        public static Translation getTranslations(Direction direction) {
            return VALUES[direction.getId()];
        }

        static {
            VALUES = Util.make(new Translation[6], translations -> {
                translations[Direction.DOWN.getId()] = DOWN;
                translations[Direction.UP.getId()] = UP;
                translations[Direction.NORTH.getId()] = NORTH;
                translations[Direction.SOUTH.getId()] = SOUTH;
                translations[Direction.WEST.getId()] = WEST;
                translations[Direction.EAST.getId()] = EAST;
            });
        }
    }
}

