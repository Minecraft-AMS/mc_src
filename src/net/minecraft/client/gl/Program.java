/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 */
package net.minecraft.client.gl;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GLImportProcessor;
import net.minecraft.client.gl.GlShader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class Program {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_32037 = 32768;
    private final Type shaderType;
    private final String name;
    private int shaderRef;

    protected Program(Type shaderType, int shaderRef, String name) {
        this.shaderType = shaderType;
        this.shaderRef = shaderRef;
        this.name = name;
    }

    public void attachTo(GlShader program) {
        RenderSystem.assertOnRenderThread();
        GlStateManager.glAttachShader(program.getProgramRef(), this.getShaderRef());
    }

    public void release() {
        if (this.shaderRef == -1) {
            return;
        }
        RenderSystem.assertOnRenderThread();
        GlStateManager.glDeleteShader(this.shaderRef);
        this.shaderRef = -1;
        this.shaderType.getProgramCache().remove(this.name);
    }

    public String getName() {
        return this.name;
    }

    public static Program createFromResource(Type type, String name, InputStream stream, String domain, GLImportProcessor loader) throws IOException {
        RenderSystem.assertOnRenderThread();
        int i = Program.loadProgram(type, name, stream, domain, loader);
        Program program = new Program(type, i, name);
        type.getProgramCache().put(name, program);
        return program;
    }

    protected static int loadProgram(Type type, String name, InputStream stream, String domain, GLImportProcessor loader) throws IOException {
        String string = TextureUtil.readResourceAsString(stream);
        if (string == null) {
            throw new IOException("Could not load program " + type.getName());
        }
        int i = GlStateManager.glCreateShader(type.getGlType());
        GlStateManager.glShaderSource(i, loader.readSource(string));
        GlStateManager.glCompileShader(i);
        if (GlStateManager.glGetShaderi(i, 35713) == 0) {
            String string2 = StringUtils.trim((String)GlStateManager.glGetShaderInfoLog(i, 32768));
            throw new IOException("Couldn't compile " + type.getName() + " program (" + domain + ", " + name + ") : " + string2);
        }
        return i;
    }

    private static Program create(Type shaderType, String name, int shaderRef) {
        return new Program(shaderType, shaderRef, name);
    }

    protected int getShaderRef() {
        return this.shaderRef;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type VERTEX = new Type("vertex", ".vsh", 35633);
        public static final /* enum */ Type FRAGMENT = new Type("fragment", ".fsh", 35632);
        private final String name;
        private final String fileExtension;
        private final int glType;
        private final Map<String, Program> programCache = Maps.newHashMap();
        private static final /* synthetic */ Type[] field_1532;

        public static Type[] values() {
            return (Type[])field_1532.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(String name, String extension, int glType) {
            this.name = name;
            this.fileExtension = extension;
            this.glType = glType;
        }

        public String getName() {
            return this.name;
        }

        public String getFileExtension() {
            return this.fileExtension;
        }

        int getGlType() {
            return this.glType;
        }

        public Map<String, Program> getProgramCache() {
            return this.programCache;
        }

        private static /* synthetic */ Type[] method_36815() {
            return new Type[]{VERTEX, FRAGMENT};
        }

        static {
            field_1532 = Type.method_36815();
        }
    }
}

