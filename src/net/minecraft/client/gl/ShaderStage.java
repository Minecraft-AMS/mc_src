/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.client.gl;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GLImportProcessor;
import net.minecraft.client.gl.ShaderProgramSetupView;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

@Environment(value=EnvType.CLIENT)
public class ShaderStage {
    private static final int MAX_INFO_LOG_LENGTH = 32768;
    private final Type type;
    private final String name;
    private int glRef;

    protected ShaderStage(Type type, int glRef, String name) {
        this.type = type;
        this.glRef = glRef;
        this.name = name;
    }

    public void attachTo(ShaderProgramSetupView program) {
        RenderSystem.assertOnRenderThread();
        GlStateManager.glAttachShader(program.getGlRef(), this.getGlRef());
    }

    public void release() {
        if (this.glRef == -1) {
            return;
        }
        RenderSystem.assertOnRenderThread();
        GlStateManager.glDeleteShader(this.glRef);
        this.glRef = -1;
        this.type.getLoadedShaders().remove(this.name);
    }

    public String getName() {
        return this.name;
    }

    public static ShaderStage createFromResource(Type type, String name, InputStream stream, String domain, GLImportProcessor loader) throws IOException {
        RenderSystem.assertOnRenderThread();
        int i = ShaderStage.load(type, name, stream, domain, loader);
        ShaderStage shaderStage = new ShaderStage(type, i, name);
        type.getLoadedShaders().put(name, shaderStage);
        return shaderStage;
    }

    protected static int load(Type type, String name, InputStream stream, String domain, GLImportProcessor loader) throws IOException {
        String string = IOUtils.toString((InputStream)stream, (Charset)StandardCharsets.UTF_8);
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

    protected int getGlRef() {
        return this.glRef;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type VERTEX = new Type("vertex", ".vsh", 35633);
        public static final /* enum */ Type FRAGMENT = new Type("fragment", ".fsh", 35632);
        private final String name;
        private final String fileExtension;
        private final int glType;
        private final Map<String, ShaderStage> loadedShaders = Maps.newHashMap();
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

        public Map<String, ShaderStage> getLoadedShaders() {
            return this.loadedShaders;
        }

        private static /* synthetic */ Type[] method_36815() {
            return new Type[]{VERTEX, FRAGMENT};
        }

        static {
            field_1532 = Type.method_36815();
        }
    }
}

