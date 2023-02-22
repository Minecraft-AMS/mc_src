/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.MemoryUtil$MemoryAllocator
 */
package net.minecraft.client.util;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class GlAllocationUtils {
    private static final MemoryUtil.MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator((boolean)false);

    public static ByteBuffer allocateByteBuffer(int size) {
        long l = ALLOCATOR.malloc((long)size);
        if (l == 0L) {
            throw new OutOfMemoryError("Failed to allocate " + size + " bytes");
        }
        return MemoryUtil.memByteBuffer((long)l, (int)size);
    }

    public static ByteBuffer resizeByteBuffer(ByteBuffer source, int size) {
        long l = ALLOCATOR.realloc(MemoryUtil.memAddress0((Buffer)source), (long)size);
        if (l == 0L) {
            throw new OutOfMemoryError("Failed to resize buffer from " + source.capacity() + " bytes to " + size + " bytes");
        }
        return MemoryUtil.memByteBuffer((long)l, (int)size);
    }
}

