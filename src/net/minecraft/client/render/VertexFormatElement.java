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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class VertexFormatElement {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Format format;
    private final Type type;
    private final int index;
    private final int count;

    public VertexFormatElement(int index, Format format, Type type, int count) {
        if (this.isValidType(index, type)) {
            this.type = type;
        } else {
            LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
            this.type = Type.UV;
        }
        this.format = format;
        this.index = index;
        this.count = count;
    }

    private final boolean isValidType(int index, Type type) {
        return index == 0 || type == Type.UV;
    }

    public final Format getFormat() {
        return this.format;
    }

    public final Type getType() {
        return this.type;
    }

    public final int getCount() {
        return this.count;
    }

    public final int getIndex() {
        return this.index;
    }

    public String toString() {
        return this.count + "," + this.type.getName() + "," + this.format.getName();
    }

    public final int getSize() {
        return this.format.getSize() * this.count;
    }

    public final boolean isPosition() {
        return this.type == Type.POSITION;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        VertexFormatElement vertexFormatElement = (VertexFormatElement)o;
        if (this.count != vertexFormatElement.count) {
            return false;
        }
        if (this.index != vertexFormatElement.index) {
            return false;
        }
        if (this.format != vertexFormatElement.format) {
            return false;
        }
        return this.type == vertexFormatElement.type;
    }

    public int hashCode() {
        int i = this.format.hashCode();
        i = 31 * i + this.type.hashCode();
        i = 31 * i + this.index;
        i = 31 * i + this.count;
        return i;
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
        POSITION("Position"),
        NORMAL("Normal"),
        COLOR("Vertex Color"),
        UV("UV"),
        MATRIX("Bone Matrix"),
        BLEND_WEIGHT("Blend Weight"),
        PADDING("Padding");

        private final String name;

        private Type(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}

