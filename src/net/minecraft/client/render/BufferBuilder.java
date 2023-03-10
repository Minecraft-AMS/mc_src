/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.primitives.Floats
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntArrays
 *  it.unimi.dsi.fastutil.ints.IntConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferVertexConsumer;
import net.minecraft.client.render.FixedColorVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class BufferBuilder
extends FixedColorVertexConsumer
implements BufferVertexConsumer {
    private static final int MAX_BUFFER_SIZE = 0x200000;
    private static final Logger LOGGER = LogUtils.getLogger();
    private ByteBuffer buffer;
    private final List<DrawArrayParameters> parameters = Lists.newArrayList();
    private int lastParameterIndex;
    private int buildStart;
    private int elementOffset;
    private int nextDrawStart;
    private int vertexCount;
    @Nullable
    private VertexFormatElement currentElement;
    private int currentElementId;
    private VertexFormat format;
    private VertexFormat.DrawMode drawMode;
    private boolean textured;
    private boolean hasOverlay;
    private boolean building;
    @Nullable
    private Vec3f[] sortingPrimitiveCenters;
    private float sortingCameraX = Float.NaN;
    private float sortingCameraY = Float.NaN;
    private float sortingCameraZ = Float.NaN;
    private boolean hasNoVertexBuffer;

    public BufferBuilder(int initialCapacity) {
        this.buffer = GlAllocationUtils.allocateByteBuffer(initialCapacity * 6);
    }

    private void grow() {
        this.grow(this.format.getVertexSize());
    }

    private void grow(int size) {
        if (this.elementOffset + size <= this.buffer.capacity()) {
            return;
        }
        int i = this.buffer.capacity();
        int j = i + BufferBuilder.roundBufferSize(size);
        LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", (Object)i, (Object)j);
        ByteBuffer byteBuffer = GlAllocationUtils.resizeByteBuffer(this.buffer, j);
        byteBuffer.rewind();
        this.buffer = byteBuffer;
    }

    private static int roundBufferSize(int amount) {
        int j;
        int i = 0x200000;
        if (amount == 0) {
            return i;
        }
        if (amount < 0) {
            i *= -1;
        }
        if ((j = amount % i) == 0) {
            return amount;
        }
        return amount + i - j;
    }

    public void sortFrom(float cameraX, float cameraY, float cameraZ) {
        if (this.drawMode != VertexFormat.DrawMode.QUADS) {
            return;
        }
        if (this.sortingCameraX != cameraX || this.sortingCameraY != cameraY || this.sortingCameraZ != cameraZ) {
            this.sortingCameraX = cameraX;
            this.sortingCameraY = cameraY;
            this.sortingCameraZ = cameraZ;
            if (this.sortingPrimitiveCenters == null) {
                this.sortingPrimitiveCenters = this.buildPrimitiveCenters();
            }
        }
    }

    public State popState() {
        return new State(this.drawMode, this.vertexCount, this.sortingPrimitiveCenters, this.sortingCameraX, this.sortingCameraY, this.sortingCameraZ);
    }

    public void restoreState(State state) {
        this.buffer.clear();
        this.drawMode = state.drawMode;
        this.vertexCount = state.vertexCount;
        this.elementOffset = this.buildStart;
        this.sortingPrimitiveCenters = state.sortingPrimitiveCenters;
        this.sortingCameraX = state.sortingCameraX;
        this.sortingCameraY = state.sortingCameraY;
        this.sortingCameraZ = state.sortingCameraZ;
        this.hasNoVertexBuffer = true;
    }

    public void begin(VertexFormat.DrawMode drawMode, VertexFormat format) {
        if (this.building) {
            throw new IllegalStateException("Already building!");
        }
        this.building = true;
        this.drawMode = drawMode;
        this.setFormat(format);
        this.currentElement = (VertexFormatElement)format.getElements().get(0);
        this.currentElementId = 0;
        this.buffer.clear();
    }

    private void setFormat(VertexFormat format) {
        if (this.format == format) {
            return;
        }
        this.format = format;
        boolean bl = format == VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL;
        boolean bl2 = format == VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
        this.textured = bl || bl2;
        this.hasOverlay = bl;
    }

    private IntConsumer createIndexWriter(VertexFormat.IntType elementFormat) {
        switch (elementFormat) {
            case BYTE: {
                return value -> this.buffer.put((byte)value);
            }
            case SHORT: {
                return value -> this.buffer.putShort((short)value);
            }
        }
        return value -> this.buffer.putInt(value);
    }

    private Vec3f[] buildPrimitiveCenters() {
        FloatBuffer floatBuffer = this.buffer.asFloatBuffer();
        int i = this.buildStart / 4;
        int j = this.format.getVertexSizeInteger();
        int k = j * this.drawMode.size;
        int l = this.vertexCount / this.drawMode.size;
        Vec3f[] vec3fs = new Vec3f[l];
        for (int m = 0; m < l; ++m) {
            float f = floatBuffer.get(i + m * k + 0);
            float g = floatBuffer.get(i + m * k + 1);
            float h = floatBuffer.get(i + m * k + 2);
            float n = floatBuffer.get(i + m * k + j * 2 + 0);
            float o = floatBuffer.get(i + m * k + j * 2 + 1);
            float p = floatBuffer.get(i + m * k + j * 2 + 2);
            float q = (f + n) / 2.0f;
            float r = (g + o) / 2.0f;
            float s = (h + p) / 2.0f;
            vec3fs[m] = new Vec3f(q, r, s);
        }
        return vec3fs;
    }

    private void writeSortedIndices(VertexFormat.IntType elementFormat) {
        float[] fs = new float[this.sortingPrimitiveCenters.length];
        int[] is = new int[this.sortingPrimitiveCenters.length];
        for (int i2 = 0; i2 < this.sortingPrimitiveCenters.length; ++i2) {
            float f = this.sortingPrimitiveCenters[i2].getX() - this.sortingCameraX;
            float g = this.sortingPrimitiveCenters[i2].getY() - this.sortingCameraY;
            float h = this.sortingPrimitiveCenters[i2].getZ() - this.sortingCameraZ;
            fs[i2] = f * f + g * g + h * h;
            is[i2] = i2;
        }
        IntArrays.mergeSort((int[])is, (i, j) -> Floats.compare((float)fs[j], (float)fs[i]));
        IntConsumer intConsumer = this.createIndexWriter(elementFormat);
        this.buffer.position(this.elementOffset);
        for (int j2 : is) {
            intConsumer.accept(j2 * this.drawMode.size + 0);
            intConsumer.accept(j2 * this.drawMode.size + 1);
            intConsumer.accept(j2 * this.drawMode.size + 2);
            intConsumer.accept(j2 * this.drawMode.size + 2);
            intConsumer.accept(j2 * this.drawMode.size + 3);
            intConsumer.accept(j2 * this.drawMode.size + 0);
        }
    }

    public void end() {
        boolean bl;
        if (!this.building) {
            throw new IllegalStateException("Not building!");
        }
        int i = this.drawMode.getSize(this.vertexCount);
        VertexFormat.IntType intType = VertexFormat.IntType.getSmallestTypeFor(i);
        if (this.sortingPrimitiveCenters != null) {
            int j = MathHelper.roundUpToMultiple(i * intType.size, 4);
            this.grow(j);
            this.writeSortedIndices(intType);
            bl = false;
            this.elementOffset += j;
            this.buildStart += this.vertexCount * this.format.getVertexSize() + j;
        } else {
            bl = true;
            this.buildStart += this.vertexCount * this.format.getVertexSize();
        }
        this.building = false;
        this.parameters.add(new DrawArrayParameters(this.format, this.vertexCount, i, this.drawMode, intType, this.hasNoVertexBuffer, bl));
        this.vertexCount = 0;
        this.currentElement = null;
        this.currentElementId = 0;
        this.sortingPrimitiveCenters = null;
        this.sortingCameraX = Float.NaN;
        this.sortingCameraY = Float.NaN;
        this.sortingCameraZ = Float.NaN;
        this.hasNoVertexBuffer = false;
    }

    @Override
    public void putByte(int index, byte value) {
        this.buffer.put(this.elementOffset + index, value);
    }

    @Override
    public void putShort(int index, short value) {
        this.buffer.putShort(this.elementOffset + index, value);
    }

    @Override
    public void putFloat(int index, float value) {
        this.buffer.putFloat(this.elementOffset + index, value);
    }

    @Override
    public void next() {
        if (this.currentElementId != 0) {
            throw new IllegalStateException("Not filled all elements of the vertex");
        }
        ++this.vertexCount;
        this.grow();
        if (this.drawMode == VertexFormat.DrawMode.LINES || this.drawMode == VertexFormat.DrawMode.LINE_STRIP) {
            int i = this.format.getVertexSize();
            this.buffer.position(this.elementOffset);
            ByteBuffer byteBuffer = this.buffer.duplicate();
            byteBuffer.position(this.elementOffset - i).limit(this.elementOffset);
            this.buffer.put(byteBuffer);
            this.elementOffset += i;
            ++this.vertexCount;
            this.grow();
        }
    }

    @Override
    public void nextElement() {
        VertexFormatElement vertexFormatElement;
        ImmutableList<VertexFormatElement> immutableList = this.format.getElements();
        this.currentElementId = (this.currentElementId + 1) % immutableList.size();
        this.elementOffset += this.currentElement.getByteLength();
        this.currentElement = vertexFormatElement = (VertexFormatElement)immutableList.get(this.currentElementId);
        if (vertexFormatElement.getType() == VertexFormatElement.Type.PADDING) {
            this.nextElement();
        }
        if (this.colorFixed && this.currentElement.getType() == VertexFormatElement.Type.COLOR) {
            BufferVertexConsumer.super.color(this.fixedRed, this.fixedGreen, this.fixedBlue, this.fixedAlpha);
        }
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        if (this.colorFixed) {
            throw new IllegalStateException();
        }
        return BufferVertexConsumer.super.color(red, green, blue, alpha);
    }

    @Override
    public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
        if (this.colorFixed) {
            throw new IllegalStateException();
        }
        if (this.textured) {
            int i;
            this.putFloat(0, x);
            this.putFloat(4, y);
            this.putFloat(8, z);
            this.putByte(12, (byte)(red * 255.0f));
            this.putByte(13, (byte)(green * 255.0f));
            this.putByte(14, (byte)(blue * 255.0f));
            this.putByte(15, (byte)(alpha * 255.0f));
            this.putFloat(16, u);
            this.putFloat(20, v);
            if (this.hasOverlay) {
                this.putShort(24, (short)(overlay & 0xFFFF));
                this.putShort(26, (short)(overlay >> 16 & 0xFFFF));
                i = 28;
            } else {
                i = 24;
            }
            this.putShort(i + 0, (short)(light & 0xFFFF));
            this.putShort(i + 2, (short)(light >> 16 & 0xFFFF));
            this.putByte(i + 4, BufferVertexConsumer.packByte(normalX));
            this.putByte(i + 5, BufferVertexConsumer.packByte(normalY));
            this.putByte(i + 6, BufferVertexConsumer.packByte(normalZ));
            this.elementOffset += i + 8;
            this.next();
            return;
        }
        super.vertex(x, y, z, red, green, blue, alpha, u, v, overlay, light, normalX, normalY, normalZ);
    }

    public Pair<DrawArrayParameters, ByteBuffer> popData() {
        DrawArrayParameters drawArrayParameters = this.parameters.get(this.lastParameterIndex++);
        this.buffer.position(this.nextDrawStart);
        this.nextDrawStart += MathHelper.roundUpToMultiple(drawArrayParameters.getIndexBufferEnd(), 4);
        this.buffer.limit(this.nextDrawStart);
        if (this.lastParameterIndex == this.parameters.size() && this.vertexCount == 0) {
            this.clear();
        }
        ByteBuffer byteBuffer = this.buffer.slice();
        this.buffer.clear();
        return Pair.of((Object)drawArrayParameters, (Object)byteBuffer);
    }

    public void clear() {
        if (this.buildStart != this.nextDrawStart) {
            LOGGER.warn("Bytes mismatch {} {}", (Object)this.buildStart, (Object)this.nextDrawStart);
        }
        this.reset();
    }

    public void reset() {
        this.buildStart = 0;
        this.nextDrawStart = 0;
        this.elementOffset = 0;
        this.parameters.clear();
        this.lastParameterIndex = 0;
    }

    @Override
    public VertexFormatElement getCurrentElement() {
        if (this.currentElement == null) {
            throw new IllegalStateException("BufferBuilder not started");
        }
        return this.currentElement;
    }

    public boolean isBuilding() {
        return this.building;
    }

    @Environment(value=EnvType.CLIENT)
    public static class State {
        final VertexFormat.DrawMode drawMode;
        final int vertexCount;
        @Nullable
        final Vec3f[] sortingPrimitiveCenters;
        final float sortingCameraX;
        final float sortingCameraY;
        final float sortingCameraZ;

        State(VertexFormat.DrawMode drawMode, int vertexCount, @Nullable Vec3f[] currentParameters, float cameraX, float cameraY, float cameraZ) {
            this.drawMode = drawMode;
            this.vertexCount = vertexCount;
            this.sortingPrimitiveCenters = currentParameters;
            this.sortingCameraX = cameraX;
            this.sortingCameraY = cameraY;
            this.sortingCameraZ = cameraZ;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class DrawArrayParameters {
        private final VertexFormat vertexFormat;
        private final int count;
        private final int vertexCount;
        private final VertexFormat.DrawMode mode;
        private final VertexFormat.IntType elementFormat;
        private final boolean hasNoVertexBuffer;
        private final boolean hasNoIndexBuffer;

        DrawArrayParameters(VertexFormat vertexFormat, int count, int vertexCount, VertexFormat.DrawMode mode, VertexFormat.IntType elementFormat, boolean hasNoVertexBuffer, boolean hasNoIndexBuffer) {
            this.vertexFormat = vertexFormat;
            this.count = count;
            this.vertexCount = vertexCount;
            this.mode = mode;
            this.elementFormat = elementFormat;
            this.hasNoVertexBuffer = hasNoVertexBuffer;
            this.hasNoIndexBuffer = hasNoIndexBuffer;
        }

        public VertexFormat getVertexFormat() {
            return this.vertexFormat;
        }

        public int getCount() {
            return this.count;
        }

        public int getVertexCount() {
            return this.vertexCount;
        }

        public VertexFormat.DrawMode getMode() {
            return this.mode;
        }

        public VertexFormat.IntType getElementFormat() {
            return this.elementFormat;
        }

        public int getIndexBufferStart() {
            return this.count * this.vertexFormat.getVertexSize();
        }

        private int getIndexBufferLength() {
            return this.hasNoIndexBuffer ? 0 : this.vertexCount * this.elementFormat.size;
        }

        public int getIndexBufferEnd() {
            return this.getIndexBufferStart() + this.getIndexBufferLength();
        }

        public boolean hasNoVertexBuffer() {
            return this.hasNoVertexBuffer;
        }

        public boolean hasNoIndexBuffer() {
            return this.hasNoIndexBuffer;
        }
    }
}

