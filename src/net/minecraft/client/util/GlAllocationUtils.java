/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class GlAllocationUtils {
    public static synchronized int genLists(int i) {
        int j = GlStateManager.genLists(i);
        if (j == 0) {
            int k = GlStateManager.getError();
            String string = "No error code reported";
            if (k != 0) {
                string = GLX.getErrorString(k);
            }
            throw new IllegalStateException("glGenLists returned an ID of 0 for a count of " + i + ", GL error (" + k + "): " + string);
        }
        return j;
    }

    public static synchronized void deleteLists(int i, int j) {
        GlStateManager.deleteLists(i, j);
    }

    public static synchronized void deleteSingletonList(int i) {
        GlAllocationUtils.deleteLists(i, 1);
    }

    public static synchronized ByteBuffer allocateByteBuffer(int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }

    public static FloatBuffer allocateFloatBuffer(int size) {
        return GlAllocationUtils.allocateByteBuffer(size << 2).asFloatBuffer();
    }
}
