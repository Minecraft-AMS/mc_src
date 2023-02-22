/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.Version
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallback
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 *  org.lwjgl.glfw.GLFWVidMode
 *  org.lwjgl.opengl.ARBFramebufferObject
 *  org.lwjgl.opengl.ARBMultitexture
 *  org.lwjgl.opengl.ARBShaderObjects
 *  org.lwjgl.opengl.ARBVertexBufferObject
 *  org.lwjgl.opengl.ARBVertexShader
 *  org.lwjgl.opengl.EXTBlendFuncSeparate
 *  org.lwjgl.opengl.EXTFramebufferObject
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.opengl.GL14
 *  org.lwjgl.opengl.GL15
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.system.MemoryUtil
 *  oshi.SystemInfo
 *  oshi.hardware.Processor
 */
package com.mojang.blaze3d.platform;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Snooper;
import net.minecraft.client.util.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.EXTBlendFuncSeparate;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;
import oshi.SystemInfo;
import oshi.hardware.Processor;

@Environment(value=EnvType.CLIENT)
public class GLX {
    private static final Logger LOGGER = LogManager.getLogger();
    public static boolean isNvidia;
    public static boolean isAmd;
    public static int GL_FRAMEBUFFER;
    public static int GL_RENDERBUFFER;
    public static int GL_COLOR_ATTACHMENT0;
    public static int GL_DEPTH_ATTACHMENT;
    public static int GL_FRAMEBUFFER_COMPLETE;
    public static int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
    public static int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
    public static int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
    public static int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER;
    private static FBOMode fboMode;
    public static final boolean useFbo = true;
    private static boolean hasShaders;
    private static boolean useShaderArb;
    public static int GL_LINK_STATUS;
    public static int GL_COMPILE_STATUS;
    public static int GL_VERTEX_SHADER;
    public static int GL_FRAGMENT_SHADER;
    private static boolean useMultitextureArb;
    public static int GL_TEXTURE0;
    public static int GL_TEXTURE1;
    public static int GL_TEXTURE2;
    private static boolean useTexEnvCombineArb;
    public static int GL_COMBINE;
    public static int GL_INTERPOLATE;
    public static int GL_PRIMARY_COLOR;
    public static int GL_CONSTANT;
    public static int GL_PREVIOUS;
    public static int GL_COMBINE_RGB;
    public static int GL_SOURCE0_RGB;
    public static int GL_SOURCE1_RGB;
    public static int GL_SOURCE2_RGB;
    public static int GL_OPERAND0_RGB;
    public static int GL_OPERAND1_RGB;
    public static int GL_OPERAND2_RGB;
    public static int GL_COMBINE_ALPHA;
    public static int GL_SOURCE0_ALPHA;
    public static int GL_SOURCE1_ALPHA;
    public static int GL_SOURCE2_ALPHA;
    public static int GL_OPERAND0_ALPHA;
    public static int GL_OPERAND1_ALPHA;
    public static int GL_OPERAND2_ALPHA;
    private static boolean separateBlend;
    public static boolean useSeparateBlendExt;
    public static boolean isOpenGl21;
    public static boolean usePostProcess;
    private static String capsString;
    private static String cpuInfo;
    public static final boolean useVbo = true;
    public static boolean needVbo;
    private static boolean useVboArb;
    public static int GL_ARRAY_BUFFER;
    public static int GL_STATIC_DRAW;
    private static final Map<Integer, String> LOOKUP_MAP;

    public static void populateSnooperWithOpenGL(Snooper snooper) {
        snooper.setFixedData("opengl_version", GlStateManager.getString(7938));
        snooper.setFixedData("opengl_vendor", GlStateManager.getString(7936));
        GLCapabilities gLCapabilities = GL.getCapabilities();
        snooper.setFixedData("gl_caps[ARB_arrays_of_arrays]", gLCapabilities.GL_ARB_arrays_of_arrays);
        snooper.setFixedData("gl_caps[ARB_base_instance]", gLCapabilities.GL_ARB_base_instance);
        snooper.setFixedData("gl_caps[ARB_blend_func_extended]", gLCapabilities.GL_ARB_blend_func_extended);
        snooper.setFixedData("gl_caps[ARB_clear_buffer_object]", gLCapabilities.GL_ARB_clear_buffer_object);
        snooper.setFixedData("gl_caps[ARB_color_buffer_float]", gLCapabilities.GL_ARB_color_buffer_float);
        snooper.setFixedData("gl_caps[ARB_compatibility]", gLCapabilities.GL_ARB_compatibility);
        snooper.setFixedData("gl_caps[ARB_compressed_texture_pixel_storage]", gLCapabilities.GL_ARB_compressed_texture_pixel_storage);
        snooper.setFixedData("gl_caps[ARB_compute_shader]", gLCapabilities.GL_ARB_compute_shader);
        snooper.setFixedData("gl_caps[ARB_copy_buffer]", gLCapabilities.GL_ARB_copy_buffer);
        snooper.setFixedData("gl_caps[ARB_copy_image]", gLCapabilities.GL_ARB_copy_image);
        snooper.setFixedData("gl_caps[ARB_depth_buffer_float]", gLCapabilities.GL_ARB_depth_buffer_float);
        snooper.setFixedData("gl_caps[ARB_compute_shader]", gLCapabilities.GL_ARB_compute_shader);
        snooper.setFixedData("gl_caps[ARB_copy_buffer]", gLCapabilities.GL_ARB_copy_buffer);
        snooper.setFixedData("gl_caps[ARB_copy_image]", gLCapabilities.GL_ARB_copy_image);
        snooper.setFixedData("gl_caps[ARB_depth_buffer_float]", gLCapabilities.GL_ARB_depth_buffer_float);
        snooper.setFixedData("gl_caps[ARB_depth_clamp]", gLCapabilities.GL_ARB_depth_clamp);
        snooper.setFixedData("gl_caps[ARB_depth_texture]", gLCapabilities.GL_ARB_depth_texture);
        snooper.setFixedData("gl_caps[ARB_draw_buffers]", gLCapabilities.GL_ARB_draw_buffers);
        snooper.setFixedData("gl_caps[ARB_draw_buffers_blend]", gLCapabilities.GL_ARB_draw_buffers_blend);
        snooper.setFixedData("gl_caps[ARB_draw_elements_base_vertex]", gLCapabilities.GL_ARB_draw_elements_base_vertex);
        snooper.setFixedData("gl_caps[ARB_draw_indirect]", gLCapabilities.GL_ARB_draw_indirect);
        snooper.setFixedData("gl_caps[ARB_draw_instanced]", gLCapabilities.GL_ARB_draw_instanced);
        snooper.setFixedData("gl_caps[ARB_explicit_attrib_location]", gLCapabilities.GL_ARB_explicit_attrib_location);
        snooper.setFixedData("gl_caps[ARB_explicit_uniform_location]", gLCapabilities.GL_ARB_explicit_uniform_location);
        snooper.setFixedData("gl_caps[ARB_fragment_layer_viewport]", gLCapabilities.GL_ARB_fragment_layer_viewport);
        snooper.setFixedData("gl_caps[ARB_fragment_program]", gLCapabilities.GL_ARB_fragment_program);
        snooper.setFixedData("gl_caps[ARB_fragment_shader]", gLCapabilities.GL_ARB_fragment_shader);
        snooper.setFixedData("gl_caps[ARB_fragment_program_shadow]", gLCapabilities.GL_ARB_fragment_program_shadow);
        snooper.setFixedData("gl_caps[ARB_framebuffer_object]", gLCapabilities.GL_ARB_framebuffer_object);
        snooper.setFixedData("gl_caps[ARB_framebuffer_sRGB]", gLCapabilities.GL_ARB_framebuffer_sRGB);
        snooper.setFixedData("gl_caps[ARB_geometry_shader4]", gLCapabilities.GL_ARB_geometry_shader4);
        snooper.setFixedData("gl_caps[ARB_gpu_shader5]", gLCapabilities.GL_ARB_gpu_shader5);
        snooper.setFixedData("gl_caps[ARB_half_float_pixel]", gLCapabilities.GL_ARB_half_float_pixel);
        snooper.setFixedData("gl_caps[ARB_half_float_vertex]", gLCapabilities.GL_ARB_half_float_vertex);
        snooper.setFixedData("gl_caps[ARB_instanced_arrays]", gLCapabilities.GL_ARB_instanced_arrays);
        snooper.setFixedData("gl_caps[ARB_map_buffer_alignment]", gLCapabilities.GL_ARB_map_buffer_alignment);
        snooper.setFixedData("gl_caps[ARB_map_buffer_range]", gLCapabilities.GL_ARB_map_buffer_range);
        snooper.setFixedData("gl_caps[ARB_multisample]", gLCapabilities.GL_ARB_multisample);
        snooper.setFixedData("gl_caps[ARB_multitexture]", gLCapabilities.GL_ARB_multitexture);
        snooper.setFixedData("gl_caps[ARB_occlusion_query2]", gLCapabilities.GL_ARB_occlusion_query2);
        snooper.setFixedData("gl_caps[ARB_pixel_buffer_object]", gLCapabilities.GL_ARB_pixel_buffer_object);
        snooper.setFixedData("gl_caps[ARB_seamless_cube_map]", gLCapabilities.GL_ARB_seamless_cube_map);
        snooper.setFixedData("gl_caps[ARB_shader_objects]", gLCapabilities.GL_ARB_shader_objects);
        snooper.setFixedData("gl_caps[ARB_shader_stencil_export]", gLCapabilities.GL_ARB_shader_stencil_export);
        snooper.setFixedData("gl_caps[ARB_shader_texture_lod]", gLCapabilities.GL_ARB_shader_texture_lod);
        snooper.setFixedData("gl_caps[ARB_shadow]", gLCapabilities.GL_ARB_shadow);
        snooper.setFixedData("gl_caps[ARB_shadow_ambient]", gLCapabilities.GL_ARB_shadow_ambient);
        snooper.setFixedData("gl_caps[ARB_stencil_texturing]", gLCapabilities.GL_ARB_stencil_texturing);
        snooper.setFixedData("gl_caps[ARB_sync]", gLCapabilities.GL_ARB_sync);
        snooper.setFixedData("gl_caps[ARB_tessellation_shader]", gLCapabilities.GL_ARB_tessellation_shader);
        snooper.setFixedData("gl_caps[ARB_texture_border_clamp]", gLCapabilities.GL_ARB_texture_border_clamp);
        snooper.setFixedData("gl_caps[ARB_texture_buffer_object]", gLCapabilities.GL_ARB_texture_buffer_object);
        snooper.setFixedData("gl_caps[ARB_texture_cube_map]", gLCapabilities.GL_ARB_texture_cube_map);
        snooper.setFixedData("gl_caps[ARB_texture_cube_map_array]", gLCapabilities.GL_ARB_texture_cube_map_array);
        snooper.setFixedData("gl_caps[ARB_texture_non_power_of_two]", gLCapabilities.GL_ARB_texture_non_power_of_two);
        snooper.setFixedData("gl_caps[ARB_uniform_buffer_object]", gLCapabilities.GL_ARB_uniform_buffer_object);
        snooper.setFixedData("gl_caps[ARB_vertex_blend]", gLCapabilities.GL_ARB_vertex_blend);
        snooper.setFixedData("gl_caps[ARB_vertex_buffer_object]", gLCapabilities.GL_ARB_vertex_buffer_object);
        snooper.setFixedData("gl_caps[ARB_vertex_program]", gLCapabilities.GL_ARB_vertex_program);
        snooper.setFixedData("gl_caps[ARB_vertex_shader]", gLCapabilities.GL_ARB_vertex_shader);
        snooper.setFixedData("gl_caps[EXT_bindable_uniform]", gLCapabilities.GL_EXT_bindable_uniform);
        snooper.setFixedData("gl_caps[EXT_blend_equation_separate]", gLCapabilities.GL_EXT_blend_equation_separate);
        snooper.setFixedData("gl_caps[EXT_blend_func_separate]", gLCapabilities.GL_EXT_blend_func_separate);
        snooper.setFixedData("gl_caps[EXT_blend_minmax]", gLCapabilities.GL_EXT_blend_minmax);
        snooper.setFixedData("gl_caps[EXT_blend_subtract]", gLCapabilities.GL_EXT_blend_subtract);
        snooper.setFixedData("gl_caps[EXT_draw_instanced]", gLCapabilities.GL_EXT_draw_instanced);
        snooper.setFixedData("gl_caps[EXT_framebuffer_multisample]", gLCapabilities.GL_EXT_framebuffer_multisample);
        snooper.setFixedData("gl_caps[EXT_framebuffer_object]", gLCapabilities.GL_EXT_framebuffer_object);
        snooper.setFixedData("gl_caps[EXT_framebuffer_sRGB]", gLCapabilities.GL_EXT_framebuffer_sRGB);
        snooper.setFixedData("gl_caps[EXT_geometry_shader4]", gLCapabilities.GL_EXT_geometry_shader4);
        snooper.setFixedData("gl_caps[EXT_gpu_program_parameters]", gLCapabilities.GL_EXT_gpu_program_parameters);
        snooper.setFixedData("gl_caps[EXT_gpu_shader4]", gLCapabilities.GL_EXT_gpu_shader4);
        snooper.setFixedData("gl_caps[EXT_packed_depth_stencil]", gLCapabilities.GL_EXT_packed_depth_stencil);
        snooper.setFixedData("gl_caps[EXT_separate_shader_objects]", gLCapabilities.GL_EXT_separate_shader_objects);
        snooper.setFixedData("gl_caps[EXT_shader_image_load_store]", gLCapabilities.GL_EXT_shader_image_load_store);
        snooper.setFixedData("gl_caps[EXT_shadow_funcs]", gLCapabilities.GL_EXT_shadow_funcs);
        snooper.setFixedData("gl_caps[EXT_shared_texture_palette]", gLCapabilities.GL_EXT_shared_texture_palette);
        snooper.setFixedData("gl_caps[EXT_stencil_clear_tag]", gLCapabilities.GL_EXT_stencil_clear_tag);
        snooper.setFixedData("gl_caps[EXT_stencil_two_side]", gLCapabilities.GL_EXT_stencil_two_side);
        snooper.setFixedData("gl_caps[EXT_stencil_wrap]", gLCapabilities.GL_EXT_stencil_wrap);
        snooper.setFixedData("gl_caps[EXT_texture_array]", gLCapabilities.GL_EXT_texture_array);
        snooper.setFixedData("gl_caps[EXT_texture_buffer_object]", gLCapabilities.GL_EXT_texture_buffer_object);
        snooper.setFixedData("gl_caps[EXT_texture_integer]", gLCapabilities.GL_EXT_texture_integer);
        snooper.setFixedData("gl_caps[EXT_texture_sRGB]", gLCapabilities.GL_EXT_texture_sRGB);
        snooper.setFixedData("gl_caps[ARB_vertex_shader]", gLCapabilities.GL_ARB_vertex_shader);
        snooper.setFixedData("gl_caps[gl_max_vertex_uniforms]", GlStateManager.getInteger(35658));
        GlStateManager.getError();
        snooper.setFixedData("gl_caps[gl_max_fragment_uniforms]", GlStateManager.getInteger(35657));
        GlStateManager.getError();
        snooper.setFixedData("gl_caps[gl_max_vertex_attribs]", GlStateManager.getInteger(34921));
        GlStateManager.getError();
        snooper.setFixedData("gl_caps[gl_max_vertex_texture_image_units]", GlStateManager.getInteger(35660));
        GlStateManager.getError();
        snooper.setFixedData("gl_caps[gl_max_texture_image_units]", GlStateManager.getInteger(34930));
        GlStateManager.getError();
        snooper.setFixedData("gl_caps[gl_max_array_texture_layers]", GlStateManager.getInteger(35071));
        GlStateManager.getError();
    }

    public static String getOpenGLVersionString() {
        if (GLFW.glfwGetCurrentContext() == 0L) {
            return "NO CONTEXT";
        }
        return GlStateManager.getString(7937) + " GL version " + GlStateManager.getString(7938) + ", " + GlStateManager.getString(7936);
    }

    public static int getRefreshRate(Window window) {
        long l = GLFW.glfwGetWindowMonitor((long)window.getHandle());
        if (l == 0L) {
            l = GLFW.glfwGetPrimaryMonitor();
        }
        GLFWVidMode gLFWVidMode = l == 0L ? null : GLFW.glfwGetVideoMode((long)l);
        return gLFWVidMode == null ? 0 : gLFWVidMode.refreshRate();
    }

    public static String getLWJGLVersion() {
        return Version.getVersion();
    }

    public static LongSupplier initGlfw() {
        LongSupplier longSupplier;
        Window.method_4492((integer, string) -> {
            throw new IllegalStateException(String.format("GLFW error before init: [0x%X]%s", integer, string));
        });
        ArrayList list = Lists.newArrayList();
        GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback((i, l) -> list.add(String.format("GLFW error during init: [0x%X]%s", i, l)));
        if (GLFW.glfwInit()) {
            longSupplier = () -> (long)(GLFW.glfwGetTime() * 1.0E9);
            for (String string2 : list) {
                LOGGER.error("GLFW error collected during initialization: {}", (Object)string2);
            }
        } else {
            throw new IllegalStateException("Failed to initialize GLFW, errors: " + Joiner.on((String)",").join((Iterable)list));
        }
        GLX.setGlfwErrorCallback((GLFWErrorCallbackI)gLFWErrorCallback);
        return longSupplier;
    }

    public static void setGlfwErrorCallback(GLFWErrorCallbackI gLFWErrorCallbackI) {
        GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)gLFWErrorCallbackI).free();
    }

    public static boolean shouldClose(Window window) {
        return GLFW.glfwWindowShouldClose((long)window.getHandle());
    }

    public static void pollEvents() {
        GLFW.glfwPollEvents();
    }

    public static String getOpenGLVersion() {
        return GlStateManager.getString(7938);
    }

    public static String getRenderer() {
        return GlStateManager.getString(7937);
    }

    public static String getVendor() {
        return GlStateManager.getString(7936);
    }

    public static void setupNvFogDistance() {
        if (GL.getCapabilities().GL_NV_fog_distance) {
            GlStateManager.fogi(34138, 34139);
        }
    }

    public static boolean supportsOpenGL2() {
        return GL.getCapabilities().OpenGL20;
    }

    public static void withTextureRestore(Runnable runnable) {
        GL11.glPushAttrib((int)270336);
        try {
            runnable.run();
        }
        finally {
            GL11.glPopAttrib();
        }
    }

    public static ByteBuffer allocateMemory(int i) {
        return MemoryUtil.memAlloc((int)i);
    }

    public static void freeMemory(Buffer buffer) {
        MemoryUtil.memFree((Buffer)buffer);
    }

    public static void init() {
        GLCapabilities gLCapabilities = GL.getCapabilities();
        useMultitextureArb = gLCapabilities.GL_ARB_multitexture && !gLCapabilities.OpenGL13;
        boolean bl = useTexEnvCombineArb = gLCapabilities.GL_ARB_texture_env_combine && !gLCapabilities.OpenGL13;
        if (useMultitextureArb) {
            capsString = capsString + "Using ARB_multitexture.\n";
            GL_TEXTURE0 = 33984;
            GL_TEXTURE1 = 33985;
            GL_TEXTURE2 = 33986;
        } else {
            capsString = capsString + "Using GL 1.3 multitexturing.\n";
            GL_TEXTURE0 = 33984;
            GL_TEXTURE1 = 33985;
            GL_TEXTURE2 = 33986;
        }
        if (useTexEnvCombineArb) {
            capsString = capsString + "Using ARB_texture_env_combine.\n";
            GL_COMBINE = 34160;
            GL_INTERPOLATE = 34165;
            GL_PRIMARY_COLOR = 34167;
            GL_CONSTANT = 34166;
            GL_PREVIOUS = 34168;
            GL_COMBINE_RGB = 34161;
            GL_SOURCE0_RGB = 34176;
            GL_SOURCE1_RGB = 34177;
            GL_SOURCE2_RGB = 34178;
            GL_OPERAND0_RGB = 34192;
            GL_OPERAND1_RGB = 34193;
            GL_OPERAND2_RGB = 34194;
            GL_COMBINE_ALPHA = 34162;
            GL_SOURCE0_ALPHA = 34184;
            GL_SOURCE1_ALPHA = 34185;
            GL_SOURCE2_ALPHA = 34186;
            GL_OPERAND0_ALPHA = 34200;
            GL_OPERAND1_ALPHA = 34201;
            GL_OPERAND2_ALPHA = 34202;
        } else {
            capsString = capsString + "Using GL 1.3 texture combiners.\n";
            GL_COMBINE = 34160;
            GL_INTERPOLATE = 34165;
            GL_PRIMARY_COLOR = 34167;
            GL_CONSTANT = 34166;
            GL_PREVIOUS = 34168;
            GL_COMBINE_RGB = 34161;
            GL_SOURCE0_RGB = 34176;
            GL_SOURCE1_RGB = 34177;
            GL_SOURCE2_RGB = 34178;
            GL_OPERAND0_RGB = 34192;
            GL_OPERAND1_RGB = 34193;
            GL_OPERAND2_RGB = 34194;
            GL_COMBINE_ALPHA = 34162;
            GL_SOURCE0_ALPHA = 34184;
            GL_SOURCE1_ALPHA = 34185;
            GL_SOURCE2_ALPHA = 34186;
            GL_OPERAND0_ALPHA = 34200;
            GL_OPERAND1_ALPHA = 34201;
            GL_OPERAND2_ALPHA = 34202;
        }
        useSeparateBlendExt = gLCapabilities.GL_EXT_blend_func_separate && !gLCapabilities.OpenGL14;
        separateBlend = gLCapabilities.OpenGL14 || gLCapabilities.GL_EXT_blend_func_separate;
        capsString = capsString + "Using framebuffer objects because ";
        if (gLCapabilities.OpenGL30) {
            capsString = capsString + "OpenGL 3.0 is supported and separate blending is supported.\n";
            fboMode = FBOMode.BASE;
            GL_FRAMEBUFFER = 36160;
            GL_RENDERBUFFER = 36161;
            GL_COLOR_ATTACHMENT0 = 36064;
            GL_DEPTH_ATTACHMENT = 36096;
            GL_FRAMEBUFFER_COMPLETE = 36053;
            GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
            GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
        } else if (gLCapabilities.GL_ARB_framebuffer_object) {
            capsString = capsString + "ARB_framebuffer_object is supported and separate blending is supported.\n";
            fboMode = FBOMode.ARB;
            GL_FRAMEBUFFER = 36160;
            GL_RENDERBUFFER = 36161;
            GL_COLOR_ATTACHMENT0 = 36064;
            GL_DEPTH_ATTACHMENT = 36096;
            GL_FRAMEBUFFER_COMPLETE = 36053;
            GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
            GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
        } else if (gLCapabilities.GL_EXT_framebuffer_object) {
            capsString = capsString + "EXT_framebuffer_object is supported.\n";
            fboMode = FBOMode.EXT;
            GL_FRAMEBUFFER = 36160;
            GL_RENDERBUFFER = 36161;
            GL_COLOR_ATTACHMENT0 = 36064;
            GL_DEPTH_ATTACHMENT = 36096;
            GL_FRAMEBUFFER_COMPLETE = 36053;
            GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
            GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
        } else {
            throw new IllegalStateException("The driver does not appear to support framebuffer objects");
        }
        isOpenGl21 = gLCapabilities.OpenGL21;
        hasShaders = isOpenGl21 || gLCapabilities.GL_ARB_vertex_shader && gLCapabilities.GL_ARB_fragment_shader && gLCapabilities.GL_ARB_shader_objects;
        capsString = capsString + "Shaders are " + (hasShaders ? "" : "not ") + "available because ";
        if (hasShaders) {
            if (gLCapabilities.OpenGL21) {
                capsString = capsString + "OpenGL 2.1 is supported.\n";
                useShaderArb = false;
                GL_LINK_STATUS = 35714;
                GL_COMPILE_STATUS = 35713;
                GL_VERTEX_SHADER = 35633;
                GL_FRAGMENT_SHADER = 35632;
            } else {
                capsString = capsString + "ARB_shader_objects, ARB_vertex_shader, and ARB_fragment_shader are supported.\n";
                useShaderArb = true;
                GL_LINK_STATUS = 35714;
                GL_COMPILE_STATUS = 35713;
                GL_VERTEX_SHADER = 35633;
                GL_FRAGMENT_SHADER = 35632;
            }
        } else {
            capsString = capsString + "OpenGL 2.1 is " + (gLCapabilities.OpenGL21 ? "" : "not ") + "supported, ";
            capsString = capsString + "ARB_shader_objects is " + (gLCapabilities.GL_ARB_shader_objects ? "" : "not ") + "supported, ";
            capsString = capsString + "ARB_vertex_shader is " + (gLCapabilities.GL_ARB_vertex_shader ? "" : "not ") + "supported, and ";
            capsString = capsString + "ARB_fragment_shader is " + (gLCapabilities.GL_ARB_fragment_shader ? "" : "not ") + "supported.\n";
        }
        usePostProcess = hasShaders;
        String string = GL11.glGetString((int)7936).toLowerCase(Locale.ROOT);
        isNvidia = string.contains("nvidia");
        useVboArb = !gLCapabilities.OpenGL15 && gLCapabilities.GL_ARB_vertex_buffer_object;
        capsString = capsString + "VBOs are available because ";
        if (useVboArb) {
            capsString = capsString + "ARB_vertex_buffer_object is supported.\n";
            GL_STATIC_DRAW = 35044;
            GL_ARRAY_BUFFER = 34962;
        } else {
            capsString = capsString + "OpenGL 1.5 is supported.\n";
            GL_STATIC_DRAW = 35044;
            GL_ARRAY_BUFFER = 34962;
        }
        isAmd = string.contains("ati");
        if (isAmd) {
            needVbo = true;
        }
        try {
            Processor[] processors = new SystemInfo().getHardware().getProcessors();
            cpuInfo = String.format("%dx %s", processors.length, processors[0]).replaceAll("\\s+", " ");
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static boolean isNextGen() {
        return usePostProcess;
    }

    public static String getCapsString() {
        return capsString;
    }

    public static int glGetProgrami(int program, int programName) {
        if (useShaderArb) {
            return ARBShaderObjects.glGetObjectParameteriARB((int)program, (int)programName);
        }
        return GL20.glGetProgrami((int)program, (int)programName);
    }

    public static void glAttachShader(int program, int shader) {
        if (useShaderArb) {
            ARBShaderObjects.glAttachObjectARB((int)program, (int)shader);
        } else {
            GL20.glAttachShader((int)program, (int)shader);
        }
    }

    public static void glDeleteShader(int shader) {
        if (useShaderArb) {
            ARBShaderObjects.glDeleteObjectARB((int)shader);
        } else {
            GL20.glDeleteShader((int)shader);
        }
    }

    public static int glCreateShader(int type) {
        if (useShaderArb) {
            return ARBShaderObjects.glCreateShaderObjectARB((int)type);
        }
        return GL20.glCreateShader((int)type);
    }

    public static void glShaderSource(int i, CharSequence charSequence) {
        if (useShaderArb) {
            ARBShaderObjects.glShaderSourceARB((int)i, (CharSequence)charSequence);
        } else {
            GL20.glShaderSource((int)i, (CharSequence)charSequence);
        }
    }

    public static void glCompileShader(int shader) {
        if (useShaderArb) {
            ARBShaderObjects.glCompileShaderARB((int)shader);
        } else {
            GL20.glCompileShader((int)shader);
        }
    }

    public static int glGetShaderi(int shader, int programName) {
        if (useShaderArb) {
            return ARBShaderObjects.glGetObjectParameteriARB((int)shader, (int)programName);
        }
        return GL20.glGetShaderi((int)shader, (int)programName);
    }

    public static String glGetShaderInfoLog(int shader, int maxLength) {
        if (useShaderArb) {
            return ARBShaderObjects.glGetInfoLogARB((int)shader, (int)maxLength);
        }
        return GL20.glGetShaderInfoLog((int)shader, (int)maxLength);
    }

    public static String glGetProgramInfoLog(int program, int maxLength) {
        if (useShaderArb) {
            return ARBShaderObjects.glGetInfoLogARB((int)program, (int)maxLength);
        }
        return GL20.glGetProgramInfoLog((int)program, (int)maxLength);
    }

    public static void glUseProgram(int program) {
        if (useShaderArb) {
            ARBShaderObjects.glUseProgramObjectARB((int)program);
        } else {
            GL20.glUseProgram((int)program);
        }
    }

    public static int glCreateProgram() {
        if (useShaderArb) {
            return ARBShaderObjects.glCreateProgramObjectARB();
        }
        return GL20.glCreateProgram();
    }

    public static void glDeleteProgram(int program) {
        if (useShaderArb) {
            ARBShaderObjects.glDeleteObjectARB((int)program);
        } else {
            GL20.glDeleteProgram((int)program);
        }
    }

    public static void glLinkProgram(int program) {
        if (useShaderArb) {
            ARBShaderObjects.glLinkProgramARB((int)program);
        } else {
            GL20.glLinkProgram((int)program);
        }
    }

    public static int glGetUniformLocation(int program, CharSequence name) {
        if (useShaderArb) {
            return ARBShaderObjects.glGetUniformLocationARB((int)program, (CharSequence)name);
        }
        return GL20.glGetUniformLocation((int)program, (CharSequence)name);
    }

    public static void glUniform1(int location, IntBuffer values) {
        if (useShaderArb) {
            ARBShaderObjects.glUniform1ivARB((int)location, (IntBuffer)values);
        } else {
            GL20.glUniform1iv((int)location, (IntBuffer)values);
        }
    }

    public static void glUniform1i(int location, int value) {
        if (useShaderArb) {
            ARBShaderObjects.glUniform1iARB((int)location, (int)value);
        } else {
            GL20.glUniform1i((int)location, (int)value);
        }
    }

    public static void glUniform1(int location, FloatBuffer values) {
        if (useShaderArb) {
            ARBShaderObjects.glUniform1fvARB((int)location, (FloatBuffer)values);
        } else {
            GL20.glUniform1fv((int)location, (FloatBuffer)values);
        }
    }

    public static void glUniform2(int location, IntBuffer values) {
        if (useShaderArb) {
            ARBShaderObjects.glUniform2ivARB((int)location, (IntBuffer)values);
        } else {
            GL20.glUniform2iv((int)location, (IntBuffer)values);
        }
    }

    public static void glUniform2(int location, FloatBuffer values) {
        if (useShaderArb) {
            ARBShaderObjects.glUniform2fvARB((int)location, (FloatBuffer)values);
        } else {
            GL20.glUniform2fv((int)location, (FloatBuffer)values);
        }
    }

    public static void glUniform3(int location, IntBuffer values) {
        if (useShaderArb) {
            ARBShaderObjects.glUniform3ivARB((int)location, (IntBuffer)values);
        } else {
            GL20.glUniform3iv((int)location, (IntBuffer)values);
        }
    }

    public static void glUniform3(int location, FloatBuffer values) {
        if (useShaderArb) {
            ARBShaderObjects.glUniform3fvARB((int)location, (FloatBuffer)values);
        } else {
            GL20.glUniform3fv((int)location, (FloatBuffer)values);
        }
    }

    public static void glUniform4(int location, IntBuffer values) {
        if (useShaderArb) {
            ARBShaderObjects.glUniform4ivARB((int)location, (IntBuffer)values);
        } else {
            GL20.glUniform4iv((int)location, (IntBuffer)values);
        }
    }

    public static void glUniform4(int location, FloatBuffer values) {
        if (useShaderArb) {
            ARBShaderObjects.glUniform4fvARB((int)location, (FloatBuffer)values);
        } else {
            GL20.glUniform4fv((int)location, (FloatBuffer)values);
        }
    }

    public static void glUniformMatrix2(int location, boolean transpose, FloatBuffer matrices) {
        if (useShaderArb) {
            ARBShaderObjects.glUniformMatrix2fvARB((int)location, (boolean)transpose, (FloatBuffer)matrices);
        } else {
            GL20.glUniformMatrix2fv((int)location, (boolean)transpose, (FloatBuffer)matrices);
        }
    }

    public static void glUniformMatrix3(int location, boolean transpose, FloatBuffer matrices) {
        if (useShaderArb) {
            ARBShaderObjects.glUniformMatrix3fvARB((int)location, (boolean)transpose, (FloatBuffer)matrices);
        } else {
            GL20.glUniformMatrix3fv((int)location, (boolean)transpose, (FloatBuffer)matrices);
        }
    }

    public static void glUniformMatrix4(int location, boolean transpose, FloatBuffer matrices) {
        if (useShaderArb) {
            ARBShaderObjects.glUniformMatrix4fvARB((int)location, (boolean)transpose, (FloatBuffer)matrices);
        } else {
            GL20.glUniformMatrix4fv((int)location, (boolean)transpose, (FloatBuffer)matrices);
        }
    }

    public static int glGetAttribLocation(int program, CharSequence name) {
        if (useShaderArb) {
            return ARBVertexShader.glGetAttribLocationARB((int)program, (CharSequence)name);
        }
        return GL20.glGetAttribLocation((int)program, (CharSequence)name);
    }

    public static int glGenBuffers() {
        if (useVboArb) {
            return ARBVertexBufferObject.glGenBuffersARB();
        }
        return GL15.glGenBuffers();
    }

    public static void glGenBuffers(IntBuffer intBuffer) {
        if (useVboArb) {
            ARBVertexBufferObject.glGenBuffersARB((IntBuffer)intBuffer);
        } else {
            GL15.glGenBuffers((IntBuffer)intBuffer);
        }
    }

    public static void glBindBuffer(int target, int bufferId) {
        if (useVboArb) {
            ARBVertexBufferObject.glBindBufferARB((int)target, (int)bufferId);
        } else {
            GL15.glBindBuffer((int)target, (int)bufferId);
        }
    }

    public static void glBufferData(int target, ByteBuffer data, int usage) {
        if (useVboArb) {
            ARBVertexBufferObject.glBufferDataARB((int)target, (ByteBuffer)data, (int)usage);
        } else {
            GL15.glBufferData((int)target, (ByteBuffer)data, (int)usage);
        }
    }

    public static void glDeleteBuffers(int buffer) {
        if (useVboArb) {
            ARBVertexBufferObject.glDeleteBuffersARB((int)buffer);
        } else {
            GL15.glDeleteBuffers((int)buffer);
        }
    }

    public static void glDeleteBuffers(IntBuffer intBuffer) {
        if (useVboArb) {
            ARBVertexBufferObject.glDeleteBuffersARB((IntBuffer)intBuffer);
        } else {
            GL15.glDeleteBuffers((IntBuffer)intBuffer);
        }
    }

    public static boolean useVbo() {
        return true;
    }

    public static void glBindFramebuffer(int target, int framebuffer) {
        switch (fboMode) {
            case BASE: {
                GL30.glBindFramebuffer((int)target, (int)framebuffer);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glBindFramebuffer((int)target, (int)framebuffer);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glBindFramebufferEXT((int)target, (int)framebuffer);
            }
        }
    }

    public static void glBindRenderbuffer(int target, int renderbuffer) {
        switch (fboMode) {
            case BASE: {
                GL30.glBindRenderbuffer((int)target, (int)renderbuffer);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glBindRenderbuffer((int)target, (int)renderbuffer);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glBindRenderbufferEXT((int)target, (int)renderbuffer);
            }
        }
    }

    public static void glDeleteRenderbuffers(int renderbuffer) {
        switch (fboMode) {
            case BASE: {
                GL30.glDeleteRenderbuffers((int)renderbuffer);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glDeleteRenderbuffers((int)renderbuffer);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glDeleteRenderbuffersEXT((int)renderbuffer);
            }
        }
    }

    public static void glDeleteFramebuffers(int framebuffer) {
        switch (fboMode) {
            case BASE: {
                GL30.glDeleteFramebuffers((int)framebuffer);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glDeleteFramebuffers((int)framebuffer);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glDeleteFramebuffersEXT((int)framebuffer);
            }
        }
    }

    public static int glGenFramebuffers() {
        switch (fboMode) {
            case BASE: {
                return GL30.glGenFramebuffers();
            }
            case ARB: {
                return ARBFramebufferObject.glGenFramebuffers();
            }
            case EXT: {
                return EXTFramebufferObject.glGenFramebuffersEXT();
            }
        }
        return -1;
    }

    public static int glGenRenderbuffers() {
        switch (fboMode) {
            case BASE: {
                return GL30.glGenRenderbuffers();
            }
            case ARB: {
                return ARBFramebufferObject.glGenRenderbuffers();
            }
            case EXT: {
                return EXTFramebufferObject.glGenRenderbuffersEXT();
            }
        }
        return -1;
    }

    public static void glRenderbufferStorage(int target, int internalFormat, int width, int height) {
        switch (fboMode) {
            case BASE: {
                GL30.glRenderbufferStorage((int)target, (int)internalFormat, (int)width, (int)height);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glRenderbufferStorage((int)target, (int)internalFormat, (int)width, (int)height);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glRenderbufferStorageEXT((int)target, (int)internalFormat, (int)width, (int)height);
            }
        }
    }

    public static void glFramebufferRenderbuffer(int target, int attachment, int renderbufferTarget, int renderbuffer) {
        switch (fboMode) {
            case BASE: {
                GL30.glFramebufferRenderbuffer((int)target, (int)attachment, (int)renderbufferTarget, (int)renderbuffer);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glFramebufferRenderbuffer((int)target, (int)attachment, (int)renderbufferTarget, (int)renderbuffer);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glFramebufferRenderbufferEXT((int)target, (int)attachment, (int)renderbufferTarget, (int)renderbuffer);
            }
        }
    }

    public static int glCheckFramebufferStatus(int target) {
        switch (fboMode) {
            case BASE: {
                return GL30.glCheckFramebufferStatus((int)target);
            }
            case ARB: {
                return ARBFramebufferObject.glCheckFramebufferStatus((int)target);
            }
            case EXT: {
                return EXTFramebufferObject.glCheckFramebufferStatusEXT((int)target);
            }
        }
        return -1;
    }

    public static void glFramebufferTexture2D(int target, int attachment, int texTarget, int texture, int level) {
        switch (fboMode) {
            case BASE: {
                GL30.glFramebufferTexture2D((int)target, (int)attachment, (int)texTarget, (int)texture, (int)level);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glFramebufferTexture2D((int)target, (int)attachment, (int)texTarget, (int)texture, (int)level);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glFramebufferTexture2DEXT((int)target, (int)attachment, (int)texTarget, (int)texture, (int)level);
            }
        }
    }

    public static int getBoundFramebuffer() {
        switch (fboMode) {
            case BASE: {
                return GlStateManager.getInteger(36006);
            }
            case ARB: {
                return GlStateManager.getInteger(36006);
            }
            case EXT: {
                return GlStateManager.getInteger(36006);
            }
        }
        return 0;
    }

    public static void glActiveTexture(int texture) {
        if (useMultitextureArb) {
            ARBMultitexture.glActiveTextureARB((int)texture);
        } else {
            GL13.glActiveTexture((int)texture);
        }
    }

    public static void glClientActiveTexture(int texture) {
        if (useMultitextureArb) {
            ARBMultitexture.glClientActiveTextureARB((int)texture);
        } else {
            GL13.glClientActiveTexture((int)texture);
        }
    }

    public static void glMultiTexCoord2f(int target, float s, float t) {
        if (useMultitextureArb) {
            ARBMultitexture.glMultiTexCoord2fARB((int)target, (float)s, (float)t);
        } else {
            GL13.glMultiTexCoord2f((int)target, (float)s, (float)t);
        }
    }

    public static void glBlendFuncSeparate(int sFactorRGB, int dFactorRGB, int sFactorAlpha, int dFactorAlpha) {
        if (separateBlend) {
            if (useSeparateBlendExt) {
                EXTBlendFuncSeparate.glBlendFuncSeparateEXT((int)sFactorRGB, (int)dFactorRGB, (int)sFactorAlpha, (int)dFactorAlpha);
            } else {
                GL14.glBlendFuncSeparate((int)sFactorRGB, (int)dFactorRGB, (int)sFactorAlpha, (int)dFactorAlpha);
            }
        } else {
            GL11.glBlendFunc((int)sFactorRGB, (int)dFactorRGB);
        }
    }

    public static boolean isUsingFBOs() {
        return true;
    }

    public static String getCpuInfo() {
        return cpuInfo == null ? "<unknown>" : cpuInfo;
    }

    public static void renderCrosshair(int i) {
        GLX.renderCrosshair(i, true, true, true);
    }

    public static void renderCrosshair(int i, boolean bl, boolean bl2, boolean bl3) {
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        GL11.glLineWidth((float)4.0f);
        bufferBuilder.begin(1, VertexFormats.POSITION_COLOR);
        if (bl) {
            bufferBuilder.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(i, 0.0, 0.0).color(0, 0, 0, 255).next();
        }
        if (bl2) {
            bufferBuilder.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(0.0, i, 0.0).color(0, 0, 0, 255).next();
        }
        if (bl3) {
            bufferBuilder.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(0.0, 0.0, i).color(0, 0, 0, 255).next();
        }
        tessellator.draw();
        GL11.glLineWidth((float)2.0f);
        bufferBuilder.begin(1, VertexFormats.POSITION_COLOR);
        if (bl) {
            bufferBuilder.vertex(0.0, 0.0, 0.0).color(255, 0, 0, 255).next();
            bufferBuilder.vertex(i, 0.0, 0.0).color(255, 0, 0, 255).next();
        }
        if (bl2) {
            bufferBuilder.vertex(0.0, 0.0, 0.0).color(0, 255, 0, 255).next();
            bufferBuilder.vertex(0.0, i, 0.0).color(0, 255, 0, 255).next();
        }
        if (bl3) {
            bufferBuilder.vertex(0.0, 0.0, 0.0).color(127, 127, 255, 255).next();
            bufferBuilder.vertex(0.0, 0.0, i).color(127, 127, 255, 255).next();
        }
        tessellator.draw();
        GL11.glLineWidth((float)1.0f);
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
    }

    public static String getErrorString(int i) {
        return LOOKUP_MAP.get(i);
    }

    public static <T> T make(Supplier<T> supplier) {
        return supplier.get();
    }

    public static <T> T make(T object, Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }

    static {
        capsString = "";
        LOOKUP_MAP = GLX.make(Maps.newHashMap(), hashMap -> {
            hashMap.put(0, "No error");
            hashMap.put(1280, "Enum parameter is invalid for this function");
            hashMap.put(1281, "Parameter is invalid for this function");
            hashMap.put(1282, "Current state is invalid for this function");
            hashMap.put(1283, "Stack overflow");
            hashMap.put(1284, "Stack underflow");
            hashMap.put(1285, "Out of memory");
            hashMap.put(1286, "Operation on incomplete framebuffer");
            hashMap.put(1286, "Operation on incomplete framebuffer");
        });
    }

    @Environment(value=EnvType.CLIENT)
    static enum FBOMode {
        BASE,
        ARB,
        EXT;

    }
}

