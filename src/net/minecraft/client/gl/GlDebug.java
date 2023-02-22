/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.EvictingQueue
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.opengl.ARBDebugOutput
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.opengl.GLDebugMessageARBCallback
 *  org.lwjgl.opengl.GLDebugMessageARBCallbackI
 *  org.lwjgl.opengl.GLDebugMessageCallback
 *  org.lwjgl.opengl.GLDebugMessageCallbackI
 *  org.lwjgl.opengl.KHRDebug
 *  org.slf4j.Logger
 */
package net.minecraft.client.gl;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Untracker;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageARBCallbackI;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.opengl.KHRDebug;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class GlDebug {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int DEBUG_MESSAGE_QUEUE_SIZE = 10;
    private static final Queue<DebugMessage> DEBUG_MESSAGES = EvictingQueue.create((int)10);
    @Nullable
    private static volatile DebugMessage lastDebugMessage;
    private static final List<Integer> KHR_VERBOSITY_LEVELS;
    private static final List<Integer> ARB_VERBOSITY_LEVELS;
    private static boolean debugMessageEnabled;

    private static String unknown(int opcode) {
        return "Unknown (0x" + Integer.toHexString(opcode).toUpperCase() + ")";
    }

    public static String getSource(int opcode) {
        switch (opcode) {
            case 33350: {
                return "API";
            }
            case 33351: {
                return "WINDOW SYSTEM";
            }
            case 33352: {
                return "SHADER COMPILER";
            }
            case 33353: {
                return "THIRD PARTY";
            }
            case 33354: {
                return "APPLICATION";
            }
            case 33355: {
                return "OTHER";
            }
        }
        return GlDebug.unknown(opcode);
    }

    public static String getType(int opcode) {
        switch (opcode) {
            case 33356: {
                return "ERROR";
            }
            case 33357: {
                return "DEPRECATED BEHAVIOR";
            }
            case 33358: {
                return "UNDEFINED BEHAVIOR";
            }
            case 33359: {
                return "PORTABILITY";
            }
            case 33360: {
                return "PERFORMANCE";
            }
            case 33361: {
                return "OTHER";
            }
            case 33384: {
                return "MARKER";
            }
        }
        return GlDebug.unknown(opcode);
    }

    public static String getSeverity(int opcode) {
        switch (opcode) {
            case 37190: {
                return "HIGH";
            }
            case 37191: {
                return "MEDIUM";
            }
            case 37192: {
                return "LOW";
            }
            case 33387: {
                return "NOTIFICATION";
            }
        }
        return GlDebug.unknown(opcode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void info(int source, int type, int id, int severity, int messageLength, long message, long l) {
        DebugMessage debugMessage;
        String string = GLDebugMessageCallback.getMessage((int)messageLength, (long)message);
        Queue<DebugMessage> queue = DEBUG_MESSAGES;
        synchronized (queue) {
            debugMessage = lastDebugMessage;
            if (debugMessage == null || !debugMessage.equals(source, type, id, severity, string)) {
                debugMessage = new DebugMessage(source, type, id, severity, string);
                DEBUG_MESSAGES.add(debugMessage);
                lastDebugMessage = debugMessage;
            } else {
                ++debugMessage.count;
            }
        }
        LOGGER.info("OpenGL debug message: {}", (Object)debugMessage);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List<String> collectDebugMessages() {
        Queue<DebugMessage> queue = DEBUG_MESSAGES;
        synchronized (queue) {
            ArrayList list = Lists.newArrayListWithCapacity((int)DEBUG_MESSAGES.size());
            for (DebugMessage debugMessage : DEBUG_MESSAGES) {
                list.add(debugMessage + " x " + debugMessage.count);
            }
            return list;
        }
    }

    public static boolean isDebugMessageEnabled() {
        return debugMessageEnabled;
    }

    public static void enableDebug(int verbosity, boolean sync) {
        RenderSystem.assertInInitPhase();
        if (verbosity <= 0) {
            return;
        }
        GLCapabilities gLCapabilities = GL.getCapabilities();
        if (gLCapabilities.GL_KHR_debug) {
            debugMessageEnabled = true;
            GL11.glEnable((int)37600);
            if (sync) {
                GL11.glEnable((int)33346);
            }
            for (int i = 0; i < KHR_VERBOSITY_LEVELS.size(); ++i) {
                boolean bl = i < verbosity;
                KHRDebug.glDebugMessageControl((int)4352, (int)4352, (int)KHR_VERBOSITY_LEVELS.get(i), (int[])null, (boolean)bl);
            }
            KHRDebug.glDebugMessageCallback((GLDebugMessageCallbackI)((GLDebugMessageCallbackI)GLX.make(GLDebugMessageCallback.create(GlDebug::info), Untracker::untrack)), (long)0L);
        } else if (gLCapabilities.GL_ARB_debug_output) {
            debugMessageEnabled = true;
            if (sync) {
                GL11.glEnable((int)33346);
            }
            for (int i = 0; i < ARB_VERBOSITY_LEVELS.size(); ++i) {
                boolean bl = i < verbosity;
                ARBDebugOutput.glDebugMessageControlARB((int)4352, (int)4352, (int)ARB_VERBOSITY_LEVELS.get(i), (int[])null, (boolean)bl);
            }
            ARBDebugOutput.glDebugMessageCallbackARB((GLDebugMessageARBCallbackI)((GLDebugMessageARBCallbackI)GLX.make(GLDebugMessageARBCallback.create(GlDebug::info), Untracker::untrack)), (long)0L);
        }
    }

    static {
        KHR_VERBOSITY_LEVELS = ImmutableList.of((Object)37190, (Object)37191, (Object)37192, (Object)33387);
        ARB_VERBOSITY_LEVELS = ImmutableList.of((Object)37190, (Object)37191, (Object)37192);
    }

    @Environment(value=EnvType.CLIENT)
    static class DebugMessage {
        private final int id;
        private final int source;
        private final int type;
        private final int severity;
        private final String message;
        int count = 1;

        DebugMessage(int source, int type, int id, int severity, String message) {
            this.id = id;
            this.source = source;
            this.type = type;
            this.severity = severity;
            this.message = message;
        }

        boolean equals(int source, int type, int id, int severity, String message) {
            return type == this.type && source == this.source && id == this.id && severity == this.severity && message.equals(this.message);
        }

        public String toString() {
            return "id=" + this.id + ", source=" + GlDebug.getSource(this.source) + ", type=" + GlDebug.getType(this.type) + ", severity=" + GlDebug.getSeverity(this.severity) + ", message='" + this.message + "'";
        }
    }
}

