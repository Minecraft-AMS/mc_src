/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.opengl.GL14
 *  org.lwjgl.opengl.GL15
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL20C
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GL32C
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.platform;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public class GlStateManager {
    private static final boolean ON_LINUX = Util.getOperatingSystem() == Util.OperatingSystem.LINUX;
    public static final int TEXTURE_COUNT = 12;
    private static final BlendFuncState BLEND = new BlendFuncState();
    private static final DepthTestState DEPTH = new DepthTestState();
    private static final CullFaceState CULL = new CullFaceState();
    private static final PolygonOffsetState POLY_OFFSET = new PolygonOffsetState();
    private static final LogicOpState COLOR_LOGIC = new LogicOpState();
    private static final StencilState STENCIL = new StencilState();
    private static final ScissorTestState SCISSOR = new ScissorTestState();
    private static int activeTexture;
    private static final Texture2DState[] TEXTURES;
    private static final ColorMask COLOR_MASK;

    public static void _disableScissorTest() {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager.SCISSOR.capState.disable();
    }

    public static void _enableScissorTest() {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager.SCISSOR.capState.enable();
    }

    public static void _scissorBox(int x, int y, int width, int height) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL20.glScissor((int)x, (int)y, (int)width, (int)height);
    }

    public static void _disableDepthTest() {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager.DEPTH.capState.disable();
    }

    public static void _enableDepthTest() {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager.DEPTH.capState.enable();
    }

    public static void _depthFunc(int func) {
        RenderSystem.assertOnRenderThreadOrInit();
        if (func != GlStateManager.DEPTH.func) {
            GlStateManager.DEPTH.func = func;
            GL11.glDepthFunc((int)func);
        }
    }

    public static void _depthMask(boolean mask) {
        RenderSystem.assertOnRenderThread();
        if (mask != GlStateManager.DEPTH.mask) {
            GlStateManager.DEPTH.mask = mask;
            GL11.glDepthMask((boolean)mask);
        }
    }

    public static void _disableBlend() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.BLEND.capState.disable();
    }

    public static void _enableBlend() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.BLEND.capState.enable();
    }

    public static void _blendFunc(int srcFactor, int dstFactor) {
        RenderSystem.assertOnRenderThread();
        if (srcFactor != GlStateManager.BLEND.srcFactorRGB || dstFactor != GlStateManager.BLEND.dstFactorRGB) {
            GlStateManager.BLEND.srcFactorRGB = srcFactor;
            GlStateManager.BLEND.dstFactorRGB = dstFactor;
            GL11.glBlendFunc((int)srcFactor, (int)dstFactor);
        }
    }

    public static void _blendFuncSeparate(int srcFactorRGB, int dstFactorRGB, int srcFactorAlpha, int dstFactorAlpha) {
        RenderSystem.assertOnRenderThread();
        if (srcFactorRGB != GlStateManager.BLEND.srcFactorRGB || dstFactorRGB != GlStateManager.BLEND.dstFactorRGB || srcFactorAlpha != GlStateManager.BLEND.srcFactorAlpha || dstFactorAlpha != GlStateManager.BLEND.dstFactorAlpha) {
            GlStateManager.BLEND.srcFactorRGB = srcFactorRGB;
            GlStateManager.BLEND.dstFactorRGB = dstFactorRGB;
            GlStateManager.BLEND.srcFactorAlpha = srcFactorAlpha;
            GlStateManager.BLEND.dstFactorAlpha = dstFactorAlpha;
            GlStateManager.glBlendFuncSeparate(srcFactorRGB, dstFactorRGB, srcFactorAlpha, dstFactorAlpha);
        }
    }

    public static void _blendEquation(int mode) {
        RenderSystem.assertOnRenderThread();
        GL14.glBlendEquation((int)mode);
    }

    public static int glGetProgrami(int program, int pname) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetProgrami((int)program, (int)pname);
    }

    public static void glAttachShader(int program, int shader) {
        RenderSystem.assertOnRenderThread();
        GL20.glAttachShader((int)program, (int)shader);
    }

    public static void glDeleteShader(int shader) {
        RenderSystem.assertOnRenderThread();
        GL20.glDeleteShader((int)shader);
    }

    public static int glCreateShader(int type) {
        RenderSystem.assertOnRenderThread();
        return GL20.glCreateShader((int)type);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void glShaderSource(int shader, List<String> strings) {
        RenderSystem.assertOnRenderThread();
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strings) {
            stringBuilder.append(string);
        }
        byte[] bs = stringBuilder.toString().getBytes(Charsets.UTF_8);
        ByteBuffer byteBuffer = MemoryUtil.memAlloc((int)(bs.length + 1));
        byteBuffer.put(bs);
        byteBuffer.put((byte)0);
        byteBuffer.flip();
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
            pointerBuffer.put(byteBuffer);
            GL20C.nglShaderSource((int)shader, (int)1, (long)pointerBuffer.address0(), (long)0L);
        }
        finally {
            MemoryUtil.memFree((Buffer)byteBuffer);
        }
    }

    public static void glCompileShader(int shader) {
        RenderSystem.assertOnRenderThread();
        GL20.glCompileShader((int)shader);
    }

    public static int glGetShaderi(int shader, int pname) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetShaderi((int)shader, (int)pname);
    }

    public static void _glUseProgram(int program) {
        RenderSystem.assertOnRenderThread();
        GL20.glUseProgram((int)program);
    }

    public static int glCreateProgram() {
        RenderSystem.assertOnRenderThread();
        return GL20.glCreateProgram();
    }

    public static void glDeleteProgram(int program) {
        RenderSystem.assertOnRenderThread();
        GL20.glDeleteProgram((int)program);
    }

    public static void glLinkProgram(int program) {
        RenderSystem.assertOnRenderThread();
        GL20.glLinkProgram((int)program);
    }

    public static int _glGetUniformLocation(int program, CharSequence name) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetUniformLocation((int)program, (CharSequence)name);
    }

    public static void _glUniform1(int location, IntBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform1iv((int)location, (IntBuffer)value);
    }

    public static void _glUniform1i(int location, int value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform1i((int)location, (int)value);
    }

    public static void _glUniform1(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform1fv((int)location, (FloatBuffer)value);
    }

    public static void _glUniform2(int location, IntBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform2iv((int)location, (IntBuffer)value);
    }

    public static void _glUniform2(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform2fv((int)location, (FloatBuffer)value);
    }

    public static void _glUniform3(int location, IntBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform3iv((int)location, (IntBuffer)value);
    }

    public static void _glUniform3(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform3fv((int)location, (FloatBuffer)value);
    }

    public static void _glUniform4(int location, IntBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform4iv((int)location, (IntBuffer)value);
    }

    public static void _glUniform4(int location, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform4fv((int)location, (FloatBuffer)value);
    }

    public static void _glUniformMatrix2(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniformMatrix2fv((int)location, (boolean)transpose, (FloatBuffer)value);
    }

    public static void _glUniformMatrix3(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniformMatrix3fv((int)location, (boolean)transpose, (FloatBuffer)value);
    }

    public static void _glUniformMatrix4(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniformMatrix4fv((int)location, (boolean)transpose, (FloatBuffer)value);
    }

    public static int _glGetAttribLocation(int program, CharSequence name) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetAttribLocation((int)program, (CharSequence)name);
    }

    public static void _glBindAttribLocation(int program, int index, CharSequence name) {
        RenderSystem.assertOnRenderThread();
        GL20.glBindAttribLocation((int)program, (int)index, (CharSequence)name);
    }

    public static int _glGenBuffers() {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL15.glGenBuffers();
    }

    public static int _glGenVertexArrays() {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL30.glGenVertexArrays();
    }

    public static void _glBindBuffer(int target, int buffer) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL15.glBindBuffer((int)target, (int)buffer);
    }

    public static void _glBindVertexArray(int array) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glBindVertexArray((int)array);
    }

    public static void _glBufferData(int target, ByteBuffer data, int usage) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL15.glBufferData((int)target, (ByteBuffer)data, (int)usage);
    }

    public static void _glBufferData(int target, long size, int usage) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL15.glBufferData((int)target, (long)size, (int)usage);
    }

    @Nullable
    public static ByteBuffer mapBuffer(int target, int access) {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL15.glMapBuffer((int)target, (int)access);
    }

    public static void _glUnmapBuffer(int target) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL15.glUnmapBuffer((int)target);
    }

    public static void _glDeleteBuffers(int buffer) {
        RenderSystem.assertOnRenderThread();
        if (ON_LINUX) {
            GL32C.glBindBuffer((int)34962, (int)buffer);
            GL32C.glBufferData((int)34962, (long)0L, (int)35048);
            GL32C.glBindBuffer((int)34962, (int)0);
        }
        GL15.glDeleteBuffers((int)buffer);
    }

    public static void _glCopyTexSubImage2D(int target, int level, int xOffset, int yOffset, int x, int y, int width, int height) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL20.glCopyTexSubImage2D((int)target, (int)level, (int)xOffset, (int)yOffset, (int)x, (int)y, (int)width, (int)height);
    }

    public static void _glDeleteVertexArrays(int array) {
        RenderSystem.assertOnRenderThread();
        GL30.glDeleteVertexArrays((int)array);
    }

    public static void _glBindFramebuffer(int target, int framebuffer) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glBindFramebuffer((int)target, (int)framebuffer);
    }

    public static void _glBlitFrameBuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glBlitFramebuffer((int)srcX0, (int)srcY0, (int)srcX1, (int)srcY1, (int)dstX0, (int)dstY0, (int)dstX1, (int)dstY1, (int)mask, (int)filter);
    }

    public static void _glBindRenderbuffer(int target, int renderbuffer) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glBindRenderbuffer((int)target, (int)renderbuffer);
    }

    public static void _glDeleteRenderbuffers(int renderbuffer) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glDeleteRenderbuffers((int)renderbuffer);
    }

    public static void _glDeleteFramebuffers(int framebuffer) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glDeleteFramebuffers((int)framebuffer);
    }

    public static int glGenFramebuffers() {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL30.glGenFramebuffers();
    }

    public static int glGenRenderbuffers() {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL30.glGenRenderbuffers();
    }

    public static void _glRenderbufferStorage(int target, int internalFormat, int width, int height) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glRenderbufferStorage((int)target, (int)internalFormat, (int)width, (int)height);
    }

    public static void _glFramebufferRenderbuffer(int target, int attachment, int renderbufferTarget, int renderbuffer) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glFramebufferRenderbuffer((int)target, (int)attachment, (int)renderbufferTarget, (int)renderbuffer);
    }

    public static int glCheckFramebufferStatus(int target) {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL30.glCheckFramebufferStatus((int)target);
    }

    public static void _glFramebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glFramebufferTexture2D((int)target, (int)attachment, (int)textureTarget, (int)texture, (int)level);
    }

    public static int getBoundFramebuffer() {
        RenderSystem.assertOnRenderThread();
        return GlStateManager._getInteger(36006);
    }

    public static void glActiveTexture(int texture) {
        RenderSystem.assertOnRenderThread();
        GL13.glActiveTexture((int)texture);
    }

    public static void glBlendFuncSeparate(int srcFactorRGB, int dstFactorRGB, int srcFactorAlpha, int dstFactorAlpha) {
        RenderSystem.assertOnRenderThread();
        GL14.glBlendFuncSeparate((int)srcFactorRGB, (int)dstFactorRGB, (int)srcFactorAlpha, (int)dstFactorAlpha);
    }

    public static String glGetShaderInfoLog(int shader, int maxLength) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetShaderInfoLog((int)shader, (int)maxLength);
    }

    public static String glGetProgramInfoLog(int program, int maxLength) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetProgramInfoLog((int)program, (int)maxLength);
    }

    public static void setupLevelDiffuseLighting(Vector3f vector3f, Vector3f vector3f2, Matrix4f matrix4f) {
        RenderSystem.assertOnRenderThread();
        Vector4f vector4f = matrix4f.transform(new Vector4f((Vector3fc)vector3f, 1.0f));
        Vector4f vector4f2 = matrix4f.transform(new Vector4f((Vector3fc)vector3f2, 1.0f));
        RenderSystem.setShaderLights(new Vector3f(vector4f.x(), vector4f.y(), vector4f.z()), new Vector3f(vector4f2.x(), vector4f2.y(), vector4f2.z()));
    }

    public static void setupGuiFlatDiffuseLighting(Vector3f vector3f, Vector3f vector3f2) {
        RenderSystem.assertOnRenderThread();
        Matrix4f matrix4f = new Matrix4f().scaling(1.0f, -1.0f, 1.0f).rotateY(-0.3926991f).rotateX(2.3561945f);
        GlStateManager.setupLevelDiffuseLighting(vector3f, vector3f2, matrix4f);
    }

    public static void setupGui3DDiffuseLighting(Vector3f vector3f, Vector3f vector3f2) {
        RenderSystem.assertOnRenderThread();
        Matrix4f matrix4f = new Matrix4f().rotationYXZ(1.0821041f, 3.2375858f, 0.0f).rotateYXZ(-0.3926991f, 2.3561945f, 0.0f);
        GlStateManager.setupLevelDiffuseLighting(vector3f, vector3f2, matrix4f);
    }

    public static void _enableCull() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.CULL.capState.enable();
    }

    public static void _disableCull() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.CULL.capState.disable();
    }

    public static void _polygonMode(int face, int mode) {
        RenderSystem.assertOnRenderThread();
        GL11.glPolygonMode((int)face, (int)mode);
    }

    public static void _enablePolygonOffset() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.POLY_OFFSET.capFill.enable();
    }

    public static void _disablePolygonOffset() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.POLY_OFFSET.capFill.disable();
    }

    public static void _polygonOffset(float factor, float units) {
        RenderSystem.assertOnRenderThread();
        if (factor != GlStateManager.POLY_OFFSET.factor || units != GlStateManager.POLY_OFFSET.units) {
            GlStateManager.POLY_OFFSET.factor = factor;
            GlStateManager.POLY_OFFSET.units = units;
            GL11.glPolygonOffset((float)factor, (float)units);
        }
    }

    public static void _enableColorLogicOp() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.COLOR_LOGIC.capState.enable();
    }

    public static void _disableColorLogicOp() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.COLOR_LOGIC.capState.disable();
    }

    public static void _logicOp(int op) {
        RenderSystem.assertOnRenderThread();
        if (op != GlStateManager.COLOR_LOGIC.op) {
            GlStateManager.COLOR_LOGIC.op = op;
            GL11.glLogicOp((int)op);
        }
    }

    public static void _activeTexture(int texture) {
        RenderSystem.assertOnRenderThread();
        if (activeTexture != texture - 33984) {
            activeTexture = texture - 33984;
            GlStateManager.glActiveTexture(texture);
        }
    }

    public static void _texParameter(int target, int pname, float param) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glTexParameterf((int)target, (int)pname, (float)param);
    }

    public static void _texParameter(int target, int pname, int param) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glTexParameteri((int)target, (int)pname, (int)param);
    }

    public static int _getTexLevelParameter(int target, int level, int pname) {
        RenderSystem.assertInInitPhase();
        return GL11.glGetTexLevelParameteri((int)target, (int)level, (int)pname);
    }

    public static int _genTexture() {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL11.glGenTextures();
    }

    public static void _genTextures(int[] textures) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glGenTextures((int[])textures);
    }

    public static void _deleteTexture(int texture) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glDeleteTextures((int)texture);
        for (Texture2DState texture2DState : TEXTURES) {
            if (texture2DState.boundTexture != texture) continue;
            texture2DState.boundTexture = -1;
        }
    }

    public static void _deleteTextures(int[] textures) {
        RenderSystem.assertOnRenderThreadOrInit();
        for (Texture2DState texture2DState : TEXTURES) {
            for (int i : textures) {
                if (texture2DState.boundTexture != i) continue;
                texture2DState.boundTexture = -1;
            }
        }
        GL11.glDeleteTextures((int[])textures);
    }

    public static void _bindTexture(int texture) {
        RenderSystem.assertOnRenderThreadOrInit();
        if (texture != GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture) {
            GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture = texture;
            GL11.glBindTexture((int)3553, (int)texture);
        }
    }

    public static int _getActiveTexture() {
        return activeTexture + 33984;
    }

    public static void _texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, @Nullable IntBuffer pixels) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glTexImage2D((int)target, (int)level, (int)internalFormat, (int)width, (int)height, (int)border, (int)format, (int)type, (IntBuffer)pixels);
    }

    public static void _texSubImage2D(int target, int level, int offsetX, int offsetY, int width, int height, int format, int type, long pixels) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glTexSubImage2D((int)target, (int)level, (int)offsetX, (int)offsetY, (int)width, (int)height, (int)format, (int)type, (long)pixels);
    }

    public static void upload(int i, int j, int k, int l, int m, NativeImage.Format format, IntBuffer intBuffer) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> GlStateManager._upload(i, j, k, l, m, format, intBuffer));
        } else {
            GlStateManager._upload(i, j, k, l, m, format, intBuffer);
        }
    }

    private static void _upload(int i, int j, int k, int l, int m, NativeImage.Format format, IntBuffer intBuffer) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._pixelStore(3314, l);
        GlStateManager._pixelStore(3316, 0);
        GlStateManager._pixelStore(3315, 0);
        format.setUnpackAlignment();
        GL11.glTexSubImage2D((int)3553, (int)i, (int)j, (int)k, (int)l, (int)m, (int)format.toGl(), (int)5121, (IntBuffer)intBuffer);
    }

    public static void _getTexImage(int target, int level, int format, int type, long pixels) {
        RenderSystem.assertOnRenderThread();
        GL11.glGetTexImage((int)target, (int)level, (int)format, (int)type, (long)pixels);
    }

    public static void _viewport(int x, int y, int width, int height) {
        RenderSystem.assertOnRenderThreadOrInit();
        Viewport.INSTANCE.x = x;
        Viewport.INSTANCE.y = y;
        Viewport.INSTANCE.width = width;
        Viewport.INSTANCE.height = height;
        GL11.glViewport((int)x, (int)y, (int)width, (int)height);
    }

    public static void _colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        RenderSystem.assertOnRenderThread();
        if (red != GlStateManager.COLOR_MASK.red || green != GlStateManager.COLOR_MASK.green || blue != GlStateManager.COLOR_MASK.blue || alpha != GlStateManager.COLOR_MASK.alpha) {
            GlStateManager.COLOR_MASK.red = red;
            GlStateManager.COLOR_MASK.green = green;
            GlStateManager.COLOR_MASK.blue = blue;
            GlStateManager.COLOR_MASK.alpha = alpha;
            GL11.glColorMask((boolean)red, (boolean)green, (boolean)blue, (boolean)alpha);
        }
    }

    public static void _stencilFunc(int func, int ref, int mask) {
        RenderSystem.assertOnRenderThread();
        if (func != GlStateManager.STENCIL.subState.func || func != GlStateManager.STENCIL.subState.ref || func != GlStateManager.STENCIL.subState.mask) {
            GlStateManager.STENCIL.subState.func = func;
            GlStateManager.STENCIL.subState.ref = ref;
            GlStateManager.STENCIL.subState.mask = mask;
            GL11.glStencilFunc((int)func, (int)ref, (int)mask);
        }
    }

    public static void _stencilMask(int mask) {
        RenderSystem.assertOnRenderThread();
        if (mask != GlStateManager.STENCIL.mask) {
            GlStateManager.STENCIL.mask = mask;
            GL11.glStencilMask((int)mask);
        }
    }

    public static void _stencilOp(int sfail, int dpfail, int dppass) {
        RenderSystem.assertOnRenderThread();
        if (sfail != GlStateManager.STENCIL.sfail || dpfail != GlStateManager.STENCIL.dpfail || dppass != GlStateManager.STENCIL.dppass) {
            GlStateManager.STENCIL.sfail = sfail;
            GlStateManager.STENCIL.dpfail = dpfail;
            GlStateManager.STENCIL.dppass = dppass;
            GL11.glStencilOp((int)sfail, (int)dpfail, (int)dppass);
        }
    }

    public static void _clearDepth(double depth) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glClearDepth((double)depth);
    }

    public static void _clearColor(float red, float green, float blue, float alpha) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glClearColor((float)red, (float)green, (float)blue, (float)alpha);
    }

    public static void _clearStencil(int stencil) {
        RenderSystem.assertOnRenderThread();
        GL11.glClearStencil((int)stencil);
    }

    public static void _clear(int mask, boolean getError) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glClear((int)mask);
        if (getError) {
            GlStateManager._getError();
        }
    }

    public static void _glDrawPixels(int width, int height, int format, int type, long pixels) {
        RenderSystem.assertOnRenderThread();
        GL11.glDrawPixels((int)width, (int)height, (int)format, (int)type, (long)pixels);
    }

    public static void _vertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer) {
        RenderSystem.assertOnRenderThread();
        GL20.glVertexAttribPointer((int)index, (int)size, (int)type, (boolean)normalized, (int)stride, (long)pointer);
    }

    public static void _vertexAttribIPointer(int index, int size, int type, int stride, long pointer) {
        RenderSystem.assertOnRenderThread();
        GL30.glVertexAttribIPointer((int)index, (int)size, (int)type, (int)stride, (long)pointer);
    }

    public static void _enableVertexAttribArray(int index) {
        RenderSystem.assertOnRenderThread();
        GL20.glEnableVertexAttribArray((int)index);
    }

    public static void _disableVertexAttribArray(int index) {
        RenderSystem.assertOnRenderThread();
        GL20.glDisableVertexAttribArray((int)index);
    }

    public static void _drawElements(int mode, int count, int type, long indices) {
        RenderSystem.assertOnRenderThread();
        GL11.glDrawElements((int)mode, (int)count, (int)type, (long)indices);
    }

    public static void _pixelStore(int pname, int param) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL11.glPixelStorei((int)pname, (int)param);
    }

    public static void _readPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
        RenderSystem.assertOnRenderThread();
        GL11.glReadPixels((int)x, (int)y, (int)width, (int)height, (int)format, (int)type, (ByteBuffer)pixels);
    }

    public static void _readPixels(int x, int y, int width, int height, int format, int type, long pixels) {
        RenderSystem.assertOnRenderThread();
        GL11.glReadPixels((int)x, (int)y, (int)width, (int)height, (int)format, (int)type, (long)pixels);
    }

    public static int _getError() {
        RenderSystem.assertOnRenderThread();
        return GL11.glGetError();
    }

    public static String _getString(int name) {
        RenderSystem.assertOnRenderThread();
        return GL11.glGetString((int)name);
    }

    public static int _getInteger(int pname) {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL11.glGetInteger((int)pname);
    }

    static {
        TEXTURES = (Texture2DState[])IntStream.range(0, 12).mapToObj(i -> new Texture2DState()).toArray(Texture2DState[]::new);
        COLOR_MASK = new ColorMask();
    }

    @Environment(value=EnvType.CLIENT)
    static class ScissorTestState {
        public final CapabilityTracker capState = new CapabilityTracker(3089);

        ScissorTestState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CapabilityTracker {
        private final int cap;
        private boolean state;

        public CapabilityTracker(int cap) {
            this.cap = cap;
        }

        public void disable() {
            this.setState(false);
        }

        public void enable() {
            this.setState(true);
        }

        public void setState(boolean state) {
            RenderSystem.assertOnRenderThreadOrInit();
            if (state != this.state) {
                this.state = state;
                if (state) {
                    GL11.glEnable((int)this.cap);
                } else {
                    GL11.glDisable((int)this.cap);
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class DepthTestState {
        public final CapabilityTracker capState = new CapabilityTracker(2929);
        public boolean mask = true;
        public int func = 513;

        DepthTestState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class BlendFuncState {
        public final CapabilityTracker capState = new CapabilityTracker(3042);
        public int srcFactorRGB = 1;
        public int dstFactorRGB = 0;
        public int srcFactorAlpha = 1;
        public int dstFactorAlpha = 0;

        BlendFuncState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CullFaceState {
        public final CapabilityTracker capState = new CapabilityTracker(2884);
        public int mode = 1029;

        CullFaceState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class PolygonOffsetState {
        public final CapabilityTracker capFill = new CapabilityTracker(32823);
        public final CapabilityTracker capLine = new CapabilityTracker(10754);
        public float factor;
        public float units;

        PolygonOffsetState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class LogicOpState {
        public final CapabilityTracker capState = new CapabilityTracker(3058);
        public int op = 5379;

        LogicOpState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Texture2DState {
        public int boundTexture;

        Texture2DState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Viewport
    extends Enum<Viewport> {
        public static final /* enum */ Viewport INSTANCE = new Viewport();
        protected int x;
        protected int y;
        protected int width;
        protected int height;
        private static final /* synthetic */ Viewport[] field_5173;

        public static Viewport[] values() {
            return (Viewport[])field_5173.clone();
        }

        public static Viewport valueOf(String string) {
            return Enum.valueOf(Viewport.class, string);
        }

        public static int getX() {
            return Viewport.INSTANCE.x;
        }

        public static int getY() {
            return Viewport.INSTANCE.y;
        }

        public static int getWidth() {
            return Viewport.INSTANCE.width;
        }

        public static int getHeight() {
            return Viewport.INSTANCE.height;
        }

        private static /* synthetic */ Viewport[] method_36749() {
            return new Viewport[]{INSTANCE};
        }

        static {
            field_5173 = Viewport.method_36749();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ColorMask {
        public boolean red = true;
        public boolean green = true;
        public boolean blue = true;
        public boolean alpha = true;

        ColorMask() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class StencilState {
        public final StencilSubState subState = new StencilSubState();
        public int mask = -1;
        public int sfail = 7680;
        public int dpfail = 7680;
        public int dppass = 7680;

        StencilState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class StencilSubState {
        public int func = 519;
        public int ref;
        public int mask = -1;

        StencilSubState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    @DeobfuscateClass
    public static enum DstFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_COLOR(768),
        ZERO(0);

        public final int value;

        private DstFactor(int value) {
            this.value = value;
        }
    }

    @Environment(value=EnvType.CLIENT)
    @DeobfuscateClass
    public static enum SrcFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_ALPHA_SATURATE(776),
        SRC_COLOR(768),
        ZERO(0);

        public final int value;

        private SrcFactor(int value) {
            this.value = value;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class LogicOp
    extends Enum<LogicOp> {
        public static final /* enum */ LogicOp AND = new LogicOp(5377);
        public static final /* enum */ LogicOp AND_INVERTED = new LogicOp(5380);
        public static final /* enum */ LogicOp AND_REVERSE = new LogicOp(5378);
        public static final /* enum */ LogicOp CLEAR = new LogicOp(5376);
        public static final /* enum */ LogicOp COPY = new LogicOp(5379);
        public static final /* enum */ LogicOp COPY_INVERTED = new LogicOp(5388);
        public static final /* enum */ LogicOp EQUIV = new LogicOp(5385);
        public static final /* enum */ LogicOp INVERT = new LogicOp(5386);
        public static final /* enum */ LogicOp NAND = new LogicOp(5390);
        public static final /* enum */ LogicOp NOOP = new LogicOp(5381);
        public static final /* enum */ LogicOp NOR = new LogicOp(5384);
        public static final /* enum */ LogicOp OR = new LogicOp(5383);
        public static final /* enum */ LogicOp OR_INVERTED = new LogicOp(5389);
        public static final /* enum */ LogicOp OR_REVERSE = new LogicOp(5387);
        public static final /* enum */ LogicOp SET = new LogicOp(5391);
        public static final /* enum */ LogicOp XOR = new LogicOp(5382);
        public final int value;
        private static final /* synthetic */ LogicOp[] field_5106;

        public static LogicOp[] values() {
            return (LogicOp[])field_5106.clone();
        }

        public static LogicOp valueOf(String string) {
            return Enum.valueOf(LogicOp.class, string);
        }

        private LogicOp(int value) {
            this.value = value;
        }

        private static /* synthetic */ LogicOp[] method_36748() {
            return new LogicOp[]{AND, AND_INVERTED, AND_REVERSE, CLEAR, COPY, COPY_INVERTED, EQUIV, INVERT, NAND, NOOP, NOR, OR, OR_INVERTED, OR_REVERSE, SET, XOR};
        }

        static {
            field_5106 = LogicOp.method_36748();
        }
    }
}

