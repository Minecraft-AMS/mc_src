/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.util;

import com.mojang.blaze3d.systems.RenderCall;
import com.mojang.blaze3d.systems.RenderCallStorage;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class GlfwUtil {
    public static void accessRecordingQueue(RenderCallStorage storage, float f) {
        ConcurrentLinkedQueue<RenderCall> concurrentLinkedQueue = storage.getRecordingQueue();
    }

    public static void accessProcessingQueue(RenderCallStorage storage, float f) {
        ConcurrentLinkedQueue<RenderCall> concurrentLinkedQueue = storage.getProcessingQueue();
    }

    public static void makeJvmCrash() {
        MemoryUtil.memSet((long)0L, (int)0, (long)1L);
    }

    public static double getTime() {
        return GLFW.glfwGetTime();
    }
}

