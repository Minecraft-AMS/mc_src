/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.IntConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class VertexFormatElement {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Format dataType;
    private final Type type;
    private final int textureIndex;
    private final int length;
    private final int byteLength;

    public VertexFormatElement(int textureIndex, Format dataType, Type type, int length) {
        if (this.isValidType(textureIndex, type)) {
            this.type = type;
        } else {
            LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
            this.type = Type.UV;
        }
        this.dataType = dataType;
        this.textureIndex = textureIndex;
        this.length = length;
        this.byteLength = dataType.getSize() * this.length;
    }

    private boolean isValidType(int index, Type type) {
        return index == 0 || type == Type.UV;
    }

    public final Format getDataType() {
        return this.dataType;
    }

    public final Type getType() {
        return this.type;
    }

    public final int getTextureIndex() {
        return this.textureIndex;
    }

    public String toString() {
        return this.length + "," + this.type.getName() + "," + this.dataType.getName();
    }

    public final int getByteLength() {
        return this.byteLength;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        VertexFormatElement vertexFormatElement = (VertexFormatElement)o;
        if (this.length != vertexFormatElement.length) {
            return false;
        }
        if (this.textureIndex != vertexFormatElement.textureIndex) {
            return false;
        }
        if (this.dataType != vertexFormatElement.dataType) {
            return false;
        }
        return this.type == vertexFormatElement.type;
    }

    public int hashCode() {
        int i = this.dataType.hashCode();
        i = 31 * i + this.type.hashCode();
        i = 31 * i + this.textureIndex;
        i = 31 * i + this.length;
        return i;
    }

    public void startDrawing(long pointer, int stride) {
        this.type.startDrawing(this.length, this.dataType.getGlId(), stride, pointer, this.textureIndex);
    }

    public void endDrawing() {
        this.type.endDrawing(this.textureIndex);
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Format {
        FLOAT(4, "Float", 5126),
        UBYTE(1, "Unsigned Byte", 5121),
        BYTE(1, "Byte", 5120),
        USHORT(2, "Unsigned Short", 5123),
        SHORT(2, "Short", 5122),
        UINT(4, "Unsigned Int", 5125),
        INT(4, "Int", 5124);

        private final int size;
        private final String name;
        private final int glId;

        private Format(int size, String name, int glId) {
            this.size = size;
            this.name = name;
            this.glId = glId;
        }

        public int getSize() {
            return this.size;
        }

        public String getName() {
            return this.name;
        }

        public int getGlId() {
            return this.glId;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        POSITION("Position", (i, j, k, l, m) -> {
            GlStateManager.vertexPointer(i, j, k, l);
            GlStateManager.enableClientState(32884);
        }, i -> GlStateManager.disableClientState(32884)),
        NORMAL("Normal", (i, j, k, l, m) -> {
            GlStateManager.normalPointer(j, k, l);
            GlStateManager.enableClientState(32885);
        }, i -> GlStateManager.disableClientState(32885)),
        COLOR("Vertex Color", (i, j, k, l, m) -> {
            GlStateManager.colorPointer(i, j, k, l);
            GlStateManager.enableClientState(32886);
        }, i -> {
            GlStateManager.disableClientState(32886);
            GlStateManager.clearCurrentColor();
        }),
        UV("UV", (i, j, k, l, m) -> {
            GlStateManager.clientActiveTexture(33984 + m);
            GlStateManager.texCoordPointer(i, j, k, l);
            GlStateManager.enableClientState(32888);
            GlStateManager.clientActiveTexture(33984);
        }, i -> {
            GlStateManager.clientActiveTexture(33984 + i);
            GlStateManager.disableClientState(32888);
            GlStateManager.clientActiveTexture(33984);
        }),
        PADDING("Padding", (i, j, k, l, m) -> {}, i -> {}),
        GENERIC("Generic", (i, j, k, l, m) -> {
            GlStateManager.enableVertexAttribArray(m);
            GlStateManager.vertexAttribPointer(m, i, j, false, k, l);
        }, GlStateManager::method_22607);

        private final String name;
        private final Starter starter;
        private final IntConsumer finisher;

        private Type(String name, Starter starter, IntConsumer intConsumer) {
            this.name = name;
            this.starter = starter;
            this.finisher = intConsumer;
        }

        private void startDrawing(int count, int glId, int stride, long pointer, int elementIndex) {
            this.starter.setupBufferState(count, glId, stride, pointer, elementIndex);
        }

        public void endDrawing(int elementIndex) {
            this.finisher.accept(elementIndex);
        }

        public String getName() {
            return this.name;
        }

        @Environment(value=EnvType.CLIENT)
        static interface Starter {
            public void setupBufferState(int var1, int var2, int var3, long var4, int var6);
        }
    }
}

