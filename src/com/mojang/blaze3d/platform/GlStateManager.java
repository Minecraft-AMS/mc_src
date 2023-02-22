/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL14
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.platform.GLX;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.Untracker;
import net.minecraft.client.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class GlStateManager {
    private static final int LIGHT_COUNT = 8;
    private static final int TEXTURE_COUNT = 8;
    private static final FloatBuffer MATRIX_BUFFER = GLX.make(MemoryUtil.memAllocFloat((int)16), floatBuffer -> Untracker.untrack(MemoryUtil.memAddress((FloatBuffer)floatBuffer)));
    private static final FloatBuffer COLOR_BUFFER = GLX.make(MemoryUtil.memAllocFloat((int)4), floatBuffer -> Untracker.untrack(MemoryUtil.memAddress((FloatBuffer)floatBuffer)));
    private static final AlphaTestState ALPHA_TEST = new AlphaTestState();
    private static final CapabilityTracker LIGHTING = new CapabilityTracker(2896);
    private static final CapabilityTracker[] LIGHT_ENABLE = (CapabilityTracker[])IntStream.range(0, 8).mapToObj(i -> new CapabilityTracker(16384 + i)).toArray(CapabilityTracker[]::new);
    private static final ColorMaterialState COLOR_MATERIAL = new ColorMaterialState();
    private static final BlendFuncState BLEND = new BlendFuncState();
    private static final DepthTestState DEPTH = new DepthTestState();
    private static final FogState FOG = new FogState();
    private static final CullFaceState CULL = new CullFaceState();
    private static final PolygonOffsetState POLY_OFFSET = new PolygonOffsetState();
    private static final LogicOpState COLOR_LOGIC = new LogicOpState();
    private static final TexGenState TEX_GEN = new TexGenState();
    private static final ClearState CLEAR = new ClearState();
    private static final StencilState STENCIL = new StencilState();
    private static final CapabilityTracker NORMALIZE = new CapabilityTracker(2977);
    private static int activeTexture;
    private static final Texture2DState[] TEXTURES;
    private static int shadeModel;
    private static final CapabilityTracker RESCALE_NORMAL;
    private static final ColorMask COLOR_MASK;
    private static final Color4 COLOR;
    private static final float DEFAULTALPHACUTOFF = 0.1f;

    public static void pushLightingAttributes() {
        GL11.glPushAttrib((int)8256);
    }

    public static void pushTextureAttributes() {
        GL11.glPushAttrib((int)270336);
    }

    public static void popAttributes() {
        GL11.glPopAttrib();
    }

    public static void disableAlphaTest() {
        GlStateManager.ALPHA_TEST.capState.disable();
    }

    public static void enableAlphaTest() {
        GlStateManager.ALPHA_TEST.capState.enable();
    }

    public static void alphaFunc(int func, float ref) {
        if (func != GlStateManager.ALPHA_TEST.func || ref != GlStateManager.ALPHA_TEST.ref) {
            GlStateManager.ALPHA_TEST.func = func;
            GlStateManager.ALPHA_TEST.ref = ref;
            GL11.glAlphaFunc((int)func, (float)ref);
        }
    }

    public static void enableLighting() {
        LIGHTING.enable();
    }

    public static void disableLighting() {
        LIGHTING.disable();
    }

    public static void enableLight(int i) {
        LIGHT_ENABLE[i].enable();
    }

    public static void disableLight(int i) {
        LIGHT_ENABLE[i].disable();
    }

    public static void enableColorMaterial() {
        GlStateManager.COLOR_MATERIAL.capState.enable();
    }

    public static void disableColorMaterial() {
        GlStateManager.COLOR_MATERIAL.capState.disable();
    }

    public static void colorMaterial(int face, int mode) {
        if (face != GlStateManager.COLOR_MATERIAL.face || mode != GlStateManager.COLOR_MATERIAL.mode) {
            GlStateManager.COLOR_MATERIAL.face = face;
            GlStateManager.COLOR_MATERIAL.mode = mode;
            GL11.glColorMaterial((int)face, (int)mode);
        }
    }

    public static void light(int light, int pname, FloatBuffer params) {
        GL11.glLightfv((int)light, (int)pname, (FloatBuffer)params);
    }

    public static void lightModel(int pname, FloatBuffer params) {
        GL11.glLightModelfv((int)pname, (FloatBuffer)params);
    }

    public static void normal3f(float nx, float ny, float nz) {
        GL11.glNormal3f((float)nx, (float)ny, (float)nz);
    }

    public static void disableDepthTest() {
        GlStateManager.DEPTH.capState.disable();
    }

    public static void enableDepthTest() {
        GlStateManager.DEPTH.capState.enable();
    }

    public static void depthFunc(int func) {
        if (func != GlStateManager.DEPTH.func) {
            GlStateManager.DEPTH.func = func;
            GL11.glDepthFunc((int)func);
        }
    }

    public static void depthMask(boolean mask) {
        if (mask != GlStateManager.DEPTH.mask) {
            GlStateManager.DEPTH.mask = mask;
            GL11.glDepthMask((boolean)mask);
        }
    }

    public static void disableBlend() {
        GlStateManager.BLEND.capState.disable();
    }

    public static void enableBlend() {
        GlStateManager.BLEND.capState.enable();
    }

    public static void blendFunc(SourceFactor sourceFactor, DestFactor destFactor) {
        GlStateManager.blendFunc(sourceFactor.value, destFactor.value);
    }

    public static void blendFunc(int sfactor, int dfactor) {
        if (sfactor != GlStateManager.BLEND.sfactor || dfactor != GlStateManager.BLEND.dfactor) {
            GlStateManager.BLEND.sfactor = sfactor;
            GlStateManager.BLEND.dfactor = dfactor;
            GL11.glBlendFunc((int)sfactor, (int)dfactor);
        }
    }

    public static void blendFuncSeparate(SourceFactor sourceFactor, DestFactor destFactor, SourceFactor sourceFactor2, DestFactor destFactor2) {
        GlStateManager.blendFuncSeparate(sourceFactor.value, destFactor.value, sourceFactor2.value, destFactor2.value);
    }

    public static void blendFuncSeparate(int sFactorRGB, int dFactorRGB, int sFactorAlpha, int dFactorAlpha) {
        if (sFactorRGB != GlStateManager.BLEND.sfactor || dFactorRGB != GlStateManager.BLEND.dfactor || sFactorAlpha != GlStateManager.BLEND.srcAlpha || dFactorAlpha != GlStateManager.BLEND.dstAlpha) {
            GlStateManager.BLEND.sfactor = sFactorRGB;
            GlStateManager.BLEND.dfactor = dFactorRGB;
            GlStateManager.BLEND.srcAlpha = sFactorAlpha;
            GlStateManager.BLEND.dstAlpha = dFactorAlpha;
            GLX.glBlendFuncSeparate(sFactorRGB, dFactorRGB, sFactorAlpha, dFactorAlpha);
        }
    }

    public static void blendEquation(int mode) {
        GL14.glBlendEquation((int)mode);
    }

    public static void setupSolidRenderingTextureCombine(int color) {
        COLOR_BUFFER.put(0, (float)(color >> 16 & 0xFF) / 255.0f);
        COLOR_BUFFER.put(1, (float)(color >> 8 & 0xFF) / 255.0f);
        COLOR_BUFFER.put(2, (float)(color >> 0 & 0xFF) / 255.0f);
        COLOR_BUFFER.put(3, (float)(color >> 24 & 0xFF) / 255.0f);
        GlStateManager.texEnv(8960, 8705, COLOR_BUFFER);
        GlStateManager.texEnv(8960, 8704, 34160);
        GlStateManager.texEnv(8960, 34161, 7681);
        GlStateManager.texEnv(8960, 34176, 34166);
        GlStateManager.texEnv(8960, 34192, 768);
        GlStateManager.texEnv(8960, 34162, 7681);
        GlStateManager.texEnv(8960, 34184, 5890);
        GlStateManager.texEnv(8960, 34200, 770);
    }

    public static void tearDownSolidRenderingTextureCombine() {
        GlStateManager.texEnv(8960, 8704, 8448);
        GlStateManager.texEnv(8960, 34161, 8448);
        GlStateManager.texEnv(8960, 34162, 8448);
        GlStateManager.texEnv(8960, 34176, 5890);
        GlStateManager.texEnv(8960, 34184, 5890);
        GlStateManager.texEnv(8960, 34192, 768);
        GlStateManager.texEnv(8960, 34200, 770);
    }

    public static void enableFog() {
        GlStateManager.FOG.capState.enable();
    }

    public static void disableFog() {
        GlStateManager.FOG.capState.disable();
    }

    public static void fogMode(FogMode fogMode) {
        GlStateManager.fogMode(fogMode.glValue);
    }

    private static void fogMode(int i) {
        if (i != GlStateManager.FOG.mode) {
            GlStateManager.FOG.mode = i;
            GL11.glFogi((int)2917, (int)i);
        }
    }

    public static void fogDensity(float f) {
        if (f != GlStateManager.FOG.density) {
            GlStateManager.FOG.density = f;
            GL11.glFogf((int)2914, (float)f);
        }
    }

    public static void fogStart(float f) {
        if (f != GlStateManager.FOG.start) {
            GlStateManager.FOG.start = f;
            GL11.glFogf((int)2915, (float)f);
        }
    }

    public static void fogEnd(float f) {
        if (f != GlStateManager.FOG.end) {
            GlStateManager.FOG.end = f;
            GL11.glFogf((int)2916, (float)f);
        }
    }

    public static void fog(int i, FloatBuffer floatBuffer) {
        GL11.glFogfv((int)i, (FloatBuffer)floatBuffer);
    }

    public static void fogi(int i, int j) {
        GL11.glFogi((int)i, (int)j);
    }

    public static void enableCull() {
        GlStateManager.CULL.capState.enable();
    }

    public static void disableCull() {
        GlStateManager.CULL.capState.disable();
    }

    public static void cullFace(FaceSides faceSides) {
        GlStateManager.cullFace(faceSides.glValue);
    }

    private static void cullFace(int i) {
        if (i != GlStateManager.CULL.mode) {
            GlStateManager.CULL.mode = i;
            GL11.glCullFace((int)i);
        }
    }

    public static void polygonMode(int i, int j) {
        GL11.glPolygonMode((int)i, (int)j);
    }

    public static void enablePolygonOffset() {
        GlStateManager.POLY_OFFSET.capFill.enable();
    }

    public static void disablePolygonOffset() {
        GlStateManager.POLY_OFFSET.capFill.disable();
    }

    public static void enableLineOffset() {
        GlStateManager.POLY_OFFSET.capLine.enable();
    }

    public static void disableLineOffset() {
        GlStateManager.POLY_OFFSET.capLine.disable();
    }

    public static void polygonOffset(float factor, float units) {
        if (factor != GlStateManager.POLY_OFFSET.factor || units != GlStateManager.POLY_OFFSET.units) {
            GlStateManager.POLY_OFFSET.factor = factor;
            GlStateManager.POLY_OFFSET.units = units;
            GL11.glPolygonOffset((float)factor, (float)units);
        }
    }

    public static void enableColorLogicOp() {
        GlStateManager.COLOR_LOGIC.capState.enable();
    }

    public static void disableColorLogicOp() {
        GlStateManager.COLOR_LOGIC.capState.disable();
    }

    public static void logicOp(LogicOp logicOp) {
        GlStateManager.logicOp(logicOp.glValue);
    }

    public static void logicOp(int i) {
        if (i != GlStateManager.COLOR_LOGIC.opcode) {
            GlStateManager.COLOR_LOGIC.opcode = i;
            GL11.glLogicOp((int)i);
        }
    }

    public static void enableTexGen(TexCoord texCoord) {
        GlStateManager.getTexGen((TexCoord)texCoord).capState.enable();
    }

    public static void disableTexGen(TexCoord texCoord) {
        GlStateManager.getTexGen((TexCoord)texCoord).capState.disable();
    }

    public static void texGenMode(TexCoord texCoord, int i) {
        TexGenCoordState texGenCoordState = GlStateManager.getTexGen(texCoord);
        if (i != texGenCoordState.mode) {
            texGenCoordState.mode = i;
            GL11.glTexGeni((int)texGenCoordState.coord, (int)9472, (int)i);
        }
    }

    public static void texGenParam(TexCoord texCoord, int i, FloatBuffer floatBuffer) {
        GL11.glTexGenfv((int)GlStateManager.getTexGen((TexCoord)texCoord).coord, (int)i, (FloatBuffer)floatBuffer);
    }

    private static TexGenCoordState getTexGen(TexCoord texCoord) {
        switch (texCoord) {
            case S: {
                return GlStateManager.TEX_GEN.s;
            }
            case T: {
                return GlStateManager.TEX_GEN.t;
            }
            case R: {
                return GlStateManager.TEX_GEN.r;
            }
            case Q: {
                return GlStateManager.TEX_GEN.q;
            }
        }
        return GlStateManager.TEX_GEN.s;
    }

    public static void activeTexture(int i) {
        if (activeTexture != i - GLX.GL_TEXTURE0) {
            activeTexture = i - GLX.GL_TEXTURE0;
            GLX.glActiveTexture(i);
        }
    }

    public static void enableTexture() {
        GlStateManager.TEXTURES[GlStateManager.activeTexture].capState.enable();
    }

    public static void disableTexture() {
        GlStateManager.TEXTURES[GlStateManager.activeTexture].capState.disable();
    }

    public static void texEnv(int i, int j, FloatBuffer floatBuffer) {
        GL11.glTexEnvfv((int)i, (int)j, (FloatBuffer)floatBuffer);
    }

    public static void texEnv(int i, int j, int k) {
        GL11.glTexEnvi((int)i, (int)j, (int)k);
    }

    public static void texEnv(int i, int j, float f) {
        GL11.glTexEnvf((int)i, (int)j, (float)f);
    }

    public static void texParameter(int i, int j, float f) {
        GL11.glTexParameterf((int)i, (int)j, (float)f);
    }

    public static void texParameter(int i, int j, int k) {
        GL11.glTexParameteri((int)i, (int)j, (int)k);
    }

    public static int getTexLevelParameter(int i, int j, int k) {
        return GL11.glGetTexLevelParameteri((int)i, (int)j, (int)k);
    }

    public static int genTexture() {
        return GL11.glGenTextures();
    }

    public static void deleteTexture(int i) {
        GL11.glDeleteTextures((int)i);
        for (Texture2DState texture2DState : TEXTURES) {
            if (texture2DState.boundTexture != i) continue;
            texture2DState.boundTexture = -1;
        }
    }

    public static void bindTexture(int texture) {
        if (texture != GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture) {
            GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture = texture;
            GL11.glBindTexture((int)3553, (int)texture);
        }
    }

    public static void texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, @Nullable IntBuffer pixels) {
        GL11.glTexImage2D((int)target, (int)level, (int)internalFormat, (int)width, (int)height, (int)border, (int)format, (int)type, (IntBuffer)pixels);
    }

    public static void texSubImage2D(int target, int level, int xOffset, int yOffset, int width, int height, int format, int type, long pixels) {
        GL11.glTexSubImage2D((int)target, (int)level, (int)xOffset, (int)yOffset, (int)width, (int)height, (int)format, (int)type, (long)pixels);
    }

    public static void copyTexSubImage2D(int i, int j, int k, int l, int m, int n, int o, int p) {
        GL11.glCopyTexSubImage2D((int)i, (int)j, (int)k, (int)l, (int)m, (int)n, (int)o, (int)p);
    }

    public static void getTexImage(int i, int j, int k, int l, long m) {
        GL11.glGetTexImage((int)i, (int)j, (int)k, (int)l, (long)m);
    }

    public static void enableNormalize() {
        NORMALIZE.enable();
    }

    public static void disableNormalize() {
        NORMALIZE.disable();
    }

    public static void shadeModel(int i) {
        if (i != shadeModel) {
            shadeModel = i;
            GL11.glShadeModel((int)i);
        }
    }

    public static void enableRescaleNormal() {
        RESCALE_NORMAL.enable();
    }

    public static void disableRescaleNormal() {
        RESCALE_NORMAL.disable();
    }

    public static void viewport(int i, int j, int k, int l) {
        Viewport.INSTANCE.x = i;
        Viewport.INSTANCE.y = j;
        Viewport.INSTANCE.width = k;
        Viewport.INSTANCE.height = l;
        GL11.glViewport((int)i, (int)j, (int)k, (int)l);
    }

    public static void colorMask(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        if (bl != GlStateManager.COLOR_MASK.red || bl2 != GlStateManager.COLOR_MASK.green || bl3 != GlStateManager.COLOR_MASK.blue || bl4 != GlStateManager.COLOR_MASK.alpha) {
            GlStateManager.COLOR_MASK.red = bl;
            GlStateManager.COLOR_MASK.green = bl2;
            GlStateManager.COLOR_MASK.blue = bl3;
            GlStateManager.COLOR_MASK.alpha = bl4;
            GL11.glColorMask((boolean)bl, (boolean)bl2, (boolean)bl3, (boolean)bl4);
        }
    }

    public static void stencilFunc(int i, int j, int k) {
        if (i != GlStateManager.STENCIL.subState.func || i != GlStateManager.STENCIL.subState.field_16203 || i != GlStateManager.STENCIL.subState.field_5147) {
            GlStateManager.STENCIL.subState.func = i;
            GlStateManager.STENCIL.subState.field_16203 = j;
            GlStateManager.STENCIL.subState.field_5147 = k;
            GL11.glStencilFunc((int)i, (int)j, (int)k);
        }
    }

    public static void stencilMask(int i) {
        if (i != GlStateManager.STENCIL.field_5153) {
            GlStateManager.STENCIL.field_5153 = i;
            GL11.glStencilMask((int)i);
        }
    }

    public static void stencilOp(int i, int j, int k) {
        if (i != GlStateManager.STENCIL.field_5152 || j != GlStateManager.STENCIL.field_5151 || k != GlStateManager.STENCIL.field_5150) {
            GlStateManager.STENCIL.field_5152 = i;
            GlStateManager.STENCIL.field_5151 = j;
            GlStateManager.STENCIL.field_5150 = k;
            GL11.glStencilOp((int)i, (int)j, (int)k);
        }
    }

    public static void clearDepth(double d) {
        if (d != GlStateManager.CLEAR.clearDepth) {
            GlStateManager.CLEAR.clearDepth = d;
            GL11.glClearDepth((double)d);
        }
    }

    public static void clearColor(float f, float g, float h, float i) {
        if (f != GlStateManager.CLEAR.clearColor.red || g != GlStateManager.CLEAR.clearColor.green || h != GlStateManager.CLEAR.clearColor.blue || i != GlStateManager.CLEAR.clearColor.alpha) {
            GlStateManager.CLEAR.clearColor.red = f;
            GlStateManager.CLEAR.clearColor.green = g;
            GlStateManager.CLEAR.clearColor.blue = h;
            GlStateManager.CLEAR.clearColor.alpha = i;
            GL11.glClearColor((float)f, (float)g, (float)h, (float)i);
        }
    }

    public static void clearStencil(int i) {
        if (i != GlStateManager.CLEAR.field_16202) {
            GlStateManager.CLEAR.field_16202 = i;
            GL11.glClearStencil((int)i);
        }
    }

    public static void clear(int i, boolean bl) {
        GL11.glClear((int)i);
        if (bl) {
            GlStateManager.getError();
        }
    }

    public static void matrixMode(int i) {
        GL11.glMatrixMode((int)i);
    }

    public static void loadIdentity() {
        GL11.glLoadIdentity();
    }

    public static void pushMatrix() {
        GL11.glPushMatrix();
    }

    public static void popMatrix() {
        GL11.glPopMatrix();
    }

    public static void getMatrix(int i, FloatBuffer floatBuffer) {
        GL11.glGetFloatv((int)i, (FloatBuffer)floatBuffer);
    }

    public static Matrix4f getMatrix4f(int i) {
        GL11.glGetFloatv((int)i, (FloatBuffer)MATRIX_BUFFER);
        MATRIX_BUFFER.rewind();
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setFromBuffer(MATRIX_BUFFER);
        MATRIX_BUFFER.rewind();
        return matrix4f;
    }

    public static void ortho(double d, double e, double f, double g, double h, double i) {
        GL11.glOrtho((double)d, (double)e, (double)f, (double)g, (double)h, (double)i);
    }

    public static void rotatef(float angle, float x, float y, float z) {
        GL11.glRotatef((float)angle, (float)x, (float)y, (float)z);
    }

    public static void rotated(double d, double e, double f, double g) {
        GL11.glRotated((double)d, (double)e, (double)f, (double)g);
    }

    public static void scalef(float f, float g, float h) {
        GL11.glScalef((float)f, (float)g, (float)h);
    }

    public static void scaled(double d, double e, double f) {
        GL11.glScaled((double)d, (double)e, (double)f);
    }

    public static void translatef(float f, float g, float h) {
        GL11.glTranslatef((float)f, (float)g, (float)h);
    }

    public static void translated(double d, double e, double f) {
        GL11.glTranslated((double)d, (double)e, (double)f);
    }

    public static void multMatrix(FloatBuffer floatBuffer) {
        GL11.glMultMatrixf((FloatBuffer)floatBuffer);
    }

    public static void multMatrix(Matrix4f matrix4f) {
        matrix4f.writeToBuffer(MATRIX_BUFFER);
        MATRIX_BUFFER.rewind();
        GL11.glMultMatrixf((FloatBuffer)MATRIX_BUFFER);
    }

    public static void color4f(float red, float green, float blue, float alpha) {
        if (red != GlStateManager.COLOR.red || green != GlStateManager.COLOR.green || blue != GlStateManager.COLOR.blue || alpha != GlStateManager.COLOR.alpha) {
            GlStateManager.COLOR.red = red;
            GlStateManager.COLOR.green = green;
            GlStateManager.COLOR.blue = blue;
            GlStateManager.COLOR.alpha = alpha;
            GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        }
    }

    public static void color3f(float red, float green, float blue) {
        GlStateManager.color4f(red, green, blue, 1.0f);
    }

    public static void texCoord2f(float f, float g) {
        GL11.glTexCoord2f((float)f, (float)g);
    }

    public static void vertex3f(float f, float g, float h) {
        GL11.glVertex3f((float)f, (float)g, (float)h);
    }

    public static void clearCurrentColor() {
        GlStateManager.COLOR.red = -1.0f;
        GlStateManager.COLOR.green = -1.0f;
        GlStateManager.COLOR.blue = -1.0f;
        GlStateManager.COLOR.alpha = -1.0f;
    }

    public static void normalPointer(int i, int j, int k) {
        GL11.glNormalPointer((int)i, (int)j, (long)k);
    }

    public static void normalPointer(int i, int j, ByteBuffer byteBuffer) {
        GL11.glNormalPointer((int)i, (int)j, (ByteBuffer)byteBuffer);
    }

    public static void texCoordPointer(int i, int j, int k, int l) {
        GL11.glTexCoordPointer((int)i, (int)j, (int)k, (long)l);
    }

    public static void texCoordPointer(int i, int j, int k, ByteBuffer byteBuffer) {
        GL11.glTexCoordPointer((int)i, (int)j, (int)k, (ByteBuffer)byteBuffer);
    }

    public static void vertexPointer(int i, int j, int k, int l) {
        GL11.glVertexPointer((int)i, (int)j, (int)k, (long)l);
    }

    public static void vertexPointer(int i, int j, int k, ByteBuffer byteBuffer) {
        GL11.glVertexPointer((int)i, (int)j, (int)k, (ByteBuffer)byteBuffer);
    }

    public static void colorPointer(int i, int j, int k, int l) {
        GL11.glColorPointer((int)i, (int)j, (int)k, (long)l);
    }

    public static void colorPointer(int i, int j, int k, ByteBuffer byteBuffer) {
        GL11.glColorPointer((int)i, (int)j, (int)k, (ByteBuffer)byteBuffer);
    }

    public static void disableClientState(int i) {
        GL11.glDisableClientState((int)i);
    }

    public static void enableClientState(int i) {
        GL11.glEnableClientState((int)i);
    }

    public static void begin(int i) {
        GL11.glBegin((int)i);
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void drawArrays(int mode, int first, int count) {
        GL11.glDrawArrays((int)mode, (int)first, (int)count);
    }

    public static void lineWidth(float f) {
        GL11.glLineWidth((float)f);
    }

    public static void callList(int i) {
        GL11.glCallList((int)i);
    }

    public static void deleteLists(int i, int j) {
        GL11.glDeleteLists((int)i, (int)j);
    }

    public static void newList(int i, int j) {
        GL11.glNewList((int)i, (int)j);
    }

    public static void endList() {
        GL11.glEndList();
    }

    public static int genLists(int i) {
        return GL11.glGenLists((int)i);
    }

    public static void pixelStore(int pname, int param) {
        GL11.glPixelStorei((int)pname, (int)param);
    }

    public static void pixelTransfer(int i, float f) {
        GL11.glPixelTransferf((int)i, (float)f);
    }

    public static void readPixels(int i, int j, int k, int l, int m, int n, ByteBuffer byteBuffer) {
        GL11.glReadPixels((int)i, (int)j, (int)k, (int)l, (int)m, (int)n, (ByteBuffer)byteBuffer);
    }

    public static void readPixels(int i, int j, int k, int l, int m, int n, long o) {
        GL11.glReadPixels((int)i, (int)j, (int)k, (int)l, (int)m, (int)n, (long)o);
    }

    public static int getError() {
        return GL11.glGetError();
    }

    public static String getString(int i) {
        return GL11.glGetString((int)i);
    }

    public static void getInteger(int i, IntBuffer intBuffer) {
        GL11.glGetIntegerv((int)i, (IntBuffer)intBuffer);
    }

    public static int getInteger(int i) {
        return GL11.glGetInteger((int)i);
    }

    public static void setProfile(RenderMode renderMode) {
        renderMode.begin();
    }

    public static void unsetProfile(RenderMode renderMode) {
        renderMode.end();
    }

    static {
        TEXTURES = (Texture2DState[])IntStream.range(0, 8).mapToObj(i -> new Texture2DState()).toArray(Texture2DState[]::new);
        shadeModel = 7425;
        RESCALE_NORMAL = new CapabilityTracker(32826);
        COLOR_MASK = new ColorMask();
        COLOR = new Color4();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum RenderMode {
        DEFAULT{

            @Override
            public void begin() {
                GlStateManager.disableAlphaTest();
                GlStateManager.alphaFunc(519, 0.0f);
                GlStateManager.disableLighting();
                GlStateManager.lightModel(2899, DiffuseLighting.singletonBuffer(0.2f, 0.2f, 0.2f, 1.0f));
                for (int i = 0; i < 8; ++i) {
                    GlStateManager.disableLight(i);
                    GlStateManager.light(16384 + i, 4608, DiffuseLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 1.0f));
                    GlStateManager.light(16384 + i, 4611, DiffuseLighting.singletonBuffer(0.0f, 0.0f, 1.0f, 0.0f));
                    if (i == 0) {
                        GlStateManager.light(16384 + i, 4609, DiffuseLighting.singletonBuffer(1.0f, 1.0f, 1.0f, 1.0f));
                        GlStateManager.light(16384 + i, 4610, DiffuseLighting.singletonBuffer(1.0f, 1.0f, 1.0f, 1.0f));
                        continue;
                    }
                    GlStateManager.light(16384 + i, 4609, DiffuseLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 1.0f));
                    GlStateManager.light(16384 + i, 4610, DiffuseLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 1.0f));
                }
                GlStateManager.disableColorMaterial();
                GlStateManager.colorMaterial(1032, 5634);
                GlStateManager.disableDepthTest();
                GlStateManager.depthFunc(513);
                GlStateManager.depthMask(true);
                GlStateManager.disableBlend();
                GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ZERO);
                GlStateManager.blendFuncSeparate(SourceFactor.ONE, DestFactor.ZERO, SourceFactor.ONE, DestFactor.ZERO);
                GlStateManager.blendEquation(32774);
                GlStateManager.disableFog();
                GlStateManager.fogi(2917, 2048);
                GlStateManager.fogDensity(1.0f);
                GlStateManager.fogStart(0.0f);
                GlStateManager.fogEnd(1.0f);
                GlStateManager.fog(2918, DiffuseLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                if (GL.getCapabilities().GL_NV_fog_distance) {
                    GlStateManager.fogi(2917, 34140);
                }
                GlStateManager.polygonOffset(0.0f, 0.0f);
                GlStateManager.disableColorLogicOp();
                GlStateManager.logicOp(5379);
                GlStateManager.disableTexGen(TexCoord.S);
                GlStateManager.texGenMode(TexCoord.S, 9216);
                GlStateManager.texGenParam(TexCoord.S, 9474, DiffuseLighting.singletonBuffer(1.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.texGenParam(TexCoord.S, 9217, DiffuseLighting.singletonBuffer(1.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.disableTexGen(TexCoord.T);
                GlStateManager.texGenMode(TexCoord.T, 9216);
                GlStateManager.texGenParam(TexCoord.T, 9474, DiffuseLighting.singletonBuffer(0.0f, 1.0f, 0.0f, 0.0f));
                GlStateManager.texGenParam(TexCoord.T, 9217, DiffuseLighting.singletonBuffer(0.0f, 1.0f, 0.0f, 0.0f));
                GlStateManager.disableTexGen(TexCoord.R);
                GlStateManager.texGenMode(TexCoord.R, 9216);
                GlStateManager.texGenParam(TexCoord.R, 9474, DiffuseLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.texGenParam(TexCoord.R, 9217, DiffuseLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.disableTexGen(TexCoord.Q);
                GlStateManager.texGenMode(TexCoord.Q, 9216);
                GlStateManager.texGenParam(TexCoord.Q, 9474, DiffuseLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.texGenParam(TexCoord.Q, 9217, DiffuseLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.activeTexture(0);
                GlStateManager.texParameter(3553, 10240, 9729);
                GlStateManager.texParameter(3553, 10241, 9986);
                GlStateManager.texParameter(3553, 10242, 10497);
                GlStateManager.texParameter(3553, 10243, 10497);
                GlStateManager.texParameter(3553, 33085, 1000);
                GlStateManager.texParameter(3553, 33083, 1000);
                GlStateManager.texParameter(3553, 33082, -1000);
                GlStateManager.texParameter(3553, 34049, 0.0f);
                GlStateManager.texEnv(8960, 8704, 8448);
                GlStateManager.texEnv(8960, 8705, DiffuseLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.texEnv(8960, 34161, 8448);
                GlStateManager.texEnv(8960, 34162, 8448);
                GlStateManager.texEnv(8960, 34176, 5890);
                GlStateManager.texEnv(8960, 34177, 34168);
                GlStateManager.texEnv(8960, 34178, 34166);
                GlStateManager.texEnv(8960, 34184, 5890);
                GlStateManager.texEnv(8960, 34185, 34168);
                GlStateManager.texEnv(8960, 34186, 34166);
                GlStateManager.texEnv(8960, 34192, 768);
                GlStateManager.texEnv(8960, 34193, 768);
                GlStateManager.texEnv(8960, 34194, 770);
                GlStateManager.texEnv(8960, 34200, 770);
                GlStateManager.texEnv(8960, 34201, 770);
                GlStateManager.texEnv(8960, 34202, 770);
                GlStateManager.texEnv(8960, 34163, 1.0f);
                GlStateManager.texEnv(8960, 3356, 1.0f);
                GlStateManager.disableNormalize();
                GlStateManager.shadeModel(7425);
                GlStateManager.disableRescaleNormal();
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.clearDepth(1.0);
                GlStateManager.lineWidth(1.0f);
                GlStateManager.normal3f(0.0f, 0.0f, 1.0f);
                GlStateManager.polygonMode(1028, 6914);
                GlStateManager.polygonMode(1029, 6914);
            }

            @Override
            public void end() {
            }
        }
        ,
        PLAYER_SKIN{

            @Override
            public void begin() {
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            }

            @Override
            public void end() {
                GlStateManager.disableBlend();
            }
        }
        ,
        TRANSPARENT_MODEL{

            @Override
            public void begin() {
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 0.15f);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.alphaFunc(516, 0.003921569f);
            }

            @Override
            public void end() {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1f);
                GlStateManager.depthMask(true);
            }
        };


        public abstract void begin();

        public abstract void end();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum DestFactor {
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

        private DestFactor(int j) {
            this.value = j;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum SourceFactor {
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

        private SourceFactor(int j) {
            this.value = j;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CapabilityTracker {
        private final int cap;
        private boolean state;

        public CapabilityTracker(int i) {
            this.cap = i;
        }

        public void disable() {
            this.setState(false);
        }

        public void enable() {
            this.setState(true);
        }

        public void setState(boolean bl) {
            if (bl != this.state) {
                this.state = bl;
                if (bl) {
                    GL11.glEnable((int)this.cap);
                } else {
                    GL11.glDisable((int)this.cap);
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Color4 {
        public float red = 1.0f;
        public float green = 1.0f;
        public float blue = 1.0f;
        public float alpha = 1.0f;

        public Color4() {
            this(1.0f, 1.0f, 1.0f, 1.0f);
        }

        public Color4(float f, float g, float h, float i) {
            this.red = f;
            this.green = g;
            this.blue = h;
            this.alpha = i;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ColorMask {
        public boolean red = true;
        public boolean green = true;
        public boolean blue = true;
        public boolean alpha = true;

        private ColorMask() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum TexCoord {
        S,
        T,
        R,
        Q;

    }

    @Environment(value=EnvType.CLIENT)
    static class TexGenCoordState {
        public final CapabilityTracker capState;
        public final int coord;
        public int mode = -1;

        public TexGenCoordState(int i, int j) {
            this.coord = i;
            this.capState = new CapabilityTracker(j);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class TexGenState {
        public final TexGenCoordState s = new TexGenCoordState(8192, 3168);
        public final TexGenCoordState t = new TexGenCoordState(8193, 3169);
        public final TexGenCoordState r = new TexGenCoordState(8194, 3170);
        public final TexGenCoordState q = new TexGenCoordState(8195, 3171);

        private TexGenState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class StencilState {
        public final StencilSubState subState = new StencilSubState();
        public int field_5153 = -1;
        public int field_5152 = 7680;
        public int field_5151 = 7680;
        public int field_5150 = 7680;

        private StencilState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class StencilSubState {
        public int func = 519;
        public int field_16203;
        public int field_5147 = -1;

        private StencilSubState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ClearState {
        public double clearDepth = 1.0;
        public final Color4 clearColor = new Color4(0.0f, 0.0f, 0.0f, 0.0f);
        public int field_16202;

        private ClearState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class LogicOpState {
        public final CapabilityTracker capState = new CapabilityTracker(3058);
        public int opcode = 5379;

        private LogicOpState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class PolygonOffsetState {
        public final CapabilityTracker capFill = new CapabilityTracker(32823);
        public final CapabilityTracker capLine = new CapabilityTracker(10754);
        public float factor;
        public float units;

        private PolygonOffsetState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CullFaceState {
        public final CapabilityTracker capState = new CapabilityTracker(2884);
        public int mode = 1029;

        private CullFaceState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class FogState {
        public final CapabilityTracker capState = new CapabilityTracker(2912);
        public int mode = 2048;
        public float density = 1.0f;
        public float start;
        public float end = 1.0f;

        private FogState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class DepthTestState {
        public final CapabilityTracker capState = new CapabilityTracker(2929);
        public boolean mask = true;
        public int func = 513;

        private DepthTestState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class BlendFuncState {
        public final CapabilityTracker capState = new CapabilityTracker(3042);
        public int sfactor = 1;
        public int dfactor = 0;
        public int srcAlpha = 1;
        public int dstAlpha = 0;

        private BlendFuncState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ColorMaterialState {
        public final CapabilityTracker capState = new CapabilityTracker(2903);
        public int face = 1032;
        public int mode = 5634;

        private ColorMaterialState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class AlphaTestState {
        public final CapabilityTracker capState = new CapabilityTracker(3008);
        public int func = 519;
        public float ref = -1.0f;

        private AlphaTestState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Texture2DState {
        public final CapabilityTracker capState = new CapabilityTracker(3553);
        public int boundTexture;

        private Texture2DState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Viewport {
        INSTANCE;

        protected int x;
        protected int y;
        protected int width;
        protected int height;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum LogicOp {
        AND(5377),
        AND_INVERTED(5380),
        AND_REVERSE(5378),
        CLEAR(5376),
        COPY(5379),
        COPY_INVERTED(5388),
        EQUIV(5385),
        INVERT(5386),
        NAND(5390),
        NOOP(5381),
        NOR(5384),
        OR(5383),
        OR_INVERTED(5389),
        OR_REVERSE(5387),
        SET(5391),
        XOR(5382);

        public final int glValue;

        private LogicOp(int j) {
            this.glValue = j;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum FaceSides {
        FRONT(1028),
        BACK(1029),
        FRONT_AND_BACK(1032);

        public final int glValue;

        private FaceSides(int j) {
            this.glValue = j;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum FogMode {
        LINEAR(9729),
        EXP(2048),
        EXP2(2049);

        public final int glValue;

        private FogMode(int j) {
            this.glValue = j;
        }
    }
}

