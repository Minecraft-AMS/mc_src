/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.client.gl;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlProgram;
import org.apache.commons.lang3.StringUtils;

@Environment(value=EnvType.CLIENT)
public class GlShader {
    private final Type shaderType;
    private final String name;
    private final int shaderRef;
    private int refCount;

    private GlShader(Type shaderType, int shaderRef, String name) {
        this.shaderType = shaderType;
        this.shaderRef = shaderRef;
        this.name = name;
    }

    public void attachTo(GlProgram glProgram) {
        ++this.refCount;
        GLX.glAttachShader(glProgram.getProgramRef(), this.shaderRef);
    }

    public void release() {
        --this.refCount;
        if (this.refCount <= 0) {
            GLX.glDeleteShader(this.shaderRef);
            this.shaderType.getLoadedShaders().remove(this.name);
        }
    }

    public String getName() {
        return this.name;
    }

    public static GlShader createFromResource(Type type, String name, InputStream sourceCode) throws IOException {
        String string = TextureUtil.readResourceAsString(sourceCode);
        if (string == null) {
            throw new IOException("Could not load program " + type.getName());
        }
        int i = GLX.glCreateShader(type.getGlType());
        GLX.glShaderSource(i, string);
        GLX.glCompileShader(i);
        if (GLX.glGetShaderi(i, GLX.GL_COMPILE_STATUS) == 0) {
            String string2 = StringUtils.trim((String)GLX.glGetShaderInfoLog(i, 32768));
            throw new IOException("Couldn't compile " + type.getName() + " program: " + string2);
        }
        GlShader glShader = new GlShader(type, i, name);
        type.getLoadedShaders().put(name, glShader);
        return glShader;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        VERTEX("vertex", ".vsh", GLX.GL_VERTEX_SHADER),
        FRAGMENT("fragment", ".fsh", GLX.GL_FRAGMENT_SHADER);

        private final String name;
        private final String fileExtension;
        private final int glType;
        private final Map<String, GlShader> loadedShaders = Maps.newHashMap();

        private Type(String string2, String string3, int j) {
            this.name = string2;
            this.fileExtension = string3;
            this.glType = j;
        }

        public String getName() {
            return this.name;
        }

        public String getFileExtension() {
            return this.fileExtension;
        }

        private int getGlType() {
            return this.glType;
        }

        public Map<String, GlShader> getLoadedShaders() {
            return this.loadedShaders;
        }
    }
}

