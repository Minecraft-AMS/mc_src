/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlShader;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class GlProgramManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void useProgram(int program) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._glUseProgram(program);
    }

    public static void deleteProgram(GlShader shader) {
        RenderSystem.assertOnRenderThread();
        shader.getFragmentShader().release();
        shader.getVertexShader().release();
        GlStateManager.glDeleteProgram(shader.getProgramRef());
    }

    public static int createProgram() throws IOException {
        RenderSystem.assertOnRenderThread();
        int i = GlStateManager.glCreateProgram();
        if (i <= 0) {
            throw new IOException("Could not create shader program (returned program ID " + i + ")");
        }
        return i;
    }

    public static void linkProgram(GlShader shader) {
        RenderSystem.assertOnRenderThread();
        shader.attachReferencedShaders();
        GlStateManager.glLinkProgram(shader.getProgramRef());
        int i = GlStateManager.glGetProgrami(shader.getProgramRef(), 35714);
        if (i == 0) {
            LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", (Object)shader.getVertexShader().getName(), (Object)shader.getFragmentShader().getName());
            LOGGER.warn(GlStateManager.glGetProgramInfoLog(shader.getProgramRef(), 32768));
        }
    }
}

