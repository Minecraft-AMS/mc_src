/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class VertexFormatElement {
    private final ComponentType componentType;
    private final Type type;
    private final int uvIndex;
    private final int componentCount;
    private final int byteLength;

    public VertexFormatElement(int uvIndex, ComponentType componentType, Type type, int componentCount) {
        if (!this.isValidType(uvIndex, type)) {
            throw new IllegalStateException("Multiple vertex elements of the same type other than UVs are not supported");
        }
        this.type = type;
        this.componentType = componentType;
        this.uvIndex = uvIndex;
        this.componentCount = componentCount;
        this.byteLength = componentType.getByteLength() * this.componentCount;
    }

    private boolean isValidType(int uvIndex, Type type) {
        return uvIndex == 0 || type == Type.UV;
    }

    public final ComponentType getComponentType() {
        return this.componentType;
    }

    public final Type getType() {
        return this.type;
    }

    public final int getComponentCount() {
        return this.componentCount;
    }

    public final int getUvIndex() {
        return this.uvIndex;
    }

    public String toString() {
        return this.componentCount + "," + this.type.getName() + "," + this.componentType.getName();
    }

    public final int getByteLength() {
        return this.byteLength;
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
        if (this.componentCount != vertexFormatElement.componentCount) {
            return false;
        }
        if (this.uvIndex != vertexFormatElement.uvIndex) {
            return false;
        }
        if (this.componentType != vertexFormatElement.componentType) {
            return false;
        }
        return this.type == vertexFormatElement.type;
    }

    public int hashCode() {
        int i = this.componentType.hashCode();
        i = 31 * i + this.type.hashCode();
        i = 31 * i + this.uvIndex;
        i = 31 * i + this.componentCount;
        return i;
    }

    public void setupState(int elementIndex, long offset, int stride) {
        this.type.setupState(this.componentCount, this.componentType.getGlType(), stride, offset, this.uvIndex, elementIndex);
    }

    public void clearState(int elementIndex) {
        this.type.clearState(this.uvIndex, elementIndex);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type POSITION = new Type("Position", (componentCount, componentType, stride, offset, uvIndex, elementIndex) -> {
            GlStateManager._enableVertexAttribArray(elementIndex);
            GlStateManager._vertexAttribPointer(elementIndex, componentCount, componentType, false, stride, offset);
        }, (uvIndex, elementIndex) -> GlStateManager._disableVertexAttribArray(elementIndex));
        public static final /* enum */ Type NORMAL = new Type("Normal", (componentCount, componentType, stride, offset, uvIndex, elementIndex) -> {
            GlStateManager._enableVertexAttribArray(elementIndex);
            GlStateManager._vertexAttribPointer(elementIndex, componentCount, componentType, true, stride, offset);
        }, (uvIndex, elementIndex) -> GlStateManager._disableVertexAttribArray(elementIndex));
        public static final /* enum */ Type COLOR = new Type("Vertex Color", (componentCount, componentType, stride, offset, uvIndex, elementIndex) -> {
            GlStateManager._enableVertexAttribArray(elementIndex);
            GlStateManager._vertexAttribPointer(elementIndex, componentCount, componentType, true, stride, offset);
        }, (uvIndex, elementIndex) -> GlStateManager._disableVertexAttribArray(elementIndex));
        public static final /* enum */ Type UV = new Type("UV", (componentCount, componentType, stride, offset, uvIndex, elementIndex) -> {
            GlStateManager._enableVertexAttribArray(elementIndex);
            if (componentType == 5126) {
                GlStateManager._vertexAttribPointer(elementIndex, componentCount, componentType, false, stride, offset);
            } else {
                GlStateManager._vertexAttribIPointer(elementIndex, componentCount, componentType, stride, offset);
            }
        }, (uvIndex, elementIndex) -> GlStateManager._disableVertexAttribArray(elementIndex));
        public static final /* enum */ Type PADDING = new Type("Padding", (componentCount, componentType, stride, offset, uvIndex, elementIndex) -> {}, (uvIndex, elementIndex) -> {});
        public static final /* enum */ Type GENERIC = new Type("Generic", (componentCount, componentType, stride, offset, uvIndex, elementIndex) -> {
            GlStateManager._enableVertexAttribArray(elementIndex);
            GlStateManager._vertexAttribPointer(elementIndex, componentCount, componentType, false, stride, offset);
        }, (uvIndex, elementIndex) -> GlStateManager._disableVertexAttribArray(elementIndex));
        private final String name;
        private final SetupTask setupTask;
        private final ClearTask clearTask;
        private static final /* synthetic */ Type[] field_1631;

        public static Type[] values() {
            return (Type[])field_1631.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(String name, SetupTask setupTask, ClearTask clearTask) {
            this.name = name;
            this.setupTask = setupTask;
            this.clearTask = clearTask;
        }

        void setupState(int componentCount, int componentType, int stride, long offset, int uvIndex, int elementIndex) {
            this.setupTask.setupBufferState(componentCount, componentType, stride, offset, uvIndex, elementIndex);
        }

        public void clearState(int uvIndex, int elementIndex) {
            this.clearTask.clearBufferState(uvIndex, elementIndex);
        }

        public String getName() {
            return this.name;
        }

        private static /* synthetic */ Type[] method_36819() {
            return new Type[]{POSITION, NORMAL, COLOR, UV, PADDING, GENERIC};
        }

        static {
            field_1631 = Type.method_36819();
        }

        @FunctionalInterface
        @Environment(value=EnvType.CLIENT)
        static interface SetupTask {
            public void setupBufferState(int var1, int var2, int var3, long var4, int var6, int var7);
        }

        @FunctionalInterface
        @Environment(value=EnvType.CLIENT)
        static interface ClearTask {
            public void clearBufferState(int var1, int var2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class ComponentType
    extends Enum<ComponentType> {
        public static final /* enum */ ComponentType FLOAT = new ComponentType(4, "Float", 5126);
        public static final /* enum */ ComponentType UBYTE = new ComponentType(1, "Unsigned Byte", 5121);
        public static final /* enum */ ComponentType BYTE = new ComponentType(1, "Byte", 5120);
        public static final /* enum */ ComponentType USHORT = new ComponentType(2, "Unsigned Short", 5123);
        public static final /* enum */ ComponentType SHORT = new ComponentType(2, "Short", 5122);
        public static final /* enum */ ComponentType UINT = new ComponentType(4, "Unsigned Int", 5125);
        public static final /* enum */ ComponentType INT = new ComponentType(4, "Int", 5124);
        private final int byteLength;
        private final String name;
        private final int glType;
        private static final /* synthetic */ ComponentType[] field_1620;

        public static ComponentType[] values() {
            return (ComponentType[])field_1620.clone();
        }

        public static ComponentType valueOf(String string) {
            return Enum.valueOf(ComponentType.class, string);
        }

        private ComponentType(int byteLength, String name, int glType) {
            this.byteLength = byteLength;
            this.name = name;
            this.glType = glType;
        }

        public int getByteLength() {
            return this.byteLength;
        }

        public String getName() {
            return this.name;
        }

        public int getGlType() {
            return this.glType;
        }

        private static /* synthetic */ ComponentType[] method_36818() {
            return new ComponentType[]{FLOAT, UBYTE, BYTE, USHORT, SHORT, UINT, INT};
        }

        static {
            field_1620 = ComponentType.method_36818();
        }
    }
}

