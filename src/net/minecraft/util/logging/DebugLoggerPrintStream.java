/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.util.logging;

import com.mojang.logging.LogUtils;
import java.io.OutputStream;
import net.minecraft.util.logging.LoggerPrintStream;
import org.slf4j.Logger;

public class DebugLoggerPrintStream
extends LoggerPrintStream {
    private static final Logger field_36382 = LogUtils.getLogger();

    public DebugLoggerPrintStream(String string, OutputStream outputStream) {
        super(string, outputStream);
    }

    @Override
    protected void log(String message) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement stackTraceElement = stackTraceElements[Math.min(3, stackTraceElements.length)];
        field_36382.info("[{}]@.({}:{}): {}", new Object[]{this.name, stackTraceElement.getFileName(), stackTraceElement.getLineNumber(), message});
    }
}

