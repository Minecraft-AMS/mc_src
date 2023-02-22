/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.VertexFormatElement;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class VertexFormat {
    private final ImmutableList<VertexFormatElement> elements;
    private final ImmutableMap<String, VertexFormatElement> elementMap;
    private final IntList offsets = new IntArrayList();
    private final int vertexSizeByte;
    @Nullable
    private VertexBuffer buffer;

    public VertexFormat(ImmutableMap<String, VertexFormatElement> elementMap) {
        this.elementMap = elementMap;
        this.elements = elementMap.values().asList();
        int i = 0;
        for (VertexFormatElement vertexFormatElement : elementMap.values()) {
            this.offsets.add(i);
            i += vertexFormatElement.getByteLength();
        }
        this.vertexSizeByte = i;
    }

    public String toString() {
        return "format: " + this.elementMap.size() + " elements: " + this.elementMap.entrySet().stream().map(Object::toString).collect(Collectors.joining(" "));
    }

    public int getVertexSizeInteger() {
        return this.getVertexSizeByte() / 4;
    }

    public int getVertexSizeByte() {
        return this.vertexSizeByte;
    }

    public ImmutableList<VertexFormatElement> getElements() {
        return this.elements;
    }

    public ImmutableList<String> getAttributeNames() {
        return this.elementMap.keySet().asList();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        VertexFormat vertexFormat = (VertexFormat)o;
        if (this.vertexSizeByte != vertexFormat.vertexSizeByte) {
            return false;
        }
        return this.elementMap.equals(vertexFormat.elementMap);
    }

    public int hashCode() {
        return this.elementMap.hashCode();
    }

    public void setupState() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::setupStateInternal);
            return;
        }
        this.setupStateInternal();
    }

    private void setupStateInternal() {
        int i = this.getVertexSizeByte();
        ImmutableList<VertexFormatElement> list = this.getElements();
        for (int j = 0; j < list.size(); ++j) {
            ((VertexFormatElement)list.get(j)).setupState(j, this.offsets.getInt(j), i);
        }
    }

    public void clearState() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::clearStateInternal);
            return;
        }
        this.clearStateInternal();
    }

    private void clearStateInternal() {
        ImmutableList<VertexFormatElement> immutableList = this.getElements();
        for (int i = 0; i < immutableList.size(); ++i) {
            VertexFormatElement vertexFormatElement = (VertexFormatElement)immutableList.get(i);
            vertexFormatElement.clearState(i);
        }
    }

    public VertexBuffer getBuffer() {
        VertexBuffer vertexBuffer = this.buffer;
        if (vertexBuffer == null) {
            this.buffer = vertexBuffer = new VertexBuffer();
        }
        return vertexBuffer;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class DrawMode
    extends Enum<DrawMode> {
        public static final /* enum */ DrawMode LINES = new DrawMode(4, 2, 2, false);
        public static final /* enum */ DrawMode LINE_STRIP = new DrawMode(5, 2, 1, true);
        public static final /* enum */ DrawMode DEBUG_LINES = new DrawMode(1, 2, 2, false);
        public static final /* enum */ DrawMode DEBUG_LINE_STRIP = new DrawMode(3, 2, 1, true);
        public static final /* enum */ DrawMode TRIANGLES = new DrawMode(4, 3, 3, false);
        public static final /* enum */ DrawMode TRIANGLE_STRIP = new DrawMode(5, 3, 1, true);
        public static final /* enum */ DrawMode TRIANGLE_FAN = new DrawMode(6, 3, 1, true);
        public static final /* enum */ DrawMode QUADS = new DrawMode(4, 4, 4, false);
        public final int glMode;
        public final int firstVertexCount;
        public final int additionalVertexCount;
        public final boolean shareVertices;
        private static final /* synthetic */ DrawMode[] field_27386;

        public static DrawMode[] values() {
            return (DrawMode[])field_27386.clone();
        }

        public static DrawMode valueOf(String string) {
            return Enum.valueOf(DrawMode.class, string);
        }

        private DrawMode(int glMode, int firstVertexCount, int additionalVertexCount, boolean shareVertices) {
            this.glMode = glMode;
            this.firstVertexCount = firstVertexCount;
            this.additionalVertexCount = additionalVertexCount;
            this.shareVertices = shareVertices;
        }

        public int getIndexCount(int vertexCount) {
            return switch (this) {
                case LINE_STRIP, DEBUG_LINES, DEBUG_LINE_STRIP, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN -> vertexCount;
                case LINES, QUADS -> vertexCount / 4 * 6;
                default -> 0;
            };
        }

        private static /* synthetic */ DrawMode[] method_36817() {
            return new DrawMode[]{LINES, LINE_STRIP, DEBUG_LINES, DEBUG_LINE_STRIP, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN, QUADS};
        }

        static {
            field_27386 = DrawMode.method_36817();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class IndexType
    extends Enum<IndexType> {
        public static final /* enum */ IndexType BYTE = new IndexType(5121, 1);
        public static final /* enum */ IndexType SHORT = new IndexType(5123, 2);
        public static final /* enum */ IndexType INT = new IndexType(5125, 4);
        public final int glType;
        public final int size;
        private static final /* synthetic */ IndexType[] field_27376;

        public static IndexType[] values() {
            return (IndexType[])field_27376.clone();
        }

        public static IndexType valueOf(String string) {
            return Enum.valueOf(IndexType.class, string);
        }

        private IndexType(int glType, int size) {
            this.glType = glType;
            this.size = size;
        }

        public static IndexType smallestFor(int indexCount) {
            if ((indexCount & 0xFFFF0000) != 0) {
                return INT;
            }
            if ((indexCount & 0xFF00) != 0) {
                return SHORT;
            }
            return BYTE;
        }

        private static /* synthetic */ IndexType[] method_36816() {
            return new IndexType[]{BYTE, SHORT, INT};
        }

        static {
            field_27376 = IndexType.method_36816();
        }
    }
}

