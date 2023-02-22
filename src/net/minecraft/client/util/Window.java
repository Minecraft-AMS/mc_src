/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.glfw.Callbacks
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallback
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 *  org.lwjgl.glfw.GLFWImage
 *  org.lwjgl.glfw.GLFWImage$Buffer
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.stb.STBImage
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.VideoMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public final class Window
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final GLFWErrorCallback errorCallback = GLFWErrorCallback.create(this::logGlError);
    private final WindowEventHandler eventHandler;
    private final MonitorTracker monitorTracker;
    private final long handle;
    private int field_5175;
    private int field_5185;
    private int field_5174;
    private int field_5184;
    private Optional<VideoMode> videoMode;
    private boolean fullscreen;
    private boolean field_5177;
    private int x;
    private int y;
    private int width;
    private int height;
    private int framebufferWidth;
    private int framebufferHeight;
    private int scaledWidth;
    private int scaledHeight;
    private double scaleFactor;
    private String phase = "";
    private boolean field_5186;
    private double field_5189 = Double.MIN_VALUE;
    private int framerateLimit;
    private boolean field_16517;

    public Window(WindowEventHandler windowEventHandler, MonitorTracker monitorTracker, WindowSettings windowSettings, String string, String string2) {
        this.monitorTracker = monitorTracker;
        this.throwOnGlError();
        this.setPhase("Pre startup");
        this.eventHandler = windowEventHandler;
        Optional<VideoMode> optional = VideoMode.fromString(string);
        this.videoMode = optional.isPresent() ? optional : (windowSettings.fullscreenWidth.isPresent() && windowSettings.fullscreenHeight.isPresent() ? Optional.of(new VideoMode(windowSettings.fullscreenWidth.getAsInt(), windowSettings.fullscreenHeight.getAsInt(), 8, 8, 8, 60)) : Optional.empty());
        this.field_5177 = this.fullscreen = windowSettings.fullscreen;
        Monitor monitor = monitorTracker.getMonitor(GLFW.glfwGetPrimaryMonitor());
        this.width = windowSettings.width > 0 ? windowSettings.width : 1;
        this.field_5174 = this.width;
        this.height = windowSettings.height > 0 ? windowSettings.height : 1;
        this.field_5184 = this.height;
        GLFW.glfwDefaultWindowHints();
        this.handle = GLFW.glfwCreateWindow((int)this.width, (int)this.height, (CharSequence)string2, (long)(this.fullscreen && monitor != null ? monitor.getHandle() : 0L), (long)0L);
        if (monitor != null) {
            VideoMode videoMode = monitor.findClosestVideoMode(this.fullscreen ? this.videoMode : Optional.empty());
            this.field_5175 = this.x = monitor.getViewportX() + videoMode.getWidth() / 2 - this.width / 2;
            this.field_5185 = this.y = monitor.getViewportY() + videoMode.getHeight() / 2 - this.height / 2;
        } else {
            int[] is = new int[1];
            int[] js = new int[1];
            GLFW.glfwGetWindowPos((long)this.handle, (int[])is, (int[])js);
            this.field_5175 = this.x = is[0];
            this.field_5185 = this.y = js[0];
        }
        GLFW.glfwMakeContextCurrent((long)this.handle);
        GL.createCapabilities();
        this.method_4479();
        this.method_4483();
        GLFW.glfwSetFramebufferSizeCallback((long)this.handle, this::onFramebufferSizeChanged);
        GLFW.glfwSetWindowPosCallback((long)this.handle, this::onWindowPosChanged);
        GLFW.glfwSetWindowSizeCallback((long)this.handle, this::onWindowSizeChanged);
        GLFW.glfwSetWindowFocusCallback((long)this.handle, this::onWindowFocusChanged);
    }

    public static void method_4492(BiConsumer<Integer, String> biConsumer) {
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
            int i = GLFW.glfwGetError((PointerBuffer)pointerBuffer);
            if (i != 0) {
                long l = pointerBuffer.get();
                String string = l == 0L ? "" : MemoryUtil.memUTF8((long)l);
                biConsumer.accept(i, string);
            }
        }
    }

    public void method_4493(boolean bl) {
        GlStateManager.clear(256, bl);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, (double)this.getFramebufferWidth() / this.getScaleFactor(), (double)this.getFramebufferHeight() / this.getScaleFactor(), 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translatef(0.0f, 0.0f, -2000.0f);
    }

    public void setIcon(InputStream icon16, InputStream icon32) {
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            if (icon16 == null) {
                throw new FileNotFoundException("icons/icon_16x16.png");
            }
            if (icon32 == null) {
                throw new FileNotFoundException("icons/icon_32x32.png");
            }
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            GLFWImage.Buffer buffer = GLFWImage.mallocStack((int)2, (MemoryStack)memoryStack);
            ByteBuffer byteBuffer = this.method_4510(icon16, intBuffer, intBuffer2, intBuffer3);
            if (byteBuffer == null) {
                throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
            }
            buffer.position(0);
            buffer.width(intBuffer.get(0));
            buffer.height(intBuffer2.get(0));
            buffer.pixels(byteBuffer);
            ByteBuffer byteBuffer2 = this.method_4510(icon32, intBuffer, intBuffer2, intBuffer3);
            if (byteBuffer2 == null) {
                throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
            }
            buffer.position(1);
            buffer.width(intBuffer.get(0));
            buffer.height(intBuffer2.get(0));
            buffer.pixels(byteBuffer2);
            buffer.position(0);
            GLFW.glfwSetWindowIcon((long)this.handle, (GLFWImage.Buffer)buffer);
            STBImage.stbi_image_free((ByteBuffer)byteBuffer);
            STBImage.stbi_image_free((ByteBuffer)byteBuffer2);
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't set icon", (Throwable)iOException);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private ByteBuffer method_4510(InputStream inputStream, IntBuffer intBuffer, IntBuffer intBuffer2, IntBuffer intBuffer3) throws IOException {
        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = TextureUtil.readResource(inputStream);
            byteBuffer.rewind();
            ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory((ByteBuffer)byteBuffer, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, (IntBuffer)intBuffer3, (int)0);
            return byteBuffer2;
        }
        finally {
            if (byteBuffer != null) {
                MemoryUtil.memFree((Buffer)byteBuffer);
            }
        }
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    private void throwOnGlError() {
        GLFW.glfwSetErrorCallback(Window::throwGlError);
    }

    private static void throwGlError(int error, long description) {
        throw new IllegalStateException("GLFW error " + error + ": " + MemoryUtil.memUTF8((long)description));
    }

    public void logGlError(int error, long description) {
        String string = MemoryUtil.memUTF8((long)description);
        LOGGER.error("########## GL ERROR ##########");
        LOGGER.error("@ {}", (Object)this.phase);
        LOGGER.error("{}: {}", (Object)error, (Object)string);
    }

    public void logOnGlError() {
        GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)this.errorCallback).free();
    }

    public void setVsync(boolean vsync) {
        this.field_16517 = vsync;
        GLFW.glfwSwapInterval((int)(vsync ? 1 : 0));
    }

    @Override
    public void close() {
        Callbacks.glfwFreeCallbacks((long)this.handle);
        this.errorCallback.close();
        GLFW.glfwDestroyWindow((long)this.handle);
        GLFW.glfwTerminate();
    }

    private void onWindowPosChanged(long window, int x, int y) {
        this.x = x;
        this.y = y;
    }

    private void onFramebufferSizeChanged(long window, int width, int height) {
        if (window != this.handle) {
            return;
        }
        int i = this.getFramebufferWidth();
        int j = this.getFramebufferHeight();
        if (width == 0 || height == 0) {
            return;
        }
        this.framebufferWidth = width;
        this.framebufferHeight = height;
        if (this.getFramebufferWidth() != i || this.getFramebufferHeight() != j) {
            this.eventHandler.onResolutionChanged();
        }
    }

    private void method_4483() {
        int[] is = new int[1];
        int[] js = new int[1];
        GLFW.glfwGetFramebufferSize((long)this.handle, (int[])is, (int[])js);
        this.framebufferWidth = is[0];
        this.framebufferHeight = js[0];
    }

    private void onWindowSizeChanged(long window, int width, int height) {
        this.width = width;
        this.height = height;
    }

    private void onWindowFocusChanged(long window, boolean focused) {
        if (window == this.handle) {
            this.eventHandler.onWindowFocusChanged(focused);
        }
    }

    public void setFramerateLimit(int framerateLimit) {
        this.framerateLimit = framerateLimit;
    }

    public int getFramerateLimit() {
        return this.framerateLimit;
    }

    public void setFullscreen(boolean bl) {
        GLFW.glfwSwapBuffers((long)this.handle);
        Window.pollEvents();
        if (this.fullscreen != this.field_5177) {
            this.field_5177 = this.fullscreen;
            this.method_4485(this.field_16517);
        }
    }

    public void waitForFramerateLimit() {
        double d = this.field_5189 + 1.0 / (double)this.getFramerateLimit();
        double e = GLFW.glfwGetTime();
        while (e < d) {
            GLFW.glfwWaitEventsTimeout((double)(d - e));
            e = GLFW.glfwGetTime();
        }
        this.field_5189 = e;
    }

    public Optional<VideoMode> getVideoMode() {
        return this.videoMode;
    }

    public void setVideoMode(Optional<VideoMode> videoMode) {
        boolean bl = !videoMode.equals(this.videoMode);
        this.videoMode = videoMode;
        if (bl) {
            this.field_5186 = true;
        }
    }

    public void method_4475() {
        if (this.fullscreen && this.field_5186) {
            this.field_5186 = false;
            this.method_4479();
            this.eventHandler.onResolutionChanged();
        }
    }

    private void method_4479() {
        boolean bl;
        boolean bl2 = bl = GLFW.glfwGetWindowMonitor((long)this.handle) != 0L;
        if (this.fullscreen) {
            Monitor monitor = this.monitorTracker.getMonitor(this);
            if (monitor == null) {
                LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
                this.fullscreen = false;
            } else {
                VideoMode videoMode = monitor.findClosestVideoMode(this.videoMode);
                if (!bl) {
                    this.field_5175 = this.x;
                    this.field_5185 = this.y;
                    this.field_5174 = this.width;
                    this.field_5184 = this.height;
                }
                this.x = 0;
                this.y = 0;
                this.width = videoMode.getWidth();
                this.height = videoMode.getHeight();
                GLFW.glfwSetWindowMonitor((long)this.handle, (long)monitor.getHandle(), (int)this.x, (int)this.y, (int)this.width, (int)this.height, (int)videoMode.getRefreshRate());
            }
        } else {
            this.x = this.field_5175;
            this.y = this.field_5185;
            this.width = this.field_5174;
            this.height = this.field_5184;
            GLFW.glfwSetWindowMonitor((long)this.handle, (long)0L, (int)this.x, (int)this.y, (int)this.width, (int)this.height, (int)-1);
        }
    }

    public void toggleFullscreen() {
        this.fullscreen = !this.fullscreen;
    }

    private void method_4485(boolean bl) {
        try {
            this.method_4479();
            this.eventHandler.onResolutionChanged();
            this.setVsync(bl);
            this.eventHandler.updateDisplay(false);
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't toggle fullscreen", (Throwable)exception);
        }
    }

    public int calculateScaleFactor(int guiScale, boolean forceUnicodeFont) {
        int i;
        for (i = 1; i != guiScale && i < this.framebufferWidth && i < this.framebufferHeight && this.framebufferWidth / (i + 1) >= 320 && this.framebufferHeight / (i + 1) >= 240; ++i) {
        }
        if (forceUnicodeFont && i % 2 != 0) {
            ++i;
        }
        return i;
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
        int i = (int)((double)this.framebufferWidth / scaleFactor);
        this.scaledWidth = (double)this.framebufferWidth / scaleFactor > (double)i ? i + 1 : i;
        int j = (int)((double)this.framebufferHeight / scaleFactor);
        this.scaledHeight = (double)this.framebufferHeight / scaleFactor > (double)j ? j + 1 : j;
    }

    public long getHandle() {
        return this.handle;
    }

    public boolean isFullscreen() {
        return this.fullscreen;
    }

    public int getFramebufferWidth() {
        return this.framebufferWidth;
    }

    public int getFramebufferHeight() {
        return this.framebufferHeight;
    }

    public static void pollEvents() {
        GLFW.glfwPollEvents();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getScaledWidth() {
        return this.scaledWidth;
    }

    public int getScaledHeight() {
        return this.scaledHeight;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public double getScaleFactor() {
        return this.scaleFactor;
    }

    @Nullable
    public Monitor getMonitor() {
        return this.monitorTracker.getMonitor(this);
    }

    public void method_21668(boolean bl) {
        InputUtil.method_21736(this.handle, bl);
    }
}

